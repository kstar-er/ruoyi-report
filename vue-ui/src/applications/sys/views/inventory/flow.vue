<template>
  <div>
    <div class="data-filter">
      <data-filter
        ref="filter"
        :filter-items="dataSource.tableHeader"
        @search="doSearch"
      />
    </div>
    <div class="extend-handle">
      <div class="extend-handle-left">
        <div class="extend-handle-left">
          <xButton
            v-authorityHandle="'wms:product:add'"
            class="mr10"
            @click="exportData"
          >
            流水记录导出
          </xButton>
        </div>
        <div class="tips">
          <span style="color:red" class="mr10">tips:</span>
          <span class="tip1 flex-center mr10">
            库存增加
          </span>
          <span class="tip2 flex-center">
            库存扣减
          </span>
        </div>
      </div>
    </div>
    <v-table
      ref="table"
      need-end-handle
      :need-check-box="true"
      :end-handle-width="200"
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
      :row-class="rowClass"
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
            :is-show-edit-table-header="isShowEditTableHeader" :modules="'flow'"
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
import { ref, reactive, getCurrentInstance, onBeforeMount, onMounted, h, resolveDirective, watch } from 'vue'
import client from '@/utils/upload/upLoadClient'

import xButton from '@/components/common/xButton'
import { useRoute } from 'vue-router'
const imgViewerVisible = ref(false)
const route = useRoute()
const myClient = ref(client)
const { proxy } = getCurrentInstance()
const isShowEditTableHeader = ref(false)
const dataSource = ref(null)

watch(() => route.path, () => {
  if (route.path === '/wms/kccx/record' && dataSource.value){
    dataSource.value.tableData.length = 0
    if (route.query.productCode){
      dataSource.value.searchData = {}
      dataSource.value.searchData.productCode = route.query.productCode
      dataSource.value.searchData.productName = route.query.productName
      dataSource.value.searchData.warehouseName = route.query.warehouseName
      dataSource.value.searchData.recordType = 'stock_main'
      proxy.$refs.filter.backfillSearch(['productCode', "productName", "warehouseName"], [route.query.productCode, route.query.productName, route.query.warehouseName])
    }
    proxy.$refs.filter.backfillSearch(['recordType'], ['stock_main'])
    dataSource.value.initData(this, proxy.$refs.table)
  }
})

const initDataSource = async () => {

  dataSource.value = new DataSource({
    modules: 'flow',
    selectUri: '/wms/record/list',
    listMethod: 'GET',
    pageSize: 20
  })
  dataSource.value.initTableHeader()

}

onMounted(() => {
  if (route.query.productCode){
    dataSource.value.searchData = {}
    dataSource.value.searchData.productCode = route.query.productCode
    dataSource.value.searchData.productName = route.query.productName
    dataSource.value.searchData.warehouseName = route.query.warehouseName
    proxy.$refs.filter.backfillSearch(['productCode', "productName", "warehouseName"], [route.query.productCode, route.query.productName, route.query.warehouseName])
  }
  dataSource.value.searchData.recordType = 'stock_main'
  proxy.$refs.filter.backfillSearch(['recordType'], ['stock_main'])
  dataSource.value.initData(this, proxy.$refs.table)
})

onBeforeMount(() => {
  initDataSource()
})

const goCompile = async (rowData) => {

  formData.value = { ...rowData, place: [rowData.province, rowData.city, rowData.region, rowData.street] }

  editControlCommon.isShow = true

}

const doSearch = (data) => {
  dataSource.value.search(data, this, proxy.$refs.table)
}

const customizeEndHandle = (rowData, rowIndex) => {
  return [h('div', { class: 'table-handel-div no-options' }, '暂无操作')]
}

const formData = ref({})

const editControlCommon = reactive({
  isShow: false,
  formInputEl: [],
  formSelectEl: [],
  formTextAreaEl: [],
  formTimeAndNumber: [],
  formUploadEl: [],
  emitOpenDialog: (val) => {
    openSelectDialog(val)
  },
  inputDone: async (val) => {
    //

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

}

const rowClass = ({ rowData, rowIndex }) => {

  if (rowData.optType === 'plus') return 'plus'
  else return 'min'
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
const getHeader = (val) => {

  return {
    title: '流水记录',
    header: [
      '产品代码', '产品名称', '操作类型', '流水变动类型', '变动数量', '单据类型', '单据号', '商家名称', '仓库名称', '仓位名称',
      '条码', '备注', '更新时间'
    ],
    style: {
      A1: {
        font: { bold: true },
        alignment: { horizontal: "center" },
        fill: { fgColor: { rgb: "FFFF00" } }
      },
      D1: {
        font: { bold: true },
        alignment: { horizontal: "center" },
        fill: { fgColor: { rgb: "FFFF00" } }
      },
      C1: {
        font: { bold: true },
        alignment: { horizontal: "center" },
        fill: { fgColor: { rgb: "FFFF00" } }
      },
      B1: {
        font: { bold: true },
        alignment: { horizontal: "center" },
        fill: { fgColor: { rgb: "FFFF00" } }
      },
      E1: {
        font: { bold: true },
        alignment: { horizontal: "center" },
        fill: { fgColor: { rgb: "FFFF00" } }
      },
      F1: {
        font: { bold: true },
        alignment: { horizontal: "center" },
        fill: { fgColor: { rgb: "FFFF00" } }
      },
      G1: {
        font: { bold: true },
        alignment: { horizontal: "center" },
        fill: { fgColor: { rgb: "FFFF00" } }
      },
      H1: {
        font: { bold: true },
        alignment: { horizontal: "center" },
        fill: { fgColor: { rgb: "FFFF00" } }
      },
      I1: {
        font: { bold: true },
        alignment: { horizontal: "center" },
        fill: { fgColor: { rgb: "FFFF00" } }
      },
      J1: {
        font: { bold: true },
        alignment: { horizontal: "center" },
        fill: { fgColor: { rgb: "FFFF00" } }
      }
    }
  }
}

const exportData = () => {
  let { title, header, style } = getHeader()
  let data = []
  dataSource.value.selections.forEach(item => {
    let { productCode, productName, recordType, optType, num,

      orderType, orderCode, merchantName, warehouseName,
      warehousePositionName, barCode, remark, updateTime } = item
    recordType = dataSource.value.forMatData('recordType', recordType)
    optType = dataSource.value.forMatData('optType', optType)
    orderType = dataSource.value.forMatData('orderType', orderType)
    data.push([
      productCode, productName, recordType, optType, num,

      orderType, orderCode, merchantName, warehouseName,
      warehousePositionName, barCode, remark, updateTime
    ])
  })
  new proxy.$DataTool().exportExcel(header, data, title, style)
}
</script>

<style lang="less" scoped>
.table{
  padding: 10px;
  background-color: #ffffff;
}
:deep(.plus){
  background-color: #d8ffe0;
}
:deep(.min){
  background-color: #ffdcdc;
}
.tips{
  display: flex;
  align-items: center;
  color: #999;
  font-size: 12px;
}
.tip1::after{
  content:'';
  display: inline-block;
  width: 20px;
  height: 20px;
  background:#d8ffe0;
  margin-left: 6px;
}
.tip2::after{
  content:'';
  display: inline-block;
  width: 20px;
  height: 20px;
  background:#f0bebe;
  margin-left: 6px;
}
</style>