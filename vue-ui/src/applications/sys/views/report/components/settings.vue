<template>
  <div>
    <el-dialog
      v-model="showDialog"
      lock-scroll
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :show-close="false"
      :fullscreen="false"
      width="70%"
      top="3%"
      destroy-on-close
    >
      <template #header="{ titleId, titleClass }">
        <div class="jx-dialog-header">
          <div :id="titleId" :class="titleClass">
            <h4 class="inline-block">
              报表配置
            </h4>
          </div>
          <div>
            <el-button
              class="dialog-close-btn" type="danger"
              icon="CloseBold"
              circle
              @click="closeDialog"
            />
          </div>
        </div>
      </template>
      <selectDialogForm
        ref="dialogForm"
        :loading="loading"
        :width="'60%'"
        :form-data="formData"
        :use-dialog="false"
        :title="'请填写相关信息'"
        :form-input-el="editControlCommon.formInputEl"
        :form-select-el="editControlCommon.formSelectEl"
        :form-text-area-el="editControlCommon.formTextAreaEl"
        :form-upload-el="editControlCommon.formUploadEl"
        :is-change="isChange"
        @close-dialog="closeDialog"
        @emit-open-dialog="editControlCommon.emitOpenDialog"
        @input-done="editControlCommon.inputDone"
      >
        <template v-if="isSelectShemeDone" #append>
          <span class="title-collapse">
            报表详情
          </span>
          <div class="demo-collapse">
            <el-collapse v-model="activeNames" @change="handleChange">
              <el-collapse-item title="定义X轴" name="1">
                <el-select
                  v-model="formData.xAxis.fieldName"
                  style="width:220px"
                  filterable
                  :placeholder="`请选择X轴`"
                  collapse-tags
                  collapse-tags-tooltip
                  class="mr10"
                  @change="xAxisChange"
                >
                  <el-option
                    v-for="option in headerOptions"
                    :key="option.label"
                    :label="option.label"
                    :value="option.value"
                  />
                </el-select>
                <el-select
                  v-if="formData.xAxis.resultType==='DATE'"
                  v-model="formData.timeUnit"
                  style="width:220px"
                  :placeholder="`请选择统计时间`"
                  collapse-tags
                  collapse-tags-tooltip
                  class="mr10"
                  @change="timeUnitChange"
                >
                  <el-option
                    v-for="option in timeUnitOptions"
                    :key="option.label"
                    :label="option.label"
                    :value="option.value"
                  />
                </el-select>
                <el-select
                  v-if="formData.xAxis.resultType==='DATE'"
                  v-model="formData.year"
                  style="width:220px"
                  :placeholder="`请选择时间`"
                  collapse-tags
                  collapse-tags-tooltip
                  class="mr10"
                >
                  <el-option
                    v-for="option in yearOptions"
                    :key="option.label"
                    :label="option.label"
                    :value="option.value"
                  />
                </el-select>
                <el-select
                  v-if="formData.timeUnit==='天'"
                  v-model="formData.month"
                  style="width:220px"
                  :placeholder="`请选择时间`"
                  collapse-tags
                  collapse-tags-tooltip
                  class="mr10"
                >
                  <el-option
                    v-for="option in monthOptions"
                    :key="option.label"
                    :label="option.label"
                    :value="option.value"
                  />
                </el-select>
              </el-collapse-item>
              <el-collapse-item title="定义Y轴" name="2">
                <div class="flex align-center">
                  <el-select
                    v-model="formData.yAxis.list"
                    multiple
                    collapse-tags
                    filterable
                    collapse-tags-tooltip
                    :max-collapse-tags="10"
                    style="width:500px"
                    placeholder="请选择Y轴要统计的数据"
                    @change="yAxisChange"
                  >
                    <el-option
                      v-for="item in yOptions"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value"
                    />
                  </el-select>
                </div>
              </el-collapse-item>
              <el-collapse-item title="过滤条件" name="3">
                <div
                  v-for="(item,idx) in formData.filterCriteria"
                  :key="idx"
                  class="filterCriteria"
                >
                  <el-select
                    v-model="item.fieldNames"
                    size="small"
                    style="width:220px"
                    :placeholder="`请选择字段`"
                    collapse-tags
                    filterable
                    collapse-tags-tooltip
                    class="mr10"
                    @change="change($event,idx)"
                  >
                    <el-option
                      v-for="option in filterOptions"
                      :key="option.label"
                      :label="option.label"
                      :value="option.value"
                    />
                  </el-select>

                  <el-select
                    v-model="item.operator"
                    size="small"
                    class="mr10"
                    style="width:120px"
                    :placeholder="`请选择过滤类型`"
                    collapse-tags
                    filterable
                    collapse-tags-tooltip
                  >
                    <el-option
                      v-for="option in typeOptions"
                      :key="option.label"
                      :label="option.label"
                      :value="option.value"
                    />
                  </el-select>
                  <el-select
                    v-if="item.resultType==='STRING'"
                    v-model="item.value"
                    multiple
                    size="small"
                    filterable
                    allow-create
                    default-first-option
                    :reserve-keyword="false"
                    placeholder="请输入值"
                    style="width: 240px"
                  >
                    <el-option
                      v-for="item in []"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value"
                    />
                  </el-select>

                  <el-date-picker
                    v-else-if="item.resultType==='DATE'"
                    v-model="item.value"
                    type="daterange"
                    range-separator="至"
                    start-placeholder="开始时间"
                    end-placeholder="结束时间"
                    size="small"
                    @change="timeChange($event,item)"
                  />
                  <el-button
                    class="dialog-close-btn ml10" type="danger"
                    icon="CloseBold"
                    size="small"
                    circle
                    @click="deleteCondition(idx)"
                  />
                </div>
                <el-button
                  type="primary"
                  text
                  @click="addCondition"
                >
                  + 新增条件
                </el-button>
              </el-collapse-item>
              <el-collapse-item
                v-if="formData.year" title="添加对比曲线"
                name="4"
              >
                <el-select
                  v-model="formData.compare.fieldName"
                  size="small"
                  style="width:220px"
                  :placeholder="`请选择对比字段`"
                  collapse-tags
                  filterable
                  collapse-tags-tooltip
                  class="mr10"
                >
                  <el-option
                    v-for="option in filterOptions"
                    :key="option.label"
                    :label="option.label"
                    :value="option.value"
                  />
                </el-select>
                <el-input
                  disabled style="width:220px" size="small"
                  value="等于"
                />
                <div
                  class="filterCriteria mt10"
                >
                  <el-select
                    v-model="formData.compare.list"
                    multiple
                    size="small"
                    filterable
                    allow-create
                    default-first-option
                    :reserve-keyword="false"
                    placeholder="请输入值"
                    style="width:500px"
                  >
                    <el-option
                      v-for="item in []"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value"
                    />
                  </el-select>
                </div>
              </el-collapse-item>
            </el-collapse>
          </div>
        </template>
      </selectDialogForm>
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
    </el-dialog>
  </div>
</template>

<script setup>
import selectDialogForm from '@/components/public/selectDialogForm.vue'
import selectDialogTable from '@/components/public/selectDialogTable.vue'
import { ref, getCurrentInstance, reactive, computed, watch, inject } from 'vue'
import { DataSource, loading } from '../utils/report'
import { pbRequest } from "@/applications/sys/public/pbRequest/index"

const dataSource = ref(new DataSource())
const isSelectShemeDone = ref(false)
const headerOptions = reactive([])
const yOptions = reactive([])
const filterOptions = reactive([])

const typeOptions = [{
  label: '等于',
  value: '等于'
}, {
  label: '处于',
  value: '处于'
}]

const timeUnitOptions = [{
  label: '统计某月',
  value: '天'
}, {
  label: '统计某年',
  value: '月'
}]

const _props = defineProps({
  // 是否展示弹框
  isShow: {
    type: Boolean,
    default: false
  },
  default: {
    type: Object,
    default: () => {
      return {}
    }
  }
})

const isChange = ref(false)
const _emits = defineEmits(['closeDialog', 'submit', 'saveReportJSON'])
const { proxy } = getCurrentInstance()

const closeDialog = () => _emits('closeDialog') // 关闭弹窗事件

watch(() => _props.isShow, () => {
  if (_props.isShow){
    headerOptions.length = 0
    yOptions.length = 0
    filterOptions.length = 0
    formData.value = { ..._props.default }
    isChange.value = !isChange.value
    if (formData.value.sourceValue){
      isSelectShemeDone.value = true
      if (formData.value.sourceInterfaceName !== 'collectResult'){
        dataSource.value.getSchemeHeader({
          schemeCode: formData.value.sourceValue
        }).then(res => {
          initSelectHeader(res)
          selectTableData.isShow = false
        })
      }
      else {
        dataSource.value.getCollectHeader({
          collectSchemeCode: formData.value.sourceValue
        }).then(res => {
          initSelectHeader(res)
          selectTableData.isShow = false
        })
      }

    }
  }
}, { immediate: true })

const showDialog = computed({
  get(){
    return _props.isShow
  },
  set(){
    //null
  }
}) // 开启弹窗事件

const formData = ref({
  xAxis: {
    fieldName: '',
    resultType: '',
    value: ''
  },
  yAxis: {
    list: [],
    groupBy: ''
  },
  filterCriteria: []
})

const deleteCondition = (idx) => {
  formData.value.filterCriteria.splice(idx, 1)
}

const addCondition = () => {

  formData.value.filterCriteria.push({})
}

const xAxisChange = (val) => {
  formData.value.xAxis.resultType = val.split('-')[2]

  if (formData.value.xAxis.resultType === 'DATE'){
    filterOptions.length = 0
    filterOptions.push(...sourceHeader.filter(item => {
      item.label = item.resultName
      item.value = item.resultName + '-' + item.resultCode + '-' + item.resultType
      return item.resultType !== 'DATE'
    }))
  } else {
    formData.value.year = null
    formData.value.month = null
    formData.value.xaxisIsTime = false
    formData.value.timeUnit = ''
    formData.value.compare.list.length = 0
  }
}

const change = (val, idx) => {
  formData.value.filterCriteria[idx].resultType = val.split('-')[2]
  formData.value.filterCriteria[idx].fieldName = val.split('-')[1]
}

const yAxisChange = (val) => {
  console.log(val)
}

const editControlCommon = reactive({
  isShow: false,
  formInputEl: [{
    title: "报表名称",
    key: 'title',
    element: 'input',
    rules: [{
      required: true,
      message: '该项不能为空',
      trigger: 'blur'
    }]
  }, {
    title: "报表类型",
    key: 'sourceInterfaceName',
    element: 'radio',
    options: [{
      label: '普通账单',
      value: 'billMain'
    }, {
      label: '汇总账单',
      value: 'collectResult'
    }],
    change: (val) => {
      formData.value.sourceInterfaceName = val
      proxy.$refs.dialogForm.updateDialogInput({ sourceValue: `` })
      isSelectShemeDone.value = false
    },
    rules: [{
      required: true,
      message: '该项不能为空',
      trigger: 'blur'
    }]
  }, {
    title: "选择账单",
    key: 'sourceValue',
    element: 'input',
    type: 'dialog',
    rules: [{
      required: true,
      message: '该项不能为空',
      trigger: 'blur'
    }]

  }],

  formSelectEl: [{
    title: "报表类型",
    key: 'chartsType',
    element: 'check',
    options: [{
      label: '折线图 & 柱状图',
      value: '折线图 & 柱状图'
    }, {
      label: '表格',
      value: '表格',
      disabled: true
    }, {
      label: '饼图',
      value: '饼图',
      disabled: true
    }],
    rules: [{
      required: true,
      message: '该项不能为空',
      trigger: 'blur'
    }]
  }],
  formTextAreaEl: [],

  emitOpenDialog: (val) => {
    openSelectDialog(val, formData.value.sourceInterfaceName)
  },
  inputDone: (val) => {
    let params = {

      ...formData.value,
      ...val
    }
    if (!params.id)
      params.id = proxy.$DataTool.randomString(10)
    _emits('saveReportJSON', params)

  }
})

const activeNames = ref(['1', '2', '3', '4'])

const handleChange = (val) => {
  console.log(val)
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

const initSelectHeader = (list) => {
  sourceHeader = list
  headerOptions.length = 0
  yOptions.length = 0
  filterOptions.length = 0
  list.forEach(item => {
    item.label = item.resultName
    item.value = item.resultName + '-' + item.resultCode + '-' + item.resultType
    filterOptions.push(item)
    if (item.resultType === 'STRING' || item.resultType === 'DATE'){
      headerOptions.push(item)
    }
    if (item.resultType === 'NUM'){
      yOptions.push(item)
    }
  })
}
let sourceHeader = []

const openSelectDialog = async (val, collectObject) => {
  selectTableData.useChoose = true
  switch (val){
  case 'sourceValue': {
    selectTableData.searchPlaceholde = '输入账单计划名称进行搜索'
    selectTableData.chooseRow = (val) => {
      if (val.children) return

      proxy.$refs.dialogForm.updateDialogInput({ sourceValue: val.schemeCode ?? val.collectSchemeCode, sourceValueName: val.schemeName ?? val.collectSchemeName })
      isSelectShemeDone.value = true
      if (collectObject !== 'collectResult'){
        dataSource.value.getSchemeHeader({
          schemeCode: val.schemeCode
        }).then(res => {
          initSelectHeader(res)
          selectTableData.isShow = false
        })

      }
      else {
        dataSource.value.getCollectHeader({
          collectSchemeCode: val.collectSchemeCode
        }).then(res => {
          initSelectHeader(res)
          selectTableData.isShow = false
        })
      }

    }
    selectTableData.doSearch = (val) => {
      selectTableData.dataSource.searchData = { }
      if (val) selectTableData.dataSource.searchData.name = val
      selectTableData.dataSource.initData()
    }
    selectTableData.dataSource = new DataSource({
      selectUri: collectObject === 'collectResult' ? `/colorful-fog/collectSchemeMain/select` : `/colorful-fog/schemeMain/selectMenu`,
      tableHeader: collectObject === 'collectResult' ? [{
        label: "账单名称",
        prop: "collectSchemeName",
        width: 200
      }, {
        label: "账单code",
        prop: "collectSchemeCode",
        width: 200
      }, {
        label: "账单描述",
        prop: "collectSchemeDesc",
        width: 200
      }] : [{
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
    if (collectObject !== 'collectResult')
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

  default: {
    return
  }
  }
}

const yearOptions = reactive([{
  label: '2024',
  value: 2024
}, {
  label: '2025',
  value: 2025
}, {
  label: '2026',
  value: 2026
}])
const monthOptions = reactive([
  {
    label: '一月',
    value: 1
  }, {
    label: '二月',
    value: 2
  }, {
    label: '三月',
    value: 3
  }, {
    label: '四月',
    value: 4
  }, {
    label: '五月',
    value: 5
  }, {
    label: '六月',
    value: 6
  }, {
    label: '七月',
    value: 7
  }, {
    label: '八月',
    value: 8
  }, {
    label: '九月',
    value: 9
  }, {
    label: '十月',
    value: 10
  }, {
    label: '十一月',
    value: 11
  }, {
    label: '十二月',
    value: 12
  }
])

const timeUnitChange = (val) => {
  if (val !== '天') delete formData.value.month
}

const timeChange = (val, item) => {
  item.value = [Date.parse(val[0]), Date.parse(val[1])]

}
</script>

<style lang="less" scoped>
.demo-collapse{
  padding: 0 20px;
  box-sizing: border-box;
  margin-bottom: 20px;
  border: 1px dashed #88bfc9;
}
.title-collapse{
  display: inline-block;
  margin-bottom: 10px ;
}
.tag{
  display: inline-flex;
  padding: 0 16px;
  justify-content: space-between;
  align-items:center;
  height: 30px;
  background: linear-gradient(to right,#e2b053, #6fe7e7);
  border-radius: 6px;
  color: #fcfcfc;
}

.filterCriteria{
  margin-bottom: 10px;
}
</style>