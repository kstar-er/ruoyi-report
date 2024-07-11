<template>
  <div style="padding:10px">
    <div class="header">
      <el-page-header @back="goBack">
        <template #content>
          <span class="route"> {{ title }} </span>
          <span class="route" style="color:#999">  {{ active }} </span>
        </template>
      </el-page-header>
    </div>
    <div class="detail-content">
      <el-button
        class="mb20"
        size="small"
        plain
        @click="backLevel"
      >
        返回上一级
      </el-button>
      <div class="content">
        <div
          v-for="(item,idx) in showingData" :key="idx"
          :class="item.warehouseType"
          draggable="true"
          @dragstart="dragStart($event, item)"
          @dragenter.prevent="enter($event, item)"
          @dragend.prevent="end($event, item)"
          @dragover.prevent="over"
          @click="changeLevel(item)"
        >
          <el-button
            class="dialog-close-btn delte-a" type="danger"
            icon="CloseBold"
            circle
            @click.stop="deleteRow(item)"
          />
          <el-button
            class="dialog-close-btn delte-a" type="primary"
            icon="Edit"
            circle
            style="left: 80%;top: 3%;"
            @click.stop="goCompile(item)"
          />

          <div class="line">
            <span class="label"> 类型：</span><span class="label-des">{{ warehouseType[item.warehouseType] ??'-' }}</span>
            <span class="label"> 名称：</span><span class="label-des">{{ item.warehouseName??'-' }}</span>
          </div>
          <div class="line">
            <span class="label"> 负责人：</span><span class="label-des">{{ item.warehouseKeeper ??'-' }}</span>
            <span class="label"> 电话：</span><span class="label-des">{{ item.linkmanPhone ??'-' }}</span>
          </div>
          <div v-if="item.warehouseType==='position'" class="line">
            <span class="label"> 行：</span><span class="label-des">{{ item.positionsLine??'-' }}</span>
            <span class="label"> 列：</span><span class="label-des">{{ item.positionsColumn ??'-' }}</span>
          </div>
          <div class="line">
            <span class="label"> 经度：</span><span class="label-des">{{ item.longitude??'-' }}</span>
            <span class="label"> 纬度：</span><span class="label-des">{{ item.latitude??'-' }}</span>
          </div>
          <div class="line">
            <span class="label"> 地址：</span>
            <span style="max-width:300px;color: red;text-decoration: underline;">
              {{ item.warehouseAddress ??'-' }}
            </span>
          </div>
          <div class="line">
            <span class="label"> 备注：</span> <span style="max-width:300px;color: red;text-decoration: underline;">
              {{ item.remark ??'-' }}
            </span>
          </div>
        </div>
        <div
          class="add" @dragover.prevent="over"
          @dragenter.prevent="enter($event, {type:'remove'})"
          @click="addRow"
        >
          <el-icon
            v-if="!moveStart" style="font-size:100px"
          >
            <Plus />
          </el-icon>
          <el-icon v-else style="font-size:100px">
            <Minus />
          </el-icon>
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
    />
  </div>
</template>

<script setup>
import { pbRequest } from "@/applications/sys/public/pbRequest/index"
import selectDialogTable from '@/components/public/selectDialogTable.vue'
import selectDialogForm from '@/components/public/selectDialogForm.vue'
import { ref, onBeforeMount, getCurrentInstance, reactive, watch } from "vue"
import { useRouter, useRoute } from 'vue-router'
import { DataSource, loading } from '../utils/warehouse'
const { proxy } = getCurrentInstance()
const router = useRouter()
const route = useRoute()
const title = ref(`仓库总览 / `)
const active = ref(`当前仓库位置：`)
const dataSource = ref(null)
const moveStart = ref(false)
const levelList = reactive([{ warehouseName: '主仓', id: '0' }])

const showingData = ref([])
const initDataSource = async () => {
  dataSource.value = new DataSource({
    modules: 'warehouse',
    selectUri: '/wms/warehouse/list',
    listMethod: 'GET',
    pageSize: 2000000
  })

  await dataSource.value.initData(this, proxy.$refs.table)

  showingData.value.push(...dataSource.value.tableData)
}

onBeforeMount(() => {
  initDataSource()
})

const goBack = () => {
  router.go(-1)
}

let changeTempStart = {}
let changeTempEnd = {}

const enter = (e, item) => {
  changeTempEnd = { ...item } // 进入时记录结束的是哪个
  console.log('我是进入事件', item)
}

const dragStart = (val, item) => {
  changeTempStart = { ...item } // 开始时记录开始的是哪个
  moveStart.value = true
  console.log('我是开始事件', item)
}

const end = (val, item) => {
  if (changeTempEnd.type === 'remove'){
    moveStart.value = false
    if (!levelList[levelList.length - 2] || levelList[levelList.length - 2].warehouseName === '主仓') return

    proxy.$alert(`是否确认将【${changeTempStart.warehouseName}】移出【${levelList[levelList.length - 1].warehouseName}】？`, '提示', {
      type: 'warning',
      showCancelButton: true,
      cancelButtonText: '再想想',
      confirmButtonText: '确认',
      callback: (action) => {
        if (action === 'cancel') return
        else {
          let params = {
            ...changeTempStart
          }
          params.parentId = levelList[levelList.length - 2].id
          dataSource.value.updateWarehouse(params).then(res => {
            if (res.code === 200){
              proxy.$message.success('移动成功：' + changeTempStart.warehouseName + '移动到' + levelList[levelList.length - 2].warehouseName)
              dataSource.value.searchWarehouseDataById(this, { warehouseName: levelList[levelList.length - 1].warehouseName }).then(res => {
                showingData.value.length = 0
                showingData.value.push(...res[0].children)
              })
            }
          })
        }
      }
    })

    return
  }

  moveStart.value = false
  if (changeTempStart.warehouseType === changeTempEnd.warehouseType && changeTempEnd.warehouseType === 'warehouse') return
  if (changeTempStart.warehouseName === changeTempEnd.warehouseName) return

  proxy.$alert(`是否确认将【${changeTempStart.warehouseName}】移动到【${changeTempEnd.warehouseName}】？`, '提示', {
    type: 'warning',
    showCancelButton: true,
    cancelButtonText: '再想想',
    confirmButtonText: '确认',
    callback: (action) => {
      if (action === 'cancel') return
      else {
        let params = {
          ...changeTempStart
        }
        params.parentId = changeTempEnd.id
        dataSource.value.updateWarehouse(params).then(res => {
          if (res.code === 200){
            proxy.$message.success('移动成功：' + changeTempStart.warehouseName + '移动到' + changeTempEnd.warehouseName)
            getCurrentData()
          }
        })
      }
    }
  })

}

const over = () => true

const warehouseType = {
  warehouse: '仓库',
  area: '库区',
  frame: '货架',
  position: '仓位'

}

const changeLevel = (item) => {
  dataSource.value.searchWarehouseDataById(this, { warehouseCode: item.warehouseCode }).then(res => {
    levelList.push({ warehouseName: item.warehouseName, warehouseCode: item.warehouseCode, id: item.id })
    showingData.value.length = 0
    showingData.value.push(...res[0].children)
  })

}

watch(() => levelList.length, () => {
  active.value = `当前仓库位置：` + levelList.map(item => item.warehouseName).join(' / ')
}, { immediate: true })

const backLevel = async () => {

  if (levelList.length === 1){
    proxy.$message.error('已经在最顶层了')
    return
  } else {
    levelList.splice(levelList.length - 1, 1)
    if (levelList.length === 1){
      await dataSource.value.initData(this, proxy.$refs.table)
      showingData.value.length = 0
      showingData.value.push(...dataSource.value.tableData)
    } else {
      dataSource.value.searchWarehouseDataById(this, { warehouseCode: levelList[levelList.length - 1].warehouseCode }).then(res => {
        showingData.value.length = 0
        showingData.value.push(...res[0].children)
      })
    }
  }

  // null
}

const formData = ref({})

const editControlCommon = reactive({
  isShow: false,
  formInputEl: [{
    title: "仓库名称",
    key: 'warehouseName',
    element: 'input',
    rules: [{
      required: true,
      message: '该项不能为空',
      trigger: 'blur'
    }]
  }, {
    title: "负责人",
    key: 'warehouseKeeper',
    element: 'input'
  }, {
    title: "联系电话",
    key: 'linkmanPhone',
    element: 'input'
  }, {
    title: "经纬度",
    key: 'mapLongitude',
    element: 'input',
    illustrate: '使用空格分隔经纬度；'
  }],
  formSelectEl: [{
    title: "仓库类型",
    key: 'warehouseType',
    element: 'select',
    options: [{
      value: 'warehouse',
      label: '仓库'
    }, {
      value: 'area',
      label: '库区'
    }, {
      value: 'frame',
      label: '货架'
    }, {
      value: 'position',
      label: '仓位'
    }],
    rules: [{
      required: true,
      message: '该项不能为空',
      trigger: 'blur'
    }]

  }, {
    title: "仓库上级",
    key: 'parentId',
    element: 'selectTree',
    options: [],
    rules: [{
      required: true,
      message: '该项不能为空',
      trigger: 'blur'
    }]

  }],
  formTextAreaEl: [{
    title: "详细地址",
    key: 'warehouseAddress',
    element: 'input',
    type: 'textarea'
  }, {
    title: "备注",
    key: 'remark',
    element: 'input',
    type: 'textarea'
  }],
  formTimeAndNumber: [{
    title: "面积/m³",
    key: 'capacity',
    element: 'number'
  }, {
    title: "行",
    key: 'positionsLine',
    element: 'number'
  }, {
    title: "列",
    key: 'positionsColumn',
    element: 'number'
  }],
  formUploadEl: [],
  emitOpenDialog: (val) => {
    openSelectDialog(val)
  },
  inputDone: async (val) => {
    let params = {
      ...val
    }

    if (params.warehouseType === 'warehouse' && params.parentId !== '0'){
      proxy.$message.error(`保存失败，仓库只能添加到最顶层`)
      return
    }
    if (params.warehouseType !== 'warehouse' && params.parentId === '0'){
      proxy.$message.error(`保存失败，最顶层只能添加仓库`)
      return
    }
    if (params.id){
      let res = await dataSource.value.updateWarehouse(params)
      if (res.code === 200){
        proxy.$message.success(`更新成功`)
        dataSource.value.initData(this, proxy.$refs.table)
        getCurrentData()
        editControlCommon.isShow = false
      }
    } else {
      let res = await dataSource.value.addWarehouse(params)
      if (res.code === 200){
        proxy.$message.success(`新增成功`)
        getCurrentData()
        editControlCommon.isShow = false
      }
    }

  }
})

const goCompile = async (rowData) => {
  editControlCommon.formSelectEl[1].options.length = 0
  editControlCommon.formSelectEl[1].options.push(...dataSource.value.tableData)
  editControlCommon.formSelectEl[1].options.unshift({
    label: '最顶层',
    value: '0',
    children: []
  })
  formData.value = { ...rowData, parentId: rowData.parentId.toString(),
    capacity: +rowData.capacity, positionsLine: +rowData.positionsLine,
    positionsColumn: +rowData.positionsColumn }

  editControlCommon.isShow = true

}

const addRow = async (rowData) => {

  formData.value = { parentId: levelList[levelList.length - 1].id.toString() }
  editControlCommon.formSelectEl[1].options.length = 0
  editControlCommon.formSelectEl[1].options.push(...dataSource.value.tableData)
  editControlCommon.formSelectEl[1].options.unshift({
    label: '最顶层',
    value: '0',
    children: []
  })

  editControlCommon.isShow = true
}

const deleteRow = async (rowData) => {
  proxy.$alert(`是否确认删除此仓库？`, '提示', {
    type: 'error',
    showCancelButton: true,
    cancelButtonText: '再想想',
    confirmButtonText: '确认删除',
    callback: (action) => {
      if (action === 'cancel') return
      else {
        dataSource.value.deleteWarehouse(rowData.id).then(async (res) => {
          if (res.code === 200){
            proxy.$message.success('删除成功')
            getCurrentData()
          }
        })
      }
    }
  })
}

const getCurrentData = async () => {
  if (levelList[levelList.length - 1].warehouseCode){
    dataSource.value.searchWarehouseDataById(this, { warehouseCode: levelList[levelList.length - 1].warehouseCode }).then(res => {
      showingData.value.length = 0
      showingData.value.push(...res[0].children)
    })
  } else {
    await dataSource.value.initData(this, proxy.$refs.table)
    showingData.value.length = 0
    showingData.value.push(...dataSource.value.tableData)
  }
}
</script>

<style lang="less" scoped>
.father{
  padding: 10px;
  width: 100%;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  border: 1px solid rgb(220, 223, 230);

  color: rgb(151, 151, 151);

  margin-bottom: 50px;
  height: 20vh;
}

.content{
  width: 100%;
  box-sizing: border-box;
  display: flex;
  color: rgb(151, 151, 151);
  flex-wrap: wrap;
}
.area:hover,.frame:hover,.position:hover,.add:hover,.warehouse:hover{
  border-color: rgb(64, 158, 255);
  opacity: .8;
}

.area,.frame,.position,.add,.warehouse{
  transition: all .5s;
  position: relative;
  box-sizing: border-box;
  padding: 10px;
  width: 25%;
  height: 200px;
  cursor: pointer;
  color: #747474;
}

.add{
  display: flex;
  justify-content: center;
  align-items: center;
  border: 1px dashed rgb(220, 223, 230);
}
.area{
  border: 1px dashed rgb(220, 223, 230);
  background: rgba(102, 255, 64, 0.2);
}
.frame{
  border: 1px dashed rgb(230, 229, 220);
  background: rgba(255, 238, 0,.2);
}
.position{
  border: 1px dashed rgb(230, 229, 220);
  background: rgba(255, 0, 119, 0.2);
}
.warehouse{
  border: 1px dashed rgb(230, 229, 220);
  background: rgba(5, 116, 206, 0.2);
}
.label-des{
  display: inline-block;
  color: red;
  text-decoration: underline;
  margin-right: 10px;
  width: 80px;
}
.label{
  display: inline-block;
  width: 70px;
}
.line{
  font-size: 14px;
  display: flex;
  justify-content: flex-start;
  margin-bottom: 10px;
}

.delte-a{
  position: absolute;
  left: 90%;
  top: 3%;
}

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
</style>
