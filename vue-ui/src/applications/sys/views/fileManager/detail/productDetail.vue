<template>
  <div>
    <div class="header">
      <el-page-header @back="goBack">
        <template #content>
          <span class="route"> {{ title }} </span>
          <span class="route" style="color:#999">  {{ active }} </span>
          <el-button
            type="warning" class="ml20"
            size="small"
            @click="saveAll"
          >
            保存所有
          </el-button>
        </template>
      </el-page-header>
    </div>
    <div class="detail-content">
      <el-card
        v-loading="loading" shadow="always"
        class="card mb20"
      >
        <template #header>
          <div class="card-header">
            <span>基础信息</span>
            <span style="font-size:12px;color:#ff3846" class="ml20">编辑完请记得保存</span>
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
            <span>子产品明细</span>
            <el-button
              type="primary" class="ml20"
              size="small"
              @click="openSelectDialog('addSon')"
            >
              添加子产品
            </el-button>
          </div>
        </template>

        <div
          v-if="productChirden.length===0" class="flex-center"
          style="color:#999;font-size:12px"
        >
          暂 无 子 产 品
        </div>
        <div v-else>
          <el-table
            :data="productChirden" style="width: 100%"
            border
            :header-cell-style="{ background: '#eeeeee', color: '#606266'}"
            stripe
          >
            <el-table-column
              v-for="(item) in dataSource.tableHeader"
              :key="item.prop"
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
      :search-input-placeholder="'输入产品名称搜索'"
      :is-show="selectTableData.isShow"
      :data-source="selectTableData.dataSource"
      :title="'选择数据'"
      :need-selection="selectTableData.needSelection"
      is-show-search-input
      :loading="loading"
      @close-dialog="selectTableData.isShow=false"
      @do-dialog-search="selectTableData.doSearch"
      @submit="selectTableData.submit"
    />
  </div>
</template>

<script setup>
import { pbRequest } from "@/applications/sys/public/pbRequest/index"
import selectDialogTable from '@/components/public/selectDialogTable.vue'
import selectDialogForm from '@/components/public/selectDialogForm.vue'
import { ref, onBeforeMount, getCurrentInstance, reactive } from "vue"
import { useRouter, useRoute } from 'vue-router'
import { DataSource, loading } from "../utils/product"
import { DataSource as type } from '../utils/productType'
const { proxy } = getCurrentInstance()
const router = useRouter()
const route = useRoute()
const title = ref(`首页 / 产品档案 / `)
const active = ref(`${route.query.productName} 明细`)
const dataSource = ref(null)
const resType = ref(null)

const productChirden = reactive([])

const goBack = () => {
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
        saveAll('justGo')
      } else if (action === 'cancel'){
        router.go(-1)
      }
    }

  })
}

onBeforeMount(() => {
  initData()

  // const fn1 = (a = [
  //   1, 2, 3, 5, 6, 10
  // ], b = [{ 5: 2 }, { 6: 3 }, { 4: 2 }, { 3: 1 }]) => {
  //   let tons = Object.values(b) // 获取b能装的吨数
  //   let res = 0 // 能运的吨数
  //   a = a.sort() // 由大到小排序，因为求最大能运多少，肯定是先装大的
  //   a.forEach(target => {
  //     // 在tons里面找到大于target的并且b[tons[i]]>0，如果找到就把 b[tons[i]]--, res += target
  //     // 找不到就算了
  //   })
  //   return res
  // }

  // console.log('答案:' + fn1('1344333'), '输入：1344333')
  // console.log('答案:' + fn1('13344333'), '输入：13344333')
  // console.log('答案:' + fn1('13444333'), '输入：13444333')
  // console.log('答案:' + fn1('13244333'), '输入：13244333')
})

const initData = async () => {
  if (!dataSource.value){
    dataSource.value = new DataSource({
      modules: 'product',
      selectUri: '/wms/product/list',
      listMethod: 'GET',
      pageSize: 20
    })
    dataSource.value.initTableHeader()
  }

  resType.value = new type({
    selectUri: '/wms/category/list',
    listMethod: 'GET',
    pageSize: 1000000
  })
  await resType.value.initData(this, proxy.$refs.table)
  editControlCommon.formSelectEl[0].options.length = 0
  editControlCommon.formSelectEl[0].options.push(...resType.value.tableData)

  getDetail()

  dataSource.value.getProductSon(route.query.id).then(res => {
    if (res.code === 200){
      productChirden.length = 0
      productChirden.push(...res.data)
    }
  })
  return
}

const handelDelete = (val) => {

  productChirden.splice(val.$index, 1)

}

const getDetail = () => {
  dataSource.value.getProductDetail(route.query.id).then(res => {
    if (res.code === 200){
      formData.value = { ...res.data, categoryId: res.data.categoryId?.toString(), height: +res.data.height,
        length: +res.data.length,
        volume: +res.data.volume
        , weight: +res.data.weight, width: +res.data.width }
      editControlCommon.changeData = !editControlCommon.changeData
    }
  })

}

const saveAll = (type) => {
  proxy.$refs.dialogForm.autoValid().then(res => {
    if (res){
      let params = { ...formData.value }
      params.children = [...productChirden]
      params.childrenCount = productChirden.length
      dataSource.value.addProduct([params]).then(res => {
        if (res.code === 200){
          if (type === 'justGo'){
            router.go(-1)
            return
          }
          proxy.$alert('保存成功', '提示', {
            showCancelButton: true,
            cancelButtonText: '放回上一页',
            confirmButtonText: '继续操作',
            distinguishCancelAndClose: true,
            type: 'success',
            icon: 'InfoFilled',
            callback: (action) => {
              if (action === 'confirm') {
                getDetail()
              } else if (action === 'cancel'){
                router.go(-1)
              }
            }
          })

        }
      })
    }
  })

}

const formData = ref({})

const editControlCommon = reactive({
  changeData: false,
  formInputEl: [
    {
      title: "产品名称",
      key: 'productName',
      element: 'input',
      rules: [{
        required: true,
        message: '该项不能为空',
        trigger: 'blur'
      }]
    }, {
      title: "产品代码",
      key: 'productCode',
      element: 'input',
      rules: [{
        required: true,
        message: '该项不能为空',
        trigger: 'blur'
      }]
    }, {
      title: "条码头",
      key: 'barCodeHead',
      element: 'input',
      rules: [{
        required: true,
        message: '该项不能为空',
        trigger: 'blur'
      }]
    }, {
      title: "产品型号",
      key: 'productSpecification',
      element: 'input'
    }, {
      title: "品牌",
      key: 'brand',
      element: 'input'
    }, {
      title: "单位",
      key: 'unit',
      element: 'input'
    }
  ],
  formSelectEl: [{
    title: "产品类别",
    key: 'categoryId',
    element: 'selectTree',
    options: [],
    change: (checkedNode) => {
      formData.value.categoryName = checkedNode.categoryName
      formData.value.categoryId = checkedNode.id
    }
  }, {
    title: "产品类型",
    key: 'productType',
    element: 'select',
    options: [{
      label: '单件',
      value: '单件'
    }, {
      label: '组合',
      value: '组合'
    }],
    rules: [{
      required: true,
      message: '该项不能为空',
      trigger: 'blur'
    }],
    change: (val) => {
      console.log(val)
    }
  }, {
    title: "使用统一条码",
    key: 'productBarcodeAlways',
    element: 'select',
    options: [{
      label: '是',
      value: true
    }, {
      label: '否',
      value: false
    }],
    rules: [{
      required: true,
      message: '该项不能为空',
      trigger: 'blur'
    }]
  }],
  formTextAreaEl: [{
    title: "备注",
    key: 'remark',
    element: 'input',
    type: 'textarea'
  }],
  formTimeAndNumber: [
    {
      title: "重量/KG",
      key: 'weight',
      element: 'number',
      type: 'textarea'
    }, {
      title: "体积/V",
      key: 'volume',
      element: 'number',
      type: 'textarea'
    }, {
      title: "长/M",
      key: 'length',
      element: 'number',
      type: 'textarea'
    }, {
      title: "宽/M",
      key: 'width',
      element: 'number',
      type: 'textarea'
    }, {
      title: "高/M",
      key: 'height',
      element: 'number',
      type: 'textarea'
    }
  ],
  formUploadEl: [],
  emitOpenDialog: (val) => {
    openSelectDialog(val)
  },
  inputDone: async (val) => {
    let params = {
      ...val
    }

  }
})

const forMatDataPro = (row, column) => {
  if (column.property === 'productBarcodeAlways') return row[column.property] ? '是' : '否'
  return row[column.property] ?? '-'
}

const openSelectDialog = async (val) => {
  switch (val){
  // 选择数据
  case 'addSon': {
    selectTableData.dataSource = dataSource.value
    selectTableData.needSelection = true

    selectTableData.dataSource.forMatDataV2 = forMatDataPro

    selectTableData.submit = () => {
      productChirden.unshift(...selectTableData.dataSource.selections)

      let temp = productChirden.filter((item, index) => index === productChirden.findIndex(i => i.id === item.id) && item.id !== route.query.id)
      productChirden.length = 0
      productChirden.push(...temp)

      proxy.$message.success('添加成功，重复数据已自动过滤')
      selectTableData.isShow = false
    }

    selectTableData.doSearch = (val) => {
      selectTableData.dataSource.searchData = { productType: '单件' }
      if (val)selectTableData.dataSource.searchData.productName = val
      selectTableData.dataSource.initData()
    }

    selectTableData.dataSource.searchData = { productType: '单件' }
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
</style>