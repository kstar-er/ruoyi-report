<template>
  <el-dropdown
    trigger="click" @command="doAction"
    @visibleChange="visibleChange"
  >
    <div class="el-dropdown-link flex-center" style="outline:none;">
      {{ title }}
      <el-icon class="el-icon--right">
        <arrow-down />
      </el-icon>
    </div>
    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item
          v-for="item in optionsList" :key="item.command"
          :command="item.command"
        >
          {{ item.label }}
        </el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>

<script setup>
import { onBeforeMount, reactive } from 'vue'
const optionsList = reactive([])
const props = defineProps({
  title: {
    type: String,
    default: '更多'
  },
  needPerList: {
    type: Object,
    default: () => {
      return []
    }
  }
})

const _emits = defineEmits(['doAction'])

const doAction = (val) => _emits('doAction', val)

const visibleChange = (val) => {
  optionsList.length = 0
  const permissionsArr = JSON.parse(sessionStorage.getItem('userInfo')).permissions
  if (permissionsArr.includes('*:*:*')) {
    optionsList.push(...props.needPerList)
  } else {
    props.needPerList.forEach(item => {
      if (permissionsArr.includes(item.per)) {
        optionsList.push(item)
      }
    })
  }
}
</script>
<style scoped lang="less">
:deep(.el-dropdown-link){
  cursor: pointer;
  color: var(--el-color-primary);
  display: flex;
  align-items: center;
}

:deep(.example-showcase){
  cursor: pointer;
  color: var(--el-color-primary);
  display: flex;
  align-items: center;
}
</style>
