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
        <!-- <xButton
          v-authorityHandle="'wms:warehouse:query'"
          type="warning"
          class="mr10"
          @click="goToAllDetail"
        >
          仓库总览
        </xButton> -->
        <xButton
          v-authorityHandle="'wms:warehouse:add'"
          @click="addRow"
        >
          新增
        </xButton>
      </div>
    </div>
    <v-table
      ref="table"
      need-end-handle
      need-expand
      :need-check-box="false"
      :end-handle-width="280"
      :total="dataSource.total"
      :current-page="dataSource.currentPage"
      :page-size="dataSource.pageSize"
      :table-data="dataSource.tableData"
      :columns="dataSource.tableHeader"
      :loading="loading&&!editControlCommon.isShow"
      :default-end-handle="false"
      :customize-end-handle="customizeEndHandle"
      :cell-handle="cellHandle"
      :for-mat-data="dataSource.forMatData"
      :need-customize-cell-renderer="true"
      :customize-cell-renderer="customizeCellRenderer"
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
            :is-show-edit-table-header="isShowEditTableHeader" :modules="'warehouse'"
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

    <selectDialogTable
      v-if="selectTableData.isShow"
      ref="dialogSearch"
      :append-table-style="{ background: '#126f9e', color: '#fff', borderColor: 'rgba(192, 192, 192,.5)' }"
      :width="'60%'"
      :search-input-placeholder="'输入搜索'"
      :is-show="selectTableData.isShow"
      :data-source="selectTableData.dataSource"
      :title="'选择数据'"
      is-show-search-input
      :loading="loading"
      @close-dialog="selectTableData.isShow=false"
      @do-dialog-search="selectTableData.doSearch"
      @choose-row="selectTableData.chooseRow"
    />

    <el-image-viewer
      v-if="imgViewerVisible"
      :url-list="imgList"
      @close="closeImgViewer"
    />
  </div>
</template>

<script setup>
import selectDialogTable from '@/components/public/selectDialogTable.vue'
import selectDialogForm from '@/components/public/selectDialogForm.vue'
import dynamicHeader from '@/components/public/dynamicHeader.vue'
import VTable from '@/components/public/virtualTable.vue'
import dataFilter from '@/components/public/dataFilter.vue'
import { DataSource, loading } from './utils/warehouse'

import { ref, reactive, getCurrentInstance, onBeforeMount, h, resolveDirective, withDirectives } from 'vue'
import { ElButton, ElImageViewer } from 'element-plus'
import client from '@/utils/upload/upLoadClient'
import router from '../../router'
import xButton from '@/components/common/xButton'
const imgViewerVisible = ref(false)
const authority = resolveDirective('authority')
const myClient = ref(client)
const { proxy } = getCurrentInstance()
const isShowEditTableHeader = ref(false)
const dataSource = ref(null)
const goToAllDetail = () => {
  router.push('/warehouse/warehouseDetail')
}
const initDataSource = async () => {

  dataSource.value = new DataSource({
    modules: 'warehouse',
    selectUri: '/wms/warehouse/list',
    listMethod: 'GET',
    pageSize: 200000
  })
  dataSource.value.initTableHeader()
  await dataSource.value.initData(this, proxy.$refs.table)

}

onBeforeMount(() => {
  initDataSource()
})

const goCompile = async (rowData) => {
  editControlCommon.formSelectEl[0].disabled = true
  editControlCommon.formSelectEl[1].disabled = true

  editControlCommon.formSelectEl[1].options.length = 0
  editControlCommon.formSelectEl[1].options.push(...dataSource.value.tableData)
  editControlCommon.formSelectEl[1].options.unshift({
    label: '最顶层',
    value: '0',
    children: []
  })
  formData.value = { ...rowData, parentId: rowData.parentId.toString(),
    capacity: +rowData.capacity, positionsLine: +rowData.positionsLine,
    positionsColumn: +rowData.positionsColumn }

  editControlCommon.isShow = true

}

const addRow = async (rowData) => {
  editControlCommon.formSelectEl[0].disabled = false
  editControlCommon.formSelectEl[1].disabled = false
  formData.value = { }
  editControlCommon.formSelectEl[1].options.length = 0
  editControlCommon.formSelectEl[1].options.push(...dataSource.value.tableData)
  editControlCommon.formSelectEl[1].options.unshift({
    label: '最顶层',
    value: '0',
    children: []
  })
  if (rowData.id){
    editControlCommon.formSelectEl[1].disabled = true
    formData.value.parentId = rowData.id.toString()
  }

  editControlCommon.isShow = true
}

const doSearch = (data) => {
  dataSource.value.search(data, this, proxy.$refs.table)
}

const deleteRow = async (rowData) => {
  proxy.$alert(`是否确认删除此仓库？`, '提示', {
    type: 'error',
    showCancelButton: true,
    cancelButtonText: '再想想',
    confirmButtonText: '确认删除',

    callback: (action) => {
      if (action === 'cancel') return
      else {
        dataSource.value.deleteWarehouse(rowData.id).then((res) => {
          if (res.code === 200){
            proxy.$message.success('删除成功')
            dataSource.value.initData(this, proxy.$refs.table)
          }
        })
      }
    }
  })
}

const customizeEndHandle = (rowData, rowIndex) => {

  let render = [withDirectives(h(
    ElButton,
    { class: 'hover-animation', onClick: () => { goCompile(rowData) }, title: '更新信息', style: 'padding:0 10px;margin:0;background:transparent;width:80px;color:#0c81b1;font-size:12px' },
    { default: () => "更新信息" }), [[authority, 'wms:warehouse:edit']]
  )]

  if (rowData.warehouseType !== 'position'){
    render.push(withDirectives(h(
      ElButton,
      { class: 'hover-animation', onClick: () => { addRow(rowData) }, title: '添加子仓', style: 'padding:0 10px;margin:0;background:transparent;width:80px;color:#0c81b1;font-size:12px' },
      { default: () => "添加子仓" }), [[authority, 'wms:warehouse:add']]
    ))
  }
  if (rowData.children.length === 0){
    render.push(withDirectives(h(
      ElButton,
      { class: 'hover-animation', onClick: () => { deleteRow(rowData) }, title: '删除产品', style: 'padding:0 10px;margin:0;background:transparent;width:80px;color:#e81123;font-size:12px' },
      { default: () => "删除仓库" }), [[authority, 'wms:warehouse:remove']]
    ))
  }
  return h('div', { class: 'table-handel-div' }, render)
}

const formData = ref({})

const editControlCommon = reactive({
  isShow: false,
  formInputEl: [{
    title: "仓库名称",
    key: 'warehouseName',
    element: 'input',
    rules: [{
      required: true,
      message: '该项不能为空',
      trigger: 'blur'
    }]
  }, {
    title: "仓库负责人",
    key: 'warehouseKeeper',
    element: 'input'
  }, {
    title: "仓库联系电话",
    key: 'linkmanPhone',
    element: 'input'
  }, {
    title: "经纬度",
    key: 'mapLongitude',
    element: 'input',
    illustrate: '使用空格分隔经纬度；'
  }],
  formSelectEl: [{
    title: "仓库类型",
    key: 'warehouseType',
    element: 'select',
    options: [{
      value: 'warehouse',
      label: '仓库'
    }, {
      value: 'area',
      label: '库区'
    }, {
      value: 'frame',
      label: '货架'
    }, {
      value: 'position',
      label: '仓位'
    }],
    rules: [{
      required: true,
      message: '该项不能为空',
      trigger: 'blur'
    }]
  }, {
    title: "仓库上级",
    key: 'parentId',
    element: 'selectTree',
    options: [],
    rules: [{
      required: true,
      message: '该项不能为空',
      trigger: 'blur'
    }]
  }],
  formTextAreaEl: [{
    title: "详细地址",
    key: 'warehouseAddress',
    element: 'input',
    type: 'textarea'
  }, {
    title: "备注",
    key: 'remark',
    element: 'input',
    type: 'textarea'
  }],
  formTimeAndNumber: [{
    title: "面积/m³",
    key: 'capacity',
    element: 'number'
  }, {
    title: "行",
    key: 'positionsLine',
    element: 'number'
  }, {
    title: "列",
    key: 'positionsColumn',
    element: 'number'
  }],
  formUploadEl: [],
  emitOpenDialog: (val) => {
    openSelectDialog(val)
  },
  inputDone: async (val) => {
    let params = {
      ...val
    }
    if (params.warehouseType === 'warehouse' && params.parentId !== '0'){
      proxy.$message.error(`保存失败，仓库只能添加到最顶层`)
      return
    }
    if (params.warehouseType !== 'warehouse' && params.parentId === '0'){
      proxy.$message.error(`保存失败，最顶层只能添加仓库`)
      return
    }
    if (params.id){
      let res = await dataSource.value.updateWarehouse(params)
      if (res.code === 200){
        proxy.$message.success(`更新成功`)
        dataSource.value.initData(this, proxy.$refs.table)
        editControlCommon.isShow = false
      }
    } else {
      let res = await dataSource.value.addWarehouse(params)
      if (res.code === 200){
        proxy.$message.success(`新增成功`)
        dataSource.value.initData(this, proxy.$refs.table)
        editControlCommon.isShow = false
      }
    }

  }
})

const customizeCellRenderer = ({ forMatValue, rowIndex, column, rowData }) => {

  return forMatValue
}

const openSelectDialog = async (val) => {
  switch (val){
  // 选择数据
  case 1: return

  default: return
  }
}

const closeImgViewer = () => {
  imgViewerVisible.value = false
  const m = (e) => { e.preventDefault() }
  document.body.style.overflow = 'auto'
  document.removeEventListener("touchmove", m, true)
}

const cellHandle = {
  'avatar': {
    class: 'link',
    onClick: (rowData) => {
      imgViewerVisible.value = true
      imgList.length = 0
      imgList.push(rowData.avatar)
      const m = (e) => { e.preventDefault() }
      document.body.style.overflow = 'hidden'
      document.addEventListener("touchmove", m, false) // 禁止页面滑动

    }
  }
}

const selectTableData = reactive({
  isShow: false,
  dataSource: null,
  doSearch: (val) => {
    console.log(val)
  },
  chooseRow: (val) => {
    console.log(val)
  }
})
</script>

<style lang="less" scoped>
.table{
  padding: 10px;
  background-color: #ffffff;
}
</style>