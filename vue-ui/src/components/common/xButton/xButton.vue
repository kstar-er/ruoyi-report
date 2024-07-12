<template>
  <div style="  display: inline-block;">
    <button
      class="button" :class="type" type="button"
    >
      <slot />
    </button>
  </div>
</template>

<script setup>
import { onBeforeMount, ref } from 'vue'
const props = defineProps({
  type: {
    type: String,
    default: 'primary'
  }
})
</script>

<style scoped lang="less">
.primary:hover {
 background-color: rgb(64, 158, 255);
 color: #fff;
}

.primary {
  background-color: rgb(231, 242, 255);
  border-color: rgba(64, 158, 255,.7) !important;
  color: #409eff;

}

.primary::after{
  background:#76bafd;
}

.info:hover{
  background-color: #5a5a5a;
  color: #fff;
}

.info {
  background-color: rgb(244, 244, 245);
  border-color: rgba(90, 90, 90,.7) !important;
  color: #4a494f;
}

.info::after{
  background:rgb(182, 182, 182);
}

.warning:hover {
  background-color: #f59505;
  color: #fff;
}

.warning {
  background-color: rgb(253, 246, 236);
  border-color: rgba(245, 149, 5,.7) !important;
  color: #f59505;
}

.warning::after{
  background:rgb(236, 179, 94);
}

.danger:hover {
  background-color: #f56c6c;
  color: #fff;
}

.danger {
  background-color: #fef0f0;
  border-color: rgba(245, 108, 108,.7) !important;
  color: #f56c6c;
}

.danger::after{
  background:#f56c6c;
}

.button{
  position: relative;
  border: 1px solid transparent;
  font-size: 13px;
  padding: 6px 16px;
  text-align: center;
  transition-duration: 0.6s;
  text-decoration: none;
  overflow: hidden;
  cursor: pointer;
  border-radius: 4px;
  font-weight: 500;
}
.button::after{
  content: '';
  display: block;
  position: absolute;
  padding-top: 300%;
  padding-left: 350%;
  margin-left: -20px !important;
  margin-top: -120%;
  opacity: 0;
  transition:all 1.2s;
}
.button:active::after{
  padding: 0;
  margin: 0;
  opacity: 1;
  transition: 0s;
}
</style>