<template>
  <div style="position:relative">
    <el-menu
      active-text-color="#52a2ff"
      :text-color="textColor"
      :background-color="bgColor"
      class="menu"
      :default-active="route.path"
      :collapse="isCollapse"
      router
    >
      <el-menu-item
        index="/index"
      >
        <el-icon><HomeFilled /></el-icon>
        <span class="nav_title">首页</span>
      </el-menu-item>
      <component
        :is="menuRender"
      />
    </el-menu>
    <div class="collapse" :style="isCollapse?{left:'78%'}:{}">
      <el-icon
        v-if="!isCollapse"
        @click="changeCollapse"
      >
        <Fold />
      </el-icon>
      <el-icon
        v-else
        @click="changeCollapse"
      >
        <Expand />
      </el-icon>
    </div>
  </div>
</template>

<script setup>
import { onBeforeMount, ref, h, resolveComponent, getCurrentInstance, watch } from 'vue'
import { useRoute } from 'vue-router'
import { useStore } from 'vuex'
import { ElMenuItem, ElSubMenu, ElIcon } from 'element-plus'
import { HomeFilled, Link } from '@element-plus/icons-vue'
const { proxy } = getCurrentInstance()
const { _state } = useStore()
const route = useRoute()
const isCollapse = ref(false)
const bgColor = ref('#fff')
const textColor = ref('#000')
const routerInfo = ref(null)

const changeCollapse = (val) => {
  isCollapse.value = !isCollapse.value
}

defineExpose({
  changeCollapse
})

const collapseColor = ref('#126f9e')

onBeforeMount(() => {
  routerInfo.value = _state.data.routerInfo
  menuRender()

  window.$eventBus.$on('changeTheLight', (val) => {
    if (!val) {
      bgColor.value = '#011f3c'
      textColor.value = '#fff'
      collapseColor.value = '#5f53ce'
    } else {
      collapseColor.value = '#126f9e'
      bgColor.value = '#fff'
      textColor.value = '#000'
    }
  })
})

// 导航栏渲染函数
const menuRender = () => {
  return routerInfo.value.map(menu => {
    // 如果导航栏需要隐藏或者是小程序目录，则不做处理
    if (menu.hidden || menu.name && menu.name.indexOf("Xcx") !== -1) return

    // 如果path是 '/'或者http开头，则表示是顶级目录的菜单，需要渲染ElMenuItem
    if (menu.path === '/' || menu.path.indexOf('http') !== -1) {
      let chi = menu.children ? menu.children[0] : menu

      // 如果path是 '/'并且children里面的path以http开头，则表示外链，则表示是外链
      if (chi.path.indexOf('http') !== -1)
        return h(ElMenuItem, { class: 'a', index: null, title: '跳转至：' + chi.path, onClick: () => {
          // 处理跳转
          jumpHref({ url: chi.path })
        } }, {
          title: () => [h(ElIcon, { style: "color:#fff" }, () => h(Link)), h('span', { class: 'nav_title', style: "color:#fff" }, chi.meta.title)]
        })

      // 渲染菜单item
      else return h(ElMenuItem, { index: '/' + chi.path },
        () => [h(ElIcon, {}, () => {
          return h(chi.meta.icon === '#' ?
            resolveComponent('Menu') :
            resolveComponent(chi.meta.icon.trim()))
        }),
        h('span', { class: 'nav_title' }, chi.meta.title)]
      )
    }

    // 如果path不是 '/'，则表示是顶级目录的目录，需要渲染ElSubMenu
    else return h(ElSubMenu, { index: menu.path + '/' }, {
      // 此处渲染目录的Icon，对应title插槽
      title: () => [h(ElIcon, {}, () => {
        return h(menu.meta.icon === '#' ?
          resolveComponent('Menu') :
          resolveComponent(menu.meta.icon.trim()))
      }),
      h('span', { class: 'nav_title' }, menu.meta.title)],

      // 此处渲染目录下面的子菜单或子目录
      default: () => menuItemRender({ menu: menu.children ? menu.children : [], path: menu.path })
    })

  })
}

const menuItemRender = ({ menu, path = '/' }) => {
  return menu.map(item => {
    // 如果导航栏需要隐藏，则不做处理
    if (item.hidden) return

    // 此处处理子目录，有children表示有子目录
    if (item.children){
      let { meta } = item
      return h(ElSubMenu, { index: path + '/' + item.path }, {
        // 此处渲染子目录的标题，子目录不需要渲染Icon
        title: () => h('span', { }, meta.title),

        // 递归渲染子目录下的children
        default: () => menuItemRender({ menu: item.children, path: path + '/' + item.path })
      })
    } else

      // 此处处理子菜单，path以http开头则处理跳转，否则渲染正常ElMenuItem
      return item.path.indexOf('http') !== -1 ? h(ElMenuItem, { style: `color:#000`, class: 'a', index: null, title: '跳转至：' + item.path, onClick: () => {
        jumpHref({ url: item.path })
      } }, {
        title: () => [h('span', { class: 'a_span' }, item.meta.title)]
      }) :
        h(ElMenuItem, { index: path + '/' + item.path }, () => item.meta.title)
  })
}

const jumpHref = ({ url }) => {
  proxy.$alert('当前菜单为站外链接，即将跳转至 ' + url, '提示', {
    type: 'warning',
    icon: 'InfoFilled',
    showCancelButton: true,
    cancelButtonText: '取消跳转',
    confirmButtonText: '确认',
    callback: (action) => {
      if (action === 'cancel') return
      else {
        window.open(url, '_blank')
      }
    }
  })
}
</script>

<style lang="less" scoped>
.menu {
  transition: .5s;
  height: calc(100vh - 40px - 30px);
  overflow-x: hidden;
  overflow-y: scroll;
  padding:10px;
  box-sizing: border-box;
  padding-bottom: 50px;
  padding-top: 30px;
  margin-right: 6px;
}

.menu:hover{
  box-shadow: 0px 0px 20px 0px rgb(170, 170, 170);
}

:deep(.el-menu){
  transition: .5s;
  border-right:none;
}

:deep(.el-menu-item){
  border-radius: 10px;
  padding: 0 10px;
  height: 50px;
  line-height: 50px;
  margin-bottom: 4px;
  .a_span{
    color:v-bind(textColor);
  }
}
:deep(.is-active){
  background: #126f9e;
  color:#fff;

}

:deep(.el-sub-menu__title){
  background: v-bind(bgColor);
  padding: 0 10px;
  height: 50px;
  line-height: 50px;
}

:deep(.el-sub-menu__title:hover){
  background: v-bind(bgColor);;
}

:deep(.el-menu-item:hover){
  background: #126f9e;
  color:#fff;
  .a_span{
    color:#fff !important;
  }

}

:deep(.nav_title){
  margin-right: 60px;
}

.collapse{
  transition: .5s;
  position: absolute;
  top: 1%;
  left: 92%;

  background: v-bind(collapseColor);
  width: 20px;
  height: 20px;
  border-radius: 50%;
  color:#fff;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  z-index: 500;
}
</style>
