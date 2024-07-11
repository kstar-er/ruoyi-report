<template>
  <div>
    <div
      class="image"
    >
      <el-button class="btn1" @click="refresh">
        刷新试试
      </el-button>
      <el-button class="btn2" @click="back">
        返回上一页
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
const router = useRouter()

const refresh = () => {
  location.reload()
}

const back = () => {
  router.go(-1)
}
</script>

<style lang="less" scoped>
.image{
  box-sizing: border-box;
  width: 100%;
  height: 650px;
  background: url('../../../../../public/assets/images/home/404.png');
  background-size: 100% 100%;
  margin: 0 auto;
  display: flex;
  align-items: flex-end;
  justify-content: flex-end;
  padding: 0 530px 200px 0;
}
.btn1{
  background: #104a5e;
  color: #fff;
  padding:18px 30px;
  border-radius: 20px;
}
.btn1:hover{
  background: #262335;
}
.btn2{
  background: #d84045;
  color: #fff;
  padding:18px 30px;
  border-radius: 20px;
}
.btn2:hover{
  background: #f7494f;
}
</style>