<template>
  <div v-if="isShow">
    <el-dialog
      v-model="showDialog"
      top="4%"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :show-close="false"
      lock-scroll
    >
      <template #header="{ titleId, titleClass }">
        <div class="jx-dialog-header">
          <h4 :id="titleId" :class="titleClass">
            构造计算表达式
            <slot name="customOperation" />
          </h4>
          <el-button
            class="dialog-close-btn"
            type="danger" icon="CloseBold"
            circle
            plain
            @click="closeDialog"
          />
        </div>
      </template>
      <div>
        <div>
          <div>
            字段名称：
          </div>
          <div
            v-for="tag in list"
            :key="tag.id"
            :ref="'tag'+tag.id"
            class="mr20 mt10 tag"
            style="cursor:pointer"

            @click="addItem(tag)"
          >
            {{ tag?.resultName??tag?.collectResultName }}
          </div>
        </div>
        <div class="mt10">
          <div>
            运算规则：
          </div>
          <div
            ref="add"
            class="mr20 mt10 tag"
            type="danger"
            :disable-transitions="false"
            style="cursor:pointer;background:rgb(252, 156, 172)"
            @click="addItem('(','danger')"
          >
            (
          </div>
          <div
            ref="add"
            class="mr20 mt10 tag"
            type="danger"
            :disable-transitions="false"
            style="cursor:pointer;background:rgb(252, 156, 172)"
            @click="addItem(')','danger')"
          >
            )
          </div>
          <div
            ref="add"
            class="mr20 mt10 tag"
            type="danger"
            :disable-transitions="false"
            style="cursor:pointer;background:rgb(252, 156, 172)"
            @click="addItem('+','danger')"
          >
            +
          </div>
          <div
            ref="subtract"
            class="mr20 mt10 tag"
            type="danger"
            :disable-transitions="false"
            style="cursor:pointer;background:rgb(252, 156, 172)"
            @click="addItem('-','danger')"
          >
            -
          </div>
          <div
            ref="multiply"
            class="mr20 mt10 tag"
            type="danger"
            :disable-transitions="false"
            style="cursor:pointer;background:rgb(252, 156, 172)"
            @click="addItem('*','danger')"
          >
            *
          </div>
          <div
            ref="divide"
            class="mr20 mt10 tag"
            type="danger"
            :disable-transitions="false"
            style="cursor:pointer;background:rgb(252, 156, 172)"
            @click="addItem('/','danger')"
          >
            /
          </div>
        </div>
        <div class="mt10">
          <div>
            表达式：
          </div>
          <div ref="expression" class="expression">
            <div v-if="expressionList.length===0" class="mt10">
              暂无公式
            </div>
            <el-tag
              v-for="(tag,index) in expressionList"
              :key="tag.name"
              class="mr6 mt10 "
              closable
              :type="tag.type?tag.type:'primary'"
              style="cursor:pointer"
              @close="deleteItem(index)"
            >
              {{ tag.name }}
            </el-tag>
          </div>
        </div>
      </div>

      <div class="mt10">
        <xButton
          type="primary" style="float:right;margin-right:10px"
          @click="submitForm"
        >
          {{ confirmText }}
        </xButton>
        <xButton
          type="danger" style="float:right;margin-right:10px"
          @click="closeDialog"
        >
          取消
        </xButton>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, getCurrentInstance, reactive, computed, watch } from 'vue'

const { proxy } = getCurrentInstance()
const _props = defineProps({
  // 是否展示选择框
  isShow: {
    type: Boolean,
    default: false
  },

  confirmText: {
    type: String,
    default: '确定'
  },

  list: {
    type: Object,
    default: () => {
      return []
    }
  },
  defaultExpresion: {
    type: String,
    default: ''
  }
})

watch(() => _props.isShow, () => {
  if (_props.isShow){
    expressionList.length = 0
    let str = _props.defaultExpresion
    let sign = 0
    let temp = ''
    let res = []
    for (let i = 0; i < str.length;){
      if (str[i] === '$'){
        sign = 1
        i += 2
      } else if (sign && str[i] === '}'){
        res.push(temp)
        temp = ''
        if (str[i + 1]) res.push(str[i + 1])
        i += 2
        sign = 0} else if (sign){
        temp += str[i]
        i++
      } else {
        res.push(str[i])
        i++
      }
    }
    res.forEach(item => {
      if ('()+-*/'.indexOf(item) === -1){
        let tag = _props.list.find(i => (i?.resultCode ?? i?.collectResultCode) === item)
        expressionList.push({
          name: tag?.resultName ?? tag?.collectResultName,
          code: tag?.resultCode ?? tag?.collectResultCode
        })

      } else {
        expressionList.push({
          name: item,
          type: 'danger'
        })
      }

    })

  }
})

const _emits = defineEmits(['closeDialog', 'inputDone', 'emitOpenDialog'])
const closeDialog = () => {
  _emits('closeDialog') // 关闭弹窗事件
}
const submitForm = () => {
  let res = ''
  expressionList.forEach(item => {
    if (item.type) res += item.name
    else res += `\${${item.code}}`
  })
  _emits('inputDone', res)
}

const expressionList = reactive([])

const addItem = (tag, type) => {
  if (type === 'danger'){
    expressionList.push({
      name: tag,
      type
    })
    return
  }
  expressionList.push({
    name: tag?.resultName ?? tag?.collectResultName,
    code: tag?.resultCode ?? tag?.collectResultCode
  })

}

const deleteItem = (val) => {

  expressionList.splice(val, 1)
}

const showDialog = computed({
  get(){

    return _props.isShow
  },
  set(){
    //null
  }
}) // 开启弹窗事件
</script>

<style lang="less" scoped>
.expression{
  margin: 10px 0;
  border: 1px dashed rgb(36, 173, 243);
  padding: 10px;
  padding-top: 0;
}
.tag{
  display: inline-block;
  background: #24adf3;
  padding: 4px 8px;
  color:#fff;
  border-radius: 4px;
  font-size: 12px;
}

.move{
  position: absolute;
}
</style>