<template>
  <div>
    <div class="header">
      <el-page-header @back="goBack">
        <template #content>
          <span class="route"> {{ title }} </span>
          <span class="route" style="color: #999"> {{ active }} </span>
          <el-button
            type="primary"
            class="ml20"
            size="small"
            @click="addCollect"
          >
            新增汇总
          </el-button>
        </template>
      </el-page-header>
    </div>
    <div
      v-loading="
        loading &&
          !editControlCommon.isShow &&
          !editFiledCommon.isShow &&
          !selectTableData.isShow
      "
      class="detail"
    >
      <el-card class="box-card" shadow="hover">
        <template #header>
          <div class="card-header">
            基本信息
          </div>
        </template>
        <div style="display: flex; align-items: center" class="mb10">
          汇总计划名称：
          <el-tag type="info" class="ml10 mr20">
            {{ detail?.collectSchemeName }}
          </el-tag>
          汇总计划描述：
          <el-tag type="info" class="ml10 mr20">
            {{ detail?.collectSchemeDesc }}
          </el-tag>
          汇总日期：
          <el-tag type="info" class="ml10 mr20">
            {{ detail?.timeFormula?.join(" 至 ") }}
          </el-tag>
          汇总类型：
          <el-tag type="info" class="ml10 mr20">
            {{ detail?.collectObject === "BILL" ? "汇总原始账单" : "二次汇总" }}
          </el-tag>
          账单类型：
          <el-tag type="info">
            {{ detail?.billType === "COST" ? "应收" : "应付" }}
          </el-tag>
        </div>
        <div style="display: flex">
          <div style="width: 120px" class="mt10">
            <el-popover
              placement="top"
              width="300"
              effect="dark"
              :hide-after="0"
            >
              <template #reference>
                <el-icon style="color: rgb(255, 0, 0)">
                  <Warning />
                </el-icon>
              </template>
              <template #default>
                相同字段已合并
              </template>
            </el-popover>
            汇总的字段：
          </div>
          <div class="collect">
            <VueDraggableNext
              :list="collectData"
              chosen-class="chosen"
              group="collect"
              force-fallback="true"
              animation="500"
              :filter="'.unmover'"
              :fallback-tolerance="4"
              @start="drag = true"
              @end="end"
            >
              <el-tag
                v-for="item in collectData"
                :key="item.id"
                :type="typeMappings[item?.collectType].color"
                :closable="typeMappings[item?.collectType].state"
                :style="typeMappings[item?.collectType].style"
                class="mr10 mt10"
                @close="handleClose(item, collectData, index)"
                @click="
                  typeMappings[item?.collectType].action
                    ? showDeital(item, collectData, 'edit')
                    : null
                "
              >
                {{ item?.collectResultName }}
              </el-tag>
              <el-button
                v-if="drag"
                type="primary"
                style="margin-top: 6px"
                text
                class="unmover"
                @click="save"
              >
                保存当前顺序
              </el-button>
              <el-button
                v-if="drag"
                type="primary"
                style="margin-top: 6px"
                class="unmover"
                text
                @click="cancel"
              >
                取消修改
              </el-button>
            </VueDraggableNext>

            <el-dropdown
              trigger="click"
              class="ml10"
              style="margin-top: 6px"
              @command="openFormula"
            >
              <el-button type="primary" text>
                + 新增字段
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="EQUATION">
                    公式
                  </el-dropdown-item>
                  <el-dropdown-item command="EMPTY" divided>
                    空白
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
      </el-card>
    </div>

    <div
      v-loading="
        loading &&
          !editControlCommon.isShow &&
          !editFiledCommon.isShow &&
          !selectTableData.isShow
      "
      class="detail"
    >
      <el-card
        v-for="(item, idx) in dataList"
        :key="item.schemeCode"
        class="mb30"
        shadow="hover"
      >
        <template #header>
          <div class="card-header">
            <div class="inline-block mr20 flex-center">
              <el-popover
                placement="top"
                width="300"
                effect="dark"
                :hide-after="0"
              >
                <template #reference>
                  <el-icon style="color: rgb(255, 0, 0)">
                    <Warning />
                  </el-icon>
                </template>
                <template #default>
                  哪个方案生成的账单数据需要汇总
                </template>
              </el-popover>
              <span class="ml6"> 汇总的账单： </span>
              <el-tag type="success">
                {{ item.schemeName }}
              </el-tag>
            </div>
            <div class="inline-block mr20 flex-center">
              <el-popover
                placement="top"
                width="300"
                effect="dark"
                :hide-after="0"
              >
                <template #reference>
                  <el-icon style="color: rgb(255, 0, 0)">
                    <Warning />
                  </el-icon>
                </template>
                <template #default>
                  哪个时间段的账单需要汇总，这个时间段去哪个数据的值
                </template>
              </el-popover>
              <span class="ml6"> 时间限制字段： </span>
              <el-tag type="success">
                {{ item.timeFieldName }}
              </el-tag>
            </div>
          </div>
        </template>
        <div>
          <el-tag
            v-for="(tag, index) in item.schemeDetailList"
            :key="tag.id"
            class="mr20 mb10"
            closable
            size="large"
            :disable-transitions="false"
            style="cursor: pointer"
            @close="handleClose(tag, item, index)"
            @click="showDeital(tag, idx, index)"
          >
            {{ tag.schemeResultName }} 汇总成--> {{ tag.collectResultName }}
          </el-tag>
          <el-button
            type="primary"
            class="mb10"
            text
            @click="addField('schemeDetail', item, idx)"
          >
            + 新增字段
          </el-button>
        </div>
      </el-card>
    </div>
    <selectDialogForm
      v-if="editControlCommon.isShow"
      ref="dialogForm"
      :width="'42%'"
      :loading="loading"
      :form-data="formData"
      :is-show="editControlCommon.isShow"
      :title="'请填写相关信息'"
      :form-input-el="editControlCommon.formInputEl"
      :form-select-el="editControlCommon.formSelectEl"
      :form-text-area-el="editControlCommon.formTextAreaEl"
      :form-upload-el="editControlCommon.formUploadEl"
      :form-time-and-number="editControlCommon.formTimeAndNumber"
      @close-dialog="editControlCommon.isShow = false"
      @emit-open-dialog="editControlCommon.emitOpenDialog"
      @input-done="editControlCommon.inputDone"
    />
    <selectDialogForm
      v-if="editFiledCommon.isShow"
      ref="dialogFormFiled"
      :width="'42%'"
      :loading="loading"
      :form-data="filedData"
      :is-show="editFiledCommon.isShow"
      :title="'请填写相关信息'"
      :form-input-el="editFiledCommon.formInputEl"
      :form-select-el="editFiledCommon.formSelectEl"
      :form-text-area-el="editFiledCommon.formTextAreaEl"
      :form-upload-el="editFiledCommon.formUploadEl"
      :form-time-and-number="editFiledCommon.formTimeAndNumber"
      @close-dialog="editFiledCommon.isShow = false"
      @emit-open-dialog="editFiledCommon.emitOpenDialog"
      @input-done="editFiledCommon.inputDone"
    />
    <selectDialogForm
      v-if="addFormula.isShow"
      ref="dialogFormula"
      :width="'42%'"
      :loading="loading"
      :form-data="formulaData"
      :is-show="addFormula.isShow"
      :title="'请填写相关信息'"
      :form-input-el="addFormula.formInputEl"
      :form-select-el="addFormula.formSelectEl"
      :form-text-area-el="addFormula.formTextAreaEl"
      :form-upload-el="addFormula.formUploadEl"
      :form-time-and-number="addFormula.formTimeAndNumber"
      @close-dialog="addFormula.isShow = false"
      @emit-open-dialog="addFormula.emitOpenDialog"
      @input-done="addFormula.inputDone"
    >
      <template #append>
        <div v-if="formulaData.collectType === 'EQUATION'">
          <el-form-item
            key="expression"
            label="计算公式"
            prop="expression"
            class="mr10"
            style="width: 85%"
          >
            <el-input
              v-model="formulaData.expression"
              type="textarea"
              placeholder="请编写计算公式"
              :autosize="{ minRows: 3, maxRows: 6 }"
            />
          </el-form-item>
          <xButton type="info" @click="create">
            构造公式
          </xButton>
        </div>
      </template>
    </selectDialogForm>
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
    <createFun
      :is-show="showCreateFun"
      :list="collectData"
      :default-expresion="formulaData.expression"
      @input-done="createFunDone"
      @close-dialog="showCreateFun = false"
    />
  </div>
</template>

<script setup>
import { pbRequest } from "@/applications/sys/public/pbRequest/index"
import selectDialogTable from "@/components/public/selectDialogTable.vue"
import selectDialogForm from "@/components/public/selectDialogForm.vue"
import { VueDraggableNext } from "vue-draggable-next"
import createFun from "../components/createFun.vue"

import { usePopup } from "@/js/tool-class/popup"
import { ref, onBeforeMount, getCurrentInstance, reactive, watch } from "vue"
import { useRouter, useRoute } from "vue-router"
import { DataSource, loading } from "../utils/summaryPlan"

const { proxy } = getCurrentInstance()
const { popup } = usePopup()
const router = useRouter()
const route = useRoute()
const title = ref(`首页 / 汇总计划明细 / `)
const active = ref(`${route.query.title}`)

const goBack = () => {
  router.go(-1)
}
const dataSource = ref(null)

const drag = ref(false)
const editItem = []
const end = val => {
  let { newIndex, oldIndex } = val
  if (newIndex === oldIndex) {
    return
  }
  if (oldIndex < newIndex) {
    ;[newIndex, oldIndex] = [oldIndex, newIndex]
  }
  for (let i = newIndex; i <= oldIndex; i++) {
    collectData.value[i].displayOrder = i + 1
  }
  editItem.splice(
    0,
    editItem.length,
    ...collectData.value.slice(newIndex, oldIndex + 1)
  )

  editItem.forEach(item => {
    dataSource.value.tableData.forEach(dataItem => {
      if (dataItem.collectResultCode === item.collectResultCode) {
        dataItem.displayOrder = item.displayOrder
        editItem.push(dataItem)
      }
    })
  })
}

const save = () => {
  popup.handleAlert("提示", "是否保存当前的展示顺序", {}, () => {
    dataSource.value.updateCollectDetails(editItem).then(res => {
      if (res.code === 200) {
        proxy.$message.success("保存成功！")
        initData()
        drag.value = false
      }
    })
  })
}
const cancel = () => {
  popup.handleAlert("提示", "是否取消修改", {}, () => {
    initData()
    drag.value = false
  })
}

const initData = async () => {
  await dataSource.value.initData(this, proxy.$refs.table)
  let res = []
  let testKey = {}
  getFilterData(dataSource.value.tableData)
  dataSource.value.tableData.forEach((item, idx) => {
    if (!item.schemeCode) return
    if (!testKey.hasOwnProperty(item.schemeCode)) {
      testKey[item.schemeCode] = res.length
      res.push({
        schemeName: item.schemeName,
        schemeCode: item.schemeCode,
        timeFieldName: item.timeFieldName,
        timeField: item.timeField,
        schemeDetailList: [{
          schemeCode: item.schemeCode,
          collectResultCode: item.collectResultCode,
          collectResultName: item.collectResultName,
          schemeResultCode: item.schemeResultCode,
          schemeResultName: item.schemeResultName,
          collectType: item.collectType,
          id: item.id,
          hideWhenPush: item.hideWhenPush,
          groupByFieldName: item.groupByFieldName,
          groupByFieldValue: item.groupByFieldValue,
          groupByField: item.groupByField,
          decimal: item.decimal
        }]
      })
    } else {
      res[testKey[item.schemeCode]].schemeDetailList.push({
        schemeCode: item.schemeCode,
        collectResultCode: item.collectResultCode,
        collectResultName: item.collectResultName,
        schemeResultCode: item.schemeResultCode,
        schemeResultName: item.schemeResultName,
        collectType: item.collectType,
        id: item.id,
        hideWhenPush: item.hideWhenPush,
        groupByFieldName: item.groupByFieldName,
        groupByFieldValue: item.groupByFieldValue,
        groupByField: item.groupByField,
        decimal: item.decimal
      })
    }
  })
  dataList.length = 0
  dataList.push(...res)
}

const detail = ref(null)

let forMatTimeFormula = value1 => {
  let now = new proxy.$DataTool()
    .timeStamp2Time(Date.parse(new Date()))
    .split("-")
  let value = value1.toString().split(".")

  if (Number(now[1]) - Math.abs(Number(value[0])) <= 0) {
    now[0] = now[0] - Math.abs(Number(now[1]) - Math.abs(Number(value[0])))
  }
  return (
    now[0] +
    "-" +
    (Number(now[1]) - Math.abs(Number(value[0]))).toString().padStart(2, "0") +
    "-" +
    value[1].toString().padStart(2, "0")
  )
}

onBeforeMount(() => {
  dataSource.value = new DataSource({
    modules: "summaryPlan",
    selectUri: "/colorful-fog/CollectSchemeDetail/select",
    pageSize: 2000
  })

  dataSource.value.getMainDetail(route.query.id).then(res => {
    if (res.code === 200) {
      detail.value = res.data.pageInfo.list[0]
      detail.value.timeFormula = [forMatTimeFormula(detail.value.timeFormulaStart),
        forMatTimeFormula(detail.value.timeFormulaEnd)]
    }
  })

  dataSource.value.searchData = {
    collectSchemeCode: route.query.collectSchemeCode
  }
  initData()
})
const typeMappings = {
  SUM: { color: "warning", state: false, style: "", action: false },
  ONE: { color: "warning", state: false, style: "", action: false },
  CONDITION_SUM: { color: "warning", state: false, style: "", action: false },
  EQUATION: {
    color: "danger",
    state: true,
    style: "cursor:pointer",
    action: true
  },
  EMPTY: {
    color: "primary",
    state: true,
    style: "cursor:pointer",
    action: true
  }
}

const dataList = reactive([])

const addCollect = () => {
  formData.value = {}
  editControlCommon.isShow = true
}

const formData = ref({})

const editControlCommon = reactive({
  isShow: false,
  formInputEl: [{
    title: "汇总的账单",
    key: "schemeName",
    element: "input",
    type: "dialog",
    rules: [{
      required: true,
      message: "该项不能为空",
      trigger: "blur"
    }]
  },
  {
    title: "时间限制字段",
    key: "timeFieldName",
    element: "input",
    type: "dialog",
    rules: [{
      required: true,
      message: "该项不能为空",
      trigger: "blur"
    }]
  }],
  formSelectEl: [],
  formTextAreaEl: [],
  formTimeAndNumber: [],
  emitOpenDialog: val => {
    openSelectDialog(val)
  },
  inputDone: val => {
    // 找一下有没有相同的账单

    let idx = dataList.findIndex(item => item.schemeCode === val.schemeCode)
    if (idx !== -1) {
      proxy.$message.error("该账单方案已经添加过了")
      return
    }
    dataList.push({
      schemeCode: val.schemeCode,
      schemeName: val.schemeName,
      collectType: val.collectType,
      timeFieldName: val.timeFieldName,
      timeField: val.timeField,
      schemeDetailList: []
    })
    editControlCommon.isShow = false
  }
})

const filedData = ref({})

const addElByCollectType = val => {
  if (val === "CONDITION_SUM") {
    if (editFiledCommon.formInputEl[2].title !== "需限制条件的字段") {
      editFiledCommon.formInputEl.splice(2, 0, {
        title: "需限制条件的字段",
        key: "groupByFieldName",
        element: "input",
        type: "dialog",
        rules: [{
          required: true,
          message: "该项不能为空",
          trigger: "blur"
        }]
      })
      editFiledCommon.formInputEl.splice(3, 0, {
        title: "条件限制值",
        key: "groupByFieldValue",
        element: "input",
        rules: [{
          required: true,
          message: "该项不能为空",
          trigger: "blur"
        }]
      })
    }
  } else {
    if (editFiledCommon.formInputEl[2].title === "需限制条件的字段") {
      editFiledCommon.formInputEl.splice(2, 2)
    }
  }
}

const editFiledCommon = reactive({
  isShow: false,
  formInputEl: [
    {
      title: "原账单字段",
      key: "schemeResultName",
      element: "input",
      type: "dialog",
      rules: [{
        required: true,
        message: "该项不能为空",
        trigger: "blur"
      }]
    },
    {
      title: "汇总后字段名称",
      key: "collectResultName",
      element: "input",
      rules: [{
        required: true,
        message: "该项不能为空",
        trigger: "blur"
      }]
    },
    {
      title: "小数点位数",
      key: "decimal",
      element: "input"
    },
    {
      title: "字段类型",
      key: "collectType",
      element: "radio",
      options: [{
        label: "所有相关字段相加",
        value: "SUM"
      },
      {
        label: "条件汇总",
        value: "CONDITION_SUM"
      },
      {
        label: "只取一行中的数据",
        value: "ONE"
      }],
      rules: [{
        required: true,
        message: "该项不能为空",
        trigger: "blur"
      }],
      change: val => {
        addElByCollectType(val)
      }
    },
    {
      title: "推送时隐藏",
      key: "hideWhenPush",
      element: "radio",
      options: [{
        label: "是",
        value: true
      },
      {
        label: "否",
        value: false
      }],
      rules: [{
        required: true,
        message: "该项不能为空",
        trigger: "blur"
      }]
    }
  ],
  formSelectEl: [],
  formTextAreaEl: [],
  formTimeAndNumber: [],
  emitOpenDialog: val => {
    openSelectDialog(val, editFiledCommon.append)
  },
  inputDone: val => {
    let resultType =
      val.collectType === "SUM" || val.collectType === "CONDITION_SUM"
        ? "NUMBER"
        : val.parentType
    let params = {
      collectSchemeCode: route.query.collectSchemeCode,
      schemeCode: editFiledCommon.append.schemeCode,
      schemeName: editFiledCommon.append.schemeName,
      timeFieldName: editFiledCommon.append.timeFieldName,
      timeField: editFiledCommon.append.timeField,
      collectType: val.collectType,
      collectResultName: val.collectResultName,
      schemeResultCode: val.schemeResultCode,
      schemeResultName: val.schemeResultName,
      hideWhenPush: val.hideWhenPush,
      groupByFieldName: val.groupByFieldName,
      groupByFieldValue: val.groupByFieldValue,
      groupByField: val.groupByField,
      decimal: val.decimal,
      resultType
    }
    let isHaveAdd = getIsHaveAdd(params) // 查一下看看是不是已经有加过了
    if (val.id) {
      params.id = val.id
      params.collectResultCode = isHaveAdd
        ? isHaveAdd.collectResultCode
        : val.collectResultCode
      dataSource.value.updateCollectDetails([params]).then(res => {
        if (res.code === 200) {
          proxy.$message.success("更新成功")
          initData()
          return
        }
      })
    } else {
      if (isHaveAdd) params.collectResultCode = isHaveAdd.collectResultCode
      dataSource.value.addCollectDetails([params]).then(res => {
        if (res.code === 200) {
          proxy.$message.success("新增成功")
          initData()
          return
        }
      })
    }
    editFiledCommon.isShow = false
  }
})
const addFormula = reactive({
  isShow: false,
  formInputEl: [{
    title: "汇总后字段名称",
    key: "collectResultName",
    element: "input",
    rules: [{
      required: true,
      message: "该项不能为空",
      trigger: "blur"
    }]
  },
  {
    title: "字段类型",
    key: "collectType",
    element: "radio",
    options: [{
      label: "公式",
      value: "EQUATION"
    }],
    rules: [{
      required: true,
      message: "该项不能为空",
      trigger: "blur"
    }]
  },
  {
    title: "推送时隐藏",
    key: "hideWhenPush",
    element: "radio",
    options: [{
      label: "是",
      value: true
    },
    {
      label: "否",
      value: false
    }],
    rules: [{
      required: true,
      message: "该项不能为空",
      trigger: "blur"
    }]
  }],
  formSelectEl: [{
    title: "结果类型",
    key: "resultType",
    element: "select",
    options: [{
      label: "数字",
      value: "NUMBER"
    },
    {
      label: "字符串",
      value: "STRING"
    },
    {
      label: "日期",
      value: "DATE"
    },
    {
      label: "链接",
      value: "URL"
    }],
    rules: [{
      required: true,
      message: "该项不能为空",
      trigger: "blur"
    }]
  }],
  formTextAreaEl: [],
  formTimeAndNumber: [{
    title: "显示顺序",
    key: "displayOrder",
    element: "number"
  }],
  inputDone: val => {
    let params = {
      collectSchemeCode: route.query.collectSchemeCode,
      displayOrder: val.displayOrder,
      collectType: val.collectType,
      collectResultName: val.collectResultName,
      id: val.id,
      resultType: val.resultType,
      hideWhenPush: val.hideWhenPush
    }
    if (val.expression) {
      params.expression = val.expression
    }

    if (val.id) {
      dataSource.value.updateCollectDetails([params]).then(res => {
        if (res.code === 200) {
          proxy.$message.success("修改成功")
          initData()
          addFormula.isShow = false
          return
        }
      })
    } else {
      let isHaveAdd = getSameAdd(params) // 查一下看看是不是已经有加过了
      if (isHaveAdd) {
        proxy.$message.error("字段名称重复")
      } else {
        dataSource.value.addCollectDetails([params]).then(res => {
          if (res.code === 200) {
            proxy.$message.success("新增成功")
            initData()
            addFormula.isShow = false
            return
          }
        })
      }
    }
  }
})
const getSameAdd = params => {
  return collectData.value.find(
    item => item.collectResultName === params.collectResultName
  )
}
const getIsHaveAdd = params => {
  return dataSource.value.tableData.find(
    item => item.collectResultName === params.collectResultName
  )
}
const dialogFormula = ref(null)
const createFunDone = res => {
  proxy.$refs.dialogFormula.updateDialogInput({ expression: res })
  formulaData.value.expression = res
  showCreateFun.value = false
}
const showCreateFun = ref(false)
const create = () => {
  showCreateFun.value = true
}
const formulaData = ref({})
const getFormula = val => {
  proxy.$refs?.dialogFormula?.clearInput()
  switch (val) {
  case "EQUATION": {
    addFormula.formInputEl = [{
      title: "汇总后字段名称",
      key: "collectResultName",
      element: "input",
      rules: [{
        required: true,
        message: "该项不能为空",
        trigger: "blur"
      }]
    },
    {
      title: "字段类型",
      key: "collectType",
      element: "radio",
      options: [{
        label: "公式",
        value: "EQUATION"
      }],
      rules: [{
        required: true,
        message: "该项不能为空",
        trigger: "blur"
      }]
    },
    {
      title: "推送时隐藏",
      key: "hideWhenPush",
      element: "radio",
      options: [{
        label: "是",
        value: true
      },
      {
        label: "否",
        value: false
      }],
      rules: [{
        required: true,
        message: "该项不能为空",
        trigger: "blur"
      }]
    }]
    break
  }
  case "EMPTY": {
    addFormula.formInputEl = [{
      title: "汇总后字段名称",
      key: "collectResultName",
      element: "input",
      rules: [{
        required: true,
        message: "该项不能为空",
        trigger: "blur"
      }]
    },
    {
      title: "字段类型",
      key: "collectType",
      element: "radio",
      options: [{
        label: "空白",
        value: "EMPTY"
      }],
      rules: [{
        required: true,
        message: "该项不能为空",
        trigger: "blur"
      }]
    }]
    break
  }
  default:
    return
  }
}
const openFormula = val => {
  getFormula(val)
  formulaData.value = {
    hideWhenPush: false,
    collectType: val,
    displayOrder: collectData.value.length + 1,
    resultType: "NUMBER"
  }
  addFormula.isShow = true
}
const addField = (type, item, index) => {
  editFiledCommon.isShow = true
  editFiledCommon.append = item
  editFiledCommon.index = index
  addElByCollectType("SUM")
  filedData.value = { hideWhenPush: false, collectType: "SUM" }
}
const tempFilter = {}
const openSelectDialog = async (val, append) => {
  selectTableData.useChoose = true
  switch (val) {
  case "schemeName": {
    selectTableData.searchPlaceholde = "输入账单计划名称进行搜索"
    selectTableData.chooseRow = val => {
      if (val.children) return
      if (route.query.collectObject !== "COLLECT") {
        formData.value.schemeCode = val.schemeCode
        proxy.$refs.dialogForm.updateDialogInput({
          schemeName: val.name,
          schemeCode: val.schemeCode
        })
      } else {
        formData.value.collectSchemeCode = val.collectSchemeCode
        proxy.$refs.dialogForm.updateDialogInput({
          schemeName: val.collectSchemeName,
          schemeCode: val.collectSchemeCode
        })
      }
      proxy.$refs.dialogForm.updateDialogInput({
        timeFieldName: null,
        timeField: null
      })
      selectTableData.isShow = false
    }
    selectTableData.doSearch = val => {
      selectTableData.dataSource.searchData = {}
      if (val) selectTableData.dataSource.searchData.name = val
      selectTableData.dataSource.initData()
    }
    selectTableData.dataSource = new DataSource({
      selectUri:
          route.query.collectObject === "COLLECT"
            ? `/colorful-fog/collectSchemeMain/select`
            : `/colorful-fog/schemeMain/selectMenu`,
      tableHeader:
          route.query.collectObject === "COLLECT"
            ? [{
              label: "账单名称",
              prop: "collectSchemeName",
              width: 200
            },
            {
              label: "账单code",
              prop: "collectSchemeCode",
              width: 200
            },
            {
              label: "账单描述",
              prop: "collectSchemeDesc",
              width: 200
            }]
            : [{
              label: "账单名称",
              prop: "name",
              width: 200
            },
            {
              label: "账单code",
              prop: "schemeCode",
              width: 200
            },
            {
              label: "类型",
              prop: "billType",
              width: 200
            }],
      pageSize: 10
    })

    selectTableData.dataSource.forMatData = (row, column) => {
      if (!row[column.property]) return "-"
      if (column.property === "billType")
        return row[column.property] === "PAY" ? "应付" : "应收"
      return row[column.property] ?? "-"
    }
    if (route.query.collectObject !== "COLLECT")
      selectTableData.dataSource.initData = async (
        context = selectTableData.dataSource
      ) => {
        let {
          data: { data, code, message }
        } = await pbRequest.post(
          `${context.selectUri}?currentPage=${context.currentPage}&pageSize=${context.pageSize}`,
          context.searchData
        )
        if (code !== 200) return
        selectTableData.dataSource.tableData.length = 0
        let dataList = Object.entries(data).reduce((a, b, idx) => {
          let temp = {
            name: b[0],
            children: b[1],
            id: idx + 1,
            schemeCode: "-"
          }
          a.push(temp)
          return a
        }, [])

        selectTableData.dataSource.tableData.push(...dataList)

        selectTableData.dataSource.total =
            selectTableData.dataSource.tableData.length
      }
    await selectTableData.dataSource.initData()
    selectTableData.isShow = true
    return
  }
  case "groupByFieldName":
  case "timeFieldName":

  case "schemeResultName": {
    selectTableData.useChoose = false
    selectTableData.searchPlaceholde = "输入字段名称进行搜索"
    selectTableData.chooseRow = selectVal => {
      if (route.query.collectObject === "COLLECT") {
        if (val === "schemeResultName")
          proxy.$refs.dialogFormFiled.updateDialogInput({
            schemeResultName: selectVal.collectResultName,
            schemeResultCode: selectVal.collectResultCode,
            collectResultName: selectVal.collectResultName
          })
        if (val === "timeFieldName")
          proxy.$refs.dialogForm.updateDialogInput({
            timeFieldName: selectVal.collectResultName,
            timeField: selectVal.collectResultCode
          })
        if (val === "groupByFieldName")
          proxy.$refs.dialogFormFiled.updateDialogInput({
            groupByFieldName: selectVal.collectResultName,
            groupByField: selectVal.collectResultCode
          })
      } else {
        if (val === "schemeResultName")
          proxy.$refs.dialogFormFiled.updateDialogInput({
            schemeResultName: selectVal.resultName,
            schemeResultCode: selectVal.resultCode,
            collectResultName: selectVal.resultName,
            decimal: selectVal.decimal,
            parentType: selectVal.resultType
          })
        if (val === "timeFieldName")
          proxy.$refs.dialogForm.updateDialogInput({
            timeFieldName: selectVal.resultName,
            timeField: selectVal.resultCode
          })
        if (val === "groupByFieldName")
          proxy.$refs.dialogFormFiled.updateDialogInput({
            groupByFieldName: selectVal.resultName,
            groupByField: selectVal.resultCode
          })
      }

      selectTableData.isShow = false
    }
    selectTableData.doSearch = val => {
      if (route.query.collectObject === "COLLECT")
        selectTableData.dataSource.searchData = {
          collectSchemeCode: append
            ? append.schemeCode
            : formData.value.collectSchemeCode
        }
      else
        selectTableData.dataSource.searchData = {
          schemeCode: append ? append.schemeCode : formData.value.schemeCode
        }
      if (val) selectTableData.dataSource.searchData.resultName = val
      selectTableData.dataSource.initData()
    }
    selectTableData.dataSource = new DataSource({
      selectUri:
          route.query.collectObject === "COLLECT"
            ? `/colorful-fog/CollectSchemeDetail/select`
            : `/colorful-fog/schemeDetail/select`,
      tableHeader:
          route.query.collectObject === "COLLECT"
            ? [{
              label: "字段名称",
              prop: "collectResultName",
              width: 200
            },
            {
              label: "字段Code",
              prop: "collectResultCode",
              width: 200
            },
            {
              label: "字段类型",
              prop: "collectType",
              width: 200
            }]
            : [{
              label: "字段名称",
              prop: "resultName",
              width: 200
            },
            {
              label: "字段Code",
              prop: "resultCode",
              width: 200
            },
            {
              label: "字段类型",
              prop: "type",
              width: 200
            }],
      pageSize: 10
    })
    if (route.query.collectObject === "COLLECT")
      selectTableData.dataSource.searchData = {
        collectSchemeCode: append
          ? append.schemeCode
          : formData.value.collectSchemeCode
      }
    else
      selectTableData.dataSource.searchData = {
        schemeCode: append ? append.schemeCode : formData.value.schemeCode
      }
    selectTableData.dataSource.forMatData = (row, column) => {
      if (column.property === "type") return fieldType[row[column.property]]
      if (column.property === "collectType")
        return collectType[row[column.property]]
      return row[column.property] ?? "-"
    }
    await selectTableData.dataSource.initData()
    selectTableData.isShow = true
    return
  }

  default: {
    return
  }
  }
}

const fieldType = {
  FIXED: "固定系数",
  ORDER_DATA: "原单据字段值",
  DEPEND: "分类系数",
  EQUATION: "自定义公式"
}
const collectType = {
  SUM: "所有相关字段相加",
  ONE: "只取一行中的数据"
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

const handleClose = (tag, item, index) => {
  const { collectType } = tag
  const removeItem =
    !tag.id && (collectType === "EQUATION" ? item : item.schemeDetailList)
  if (removeItem) {
    removeItem.splice(index, 1)
  }
  if (item?.schemeDetailList?.length === 1) {
    proxy.$alert("已经是最后一个字段了，确认删除请点继续！", "提示", {
      type: "error",
      icon: "InfoFilled",
      confirmButtonText: "继续",
      callback: action => {
        if (action === "confirm") {
          dataSource.value.deleteCollectDetails([tag.id]).then(res => {
            if (res.code === 200) {
              proxy.$message.success("删除成功")
              initData()
            }
          })
        }
      }
    })
    return
  }
  proxy.$alert("是否确认删除当前字段？", "提示", {
    type: "error",
    icon: "InfoFilled",
    confirmButtonText: "确定",
    callback: action => {
      if (action === "confirm") {
        dataSource.value.deleteCollectDetails([tag.id]).then(res => {
          if (res.code === 200) {
            proxy.$message.success("删除成功")
            initData()
          }
        })
      }
    }
  })
}

const showDeital = (item, index, type) => {
  if (item.collectType === "EQUATION" || item.collectType === "EMPTY") {
    getFormula(item.collectType)
    formulaData.value = { ...item }
    addFormula.append = item
    addFormula.index = index
    addFormula.isShow = true
  } else {
    filedData.value = { ...item }
    if (item.collectType === "CONDITION_SUM") {
      if (editFiledCommon.formInputEl[2].title !== "需限制条件的字段") {
        editFiledCommon.formInputEl.splice(2, 0, {
          title: "需限制条件的字段",
          key: "groupByFieldName",
          element: "input",
          type: "dialog",
          rules: [{
            required: true,
            message: "该项不能为空",
            trigger: "blur"
          }]
        })
        editFiledCommon.formInputEl.splice(3, 0, {
          title: "条件限制值",
          key: "groupByFieldValue",
          element: "input",
          rules: [{
            required: true,
            message: "该项不能为空",
            trigger: "blur"
          }]
        })
      }
    } else {
      if (editFiledCommon.formInputEl[2].title === "需限制条件的字段") {
        editFiledCommon.formInputEl.splice(2, 2)
      }
    }

    editFiledCommon.append = item
    editFiledCommon.index = index
    editFiledCommon.isShow = true
  }
}
const collectData = ref([])
const getFilterData = list => {
  collectData.value = list.filter(
    (item, idx) =>
      list.findIndex(a => a.collectResultName === item.collectResultName) ===
      idx
  )
  collectData.value.sort((a, b) => a.displayOrder - b.displayOrder)
  return collectData.value
}
</script>

<style lang="less" scoped>
.chosen {
  background: #ecf5ff;
}
.collect {
  display: flex;
}
.detail {
  margin-top: 6px;
  font-size: 14px;
  padding: 10px 20px;
}
.header {
  padding: 20px;
  background: #fff;
}
.route {
  font-size: 14px;
  font-weight: 500;
  color: #252525;
  letter-spacing: 1px;
  cursor: default;
}
.table {
  overflow-x: hidden;
  margin-top: 10px;
}
:deep(.is-active) {
  background: #ecf5ff;
}
.demo-tabs {
  width: 150px;
}

:deep(.show-detail) {
  font-size: 12px;
  color: #22a6f2;
  cursor: pointer;
}

.card-header {
  display: flex;
  align-items: center;
}
</style>
