<template>
  <div :style="{width:'100%',paddingBottom:needPagination?0:10+'px',boxSizing:'border-box'}" class="table table-virtual">
    <el-auto-resizer>
      <template #default="{ width }">
        <el-table-v2
          ref="tableRef"
          v-model:expanded-row-keys="expandedRowKeys"
          :cache="cache"
          :columns="columns"
          :row-class="rowClass"
          :data="tableData"
          :width="width"
          :height="height"
          :row-key="rowKey"
          :row-height="38"
          :header-height="38"
          :expand-column-key="needExpand?needCheckBox?columns[1].key:columns[0].key:''"
          :estimated-row-height="estimatedRowHeight"
          :fixed-data="needFixedCheckData?fixedData:[]"
          fixed
          @scroll="onScroll"
        >
          <template #empty>
            <div class="empty">
              <span class="label">数据消失在茫茫人海中...</span>
            </div>
          </template>
          <template #row="props">
            <Row v-bind="props" />
          </template>

          <template v-if="loading" #overlay>
            <div
              class="el-loading-mask flex-center"
              style="flex-direction:column;background: rgba(255,255,255,.6)"
            >
              <el-icon
                class="is-loading" color="var(--el-color-primary)"
                :size="26"
              >
                <loading-icon />
              </el-icon>
              <div class="mt20" style="color:var(--el-color-primary);font-size:16px">
                {{ loadPrompt }}
              </div>
            </div>
          </template>
          <template v-if="showFooter" #footer>
            <slot name="customizeFooter" />
          </template>
        </el-table-v2>
      </template>
    </el-auto-resizer>
    <div class="page">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        background
        :default-current-page="1"
        :layout="!needChangeSize ? 'total, prev, pager, next, jumper':'total, sizes, prev, pager, next, jumper'"
        :page-sizes="[10, 20, 30, 50, 100, 500, 1000, 5000, 10000, 100000, 1000000]"
        :total="needPagination ? total : 1"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
    <div
      class="select-square" :style="{top:mouseDownStartY+'px',left:mouseDownStartX+'px',height:selectSquareHeight+'px',width:selectSquareWidth+'px'}"
      :class="moveLocation==='left'?'rotate1':'rotate2'"
    />
  </div>
  <div class="contextmenu-content">
    <div class="list">
      <div class="item" @click="useCopy ">
        复制
      </div>
      <div class="item" @click="_emits('refresh') ">
        刷新
      </div>
      <div class="item" @click="_emits('save') ">
        保存
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, unref, h, watch, onMounted, getCurrentInstance } from 'vue'
import { Loading as LoadingIcon, SortUp, SortDown } from '@element-plus/icons-vue'
import { ElCheckbox, ElButton, TableV2FixedDir } from 'element-plus'

import { DataTool } from '@/js/tool-class/dataTool'
import { useRouter } from 'vue-router'
const { proxy } = getCurrentInstance()
const router = useRouter()
const _emits = defineEmits([
  'save', 'refresh', 'editRow', 'deleteRow', 'selectionChange', 'sizeChange', 'currentChange', 'editTableHeader', 'selectionChangeRow'
])
const _props = defineProps({
  // 加载动画
  loading: {
    type: Boolean,
    default: false
  },

  // 每行的key值
  rowKey: {
    type: String,
    default: 'id'
  },

  // 加载动画提示文字
  loadPrompt: {
    type: String,
    default: ''
  },

  // 预渲染条数
  cache: {
    type: Number,
    default: 6
  },

  // 渲染的列
  columns: {
    type: Array,
    default: () => { return [] }
  },

  // 渲染的数据
  tableData: {
    type: Array,
    default: () => { return [] }
  },

  // 是否需要checkBox
  needCheckBox: {
    type: Boolean,
    default: false
  },

  // 是否展示全选按钮
  checkAll: {
    type: Boolean,
    default: true
  },

  // 是否展示合计行，合计行用途不一，所以仅提供插槽，布局样式需自己另外设计
  showFooter: {
    type: Boolean,
    default: false
  },

  // 需要最后操作列
  needEndHandle: {
    type: Boolean,
    default: false
  },

  // 需要改变页尺寸
  needChangeSize: {
    type: Boolean,
    default: true
  },

  // 分页总数
  total: {
    type: Number,
    default: 0
  },

  // 当前页码
  currentPage: {
    type: Number,
    default: 1
  },

  // 当前页码
  pageSize: {
    type: Number,
    default: 1
  },

  // 附加在cell中的属性 支持添加Class和点击事件 class需要样式穿透
  cellHandle: {
    type: Object,
    default: () => {return {}}
  },

  cellHandleFun: {
    type: Function,
    default: () => {return ''}
  },

  // 是否需要自定义渲染函数
  needCustomizeCellRenderer: {
    type: Boolean,
    default: false
  },

  // 自定义单元格渲染函数
  customizeCellRenderer: {
    type: Function,
    default: (dataKey, value, forMatValue, rowData, rowIndex) => {
      // null
    }
  },

  //  需要把选择的数据固定在表格上方 无法跟fixed列一起使用，否则样式会错乱
  needFixedCheckData: {
    type: Boolean,
    default: false
  },

  // 是否支持通过拖拽自定义表头宽度
  needDynamicEditingHeader: {
    type: Boolean,
    default: true
  },

  // 格式化数据
  forMatData: {
    type: Function,
    default: (key, value) => {
      let onShowValue = ''
      if (key && value && (key.indexOf('Time') !== -1 || key.indexOf('Date') !== -1)){
        onShowValue = new DataTool().timeStamp2Time(value, 'yyyy-MM-dd hh:mm:ss')
      } else onShowValue = value ?? '-'
      return onShowValue
    }
  },

  // 高亮的class
  rowClass: {
    type: Function,
    default: ({ rowData: { checked }, rowIndex }) => {
      if (checked) return 'checked-light-row'
      if (rowIndex % 2 === 0) return 'stripe'
      else return ''
    }
  },

  // 自定义列宽度
  autoWidth: {
    type: Number,
    default: 0
  },

  // 操作列宽度
  endHandleWidth: {
    type: Number,
    default: 160
  },

  // 默认操作列
  defaultEndHandle: {
    type: Boolean,
    default: true
  },

  // 自定义操作列渲染函数
  customizeEndHandle: {
    type: Function,
    default: (rowData) => {
      return [h('div', { class: 'table-handel-div no-options' }, '暂无操作')]
    }
  },

  // 自定义行函数函数
  row: {
    type: Function,
    default: ({ rowData, rowIndex, cells, columns }) => {
      return cells
    }
  },

  // 是否需要展开行
  needExpand: {
    type: Boolean,
    default: false
  },

  // 渲染行预估高度
  estimatedRowHeight: {
    type: Number
  },

  // 是否需要分页
  needPagination: {
    type: Boolean,
    default: true
  },
  useUpdateHeader: {
    type: Boolean,
    default: true
  }
})

const pageColor = ref('#3d84be')

const expandedRowKeys = ref([])

let tableData = ref(_props.tableData) // 渲染数据
const height = ref(_props.showFooter ? window.screen.height - 460 - 50 : window.screen.height - 450) // 表格高度

const pageSize = ref(_props.pageSize) // 页大小
const currentPage = ref(_props.currentPage) // 页码
const handleCurrentChange = (val) => _emits('currentChange', val) // 页码改变事件
const handleSizeChange = (val) => _emits('sizeChange', val) // 也尺寸变化事件

const isMouseDown_V = ref(false) // 是否按下鼠标，用于鼠标左键+Ctrl快捷选择
const isKeyDown_V = ref(false) // 是否按下Ctrl，用于鼠标左键+Ctrl快捷选择
const moveLocation = ref('right')

const mouseDownStartX = ref(-1) // 选择框开始的X坐标
const mouseDownStartY = ref(-1) // 选择框开始的Y坐标
const selectSquareHeight = ref(0) // 选择框高度
const selectSquareWidth = ref(0) // 选择框宽度

const startIdx = ref(-1) // 快捷选择开始索引
const endIdx = ref(-1) // 快捷选择结束索引

const allSelected = ref(false) // 全选状态
const containsChecked = ref(false) // 非全选但是有选中状态的行

const selections = ref([])
const columns = ref(_props.columns) // 需要渲染的列

const tableRef = ref(null) // 虚拟列表对象
const scrollTop = ref(0) // 滑动Y轴高度

// 自定义行函数函数 - 用于 合并行
const Row = ref(_props.row)

// 初始化渲染列 立即执行函数
const initColumns = function(){
  let startX = 0
  columns.value.forEach(item => {
    // 定义基础属性 兼容第一版非虚拟列表
    item.canMove = false
    item.align = 'left'
    item.fixed = item.isFixed ? TableV2FixedDir.LEFT : '',
    item.key = item.key ? item.key : item.prop
    item.dataKey = item.key
    item.title = item.title ? item.title : item.label
    item.headerCellRenderer = _props.needDynamicEditingHeader ? ({ column }) => {
      return [h(
        'div',
        { class: `header-cell-${item.dataKey} header-cell`, title: item.title }, item.title,
        [h('div', { class: 'sort-icon-div' }, [h(SortDown, { class: 'sort-icon', onClick: onSort.bind(this, [item.dataKey, 'down']) }),
          h(SortUp, { class: 'sort-icon', onClick: onSort.bind(this, [item.dataKey, 'up']) })]), h('div', {
          // 动态调整列宽度逻辑 ， 重点： onMousedown、绝对定位、left属性
          onMousedown: (event) => {
            if (_props.needExpand) return
            startX = event.clientX
            let rowDom = document.querySelectorAll('.el-table-v2__header-row')
            let dom = document.querySelector(`.header-border-${item.dataKey}`)
            rowDom[0].style.cursor = 'e-resize'
            rowDom[1].style.cursor = 'e-resize'
            column.canMove = true
            rowDom[0]?.addEventListener('mousemove', (event) => {
              if (column.canMove){
                dom.style.left = column.width - 15 + (event.clientX - startX) + 'px'
              }
            })
            rowDom[1]?.addEventListener('mousemove', (event) => {
              if (column.canMove){
                dom.style.left = column.width - 15 + (event.clientX - startX) + 'px'
              }
            })
            rowDom[0]?.addEventListener('mouseup', (event) => {
              let dom = document.querySelector(`.header-border-${item.dataKey}`)
              let rowDom = document.querySelectorAll('.el-table-v2__header-row')
              rowDom[0].style.cursor = 'default'
              rowDom[1].style.cursor = 'default'
              if (+dom.style.left.replace('px', '') + 15 > 0) {
                column.width = +dom.style.left.replace('px', '') + 15
                column.canMove = false
                startX = 0
              } else dom.style.left = column.width - 15
            })
            rowDom[1]?.addEventListener('mouseup', (event) => {
              let dom = document.querySelector(`.header-border-${item.dataKey}`)
              let rowDom = document.querySelectorAll('.el-table-v2__header-row')
              rowDom[0].style.cursor = 'default'
              rowDom[1].style.cursor = 'default'
              if (+dom.style.left.replace('px', '') + 15 > 0) {
                column.width = +dom.style.left.replace('px', '') + 15
                column.canMove = false
                startX = 0
              } else dom.style.left = column.width - 15
            })
          },
          class: `header-border-${item.dataKey} header-border`, style: `left:${item.width - 15}px;top:-14px` })])]
    } : () => {
      return [h(
        'div',
        { class: `header-cell-${item.dataKey} header-cell`, title: item.title }, item.title)]
    }
    item.cellRenderer = ({ rowData, column, rowIndex }) => {
      // 格式化数据
      let forMatValue = _props.forMatData(column.dataKey, rowData[column.dataKey], rowData)

      // 单元格渲染函数
      return defaultCellRenderer({ rowData, rowIndex, column, forMatValue })
    }
  })
}()

const onSort = ([dataKey, type]) => {
  tableData.value.sort((a, b) => {
    if (a[dataKey] < b[dataKey]) return type === 'up' ? -1 : 1
    else if (a[dataKey] > b[dataKey]) return type === 'up' ? 1 : -1
    else return 0
  })

  // console.log(tableData.value, dataKey)
}

// 默认单元格渲染函数
const defaultCellRenderer = ({ forMatValue, rowIndex, column, rowData }) => {
  const settings = JSON.parse(localStorage.getItem('userSettings'))
  let style = ``
  if (settings) style = `color:${settings.tableSetting.color};font-size:${settings.tableSetting.fontSize}px`
  const key = column.prop ? column.prop : column.key
  const expandClass = _props.cellHandle.hasOwnProperty(key) ? _props.cellHandle[key].class : ''
  const expandClassFun = _props.cellHandleFun({ key, rowData })
  return [h(
    'div',
    { class: `row-cell ${expandClass} ${expandClassFun}`, title: forMatValue, style: style.length !== 0 ? style : '',
      onMouseenter: (e) => {
        if (_props.needExpand) return
        if (isMouseDown_V.value && isKeyDown_V.value){
          selections.value.length = 0
          startIdx.value = startIdx.value < rowIndex ? startIdx.value : rowIndex
          endIdx.value = startIdx.value < rowIndex ? rowIndex : startIdx.value
          for (let i = startIdx.value;i <= endIdx.value;i++){
            tableData.value[i].checked = true
          }
          allSelected.value = tableData.value.every((row) => row.checked)
          containsChecked.value = tableData.value.some((row) => row.checked)
          let _selections = tableData.value.filter(item => item.checked)
          selections.value = _selections
        }
      },
      onmousedown: (e) => {
        if (_props.needExpand) return
        if (isKeyDown_V.value) {
          tableData.value[rowIndex].checked = !tableData.value[rowIndex].checked
          allSelected.value = tableData.value.every((row) => row.checked)
          containsChecked.value = tableData.value.some((row) => row.checked)
          let _selections = tableData.value.filter(item => item.checked)
          selections.value = _selections
          mouseDownStartX.value = e.x
          mouseDownStartY.value = e.y
        }
        startIdx.value = rowIndex
        isMouseDown_V.value = true
      },
      onselectstart: () => {
        if (isKeyDown_V.value) return false
      },
      onClick: _props.cellHandle.hasOwnProperty(key) && _props.cellHandle[key].onClick ? _props.cellHandle[key].onClick.bind(this, rowData) : () => {
        //null
      }
    }, _props.needCustomizeCellRenderer ? _props.customizeCellRenderer({ forMatValue, rowIndex, column, rowData }) : forMatValue)]

}

// checkBox渲染函数
const initCheckColumns = function(){
  columns.value.unshift({
    key: 'selection',
    width: 50,
    align: 'center',
    fixed: _props.needFixedCheckData ? '' : TableV2FixedDir.LEFT,
    cellRenderer: ({ rowData, rowKey }) => {

      return [h(
        ElCheckbox,
        { onChange: (value) => {
          const _data = unref(tableData)
          rowData.checked = value
          allSelected.value = _data.every((row) => row.checked)
          containsChecked.value = _data.some((row) => row.checked)
          let _selections = _data.filter(item => item.checked)
          selections.value = _selections
          _emits('selectionChange', selections.value)
          _emits('selectionChangeRow', rowData)
        }, modelValue: rowData.checked, indeterminate: false }
      )]
    },
    headerCellRenderer: () => {
      return [h(
        ElCheckbox,
        { style: { display: !_props.checkAll ? 'none' : 'line-block' }, onChange: (value) => {
          const _data = unref(tableData)
          tableData.value = _data.map((row) => {
            row.checked = value
            return row
          })
          allSelected.value = _data.every((row) => row.checked)
          containsChecked.value = _data.some((row) => row.checked)
          selections.value = allSelected.value ? _data : []
          _emits('selectionChange', selections.value)
        }, modelValue: allSelected.value, indeterminate: containsChecked.value && !allSelected.value, disabled: tableData.value.length === 0 }
      )]
    }
  })
}

// 需要多选框
if (_props.needCheckBox){
  initCheckColumns()
}

//操作列渲染函数
const initHandleColumns = () => {
  columns.value.push({
    key: 'handle',
    width: _props.endHandleWidth,
    fixed: TableV2FixedDir.RIGHT,
    title: '操作',
    align: 'center',
    cellRenderer: ({ rowData, rowIndex }) => {
      if (_props.defaultEndHandle){
        return [h('div', { class: 'table-handel-div' }, [h(
          ElButton,
          { onClick: () => { _emits('editRow', rowData) }, type: "primary", icon: "Edit", text: true, style: 'padding:0;margin:0;background:transparent' },
          { default: () => "编辑" }
        ), h(
          ElButton,
          { onClick: () => { _emits('deleteRow', rowData) }, type: "danger", icon: "Delete", text: true, style: 'padding:0;margin:0;background:transparent' },
          { default: () => "删除" }
        )])]
      } else return _props.customizeEndHandle(rowData, rowIndex)

    },
    headerCellRenderer: () => {
      const handelClass = _props.useUpdateHeader ? 'table-handel-btn' : 'table-handel-btn disable'
      return [h('div', { class: 'table-handel-div-header' }, [h(
        'span',
        { style: 'color:#fff;margin-right:10px' },
        '操作'
      ), h(
        ElButton,
        { onClick: () => { _emits('refresh') }, title: '刷新', icon: "Refresh", text: true, style: "color:#ffa500;background:transparent", class: 'table-handel-btn' }
      ), h(
        ElButton,
        { onClick: () => { _emits('editTableHeader') }, title: '调整表头', icon: "Operation", text: true, style: "color:#ffa500;background:transparent", class: handelClass }
      )])]
    }
  })
}

// 需要操作列
if (_props.needEndHandle){
  initHandleColumns()
}

// 选择数据后固定到表格上方
if (_props.needFixedCheckData){
  const fixedData = ref([])
  watch(() => selections.value.length, () => {
    fixedData.value.length = 0
    fixedData.value.push(...selections.value)
  })
}

// 动态修改视窗高度 最大为800
watch([() => _props.total, () => _props.tableData.length, () => _props.pageSize, () => _props.currentPage], () => {
  tableData.value = _props.tableData
})

// 切换路由时 需要手动触发滚动 不然可视区域为空白
watch(() => router.currentRoute.value.path, (newValue, oldValue) => {
  if (newValue.toLowerCase().indexOf('new') === -1){
    setTimeout(() => {
      tableRef.value?.scrollToTop(scrollTop.value - 1)
      tableRef.value?.scrollToLeft(0)
    }, 0)
  }
})

onMounted(() => {

  // 监听全局键盘事件，判断是否按下Control
  window.addEventListener('keydown', (e) => {
    if (e.key === 'Control'){
      isKeyDown_V.value = true
    }
  })
  window.addEventListener('keyup', (e) => {
    if (e.key === 'Control'){
      isKeyDown_V.value = false
    }
  })
  if (!_props.needExpand) {
    window.addEventListener('mousemove', (e) => {
      if (isMouseDown_V.value && isKeyDown_V.value){
        selectSquareHeight.value = Math.abs(e.y - mouseDownStartY.value)
        selectSquareWidth.value = Math.abs(e.x - mouseDownStartX.value)
        if (e.x - mouseDownStartX.value < 0 && e.y - mouseDownStartY.value < 0)
          moveLocation.value = 'left'
        else moveLocation.value = 'right'
      }
    })
    window.addEventListener('mouseup', (e) => {
      if (isMouseDown_V.value){
        isMouseDown_V.value = false
        selectSquareHeight.value = 0
        selectSquareWidth.value = 0
        mouseDownStartX.value = -1
        mouseDownStartY.value = -1
        _emits('selectionChange', selections.value)
      }
    })
  }

  // 快捷选择数据 - 鼠标释放时分发数据

  // 获取个性化设置
  const settings = JSON.parse(localStorage.getItem('userSettings'))
  if (settings && settings.tableSetting && settings.tableSetting.headerColor && settings.tableSetting.headerColor.length !== 0){
    setTimeout(() => {
      const dom = document.getElementsByClassName('el-table-v2__header-cell')
      if (!dom) return
      for (let i = 0; i < dom.length; i++){
        dom[i].style.backgroundColor = settings.tableSetting.headerColor
      }
    })
  }

  initContextmenu()
})

// 记录滚动高度
const onScroll = (evt) => {
  scrollTop.value = evt.scrollTop
}

// 清除所有选中状态
const clearSelection = () => {
  setTimeout(() => {
    for (let i = 0;i < tableData.value.length;i++){
      tableData.value[i].checked = false
    }
    tableData.value = _props.tableData
    allSelected.value = false // 全选状态
    containsChecked.value = false // 非全选但是有选中状态的行
    selections.value = []
    _emits('selectionChange', selections.value)
  }, 0)
}

// 手动渲染
const handInitColumns = (needCheckBox, needHandle) => {
  let startX = 0
  columns.value.forEach(item => {
    // 定义基础属性 兼容第一版非虚拟列表
    item.canMove = false
    item.align = 'left'
    item.fixed = item.isFixed ? TableV2FixedDir.LEFT : '',
    item.key = item.key ? item.key : item.prop
    item.dataKey = item.key
    item.title = item.title ? item.title : item.label
    item.headerCellRenderer = _props.needDynamicEditingHeader ? ({ column }) => {
      return [h(
        'div',
        { class: `header-cell-${item.dataKey} header-cell`, title: item.title }, item.title,
        [h('div', {
          // 动态调整列宽度逻辑 ， 重点： onMousedown、绝对定位、left属性
          onMousedown: (event) => {
            startX = event.clientX
            let rowDom = document.querySelectorAll('.el-table-v2__header-row')
            let dom = document.querySelector(`.header-border-${item.dataKey}`)
            rowDom[0].style.cursor = 'e-resize'
            rowDom[1].style.cursor = 'e-resize'
            column.canMove = true
            rowDom[0]?.addEventListener('mousemove', (event) => {
              if (column.canMove){
                dom.style.left = column.width - 15 + (event.clientX - startX) + 'px'
              }
            })
            rowDom[1]?.addEventListener('mousemove', (event) => {
              if (column.canMove){
                dom.style.left = column.width - 15 + (event.clientX - startX) + 'px'
              }
            })
            rowDom[0]?.addEventListener('mouseup', (event) => {
              let dom = document.querySelector(`.header-border-${item.dataKey}`)
              let rowDom = document.querySelectorAll('.el-table-v2__header-row')
              rowDom[0].style.cursor = 'default'
              rowDom[1].style.cursor = 'default'
              if (+dom.style.left.replace('px', '') + 15 > 0) {
                column.width = +dom.style.left.replace('px', '') + 15
                column.canMove = false
                startX = 0
              } else dom.style.left = column.width - 15
            })
            rowDom[1]?.addEventListener('mouseup', (event) => {
              let dom = document.querySelector(`.header-border-${item.dataKey}`)
              let rowDom = document.querySelectorAll('.el-table-v2__header-row')
              rowDom[0].style.cursor = 'default'
              rowDom[1].style.cursor = 'default'
              if (+dom.style.left.replace('px', '') + 15 > 0) {
                column.width = +dom.style.left.replace('px', '') + 15
                column.canMove = false
                startX = 0
              } else dom.style.left = column.width - 15
            })

          },
          class: `header-border-${item.dataKey} header-border`, style: `left:${item.width - 15}px;top:-14px` })])]
    } : () => {
      return [h(
        'div',
        { class: `header-cell-${item.dataKey} header-cell`, title: item.title }, item.title)]
    }
    item.cellRenderer = ({ rowData, column, rowIndex }) => {
      // 格式化数据
      let forMatValue = _props.forMatData(column.dataKey, rowData[column.dataKey], rowData)

      // 单元格渲染函数
      return defaultCellRenderer({ rowData, rowIndex, column, forMatValue })
    }
  })
  if (needCheckBox){
    initCheckColumns()
  }
  if (needHandle) {
    initHandleColumns()
  }
}

const copyText = ref('')

// 添加右键菜单
const initContextmenu = () => {
  const areaEl = document.querySelector('.table-virtual')
  const contextMenu = document.querySelector('.contextmenu-content')
  contextMenu.addEventListener('contextmenu', (e) => {
    e.preventDefault()
  }, false)
  const onContextMenu = e => {
    e.preventDefault()
    copyText.value = e.target.title
    contextMenu.style.display = 'none'
    contextMenu.style.display = 'block'
    contextMenu.style.left = e.clientX + 'px'
    contextMenu.style.top = e.clientY + 'px'
  }
  areaEl.addEventListener('contextmenu', onContextMenu, false)
  window.addEventListener('click', () => {
    if (contextMenu.style.display === 'block') contextMenu.style.display = 'none'
  }, false)
}

const useCopy = () => {
  const clipboard = navigator.clipboard
  if (copyText.value.trim().length === 0 || !copyText.value) {
    proxy.$message.error('空白内容无法复制到剪贴板')
    return
  }
  clipboard.writeText(copyText.value)
    .then(() => {
      proxy.$message.success('已成功复制到剪切板')
    })
    .catch((err) => {
      console.error('无法复制到剪贴板: ', err)
    })
}

const closeTree = () => {
  expandedRowKeys.value.length = 0
}

defineExpose({
  clearSelection, // 清除筛选项
  handInitColumns, //手动渲染列
  closeTree // 树形数据关闭
})
</script>

<style scoped lang="less">
.page {
  background: #fff;
  width: 100%;
  display: flex;
  justify-content: center;
  padding: 20px 0;
}
.table{
  padding-left: 10px !important;
  padding-right: 10px !important;
  background: white;
:deep(.el-button){
  padding-left: 0;
  margin-left:0 ;
}
.empty{
  width: 86vw;
  height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
  .label{
    font-weight: 700;
    font-size: 16px;
    color:#afafaf;

  }
}

:deep(.el-table-v2__fixed-header-row){
  background: #c6c6c6;
}

:deep(.el-table-v2__header-cell){
  border-top: 1px solid #ebeef5 !important;
  border-left:1px solid rgba(192, 192, 192,.5) !important;
  background: @tb-background-default;
  color:#ffffff;
  font-weight: 800;
}

:deep(.el-table-v2__header-row){
  border-right:1px solid #ebeef5 !important;
}

:deep(.el-table-v2__row-cell){
  border-left:1px solid #ebeef5 !important;
  color:#606266;

}
:deep(.el-vl__horizontal){
  transition: .5s;
  height: 6px !important;
}
:deep(.el-vl__vertical){
  transition: .5s;
  width: 6px !important;
  right: 12px !important;
  z-index: 2000;
}
:deep(.is-hovered){
  background: #f1fbff;
}
:deep(.el-table-v2__left){
  .el-vl__vertical{
    display: none !important;
  }
}
:deep(.is-loading){
  font-size: 40px !important;
}
}
:deep(.el-pagination){
  .is-active{
    background: v-bind(pageColor) !important;
  }
  .el-input__wrapper{
    height: 26px !important;
  }
}
.example-showcase .el-table-v2__overlay {
  z-index: 9;
}

.contextmenu-content{
  position: fixed;
  top: 20%;
  left: 20%;
  z-index: 1000000;
  user-select: none;
  display: none;
  font-size:12px;
  color:rgb(96, 98, 102);
  box-shadow: 0px 4px 11px 4px rgb(221, 221, 221);
  .list {
    border:1px solid #dadce0;
    min-width: 160px;
    overflow: hidden; /* 处理圆角 */
  }
  .item {
    box-sizing: border-box;
    padding: 0 5px;
    height: 30px;
    line-height: 30px;
    padding-left: 20px;
    word-break: keep-all; /* 很重要，否则会换行 */
    background-color: #fff;
    cursor: default;
  }
  .item:hover {
    background-color: rgb(197, 197, 197);
    color: #000;
  }
}
</style>

<style>
.select-square{
  position: fixed;
  border:1px dashed #23aaf2;
}
.rotate1{
  transform-origin: left top;
  transform: rotate(180deg);
}
.rotate2{
  transform-origin: left top;
  transform: rotate(0deg);
}
.table-handel-div-header{
  display: flex;
  z-index: 2000;
  padding-left:20px;
  align-items: center;
  justify-content: center;
  width: 100%;
}
.table-handel-btn{
 width: 30px;
 height: 30px;
 font-size: 16px;
}
.table-handel-btn:hover{
  animation-name: hide2show;
  animation-duration: .4s;
  animation-fill-mode:forwards;
  animation-timing-function:linear;
}
.table-handel-div{
  width: 100%;
  display: flex;
  justify-content: space-evenly;
}

.header-cell{
  text-overflow: ellipsis;
  white-space: nowrap;
  position: relative;
  display: flex;
  width: 150px;
}

.header-border{
  width: 8px;
  height: 46px;
  position: absolute;
  z-index:  2000;
  transition: background .5s;
}

.header-border:hover{
  background: rgb(216, 255, 224);
}

.el-table-v2__header-cell{
  overflow: visible;
}

.header-border:hover{
  cursor: e-resize;
}
.row-cell{
  width:100%;
  height: 100%;
  line-height: 40px;
  font-size: 14px;
  cursor: default;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.el-vl__vertical .el-scrollbar__thumb{
  left:80%;
}
.sort-icon-div{
  display: flex;
  justify-content: center;
  align-items: center;
  margin-left: 4px;
}
.sort-icon{
  width: 1em;
  height:1em;
  cursor: pointer;
}
.disable{
  display: none;
}
.stripe{
  background: rgb(249, 249, 249);
}
</style>
