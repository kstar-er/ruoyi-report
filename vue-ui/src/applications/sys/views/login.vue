<template>
  <div class="background">
    <h1 class="mt100 mb60" style="textShadow: 6px 1px 3px #fff;color:#4d4d4d">
      <!-- 欢迎回来 -->
    </h1>
    <div
      v-loading="loading" class="login-box"
    >
      <h2 style="textShadow: 6px 2px 3px rgb(110, 110, 110);color:#4d4d4d">
        欢 &nbsp;迎 &nbsp;回 &nbsp; 来
      </h2>
      <h3>
        基础框架服务后台管理
      </h3>
      <div class="form_area">
        <el-form label-width="0px" :model="user">
          <el-form-item label="">
            <el-input
              ref="name"
              v-model="user.userName"
              clearable
              placeholder="请输入账号"
              @keyup.enter="login"
            />
          </el-form-item>
          <el-form-item label="">
            <el-input
              ref="psw"
              v-model="user.password"
              type="passworld"
              clearable
              show-password
              placeholder="请输入密码"
              @keyup.enter="login"
            />
          </el-form-item>
          <el-form-item label-width="12px">
            <div style="width: 100%;text-align: center;">
              <el-button
                class="logn-btn"
                type="primary"
                @click="login"
              >
                登 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 录
              </el-button>
            </div>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { reactive, onMounted, ref, getCurrentInstance } from 'vue'
import { userLogin, loading, getUserInfo } from '../utils/login/index'
const router = useRouter()
const { proxy } = getCurrentInstance()

let user = reactive({
  userName: '',
  password: ''
})

onMounted(() => {
  user.userName = ''
  user.password = ''
  localStorage.removeItem('client-header-store')
})

const erroring = ref(false)

// 登录
const login = async () => {
  proxy.$refs.psw?.blur()
  proxy.$refs.name?.blur()
  if (user.userName.length === 0 || user.password.length === 0){
    proxy.$message.error('请填写账号密码')
    return
  }
  let params = {
    password: user.password,
    username: user.userName
  }
  let ctx = await userLogin(params)
  if (ctx.code === 200){
    sessionStorage.setItem('token', ctx.data.access_token)
    let userInfo = await getUserInfo()
    if (userInfo.code === 200){
      sessionStorage.setItem('userInfo', JSON.stringify(userInfo.data))
      proxy.$message.success('登录成功')

      let routerInfo = JSON.parse(sessionStorage.getItem('routerTabs'))
      let activeIndex = JSON.parse(sessionStorage.getItem('activeIndex'))
      if (routerInfo && routerInfo[activeIndex]){
        setTimeout(() => {
          router.push(routerInfo[activeIndex].path)
        }, 0)
      } else {
        setTimeout(() => {
          router.push(`/index`)
        }, 0)
      }

    }
  } else { erroring.value = true }
}
</script>

<style lang="less" scoped>
.background {
  height: 100vh;
  width: 100vw;
  background: rgb(241, 241, 241);
  background: url('../../../../public/assets/images//login/loginBg.png');
  background-size: 100% 100%;
  display: flex;
  align-items: center;
  justify-content: flex-start;
  flex-direction: column;
  overflow: hidden;
}
.login-box{
  height: 480px;
  width: 430px;
  padding: 60px;
  box-sizing: border-box;
  background: #fff;
  text-align: center;
  box-shadow: 0 0 20px 10px #dddddd;
}
.logn-btn{
  font-weight: 700;
  margin-top: 10px;
  width: 280px;
  height: 36px;
  border-radius: 10px;
  background: rgba(0, 21, 41,.8);
  border:none;
}
.logn-btn:hover{
  background: rgba(0, 21, 41,.7);
}
:deep(.el-input__wrapper){
  padding: 6px 20px;
  color:rgb(117, 117, 117);
}
:deep(.el-input__inner::first-line){
  color:rgb(117, 117, 117);
}
</style>