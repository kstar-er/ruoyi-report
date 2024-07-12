<template>
  <div>
    <div v-if="isShow&&useDialog">
      <el-dialog
        v-model="showDialog"
        :title="title"
        top="4%"
        :close-on-click-modal="false"
        :close-on-press-escape="false"
        :show-close="false"
        :width="width"
        lock-scroll
      >
        <template #header="{ titleId, titleClass }">
          <div class="jx-dialog-header">
            <h4 :id="titleId" :class="titleClass">
              {{ title }}
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
        <div v-loading="loading">
          <el-form
            ref="ruleFormRef"
            autocomplete="off"
            :model="myformData"
            label-width="150px"
            :inline="true"
            scroll-to-error
            :disabled="disabled"
          >
            <el-form-item
              v-for="item in formInputEl"
              :key="item.key"
              :label="item.title"
              :rules="item.rules"
              :prop="item.key"
              class="mr10"
            >
              <template #label>
                <el-popover
                  v-if="item.illustrate" placement="top"
                  width="300"
                  :hide-after="0"
                >
                  <template #reference>
                    <el-icon style="color:orange">
                      <Warning />
                    </el-icon>
                  </template>
                  <template #default>
                    {{ item.illustrate }}
                    <el-link
                      v-if="item.key==='mapLongitude'||item.key==='mapLatitude'"
                      href="https://lbs.qq.com/getPoint/" target="_blank"
                      class="ml10"
                      type="primary"
                    >
                      点击跳转拾取经纬度
                    </el-link>
                  </template>
                </el-popover>
                {{ item.title }}
              </template>
              <el-input
                v-if="item.element === 'input'"
                v-model="myformData[item.key]"
                clearable
                :type="item.type" style="width:220px"
                :placeholder="`请输入${item.title}`"
                :disabled="item.canInput ? !item.canInput : item.type === 'dialog'||item.disabled"
                :autosize="{ minRows: 1, maxRows: 4 }"
                :show-password="item.type==='password'?true:false"
                autocomplete="new-password"
              >
                <template v-if="item.type === 'dialog'" #append>
                  <el-button :disabled="item.disabled" @click="emitOpenDialog(item.key)">
                    <el-icon><Edit /></el-icon>
                  </el-button>
                </template>
              </el-input>
              <el-radio-group
                v-if="item.element === 'radio'"
                v-model="myformData[item.key]" :disabled="item.disabled"
                @change="item.change"
              >
                <el-radio
                  v-for="option in item.options" :key="option.label"
                  :label="option.value"
                  :value="option.value"
                >
                  {{ option.label }}
                </el-radio>
              </el-radio-group>
            </el-form-item>
            <div />

            <el-form-item
              v-for="item in formSelectEl"
              :key="item.key"
              :label="item.title"
              :rules="item.rules"
              :prop="item.key"
              class="mr10"
            >
              <template #label>
                <el-popover
                  v-if="item.illustrate" placement="top"
                  width="300"
                  :hide-after="0"
                >
                  <template #reference>
                    <el-icon style="color:orange">
                      <Warning />
                    </el-icon>
                  </template>
                  <template #default>
                    {{ item.illustrate }}
                  </template>
                </el-popover>
                {{ item.title }}
              </template>
              <el-radio-group
                v-if="item.element === 'radio'" v-model="myformData[item.key]"
                :disabled="item.disabled"
                @change="item.change"
              >
                <el-radio
                  v-for="option in item.options" :key="option.label"
                  :label="option.value"
                  :value="option.value"
                >
                  {{ option.label }}
                </el-radio>
              </el-radio-group>
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
              <el-tree-select
                v-if="item.element === 'selectTree'"
                v-model="myformData[item.key]"
                :data="item.options"
                :placeholder="`请选择${item.title}`"
                check-strictly
                :render-after-expand="false"
                show-checkbox
                check-on-click-node
                :disabled="item.disabled"
              />
              <el-cascader
                v-if="item.element === 'proAndCityAndArea'"
                v-model="myformData[item.key]"
                style="width:220px"
                :disabled="item.disabled"
                :placeholder="`请选择${item.title}`"
                :options="pcaTextArr"
                collapse-tags
                collapse-tags-tooltip
                @change="handleChange"
              />
            </el-form-item>
            <div />
            <el-form-item
              v-for="item in formTimeAndNumber"
              :key="item.key"
              :label="item.title"
              :rules="item.rules"
              :prop="item.key"
              class="mr10"
            >
              <el-time-picker
                v-if="item.element === 'timerange'"
                v-model="myformData[item.key]"
                is-range
                range-separator="至"
                start-placeholder="开始时间"
                style="width:200px"
                end-placeholder="结束时间"
              />
              <el-date-picker
                v-if="item.element === 'datetimerange'"
                v-model="myformData[item.key]"
                type="datetimerange"
                range-separator="至"
                start-placeholder="开始时间"
                style="width:200px"
                end-placeholder="结束时间"
                value-format="YYYY-MM-DD HH:mm:ss"
                :disabled="item.disabled"
              />
              <el-date-picker
                v-if="item.element === 'daterange'"
                v-model="myformData[item.key]"
                type="daterange"
                range-separator="至"
                start-placeholder="开始时间"
                style="width:200px"
                end-placeholder="结束时间"
                value-format="YYYY-MM-DD"
                :disabled="item.disabled"
              />
              <el-date-picker
                v-if="item.element === 'date'"
                v-model="myformData[item.key]"
                :type="item.type"
                range-separator="至"
                start-placeholder="开始时间"
                style="width:200px"
                end-placeholder="结束时间"
                value-format="YYYY-MM"
                :disabled="item.disabled"
              />
              <el-input-number
                v-if="item.element === 'number'"
                v-model="myformData[item.key]" :disabled="item.disabled"
                :min="0"
                :max="item.max?item.max:100000000"
                controls-position="right"
                placeholder="1"
                size="small"
              />
            </el-form-item>
            <div />
            <el-form-item
              v-if="isLimits"
              m-item
              label="菜单与权限"
              :prop="myformData.menuIds"
              class="mr10"
              style="width:90%"
            >
              <template #label>
                <el-popover placement="left" width="400">
                  <template #reference>
                    <el-icon style="color:orange">
                      <Warning />
                    </el-icon>
                  </template>
                  <template #default>
                    如果是菜单或目录，勾选则表示可见，反之不可见，如果是功能按钮，勾选表示可用，反之不可用。
                  </template>
                </el-popover>
                {{ '菜单权限' }}
              </template>

              <div class="mb8">
                <el-checkbox
                  v-model="allCheck" label="全选 / 全不选"
                  @change="onCheckChange"
                />
                <el-checkbox
                  v-model="myformData.menuCheckStrictly" label="父子联动"
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
                  ref="treeRef"
                  node-key="id"
                  :default-checked-keys="defaultExpandedKeys"
                  :props="defaultProps"
                  :data="selectTreeNode"
                  show-checkbox
                  highlight-current
                  check-on-click-node
                  :check-strictly="!myformData.menuCheckStrictly"
                  @check-change="selectTreeCurrentChange"
                />
              </div>
            </el-form-item>
            <div />

            <div />
            <el-form-item
              v-for="item in formTextAreaEl"
              :key="item.key"
              :label="item.title"
              :rules="item.rules"
              :prop="item.key"
              class="mr10"
              style="width:90%"
            >
              <el-input
                v-if="item.element === 'input'"
                v-model="myformData[item.key]"
                :type="item.type"
                :placeholder="`请输入${item.title}`"
                :disabled="item.type === 'dialog'||item.disabled"
                :autosize="{ minRows: 3, maxRows: 6 }"
              >
                <template v-if="item.type === 'dialog'" #append>
                  <el-button @click="emitOpenDialog(item.key)">
                    <el-icon><Edit /></el-icon>
                  </el-button>
                </template>
              </el-input>
            </el-form-item>
            <div />
            <el-form-item
              v-for="item in formUploadEl"
              :key="item.key"
              :label="item.title"
              :rules="item.rules"
              :prop="item.key"
              class="mr10"
            >
              <el-upload
                :ref="'upload'+item.key"
                v-model="myformData"
                :file-list="myformData[item.key] ? [{url:myformData[item.key]}] : []"
                class="mr20"
                action="https://jxwlapp.oss-cn-guangzhou.aliyuncs.com"
                :data="myClient"
                :limit="1"
                :before-upload="item.beforeUpload"
                :on-success="item.onSuccess"
                :on-exceed="onExceed"
                accept=".png, .jpg, .jpeg"
                list-type="picture-card"
              >
                <el-icon><Plus /></el-icon>
                <template #file="{ file }">
                  <el-image
                    style="width: 120px; height: 120px"
                    :src="file.url"
                    :zoom-rate="1.2"
                    :preview-src-list="[file.url]"
                    :initial-index="1"
                    fit="cover"
                  />
                  <el-button
                    class="resetUpload" icon="Delete"
                    circle
                    size="small"
                    @click="removeUpload('upload'+item.key,item.key)"
                  />
                </template>
              </el-upload>
            </el-form-item>
            <slot name="append" />
          </el-form>
        </div>

        <div class="mt10">
          <xButton
            :loading="loading"
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
    <div v-else v-loading="loading">
      <el-form
        ref="ruleFormRef"
        autocomplete="off"
        :model="myformData"
        label-width="150px"
        :inline="true"
        scroll-to-error
        :disabled="disabled"
      >
        <el-form-item
          v-for="item in formInputEl"
          :key="item.key"
          :label="item.title"
          :rules="item.rules"
          :prop="item.key"
          class="mr10"
        >
          <template #label>
            <el-popover
              v-if="item.illustrate" placement="top"
              width="300"
              :hide-after="0"
            >
              <template #reference>
                <el-icon style="color:orange">
                  <Warning />
                </el-icon>
              </template>
              <template #default>
                {{ item.illustrate }}
                <el-link
                  v-if="item.key==='mapLongitude'||item.key==='mapLatitude'"
                  href="https://lbs.qq.com/getPoint/" target="_blank"
                  class="ml10"
                  type="primary"
                >
                  点击跳转拾取经纬度
                </el-link>
              </template>
            </el-popover>
            {{ item.title }}
          </template>
          <el-input
            v-if="item.element === 'input'"
            v-model="myformData[item.key]"
            clearable
            :type="item.type" style="width:220px"
            :placeholder="`请输入${item.title}`"
            :disabled="item.canInput ? !item.canInput : item.type === 'dialog'||item.disabled"
            :autosize="{ minRows: 1, maxRows: 4 }"
            :show-password="item.type==='password'?true:false"
            autocomplete="new-password"
          >
            <template v-if="item.type === 'dialog'" #append>
              <el-button :disabled="item.disabled" @click="emitOpenDialog(item.key)">
                <el-icon><Edit /></el-icon>
              </el-button>
            </template>
          </el-input>
          <el-radio-group
            v-if="item.element === 'radio'"
            v-model="myformData[item.key]" :disabled="item.disabled"
            @change="item.change"
          >
            <el-radio
              v-for="option in item.options" :key="option.label"
              :label="option.value"
              :value="option.value"
            >
              {{ option.label }}
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <div />

        <el-form-item
          v-for="item in formSelectEl"
          :key="item.key"
          :label="item.title"
          :rules="item.rules"
          :prop="item.key"
          class="mr10"
        >
          <template #label>
            <el-popover
              v-if="item.illustrate" placement="top"
              width="300"
              :hide-after="0"
            >
              <template #reference>
                <el-icon style="color:orange">
                  <Warning />
                </el-icon>
              </template>
              <template #default>
                {{ item.illustrate }}
              </template>
            </el-popover>
            {{ item.title }}
          </template>
          <el-checkbox-group v-if="item.element === 'check'" v-model="myformData[item.key]">
            <el-checkbox
              v-for="option in item.options" :key="option.value"
              :disabled="option.disabled"
              :label="option.label" :value="option.value"
            />
          </el-checkbox-group>

          <el-radio-group
            v-if="item.element === 'radio'" v-model="myformData[item.key]"
            :disabled="item.disabled"
            @change="item.change"
          >
            <el-radio
              v-for="option in item.options" :key="option.label"
              :label="option.value"
              :value="option.value"
            >
              {{ option.label }}
            </el-radio>
          </el-radio-group>
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
          <el-tree-select
            v-if="item.element === 'selectTree'"
            v-model="myformData[item.key]"
            :data="item.options"
            :placeholder="`请选择${item.title}`"
            check-strictly
            :render-after-expand="false"
            show-checkbox
            check-on-click-node
            :disabled="item.disabled"
          />
          <el-cascader
            v-if="item.element === 'proAndCityAndArea'"
            v-model="myformData[item.key]"
            style="width:220px"
            :disabled="item.disabled"
            :placeholder="`请选择${item.title}`"
            :options="pcaTextArr"
            collapse-tags
            collapse-tags-tooltip
            @change="handleChange"
          />
        </el-form-item>
        <div />
        <el-form-item
          v-for="item in formTimeAndNumber"
          :key="item.key"
          :label="item.title"
          :rules="item.rules"
          :prop="item.key"
          class="mr10"
        >
          <el-time-picker
            v-if="item.element === 'timerange'"
            v-model="myformData[item.key]"
            is-range
            range-separator="至"
            start-placeholder="开始时间"
            style="width:200px"
            end-placeholder="结束时间"
          />
          <el-date-picker
            v-if="item.element === 'datetimerange'"
            v-model="myformData[item.key]"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            style="width:200px"
            end-placeholder="结束时间"
            value-format="YYYY-MM-DD HH:mm:ss"
            :disabled="item.disabled"
          />
          <el-date-picker
            v-if="item.element === 'daterange'"
            v-model="myformData[item.key]"
            type="daterange"
            range-separator="至"
            start-placeholder="开始时间"
            style="width:200px"
            end-placeholder="结束时间"
            value-format="YYYY-MM-DD"
            :disabled="item.disabled"
          />
          <el-date-picker
            v-if="item.element === 'date'"
            v-model="myformData[item.key]"
            :type="item.type"
            range-separator="至"
            start-placeholder="开始时间"
            style="width:200px"
            end-placeholder="结束时间"
            value-format="YYYY-MM"
            :disabled="item.disabled"
          />
          <el-input-number
            v-if="item.element === 'number'"
            v-model="myformData[item.key]" :disabled="item.disabled"
            :min="0"
            :max="item.max?item.max:100000000"
            controls-position="right"
            placeholder="1"
            size="small"
          />
        </el-form-item>
        <div />
        <el-form-item
          v-if="isLimits"
          m-item
          label="菜单与权限"
          :prop="myformData.menuIds"
          class="mr10"
          style="width:90%"
        >
          <template #label>
            <el-popover placement="left" width="400">
              <template #reference>
                <el-icon style="color:orange">
                  <Warning />
                </el-icon>
              </template>
              <template #default>
                如果是菜单或目录，勾选则表示可见，反之不可见，如果是功能按钮，勾选表示可用，反之不可用。
              </template>
            </el-popover>
            {{ '菜单权限' }}
          </template>

          <div class="mb8">
            <el-checkbox
              v-model="allCheck" label="全选 / 全不选"
              @change="onCheckChange"
            />
            <el-checkbox
              v-model="myformData.menuCheckStrictly" label="父子联动"
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
              ref="treeRef"
              node-key="id"
              :default-checked-keys="defaultExpandedKeys"
              :props="defaultProps"
              :data="selectTreeNode"
              show-checkbox
              highlight-current
              check-on-click-node
              :check-strictly="!myformData.menuCheckStrictly"
              @check-change="selectTreeCurrentChange"
            />
          </div>
        </el-form-item>
        <div />

        <div />
        <el-form-item
          v-for="item in formTextAreaEl"
          :key="item.key"
          :label="item.title"
          :rules="item.rules"
          :prop="item.key"
          class="mr10"
          style="width:90%"
        >
          <el-input
            v-if="item.element === 'input'"
            v-model="myformData[item.key]"
            :type="item.type"
            :placeholder="`请输入${item.title}`"
            :disabled="item.type === 'dialog'||item.disabled"
            :autosize="{ minRows: 3, maxRows: 6 }"
          >
            <template v-if="item.type === 'dialog'" #append>
              <el-button @click="emitOpenDialog(item.key)">
                <el-icon><Edit /></el-icon>
              </el-button>
            </template>
          </el-input>
        </el-form-item>
        <div />
        <el-form-item
          v-for="item in formUploadEl"
          :key="item.key"
          :label="item.title"
          :rules="item.rules"
          :prop="item.key"
          class="mr10"
        >
          <el-upload
            :ref="'upload'+item.key"
            v-model="myformData"
            :file-list="myformData[item.key] ? [{url:myformData[item.key]}] : []"
            class="mr20"
            action="https://jxwlapp.oss-cn-guangzhou.aliyuncs.com"
            :data="myClient"
            :limit="1"
            :before-upload="item.beforeUpload"
            :on-success="item.onSuccess"
            :on-exceed="onExceed"
            accept=".png, .jpg, .jpeg"
            list-type="picture-card"
          >
            <el-icon><Plus /></el-icon>
            <template #file="{ file }">
              <el-image
                style="width: 120px; height: 120px"
                :src="file.url"
                :zoom-rate="1.2"
                :preview-src-list="[file.url]"
                :initial-index="1"
                fit="cover"
              />
              <el-button
                class="resetUpload" icon="Delete"
                circle
                size="small"
                @click="removeUpload('upload'+item.key,item.key)"
              />
            </template>
          </el-upload>
        </el-form-item>
        <slot name="append" />
      </el-form>
      <div class="mt10 footer">
        <el-button
          type="primary" size="small"
          @click="submitForm"
        >
          确定
        </el-button>
        <el-button
          type="danger" size="small"
          @click="closeDialog"
        >
          取消
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, getCurrentInstance, reactive, computed, watch } from 'vue'
import { pcaTextArr } from 'element-china-area-data'

const { proxy } = getCurrentInstance()
const _props = defineProps({
  // 是否展示选择框
  isShow: {
    type: Boolean,
    default: false
  },

  useDialog: {
    type: Boolean,
    default: true
  },

  loading: {
    type: Boolean,
    default: false
  },
  confirmText: {
    type: String,
    default: '确定'
  },

  disabled: {
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
    default: '60%'
  },

  // 表单
  formSelectEl: {
    type: Object,
    default: () => { return {} }
  },

  // 表单
  formInputEl: {
    type: Object,
    default: () => { return {} }
  },

  // 表单
  formTextAreaEl: {
    type: Object,
    default: () => { return {} }
  },

  // 表单
  formUploadEl: {
    type: Object,
    default: () => { return {} }
  },

  // 表单
  formTimeAndNumber: {
    type: Object,
    default: () => { return {} }
  },

  myClient: {
    type: Object,
    default: () => { return {} }
  },

  isLimits: {
    type: Boolean,
    default: false
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
  },

  isChange: {
    type: Boolean,
    default: false
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
      if (_props.isLimits){
        let menuList = proxy.$refs.treeRef?.getCheckedKeys()
        let halfMenuList = proxy.$refs.treeRef?.getHalfCheckedKeys()
        myformData.value.menuIds = menuList.concat(halfMenuList)
      }
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

const removeUpload = (val, key) => {
  myformData.value[key] = ''
  proxy.$refs[val][0].clearFiles()
}

const onExceed = () => {
  proxy.$message.error('已上传过图片，需要重新上传请先删除之前的图片')
}

const handleChange = (value) => {
  myformData.value.province = value[0]
  myformData.value.city = value[1]
  myformData.value.area = value[2] ?? ''

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
    proxy.$refs.treeRef.setCheckedKeys(_props.selectTreeNode.map(item => item.id))
  } else {
    proxy.$refs.treeRef.setCheckedKeys([])
  }

}

watch(() => _props.isChange, () => {
  console.log(_props.formData)
  Object.assign(myformData.value, _props.formData)
}, { immediate: true })

const selectTreeCurrentChange = (val, node) => {

  // console.log(val, node)
}

const clearInput = (val, node) => {
  myformData.value = {}
}

defineExpose({
  updateDialogInput, successSubmit, clearInput
})

/**
 * @param {{ HTML调用实例 }}
 *
 */
//   <selectDialogForm
//     ref="dialogForm"
//     :is-show="isShow"
//     :title="'测试弹框'"
//     :form-el="formEl"
//     @close-dialog="isShow=false"
//     @emitOpenDialog="emitOpenDialog"
//     @inputDone="inputDone"
//   >
/**
 * @param {{ JS 调用实例 }}
 *
 */
// let isShow = ref(false)
// const formEl = reactive([{
//   label: '测试',
//   element: 'input',
//   type: 'text',
//   key: 'test1',
//   rules: [{
//     required: true,
//     message: '不能为空',
//     trigger: 'blur'
//   }]
// }, {
//   label: '测试',
//   element: 'input',
//   type: 'dialog',
//   key: 'test2',
//   rules: [{
//     required: true,
//     message: '不能为空',
//     trigger: 'blur'
//   }]
// }])

// const emitOpenDialog = (val) => {
//   let key = {
//     test2: 9999,
//     test3: 8888
//   }
//   proxy.$refs.dialogForm.updateDialogInput(key)
// }

// const inputDone = (doneRes) => {
//   console.log(doneRes)
//   isShow = false
// }
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

:deep(.el-select){
  width: 220px;
}

.footer{
  display: flex;
  justify-content: flex-end;
}
</style>