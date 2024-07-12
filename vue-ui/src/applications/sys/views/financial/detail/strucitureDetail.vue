<template>
  <div>
    <div class="header">
      <el-page-header @back="goBack">
        <template #content>
          <span class="route"> {{ title }} </span>
          <span class="route" style="color:#999">  {{ active }} </span>
        </template>
      </el-page-header>
    </div>
    <div class="detail">
      <dataTable
        :data-source="dataSource"
        :loading="loading"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onBeforeMount, getCurrentInstance } from "vue"
import { useRouter, useRoute } from 'vue-router'
import { DataSource, loading } from '../utils/structure'
import dataTable from '@/components/public/dataTable.vue'
const { proxy } = getCurrentInstance()
const router = useRouter()
const route = useRoute()
const title = ref(`首页 / ${route.query.title} / `)
const active = ref(`${route.meta.title}`)

const dataSource = ref(null)

const goBack = () => {
  router.go(-1)
}

onBeforeMount(() => {
  initDataSource()
})

const initDataSource = () => {
  dataSource.value = new DataSource({
    tableHeader: [
      {
        title: "id",
        key: 'id',
        width: 100
      }, {
        title: "所属表",
        key: 'tableName',
        width: 200
      }, {
        title: "字段名",
        key: 'fieldName',
        width: 200
      }, {
        title: "字段key",
        key: 'field',
        width: 200
      }, {
        title: "数据类型",
        key: 'type',
        width: 200
      }, {
        title: "创建时间",
        key: 'createTime',
        width: 200
      }, {
        title: "创建人",
        key: 'createUser',
        width: 200
      }, {
        title: "更新时间",
        key: 'updateTime',
        width: 200
      }, {
        title: "更新人",
        key: 'updateUser',
        width: 200
      }
    ],
    selectUri: '/colorful-fog/tableFiled/select',
    pageSize: 200
  })
  dataSource.value.searchData = { tableName: route.query.orderTable }
  dataSource.value.forMatData = forMatData
}

const forMatData = (row, column) => {

  return row[column.property] ? row[column.property] : '-'
}
</script>

<style lang="less" scoped>
.detail{
  margin-top: 6px;
  background: #fff;
  padding: 10px;
  font-size: 14px;
}
.header{
  padding: 20px;
  background: #fff;
}
.route{
  font-size: 14px;
  font-weight: 500;
  color: #252525;
  letter-spacing: 1px;
  cursor: default;
}
</style>