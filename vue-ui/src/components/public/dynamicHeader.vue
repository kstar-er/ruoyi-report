<template>
  <div class="container">
    <div class="drag-box">
      <VueDraggableNext
        :list="myHeaderData"
        chosen-class="chosen"
        group="name"
        force-fallback="true"
        animation="500"
        :fallback-tolerance="4"
        @start="drag = true"
        @end="end"
      >
        <transition-group>
          <div
            v-for="element in myHeaderData" :key="element.key?element.key:element.prop"
            class="item"
          >
            <el-tag size="large">
              {{ element.title?element.title:element.label }}
            </el-tag>
            <div>
              <el-checkbox
                v-model="element.isFixed" label="固定在左边"
                size="large"
              />
              <el-checkbox
                v-model="element.isShow" label="是否展示"
                size="large"
              />
            </div>
          </div>
        </transition-group>
      </VueDraggableNext>
    </div>
    <div class="drag-btn">
      <el-button type="warning" @click="reSetHeader">
        重置
      </el-button>
      <el-button type="primary" @click="confirmClick">
        保存
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, onBeforeMount } from 'vue'
import { VueDraggableNext } from 'vue-draggable-next'
import { ElMessageBox } from 'element-plus'
import { useStore } from 'vuex'

const { _state, commit } = useStore()
const emit = defineEmits(['updateCompleted'])

// const store = useStore()
const props = defineProps({
  isShowEditTableHeader: {
    type: Boolean,
    default: false
  },
  modules: {
    type: String,
    default: ''
  },
  filterList: {
    type: Array,
    default: () => {
      return []
    }
  }
})

const defaultTableHeader = _state.data[props.modules].defaultTableHeader // 拆成普通对象 防止响应式污染默认数据
const myHeaderData = reactive([])
const drag = ref(false)
const initMyHeaderData = () => {
  myHeaderData.length = 0
  if (props.filterList.length === 0) myHeaderData.push(..._state.data[props.modules].tableHeader)
  else {
    _state.data[props.modules].tableHeader.forEach(item => {
      if (!props.filterList.includes(item.key)) myHeaderData.push(item)
    })
  }
}

onBeforeMount(() => {
  initMyHeaderData()
})
const end = (val) => {
  drag.value = false
}
const reSetHeader = () => {
  ElMessageBox.confirm(
    '重置之后保存的数据将会丢失，是否继续？',
    '提示',
    {
      confirmButtonText: '继续',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    myHeaderData.length = 0
    myHeaderData.push(...JSON.parse(JSON.stringify(defaultTableHeader))) // 深拷贝 防止响应式污染默认数据
    commit(`${props.modules}/updateTableHeader`, myHeaderData)
    ElMessageBox.alert('修改成功！刷新后生效', '提示', {
      type: 'warning',
      icon: 'InfoFilled',
      confirmButtonText: '立即刷新',
      callback: (action) => {
        emit('updateCompleted')
        location.reload()
      }
    })
    emit('updateCompleted')
  }).catch(() => {
    console.log('catch')
  })
}
const confirmClick = () => {
  ElMessageBox.confirm(
    '确认保存？',
    '提示',
    {
      confirmButtonText: '继续',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    if (props.filterList.length !== 0)
      _state.data[props.modules].tableHeader.forEach(item => {
        if (props.filterList.includes(item.key)) myHeaderData.push(item)
      }) // 处理一下过滤数据
    commit(`${props.modules}/updateTableHeader`, myHeaderData)
    ElMessageBox.alert('设置成功！刷新后生效', '提示', {
      type: 'warning',
      icon: 'InfoFilled',
      confirmButtonText: '立即刷新',
      callback: (action) => {
        emit('updateCompleted')
        location.reload()
      }
    })
  }).catch(() => {
    console.log('catch')
  })
}
</script>

<style  lang="less" scoped>
.container{
    position:relative;
}
.drag-box{
    height: 82vh;
    overflow-x: hidden;
    overflow-y: scroll;
}
.drag-btn{
    margin-top: 16px;
    display: flex;
    justify-content: flex-end;
}
.item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 6px 20px;
    background-color: #f7f7f7;
    border: solid 1px #eee;
    margin-bottom: 10px;
}

.chosen {
    background: #ecf5ff;
}
</style>