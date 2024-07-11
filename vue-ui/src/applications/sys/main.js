import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import installElementPlus from './plugins/element'
import { DataTool } from '@/js/tool-class/dataTool'
import useEventBus from '@/js/tool-class/eventBus'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import 'vant/lib/index.css'
import xButton from '@/components/common/xButton/index.js'

useEventBus() // 使用事件总线
if (process.env.VUE_APP_TITLE === 'dev'){
  const { config: { ip, devServer: { https, port } } } = require('@/../models')
  https ? document.querySelector('link[rel="icon"]').setAttribute('href', `https://${ip}:${port}/jxwl.ico`) :
    document.querySelector('link[rel="icon"]').setAttribute('href', `http://${ip}:${port}/test.ico`)
}

const app = createApp(App)

// 权限指令-表格按钮
app.directive('authority', (el, binding) => {
  const permissionsArr = JSON.parse(sessionStorage.getItem('userInfo'))?.permissions
  if (!permissionsArr) return
  if (permissionsArr.includes('*:*:*')) return
  if (!permissionsArr.includes(binding.value)){
    el.disabled = 'disabled'
    el.style.cursor = 'no-drop'
    el.title = '暂无权限'
  }
})

// 权限指令-操作栏按钮
app.directive('authorityHandle', (el, binding) => {
  const permissionsArr = JSON.parse(sessionStorage.getItem('userInfo'))?.permissions
  if (!permissionsArr) return
  if (permissionsArr.includes('*:*:*')) return
  if (!permissionsArr.includes(binding.value)){
    el.style.display = 'none'
  }
})

//  自动聚焦指令
app.directive('focus', (el, binding) => {
  let dom = el.querySelector('input')
  dom.focus()
})

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.config.globalProperties.$DataTool = DataTool

installElementPlus(app)

app.use(xButton)
app.use(store).use(router).mount('#app')