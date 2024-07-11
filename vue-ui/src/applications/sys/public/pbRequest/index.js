import axios from 'axios'
import { errorHandle } from './errorHandle'
import router from '../../router/index'
import { ElMessageBox } from 'element-plus'
import { ref } from 'vue'

// axios.defaults.headers["Content-Type"] = "application/json"

/**
 * 加载动画
 */
let loading = ref(false)

/**
 * 获取当前时间
 */
const getTime = () => {
  let date = new Date()
  let year = date.getFullYear()
  let month = date.getMonth() + 1
  let day = date.getDate()
  let hour = date.getHours()
  let minute = date.getMinutes()
  let second = date.getSeconds()
  return year + '/' + month + '/' + day + ' ' + hour + ':' + minute + ':' + second.toString().padStart(2, '0')
}

/**
 * 创建请求实例
 */
const pbRequest = axios.create({

  // baseURL: process.env.VUE_APP_TITLE !== 'pro' ? 'http://192.168.28.128:8080' : 'https://manage.haoxindian.com:8080',

  // baseURL: process.env.VUE_APP_TITLE !== 'pro' ? 'http://192.168.28.134:9999' : 'https://logistics.jiaxianwuliu.com:9998',

  baseURL: process.env.VUE_APP_TITLE !== 'pro' ? 'http://192.168.28.134:8884' : 'http://8.138.143.21:8884', // 对外服务器
  headers: {
    "Content-Type": "application/json"
  }
})

/**
 * 创建请求拦截器
 */
pbRequest.interceptors.request.use(

  // 对请求之前需要做的操作
  config => {
    loading.value = true
    console.log('---------请求拦截---------' + config.url + `  请求开始时间：${ getTime() }`)
    config.requestStartTime = Date.parse(new Date())

    // post请求需要序列化参数
    // if (config.method === 'post' && config.data) {
    //   config.data = JSON.stringify(config.data)
    // }
    // if (process.env.VUE_APP_TITLE !== 'pro')
    // config.headers['ip'] = '192.168.28.123'

    if (sessionStorage.getItem('token')){
      const token = sessionStorage.getItem('token')
      if (token) {
        config.headers['Authorization'] = 'Bearer ' + token
      } else {
        router.push('/login')
      }
    }
    return config
  },

  // 请求错误时需要做的操作
  error => {
    conosle.log(error)
  }
)

/**
 * 创建响应拦截器
 */
pbRequest.interceptors.response.use(

  // 对响应数据需要做的操作
  response => {
    loading.value = false
    console.log('---------响应拦截---------' + response.config.url + `  请求响应时间：${ getTime() }`)
    response.responseTime = Date.parse(new Date())

    // 接口响应超过10秒钟
    if (response.responseTime - response.config.requestStartTime >= 10000) {
      console.warn(response.config.url + '该接口响应时间过长')
    }
    if (response.data.code === 401){
      ElMessageBox.alert('登录过期，请重新登录', '提示', {
        confirmButtonText: '确定',
        type: 'warning',
        icon: 'InfoFilled',
        callback: (action) => {
          if (action === 'confirm') {
            sessionStorage.removeItem('token')
            sessionStorage.removeItem('userInfo')
            router.push('/login')
          }
        }
      }).then(res => {
        //null
      }).catch(() => {
        //null
      })
      return response
    }
    if (response.data.code !== 200){
      ElMessageBox.alert(response.data.msg, '提示', {
        type: 'warning',
        icon: 'InfoFilled',
        confirmButtonText: '我知道了',
        dangerouslyUseHTMLString: true
      }).then(res => {
        //null
      }).catch(() => {
        //null
      })
      return response
    }
    return response
  },

  // 响应错误处理
  error => {
    loading.value = false
    let alertText = '出现了预期之外的错误'
    if (error.code === 'ERR_NETWORK'){
      alertText = `系统正在更新，请稍后再试！`
    }
    else if (error.code === 'ERR_BAD_REQUEST'){
      alertText = `出现了预期之外的错误；这可能是个客户端的错误！`
    }
    ElMessageBox.alert(alertText, '提示', {
      type: 'warning',
      icon: 'InfoFilled',
      confirmButtonText: '我知道了',
      dangerouslyUseHTMLString: true
    }).then(res => {
      //null
    }).catch(() => {
      //null
    })
    return { data: { code: 'ERR_BAD_REQUEST', msg: 'Error', data: null } }
  }
)

export { pbRequest, loading }
