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
            @click="addRow('CW')"
          >
            创建盘点任务-按仓位盘
          </xButton>
          <xButton
            v-authorityHandle="'wms:OutWarehouseOrderMain:add'"
            class="ml10"
            @click="addRow('CP')"
          >
            创建盘点任务-按产品盘
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
      :loading="loading&&!showDetail"
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
            :is-show-edit-table-header="isShowEditTableHeader" :modules="'checkMissions'"
          />
        </div>
      </template>
    </el-drawer>
    <el-dialog
      v-model="showDetail"
      lock-scroll
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :show-close="false"
      top="4%"
      width="60%"
      destroy-on-close
    >
      <template #header="{ titleId, titleClass }">
        <div class="jx-dialog-header">
          <h4 :id="titleId" :class="titleClass">
            {{ '盘点人员' }}
          </h4>
          <div>
            <el-button
              class="dialog-close-btn" type="danger"
              icon="CloseBold"
              circle
              @click="showDetail=false"
            />
          </div>
        </div>
      </template>
      <el-table
        :data="userData" stripe
        style="width: 100%"
      >
        <el-table-column
          v-for="(item,index) in tableHeader"
          :key="index"
          :prop="item.key" :label="item.title"
          :width="item.width"
        />
      </el-table>
    </el-dialog>
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
const showDetail = ref(false)
const authority = resolveDirective('authority')

const { proxy } = getCurrentInstance()
const isShowEditTableHeader = ref(false)
const dataSource = ref(null)

const tableHeader = [{
  title: "盘点人员账号",
  key: "inventoryUserId"

}, {
  title: "盘点人员名称",
  key: "inventoryUserName"

}]
const userData = reactive([])
const initDataSource = async () => {

  dataSource.value = new DataSource({
    modules: 'checkMissions',
    selectUri: '/wms/taskMain/list',
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

  router.push(`/order/checkMissionsDetail?inventoryType=${rowData.inventoryType}&id=${rowData.id}&inventoryTaskOrderCode=${rowData.inventoryTaskOrderCode}`)
}

const addRow = async (type) => {
  router.push('/order/checkMissionsDetail?inventoryType=' + type)
}

const doSearch = (data) => {
  if (Object.keys(data).length === 0){
    data.processStatusList = 'out_wait_out,out_out_ing'
  }
  dataSource.value.search(data, this, proxy.$refs.table)
}

const deleteRow = async (rowData) => {

  proxy.$alert(`是否确认删除此盘点任务？`, '提示', {
    type: 'error',
    showCancelButton: true,
    cancelButtonText: '再想想',
    confirmButtonText: '确认删除',
    callback: (action) => {
      if (action === 'cancel') return
      else {
        dataSource.value.deleteTask(rowData.id).then((res) => {
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
  if (column.key === 'profitAndLossQuantitySum') {
    return [h(ElTag, { class: 'mr10', type: rowData[column.key] === 0 ? 'warning' : rowData[column.key] > 0 ? 'success' : 'danger' }, { default: () => rowData[column.key] === 0 ? '平' : rowData[column.key] > 0 ? '盈' : '亏' }),
      h(ElTag, { type: rowData[column.key] === 0 ? 'warning' : rowData[column.key] > 0 ? 'success' : 'danger' }, { default: () => forMatValue })]
  }
  return forMatValue
}

const cellHandle = {
  'userObject': {
    class: 'link',
    onClick: (rowData) => {
      userData.length = 0
      userData.push(...JSON.parse(rowData.userObject))
      showDetail.value = true
    }
  }
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