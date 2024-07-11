<template>
  <div>
    <div class="header">
      <el-page-header @back="goBack">
        <template #content>
          <span class="route"> {{ title }} </span>
          <span class="route" style="color:#999">  {{ `${myId!==-1 ? inWarehouseCode + ' 入库单明细' : '新增入库单'} ` }} </span>
          <el-button
            v-if="!formData.processStatus||formData.processStatus==='in_create'"
            type="warning" class="ml20"
            size="small"
            @click="editControlCommon.inputDone(null)"
          >
            保存所有
          </el-button>
          <el-button
            v-if="formData.processStatus==='in_ing'"
            type="success" class="ml20"
            size="small"
            @click="partDoc"
          >
            部分入库
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
              {{ processStatus[formData.processStatus] ?? '制单' }}
            </el-tag>
            <span class="ml12">入库类型：</span>
            <el-tag style="font-size:12px;color:#61afff">
              {{ route.query.type === 'product' ? '普通入库' : '调拨入库' }}
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
        v-if="route.query.type !== 'product'"
        shadow="always"
        class="card mb20"
      >
        <template #header>
          <div class="card-header ">
            <span>已选择{{ route.query.type==='allocation'?'调拨单':'产品' }}</span>
            <span
              v-if="inWarehouseCode!==-1" class="route ml10"
              style="color:#999"
            >  {{ inWarehouseCode }} </span>
            <el-button
              v-if="!formData.processStatus||formData.processStatus==='in_create'"
              type="primary" class="ml10"
              size="small"

              @click="openSelectDialog(route.query.type)"
            >
              选择/添加 {{ route.query.type==='allocation'?'调拨单':'产品' }}
            </el-button>
            <span class="route ml10" style="color:#24acf2">  总数：{{ orderList.length }} </span>
          </div>
        </template>

        <div
          v-if="orderList.length===0" class="flex-center"
          style="color:#999;font-size:12px"
        >
          暂 无 已 选 择 的 {{ route.query.type==='allocation'?'调 拨 单':'xxx' }}
        </div>
        <div>
          <el-tag
            v-for="(tag,index) in orderList"
            :key="tag"
            class="mr20"
            :closable="!formData.processStatus&&true"
            :disable-transitions="false"
            style="cursor:pointer"
            @close="handleCloseCondition(tag,index)"
          >
            {{ tag }}
          </el-tag>
        </div>
      </el-card>

      <el-card
        shadow="always"
        class="card mb20"
      >
        <template #header>
          <div class="card-header">
            <span>入库产品列表</span>
            <span
              v-if="inWarehouseCode!==-1" class="route ml10"
              style="color:#999"
            >  {{ inWarehouseCode }} </span>
            <el-button
              v-show="!formData.processStatus||formData.processStatus==='in_create'"
              v-if="route.query.type === 'product'"
              type="primary" class="ml10"
              size="small"
              @click="openSelectDialog(route.query.type)"
            >
              选择/添加 {{ route.query.type==='allocation'?'调拨单':'产品' }}
            </el-button>
            <span class="route ml10" style="color:#24acf2">  总数：{{ productDetailList.length }} </span>
          </div>
        </template>

        <div
          v-if="productDetailList.length===0" class="flex-center"
          style="color:#999;font-size:12px"
        >
          暂 无 出 库 产 品
        </div>
        <div v-else>
          <el-table
            :data="productDetailList" style="width: 100%;"
            border
            max-height="500"
            :header-cell-style="{ background: '#a0cfff', color: '#606266'}"
            stripe
          >
            <el-table-column
              label="请填入批次号"
              prop="batchCode"
              :width="200"
            >
              <template #default="scope">
                <el-input
                  v-model="scope.row.batchCode" size="small" :disabled="(formData.processStatus&&true)"
                  placeholder="请填入批次号，必填"
                />
              </template>
            </el-table-column>
            <el-table-column
              label="入库数量"
              prop="number"
              :width="200"
            >
              <template #default="scope">
                <el-input-number
                  v-if="!formData.processStatus||formData.processStatus==='in_create'"
                  v-model="scope.row.number" size="small"
                  :disabled="route.query.type==='allocation'"
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
              v-if="route.query.type==='product'"
              fixed="right" label="操作"
              width="120"
              align="center"
            >
              <template #default="row">
                <el-button
                  link type="danger"
                  size="small"
                  :disabled="formData.processStatus&&true"
                  @click="handelDelete(row)"
                >
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-card>

      <el-card
        v-if="myId!==-1"
        shadow="always"
        class="card mb20"
      >
        <template #header>
          <div class="card-header" style="display:flex;justify-content:space-between">
            <div>
              <span>入库产品明细</span>
              <span
                v-if="inWarehouseCode!==-1" class="route ml10"
                style="color:#999"
              >  {{ inWarehouseCode }} </span>
              <span class="route ml10" style="color:#24acf2">  总数：{{ productSubdividelist.length }} </span>
            </div>
            <div>
              <el-button
                v-if="isShowHandleOut"
                type="success" class="ml10"
                size="small"

                @click="confirmOut"
              >
                确认入库
              </el-button>
              <el-button
                :type="isShowHandleOut ? 'danger':'primary'" class="ml10"
                size="small"
                :disabled="formData.processStatus!=='in_create'&&formData.processStatus!=='in_wait_out'&&formData.processStatus!=='in_ing'"
                @click="handleOut"
              >
                {{ isShowHandleOut?'取消手动入库':'手动入库模式' }}
              </el-button>
            </div>
          </div>
        </template>

        <div>
          <el-table
            :data="productSubdividelist" style="width: 100%;"
            border
            max-height="500"
            :header-cell-style="{ background: '#a0cfff', color: '#606266'}"
            stripe
          >
            <el-table-column
              v-if="isShowHandleOut"
              label="填写入库数量"
              prop="shouldInNumber"
              :width="200"
            >
              <template #default="scope">
                <el-input-number
                  v-model="scope.row.isInNumber"
                  :disabled="scope.row.noInNumber===0" size="small"
                  :min="0"
                  :max="scope.row.noInNumber"
                />
              </template>
            </el-table-column>
            <el-table-column
              v-if="isShowHandleOut"
              label="请选择入库仓位"
              prop="positionName"
              :width="200"
            >
              <template #default="scope">
                <el-button
                  type="primary"
                  link
                  @click="openSelectDialog('inPositionCode', scope.row)"
                >
                  {{ scope.row.positionName??'选择仓位' }}
                </el-button>
              </template>
            </el-table-column>
            <el-table-column
              label="应入"
              prop="shouldInNumber"
              :width="200"
            >
              <template #default="scope">
                <el-tag class="mr6">
                  {{ scope.row.shouldInNumber }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column
              label="已入"
              prop="haveInNumber"
              :width="200"
            >
              <template #default="scope">
                <el-tag type="danger">
                  {{ scope.row.haveInNumber }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column
              label="剩余未入"
              prop="noInNumber"
              :width="200"
            >
              <template #default="scope">
                <el-tag class="mr6" type="warning">
                  {{ scope.row.noInNumber }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column
              label="条码头"
              prop="barCodeHead"
              :width="200"
            />
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
              label="入库仓"
              prop="warehouseName"
              :width="200"
            />
            <el-table-column
              label="推荐仓位"
              prop="positionName"
              :width="200"
            />
          </el-table>
        </div>
      </el-card>
      <el-card
        v-if="myId!==-1"
        shadow="always"
        class="card mb20"
      >
        <template #header>
          <div class="card-header">
            <span>入库库位明细</span>
            <span
              v-if="inWarehouseCode!==-1" class="route ml10"
              style="color:#999"
            >  {{ inWarehouseCode }} </span>
            <span class="route ml10" style="color:#24acf2">  总数：{{ productPositionlist.length }} </span>
          </div>
        </template>

        <div
          v-if="productPositionlist.length===0" class="flex-center"
          style="color:#999;font-size:12px"
        >
          暂 无 库 位 明 细
        </div>
        <div v-else>
          <el-table
            :data="productPositionlist" style="width: 100%"
            border
            :header-cell-style="{ background: '#a0cfff', color: '#606266'}"
            stripe
          >
            <el-table-column
              label="入库数量"
              prop="number"
              :width="200"
            />
            <el-table-column
              label="入库仓"
              prop="warehouseName"
              :width="200"
            />
            <el-table-column
              label="入库仓位"
              prop="positionName"
              :width="200"
            />

            <el-table-column
              label="产品代码"
              prop="productCode"
              :width="200"
            />
            <el-table-column
              label="产品名称"
              prop="productName"
            />
            <el-table-column
              label="产品型号"
              prop="productSpecification"
              :width="200"
            />
            <el-table-column
              label="产品类型"
              prop="productType"
              :width="200"
            />
          </el-table>
        </div>
      </el-card>
      <el-card
        v-if="myId!==-1"
        shadow="always"
        class="card"
      >
        <template #header>
          <div class="card-header">
            <span>入库扫码明细</span>
            <span
              v-if="inWarehouseCode!==-1" class="route ml10"
              style="color:#999"
            >  {{ inWarehouseCode }} </span>
            <span class="route ml10" style="color:#24acf2">  总数：{{ productScanlist.length }} </span>
            <!-- <el-link type="primary" class="ml10">
              上一页
            </el-link>
            <el-link type="primary" class="ml10">
              下一页
            </el-link> -->
          </div>
        </template>

        <div
          v-if="productScanlist.length===0" class="flex-center"
          style="color:#999;font-size:12px"
        >
          暂 无 扫 码 明 细
        </div>
        <div v-else>
          <el-table
            :data="productScanlist" style="width: 100%;"
            border
            max-height="800"
            :header-cell-style="{ background: '#a0cfff', color: '#606266'}"
            stripe
          >
            <el-table-column
              label="条形码"
              prop="barCode"
              :width="200"
            />

            <el-table-column
              label="入库数量"
              prop="number"
              :width="200"
            >
              <template #default="">
                1
              </template>
            </el-table-column>
            <el-table-column
              label="入库仓"
              prop="warehouseName"
              :width="200"
            />
            <el-table-column
              label="入库仓位"
              prop="positionName"
              :width="200"
            />

            <el-table-column
              label="产品代码"
              prop="productCode"
              :width="200"
            />
            <el-table-column
              label="产品名称"
              prop="productName"
              :width="200"
            />
            <el-table-column
              label="产品型号"
              prop="productSpecification"
              :width="200"
            />
            <el-table-column
              label="产品类型"
              prop="productType"
              :width="200"
            />
            <el-table-column
              label="扫码时间"
              prop="scanTime"
              :width="200"
            />
            <el-table-column
              v-if="route.query.type==='product'"
              fixed="right" label="操作"
              width="120"
              align="center"
            >
              <template #default="scope">
                <el-button
                  link type="success"
                  :disabled="formData.processStatus==='out_cancel'"
                  size="small"
                  @click="changeScanCode(scope.row)"
                >
                  更换条码
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
import { DataSource, loading } from '../utils/inDetail'
import { DataSource as warehouse } from "../../fileManager/utils/warehouse"
const { proxy } = getCurrentInstance()
const router = useRouter()
const route = useRoute()
const title = ref(`首页 / 产品档案 / `)

const dataSource = ref(null)
const productDetailList = reactive([])
let isSave = false

let processStatus1 = {
  allocation_create: '制单',
  allocation_wait_out: '等待入库',
  allocation_out_ing: '入库中',
  allocation_ing: '调拨中',
  allocation_in_ing: '入库中',
  allocation_doc: '归档'
}

let processStatus = {
  in_create: '制单',
  in_wait_out: '等待入库',
  in_ing: '入库中',
  in_doc: '归档',
  in_cancel: '作废'
}

const goBack = () => {
  if (formData.value.processStatus){
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
const inWarehouseCode = ref(-1)

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
  if (route.query.id){
    editControlCommon.formTimeAndNumber.push(...[{
      title: "总数量",
      key: 'totalNumber',
      element: 'number',
      disabled: true
    }, {
      title: "总体积",
      key: 'totalVolume',
      element: 'number',
      disabled: true
    }])
  }
  if (route.query.type !== 'product'){
    dataSource.value.tableHeader.unshift({
      title: "调拨单号",
      dataKey: "allocationCode",
      key: 'allocationCode',
      width: 200,
      type: 'text'
    })
  }
  formData.value = { type: route.query.type === 'product' ? '普通入库' : '调拨入库' }
  setTimeout(() => {
    editControlCommon.changeData = !editControlCommon.changeData
  })
  if (route.query.id) getDetail()
}

const productSubdividelist = reactive([])
const productPositionlist = reactive([])
const productScanlist = reactive([])
const getDetail = (val) => {

  myId.value = route.query.id ? route.query.id : val.id
  inWarehouseCode.value = route.query.inWarehouseCode ? route.query.inWarehouseCode : val.inWarehouseCode

  dataSource.value.getInDetail(myId.value).then(res => {
    if (res.code === 200){
      formData.value = { ...res.data, totalVolume: +res.data.totalVolume, totalNumber: +res.data.totalNumber }
      editControlCommon.changeData = !editControlCommon.changeData

      productDetailList.length = 0
      productSubdividelist.length = 0
      productPositionlist.length = 0
      productScanlist.length = 0

      dataSource.value.getInProductList(inWarehouseCode.value).then(res => {
        productDetailList.push(...res.rows)
        if (route.query.type === 'allocation'){

          orderList.length = 0
          productDetailList.forEach(item => {
            if (!orderList.includes(item.allocationCode)){
              orderList.push(item.allocationCode)
            }
          })
        }
      })

      dataSource.value.getInProductDetailList(inWarehouseCode.value).then(res => {
        productSubdividelist.push(...res.rows)

      })

      dataSource.value.getInPositionList(inWarehouseCode.value).then(res => {
        productPositionlist.push(...res.rows)
      })

      dataSource.value.getInScanList(inWarehouseCode.value).then(res => {
        productScanlist.push(...res.rows)
      })

    }
  })

}

const formData = ref({})

const editControlCommon = reactive({
  changeData: false,
  formInputEl: [{
    title: "入库仓",
    key: 'warehouseName',
    element: 'input',
    type: 'dialog',
    rules: [{
      required: true,
      message: '该项不能为空',
      trigger: 'blur'
    }]
  }, {
    title: "验货人",
    key: 'inspectionPerson',
    element: 'input'

  }],
  formSelectEl: [{
    title: "入库单类型",
    key: 'type',
    element: 'select',
    option: [{
      value: '调拨入库',
      label: '调拨入库'
    }, {
      value: '普通入库',
      label: '普通入库'
    }],
    disabled: true
  }],
  formTextAreaEl: [{
    title: "备注",
    key: 'remark',
    element: 'input',
    type: 'textarea'
  }],
  formTimeAndNumber: [],
  formUploadEl: [],
  emitOpenDialog: (val) => {
    openSelectDialog(val)
  },
  inputDone: async (val, type) => {
    if (productDetailList.length === 0){
      proxy.$message.error(`入库产品列表不能为空`)
      return
    }
    isSave = true
    if (!val){
      let params = proxy.$refs.dialogForm.manualEmitData()
      params.inWarehouseOrderDetailList = productDetailList
      if (params.id){
        let res = await dataSource.value.updateInOrder(params)
        if (res.code === 200){
          proxy.$message.success(`更新成功`)
          if (type === 'saveAndGo') router.go(-1)
          else getDetail()
        }
      } else {
        let res = await dataSource.value.addInOrder(params)
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
  if (column.property === 'processStatus') return processStatus1[row[column.property]] ?? '-'
  return row[column.property] ?? '-'
}

let orderList = reactive([])

const openSelectDialog = async (type, append) => {
  switch (type){
  // 选择数据

  case 'allocation': {
    if (!formData.value.warehouseName){
      proxy.$message.error('请先选择入库仓！')
      return
    }
    selectTableData.title = '请选择调拨单'
    selectTableData.placeholder = '输入调拨单号查找'
    selectTableData.needSelection = true
    selectTableData.dataSource = new DataSource({
      selectUri: '/wms/allocationOrderMain/list',
      listMethod: 'GET',
      pageSize: 10,
      tableHeader: [
        {
          title: "流程状态",
          dataKey: "processStatus",
          key: 'processStatus',
          width: 200

        }, {
          title: "调拨单号",
          dataKey: "allocationCode",
          key: 'allocationCode',
          width: 200

        }, {
          title: "入库名称",
          dataKey: "toWarehouseName",
          key: 'toWarehouseName',
          width: 200

        }, {
          title: "提库名称",
          dataKey: "warehouseName",
          key: 'warehouseName',
          width: 200

        }, {
          title: "调拨总数量",
          dataKey: "totalNumber",
          key: 'totalNumber',
          width: 200

        }, {
          title: "调拨总体积",
          dataKey: "totalVolume",
          key: 'totalVolume',
          width: 200
        }
      ]
    })
    selectTableData.dataSource.forMatDataV2 = forMatDataPro
    selectTableData.submit = () => {

      selectTableData.dataSource.selections.forEach(item => {
        if (!orderList.includes(item.allocationCode))
          orderList.push(item.allocationCode)
      })

      dataSource.value.getAllocationDetail(orderList).then(res => {
        if (res.code === 200){
          productDetailList.length = 0
          productDetailList.unshift(...res.data)
          selectTableData.isShow = false
        }
      })

    }

    selectTableData.doSearch = (val) => {
      selectTableData.dataSource.searchData = { processStatus: 'allocation_ing', toWarehouseName: formData.value.warehouseName }
      if (val) selectTableData.dataSource.searchData.allocationCode = val
      selectTableData.dataSource.initData()
    }

    selectTableData.dataSource.searchData = { processStatus: 'allocation_ing', toWarehouseName: formData.value.warehouseName }
    await selectTableData.dataSource.initData()
    selectTableData.isShow = true
    return
  }
  case 'warehouseName': {
    selectTableData.title = '请选择入库仓'
    selectTableData.placeholder = '输入仓库名称搜索'
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
      if (type === 'warehouseName'){
        proxy.$refs.dialogForm.updateDialogInput({ warehouseName: val.warehouseName })
        proxy.$refs.dialogForm.updateDialogInput({ warehouseCode: val.warehouseCode })
        formData.value.warehouseName = val.warehouseName
        formData.value.warehouseCode = val.warehouseName
        openSelectDialog(route.query.type)
      }
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
  case 'inPositionCode': {
    selectTableData.title = '请选择入库仓位'
    selectTableData.placeholder = '输入仓位名称搜索'
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
      append.positionCode = val.warehouseCode
      append.positionName = val.warehouseName
      selectTableData.isShow = false
    }

    selectTableData.doSearch = async (val) => {
      selectTableData.dataSource.searchData = { warehouseName: formData.value.warehouseName, maxParentCode: formData.value.warehouseCode }
      if (val) selectTableData.dataSource.searchData.warehouseName = val
      await selectTableData.dataSource.initData()

      selectTableData.dataSource.total = selectTableData.dataSource.tableData.length
    }

    selectTableData.dataSource.searchData = { warehouseName: formData.value.warehouseName, maxParentCode: formData.value.warehouseCode }
    await selectTableData.dataSource.initData()

    selectTableData.dataSource.total = selectTableData.dataSource.tableData.length
    selectTableData.isShow = true
    return
  }

  case 'product': {
    if (!formData.value.warehouseName){
      proxy.$message.error('请先选择入库仓库')
      return
    }
    selectTableData.title = '请选择入库的产品'
    selectTableData.placeholder = '输入产品名称搜索'
    selectTableData.needSelection = true
    selectTableData.dataSource = new DataSource({
      selectUri: '/wms/product/list',
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
      productDetailList.unshift(...selectTableData.dataSource.selections)

      let temp = productDetailList.filter((item, index) => index === productDetailList.findIndex(i => {
        i.number = 0
        delete i.id
        return i.productCode === item.productCode
      }))
      productDetailList.length = 0
      productDetailList.push(...temp)

      proxy.$message.success('添加成功，重复数据已自动过滤')
      selectTableData.isShow = false
    }

    selectTableData.doSearch = (val) => {
      selectTableData.dataSource.searchData = { }
      if (val)selectTableData.dataSource.searchData.productName = val
      selectTableData.dataSource.initData()
    }

    await selectTableData.dataSource.initData()
    selectTableData.isShow = true
    return
  }

  default: return
  }
}

const handleCloseCondition = (tag, index) => {
  orderList.splice(index, 1)
  if (orderList.length === 0) {
    productDetailList.length = 0
    return
  }
  dataSource.value.getAllocationDetail(orderList).then(res => {
    if (res.code === 200){
      productDetailList.length = 0
      productDetailList.unshift(...res.data)
      selectTableData.isShow = false
    }
  })
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

const handelDelete = (val) => {

  productDetailList.splice(val.$index, 1)

}

const changeScanCode = (row) => {
  proxy.$prompt(`当前入库单：${route.query.inWarehouseCode}，当前条码：${row.barCode}，请输入需要替换的条码`, '提示', {
    confirmButtonText: '确定更换',
    cancelButtonText: '取消'

  })
    .then(({ value }) => {
      proxy.$alert(`是否确认将 ${row.barCode} 更换成 ${value} ？`, '提示', {
        type: 'info',
        showCancelButton: true,
        cancelButtonText: '取消',
        confirmButtonText: '确认更换',
        distinguishCancelAndClose: true,
        callback: (action) => {
          if (action === 'confirm') {
            let params = {
              inWarehouseCode: route.query.inWarehouseCode,
              oldBarCode: row.barCode,
              newBarCode: value
            }
            dataSource.value.changeBarCode(params).then((res) => {
              if (res.code === 200){
                proxy.$message.success('更换成功')
                getDetail()
              }
            })
          }
        }
      })
    })
    .catch(() => {
      //
    })
}

const isShowHandleOut = ref(false)

const handleOut = () => {
  if (!isShowHandleOut.value) {
    productSubdividelist.forEach(item => {
      isShowHandleOut.value = true
      item.isInNumber = 0
    })
  } else isShowHandleOut.value = false
}

const confirmOut = () => {
  let params = []
  let isOK = true
  productSubdividelist.forEach(item => {
    if (item.isInNumber !== 0){
      if (!item.positionCode || !item.positionName){
        proxy.$message.error('请补充仓位')
        isOK = false
        return
      }
      params.push({
        "inWarehouseCode": item.inWarehouseCode,
        "materialDetailId": item.id,
        "materialCode": item.productCode,
        "inWarehouseNumber": item.isInNumber,
        "positionCode": item.positionCode,
        "positionName": item.positionName,
        "barCodeHead": item.barCodeHead
      })
    }
  })
  if (!isOK) return
  if (params.length === 0) {
    proxy.$message.error('手动入库数量为0')
    return
  }

  proxy.$alert(`确认无误并进行手动入库？`, '提示', {
    type: 'info',
    showCancelButton: true,
    cancelButtonText: '取消',
    confirmButtonText: '确认入库',
    distinguishCancelAndClose: true,
    callback: (action) => {
      if (action === 'confirm') {
        dataSource.value.manualInWarehouse(params).then((res) => {
          if (res.code === 200){
            proxy.$message.success('入库成功')
            isShowHandleOut.value = false
            getDetail()
          }
        })
      }
    }
  })
}

const partDoc = () => {
  let params = { inWarehouseCode: inWarehouseCode.value }
  proxy.$alert(`确认将已经扫码的货物归档并增加库存？未扫码货物没有影响`, '提示', {
    type: 'info',
    showCancelButton: true,
    cancelButtonText: '取消',
    confirmButtonText: '确认入库',
    distinguishCancelAndClose: true,
    callback: (action) => {
      if (action === 'confirm') {
        dataSource.value.completeButLack(params).then((res) => {
          if (res.code === 200){
            proxy.$message.success('入库成功')
            isShowHandleOut.value = false
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