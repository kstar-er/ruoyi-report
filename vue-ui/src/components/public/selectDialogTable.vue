<template>
  <div>
    <el-dialog
      v-model="showDialog"
      lock-scroll
      :title="title"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :show-close="false"
      :fullscreen="fullscreen"
      :width="width"
      top="4%"
      destroy-on-close
    >
      <template #header="{ titleId, titleClass }">
        <div class="jx-dialog-header">
          <div :id="titleId" :class="titleClass">
            <h5 class="inline-block">
              {{ title }}
            </h5>
            <el-input
              v-if="isShowSearchInput"
              v-model="searchVal"
              clearable
              :placeholder="searchInputPlaceholder"
              style="width:220px;margin-left:20px"
            >
              <template #append>
                <el-button icon="Search" @click="doSearch" />
              </template>
            </el-input>
            <slot name="customOperation" />
          </div>

          <div>
            <el-button
              class="dialog-close-btn" type="primary"
              icon="RefreshLeft"
              circle
              @click="refresh"
            />
            <el-button
              v-if="!hideClose"
              class="dialog-close-btn" type="danger"
              icon="CloseBold"
              circle
              @click="closeDialog"
            />
          </div>
        </div>
      </template>
      <simpleTable
        v-if="showDialog"
        ref="simple"
        :for-mat-data="dataSource.forMatDataV2"
        :options-width="100"
        :loading="loading"
        :show-tips="false"
        small

        :max-height="maxHeight"
        :current-page="dataSource.currentPage"
        :table-header="dataSource.tableHeader"
        :table-data="dataSource.tableData"
        :page-size="dataSource.pageSize"
        :total="dataSource.total"
        :need-change-size="needChangeSize"
        :need-end-control="needEndControl"
        :need-selection="needSelection"
        :row-style="rowStyle"
        :header-cell-style="appendTableStyle"
        @choose-row="chooseRow"
        @current-change="dataSource.currentPageChange($event,dataSource,proxy.$refs.simple)"
        @size-change="dataSource.pageSizeChange($event,dataSource,proxy.$refs.simple)"
        @selection-change="dataSource.selectionChange($event,dataSource)"
      >
        <template #endOption="{row}">
          <el-button
            id="optionsBtn"
            text
            type="primary"
            style="background:transparent"
            @click="chooseRow(row)"
          >
            确定
          </el-button>
        </template>
      </simpleTable>
      <div v-if="needSelection" class="mt10">
        <el-button
          size="small"
          type="primary" style="float:right;margin-right:10px"
          @click="submit"
        >
          确定
        </el-button>
        <el-button
          size="small"
          type="danger" style="float:right;margin-right:10px"
          @click="closeDialog"
        >
          取消
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import simpleTable from '@/components/public/simpleTable'
import { ref, getCurrentInstance, watch, computed } from 'vue'

const _props = defineProps({
  // 表格loading
  loading: {
    type: Boolean,
    default: false
  },

  hideClose: {
    type: Boolean,
    default: false
  },

  // 是否展示弹框
  isShow: {
    type: Boolean,
    default: false
  },

  fullscreen: {
    type: Boolean,
    default: false
  },

  // 弹框宽度
  width: {
    type: String,
    default: '40%'
  },

  // 数据源 => 请确保dataSource是PublicDataSource实例或它子类的实例
  dataSource: {
    type: Object,
    default: () => { return {} }
  },

  rowStyle: {
    type: [Boolean, Function],
    default: false
  },

  // 弹框标题
  title: {
    type: String,
    default: ''
  },

  // 是否展示搜索框
  isShowSearchInput: {
    type: Boolean,
    default: false
  },

  // 搜索框的占位文字
  searchInputPlaceholder: {
    type: String,
    default: ''
  },

  needEndControl: {
    type: Boolean,
    default: true
  },

  needChangeSize: {
    type: Boolean,
    default: false
  },

  needSelection: {
    type: Boolean,
    default: false
  },
  maxHeight: {
    type: Number,
    default: 500
  },
  appendTableStyle: {
    type: Object,
    default: () => {
      return { background: '#126f9e', color: '#fff', borderColor: 'rgba(192, 192, 192,.5)' }
    }
  }

})

const _emits = defineEmits(['closeDialog', 'doDialogSearch', 'chooseRow', 'submit'])
const { proxy } = getCurrentInstance()
const searchVal = ref('') // 条件搜索的value值

const closeDialog = () => _emits('closeDialog') // 关闭弹窗事件
const chooseRow = (row) => _emits('chooseRow', row) // 选择一行事件
const doSearch = () => _emits('doDialogSearch', searchVal.value) // 条件搜索事件
const submit = () => _emits('submit')

watch(() => _props.isShow, () => {
  searchVal.value = ''
  if (_props.isShow && _props.dataSource.tableData.length === 0) {
    _props.dataSource.initData()
  }
})
const refresh = () => {
  _props.dataSource.initData()
}

const showDialog = computed({
  get(){
    return _props.isShow
  },
  set(){
    //null
  }
}) // 开启弹窗事件

const dialogSearch = (searchData = {}) => {
  _props.dataSource.search(searchData)
}

defineExpose({
  dialogSearch
})

/**
 * @param {{ HTML调用实例 }}
 */
// <selectDialogTable
//   ref="testDialog"
//   :is-show="isShow"
//   :data-source="pickupDataSource"
//   :title="'测试弹框'"
//   :is-show-search-input="true"
//   :search-input-placeholder="'请输入xxx'"
//   :search-value-key="'test'"
//   :loading="loading"
//   @close-dialog="isShow=false"
//   @doDialogSearch="doDialogSearch"
//   @chooseRow="chooseRow"
// />

/**
 *  @param {{ JS 调用实例 }}
 */

// const doDialogSearch = (val) => {
//   if (val.length === 0) proxy.$refs.testDialog.dialogSearch({ test: val })
//   else proxy.$refs.testDialog.dialogSearch({ test: val, test2: val })
// }
// const chooseRow = (row) => {
//   console.log(row)
// }
</script>

<style lang="less" scoped>

</style>