<template>
  <div class="home">
    <div class="header">
      <infoUser
        :user-color="'#666'" show-change-collapse
        @changeCollapse="changeCollapse"
      />
    </div>
    <div class="main-container">
      <div class="aside">
        <routerAside ref="aside" :router-info="routerInfo" />
      </div>
      <div class="main-content">
        <routerTab />
        <div class="scroll">
          <router-view v-slot="{ Component }">
            <keep-alive>
              <component
                :is="Component" v-if="$route.meta.keepAlive"
                :key="$route.name"
              />
            </keep-alive>
            <transition name="scale">
              <component
                :is="Component" v-if="!$route.meta.keepAlive"
                :key="$route.name"
              />
            </transition>
          </router-view>
        </div>
      </div>
    </div>
    <el-backtop
      :right="100" :bottom="40"
    />
  </div>
</template>

<script setup>
import { onBeforeMount, ref, getCurrentInstance } from 'vue'

import routerAside from '@/applications/sys/public/components/routerAside.vue'
import routerTab from '@/components/public/routerTab.vue'
import infoUser from '@/components/public/infoUser.vue'
const { proxy } = getCurrentInstance()
const light = ref(true)
const env = process.env.VUE_APP_TITLE
const bgColor = ref('#126f9e')
const showMore = ref(false)
onBeforeMount(() => {
  window.$eventBus.$on('changeTheLight', (val) => {
    light.value = val
  })

})
const routerInfo = ref(null)

const isCollapse = ref(false)
const changeCollapse = (val) => {
  isCollapse.value = val
  proxy.$refs.aside.changeCollapse(val)
}
</script>

<style lang="less" scoped>
.home{
  height: 100vh;
  overflow: hidden;
}

.aside{
  padding: 10px 0 10px 10px;
  transition: .5s;
}

.main-container {
  display: flex;

}

.main-content{

  transition: .5s;
  flex: 1;
  margin: 10px;
  box-sizing: border-box;
  overflow-y: scroll;
  overflow-x: hidden;
  // display: flex;
  // width: 100vw;
}

.scroll{
  height: calc(100vh - 40px - 30px - 34px);
}

.main-content:hover{
  box-shadow: 0px 0px 20px 0px rgb(170, 170, 170);
}

.header{
  transition: .5s;
}
.header:hover{
  box-shadow: 0px 0px 20px 0px rgb(170, 170, 170);
}

.scale-enter-active{
  transition: all 0.5s ease-in-out;
}
.scale-leave-active {
  transition: all 0.1s linear;
}

.scale-enter-from{
  opacity: 0;

}
.scale-leave-to {
  opacity: 0;

}
:deep(.select-square){
  border: none;
}
</style>