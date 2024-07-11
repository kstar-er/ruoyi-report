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
          @click="exportBarCode"
        >
          出库条码记录导出
        </xButton>
      </div>
    </div>
    <v-table
      ref="table"
      need-end-handle
      :need-check-box="true"
      :end-handle-width="160"
      :total="dataSource.total"
      :current-page="dataSource.currentPage"
      :page-size="dataSource.pageSize"
      :table-data="dataSource.tableData"
      :columns="dataSource.tableHeader"
      :loading="loading"
      :default-end-handle="false"
      :customize-end-handle="customizeEndHandle"
      :cell-handle="cellHandle"
      :for-mat-data="dataSource.forMatData"
      :need-customize-cell-renderer="true"
      :customize-cell-renderer="customizeCellRenderer"
      @selection-change="dataSource.selectionChange($event,dataSource,proxy.$refs.table)"
      @refresh="dataSource.initData(dataSource, proxy.$refs.table)"
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

const { proxy } = getCurrentInstance()
const isShowEditTableHeader = ref(false)
const dataSource = ref(null)

const initDataSource = async () => {

  dataSource.value = new DataSource({
    selectUri: '/wms/OutWarehouseOrderScanDetail/list',
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
        title: "出库单号",
        dataKey: "outWarehouseCode",
        key: 'outWarehouseCode',
        width: 200,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "销售单号",
        dataKey: "saleOrderCode",
        key: 'saleOrderCode',
        width: 200,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "调拨单号",
        dataKey: "allocationCode",
        key: 'allocationCode',
        width: 200,
        type: 'text',
        isShow: true,
        isFixed: false
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
      }, {
        title: "扫码时间",
        dataKey: "scanTime",
        key: 'scanTime',
        width: 200,
        type: 'none',
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

const doSearch = (data) => {
  dataSource.value.search(data, this, proxy.$refs.table)
}

const customizeEndHandle = (rowData, rowIndex) => {
  return [h('div', { class: 'table-handel-div no-options' }, '暂无操作')]
}

const customizeCellRenderer = ({ forMatValue, rowIndex, column, rowData }) => {
  return forMatValue
}

const cellHandle = {

}

const getHeader = (val) => {
  return {
    title: '仓位库存记录',
    header: [
      '条码', '产品代码', '产品名称', '产品类型', '产品型号', '相关出库单号', '相关销售单号', '相关调拨单号', '仓库名称', '仓位名称', '扫码时间'
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
      },
      K1: {
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
      outWarehouseCode = '暂无', saleOrderCode = '暂无', allocationCode = '暂无', warehouseName,
      positionName, scanTime } = item

    data.push([
      barCode, productCode, productName, productType, productSpecification,
      outWarehouseCode, saleOrderCode, allocationCode, warehouseName,
      positionName, scanTime
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
</style>