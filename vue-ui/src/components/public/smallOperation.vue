<template>
  <div class="header mr50" :style="{color: textColor}">
    <div class="function">
      <ul class="function-list">
        <li
          v-for="item,idx in functionList"
          :key="idx" class="function-list-item"
          :title="item.title"
          @click="item.callback"
        >
          <el-icon v-if="item.icon">
            <component :is="item.icon" />
          </el-icon>
          <div v-else>
            <el-image
              :src="item.image"
              mode="scaleToFill"
              style="height: 22px;width: 22px;margin-top:4px"
            />
          </div>
        </li>
        <li class="">
          <div ref="spanRef" class="user-info mr20">
            <div :class="textColor==='#000'?'image':'image2'" />
            <span
              v-click-outside="onClickOutside" class="text"
              :title="user?.nickName"
            >{{ user?.nickName }}</span>
          </div>
          <el-popover
            ref="popoverRef"
            :virtual-ref="spanRef"
            trigger="click"
            title="个人信息"
            virtual-triggering
            width="250px"
            :hide-after="0"
          >
            <div>
              <span>账号：</span>
              <span style="color:#686868;">{{ user?.userName }}</span>
            </div>
            <div class="mt10">
              <span>昵称：</span>
              <span style="color:#686868;">{{ user?.nickName }}</span>
            </div>
            <div class="mt10">
              <span>电话：</span>
              <span style="color:#686868;">{{ user?.phonenumber }}</span>
            </div>
            <div class="mt10">
              <span>账号状态：</span>
              <el-tag
                v-if="user?.status==='0'" class="ml-2"
                type="success"
              >
                正常
              </el-tag>
              <el-tag
                v-else class="ml-2"
                type="danger"
              >
                停用
              </el-tag>
            </div>
          </el-popover>
        </li>
        <div
          class="flex-center ml20" style="color:#48a2ff;font-size:12px"
          @click="goVersion"
        >
          <el-icon><Tickets /></el-icon>
          <el-link type="primary" class="ml6">
            更新日志
          </el-link>
        </div>
      </ul>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, getCurrentInstance, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessageBox, ElMessage, ClickOutside as vClickOutside } from 'element-plus'
import { logout } from '@/applications/sys/utils/login/index'
import screenfull from "screenfull"
const { proxy } = getCurrentInstance()

const spanRef = ref()
const textColor = ref('#000')
let user = JSON.parse(sessionStorage.getItem('userInfo'))?.user
const router = useRouter()

const clearStorage = () => {
  ElMessageBox.alert('是否确定清空您设置？清空后需要重新设置！', '提示', {
    type: 'warning',
    icon: 'InfoFilled',
    showCloseButton: true,
    confirmButtonText: '确认清空',
    callback: (action) => {
      if (action !== 'cancel') {
        localStorage.clear()
        ElMessageBox.alert('清空完成！刷新后生效', '提示', {
          type: 'warning',
          icon: 'InfoFilled',
          confirmButtonText: '立即刷新',
          callback: (action) => {
            location.reload()
          }
        })
      }
    }
  })
}

const quit = () => {
  ElMessageBox.alert('是否退出登录 ?', '提示', {
    // if you want to disable its autofocus
    // autofocus: false,
    type: 'warning',
    icon: 'InfoFilled',
    confirmButtonText: '确定退出',
    callback: (action) => {
      if (action === 'confirm') {
        logout().then(res => {
          if (res.code === 200){
            sessionStorage.removeItem('token')
            sessionStorage.removeItem('userInfo')
            router.push('/login')
          }
        })
      }
    }
  })
}
const fullScreen = () => {
  if (!screenfull.isEnabled){
    ElMessage.error('抱歉，您当前浏览器不支持全屏')
    return
  }

  !screenfull.isFullscreen ? screenfull.request() : screenfull.exit()
  setTimeout(() => {
    functionList[1].icon = screenfull.isFullscreen ? 'Crop' : 'FullScreen'
    functionList[1].title = screenfull.isFullscreen ? '关闭全屏' : '全屏'
  }, 100)

}

let light = ref(true)
const changeTheLight = () => {
  light.value = !light.value
  sessionStorage.setItem('light', light.value)
  changeSelf()
  window.$eventBus.$emit('changeTheLight', light.value)
}

const changeSelf = () => {
  if (light.value) {
    textColor.value = '#000'
    functionList[0].image = require('../../../public/assets/images/home/light.png')

    // document.body.style.background = '#f6f6f6'
  }
  else {
    functionList[0].image = require('../../../public/assets/images/home/noLight.png')

    // document.body.style.background = '#202224'
    textColor.value = '#fff'
  }
}

onMounted(() => {
  let localLight = sessionStorage.getItem('light')
  if (localLight) light.value = localLight === 'true'
  changeSelf()
  nextTick(() => {
    window.$eventBus.$emit('changeTheLight', light.value)
  })
})

const functionList = reactive([{
  image: require('../../../public/assets/images/home/light.png'),
  title: '深色/浅色模式',
  callback: changeTheLight
}, {
  icon: screenfull.isFullscreen ? 'Crop' : 'FullScreen',
  title: screenfull.isFullscreen ? '关闭全屏' : '全屏',
  callback: fullScreen
}, {
  icon: 'DeleteFilled',
  title: '清空系统缓存',
  callback: clearStorage
}, {
  icon: 'SwitchButton',
  title: '退出登录',
  callback: quit
}])

const onClickOutside = () => {
  proxy.$refs.popoverRef?.delayHide?.()
}

const goVersion = () => {
  window.open("https://manage.haoxindian.com:5000/version.html", '_blank')

}
</script>

<style lang="less" scoped>
.search {
    display: flex;
    flex-direction: column;
    flex-wrap: wrap;
    align-content: space-around;
    justify-content: space-evenly
}

.function-list{
  list-style: none;
  display: flex;
  // align-items: center;

  .function-list-item{
    font-size: 18px;

    cursor: pointer;
    width: 46px;
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  .function-list-item:hover{

    animation-name: hide2show;
    animation-duration: .4s;
    animation-fill-mode:forwards;
    animation-timing-function:linear;
  }
  .user-info{
    font-size: 14px;
    margin-left: 10px;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;

    width: 100%;
    height: 100%;

  }
}

.image{
  width: 30px;
  height: 30px;
  background: url('../../../public/assets/images/home/user.png');
  background-size: 100% 100%;
  margin: 0px 10px;
}

.image2{
  width: 30px;
  height: 30px;
  background: url('../../../public/assets/images/home/noLightUser.png');
  background-size: 100% 100%;
  margin: 0px 10px;
}

.text{
  display: inline-block;
  max-width: 120px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>