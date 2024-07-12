<template>
  <!-- 单据结构  -->
  <div>
    <div class="data-filter">
      <data-filter :filter-items="dataSource.tableHeader" @search="doSearch" />
    </div>
    <div class="extend-handle">
      <div class="extend-handle-left">
        <div class="ml10">
          当前账单：
          <el-input
            v-model="name"
            clearable
            style="width: 220px"
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
        <el-button
          class="ml10" type="primary"
          plain @click="complexExport"
        >
          合并导出
        </el-button>
        <el-button
          class="ml10" type="primary"
          plain @click="openEmpty"
        >
          更新空白字段
        </el-button>
      </div>
    </div>
    <div style="padding: 0 10px 10px 10px; background: #fff">
      <simpleTable
        ref="table"
        need-selection
        :options-width="280"
        need-end-control
        :total="dataSource.total"
        :current-page="dataSource.currentPage"
        :page-size="dataSource.pageSize"
        :table-data="dataSource.tableData"
        :table-header="dataSource.tableHeader"
        :loading="loading && !selectTableData.isShow && !showUpdateDialog"
        :for-mat-data="dataSource.forMatDataV2"
        @selectionChange="
          dataSource.selectionChange($event, dataSource, proxy.$refs.table)
        "
        @refresh="dataSource.initData(dataSource, proxy.$refs.table)"
        @current-change="
          dataSource.currentPageChange($event, dataSource, proxy.$refs.table)
        "
        @sizeChange="
          dataSource.pageSizeChange($event, dataSource, proxy.$refs.table)
        "
      >
        <template #endOption="{ row }">
          <customizeEndHandle :rowdata="row" />
        </template>
      </simpleTable>
    </div>
    <selectDialogTable
      v-if="selectTableData.isShow"
      ref="dialogSearch"
      :use-choose="false"
      :append-table-style="{
        background: '#126f9e',
        color: '#fff',
        borderColor: 'rgba(192, 192, 192,.5)'
      }"
      :width="'60%'"
      :search-input-placeholder="selectTableData.searchPlaceholde"
      :is-show="selectTableData.isShow"
      :data-source="selectTableData.dataSource"
      :title="'选择数据'"
      is-show-search-input
      :loading="loading"
      @close-dialog="selectTableData.isShow = false"
      @do-dialog-search="selectTableData.doSearch"
      @choose-row="selectTableData.chooseRow"
    />

    <el-dialog
      v-model="selectTime"
      width="400"
      title="请选择自动审核时间"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :show-close="false"
      center
      destroy-on-close
    >
      <template #header="{ titleId, titleClass }">
        <div class="jx-dialog-header">
          <div :id="titleId" :class="titleClass">
            <h4 class="inline-block">
              请选择自动审核时间
            </h4>
          </div>
          <div>
            <el-button
              class="dialog-close-btn"
              type="danger"
              icon="CloseBold"
              circle
              @click="selectTime = false"
            />
          </div>
        </div>
      </template>
      <div class="mb40 flex-center">
        <el-date-picker
          v-model="autoAuditTime"
          type="date"
          placeholder="请选择日期"
          value-format="x"
        />
      </div>
      <div class="mt10">
        <xButton
          type="primary"
          style="float: right; margin-right: 10px"
          @click="isSelect"
        >
          确认
        </xButton>
        <xButton
          type="danger"
          style="float: right; margin-right: 10px"
          @click="selectTime = false"
        >
          取消
        </xButton>
      </div>
    </el-dialog>
    <el-dialog
      v-model="updateEmpty"
      width="900"
      title="请填写相关信息"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :show-close="false"
      center
      destroy-on-close
    >
      <template #header="{ titleId, titleClass }">
        <div class="jx-dialog-header">
          <div :id="titleId" :class="titleClass">
            <h4 class="inline-block">
              请填写相关信息
            </h4>
          </div>
          <div>
            <el-button
              class="dialog-close-btn"
              type="danger"
              icon="CloseBold"
              circle
              @click="updateEmpty = false"
            />
          </div>
        </div>
      </template>
      <el-form
        ref="Form" :model="emptyForm"
        label-width="auto"
      >
        <el-form-item
          v-for="(item, index) in emptyForm"
          :key="index"
          :label="`${item?.collectResultName}：`"
          :prop="`collect${item?.id}`"
        >
          <el-input-number
            v-if="item?.resultType === 'NUMBER'"
            v-model="emptyForm[index].updateValue"
            :min="-1000000000"
            controls-position="right"
            placeholder="1"
            size="small"
          />
          <el-input
            v-if="item?.resultType === 'STRING'"
            v-model="emptyForm[index].updateValue"
            :placeholder="`请输入${item?.collectResultName}的相关信息`"
          />
          <el-date-picker
            v-if="item?.resultType === 'DATE'"
            v-model="emptyForm[index].updateValue"
            type="date"
            value-format="x"
            :placeholder="`请选择${item?.collectResultName}的日期`"
          />
          <el-upload
            v-if="item?.resultType === 'URL'"
            :ref="'upload' + item.id"
            v-model="emptyForm[index].updateValue"
            class="mr20"
            action="https://jxwlapp.oss-cn-guangzhou.aliyuncs.com"
            :file-list="imgList[index]"
            :data="myClient"
            :before-upload="beforeUpload"
            :before-remove="e => removeUpload(e, index)"
            :on-preview="e => showPreview(e, index)"
            :on-success="(v, e) => onSuccess(e, index)"
          >
            <el-button type="primary">
              点击上传文件
            </el-button>
          </el-upload>
          <el-image-viewer
            v-if="showImg"
            :url-list="[list]"
            @close="showImg = false"
          />
        </el-form-item>
      </el-form>
      <div class="mt10">
        <xButton
          type="primary"
          style="float: right; margin-right: 10px"
          @click="updateConfirm"
        >
          确认
        </xButton>
        <xButton
          type="danger"
          style="float: right; margin-right: 10px"
          @click="updateEmpty = false"
        >
          取消
        </xButton>
      </div>
    </el-dialog>
    <el-dialog
      v-model="showFile"
      width="600"
      title="请点击查看相关信息"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :show-close="false"
      center
      destroy-on-close
    >
      <template #header="{ titleId, titleClass }">
        <div class="jx-dialog-header">
          <div :id="titleId" :class="titleClass">
            <h4 class="inline-block">
              请点击查看相关信息
            </h4>
          </div>
          <div>
            <el-button
              class="dialog-close-btn"
              type="danger"
              icon="CloseBold"
              circle
              @click="showFile = false"
            />
          </div>
        </div>
      </template>
      <el-tag
        v-for="(item, index) in fileList"
        :key="index"
        class="file"
        @click="handleFileList(item, index)"
      >
        {{ item }}
      </el-tag>
      <el-image-viewer
        v-if="showView"
        :url-list="[file]"
        @close="showView = false"
      />
    </el-dialog>

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
              class="dialog-close-btn"
              type="danger"
              icon="CloseBold"
              circle
              @click="
                showUpdateDialog = false;
                checkResult = ''
              "
            />
          </div>
        </div>
      </template>

      <div>
        <el-select
          v-model="checkResult"
          placeholder="请选择要查看的字段"
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
          <span class="table-label" style="font-weight: 800">更新前</span>
          <span class="table-label" style="font-weight: 800">更新后</span>
          <span class="table-label" style="font-weight: 800">操作时间</span>
          <span class="table-label" style="font-weight: 800">操作人</span>
        </div>
        <div class="update-list">
          <div
            v-for="(item, index) in updateTable"
            :key="index"
            class="mt10 mb10 pb10"
            style="border-bottom: 1px dashed #ccc"
          >
            <span class="table-label" :title="item.originValue">{{
              item.originValue
            }}</span>
            <span class="table-label" :title="item.afterValue">{{
              item.afterValue
            }}</span>
            <span class="table-label">{{ item.updateTime }}</span>
            <span class="table-label">{{ item.updateUser }}</span>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { pbRequest } from "@/applications/sys/public/pbRequest/index"
import moreAction from "@/components/public/moreOptions.vue"
import selectDialogTable from "@/components/public/selectDialogTable.vue"
import simpleTable from "@/components/public/simpleTable.vue"
import dataFilter from "@/components/public/dataFilter.vue"
import { DataSource, loading } from "./utils/summarizeData"
import { DataSource as SummaryPlan } from "./utils/summaryPlan"
import {
  ref,
  getCurrentInstance,
  onBeforeMount,
  h,
  watch,
  reactive,
  withDirectives,
  resolveDirective
} from "vue"
import { ElButton, ElLink } from "element-plus"
const authority = resolveDirective("authority")
import { useRoute, useRouter } from "vue-router"
import client from "@/utils/upload/upLoadClient"
import { usePopup } from "@/js/tool-class/popup"
const route = useRoute()
const router = useRouter()
const checkResult = ref("")
const { proxy } = getCurrentInstance()
const { popup } = usePopup()
const isShowEditTableHeader = ref(false)
const dataSource = ref(null)
const collectSchemeCode = ref("")
const name = ref("")

watch(
  () => route.path,
  () => {
    if (
      route.path === "/financial/SummarizeData" &&
      dataSource.value &&
      route.query.collectSchemeCode
    ) {
      collectSchemeCode.value = route.query.collectSchemeCode
      name.value = route.query.name
      dataSource.value.searchData.collectSchemeCode = collectSchemeCode.value
      dataSource.value.tableData.length = 0
      initData()
    }
  }
)

const initDataSource = async () => {
  collectSchemeCode.value = route.query.collectSchemeCode
  name.value = route.query.name

  dataSource.value = new DataSource({
    tableHeader: [],
    selectUri: "/colorful-fog/CollectResultMain/list"
  })
  dataSource.value.forMatDataV2 = (row, column) => {
    let res = dataSource.value.forMatData(row, column)

    return res
  }
  if (!collectSchemeCode.value) {
    selectScheme()
    return
  }
  dataSource.value.searchData = {
    collectSchemeCode: collectSchemeCode.value
  }

}

const initData = () => {
  dataSource.value.initTableHeader(this).then(res => {
    if (res && res.length !== 0){
      let dataTool = new proxy.$DataTool()
      dataSource.value.forMatData = (row, column) => {
        if (column.property === 'auditStatus') {
          let auditStatus = {
            WAIT_START_AUDIT: '待发起审核',
            WAIT_AUDIT: '等待审核',
            PASS_AUDIT: '审核通过',
            REFUSE_AUDIT: '审核不通过'
          }
          return auditStatus[row[column.property]]
        }
        let item = res[column.getColumnIndex() - 1]
        if (row[column.property] === 'NULL' || !row.hasOwnProperty(column.property)) return '-'
        else if (item.dataType === "DATE") return dataTool.timeStamp2Time(+row[column.property], 'yyyy-MM-dd hh:mm:ss')
        else if (item.dataType === "NUM" && !Number.isNaN(Number(row[column.property])))
          return Number(row[column.property]).toFixed(item.decimal ?? 2)
        else if (typeof row[column.property] === "string" && row[column.property].startsWith("http")) {
          return h(
            ElLink,
            { type: "primary", onClick: () => handleClick(row, column) },
            { default: () => "点击查看" }
          )
        }
        else return row[column.property] ?? '-'
      }
      dataSource.value.initData(this, proxy.$refs.table)
    }
  })
}

onBeforeMount(async () => {
  initDataSource()
})

const showFile = ref(false)
const fileList = ref([])
const fileType = [
  "txt",
  "docx",
  "doc",
  "jpeg",
  "png",
  "jpg",
  "xls",
  "xlsx",
  "pdf"
]

const handleClick = (row, column) => {
  fileList.value = []
  if (
    typeof row[column.property] === "string" &&
    fileType.includes(row[column.property].split(".").pop().toLowerCase())
  ) {
    row[column.property].split(";").forEach(item => {
      fileList.value.push(item.split("/").pop())
    })
    showFile.value = true
  }
}

const imgList = ref([])

const openEmpty = () => {
  if (!dataSource.value.selections.length) {
    proxy.$message.error("请先选择修改数据")
    return
  }

  dataSource.value.getCollectFieldList(collectSchemeCode.value).then(res => {
    let collectData = res.data.pageInfo.list.filter(
      item => item.collectType === "EMPTY"
    )
    if (collectData.length === 0) {
      proxy.$message.error("暂无空白字段可以更改")
      return
    }
    imgList.value = []
    emptyForm.length = 0
    collectData.forEach((item, index) => {
      let updateValue = null
      switch (item.resultType) {
      case "NUMBER":
        updateValue =
            dataSource.value.selections[0][item.collectResultCode] ?? 0
        break
      case "URL":
        imgList.value[index] = []
        let urls = dataSource.value.selections[0]
          ? []
          : [item.collectResultCode].split(";")
        if (urls[0]) {
          imgList.value[index] = urls.map(url => {
            let name = url.split("/").pop()
            return { url, name }
          })
          updateValue = urls
        } else {
          updateValue = []
        }
        break
      case "DATE":
        updateValue =
            dataSource.value.selections[0][item.collectResultCode] ?? ""
        break
      default:
        updateValue =
            dataSource.value.selections[0][item.collectResultCode] ?? ""
        break
      }
      emptyForm.push({
        billType: "COLLECT",
        updateValue,
        fieldCode: item.collectResultCode,
        fieldName: item.collectResultName,
        billCode: "",
        collectResultName: item.collectResultName,
        resultType: item.resultType,
        id: item.id
      })
    })
    updateEmpty.value = true
  })
}

const showView = ref("")
const emptyForm = reactive([])

const updateEmpty = ref(false)
let myClient = ref(client)
const beforeUpload = val => {
  ;myClient.value.name = val.name,
  myClient.value.key = `collectBill/${val.name}`
  return true
}
const onSuccess = (val, index) => {

  emptyForm[index].updateValue.push(
    "https://jxwlapp.oss-cn-guangzhou.aliyuncs.com/" + `collectBill/${val.name}`
  )

}
const updateConfirm = () => {
  popup.handleAlert("提示", "是否更新空白字段", {}, () => {
    dataSource.value.selections.map((item, index) => {
      let params = []
      emptyForm.forEach((formItem, i) => {
        formItem.billCode = item.billCode
        if (Array.isArray(formItem.updateValue)) {
          formItem.updateValue = formItem.updateValue.join(";")
        }
        if (formItem.updateValue === null) {
          formItem.updateValue = ""
        }
        params.push(formItem)
      })
      dataSource.value.updateCollect(params).then(res => {
        if (res.code === 200) {
          proxy.$message.success("更新成功")
          imgList.value = []
          dataSource.value.tableData.length = 0
          dataSource.value.initData()
          updateEmpty.value = false
        }
      })
    })

  })
}
const list = ref([])
const file = ref([])
const handleFileList = (item, index) => {
  file.value = []
  if (
    [
      "txt", "docx", "doc", "xls", "xlsx", "pdf"
    ].includes(
      item.split(".").pop().toLowerCase()
    )
  ) {
    window.location = `https://jxwlapp.oss-cn-guangzhou.aliyuncs.com/collectBill/${item}?download`
    return
  }
  file.value = `https://jxwlapp.oss-cn-guangzhou.aliyuncs.com/collectBill/${item}`
  showView.value = true
}
function findImageIndex(images, targetSuffix) {
  for (let i = 0; i < images.length; i++) {
    const imageSrc = images[i]
    if (imageSrc.endsWith(targetSuffix)) {
      return i
    }
  }
  return -1
}

const showImg = ref(false)
const imgIndex = ref(0)
const showPreview = (e, ind) => {
  showImg.value = false
  list.value = []
  if (
    [
      "txt", "docx", "doc", "xls", "xlsx", "pdf"
    ].includes(
      e.name.split(".").pop().toLowerCase()
    )
  ) {
    window.location = `https://jxwlapp.oss-cn-guangzhou.aliyuncs.com/collectBill/${e.name}?download`
    return
  }
  imgIndex.value = findImageIndex(emptyForm[ind].updateValue, e.name)
  list.value = emptyForm[ind].updateValue[imgIndex.value]
  showImg.value = true
}
const removeUpload = (val, ind) => {
  let itemInd = emptyForm[ind].updateValue.indexOf(val.url)
  emptyForm[ind].updateValue.splice(itemInd, 1)
}
const doSearch = data => {
  if (Object.keys(data).length === 0) {
    if (!collectSchemeCode.value) {
      selectScheme()
      return
    }
    data.collectSchemeCode = collectSchemeCode.value
    dataSource.value.search(data, this, proxy.$refs.table)
    return
  }

  let tempData = {
    collectSchemeCode: collectSchemeCode.value,
    fieldSelectList: {}
  }

  Object.entries(data).forEach(([key, value]) => {
    if (
      [
        `belongArchiveName`,
        `billCode`,
        `billType`,
        `costTerm`,
        `auditStatus`
      ].includes(key)
    ) {
      tempData[key] = value
    } else {
      tempData.fieldSelectList[key] = value
    }
  })

  dataSource.value.search(tempData, this, proxy.$refs.table)
}

const customizeEndHandle = ({ rowdata }) => {
  return [h("div", { class: "table-handel-div flex-center" }, [withDirectives(
    h(
      ElButton,
      {
        class: "hover-animation",
        onClick: () => {
          exportData(rowdata)
        },
        title: "导出汇总",
        style:
              "padding:0;margin:0;background:transparent;width:80px;color:#0c81b1;margin-right:10px"
      },
      { default: () => "导出汇总" }
    ),
    [[authority, "SummarizeData:export"]]
  ),
  withDirectives(
    h(
      ElButton,
      {
        class: "hover-animation",
        onClick: () => {
          timePicker(rowdata)
        },
        title: "查看修改日志",
        style:
              "padding:0;margin:0 10px 0 0;background:transparent;width:80px;color:#0c81b1"
      },
      { default: () => "递交审核" }
    ),
    [[authority, "SummarizeData:update"]]
  ),
  h(moreAction, {
    title: "更多",
    needPerList: perList,
    onDoAction: val => {
      doAction(rowdata, val)
    }
  })])]
}

const refresh = rowData => {
  let params = {
    collectResultCode: rowData.billCode
  }
  dataSource.value.refresh(params).then(res => {
    if (res.code === 200) {
      proxy.$alert("刷新成功", "提示", {
        type: "success",
        icon: "InfoFilled",
        showCancelButton: false,
        callback: () => {
          initData()
        }
      })
    }
  })
}

const doAction = (rowData, val) => {
  switch (val) {
  case "refresh": {
    refresh(rowData)
    return
  }
  case "date": {
    checkUpdateData(rowData)
    return
  }

  default: {
    return
  }
  }
}

let perList = [{
  per: "SummarizeData:refresh",
  label: "刷新账单",
  command: `refresh`
},
{
  per: "SummarizeData:update",
  label: "修改日志",
  command: `date`
}]

const selectTime = ref(false)
const autoAuditTime = ref("")
const submitRow = ref(null)
const isSelect = () => {
  if (autoAuditTime.value !== "") {
    submitAduit(submitRow.value)
  } else {
    proxy.$message.error("请选择自动审核时间")
  }
}
const timePicker = rowdata => {
  autoAuditTime.value = ""
  submitRow.value = rowdata
  selectTime.value = true
}
const submitAduit = rowdata => {
  let params = {
    auditStatus: "WAIT_AUDIT",
    id: rowdata.id,
    autoAuditTime: autoAuditTime.value
  }
  dataSource.value.audit(params).then(res => {
    if (res.code === 200) {
      proxy.$message.success("递交成功")
      selectTime.value = false
      initData()
      return
    }
  })
}

const showUpdateDialog = ref(false)
const resultNameOptions = reactive([])
const checkCode = ref("")
const updateTable = reactive([])

const selectChange = val => {
  updateTable.length = 0
  if (updataObj[checkResult.value])
    updateTable.push(...updataObj[checkResult.value])
}

let updataObj = {}

const checkUpdateData = rowData => {
  resultNameOptions.length = 0
  checkCode.value = rowData.billCode
  dataSource.value
    .checkUpdateData({
      billCode: checkCode.value
    })
    .then(res => {
      if (res.data) {
        updataObj = res.data
        updateTable.length = 0
        dataSource.value.tableHeader.forEach((item, idx) => {
          if (item.title) {
            if (updataObj[item.key]) {
              let temp = {
                label: item.title,
                value: item.key
              }
              resultNameOptions.push(temp)
            }
          }
        })
        if (resultNameOptions.length !== 0) {
          checkResult.value = resultNameOptions[0].value
        }
        if (updataObj[checkResult.value])
          updateTable.push(...updataObj[checkResult.value])
        showUpdateDialog.value = true
      } else {
        proxy.$message.error("暂无修改记录")
      }
    })
}

const isTimestamp = timestamp => {
  let regex = /^\d{13}$/
  if (regex.test(timestamp)) {
    let date = new Date(+timestamp)
    return !isNaN(date.getTime())
  }
  return false
}

const exportData = ({ billCode, id }) => {
  let sheetList = []
  dataSource.value.getCollectExportData({ code: billCode, id }).then(res => {
    if (res.code === 200) {
      res.data.forEach(item => {
        let title = item.schemeName
        let header = [`所属经销商Code`, `所属经销商名称`, `账单编码`, `账期`]
        let headerCode = [`belongArchiveCode`,
          `belongArchiveName`,
          `billCode`,
          `costTerm`]
        let headerType = [`STRING`, `STRING`, `STRING`, `STRING`]
        let style = {}
        let sheetData = []
        item.billResultVO.resultNameList.forEach(data => {
          header.push(data.resultName)
          headerCode.push(data.resultCode)
          headerType.push({ type: item.dataType, decimal: item.decimal })
        })
        item.billResultVO.resultDataList.list.forEach(data => {
          data = { ...data, ...data.data }
          let temp = []
          headerCode.forEach((key, idx) => {
            if (headerType[idx].type === "DATE")
              temp.push(
                new proxy.$DataTool().timeStamp2Time(
                  +data[key],
                  "yyyy-MM-dd hh:mm:ss"
                )
              )
            else if (
              headerType[idx].type === "NUM" &&
              !Number.isNaN(Number(data[key]))
            ) {
              temp.push({
                v: Number(data[key]),
                t: "n",
                z: '0.' + new Array(headerType[idx].decimal ?? 2).fill(0).join('')
              })
            } else data[key] === "NULL" ? temp.push(`-`) : temp.push(data[key])
          })
          sheetData.push(temp)
        })
        sheetList.push({ title, header, style, data: sheetData })
      })
      new proxy.$DataTool().complexExcel(sheetList, "汇总数据")
    }
  })
}

const selectScheme = async () => {
  selectTableData.searchPlaceholde = "输入账单计划名称进行搜索"
  selectTableData.chooseRow = async val => {
    collectSchemeCode.value = val.collectSchemeCode
    name.value = val.collectSchemeName
    dataSource.value.searchData.collectSchemeCode = val.collectSchemeCode
    dataSource.value.tableData.length = 0
    dataSource.value.tableHeader.length = 0

    initData()
    selectTableData.isShow = false
  }
  selectTableData.doSearch = val => {
    selectTableData.dataSource.searchData = {}
    if (val) selectTableData.dataSource.searchData.collectSchemeName = val
    selectTableData.dataSource.initData()
  }
  selectTableData.dataSource = new SummaryPlan({
    selectUri: `/colorful-fog/collectSchemeMain/select`,
    tableHeader: [{
      label: "汇总账单名称",
      prop: "collectSchemeName",
      width: 200
    },
    {
      label: "汇总账单Code",
      prop: "collectSchemeCode",
      width: 200
    },
    {
      label: "备注",
      prop: "collectSchemeDesc",
      width: 200
    }],
    pageSize: 10
  })

  selectTableData.dataSource.forMatData = (row, column) => {
    if (column.property === "billType")
      return row[column.property] === "PAY" ? "应付" : "应收"
    return row[column.property] ?? "-"
  }

  await selectTableData.dataSource.initData()
  selectTableData.isShow = true
  return
}

const selectTableData = reactive({
  isShow: false,
  dataSource: null,
  searchPlaceholde: "",
  doSearch: val => {
    console.log(val)
  },
  chooseRow: val => {
    console.log(val)
  }
})

const complexExport = () => {
  let sheetList = []
  if (dataSource.value.selections.length > 0) {
    dataSource.value
      .complexExport(dataSource.value.selections.map(item => item.id))
      .then(res => {
        if (res.code === 200) {
          res.data.forEach(item => {
            let title = item.schemeName
            let header = [`所属经销商Code`,
              `所属经销商名称`,
              `账单编码`,
              `账期`]
            let headerCode = [`belongArchiveCode`,
              `belongArchiveName`,
              `billCode`,
              `costTerm`]
            let headerType = [`STRING`, `STRING`, `STRING`, `STRING`]
            let style = {}
            let sheetData = []
            item.billResultVO.resultNameList.forEach(data => {
              header.push(data.resultName)
              headerCode.push(data.resultCode)
              headerType.push({ type: data.resultType, decimal: data.decimal })
            })

            item.billResultVO.resultDataList.list.forEach(data => {
              data = { ...data, ...data.data }
              let temp = []

              headerCode.forEach((key, idx) => {
                if (headerType[idx].type === "DATE")
                  temp.push(
                    new proxy.$DataTool().timeStamp2Time(
                      +data[key],
                      "yyyy-MM-dd hh:mm:ss"
                    )
                  )
                else if (
                  headerType[idx].type === "NUM" &&
                  !Number.isNaN(Number(data[key]))
                ) {
                  temp.push({
                    v: Number(data[key]),
                    t: "n",
                    z: '0.' + new Array(headerType[idx].decimal ?? 2).fill(0).join('')
                  })
                } else
                  data[key] === "NULL" ? temp.push(`-`) : temp.push(data[key])
              })

              sheetData.push(temp)
            })
            sheetList.push({ title, header, style, data: sheetData })
          })
          new proxy.$DataTool().complexExcel(sheetList, "汇总数据")
        }
      })
  }
}
</script>

<style lang="less" scoped>
.table {
  padding: 10px;
  background-color: #ffffff;
}
.file {
  margin: 10px;
  cursor: pointer;
}

.table-label {
  width: 20%;
  height: 20px;
  margin-right: 20px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  display: inline-block;
  line-height: 20px;
  text-align: left;
}
.update-list {
  max-height: 600px;
  overflow-y: scroll;
}
</style>
