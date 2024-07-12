"use strict";(self.webpackChunksystem=self.webpackChunksystem||[]).push([[6228],{96228:function(e,l,a){a.r(l),a.d(l,{default:function(){return c}});var t=a(73396),d=a(87139),o=a(44870);const s={key:0},i={class:"jx-dialog-header"},u=["id"],p={class:"mb8"},r={style:{width:"100%",border:"1px solid #dcdfe6",padding:"10px"}},n={class:"mt10"};l={__name:"dataLimits",props:{isShow:{type:Boolean,default:!1},loading:{type:Boolean,default:!1},title:{type:String,default:""},width:{type:String,default:"50%"},formEl:{type:Object,default:()=>({})},formData:{type:Object,default:()=>({})},selectTreeNode:{type:Object,default:()=>[]},defaultExpandedKeys:{type:Object,default:()=>[]}},emits:["closeDialog","inputDone","emitOpenDialog"],setup(e,{expose:l,emit:a}){const c=e,m=(0,t.FN)()["proxy"];let f=(0,o.iH)({});const y=()=>{f.value={},a("closeDialog")},h=(0,t.Fl)({get(){return c.isShow&&0!==Object.values(c.formData).length&&Object.assign(f.value,c.formData),c.isShow||k(),c.isShow},set(){}}),w=async()=>{await m.$refs.ruleFormRef.validate(((e,l)=>{e&&((e=m.$refs.dataTreeRef?.getCheckedKeys())&&(f.value.deptIds=e),a("inputDone",JSON.parse(JSON.stringify(f.value))))}))},k=()=>{f.value={},m.$refs.ruleFormRef.resetFields()},g=(0,o.iH)(!1),b=(0,o.qj)({label:"label",children:"children"}),v=e=>{e?m.$refs.dataTreeRef?.setCheckedKeys(c.selectTreeNode.map((e=>e.id))):m.$refs.dataTreeRef?.setCheckedKeys([])};return l({updateDialogInput:e=>{Object.keys(e).forEach((l=>{f.value[l]=e[l]}))},successSubmit:k}),(l,c)=>{const m=(0,t.up)("el-button"),k=(0,t.up)("Edit"),_=(0,t.up)("el-icon"),S=(0,t.up)("el-input"),x=(0,t.up)("el-option"),U=(0,t.up)("el-select"),C=(0,t.up)("el-form-item"),j=(0,t.up)("el-checkbox"),V=(0,t.up)("el-tree"),D=(0,t.up)("el-form");var O=(0,t.up)("el-dialog");return e.isShow?((0,t.wg)(),(0,t.iD)("div",s,[(0,t.Wm)(O,{modelValue:h.value,"onUpdate:modelValue":c[1]||(c[1]=e=>h.value=e),title:"编辑数据权限",top:"4%","close-on-click-modal":!1,"show-close":!1,width:e.width,"lock-scroll":""},{header:(0,t.w5)((({titleId:e,titleClass:a})=>[(0,t._)("div",i,[(0,t._)("h4",{id:e,class:(0,d.C_)(a)},[(0,t.Uk)(" 编辑数据权限 "),(0,t.WI)(l.$slots,"customOperation")],10,u),(0,t.Wm)(m,{class:"dialog-close-btn",type:"danger",icon:"CloseBold",circle:"",onClick:y})])])),default:(0,t.w5)((()=>[(0,t._)("div",null,[(0,t.Wm)(D,{ref:"ruleFormRef",autocomplete:"off",model:(0,o.SU)(f),"label-width":"150px",inline:!0,"scroll-to-error":""},{default:(0,t.w5)((()=>[((0,t.wg)(!0),(0,t.iD)(t.HY,null,(0,t.Ko)(e.formEl,(e=>((0,t.wg)(),(0,t.j4)(C,{key:e.key,label:e.title,rules:e.rules,prop:e.key,class:"mr10"},{default:(0,t.w5)((()=>["input"===e.element?((0,t.wg)(),(0,t.j4)(S,{key:0,modelValue:(0,o.SU)(f)[e.key],"onUpdate:modelValue":l=>(0,o.SU)(f)[e.key]=l,clearable:"",type:e.type,style:{width:"220px"},placeholder:"请输入"+e.title,disabled:"dialog"===e.type||e.disabled,autosize:{minRows:1,maxRows:4},"show-password":"password"===e.type,autocomplete:"new-password"},(0,t.Nv)({_:2},["dialog"===e.type?{name:"append",fn:(0,t.w5)((()=>[(0,t.Wm)(m,{onClick:l=>{return t=e.key,a("emitOpenDialog",t);var t}},{default:(0,t.w5)((()=>[(0,t.Wm)(_,null,{default:(0,t.w5)((()=>[(0,t.Wm)(k)])),_:1})])),_:2},1032,["onClick"])])),key:"0"}:void 0]),1032,["modelValue","onUpdate:modelValue","type","placeholder","disabled","show-password"])):(0,t.kq)("",!0),"select"===e.element?((0,t.wg)(),(0,t.j4)(U,{key:1,modelValue:(0,o.SU)(f)[e.key],"onUpdate:modelValue":l=>(0,o.SU)(f)[e.key]=l,style:{width:"220px"},disabled:e.disabled,placeholder:"请选择"+e.title,multiple:"multiple"===e.type,"collapse-tags":"","collapse-tags-tooltip":"",onChange:e.change},{default:(0,t.w5)((()=>[((0,t.wg)(!0),(0,t.iD)(t.HY,null,(0,t.Ko)(e.options,(e=>((0,t.wg)(),(0,t.j4)(x,{key:e.label,label:e.label,value:e.value},null,8,["label","value"])))),128))])),_:2},1032,["modelValue","onUpdate:modelValue","disabled","placeholder","multiple","onChange"])):(0,t.kq)("",!0)])),_:2},1032,["label","rules","prop"])))),128)),(0,o.SU)(f)&&"2"===(0,o.SU)(f).dataScope?((0,t.wg)(),(0,t.j4)(C,{key:0,"m-item":"",label:"自定义数据权限",prop:(0,o.SU)(f).menuIds,class:"mr10",style:{width:"90%"}},{default:(0,t.w5)((()=>[(0,t._)("div",p,[(0,t.Wm)(j,{modelValue:g.value,"onUpdate:modelValue":c[0]||(c[0]=e=>g.value=e),label:"全选 / 全不选",onChange:v},null,8,["modelValue"])]),(0,t._)("div",r,[(0,t.Wm)(V,{ref:"dataTreeRef","node-key":"id","default-checked-keys":e.defaultExpandedKeys,props:b,data:e.selectTreeNode,"default-expanded-keys":e.defaultExpandedKeys,"show-checkbox":""},null,8,["default-checked-keys","props","data","default-expanded-keys"])])])),_:1},8,["prop"])):(0,t.kq)("",!0)])),_:1},8,["model"])]),(0,t._)("div",n,[(0,t.Wm)(m,{loading:e.loading,type:"primary",style:{float:"right","margin-right":"10px"},onClick:w},{default:(0,t.w5)((()=>[(0,t.Uk)(" 确定 ")])),_:1},8,["loading"]),(0,t.Wm)(m,{type:"danger",style:{float:"right","margin-right":"10px"},onClick:y},{default:(0,t.w5)((()=>[(0,t.Uk)(" 取消 ")])),_:1})])])),_:3},8,["modelValue","width"])])):(0,t.kq)("",!0)}}};var c=(0,a(40089).Z)(l,[["__scopeId","data-v-0e3fb5e8"]])}}]);