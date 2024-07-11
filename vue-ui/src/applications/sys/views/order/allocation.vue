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
          v-authorityHandle="'wms:AllOrderMain:add'"
          @click="addRow"
        >
          新增调拨单
        </xButton>
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
            :is-show-edit-table-header="isShowEditTableHeader" :modules="'allocation'"
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
import { DataSource, loading } from './utils/allocationDetail'
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
    modules: 'allocation',
    selectUri: '/wms/allocationOrderMain/list',
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
  router.push(`/order/allocationDetail?id=${rowData.id}&allocationCode=${rowData.allocationCode}`)

}

const addRow = async (rowData) => {
  router.push('/order/allocationDetail')

}

const doSearch = (data) => {
  dataSource.value.search(data, this, proxy.$refs.table)
}

const deleteRow = async (rowData) => {
  if (rowData.processStatus !== 'allocation_create' && rowData.processStatus !== 'allocation_wait_out'){
    proxy.$message.error('该流程状态不允许删除')
    return
  }
  proxy.$alert(`是否确认删除此调拨单？`, '提示', {
    type: 'error',
    showCancelButton: true,
    cancelButtonText: '再想想',
    confirmButtonText: '确认删除',

    callback: (action) => {
      if (action === 'cancel') return
      else {
        dataSource.value.deleteAllocationOrder(rowData.id).then((res) => {
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
  return [h('div', { class: 'table-handel-div' }, [withDirectives(h(
    ElButton,
    { class: 'hover-animation', onClick: () => { goCompile(rowData) }, title: '编辑', style: 'padding:0 10px;margin:0;background:transparent;width:80px;color:#0c81b1;margin-right:10px;font-size:12px' },
    { default: () => "编辑" }), [[authority, 'wms:AllOrderMain:edit']]
  ), withDirectives(h(
    ElButton,
    { class: 'hover-animation', onClick: () => {
      deleteRow(rowData) }, title: '删除',
    style: 'padding:0 10px;margin:0;background:transparent;width:80px;color:#e81123;font-size:12px' },
    { default: () => "删除" }), [[authority, 'wms:AllOrderMain:remove']]
  )])]
}

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

const cellHandle = {

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