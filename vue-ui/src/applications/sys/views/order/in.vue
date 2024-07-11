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
            调拨入库
          </xButton>
          <xButton
            v-authorityHandle="'wms:OutWarehouseOrderMain:add'"
            class="ml10"
            @click="addRowProduct"
          >
            选择产品入库
          </xButton>
        </div>
        <div class="tips ml10">
          <span style="color:red" class="mr10">tips:</span>
          默认只展示【等待入库】/【入库中】单据
        </div>
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
            :is-show-edit-table-header="isShowEditTableHeader" :modules="'out'"
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
import { DataSource, loading } from './utils/inDetail'
import { ref, reactive, getCurrentInstance, onBeforeMount, h, resolveDirective, withDirectives } from 'vue'
import { ElButton } from 'element-plus'

import router from '../../router'
import xButton from '@/components/common/xButton'

const authority = resolveDirective('authority')

const { proxy } = getCurrentInstance()
const isShowEditTableHeader = ref(false)
const dataSource = ref(null)

const initDataSource = async () => {

  dataSource.value = new DataSource({
    modules: 'inOrder',
    selectUri: '/wms/InWarehouseOrderMain/list',
    listMethod: 'GET',
    pageSize: 20
  })
  dataSource.value.initTableHeader()

  dataSource.value.searchData.processStatusList = 'in_create,in_wait_out,in_ing'
  await dataSource.value.initData(this, proxy.$refs.table)

}

onBeforeMount(() => {
  initDataSource()
})

const goCompile = async (rowData) => {
  let type = rowData.type === '调拨入库' ? 'allocation' : 'product'
  router.push(`/order/inDetail?type=${type}&id=${rowData.id}&inWarehouseCode=${rowData.inWarehouseCode}`)
}

const addRow = async (rowData) => {
  router.push('/order/inDetail?type=allocation')
}

const addRowProduct = async (rowData) => {
  router.push('/order/inDetail?type=product')
}

const doSearch = (data) => {
  if (Object.keys(data).length === 0){
    data.processStatusList = 'in_create,in_wait_out,in_ing'
  }
  dataSource.value.search(data, this, proxy.$refs.table)
}

const deleteRow = async (rowData) => {

  if (rowData.processStatus === 'in_create' || rowData.processStatus === 'in_wait_out'){
    proxy.$alert(`是否确认删除此出库单？`, '提示', {
      type: 'error',
      showCancelButton: true,
      cancelButtonText: '再想想',
      confirmButtonText: '确认删除',
      callback: (action) => {
        if (action === 'cancel') return
        else {
          dataSource.value.deleteInOrder(rowData.id, false).then((res) => {
            if (res.code === 200){
              proxy.$message.success('删除成功')
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
    style: 'padding:0 10px;margin:0;background:transparent;width:80px;color:#e81123;font-size:12px' },
    { default: () => "删除" }), [[authority, 'wms:OutWarehouseOrderMain:remove']]
  )])]
}

const customizeCellRenderer = ({ forMatValue, rowIndex, column, rowData }) => {
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