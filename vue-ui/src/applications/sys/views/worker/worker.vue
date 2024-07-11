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
          v-authorityHandle="'wms:employee:add'"
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
      :end-handle-width="180"
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
      @selectionChange="dataSource.selectionChange($event,dataSource,proxy.$refs.table)"
      @refresh="dataSource.initData(dataSource, proxy.$refs.table)"
      @editRow="goCompile"
      @current-change="dataSource.currentPageChange($event,dataSource,proxy.$refs.table)"
      @sizeChange="dataSource.pageSizeChange($event,dataSource,proxy.$refs.table)"
      @editTableHeader="isShowEditTableHeader = true"
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
            :is-show-edit-table-header="isShowEditTableHeader" :modules="'worker'"
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
      @emitOpenDialog="editControlCommon.emitOpenDialog"
      @inputDone="editControlCommon.inputDone"
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
      @doDialogSearch="selectTableData.doSearch"
      @chooseRow="selectTableData.chooseRow"
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
import { DataSource as warehouse } from "../fileManager/utils/warehouse"
import dataFilter from '@/components/public/dataFilter.vue'
import { DataSource, loading } from './utils/index'
import { ref, reactive, getCurrentInstance, onBeforeMount, h, resolveDirective, withDirectives } from 'vue'
import { ElButton, ElSwitch, ElImageViewer } from 'element-plus'
import client from '@/utils/upload/upLoadClient'
import router from '../../router'
import xButton from '@/components/common/xButton'
const imgViewerVisible = ref(false)
const authority = resolveDirective('authority')
const myClient = ref(client)
const { proxy } = getCurrentInstance()
const isShowEditTableHeader = ref(false)
const dataSource = ref(null)
const userInfo = JSON.parse(sessionStorage.getItem('userInfo'))
const initDataSource = () => {
  dataSource.value = new DataSource({
    modules: 'worker',
    selectUri: '/wms/employee/list',
    listMethod: 'GET'
  })
  dataSource.value.initTableHeader()

  dataSource.value.initData(this, proxy.$refs.table)
}

onBeforeMount(() => {
  initDataSource()
})

const goCompile = async (rowData, rowIndex) => {

  editControlCommon.formSelectEl[0].options.length = 0
  let res = await dataSource.value.getRoleById(userInfo.user.userId)
  if (res.code === 200){
    editControlCommon.formSelectEl[0].options.length = 0
    res.data.roles.forEach(item => {
      let { roleId, roleName } = item
      editControlCommon.formSelectEl[0].options.push({
        label: roleName,
        value: roleId + ' ' + roleName
      })
    })
  } else return
  formData.value = rowData
  formData.value.role = rowData.employeeRoleName
  editControlCommon.isShow = true
}

const addRow = async () => {
  let res = await dataSource.value.getRoleById(userInfo.user.userId)
  if (res.code === 200){
    editControlCommon.formSelectEl[0].options.length = 0
    res.data.roles.forEach(item => {
      let { roleId, roleName } = item
      editControlCommon.formSelectEl[0].options.push({
        label: roleName,
        value: roleId + ' ' + roleName
      })
    })
  } else return
  formData.value = { }
  editControlCommon.isShow = true
}

const forMatDataPro = (row, column) => {
  const warehouseType = {
    warehouse: '仓库',
    area: '库区',
    frame: '货架',
    position: '仓位'

  }
  if (column.property === 'warehouseType') return warehouseType[row[column.property]] ?? '-'
  if (column.property === 'processStatus') return processStatus1[row[column.property]] ?? '-'
  return row[column.property] ?? '-'
}

const resetPsw = (rowData) => {
  let params = {
    ...rowData
  }
  proxy.$prompt('请输入新的密码', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  }).then(({ value }) => {
    params.password = value
    dataSource.value.changePsw(params).then(res => {
      if (res.code === 200) proxy.$message.success('修改成功')
      dataSource.value.initData(this, proxy.$refs.table)
    })
  }).catch(() => {
    //null
  })
}

const doSearch = (data) => {
  dataSource.value.search(data, this, proxy.$refs.table)
}

const customizeEndHandle = (rowData, rowIndex) => {
  return [h('div', { class: 'table-handel-div' }, [withDirectives(h(
    ElButton,
    { class: 'hover-animation', onClick: () => { goCompile(rowData) }, title: '编辑', style: 'padding:0 10px;margin:0;background:transparent;width:80px;color:#0c81b1;margin-right:10px;font-size:12px' },
    { default: () => "编辑" }), [[authority, 'wms:employee:edit']]
  ), withDirectives(h(
    ElButton,
    { class: 'hover-animation', onClick: () => {
      deleteRow(rowData) }, title: '删除',
    style: 'padding:0 10px;margin:0;background:transparent;width:80px;color:#e81123;font-size:12px' },
    { default: () => "删除" }), [[authority, 'wms:employee:remove']]
  )])]
}

const deleteRow = async (rowData) => {
  proxy.$alert(`是否确认删除此员工？`, '提示', {
    type: 'error',
    showCancelButton: true,
    cancelButtonText: '再想想',
    confirmButtonText: '确认删除',

    callback: (action) => {
      if (action === 'cancel') return
      else {
        dataSource.value.deleteWorker(rowData.id).then((res) => {
          if (res.code === 200){
            proxy.$message.success('删除成功')
            dataSource.value.initData(this, proxy.$refs.table)
          }
        })
      }
    }
  })
}

const formData = ref({})

const editControlCommon = reactive({
  isShow: false,
  formInputEl: [
    {
      title: "员工姓名",
      key: 'employeeName',
      element: 'input',

      rules: [{
        required: true,
        message: '该项不能为空',
        trigger: 'blur'
      }]
    }, {
      title: "员工电话",
      key: 'employeePhone',
      element: 'input',
      rules: [{
        required: true,
        message: '该项不能为空',
        trigger: 'blur'
      }]
    }, {
      title: "登录账号",
      key: 'accountId',
      element: 'input',
      rules: [{
        required: true,
        message: '该项不能为空',
        trigger: 'blur'
      }]
    }, {
      title: "密码",
      key: 'accountPassword',
      element: 'input',
      type: 'password'
    }, {
      title: "确认密码",
      key: 'repeatPassword',
      element: 'input',
      type: 'password'
    }, {
      title: "所属仓库",
      key: 'employeeWarehouseName',
      element: 'input',
      type: 'dialog',
      rules: [{
        required: true,
        message: '该项不能为空',
        trigger: 'blur'
      }]
    }, {
      title: "用户邮箱",
      key: 'email',
      element: 'input'
    }

  ],
  formSelectEl: [{
    title: "用户角色",
    key: 'role',
    element: 'select',
    options: [],
    rules: [{
      required: true,
      message: '该项不能为空',
      trigger: 'blur'
    }],
    change: (val) => {
      // NULL
    }
  }],
  formTextAreaEl: [{
    title: "备注",
    key: 'remark',
    element: 'input',
    type: 'textarea'
  }],
  formUploadEl: [],
  emitOpenDialog: (val) => {
    selectWarehouse()
  },
  inputDone: async (val) => {
    let params = {
      ...val
    }
    params.employeeRoleName = params.role.split(' ')[1]
    params.roleIds = params.role.split(' ')[0]

    if (params.userId){
      let res = await dataSource.value.updateWorker(params)
      if (res.code === 200){
        proxy.$message.success('更新成功')
        dataSource.value.initData(this, proxy.$refs.table)
        editControlCommon.isShow = false
      }
    } else {
      if (!val.accountPassword) {
        proxy.$message.error('请输入密码')
        return
      }
      if (val.repeatPassword !== val.accountPassword) {
        proxy.$message.error('两次密码不一样')
        return
      }
      let res = await dataSource.value.addWorker(params)
      if (res.code === 200){
        proxy.$message.success('新增成功')
        dataSource.value.initData(this, proxy.$refs.table)
        editControlCommon.isShow = false
      }
    }
  }
})
const customizeCellRenderer = ({ forMatValue, rowIndex, column, rowData }) => {
  if (column.key === 'status') return [h(ElSwitch, { modelValue: !+rowData.status, onChange: (val) => {
    proxy.$alert(`是否变更用户账号状态，当前： ${!val ? '启用' : '停用'} ，变更为：${val ? '启用' : '停用'}`, '提示', {
      type: 'error',
      showCancelButton: true,
      cancelButtonText: '再想想',
      confirmButtonText: '确认',

      callback: (action) => {
        if (action === 'cancel') return
        else {
          rowData.status = !val ? '1' : '0'
          dataSource.value.changeStatus(rowData).then((res) => {
            if (res.code === 200){
              proxy.$message.success('修改成功')
            }
            dataSource.value.initData(this, proxy.$refs.table)
          })
        }
      }
    })
  } })]
  return forMatValue
}

const selectWarehouse = async () => {

  selectTableData.title = '请选择出库仓'
  selectTableData.placeholder = '输入仓库名称搜索'
  selectTableData.needSelection = false
  selectTableData.dataSource = new warehouse({
    selectUri: '/wms/warehouse/list',
    listMethod: 'GET',
    pageSize: 10,
    tableHeader: [{
      title: "仓库名称",
      dataKey: "warehouseName",
      key: 'warehouseName',
      width: 200,
      type: 'text',
      isShow: true,
      isFixed: true
    }, {
      title: "仓库类型",
      dataKey: "warehouseType",
      key: 'warehouseType',
      width: 200,
      type: 'select',
      isShow: true,
      isFixed: true,
      options: []
    }, {
      title: "仓库地址",
      dataKey: "warehouseAddress",
      key: 'warehouseAddress',
      width: 200,
      type: 'text',
      isShow: true,
      isFixed: false
    }]
  })
  selectTableData.dataSource.forMatDataV2 = forMatDataPro
  selectTableData.chooseRow = (val) => {
    proxy.$refs.dialogForm.updateDialogInput({ employeeWarehouseName: val.warehouseName, employeeWarehouseCode: val.warehouseCode })
    selectTableData.isShow = false
  }

  selectTableData.doSearch = async (val) => {
    selectTableData.dataSource.searchData = { }
    if (val) selectTableData.dataSource.searchData.warehouseName = val
    await selectTableData.dataSource.initData()
    selectTableData.dataSource.tableData.forEach(item => {
      delete item.children
    })
    selectTableData.dataSource.total = selectTableData.dataSource.tableData.length
  }

  selectTableData.dataSource.searchData = { }
  await selectTableData.dataSource.initData()
  selectTableData.dataSource.tableData.forEach(item => {
    delete item.children
  })
  selectTableData.dataSource.total = selectTableData.dataSource.tableData.length
  selectTableData.isShow = true
  return

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