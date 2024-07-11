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
            仓位库存导出
          </xButton>
          <div class="ml10">
            当前仓库：
            <el-input
              v-model="name"
              clearable
              style="width:220px"
              :placeholder="`请选择仓库`"
              :disabled="true"
            >
              <template #append>
                <el-button @click="selectWarehouse">
                  <el-icon><Edit /></el-icon>
                </el-button>
              </template>
            </el-input>
          </div>
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
      :loading="loading&&!selectTableData.isShow"
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
            :is-show-edit-table-header="isShowEditTableHeader" :modules="'position'"
          />
        </div>
      </template>
    </el-drawer>

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
import { DataSource as warehouse } from "../fileManager/utils/warehouse"
import dynamicHeader from '@/components/public/dynamicHeader.vue'
import VTable from '@/components/public/virtualTable.vue'
import dataFilter from '@/components/public/dataFilter.vue'
import { DataSource, loading } from './utils/warehouse'
import { ref, reactive, getCurrentInstance, onBeforeMount, onMounted, h, resolveDirective, withDirectives, watch } from 'vue'
import { useRoute } from 'vue-router'
import { ElButton } from 'element-plus'
import xButton from '@/components/common/xButton'
const imgViewerVisible = ref(false)
const authority = resolveDirective('authority')
const { proxy } = getCurrentInstance()
const isShowEditTableHeader = ref(false)
const dataSource = ref(null)
const route = useRoute()

const name = ref('')
const code = ref('')

watch(() => route.path, () => {
  if (route.path === '/wms/kccx/positionDetail' && dataSource.value){
    name.value = route.query.warehouseName
    code.value = route.query.warehouseCode
    dataSource.value.searchData = { ckWarehouseCode: code.value }
    if (route.query.productCode){
      dataSource.value.searchData.productCode = route.query.productCode
      dataSource.value.searchData.productName = route.query.productName
      proxy.$refs.filter.backfillSearch(['productCode', "productName"], [route.query.productCode, route.query.productName])
    }
    dataSource.value.tableData.length = 0
    dataSource.value.initData(this, proxy.$refs.table)
  }
})

const initDataSource = async () => {
  name.value = route.query.warehouseName
  code.value = route.query.warehouseCode
  dataSource.value = new DataSource({
    modules: 'position',
    selectUri: '/wms/position/listGrouping',
    listMethod: 'GET',
    pageSize: 20
  })
  dataSource.value.initTableHeader()
  if (!name.value){
    selectWarehouse()
    return
  } else {
    dataSource.value.searchData = { ckWarehouseCode: code.value }
  }

}

onMounted(() => {
  if (route.query.productCode){
    dataSource.value.searchData.productCode = route.query.productCode
    dataSource.value.searchData.productName = route.query.productName
    proxy.$refs.filter.backfillSearch(['productCode', "productName"], [route.query.productCode, route.query.productName])
  }
  if (!name.value){
    return
  }
  dataSource.value.initData(this, proxy.$refs.table)
})

onBeforeMount(() => {
  initDataSource()
})

const doSearch = (data) => {

  if (code.value){
    data.ckWarehouseCode = code.value
  } else {
    selectWarehouse()
    return
  }

  dataSource.value.search(data, this, proxy.$refs.table)
}

const customizeEndHandle = (rowData, rowIndex) => {
  let render = [withDirectives(h(
    ElButton,
    { class: 'hover-animation', onClick: () => { changePosition(rowData) }, title: '批量调整库位', style: 'padding:0 10px;margin:0;background:transparent;width:100px;color:#0c81b1;font-size:12px' },
    { default: () => "批量调整库位" }), [[authority, 'wms:warehouse:edit']]
  )]
  return h('div', { class: 'table-handel-div' }, render)
}

const customizeCellRenderer = ({ forMatValue, rowIndex, column, rowData }) => {
  return forMatValue
}

const changePosition = async (rowData) => {
  selectTableData.title = '请选择要调整的库位'
  selectTableData.placeholder = '输入库位名称搜索'
  selectTableData.needSelection = false
  selectTableData.dataSource = new warehouse({
    selectUri: '/wms/warehouse/listDemo',
    listMethod: 'GET',
    pageSize: 10,
    tableHeader: [{
      title: "仓库名称",
      dataKey: "warehouseName",
      key: 'warehouseName',
      width: 200
    }, {
      title: "仓库类型",
      dataKey: "warehouseType",
      key: 'warehouseType',
      width: 200
    }, {
      title: "仓库地址",
      dataKey: "warehouseAddress",
      key: 'warehouseAddress',
      width: 200
    }]
  })
  selectTableData.dataSource.forMatDataV2 = forMatDataPro
  selectTableData.chooseRow = (val) => {
    proxy.$prompt(`${rowData.productName}，总数量：${rowData.sumNum}，请输入移动数量`, '提示', {
      confirmButtonText: '确定更换',
      cancelButtonText: '取消'
    })
      .then(({ value }) => {
        proxy.$alert(`是否确认将【${rowData.productName}】调整【${value}个】至【${val.warehouseName}】`, '提示', {
          type: 'info',
          showCancelButton: true,
          cancelButtonText: '取消',
          confirmButtonText: '确认',
          distinguishCancelAndClose: true,
          callback: (action) => {
            if (action === 'confirm') {
              let params = {
                "orgWarehousePositionCode": rowData.positionCode,
                "productCode": rowData.productCode,
                "desWarehousePositionCode": val.warehouseCode,
                "productNum": +value
              }
              dataSource.value.batchReset([params]).then(res => {
                if (res.code === 200){
                  proxy.$message.success('调整成功')
                  dataSource.value.initData(this, proxy.$refs.table)
                }
              })
            }
          }
        })
      })
      .catch(() => {
      //
      })
    selectTableData.isShow = false
  }

  selectTableData.doSearch = async (val) => {
    selectTableData.dataSource.searchData = { warehouseCode: code.value, maxParentCode: code.value }
    if (val) selectTableData.dataSource.searchData.warehouseName = val
    await selectTableData.dataSource.initData()
  }

  selectTableData.dataSource.searchData = { warehouseCode: code.value, maxParentCode: code.value }
  await selectTableData.dataSource.initData()
  selectTableData.isShow = true
  return
}
const closeImgViewer = () => {
  imgViewerVisible.value = false
  const m = (e) => { e.preventDefault() }
  document.body.style.overflow = 'auto'
  document.removeEventListener("touchmove", m, true)
}

const cellHandle = {

}

const getHeader = (val) => {
  return {
    title: '仓位库存记录',
    header: [
      '产品代码', '产品名称', '仓位名称', '类型', '产品类型', '产品型号', '产品单位', '产品条码', '是否冻结', '备注', '更新时间'
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
    let { productCode, productName, positionName, warehouseType,
      productType, productSpecification,
      productUnit, barCode, isFrozen, remark,
      updateTime } = item

    data.push([
      productCode, productName, positionName, warehouseType,
      productType, productSpecification,
      productUnit, barCode, isFrozen, remark,
      updateTime
    ])
  })
  new proxy.$DataTool().exportExcel(header, data, title, style)
}

const forMatDataPro = (row, column) => {
  const warehouseType = {
    warehouse: '仓库',
    area: '库区',
    frame: '货架',
    position: '仓位'

  }
  if (column.property === 'warehouseType') return warehouseType[row[column.property]] ?? '-'
  if (column.property === 'processStatus') return processStatus1[row[column.property]] ?? '-'
  return row[column.property] ?? '-'
}

const selectWarehouse = async () => {

  selectTableData.title = '请选择出库仓'
  selectTableData.placeholder = '输入仓库名称搜索'
  selectTableData.needSelection = false
  selectTableData.dataSource = new warehouse({
    selectUri: '/wms/warehouse/list',
    listMethod: 'GET',
    pageSize: 10,
    tableHeader: [{
      title: "仓库名称",
      dataKey: "warehouseName",
      key: 'warehouseName',
      width: 200,
      type: 'text',
      isShow: true,
      isFixed: true
    }, {
      title: "仓库类型",
      dataKey: "warehouseType",
      key: 'warehouseType',
      width: 200,
      type: 'select',
      isShow: true,
      isFixed: true,
      options: []
    }, {
      title: "仓库地址",
      dataKey: "warehouseAddress",
      key: 'warehouseAddress',
      width: 200,
      type: 'text',
      isShow: true,
      isFixed: false
    }]
  })
  selectTableData.dataSource.forMatDataV2 = forMatDataPro
  selectTableData.chooseRow = (val) => {

    name.value = val.warehouseName
    code.value = val.warehouseCode
    dataSource.value.searchData = { ckWarehouseCode: code.value }
    dataSource.value.initData(this, proxy.$refs.table)
    selectTableData.isShow = false
  }

  selectTableData.doSearch = async (val) => {
    selectTableData.dataSource.searchData = { }
    if (val) selectTableData.dataSource.searchData.warehouseName = val
    await selectTableData.dataSource.initData()
    selectTableData.dataSource.tableData.forEach(item => {
      delete item.children
    })
    selectTableData.dataSource.total = selectTableData.dataSource.tableData.length
  }

  selectTableData.dataSource.searchData = { }
  await selectTableData.dataSource.initData()
  selectTableData.dataSource.tableData.forEach(item => {
    delete item.children
  })
  selectTableData.dataSource.total = selectTableData.dataSource.tableData.length
  selectTableData.isShow = true
  return

}

const selectTableData = reactive({
  isShow: false,
  dataSource: null,
  searchPlaceholde: '',
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