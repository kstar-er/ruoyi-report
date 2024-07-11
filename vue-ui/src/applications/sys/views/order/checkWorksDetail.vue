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
        <div class="extend-handle-left" />
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
            :is-show-edit-table-header="isShowEditTableHeader" :modules="'checkWorks'"
          />
        </div>
      </template>
    </el-drawer>
    <el-dialog
      v-model="showMachineDetail"
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
              @click="showMachineDetail=false"
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

    <el-dialog
      v-model="showDetail"
      lock-scroll
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :show-close="false"
      top="4%"
      fullscreen
      destroy-on-close
    >
      <template #header="{ titleId, titleClass }">
        <div class="jx-dialog-header">
          <h4 :id="titleId" :class="titleClass">
            {{ '盘点人员' }}
            <el-button
              type="primary" class="ml20"
              size="small"
              @click="finishTaskMain"
            >
              盘点已完成
            </el-button>
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
      <div>
        <el-table
          :data="listDataSource.tableData" style="width: 100%"
          border
          :header-cell-style="{ background: '#eeeeee', color: '#606266'}"
          stripe
        >
          <el-table-column
            label="账面数量"
            prop="bookQuantity"
            :width="200"
          >
            <template #default="scope">
              <el-input-number
                v-model="scope.row.bookQuantity" size="small"
                :disabled="true"
                :min="1"
              />
            </template>
          </el-table-column>
          <el-table-column
            label="实盘数量"
            prop="firmOfferQuantity"
            :width="200"
          >
            <template #default="scope">
              <el-input-number
                v-model="scope.row.firmOfferQuantity" size="small"

                :min="0"
                @change="checkChange($event,scope.row)"
              />
            </template>
          </el-table-column>
          <el-table-column
            label="盈亏"
            prop="profitAndLossQuantity"
            :width="200"
          >
            <template #default="scope">
              <el-tag :type="scope.row.profitAndLossQuantity===0?'warning':scope.row.profitAndLossQuantity>0?'primary':'danger'">
                {{ scope.row.profitAndLossQuantity===0?'平':scope.row.profitAndLossQuantity > 0 ?'盈':'亏' }}
              </el-tag>
              <el-tag :type="scope.row.profitAndLossQuantity===0?'warning':scope.row.profitAndLossQuantity>0?'primary':'danger'" class="ml10">
                {{ (scope.row.profitAndLossQuantity) }}
              </el-tag>
            </template>
          </el-table-column>

          <el-table-column
            v-for="(item) in listDataSource.tableHeader"
            :key="item.prop?? item.key"
            :fixed="item.isFixed"
            :label="item.label ?? item.title"
            :prop="item.prop ?? item.key"
            :min-width="item.width"
            :formatter="forMatDataPro"
            align="left"
            sortable
            show-overflow-tooltip
          />

          <el-table-column
            fixed="right" label="操作"
            width="120"
            align="center"
          >
            <template #default="row">
              <el-button
                link type="primary"
                size="small"

                @click="updateRemark(row)"
              >
                备注
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
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
const showMachineDetail = ref(false)
const authority = resolveDirective('authority')
const showDetail = ref(false)
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
    modules: 'checkWorks',
    selectUri: '/wms/taskMainAllocation/list',
    listMethod: 'GET',
    pageSize: 20
  })
  dataSource.value.initTableHeader()

  await dataSource.value.initData(this, proxy.$refs.table)

}

onBeforeMount(() => {
  initDataSource()
})
const listDataSource = ref(null)
let row = null

const goCompile = async (rowData) => {
  row = rowData
  if (!listDataSource.value){
    listDataSource.value = new DataSource({
      selectUri: '/wms/taskDetailAllocation/list',
      listMethod: 'GET',
      tableHeader: [
        {
          title: "流程状态",
          dataKey: "processStatus",
          key: 'processStatus',
          width: 200,
          type: 'text'
        }, {
          title: "产品代码",
          dataKey: "productCode",
          key: 'productCode',
          width: 200,
          type: 'text'
        }, {
          title: "产品名称",
          dataKey: "productName",
          key: 'productName',
          width: 200
        }, {
          title: "产品类型",
          dataKey: "productType",
          key: 'productType',
          width: 200,
          type: 'select'
        }, {
          title: "产品型号",
          dataKey: "productSpecification",
          key: 'productSpecification',
          width: 200
        }, {
          title: "仓位名称",
          dataKey: "warehousePositionName",
          key: 'warehousePositionName',
          width: 200
        }, {
          title: "仓位代码",
          dataKey: "warehousePositionCode",
          key: 'warehousePositionCode',
          width: 200
        }, {
          title: "备注",
          dataKey: "comment",
          key: 'comment',
          width: 200
        }
      ],
      pageSize: 100
    })
  }

  listDataSource.value.searchData = { inventoryTaskOrderCode: rowData.inventoryTaskOrderCode, inventoryUserName: rowData.inventoryUserName }
  await listDataSource.value.initData()
  showDetail.value = true

}

const doSearch = (data) => {
  if (Object.keys(data).length === 0){
    data.processStatusList = 'out_wait_out,out_out_ing'
  }
  dataSource.value.search(data, this, proxy.$refs.table)
}

const customizeEndHandle = (rowData, rowIndex) => {
  return [h('div', { class: 'table-handel-div' }, [withDirectives(h(
    ElButton,
    { class: 'hover-animation', onClick: () => { goCompile(rowData) }, title: '查看盘点任务', style: 'padding:0 10px;margin:0;background:transparent;width:80px;color:#0c81b1;margin-right:10px;font-size:12px' },
    { default: () => "查看盘点任务" }), [[authority, 'wms:OutWarehouseOrderMain:edit']]
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

const updateRemark = (row) => {

  proxy.$prompt('请输入备注', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  }).then(({ value }) => {
    let params = { ...row.row }
    if (params.comment) params.comment = `${params.comment}\n${value}`
    else params.comment = value
    dataSource.value.updateRemark(params).then(res => {
      if (res.code === 200) proxy.$message.success('修改成功')
      listDataSource.value.initData()
    })
  }).catch(() => {
    //null
  })
}

const checkChange = async ($event, row) => {
  row.profitAndLossQuantity = $event - row.bookQuantity
  let params = { ...row }
  let res = await dataSource.value.updateCheckWorks(params)
  if (res.code === 200){
    proxy.$message.success(`更新成功`)
    listDataSource.value.initData()
  }
}

const finishTaskMain = async () => {
  proxy.$alert(`是否已盘点完成`, '提示', {
    type: 'warning',
    showCancelButton: true,
    cancelButtonText: '再想想',
    confirmButtonText: '确认删除',
    callback: (action) => {
      if (action === 'cancel') return
      else {
        dataSource.value.updateCheckWorks(row).then(res => {
          if (res.code === 200){
            proxy.$message.success(`操作完成`)
            showDetail.value = false
            dataSource.value.initData(this, proxy.$refs.table)
          }
        })
      }
    }
  })

}

const forMatDataPro = (row, column) => {
  const processStatus = {
    inventory_create: '制单',
    inventory_reject: '驳回',
    inventory_ing: '盘点中',
    inventory_check: '审核中',
    inventory_doc: '归档',

    inventory_task_create: '制单',
    inventory_task_start: '盘点中',
    inventory_task_audit: '审核',
    inventory_task_doc: '归档',

    inventory_task_allocation_not_started: '未盘点',
    inventory_task_allocation_end: '盘点完成',
    inventory_task_allocation_reviewed: '待复盘',

    inventory_task_detail_not_started: "汇总失败",
    inventory_task_detail_end: '汇总完成',
    inventory_task_detail_err: '汇总异常'

  }
  if (column.property === 'processStatus') return processStatus[row[column.property]] ?? '-'
  return row[column.property] ?? '-'
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