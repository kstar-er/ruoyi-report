<template>
  <div v-if="isShow">
    <el-dialog
      v-model="showDialog"
      top="4%"
      width="80%"
      :close-on-click-modal="false"
      :show-close="false"
      lock-scroll
    >
      <template #header="{ titleId, titleClass }">
        <div class="jx-dialog-header">
          <h4 :id="titleId" :class="titleClass">
            构造计算表达式
            <slot name="customOperation" />
          </h4>
          <el-button
            class="dialog-close-btn"
            type="danger" icon="CloseBold"
            circle
            plain
            @click="closeDialog"
          />
        </div>
      </template>
      <div>
        <component
          :is="condition" :is-component="true" :condition-item="conditionItem"
        />
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import condition from '../conditionsConfig/index.vue'
import { ref, getCurrentInstance, reactive, computed, watch } from 'vue'

const { proxy } = getCurrentInstance()
const _props = defineProps({
  // 是否展示选择框
  isShow: {
    type: Boolean,
    default: false
  },

  conditionItem: {
    type: Object,
    default: () => {
      return {}
    }
  }

})

const _emits = defineEmits(['closeDialog', 'inputDone', 'emitOpenDialog'])
const closeDialog = () => {
  _emits('closeDialog') // 关闭弹窗事件
}
const submitForm = () => {

  _emits('inputDone')
}

const showDialog = computed({
  get(){
    return _props.isShow
  },
  set(){
    //null
  }
}) // 开启弹窗事件
</script>

<style lang="less" scoped>
.expression{
  margin: 10px 0;
  border: 1px dashed rgb(36, 173, 243);
  padding: 10px;
  padding-top: 0;
}
.tag{
  display: inline-block;
  background: #24adf3;
  padding: 4px 8px;
  color:#fff;
  border-radius: 4px;
  font-size: 12px;
}

.move{
  position: absolute;
}
</style>