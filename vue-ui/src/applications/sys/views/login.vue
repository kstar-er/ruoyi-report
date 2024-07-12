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
        财务系统
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
            <div v-if="false" style="width: 100%;text-align: center;">
              <el-button
                class="logn-btn"
                type="primary"
                @click="register"
              >
                注 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 册
              </el-button>
            </div>
          </el-form-item>
        </el-form>
      </div>
    </div>

    <div>
      <el-dialog
        v-model="showRegister"
        :show-close="false" title="请完善注册信息"
        width="500" center
      >
        <template #header="{ titleId, titleClass }">
          <div class="jx-dialog-header">
            <h4 :id="titleId" :class="titleClass">
              请完善注册信息
            </h4>
            <el-button
              class="dialog-close-btn"
              type="danger" icon="CloseBold"
              circle
              plain
              @click="showRegister = false"
            />
          </div>
        </template>
        <el-form
          ref="registerForm"
          :model="form" label-width="auto" :rules="rules"
        >
          <div v-for="(item,index) in registerItem" :key="index">
            <el-form-item :label="item.label" :prop="item.prop">
              <el-input
                v-model="form[item.prop]"
                :placeholder="item.placeholder"
                :show-password="item?.showPassword"
                :maxlength="item?.maxlength"
                autocomplete="new-password"
                clearable
              />
            </el-form-item>
          </div>
          <el-form-item
            label="验证码" inline-message="true"
            prop="code"
          >
            <span style="margin-right:20px">
              <el-input
                v-model="form.code"
                placeholder="请输入验证码"
                width="100px"
                size="small"
                maxlength="8"
                clearable
              />
            </span>

            <el-button type="primary" @click="getCode">
              获取验证码
            </el-button>
          </el-form-item>
          <el-form-item>
            <div style="width: 100%;text-align: center;">
              <el-button
                class="logn-btn"
                type="primary"
                @click="conformRegister"
              >
                确认注册并登录
              </el-button>
            </div>
            <div style="width: 100%;text-align: center;">
              <el-button
                class="logn-btn"
                type="primary"
                @click="showRegister = false"
              >
                取消
              </el-button>
            </div>
          </el-form-item>
        </el-form>
      </el-dialog>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { reactive, onMounted, ref, getCurrentInstance, computed } from 'vue'
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

// 注册
const showRegister = ref(false)
const registerItem = reactive([{
  label: '手机号',
  prop: 'phone',
  placeholder: '请输入手机号'
},
{
  label: '昵称',
  prop: 'nickName',
  placeholder: '请输入昵称'
},
{
  label: '密码',
  prop: 'password',
  placeholder: '请输入密码',
  showPassword: true,
  maxlength: 16
},
{
  label: '确认密码',
  prop: 'confirmPassword',
  placeholder: '请输入确认密码',
  showPassword: true,
  maxlength: 16
}])
const form = reactive({
  phone: '',
  nickName: '',
  password: '',
  confirmPassword: '',
  code: ''
})
const rules = reactive({
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3456789]\d{9}$/, message: '手机号码格式不正确', trigger: 'blur' }],
  nickName: [{ required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 1, max: 10, message: '昵称的长度在1-10个字符之间', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 16, message: '密码的长度在6-16位之间', trigger: 'blur' }],
  confirmPassword: [{ required: true, message: '请输入确认密码', trigger: 'blur' },
    { min: 6, max: 16, message: '密码的长度在6-16位之间', trigger: 'blur' }],
  code: [{ required: true, message: '请输入验证码', trigger: 'blur' }]

})
const register = () => {
  showRegister.value = true
}

const registerForm = ref(null)
const passwordRule = computed(() => {
  return form.password === form.confirmPassword
})
const conformRegister = () => {
  registerForm.value.validate((valid) => {
    if (!valid) return false
    else {
      console.log("ddd", form, passwordRule.value)
      if (!passwordRule.value) {
        proxy.$message.error('确认密码输入错误')
        return false
      }
      else {
        user.userName = form.nickName
        user.password = form.password
        form.phone = ''
        form.nickName = ''
        form.password = ''
        form.confirmPassword = ''
        form.code = ''
        proxy.$message.success('注册成功')
        showRegister.value = false
      }

    }
  })
}
const getCode = () => {
  //
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