<template>
  <div>
    <div class="header">
      <el-page-header @back="goBack">
        <template #content>
          <span class="route"> {{ title }} </span>
          <span class="route" style="color:#999">  {{ active }} </span>
          <el-button
            :disabled="formData.processStatus&&formData.processStatus!=='allocation_wait_out'"
            type="warning" class="ml20"
            size="small"
            @click="editControlCommon.inputDone(null)"
          >
            保存所有
          </el-button>
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
        class="card"
      >
        <template #header>
          <div class="card-header">
            <span>调拨产品列表</span>
            <el-button
              :disabled="formData.processStatus&&formData.processStatus!=='allocation_wait_out'"
              type="primary" class="ml20"
              size="small"
              @click="openSelectDialog('product')"
            >
              添加
            </el-button>
          </div>
        </template>

        <div
          v-if="allocationOrderDetailList.length===0" class="flex-center"
          style="color:#999;font-size:12px"
        >
          暂 无 调 拨
        </div>
        <div v-else>
          <el-table
            :data="allocationOrderDetailList" style="width: 100%"
            border
            :header-cell-style="{ background: '#eeeeee', color: '#606266'}"
            stripe
          >
            <el-table-column
              label="调拨数量"
              prop="number"
              :width="200"
            >
              <template #default="scope">
                <el-input-number
                  v-model="scope.row.number" size="small"
                  :disabled="formData.processStatus&&formData.processStatus!=='allocation_wait_out'"
                  :min="1"
                />
              </template>
            </el-table-column>
            <el-table-column
              v-for="(item) in dataSource.tableHeader"
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
                  :disabled="formData.processStatus&&formData.processStatus!=='allocation_wait_out'"
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
      :width="'60%'"
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
import { DataSource, loading } from "../utils/allocationDetail"
import { DataSource as warehouse } from "../../fileManager/utils/warehouse"
const { proxy } = getCurrentInstance()
const router = useRouter()
const route = useRoute()
const title = ref(`首页 / 产品档案 / `)
const active = ref(`${route.query.id ? route.query.allocationCode + ' 调拨单明细' : '新增调拨单'} `)
const dataSource = ref(null)
const allocationOrderDetailList = reactive([])
let isSave = false
let processStatus = {
  allocation_create: '制单',
  allocation_wait_out: '等待出库',
  allocation_out_ing: '出库中',
  allocation_ing: '调拨中',
  allocation_in_ing: '入库中',
  allocation_doc: '归档'

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

})
const myId = ref(-1)
const allocationCode = ref(-1)
const initData = async () => {
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
  if (route.query.id) getDetail()

}

const handelDelete = (val) => {

  allocationOrderDetailList.splice(val.$index, 1)

}

const getDetail = (val) => {

  myId.value = route.query.id ? route.query.id : val.id
  allocationCode.value = route.query.allocationCode ? route.query.allocationCode : val.allocationCode
  dataSource.value.getAllocationDetail(myId.value).then(res => {
    if (res.code === 200){
      formData.value = { ...res.data, totalVolume: +res.data.totalVolume, totalNumber: +res.data.totalNumber }
      editControlCommon.changeData = !editControlCommon.changeData
    }
  })
  dataSource.value.getAllocationListDetail(allocationCode.value).then(res => {
    if (res.code === 200){
      allocationOrderDetailList.length = 0
      allocationOrderDetailList.push(...res.rows)
      console.log(res.rows)
    }
  })

}

const formData = ref({})

const editControlCommon = reactive({
  changeData: false,
  formInputEl: [{
    title: "提货仓库",
    key: 'warehouseName',
    element: 'input',
    type: 'dialog',
    rules: [{
      required: true,
      message: '该项不能为空',
      trigger: 'blur'
    }]
  }, {
    title: "目的仓库",
    key: 'toWarehouseName',
    element: 'input',
    type: 'dialog',
    rules: [{
      required: true,
      message: '该项不能为空',
      trigger: 'blur'
    }]
  }, {
    title: "提货人",
    key: 'pickingMan',
    element: 'input'
  }],
  formSelectEl: [],
  formTextAreaEl: [{
    title: "备注",
    key: 'remark',
    element: 'input',
    type: 'textarea'
  }],
  formTimeAndNumber: [{
    title: "总数量",
    key: 'totalNumber',
    element: 'number',
    disabled: true
  }, {
    title: "总体积",
    key: 'totalVolume',
    element: 'number',
    disabled: true
  }],
  formUploadEl: [],
  emitOpenDialog: (val) => {
    openSelectDialog(val)
  },
  inputDone: async (val, type) => {
    if (allocationOrderDetailList.length === 0){
      proxy.$message.error(`调拨产品列表不能为空`)
      return
    }
    isSave = true
    if (!val){

      let params = proxy.$refs.dialogForm.manualEmitData()
      params.allocationOrderDetailList = allocationOrderDetailList
      if (params.id){
        let res = await dataSource.value.updateAllocation(params)
        if (res.code === 200){
          proxy.$message.success(`更新成功`)
          if (type === 'saveAndGo') router.go(-1)
          else getDetail()
        }
      } else {
        let res = await dataSource.value.addAllocation(params)
        if (res.code === 200){
          proxy.$message.success(`新增成功`)
          if (type === 'saveAndGo') router.go(-1)
          else {
            getDetail(res.data)
          }
        }
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
  case 'toWarehouseName':
  case 'warehouseName': {
    selectTableData.title = type === 'warehouseName' ? '请选择提货仓库' : '请选择目的仓库'
    selectTableData.placeholder = type === 'warehouseName' ? '仓库名称搜索' : '仓库名称搜索'
    selectTableData.needSelection = false
    selectTableData.dataSource = new warehouse({
      selectUri: '/wms/warehouse/list',
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

      if (type === 'toWarehouseName'){
        proxy.$refs.dialogForm.updateDialogInput({ toWarehouseName: val.warehouseName })
        proxy.$refs.dialogForm.updateDialogInput({ toWarehouseCode: val.warehouseCode })
        formData.value.toWarehouseName = val.warehouseName
        formData.value.toWarehouseCode = val.warehouseCode
      }
      else if (type === 'warehouseName'){
        if (formData.value.warehouseName && formData.value.warehouseName !== val.warehouseName){
          allocationOrderDetailList.length = 0
        }
        proxy.$refs.dialogForm.updateDialogInput({ warehouseName: val.warehouseName })
        proxy.$refs.dialogForm.updateDialogInput({ warehouseCode: val.warehouseCode })
        formData.value.warehouseName = val.warehouseName
        formData.value.warehouseCode = val.warehouseCode

      }

      selectTableData.isShow = false
    }

    selectTableData.doSearch = async (val) => {
      selectTableData.dataSource.searchData = { }
      if (val) selectTableData.dataSource.searchData.warehouseName = val
      selectTableData.dataSource.initData()
      await selectTableData.dataSource.tableData.forEach(item => {
        delete item.children
      })
    }

    selectTableData.dataSource.searchData = { }
    await selectTableData.dataSource.initData()
    selectTableData.dataSource.tableData.forEach(item => {
      delete item.children
    })
    selectTableData.isShow = true
    return
  }

  case 'product': {
    if (!formData.value.warehouseName){
      proxy.$message.error('请先选择提货仓库')
      return
    }
    selectTableData.title = '请选择调拨的产品'
    selectTableData.placeholder = '输入产品名称搜索'
    selectTableData.needSelection = true
    selectTableData.dataSource = new DataSource({
      selectUri: '/wms/main/list',
      listMethod: 'GET',
      pageSize: 10,
      tableHeader: [
        {
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
          title: "产品所在仓库",
          dataKey: "warehouseName",
          key: 'warehouseName',
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
          title: "现有量",
          dataKey: "nowNumber",
          key: 'nowNumber',
          width: 200,
          type: 'none',
          isShow: true,
          isFixed: false
        }, {
          title: "冻结量",
          dataKey: "frozenNumber",
          key: 'frozenNumber',
          width: 200,
          type: 'none',
          isShow: true,
          isFixed: false
        }
      ]
    })
    selectTableData.dataSource.forMatDataV2 = forMatDataPro

    selectTableData.submit = () => {
      allocationOrderDetailList.unshift(...selectTableData.dataSource.selections)

      let temp = allocationOrderDetailList.filter((item, index) => index === allocationOrderDetailList.findIndex(i => {
        i.number = 0
        return i.productCode === item.productCode
      }))
      allocationOrderDetailList.length = 0
      allocationOrderDetailList.push(...temp)

      proxy.$message.success('添加成功，重复数据已自动过滤')
      selectTableData.isShow = false
    }

    selectTableData.doSearch = (val) => {
      selectTableData.dataSource.searchData = { warehouseName: formData.value.warehouseName }
      if (val)selectTableData.dataSource.searchData.productName = val
      selectTableData.dataSource.initData()
    }

    selectTableData.dataSource.searchData = { warehouseName: formData.value.warehouseName }
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