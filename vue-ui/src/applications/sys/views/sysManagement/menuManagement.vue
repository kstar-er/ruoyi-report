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
        <el-button
          class="handle-btn"
          color="#4a78bd"
          style="color: #666"
          plain
          @click="close"
        >
          全部折叠
        </el-button>
        <el-button
          v-authorityHandle="'system:menu:add'"
          class="handle-btn"
          color="#4a78bd"
          style="color: #666"
          plain
          @click="addRow"
        >
          新增
        </el-button>
      </div>
    </div>
    <v-table
      ref="table"
      need-end-handle
      :need-check-box="false"
      need-customize-cell-renderer
      need-expand
      row-key="menuId"
      :need-pagination="false"
      :total="dataSource.total"
      :current-page="dataSource.currentPage"
      :page-size="dataSource.pageSize"
      :table-data="dataSource.tableData"
      :columns="dataSource.tableHeader"
      :loading="loading&&!editControlCommon.isShow"
      :default-end-handle="false"
      :customize-end-handle="customizeEndHandle"
      :for-mat-data="dataSource.forMatData"
      :customize-cell-renderer="customizeCellRenderer"
      :end-handle-width="140"
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
            :is-show-edit-table-header="isShowEditTableHeader" :modules="'menu'"
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
    >
      <template #customOperation>
        <div class="flex-center ml10">
          <span style="font-size:14px;font-weight:500;color:#606266">权限类型:</span>
          <el-radio-group
            v-model="menuType"
            class="ml10"
            :disabled="formData.menuId ? true:false"
            @change="menuTypeChange"
          >
            <el-radio
              label="M"
            >
              目录
            </el-radio>
            <el-radio
              label="C"
            >
              菜单
            </el-radio>
            <el-radio
              label="F"
            >
              按钮
            </el-radio>
          </el-radio-group>
        </div>
      </template>
    </selectDialogForm>
  </div>
</template>

<script setup>
import selectDialogForm from '@/components/public/selectDialogForm.vue'
import dynamicHeader from '@/components/public/dynamicHeader.vue'
import VTable from '@/components/public/virtualTable.vue'
import dataFilter from '@/components/public/dataFilter.vue'
import { DataSource, loading } from '../../utils/sysManagement/menuManagement'
import { ref, reactive, getCurrentInstance, onBeforeMount, h, resolveDirective, withDirectives } from 'vue'
import { ElButton, ElTag } from 'element-plus'
import client from '@/utils/upload/upLoadClient'

const authority = resolveDirective('authority')
const myClient = ref(client)
const { proxy } = getCurrentInstance()
const isShowEditTableHeader = ref(false)
const dataSource = ref(null)

const initDataSource = async () => {
  dataSource.value = new DataSource({
    modules: 'menu',
    selectUri: '/system/menu/list'
  })
  dataSource.value.initTableHeader()
  await dataSource.value.initData(this, proxy.$refs.table)
}

onBeforeMount(() => {
  initDataSource()
})

const goCompile = async ({ menuId }) => {
  let ctx = await dataSource.value.getMenuInfo(menuId)
  if (ctx.code === 200){
    formData.value = { ...ctx.data }
    menuTypeChange(formData.value.menuType)
    menuType.value = formData.value.menuType
    editControlCommon.formSelectEl[0].options = [editControlCommon.formSelectEl[0].options[0]]
    editControlCommon.formSelectEl[0].options.push(...dataSource.value.tableData)
  }
  editControlCommon.isShow = true
}

const addRow = async () => {
  menuTypeChange(menuType.value)
  editControlCommon.formSelectEl[0].options = [editControlCommon.formSelectEl[0].options[0]]
  editControlCommon.formSelectEl[0].options.push(...dataSource.value.tableData)
  formData.value = { parentId: 0, orderNum: 0, isFrame: '1', status: '0', visible: '0', isCache: '0', icon: '#' }
  editControlCommon.isShow = true
}

const addByCurrent = async ({ menuId, path, perms }) => {
  menuTypeChange(menuType.value)
  editControlCommon.formSelectEl[0].options = [editControlCommon.formSelectEl[0].options[0]]
  editControlCommon.formSelectEl[0].options.push(...dataSource.value.tableData)
  formData.value = { parentId: menuId, orderNum: 0, isFrame: '1', isCache: '0', status: '0', visible: '0', perms: perms ? perms : path }
  editControlCommon.isShow = true
}

const doSearch = (data) => {
  dataSource.value.search(data, this, proxy.$refs.table)
}

const customizeEndHandle = (rowData) => {
  if (rowData.menuType === 'F'){
    return [h('div', { class: 'table-handel-div' }, [withDirectives(h(
      ElButton,
      { class: 'hover-animation', onClick: () => { goCompile(rowData) }, title: '编辑', type: "primary", icon: "Edit", text: true, style: 'padding:0;margin:0;background:transparent' },
      { default: () => "" }), [[authority, 'system:menu:edit']]
    )])]
  } else {
    return [h('div', { class: 'table-handel-div' }, [withDirectives(h(
      ElButton,
      { class: 'hover-animation', onClick: () => { goCompile(rowData) }, title: '编辑', type: "primary", icon: "Edit", text: true, style: 'padding:0;margin:0;background:transparent' },
      { default: () => "" }), [[authority, 'system:menu:edit']]
    ), withDirectives(h(
      ElButton,
      { class: 'hover-animation', onClick: () => { addByCurrent(rowData) }, title: '在当前目录新增', type: "warning", icon: "CirclePlus", text: true, style: 'padding:0;margin:0;background:transparent' },
      { default: () => "" }), [[authority, 'system:menu:edit']]
    )])]
  }

}

const formData = ref({})
const menuType = ref('M')

const editControlCommon = reactive({
  isShow: false,
  formInputEl: [],
  formSelectEl: [{
    title: "所属上级",
    key: 'parentId',
    element: 'selectTree',
    options: [{
      value: 0,
      label: '顶级目录',
      children: []
    }]
  }, {
    title: "显示状态",
    key: 'visible',
    element: 'radio',
    options: [{
      label: '显示',
      value: '0'
    }, {
      label: '隐藏',
      value: '1'
    }]
  }, {
    title: "状态",
    key: 'status',
    element: 'radio',
    options: [{
      label: '正常',
      value: '0'
    }, {
      label: '停用',
      value: '1'
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
    key: 'orderNum',
    element: 'number',
    rules: [{
      required: true,
      message: '该项不能为空',
      trigger: 'blur'
    }]
  }],
  emitOpenDialog: (val) => {
    console.log(val)
  },
  inputDone: async (val) => {
    let params = {
      ...val
    }
    params.menuType = menuType.value
    if (params.menuId){
      let res = await dataSource.value.updateMenu(params)
      if (res.code === 200){
        proxy.$message.success('更新成功')
        dataSource.value.initData(this, proxy.$refs.table)
        editControlCommon.isShow = false
      }
    } else {
      let res = await dataSource.value.addMenu(params)
      if (res.code === 200){
        proxy.$message.success('新增成功')
        dataSource.value.initData(this, proxy.$refs.table)
        editControlCommon.isShow = false
      }
    }
  }
})

const menuTypeChange = (val) => {
  proxy.$refs.dialogForm?.successSubmit()
  if (val === 'C'){
    editControlCommon.formInputEl = [
      {
        title: "菜单名称",
        key: 'menuName',
        element: 'input',
        rules: [{
          required: true,
          message: '该项不能为空',
          trigger: 'blur'
        }]
      }, {
        title: "路由地址",
        key: 'path',
        element: 'input',
        rules: [{
          required: true,
          message: '该项不能为空',
          trigger: 'blur'
        }],
        illustrate: `无需拼接'/', 单一字符串即可`
      }, {
        title: "权限字符",
        key: 'perms',
        element: 'input',
        illustrate: `如果是顶级目录，权限标识格式: test; 如果不是，权限标识格式: test:test1:test2`
      }, {
        title: "组件地址",
        key: 'component',
        element: 'input',
        illustrate: `默认在根目录下, 假设根目录为sys, 填写的地址为: '/views/test', 则表示的组件为: sys/views/test.vue`
      }, {
        title: "菜单图标",
        key: 'icon',
        element: 'input',
        illustrate: `目录图标请用element里面支持的Icon图标, 大写字母开头, 非顶级菜单不显示图标`
      }, {
        title: "启用缓存",
        key: 'isCache',
        element: 'radio',
        options: [{
          label: '是',
          value: '0'
        }, {
          label: '否',
          value: '1'
        }],
        rules: [{
          required: true,
          message: '该项不能为空',
          trigger: 'blur'
        }]
      }, {
        title: "是否外链",
        key: 'isFrame',
        element: 'radio',
        options: [{
          label: '是',
          value: '0'
        }, {
          label: '否',
          value: '1'
        }],
        rules: [{
          required: true,
          message: '该项不能为空',
          trigger: 'blur'
        }]
      }
    ]
  }
  if (val === 'F'){
    editControlCommon.formInputEl = [{
      title: "文案标识",
      key: 'menuName',
      element: 'input',
      rules: [{
        required: true,
        message: '该项不能为空',
        trigger: 'blur'
      }]
    }, {
      title: "权限标识",
      key: 'perms',
      element: 'input',
      illustrate: `如果是顶级目录，权限标识格式: test; 如果不是，权限标识格式: test:test1:test2`
    }]
  }
  if (val === 'M'){
    editControlCommon.formInputEl = [{
      title: "目录名称",
      key: 'menuName',
      element: 'input',
      rules: [{
        required: true,
        message: '该项不能为空',
        trigger: 'blur'
      }]
    }, {
      title: "权限标识",
      key: 'perms',
      element: 'input',
      illustrate: `如果是顶级目录，权限标识格式: test; 如果不是，权限标识格式: test:test1:test2`
    }, {
      title: "目录图标",
      key: 'icon',
      element: 'input',
      illustrate: `目录图标请用element里面支持的Icon图标, 大写字母开头`
    }, {
      title: "路由地址",
      key: 'path',
      element: 'input',
      rules: [{
        required: true,
        message: '该项不能为空',
        trigger: 'blur'
      }]
    }]
  }
}

const getLevel = (rowData) => {
  let level = 0
  while (true){
    // eslint-disable-next-line no-loop-func
    let idx = dataSource.value.noHandleData.findIndex(item => item.menuId === rowData.menuId)
    if (idx !== -1 && dataSource.value.noHandleData[idx].parentId !== 0){
      level++
      rowData = dataSource.value.noHandleData.find(item => item.menuId === dataSource.value.noHandleData[idx].parentId)
      if (!rowData) return level
    } else {
      return level
    }

  }

}

const customizeCellRenderer = ({ forMatValue, rowIndex, column, rowData }) => {
  if (column.key === 'menuType') {
    return [h(ElTag, { type: rowData[column.key] === 'M' ? '' : rowData[column.key] === 'C' ? 'success' : 'danger' }, { default: () => forMatValue })]
  }
  if (column.key === 'perms'){
    if (rowData.parentId === 0) return forMatValue
    return [h('div', { style: `padding-left:${getLevel(rowData) * 10}px` }, forMatValue)]
  }
  return forMatValue
}

const close = () => {
  proxy.$refs.table.closeTree()
}
</script>

<style lang="less" scoped>
.table{
  padding: 10px;
  background-color: #ffffff;
}
:deep(.el-dialog__title){
  display: flex;
  align-items: center;
}
</style>