<template>
  <div :class="hideMain ? 'move':''">
    <div class="data-filter">
      <data-filter
        :filter-items="dataSource.tableHeader"
        @search="doSearch"
      />
    </div>
    <div class="extend-handle">
      <div class="extend-handle-left">
        <el-button
          class="handle-btn mr10"
          color="#4a78bd"
          style="color: #666"
          plain
          @click="close"
        >
          全部折叠
        </el-button>
        <el-dropdown
          v-if="permissions.includes('settlementPlan:add')||permissions.includes('*:*:*')" trigger="click"
          @command="addRow"
          @visible-change="dropClick"
        >
          <el-button
            class="handle-btn mr10"
            color="#4a78bd"
            style="color: #666"
            plain
          >
            新增一个方案 <el-icon class="ml10">
              <CirclePlus />
            </el-icon>
          </el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <div v-for="(item,index) in (solutionList ?? [])" :key="index">
                <el-dropdown-item :command="item">
                  {{ item }}
                </el-dropdown-item>
              </div>
              <el-dropdown-item command="新增方案" divided>
                新增方案
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
        <el-button
          class="handle-btn mr10"
          color="#4a78bd"
          style="color: #666"
          plain
          @click="showProgrammeDetail"
        >
          方案绑定情况
        </el-button>
      </div>
    </div>
    <v-table
      ref="table"
      need-end-handle
      need-customize-cell-renderer
      need-expand
      row-key="name"
      :need-check-box="false"
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
      :end-handle-width="260"
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
            :is-show-edit-table-header="isShowEditTableHeader" :modules="'depart'"
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
  </div>

  <el-dialog
    v-model="showDialog"
    lock-scroll
    :close-on-click-modal="false"
    :close-on-press-escape="false"
    :show-close="false"
    width="30%"
    top="4%"
    destroy-on-close
  >
    <template #header="{ titleId, titleClass }">
      <div class="jx-dialog-header">
        <div :id="titleId" :class="titleClass">
          <h4 class="inline-block">
            修改账单分组
          </h4>
        </div>
        <div>
          <el-button
            class="dialog-close-btn" type="danger"
            icon="CloseBold"
            circle
            @click="showDialog = false"
          />
        </div>
      </div>
    </template>
    <div>
      <span class="mr10">选择或输入你想变更的分组</span>
      <el-select
        v-model="selectGroup" placeholder="选择或输入你想变更的分组"
        style="width: 240px"
        filterable
        allow-create
      >
        <el-option
          v-for="item in solutionList"
          :key="item"
          :label="item"
          :value="item"
        />
      </el-select>
    </div>
    <div class="mt10">
      <el-button
        type="primary" style="float:right;margin-right:10px"
        @click="update"
      >
        确定
      </el-button>
    </div>
  </el-dialog>

  <div v-if="hideMain">
    <programme
      :edit-id="editId"
      :add-type="addType"
      @back="back"
    />
  </div>
</template>

<script setup>
import moreAction from '@/components/public/moreOptions.vue'
import programme from './programme.vue'
import selectDialogForm from '@/components/public/selectDialogForm.vue'
import dynamicHeader from '@/components/public/dynamicHeader.vue'
import VTable from '@/components/public/virtualTable.vue'
import dataFilter from '@/components/public/dataFilter.vue'
import { DataSource, loading, updateSchemeMain } from './utils/programme.js'
import { ref, reactive, getCurrentInstance, onBeforeMount, h, resolveDirective, withDirectives } from 'vue'
import { ElButton, ElTag, ElMessageBox } from 'element-plus'
import client from '@/utils/upload/upLoadClient'
import { useRouter } from 'vue-router'
const router = useRouter()
const authority = resolveDirective('authority')
const myClient = ref(client)
const { proxy } = getCurrentInstance()
const isShowEditTableHeader = ref(false)
const dataSource = ref(null)
const permissions = JSON.parse(sessionStorage.getItem('userInfo')).permissions
const initDataSource = () => {
  dataSource.value = new DataSource({
    modules: 'programme',
    selectUri: '/colorful-fog/schemeMain/selectMenu'
  })
  dataSource.value.initTableHeader()
  dataSource.value.initData(this, proxy.$refs.table)
}
const showDialog = ref(false)
onBeforeMount(() => {
  initDataSource()
})
const hideMain = ref(false)

const editId = ref(0)
const goCompile = async ({ id, sort }) => {
  editId.value = id
  addType.value = sort
  hideMain.value = true

}
const addType = ref('')
const solutionList = ref([])
const selectGroup = ref('')
const dropClick = (val) => {
  if (!val) return
  dataSource.value.getSolutionList().then(res => {
    if (res.code === 200) {
      solutionList.value = res.data
    }
  })
}

const newSolution = () => {
  ElMessageBox.prompt('请输入方案名称', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  }).then(res => {
    addRow(res.value)
  })
    .catch(() => {
      //
    })

}
const addRow = (val) => {
  if (val === '新增方案') {
    newSolution()
  }
  else {
    editId.value = -1
    addType.value = val
    hideMain.value = true
  }
}

const doSearch = (data) => {
  dataSource.value.search(data, this, proxy.$refs.table)
}

let perList = [{
  per: 'settlementPlan:refresh',
  label: '刷新账单',
  command: `refresh`
}, {
  per: 'settlementPlan:delete',
  label: '删除计划',
  command: `delete`
}, {
  per: 'settlementPlan:createBill',
  label: '生成账单',
  command: `createBill`
}, {
  per: 'settlementPlan:add',
  label: '修改账单分组',
  command: `updateGroup`
}]

const customizeEndHandle = (rowData) => {
  if (!rowData.id){
    return [h('div', { class: 'table-handel-div no-options' }, '暂无操作')]
  }
  return [h('div', { class: 'table-handel-div' }, [withDirectives(
    h(
      ElButton,
      {
        class: 'hover-animation',
        onClick: () => {
          goCompile(rowData)
        },
        title: '编辑计划',
        style:
              'padding:0;margin:0;background:transparent;width:80px;color:#0c81b1;margin-right:10px'
      },
      { default: () => '编辑计划' }
    ),
    [[authority, 'settlementPlan:add']]
  ), withDirectives(
    h(
      ElButton,
      {
        class: 'hover-animation',
        onClick: () => {
          checkBillData(rowData)
        },
        title: '查看此计划账单',
        style:
              'padding:0;margin:0;background:transparent;width:80px;color:#16d46b;margin-right:10px'
      },
      { default: () => '查看账单' }
    ),
    [[authority, 'settlementPlan:check']]
  ),
  h(moreAction, { title: '更多', needPerList: perList, onDoAction: (val) => {
    doAction(rowData, val)
  } })])]
}
let row = {}
const doAction = (rowData, val) => {
  switch (val){
  case 'refresh': {
    refresh(rowData)
    return
  }
  case 'delete': {
    deleteRow(rowData)
    return
  }
  case 'createBill': {
    createBill(rowData)
    return
  }
  case 'updateGroup': {
    row = rowData
    selectGroup.value = row.sort
    dataSource.value.getSolutionList().then(res => {
      if (res.code === 200) {
        solutionList.value = res.data
        showDialog.value = true
      }
    })

  }
  default: {
    return
  }
  }
}

const createBill = (rowData) => {
  proxy.$alert('是否确认生成账单数据？', '提示', {
    type: 'info',
    icon: 'InfoFilled',
    confirmButtonText: '确定',
    callback: action => {
      if (action === 'confirm') {

        dataSource.value.createBill({ code: rowData.schemeCode }).then(res => {
          if (res.code === 200) {
            editControlCommon.isShow = false
            proxy.$message.success('生成成功')
            dataSource.value.initData(this, proxy.$refs.table)
          }
        })
      }
    }
  })
}

const refresh = (rowData) => {
  let params = {
    schemeCode: rowData.schemeCode
  }
  dataSource.value.refresh(params).then(res => {
    if (res.code === 200){
      proxy.$alert('刷新成功', '提示', {
        type: 'success',
        icon: 'InfoFilled',
        showCancelButton: false,
        callback: () => {
          dataSource.value.initData(this, proxy.$refs.table)
        }
      })
    }
  })

}

const checkBillData = (rowData) => {
  router.push('/financial/Detail/billingdata?schemeCode=' + rowData.schemeCode + '&name=' + rowData.name)
}

const formData = ref({})

const editControlCommon = reactive({
  isShow: false,
  formInputEl: [{
    title: "部门名称",
    key: 'deptName',
    element: 'input'
  }, {
    title: "部门负责人",
    key: 'leader',
    element: 'input',
    type: 'dialog',
    canInput: true
  }, {
    title: "负责人联系电话",
    key: 'phone',
    element: 'input'
  }, {
    title: "邮箱",
    key: 'email',
    element: 'input'
  }],
  formSelectEl: [{
    title: "所属上级部门",
    key: 'parentId',
    element: 'selectTree',
    options: [],
    rules: [{
      required: true,
      message: '该项不能为空',
      trigger: 'blur'
    }]
  }, {
    title: "部门状态",
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
  formTimeAndNumber: [{
    title: "显示顺序",
    key: 'orderNum',
    element: 'number'
  }],
  formTextAreaEl: [{
    title: "备注",
    key: 'remark',
    element: 'input',
    type: 'textarea'
  }],
  emitOpenDialog: (val) => {
    console.log(val)
  },
  inputDone: async (val) => {
    let params = {
      ...val
    }
    if (params.deptId){
      let res = await dataSource.value.updateDept(params)
      if (res.code === 200){
        proxy.$message.success('更新成功')
        dataSource.value.initData(this, proxy.$refs.table)
        editControlCommon.isShow = false
      }
    } else {
      let res = await dataSource.value.addDept(params)
      if (res.code === 200){
        proxy.$message.success('新增成功')
        dataSource.value.initData(this, proxy.$refs.table)
        editControlCommon.isShow = false
      }
    }
  }
})

const customizeCellRenderer = ({ forMatValue, rowIndex, column, rowData }) => {
  if (column.key === 'status') {
    return [h(ElTag, { type: rowData[column.key] === '0' ? '' : 'danger' }, { default: () => forMatValue })]
  }
  return forMatValue
}

const close = () => {
  proxy.$refs.table.closeTree()
}

const deleteRow = (rowData) => {
  proxy.$alert('是否确认删除当前数据？', '提示', {
    type: 'error',
    icon: 'InfoFilled',
    confirmButtonText: '确定',
    callback: action => {
      if (action === 'confirm') {
        let params = [rowData.id]
        dataSource.value.deleteSchemeMain(params).then(res => {
          if (res.code === 200) {
            proxy.$alert('删除成功', '提示', {
              type: 'success',
              icon: 'InfoFilled',
              showCancelButton: false,
              callback: () => {
                dataSource.value.initData(this, proxy.$refs.table)
              }
            })
          }
        })
      }
    }
  })
}

const back = () => {
  dataSource.value.initData(this, proxy.$refs.table)
  hideMain.value = false
}

const showProgrammeDetail = () => {
  router.push('/programmeDetail')
}

const update = () => {
  row.sort = selectGroup.value
  updateSchemeMain(row).then(res => {
    if (res.code === 200){
      dataSource.value.initData(this, proxy.$refs.table)
      showDialog.value = false
      proxy.$message.success('更新成功')
    }
  })
}
</script>

<style lang="less" scoped>
.table{
  padding: 10px;
  background-color: #ffffff;
}
.move{
  z-index: 100;
  position: absolute;
  left: 0;
  top: 0;
  animation: test .5s linear 0s 1 normal forwards;
}

@keyframes test {
  0% {
    left: 0;

  }
  100% {
    left: -100%;

  }
}
</style>