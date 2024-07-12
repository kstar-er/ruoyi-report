"use strict";(self.webpackChunksystem=self.webpackChunksystem||[]).push([[7343],{47343:function(e,a,l){l.r(a),l.d(a,{default:function(){return b}}),l(57658);var t=l(73396),o=l(44870),n=l(87139),r=l(6094),i=l(83762),s=l(12862),d=l(81081),u=(a=l(20566),l(68521));class c extends a.Z{constructor({modules:e="",selectUri:a="/",deleteUri:l="/",selectOneUri:t="/",pageSize:o=20,tableHeader:n}={}){super({modules:e,selectUri:a,deleteUri:l,selectOneUri:t,pageSize:o,tableHeader:n})}forMatData(e,a,l,t){return"dealFlag"!==e?e&&a&&(-1!==e.indexOf("Time")||-1!==e.indexOf("Date"))?(new u.x).timeStamp2Time(a,"yyyy-MM-dd hh:mm:ss"):a??"-":a?"是":"否"}async initData(e=this){var{data:a,code:l}=(await d.K.post(`${e.selectUri}?currentPage=${e.currentPage}&pageSize=`+e.pageSize,e.searchData))["data"];200===l&&(e.total=a.pageInfo.total,e.tableData.length=0,e.tableData.push(...a.pageInfo.list))}currentPageChange(e,a=this,l){a.currentPage=e,a.initData(a,l)}pageSizeChange(e,a=this,l){a.currentPage=1,a.pageSize=e,a.initData(a,l)}search(e,a=this,l){a.searchData=e,a.currentPage=1,a.initData(a,l,!0)}async exportAllData(e){var{code:e,data:a,msg:l}=(await d.K.post("/colorful-fog/errReason/exportErrReason",e))["data"];return 200===e?{data:a,code:e}:l}async updateErrorData(e){var{code:e,data:a,msg:l}=(await d.K.post("/colorful-fog/errReason/dealErr",e))["data"];return 200===e?{data:a,code:e}:l}}var h=l(82261);const m={class:"data-filter"},f={class:"extend-handle"},p={class:"extend-handle-left"},g=["id"];a={__name:"classificationError",setup(e){const a=(0,t.FN)()["proxy"],l=(0,o.iH)(!1),u=(0,o.iH)(null),b=()=>{u.value=new c({modules:"classificationError",selectUri:"/colorful-fog/errReason/list"}),u.value.searchData.dealFlag=0,u.value.initTableHeader(),u.value.initData(this,a.$refs.table)},v=((0,t.wF)((()=>{b()})),e=>{u.value.search(e,this,a.$refs.table)}),w=e=>[(0,t.h)("div",{class:"table-handel-div no-options"},"暂无操作")],y=({forMatValue:e})=>e,C=e=>({title:"分类错误-数据缺失",header:["记录id","账单编码","方案类型","方案分类","方案名称","依赖名称","方案编码","依赖Code","分类数据","分类值","分类失败理由","是否已处理"],style:{A1:{font:{bold:!0},alignment:{horizontal:"center"},fill:{fgColor:{rgb:"FFFF00"}}},D1:{font:{bold:!0},alignment:{horizontal:"center"},fill:{fgColor:{rgb:"FFFF00"}}},C1:{font:{bold:!0},alignment:{horizontal:"center"},fill:{fgColor:{rgb:"FFFF00"}}},B1:{font:{bold:!0},alignment:{horizontal:"center"},fill:{fgColor:{rgb:"FFFF00"}}},E1:{font:{bold:!0},alignment:{horizontal:"center"},fill:{fgColor:{rgb:"FFFF00"}}},F1:{font:{bold:!0},alignment:{horizontal:"center"},fill:{fgColor:{rgb:"FFFF00"}}}}}),F=async()=>{u.value.selections.length?(u.value.selections.forEach((e=>{e.dealFlag=1})),u.value.updateErrorData(u.value.selections).then((e=>{200===e.code&&a.$alert("处理成功","提示",{type:"success",icon:"InfoFilled",showCancelButton:!1,callback:async()=>{u.value.tableData.length=0,u.value.initData(this,a.$refs.table)}})}))):a.$message.error("请先选择方案")},D=()=>{if(0!==u.value.selections.length){var e=u.value.selections.map((e=>e.id));let l=[],{title:t,header:o,style:n}=C(),r=[];u.value.exportAllData(e).then((e=>{e.data.errReasonList.forEach((e=>{e=[e.id,e.billCode,e.billType,e.sort,e.schemeName,e.dependName,e.schemeCode,e.dependCode,e.key,e.value,e.reason,e.dealFlag?"是":"否"],r.push(e)})),l.push({title:t,header:o,style:n,data:r}),l.push(...x(e.data.exportExcelVOList)),(new a.$DataTool).complexExcel(l,"错误及账单数据")}))}},k=e=>!!/^\d{13}$/.test(e)&&(e=new Date(+e),!isNaN(e.getTime())),x=e=>e.reduce(((e,l)=>{var t=l.schemeName;let o=["所属经销商Code","所属经销商名称","账单编码","账期"],n=["belongArchiveCode","belongArchiveName","billCode","costTerm"],r=[];return l.billResultVO.resultNameList.map((e=>{o.push(e.resultName),n.push(e.resultCode)})),l.billResultVO.resultDataList.list.map((e=>{e={...e,...e.data};let l=[];n.forEach((t=>{k(e[t])?l.push((new a.$DataTool).timeStamp2Time(+e[t],"yyyy-MM-dd hh:mm:ss")):l.push(e[t])})),r.push(l)})),e.push({title:t,header:o,style:{},data:r}),e}),[]),S=async e=>{e=await(new a.$DataTool).xlsx2DataObject(e.raw);let l=U(e);a.$alert("是否确认导入更新分类？","提示",{type:"info",icon:"InfoFilled",confirmButtonText:"确定",callback:e=>{"confirm"===e&&u.value.updateErrorData(l).then((e=>{200===e.code&&a.$alert("更新成功","提示",{type:"success",icon:"InfoFilled",showCancelButton:!1,callback:async()=>{u.value.tableData.length=0,u.value.initData(this,a.$refs.table)}})}))}})},U=e=>{let a=[];return e.forEach((e=>{a.push({id:e["记录id"],dependCode:e["依赖Code"],key:e["分类数据"],value:e["分类值"],reason:e["分类失败理由"]})})),a};return(e,c)=>{var b=(0,t.up)("el-upload");const C=(0,t.up)("CircleCloseFilled"),k=(0,t.up)("el-icon");var x=(0,t.up)("el-drawer");return(0,t.wg)(),(0,t.iD)("div",null,[(0,t._)("div",m,[(0,t.Wm)(s.Z,{"filter-items":u.value.tableHeader,onSearch:v},null,8,["filter-items"])]),(0,t._)("div",f,[(0,t._)("div",p,[(0,t.Wm)((0,o.SU)(h.ElButton),{class:"handle-btn",color:"#4a78bd",style:{color:"#666"},plain:"",onClick:D},{default:(0,t.w5)((()=>[(0,t.Uk)(" 导出错误数据 ")])),_:1}),(0,t.Wm)(b,{class:"inline-block ml10",accept:".xlsx, .xls","on-change":S,"show-file-list":!1,"auto-upload":!1},{default:(0,t.w5)((()=>[(0,t.Wm)((0,o.SU)(h.ElButton),{class:"handle-btn",color:"#4a78bd",style:{color:"#666"},plain:""},{default:(0,t.w5)((()=>[(0,t.Uk)(" 导入Excel更新 ")])),_:1})])),_:1}),(0,t.Wm)((0,o.SU)(h.ElButton),{class:"handle-btn ml10",color:"#4a78bd",style:{color:"#666"},plain:"",onClick:F},{default:(0,t.w5)((()=>[(0,t.Uk)(" 已处理 ")])),_:1})])]),(0,t.Wm)(i.Z,{ref:"table","need-end-handle":"","need-check-box":"","need-customize-cell-renderer":"","row-key":"roleId","end-handle-width":160,total:u.value.total,"current-page":u.value.currentPage,"page-size":u.value.pageSize,"table-data":u.value.tableData,columns:u.value.tableHeader,loading:(0,o.SU)(d.V),"default-end-handle":!1,"customize-end-handle":w,"for-mat-data":u.value.forMatData,"customize-cell-renderer":y,onSelectionChange:c[0]||(c[0]=e=>u.value.selectionChange(e,u.value,(0,o.SU)(a).$refs.table)),onRefresh:c[1]||(c[1]=e=>u.value.initData(u.value,(0,o.SU)(a).$refs.table)),onCurrentChange:c[2]||(c[2]=e=>u.value.currentPageChange(e,u.value,(0,o.SU)(a).$refs.table)),onSizeChange:c[3]||(c[3]=e=>u.value.pageSizeChange(e,u.value,(0,o.SU)(a).$refs.table)),onEditTableHeader:c[4]||(c[4]=e=>l.value=!0)},null,8,["total","current-page","page-size","table-data","columns","loading","for-mat-data"]),(0,t.Wm)(x,{modelValue:l.value,"onUpdate:modelValue":c[6]||(c[6]=e=>l.value=e),"show-close":!1},{header:(0,t.w5)((({titleId:e,titleClass:a})=>[(0,t._)("h4",{id:e,class:(0,n.C_)(a)}," 通过拖拽设置您喜欢的表头顺序 ",10,g),(0,t.Wm)((0,o.SU)(h.ElButton),{type:"danger",onClick:c[5]||(c[5]=e=>l.value=!1)},{default:(0,t.w5)((()=>[(0,t.Wm)(k,{class:"el-icon--left"},{default:(0,t.w5)((()=>[(0,t.Wm)(C)])),_:1}),(0,t.Uk)(" 关闭 ")])),_:1})])),default:(0,t.w5)((()=>[(0,t._)("div",null,[(0,t.Wm)(r.Z,{"is-show-edit-table-header":l.value,modules:"classificationError"},null,8,["is-show-edit-table-header"])])])),_:1},8,["modelValue"])])}}};var b=(0,l(40089).Z)(a,[["__scopeId","data-v-0213232e"]])},6094:function(e,a,l){l.d(a,{Z:function(){return m}}),l(57658);var t=l(73396),o=l(44870),n=l(49242),r=l(87139),i=l(98249),s=l(40130),d=l(46623);const u={class:"container"},c={class:"drag-box"},h={class:"drag-btn"};a={__name:"dynamicHeader",props:{isShowEditTableHeader:{type:Boolean,default:!1},modules:{type:String,default:""},filterList:{type:Array,default:()=>[]}},emits:["updateCompleted"],setup(e,{emit:a}){const l=e,{_state:m,commit:f}=(0,d.oR)(),p=m.data[l.modules].defaultTableHeader,g=(0,o.qj)([]),b=(0,o.iH)(!1),v=((0,t.wF)((()=>{(g.length=0)===l.filterList.length?g.push(...m.data[l.modules].tableHeader):m.data[l.modules].tableHeader.forEach((e=>{l.filterList.includes(e.key)||g.push(e)}))})),e=>{b.value=!1}),w=()=>{s.T.confirm("重置之后保存的数据将会丢失，是否继续？","提示",{confirmButtonText:"继续",cancelButtonText:"取消",type:"warning"}).then((()=>{g.length=0,g.push(...JSON.parse(JSON.stringify(p))),f(l.modules+"/updateTableHeader",g),s.T.alert("修改成功！刷新后生效","提示",{type:"warning",icon:"InfoFilled",confirmButtonText:"立即刷新",callback:e=>{a("updateCompleted"),location.reload()}}),a("updateCompleted")})).catch((()=>{}))},y=()=>{s.T.confirm("确认保存？","提示",{confirmButtonText:"继续",cancelButtonText:"取消",type:"warning"}).then((()=>{0!==l.filterList.length&&m.data[l.modules].tableHeader.forEach((e=>{l.filterList.includes(e.key)&&g.push(e)})),f(l.modules+"/updateTableHeader",g),s.T.alert("设置成功！刷新后生效","提示",{type:"warning",icon:"InfoFilled",confirmButtonText:"立即刷新",callback:e=>{a("updateCompleted"),location.reload()}})})).catch((()=>{}))};return(e,a)=>{const l=(0,t.up)("el-tag"),s=(0,t.up)("el-checkbox");var d=(0,t.up)("el-button");return(0,t.wg)(),(0,t.iD)("div",u,[(0,t._)("div",c,[(0,t.Wm)((0,o.SU)(i.J),{list:g,"chosen-class":"chosen",group:"name","force-fallback":"true",animation:"500","fallback-tolerance":4,onStart:a[0]||(a[0]=e=>b.value=!0),onEnd:v},{default:(0,t.w5)((()=>[(0,t.Wm)(n.W3,null,{default:(0,t.w5)((()=>[((0,t.wg)(!0),(0,t.iD)(t.HY,null,(0,t.Ko)(g,(e=>((0,t.wg)(),(0,t.iD)("div",{key:e.key||e.prop,class:"item"},[(0,t.Wm)(l,{size:"large"},{default:(0,t.w5)((()=>[(0,t.Uk)((0,r.zw)(e.title||e.label),1)])),_:2},1024),(0,t._)("div",null,[(0,t.Wm)(s,{modelValue:e.isFixed,"onUpdate:modelValue":a=>e.isFixed=a,label:"固定在左边",size:"large"},null,8,["modelValue","onUpdate:modelValue"]),(0,t.Wm)(s,{modelValue:e.isShow,"onUpdate:modelValue":a=>e.isShow=a,label:"是否展示",size:"large"},null,8,["modelValue","onUpdate:modelValue"])])])))),128))])),_:1})])),_:1},8,["list"])]),(0,t._)("div",h,[(0,t.Wm)(d,{type:"warning",onClick:w},{default:(0,t.w5)((()=>[(0,t.Uk)(" 重置 ")])),_:1}),(0,t.Wm)(d,{type:"primary",onClick:y},{default:(0,t.w5)((()=>[(0,t.Uk)(" 保存 ")])),_:1})])])}}};var m=(0,l(40089).Z)(a,[["__scopeId","data-v-4c5f22fa"]])}}]);