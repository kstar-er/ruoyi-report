"use strict";(self.webpackChunksystem=self.webpackChunksystem||[]).push([[566,9653],{20566:function(e,t,a){a(57658),a(21703);var s=a(81081),i=a(82883);a(68521);const r=i.default["_state"];t.Z=class{constructor({modules:e="",selectUri:t="/",deleteUri:a="/",selectOneUri:s="/",pageSize:i=20,tableHeader:r=[]}={}){this.modules=e,this.selectUri=t,this.selectOneUri=s,this.deleteUri=a,this.searchData={},this.currentPage=1,this.pageSize=i,this.total=0,this.tableData=[],this.tableHeader=r,this.selections=[]}async initData(e=this,t){var{data:a,code:i}=(await s.K.post(`${e.selectUri}?pageNum=${e.currentPage}&pageSize=`+e.pageSize,e.searchData))["data"];200===i&&(e.total=a.total,e.tableData.length=0,e.tableData.push(...a.rows),t?.clearSelection())}currentPageChange(e,t=this,a){t.currentPage=e,t.initData(t,a)}pageSizeChange(e,t=this,a){t.currentPage=1,t.pageSize=e,t.initData(t,a)}search(e,t=this,a){t.searchData=e,t.currentPage=1,t.initData(t,a,!0)}async delete(e,t,a=this){var{code:e,message:i}=(await s.K.post(""+a.deleteUri,e))["data"];return 200===e?(a.initData(a,t),e):i}selectionChange(e,t=this){t.selections.length=0,t.selections.push(...e)}initTableHeader(e=this){if(0===e.tableHeader.length){if(0===e.modules.length||!r.data[e.modules])throw new Error("获取store中的表头, 请提供modules名");e.tableHeader.length=0,e.tableHeader.push(...r.data[e.modules].tableHeader.filter((e=>e.isShow)))}}async selectOne(e,t=this){t=(await s.K.post(t.selectOneUri+"?id="+e))["data"]["data"];return t}}}}]);