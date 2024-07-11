<template>
  <div>
    <div class="data-filter">
      <data-filter
        ref="filter"
        :filter-items="dataSource.tableHeader"
        @search="doSearch"
      />
    </div>
    <div class="extend-handle">
      <div class="extend-handle-left">
        <xButton
          v-authorityHandle="'wms:product:add'"
          class="mr10"
          @click="exportData"
        >
          仓库库存导出
        </xButton>
        <div class="tips ml10">
          <span style="color:red" class="mr10">tips:</span>
          当前显示：{{ dataSource.searchData.productType ? dataSource.searchData.productType==='单件'?'单件':'组合' :'所有' }} 库存
        </div>
      </div>
    </div>
    <v-table
      ref="table"
      need-end-handle
      :need-check-box="true"
      :end-handle-width="240"
      :total="dataSource.total"
      :current-page="dataSource.currentPage"
      :page-size="dataSource.pageSize"
      :table-data="dataSource.tableData"
      :columns="dataSource.tableHeader"
      :loading="loading&&!editControlCommon.isShow"
      :default-end-handle="false"
      :customize-end-handle="customizeEndHandle"
      :cell-handle="cellHandle"
      :for-mat-data="dataSource.forMatData"
      :need-customize-cell-renderer="true"
      :customize-cell-renderer="customizeCellRenderer"
      @selection-change="dataSource.selectionChange($event,dataSource,proxy.$refs.table)"
      @refresh="dataSource.initData(dataSource, proxy.$refs.table)"
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
            :is-show-edit-table-header="isShowEditTableHeader" :modules="'stock'"
          />
        </div>
      </template>
    </el-drawer>
    <selectDialogForm
      v-if="editControlCommon.isShow"
      ref="dialogForm"
      :width="'60%'"
      :loading="loading"
      :my-client="myClient"
      :form-data="formData"
      :is-show="editControlCommon.isShow"
      :title="'请填写相关信息'"
      :form-input-el="editControlCommon.formInputEl"
      :form-select-el="editControlCommon.formSelectEl"
      :form-text-area-el="editControlCommon.formTextAreaEl"
      :form-upload-el="editControlCommon.formUploadEl"
      :form-time-and-number="editControlCommon.formTimeAndNumber"
      @close-dialog="editControlCommon.isShow=false"
      @emit-open-dialog="editControlCommon.emitOpenDialog"
      @input-done="editControlCommon.inputDone"
    />

    <selectDialogTable
      v-if="selectTableData.isShow"
      ref="dialogSearch"
      :append-table-style="{ background: '#126f9e', color: '#fff', borderColor: 'rgba(192, 192, 192,.5)' }"
      :width="'60%'"
      :search-input-placeholder="'输入搜索'"
      :is-show="selectTableData.isShow"
      :data-source="selectTableData.dataSource"
      :title="'选择数据'"
      is-show-search-input
      :loading="loading"
      @close-dialog="selectTableData.isShow=false"
      @do-dialog-search="selectTableData.doSearch"
      @choose-row="selectTableData.chooseRow"
    />
  </div>
</template>

<script setup>
import selectDialogTable from '@/components/public/selectDialogTable.vue'
import selectDialogForm from '@/components/public/selectDialogForm.vue'
import dynamicHeader from '@/components/public/dynamicHeader.vue'
import VTable from '@/components/public/virtualTable.vue'
import dataFilter from '@/components/public/dataFilter.vue'
import { DataSource, loading } from './utils/warehouse'
import { ref, reactive, getCurrentInstance, onBeforeMount, h, resolveDirective, withDirectives, onMounted } from 'vue'
import { ElButton } from 'element-plus'
import client from '@/utils/upload/upLoadClient'
import { useRouter } from 'vue-router'
const imgViewerVisible = ref(false)
const authority = resolveDirective('authority')
const myClient = ref(client)
const { proxy } = getCurrentInstance()
const isShowEditTableHeader = ref(false)
const dataSource = ref(null)
const router = useRouter()
const initDataSource = async () => {

  dataSource.value = new DataSource({
    modules: 'stock',
    selectUri: '/wms/main/list',
    listMethod: 'GET',
    pageSize: 20
  })
  dataSource.value.initTableHeader()

}

onBeforeMount(() => {
  initDataSource()
})

onMounted(() => {

  dataSource.value.searchData.productType = `单件`

  proxy.$refs.filter.backfillSearch(['productType'], ['单件'])

  dataSource.value.initData(this, proxy.$refs.table)
})

const doSearch = (data) => {
  dataSource.value.search(data, this, proxy.$refs.table)
}

const customizeEndHandle = (rowData, rowIndex) => {
  let render = [withDirectives(h(
    ElButton,
    { class: 'hover-animation', onClick: () => { goCheck(rowData) }, title: '查看库位库存', style: 'padding:0 10px;margin:0;background:transparent;width:100px;color:#0c81b1;font-size:12px' },
    { default: () => "查看库位库存" }), [[authority, 'wms:warehouse:edit']]
  ), withDirectives(h(
    ElButton,
    { class: 'hover-animation', onClick: () => { goCheckFlow(rowData) }, title: '查看库存流水', style: 'padding:0 10px;margin:0;background:transparent;width:100px;color:#0c81b1;font-size:12px' },
    { default: () => "查看库存流水" }), [[authority, 'wms:warehouse:edit']]
  )]

  return h('div', { class: 'table-handel-div' }, render)
}

const goCheckFlow = async (rowData) => {

  router.push(`/wms/kccx/record?warehouseName=${rowData.warehouseName}&warehouseCode=${rowData.warehouseCode}&productName=${rowData.productName}&productCode=${rowData.productCode}`)

}

const goCheck = async (rowData) => {

  router.push(`/wms/kccx/positionDetail?warehouseName=${rowData.warehouseName}&warehouseCode=${rowData.warehouseCode}&productName=${rowData.productName}&productCode=${rowData.productCode}`)

}

const formData = ref({})

const editControlCommon = reactive({
  isShow: false,
  formInputEl: [],
  formSelectEl: [],
  formTextAreaEl: [],
  formTimeAndNumber: [],
  formUploadEl: [],
  emitOpenDialog: (val) => {
    openSelectDialog(val)
  },
  inputDone: async (val) => {
    let params = {
      ...val
    }

    //

  }
})

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

const closeImgViewer = () => {
  imgViewerVisible.value = false
  const m = (e) => { e.preventDefault() }
  document.body.style.overflow = 'auto'
  document.removeEventListener("touchmove", m, true)
}

const cellHandle = {
  'avatar': {
    class: 'link',
    onClick: (rowData) => {
      imgViewerVisible.value = true
      imgList.length = 0
      imgList.push(rowData.avatar)
      const m = (e) => { e.preventDefault() }
      document.body.style.overflow = 'hidden'
      document.addEventListener("touchmove", m, false) // 禁止页面滑动

    }
  }
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

const getHeader = (val) => {

  return {
    title: '仓库库存记录',
    header: [
      '产品代码', '产品名称', '产品所在仓库', '产品类型', '产品型号', '产品单位', '现有量', '冻结量', '安全库存', '库存上限', '备注', '更新时间'
    ],
    style: {
      A1: {
        font: { bold: true },
        alignment: { horizontal: "center" },
        fill: { fgColor: { rgb: "FFFF00" } }
      },
      B1: {
        font: { bold: true },
        alignment: { horizontal: "center" },
        fill: { fgColor: { rgb: "FFFF00" } }
      },
      C1: {
        font: { bold: true },
        alignment: { horizontal: "center" },
        fill: { fgColor: { rgb: "FFFF00" } }
      },
      G1: {
        font: { bold: true },
        alignment: { horizontal: "center" },
        fill: { fgColor: { rgb: "FFFF00" } }
      },
      H1: {
        font: { bold: true },
        alignment: { horizontal: "center" },
        fill: { fgColor: { rgb: "FFFF00" } }
      },
      I1: {
        font: { bold: true },
        alignment: { horizontal: "center" },
        fill: { fgColor: { rgb: "FFFF00" } }
      },
      J1: {
        font: { bold: true },
        alignment: { horizontal: "center" },
        fill: { fgColor: { rgb: "FFFF00" } }
      }
    }
  }
}

const exportData = () => {
  let { title, header, style } = getHeader()
  let data = []
  dataSource.value.selections.forEach(item => {
    let { productCode, productName, warehouseName, productType,
      productSpecification, productUnit,
      nowNumber, frozenNumber, safeNumber, excessNumber,
      remark, updateTime } = item

    data.push([
      productCode, productName, warehouseName, productType,
      productSpecification, productUnit,
      nowNumber, frozenNumber, safeNumber, excessNumber,
      remark, updateTime
    ])
  })
  new proxy.$DataTool().exportExcel(header, data, title, style)
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