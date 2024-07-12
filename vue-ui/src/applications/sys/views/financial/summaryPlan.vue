<template>
  <!-- 单据结构  -->
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
          v-authorityHandle="'summaryPlan:add'"
          class="handle-btn"
          color="#4a78bd"
          style="color: #666"
          plain
          @click="addRow"
        >
          新增汇总计划
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
      :loading="loading&&!editControlCommon.isShow&&!isShowExportFilter"
      :default-end-handle="false"
      :customize-end-handle="customizeEndHandle"
      :for-mat-data="dataSource.forMatData"
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
            :is-show-edit-table-header="isShowEditTableHeader" :modules="'role'"
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
      :need-selection="selectTableData.useChoose"
      :use-choose="selectTableData.useChoose"
      :append-table-style="{ background: '#126f9e', color: '#fff', borderColor: 'rgba(192, 192, 192,.5)' }"
      :width="'60%'"
      :search-input-placeholder="selectTableData.searchPlaceholde"
      :is-show="selectTableData.isShow"
      :data-source="selectTableData.dataSource"
      :title="'选择数据'"
      is-show-search-input
      :loading="loading"
      @close-dialog="selectTableData.isShow=false"
      @do-dialog-search="selectTableData.doSearch"
      @choose-row="selectTableData.chooseRow"
      @submit="selectTableData.chooseRow"
    />

    <el-dialog
      v-model="isShowExportFilter"
      top="4%"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :show-close="false"
      lock-scroll
    >
      <template #header="{ titleId, titleClass }">
        <div class="jx-dialog-header">
          <h5 :id="titleId" :class="titleClass">
            导出实时账单数据
            <span class="tips">
              不填写筛选条件默认全导出！
            </span>
          </h5>
          <el-button
            class="dialog-close-btn"
            type="danger" icon="CloseBold"
            circle
            plain
            @click="isShowExportFilter=false"
          />
        </div>
      </template>
      <div class="mt10">
        <span class="filter-label">正在导出的账单：</span>
        <span> {{ row.collectSchemeName }}</span>
      </div>
      <div class="mt20">
        <span class="filter-label">时间：</span>
        <el-date-picker
          v-model="exportFilter.time"
          type="datetimerange"
          value-format="YYYY-MM-DD hh:mm:ss"
          range-separator="至"
          start-placeholder="开始时间"
          end-placeholder="结束时间"
          class="mr10"
        />
      </div>
      <div class="mt20">
        <span class="filter-label">账单所属人员：</span>
        <el-tag
          v-for="(tag,index) in exportFilter.users"
          :key="tag"
          class="mr10"
          closable
          :disable-transitions="false"
          @close="handleClose(tag,index)"
        >
          {{ tag }}
        </el-tag>
        <el-input
          v-if="inputVisible"
          ref="InputRef"
          v-model="newInputValue"
          class="new-input"
          size="small"
          placeholder="请输入Code"
          @keyup.enter="handleInputConfirm"
          @blur="handleInputConfirm"
        />
        <el-button
          v-else class="button-new-tag"
          size="small" @click="showInput"
        >
          + 新增
        </el-button>
      </div>
      <div class="mt20">
        <span class="filter-label">需要导出的账单：</span>
        <el-select
          v-model="exportFilter.scheme"
          multiple
          filterable
          placeholder="不选则默认全导出"
          style="width: 40vw"
        >
          <el-option
            v-for="item in schemeOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </div>

      <div class="footer ">
        <el-button
          type="primary" size="small"
          :loading="loading"
          @click="exportNewData"
        >
          确认导出
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import moreAction from '@/components/public/moreOptions.vue'
import selectDialogForm from '@/components/public/selectDialogForm.vue'
import selectDialogTable from '@/components/public/selectDialogTable.vue'
import dynamicHeader from '@/components/public/dynamicHeader.vue'
import VTable from '@/components/public/virtualTable.vue'
import dataFilter from '@/components/public/dataFilter.vue'
import { DataSource, loading } from './utils/summaryPlan'
import { ref, reactive, getCurrentInstance, onBeforeMount, h, resolveDirective, withDirectives } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElButton } from 'element-plus'
import client from '@/utils/upload/upLoadClient'
const router = useRouter()
const route = useRoute()
const isShowExportFilter = ref(false)
const authority = resolveDirective('authority')
const myClient = ref(client)
const { proxy } = getCurrentInstance()
const isShowEditTableHeader = ref(false)
const dataSource = ref(null)

const initDataSource = () => {
  dataSource.value = new DataSource({
    modules: 'summaryPlan',
    selectUri: '/colorful-fog/collectSchemeMain/select'
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

let forMatTimeFormula = (value1) => {
  let now = new proxy.$DataTool().timeStamp2Time(Date.parse(new Date())).split('-')
  let value = value1.toString().split('.')

  if (Number(now[1]) - Math.abs(Number(value[0])) <= 0){
    now[0] = now[0] - Math.abs(Number(now[1]) - Math.abs(Number(value[0])))
  }
  return now[0] + '-' + (Number(now[1]) - Math.abs(Number(value[0]))).toString().padStart(2, '0') + '-' + value[1].toString().padStart(2, '0')
}

const goCompile = async (rowData) => {
  formData.value = { ...rowData }
  formData.value.timeFormula = [forMatTimeFormula(rowData.timeFormulaStart), forMatTimeFormula(rowData.timeFormulaEnd)]

  // formData.value.costTermFormula = Math.abs(+rowData.costTermFormula)
  let now = new proxy.$DataTool().timeStamp2Time(Date.parse(new Date())).slice(0, 7)
  formData.value.costTermFormula = new proxy.$DataTool().timeStamp2Time(Date.parse(now) + formData.value.costTermFormula * 2592000000).slice(0, 7)
  editControlCommon.isShow = true
}

const doSearch = (data) => {
  dataSource.value.search(data, this, proxy.$refs.table)
}

const customizeEndHandle = (rowData) => {
  if (rowData.admin){
    return [h('div', { class: 'table-handel-div no-options' }, '暂无操作')]
  }
  return [h('div', { class: 'table-handel-div' }, [withDirectives(
    h(
      ElButton,
      {
        class: 'hover-animation',
        onClick: () => {
          router.push('/detail/collectDetail?id=' + rowData.id + '&title=' + rowData.collectSchemeName + '&collectSchemeCode=' + rowData.collectSchemeCode + '&collectObject=' + rowData.collectObject)
        },
        title: '修改计划',
        style:
              'padding:0;margin:0;background:transparent;width:80px;color:#0c81b1;margin-right:10px'
      },
      { default: () => '修改计划' }
    ),
    [[authority, 'summaryPlan:add']]
  ), withDirectives(
    h(
      ElButton,
      {
        class: 'hover-animation',
        onClick: () => {
          showDetail(rowData)
        },
        title: '查看汇总账单',
        style:
              'padding:0;margin:0;background:transparent;width:100px;color:#0c81b1;'
      },
      { default: () => '查看汇总账单' }
    ),
    [[authority, 'summaryPlan:check']]
  ),
  h(moreAction, { title: '更多', needPerList: perList, onDoAction: (val) => {

    doAction(rowData, val) }
  })])]
}

const exportNewData = (rowData) => {
  let params = {
    collectCode: row.collectSchemeCode
  }
  if (exportFilter.value.users.length !== 0) params.belongArchiveCode = exportFilter.value.users
  if (exportFilter.value.shceme.length !== 0) params.schemeCode = exportFilter.value.shceme
  if (exportFilter.value.time) {
    params.timeDto = {
      endTime: Date.parse(exportFilter.value.time[1]),
      startTime: Date.parse(exportFilter.value.time[0])
    }
  }
  let sheetList = []
  dataSource.value.createAndExport(params).then(res => {
    if (res.code === 200){
      res.data.forEach(item => {
        let title = item.schemeName
        let header = [`所属经销商Code`, `所属经销商名称`, `账单编码`, `账期`]
        let headerCode = [`belongArchiveCode`, `belongArchiveName`, `billCode`, `costTerm`]
        let headerType = [`STRING`, `STRING`, `STRING`, `STRING`]
        let style = {}
        let sheetData = []
        item.billResultVO.resultNameList.forEach(data => {
          header.push(data.resultName)
          headerCode.push(data.resultCode)
          headerType.push({ data: item.dataType, decimal: data.decimal })
        })
        item.billResultVO.resultDataList.list.forEach(data => {
          data = { ...data, ...data.data }
          let temp = []

          headerCode.forEach((key, idx) => {
            if (headerType[idx].type === 'DATE') temp.push(new proxy.$DataTool().timeStamp2Time(+data[key], 'yyyy-MM-dd hh:mm:ss'))
            else if (headerType[idx].type === 'NUM' && !Number.isNaN(Number(data[key]))) {
              temp.push({
                v: Number(data[key]),
                t: 'n',
                z: '0.' + new Array(headerType[idx].decimal ?? 2).fill(0).join('')
              })}
            else data[key] === 'NULL' ? temp.push(`-`) : temp.push(data[key])
          })

          sheetData.push(temp)
        })
        sheetList.push({ title, header, style, data: sheetData })
      })
      new proxy.$DataTool().complexExcel(sheetList, '汇总数据')
      isShowExportFilter.value = false
    }
  })

}

const doAction = (rowData, val) => {
  switch (val){
  case 'exportNewData': {
    let dataSource = new DataSource({
      selectUri: '/colorful-fog/CollectSchemeDetail/select',
      pageSize: 2000
    })
    dataSource.searchData = { collectSchemeCode: rowData.collectSchemeCode }
    dataSource.initData().then(res => {
      schemeOptions.length = 0
      dataSource.tableData.reduce((a, b) => {
        if (!a.includes(b.schemeCode)){
          schemeOptions.push({
            value: b.schemeCode,
            label: b.schemeName
          })
          a.push(b.schemeCode)
        }
        return a
      }, [])

      isShowExportFilter.value = true
      row = { ...rowData }
      exportFilter.value = { users: [], shceme: [] }
    })

    return
  }

  case 'add': {
    goCompile(rowData)
    return
  }
  case 'delete': {
    deleteRow(rowData)
    return
  }
  case 'create': {
    createCollect(rowData)
    return
  }
  case 'createShop': {
    proxy.$prompt('请输入商家Code', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    })
      .then(({ value }) => {
        let params = {
          collectSchemeCode: rowData.collectSchemeCode,
          belongArchiveCode: value
        }
        dataSource.value.createOneUser(params).then(res => {
          if (res.code === 200){
            proxy.$message.success('创建成功')
          }
        })

        // null
      })
      .catch(() => {
      // null
      })
    return
  }
  default: {
    return
  }
  }
}

let perList = [
  {
    per: 'summaryPlan:add',
    label: '编辑',
    command: `add`
  }, {
    per: 'summaryPlan:delete',
    label: '删除',
    command: `delete`
  }, {
    per: 'summaryPlan:create',
    label: '生成汇总账单',
    command: `create`
  }, {
    per: 'summaryPlan:create',
    label: '创建商家汇总账单',
    command: `createShop`
  }, {
    per: 'summaryPlan:create',
    label: '导出实时账单',
    command: `exportNewData`
  }
]

const createCollect = (rowData) => {
  proxy.$alert('是否确认生成汇总数据？', '提示', {
    type: 'error',
    icon: 'InfoFilled',
    confirmButtonText: '确定',
    callback: action => {
      if (action === 'confirm') {

        dataSource.value.createCollect({ code: rowData.collectSchemeCode }).then(res => {
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

let row = reactive({})

const showDetail = (rowData) => {
  router.push('/financial/SummarizeData?collectSchemeCode=' + rowData.collectSchemeCode + '&name=' + rowData.collectSchemeName + '&collectObject=' + rowData.collectObject)

}

const formData = ref({})

const editControlCommon = reactive({
  isShow: false,
  formInputEl: [{
    title: "汇总计划名称",
    key: 'collectSchemeName',
    element: 'input',
    rules: [{
      required: true,
      message: '该项不能为空',
      trigger: 'blur'
    }]
  }],
  formTimeAndNumber: [{
    title: "账期",
    key: 'costTermFormula',
    element: 'date',
    rules: [{
      required: true,
      message: '该项不能为空',
      trigger: 'blur'
    }],
    type: "month"
  }, {
    title: "账单生成时间",
    key: 'timeFormula',
    element: 'daterange'
  }],
  formSelectEl: [{
    title: "汇总类型",
    key: 'collectObject',
    element: 'radio',
    options: [{
      label: '二次汇总',
      value: 'COLLECT'
    }, {
      label: '汇总原始账单',
      value: 'BILL'
    }],
    rules: [{
      required: true,
      message: '该项不能为空',
      trigger: 'blur'
    }]
  }, {
    title: "账单类型",
    key: 'billType',
    element: 'radio',
    options: [{
      label: '应收',
      value: 'COST'
    }, {
      label: '应付',
      value: 'PAY'
    }],
    rules: [{
      required: true,
      message: '该项不能为空',
      trigger: 'blur'
    }]
  }],
  formTextAreaEl: [{
    title: "汇总计划备注",
    key: 'collectSchemeDesc',
    element: 'input',
    type: 'textarea'
  }],

  emitOpenDialog: (val) => {
    console.log(val)
  },
  inputDone: (val) => {
    let params = { ...val }

    let timeFormulaStart = val.timeFormula[0].split('-')
    let timeFormulaEnd = val.timeFormula[1].split('-')
    let now = new proxy.$DataTool().timeStamp2Time(Date.parse(new Date())).split('-')

    params.timeFormulaStart = `${Number(timeFormulaStart[1]) - Number(now[1])}.${Number(timeFormulaStart[2])}`
    params.timeFormulaEnd = `${Number(now[1]) - Number(timeFormulaEnd[1])}.${Number(timeFormulaEnd[2])}`
    let nowDate = new proxy.$DataTool().timeStamp2Time(Date.parse(new Date())).slice(0, 7)

    params.costTermFormula = (Date.parse(params.costTermFormula) - Date.parse(nowDate)) / 2592000000 | 0
    proxy.$alert('是否确认提交？', '提示', {
      type: 'info',
      icon: 'InfoFilled',
      confirmButtonText: '确定',
      callback: (action) => {
        if (action === 'confirm') {
          if (params.id){
            dataSource.value.updateCollent([params]).then((res) => {
              if (res.code === 200){
                editControlCommon.isShow = false
                proxy.$message.success('更新成功')
                dataSource.value.initData(this, proxy.$refs.table)
              }
            })
          } else {

            dataSource.value.addCollent(params).then((res) => {
              if (res.code === 200){
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
  return forMatValue
}

const deleteRow = async (rowData) => {
  if (!rowData.id && dataSource.value.selections.length === 0){
    proxy.$message.error('请选择数据')
    return
  }
  proxy.$alert('是否确认删除？', '提示', {
    type: 'error',
    icon: 'InfoFilled',
    confirmButtonText: '确定',
    callback: (action) => {
      if (action === 'confirm') {
        let params = rowData.id ? [rowData.id] : dataSource.value.selections.map(item => item.id)
        dataSource.value.deleteCollent(params).then(res => {
          if (res.code === 200){
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

const selectTableData = reactive({
  isShow: false,
  dataSource: null,
  searchPlaceholde: '',
  doSearch: (val) => {
    console.log(val)
  },
  chooseRow: (val) => {
    console.log(val)
  }
})

const exportFilter = ref({ users: [], scheme: [] })
const newInputValue = ref('')
const inputVisible = ref(false)

const showInput = () => {
  inputVisible.value = true
}

const handleInputConfirm = () => {
  exportFilter.value.users.push(newInputValue.value)
  newInputValue.value = ''
  inputVisible.value = false
}

const handleClose = (tag, index) => {
  exportFilter.value.users.splice(index, 1)
}

const schemeOptions = reactive([])
</script>

<style lang="less" scoped>
.table{
  padding: 10px;
  background-color: #ffffff;
}
.new-input{
  width: 200px;
  display: inline-block;
}
.filter-label{
  display: inline-block;
  width: 120px;
}
.footer{
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>
