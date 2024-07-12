<template>
  <div>
    <div
      v-for="(item,idx) in dataSource.tableData" :key="item.id"
      class="list-item"
    >
      <span>{{ idx+1 }}、</span>
      <el-link
        type="primary" class="link"
        @click="goEdit(item)"
      >
        {{ item.conditionName ?? '-' }}
      </el-link>
      <el-checkbox v-model="item.checked" label="" />
    </div>

    <div class="flex-center mt10">
      <el-pagination
        layout="prev, pager, next" :total="dataSource.total"
        background
        small
        :pager-count="5"
      />
    </div>
  </div>
</template>

<script setup>
import {} from 'vue'

const props = defineProps({
  dataSource: {
    type: Object,
    default: () => {
      return {}
    }
  }
})

const emits = defineEmits(['edit'])

const goEdit = (val) => {
  emits('edit', val)
}
</script>

<style scoped lang="less">
.list-item{
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 6px;
  margin: 6px 0;
  border: 1px dashed #a0cfff;
}

.list-item:hover{
  background: #f6f6f6;
}

.link{
  width: 70%;
  overflow: hidden;
}
</style>