<template>
  <div>
    <div class="header">
      <el-page-header @back="goBack">
        <template #content>
          <span class="route"> {{ title }} </span>
          <span class="route" style="color:#999">  {{ active }} </span>
        </template>
      </el-page-header>
    </div>
    <div class="detail">
      <div class="table" style="width:100%;">
        <el-card
          class="box-card" shadow="hover"
        >
          <template #header>
            <div class="card-header">
              <span style="width:120px">定义分类的名词</span>
              <el-dropdown
                trigger="click" style="color: #FFFFFF;"
                @command="addRow"
              >
                <el-button
                  size="small"
                  class="handle-btn"
                  color="#4a78bd"
                  style="color: #666"
                  plain
                >
                  新增分类方案
                  <el-icon class="el-icon--right">
                    <arrow-down />
                  </el-icon>
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item
                      v-for="item in addRuleList" :key="item.value"
                      :command="item.value"
                    >
                      {{ item.label }}
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
              <el-dropdown

                ref="step00"
                trigger="click" style="color: #FFFFFF;"
                @command="selectSchemeName"
              >
                <el-button
                  size="small"
                  class="handle-btn ml10"
                  color="#4a78bd"
                  style="color: #666"
                  plain
                >
                  {{ selectedSchemeName ?? '选择已有方案' }}
                  <el-icon class="el-icon--right">
                    <arrow-down />
                  </el-icon>
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item
                      v-for="item in schemeList" :key="item.schemeCode"
                      :command="item.schemeName + ' && ' + item.schemeCode"
                    >
                      {{ item.schemeName }}
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
              <el-button
                size="small"
                class="ml10"
                type="warning"
                style="color: #666"
                plain
                @click="refreshSchemeList"
              >
                重置刷新
              </el-button>
            </div>
          </template>
          <div>
            <dataTable
              use-edit
              use-delete
              :use-check-box="false"
              :data-source="dataSourceRule"
              :loading="loading&&!editControlCommon.isShow&&!selectTableData.isShow"
              @handle-edit="handleEdit"
              @handle-delete="handleDelete"
            >
              <template #customColumnEnd>
                <el-table-column
                  label="需分类的数据"
                  prop="orderFieldName"
                  align="center"
                  show-overflow-tooltip
                >
                  <template #default="scope">
                    <el-link v-if="scope.row.dependType==='SORT'" ref="step1">
                      {{ `【${scope.row.orderName ?? scope.row.schemeName}】` + '中的' + `【${scope.row.orderFieldName}】` + `(${scope.row.orderField})` }}
                    </el-link>
                    <el-link v-if="scope.row.dependType==='RANGE'" ref="step01">
                      {{ `【${scope.row.orderName ?? scope.row.schemeName}】` + '中的' + `【${scope.row.orderFieldName}】` + `(${scope.row.orderField})` }}
                    </el-link>
                    <el-tag
                      v-if="scope.row.dependType==='DEPEND'"
                      ref="step2"
                      class="show-detail"
                      @click="showSecondDetail(scope.row.secondDependCode,scope)"
                    >
                      点击查看类别组具体有哪些类别
                    </el-tag>
                  </template>
                </el-table-column>
              </template>
            </dataTable>
            <div v-if="selectedSchemeName" class="append">
              <el-icon
                class="append-options" title="为此方案添加一个分类"
                @click="addRow(route.query.dependType)"
              >
                <CirclePlus />
              </el-icon>
            </div>
          </div>
        </el-card>

        <el-card
          v-if="dataSourceRule.tableData.length !== 0" class="box-card mt20"
          shadow="hover"
        >
          <template #header>
            <div class="card-header">
              <span>分类规则说明</span>
            </div>
          </template>
          <div style="padding: 0px 0px 0px 10px;width:100%">
            <el-descriptions
              title=""
              :column="2"
            >
              <el-descriptions-item label="你需要输入">
                <el-tag type="danger">
                  {{
                    route.query.dependType === 'SORT' ? `某个 【名词组合】，并且你希望这个 【名词组合】 是属于什么类别` :
                    `某个范围的【起始值】和【结束值】，并且你希望它属于什么【类别】`
                  }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="会产生的结果">
                <el-tag type="danger">
                  未来会按照分类区分计费方式
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item v-if="route.query.dependType === 'SORT' " label="名词组合规则">
                <el-tag type="danger">
                  每个名词请用 【-】 连接
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="如果你不会操作，请看">
                <el-button
                  plain
                  type="warning" size="small"
                  @click="handleShowTeach"
                >
                  操作指引
                </el-button>
              </el-descriptions-item>
            </el-descriptions>
          </div>
        </el-card>
        <div class="detail-table">
          <el-card
            v-if="dataSourceRule.tableData.length !== 0" class="box-card mt20"
            shadow="hover"
          >
            <template #header>
              <div class="card-header">
                <span>将名词分类</span>
              </div>
              <div class="card-header">
                <span>①</span>
                <span class="tip ml10">
                  <span class="mr10">"其他!":</span>  需要处理分类表中的其他情况，作为兜底或者某些特殊的分类时需要用到
                </span>
              </div>
              <div class="card-header">
                <span>②</span>
                <span class="tip ml10">
                  <span class="mr10">"$key":</span> 分类结果填的是"$key"时，转换的结果将会和传入的key相等，可用于对字段的拼接
                </span>
              </div>
            </template>
            <div>
              <div class="data-filter">
                <data-filter
                  :filter-items="getDependDataHeader()"
                  @search="doSearch"
                />
              </div>
              <div class="extend-handle">
                <div class="extend-handle-left">
                  <el-button
                    size="small"
                    class="handle-btn"
                    color="#4a78bd"
                    style="color: #666"
                    @click="addDependData"
                  >
                    新增一条数据
                  </el-button>
                  <el-button
                    ref="step5"
                    size="small"
                    class="handle-btn "
                    color="#4a78bd"
                    style="color: #666"
                    @click="saveAllData"
                  >
                    全部保存
                  </el-button>
                </div>
                <div class="extend-handle-right pr10 flex-center">
                  <el-button

                    ref="step6" color="#5f6368" style="color: #FFFFFF"
                    size="small"
                    icon="Download"
                    class="mr10" circle
                    @click="exportExmp"
                  />
                  <el-upload
                    ref="step7"
                    class="inline-block"
                    accept=".xlsx, .xls"
                    :on-change="fileChoice"
                    :show-file-list="false"
                    :auto-upload="false"
                  >
                    <el-button
                      size="small"
                      class="handle-btn"
                      color="#4a78bd"
                      style="color: #666"
                    >
                      导入数据
                    </el-button>
                  </el-upload>
                  <el-button
                    size="small"
                    plain
                    class="ml12"
                    type="danger"
                    @click="deleteDependData"
                  >
                    删除
                  </el-button>
                </div>
              </div>
              <dataTable
                :use-edit="false"
                :use-delete="true"
                use-check-box
                :data-source="dataSource"
                :loading="loading&&!editControlCommon.isShow&&!selectTableData.isShow"
                use-pagination
                @handle-delete="handleDeleteData"
              >
                <template #customColumn>
                  <el-table-column
                    v-if="route.query.dependType==='SORT'"
                    label="名词组合"

                    prop="key"
                    align="center"
                    show-overflow-tooltip
                  >
                    <template #header>
                      <el-space ref="step3">
                        名词组合
                      </el-space>
                    </template>
                    <template #default="scope">
                      <el-input
                        v-model="scope.row.key" :placeholder="`请输入名词组合`"
                        clearable size="small"
                      />
                    </template>
                  </el-table-column>
                  <el-table-column
                    v-if="route.query.dependType==='RANGE'"
                    :label="'起始值,结束值'"

                    prop="rangeStart"
                    align="center"
                    show-overflow-tooltip
                  >
                    <template #header>
                      <el-space ref="step02">
                        起始值,结束值（开区间和闭区间，请用【,(英文逗号)】隔开）
                      </el-space>
                    </template>
                    <template #default="scope">
                      <el-input
                        v-model="scope.row.rangeStart" :placeholder="`请输入起始值-结束值`"
                        clearable size="small"
                      />
                    </template>
                  </el-table-column>

                  <el-table-column
                    :label="'所属类别 / 具体值 / 具体系数'"

                    prop="value"
                    align="center"
                    show-overflow-tooltip
                  >
                    <template #header>
                      <el-space ref="step4">
                        所属类别 / 具体值 / 具体系数
                      </el-space>
                    </template>
                    <template #default="scope">
                      <el-tag
                        type="success" size="small"
                        class="mr10"
                      >
                        属于
                      </el-tag>
                      <el-input
                        v-model="scope.row.value"
                        style="width:90%" :placeholder="`所属类别 / 具体值 / 具体系数`"
                        clearable size="small"
                      />
                    </template>
                  </el-table-column>
                </template>
              </dataTable>
            </div>
          </el-card>
        </div>
      </div>
    </div>
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
      @close-dialog="editControlCommon.isShow=false"
      @emit-open-dialog="editControlCommon.emitOpenDialog"
      @input-done="editControlCommon.inputDone"
    >
      <template #customOperation>
        <div class="inline-block ml10" style="font-weight:500;font-size:14px">
          <span style="color:red"> 取值来源：</span>
          <el-select
            v-model="formData.getDataFrom"
            style="width:220px"
            :placeholder="`请选择系数类型`"
            :disabled="true"
            @change="typeChange"
          >
            <el-option
              v-for="option in typeOptions"
              :key="option.label"
              :label="option.label"
              :value="option.value"
            />
          </el-select>
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

    <el-tour v-model="showTeach" :close-on-press-escape="false">
      <el-tour-step
        v-if="step1&&step1.$el" :target="step1?.$el"
        title="查看需要分类的数据"
      >
        <div>
          单一名词直接按照描述填写数据
        </div>
      </el-tour-step>
      <el-tour-step
        v-if="step2&&step2.$el" :target="step2?.$el"
        title="查看需要分类的数据"
      >
        <el-image
          src="https://jxwlapp.oss-cn-guangzhou.aliyuncs.com/teachStep2.png"
          :zoom-rate="1.2"
          :max-scale="7"
          :min-scale="0.2"
          :preview-src-list="['https://jxwlapp.oss-cn-guangzhou.aliyuncs.com/teachStep2.png']"
          :initial-index="4"
          fit="cover"
        />
        <div>如果是类别组，选择类别组里面的其中一个</div>
      </el-tour-step>
      <el-tour-step
        title="构造数据" :target="step3?.$el"
        placement="top"
      >
        <div class="mt10">
          1、假设，需要分类的数据是
        </div>
        <div class="mt10">
          <el-tag type="danger">
            【物料代码-产品类别(良品、不良品 二选一)】
          </el-tag>
        </div>
        <div class="mt10">
          2、我们就可以构造出一个分类
        </div>
        <div class="mt10">
          <el-tag type="success">
            JIAXIAN001-良品
          </el-tag>
        </div>
      </el-tour-step>
      <el-tour-step
        title="帮分类设定一个值" :target="step4?.$el"
        placement="top"
      >
        <div class="mt10">
          1、上一步得到一个名词组合
        </div>
        <div class="mt10">
          <el-tag type="danger">
            JIAXIAN001-良品
          </el-tag>
        </div>
        <div class="mt10">
          2、设定 所属类别 / 具体值 / 具体系数：
        </div>
        <div class="mt10">
          <el-tag type="success">
            0.8
          </el-tag>
        </div>
      </el-tour-step>
      <el-tour-step
        title="最后记得点击保存" :target="step5?.$el"
        placement="top"
      >
        <div class="mt10">
          我们得到 【JIAXIAN001-良品】和一个值 0.8，它表示
        </div>
        <div class="mt10">
          <el-link type="danger">
            【JIAXIAN001-良品】会使用0.8这个系数计算金额
          </el-link>
        </div>
      </el-tour-step>
      <el-tour-step
        title="明白上面的意思后，我们也可以" :target="step6?.$el"
        placement="left"
      >
        <el-button
          type="primary" size="small"
          @click="exportExmp"
        >
          下载导入模板
        </el-button>
      </el-tour-step>
      <el-tour-step
        title="填好Excel数据后" :target="step7?.$el"
        placement="top"
      >
        <div class="mt10">
          我们就可以点击导入了
        </div>
      </el-tour-step>
    </el-tour>
    <el-tour v-model="showTeach0" :close-on-press-escape="false">
      <el-tour-step :target="step01?.$el" title="查看需分类的数据是哪个字段">
        <div>
          例如：需分类的字段【体积】
        </div>
      </el-tour-step>
      <el-tour-step
        :target="step02?.$el" title="输入一个起始值和结束值，分为开区间和闭区间"
        placement="top"
      >
        <div>
          <el-tag type="success">
            假设输入 - > (5,10)
          </el-tag>
        </div>
      </el-tour-step>

      <el-tour-step
        :target="step4?.$el" title="帮这个范围设定一个值"
        placement="top"
      >
        <div>
          <el-tag type="success">
            假设输入 - > 大件
          </el-tag>
        </div>
      </el-tour-step>
      <el-tour-step
        :target="step5?.$el" title="最后记得点击保存"
        placement="top"
      >
        <div class="mt10">
          我们得到 【5-10】 和一个值 【大件】 ，它表示
        </div>
        <div class="mt10">
          <el-link type="danger">
            【5-10】这个范围属于 【大件】
          </el-link>
        </div>
      </el-tour-step>
      <el-tour-step
        title="明白上面的意思后，我们也可以" :target="step6?.$el"
        placement="left"
      >
        <el-button
          type="primary" size="small"
          @click="exportExmp"
        >
          下载导入模板
        </el-button>
      </el-tour-step>
      <el-tour-step
        title="填好Excel数据后" :target="step7?.$el"
        placement="top"
      >
        <div class="mt10">
          我们就可以点击导入了
        </div>
      </el-tour-step>
    </el-tour>
    <el-tour v-model="showTeach2" :close-on-press-escape="false">
      <el-tour-step :target="step00?.$el" title="先创建一个分类规则吧！">
        <div>
          选择你想要的分类项
        </div>
      </el-tour-step>
    </el-tour>
  </div>
</template>

<script setup>
import { pbRequest } from "@/applications/sys/public/pbRequest/index"
import selectDialogTable from '@/components/public/selectDialogTable.vue'
import selectDialogForm from '@/components/public/selectDialogForm.vue'
import dataFilter from '@/components/public/dataFilter.vue'
import { ref, onBeforeMount, getCurrentInstance, reactive, watch, h, toRaw } from "vue"
import { useRouter, useRoute } from 'vue-router'
import { DataSource, loading } from '../utils/relationship'
import { getAddRuleList } from './js/relationship'
import dataTable from '@/components/public/dataTable.vue'

const { proxy } = getCurrentInstance()
const router = useRouter()
const route = useRoute()
const title = ref(`首页 / ${route.query.title} / `)
const active = ref(`${route.query.name}`)

const showTeach = ref(false)
const showTeach0 = ref(false)
const showTeach2 = ref(false)

const step1 = ref()
const step2 = ref()
const step3 = ref()
const step4 = ref()
const step5 = ref()
const step6 = ref()
const step7 = ref()

const step01 = ref()
const step02 = ref()
const step03 = ref()

const step00 = ref()
const selectedSchemeName = ref(null)
const dataSourceRule = ref(null)
const dataSource = ref(null)

const typeOptions = [{
  label: '源数据表',
  value: 'TABLE'
}, {
  label: '方案字段',
  value: 'SCHEME'
}]

const goBack = () => {
  proxy.$alert('即将离开当前编辑页，未保存的数据将失效', '提示', {
    confirmButtonText: '确认离开',
    type: 'warning',
    icon: 'InfoFilled',
    callback: (action) => {
      if (action === 'confirm') {
        router.go(-1)
      }
    }
  })

}

onBeforeMount(() => {
  initDataSource()
  addRuleList.push(...getAddRuleList(route.query.dependType))
})

const initDataSource = async () => {
  dataSourceRule.value = new DataSource({
    tableHeader: getDependRuleHeader(),
    selectUri: '/colorful-fog/dependRule/select',
    pageSize: 100
  })
  dataSourceRule.value.searchData = { dependCode: route.query.dependCode }
  dataSourceRule.value.forMatData = forMatData

  dataSourceRule.value.initData = async (context = dataSourceRule.value, tableRef, type) => {
    let { data: { data, code, message } } = await pbRequest.post(`${context.selectUri}?currentPage=${context.currentPage}&pageSize=${context.pageSize}`, context.searchData)
    if (code !== 200) return
    context.total = data.pageInfo.total
    context.tableData.length = 0

    context.tableData.push(...data.pageInfo.list)
    if (schemeList.value.length === 0){
      schemeList.value = data.pageInfo.list.filter((item, idx) => data.pageInfo.list.findIndex(a => a.schemeName === item.schemeName) === idx)
    }
    if (dataSourceRule.value.tableData.length === 0 && schemeList.value.length !== 0){
      schemeList.value = []
      selectSchemeName(null)
    }
    tableRef?.clearSelection()
  }

  dataSource.value = new DataSource({
    selectUri: '/colorful-fog/dependData/select',
    pageSize: 20
  })
  dataSource.value.searchData = { dependCode: route.query.dependCode }
  dataSource.value.forMatData = forMatData

  dataSource.value.initData = async (context = dataSource.value, tableRef) => {
    let { data: { data, code, message } } = await pbRequest.post(`${context.selectUri}?currentPage=${context.currentPage}&pageSize=${context.pageSize}`, context.searchData)
    if (code !== 200) return
    context.total = data.pageInfo.total
    context.tableData.length = 0

    data.pageInfo.list.forEach(item => {
      item.rangeStart = item.rangeStart + ',' + item.rangeEnd
      context.tableData.push(item)
    })
    tableRef?.clearSelection()
  }
}

const refreshSchemeList = () => {
  schemeList.value.length = 0
  selectSchemeName(null)
}

const selectSchemeName = (val) => {
  selectedSchemeName.value = val
  dataSourceRule.value.searchData = { dependCode: route.query.dependCode, schemeName: val?.split(' && ')[0] }
  dataSourceRule.value.initData()
}

const schemeList = ref([])

const handleDeleteData = ({ idx, row }) => {
  if (!row.id){
    dataSource.value.tableData.splice(idx, 1)
    return
  }
  proxy.$alert('是否确认删除该条数据', '提示', {
    type: 'error',
    icon: 'InfoFilled',
    confirmButtonText: '确定',
    callback: (action) => {
      if (action === 'confirm') {
        dataSource.value.deleteDependData([row.id]).then(res => {
          if (res.code === 200){
            proxy.$message.success('删除成功')
            dataSource.value.initData(this, proxy.$refs.table)
          }
        })
      }
    }
  })
  return

}

const handleDelete = ({ idx, row }) => {

  proxy.$alert('是否确认删除该规则？', '提示', {
    type: 'error',
    icon: 'InfoFilled',
    confirmButtonText: '确定',
    callback: (action) => {
      if (action === 'confirm') {
        dataSource.value.deleteDependRule([row.id]).then(res => {
          if (res.code === 200){
            proxy.$alert('删除成功', '提示', {
              type: 'success',
              icon: 'InfoFilled',
              showCancelButton: false,
              callback: () => {
                schemeList.value = []
                dataSourceRule.value.initData(this, proxy.$refs.table)
              }
            })
          }
        })
      }
    }
  })
}

let dependType = {
  SORT: '单一名词（按名词分类）',
  RANGE: '单一名词（按范围分类）',
  DEPEND: '类别组',
  OTHER: '其他'
}

const forMatData = (row, column) => {
  if (column.property === 'dependType'){
    return dependType[row[column.property]]
  }
  if (column.property === 'orderFieldName'){
    if (row.secondDependCode) return h('span', { ref: el => step1.value = el, class: 'show-detail', onClick: showSecondDetail.bind(this, row.secondDependCode) }, '点击查看具体分类数据')
    return `【${row.orderName}】` + '中的' + `【${row[column.property]}】` + `(${row.orderField})`
  }
  return row[column.property] ? row[column.property] : '-'
}

const addRuleList = reactive([])

const getDependRuleHeader = () => {

  return []

}

const getDependDataHeader = () => {
  if (route.query.dependType === 'SORT') {
    return [{
      title: "名词",
      key: 'key',
      dataKey: "key",
      type: 'text'
    },
    {
      title: "取值",
      key: 'value',
      dataKey: "value",
      type: 'text'
    }]
  }
  else {
    return [{
      title: "范围",
      key: 'range',
      dataKey: "range",
      type: 'text'
    }, {
      title: "取值",
      key: 'value',
      dataKey: "value",
      type: 'text'
    }]
  }

}

const doSearch = (val) => {

  const { range, value, key } = val

  let data = { key, dependCode: route.query.dependCode, value, range }

  dataSource.value.search(data, this, proxy.$refs.table)

}

const saveAllData = () => {
  let paramsAdd = dataSource.value.tableData.filter(item => {
    if (!item.id){
      if (item.rangeStart){
        item.rangeEnd = item.rangeStart.split(',')[1]
        item.rangeStart = item.rangeStart.split(',')[0]

      }
    }
    return !item.id
  })
  let paramsUpdate = dataSource.value.tableData.filter(item => {
    if (item.id){

      if (item.rangeStart){
        item.rangeEnd = item.rangeStart.split(',')[1]
        item.rangeStart = item.rangeStart.split(',')[0]

      }
    }

    return item.id
  })

  if (paramsAdd.length !== 0){
    dataSource.value.addDependDataBatch(paramsAdd).then(res => {
      if (res.code === 200){
        proxy.$notify({
          title: '执行了新增操作',
          message: '新增保存成功',
          type: 'success'
        })
        dataSource.value.initData(this, proxy.$refs.table)
      }
    })
  }
  if (paramsUpdate.length !== 0){
    dataSource.value.updateDependData(paramsUpdate).then(res => {
      if (res.code === 200){
        proxy.$notify({
          title: '执行了更新操作',
          message: '更新保存成功',
          type: 'success'
        })
        dataSource.value.initData(this, proxy.$refs.table)
      }
    })
  }

}

const deleteDependData = () => {
  if (dataSource.value.selections.length === 0){
    proxy.$message.error('请选择数据')
    return
  }
  proxy.$alert('是否确认删除？', '提示', {
    type: 'error',
    icon: 'InfoFilled',
    confirmButtonText: '确定',
    callback: (action) => {
      if (action === 'confirm') {
        let params = dataSource.value.selections.map(item => item.id)
        dataSource.value.deleteDependData(params).then(res => {
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

const addDependData = () => {
  dataSource.value.tableData.unshift({ dependCode: route.query.dependCode })
}
const disabledSelectDataForm = ref(false)

const addRow = (val) => {
  disabledSelectDataForm.value = false
  formData.value = { dependCode: route.query.dependCode, dependType: val, getDataFrom: 'SCHEME' }

  if (val === 'SORT' || val === 'RANGE') {
    editControlCommon.formInputEl = [{
      title: "选择一个方案",
      key: 'schemeName',
      element: 'input',
      type: 'dialog'
    }, {
      title: "选择方案中的字段",
      key: 'orderFieldNameScheme',
      element: 'input',
      type: 'dialog'
    }]
  }

  tempFilter = toRaw(formData.value)
  editControlCommon.isShow = true
  if (selectedSchemeName.value){
    formData.value.schemeCode = selectedSchemeName.value.split(' && ')[1]
    formData.value.schemeName = selectedSchemeName.value.split(' && ')[0]
    openSelectDialog(`orderFieldNameScheme`)

  }
}

const handleEdit = ({ idx, row }) => {
  if (row.dependType === 'SORT' || row.dependType === 'RANGE') {
    if (row.getDataFrom === 'Table'){
      editControlCommon.formInputEl = [{
        title: "选择单据表",
        key: 'orderName',
        element: 'input',
        type: 'dialog'
      }, {
        title: "选择表中某个字段",
        key: 'orderFieldName',
        element: 'input',
        type: 'dialog'
      }]
    } else {
      editControlCommon.formInputEl = [{
        title: "选择一个方案",
        key: 'schemeName',
        element: 'input',
        type: 'dialog'
      }, {
        title: "选择方案中的字段",
        key: 'orderFieldNameScheme',
        element: 'input',
        type: 'dialog'
      }]
    }

  }
  else {
    editControlCommon.formInputEl = [{
      title: "要依赖哪个分类",
      key: 'secondDependCode',
      element: 'input',
      type: 'dialog'
    }]

  }
  formData.value = row
  formData.value.orderFieldNameScheme = formData.value.orderFieldName
  tempFilter = toRaw(formData.value)
  editControlCommon.isShow = true
}
const formData = ref({})

const editControlCommon = reactive({
  isShow: false,
  formInputEl: [],
  formSelectEl: [],
  formTextAreaEl: [],
  formTimeAndNumber: [],
  emitOpenDialog: (val) => {
    openSelectDialog(val)
  },
  inputDone: (val) => {
    if (val.id){
      dataSource.value.updateDependRule([val]).then((res) => {
        if (res.code === 200){
          editControlCommon.isShow = false
          proxy.$message.success('更新成功')
          dataSourceRule.value.initData(this, proxy.$refs.table)
        }
      })
    } else {
      dataSource.value.addDependRule([val]).then((res) => {
        if (res.code === 200){
          editControlCommon.isShow = false
          proxy.$message.success('新增成功')
          dataSourceRule.value.initData(this, proxy.$refs.table)
        }
      })
    }

  }
})

let tempFilter = {}

const openSelectDialog = async (val) => {
  selectTableData.useChoose = true
  switch (val){
  case 'orderName': {
    selectTableData.searchPlaceholde = '输入单据名称进行搜索'
    selectTableData.chooseRow = (val) => {
      tempFilter.orderTable = val.orderTable
      proxy.$refs.dialogForm.updateDialogInput({ orderName: val.orderName, orderTable: val.orderTable })

      selectTableData.isShow = false
    }
    selectTableData.doSearch = (val) => {
      selectTableData.dataSource.searchData = { }
      if (val) selectTableData.dataSource.searchData.orderName = val
      selectTableData.dataSource.initData()
    }
    selectTableData.dataSource = new DataSource({
      selectUri: `/colorful-fog/orderTable/select`,
      tableHeader: [{
        label: "单据名称",
        prop: "orderName",
        width: 200
      }, {
        label: "单据关联表",
        prop: "orderTable",
        width: 200
      }, {
        label: "单据关联表名称",
        prop: "orderTableName",
        width: 200
      }, {
        label: "类型",
        prop: "type",
        width: 200
      }],
      pageSize: 10
    })

    selectTableData.dataSource.forMatData = (row, column) => {
      if (column.property === 'type') return row[column.property] === 'MAIN' ? '主表' : '明细'
      else return row[column.property] ?? '-'
    }
    await selectTableData.dataSource.initData()
    selectTableData.isShow = true
    return
  }
  case 'orderFieldName': {
    if (!formData.value.orderTable) {
      proxy.$message.error('请先选择选择名词所在表')
      return
    }
    selectTableData.searchPlaceholde = '输入字段名进行搜索'
    selectTableData.chooseRow = (val) => {
      proxy.$refs.dialogForm.updateDialogInput({ orderFieldName: val.fieldName, orderField: val.field })
      selectTableData.isShow = false
    }
    selectTableData.doSearch = (val) => {
      selectTableData.dataSource.searchData = { tableName: tempFilter.orderTable }
      if (val) selectTableData.dataSource.searchData.fieldName = val
      selectTableData.dataSource.initData()
    }
    selectTableData.dataSource = new DataSource({
      selectUri: '/colorful-fog/tableFiled/select',
      tableHeader: [{
        label: "字段名",
        prop: 'fieldName',
        width: 200
      }, {
        label: "字段key",
        prop: 'field',
        width: 200
      }],
      pageSize: 10
    })
    selectTableData.dataSource.forMatData = (row, column) => {
      return row[column.property] ?? '-'
    }

    selectTableData.dataSource.searchData = { tableName: tempFilter.orderTable }
    await selectTableData.dataSource.initData()
    selectTableData.isShow = true
    return
  }
  case 'secondDependCode': {
    selectTableData.searchPlaceholde = '输入分类关系名称搜索'
    selectTableData.chooseRow = (val) => {
      proxy.$refs.dialogForm.updateDialogInput({ secondDependCode: val.dependCode })
      selectTableData.isShow = false
    }
    selectTableData.doSearch = (val) => {
      selectTableData.dataSource.searchData = { }
      if (val) selectTableData.dataSource.searchData.name = val
      selectTableData.dataSource.initData()
    }
    selectTableData.dataSource = new DataSource({
      selectUri: '/colorful-fog/dependMain/list',
      tableHeader: [{
        label: "依赖关系名称",
        prop: 'name',
        width: 200
      }, {
        label: "依赖Code",
        prop: 'dependCode',
        width: 200
      }],
      pageSize: 10
    })
    selectTableData.dataSource.forMatData = (row, column) => {
      return row[column.property] ?? '-'
    }
    selectTableData.dataSource.searchData = { }
    await selectTableData.dataSource.initData()
    selectTableData.isShow = true
    return
  }
  case 'schemeName': {
    selectTableData.searchPlaceholde = '输入方案名称搜索'
    selectTableData.chooseRow = (val) => {
      if (val.children) return
      tempFilter.schemeCode = val.schemeCode
      tempFilter.schemeName = val.schemeName
      proxy.$refs.dialogForm.updateDialogInput({ schemeName: val.name, schemeCode: val.schemeCode })

      selectTableData.isShow = false
    }
    selectTableData.doSearch = (val) => {
      selectTableData.dataSource.searchData = { }
      if (val) selectTableData.dataSource.searchData.name = val
      selectTableData.dataSource.initData()
    }
    selectTableData.dataSource = new DataSource({
      selectUri: '/colorful-fog/schemeMain/selectMenu',
      tableHeader: [{
        label: "方案名称",
        prop: 'name',
        width: 200
      }, {
        label: "方案Code",
        prop: 'schemeCode',
        width: 200
      }],
      pageSize: 10
    })
    selectTableData.dataSource.forMatData = (row, column) => {
      return row[column.property] ?? '-'
    }
    selectTableData.dataSource.searchData = { }
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

  case 'orderFieldNameScheme': {
    selectTableData.searchPlaceholde = '输入字段名称搜索'
    selectTableData.chooseRow = (val) => {
      proxy.$refs.dialogForm.updateDialogInput({ orderFieldName: val.resultName, orderField: val.resultCode, orderFieldNameScheme: val.resultName })
      selectTableData.isShow = false

    }
    selectTableData.doSearch = (val) => {
      selectTableData.dataSource.searchData = { schemeCode: tempFilter.schemeCode }
      if (val) selectTableData.dataSource.searchData.name = val
      selectTableData.dataSource.initData()
    }
    selectTableData.dataSource = new DataSource({
      selectUri: '/colorful-fog/schemeDetail/select',
      tableHeader: [{
        label: "字段名称",
        prop: 'resultName',
        width: 200
      }, {
        label: "字段Code",
        prop: 'resultCode',
        width: 200
      }],
      pageSize: 10
    })
    selectTableData.dataSource.forMatData = (row, column) => {
      return row[column.property] ?? '-'
    }
    selectTableData.dataSource.searchData = { schemeCode: tempFilter.schemeCode }
    await selectTableData.dataSource.initData()
    selectTableData.isShow = true
    return
  }

  default: {
    selectTableData.useChoose = false
    selectTableData.searchPlaceholde = '输入类别名称搜索'
    selectTableData.chooseRow = (val) => {
      proxy.$refs.dialogForm.updateDialogInput({ secondDependCode: val.dependCode })
      selectTableData.isShow = false
    }
    selectTableData.doSearch = (val) => {
      selectTableData.dataSource.searchData = { }
      if (val) selectTableData.dataSource.searchData.name = val
      selectTableData.dataSource.initData()
    }
    selectTableData.dataSource = new DataSource({
      selectUri: '/colorful-fog/dependData/select',
      tableHeader: [{
        label: "分类的名词",
        prop: 'key',
        width: 200
      }, {
        label: "开始值",
        prop: 'rangeStart',
        width: 200
      }, {
        label: "结束值",
        prop: 'rangeEnd',
        width: 200
      }, {
        label: "分类",
        prop: 'value',
        width: 200
      }],
      pageSize: 10
    })
    selectTableData.dataSource.forMatData = (row, column) => {
      return row[column.property] ?? '-'
    }
    selectTableData.dataSource.searchData = { dependCode: val }
    await selectTableData.dataSource.initData()
    selectTableData.isShow = true
    return

  }
  }
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
const handleXlsxData = (file) => {
  if (route.query.dependType === 'SORT'){
    return file.map((item) => {
      const keys = Object.keys(item).slice(0, -1)
      const key = keys.reduce((acc, val, index) => {
        return acc + (index > 0 ? '[-]' : '') + item[val]
      }, '')
      return {
        key,
        value: item['转换结果值'],
        dependCode: route.query.dependCode
      }
    })
  } else {
    return file.map((item) => {
      return {
        rangeStart: item['起始值,结束值（开区间和闭区间，请用【,(英文逗号)】隔开）'].split(','[0])[0],
        rangeEnd: item['起始值,结束值（开区间和闭区间，请用【,(英文逗号)】隔开）'].split(','[0])[1],
        key: item['起始值,结束值（开区间和闭区间，请用【,(英文逗号)】隔开）'],
        value: item['转换结果值'],
        dependCode: route.query.dependCode
      }
    })

  }

}

const fileChoice = async (file) => {
  let dataTool = new proxy.$DataTool()
  let xlsxData = await dataTool.xlsx2DataObject(file.raw)
  let data = handleXlsxData(xlsxData)
  proxy.$alert('是否确认导入？', '提示', {
    type: 'info',
    icon: 'InfoFilled',
    confirmButtonText: '确定',
    callback: (action) => {
      if (action === 'confirm') {
        dataSource.value.addDependDataBatch(data).then(res => {
          if (res.code === 200){
            proxy.$alert('导入成功', '提示', {
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

const exportExmp = () => {
  if (route.query.dependType === 'SORT'){
    window.location = 'https://jxwlapp.oss-cn-guangzhou.aliyuncs.com/excelTemplate/名词分类导入模板.xlsx?download'
  }
  if (route.query.dependType === 'RANGE'){
    window.location = 'https://jxwlapp.oss-cn-guangzhou.aliyuncs.com/excelTemplate/范围分类导入模板.xlsx?download'
  }

}

const showSecondDetail = (code, scope) => {
  openSelectDialog(code)
}

const handleShowTeach = () => {
  if (dataSourceRule.value.tableData.length === 0){
    proxy.$message.error('先添加一条规则吧')
    return
  }
  if (route.query.dependType === 'RANGE') showTeach0.value = true
  else showTeach.value = true
}

const typeChange = (val) => {
  formData.value.getDataFrom = val
  proxy.$refs.dialogForm.updateDialogInput({ getDataFrom: val })
  if (val !== 'SCHEME'){
    editControlCommon.formInputEl = [{
      title: "选择分类项所在表",
      key: 'orderName',
      element: 'input',
      type: 'dialog'
    }, {
      title: "选择表中某个字段",
      key: 'orderFieldName',
      element: 'input',
      type: 'dialog'
    }]
  }
}
</script>

<style lang="less" scoped>
.detail{
  margin-top: 6px;
  font-size: 14px;
  padding: 10px 20px;
  display: flex;
}
.header{
  padding: 20px;
  background: #fff;
}
.route{
  font-size: 14px;
  font-weight: 500;
  color: #252525;
  letter-spacing: 1px;
  cursor: default;
}
.table{

  overflow-x: hidden;
  margin-top: 10px;
}
:deep(.is-active){
  background: #ecf5ff;
}
.demo-tabs{
  width: 150px;
}

:deep(.show-detail){
  font-size: 12px;
  color:#22a6f2;
  cursor: pointer;
}

.card-header{
  display: flex;
  align-items: center;
}

.tip{
  color:#ff0000;
  font-size: 12px;
}

.detail-table{
  :deep(.el-card__body){
    padding-top:0;
  }
}
.append{
  margin: 2px auto;
  width: 97%;
  background: #ffffff;
  padding: 8px 14px;
  font-size: 20px;
  color: #81868f;
}
.append-options{
  cursor: pointer;
}
</style>