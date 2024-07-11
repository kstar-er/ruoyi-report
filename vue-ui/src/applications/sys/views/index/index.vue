<template>
  <div style="padding:10px">
    <div class="header">
      <div class="header-text">
        <div v-if="time==='早上好'" style="font-size:100px">
          <el-icon><Sunny /></el-icon>
        </div>
        <div v-if="time==='下午好'" style="font-size:100px">
          <el-icon><Sunrise /></el-icon>
        </div>
        <div v-if="time==='晚上好'" style="font-size:100px">
          <el-icon><MoonNight /></el-icon>
        </div>
        <span class="mb10">{{ time + '，'+ user?.nickName + '。'+ helloWord }}</span>
        <!-- <span class="mb10 ml6 mt20" style="font-size:14px">部门：{{ user?.dept.deptName }}</span> -->
        <span class="mb10 ml6 mt20" style="font-size:14px">角色：{{ roles?.join('，').length===0?'暂无角色':roles?.join('，') }}</span>
        <span class="mb10 ml6" style="font-size:14px">账号状态：
          <el-tag :type="user?.status==='0'?'success':'danger'">
            {{ user?.status==='0'?'正常':'停用' }}
          </el-tag>
        </span>
        <span style="font-size:14px" class="ml6">上一次登录时间：{{ user?.loginDate??'暂无记录' }}
          <el-button
            size="small"
            class="btn"
            style="margin-left:14px"
            type="danger"
            plain
            @click="quit"
          >注销</el-button>
        </span>
      </div>
      <image
        src="https://demo.gin-vue-admin.com/assets/dashboard-70e55b71.png"
        mode="scaleToFill"
        class="image"
      />
    </div>
    <div class="handle">
      <span>通知</span>
      <div v-if="false" class="content">
        暂无通知
      </div>
      <div v-else class="no-content">
        暂无通知
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import { logout } from '@/applications/sys/utils/login/index'
const router = useRouter()
let user = JSON.parse(sessionStorage.getItem('userInfo'))?.user
let roles = user?.roles.map(item => item.roleName)
const getTime = () => {
  let date = new Date()
  if (date.getHours() >= 0 && date.getHours() < 12){
    return "上午好"

  } else if (date.getHours() >= 12 && date.getHours() < 18){
    return "下午好"
  } else {
    return "晚上好"
  }
}
const time = ref(getTime())
const helloWord = ref(time.value === "上午好" ? '一日之计在于晨，开始你一天的工作吧。' : time.value === "下午好" ? '工作累了，来杯下午茶吧。' : '时候不早了，注意休息噢。')

const quit = () => {
  ElMessageBox.alert('是否确认注销登录？', '提示', {
    type: 'warning',
    icon: 'InfoFilled',
    confirmButtonText: '确定注销',
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
</script>

<style lang="less" scoped>
.header{
  background: #fff;
  padding: 0 20px;
  height: 20rem;
  border-radius: 0 0 4px 4px;
  box-sizing: border-box;
  font-size: 22px;
  color: rgb(75 85 99);
  display: flex;
  justify-content: space-between;
  overflow: hidden;
  align-items: center;
  box-shadow: 0 0 10px 0px #dddddd;
}
.header-text{
  display: flex;
  flex-direction: column;
  color: rgb(75 85 99);
}
.image{
  height: 600px;
  width: 600px;
  background: url('https://demo.gin-vue-admin.com/assets/dashboard-70e55b71.png');
  background-size: 100% 100%;
  margin-right: 40px;
}
.btn:hover{
  background: #e48585;
  border-color: #e48585;
}
.handle{
  background: #fff;
  padding: 20px;
  height: 18rem;
  border-radius: 4px;
  box-sizing: border-box;
  font-size: 22px;
  color: rgb(75 85 99);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  box-shadow: 0 0 10px 0px #dddddd;
  margin-top: 20px;
}
.content,.no-content{
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}
.no-content{
  font-size: 14px;
  color: #999;
  letter-spacing:10px;
}
</style>