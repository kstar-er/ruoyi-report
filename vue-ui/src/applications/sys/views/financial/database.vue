<template>
  <!-- 单据结构  -->
  <div>
    <div class="data-filter">
      <data-filter :filter-items="dataSource.tableHeader" @search="doSearch" />
    </div>
    <div class="extend-handle">
      <div class="extend-handle-left">
        <xButton
          v-authorityHandle="'database:database:edit'"
          class="mr10"
          @click="addRow"
        >
          新增
        </xButton>
        <xButton
          v-authorityHandle="'database:database:test'"
          class="mr10"
          @click="test(0)"
        >
          测试连接
        </xButton>
        <xButton
          v-authorityHandle="'database:database:delete'"
          type="danger"
          @click="deleteRows"
        >
          批量删除
        </xButton>
      </div>
    </div>
    <v-table
      ref="table"
      need-end-handle
      need-check-box
      need-customize-cell-renderer
      row-key="roleId"
      :end-handle-width="200"
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
    >
      <template #append>
        <div>
          <el-form-item
            key="sourceConfig"
            label="数据库配置JSON"
            prop="sourceConfig"
            class="mr10"
            style="width:90%"
          >
            <el-input
              v-model="formData.sourceConfig"
              type="textarea"
              placeholder="`请编写配置JSON"
              :autosize="{ minRows: 3, maxRows: 6 }"
            />
          </el-form-item>
          <xButton
            type="info"
            @click="test(formData.sourceConfig)"
          >
            测试链接
          </xButton>
        </div>
      </template>
    </selectDialogForm>
    <selectDialogForm
      v-if="testForm.isShow"
      ref="dialogForm"
      :confirm-text="'开始测试'"
      :width="'54%'"
      :loading="loading"
      :my-client="myClient"
      :form-data="testFormData"
      :is-show="testForm.isShow"
      :title="'测试数据库连接'"
      @close-dialog="testForm.isShow = false"
      @emitOpenDialog="testForm.emitOpenDialog"
      @inputDone="testForm.inputDone"
    >
      <template #append>
        <el-form-item
          v-for="item in testForm.formInputEl"
          :key="item.key"
          :label="item.title"
          :rules="item.rules"
          :prop="item.key"
          class="mr10"
        >
          <template #label>
            <el-popover
              v-if="item.illustrate" placement="top"
              width="300"
              :hide-after="0"
            >
              <template #reference>
                <el-icon style="color:orange">
                  <Warning />
                </el-icon>
              </template>
              <template #default>
                {{ item.illustrate }}
                <el-link
                  v-if="item.key==='mapLongitude'||item.key==='mapLatitude'"
                  href="https://lbs.qq.com/getPoint/" target="_blank"
                  class="ml10"
                  type="primary"
                >
                  点击跳转拾取经纬度
                </el-link>
              </template>
            </el-popover>
            {{ item.title }}
          </template>
          <el-input
            v-if="item.element === 'input'"
            v-model="testFormData[item.key]"
            clearable
            :type="item.type" style="width:220px"
            :placeholder="`请输入${item.title}`"
            :disabled="item.canInput ? !item.canInput : item.type === 'dialog'||item.disabled"
            :autosize="{ minRows: 1, maxRows: 4 }"
            :show-password="item.type==='password'?true:false"
            autocomplete="new-password"
          >
            <template v-if="item.type === 'dialog'" #append>
              <el-button :disabled="item.disabled" @click="emitOpenDialog(item.key)">
                <el-icon><Edit /></el-icon>
              </el-button>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item
          v-for="item in testForm.formSelectEl"
          :key="item.key"
          :label="item.title"
          :rules="item.rules"
          :prop="item.key"
          class="mr10"
        >
          <template #label>
            <el-popover
              v-if="item.illustrate" placement="top"
              width="300"
              :hide-after="0"
            >
              <template #reference>
                <el-icon style="color:orange">
                  <Warning />
                </el-icon>
              </template>
              <template #default>
                {{ item.illustrate }}
              </template>
            </el-popover>
            {{ item.title }}
          </template>

          <el-select
            v-if="item.element === 'select'"
            v-model="testFormData[item.key]"
            style="width:220px"
            :disabled="item.disabled"
            :placeholder="`请选择${item.title}`"
            :multiple="item.type==='multiple'"
            collapse-tags
            collapse-tags-tooltip
            @change="item.change"
          >
            <el-option
              v-for="option in item.options"
              :key="option.label"
              :label="option.label"
              :value="option.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item
          v-for="item in testForm.formTextAreaEl"
          :key="item.key"
          :label="item.title"
          :rules="item.rules"
          :prop="item.key"
          class="mr10"
          style="width:90%"
        >
          <el-input
            v-if="item.element === 'input'"
            v-model="testFormData[item.key]"
            :type="item.type"
            :placeholder="`请输入${item.title}`"
            :disabled="item.type === 'dialog'||item.disabled"
            :autosize="{ minRows: 3, maxRows: 6 }"
          />
          <span v-if="item.key==='json'" :style="connectSuccess?{color:'green'}:{color:'red'}">{{ connectText }}</span>
        </el-form-item>
      </template>
    </selectDialogForm>
  </div>
</template>

<script setup>
import selectDialogForm from '@/components/public/selectDialogForm.vue'
import dynamicHeader from '@/components/public/dynamicHeader.vue'
import VTable from '@/components/public/virtualTable.vue'
import dataFilter from '@/components/public/dataFilter.vue'
import { DataSource, loading } from './utils/database'
import {
  ref,
  reactive,
  getCurrentInstance,
  onBeforeMount,
  h,
  resolveDirective,
  withDirectives,
  watch
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
    tableHeader: [
      {
        title: "数据库名称",
        dataKey: "sourceName",
        key: 'sourceName',
        width: 300,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "数据库Code",
        dataKey: "sourceCode",
        key: 'sourceCode',
        width: 300,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "数据库类型",
        dataKey: "sourceType",
        key: 'sourceType',
        width: 300,
        type: 'select',
        isShow: true,
        isFixed: false,
        options: [{
          label: 'mysql',
          value: 'mysql'
        }]
      }, {
        title: "配置",
        dataKey: "sourceConfig",
        key: 'sourceConfig',
        width: 300,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "描述",
        dataKey: "sourceDesc",
        key: 'sourceDesc',
        width: 300,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "创建人",
        dataKey: "createUser",
        key: 'createUser',
        width: 300,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "创建时间",
        dataKey: "createTime",
        key: 'createTime',
        width: 300,
        type: 'date',
        isShow: true,
        isFixed: false
      }, {
        title: "更新人",
        dataKey: "updateUser",
        key: 'updateUser',
        width: 300,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "更新时间",
        dataKey: "updateTime",
        key: 'updateTime',
        width: 300,
        type: 'date',
        isShow: true,
        isFixed: false
      }
    ],
    selectUri: '/colorful-fog/dataSource/list'
  })
  dataSource.value.initTableHeader()
  dataSource.value.initData(this, proxy.$refs.table)
}

onBeforeMount(() => {
  initDataSource()
})

const addRow = async () => {
  formData.value = { }
  editControlCommon.isShow = true
}

const goCompile = async rowData => {
  formData.value = rowData
  formData.value.submitType = 'edit'
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
    [[authority, 'database:database:edit']]
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
      { default: () => '删除' }
    ),
    [[authority, 'database:database:delete']]
  )])]
}

const formData = ref({})

const editControlCommon = reactive({
  isShow: false,
  formInputEl: [{
    title: '数据库名称',
    key: 'sourceName',
    element: 'input',
    rules: [{
      required: true,
      message: '该项不能为空',
      trigger: 'blur'
    }]
  }, {
    title: '数据库Code',
    key: 'sourceCode',
    element: 'input',
    rules: [{
      required: true,
      message: '该项不能为空',
      trigger: 'blur'
    }]
  }],
  formSelectEl: [{
    title: '数据库类型',
    key: 'sourceType',
    element: 'select',
    options: [{
      label: 'mysql',
      value: 'mysql'
    }]
  }],
  formTextAreaEl: [{
    title: '描述',
    key: 'sourceDesc',
    element: 'input',
    type: 'textarea'
  }],
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
          if (formData.value.submitType === 'edit') {
            dataSource.value.update(val).then(res => {
              if (res.code === 200) {
                editControlCommon.isShow = false
                proxy.$message.success('更新成功')
                dataSource.value.initData(this, proxy.$refs.table)
              }
            })
          } else {
            dataSource.value.add(val).then(res => {
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

const testFormData = ref({})
const connectSuccess = ref(false)
const connectText = ref('')
const testForm = reactive({
  isShow: false,
  formInputEl: [
    {
      title: '主机',
      key: 'local',
      element: 'input'
    }, {
      title: '端口',
      key: 'port',
      element: 'input'
    }, {
      title: '用户',
      key: 'username',
      element: 'input'
    }, {
      title: '密码',
      key: 'password',
      element: 'input',
      type: 'password'
    }, {
      title: '数据库',
      key: 'dbName',
      element: 'input'
    }
  ],
  formSelectEl: [{
    title: '驱动程序',
    key: 'driverName',
    element: 'select',
    options: [{
      label: 'com.mysql.cj.jdbc.Driver',
      value: 'com.mysql.cj.jdbc.Driver'
    }]
  }],
  formTextAreaEl: [{
    title: '追加参数',
    key: 'appendJSON',
    element: 'input',
    type: 'textarea'
  }, {
    title: 'URL',
    key: 'json',
    element: 'input',
    type: 'textarea',
    rules: [{
      required: true,
      message: '该项不能为空',
      trigger: 'blur'
    }]
  }],
  inputDone: (val) => {
    let params = {
      sourceType: 'mysql',
      sourceConfig: val.json
    }
    dataSource.value.testConnect(params).then(res => {
      if (res.code === 200){
        connectSuccess.value = true
        connectText.value = '连接成功'
        proxy.$alert('连接成功', '提示', {
          type: 'success',
          icon: 'InfoFilled',
          confirmButtonText: '确定'
        })
      } else {
        connectSuccess.value = false
        connectText.value = '连接失败，错误信息：' + res
      }
    })
  }
})

const customizeCellRenderer = ({ forMatValue, rowIndex, column, rowData }) => {
  return forMatValue
}

const deleteRow = async rowData => {
  proxy.$alert('是否确认删除当前数据？', '提示', {
    type: 'error',
    icon: 'InfoFilled',
    confirmButtonText: '确定',
    callback: action => {
      if (action === 'confirm') {
        let params = [rowData.id]
        dataSource.value.deleteRow(params).then(res => {
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

const deleteRows = async () => {
  if (dataSource.value.selections.length === 0){
    proxy.$message.error('请选择数据')
    return
  }
  proxy.$alert('是否确认删除当前数据？', '提示', {
    type: 'error',
    icon: 'InfoFilled',
    confirmButtonText: '确定',
    callback: action => {
      if (action === 'confirm') {
        let params = dataSource.value.selections.map(item => item.id)
        dataSource.value.deleteRow(params).then(res => {
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

const test = (val) => {
  if (!val){
    testFormData.value = {}
    testForm.isShow = true
    return
  }

  try {
    testFormData.value = JSON.parse(val)
    testFormData.value.json = val
    testFormData.value.appendJSON = testFormData.value.jdbcUrl.split("?")[1]
    testFormData.value.local = testFormData.value.jdbcUrl.split("?")[0].split('//')[1].split('/')[0].split(':')[0]
    testFormData.value.port = testFormData.value.jdbcUrl.split("?")[0].split('//')[1].split('/')[0].split(':')[1]
    testFormData.value.dbName = testFormData.value.jdbcUrl.split("?")[0].split('//')[1].split('/')[1]
    testForm.isShow = true
  }
  catch (err) {
    console.log('解析失败')
    testFormData.value = {}
    testForm.isShow = true
  }

}

watch([
  () => testFormData.value.local,
  () => testFormData.value.port,
  () => testFormData.value.username,
  () => testFormData.value.password,
  () => testFormData.value.driverName,
  () => testFormData.value.dbName,
  () => testFormData.value.appendJSON
], () => {
  let temp = {
    driverName: testFormData.value.driverName ?? '',
    jdbcUrl: `jdbc:mysql://${testFormData.value.local ?? ''}:${testFormData.value.port ?? ''}/${testFormData.value.dbName ?? ''}`,
    username: testFormData.value.username ?? '',
    password: testFormData.value.password ?? ''
  }
  if (testFormData.value.appendJSON)
    temp.jdbcUrl += `?${testFormData.value.appendJSON ?? ''}`

  testFormData.value.json = JSON.stringify(temp)
}, { immediate: true })

watch(() => testFormData.value.json, () => {
  if (!testFormData.value.json) return
  let temp = JSON.parse(testFormData.value.json)
  testFormData.value.appendJSON = temp.jdbcUrl.split("?")[1]
  testFormData.value.local = temp.jdbcUrl.split("?")[0].split('//')[1].split('/')[0].split(':')[0]
  testFormData.value.port = temp.jdbcUrl.split("?")[0].split('//')[1].split('/')[0].split(':')[1]
  testFormData.value.dbName = temp.jdbcUrl.split("?")[0].split('//')[1].split('/')[1]
  testFormData.value.username = temp.username
  testFormData.value.password = temp.password
  testFormData.value.driverName = 'com.mysql.cj.jdbc.Driver'
})
</script>

<style lang="less" scoped>
.table {
  padding: 10px;
  background-color: #ffffff;
}
</style>
