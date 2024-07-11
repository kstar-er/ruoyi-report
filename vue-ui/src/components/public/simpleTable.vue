<!-- eslint-disable vue/no-mutating-props -->
<template>
  <div class="table">
    <el-table
      ref="tableRef"
      v-loading="loading"
      class="table-class"
      fix
      highlight-current-row
      :show-summary="needSummary"
      :span-method="objectSpanMethod"
      :summary-method="summary"
      stripe
      :data="tableData"
      :row-key="row => row.id"
      :max-height="maxHeight"
      :row-style="rowStyle ? rowStyle:{height: '35px',borderColor:'rgba(192, 192, 192,.5)'}"
      :cell-style="{padding: '0',borderColor:'rgba(192, 192, 192,.5)'}"
      style="width: 100%"
      :border="needBorder"
      :header-cell-style="headerCellStyle"
      :header-row-style="{ borderColor:'rgba(192, 192, 192,.5)' }"
      @cell-dblclick="cellDbClick"
      @selection-change="selectionChange"
      @row-dblclick="rowDblclick"
    >
      <el-table-column
        v-if="needSelection"
        :reserve-selection="reserveSelection"
        type="selection"
        width="55"
        align="center"
        fixed="left"
      />
      <el-table-column
        v-if="needIndex"
        label="序号"
        type="index"
        width="60"
        align="center"
      >
        <template #default="scope">
          <span>{{ (currentPage - 1) * pageSize + scope.$index + 1 }}</span>
        </template>
      </el-table-column>
      <el-table-column
        v-for="item in tableHeader"
        :key="item.prop"
        :fixed="item.isFixed"
        :label="item.label ?? item.title"
        :prop="item.prop ?? item.key"
        :min-width="item.width"
        :formatter="forMatData"
        align="center"
        :sortable="sortable"
        show-overflow-tooltip
      >
        <template v-if="item.needScope" #default="scope">
          <slot
            :name="item.prop" :row="scope.row"
            :index="scope.$index"
          />
        </template>
      </el-table-column>
      <el-table-column
        v-if="needEndControl&&!needSelection"
        fixed="right"
        label="操作"
        align="center"
        :width="optionsWidth"
      >
        <template #default="scope">
          <div
            style="background:transparent"
            class="flex-center"
          >
            <slot
              name="endOption"
              :row="scope.row"
            />
          </div>
        </template>
      </el-table-column>
    </el-table>
  </div>
  <div v-if="needPage" class="page">
    <el-pagination
      :current-page="currentPage"
      background
      :small="small"
      layout="total, sizes, prev, pager, next"
      :page-sizes="[10, 20, 30, 50, 100, 200, 500, 1000]"
      :total="total"
      :page-size="pageSize"
      @current-change="currentChange"
      @size-change="handleSizeChange"
    />
  </div>
</template>

<script setup>
import { reactive, getCurrentInstance } from 'vue'
const { proxy } = getCurrentInstance()
const selections = reactive([])
let emit = defineEmits([
  "selectionChange",
  "currentChange",
  'submit',
  'refresh', 'chooseRow', 'sizeChange', 'cellDbClick'
])
let selectionChange = (selection) => {
  selections.length = 0
  selections.push(...selection)
  emit('selectionChange', selection)
}
let chooseRow = (row) => emit('chooseRow', row)

const rowDblclick = (row) => {
  if (!props.needSelection) emit('chooseRow', row)
  else {
    proxy.$refs.tableRef.toggleRowSelection(row, undefined)
    console.log(row)
  }

}
let currentChange = (page) => emit('currentChange', page)
const handleSizeChange = (val) => emit('sizeChange', val)
const cellDbClick = (row, column, cell, event) => emit('cellDbClick', row, column, cell, event)
const props = defineProps({
  forMatData: {
    type: Function,
    default: (row, column) => row[column.property] ?? '-'
  },
  total: {
    type: Number,
    default: 0
  },
  pageSize: {
    type: Number,
    default: 10
  },
  needChangeSize: {
    type: Boolean,
    default: false
  },
  rowStyle: {
    type: [Boolean, Function],
    default: false
  },
  loading: {
    type: Boolean,
    default: true
  },
  needBorder: {
    type: Boolean,
    default: true
  },
  needSummary: {
    type: Boolean,
    default: false
  },
  sortable: {
    type: Boolean,
    default: false
  },
  needSelection: {
    type: Boolean,
    default: false
  },
  small: {
    type: Boolean,
    default: false
  },
  optionsWidth: {
    type: Number,
    default: 260
  },
  needEndControl: {
    type: Boolean,
    default: false
  },
  showTips: {
    type: Boolean,
    default: true
  },
  currentPage: {
    type: [Number, String],
    default: 1
  },
  tips: {
    type: String,
    default: 'Tips: 按住shift + 鼠标滚轮可以横向滚动表格 && 按住 Ctrl + 鼠标左键快捷选择'
  },
  headerCellStyle: {
    type: Object,
    default(){
      return { background: '#126f9e', color: '#fff', borderColor: 'rgba(192, 192, 192,.5)' }
    }
  },
  needPage: {
    type: Boolean,
    default: true
  },
  objectSpanMethod: {
    type: Function,
    default(){
      return (row, column, rowIndex, columnIndex) => {return { } }
    }
  },
  summary: {
    type: Function,
    default(){
      // null
    }
  },
  tableHeader: {
    type: Array,
    default(){
      return []
    }
  },
  detailsTableHeader: {
    type: Array,
    default(){
      return []
    }
  },
  tableData: {
    type: Array,
    default(){
      return []
    }
  },
  maxHeight: {
    type: Number,
    default: 1000
  },
  needIndex: {
    type: Boolean,
    default: false
  },
  reserveSelection: {
    type: Boolean,
    default: false
  }
})

const clearSelection = function() {
  proxy.$refs.tableRef.clearSelection()
}

const setSelect = function(row, select) {
  proxy.$refs.tableRef.toggleRowSelection(row, select)
}

defineExpose({
  clearSelection,
  setSelect
})

const handleError = (err) => {
  console.log(err)
}
</script>

<style lang="less" scoped>
.page {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
.handle {
  padding: 14px 0px 14px 2px;
  .upload {
    display: inline-block;
    margin: 0 10px;
  }
}
.table_title {
  font-size: 16px;
  font-weight: bold;
  line-height: 40px;
  color: #595959;
}

:deep(.el-table__body-wrapper){
  z-index: 20;
  &::-webkit-scrollbar {
    width : 8px;
    height: 8px;
  }
}
:deep(.el-scrollbar__bar.is-horizontal){
  color: #ff0000 !important;
  height: 6px;
}
.table-class{
  border: none;
}
:deep(.el-table--enable-row-hover .el-table__body tr:hover > td) {
   background-color: rgb(226, 226, 226);
}
</style>