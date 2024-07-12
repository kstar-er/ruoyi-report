
<template>
  <div class="mytabNav" :style="{background:bgColor,color:textColor}">
    <div class="tab-list">
      <span>讯序连统一管理后台</span>
    </div>

    <smallOperation />
  </div>
</template>

<script setup>
import smallOperation from '@/components/public/smallOperation.vue'
import { onMounted, ref } from 'vue'

const emits = defineEmits(['changeCollapse'])
const bgColor = ref('#fff')
const textColor = ref('#000')
const props = defineProps({
  showChangeCollapse: {
    type: Boolean,
    default: false
  }
})

onMounted(() => {
  if (localStorage.getItem('collapse') && localStorage.getItem('collapse') === 'true'){
    isCollapse.value = true
    emits('changeCollapse', isCollapse.value)
  }
  window.$eventBus.$on('changeTheLight', (val) => {
    if (!val) {
      bgColor.value = '#126f9e'
      textColor.value = '#fff'
    } else {
      bgColor.value = '#fff'
      textColor.value = '#000'
    }
  })
})

const isCollapse = ref(false)

const changeCollapse = () => {
  isCollapse.value = !isCollapse.value
  localStorage.setItem('collapse', isCollapse.value)
  emits('changeCollapse', isCollapse.value)
}
</script>

<script>

</script>

<style lang="less" scoped>
.mytabNav{
  padding: 6px 0 6px 20px;
  position: relative;
  display: flex;
  height: 40px;
  align-items: center;
  justify-content: space-between;
  transition: .5s;
}
.tab-list{
  display: flex;
  align-items: center;
}
</style>