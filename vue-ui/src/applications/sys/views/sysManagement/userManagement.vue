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
          v-authorityHandle="'system:user:query'"
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
            :is-show-edit-table-header="isShowEditTableHeader" :modules="'userManagement'"
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
import dataFilter from '@/components/public/dataFilter.vue'
import { DataSource, loading } from '../../utils/sysManagement/userManagement'
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
    modules: 'userManagement',
    selectUri: '/system/user/list'
  })
  dataSource.value.initTableHeader()

  dataSource.value.initData(this, proxy.$refs.table)
}

onBeforeMount(() => {
  initDataSource()
})

const goCompile = async ({ userId }, rowIndex) => {
  editControlCommon.formInputEl[0].disabled = true
  editControlCommon.formInputEl[1].disabled = true
  editControlCommon.formInputEl[2].disabled = true
  const ctx = await dataSource.value.getUser(userId)
  editControlCommon.formSelectEl[0].options.length = 0
  let res = await dataSource.value.getRoleById(userInfo.user.userId)
  if (res.code === 200){
    editControlCommon.formSelectEl[0].options.length = 0
    res.data.roles.forEach(item => {
      let { roleId, roleName } = item
      editControlCommon.formSelectEl[0].options.push({
        label: roleName,
        value: roleId
      })
    })
  } else return
  formData.value = ctx.data.user
  formData.value.roleIds = formData.value.admin ? 1 : ctx.data.roleIds[0]
  if (rowIndex === 0) editControlCommon.formSelectEl[0].disabled = true
  editControlCommon.isShow = true
}

const addRow = async () => {
  editControlCommon.formInputEl[0].disabled = false
  editControlCommon.formInputEl[1].disabled = false
  editControlCommon.formInputEl[2].disabled = false
  let res = await dataSource.value.getRoleById(userInfo.user.userId)
  if (res.code === 200){
    editControlCommon.formSelectEl[0].options.length = 0
    res.data.roles.forEach(item => {
      let { roleId, roleName } = item
      editControlCommon.formSelectEl[0].options.push({
        label: roleName,
        value: roleId
      })
    })
  } else return
  formData.value = { status: '0', sex: '0', roleIds: [] }
  editControlCommon.isShow = true
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
    { class: 'hover-animation', onClick: () => { goCompile(rowData, rowIndex) }, title: '编辑', type: "primary", icon: "Edit", text: true, style: 'padding:0;margin:0;background:transparent' },
    { default: () => "" }), [[authority, 'system:user:edit']]
  ), withDirectives(h(
    ElButton,
    { class: 'hover-animation', onClick: () => { resetPsw(rowData) }, title: '重置密码', type: "success", icon: "Key", text: true, style: 'padding:0;margin:0;background:transparent' },
    { default: () => "" }), [[authority, 'system:user:resetPwd']]
  )]), h(
    ElButton,
    { style: 'margin: 0 10px', onClick: () => { loginData(rowData) }, title: '重置密码', type: "primary", link: true },
    { default: () => "登录日志" })]
}

const loginData = ({ userName }) => {
  router.push(`/system/loginRecord?userName=${userName}`)
}

const formData = ref({})
const timeStampSign = ref('')
const picType = ref('')
const editControlCommon = reactive({
  isShow: false,
  formInputEl: [
    {
      title: "用户账号",
      key: 'userName',
      element: 'input',
      rules: [{
        required: true,
        message: '该项不能为空',
        trigger: 'blur'
      }]
    }, {
      title: "密码",
      key: 'password',
      element: 'input',
      type: 'password'
    }, {
      title: "确认密码",
      key: 'repeatPassword',
      element: 'input',
      type: 'password'
    }, {
      title: "用户昵称",
      key: 'nickName',
      element: 'input',
      rules: [{
        required: true,
        message: '该项不能为空',
        trigger: 'blur'
      }]
    }, {
      title: "手机号码",
      key: 'phonenumber',
      element: 'input'
    }, {
      title: "用户邮箱",
      key: 'email',
      element: 'input'
    }
  ],
  formSelectEl: [{
    title: "用户角色",
    key: 'roleIds',
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
  }, {
    title: "性别",
    key: 'sex',
    element: 'radio',
    options: [{
      label: '男',
      value: '0'
    }, {
      label: '女',
      value: '1'
    }],
    rules: [{
      required: true,
      message: '该项不能为空',
      trigger: 'blur'
    }]
  }, {
    title: "账号状态",
    key: 'status',
    element: 'radio',
    options: [{
      label: '正常',
      value: '0'
    }, {
      label: '停用',
      value: '1'
    }],
    rules: [{
      required: true,
      message: '该项不能为空',
      trigger: 'blur'
    }]
  }],
  formTextAreaEl: [{
    title: "备注",
    key: 'remark',
    element: 'input',
    type: 'textarea'
  }],
  formUploadEl: [{
    title: "上传头像",
    key: 'avatar',
    element: 'input',
    onSuccess: () => {
      formData.value.avatar = 'https://jxwlapp.oss-cn-guangzhou.aliyuncs.com' + `/xindian/avatar/${timeStampSign.value}.${picType.value}`
      proxy.$refs.dialogForm.updateDialogInput({ avatar: 'https://jxwlapp.oss-cn-guangzhou.aliyuncs.com' + `/xindian/avatar/${timeStampSign.value}.${picType.value}` })
    },
    beforeUpload: (val) => {
      if (formData.value.avatar){
        timeStampSign.value = formData.value.avatar.split('avatar/')[1].split('.')[0]
      } else timeStampSign.value = Date.parse(new Date())
      picType.value = val.type.split('/')[1]
      myClient.value.name = 'test'
      myClient.value.key = `xindian/avatar/${timeStampSign.value}.${picType.value}`
      return true
    }
  }],
  emitOpenDialog: (val) => {
    openSelectDialog(val)
  },
  inputDone: async (val) => {
    let params = {
      ...val
    }
    params.roleIds = [params.roleIds]
    if (params.userId){
      let res = await dataSource.value.updateUser(params)
      if (res.code === 200){
        proxy.$message.success('更新成功')
        dataSource.value.initData(this, proxy.$refs.table)
        editControlCommon.isShow = false
      }
    } else {
      if (val.repeatPassword !== val.password) {
        proxy.$message.error('两次密码不一样')
        return
      }
      let res = await dataSource.value.addUser(params)
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