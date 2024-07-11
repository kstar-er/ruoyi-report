<template>
  <div>
    <div class="data-filter">
      <data-filter
        :filter-items="dataSource.tableHeader"
        @search="doSearch"
      />
    </div>
    <div class="extend-handle">
      <div class="extend-handle-left" />
    </div>
    <v-table
      ref="table"
      need-end-handle
      :need-check-box="false"
      :end-handle-width="140"
      :total="dataSource.total"
      :current-page="dataSource.currentPage"
      :page-size="dataSource.pageSize"
      :table-data="dataSource.tableData"
      :columns="dataSource.tableHeader"
      :loading="loading"
      :default-end-handle="false"

      :for-mat-data="dataSource.forMatData"
      :need-customize-cell-renderer="true"
      :customize-cell-renderer="customizeCellRenderer"
      @selectionChange="dataSource.selectionChange($event,dataSource,proxy.$refs.table)"
      @refresh="dataSource.initData(dataSource, proxy.$refs.table)"
      @current-change="dataSource.currentPageChange($event,dataSource,proxy.$refs.table)"
      @sizeChange="dataSource.pageSizeChange($event,dataSource,proxy.$refs.table)"
      @editTableHeader="isShowEditTableHeader = true"
    />
  </div>
</template>

<script setup>
import VTable from '@/components/public/virtualTable.vue'
import dataFilter from '@/components/public/dataFilter.vue'
import { DataSource, loading } from '../../utils/sysManagement/userManagement'
import { ref, getCurrentInstance, onBeforeMount, watch } from 'vue'
import { useRoute } from 'vue-router'

const { proxy } = getCurrentInstance()
const route = useRoute()
const dataSource = ref(null)

watch(() => route.path, async () => {
  if (route.path === '/system/loginRecord') {
    if (route.query){
      if (route.query.userName){
        dataSource.value.searchData = {
          userName: route.query.userName
        }
        dataSource.value.initData(this, proxy.$refs.table)
      }
    }
  }
})

const initDataSource = () => {
  dataSource.value = new DataSource({
    selectUri: '/system/logininfor/list',
    tableHeader: [
      {
        title: "用户名称",
        dataKey: "userName",
        key: 'userName',
        width: 250,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "登录时间",
        dataKey: "loginTime",
        key: 'loginTime',
        width: 250,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "浏览器",
        dataKey: "browser",
        key: 'browser',
        width: 250,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "ip",
        dataKey: "ipaddr",
        key: 'ipaddr',
        width: 250,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "ip类型",
        dataKey: "loginLocation",
        key: 'loginLocation',
        width: 250,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "操作系统",
        dataKey: "os",
        key: 'os',
        width: 250,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "infoId",
        dataKey: "infoId",
        key: 'infoId',
        width: 250,
        type: 'text',
        isShow: true,
        isFixed: false
      }
    ]
  })
  dataSource.value.initTableHeader()
  if (route.query.userName){
    dataSource.value.searchData = {
      userName: route.query.userName
    }
  }
  dataSource.value.initData(this, proxy.$refs.table)
}

onBeforeMount(() => {
  initDataSource()
})

const doSearch = (data) => {
  dataSource.value.search(data, this, proxy.$refs.table)
}

const customizeCellRenderer = ({ forMatValue, rowIndex, column, rowData }) => {

  return forMatValue
}
</script>

<style lang="less" scoped>
.table{
  padding: 10px;
  background-color: #ffffff;
}
</style>