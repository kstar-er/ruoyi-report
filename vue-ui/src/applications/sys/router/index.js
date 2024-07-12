import { createRouter, createWebHistory } from 'vue-router'
import NProgress from 'nprogress'
import store from '../store/index'
import { getRouters } from '../utils/login/index'
const { _state, commit } = store
import 'nprogress/nprogress.css'

NProgress.configure({
  easing: 'ease',
  speed: 500,
  showSpinner: true,
  trickleSpeed: 200,
  minimum: 0.3
})
const routes = [{
  path: '/',
  redirect: "/login"
}, {
  path: '/main',
  name: 'main',
  component: () => import('../views/home.vue'),
  children: [
    {
      path: '/index',
      name: 'index',
      component: () => import('../views/index/index.vue'),
      meta: {
        title: '首页',
        keepAlive: true //设置页面是否需要使用缓存
      },
      children: []
    }, {
      path: '/detail/strucitureDetail',
      name: 'strucitureDetail',
      component: () => import('../views/financial/detail/strucitureDetail.vue'),
      meta: {
        title: '修改明细',
        keepAlive: false //设置页面是否需要使用缓存
      },
      children: []
    }, {
      path: '/programmeDetail',
      name: 'programmeDetail',
      component: () => import('../views/financial/programmeDetail.vue'),
      meta: {
        title: '方案用户绑定情况',
        keepAlive: false //设置页面是否需要使用缓存
      },
      children: []
    }, {
      path: '/detail/relationshipDetail',
      name: 'relationshipDetail',
      component: () => import('../views/financial/detail/relationshipDetail.vue'),
      meta: {
        title: '修改分类明细',
        keepAlive: false //设置页面是否需要使用缓存
      },
      children: []
    }, {
      path: '/detail/collectDetail',
      name: 'collectDetail',
      component: () => import('../views/financial/detail/collectDetail.vue'),
      meta: {
        title: '汇总计划明细',
        keepAlive: false //设置页面是否需要使用缓存
      },
      children: []
    },

    {
      path: '/foreignDetailList',
      name: 'foreignDetailList',
      component: () => import('../views/financial/foreignDetailList.vue'),
      meta: {
        title: '1',
        keepAlive: false //设置页面是否需要使用缓存
      },
      children: []
    }
  ]
},
{
  path: '/login',
  name: 'login',
  component: () => import('../views/login.vue'),
  meta: {
    title: '登录',
    keepAlive: false //设置页面是否需要使用缓存
  }
}]

const router = createRouter({
  history: createWebHistory(),
  routes,
  base: '/sys/management'
})

// 把组件路径换成import，因为路由数据有children的表示为目录，需要递归
const handleRouterInfo = (data) => {

  return data.map((item) => {
    if (item.path === '/' || item.children) {
      delete item.component
      item.children = handleChildren(item.children)
    }
    return item
  })
}

const handleChildren = (children) => {
  return children.map(compoents => {
    if (compoents.children) return handleRouterInfo([compoents])[0] // 如果又childrend，需要递归去创建孩子的子路由
    let com = compoents.component
    compoents.meta.keepAlive = !compoents.meta.noCache // 是否使用缓存
    compoents.component = () => {
      // 动态加载组件，如果找不到该组件，给他一个404页面
      return import('@/applications/sys' + com + '.vue').catch(err => {
        return import('@/applications/sys/views/error/404.vue')
      })
    }
    return compoents
  })
}

// 动态添加路由
const routerInit = async () => {
  let ctx = await getRouters() // 获取后台路由数据
  if (ctx.code === 200){
    let routerInfo = handleRouterInfo(ctx.data) // 处理路由数据
    routerInfo.forEach(r => {
      router.addRoute('main', r)

      // 逐个添加到main路由的children里面,效果如下
      // {
      //   path: '/main',
      //   name: 'main',
      //   component: () => import('../views/home.vue'),
      //   children:[{。。。},{。。。}, {。。。}]
      // }
    })

    // 防止空路由 加个重定向
    router.addRoute({
      name: 'Redirect',
      path: '/:pathMatch(.*)', redirect: '/login'
    })
    return routerInfo
  } else return []

}

const filterRoute = (data) => {
  let filterRouteName = ['System', 'Financial']
  return data.filter((item) => filterRouteName.includes(item.name))
}

router.beforeEach(async (to, from, next) => {
  NProgress.start() // 加载条开始
  if (to.path === '/login') {
    // 如果登录页面，把状态清楚，把路由标识交标记为false
    sessionStorage.removeItem('token')
    sessionStorage.removeItem('userInfo')
    commit('updateRouterInfo', { data: [], sign: false })
    next()
  }
  else if (!_state.data.isLoadRouter){
    // 如果不是登陆页面并且没有加载过路由，执行加载路由
    let ctx = await routerInit() // 加载路由
    ctx = filterRoute(ctx) // 看情况自己过滤一下不需要显示的模块
    commit('updateRouterInfo', { data: ctx, sign: true })
    next({ ...to, replace: true }) // 这里因为这里进去某个路由之前，是先有url再注册路由的，所以next的时候要把replace设置成true
  } else next() // 又不是登陆页面 又加载过路由了直接进去就可了

})

router.afterEach((to, from, next) => {
  window.document.title = to.meta.title ? to.meta.title : '基础框架'
  NProgress.done() // 加载条结束
})

export default router
