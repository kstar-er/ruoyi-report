<template>
  <div>
    <div class="mt10 mb20 " style="width: 80vw;margin:0 auto">
      <div class="title">
        单据原数据
      </div>
      <el-table :data="orginData" height="400">
        <el-table-column
          v-for="(item,idx) in originDataHeader" :key="idx"
          :width="200"
          :prop="item.resultCode" :label="item.resultName"
          :formatter="forMatData"
        />
      </el-table>
    </div>

    <div style="width: 80vw;margin:20px auto">
      <div class="title">
        生成的账单数据
      </div>
      <el-table :data="resultData" height="400">
        <el-table-column
          v-for="(item,idx) in resultDataHeader" :key="idx"
          :prop="item.resultCode" :label="item.resultName"
          :formatter="forMatData"
          width="200"
        />
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { reactive, watch, getCurrentInstance } from "vue"
const { proxy } = getCurrentInstance()
const props = defineProps({
  testData: {
    type: Object,
    default: () => {
      return {}
    }
  }
})

const originDataHeader = reactive([])
const orginData = reactive([])
const resultDataHeader = reactive([])
const resultData = reactive([])
watch(() => props.testData.resultNameList.length, () => {
  originDataHeader.length = 0
  orginData.length = 0
  resultDataHeader.length = 0
  resultData.length = 0
  originDataHeader.push(...props.testData.originNameList.filter(item => item.resultName && item.resultName.indexOf('外键') === -1))
  resultDataHeader.push(...props.testData.resultNameList.filter(item => item.resultName))
  orginData.push(...props.testData.originDataList.map(item => item.keyMap))
  resultData.push(...props.testData.resultDataList.map(item => item.data))

})

const forMatData = (row, column) => {
  if (!row[column.property] || row[column.property] === 'NULL' || row[column.property] === '0') return '-'
  if (column.label.indexOf('时间') !== -1 || column.label.indexOf('日期') !== -1){
    return new proxy.$DataTool().timeStamp2Time(+row[column.property], 'yyyy-MM-dd hh:mm:ss')
  }
  else return row[column.property] ?? '-'
}
</script>

<style lang="less" scoped>
.title{
  border-left: 6px solid #297aed;
  padding: 6px 10px;
  font-weight: 800;
  font-size: 15px;
}
</style>