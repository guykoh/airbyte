/*
 * Copyright (c) 2022 Airbyte, Inc., all rights reserved.
 */

package io.airbyte.integrations.source.postgres;

import static io.airbyte.integrations.debezium.internals.DebeziumEventUtils.CDC_DELETED_AT;
import static io.airbyte.integrations.debezium.internals.DebeziumEventUtils.CDC_UPDATED_AT;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import io.airbyte.commons.functional.CheckedConsumer;
import io.airbyte.commons.functional.CheckedFunction;
import io.airbyte.commons.json.Jsons;
import io.airbyte.commons.util.AutoCloseableIterator;
import io.airbyte.db.factory.DatabaseDriver;
import io.airbyte.db.jdbc.JdbcDatabase;
import io.airbyte.db.jdbc.streaming.AdaptiveStreamingQueryConfig;
import io.airbyte.integrations.base.IntegrationRunner;
import io.airbyte.integrations.base.Source;
import io.airbyte.integrations.base.ssh.SshWrappedSource;
import io.airbyte.integrations.debezium.AirbyteDebeziumHandler;
import io.airbyte.integrations.source.jdbc.AbstractJdbcSource;
import io.airbyte.integrations.source.jdbc.dto.JdbcPrivilegeDto;
import io.airbyte.integrations.source.relationaldb.StateManager;
import io.airbyte.integrations.source.relationaldb.TableInfo;
import io.airbyte.protocol.models.AirbyteCatalog;
import io.airbyte.protocol.models.AirbyteConnectionStatus;
import io.airbyte.protocol.models.AirbyteMessage;
import io.airbyte.protocol.models.AirbyteStream;
import io.airbyte.protocol.models.CommonField;
import io.airbyte.protocol.models.ConfiguredAirbyteCatalog;
import io.airbyte.protocol.models.SyncMode;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PostgresSource extends AbstractJdbcSource<JDBCType> implements Source {

  private static final Logger LOGGER = LoggerFactory.getLogger(PostgresSource.class);
  private static final String KEY_STORE_PASS = RandomStringUtils.randomAlphanumeric(8);
  private static final String CA_CERTIFICATE = "ca.crt";
  private static final String CLIENT_CERTIFICATE = "client.crt";
  private static final String CLIENT_KEY = "client.key";
  private static final String CLIENT_ENCRYPTED_KEY = "client.pk8";

  public static final String CDC_LSN = "_ab_cdc_lsn";

  static final String DRIVER_CLASS = DatabaseDriver.POSTGRESQL.getDriverClassName();
  private List<String> schemas;

  public static Source sshWrappedSource() {
    return new SshWrappedSource(new PostgresSource(), List.of("host"), List.of("port"));
  }

  PostgresSource() {
    super(DRIVER_CLASS, AdaptiveStreamingQueryConfig::new, new PostgresSourceOperations());
  }

  @Override
  public JsonNode toDatabaseConfig(final JsonNode config) {
    return toDatabaseConfigStatic(config);
  }

  // todo (cgardens) - restructure AbstractJdbcSource so to take this function in the constructor. the
  // current structure forces us to declarehave a bunch of pure function methods as instance members
  // when they could be static.
  public JsonNode toDatabaseConfigStatic(final JsonNode config) {
    final List<String> additionalParameters = new ArrayList<>();

    final StringBuilder jdbcUrl = new StringBuilder(String.format("jdbc:postgresql://%s:%s/%s?",
        config.get("host").asText(),
        config.get("port").asText(),
        config.get("database").asText()));

    if (config.get("jdbc_url_params") != null && !config.get("jdbc_url_params").asText().isEmpty()) {
      jdbcUrl.append(config.get("jdbc_url_params").asText()).append("&");
    }

    // assume ssl if not explicitly mentioned.
    if (!config.has("ssl") || config.get("ssl").asBoolean()) {
      if ("disable".equals(config.get("ssl_mode").get("mode").asText())) {
        additionalParameters.add("sslmode=disable");
      } else {
        additionalParameters.addAll(obtainConnectionOptions(config.get("ssl_mode")));
      }
    }

    if (config.has("schemas") && config.get("schemas").isArray()) {
      schemas = new ArrayList<>();
      for (final JsonNode schema : config.get("schemas")) {
        schemas.add(schema.asText());
      }
    }

    if (schemas != null && !schemas.isEmpty()) {
      additionalParameters.add("currentSchema=" + String.join(",", schemas));
    }

    additionalParameters.forEach(x -> jdbcUrl.append(x).append("&"));

    final ImmutableMap.Builder<Object, Object> configBuilder = ImmutableMap.builder()
        .put("username", config.get("username").asText())
        .put("jdbc_url", jdbcUrl.toString());

    if (config.has("password")) {
      configBuilder.put("password", config.get("password").asText());
    }

    return Jsons.jsonNode(configBuilder.build());
  }

  @Override
  public Set<String> getExcludedInternalNameSpaces() {
    return Set.of("information_schema", "pg_catalog", "pg_internal", "catalog_history");
  }

  @Override
  public AirbyteCatalog discover(final JsonNode config) throws Exception {
    final AirbyteCatalog catalog = super.discover(config);

    if (isCdc(config)) {
      final List<AirbyteStream> streams = catalog.getStreams().stream()
          .map(PostgresSource::removeIncrementalWithoutPk)
          .map(PostgresSource::setIncrementalToSourceDefined)
          .map(PostgresSource::addCdcMetadataColumns)
          .collect(toList());

      catalog.setStreams(streams);
    }

    return catalog;
  }

  @Override
  public List<TableInfo<CommonField<JDBCType>>> discoverInternal(final JdbcDatabase database) throws Exception {
    if (schemas != null && !schemas.isEmpty()) {
      // process explicitly selected (from UI) schemas
      final List<TableInfo<CommonField<JDBCType>>> internals = new ArrayList<>();
      for (final String schema : schemas) {
        LOGGER.info("Checking schema: {}", schema);
        final List<TableInfo<CommonField<JDBCType>>> tables = super.discoverInternal(database, schema);
        internals.addAll(tables);
        for (final TableInfo<CommonField<JDBCType>> table : tables) {
          LOGGER.info("Found table: {}.{}", table.getNameSpace(), table.getName());
        }
      }
      return internals;
    } else {
      LOGGER.info("No schemas explicitly set on UI to process, so will process all of existing schemas in DB");
      return super.discoverInternal(database);
    }
  }

  @Override
  public List<CheckedConsumer<JdbcDatabase, Exception>> getCheckOperations(final JsonNode config)
      throws Exception {
    final List<CheckedConsumer<JdbcDatabase, Exception>> checkOperations = new ArrayList<>(
        super.getCheckOperations(config));

    if (isCdc(config)) {
      checkOperations.add(database -> {
        final List<JsonNode> matchingSlots = database.queryJsons(connection -> {
          final String sql = "SELECT * FROM pg_replication_slots WHERE slot_name = ? AND plugin = ? AND database = ?";
          final PreparedStatement ps = connection.prepareStatement(sql);
          ps.setString(1, config.get("replication_method").get("replication_slot").asText());
          ps.setString(2, PostgresUtils.getPluginValue(config.get("replication_method")));
          ps.setString(3, config.get("database").asText());

          LOGGER.info(
              "Attempting to find the named replication slot using the query: " + ps.toString());

          return ps;
        }, sourceOperations::rowToJson);

        if (matchingSlots.size() != 1) {
          throw new RuntimeException(
              "Expected exactly one replication slot but found " + matchingSlots.size()
                  + ". Please read the docs and add a replication slot to your database.");
        }

      });

      checkOperations.add(database -> {
        final List<JsonNode> matchingPublications = database.queryJsons(connection -> {
          final PreparedStatement ps = connection.prepareStatement("SELECT * FROM pg_publication WHERE pubname = ?");
          ps.setString(1, config.get("replication_method").get("publication").asText());
          LOGGER.info("Attempting to find the publication using the query: " + ps);
          return ps;
        }, sourceOperations::rowToJson);

        if (matchingPublications.size() != 1) {
          throw new RuntimeException(
              "Expected exactly one publication but found " + matchingPublications.size()
                  + ". Please read the docs and add a publication to your database.");
        }

      });
    }

    return checkOperations;
  }

  @Override
  public AutoCloseableIterator<AirbyteMessage> read(final JsonNode config,
                                                    final ConfiguredAirbyteCatalog catalog,
                                                    final JsonNode state)
      throws Exception {
    // this check is used to ensure that have the pgoutput slot available so Debezium won't attempt to
    // create it.
    final AirbyteConnectionStatus check = check(config);

    if (check.getStatus().equals(AirbyteConnectionStatus.Status.FAILED)) {
      throw new RuntimeException("Unable establish a connection: " + check.getMessage());
    }

    return super.read(config, catalog, state);
  }

  @Override
  public List<AutoCloseableIterator<AirbyteMessage>> getIncrementalIterators(
                                                                             final JdbcDatabase database,
                                                                             final ConfiguredAirbyteCatalog catalog,
                                                                             final Map<String, TableInfo<CommonField<JDBCType>>> tableNameToTable,
                                                                             final StateManager stateManager,
                                                                             final Instant emittedAt) {
    /**
     * If a customer sets up a postgres source with cdc parameters (replication_slot and publication)
     * but selects all the tables in FULL_REFRESH mode then we would still end up going through this
     * path. We do have a check in place for debezium to make sure only tales in INCREMENTAL mode are
     * synced {@link DebeziumRecordPublisher#getTableWhitelist(ConfiguredAirbyteCatalog)} but we should
     * have a check here as well to make sure that if no table is in INCREMENTAL mode then skip this
     * part
     */
    final JsonNode sourceConfig = database.getSourceConfig();
    if (isCdc(sourceConfig)) {
      final AirbyteDebeziumHandler handler = new AirbyteDebeziumHandler(sourceConfig,
          PostgresCdcTargetPosition.targetPosition(database),
          PostgresCdcProperties.getDebeziumProperties(sourceConfig), catalog, false);
      return handler.getIncrementalIterators(
          new PostgresCdcSavedInfoFetcher(stateManager.getCdcStateManager().getCdcState()),
          new PostgresCdcStateHandler(stateManager), new PostgresCdcConnectorMetadataInjector(),
          emittedAt);

    } else {
      return super.getIncrementalIterators(database, catalog, tableNameToTable, stateManager, emittedAt);
    }
  }

  @VisibleForTesting
  static boolean isCdc(final JsonNode config) {
    final boolean isCdc = config.hasNonNull("replication_method")
        && config.get("replication_method").hasNonNull("replication_slot")
        && config.get("replication_method").hasNonNull("publication");
    LOGGER.info("using CDC: {}", isCdc);
    return isCdc;
  }

  /*
   * It isn't possible to recreate the state of the original database unless we include extra
   * information (like an oid) when using logical replication. By limiting to Full Refresh when we
   * don't have a primary key we dodge the problem for now. As a work around a CDC and non-CDC source
   * could be configured if there's a need to replicate a large non-PK table.
   *
   * Note: in place mutation.
   */
  private static AirbyteStream removeIncrementalWithoutPk(final AirbyteStream stream) {
    if (stream.getSourceDefinedPrimaryKey().isEmpty()) {
      stream.getSupportedSyncModes().remove(SyncMode.INCREMENTAL);
    }

    return stream;
  }

  @Override
  public Set<JdbcPrivilegeDto> getPrivilegesTableForCurrentUser(final JdbcDatabase database,
                                                                final String schema)
      throws SQLException {
    final CheckedFunction<Connection, PreparedStatement, SQLException> statementCreator = connection -> {
      final PreparedStatement ps = connection.prepareStatement(
          """
                 SELECT DISTINCT table_catalog,
                                 table_schema,
                                 table_name,
                                 privilege_type
                 FROM            information_schema.table_privileges
                 WHERE           grantee = ?
                 AND             privilege_type = 'SELECT'
                 UNION ALL
                 SELECT r.rolname           AS table_catalog,
                        n.nspname           AS table_schema,
                        c.relname           AS table_name,
                        -- the initial query is supposed to get a SELECT type. Since we use a UNION query
                        -- to get Views that we can read (i.e. select) - then lets fill this columns with SELECT
                        -- value to keep the backward-compatibility
                        COALESCE ('SELECT') AS privilege_type
                 FROM   pg_class c
                 JOIN   pg_namespace n
                 ON     n.oid = relnamespace
                 JOIN   pg_roles r
                 ON     r.oid = relowner,
                 Unnest(COALESCE(relacl::text[], Format('{%s=arwdDxt/%s}', rolname, rolname)::text[])) acl,
                        Regexp_split_to_array(acl, '=|/') s
                 WHERE  r.rolname = ?
                 AND    (
                               nspname = 'public'
                          OR   nspname = ?)
                        -- 'm' means Materialized View
                 AND    c.relkind = 'm'
                 AND    (
                               -- all grants
                               c.relacl IS NULL
                               -- read grant
                        OR     s[2] = 'r')
                 UNION
                 SELECT DISTINCT table_catalog,
                         table_schema,
                         table_name,
                         privilege_type
                 FROM            information_schema.table_privileges p
                 JOIN			 information_schema.applicable_roles r ON p.grantee = r.role_name
                 WHERE   r.grantee in
                (WITH RECURSIVE membership_tree(grpid, userid) AS (
                    SELECT pg_roles.oid, pg_roles.oid
                    FROM pg_roles WHERE oid = (select oid from pg_roles where  rolname=?)
                  UNION ALL
                    SELECT m_1.roleid, t_1.userid
                    FROM pg_auth_members m_1, membership_tree t_1
                    WHERE m_1.member = t_1.grpid
                )
                SELECT DISTINCT m.rolname AS grpname
                FROM membership_tree t, pg_roles r, pg_roles m
                WHERE t.grpid = m.oid AND t.userid = r.oid)
                 AND             privilege_type = 'SELECT';
          """);
      final String username = getUsername(database.getDatabaseConfig());
      ps.setString(1, username);
      ps.setString(2, username);
      ps.setString(3, username);
      ps.setString(4, username);
      return ps;
    };

    return database.queryJsons(statementCreator, sourceOperations::rowToJson)
        .stream()
        .map(e -> JdbcPrivilegeDto.builder()
            .schemaName(e.get("table_schema").asText())
            .tableName(e.get("table_name").asText())
            .build())
        .collect(toSet());
  }

  @VisibleForTesting
  static String getUsername(final JsonNode databaseConfig) {
    final String jdbcUrl = databaseConfig.get("jdbc_url").asText();
    final String username = databaseConfig.get("username").asText();

    // Azure Postgres server has this username pattern: <username>@<host>.
    // Inside Postgres, the true username is just <username>.
    // The jdbc_url is constructed in the toDatabaseConfigStatic method.
    if (username.contains("@") && jdbcUrl.contains("azure.com:")) {
      final String[] tokens = username.split("@");
      final String postgresUsername = tokens[0];
      LOGGER.info("Azure username \"{}\" is detected; use \"{}\" to check permission", username, postgresUsername);
      return postgresUsername;
    }

    return username;
  }

  @Override
  protected boolean isNotInternalSchema(final JsonNode jsonNode, final Set<String> internalSchemas) {
    return false;
  }

  /*
   * Set all streams that do have incremental to sourceDefined, so that the user cannot set or
   * override a cursor field.
   *
   * Note: in place mutation.
   */
  private static AirbyteStream setIncrementalToSourceDefined(final AirbyteStream stream) {
    if (stream.getSupportedSyncModes().contains(SyncMode.INCREMENTAL)) {
      stream.setSourceDefinedCursor(true);
    }

    return stream;
  }

  // Note: in place mutation.
  private static AirbyteStream addCdcMetadataColumns(final AirbyteStream stream) {
    final ObjectNode jsonSchema = (ObjectNode) stream.getJsonSchema();
    final ObjectNode properties = (ObjectNode) jsonSchema.get("properties");

    final JsonNode stringType = Jsons.jsonNode(ImmutableMap.of("type", "string"));
    final JsonNode numberType = Jsons.jsonNode(ImmutableMap.of("type", "number"));
    properties.set(CDC_LSN, numberType);
    properties.set(CDC_UPDATED_AT, stringType);
    properties.set(CDC_DELETED_AT, stringType);

    return stream;
  }

  public static void main(final String[] args) throws Exception {
    final Source source = PostgresSource.sshWrappedSource();
    LOGGER.info("starting source: {}", PostgresSource.class);
    new IntegrationRunner(source).run(args);
    LOGGER.info("completed source: {}", PostgresSource.class);
  }

  /* Helpers */

  private List<String> obtainConnectionOptions(final JsonNode encryption) {
    final List<String> additionalParameters = new ArrayList<>();
    if (!encryption.isNull()) {
      final var method = encryption.get("mode").asText();
      final var clientKeyPassword = getKeyStorePassword(encryption.get("client_key_password"));
      if ("verify-ca".equals(method)) {
        additionalParameters.addAll(obtainConnectionCaOptions(encryption, method, clientKeyPassword));
      } else if ("verify-full".equals(method)) {
        additionalParameters.addAll(obtainConnectionFullOptions(encryption, method, clientKeyPassword));
      } else {
        additionalParameters.add("ssl=true");
        additionalParameters.add("sslmode=" + method);
      }
    }
    return additionalParameters;
  }

  private List<String> obtainConnectionFullOptions(final JsonNode encryption,
                                                   final String method,
                                                   final String clientKeyPassword) {
    final List<String> additionalParameters = new ArrayList<>();
    try {
      convertAndImportFullCertificate(encryption.get("ca_certificate").asText(),
          encryption.get("client_certificate").asText(), encryption.get("client_key").asText(), clientKeyPassword);
    } catch (final IOException | InterruptedException e) {
      throw new RuntimeException("Failed to import certificate into Java Keystore");
    }
    additionalParameters.add("ssl=true");
    additionalParameters.add("sslmode=" + method);
    additionalParameters.add("sslrootcert=" + CA_CERTIFICATE);
    additionalParameters.add("sslcert=" + CLIENT_CERTIFICATE);
    additionalParameters.add("sslkey=" + CLIENT_ENCRYPTED_KEY);
    additionalParameters.add("sslfactory=org.postgresql.ssl.DefaultJavaSSLFactory");
    return additionalParameters;
  }

  private List<String> obtainConnectionCaOptions(final JsonNode encryption,
                                                 final String method,
                                                 final String clientKeyPassword) {
    final List<String> additionalParameters = new ArrayList<>();
    try {
      convertAndImportCaCertificate(encryption.get("ca_certificate").asText(), clientKeyPassword);
    } catch (final IOException | InterruptedException e) {
      throw new RuntimeException("Failed to import certificate into Java Keystore");
    }
    additionalParameters.add("ssl=true");
    additionalParameters.add("sslmode=" + method);
    additionalParameters.add("sslrootcert=" + CA_CERTIFICATE);
    additionalParameters.add("sslfactory=org.postgresql.ssl.DefaultJavaSSLFactory");
    return additionalParameters;
  }

  private static void convertAndImportFullCertificate(final String caCertificate,
                                                      final String clientCertificate,
                                                      final String clientKey,
                                                      final String clientKeyPassword)
      throws IOException, InterruptedException {
    final Runtime run = Runtime.getRuntime();
    createCertificateFile(CA_CERTIFICATE, caCertificate);
    createCertificateFile(CLIENT_CERTIFICATE, clientCertificate);
    createCertificateFile(CLIENT_KEY, clientKey);
    // add CA certificate to the custom keystore
    runProcess("keytool -alias ca-certificate -keystore customkeystore"
        + " -import -file " + CA_CERTIFICATE + " -storepass " + clientKeyPassword + " -noprompt", run);
    // add client certificate to the custom keystore
    runProcess("keytool -alias client-certificate -keystore customkeystore"
        + " -import -file " + CLIENT_CERTIFICATE + " -storepass " + clientKeyPassword + " -noprompt", run);
    // convert client.key to client.pk8 based on the documentation
    runProcess("openssl pkcs8 -topk8 -inform PEM -in " + CLIENT_KEY + " -outform DER -out "
        + CLIENT_ENCRYPTED_KEY + " -nocrypt", run);
    runProcess("rm " + CLIENT_KEY, run);

    String result = System.getProperty("user.dir") + "/customkeystore";
    System.setProperty("javax.net.ssl.trustStore", result);
    System.setProperty("javax.net.ssl.trustStorePassword", clientKeyPassword);
  }

  private static void convertAndImportCaCertificate(final String caCertificate,
                                                    final String clientKeyPassword)
      throws IOException, InterruptedException {
    final Runtime run = Runtime.getRuntime();
    createCertificateFile(CA_CERTIFICATE, caCertificate);
    runProcess("keytool -import -alias rds-root -keystore customkeystore"
        + " -file " + CA_CERTIFICATE + " -storepass " + clientKeyPassword + " -noprompt", run);

    String result = System.getProperty("user.dir") + "/customkeystore";
    System.setProperty("javax.net.ssl.trustStore", result);
    System.setProperty("javax.net.ssl.trustStorePassword", clientKeyPassword);
  }

  private static void createCertificateFile(String fileName, String fileValue) throws IOException {
    try (final PrintWriter out = new PrintWriter(fileName, StandardCharsets.UTF_8)) {
      out.print(fileValue);
    }
  }

  private static String getKeyStorePassword(final JsonNode sslMode) {
    var keyStorePassword = KEY_STORE_PASS;
    if (!sslMode.isNull() || !sslMode.isEmpty()) {
      keyStorePassword = sslMode.asText();
    }
    return keyStorePassword;
  }

  private static void runProcess(final String cmd, final Runtime run) throws IOException, InterruptedException {
    final Process pr = run.exec(cmd);
    if (!pr.waitFor(50, TimeUnit.SECONDS)) {
      pr.destroy();
      throw new RuntimeException("Timeout while executing: " + cmd);
    }
  }

}
