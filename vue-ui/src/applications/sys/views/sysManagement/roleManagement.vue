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
          v-authorityHandle="'system:role:add'"
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
      need-customize-cell-renderer
      row-key="roleId"
      :end-handle-width="140"
      :total="dataSource.total"
      :current-page="dataSource.currentPage"
      :page-size="dataSource.pageSize"
      :table-data="dataSource.tableData"
      :columns="dataSource.tableHeader"
      :loading="loading&&!editControlCommon.isShow&&!dataLimit.isShow"
      :default-end-handle="false"
      :customize-end-handle="customizeEndHandle"
      :for-mat-data="dataSource.forMatData"
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
            :is-show-edit-table-header="isShowEditTableHeader" :modules="'role'"
          />
        </div>
      </template>
    </el-drawer>
    <selectDialogForm
      v-if="editControlCommon.isShow"
      ref="dialogForm"
      :width="'60%'"
      is-limits
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
      :select-tree-node="selectTreeNode"
      :default-expanded-keys="defaultExpandedKeys"
      @close-dialog="editControlCommon.isShow=false"
      @emitOpenDialog="editControlCommon.emitOpenDialog"
      @inputDone="editControlCommon.inputDone"
    />
    <dataLimits
      v-if="dataLimit.isShow"
      ref="dataLimitForm"
      :form-data="formData"
      :loading="loading"
      :is-show="dataLimit.isShow"
      :form-el="dataLimit.formEl"
      :default-expanded-keys="dataLimit.defaultExpandedKeys"
      :select-tree-node="dataLimit.selectTreeNode"
      @close-dialog="dataLimit.isShow=false"
      @emitOpenDialog="dataLimit.emitOpenDialog"
      @inputDone="dataLimit.inputDone"
    />
  </div>
</template>

<script setup>
import selectDialogForm from '@/components/public/selectDialogForm.vue'
import dataLimits from './components/dataLimits.vue'
import dynamicHeader from '@/components/public/dynamicHeader.vue'
import VTable from '@/components/public/virtualTable.vue'
import dataFilter from '@/components/public/dataFilter.vue'
import { DataSource, loading } from '../../utils/sysManagement/roleManagement'
import { ref, reactive, getCurrentInstance, onBeforeMount, h, resolveDirective, withDirectives } from 'vue'
import { ElButton, ElSwitch } from 'element-plus'
import client from '@/utils/upload/upLoadClient'

const authority = resolveDirective('authority')
const myClient = ref(client)
const { proxy } = getCurrentInstance()
const isShowEditTableHeader = ref(false)
const dataSource = ref(null)

const initDataSource = () => {
  dataSource.value = new DataSource({
    modules: 'role',
    selectUri: '/system/role/list'
  })
  dataSource.value.initTableHeader()
  dataSource.value.initData(this, proxy.$refs.table)
}

onBeforeMount(() => {
  initDataSource()
})

const selectTreeNode = ref([])
const defaultExpandedKeys = ref([])

const addRow = async () => {
  const { data, code } = await new proxy.$DataTool().getMenuSelect()
  if (code === 200) selectTreeNode.value = data
  else return
  formData.value = { status: '0', roleSort: dataSource.value.total + 1 }
  defaultExpandedKeys.value = []
  editControlCommon.isShow = true
}

const goCompile = async ({ roleId }) => {
  let { data, code } = await dataSource.value.getRoleMenu(roleId)
  if (code === 200) {
    defaultExpandedKeys.value = data.checkedKeys
    selectTreeNode.value = data.menus
  }
  else return
  ({ data, code } = await dataSource.value.getRoleInfo(roleId))
  if (code === 200) formData.value = data
  else return
  editControlCommon.isShow = true
}

const doSearch = (data) => {
  dataSource.value.search(data, this, proxy.$refs.table)
}

const customizeEndHandle = (rowData) => {
  if (rowData.admin){
    return [h('div', { class: 'table-handel-div no-options' }, '暂无操作')]
  }
  return [h('div', { class: 'table-handel-div' }, [withDirectives(h(
    ElButton,
    { class: 'hover-animation', onClick: () => { goCompile(rowData) }, title: '编辑', type: "primary", icon: "Edit", text: true, style: 'padding:0;margin:0;background:transparent' },
    { default: () => "" }), [[authority, 'system:role:edit']]
  ), withDirectives(h(
    ElButton,
    { class: 'hover-animation', onClick: () => { editDataLimits(rowData) }, title: '数据权限', type: "warning", icon: "EditPen", text: true, style: 'padding:0;margin:0;background:transparent' },
    { default: () => "" }), [[authority, 'system:role:edit']]
  )])]
}

const formData = ref({})

const editControlCommon = reactive({
  isShow: false,
  formInputEl: [{
    title: "角色名称",
    key: 'roleName',
    element: 'input',
    rules: [{
      required: true,
      message: '该项不能为空',
      trigger: 'blur'
    }]
  }, {
    title: "权限字符",
    key: 'roleKey',
    element: 'input'
  }],
  formSelectEl: [{
    title: "状态",
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
  formTimeAndNumber: [{
    title: "显示顺序",
    key: 'roleSort',
    element: 'number'
  }],
  emitOpenDialog: (val) => {
    console.log(val)
  },
  inputDone: (val) => {

    if (val.roleId){
      dataSource.value.upDateRole(val).then((res) => {
        if (res.code === 200){
          editControlCommon.isShow = false
          proxy.$message.success('更新成功')
          dataSource.value.initData(this, proxy.$refs.table)
        }
      })
    } else {
      val.dataScope = '4'
      dataSource.value.addRole(val).then((res) => {
        if (res.code === 200){
          editControlCommon.isShow = false
          proxy.$message.success('新增成功')
          dataSource.value.initData(this, proxy.$refs.table)
        }
      })
    }

  }
})

const customizeCellRenderer = ({ forMatValue, rowIndex, column, rowData }) => {
  if (column.key === 'status') return [h(ElSwitch, { modelValue: !+rowData.status, onChange: (val) => {
    proxy.$alert(`是否变更该角色状态，当前： ${!val ? '启用' : '停用'} ，变更为：${val ? '启用' : '停用'}`, '提示', {
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

const dataLimit = reactive({
  isShow: false,
  formEl: [{
    title: "角色名称",
    key: 'roleName',
    element: 'input',
    disabled: true
  }, {
    title: "权限字符",
    key: 'roleKey',
    element: 'input',
    disabled: true
  }, {
    title: "数据权限范围",
    key: 'dataScope',
    element: 'select',
    options: [
      {
        label: '所有数据范围',
        value: '1'
      }, {
        label: '自定义数据权限',
        value: '2'
      }, {
        label: '本部门数据权限',
        value: '3'
      }, {
        label: '本部门及以下数据权限',
        value: '4'
      }, {
        label: '仅本人数据权限',
        value: '5'
      }
    ]
  }],
  defaultExpandedKeys: [],
  selectTreeNode: [],
  emitOpenDialog: (val) => {
    console.log(val)
  },
  inputDone: (val) => {
    if (val.roleId){
      if (!val.deptIds) val.deptIds = []
      dataSource.value.setDataScope(val).then((res) => {
        if (res.code === 200){
          dataLimit.isShow = false
          proxy.$message.success('更新成功')
          dataSource.value.initData(this, proxy.$refs.table)
        }
      })
    }
  }
})

const editDataLimits = async ({ roleId }) => {
  let { data, code } = await dataSource.value.getDeptTree(roleId)
  if (code === 200){
    dataLimit.defaultExpandedKeys = data.checkedKeys
    dataLimit.selectTreeNode = data.depts
  } else return
  ({ data, code } = await dataSource.value.getRoleInfo(roleId))
  if (code === 200) formData.value = data
  else return
  dataLimit.isShow = true
}
</script>

<style lang="less" scoped>
.table{
  padding: 10px;
  background-color: #ffffff;
}
</style>
