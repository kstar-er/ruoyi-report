<template>
  <div class="expand-tableData">
    <div>
      <div
        v-for="item in detailsTableHeaderData" :key="item.prop"
        class="descriptions"
      >
        <!-- <div class="descriptions-idx">
          {{ idx+1 }}
        </div> -->
        <div class="descriptions-item long">
          <span class="descriptions-item-span">物料代码: </span>
          <span class="descriptions-item-span"> {{ item.materialCode }}</span>
        </div>
        <div class="descriptions-item dlong">
          <span class="descriptions-item-span">型号：</span>
          <span class="descriptions-item-span"> {{ item.materialSpecification }}</span>
        </div>
        <div class="descriptions-item">
          <span class="descriptions-item-span">数量：</span>
          <span class="descriptions-item-span"> {{ item.number }}</span>
        </div>
        <div class="descriptions-item">
          <span class="descriptions-item-span">体积：</span>
          <span class="descriptions-item-span"> {{ item.volume.toFixed(2) + ' m³' }}</span>
        </div>
        <div v-if="item.materialStatus" class="descriptions-item">
          <span class="descriptions-item-span">品质：</span>
          <span class="descriptions-item-span"> {{ item.materialStatus==='BAD'?'不良品':'良品' }}</span>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup>
const props = defineProps({
  detailsTableHeader: {
    type: Array,
    default(){
      return []
    }
  },
  detailsTableHeaderData: {
    type: Array,
    default(){
      return []
    }
  }
})
const detailsDataForMatData = (row, column) => {
  if (column.label.indexOf('时间') !== -1){
    return changeTime(row[column.property])
  }
  else return row[column.property]
}
</script>

<style lang="less" scoped>
.expand-tableData{
    width: 60vw;
    margin-left: 4vw;
}
.descriptions{
  display: flex;
  align-items: center;
  &-idx{
    width: 28px;
    height: 22px;
    border: 1px solid #303133;
    border-radius: 50%;
    display: flex;
    justify-content: center;
    align-items: center;
    font-size: 12px;
    color: #303133;
  }
  &-item{
    padding: 8px;
    width: 150px;
    margin-left: 20px;
    &-span{
    font-weight: 700;
  }
  }
  .dlong{
    width: 600px;
  }
  .long{
    width: 250px;
  }

}
</style>