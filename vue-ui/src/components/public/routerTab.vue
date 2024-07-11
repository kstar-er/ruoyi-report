
<template>
  <div class="container">
    <el-scrollbar>
      <div class="tab-list">
        <div
          v-for="item,idx in openTab"
          :key="item.name"
          class="tabs flex-center"
          :class="
            { 'active': activeIndex===idx, 'closing': item.isClosing }
          "

          @click="changeRoute(idx,item.path,item.query)"
        >
          <div
            class="c" :style="activeIndex===idx?{background:'#126f9e'}:{}"
            :class="
              { 'c-active': activeIndex===idx }"
          />
          <span
            class="mr10 hidden text-hidden" :title="item.meta.title"
            style="max-width:200px"
          >
            {{ item.meta.title }}
          </span>
          <el-icon
            class="close"
            :class="'active-close'"
            @click.stop="removeTab(idx)"
          >
            <CloseBold />
          </el-icon>
        </div>
        <el-tooltip
          class="box-item "
          effect="dark"
          content="关闭其他选项卡"
          placement="bottom"
        >
          <el-button
            class="ml10"
            circle
            icon="Delete"
            type="info"
            size="small"
            @click="closeOrther"
          />
        </el-tooltip>
      </div>
    </el-scrollbar>
  </div>
</template>

<script setup>
import { onBeforeMount, onMounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'

const emits = defineEmits(['changeCollapse'])
const activeColor = ref('#126f9e')
const props = defineProps({
  bgColor: {
    type: String,
    default: '#fff'
  },
  color: {
    type: String,
    default: '#666'
  },
  userColor: {
    type: String,
    default: '#fff'
  }
})
const activeIndex = ref(0)
const openTab = reactive([])
const route = useRoute()
const router = useRouter()

watch(() => route.path, (newValue, oldValue) => {

  let sign = openTab.findIndex(item => item.path === newValue)

  if (sign < 0){
    let temp = {
      name: route.matched[route.matched.length - 1].name,
      path: route.matched[route.matched.length - 1].path,
      meta: route.matched[route.matched.length - 1].meta,
      query: route.query
    }
    openTab.push(temp)
    let routerMapIdx = router.getRoutes().findIndex(item => item.path === openTab[openTab.length - 1].path)
    if (route.path.indexOf('Detail') === -1) router.getRoutes()[routerMapIdx].meta.keepAlive = true

    activeIndex.value = openTab.length - 1
  } else {
    openTab.length = 0
    openTab.push(...JSON.parse(sessionStorage.getItem('routerTabs')))
    activeIndex.value = sign
  }
}, { immediate: true })

watch(() => openTab.length, () => {
  sessionStorage.setItem('routerTabs', JSON.stringify(openTab))
  sessionStorage.setItem('activeIndex', JSON.stringify(activeIndex.value))
})

watch(activeIndex, () => {
  sessionStorage.setItem('activeIndex', JSON.stringify(activeIndex.value))
})

onBeforeMount(() => {
  if (sessionStorage.getItem('routerTabs')){
    openTab.length = 0
    openTab.push(...JSON.parse(sessionStorage.getItem('routerTabs')))
  }
  if (sessionStorage.getItem('activeIndex')) activeIndex.value = JSON.parse(sessionStorage.getItem('activeIndex'))
  sessionStorage.setItem('routerTabs', JSON.stringify(openTab))
  sessionStorage.setItem('activeIndex', JSON.stringify(activeIndex.value))
  window.addEventListener('resize', getWindowInfo)
})

const tabMaxCount = ref(10)
const getWindowInfo = () => {
  const windowInfo = {
    width: window.innerWidth,
    hight: window.innerHeight
  }
  if (windowInfo.width && windowInfo.width > 1500){
    tabMaxCount.value = +((windowInfo.width - 200) / 200).toFixed(0)
  } else tabMaxCount.value = +((windowInfo.width - 350) / 400).toFixed(0) - 1
  if (tabMaxCount.value < 0) tabMaxCount.value = 0

}

onMounted(() => {
  if (localStorage.getItem('collapse') && localStorage.getItem('collapse') === 'true'){
    isCollapse.value = true
    emits('changeCollapse', isCollapse.value)
  }

})

const changeRoute = (idx, path, query) => {
  activeIndex.value = idx
  router.push({
    path,
    query
  })
}

const isCollapse = ref(false)

const closeOrther = () => {
  openTab.forEach((item, idx) => {
    if (idx !== activeIndex.value && item.path.indexOf('Detail') === -1) changeRouterKeepAlive(false, idx)
  })
  let temp = openTab[activeIndex.value]
  openTab.length = 0
  openTab.push(temp)
  activeIndex.value = 0
}

const changeRouterKeepAlive = (keepAlive, routeIdx) => {
  let routerMapIdx = router.getRoutes().findIndex(item => item.path === openTab[routeIdx].path)
  if (router.getRoutes()[routerMapIdx])
    router.getRoutes()[routerMapIdx].meta.keepAlive = keepAlive

}

const removeTab = (idx) => {
  if (openTab.length === 1){
    ElMessageBox.alert('关闭最后一个标签页面将会退出登录，是否确定', '提示', {
      confirmButtonText: '确定退出',
      type: 'warning',
      icon: 'InfoFilled',
      callback: (action) => {
        if (action === 'confirm') {
          changeRouterKeepAlive(false, idx)
          openTab.splice(idx, 1)
          sessionStorage.removeItem('token')
          sessionStorage.removeItem('userInfo')
          router.push('/login')
        }
      }
    })
  } else {
    openTab[idx].isClosing = true

    changeRouterKeepAlive(false, idx)
    if (idx === activeIndex.value && idx === 0) router.push(openTab[++activeIndex.value].path)
    else if (idx === activeIndex.value) router.push(openTab[--activeIndex.value].path)
    if (idx < activeIndex.value) activeIndex.value--
    openTab.splice(idx, 1)

  }
}
</script>

<script>

</script>

<style lang="less" scoped>
.container{
  height: 36px;
  background: #ffffff;
}
.tab-list{
  width: 98%;
  overflow-x:scroll;
  display: flex;
  align-items: center;
  color: #fff;
  background: #ffffff;
  font-weight: 800;
}
.tabs{
  background: #fff;
  padding: 8px 14px;
  font-size: 14px;
  cursor: pointer;
  color:#797979;
}
.active{
  background: #f5f9ff;
  color: #126f9e;
}
.close{
  border-radius: 100%;
  padding: 2px;
}
.close:hover{
  background: #f5f9ff;
  transition: .6s;
  color: #126f9e;
}
.active-close:hover{
  background: #126f9e;
  transition: .6s;
  color: rgb(255, 255, 255);
}

.closing{
  animation: hide 1s;
}

.c{
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #dadada;
  margin-right: 8px;
}

.c-active{
  box-shadow: 0px 0px 10px 0px #126f9e;
  transition: 1s;
}
</style>