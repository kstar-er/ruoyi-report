<template>
  <div v-if="isShow">
    <el-dialog
      v-model="showDialog"
      title="编辑数据权限"
      top="4%"
      :close-on-click-modal="false"
      :show-close="false"
      :width="width"
      lock-scroll
    >
      <template #header="{ titleId, titleClass }">
        <div class="jx-dialog-header">
          <h4 :id="titleId" :class="titleClass">
            编辑数据权限
            <slot name="customOperation" />
          </h4>
          <el-button
            class="dialog-close-btn"
            type="danger" icon="CloseBold"
            circle
            @click="closeDialog"
          />
        </div>
      </template>
      <div>
        <el-form
          ref="ruleFormRef"
          autocomplete="off"
          :model="myformData"
          label-width="150px"
          :inline="true"
          scroll-to-error
        >
          <el-form-item
            v-for="item in formEl"
            :key="item.key"
            :label="item.title"
            :rules="item.rules"
            :prop="item.key"
            class="mr10"
          >
            <el-input
              v-if="item.element === 'input'"
              v-model="myformData[item.key]"
              clearable
              :type="item.type" style="width:220px"
              :placeholder="`请输入${item.title}`"
              :disabled="item.type === 'dialog'||item.disabled"
              :autosize="{ minRows: 1, maxRows: 4 }"
              :show-password="item.type==='password'?true:false"
              autocomplete="new-password"
            >
              <template v-if="item.type === 'dialog'" #append>
                <el-button @click="emitOpenDialog(item.key)">
                  <el-icon><Edit /></el-icon>
                </el-button>
              </template>
            </el-input>
            <el-select
              v-if="item.element === 'select'"
              v-model="myformData[item.key]"
              style="width:220px"
              :disabled="item.disabled"
              :placeholder="`请选择${item.title}`"
              :multiple="item.type==='multiple'"
              collapse-tags
              collapse-tags-tooltip
              @change="item.change"
            >
              <el-option
                v-for="option in item.options"
                :key="option.label"
                :label="option.label"
                :value="option.value"
              />
            </el-select>
          </el-form-item>

          <el-form-item
            v-if="myformData&&myformData.dataScope === '2'"
            m-item
            label="自定义数据权限"
            :prop="myformData.menuIds"
            class="mr10"
            style="width:90%"
          >
            <div class="mb8">
              <el-checkbox
                v-model="allCheck" label="全选 / 全不选"
                @change="onCheckChange"
              />
            </div>
            <div
              style="
                  width:100%;
                  border:1px solid #dcdfe6;
                  padding:10px
                "
            >
              <el-tree
                ref="dataTreeRef"
                node-key="id"
                :default-checked-keys="defaultExpandedKeys"
                :props="defaultProps"
                :data="selectTreeNode"
                :default-expanded-keys="defaultExpandedKeys"
                show-checkbox
              />
            </div>
          </el-form-item>
        </el-form>
      </div>

      <div class="mt10">
        <el-button
          :loading="loading"
          type="primary" style="float:right;margin-right:10px"
          @click="submitForm"
        >
          确定
        </el-button>
        <el-button
          type="danger" style="float:right;margin-right:10px"
          @click="closeDialog"
        >
          取消
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, getCurrentInstance, reactive, computed } from 'vue'

const { proxy } = getCurrentInstance()
const _props = defineProps({
  // 是否展示选择框
  isShow: {
    type: Boolean,
    default: false
  },

  loading: {
    type: Boolean,
    default: false
  },

  // 弹框标题
  title: {
    type: String,
    default: ''
  },

  // 弹框宽度
  width: {
    type: String,
    default: '50%'
  },

  // 表单
  formEl: {
    type: Object,
    default: () => { return {} }
  },

  formData: {
    type: Object,
    default: () => { return {} }
  },

  selectTreeNode: {
    type: Object,
    default: () => { return [] }
  },

  defaultExpandedKeys: {
    type: Object,
    default: () => { return [] }
  }
})

let myformData = ref({})
const _emits = defineEmits(['closeDialog', 'inputDone', 'emitOpenDialog'])
const closeDialog = () => {
  myformData.value = {}
  _emits('closeDialog')
}// 关闭弹窗事件

const showDialog = computed({
  get(){
    if (_props.isShow && Object.values(_props.formData).length !== 0){
      Object.assign(myformData.value, _props.formData)
    }
    if (!_props.isShow) successSubmit()
    return _props.isShow
  },
  set(){
    //null
  }
}) // 开启弹窗事件

const emitOpenDialog = (key) => _emits('emitOpenDialog', key) // 输入框按钮弹窗

const submitForm = async () => {
  await proxy.$refs.ruleFormRef.validate((valid, fields) => {
    if (valid) {
      let deptIds = proxy.$refs.dataTreeRef?.getCheckedKeys()
      if (deptIds) myformData.value.deptIds = deptIds
      _emits('inputDone', JSON.parse(JSON.stringify(myformData.value)))
    } else {
      console.log('error submit!', fields)
    }
  })
}

const updateDialogInput = (propObj) => {
  Object.keys(propObj).forEach(key => {
    myformData.value[key] = propObj[key]
  })
}

const successSubmit = () => {
  myformData.value = {}
  proxy.$refs.ruleFormRef.resetFields()
}

const allCheck = ref(false)

const defaultProps = reactive({
  label: 'label',
  children: 'children'
})

const onCheckChange = (val) => {
  // null
  if (val){
    proxy.$refs.dataTreeRef?.setCheckedKeys(_props.selectTreeNode.map(item => item.id))
  } else {
    proxy.$refs.dataTreeRef?.setCheckedKeys([])
  }
  console.log(proxy.$refs.dataTreeRef?.getCheckedKeys())
}

defineExpose({
  updateDialogInput, successSubmit
})
</script>

<style lang="less" scoped>
:deep(.el-form-item__label){
	text-align: right;
	display: inline-block;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
}
:deep(.el-upload-list__item,.el-upload){
	width: 120px;
	height: 120px;
}
:deep(.el-upload){
	width: 120px;
	height: 120px;
}

.resetUpload{
	position: absolute;
	top: 0;
	left: 0;
}
</style>