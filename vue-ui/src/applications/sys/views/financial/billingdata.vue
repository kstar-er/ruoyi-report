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
          v-authorityHandle="'billingdata:export'"
          class="handle-btn mr10"
          color="#4a78bd"
          style="color: #666"
          plain
          @click="exportData"
        >
          导出账单
        </el-button>
        <el-upload
          v-authorityHandle="'billingdata:update'"
          class="inline-block mr10"
          accept=".xlsx, .xls"
          :on-change="fileChoice"
          :show-file-list="false"
          :auto-upload="false"
        >
          <el-button
            class="handle-btn"
            color="#4a78bd"
            style="color: #666"
            plain
          >
            导入Excel更新
          </el-button>
        </el-upload>
        <el-upload
          v-authorityHandle="'billingdata:update'"
          class="inline-block mr10"
          accept=".xlsx, .xls"
          :on-change="selectFile"
          :show-file-list="false"
          :auto-upload="false"
        >
          <el-button
            v-authorityHandle="'billingdata:update'"
            type="danger"
            style="color: #666"
            plain
          >
            导入设置无效账单
          </el-button>
        </el-upload>

        <el-button
          v-authorityHandle="'billingdata:update'"
          type="danger"
          style="color: #666"
          plain
          @click="setInValid"
        >
          设为无效账单
        </el-button>
        <div class="ml10">
          当前账单：
          <el-input
            v-model="name"
            clearable
            style="width:220px"
            :placeholder="`请选择方案`"
            :disabled="true"
          >
            <template #append>
              <el-button @click="selectScheme">
                <el-icon><Edit /></el-icon>
              </el-button>
            </template>
          </el-input>
        </div>
      </div>
    </div>
    <div style="padding:0 10px 10px 10px;background:#fff">
      <simpleTable
        ref="table"
        need-selection
        :options-width="340"
        need-end-control
        :total="dataSource.total"
        :current-page="dataSource.currentPage"
        :page-size="dataSource.pageSize"
        :table-data="dataSource.tableData"
        :table-header="dataSource.tableHeader"
        :loading="loading&&!selectTableData.isShow&&!showUpdateDialog"
        :for-mat-data="dataSource.forMatData"
        @selectionChange="dataSource.selectionChange($event, dataSource, proxy.$refs.table)"
        @refresh="dataSource.initData(dataSource, proxy.$refs.table)"
        @current-change="dataSource.currentPageChange($event, dataSource, proxy.$refs.table)"
        @sizeChange="dataSource.pageSizeChange($event, dataSource, proxy.$refs.table)"
      >
        <template #endOption="{row}">
          <div>
            <customizeEndHandle :rowdata="row" />
          </div>
        </template>
      </simpleTable>
    </div>

    <selectDialogTable
      v-if="selectTableData.isShow"
      ref="dialogSearch"
      :use-choose="false"
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
    />
    <el-dialog
      v-model="showUpdateDialog"
      lock-scroll
      title="账单字段更新日志"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :show-close="false"
      top="4%"
      width="70%"
      destroy-on-close
    >
      <template #header="{ titleId, titleClass }">
        <div class="jx-dialog-header">
          <div :id="titleId" :class="titleClass">
            <h4 class="inline-block">
              账单字段更新日志
            </h4>
          </div>
          <div>
            <el-button
              class="dialog-close-btn" type="danger"
              icon="CloseBold"
              circle
              @click="showUpdateDialog = false;checkResult = '' "
            />
          </div>
        </div>
      </template>

      <div>
        <el-select
          v-model="checkResult" placeholder="请选择要查看的字段"
          style="width: 240px"
          filterable
          @change="selectChange"
        >
          <el-option
            v-for="item in resultNameOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
        <div class="mt10 mb10">
          <span class="table-label" style="font-weight:800">更新前</span>
          <span class="table-label" style="font-weight:800">更新后</span>
          <span class="table-label" style="font-weight:800">操作时间</span>
          <span class="table-label" style="font-weight:800">操作人</span>
        </div>
        <div class=" update-list">
          <div
            v-for="(item,index) in updateTable" :key="index"
            class="mt10 mb10 pb10"
            style="border-bottom: 1px dashed #ccc;"
          >
            <span class="table-label" :title="item.originValue">{{ item.originValue }}</span>
            <span class="table-label" :title="item.afterValue">{{ item.afterValue }}</span>
            <span class="table-label">{{ item.updateTime }}</span>
            <span class="table-label">{{ item.updateUser }}</span>
          </div>
        </div>
      </div>
    </el-dialog>
    <el-dialog
      v-model="showUpdate"
      lock-scroll
      title="账单字段更新日志"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :show-close="false"
      top="4%"
      width="90%"
      destroy-on-close
    >
      <template #header="{ titleId, titleClass }">
        <div class="jx-dialog-header">
          <div :id="titleId" :class="titleClass">
            <h4 class="inline-block">
              更新账单
              <span class="tips">
                修改时请勿删除原始数据，直接在旧数据后面拼接上修改的值，用:隔开
                【样例：海珠区:天河区】
              </span>
            </h4>
          </div>
        </div>
      </template>

      <div>
        <el-table :data="editRow" style="width: 100%">
          <el-table-column
            v-for="item in dataSource.tableHeader" :key="item.key"
            :label="item.title"
            :width="item.width"
          >
            <template #default="scope">
              <div style="display: flex; align-items: center">
                <el-input v-model=" scope.row[item.key]" size="small" />
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div class="mt10 footer">
        <el-button
          type="primary" style="float:right;margin-right:10px"
          @click="submit"
        >
          确定修改
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { pbRequest } from "@/applications/sys/public/pbRequest/index"
import selectDialogTable from '@/components/public/selectDialogTable.vue'
import simpleTable from '@/components/public/simpleTable.vue'
import dataFilter from '@/components/public/dataFilter.vue'
import { DataSource, loading } from './utils/billingData'
import { ref, getCurrentInstance, onBeforeMount, h, watch, reactive, withDirectives, resolveDirective } from 'vue'
import { ElButton } from 'element-plus'
const authority = resolveDirective('authority')
import { useRoute, useRouter } from 'vue-router'
const route = useRoute()
const router = useRouter()
const checkResult = ref('')
const { proxy } = getCurrentInstance()
const isShowEditTableHeader = ref(false)
const dataSource = ref(null)
const schemeCode = ref('')
const name = ref('')
const showUpdate = ref(false)
watch(() => route.path, () => {
  if (route.path === '/financial/Detail/billingdata' && dataSource.value && route.query.schemeCode){
    schemeCode.value = route.query.schemeCode
    name.value = route.query.name
    dataSource.value.searchData.schemeCode = schemeCode.value
    dataSource.value.tableData.length = 0
    initData()
  }
})

const initDataSource = async () => {
  schemeCode.value = route.query.schemeCode
  name.value = route.query.name

  dataSource.value = new DataSource({
    tableHeader: [],
    selectUri: '/colorful-fog/billResult/list'
  })

  if (!schemeCode.value){
    selectScheme()
    return
  }
  dataSource.value.searchData = {
    schemeCode: schemeCode.value
  }

  initData()
}

const initData = () => {
  dataSource.value.initTableHeader(this).then(res => {
    if (res && res.length !== 0){
      let dataTool = new proxy.$DataTool()
      dataSource.value.forMatData = (row, column) => {
        let item = res[column.getColumnIndex() - 1]
        if (row[column.property] === 'NULL' || !row.hasOwnProperty(column.property)) return '-'
        else if (item.dataType === "DATE") return dataTool.timeStamp2Time(+row[column.property], 'yyyy-MM-dd hh:mm:ss')
        else if (item.dataType === "NUM" && !Number.isNaN(Number(row[column.property])))
          return Number(row[column.property]).toFixed(item.decimal)
        else return row[column.property] ?? '-'
      }
      dataSource.value.initData(this, proxy.$refs.table)
    }
  })
}

onBeforeMount(() => {
  initDataSource()
})

const doSearch = (data) => {

  if (Object.keys(data).length === 0){
    if (!schemeCode.value){
      selectScheme()
      return
    }
    data.schemeCode = schemeCode.value
    dataSource.value.search(data, this, proxy.$refs.table)
    return
  }

  let tempData = { schemeCode: schemeCode.value, fieldSelectList: {} }

  Object.entries(data).forEach(([key, value]) => {
    if ([
      `belongArchiveName`, `billCode`, `billType`, `costTerm`, `status`, `batchCode`, `auditStatus`
    ].includes(key)){
      tempData[key] = value
    } else {
      tempData.fieldSelectList[key] = value
    }
  })

  dataSource.value.search(tempData, this, proxy.$refs.table)
}

const customizeEndHandle = ({ rowdata }) => {
  return [h('div', { class: 'table-handel-div' }, [withDirectives(h(
    ElButton,
    { class: 'hover-animation', onClick: () => { checkUpdateData(rowdata) }, title: '查看修改日志', style: 'padding:0;margin:0;background:transparent;width:80px;color:#0c81b1;margin-right:10px' },
    { default: () => "修改日志" }), [[authority, 'billingdata:checkUpdateData']]
  ), withDirectives(h(
    ElButton,
    { class: 'hover-animation', onClick: () => { updateRow(rowdata) }, title: '修改该条账单', style: 'padding:0;margin:0;background:transparent;width:80px;color:#0c81b1;margin-right:10px' },
    { default: () => "修改账单" }), [[authority, 'billingdata:checkUpdateData']]
  ), withDirectives(h(
    ElButton,
    { class: 'hover-animation', onClick: () => { updateBill(rowdata) }, title: '更新该批次账单', style: 'padding:0;margin:0;background:transparent;width:100px;color:#0c81b1;' },
    { default: () => "更新该批次账单" }), [[authority, 'billingdata:refresh']]
  )])]
}
let editRow = ref(null)

const updateRow = (rowData) => {
  editRow.value = [rowData]
  showUpdate.value = true
}

const submit = () => {
  let params = []
  Object.entries(editRow.value[0]).forEach(([code, value]) => {
    console.log(value)
    if (value && value.toString().split(':').length === 2){
      let item = dataSource.value.tableHeader.find(item => item.key === code)
      params.push({
        "billType": 'BILL',
        "billCode": editRow.value[0].billCode,
        "fieldName": item.title,
        "fieldCode": item.key,
        "updateValue": value.split(':')[1]
      })
    }
  })
  proxy.$alert('是否确认更新账单？', '提示', {
    type: 'info',
    icon: 'InfoFilled',
    confirmButtonText: '确定',
    callback: (action) => {
      if (action === 'confirm') {
        dataSource.value.updateBill(params).then(res => {
          if (res.code === 200){
            proxy.$alert(res.data, '提示', {
              type: 'success',
              icon: 'InfoFilled',
              showCancelButton: false,
              callback: async () => {
                dataSource.value.tableData.length = 0
                initData()
                showUpdate.value = false
              }
            })
          }
        })
      }
    }
  })
}

const updateBill = (rowData) => {
  proxy.$alert('是否确认更新当前批次的所有账单？', '提示', {
    type: 'info',
    icon: 'InfoFilled',
    confirmButtonText: '确定',
    callback: (action) => {
      if (action === 'confirm') {
        dataSource.value.updateBillFlash({
          schemeCode: schemeCode.value,
          batchCode: rowData.batchCode
        }).then(res => {
          if (res.code === 200){
            proxy.$message.success('刷新成功')
            dataSource.value.tableData.length = 0
            initData()
          }
        })
      }
    }
  })
}

const showUpdateDialog = ref(false)
const resultNameOptions = reactive([])
const checkCode = ref('')
const updateTable = reactive([])

const selectChange = (val) => {
  updateTable.length = 0
  if (updataObj[checkResult.value])
    updateTable.push(...updataObj[checkResult.value])
}

let updataObj = {}

const checkUpdateData = (rowData) => {
  resultNameOptions.length = 0
  checkCode.value = rowData.billCode
  dataSource.value.checkUpdateData({
    billCode: checkCode.value
  }).then(res => {
    if (res.data){
      updataObj = res.data
      updateTable.length = 0
      dataSource.value.tableHeader.forEach((item, idx) => {
        if (item.title){
          if (updataObj[item.key]){
            let temp = {
              label: item.title,
              value: item.key
            }
            resultNameOptions.push(temp)
          }
        }
      })
      if (resultNameOptions.length !== 0){
        checkResult.value = resultNameOptions[0].value
      }
      if (updataObj[checkResult.value])
        updateTable.push(...updataObj[checkResult.value])
      showUpdateDialog.value = true
    } else {
      proxy.$message.error('暂无修改记录')
    }
  })

}

const exportData = () => {
  let series = []
  let header = []
  let headerCode = []
  let headerType = []
  let style = {}
  let data = []

  dataSource.value.tableHeader.forEach(item => {
    if (item.key !== 'selection' && item.key !== 'handle'){
      header.push(item.title)
      headerCode.push(item.key)
      headerType.push({ type: item.dataType, decimal: item.decimal })
    }
  })

  dataSource.value.selections.forEach((item) => {
    let temp = []
    headerCode.forEach((key, idx) => {
      if (headerType[idx].type === 'DATE') temp.push(new proxy.$DataTool().timeStamp2Time(+item[key], 'yyyy-MM-dd hh:mm:ss'))
      else if (headerType[idx].type === 'NUM' && !Number.isNaN(Number(item[key]))) {
        temp.push({
          v: Number(item[key]),
          t: 'n',
          z: '0.' + new Array(headerType[idx].decimal ?? 2).fill(0).join('')
        })
      }
      else item[key] === 'NULL' ? temp.push(`-`) : temp.push(item[key])

    })
    data.push(temp)
  })

  series.push({
    header,
    data,
    title: '账单数据',
    style
  })

  series.push({
    header: [`更新说明`, `样例`],
    data: [[`如果想更新账单数据，在要跟新的数据单元格后面写上需要更新的值，用:隔开`, `天河区:海珠区`]],
    style: {},
    title: '账单更新说明'
  })

  new proxy.$DataTool().complexExcel(series, '账单数据')

}

const handleXlsxData = (file) => {
  let updateList = []
  let keys = Object.keys(file[0])
  file.forEach(item => {
    keys.forEach(key => {
      if (item[key] && item[key].toString().indexOf(':') !== -1 && key !== '更新说明'){
        let fieldCode = dataSource.value.tableHeader.find(item => item.title === key).key
        updateList.push({
          "billType": 'BILL',
          "billCode": item['账单编码'],
          "fieldName": key,
          "fieldCode": fieldCode,
          "updateValue": item[key].split(':')[1]
        })
      }
    })
  })
  return updateList
}

const fileChoice = async (file) => {
  let dataTool = new proxy.$DataTool()
  let xlsxData = await dataTool.xlsx2DataObject(file.raw)
  let data = handleXlsxData(xlsxData)
  proxy.$alert('是否确认导入更新账单？', '提示', {
    type: 'info',
    icon: 'InfoFilled',
    confirmButtonText: '确定',
    callback: (action) => {
      if (action === 'confirm') {
        dataSource.value.updateBill(data).then(res => {
          if (res.code === 200){
            proxy.$alert(res.data, '提示', {
              type: 'success',
              icon: 'InfoFilled',
              showCancelButton: false,
              callback: async () => {
                dataSource.value.tableData.length = 0
                initData()
              }
            })
          }
        })
      }
    }
  })
}

const selectScheme = async () => {
  selectTableData.searchPlaceholde = '输入账单计划名称进行搜索'
  selectTableData.chooseRow = async (val) => {
    if (val.children) return
    schemeCode.value = val.schemeCode
    name.value = val.name
    dataSource.value.searchData.schemeCode = val.schemeCode
    dataSource.value.tableData.length = 0
    dataSource.value.tableHeader.length = 0
    initData()
    selectTableData.isShow = false

  }
  selectTableData.doSearch = (val) => {
    selectTableData.dataSource.searchData = { }
    if (val) selectTableData.dataSource.searchData.name = val
    selectTableData.dataSource.initData()
  }
  selectTableData.dataSource = new DataSource({
    selectUri: `/colorful-fog/schemeMain/selectMenu`,
    tableHeader: [{
      label: "账单名称",
      prop: "name",
      width: 200
    }, {
      label: "账单code",
      prop: "schemeCode",
      width: 200
    }, {
      label: "类型",
      prop: "billType",
      width: 200
    }],
    pageSize: 10
  })

  selectTableData.dataSource.forMatData = (row, column) => {
    if (!row[column.property]) return '-'
    if (column.property === 'billType') return row[column.property] === 'PAY' ? '应付' : '应收'
    return row[column.property] ?? '-'
  }
  selectTableData.dataSource.initData = async (context = selectTableData.dataSource) => {
    let { data: { data, code, message } } = await pbRequest.post(`${context.selectUri}?currentPage=${context.currentPage}&pageSize=${context.pageSize}`, context.searchData)
    if (code !== 200) return
    selectTableData.dataSource.tableData.length = 0
    let dataList = Object.entries(data).reduce((a, b, idx) => {
      let temp = { name: b[0], children: b[1], id: idx + 1, schemeCode: '-' }
      a.push(temp)
      return a
    }, [])

    selectTableData.dataSource.tableData.push(...dataList)

    selectTableData.dataSource.total = selectTableData.dataSource.tableData.length
  }
  await selectTableData.dataSource.initData()
  selectTableData.isShow = true
  return
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

const setInValid = () => {
  if (dataSource.value.selections.length === 0){
    proxy.$message.error('请先选择数据')
    return
  }
  let params = dataSource.value.selections.map(item => item.billCode)
  proxy.$alert('是否确认设置无效', '提示', {
    type: 'info',
    icon: 'InfoFilled',
    confirmButtonText: '确定',
    callback: (action) => {
      if (action === 'confirm') {
        dataSource.value.setInValid(params).then(res => {
          if (res.code === 200){
            proxy.$message.success('设置成功')
            dataSource.value.tableData.length = 0
            initData()
          }
        })
      }
    }
  })
}

const selectFile = async (file) => {
  let dataTool = new proxy.$DataTool()
  let xlsxData = await dataTool.xlsx2DataObject(file.raw)
  let params = []
  let keys = Object.keys(xlsxData[0])
  xlsxData.forEach(item => {
    params.push(item['账单编码'])
  })
  proxy.$alert('是否确认设置无效', '提示', {
    type: 'info',
    icon: 'InfoFilled',
    confirmButtonText: '确定',
    callback: (action) => {
      if (action === 'confirm') {
        dataSource.value.setInValid(params).then(res => {
          if (res.code === 200){
            proxy.$message.success('设置成功')
            dataSource.value.tableData.length = 0
            initData()
          }
        })
      }
    }
  })
}
</script>

<style lang="less" scoped>
.table{
  padding: 10px;
  background-color: #ffffff;
}
.table-label{
  width: 25%;
  height: 20px;
  display: inline-block;
  line-height: 20px;
  text-align:left;
}
.footer{
  display: flex;
  justify-content: flex-end;
}
.table-label{
  width: 20%;
  height: 20px;
  margin-right: 20px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  display: inline-block;
  line-height: 20px;
  text-align:left;

}
.update-list{
  max-height: 600px;
  overflow-y: scroll;
}
</style>
