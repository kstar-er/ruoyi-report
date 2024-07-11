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
      path: '/warehouse/warehouseDetail',
      name: 'warehouseDetail',
      component: () => import('../views/fileManager/detail/warehouseDetail.vue'),
      meta: {
        title: '仓库总览',
        keepAlive: false //设置页面是否需要使用缓存
      }
    }, {
      path: '/product/productDetail',
      name: 'productDetail',
      component: () => import('../views/fileManager/detail/productDetail.vue'),
      meta: {
        title: '产品明细',
        keepAlive: false //设置页面是否需要使用缓存
      }
    }, {
      path: '/order/inDetail',
      name: 'inDetail',
      component: () => import('../views/order/detail/inDetail.vue'),
      meta: {
        title: '入库明细',
        keepAlive: false //设置页面是否需要使用缓存
      }
    }, {
      path: '/order/outDetail',
      name: 'outDetail',
      component: () => import('../views/order/detail/outDetail.vue'),
      meta: {
        title: '出库明细',
        keepAlive: false //设置页面是否需要使用缓存
      }
    }, {
      path: '/order/allocationDetail',
      name: 'allocationDetail',
      component: () => import('../views/order/detail/allocationDetail.vue'),
      meta: {
        title: '调拨明细',
        keepAlive: false //设置页面是否需要使用缓存
      }
    }, {
      path: '/order/checkOrderDetail',
      name: 'checkOrderDetail',
      component: () => import('../views/order/detail/checkOrderDetail.vue'),
      meta: {
        title: '盘点明细',
        keepAlive: false //设置页面是否需要使用缓存
      }
    }, {
      path: '/order/checkMissionsDetail',
      name: 'checkMissionsDetail',
      component: () => import('../views/order/detail/checkMissionsDetail.vue'),
      meta: {
        title: '盘点任务明细',
        keepAlive: false //设置页面是否需要使用缓存
      }
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
    if (item.path === '/' || item.children && item.name.indexOf("Xcx") === -1) {
      delete item.component
      item.children = handleChildren(item.children)
    }
    return item
  })
}

const handleChildren = (children) => {
  return children.map(compoents => {
    if (compoents.children) return handleRouterInfo([compoents])[0]
    let com = compoents.component
    compoents.meta.keepAlive = !compoents.meta.noCache
    compoents.component = () => {
      return import('@/applications/sys' + com + '.vue').catch(err => {
        return import('@/applications/sys/views/error/404.vue')
      })
    }
    return compoents
  })
}

// 动态添加路由
const routerInit = async () => {
  let ctx = await getRouters()
  if (ctx.code === 200){
    let routerInfo = handleRouterInfo(ctx.data)
    console.log(routerInfo)
    routerInfo.forEach(r => {
      router.addRoute('main', r)
    })
    router.addRoute({
      name: 'Redirect',
      path: '/:pathMatch(.*)', redirect: '/login'
    })
    return routerInfo
  } else return []

}

router.beforeEach(async (to, from, next) => {
  if (to.fullPath.indexOf('/weChat/minPro') !== -1) {
    next()
    return
  }

  NProgress.start()
  if (to.path === '/login') {
    sessionStorage.removeItem('token')
    sessionStorage.removeItem('userInfo')
    commit('updateRouterInfo', { data: [], sign: false })
    next()
  }
  else if (!_state.data.isLoadRouter){
    let ctx = await routerInit()
    ctx = ctx.filter(item => ['Wms', 'System'].includes(item.name))
    commit('updateRouterInfo', { data: ctx, sign: true })
    next({ ...to, replace: true })
  } else next()

})

router.afterEach((to, from, next) => {
  window.document.title = to.meta.title ? to.meta.title : '基础框架'
  NProgress.done()
})
export default router
