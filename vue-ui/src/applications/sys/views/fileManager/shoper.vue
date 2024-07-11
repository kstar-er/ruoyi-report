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
          v-authorityHandle="'wms:merchant:add'"
          @click="addRow"
        >
          新增
        </xButton>
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
            :is-show-edit-table-header="isShowEditTableHeader" :modules="'shoper'"
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
import { DataSource, loading } from './utils/shoper'

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
    modules: 'shoper',
    selectUri: '/wms/merchant/list',
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

  formData.value = { ...rowData, place: [rowData.province, rowData.city, rowData.region, rowData.street] }

  editControlCommon.isShow = true

}

const addRow = async (rowData) => {

  formData.value = { }
  editControlCommon.isShow = true
}

const doSearch = (data) => {
  dataSource.value.search(data, this, proxy.$refs.table)
}

const deleteRow = async (rowData) => {
  proxy.$alert(`是否确认删除此商家？`, '提示', {
    type: 'error',
    showCancelButton: true,
    cancelButtonText: '再想想',
    confirmButtonText: '确认删除',

    callback: (action) => {
      if (action === 'cancel') return
      else {
        dataSource.value.deleteMerchant(rowData.id).then((res) => {
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
    { default: () => "更新信息" }), [[authority, 'wms:merchant:edit']]
  ), withDirectives(h(
    ElButton,
    { class: 'hover-animation', onClick: () => { deleteRow(rowData) }, title: '删除产品', style: 'padding:0 10px;margin:0;background:transparent;width:80px;color:#e81123;font-size:12px' },
    { default: () => "删除商家" }), [[authority, 'wms:merchant:remove']]
  )])]

  return render
}

const formData = ref({})

const editControlCommon = reactive({
  isShow: false,
  formInputEl: [
    {
      title: "商家名称",
      key: 'merchantName',
      element: 'input',
      rules: [{
        required: true,
        message: '该项不能为空',
        trigger: 'blur'
      }]
    }, {
      title: "登录账号名",
      key: 'accountId',
      element: 'input',
      rules: [{
        required: true,
        message: '该项不能为空',
        trigger: 'blur'
      }]
    }, {
      title: "商家联系人",
      key: 'merchantLinkman',
      element: 'input',
      rules: [{
        required: true,
        message: '该项不能为空',
        trigger: 'blur'
      }]
    }, {
      title: "联系电话",
      key: 'merchantLinkmanPhone',
      element: 'input',
      rules: [{
        required: true,
        message: '该项不能为空',
        trigger: 'blur'
      }]
    }, {
      title: "商家简称",
      key: 'merchantShortName',
      element: 'input'
    }, {
      title: "电子邮箱",
      key: 'email',
      element: 'input'
    }
  ],
  formSelectEl: [{
    title: "省/市/区",
    key: 'place',
    element: 'proAndCityAndArea'

  }],
  formTextAreaEl: [{
    title: "详细地址",
    key: 'storeAddress',
    element: 'input',
    type: 'textarea'
  }, {
    title: "备注",
    key: 'remark',
    element: 'input',
    type: 'textarea'
  }],
  formTimeAndNumber: [],
  formUploadEl: [],
  emitOpenDialog: (val) => {
    openSelectDialog(val)
  },
  inputDone: async (val) => {
    let params = {
      ...val
    }

    params.province = params.place[0]
    params.city = params.place[1]
    params.region = params.place[2]
    params.street = params.place[3]

    if (params.id){
      let res = await dataSource.value.updateMerchant(params)
      if (res.code === 200){
        proxy.$message.success(`更新成功`)
        dataSource.value.initData(this, proxy.$refs.table)
        editControlCommon.isShow = false
      }
    } else {
      let res = await dataSource.value.addMerchant(params)
      if (res.code === 200){
        proxy.$message.success(`新增成功`)
        dataSource.value.initData(this, proxy.$refs.table)
        editControlCommon.isShow = false
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
</script>

<style lang="less" scoped>
.table{
  padding: 10px;
  background-color: #ffffff;
}
</style>