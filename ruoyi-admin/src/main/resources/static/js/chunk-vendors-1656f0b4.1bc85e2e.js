"use strict";(self.webpackChunksystem=self.webpackChunksystem||[]).push([[9218],{98005:function(e,t,n){function r(){return"undefined"!=typeof navigator&&"undefined"!=typeof window?window:void 0!==n.g?n.g:{}}n.d(t,{F1:function(){return u}}),n(57658);const o="function"==typeof Proxy,a="devtools-plugin:setup";let i,s;function l(){return void 0===i&&("undefined"!=typeof window&&window.performance?(i=!0,s=window.performance):void 0!==n.g&&null!=(e=n.g.perf_hooks)&&e.performance?(i=!0,s=n.g.perf_hooks.performance):i=!1),(i?s:Date).now();var e}class c{constructor(e,t){this.target=null,this.targetQueue=[],this.onQueue=[],this.plugin=e,this.hook=t;var n={};if(e.settings)for(const l in e.settings){var r=e.settings[l];n[l]=r.defaultValue}const o="__vue-devtools-plugin-settings__"+e.id;let a=Object.assign({},n);try{var i=localStorage.getItem(o),s=JSON.parse(i);Object.assign(a,s)}catch(e){}this.fallbacks={getSettings(){return a},setSettings(e){try{localStorage.setItem(o,JSON.stringify(e))}catch(e){}a=e},now(){return l()}},t&&t.on("plugin:settings:set",((e,t)=>{e===this.plugin.id&&this.fallbacks.setSettings(t)})),this.proxiedOn=new Proxy({},{get:(e,t)=>this.target?this.target.on[t]:(...e)=>{this.onQueue.push({method:t,args:e})}}),this.proxiedTarget=new Proxy({},{get:(e,t)=>this.target?this.target[t]:"on"===t?this.proxiedOn:Object.keys(this.fallbacks).includes(t)?(...e)=>(this.targetQueue.push({method:t,args:e,resolve:()=>{}}),this.fallbacks[t](...e)):(...e)=>new Promise((n=>{this.targetQueue.push({method:t,args:e,resolve:n})}))})}async setRealTarget(e){this.target=e;for(const t of this.onQueue)this.target.on[t.method](...t.args);for(const t of this.targetQueue)t.resolve(await this.target[t.method](...t.args))}}function u(e,t){var n=e,i=r(),s=r().__VUE_DEVTOOLS_GLOBAL_HOOK__,l=o&&n.enableEarlyProxy;!s||!i.__VUE_DEVTOOLS_PLUGIN_API_AVAILABLE__&&l?(l=l?new c(n,s):null,(i.__VUE_DEVTOOLS_PLUGINS__=i.__VUE_DEVTOOLS_PLUGINS__||[]).push({pluginDescriptor:n,setupFn:t,proxy:l}),l&&t(l.proxiedTarget)):s.emit(a,e,t)}},40089:function(e,t){t.Z=(e,t)=>{var n,r,o=e.__vccOpts||e;for([n,r]of t)o[n]=r;return o}},73701:function(e,t,n){n.d(t,{$B:function(){return o},$Q:function(){return r}});var r=!1,o=!0},22483:function(e,t,n){n.d(t,{PO:function(){return L},p7:function(){return je},tv:function(){return $e},yj:function(){return Le}}),n(57658),n(21703);var r,o,a=n(73396),i=n(44870);
/*!
  * vue-router v4.2.2
  * (c) 2023 Eduardo San Martin Morote
  * @license MIT
  */const s="undefined"!=typeof window;function l(e){return e.__esModule||"Module"===e[Symbol.toStringTag]}const c=Object.assign;function u(e,t){var n={};for(const o in t){var r=t[o];n[o]=p(r)?r.map(e):e(r)}return n}const f=()=>{},p=Array.isArray,h=/\/$/,d=e=>e.replace(h,"");function v(e,t,n="/"){let r,o={},a="",i="";var s=t.indexOf("#");let l=t.indexOf("?");return-1<(l=s<l&&0<=s?-1:l)&&(r=t.slice(0,l),a=t.slice(l+1,-1<s?s:t.length),o=e(a)),-1<s&&(r=r||t.slice(0,s),i=t.slice(s,t.length)),{fullPath:(r=function(e,t){if(e.startsWith("/"))return e;if(!e)return t;t=t.split("/");var n=e.split("/");e=n[n.length-1];".."!==e&&"."!==e||n.push("");let r,o,a=t.length-1;for(r=0;r<n.length;r++)if("."!==(o=n[r])){if(".."!==o)break;1<a&&a--}return t.slice(0,a).join("/")+"/"+n.slice(r-(r===n.length?1:0)).join("/")}(null!=r?r:t,n))+(a&&"?")+a+i,path:r,query:o,hash:i}}function g(e,t){return t&&e.toLowerCase().startsWith(t.toLowerCase())?e.slice(t.length)||"/":e}function m(e,t){return(e.aliasOf||e)===(t.aliasOf||t)}function y(e,t){if(Object.keys(e).length!==Object.keys(t).length)return!1;for(const o in e)if(n=e[o],r=t[o],!(p(n)?b(n,r):p(r)?b(r,n):n===r))return!1;var n,r;return!0}function b(e,t){return p(t)?e.length===t.length&&e.every(((e,n)=>e===t[n])):1===e.length&&e[0]===t}function w(e){var t;return"/"!==(e=e||(s?(e=(t=document.querySelector("base"))&&t.getAttribute("href")||"/").replace(/^\w+:\/\/[^\/]+/,""):"/"))[0]&&"#"!==e[0]&&(e="/"+e),d(e)}(t=r=r||{}).pop="pop",t.push="push",(n=o=o||{}).back="back",n.forward="forward",n.unknown="";const E=/^[^#]+#/;function O(e,t){return e.replace(E,"#")+t}const k=()=>({left:window.pageXOffset,top:window.pageYOffset});function _(e){let t;if("el"in e){var n=e.el,r="string"==typeof n&&n.startsWith("#");r="string"==typeof n?r?document.getElementById(n.slice(1)):document.querySelector(n):n;if(!r)return;n=r,r=e,o=document.documentElement.getBoundingClientRect(),n=n.getBoundingClientRect(),t={behavior:r.behavior,left:n.left-o.left-(r.left||0),top:n.top-o.top-(r.top||0)}}else t=e;var o;"scrollBehavior"in document.documentElement.style?window.scrollTo(t):window.scrollTo(null!=t.left?t.left:window.pageXOffset,null!=t.top?t.top:window.pageYOffset)}function P(e,t){return(history.state?history.state.position-t:-1)+e}const S=new Map;let R=()=>location.protocol+"//"+location.host;function x(e,t){var{pathname:t,search:n,hash:r}=t,o=e.indexOf("#");if(-1<o){o=r.includes(e.slice(o))?e.slice(o).length:1;let t=r.slice(o);return g(t="/"!==t[0]?"/"+t:t,"")}return g(t,e)+n+r}function C(e,t,n,a){let i=[],s=[],l=null;const u=({state:s})=>{var c=x(e,location);const u=n.value;var f=t.value;let p=0;if(s){if(n.value=c,t.value=s,l&&l===u)return void(l=null);p=f?s.position-f.position:0}else a(c);i.forEach((e=>{e(n.value,u,{delta:p,type:r.pop,direction:p?0<p?o.forward:o.back:o.unknown})}))};function f(){var e=window["history"];e.state&&e.replaceState(c({},e.state,{scroll:k()}),"")}return window.addEventListener("popstate",u),window.addEventListener("beforeunload",f,{passive:!0}),{pauseListeners:function(){l=n.value},listen:function(e){i.push(e);var t=()=>{var t=i.indexOf(e);-1<t&&i.splice(t,1)};return s.push(t),t},destroy:function(){for(const e of s)e();s=[],window.removeEventListener("popstate",u),window.removeEventListener("beforeunload",f)}}}function j(e,t,n,r=!1,o=!1){return{back:e,current:t,forward:n,replaced:r,position:window.history.length,scroll:o?k():null}}function $(e){const{history:t,location:n}=window,r={value:x(e,n)},o={value:t.state};function a(r,a,i){var s=e.indexOf("#");s=-1<s?(n.host&&document.querySelector("base")?e:e.slice(s))+r:R()+e+r;try{t[i?"replaceState":"pushState"](a,"",s),o.value=a}catch(r){n[i?"replace":"assign"](s)}}return o.value||a(r.value,{back:null,current:r.value,forward:null,position:t.length-1,replaced:!0,scroll:null},!0),{location:r,state:o,push:function(e,n){a((i=c({},o.value,t.state,{forward:e,scroll:k()})).current,i,!0);var i=c({},j(r.value,e,null),{position:i.position+1},n);a(e,i,!1),r.value=e},replace:function(e,n){a(e,c({},t.state,j(o.value.back,e,o.value.forward,!0),n,{position:o.value.position}),!0),r.value=e}}}function L(e){const t=$(e=w(e)),n=C(e,t.state,t.location,t.replace);return e=c({location:"",base:e,go:function(e,t=!0){t||n.pauseListeners(),history.go(e)},createHref:O.bind(null,e)},t,n),Object.defineProperty(e,"location",{enumerable:!0,get:()=>t.location.value}),Object.defineProperty(e,"state",{enumerable:!0,get:()=>t.state.value}),e}function A(e){return"string"==typeof e||"symbol"==typeof e}const F={path:"/",name:void 0,params:{},query:{},hash:"",fullPath:"/",matched:[],meta:{},redirectedFrom:void 0},U=Symbol("");function q(e,t){return c(new Error,{type:e,[U]:!0},t)}function I(e,t){return e instanceof Error&&U in e&&(null==t||e.type&t)}const V={sensitive:!1,strict:!1,start:!0,end:!0},B=/[.+*?^${}()[\]/\\]/g;function G(e,t){var n=c({},V,t),r=[];let o=n.start?"^":"";const a=[];for(const c of e){var i=c.length?[]:[90];n.strict&&!c.length&&(o+="/");for(let e=0;e<c.length;e++){var s=c[e];let r=40+(n.sensitive?.25:0);if(0===s.type)e||(o+="/"),o+=s.value.replace(B,"\\$&"),r+=40;else if(1===s.type){var{value:s,repeatable:l,optional:u,regexp:f}=s;a.push({name:s,repeatable:l,optional:u});const n=f||"[^/]+?";if("[^/]+?"!==n){r+=10;try{new RegExp(`(${n})`)}catch(t){throw new Error(`Invalid custom RegExp for param "${s}" (${n}): `+t.message)}}let i=l?`((?:${n})(?:/(?:${n}))*)`:`(${n})`;e||(i=u&&c.length<2?`(?:/${i})`:"/"+i),u&&(i+="?"),o+=i,r+=20,u&&(r+=-8),l&&(r+=-20),".*"===n&&(r+=-50)}i.push(r)}r.push(i)}n.strict&&n.end&&(r[t=r.length-1][r[t].length-1]+=.7000000000000001),n.strict||(o+="/?"),n.end?o+="$":n.strict&&(o+="(?:/|$)");const h=new RegExp(o,n.sensitive?"":"i");return{re:h,score:r,keys:a,parse:function(e){var t=e.match(h),n={};if(!t)return null;for(let i=1;i<t.length;i++){var r=t[i]||"",o=a[i-1];n[o.name]=r&&o.repeatable?r.split("/"):r}return n},stringify:function(t){let n="",r=!1;for(const l of e){r&&n.endsWith("/")||(n+="/"),r=!1;for(const e of l)if(0===e.type)n+=e.value;else if(1===e.type){var{value:o,repeatable:a,optional:i}=e,s=o in t?t[o]:"";if(p(s)&&!a)throw new Error(`Provided param "${o}" is an array but it is not repeatable (* or + modifiers)`);if(a=p(s)?s.join("/"):s,!a){if(!i)throw new Error(`Missing required param "${o}"`);l.length<2&&(n.endsWith("/")?n=n.slice(0,-1):r=!0)}n+=a}}return n||"/"}}}function D(e,t){let n=0;for(var r=e.score,o=t.score;n<r.length&&n<o.length;){var a=function(e,t){let n=0;for(;n<e.length&&n<t.length;){var r=t[n]-e[n];if(r)return r;n++}return e.length<t.length?1===e.length&&80===e[0]?-1:1:e.length>t.length?1===t.length&&80===t[0]?1:-1:0}(r[n],o[n]);if(a)return a;n++}if(1===Math.abs(o.length-r.length)){if(M(r))return 1;if(M(o))return-1}return o.length-r.length}function M(e){var t=e[e.length-1];return 0<e.length&&t[t.length-1]<0}const T={type:0,value:""},Q=/[a-zA-Z0-9_]/;function J(e,t,n){return n=G(function(e){if(!e)return[[]];if("/"===e)return[[T]];if(!e.startsWith("/"))throw new Error(`Invalid path "${e}"`);function t(e){throw new Error(`ERR (${n})/"${c}": `+e)}let n=0,r=n;const o=[];let a;function i(){a&&o.push(a),a=[]}let s,l=0,c="",u="";function f(){c&&(0===n?a.push({type:0,value:c}):1===n||2===n||3===n?(1<a.length&&("*"===s||"+"===s)&&t(`A repeatable param (${c}) must be alone in its segment. eg: '/:ids+.`),a.push({type:1,value:c,regexp:u,repeatable:"*"===s||"+"===s,optional:"*"===s||"?"===s})):t("Invalid state to consume buffer"),c="")}function p(){c+=s}for(;l<e.length;)if("\\"===(s=e[l++])&&2!==n)r=n,n=4;else switch(n){case 0:"/"===s?(c&&f(),i()):":"===s?(f(),n=1):p();break;case 4:p(),n=r;break;case 1:"("===s?n=2:Q.test(s)?p():(f(),n=0,"*"!==s&&"?"!==s&&"+"!==s&&l--);break;case 2:")"===s?"\\"==u[u.length-1]?u=u.slice(0,-1)+s:n=3:u+=s;break;case 3:f(),n=0,"*"!==s&&"?"!==s&&"+"!==s&&l--,u="";break;default:t("Unknown state")}return 2===n&&t(`Unfinished custom RegExp for param "${c}"`),f(),i(),o}(e.path),n),n=c(n,{record:e,parent:t,children:[],alias:[]}),t&&!n.record.aliasOf==!t.record.aliasOf&&t.children.push(n),n}function W(e,t){const n=[],r=new Map;function o(e,i,s){var l,u,p=!s,h={path:(l=e).path,redirect:l.redirect,name:l.name,meta:l.meta||{},aliasOf:void 0,beforeEnter:l.beforeEnter,props:function(e){var t={},n=e.props||!1;if("component"in e)t.default=n;else for(const r in e.components)t[r]="boolean"==typeof n?n:n[r];return t}(l),children:l.children||[],instances:{},leaveGuards:new Set,updateGuards:new Set,enterCallbacks:{},components:"components"in l?l.components||null:l.component&&{default:l.component}},d=(h.aliasOf=s&&s.record,K(t,e)),v=[h];if("alias"in e)for(const t of"string"==typeof e.alias?[e.alias]:e.alias)v.push(c({},h,{components:(s?s.record:h).components,path:t,aliasOf:s?s.record:h}));let g;for(const t of v){var m,y=t["path"];if(i&&"/"!==y[0]&&(m="/"===(m=i.record.path)[m.length-1]?"":"/",t.path=i.record.path+(y&&m+y)),u=J(t,i,d),s?s.alias.push(u):((g=g||u)!==u&&g.alias.push(u),p&&e.name&&!H(u)&&a(e.name)),h.children){var b=h.children;for(let e=0;e<b.length;e++)o(b[e],u,s&&s.children[e])}if(s=s||u,u.record.components&&Object.keys(u.record.components).length||u.record.name||u.record.redirect){w=void 0;var w=u;let e=0;for(;e<n.length&&0<=D(w,n[e])&&(w.record.path!==n[e].record.path||!function e(t,n){return n.children.some((n=>n===t||e(t,n)))}(w,n[e]));)e++;n.splice(e,0,w),w.record.name&&!H(w)&&r.set(w.record.name,w)}}return g?()=>{a(g)}:f}function a(e){var t;A(e)?(t=r.get(e))&&(r.delete(e),n.splice(n.indexOf(t),1),t.children.forEach(a),t.alias.forEach(a)):-1<(t=n.indexOf(e))&&(n.splice(t,1),e.record.name&&r.delete(e.record.name),e.children.forEach(a),e.alias.forEach(a))}return t=K({strict:!1,end:!0,sensitive:!1},t),e.forEach((e=>o(e))),{addRoute:o,resolve:function(e,t){let o,a,i,s={};if("name"in e&&e.name){if(!(o=r.get(e.name)))throw q(1,{location:e});i=o.record.name,s=c(N(t.params,o.keys.filter((e=>!e.optional)).map((e=>e.name))),e.params&&N(e.params,o.keys.map((e=>e.name)))),a=o.stringify(s)}else if("path"in e)a=e.path,(o=n.find((e=>e.re.test(a))))&&(s=o.parse(a),i=o.record.name);else{if(!(o=t.name?r.get(t.name):n.find((e=>e.re.test(t.path)))))throw q(1,{location:e,currentLocation:t});i=o.record.name,s=c({},t.params,e.params),a=o.stringify(s)}var l=[];let u=o;for(;u;)l.unshift(u.record),u=u.parent;return{name:i,path:a,params:s,matched:l,meta:l.reduce(((e,t)=>c(e,t.meta)),{})}},removeRoute:a,getRoutes:function(){return n},getRecordMatcher:function(e){return r.get(e)}}}function N(e,t){var n={};for(const r of t)r in e&&(n[r]=e[r]);return n}function H(e){for(;e;){if(e.record.aliasOf)return 1;e=e.parent}}function K(e,t){var n={};for(const r in e)n[r]=(r in t?t:e)[r];return n}const z=/#/g,Y=/&/g,Z=/\//g,X=/=/g,ee=/\?/g,te=/\+/g,ne=/%5B/g,re=/%5D/g,oe=/%5E/g,ae=/%60/g,ie=/%7B/g,se=/%7C/g,le=/%7D/g,ce=/%20/g;function ue(e){return encodeURI(""+e).replace(se,"|").replace(ne,"[").replace(re,"]")}function fe(e){return ue(e).replace(te,"%2B").replace(ce,"+").replace(z,"%23").replace(Y,"%26").replace(ae,"`").replace(ie,"{").replace(le,"}").replace(oe,"^")}function pe(e){return null==e?"":ue(e).replace(z,"%23").replace(ee,"%3F").replace(Z,"%2F")}function he(e){try{return decodeURIComponent(""+e)}catch(e){}return""+e}function de(e){var t={};if(""!==e&&"?"!==e){var n=("?"===e[0]?e.slice(1):e).split("&");for(let e=0;e<n.length;++e){var r=n[e].replace(te," "),o=r.indexOf("="),a=he(o<0?r:r.slice(0,o));r=o<0?null:he(r.slice(o+1));if(a in t){let e=t[a];(e=p(e)?e:t[a]=[e]).push(r)}else t[a]=r}}return t}function ve(e){let t="";for(let r in e){var n=e[r];r=fe(r).replace(X,"%3D"),null==n?void 0!==n&&(t+=(t.length?"&":"")+r):(p(n)?n.map((e=>e&&fe(e))):[n&&fe(n)]).forEach((e=>{void 0!==e&&(t+=(t.length?"&":"")+r,null!=e)&&(t+="="+e)}))}return t}const ge=Symbol(""),me=Symbol(""),ye=Symbol(""),be=Symbol(""),we=Symbol("");function Ee(){let e=[];return{add:function(t){return e.push(t),()=>{var n=e.indexOf(t);-1<n&&e.splice(n,1)}},list:()=>e,reset:function(){e=[]}}}function Oe(e,t,n,r,o){const a=r&&(r.enterCallbacks[o]=r.enterCallbacks[o]||[]);return()=>new Promise(((i,s)=>{var l=e=>{var l;!1===e?s(q(4,{from:n,to:t})):e instanceof Error?s(e):"string"==typeof(l=e)||l&&"object"==typeof l?s(q(2,{from:t,to:e})):(a&&r.enterCallbacks[o]===a&&"function"==typeof e&&a.push(e),i())},c=e.call(r&&r.instances[o],t,n,l);let u=Promise.resolve(c);(u=e.length<3?u.then(l):u).catch((e=>s(e)))}))}function ke(e,t,n,r){var o=[];for(const s of e)for(const e in s.components){var a=s.components[e];if("beforeRouteEnter"===t||s.instances[e])if("object"==typeof(i=a)||"displayName"in i||"props"in i||"__vccOpts"in i){var i=(a.__vccOpts||a)[t];i&&o.push(Oe(i,n,r,s,e))}else{let i=a();o.push((()=>i.then((o=>o?(o=l(o)?o.default:o,(o=((s.components[e]=o).__vccOpts||o)[t])&&Oe(o,n,r,s,e)()):Promise.reject(new Error(`Couldn't resolve component "${e}" at "${s.path}"`))))))}}return o}function _e(e){const t=(0,a.f3)(ye),n=(0,a.f3)(be),r=(0,a.Fl)((()=>t.resolve((0,i.SU)(e.to)))),o=(0,a.Fl)((()=>{var e,t,o=r.value["matched"],a=o["length"],i=o[a-1],s=n.matched;return i&&s.length?!(-1<(e=s.findIndex(m.bind(null,i))))&&(t=Se(o[a-2]),1<a)&&Se(i)===t&&s[s.length-1].path!==t?s.findIndex(m.bind(null,o[a-2])):e:-1}));var s=(0,a.Fl)((()=>-1<o.value&&function(e,t){for(const r in t){var n=t[r];const o=e[r];if("string"==typeof n){if(n!==o)return!1}else if(!p(o)||o.length!==n.length||n.some(((e,t)=>e!==o[t])))return!1}return!0}(n.params,r.value.params))),l=(0,a.Fl)((()=>-1<o.value&&o.value===n.matched.length-1&&y(n.params,r.value.params)));return{route:r,href:(0,a.Fl)((()=>r.value.href)),isActive:s,isExactActive:l,navigate:function(n={}){return function(e){if(!(e.metaKey||e.altKey||e.ctrlKey||e.shiftKey||e.defaultPrevented||void 0!==e.button&&0!==e.button)){if(e.currentTarget&&e.currentTarget.getAttribute){var t=e.currentTarget.getAttribute("target");if(/\b_blank\b/i.test(t))return}return e.preventDefault&&e.preventDefault(),1}}(n)?t[(0,i.SU)(e.replace)?"replace":"push"]((0,i.SU)(e.to)).catch(f):Promise.resolve()}}}const Pe=(0,a.aZ)({name:"RouterLink",compatConfig:{MODE:3},props:{to:{type:[String,Object],required:!0},replace:Boolean,activeClass:String,exactActiveClass:String,custom:Boolean,ariaCurrentValue:{type:String,default:"page"}},useLink:_e,setup(e,{slots:t}){const n=(0,i.qj)(_e(e)),r=(0,a.f3)(ye)["options"],o=(0,a.Fl)((()=>({[Re(e.activeClass,r.linkActiveClass,"router-link-active")]:n.isActive,[Re(e.exactActiveClass,r.linkExactActiveClass,"router-link-exact-active")]:n.isExactActive})));return()=>{var r=t.default&&t.default(n);return e.custom?r:(0,a.h)("a",{"aria-current":n.isExactActive?e.ariaCurrentValue:null,href:n.href,onClick:n.navigate,class:o.value},r)}}});function Se(e){return e?(e.aliasOf||e).path:""}const Re=(e,t,n)=>null!=e?e:null!=t?t:n;function xe(e,t){return e?1===(e=e(t)).length?e[0]:e:null}const Ce=(0,a.aZ)({name:"RouterView",inheritAttrs:!1,props:{name:{type:String,default:"default"},route:Object},compatConfig:{MODE:3},setup(e,{attrs:t,slots:n}){const r=(0,a.f3)(we),o=(0,a.Fl)((()=>e.route||r.value)),s=(0,a.f3)(me,0),l=(0,a.Fl)((()=>{let e=(0,i.SU)(s);for(var t,n=o.value["matched"];(t=n[e])&&!t.components;)e++;return e})),u=(0,a.Fl)((()=>o.value.matched[l.value])),f=((0,a.JJ)(me,(0,a.Fl)((()=>l.value+1))),(0,a.JJ)(ge,u),(0,a.JJ)(we,o),(0,i.iH)());return(0,a.YP)((()=>[f.value,u.value,e.name]),(([e,t,n],[r,o])=>{t&&(t.instances[n]=e,o)&&o!==t&&e&&e===r&&(t.leaveGuards.size||(t.leaveGuards=o.leaveGuards),t.updateGuards.size||(t.updateGuards=o.updateGuards)),!e||!t||o&&m(t,o)&&r||(t.enterCallbacks[n]||[]).forEach((t=>t(e)))}),{flush:"post"}),()=>{var r=o.value;const i=e.name,s=u.value;var l,p=s&&s.components[i];return p?(l=(l=s.props[i])?!0===l?r.params:"function"==typeof l?l(r):l:null,l=(0,a.h)(p,c({},l,t,{onVnodeUnmounted:e=>{e.component.isUnmounted&&(s.instances[i]=null)},ref:f})),xe(n.default,{Component:l,route:r})||l):xe(n.default,{Component:p,route:r})}}});function je(e){const t=W(e.routes,e),n=e.parseQuery||de,o=e.stringifyQuery||ve,l=e.history,h=Ee(),d=Ee(),g=Ee(),b=(0,i.XI)(F);let w=F;s&&e.scrollBehavior&&"scrollRestoration"in history&&(history.scrollRestoration="manual");const E=u.bind(null,(e=>""+e)),O=u.bind(null,pe),R=u.bind(null,he);function x(e,r){if(r=c({},r||b.value),"string"==typeof e){var a=v(n,e,r.path);const o=t.resolve({path:a.path},r),i=l.createHref(a.fullPath);return c(a,o,{params:R(o.params),hash:he(a.hash),redirectedFrom:void 0,href:i})}let i;if("path"in e)i=c({},e,{path:v(n,e.path,r.path).path});else{var s=c({},e.params);for(const e in s)null==s[e]&&delete s[e];i=c({},e,{params:O(s)}),r.params=O(r.params)}const u=t.resolve(i,r);var f;a=e.hash||"",u.params=E(R(u.params)),r=o,f=c({},e,{hash:ue(a).replace(ie,"{").replace(le,"}").replace(oe,"^"),path:u.path}),r=f.query?r(f.query):"",r=f.path+(r&&"?")+r+(f.hash||"");const h=l.createHref(r);return c({fullPath:r,hash:a,query:o===ve?function(e){var t={};for(const r in e){var n=e[r];void 0!==n&&(t[r]=p(n)?n.map((e=>null==e?null:""+e)):null==n?n:""+n)}return t}(e.query):e.query||{}},u,{redirectedFrom:void 0,href:h})}function C(e){return"string"==typeof e?v(n,e,b.value.path):c({},e)}function j(e,t){if(w!==e)return q(8,{from:t,to:e})}function $(e){return U(e)}function L(e){var t=e.matched[e.matched.length-1];if(t&&t.redirect){t=t["redirect"];let n="function"==typeof t?t(e):t;return"string"==typeof n&&((n=n.includes("?")||n.includes("#")?n=C(n):{path:n}).params={}),c({query:e.query,hash:e.hash,params:"path"in n?{}:e.params},n)}}function U(e,t){var n=w=x(e);const r=b.value,a=e.state,i=e.force,s=!0===e.replace;var l,u,f;e=L(n);if(e)return U(c(C(e),{state:"object"==typeof e?c({},a,e.state):a,force:i,replace:s}),t||n);const p=n;let h;return p.redirectedFrom=t,!i&&(e=o,l=r,u=l.matched.length-1,f=n.matched.length-1,-1<u)&&u==f&&m(l.matched[u],n.matched[f])&&y(l.params,n.params)&&e(l.query)===e(n.query)&&l.hash===n.hash&&(h=q(16,{to:p,from:r}),z(r,r,!0,!1)),(h?Promise.resolve(h):B(p,r)).catch((e=>I(e)?I(e,2)?e:K(e):H(e,p,r))).then((e=>{if(e){if(I(e,2))return U(c({replace:s},C(e.to),{state:"object"==typeof e.to?c({},a,e.to.state):a,force:i}),t||p)}else e=D(p,r,!0,s,a);return G(p,r,e),e}))}function V(e){var t=X.values().next().value;return t&&"function"==typeof t.runWithContext?t.runWithContext(e):e()}function B(e,t){let n;const[r,o,a]=function(e,t){var n=[],r=[],o=[],a=Math.max(t.matched.length,e.matched.length);for(let i=0;i<a;i++){const a=t.matched[i],s=(a&&(e.matched.find((e=>m(e,a)))?r:n).push(a),e.matched[i]);!s||t.matched.find((e=>m(e,s)))||o.push(s)}return[n,r,o]}(e,t);n=ke(r.reverse(),"beforeRouteLeave",e,t);for(const s of r)s.leaveGuards.forEach((r=>{n.push(Oe(r,e,t))}));const i=function(e,t){return(e=j(e,t))?Promise.reject(e):Promise.resolve()}.bind(null,e,t);return n.push(i),te(n).then((()=>{n=[];for(const r of h.list())n.push(Oe(r,e,t));return n.push(i),te(n)})).then((()=>{n=ke(o,"beforeRouteUpdate",e,t);for(const r of o)r.updateGuards.forEach((r=>{n.push(Oe(r,e,t))}));return n.push(i),te(n)})).then((()=>{n=[];for(const r of e.matched)if(r.beforeEnter&&!t.matched.includes(r))if(p(r.beforeEnter))for(const o of r.beforeEnter)n.push(Oe(o,e,t));else n.push(Oe(r.beforeEnter,e,t));return n.push(i),te(n)})).then((()=>(e.matched.forEach((e=>e.enterCallbacks={})),(n=ke(a,"beforeRouteEnter",e,t)).push(i),te(n)))).then((()=>{n=[];for(const r of d.list())n.push(Oe(r,e,t));return n.push(i),te(n)})).catch((e=>I(e,8)?e:Promise.reject(e)))}function G(e,t,n){for(const r of g.list())V((()=>r(e,t,n)))}function D(e,t,n,r,o){var a=j(e,t);if(a)return a;a=t===F;var i=s?history.state:{};n&&(r||a?l.replace(e.fullPath,c({scroll:a&&i&&i.scroll},o)):l.push(e.fullPath,o)),z(b.value=e,t,n,a),K()}let M;function T(){M=M||l.listen(((e,t,n)=>{if(ee.listening){const t=x(e);var o;e=L(t);if(e)U(c(e,{replace:!0}),t).catch(f);else{w=t;const a=b.value;s&&(e=P(a.fullPath,n.delta),o=k(),S.set(e,o)),B(t,a).catch((e=>I(e,12)?e:I(e,2)?(U(e.to,t).then((e=>{I(e,20)&&!n.delta&&n.type===r.pop&&l.go(-1,!1)})).catch(f),Promise.reject()):(n.delta&&l.go(-n.delta,!1),H(e,t,a)))).then((e=>{(e=e||D(t,a,!1))&&(n.delta&&!I(e,8)?l.go(-n.delta,!1):n.type===r.pop&&I(e,20)&&l.go(-1,!1)),G(t,a,e)})).catch(f)}}}))}let Q,J=Ee(),N=Ee();function H(e,t,n){K(e);var r=N.list();return r.length&&r.forEach((r=>r(e,t,n))),Promise.reject(e)}function K(e){return Q||(Q=!e,T(),J.list().forEach((([t,n])=>e?n(e):t())),J.reset()),e}function z(t,n,r,o){const i=e["scrollBehavior"];if(!s||!i)return Promise.resolve();const l=!r&&(c=P(t.fullPath,0),u=S.get(c),S.delete(c),u)||(o||!r)&&history.state&&history.state.scroll||null;var c,u;(0,a.Y3)().then((()=>i(t,n,l))).then((e=>e&&_(e))).catch((e=>H(e,t,n)))}const Y=e=>l.go(e);let Z;const X=new Set,ee={currentRoute:b,listening:!0,addRoute:function(e,n){let r,o;return o=A(e)?(r=t.getRecordMatcher(e),n):e,t.addRoute(o,r)},removeRoute:function(e){(e=t.getRecordMatcher(e))&&t.removeRoute(e)},hasRoute:function(e){return!!t.getRecordMatcher(e)},getRoutes:function(){return t.getRoutes().map((e=>e.record))},resolve:x,options:e,push:$,replace:function(e){return $(c(C(e),{replace:!0}))},go:Y,back:()=>Y(-1),forward:()=>Y(1),beforeEach:h.add,beforeResolve:d.add,afterEach:g.add,onError:N.add,isReady:function(){return Q&&b.value!==F?Promise.resolve():new Promise(((e,t)=>{J.add([e,t])}))},install(e){e.component("RouterLink",Pe),e.component("RouterView",Ce),e.config.globalProperties.$router=this,Object.defineProperty(e.config.globalProperties,"$route",{enumerable:!0,get:()=>(0,i.SU)(b)}),s&&!Z&&b.value===F&&(Z=!0,$(l.location).catch((e=>{})));var t={};for(const r in F)t[r]=(0,a.Fl)((()=>b.value[r]));e.provide(ye,this),e.provide(be,(0,i.qj)(t)),e.provide(we,b);const n=e.unmount;X.add(e),e.unmount=function(){X.delete(e),X.size<1&&(w=F,M&&M(),M=null,b.value=F,Z=!1,Q=!1),n()}}};function te(e){return e.reduce(((e,t)=>e.then((()=>V(t)))),Promise.resolve())}return ee}function $e(){return(0,a.f3)(ye)}function Le(){return(0,a.f3)(be)}}}]);