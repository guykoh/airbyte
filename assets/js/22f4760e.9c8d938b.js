"use strict";(self.webpackChunkdocu=self.webpackChunkdocu||[]).push([[4020],{3905:function(e,t,r){r.d(t,{Zo:function(){return c},kt:function(){return d}});var n=r(67294);function a(e,t,r){return t in e?Object.defineProperty(e,t,{value:r,enumerable:!0,configurable:!0,writable:!0}):e[t]=r,e}function s(e,t){var r=Object.keys(e);if(Object.getOwnPropertySymbols){var n=Object.getOwnPropertySymbols(e);t&&(n=n.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),r.push.apply(r,n)}return r}function o(e){for(var t=1;t<arguments.length;t++){var r=null!=arguments[t]?arguments[t]:{};t%2?s(Object(r),!0).forEach((function(t){a(e,t,r[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(r)):s(Object(r)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(r,t))}))}return e}function i(e,t){if(null==e)return{};var r,n,a=function(e,t){if(null==e)return{};var r,n,a={},s=Object.keys(e);for(n=0;n<s.length;n++)r=s[n],t.indexOf(r)>=0||(a[r]=e[r]);return a}(e,t);if(Object.getOwnPropertySymbols){var s=Object.getOwnPropertySymbols(e);for(n=0;n<s.length;n++)r=s[n],t.indexOf(r)>=0||Object.prototype.propertyIsEnumerable.call(e,r)&&(a[r]=e[r])}return a}var l=n.createContext({}),u=function(e){var t=n.useContext(l),r=t;return e&&(r="function"==typeof e?e(t):o(o({},t),e)),r},c=function(e){var t=u(e.components);return n.createElement(l.Provider,{value:t},e.children)},p={inlineCode:"code",wrapper:function(e){var t=e.children;return n.createElement(n.Fragment,{},t)}},m=n.forwardRef((function(e,t){var r=e.components,a=e.mdxType,s=e.originalType,l=e.parentName,c=i(e,["components","mdxType","originalType","parentName"]),m=u(r),d=a,f=m["".concat(l,".").concat(d)]||m[d]||p[d]||s;return r?n.createElement(f,o(o({ref:t},c),{},{components:r})):n.createElement(f,o({ref:t},c))}));function d(e,t){var r=arguments,a=t&&t.mdxType;if("string"==typeof e||a){var s=r.length,o=new Array(s);o[0]=m;var i={};for(var l in t)hasOwnProperty.call(t,l)&&(i[l]=t[l]);i.originalType=e,i.mdxType="string"==typeof e?e:a,o[1]=i;for(var u=2;u<s;u++)o[u]=r[u];return n.createElement.apply(null,o)}return n.createElement.apply(null,r)}m.displayName="MDXCreateElement"},83505:function(e,t,r){r.r(t),r.d(t,{assets:function(){return c},contentTitle:function(){return l},default:function(){return d},frontMatter:function(){return i},metadata:function(){return u},toc:function(){return p}});var n=r(87462),a=r(63366),s=(r(67294),r(3905)),o=["components"],i={},l="Harness",u={unversionedId:"integrations/sources/harness",id:"integrations/sources/harness",title:"Harness",description:"Overview",source:"@site/../docs/integrations/sources/harness.md",sourceDirName:"integrations/sources",slug:"/integrations/sources/harness",permalink:"/integrations/sources/harness",draft:!1,editUrl:"https://github.com/airbytehq/airbyte/blob/master/docs/../docs/integrations/sources/harness.md",tags:[],version:"current",frontMatter:{},sidebar:"mySidebar",previous:{title:"Greenhouse",permalink:"/integrations/sources/greenhouse"},next:{title:"Harvest",permalink:"/integrations/sources/harvest"}},c={},p=[{value:"Overview",id:"overview",level:2},{value:"Output schema",id:"output-schema",level:3},{value:"Features",id:"features",level:3},{value:"Performance considerations",id:"performance-considerations",level:3},{value:"Getting started",id:"getting-started",level:2},{value:"Requirements",id:"requirements",level:3},{value:"Changelog",id:"changelog",level:2}],m={toc:p};function d(e){var t=e.components,r=(0,a.Z)(e,o);return(0,s.kt)("wrapper",(0,n.Z)({},m,r,{components:t,mdxType:"MDXLayout"}),(0,s.kt)("h1",{id:"harness"},"Harness"),(0,s.kt)("h2",{id:"overview"},"Overview"),(0,s.kt)("p",null,"The Harness source is maintained by ",(0,s.kt)("a",{parentName:"p",href:"https://github.com/faros-ai/airbyte-connectors/tree/main/sources/harness-source"},"Faros\nAI"),".\nPlease file any support requests on that repo to minimize response time from the\nmaintainers. The source supports both Full Refresh and Incremental syncs. You\ncan choose if this source will copy only the new or updated data, or all rows in\nthe tables and columns you set up for replication, every time a sync is run."),(0,s.kt)("h3",{id:"output-schema"},"Output schema"),(0,s.kt)("p",null,"Only one stream is currently available from this source:"),(0,s.kt)("ul",null,(0,s.kt)("li",{parentName:"ul"},(0,s.kt)("a",{parentName:"li",href:"https://docs.harness.io/article/ba4vs50071-use-workflows-api"},"Executions")," ","(","Incremental",")")),(0,s.kt)("p",null,"If there are more endpoints you'd like Faros AI to support, please ",(0,s.kt)("a",{parentName:"p",href:"https://github.com/faros-ai/airbyte-connectors/issues/new"},"create an\nissue.")),(0,s.kt)("h3",{id:"features"},"Features"),(0,s.kt)("table",null,(0,s.kt)("thead",{parentName:"table"},(0,s.kt)("tr",{parentName:"thead"},(0,s.kt)("th",{parentName:"tr",align:"left"},"Feature"),(0,s.kt)("th",{parentName:"tr",align:"left"},"Supported?"))),(0,s.kt)("tbody",{parentName:"table"},(0,s.kt)("tr",{parentName:"tbody"},(0,s.kt)("td",{parentName:"tr",align:"left"},"Full Refresh Sync"),(0,s.kt)("td",{parentName:"tr",align:"left"},"Yes")),(0,s.kt)("tr",{parentName:"tbody"},(0,s.kt)("td",{parentName:"tr",align:"left"},"Incremental Sync"),(0,s.kt)("td",{parentName:"tr",align:"left"},"Yes")),(0,s.kt)("tr",{parentName:"tbody"},(0,s.kt)("td",{parentName:"tr",align:"left"},"SSL connection"),(0,s.kt)("td",{parentName:"tr",align:"left"},"Yes")),(0,s.kt)("tr",{parentName:"tbody"},(0,s.kt)("td",{parentName:"tr",align:"left"},"Namespaces"),(0,s.kt)("td",{parentName:"tr",align:"left"},"No")))),(0,s.kt)("h3",{id:"performance-considerations"},"Performance considerations"),(0,s.kt)("p",null,"The Harness source should not run into Harness API limitations under normal\nusage.  Please ",(0,s.kt)("a",{parentName:"p",href:"https://github.com/faros-ai/airbyte-connectors/issues/new"},"create an\nissue")," if you see any\nrate limit issues that are not automatically retried successfully."),(0,s.kt)("h2",{id:"getting-started"},"Getting started"),(0,s.kt)("h3",{id:"requirements"},"Requirements"),(0,s.kt)("ul",null,(0,s.kt)("li",{parentName:"ul"},"Harness Account Id"),(0,s.kt)("li",{parentName:"ul"},"Harness API Key"),(0,s.kt)("li",{parentName:"ul"},"Harness API URL, if using a self-hosted Harness instance")),(0,s.kt)("p",null,"Please follow the ",(0,s.kt)("a",{parentName:"p",href:"https://ngdocs.harness.io/article/tdoad7xrh9-add-and-manage-api-keys#harness_api_key"},"their documentation for generating a Harness API\nKey"),"."),(0,s.kt)("h2",{id:"changelog"},"Changelog"),(0,s.kt)("table",null,(0,s.kt)("thead",{parentName:"table"},(0,s.kt)("tr",{parentName:"thead"},(0,s.kt)("th",{parentName:"tr",align:"left"},"Version"),(0,s.kt)("th",{parentName:"tr",align:"left"},"Date"),(0,s.kt)("th",{parentName:"tr",align:"left"},"Pull Request"),(0,s.kt)("th",{parentName:"tr",align:"left"},"Subject"))),(0,s.kt)("tbody",{parentName:"table"},(0,s.kt)("tr",{parentName:"tbody"},(0,s.kt)("td",{parentName:"tr",align:"left"},"0.1.23"),(0,s.kt)("td",{parentName:"tr",align:"left"},"2021-11-16"),(0,s.kt)("td",{parentName:"tr",align:"left"},(0,s.kt)("a",{parentName:"td",href:"https://github.com/faros-ai/airbyte-connectors/pull/153"},"153")),(0,s.kt)("td",{parentName:"tr",align:"left"},"Add Harness source and Faros destination's converter")))))}d.isMDXComponent=!0}}]);