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
            v-authorityHandle="'wms:OutWarehouseOrderMain:add'"
            @click="addRow"
          >
            创建盘点单
          </xButton>
        </div>
        <!-- <div class="tips ml10">
          <span style="color:red" class="mr10">tips:</span>
          默认只展示【等待出库】/【出库中】单据
        </div> -->
      </div>
    </div>
    <v-table
      ref="table"
      need-end-handle
      :need-check-box="false"
      :end-handle-width="200"
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
            :is-show-edit-table-header="isShowEditTableHeader" :modules="'sheet'"
          />
        </div>
      </template>
    </el-drawer>
  </div>
</template>

<script setup>
import dynamicHeader from '@/components/public/dynamicHeader.vue'
import VTable from '@/components/public/virtualTable.vue'
import dataFilter from '@/components/public/dataFilter.vue'
import { DataSource, loading } from './utils/checkOrder'
import { ref, reactive, getCurrentInstance, onBeforeMount, h, resolveDirective, withDirectives } from 'vue'
import { ElButton, ElTag } from 'element-plus'

import router from '../../router'
import xButton from '@/components/common/xButton'

const authority = resolveDirective('authority')

const { proxy } = getCurrentInstance()
const isShowEditTableHeader = ref(false)
const dataSource = ref(null)

const initDataSource = async () => {

  dataSource.value = new DataSource({
    modules: 'sheet',
    selectUri: '/wms/sheet/list',
    listMethod: 'GET',
    pageSize: 20
  })
  dataSource.value.initTableHeader()

  await dataSource.value.initData(this, proxy.$refs.table)

}

onBeforeMount(() => {
  initDataSource()
})

const goCompile = async (rowData) => {

  router.push(`/order/checkOrderDetail?id=${rowData.id}&inventorySheetOrderCode=${rowData.inventorySheetOrderCode}`)
}

const addRow = async (rowData) => {
  router.push('/order/checkOrderDetail')
}

const doSearch = (data) => {
  if (Object.keys(data).length === 0){
    data.processStatusList = 'out_wait_out,out_out_ing'
  }
  dataSource.value.search(data, this, proxy.$refs.table)
}

const deleteRow = async (rowData) => {

  proxy.$alert(`是否确认删除此出库单？`, '提示', {
    type: 'error',
    showCancelButton: true,
    cancelButtonText: '再想想',
    confirmButtonText: '确认删除',
    callback: (action) => {
      if (action === 'cancel') return
      else {
        dataSource.value.deleteSheet(rowData.id, false).then((res) => {
          if (res.code === 200){
            proxy.$message.success('删除成功')
            dataSource.value.initData(this, proxy.$refs.table)
          }
        })
      }
    }
  })

}

const cancelRow = (rowData) => {

  if (rowData.processStatus === 'out_wait_out' || rowData.processStatus === 'out_create'){
    proxy.$alert(`该流程状态可直接删除，确认删除请点击确认`, '提示', {
      type: 'error',
      showCancelButton: true,
      cancelButtonText: '再想想',
      confirmButtonText: '确认删除',
      callback: (action) => {
        if (action === 'cancel') return
        else {
          dataSource.value.deleteOutOrder(rowData.id, false).then((res) => {
            if (res.code === 200){
              proxy.$message.success('删除成功')
              dataSource.value.initData(this, proxy.$refs.table)
            }
          })
        }
      }
    })
  } else {
    proxy.$alert(`是否确认作废？`, '提示', {
      type: 'error',
      showCancelButton: true,
      cancelButtonText: '取消',
      confirmButtonText: '确认作废',
      distinguishCancelAndClose: true,
      callback: (action) => {
        if (action === 'confirm') {
          dataSource.value.deleteOutOrder(rowData.id, true).then((res) => {
            if (res.code === 200){
              proxy.$message.success('作废成功')
              dataSource.value.initData(this, proxy.$refs.table)
            }
          })
        }
      }
    })
  }

}

const customizeEndHandle = (rowData, rowIndex) => {

  return [h('div', { class: 'table-handel-div' }, [withDirectives(h(
    ElButton,
    { class: 'hover-animation', onClick: () => { goCompile(rowData) }, title: '编辑', style: 'padding:0 10px;margin:0;background:transparent;width:80px;color:#0c81b1;margin-right:10px;font-size:12px' },
    { default: () => "编辑" }), [[authority, 'wms:OutWarehouseOrderMain:edit']]
  ), withDirectives(h(
    ElButton,
    { class: 'hover-animation', onClick: () => {
      deleteRow(rowData) }, title: '删除',
    style: 'padding:0 10px;margin:0;background:transparent;width:80px;color:#e81123;margin-right:10px;font-size:12px' },
    { default: () => "删除" }), [[authority, 'wms:OutWarehouseOrderMain:remove']]
  )])]
}

const customizeCellRenderer = ({ forMatValue, rowIndex, column, rowData }) => {
  if (column.key === 'profitAndLossQuantitySum') {
    return [h(ElTag, { class: 'mr10', type: rowData[column.key] === 0 ? 'warning' : rowData[column.key] > 0 ? 'success' : 'danger' }, { default: () => rowData[column.key] === 0 ? '平' : rowData[column.key] > 0 ? '盈' : '亏' }),
      h(ElTag, { type: rowData[column.key] === 0 ? 'warning' : rowData[column.key] > 0 ? 'success' : 'danger' }, { default: () => forMatValue })]
  }
  return forMatValue
}

const cellHandle = {

}
</script>

<style lang="less" scoped>
.table{
  padding: 10px;
  background-color: #ffffff;
}
.tips{
  display: flex;
  align-items: center;
  color: #999;
  font-size: 12px;
}
</style>