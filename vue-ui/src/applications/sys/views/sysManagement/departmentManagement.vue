<template>
  <div>
    <div class="data-filter">
      <data-filter
        :filter-items="dataSource.tableHeader"
        @search="doSearch"
      />
    </div>
    <div class="extend-handle">
      <div class="extend-handle-left">
        <xButton
          class="mr10"

          @click="close"
        >
          全部折叠
        </xButton>
        <xButton

          v-authorityHandle="'system:dept:add'"
          @click="addRow"
        >
          新增
        </xButton>
      </div>
    </div>
    <v-table
      ref="table"
      need-end-handle
      :need-check-box="false"
      need-customize-cell-renderer
      need-expand
      row-key="deptId"
      :need-pagination="false"
      :total="dataSource.total"
      :current-page="dataSource.currentPage"
      :page-size="dataSource.pageSize"
      :table-data="dataSource.tableData"
      :columns="dataSource.tableHeader"
      :loading="loading&&!editControlCommon.isShow"
      :default-end-handle="false"
      :customize-end-handle="customizeEndHandle"
      :for-mat-data="dataSource.forMatData"
      :customize-cell-renderer="customizeCellRenderer"
      :end-handle-width="140"
      @selection-change="dataSource.selectionChange($event,dataSource,proxy.$refs.table)"
      @refresh="dataSource.initData(dataSource, proxy.$refs.table)"
      @edit-row="goCompile"
      @current-change="dataSource.currentPageChange($event,dataSource,proxy.$refs.table)"
      @size-change="dataSource.pageSizeChange($event,dataSource,proxy.$refs.table)"
      @edit-table-header="isShowEditTableHeader = true"
    />
    <el-drawer
      v-model="isShowEditTableHeader"
      :show-close="false"
    >
      <template #header="{ titleId, titleClass }">
        <h4 :id="titleId" :class="titleClass">
          通过拖拽设置您喜欢的表头顺序
        </h4>
        <el-button type="danger" @click="isShowEditTableHeader = false">
          <el-icon class="el-icon--left">
            <CircleCloseFilled />
          </el-icon>
          关闭
        </el-button>
      </template>
      <template #default>
        <div>
          <dynamicHeader
            :is-show-edit-table-header="isShowEditTableHeader" :modules="'depart'"
          />
        </div>
      </template>
    </el-drawer>
    <selectDialogForm
      v-if="editControlCommon.isShow"
      ref="dialogForm"
      :width="'60%'"
      :loading="loading"
      :my-client="myClient"
      :form-data="formData"
      :is-show="editControlCommon.isShow"
      :title="'请填写相关信息'"
      :form-input-el="editControlCommon.formInputEl"
      :form-select-el="editControlCommon.formSelectEl"
      :form-text-area-el="editControlCommon.formTextAreaEl"
      :form-upload-el="editControlCommon.formUploadEl"
      :form-time-and-number="editControlCommon.formTimeAndNumber"
      @close-dialog="editControlCommon.isShow=false"
      @emit-open-dialog="editControlCommon.emitOpenDialog"
      @input-done="editControlCommon.inputDone"
    />
  </div>
</template>

<script setup>
import selectDialogForm from '@/components/public/selectDialogForm.vue'
import dynamicHeader from '@/components/public/dynamicHeader.vue'
import VTable from '@/components/public/virtualTable.vue'
import dataFilter from '@/components/public/dataFilter.vue'
import { DataSource, loading } from '../../utils/sysManagement/departmentManagement'
import { ref, reactive, getCurrentInstance, onBeforeMount, h, resolveDirective, withDirectives } from 'vue'
import { ElButton, ElTag } from 'element-plus'
import client from '@/utils/upload/upLoadClient'

const authority = resolveDirective('authority')
const myClient = ref(client)
const { proxy } = getCurrentInstance()
const isShowEditTableHeader = ref(false)
const dataSource = ref(null)

const initDataSource = () => {
  dataSource.value = new DataSource({
    modules: 'depart',
    selectUri: '/system/dept/list',
    listMethod: 'post'
  })
  dataSource.value.initTableHeader()
  dataSource.value.initData(this, proxy.$refs.table)
}

onBeforeMount(() => {
  initDataSource()
})

const goCompile = async ({ deptId }) => {
  editControlCommon.formSelectEl[0].options = [...dataSource.value.tableData]
  let ctx = await dataSource.value.getDept(deptId)
  formData.value = { ...ctx.data }
  if (formData.value.parentId === 0){
    editControlCommon.formSelectEl[0].options.unshift({
      label: '顶级部门',
      value: 0,
      children: []
    })
    editControlCommon.formSelectEl[0].disabled = true
  } else editControlCommon.formSelectEl[0].disabled = false
  editControlCommon.isShow = true
}

const addRow = async () => {
  editControlCommon.formSelectEl[0].options = [...dataSource.value.tableData]
  editControlCommon.formSelectEl[0].disabled = false
  formData.value = { status: '0', parentId: 100, orderNum: 0 }
  editControlCommon.isShow = true
}

const addDeptByCurrent = async ({ deptId }) => {
  editControlCommon.formSelectEl[0].options = [...dataSource.value.tableData]
  formData.value = { status: '0', parentId: deptId, orderNum: 0 }
  editControlCommon.isShow = true
}

const doSearch = (data) => {
  dataSource.value.search(data, this, proxy.$refs.table)
}

const customizeEndHandle = (rowData) => {
  if (rowData.parentId === 0){
    return [h('div', { class: 'table-handel-div' }, [withDirectives(h(
      ElButton,
      { class: 'hover-animation', onClick: () => { goCompile(rowData) }, title: '编辑', type: "primary", icon: "Edit", text: true, style: 'padding:0;margin:0;background:transparent' },
      { default: () => "" }), [[authority, 'system:dept:edit']]
    ), withDirectives(h(
      ElButton,
      { class: 'hover-animation', onClick: () => { addDeptByCurrent(rowData) }, title: '新增下级部门', type: "warning", icon: "CirclePlus", text: true, style: 'padding:0;margin:0;background:transparent' },
      { default: () => "" }), [[authority, 'system:dept:edit']]
    )])]
  }
  return [h('div', { class: 'table-handel-div' }, [withDirectives(h(
    ElButton,
    { class: 'hover-animation', onClick: () => { goCompile(rowData) }, title: '编辑', type: "primary", icon: "Edit", text: true, style: 'padding:0;margin:0;background:transparent' },
    { default: () => "" }), [[authority, 'system:dept:edit']]
  ), withDirectives(h(
    ElButton,
    { class: 'hover-animation', onClick: () => { addDeptByCurrent(rowData) }, title: '新增下级部门', type: "warning", icon: "CirclePlus", text: true, style: 'padding:0;margin:0;background:transparent' },
    { default: () => "" }), [[authority, 'system:dept:edit']]
  )])]
}
const formData = ref({})

const editControlCommon = reactive({
  isShow: false,
  formInputEl: [{
    title: "部门名称",
    key: 'deptName',
    element: 'input'
  }, {
    title: "部门负责人",
    key: 'leader',
    element: 'input',
    type: 'dialog',
    canInput: true
  }, {
    title: "负责人联系电话",
    key: 'phone',
    element: 'input'
  }, {
    title: "邮箱",
    key: 'email',
    element: 'input'
  }],
  formSelectEl: [{
    title: "所属上级部门",
    key: 'parentId',
    element: 'selectTree',
    options: [],
    rules: [{
      required: true,
      message: '该项不能为空',
      trigger: 'blur'
    }]
  }, {
    title: "部门状态",
    key: 'status',
    element: 'radio',
    options: [{
      label: '正常',
      value: '0'
    }, {
      label: '停用',
      value: '1'
    }],
    rules: [{
      required: true,
      message: '该项不能为空',
      trigger: 'blur'
    }]
  }],
  formTimeAndNumber: [{
    title: "显示顺序",
    key: 'orderNum',
    element: 'number'
  }],
  formTextAreaEl: [{
    title: "备注",
    key: 'remark',
    element: 'input',
    type: 'textarea'
  }],
  emitOpenDialog: (val) => {
    console.log(val)
  },
  inputDone: async (val) => {
    let params = {
      ...val
    }
    if (params.deptId){
      let res = await dataSource.value.updateDept(params)
      if (res.code === 200){
        proxy.$message.success('更新成功')
        dataSource.value.initData(this, proxy.$refs.table)
        editControlCommon.isShow = false
      }
    } else {
      let res = await dataSource.value.addDept(params)
      if (res.code === 200){
        proxy.$message.success('新增成功')
        dataSource.value.initData(this, proxy.$refs.table)
        editControlCommon.isShow = false
      }
    }
  }
})

const customizeCellRenderer = ({ forMatValue, rowIndex, column, rowData }) => {
  if (column.key === 'status') {
    return [h(ElTag, { type: rowData[column.key] === '0' ? 'primary' : 'danger' }, { default: () => forMatValue })]
  }
  return forMatValue
}

const close = () => {
  proxy.$refs.table.closeTree()
}
</script>

<style lang="less" scoped>
.table{
  padding: 10px;
  background-color: #ffffff;
}
</style>