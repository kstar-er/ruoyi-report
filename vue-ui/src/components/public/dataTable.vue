<!-- eslint-disable vue/no-mutating-props -->
<template>
  <div class="table">
    <el-table
      ref="tableRef"
      v-loading="loading"
      class="table-class"
      highlight-current-row
      fix
      border
      stripe
      style="width: 100%"
      :data="dataSource.tableData"
      :row-style="rowStyle"
      :cell-style="{padding: '0',borderColor:'rgba(192, 192, 192,.5)'}"
      :row-key="getRowKey"
      :header-cell-style="{ background: '#f3f3f3', color: '#000', borderColor: 'rgba(192, 192, 192,.5)',fontWeight:'800',fontSize:'15px' }"
      :header-row-style="{ borderColor:'rgba(192, 192, 192,.5)'}"
      @selection-change="dataSource.selectionChange($event,dataSource)"
    >
      <el-table-column
        v-if="useCheckBox"
        type="selection"
        width="55"
        align="center"
        fixed="left"
      />
      <slot name="customColumn" />
      <el-table-column
        v-for="item in dataSource.tableHeader"
        :key="item.key"
        :fixed="item.isFixed"
        :label="item.title"
        :width="item.width"
        :prop="item.key"
        :formatter="dataSource.forMatData"
        align="center"
        show-overflow-tooltip
      />
      <slot name="customColumnEnd" />
      <el-table-column
        v-if="useEdit||useDelete" label="操作"
        align="center"
        :width="200"
      >
        <template #default="scope">
          <el-button
            v-if="useEdit"
            title="编辑"
            type="primary"
            icon="Edit"
            text
            style="padding:0;margin:0;background:transparent;margin-right:30px"
            class="hover-animation "
            size="small"
            @click="handleEdit(scope.$index, scope.row)"
          />
          <el-button
            v-if="useDelete"
            class="hover-animation"
            title="编辑"
            type="danger"
            icon="Delete"
            text
            style="padding:0;margin:0;background:transparent"
            size="small"
            @click="handleDelete(scope.$index, scope.row)"
          />
        </template>
      </el-table-column>
    </el-table>
  </div>
  <div v-if="usePagination" class="page">
    <el-pagination
      v-model:current-page="dataSource.currentPage"
      v-model:page-size="dataSource.pageSize"
      small
      background
      :default-current-page="1"
      :layout="'total, sizes, prev, pager, next, jumper'"
      :page-sizes="[10, 20, 30, 50, 100, 500]"
      :total="dataSource.total"
      @size-change="dataSource.pageSizeChange($event,dataSource,proxy.$refs.tableRef)"
      @current-change="currentPageChange"
    />
  </div>
</template>

<script setup>
import { ref, reactive, getCurrentInstance, onBeforeMount } from 'vue'
const { proxy } = getCurrentInstance()

const props = defineProps({
  dataSource: {
    type: Object,
    default: null
  },
  loading: {
    type: Boolean,
    default: false
  },
  useDelete: {
    type: Boolean,
    default: false
  },
  useEdit: {
    type: Boolean,
    default: false
  },
  useCheckBox: {
    type: Boolean,
    default: false
  },
  usePagination: {
    type: Boolean,
    default: false
  }
})
const _emits = defineEmits(['handleEdit', 'handleDelete'])

onBeforeMount(() => {
  initData()
})

const handleDelete = (idx, row) => {
  _emits('handleDelete', { idx, row })
}

const handleEdit = (idx, row) => {
  _emits('handleEdit', { idx, row })
}

const initData = () => {
  if (props.dataSource) {
    props.dataSource.initData(this, proxy.$refs.tableRef)
  }
}

const rowStyle = ({ row }) => {
  let style = { height: '35px', borderColor: 'rgba(192, 192, 192,.5)', fontSize: '15px' }
  return style
}

const getRowKey = (row) => {
  return row.id
}

const currentPageChange = (page) => {
  props.dataSource.currentPageChange(page, this, proxy.$refs.tableRef, true)
}

const clearSelection = () => {
  proxy.$refs.tableRef.clearSelection()
}

defineExpose({
  clearSelection
})
</script>

<style lang="less" scoped>
.table{
  width: 100%;
  padding: 0 10px;
  background-color: #fff;
  box-sizing: border-box;
}
.page {
  background: white;
  display: flex;
  justify-content: center;
  height: 80px;
}

:deep(.el-table__body tr:hover > td) {
  background-color: #f5f5f5 !important;
}

:deep(.el-table__row--striped){
  background: #f8f8f8 !important;
}
</style>