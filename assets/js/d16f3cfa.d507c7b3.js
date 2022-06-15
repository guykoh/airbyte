"use strict";(self.webpackChunkdocu=self.webpackChunkdocu||[]).push([[4971],{3905:function(e,t,a){a.d(t,{Zo:function(){return s},kt:function(){return g}});var r=a(67294);function n(e,t,a){return t in e?Object.defineProperty(e,t,{value:a,enumerable:!0,configurable:!0,writable:!0}):e[t]=a,e}function o(e,t){var a=Object.keys(e);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);t&&(r=r.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),a.push.apply(a,r)}return a}function i(e){for(var t=1;t<arguments.length;t++){var a=null!=arguments[t]?arguments[t]:{};t%2?o(Object(a),!0).forEach((function(t){n(e,t,a[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(a)):o(Object(a)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(a,t))}))}return e}function l(e,t){if(null==e)return{};var a,r,n=function(e,t){if(null==e)return{};var a,r,n={},o=Object.keys(e);for(r=0;r<o.length;r++)a=o[r],t.indexOf(a)>=0||(n[a]=e[a]);return n}(e,t);if(Object.getOwnPropertySymbols){var o=Object.getOwnPropertySymbols(e);for(r=0;r<o.length;r++)a=o[r],t.indexOf(a)>=0||Object.prototype.propertyIsEnumerable.call(e,a)&&(n[a]=e[a])}return n}var c=r.createContext({}),A=function(e){var t=r.useContext(c),a=t;return e&&(a="function"==typeof e?e(t):i(i({},t),e)),a},s=function(e){var t=A(e.components);return r.createElement(c.Provider,{value:t},e.children)},u={inlineCode:"code",wrapper:function(e){var t=e.children;return r.createElement(r.Fragment,{},t)}},p=r.forwardRef((function(e,t){var a=e.components,n=e.mdxType,o=e.originalType,c=e.parentName,s=l(e,["components","mdxType","originalType","parentName"]),p=A(a),g=n,d=p["".concat(c,".").concat(g)]||p[g]||u[g]||o;return a?r.createElement(d,i(i({ref:t},s),{},{components:a})):r.createElement(d,i({ref:t},s))}));function g(e,t){var a=arguments,n=t&&t.mdxType;if("string"==typeof e||n){var o=a.length,i=new Array(o);i[0]=p;var l={};for(var c in t)hasOwnProperty.call(t,c)&&(l[c]=t[c]);l.originalType=e,l.mdxType="string"==typeof e?e:n,i[1]=l;for(var A=2;A<o;A++)i[A]=a[A];return r.createElement.apply(null,i)}return r.createElement.apply(null,a)}p.displayName="MDXCreateElement"},1467:function(e,t,a){a.r(t),a.d(t,{assets:function(){return s},contentTitle:function(){return c},default:function(){return g},frontMatter:function(){return l},metadata:function(){return A},toc:function(){return u}});var r=a(87462),n=a(63366),o=(a(67294),a(3905)),i=["components"],l={},c="Airtable",A={unversionedId:"integrations/sources/airtable",id:"integrations/sources/airtable",title:"Airtable",description:"Features",source:"@site/../docs/integrations/sources/airtable.md",sourceDirName:"integrations/sources",slug:"/integrations/sources/airtable",permalink:"/integrations/sources/airtable",draft:!1,editUrl:"https://github.com/airbytehq/airbyte/blob/master/docs/../docs/integrations/sources/airtable.md",tags:[],version:"current",frontMatter:{},sidebar:"mySidebar",previous:{title:"Connector Catalog",permalink:"/integrations/"},next:{title:"Amazon Ads",permalink:"/integrations/sources/amazon-ads"}},s={},u=[{value:"Features",id:"features",level:2},{value:"Supported Tables",id:"supported-tables",level:2},{value:"Getting started",id:"getting-started",level:2},{value:"Requirements",id:"requirements",level:3},{value:"Setup guide",id:"setup-guide",level:3},{value:"Performance Considerations (Airbyte Open-Source)",id:"performance-considerations-airbyte-open-source",level:3},{value:"Changelog",id:"changelog",level:2}],p={toc:u};function g(e){var t=e.components,l=(0,n.Z)(e,i);return(0,o.kt)("wrapper",(0,r.Z)({},p,l,{components:t,mdxType:"MDXLayout"}),(0,o.kt)("h1",{id:"airtable"},"Airtable"),(0,o.kt)("h2",{id:"features"},"Features"),(0,o.kt)("table",null,(0,o.kt)("thead",{parentName:"table"},(0,o.kt)("tr",{parentName:"thead"},(0,o.kt)("th",{parentName:"tr",align:"left"},"Feature"),(0,o.kt)("th",{parentName:"tr",align:"left"},"Supported?","(","Yes/No",")"),(0,o.kt)("th",{parentName:"tr",align:"left"},"Notes"))),(0,o.kt)("tbody",{parentName:"table"},(0,o.kt)("tr",{parentName:"tbody"},(0,o.kt)("td",{parentName:"tr",align:"left"},"Full Refresh Sync"),(0,o.kt)("td",{parentName:"tr",align:"left"},"Yes"),(0,o.kt)("td",{parentName:"tr",align:"left"})),(0,o.kt)("tr",{parentName:"tbody"},(0,o.kt)("td",{parentName:"tr",align:"left"},"Incremental Sync"),(0,o.kt)("td",{parentName:"tr",align:"left"},"No"),(0,o.kt)("td",{parentName:"tr",align:"left"})))),(0,o.kt)("p",null,"This source syncs data from the ",(0,o.kt)("a",{parentName:"p",href:"https://airtable.com/api"},"Airtable API"),"."),(0,o.kt)("h2",{id:"supported-tables"},"Supported Tables"),(0,o.kt)("p",null,"This source allows you to configure any table in your Airtable base. In case you you rename or add a column to any existing table, you will need to recreate the source to update the Airbyte catalog. "),(0,o.kt)("h2",{id:"getting-started"},"Getting started"),(0,o.kt)("h3",{id:"requirements"},"Requirements"),(0,o.kt)("ul",null,(0,o.kt)("li",{parentName:"ul"},"An Airtable account & API key"),(0,o.kt)("li",{parentName:"ul"},"Base ID"),(0,o.kt)("li",{parentName:"ul"},"Tables you'd like to replicate")),(0,o.kt)("h3",{id:"setup-guide"},"Setup guide"),(0,o.kt)("ol",null,(0,o.kt)("li",{parentName:"ol"},"To find your API key, navigate to your ",(0,o.kt)("a",{parentName:"li",href:"https://airtable.com/account"},"account page"),'. On your account overview page, under the API heading, there\'s a button that says "Generate API key."\n',(0,o.kt)("img",{alt:"img.png",src:a(43180).Z,width:"1536",height:"1118"})),(0,o.kt)("li",{parentName:"ol"},"Generate an API key by clicking the button. If one already exists, click the key to reveal it and copy it. ",(0,o.kt)("img",{alt:"img.png",src:a(50685).Z,width:"797",height:"256"}),". See ",(0,o.kt)("a",{parentName:"li",href:"https://support.airtable.com/hc/en-us/articles/219046777-How-do-I-get-my-API-key-"},"here")," for more information on managing your API keys. "),(0,o.kt)("li",{parentName:"ol"},"Find the Airtable base containing the tables you'd like to replicate by visiting ",(0,o.kt)("a",{parentName:"li",href:"https://airtable.com/api"},"https://airtable.com/api")," and logging in. Once you're logged in, you'll see a list of available bases: ",(0,o.kt)("img",{alt:"bases",src:a(48708).Z,width:"333",height:"188"}),". Click the base whose tables you want to replicate. You'll find the base ID on the next page: ",(0,o.kt)("img",{src:a(11915).Z,width:"327",height:"65"}),". Copy this ID for use when configuring the connector. ")),(0,o.kt)("h3",{id:"performance-considerations-airbyte-open-source"},"Performance Considerations (Airbyte Open-Source)"),(0,o.kt)("p",null,"See information about rate limits ",(0,o.kt)("a",{parentName:"p",href:"https://support.airtable.com/hc/en-us/articles/203313985-Public-REST-API"},"here"),"."),(0,o.kt)("h2",{id:"changelog"},"Changelog"),(0,o.kt)("table",null,(0,o.kt)("thead",{parentName:"table"},(0,o.kt)("tr",{parentName:"thead"},(0,o.kt)("th",{parentName:"tr",align:"left"},"Version"),(0,o.kt)("th",{parentName:"tr",align:"left"},"Date"),(0,o.kt)("th",{parentName:"tr",align:"left"},"Pull Request"),(0,o.kt)("th",{parentName:"tr",align:"left"},"Subject"))),(0,o.kt)("tbody",{parentName:"table"},(0,o.kt)("tr",{parentName:"tbody"},(0,o.kt)("td",{parentName:"tr",align:"left"},"0.1.2"),(0,o.kt)("td",{parentName:"tr",align:"left"},"2022-04-30"),(0,o.kt)("td",{parentName:"tr",align:"left"},(0,o.kt)("a",{parentName:"td",href:"https://github.com/airbytehq/airbyte/pull/12500"},"12500")),(0,o.kt)("td",{parentName:"tr",align:"left"},"Improve input configuration copy")),(0,o.kt)("tr",{parentName:"tbody"},(0,o.kt)("td",{parentName:"tr",align:"left"},"0.1.1"),(0,o.kt)("td",{parentName:"tr",align:"left"},"2021-12-06"),(0,o.kt)("td",{parentName:"tr",align:"left"},(0,o.kt)("a",{parentName:"td",href:"https://github.com/airbytehq/airbyte/pull/8425"},"8425")),(0,o.kt)("td",{parentName:"tr",align:"left"},"Update title, description fields in spec")))))}g.isMDXComponent=!0},43180:function(e,t,a){t.Z=a.p+"assets/images/airtable_api_key1-4a5489a74b8c27064cb38cdd441669df.png"},50685:function(e,t,a){t.Z=a.p+"assets/images/airtable_api_key2-572c86c9c24d4a9a82bf0f4d9d2a83b0.png"},11915:function(e,t){t.Z="data:image/png;base64,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"},48708:function(e,t,a){t.Z=a.p+"assets/images/airtable_bases_ui_list1-bbabeb41d35c9bee3988e354297ad77f.png"}}]);