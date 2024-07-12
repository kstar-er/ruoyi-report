<template>
  <div>
    <router-view />
  </div>
</template>

<script>
export default {
  name: 'App'
}
</script>

<script setup>
const debounce = (fn, delay) => {
  let timer = null
  return function() {
    let context = this
    let args = arguments
    clearTimeout(timer)
    timer = setTimeout(function() {
      fn.apply(context, args)
    }, delay)
  }
}

function throttle(func, wait) {
  let previous = 0
  return function() {
    let now = Date.now()
    let context = this
    let args = arguments
    if (now - previous > wait) {
      func.apply(context, args)
      previous = now
    }
  }
}

const _ResizeObserver = window.ResizeObserver
window.ResizeObserver = class ResizeObserver extends _ResizeObserver {
  constructor(callback) {
    callback = debounce(callback, 16)
    super(callback)
  }
}
</script>

<script>

</script>

<style lang="less">
@import '@/css/dialog.less';
@import '@/css/drawer.less';
@import '@/css/editPage.less';
@import '@/css/extraHandle.less';
@import './public/css/default.less';
@import '@/css/messageBox.less';
html,
body {
  transition: .5s;
  padding: 0;
  margin: 0;
  background: @bg-gray;
  color: @text;
  backface-visibility: hidden;
  background: #f6f6f6;
}

* {
  scrollbar-color: #e5e5e5 #f7f7f9; /* 滑块颜色  滚动条背景颜色 */
  scrollbar-width: thin; /* 滚动条宽度有三种：thin、auto、none */
  font-family: 'Microsoft YaHei', '微软雅黑', Arial, sans-serif;
}

#nprogress .bar {
  background: #126f9e !important;
  height: 2px !important;
  z-index: 2001 !important;
  border-radius: 20px !important;
  box-shadow: 0px 0px 20px 2px #126f9e;
}

.spinner-icon{
  border-top-color: #126f9e!important;
  border-left-color: #e307dc!important;
}

::-webkit-scrollbar-track-piece {
  background-color: #F0F2F5;
}

::-webkit-scrollbar {
  transition: .5s;
  width: 10px;
  height: 9px;
}

::-webkit-scrollbar-thumb {
  background-color: gray;
  background-clip: padding-box;
  min-height: 28px;
}

::-webkit-scrollbar-thumb:hover {
  background-color: #bbb;
}
input:-webkit-autofill , textarea:-webkit-autofill, select:-webkit-autofill {
	-webkit-box-shadow: 0 0 0px 1000px transparent  inset !important;
  background-color:transparent;
  background-image: none;
  transition: background-color 50000s ease-in-out 0s;
}
/** 滚动条美化 **/
/*定义滚动条高宽及背景 高宽分别对应横竖滚动条的尺寸*/
::-webkit-scrollbar {
	width: 6px;
	height: 8px;
	background-color: #c8c9cc;
  display: none;
}

/*定义滚动条轨道 内阴影+圆角*/
::-webkit-scrollbar-track {
	-webkit-box-shadow: inset 0 0 6px rgba(0, 0, 0, 0.3);
	border-radius: 10px;
	background-color: #c8c9cc;
}

/*定义滑块 内阴影+圆角*/
::-webkit-scrollbar-thumb {
	border-radius: 10px;
	-webkit-box-shadow: inset 0 0 6px rgba(0, 0, 0, .3);
	background-color: #c8c9cc;
}
</style>
