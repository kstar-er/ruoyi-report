<template>
  <div
    v-loading="
      loading &&
        !editControlCommon.isShow &&
        !selectTableData.isShow &&
        !showCreateCondition &&
        !showCreateFun
    "
    class="container"
    :class="{
      hide: hideMove
    }"
  >
    <div class="content">
      <div>
        <h3 class="mt10 mb20">
          {{ addType }}
        </h3>
        <el-page-header>
          <template #icon>
            <el-button
              type="info" size="small"
              @click="initData"
            >
              刷新数据
            </el-button>
            <el-button
              type="danger" size="small"
              @click="back"
            >
              返回
            </el-button>
          </template>
          <template #title>
            <div />
          </template>
          <template #content>
            <div class="flex-center">
              <span style="font-size: 16px">计划名称：</span>
              <el-input
                v-model="editData.schemeMain.name"
                class="ml10"
                style="width: 280px"
                placeholder="请输入计划名称"
              />
              <span
                class="text-sm mr10"
                style="color: var(--el-text-color-regular)"
              />
            </div>
          </template>
        </el-page-header>
      </div>
      <div class="content-body">
        <el-card class="box-card mb30">
          <template #header>
            <div class="card-header">
              <span>基本信息</span>

              <el-popover
                placement="top" width="300"
                :hide-after="0"
              >
                <template #reference>
                  <el-icon style="color: orange" class="ml6">
                    <Warning />
                  </el-icon>
                </template>
                <template #default>
                  方案的基本信息
                </template>
              </el-popover>
              <el-button
                type="primary"
                size="small"
                class="ml10"
                color="#4a78bd"
                @click="saveMain"
              >
                提交保存
              </el-button>
            </div>
          </template>
          <div style="display: flex; flex-direction: column">
            <div>
              <span class="label-base">
                <span style="color: red">*</span> 账单类型：
              </span>
              <el-radio-group
                v-model="editData.schemeMain.billType"
                class="ml-4"
              >
                <el-radio value="PAY" label="PAY">
                  应付
                </el-radio>
                <el-radio value="COST" label="COST">
                  应收
                </el-radio>
              </el-radio-group>
            </div>

            <div style="display: flex; flex-wrap: wrap" class="mt10">
              <div class="mr10 mt10">
                <span class="label-base">
                  <span style="color: red">*</span> 关联数据库：
                </span>
                <el-input
                  v-model="editData.schemeMain.dataSourceName"
                  style="width: 260px"
                  placeholder="请选择"
                  class="input-with-select"
                  disabled
                >
                  <template #append>
                    <el-button
                      icon="Edit"
                      :disabled="myEditId !== -1"
                      @click="openSelectDialog('dataSourceName')"
                    />
                  </template>
                </el-input>
              </div>
              <div class="mr10 mt10">
                <span> <span style="color: red">*</span> 账单粒度： </span>
                <el-input
                  v-model="editData.schemeMain.granularity"
                  style="width: 260px"
                  placeholder="请选择"
                  class="input-with-select"
                  disabled
                >
                  <template #append>
                    <el-button
                      icon="Edit"
                      :disabled="myEditId !== -1"
                      @click="openSelectDialog('granularity')"
                    />
                  </template>
                </el-input>
              </div>
              <div class="mr10 mt10">
                <span>
                  <span style="color: red">*</span> 账单归属人绑定字段：
                </span>
                <el-input
                  v-model="editData.schemeMain.belongRelTableField"
                  style="width: 260px"
                  placeholder="请选择"
                  class="input-with-select"
                  disabled
                >
                  <template #append>
                    <el-button
                      icon="Edit"
                      :disabled="myEditId !== -1"
                      @click="openSelectDialog('belongRelTableName')"
                    />
                  </template>
                </el-input>
              </div>
            </div>

            <div
              style="display: flex; flex-wrap: wrap; align-items: center"
              class="mt20"
            >
              <span class="label-base">
                <span style="color: red">*</span> 账单时间限制表：
              </span>
              <el-input
                v-model="editData.schemeMain.timeFieldTable"
                style="width: 260px"
                placeholder="请选择"
                class="input-with-select"
                disabled
              >
                <template #append>
                  <el-button
                    icon="Edit"
                    :disabled="myEditId !== -1"
                    @click="openSelectDialog('timeFieldTable')"
                  />
                </template>
              </el-input>

              <span class="label-base ml10">
                <span style="color: red">*</span> 账单时间限制字段：
              </span>
              <el-input
                v-model="editData.schemeMain.timeFieldName"
                style="width: 260px"
                placeholder="请选择"
                class="input-with-select"
                disabled
              >
                <template #append>
                  <el-button
                    icon="Edit"
                    :disabled="myEditId !== -1"
                    @click="openSelectDialog('timeFieldName')"
                  />
                </template>
              </el-input>
            </div>
            <div
              style="display: flex; flex-wrap: wrap; align-items: center"
              class="mt20"
            >
              <span class="label-base">
                <span style="color: red">*</span> 账单生成计划：
              </span>
              <el-select
                v-model="editData.schemeMain.executionUnit"
                class="m-2"
                placeholder="选择执行频率"
                style="width: 260px"
                @change="unitChange($event,1)"
              >
                <el-option
                  v-for="item in unitOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
              <span class="ml20" style="margin-right: 46px">
                {{
                  editData.schemeMain.executionUnit === "MONTHLY"
                    ? "每月"
                    : "每日"
                }}
              </span>

              <el-select
                v-model="editData.schemeMain.executionTime"
                class="m-2"
                placeholder="选择执行时间"
                style="width: 200px"
              >
                <el-option
                  v-for="item in timeOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
              <span class="ml6"> 生成一次 </span>
            </div>

            <div
              style="display: flex; align-items: center; width: 500px"
              class="mt20"
            >
              <span class="label-base mr20">
                <span style="color: red">*</span> 账单生成限制：
              </span>
              <el-date-picker
                v-model="editData.schemeMain.timeFormula"
                :type="
                  editData.schemeMain.executionUnit === 'MONTHLY'
                    ? 'daterange'
                    : 'datetimerange'
                "
                :value-format="
                  editData.schemeMain.executionUnit === 'MONTHLY'
                    ? 'YYYY-MM-DD'
                    : 'YYYY-MM-DD H:mm:ss'
                "
                range-separator="至"
                start-placeholder="开始时间"
                end-placeholder="结束时间"
                style="width: 400px"
              />
            </div>
          </div>
        </el-card>
        <el-card class="box-card mb30">
          <template #header>
            <div class="card-header">
              <span>关联用户</span>
              <el-popover
                placement="top" width="300"
                :hide-after="0"
              >
                <template #reference>
                  <el-icon style="color: orange" class="ml6">
                    <Warning />
                  </el-icon>
                </template>
                <template #default>
                  方案绑定的用户，谁使用该方案
                </template>
              </el-popover>
              <el-upload
                v-if="myEditId !== -1"
                class="inline-block ml10"
                accept=".xlsx, .xls"
                :on-change="fileChoice"
                :show-file-list="false"
                :auto-upload="false"
              >
                <el-button color="#4a78bd" size="small">
                  批量导入绑定
                </el-button>
              </el-upload>
            </div>
          </template>
          <div>
            <div style="display: flex">
              <div class="mr10 mt10">
                <el-popover
                  placement="top" width="300"
                  :hide-after="0"
                >
                  <template #reference>
                    <el-icon style="color: orange" class="mr6">
                      <Warning />
                    </el-icon>
                  </template>
                  <template #default>
                    这份账单的所属人记录在哪份表单中
                  </template>
                </el-popover>
                <span>
                  <span style="color: red">*</span> 绑定用户所属表：
                </span>
                <el-input
                  v-model="editData.schemeMain.userDataTable"
                  style="width: 260px"
                  placeholder="请选择"
                  class="input-with-select"
                  disabled
                >
                  <template #append>
                    <el-button
                      icon="Edit"
                      :disabled="myEditId !== -1"
                      @click="openSelectDialog('userDataTable')"
                    />
                  </template>
                </el-input>
              </div>
              <div class="mr10 mt10">
                <el-popover
                  placement="top" width="300"
                  :hide-after="0"
                >
                  <template #reference>
                    <el-icon style="color: orange" class="mr6">
                      <Warning />
                    </el-icon>
                  </template>
                  <template #default>
                    档案中用户的哪个字段和订单的用户字段关联
                  </template>
                </el-popover>
                <span> <span style="color: red">*</span> 绑定的字段： </span>
                <el-input
                  v-model="editData.schemeMain.userDataCodeField"
                  style="width: 260px"
                  placeholder="请选择"
                  class="input-with-select"
                  disabled
                >
                  <template #append>
                    <el-button
                      icon="Edit"
                      :disabled="myEditId !== -1"
                      @click="openSelectDialog('userDataCodeField')"
                    />
                  </template>
                </el-input>
              </div>
              <div class="mr10 mt10">
                <el-popover
                  placement="top" width="300"
                  :hide-after="0"
                >
                  <template #reference>
                    <el-icon style="color: orange" class="mr6">
                      <Warning />
                    </el-icon>
                  </template>
                  <template #default>
                    显示档案中用户名称的字段是哪个
                  </template>
                </el-popover>
                <span> <span style="color: red">*</span> 绑定的名称： </span>
                <el-input
                  v-model="editData.schemeMain.userDataNameField"
                  style="width: 260px"
                  placeholder="请选择"
                  class="input-with-select"
                  disabled
                >
                  <template #append>
                    <el-button
                      icon="Edit"
                      :disabled="myEditId !== -1"
                      @click="openSelectDialog('userDataNameField')"
                    />
                  </template>
                </el-input>
              </div>
            </div>
            <div v-if="myEditId !== -1" class="mt10">
              <div v-if="selectUser" class="flex-center mt20">
                <el-select
                  v-model="selectUserList"
                  multiple
                  placeholder="选择绑定用户"
                  filterable
                  style="width: 100%"
                  collapse-tags
                  collapse-tags-tooltip
                  :max-collapse-tags="14"
                >
                  <template #header>
                    <el-checkbox
                      v-model="checkAll"
                      :indeterminate="indeterminate"
                      @change="handleCheckAll"
                    >
                      选择所有用户
                    </el-checkbox>
                  </template>
                  <el-option
                    v-for="item in selectUserOptions"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  />
                </el-select>
                <el-button
                  class="ml10"
                  type="success"
                  size="small"
                  @click="saveBindUser"
                >
                  保存
                </el-button>
              </div>
            </div>
          </div>
        </el-card>
        <el-card v-if="myEditId !== -1" class="box-card mb30">
          <template #header>
            <div class="card-header">
              <span>账单过滤条件</span>
              <el-popover
                placement="top" width="300"
                :hide-after="0"
              >
                <template #reference>
                  <el-icon style="color: orange" class="ml6">
                    <Warning />
                  </el-icon>
                </template>
                <template #default>
                  哪些账单会使用该方案，不符合条件的账单，将会被过滤
                </template>
              </el-popover>
            </div>
          </template>
          <div>
            <el-tag
              v-for="tag in editData.conditionList"
              :key="tag"
              class="mr20 mt10"
              closable
              :disable-transitions="false"
              style="cursor: pointer"
              size="large"
              type="warning"
              @click="handleEditCondition(tag)"
              @close="handleCloseCondition(tag)"
            >
              {{ tag.conditionName }}
            </el-tag>
            <el-dropdown trigger="click" @command="openSelectDialog">
              <el-button
                type="warning" text
                class="mt10"
              >
                + 新增条件
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="[!]AND">
                    关联条件：并且
                  </el-dropdown-item>
                  <el-dropdown-item command="[!]OR">
                    关联条件：或者
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-card>
        <el-card v-if="myEditId !== -1" class="box-card mb30">
          <template #header>
            <div class="card-header">
              <span>账单数据定义</span>
              <el-popover
                placement="top" width="300"
                :hide-after="0"
              >
                <template #reference>
                  <el-icon style="color: orange" class="ml6">
                    <Warning />
                  </el-icon>
                </template>
                <template #default>
                  最后生成的账单中包含哪些数据
                </template>
              </el-popover>
              <div
                style="
                  display: inline-flex;
                  align-items: center;
                  font-size: 12px;
                "
                class="ml10"
              >
                <div class="common exp">
                  正常
                </div>
                <div class="hideWhenPush exp">
                  推送时隐藏
                </div>
                <div class="hideWhenExport exp">
                  导出时隐藏
                </div>
                <div class="complex exp">
                  推送和导出都隐藏
                </div>
              </div>
              <el-button
                type="primary"
                size="small"
                class="ml10"
                style="float: right"
                @click="openSelectDialog('schemeName')"
              >
                + 复制其他方案基础字段
              </el-button>
            </div>
          </template>
          <div class="collect">
            <VueDraggableNext
              :list="editData.schemeDetailList"
              chosen-class="chosen"
              group="collect"
              force-fallback="true"
              animation="500"
              :filter="'.unmover'"
              :fallback-tolerance="4"
              @start="drag = true"
              @end="end"
            >
              <div
                v-for="(tag, index) in editData.schemeDetailList"
                :key="tag.id"
                class="tag mr20 mt10"
                :class="getAppendClass(tag)"
                closable
                size="default"
                :disable-transitions="false"
                style="cursor: pointer"
                @click="showSchemeDeital(tag, index)"
              >
                {{ tag.resultName }}
                <el-icon
                  style="margin-left: 6px; font-size: 14px"
                  @click.stop="handleClose(tag, index)"
                >
                  <CircleClose />
                </el-icon>
              </div>
              <el-button
                v-if="drag"
                type="primary"
                class="unmover"
                text
                @click="save"
              >
                保存当前顺序
              </el-button>
              <el-button
                v-if="drag"
                type="primary"
                class="unmover"
                text
                @click="cancel"
              >
                取消修改
              </el-button>
              <el-button
                type="primary"
                class="unmover"
                text
                @click="openSelectDialog('schemeDetail')"
              >
                + 新增字段
              </el-button>
            </VueDraggableNext>
          </div>
        </el-card>
        <el-card v-if="myEditId !== -1" class="box-card mb30">
          <template #header>
            <div>
              <span>测试方案</span>
              <el-button class="ml10" @click="createTest">
                构建测试数据
              </el-button>
            </div>
          </template>
          <div>
            <test :test-data="testData" />
          </div>
        </el-card>
      </div>
    </div>
    <selectDialogTable
      v-if="selectTableData.isShow"
      ref="dialogSearch"
      :need-selection="selectTableData.useChoose"
      :use-choose="selectTableData.useChoose"
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
      @submit="selectTableData.submit"
    />
    <selectDialogForm
      v-if="editControlCommon.isShow"
      ref="dialogForm"
      :width="'60%'"
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
    >
      <template #customOperation>
        <div
          class="inline-block ml10"
          style="font-weight: 500; font-size: 14px"
        >
          <span style="color: red"> 请选择系数类型：</span>
          <el-cascader
            v-model="formData.type"
            style="width: 220px"
            :options="typeOptions"
            :placeholder="`请选择系数类型`"
            :disabled="formData.resultCode && formData.resultCode.length !== 0"
            @change="typeChange"
          />
          <span
            v-if="
              ['COUNT', 'FOR_MUL_TAG', 'MAX', 'MIN', 'SUM'].includes(
                formData.type
              )
            "
            style="color: red"
            class="ml10"
          >
            请选择字段来源：</span>
          <el-select
            v-if="
              ['COUNT', 'FOR_MUL_TAG', 'MAX', 'MIN', 'SUM'].includes(
                formData.type
              )
            "
            v-model="formData.getDataFrom"
            :disabled="
              ['SUM', 'COUNT', 'MAX', 'MIN', 'FOR_MUL_TAG'].includes(
                formData.type
              )
            "
            style="width: 220px"
            :placeholder="`请选择字段来源`"
            @change="getDataChange"
          >
            <el-option
              v-for="option in filedOptions"
              :key="option.label"
              :label="option.label"
              :value="option.value"
            />
          </el-select>

          <el-button
            v-if="formData.type === 'ORDER_DATA'"
            class="ml10"
            size="small"
            @click="createFiledBatch"
          >
            批量添加字段值
          </el-button>
        </div>
      </template>
      <template #append>
        <div v-if="formData.type === 'EQUATION'">
          <el-form-item
            key="expression"
            label="计算公式"
            prop="expression"
            class="mr10"
            style="width: 90%"
          >
            <el-input
              v-model="formData.expression"
              type="textarea"
              placeholder="请编写计算公式"
              :autosize="{ minRows: 3, maxRows: 6 }"
            />
          </el-form-item>
          <xButton type="info" @click="create">
            构造公式
          </xButton>
        </div>
        <div
          v-if="
            formData.type === 'SINGLE_TAG' || formData.type === 'FOR_MUL_TAG'
          "
        >
          <el-form-item
            key="singleType"
            label="匹配类型"
            prop="singleType"
            class="mr10"
            style="width: 90%"
          >
            <el-radio-group v-model="formData.singleType">
              <el-radio label="anyMatch" value="anyMatch">
                任意一个符合
              </el-radio>
              <el-radio label="allMatch" value="allMatch">
                都符合
              </el-radio>
              <el-radio label="noneMatch" value="noneMatch">
                全不符合
              </el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item
            key="singleExpression"
            label="匹配规则"
            prop="singleExpression"
            class="mr10"
            style="width: 100%"
          >
            <div
              class="flex-center"
              style="align-items: flex-start; flex-wrap: wrap"
            >
              <div style="max-width: 200px" class="mr10">
                【{{ formData.groupByFieldName }}】
              </div>

              <div style="max-width: 100px">
                {{
                  formData.singleType === "noneMatch"
                    ? "所有都不符合"
                    : formData.singleType === "anyMatch"
                      ? "在"
                      : "所有都符合"
                }}
              </div>

              <el-input
                v-model="formData.text1"
                class="ml6 mr6"
                style="width: 240px"
                :rows="2"
                type="textarea"
                placeholder="输入取值条件，用，隔开"
              />
              {{ formData.singleType === "anyMatch" ? "其中之一" : "" }}
              <div style="max-width: 100px">
                ，取
              </div>

              <el-input
                v-model="formData.text2"
                class="ml6 mr6 mb10"
                style="width: 240px"
                :rows="2"
                type="textarea"
                placeholder="输入要取的值"
              />
              <div style="max-width: 100px">
                否则，取
              </div>

              <el-input
                v-model="formData.text3"
                style="width: 240px"
                class="ml6"
                :rows="2"
                type="textarea"
                placeholder="输入要取的值"
              />
            </div>
          </el-form-item>
        </div>
      </template>
    </selectDialogForm>

    <createFun
      :is-show="showCreateFun"
      :list="editData.schemeDetailList"
      :default-expresion="formData.expression"
      @input-done="createFunDone"
      @close-dialog="showCreateFun = false"
    />
    <createCondition
      :is-show="showCreateCondition"
      :condition-item="conditionItem"
      @input-done="createFunDone"
      @close-dialog="closeShowCreateCondition"
    />
  </div>
</template>

<script setup>
import { pbRequest } from "@/applications/sys/public/pbRequest/index"
import createFun from "./components/createFun.vue"
import test from "./components/test.vue"
import createCondition from "./components/createCondition.vue"
import { VueDraggableNext } from "vue-draggable-next"
import selectDialogForm from "@/components/public/selectDialogForm.vue"
import selectDialogTable from "@/components/public/selectDialogTable.vue"
import { usePopup } from "@/js/tool-class/popup"
import { DataSource, loading } from "./utils/relationship.js"
import {
  updateSchemeDetailBatch,
  addCondition,
  getDetail,
  addSchemeDetailBatch,
  deleteSchemeDetail,
  updateSchemeMain,
  addSchemeMain,
  updateSchemeDetails,
  deleteCondition,
  addSchemeUserBatch,
  createTestData,
  copyField,
  deleteSchemeUser,
  schemeUserBatch
} from "./utils/programme.js"
import { ref, watch, reactive, getCurrentInstance, toRaw, nextTick } from "vue"

const { proxy } = getCurrentInstance()
const { popup } = usePopup()
const selectUser = ref(false)
const selectUserList = ref([])
const selectUserOptions = reactive([])

const checkAll = ref(false)
const indeterminate = ref(false)

const handleCheckAll = val => {
  indeterminate.value = false
  if (val) {
    selectUserList.value = selectUserOptions.map(_ => _.value)
  } else {
    selectUserList.value = []
  }
}

const props = defineProps({
  editId: {
    type: Number,
    default: 0
  },
  addType: {
    type: String,
    default: ""
  }
})

const unitOptions = [{
  label: "每日生成一次",
  value: "DAILY"
},
{
  label: "每月生成一次",
  value: "MONTHLY"
}]

const editData = ref({
  conditionList: [],
  schemeDetailList: [],
  schemeMain: { name: "" },
  schemeUserRelations: []
})
const myEditId = ref(-1)
const initData = () => {
  if (myEditId.value === -1) return
  getDetail(myEditId.value).then(res => {
    if (res.code === 200) {
      editData.value = res.data
      editData.value.schemeMain.timeFormula = [forMatTimeFormula(editData.value.schemeMain.timeFormulaStart, editData.value.schemeMain.executionUnit),
        forMatTimeFormula(editData.value.schemeMain.timeFormulaEnd, editData.value.schemeMain.executionUnit)]
      console.log(editData.value.schemeMain.timeFormula)
      unitChange(editData.value.schemeMain.executionUnit)
      selectUserList.value = editData.value.schemeUserRelations.map(
        item => item.archiveUserName + " " + item.archiveUserCode
      )
      sort(sortType)
      if (selectUserOptions.length === 0) openSelectDialog("archiveUserName")
    }
  })
}

const drag = ref(false)
const editItem = ref([])
const end = val => {
  const { newIndex, oldIndex } = val
  if (newIndex === oldIndex) {
    return
  }
  if (oldIndex < newIndex) {
    ;[newIndex, oldIndex] = [oldIndex, newIndex]
  }
  for (let i = newIndex; i <= oldIndex; i++) {
    editData.value.schemeDetailList[i].displayOrder = i + 1
  }
  editItem.value = [...editData.value.schemeDetailList]
  editItem.value = editItem.value.slice(newIndex, oldIndex + 1)
}

const save = () => {
  popup.handleAlert("提示", "是否保存当前的展示顺序", {}, () => {
    updateSchemeDetails(editItem.value).then(res => {
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

watch(
  () => props.editId,
  () => {
    myEditId.value = toRaw(props.editId)
    initData()
  },
  { immediate: true }
)

const _emits = defineEmits(["back"])
const hideMove = ref(false)

const formData = ref({})
const showCreateFun = ref(false)
const showCreateCondition = ref(false)

const handleEditCondition = (tag, index) => {
  conditionItem.value = tag
  showCreateCondition.value = true
}

const closeShowCreateCondition = () => {
  initData()
  showCreateCondition.value = false
}

const handleCloseCondition = tag => {
  proxy.$alert("是否确认删除当前生效条件？", "提示", {
    type: "error",
    icon: "InfoFilled",
    confirmButtonText: "确定",
    callback: action => {
      if (action === "confirm") {
        deleteCondition([tag.id]).then(res => {
          if (res.code === 200) {
            proxy.$message.success("删除成功")
            initData()
          }
        })
      }
    }
  })
}

const handleClose = (tag, index) => {
  proxy.$alert("是否确认删除当前字段？", "提示", {
    type: "error",
    icon: "InfoFilled",
    confirmButtonText: "确定",
    callback: action => {
      if (action === "confirm") {
        deleteSchemeDetail([tag.id]).then(res => {
          if (res.code === 200) {
            proxy.$message.success("删除成功")
            initData()
          }
        })
      }
    }
  })
}

const back = () => {
  hideMove.value = true
  setTimeout(() => {
    _emits("back")
    hideMove.value = false
  }, 800)
}

const typeOptions = [
  {
    label: "单据字段值",
    value: "ORDER_DATA"
  },
  {
    label: "固定系数",
    value: "FIXED"
  },
  {
    label: "分类系数",
    value: "DEPEND"
  },
  {
    label: "自定义公式",
    value: "EQUATION"
  },
  {
    label: "多行匹配",
    value: "FOR_MUL_TAG"
  },
  {
    label: "计算",
    value: "calc",
    children: [{
      label: "汇总",
      value: "SUM"
    },
    {
      label: "计数",
      value: "COUNT"
    },
    {
      label: "最大值",
      value: "MAX"
    },
    {
      label: "最小值",
      value: "MIN"
    }]
  },
  {
    label: "正则表达式",
    value: "REGEX"
  }
]

const filedOptions = [{
  label: "当前方案字段",
  value: "SCHEME"
},
{
  label: "源数据表",
  value: "TABLE"
}]

const typeChange = val => {
  val = val[val.length - 1]
  proxy.$refs?.dialogForm?.clearInput()
  formData.value = {
    hideWhenPush: false,
    hideWhenExport: false,
    tgetDataFrom: "SCHEME",
    resultType: "STRING",
    type: val,
    displayOrder: editData.value.schemeDetailList.length + 1,
    calculateOrder: editData.value.schemeDetailList.length + 1
  }

  switch (val) {
  case "REGEX": {
    editControlCommon.formInputEl = [{
      title: "字段名称",
      key: "resultName",
      element: "input"
    },
    {
      title: "绑定数据表",
      key: "orderName",
      element: "input",
      type: "dialog"
    },
    {
      title: "绑定数据表字段",
      key: "orderField",
      element: "input",
      type: "dialog"
    }]
    editControlCommon.formTimeAndNumber = [{
      title: "显示顺序",
      key: "displayOrder",
      element: "number"
    }]
    editControlCommon.formTextAreaEl = [{
      title: "正则表达式",
      key: "expression",
      element: "input",
      type: "textarea"
    }]
    getDataChange("SCHEME")
    return
  }
  case "FOR_MUL_TAG":
  case "SINGLE_TAG": {
    formData.value.singleType = "anyMatch"
    formData.value.oneRowTag = false
    editControlCommon.formInputEl = [
      {
        title: "字段名称",
        key: "resultName",
        element: "input"
      },
      {
        title: "绑定数据表",
        key: "orderName",
        element: "input",
        type: "dialog"
      },
      {
        title: "分组字段",
        key: "orderField",
        element: "input",
        type: "dialog"
      },
      {
        title: "取值字段",
        key: "groupByFieldName",
        element: "input",
        type: "dialog"
      },
      {
        title: "单行标记",
        key: "oneRowTag",
        element: "radio",
        options: [{
          label: "是",
          value: true
        },
        {
          label: "否",
          value: false
        }],
        illustrate:
            "勾选之后，同一分组的数据计算出来的结果只会放到该组中的其中一行，其他行设为0",
        change: val => {
          if (val) {
            if (!editControlCommon.formInputEl[5]) {
              editControlCommon.formInputEl.push({
                title: "其他行数值",
                key: "otherRowValue",
                element: "input"
              })
            }
          } else {
            if (editControlCommon.formInputEl[5]) {
              editControlCommon.formInputEl.splice(5, 1)
            }
          }
          console.log(val)
        }
      }
    ]
    editControlCommon.formTimeAndNumber = [{
      title: "显示顺序",
      key: "displayOrder",
      element: "number"
    }]
    editControlCommon.formTextAreaEl = []
    getDataChange("SCHEME")
    return
  }
  case "MAX":
  case "MIN":
  case "COUNT":
  case "SUM": {
    formData.value.oneRowTag = false
    editControlCommon.formInputEl = [
      {
        title: "字段名称",
        key: "resultName",
        element: "input"
      },
      {
        title: "绑定数据表",
        key: "orderName",
        element: "input",
        type: "dialog"
      },
      {
        title: "绑定数据表字段",
        key: "orderField",
        element: "input",
        type: "dialog"
      },
      {
        title: "汇总字段所在表",
        key: "groupByTable",
        element: "input",
        type: "dialog"
      },
      {
        title: "汇总字段",
        key: "groupByFieldName",
        element: "input",
        type: "dialog"
      },
      {
        title: "单行标记",
        key: "oneRowTag",
        element: "radio",
        options: [{
          label: "是",
          value: true
        },
        {
          label: "否",
          value: false
        }]
      }
    ]
    editControlCommon.formTimeAndNumber = [{
      title: "显示顺序",
      key: "displayOrder",
      element: "number"
    }]
    editControlCommon.formTextAreaEl = []
    getDataChange("SCHEME")

    return
  }

  case "ORDER_DATA": {
    editControlCommon.formInputEl = [{
      title: "字段名称",
      key: "resultName",
      element: "input"
    },
    {
      title: "绑定数据表",
      key: "orderName",
      element: "input",
      type: "dialog"
    },
    {
      title: "绑定数据表字段",
      key: "orderField",
      element: "input",
      type: "dialog"
    }]
    editControlCommon.formTimeAndNumber = [{
      title: "显示顺序",
      key: "displayOrder",
      element: "number"
    }]
    editControlCommon.formTextAreaEl = []
    return
  }
  case "FIXED": {
    editControlCommon.formInputEl = [{
      title: "字段名称",
      key: "resultName",
      element: "input"
    }]
    editControlCommon.formTimeAndNumber = [{
      title: "显示顺序",
      key: "displayOrder",
      element: "number"
    }]
    if (formData.value.resultType === "NUM") {
      editControlCommon.formTimeAndNumber.push({
        title: "固定计算系数",
        key: "fixedCoefficient",
        element: "number"
      })
    } else if (formData.value.resultType === "STRING") {
      editControlCommon.formInputEl.push({
        title: "固定值",
        key: "fixedCoefficient",
        element: "input"
      })
    } else {
      editControlCommon.formTimeAndNumber.push({
        title: "固定日期",
        key: "fixedCoefficient",
        element: "date"
      })
    }

    return
  }
  case "DEPEND": {
    editControlCommon.formInputEl = [{
      title: "字段名称",
      key: "resultName",
      element: "input"
    },
    {
      title: "绑定分类关系",
      key: "dependCode",
      element: "input",
      type: "dialog"
    }]
    editControlCommon.formTimeAndNumber = [{
      title: "显示顺序",
      key: "displayOrder",
      element: "number"
    }]
    return
  }
  case "EQUATION": {
    editControlCommon.formInputEl = [{
      title: "字段名称",
      key: "resultName",
      element: "input"
    }]
    editControlCommon.formTimeAndNumber = [{
      title: "显示顺序",
      key: "displayOrder",
      element: "number"
    }]
    return
  }
  default:
    return
  }
}

const getDataChange = val => {
  nextTick(() => {
    if (val === "TABLE") {
      if (editControlCommon.formInputEl[1].title === "绑定数据表") {
        editControlCommon.formInputEl[1].disabled = false
      }
      formData.value.getDataFrom = val
      proxy.$refs.dialogForm.updateDialogInput({
        orderName: ``,
        orderTable: ``,
        getDataFrom: "",
        groupByTable: ""
      })
    } else {
      if (editControlCommon.formInputEl[1].title === "绑定数据表") {
        editControlCommon.formInputEl[1].disabled = true
      }
      formData.value.getDataFrom = val
      proxy.$refs.dialogForm.updateDialogInput({
        getDataFrom: val,
        orderName: editData.value.schemeMain.name,
        orderTable: editData.value.schemeMain.schemeCode,
        groupByTable: editData.value.schemeMain.schemeCode
      })
    }
  })
}

const create = () => {
  showCreateFun.value = true
}
const conditionItem = ref({})

const openSelectDialog = async (name, type) => {
  switch (name) {
  case `SCHEME`: {
    selectTableData.dataSource = new DataSource({
      selectUri: `/colorful-fog/orderTable/select`,
      tableHeader: [{
        label: "字段code",
        prop: "resultCode",
        width: 200
      },
      {
        label: "字段名称",
        prop: "resultName",
        width: 200
      },
      {
        label: "字段类型",
        prop: "type",
        width: 200
      }],
      pageSize: 10
    })

    selectTableData.dataSource.forMatData = (row, column) => {
      if (column.property === "type") return fieldType[row[column.property]]
      else return row[column.property] ?? "-"
    }
    selectTableData.dataSource.tableData = editData.value.schemeDetailList
    selectTableData.isShow = true
    selectTableData.chooseRow = val => {
      if (type === "orderField") {
        proxy.$refs.dialogForm.updateDialogInput({
          orderField: val.resultCode
        })
        formData.value.orderField = val.resultCode
        console.log(formData.value)
        selectTableData.isShow = false
        return
      }
      if (type === "groupByFieldName") {
        proxy.$refs.dialogForm.updateDialogInput({
          groupByFieldName: val.resultName,
          groupByField: val.resultCode
        })
        formData.value.groupByFieldName = val.resultName
        selectTableData.isShow = false
        return
      }

      selectTableData.isShow = false
    }

    return
  }
  case "archiveUserName": {
    selectTableData.dataSource = new DataSource({
      selectUri: `/colorful-fog/schemeUser/selectUserData`,
      tableHeader: [],
      pageSize: 10
    })

    selectTableData.dataSource.searchData = {
      dataSourceId: editData.value.schemeMain.dataSourceId,
      userDataTable: editData.value.schemeMain.userDataTable,
      userDataCodeField: editData.value.schemeMain.userDataCodeField,
      userDataNameField: editData.value.schemeMain.userDataNameField
    }
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
      selectTableData.dataSource.tableData.push(...data)
      selectTableData.dataSource.total = 10
    }
    await selectTableData.dataSource.initData()
    selectTableData.dataSource.total =
        selectTableData.dataSource.tableData.length
    if (selectUserOptions.length === 0) {
      let temp = []
      selectTableData.dataSource.tableData.forEach(item => {
        if (!temp.includes(item.name)) {
          selectUserOptions.push({
            value: item.name + " " + item.code,
            label: item.name
          })
          temp.push(item.name)
        }
      })
    }

    selectUser.value = true

    return
  }
  case "dataSourceName": {
    selectTableData.useChoose = false
    selectTableData.searchPlaceholde = "输入数据库名称进行搜索"
    selectTableData.chooseRow = val => {
      editData.value.schemeMain.dataSourceName = val.sourceName
      editData.value.schemeMain.dataSourceId = val.id

      selectTableData.isShow = false
    }
    selectTableData.doSearch = val => {
      selectTableData.dataSource.searchData = {}
      if (val) selectTableData.dataSource.searchData.sourceName = val
      selectTableData.dataSource.initData()
    }
    selectTableData.dataSource = new DataSource({
      selectUri: `/colorful-fog/dataSource/list`,
      tableHeader: [{
        label: "数据库名称",
        prop: "sourceName",
        width: 200
      },
      {
        label: "数据库Code",
        prop: "sourceCode",
        width: 200
      }],
      pageSize: 10
    })

    selectTableData.dataSource.forMatData = (row, column) => {
      return row[column.property] ?? "-"
    }
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
      context.total = data.length
      context.tableData = data
    }
    await selectTableData.dataSource.initData()
    selectTableData.isShow = true
    return
  }
  case "[!]OR":
  case "[!]AND": {
    proxy
      .$prompt("请输入表达式名称", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消"
      })
      .then(({ value }) => {
        initData()
        addCondition({
          schemeCode: editData.value.schemeMain.schemeCode,
          conditionField: "",
          publicConnect: name,
          conditionName: value
        }).then(res => {
          if (res.code === 200) {
            conditionItem.value = res.data
            showCreateCondition.value = true
          }
        })
      })
      .catch(() => {
        // null
      })
    return
  }
  case "granularity": {
    selectTableData.useChoose = false
    selectTableData.searchPlaceholde = "输入单据名称进行搜索"
    selectTableData.chooseRow = val => {
      editData.value.schemeMain.granularity = val.orderTable
      editData.value.schemeDetailList.length = 0
      selectTableData.isShow = false
    }
    selectTableData.doSearch = val => {
      selectTableData.dataSource.searchData = {}
      if (val) selectTableData.dataSource.searchData.orderName = val
      selectTableData.dataSource.initData()
    }
    selectTableData.dataSource = new DataSource({
      selectUri: `/colorful-fog/orderTable/select`,
      tableHeader: [{
        label: "单据名称",
        prop: "orderName",
        width: 200
      },
      {
        label: "单据关联表",
        prop: "orderTable",
        width: 200
      },
      {
        label: "备注",
        prop: "comment",
        width: 200
      }],
      pageSize: 10
    })

    selectTableData.dataSource.forMatData = (row, column) => {
      return row[column.property] ?? "-"
    }
    await selectTableData.dataSource.initData()
    selectTableData.isShow = true
    return
  }
  case "schemeDetail": {
    typeChange(["ORDER_DATA"])

    editControlCommon.isShow = true

    return
  }
  case "groupByTable":
  case "timeFieldTable":
  case "userDataTable":
  case "belongRelTableName":
  case "groupByField":
  case "orderName": {
    selectTableData.searchPlaceholde = "输入单据表名称进行搜索"
    selectTableData.chooseRow = val => {
      if (name === "belongRelTableName") {
        editData.value.schemeMain.belongRelTableName = val.orderTable
        editData.value.schemeMain.orderName = val.orderName
        selectTableData.isShow = false
        openSelectDialog("belongRelTableField")
        return
      }
      if (name === "userDataTable") {
        editData.value.schemeMain.userDataTable = val.orderTable
        selectTableData.isShow = false
        return
      }
      if (name === "timeFieldTable") {
        editData.value.schemeMain.timeFieldTable = val.orderTable
        selectTableData.isShow = false
        return
      }
      if (name === "groupByTable") {
        formData.value.groupByTable = val.orderTable
        proxy.$refs.dialogForm.updateDialogInput({
          groupByTable: val.orderTable
        })
        selectTableData.isShow = false
        return
      }

      proxy.$refs.dialogForm.updateDialogInput({
        orderName: val.orderName,
        orderTable: val.orderTable
      })
      formData.value.orderTable = val.orderTable
      formData.value.orderName = val.orderName
      selectTableData.isShow = false
    }
    selectTableData.doSearch = val => {
      selectTableData.dataSource.searchData = {}
      if (val) selectTableData.dataSource.searchData.orderName = val
      selectTableData.dataSource.initData()
    }
    selectTableData.dataSource = new DataSource({
      selectUri: `/colorful-fog/orderTable/select`,
      tableHeader: [{
        label: "单据名称",
        prop: "orderName",
        width: 200
      },
      {
        label: "单据关联表",
        prop: "orderTable",
        width: 200
      },
      {
        label: "单据关联表名称",
        prop: "orderTableName",
        width: 200
      },
      {
        label: "类型",
        prop: "type",
        width: 200
      }],
      pageSize: 10
    })

    selectTableData.dataSource.forMatData = (row, column) => {
      if (column.property === "type")
        return row[column.property] === "MAIN" ? "主表" : "明细"
      else return row[column.property] ?? "-"
    }
    await selectTableData.dataSource.initData()
    selectTableData.isShow = true
    return
  }

  case "timeFieldName":
  case "userDataNameField":
  case "userDataCodeField":
  case "belongRelTableField":
  case "groupByFieldName":
  case "orderField": {
    if (formData.value.getDataFrom === "SCHEME") {
      openSelectDialog("SCHEME", name)
      return
    }

    selectTableData.useChoose = false
    selectTableData.searchPlaceholde = "输入字段名称进行搜索"
    selectTableData.chooseRow = val => {
      if (name === "belongRelTableField") {
        editData.value.schemeMain.belongRelTableField = val.field
        selectTableData.isShow = false
        return
      }
      if (name === "userDataCodeField") {
        editData.value.schemeMain.userDataCodeField = val.field
        selectTableData.isShow = false
        return
      }
      if (name === "userDataNameField") {
        editData.value.schemeMain.userDataNameField = val.field
        selectTableData.isShow = false
        return
      }
      if (name === "groupByFieldName") {
        proxy.$refs.dialogForm.updateDialogInput({
          groupByFieldName: val.fieldName,
          groupByField: val.field
        })
        formData.value.groupByFieldName = val.fieldName
        editData.value.schemeMain.groupByFieldName = val.fieldName
        editData.value.schemeMain.groupByField = val.field
        selectTableData.isShow = false
        return
      }
      if (name === "timeFieldName") {
        editData.value.schemeMain.timeFieldName = val.fieldName
        editData.value.schemeMain.timeField = val.field
        selectTableData.isShow = false
        return
      }
      proxy.$refs.dialogForm.updateDialogInput({ orderField: val.field })
      formData.value.orderField = val.field
      selectTableData.isShow = false
    }
    selectTableData.doSearch = val => {
      if (name === "belongRelTableField")
        selectTableData.dataSource.searchData = {
          tableName: editData.value.schemeMain.belongRelTableName
        }
      else
        selectTableData.dataSource.searchData = {
          tableName: formData.value.orderTable
        }
      if (val) selectTableData.dataSource.searchData.fieldName = val
      selectTableData.dataSource.initData()
    }
    selectTableData.dataSource = new DataSource({
      selectUri: `/colorful-fog/tableFiled/select`,
      tableHeader: [{
        label: "字段key",
        prop: "field",
        width: 200
      },
      {
        label: "字段名",
        prop: "fieldName",
        width: 200
      },
      {
        label: "数据类型",
        prop: "type",
        width: 200
      }],
      pageSize: 10
    })
    if (name === "groupByTable")
      selectTableData.dataSource.searchData = {
        tableName: formData.value.groupByTable
      }
    else if (name === "timeFieldName")
      selectTableData.dataSource.searchData = {
        tableName: editData.value.schemeMain.timeFieldTable
      }
    else if (name === "userDataCodeField" || name === "userDataNameField")
      selectTableData.dataSource.searchData = {
        tableName: editData.value.schemeMain.userDataTable
      }
    else if (name === "belongRelTableField")
      selectTableData.dataSource.searchData = {
        tableName: editData.value.schemeMain.belongRelTableName
      }
    else
      selectTableData.dataSource.searchData = {
        tableName: formData.value.orderTable
      }
    selectTableData.dataSource.forMatData = (row, column) => {
      return row[column.property] ?? "-"
    }
    await selectTableData.dataSource.initData()
    selectTableData.isShow = true
    return
  }
  case "dependCode": {
    selectTableData.useChoose = false
    selectTableData.searchPlaceholde = "输入分类关系名称进行搜索"
    selectTableData.chooseRow = val => {
      proxy.$refs.dialogForm.updateDialogInput({ dependCode: val.dependCode })
      formData.value.dependCode = val.dependCode
      selectTableData.isShow = false
    }
    selectTableData.doSearch = val => {
      selectTableData.dataSource.searchData = {}
      if (val) selectTableData.dataSource.searchData.name = val
      selectTableData.dataSource.initData()
    }
    selectTableData.dataSource = new DataSource({
      selectUri: `/colorful-fog/dependMain/list`,
      tableHeader: [{
        label: "分类Code",
        prop: "dependCode",
        width: 200
      },
      {
        label: "分类名称",
        prop: "name",
        width: 200
      },
      {
        label: "分类类型",
        prop: "dependType",
        width: 200
      }],
      pageSize: 10
    })

    selectTableData.dataSource.forMatData = (row, column) => {
      if (column.property === "dependType")
        return row[column.property] === "RANGE" ? "范围分类" : "名词分类"
      else return row[column.property] ?? "-"
    }
    await selectTableData.dataSource.initData()
    selectTableData.isShow = true
    return
  }
  case "schemeName": {
    selectTableData.searchPlaceholde = "输入账单计划名称进行搜索"
    selectTableData.chooseRow = val => {
      if (val.children) return
      if (val.schemeCode === editData.value.schemeMain.schemeCode) {
        proxy.$message.error("不可以选择当前的方案")
        return
      }
      let params = {
        sourceSchemeCode: val.schemeCode,
        targetSchemeCode: editData.value.schemeMain.schemeCode
      }
      copyField(params).then(res => {
        if (res.code === 200) {
          initData()
          proxy.$message.success("复制成功")
          selectTableData.isShow = false
        }
      })
    }
    selectTableData.doSearch = val => {
      selectTableData.dataSource.searchData = {}
      if (val) selectTableData.dataSource.searchData.name = val
      selectTableData.dataSource.initData()
    }
    selectTableData.dataSource = new DataSource({
      selectUri: `/colorful-fog/schemeMain/selectMenu`,
      tableHeader: [{
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
  default: {
    return
  }
  }
}

const createFunDone = res => {
  proxy.$refs.dialogForm.updateDialogInput({ expression: res })
  formData.value.expression = res
  showCreateFun.value = false
}

const editControlCommon = reactive({
  isShow: false,
  formInputEl: [],
  formSelectEl: [{
    title: "计算结果类型",
    key: "resultType",
    element: "radio",
    options: [{
      label: "数字",
      value: "NUM"
    },
    {
      label: "文本",
      value: "STRING"
    },
    {
      label: "日期",
      value: "DATE"
    }],
    rules: [{
      required: true,
      message: "该项不能为空",
      trigger: "blur"
    }],
    change: val => {
      editControlCommon.formTimeAndNumber.splice(1, 1)
      formData.value.decimalPlaces = 2
      if (val === "NUM") {
        editControlCommon.formTimeAndNumber.push({
          title: "保留小数位数",
          key: "decimalPlaces",
          element: "number"
        })
      }
      if (formData.value.type === "FIXED") {
        editControlCommon.formInputEl.splice(1, 1)
        editControlCommon.formTimeAndNumber.splice(1, 1)
        if (val === "NUM") {
          editControlCommon.formTimeAndNumber.push({
            title: "固定计算系数",
            key: "fixedCoefficient",
            element: "number"
          })
        } else if (val === "STRING") {
          editControlCommon.formInputEl.push({
            title: "固定值",
            key: "fixedCoefficient",
            element: "input"
          })
        } else {
          editControlCommon.formTimeAndNumber.push({
            title: "固定日期",
            key: "fixedCoefficient",
            element: "date"
          })
        }
      }
    }
  },
  {
    title: "推送时是否隐藏",
    key: "hideWhenPush",
    element: "radio",
    options: [{
      label: "否",
      value: false
    },
    {
      label: "是",
      value: true
    }],
    rules: [{
      required: true,
      message: "该项不能为空",
      trigger: "blur"
    }]
  },
  {
    title: "导出时是否隐藏",
    key: "hideWhenExport",
    element: "radio",
    options: [{
      label: "否",
      value: false
    },
    {
      label: "是",
      value: true
    }],
    rules: [{
      required: true,
      message: "该项不能为空",
      trigger: "blur"
    }]
  }],
  formTimeAndNumber: [],
  formTextAreaEl: [],
  emitOpenDialog: val => {
    openSelectDialog(val)
  },
  inputDone: async val => {
    let params = {
      ...val
    }
    if (params.fixedCoefficient) {
      if (params.resultType === "NUM")
        params.fixedCoefficient = +params.fixedCoefficient
      else if (params.resultType === "DATE")
        params.fixedCoefficient = Date.parse(params.fixedCoefficient)
    }
    if (params.type === "FOR_MUL_TAG" || params.type === "SINGLE_TAG") {
      let str = `${params.singleType}(${params.text1})?${params.text2}:${params.text3}`
      params.expression = str
    }

    if (params.id) {
      let res = await updateSchemeDetailBatch([params])
      if (res.code === 200) {
        proxy.$message.success("更新成功")
        initData()
        editControlCommon.isShow = false
      }
    } else {
      params.schemeCode = editData.value.schemeMain.schemeCode
      let res = await addSchemeDetailBatch([params])
      if (res.code === 200) {
        proxy.$message.success("新增成功")
        initData()
        editControlCommon.isShow = false
      }
    }
  }
})

const showSchemeDeital = (tag, index) => {
  formData.value.resultType = tag.resultType
  typeChange([tag.type], "update")
  console.log(tag)
  formData.value = {
    ...tag,
    hideWhenPush: tag.hideWhenPush ?? false,
    hideWhenExport: tag.hideWhenExport ?? false
  }
  if (formData.value.fixedCoefficient) {
    if (formData.value.resultType === "NUM")
      formData.value.fixedCoefficient = +formData.value.fixedCoefficient
    else if (formData.value.resultType === "DATE")
      formData.value.fixedCoefficient = new proxy.$DataTool().timeStamp2Time(
        +formData.value.fixedCoefficient
      )
  }

  if (
    formData.value.type === "SINGLE_TAG" ||
    formData.value.type === "FOR_MUL_TAG"
  ) {
    formData.value.singleType = formData.value.expression.split("(")[0]
    formData.value.text1 = formData.value.expression.split("(")[1].split(")")[0]
    formData.value.text2 = formData.value.expression
      .split("(")[1]
      .split(")")[1]
      .split("?")[1]
      .split(":")[0]
    formData.value.text3 = formData.value.expression
      .split("(")[1]
      .split(")")[1]
      .split("?")[1]
      .split(":")[1]
  }
  editControlCommon.isShow = true
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

const checkDataEmpty = val => {
  let checkList = [
    "belongRelTableField",
    "belongRelTableName",
    "billType",
    "dataSourceId",
    "dataSourceName",
    "executionTime",
    "executionUnit",
    "granularity",
    "name",
    "sort",
    "userDataCodeField",
    "userDataNameField",
    "userDataTable",
    "timeField",
    "timeFieldName",
    "timeFieldTable",
    "timeFormula"
  ]
  for (let i = 0; i < checkList.length; i++) {
    if (
      !val[checkList[i]] && val[checkList[i]] !== 0 ||
      val[checkList[i]].toString().length === 0
    )
      return false
  }
  return true
}

let forMatTimeFormula = (sourceValue, type) => {
  if (!sourceValue) return
  let value = sourceValue.toString().split(".")

  if (type === 'MONTHLY') {
    let res = new proxy.$DataTool().timeStamp2Time(Date.parse(new Date()) + Number(value[0]) * 2592000000).split("-").slice(0, 2).join('-')
    return res + '-' + value[1].padStart(2, '0')
  }
  let res = new proxy.$DataTool().timeStamp2Time(Date.parse(new Date()) + Number(value[0]) * 86400000)
  return res + ' ' + value[1] + ':00:00'

}

const saveMain = () => {

  editData.value.schemeMain.sort = props.addType
  if (!checkDataEmpty(editData.value.schemeMain)){
    proxy.$message.error('请将信息填写完整')
    return
  }

  if (editData.value.schemeMain.executionUnit === "MONTHLY") {
    let now = new proxy.$DataTool()
      .timeStamp2Time(Date.parse(new Date()))
      .split("-")
    let timeFormulaStart = editData.value.schemeMain.timeFormula[0].split("-")
    let timeFormulaEnd = editData.value.schemeMain.timeFormula[1].split("-")
    editData.value.schemeMain.timeFormulaStart = `${
      Number(timeFormulaStart[1]) - Number(now[1])
    }.${Number(timeFormulaStart[2])}`
    editData.value.schemeMain.timeFormulaEnd = `${
      Number(now[1]) - Number(timeFormulaEnd[1])
    }.${Number(timeFormulaEnd[2])}`
  } else {
    let now = new proxy.$DataTool().timeStamp2Time(Date.parse(new Date()))
    let timeFormulaStart = editData.value.schemeMain.timeFormula[0].split(" ")
    let timeFormulaEnd = editData.value.schemeMain.timeFormula[1].split(" ")

    editData.value.schemeMain.timeFormulaStart = `${
      (Date.parse(timeFormulaStart[0]) - Date.parse(now)) / 86400000
    }.${timeFormulaStart[1].split(":")[0]}`

    editData.value.schemeMain.timeFormulaEnd = `${
      (Date.parse(timeFormulaEnd[0]) - Date.parse(now)) / 86400000
    }.${timeFormulaEnd[1].split(":")[0]}`

  }

  if (myEditId.value === -1){
    editData.value.schemeMain.sort = props.addType
    if (!checkDataEmpty(editData.value.schemeMain)){
      proxy.$message.error('请将信息填写完整')
      return
    }

    addSchemeMain(editData.value.schemeMain).then(res => {
      if (res.code === 200){
        proxy.$message.success('新增成功')
        myEditId.value = res.data.id
        initData()
      }
    })
    return
  }

  updateSchemeMain(editData.value.schemeMain).then(res => {
    if (res.code === 200){
      proxy.$message.success('更新成功')
    }
  })
}

const saveBindUser = () => {
  let params = {
    schemeCode: editData.value.schemeMain.schemeCode,
    tableName: editData.value.schemeMain.userDataTable,
    entityList: selectUserList.value.map(item => {
      return {
        schemeCode: editData.value.schemeMain.schemeCode,
        archiveUserCode: item.split(" ")[1],
        archiveUserName: item.split(" ")[0]
      }
    })
  }

  proxy.$alert("确认保存？会覆盖以前保存的数据", "提示", {
    type: "info",
    icon: "InfoFilled",
    confirmButtonText: "确定继续",
    callback: action => {
      if (action === "confirm") {
        schemeUserBatch(params).then(res => {
          if (res.code === 200) {
            proxy.$message.success("保存成功")
          }
        })
      }
    }
  })
}

const fileChoice = async file => {
  let dataTool = new proxy.$DataTool()
  let xlsxData = await dataTool.xlsx2DataObject(file.raw)
  let data = handleXlsxData(xlsxData)
  let params = {
    dataSourceId: editData.value.schemeMain.dataSourceId,
    userDataTable: editData.value.schemeMain.userDataTable,
    userDataCodeField: editData.value.schemeMain.userDataCodeField,
    userDataNameField: editData.value.schemeMain.userDataNameField,
    userNameList: data,
    schemeCode: editData.value.schemeMain.schemeCode
  }

  proxy.$alert("是否确认导入？", "提示", {
    type: "info",
    icon: "InfoFilled",
    confirmButtonText: "确定",
    callback: action => {
      if (action === "confirm") {
        addSchemeUserBatch(params).then(res => {
          if (res.code === 200) {
            proxy.$alert("导入成功", "提示", {
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
    }
  })
}

const handleXlsxData = file => {
  return file.map(item => {
    return item["用户名称"]
  })
}

const fieldType = {
  FIXED: "固定系数",
  ORDER_DATA: "原单据字段值",
  SUM: "汇总",
  DEPEND: "分类系数",
  EQUATION: "自定义公式",
  FOR_MUL_TAG: "循环标记"
}

const testData = reactive({
  resultNameList: [],
  resultDataList: [],
  originNameList: [],
  originDataList: []
})

const createTest = () => {
  testData.resultNameList = []
  createTestData({ code: editData.value.schemeMain.schemeCode }).then(res => {
    testData.resultNameList = res.data.resultNameList
    testData.resultDataList = res.data.billDataList
    testData.originNameList = res.data.originNameList
    testData.originDataList = res.data.originDataList
  })
}

const timeOptions = reactive([])

const unitChange = (val, onlyChange) => {
  if (val === editData.value.schemeMain.executionUnit === val) return
  if (onlyChange){
    editData.value.schemeMain.timeFormula = []

  }
  timeOptions.length = 0
  if (val === "DAILY") {
    timeOptions.push(
      ...Array.from({ length: 24 }, (v, i) => {
        return { label: i + "点", value: i }
      })
    )
  } else {
    timeOptions.push(
      ...Array.from({ length: 31 }, (v, i) => {
        return { label: i + 1 + "日", value: i + 1 }
      })
    )
  }
}

let sortType = ""
const sort = val => {
  sortType = "display"
  editData.value.schemeDetailList.sort((a, b) => {
    return a.displayOrder - b.displayOrder
  })
}

const createFiledBatch = async () => {
  selectTableData.useChoose = false
  selectTableData.searchPlaceholde = "输入单据表名称进行搜索"
  selectTableData.chooseRow = val => {
    proxy.$refs.dialogForm.updateDialogInput({
      orderName: val.orderName,
      orderTable: val.orderTable
    })
    formData.value.orderTable = val.orderTable
    formData.value.orderName = val.orderName
    selectTableData.isShow = false
    createFiledsBatch()
  }
  selectTableData.doSearch = val => {
    selectTableData.dataSource.searchData = {}
    if (val) selectTableData.dataSource.searchData.orderName = val
    selectTableData.dataSource.initData()
  }
  selectTableData.dataSource = new DataSource({
    selectUri: `/colorful-fog/orderTable/select`,
    tableHeader: [{
      label: "单据名称",
      prop: "orderName",
      width: 200
    },
    {
      label: "单据关联表",
      prop: "orderTable",
      width: 200
    },
    {
      label: "单据关联表名称",
      prop: "orderTableName",
      width: 200
    },
    {
      label: "类型",
      prop: "type",
      width: 200
    }],
    pageSize: 10
  })

  selectTableData.dataSource.forMatData = (row, column) => {
    if (column.property === "type")
      return row[column.property] === "MAIN" ? "主表" : "明细"
    else return row[column.property] ?? "-"
  }
  await selectTableData.dataSource.initData()
  selectTableData.isShow = true
  return
}

const createFiledsBatch = async (row, column) => {
  selectTableData.useChoose = true
  selectTableData.searchPlaceholde = "输入字段名称进行搜索"
  selectTableData.submit = async val => {
    let params = []
    selectTableData.dataSource.selections.forEach((item, idx) => {
      let temp = {
        schemeCode: editData.value.schemeMain.schemeCode,
        calculateOrder: formData.value.calculateOrder + idx,
        displayOrder: formData.value.displayOrder + idx,
        orderTable: formData.value.orderTable,
        orderName: formData.value.orderName,
        orderField: item.field,
        resultType: "STRING",
        resultName: item.fieldName,
        type: "ORDER_DATA",
        getDataFrom: "TABLE"
      }
      params.push(temp)
    })
    let res = await addSchemeDetailBatch(params)
    if (res.code === 200) {
      proxy.$message.success("新增成功")
      initData()
      selectTableData.isShow = false
    }
  }
  selectTableData.doSearch = val => {
    selectTableData.dataSource.searchData = {
      tableName: formData.value.orderTable
    }
    if (val) selectTableData.dataSource.searchData.fieldName = val
    selectTableData.dataSource.initData()
  }
  selectTableData.dataSource = new DataSource({
    selectUri: `/colorful-fog/tableFiled/select`,
    tableHeader: [{
      label: "字段key",
      prop: "field",
      width: 200
    },
    {
      label: "字段名",
      prop: "fieldName",
      width: 200
    },
    {
      label: "数据类型",
      prop: "type",
      width: 200
    }],
    pageSize: 10
  })
  selectTableData.dataSource.searchData = {
    tableName: formData.value.orderTable
  }

  selectTableData.dataSource.forMatData = (row, column) => {
    return row[column.property] ?? "-"
  }
  await selectTableData.dataSource.initData()
  selectTableData.isShow = true
  return
}

const getAppendClass = tag => {
  if (tag.hideWhenPush && tag.hideWhenExport) {
    return "complex"
  } else if (tag.hideWhenPush) {
    return "hideWhenPush"
  } else if (tag.hideWhenExport) {
    return "hideWhenExport"
  } else {
    return `common`
  }
}
</script>

<style lang="less" scoped>
.chosen {
  background: #ecf5ff;
}
.collect {
  display: flex;
}
.container {
  display: flex;
  background: white;
  padding: 10px;

  .content {
    flex: 1;
    padding: 10px 20px;
  }

  .content-body {
    padding: 30px 0;
    font-size: 14px;
  }
}

.hide {
  position: absolute;
  left: 0;
  top: 0;
  width: 100%;
  animation: test 1s linear 0s 1 normal forwards;
}

@keyframes test {
  0% {
    left: 0;
    opacity: 1;
  }
  100% {
    left: 100%;
    opacity: 0;
  }
}

:deep(.el-card__body) {
  padding-top: 10px;
}

.label-base {
  display: inline-block;
  width: 140px;
}

.box-card {
  max-width: 100%;
}

.tag {
  display: inline-flex;
  align-items: center;
  justify-content: space-around;
  padding: 6px 10px;
  border-radius: 4px;
  font-size: 12px;
  color: #fff;
}

.common {
  background: #61adf8;
}
.hideWhenExport {
  background: #f82fee;
}
.hideWhenPush {
  background: #f18226;
}
.complex {
  background: linear-gradient(to right, #f82fee, #f18226);
}

.exp {
  color: #fff;
  padding: 4px;
  display: inline-block;
  margin: 0 10px;
}
</style>
