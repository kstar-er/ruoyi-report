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
        <div class="extend-handle-left">
          <xButton
            v-authorityHandle="'wms:product:add'"
            class="mr10"
            @click="exportBarCode"
          >
            条码记录导出
          </xButton>
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
      :loading="loading"
      :default-end-handle="false"
      :customize-end-handle="customizeEndHandle"
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
import client from '@/utils/upload/upLoadClient'
import router from '../../router'
import xButton from '@/components/common/xButton'
const imgViewerVisible = ref(false)
const authority = resolveDirective('authority')
const myClient = ref(client)
const { proxy } = getCurrentInstance()
const isShowEditTableHeader = ref(false)
const dataSource = ref(null)

const initDataSource = async () => {

  dataSource.value = new DataSource({
    selectUri: '/wms/position/list',
    listMethod: 'GET',
    pageSize: 20,
    tableHeader: [
      {
        title: "产品代码",
        dataKey: "productCode",
        key: 'productCode',
        width: 200,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "产品名称",
        dataKey: "productName",
        key: 'productName',
        width: 200,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "产品类型",
        dataKey: "productType",
        key: 'productType',
        width: 200,
        type: 'none',
        isShow: true,
        isFixed: false
      }, {
        title: "规格型号",
        dataKey: "productSpecification",
        key: 'productSpecification',
        width: 200,
        type: 'none',
        isShow: true,
        isFixed: false
      }, {
        title: "条码",
        dataKey: "barCode",
        key: 'barCode',
        width: 200,
        type: 'text',
        isShow: true,
        isFixed: true
      }, {
        title: "仓库名称",
        dataKey: "warehouseName",
        key: 'warehouseName',
        width: 200,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "仓位名",
        dataKey: "positionName",
        key: 'positionName',
        width: 200,
        type: 'text',
        isShow: true,
        isFixed: false
      }
    ]
  })
  dataSource.value.initTableHeader()
  await dataSource.value.initData(this, proxy.$refs.table)

}

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

const customizeCellRenderer = ({ forMatValue, rowIndex, column, rowData }) => {
  return forMatValue
}

const customizeEndHandle = (rowData, rowIndex) => {
  return [h('div', { class: 'table-handel-div no-options' }, '暂无操作')]
}

const getHeader = (val) => {
  return {
    title: '仓位库存记录',
    header: [
      '条码', '产品代码', '产品名称', '产品类型', '产品型号', '仓库名称', '仓位名称'
    ],
    style: {
      A1: {
        font: { bold: true },
        alignment: { horizontal: "center" },
        fill: { fgColor: { rgb: "FFFF00" } }
      },
      B1: {
        font: { bold: true },
        alignment: { horizontal: "center" },
        fill: { fgColor: { rgb: "FFFF00" } }
      },
      C1: {
        font: { bold: true },
        alignment: { horizontal: "center" },
        fill: { fgColor: { rgb: "FFFF00" } }
      },
      D1: {
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
      }
    }
  }
}

const exportBarCode = () => {
  let { title, header, style } = getHeader()
  let data = []
  dataSource.value.selections.forEach(item => {
    let { barCode, productCode, productName, productType, productSpecification,
      warehouseName,
      positionName } = item

    data.push([
      barCode, productCode, productName, productType, productSpecification,
      warehouseName,
      positionName
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
  background-color: #e68d8d;
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