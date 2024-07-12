<template>
  <div class="inline-block">
    <el-date-picker
      v-model="myValue"
      type="date"
      clearable
      class="mr10 "
      placeholder="请选择"
      style="width:200px"
      @change="valueChange"
    />
  </div>
</template>

<script setup>
import { ref, onBeforeMount, watch } from 'vue'
const emits = defineEmits(['valueChange'])
const props = defineProps({
  value: {
    type: Number || String,
    default: undefined
  }
})
const myValue = ref(null)

watch(() => props.value, () => {
  myValue.value = props.value
})

onBeforeMount(() => {
  myValue.value = props.value
})

const valueChange = (val) => {
  myValue.value = val
  emits('valueChange', isNaN(Date.parse(val)) ? null : Date.parse(val))
}
</script>