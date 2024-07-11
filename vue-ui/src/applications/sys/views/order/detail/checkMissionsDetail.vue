<template>
  <div>
    <div class="header">
      <el-page-header @back="goBack">
        <template #content>
          <span class="route"> {{ title }} </span>
          <span class="route" style="color:#999">  {{ active }} </span>
          <el-button
            :disabled="formData.processStatus&&formData.processStatus!=='inventory_create'"
            type="warning" class="ml20"
            size="small"
            @click="editControlCommon.inputDone(null)"
          >
            保存所有
          </el-button>
          <el-button
            :disabled="formData.processStatus!=='inventory_create'"
            type="primary"
            size="small"
            @click="updateType"
          >
            修改为 {{ formData.isOpenCheck===1 ? '暗盘':'明盘' }}
          </el-button>
          <el-button
            type="success"
            size="small"
            @click="submit"
          >
            递交状态
          </el-button>
          <el-button
            type="primary"
            size="small"
            @click="collect"
          >
            一键汇总
          </el-button>

          <!-- <el-button
            v-if="formData.processStatus==='inventory_check'||formData.processStatus==='inventory_ing' "
            type="danger"

            size="small"
            @click="rollBack"
          >
            回退
          </el-button> -->
        </template>
      </el-page-header>
    </div>
    <div class="detail-content">
      <el-card
        v-loading="loading&&!selectTableData.isShow" shadow="always"
        class="card mb20"
      >
        <template #header>
          <div class="card-header">
            <span>基础信息</span>
            <span style="font-size:12px;color:#ff3846" class="ml20">编辑完请记得保存</span>
            <span class="ml12">当前状态：</span>
            <el-tag style="font-size:12px;color:#61afff">
              {{ processStatus[formData.processStatus]??'制单' }}
            </el-tag>
            <span v-if="formData.processStatus" class="ml12">盈亏总数：</span>

            <el-tag v-if="formData.processStatus" :type="!formData.profitAndLossQuantitySum===0?'warning':formData.profitAndLossQuantitySum>0?'primary':'danger'">
              {{ !formData.profitAndLossQuantitySum?'平':formData.profitAndLossQuantitySum > 0 ?'盈':'亏' }}
            </el-tag>
            <el-tag
              v-if="formData.processStatus" :type="formData.profitAndLossQuantity===0?'warning':formData.profitAndLossQuantity>0?'primary':'danger'"
              class="ml10"
            >
              {{ (formData.profitAndLossQuantitySum) }}
            </el-tag>
          </div>
        </template>
        <selectDialogForm
          ref="dialogForm"
          :change-data="editControlCommon.changeData"
          :use-dialog="false"
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
        />
      </el-card>
      <el-card
        shadow="always"
        class="card mt20"
      >
        <template #header>
          <div class="card-header">
            <span>盘点人员</span>

            <span class="route ml10" style="color:#24acf2">  总数：{{ selectUserList?.length }} </span>
          </div>
        </template>

        <div>
          <el-select
            v-model="selectUserList"
            multiple
            placeholder="选择盘点人员"
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
                选择所有人员
              </el-checkbox>
            </template>
            <el-option
              v-for="item in userList"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </div>
      </el-card>

      <el-card
        shadow="always"
        class="card mt20"
      >
        <template #header>
          <div class="card-header">
            <span>盘点产品列表 - 按 {{ formData.inventoryType==='CP'?'产品':'仓位' }} 盘</span>
            <el-button
              type="primary" class="ml20"
              size="small"
              @click="openSelectDialog(formData.inventoryType)"
            >
              添加
            </el-button>

            <el-button
              type="warning"
              link
            >
              上一页
            </el-button>
            <el-button
              type="warning"
              link
            >
              下一页
            </el-button>
            <span class="route ml10" style="color:#24acf2">  总数：{{ listDataSource.tableData?.length }} </span>
          </div>
        </template>

        <div
          v-if="listDataSource.tableData.length===0" class="flex-center"
          style="color:#999;font-size:12px"
        >
          暂 无 盘 点 产 品
        </div>
        <div v-else>
          <el-table
            :data="listDataSource.tableData" style="width: 100%"
            border
            :header-cell-style="{ background: '#eeeeee', color: '#606266'}"
            stripe
          >
            <el-table-column
              label="账面数量"
              prop="bookQuantity"
              :width="200"
            >
              <template #default="scope">
                <el-input-number
                  v-model="scope.row.bookQuantity" size="small"
                  :disabled="true"
                  :min="1"
                />
              </template>
            </el-table-column>
            <el-table-column
              label="实盘数量"
              prop="firmOfferQuantity"
              :width="200"
            >
              <template #default="scope">
                <el-input-number
                  v-model="scope.row.firmOfferQuantity" size="small"
                  :disabled="formData.processStatus!=='6'"
                  :min="0"
                  @change="checkChange($event,scope.row)"
                />
              </template>
            </el-table-column>
            <el-table-column
              v-if="formData.processStatus&&formData.processStatus!=='inventory_create'"
              label="盈亏"
              prop="profitAndLossQuantity"
              :width="200"
            >
              <template #default="scope">
                <el-tag :type="scope.row.profitAndLossQuantity===0?'warning':scope.row.profitAndLossQuantity>0?'primary':'danger'">
                  {{ scope.row.profitAndLossQuantity===0?'平':scope.row.profitAndLossQuantity > 0 ?'盈':'亏' }}
                </el-tag>
                <el-tag :type="scope.row.profitAndLossQuantity===0?'warning':scope.row.profitAndLossQuantity>0?'primary':'danger'" class="ml10">
                  {{ (scope.row.profitAndLossQuantity) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column
              v-for="(item) in listDataSource.tableHeader"
              :key="item.prop?? item.key"
              :fixed="item.isFixed"
              :label="item.label ?? item.title"
              :prop="item.prop ?? item.key"
              :min-width="item.width"
              :formatter="forMatDataPro"
              align="left"
              sortable
              show-overflow-tooltip
            />
            <el-table-column
              fixed="right" label="操作"
              width="120"
              align="center"
            >
              <template #default="row">
                <el-button
                  link type="danger"
                  size="small"
                  :disabled="formData.processStatus&&formData.processStatus!=='inventory_task_create'"
                  @click="handelDelete(row)"
                >
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-card>
    </div>
    <selectDialogTable
      v-if="selectTableData.isShow"
      ref="dialogSearch"
      :append-table-style="{ background: '#126f9e', color: '#fff', borderColor: 'rgba(192, 192, 192,.5)' }"
      :width="'70%'"
      :search-input-placeholder="selectTableData.placeholder"
      :is-show="selectTableData.isShow"
      :data-source="selectTableData.dataSource"
      :title="selectTableData.title"
      :need-selection="selectTableData.needSelection"
      is-show-search-input
      :loading="loading"
      @close-dialog="selectTableData.isShow=false"
      @do-dialog-search="selectTableData.doSearch"
      @submit="selectTableData.submit"
      @chooseRow="selectTableData.chooseRow"
    />
  </div>
</template>

<script setup>
import { pbRequest } from "@/applications/sys/public/pbRequest/index"
import selectDialogTable from '@/components/public/selectDialogTable.vue'
import selectDialogForm from '@/components/public/selectDialogForm.vue'
import { ref, onBeforeMount, getCurrentInstance, reactive } from "vue"
import { useRouter, useRoute } from 'vue-router'
import { DataSource, loading } from "../utils/checkOrder"
import { DataSource as warehouse } from "../../fileManager/utils/warehouse"
const { proxy } = getCurrentInstance()
const router = useRouter()
const route = useRoute()
const title = ref(`首页 / 盘点管理 / `)
const active = ref(`${route.query.id ? route.query. inventoryTaskOrderCode + ' 盘点任务明细' : '新增盘点任务'} `)
const dataSource = ref(null)

let isSave = false

let processStatus = {
  inventory_create: '制单',
  inventory_reject: '驳回',
  inventory_ing: '盘点中',
  inventory_check: '审核中',
  inventory_doc: '归档',
  inventory_task_create: '制单',
  inventory_task_start: '盘点中',
  inventory_task_audit: '审核',
  inventory_task_doc: '归档'
}

const userList = reactive([])

const selectUserList = ref([])

const checkAll = ref(false)
const indeterminate = ref(false)

const handleCheckAll = (val) => {
  indeterminate.value = false
  if (val) {
    selectUserList.value = userList.map((_) => _.value)
  } else {
    selectUserList.value = []
  }
}

const goBack = () => {
  if (formData.value.processStatus || formData.value.processStatus === 'allocation_wait_out'){
    router.go(-1)
    return
  }
  if (!isSave){
    proxy.$alert('即将离开当前编辑页，未保存的数据将失效', '提示', {
      showCancelButton: true,
      cancelButtonText: '直接离开',
      confirmButtonText: '离开并保存',
      type: 'warning',
      icon: 'InfoFilled',
      distinguishCancelAndClose: true,
      closeOnClickModal: true,
      callback: (action) => {

        if (action === 'confirm') {
          editControlCommon.inputDone(null, 'saveAndGo')
        } else if (action === 'cancel'){
          router.go(-1)
        }
      }

    })
  } else {
    router.go(-1)
  }

}

onBeforeMount(() => {
  initData()
  getUserList()

})

const getUserList = () => {
  dataSource.value.getUserList().then(res => {
    if (res.code === 200){
      res.rows.forEach(item => {
        userList.push({
          label: item.employeeName,
          value: item.employeeName + ' ' + item.accountId
        })
      })
    }
  })
}

const myId = ref(-1)
const inventoryTaskOrderCode = ref(-1)

const initData = async () => {
  if (!route.query.id){
    formData.value = { inventoryType: route.query.inventoryType, isOpenCheck: 1, inventoryTaskName: '例行盘点' }
    setTimeout(() => {
      editControlCommon.changeData = !editControlCommon.changeData
    }, 500)

    console.log(formData.value)
  }

  if (!dataSource.value){
    dataSource.value = new DataSource({
      selectUri: '/wms/product/list',
      listMethod: 'GET',
      tableHeader: [{
        title: "产品代码",
        dataKey: "productCode",
        key: 'productCode',
        width: 200,
        type: 'text'
      }, {
        title: "产品名称",
        dataKey: "productName",
        key: 'productName',
        width: 200
      }, {
        title: "产品类型",
        dataKey: "productType",
        key: 'productType',
        width: 200,
        type: 'select'
      }, {
        title: "产品型号",
        dataKey: "productSpecification",
        key: 'productSpecification',
        width: 200
      }]
    })
  }
  if (!listDataSource.value){
    listDataSource.value = new DataSource({
      selectUri: '/wms/taskDetail/list',
      listMethod: 'GET',
      tableHeader: [
        {
          title: "产品代码",
          dataKey: "productCode",
          key: 'productCode',
          width: 200,
          type: 'text'
        }, {
          title: "产品名称",
          dataKey: "productName",
          key: 'productName',
          width: 200
        }, {
          title: "产品类型",
          dataKey: "productType",
          key: 'productType',
          width: 200,
          type: 'select'
        }, {
          title: "产品型号",
          dataKey: "productSpecification",
          key: 'productSpecification',
          width: 200
        }, {
          title: "仓位名称",
          dataKey: "warehousePositionName",
          key: 'warehousePositionName',
          width: 200
        }, {
          title: "仓位代码",
          dataKey: "warehousePositionCode",
          key: 'warehousePositionCode',
          width: 200
        }
      ],
      pageSize: 100
    })
  }

  if (route.query.id) getDetail()

}

const handelDelete = (val) => {
  listDataSource.value.tableData.splice(val.$index, 1)
}

const listDataSource = ref(null)

const getDetail = async (val) => {
  myId.value = route.query.id ? route.query.id : val
  inventoryTaskOrderCode.value = route.query. inventoryTaskOrderCode ? route.query. inventoryTaskOrderCode : ``

  dataSource.value.getTask(myId.value).then(async res => {
    if (res.code === 200){
      formData.value = { ...res.data }
      inventoryTaskOrderCode.value = res.data.nventoryTaskOrderCode
      editControlCommon.changeData = !editControlCommon.changeData
      selectUserList.value.length = 0
      JSON.parse(formData.value.userObject).forEach(item => {
        selectUserList.value.push(item.inventoryUserName + ' ' + item.inventoryUserId)
      })
      editControlCommon.formSelectEl[0].disabled = true
      editControlCommon.formSelectEl[1].disabled = true
      listDataSource.value.searchData = { inventoryTaskOrderCode: formData.value.inventoryTaskOrderCode }
      listDataSource.value.initData()
    }
  })

}

const formData = ref({})

const editControlCommon = reactive({
  changeData: false,
  formInputEl: [{
    title: "盘点任务名称",
    key: 'inventoryTaskName',
    element: 'input',
    rules: [{
      required: true,
      message: '该项不能为空',
      trigger: 'blur'
    }]
  }, {
    title: "选择盘点仓库",
    key: 'warehouseName',
    element: 'input',
    type: 'dialog',
    rules: [{
      required: true,
      message: '该项不能为空',
      trigger: 'blur'
    }]
  }],
  formSelectEl: [{
    title: "明盘/暗盘",
    key: 'isOpenCheck',
    element: 'select',
    options: [{
      label: '暗盘',
      value: 2
    }, {
      label: '明盘',
      value: 1
    }],
    rules: [{
      required: true,
      message: '该项不能为空',
      trigger: 'blur'
    }]
  }, {
    title: "盘点类型",
    key: 'inventoryType',
    element: 'select',
    options: [{
      label: '仓位',
      value: 'CW'
    }, {
      label: '产品',
      value: `CP`
    }],
    change: (val) => {
      formData.value.inventoryType = val
    }
  }],
  formTextAreaEl: [{
    title: "备注",
    key: 'comment',
    element: 'input',
    type: 'textarea'
  }],
  formTimeAndNumber: [{
    title: "盘点日期",
    key: 'inventoryTaskDate',
    element: 'date'

  }],
  formUploadEl: [],
  emitOpenDialog: (val) => {
    openSelectDialog(val)
  },
  inputDone: (val, type) => {
    if (listDataSource.value.tableData.length === 0){
      proxy.$message.error(`盘点产品列表不能为空`)
      return
    }
    isSave = true
    if (!val){
      let params = proxy.$refs.dialogForm.manualEmitData()
      params.inventoryTaskDetailList = listDataSource.value.tableData
      params.inventoryUserDtoList = selectUserList.value.map(item => {
        return {
          inventoryUserId: item.split(' ')[1],
          inventoryUserName: item.split(' ')[0]
        }
      })
      if (params.id){
        dataSource.value.updateCheckMissions(params).then(res => {
          if (res.code === 200){
            proxy.$message.success(`更新成功`)
            if (type === 'saveAndGo') router.go(-1)
            else getDetail(+res.msg)
          }
        })

      } else {
        dataSource.value.addCheckMissions(params).then(res => {
          if (res.code === 200){
            proxy.$message.success(`新增成功`)
            if (type === 'saveAndGo') router.go(-1)
            else {

              getDetail(+res.msg)
            }
          }
        })

      }
    }

  }
})

const forMatDataPro = (row, column) => {
  const warehouseType = {
    warehouse: '仓库',
    area: '库区',
    frame: '货架',
    position: '仓位'

  }
  if (column.property === 'warehouseType') return warehouseType[row[column.property]] ?? '-'
  return row[column.property] ?? '-'
}

const openSelectDialog = async (type) => {
  switch (type){
  // 选择数据

  case 'warehouseName': {
    selectTableData.title = '请盘点提货仓库'
    selectTableData.placeholder = '仓库名称搜索'
    selectTableData.needSelection = false
    selectTableData.dataSource = new warehouse({
      selectUri: '/wms/warehouse/listDemo',
      listMethod: 'GET',
      pageSize: 10,
      tableHeader: [{
        title: "仓库名称",
        dataKey: "warehouseName",
        key: 'warehouseName',
        width: 200,
        type: 'text',
        isShow: true,
        isFixed: true
      }, {
        title: "仓库类型",
        dataKey: "warehouseType",
        key: 'warehouseType',
        width: 200,
        type: 'select',
        isShow: true,
        isFixed: true,
        options: []
      }, {
        title: "仓库地址",
        dataKey: "warehouseAddress",
        key: 'warehouseAddress',
        width: 200,
        type: 'text',
        isShow: true,
        isFixed: false
      }]
    })
    selectTableData.dataSource.forMatDataV2 = forMatDataPro
    selectTableData.chooseRow = (val) => {

      proxy.$refs.dialogForm.updateDialogInput({ warehouseName: val.warehouseName })
      proxy.$refs.dialogForm.updateDialogInput({ warehouseCode: val.warehouseCode })
      formData.value.warehouseName = val.warehouseName
      formData.value.warehouseCode = val.warehouseCode
      listDataSource.value.tableData.length = 0
      selectTableData.isShow = false
    }

    selectTableData.doSearch = async (val) => {
      selectTableData.dataSource.searchData = { }
      if (val) selectTableData.dataSource.searchData.warehouseName = val
      await selectTableData.dataSource.initData()
      selectTableData.dataSource.tableData.forEach(item => {
        delete item.children
      })
      selectTableData.dataSource.total = selectTableData.dataSource.tableData.length
    }

    selectTableData.dataSource.searchData = { }
    await selectTableData.dataSource.initData()
    selectTableData.dataSource.tableData.forEach(item => {
      delete item.children
    })
    selectTableData.dataSource.total = selectTableData.dataSource.tableData.length
    selectTableData.isShow = true
    return
  }

  case 'CW': {
    if (!formData.value.warehouseName){
      proxy.$message.error('请先选择盘点仓库')
      return
    }
    selectTableData.title = '请需要盘点的仓库'
    selectTableData.placeholder = '仓库名称搜索'
    selectTableData.needSelection = true
    selectTableData.dataSource = new warehouse({
      selectUri: '/wms/warehouse/listDemo',
      listMethod: 'GET',
      pageSize: 10,
      tableHeader: [{
        title: "仓库名称",
        dataKey: "warehouseName",
        key: 'warehouseName',
        width: 200,
        type: 'text',
        isShow: true,
        isFixed: true
      }, {
        title: "仓库类型",
        dataKey: "warehouseType",
        key: 'warehouseType',
        width: 200,
        type: 'select',
        isShow: true,
        isFixed: true,
        options: []
      }, {
        title: "仓库地址",
        dataKey: "warehouseAddress",
        key: 'warehouseAddress',
        width: 200,
        type: 'text',
        isShow: true,
        isFixed: false
      }]
    })
    selectTableData.dataSource.forMatDataV2 = forMatDataPro
    selectTableData.submit = (val) => {
      let idList = selectTableData.dataSource.selections.map(item => item.warehouseCode)
      dataSource.value.getTaskDetail({
        warehouseCode: formData.value.warehouseCode,
        inventoryType: formData.value.inventoryType,
        list: idList
      }).then(res => {
        if (res.code === 200){
          listDataSource.value.tableData.length = 0
          listDataSource.value.tableData.push(...res.data)
          selectTableData.isShow = false
        }
      })

    }

    selectTableData.doSearch = async (val) => {
      selectTableData.dataSource.searchData = { warehouseName: formData.value.warehouseName, warehouseCode: formData.value.warehouseCode, maxParentCode: formData.value.warehouseCode }
      if (val) selectTableData.dataSource.searchData.warehouseName = val
      await selectTableData.dataSource.initData()

      selectTableData.dataSource.total = selectTableData.dataSource.tableData.length
    }

    selectTableData.dataSource.searchData = { warehouseName: formData.value.warehouseName, warehouseCode: formData.value.warehouseCode, maxParentCode: formData.value.warehouseCode }
    await selectTableData.dataSource.initData()

    selectTableData.dataSource.total = selectTableData.dataSource.tableData.length
    selectTableData.isShow = true
    return
  }

  case 'CP': {
    if (!formData.value.warehouseName){
      proxy.$message.error('请先选择盘点仓库')
      return
    }
    selectTableData.title = '请选择盘点的产品'
    selectTableData.placeholder = '输入产品名称搜索'
    selectTableData.needSelection = true
    selectTableData.dataSource = new DataSource({
      selectUri: '/wms/position/listGrouping',
      listMethod: 'GET',
      pageSize: 10,
      tableHeader: [
        {
          title: "仓位名称",
          dataKey: "positionName",
          key: 'positionName',
          width: 200
        }, {
          title: "产品代码",
          dataKey: "productCode",
          key: 'productCode',
          width: 200,
          type: 'text',
          isShow: true,
          isFixed: true
        }, {
          title: "产品名称",
          dataKey: "productName",
          key: 'productName',
          width: 200
        }, {
          title: "产品类型",
          dataKey: "productType",
          key: 'productType',
          width: 200,
          type: 'select'
        }, {
          title: "产品型号",
          dataKey: "productSpecification",
          key: 'productSpecification',
          width: 200
        }, {
          title: "账面数量",
          dataKey: "sumNum",
          key: 'sumNum',
          width: 200,
          type: 'none',
          isShow: true,
          isFixed: false
        }, {
          title: "类型",
          dataKey: "productType",
          key: 'productType',
          width: 200,
          type: 'none',
          isShow: true,
          isFixed: false
        }
      ]
    })
    selectTableData.dataSource.forMatDataV2 = forMatDataPro

    selectTableData.submit = () => {
      let idList = selectTableData.dataSource.selections.map(item => item.productCode)
      dataSource.value.getTaskDetail({
        warehouseCode: formData.value.warehouseCode,
        inventoryType: formData.value.inventoryType,
        list: idList
      }).then(res => {
        if (res.code === 200){
          listDataSource.value.tableData.length = 0
          listDataSource.value.tableData.push(...res.data)
          selectTableData.isShow = false
        }
      })

      selectTableData.isShow = false
    }

    selectTableData.doSearch = (val) => {
      selectTableData.dataSource.searchData = { warehouseName: formData.value.warehouseName }
      if (val)selectTableData.dataSource.searchData.productName = val
      selectTableData.dataSource.initData()
    }

    selectTableData.dataSource.searchData = { ckWarehouseCode: formData.value.warehouseCode }
    await selectTableData.dataSource.initData()
    selectTableData.isShow = true
    return
  }

  default: return
  }
}

const selectTableData = reactive({
  isShow: false,
  dataSource: null,
  doSearch: (val) => {
    console.log(val)
  },
  submit: (val) => {
    console.log(val)
  }
})

const submit = () => {

  proxy.$alert(`是否确认递交该盘点单？`, '提示', {
    type: 'info',
    showCancelButton: true,
    cancelButtonText: '再想想',
    confirmButtonText: '确认',
    callback: (action) => {
      if (action === 'cancel') return
      else {
        dataSource.value.nextStatusTask(formData.value.inventoryTaskOrderCode).then((res) => {
          if (res.code === 200){
            proxy.$message.success('递交成功')
            getDetail()
          }
        })
      }
    }
  })

}

const checkChange = async ($event, row) => {
  row.profitAndLossQuantity = $event - row.bookQuantity
  let params = { ...formData.value }
  params.inventorySheetDetailList = listDataSource.value.tableData
  let res = await dataSource.value.updateSheet(params)
  if (res.code === 200){
    proxy.$message.success(`更新成功`)
    getDetail()
  }
}

const rollBack = async () => {
  proxy.$alert(`是否确认回退盘点单？`, '提示', {
    type: 'info',
    showCancelButton: true,
    cancelButtonText: '再想想',
    confirmButtonText: '确认',
    callback: (action) => {
      if (action === 'cancel') return
      else {
        dataSource.value.backProcess(formData.value.id).then((res) => {
          if (res.code === 200){
            proxy.$message.success('回退成功')
            getDetail()
          }
        })
      }
    }
  })
}

const collect = () => {
  proxy.$alert(`是否确认汇总盘点？`, '提示', {
    type: 'info',
    showCancelButton: true,
    cancelButtonText: '再想想',
    confirmButtonText: '确认',
    callback: (action) => {
      if (action === 'cancel') return
      else {
        dataSource.value.oneClickSummary(formData.value.inventoryTaskOrderCode).then(res => {
          if (res.code === 200){
            proxy.$message.success('汇总成功')
            getDetail()
          }
        })
      }
    }
  })
}

const updateType = () => {
  proxy.$alert(`是否确认修改明/盘？`, '提示', {
    type: 'info',
    showCancelButton: true,
    cancelButtonText: '再想想',
    confirmButtonText: '确认',
    callback: (action) => {
      if (action === 'cancel') return
      else {
        let params = { ...formData.value, isOpenCheck: formData.value.isOpenCheck === 2 ? '1' : '2' }
        dataSource.value.setIsOpenCheck(params).then(res => {
          if (res.code === 200){
            proxy.$message.success('修改成功')
            getDetail()
          }
        })
      }
    }
  })
}
</script>

<style lang="less" scoped>
.detail-content{
  background:#fff;
  padding: 20px 20px;
  border-top: 1px solid #ececec;
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

:deep(.el-card__header){
  font-size:14px;
  background: #eeeeee;
  padding: 10px 20px;
}
.card-header{
  display: inline-flex;
  align-items:center ;
}
</style>