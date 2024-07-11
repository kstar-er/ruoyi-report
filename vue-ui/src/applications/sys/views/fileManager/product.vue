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
          v-authorityHandle="'wms:product:add'"
          @click="addRow"
        >
          新增
        </xButton>
        <el-upload
          class="inline-block ml12"
          accept=".xlsx, .xls"
          :on-change="fileChoice"
          :show-file-list="false"
          :auto-upload="false"
        >
          <el-button
            class="handle-btn mr10"
            color="#4a78bd"
            style="color: #666"
            plain
          >
            导入Excel
          </el-button>
        </el-upload>
        <el-tooltip

          class="box-item"
          effect="dark"
          content="下载导入模板"
          placement="top"
        >
          <el-button
            style="margin-top:2px;color: #FFFFFF"
            color="#5f6368"
            size="small"
            icon="Download" circle
            @click="downloadTemplate"
          />
        </el-tooltip>
      </div>
    </div>
    <v-table
      ref="table"
      need-end-handle
      :need-check-box="false"
      :end-handle-width="280"
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
            :is-show-edit-table-header="isShowEditTableHeader" :modules="'product'"
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

    <el-image-viewer
      v-if="imgViewerVisible"
      :url-list="imgList"
      @close="closeImgViewer"
    />
  </div>
</template>

<script setup>
import selectDialogTable from '@/components/public/selectDialogTable.vue'
import selectDialogForm from '@/components/public/selectDialogForm.vue'
import dynamicHeader from '@/components/public/dynamicHeader.vue'
import VTable from '@/components/public/virtualTable.vue'
import dataFilter from '@/components/public/dataFilter.vue'
import { DataSource, loading } from './utils/product'
import { DataSource as type } from './utils/productType'
import { ref, reactive, getCurrentInstance, onBeforeMount, h, resolveDirective, withDirectives } from 'vue'
import { ElButton, ElImageViewer } from 'element-plus'
import client from '@/utils/upload/upLoadClient'
import router from '../../router'
import xButton from '@/components/common/xButton'
const imgViewerVisible = ref(false)
const authority = resolveDirective('authority')
const myClient = ref(client)
const { proxy } = getCurrentInstance()
const isShowEditTableHeader = ref(false)
const dataSource = ref(null)
const resType = ref(null)
const initDataSource = async () => {

  dataSource.value = new DataSource({
    modules: 'product',
    selectUri: '/wms/product/list',
    listMethod: 'GET',
    pageSize: 20
  })
  dataSource.value.initTableHeader()
  await dataSource.value.initData(this, proxy.$refs.table)

  resType.value = new type({
    selectUri: '/wms/category/list',
    listMethod: 'GET',
    pageSize: 1000000
  })
  await resType.value.initData(this, proxy.$refs.table)

}

onBeforeMount(() => {
  initDataSource()
})

const goCompile = async (rowData) => {
  if (editControlCommon.formSelectEl[0].options.length === 0){
    editControlCommon.formSelectEl[0].options.push(...resType.value.tableData)
  }
  formData.value = { ...rowData, categoryId: rowData.categoryId?.toString(), height: +rowData.height,
    length: +rowData.length,
    volume: +rowData.volume
    , weight: +rowData.weight, width: +rowData.width }
  dataSource.value.getProductSon(rowData.id).then(res => {
    if (res.code === 200){

      formData.value.children = [...res.data]
      formData.value.childrenCount = res.data.length
      editControlCommon.isShow = true
    }
  })

}

const addRow = async (rowData) => {
  if (editControlCommon.formSelectEl[0].options.length === 0){
    editControlCommon.formSelectEl[0].options.push(...resType.value.tableData)

  }
  formData.value = { }
  editControlCommon.isShow = true
}

const doSearch = (data) => {
  dataSource.value.search(data, this, proxy.$refs.table)
}

const deleteRow = async (rowData) => {
  proxy.$alert(`是否确认删除此商品？`, '提示', {
    type: 'error',
    showCancelButton: true,
    cancelButtonText: '再想想',
    confirmButtonText: '确认删除',

    callback: (action) => {
      if (action === 'cancel') return
      else {
        dataSource.value.deleteProduct(rowData.id).then((res) => {
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
  let render = [h('div', { class: 'table-handel-div' }, [withDirectives(h(
    ElButton,
    { class: 'hover-animation', onClick: () => { goCompile(rowData) }, title: '更新信息', style: 'padding:0 10px;margin:0;background:transparent;width:80px;color:#0c81b1;font-size:12px' },
    { default: () => "更新信息" }), [[authority, 'wms:product:edit']]
  ), withDirectives(h(
    ElButton,
    { class: 'hover-animation', onClick: () => { deleteRow(rowData) }, title: '删除产品', style: 'padding:0 10px;margin:0;background:transparent;width:80px;color:#e81123;font-size:12px' },
    { default: () => "删除产品" }), [[authority, 'wms:product:remove']]
  )])]
  if (rowData.productType === '组合'){
    render.splice(0, 0, withDirectives(h(
      ElButton,
      { class: 'hover-animation', onClick: () => {
        router.push(`/product/productDetail?id=${rowData.id}&productName=${rowData.productName}`)
      }, title: '查看子产品', style: 'padding:0 10px;margin:0;background:transparent;width:80px;color:#0c81b1;font-size:12px' },
      { default: () => "查看子产品" }), [[authority, 'wms:product:query']]
    ))
  }
  return render
}

const formData = ref({})

const editControlCommon = reactive({
  isShow: false,
  formInputEl: [
    {
      title: "产品名称",
      key: 'productName',
      element: 'input',
      rules: [{
        required: true,
        message: '该项不能为空',
        trigger: 'blur'
      }]
    }, {
      title: "产品代码",
      key: 'productCode',
      element: 'input',
      rules: [{
        required: true,
        message: '该项不能为空',
        trigger: 'blur'
      }]
    }, {
      title: "条码头",
      key: 'barCodeHead',
      element: 'input',
      rules: [{
        required: true,
        message: '该项不能为空',
        trigger: 'blur'
      }]
    }, {
      title: "产品型号",
      key: 'productSpecification',
      element: 'input'
    }, {
      title: "品牌",
      key: 'brand',
      element: 'input'
    }, {
      title: "单位",
      key: 'unit',
      element: 'input'
    }
  ],
  formSelectEl: [{
    title: "产品类别",
    key: 'categoryId',
    element: 'selectTree',
    options: [],
    change: (checkedNode) => {
      proxy.$refs.dialogForm.updateDialogInput({ categoryName: checkedNode.categoryName })
    }
  }, {
    title: "产品类型",
    key: 'productType',
    element: 'select',
    options: [{
      label: '单件',
      value: '单件'
    }, {
      label: '组合',
      value: '组合'
    }],
    rules: [{
      required: true,
      message: '该项不能为空',
      trigger: 'blur'
    }],
    change: (val) => {
      console.log(val)
    }
  }, {
    title: "使用统一条码",
    key: 'productBarcodeAlways',
    element: 'select',
    options: [{
      label: '是',
      value: true
    }, {
      label: '否',
      value: false
    }],
    rules: [{
      required: true,
      message: '该项不能为空',
      trigger: 'blur'
    }],
    illustrate: '同一商品编号，使用相同的条码头，每个商品的条码不一致选“否”，同一商品编号，所有商品条码一致，选“是”'
  }],
  formTextAreaEl: [{
    title: "备注",
    key: 'remark',
    element: 'input',
    type: 'textarea'
  }],
  formTimeAndNumber: [
    {
      title: "重量/KG",
      key: 'weight',
      element: 'number',
      type: 'textarea'
    }, {
      title: "体积/V",
      key: 'volume',
      element: 'number',
      type: 'textarea'
    }, {
      title: "长/M",
      key: 'length',
      element: 'number',
      type: 'textarea'
    }, {
      title: "宽/M",
      key: 'width',
      element: 'number',
      type: 'textarea'
    }, {
      title: "高/M",
      key: 'height',
      element: 'number',
      type: 'textarea'
    }
  ],
  formUploadEl: [],
  emitOpenDialog: (val) => {
    openSelectDialog(val)
  },
  inputDone: async (val) => {
    let params = {
      ...val
    }

    if (params.productType === '组合' && !params.id){
      params.childrenCount = 0
      params.children = []
    }
    let res = await dataSource.value.addProduct([params])
    if (res.code === 200){
      if (params.productType === '组合' && !params.id){
        proxy.$alert(`你添加了一个组合产品，是否继续为该组合产品添加子产品？`, '提示', {
          type: 'success',
          showCancelButton: true,
          cancelButtonText: '等下添加',
          confirmButtonText: '前往添加',

          callback: (action) => {
            if (action === 'cancel') {
              // null
            }
            else {
              router.push(`/product/productDetail?id=${ res.data[0].id}&productName=${ res.data[0].productName}`)
            }
            dataSource.value.initData(this, proxy.$refs.table)
            editControlCommon.isShow = false
          }
        })
      } else {
        proxy.$alert(`操作成功`, '提示', {
          type: 'success',
          showCancelButton: false,
          confirmButtonText: '继续',

          callback: (action) => {
            dataSource.value.initData(this, proxy.$refs.table)
            editControlCommon.isShow = false
          }
        })
      }
    }

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

const fileChoice = async (file) => {

  let xlsxData = await new proxy.$DataTool().xlsx2DataObject(file.raw)
  let data = []
  let tempProductName = []

  xlsxData.forEach(item => {
    let detail = {
      productType: item['产品类型'] ?? '',
      productName: item['产品名称'] ?? '',
      productCode: item['产品代码'] ?? '',
      barCodeHead: item['条码头'] ?? '',
      productSpecification: item['产品型号'] ?? '',
      brand: item['品牌'] ?? '',
      unit: item['单位'] ?? '',
      weight: item['重量/KG'] ?? 0,
      volume: item['体积/V'] ?? 0,
      length: item['长/M'] ?? 0,
      width: item['宽/M'] ?? 0,
      height: item['高/M'] ?? 0,
      productBarcodeAlways: item['使用统一条码'] === '是' ? 1 : 0,
      remark: item['备注']
    } // 基础信息

    if (detail.productType === '组合'){
      detail.children = []
      detail.childrenCount = 0 // 如果是组合，就要加children
      data.push(detail)
      tempProductName.push(detail.productName)

    } else {

      if (item['父产品名称（组合必填）']){ // 如果不是，看看是不是有父产品名称
        // 有的话看看父产品名称在哪个索引
        let idx = tempProductName.findIndex(tempProductName => tempProductName === item['父产品名称（组合必填）'])
        console.log(idx)

        data[idx].children.push(detail)
        data[idx].childrenCount += 1
      } else {
        tempProductName.push('-')
        data.push(detail)
      }
    }
  })
  dataSource.value.addProduct(data).then(res => {
    if (res.code === 200) {
      proxy.$message.success('导入成功')
      dataSource.value.initData(this, proxy.$refs.table)
    }
  })
}

const downloadTemplate = () => {
  window.location = 'https://jxwlapp.oss-cn-guangzhou.aliyuncs.com/excelTemplate/pro/产品导入模板.xlsx?download'
}
</script>

<style lang="less" scoped>
.table{
  padding: 10px;
  background-color: #ffffff;
}
</style>