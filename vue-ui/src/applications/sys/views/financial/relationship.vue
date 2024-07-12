<template>
  <!-- 单据结构  -->
  <div>
    <div class="data-filter">
      <data-filter :filter-items="dataSource.tableHeader" @search="doSearch" />
    </div>
    <div class="extend-handle">
      <div class="extend-handle-left">
        <el-button
          v-authorityHandle="'summaryPlan:add'"
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
      need-check-box
      need-customize-cell-renderer
      row-key="roleId"
      :end-handle-width="300"
      :total="dataSource.total"
      :current-page="dataSource.currentPage"
      :page-size="dataSource.pageSize"
      :table-data="dataSource.tableData"
      :columns="dataSource.tableHeader"
      :loading="loading && !editControlCommon.isShow"
      :default-end-handle="false"
      :customize-end-handle="customizeEndHandle"
      :for-mat-data="dataSource.forMatData"
      :customize-cell-renderer="customizeCellRenderer"
      @selectionChange="dataSource.selectionChange($event, dataSource, proxy.$refs.table)"
      @refresh="dataSource.initData(dataSource, proxy.$refs.table)"
      @editRow="goCompile"
      @current-change="dataSource.currentPageChange($event, dataSource, proxy.$refs.table)"
      @sizeChange="dataSource.pageSizeChange($event, dataSource, proxy.$refs.table)"
      @editTableHeader="isShowEditTableHeader = true"
    />
    <el-drawer v-model="isShowEditTableHeader" :show-close="false">
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
            :is-show-edit-table-header="isShowEditTableHeader"
            :modules="'role'"
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
      @close-dialog="editControlCommon.isShow = false"
      @emitOpenDialog="editControlCommon.emitOpenDialog"
      @inputDone="editControlCommon.inputDone"
    />
  </div>
</template>

<script setup>
import selectDialogForm from '@/components/public/selectDialogForm.vue'
import dynamicHeader from '@/components/public/dynamicHeader.vue'
import VTable from '@/components/public/virtualTable.vue'
import dataFilter from '@/components/public/dataFilter.vue'
import { DataSource, loading } from './utils/relationship'
import {
  ref,
  reactive,
  getCurrentInstance,
  onBeforeMount,
  h,
  resolveDirective,
  withDirectives
} from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElButton, ElTag } from 'element-plus'
import client from '@/utils/upload/upLoadClient'
const router = useRouter()
const route = useRoute()
const authority = resolveDirective('authority')
const myClient = ref(client)
const { proxy } = getCurrentInstance()
const isShowEditTableHeader = ref(false)
const dataSource = ref(null)

const initDataSource = () => {
  dataSource.value = new DataSource({
    modules: 'relationship',
    selectUri: '/colorful-fog/dependMain/list'
  })
  dataSource.value.initTableHeader()
  dataSource.value.initData(this, proxy.$refs.table)
}

onBeforeMount(() => {
  initDataSource()
})

const addRow = async () => {
  formData.value = { isLoseEfficacy: false, dependType: 'SORT' }
  editControlCommon.isShow = true
}

const goCompile = async rowData => {
  formData.value = rowData
  editControlCommon.isShow = true
}

const doSearch = data => {
  dataSource.value.search(data, this, proxy.$refs.table)
}

const customizeEndHandle = rowData => {
  if (rowData.admin) {
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
        title: '编辑',
        style:
              'padding:0;margin:0;background:transparent;width:80px;color:#0c81b1;margin-right:10px'
      },
      { default: () => '编辑' }
    ),
    [[authority, 'summaryPlan:add']]
  ),
  withDirectives(
    h(
      ElButton,
      {
        class: 'hover-animation',
        onClick: () => {
          checkDetail(rowData)
        },
        title: '查看明细',
        style:
              'padding:0;margin:0;background:transparent;width:80px;color:#0c81b1;margin-right:10px'
      },
      { default: () => '修改明细' }
    ),
    [[authority, 'summaryPlan:add']]
  ),
  withDirectives(
    h(
      ElButton,
      {
        class: 'hover-animation',
        onClick: () => {
          deleteRow(rowData)
        },
        title: '删除',
        style:
              'padding:0;margin:0;background:transparent;width:80px;color:red;'
      },
      { default: () => '关闭依赖' }
    ),
    [[authority, 'summaryPlan:delete']]
  )])]
}

const checkDetail = rowData => {
  console.log(rowData)
  router.push(
    `/detail/relationshipDetail?id=${rowData.id}&dependCode=${rowData.dependCode}&dependType=${rowData.dependType}&title=${route.meta.title}&name=${rowData.name}`
  )
}

const formData = ref({})

const editControlCommon = reactive({
  isShow: false,
  formInputEl: [{
    title: '依赖关系名称',
    key: 'name',
    element: 'input',
    rules: [{
      required: true,
      message: '该项不能为空',
      trigger: 'blur'
    }]
  }],
  formSelectEl: [{
    title: '是否启用',
    key: 'isLoseEfficacy',
    element: 'radio',
    options: [{
      label: '是',
      value: false
    },
    {
      label: '否',
      value: true
    }],
    rules: [{
      required: true,
      message: '该项不能为空',
      trigger: 'blur'
    }]
  },
  {
    title: '类型',
    key: 'dependType',
    element: 'radio',
    options: [{
      label: '名词分类',
      value: 'SORT'
    },
    {
      label: '范围分类',
      value: 'RANGE'
    }],
    rules: [{
      required: true,
      message: '该项不能为空',
      trigger: 'blur'
    }]
  }],
  formTextAreaEl: [],
  formTimeAndNumber: [],
  emitOpenDialog: val => {
    console.log(val)
  },
  inputDone: val => {
    proxy.$alert('是否确认提交？', '提示', {
      type: 'info',
      icon: 'InfoFilled',
      confirmButtonText: '确定',
      callback: action => {
        if (action === 'confirm') {
          if (val.id) {
            dataSource.value.updateDependMain(val).then(res => {
              if (res.code === 200) {
                editControlCommon.isShow = false
                proxy.$message.success('更新成功')
                dataSource.value.initData(this, proxy.$refs.table)
              }
            })
          } else {
            dataSource.value.addDependMain(val).then(res => {
              if (res.code === 200) {
                editControlCommon.isShow = false
                proxy.$message.success('新增成功')
                dataSource.value.initData(this, proxy.$refs.table)
              }
            })
          }
        }
      }
    })
  }
})

const customizeCellRenderer = ({ forMatValue, rowIndex, column, rowData }) => {
  if (column.key === 'isLoseEfficacy') {
    let type = !rowData.isLoseEfficacy ? 'success' : 'danger'
    let text = !rowData.isLoseEfficacy ? '生效中' : '已失效'
    return h(ElTag, { type }, { default: () => text })
  }
  return forMatValue
}

const deleteRow = async rowData => {
  proxy.$alert('是否关闭使用当前依赖？', '提示', {
    type: 'error',
    icon: 'InfoFilled',
    confirmButtonText: '确定',
    callback: action => {
      if (action === 'confirm') {
        let params = [rowData.id]
        dataSource.value.deleteDependMain(params).then(res => {
          if (res.code === 200) {
            proxy.$alert('关闭成功', '提示', {
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
</script>

<style lang="less" scoped>
.table {
  padding: 10px;
  background-color: #ffffff;
}
</style>
