<template>
  <div class="container">
    <div class="container_common">
      <div
        v-for="(item,idx) in filterForm" :key="idx"
        class="container_common_formArea"
      >
        <span
          v-if="item.type" class="container_common_formArea-label"
          :title="item.label"
        >{{ item.label }}：</span>
        <el-input
          v-if="item.type==='text'"
          v-model="item.value" clearable
          :placeholder="item.placeholder ? item.placeholder:`请输入${item.label}`"
          class="container_common_formArea-input"
          @keyup.enter="search"
        />
        <el-input
          v-if="item.type==='dialog'"
          v-model="item.value"
          clearable
          :placeholder="`请选择${item.label}`"
          class="container_common_formArea-input"
          @keyup.enter="search"
        >
          <template #append>
            <el-button @click="openDialog({ type:'default',index:idx,prop:item.prop })">
              <el-icon><Edit /></el-icon>
            </el-button>
          </template>
        </el-input>
        <el-select
          v-if=" item.type==='select' || item.type === 'multiple' "
          v-model="item.value"
          clearable
          :multiple="Boolean(item.type === 'multiple')"
          collapse-tags
          class="container_common_formArea-input"
          :placeholder="`请选择${item.label}`"
        >
          <el-option
            v-for="i in item.options"
            :key="i.value"
            :label="i.label"
            :value="i.value"
          />
        </el-select>
        <el-date-picker
          v-if="item.type==='date'"
          v-model="item.value"
          class="container_common_formArea-input"
          type="date"
          :placeholder="`请选择${item.label}`"
          :default-time="defaultTime"
          value-format="YYYY/MM/DD"
        />
        <el-date-picker
          v-if="item.type==='daterange'||item.type==='datetimerange'"
          v-model="item.value"
          class="container_common_formArea-input"
          :type="item.type"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          unlink-panels
          :default-time="defaultTime"
          :value-format="item.type==='datetimerange'?'YYYY/MM/DD HH:mm':'YYYY/MM/DD'"
        />
      </div>
      <div class="container_common_senionItems">
        <div class="container_btnGroup">
          <el-tooltip
            class="box-item"
            effect="dark"
            content="按回车可以直接执行搜索"
            placement="bottom"
          >
            <xButton
              class=""
              @click.stop="search"
            >
              查询
            </xButton>
          </el-tooltip>
          <xButton
            type="info"
            class="ml10"
            @click.stop="resetInput(1)"
          >
            重置
          </xButton>
        </div>
        <xButton
          v-if="!isPad"
          type="warning"
          class="ml10"
          @click.stop="showSenion"
        >
          {{ activeNames.length !== 0 ? '收起': '更多' }}
        </xButton>
        <el-popover
          v-if="useDataFilter"
          placement="bottom"
          title="数据过滤器"
          :width="200"
          trigger="click"
          content="this is content, this is content, this is content"
        >
          <template #reference>
            <el-button
              icon="Filter"
              circle
              size="small"
            />
          </template>
          <div class="filter-content">
            <div class="filter-content-item">
              <span>显示最近7天: </span>
              <el-switch
                v-model="isShowCurrentDay"
                class="ml-2"
                style="--el-switch-on-color: #13ce66; --el-switch-off-color: #ff4949;margin-left:10px"
                @change="currentDayChange"
              />
            </div>
          </div>
        </el-popover>
      </div>
    </div>
    <el-collapse v-model="activeNames">
      <el-collapse-item name="1">
        <template #title />
        <div class="container_common">
          <div
            v-for="(item,idx) in senionItems" :key="idx"
            class="container_common_formArea"
          >
            <span
              v-if="item.type" class="container_common_formArea-label"
              :title="item.label"
            >{{ item.label }}：</span>
            <el-input
              v-if="item.type==='text'"
              v-model="item.value" clearable
              :placeholder="`请输入${item.label}`"
              class="container_common_formArea-input"
              @keyup.enter="search"
            />
            <el-input
              v-if="item.type==='dialog'"
              v-model="item.value"
              clearable
              :placeholder="`请选择${item.label}`"
              class="container_common_formArea-input"
              @keyup.enter="search"
            >
              <template #append>
                <el-button @click="openDialog({ type:'expand',index:idx,prop:item.prop })">
                  <el-icon><Edit /></el-icon>
                </el-button>
              </template>
            </el-input>
            <el-select
              v-if="item.type==='select'"
              v-model="item.value"
              clearable
              class="container_common_formArea-input"
              :placeholder="(item.label==='上楼'||item.label==='卸货')?`请选择${'是否'+item.label}`:`${item.label}`"
            >
              <el-option
                v-for="i in item.options"
                :key="i.value"
                :label="i.label"
                :value="i.value"
              />
            </el-select>
            <el-date-picker
              v-if="item.type==='daterange'||item.type==='datetimerange'"
              v-model="item.value"
              class="container_common_formArea-input"
              :type="item.type"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              unlink-panels
              :default-time="defaultTime"
              :shortcuts="item.shortcuts"
              :disabled-minutes="getDisabledMinutes"
              :disabled-seconds="getDisabledSecond"
              :value-format="item.type==='datetimerange'?'YYYY/MM/DD HH:mm':'YYYY/MM/DD'"
            />
            <el-date-picker
              v-if="item.type==='date'"
              v-model="item.value"
              class="container_common_formArea-input"
              type="date"
              :placeholder="`请选择${item.label}`"
              :default-time="defaultTime"
              value-format="YYYY/MM/DD"
            />
          </div>
          <!-- <div class="container_common_extra">
            <el-button
              type="success"
              :loading="false"
              color="rgb(63, 63, 63)"
              @click="resetInput(0)"
            >
              重置高级筛选
            </el-button>
          </div> -->
        </div>
      </el-collapse-item>
    </el-collapse>
  </div>
</template>

<script setup>
import { onBeforeMount, reactive, ref } from 'vue'

const defaultTime = new Date(2000, 1, 1, 12, 0, 0)

let _emit = defineEmits(["search", 'openDialog', 'currentDayChange'])

const _props = defineProps({
  filterItems: {
    type: Object,
    default(){
      return []
    }
  },
  expandFilterItems: {
    type: Object,
    default(){
      return []
    }
  },
  useDataFilter: {
    type: Boolean,
    default: false
  },
  isPad: {
    type: Boolean,
    default: false
  }
})

let activeNames = reactive([])

let isShowCurrentDay = ref(_props.useDataFilter)

const currentDayChange = () => {
  _emit('currentDayChange', isShowCurrentDay.value)
}

let filterForm = reactive([])

let senionItems = reactive([])

const showSenion = function(){
  if (activeNames.length === 0){
    activeNames.push('1')
  } else {
    activeNames.splice(0, 1)
  }

}

const search = function(){
  let filter = filterForm.filter(item => item.value && item.value.toString().length !== 0 || item.value === 0)
  filter = filter.concat(senionItems.filter(item => item.value && item.value.toString().length !== 0 || item.value === 0))
  let temp = {}
  filter.forEach(item => {
    if (item.prop.indexOf('Time') !== -1 || item.prop.indexOf('Date') !== -1){
      if (item.type !== 'daterange' && item.type !== 'datetimerange') {
        temp[item.prop] = Date.parse(item.value + ' 00:00:00')
      } else temp[item.prop] = typeof item.value === 'string' ? item.value.replace(/(^\s*)/g, "") : item.value
    }
    else temp[item.prop] = typeof item.value === 'string' ? item.value.replace(/(^\s*)/g, "") : item.value
  })
  _emit('search', temp)
}

const resetInput = function(b){
  if (b){
    // 重置全部
    for (let i = 0;i < filterForm.length;i++){
      filterForm[i].value = ''
    }
  }

  // 重置高级筛选
  for (let i = 0;i < senionItems.length;i++){
    senionItems[i].value = ''
  }
  if (b) _emit('search', {})
  else search()

}

let openDialog = (propObj) => _emit('openDialog', propObj)

const backfill = (propObj, val) => {
  if (propObj.type === 'expand'){
    senionItems[propObj.index].value = val
  } else {
    filterForm[propObj.index].value = val
  }
}

const backfillSearch = (keyList, valList) => {
  keyList.forEach((key, index) => {
    let idx = filterForm.findIndex(item => item.prop === key)
    filterForm[idx].value = valList[index]
  })

}

defineExpose({
  backfill,
  backfillSearch
})
onBeforeMount(() => {
  let daterangeList = []
  _props.filterItems.forEach((item) => {
    if (item.type !== 'none'){
      if ((item.dataKey === 'processStatus' || item.prop) && item.type === 'multiple'){
        filterForm.push({
          label: item.title ? item.title : item.label,
          prop: item.prop,
          value: '',
          type: item.type,
          options: item.options ? item.options : []
        })
      } else if (item.type === 'daterange' || item.type === 'datetimerange'){
        daterangeList.push(
          {
            label: item.title ? item.title : item.label,
            prop: item.dataKey ? item.dataKey : item.prop,
            value: '',
            type: item.type,
            options: item.options ? item.options : [],
            shortcuts: item.shortcuts ? item.shortcuts : []
          }
        )
      } else {
        filterForm.push({
          label: item.title ? item.title : item.label,
          prop: item.dataKey ? item.dataKey : item.prop,
          value: '',
          type: item.type,
          options: item.options ? item.options : []
        })
      }
    }
  })
  filterForm.push(...daterangeList)

  let count = window.screen.width < 1920 ? 3 : 4
  if (_props.isPad) senionItems.push(...filterForm)
  else senionItems.push(...filterForm.splice(count, filterForm.length))
  _props.expandFilterItems.forEach(item => {
    senionItems.unshift({
      label: item.title ? item.title : item.label,
      prop: item.dataKey ? item.dataKey : item.prop,
      value: '',
      type: item.type,
      options: item.options ? item.options : []
    })
  })
})

const getDisabledMinutes = (data) => {
  let start = makeRange(1, 29)
  let end = makeRange(31, 59)
  start = start.concat(end)
  return start
}

const getDisabledSecond = (data) => {
  return makeRange(1, 59)
}

const makeRange = (start, end) => {
  const result = []
  for (let i = start; i <= end; i++) {
    result.push(i)
  }
  return result
}
</script>

<style lang="less" scoped>
.container{
  width: 100%;
  :deep(.el-collapse-item__wrap){
    border-bottom: none;
  }
  &_btnGroup{
    display: flex;
  }
  :deep(.el-collapse-item__content){
    padding-bottom: 0;
  }

    border-bottom:1px solid rgb(235, 238, 245);
    width: 98%;
    text-align: center;

  :deep(.el-collapse-item__header){
    display: hidden;
    opacity: 0;
    height: 0 !important;
    width: 0 !important;
  }
  :deep(.el-collapse){
    border: none;
  }
  :deep(.is-active:hover) {
    background-color: transparent;
  }
  &_common{
    display: flex;
    flex-wrap: wrap;
    padding-bottom: 4px;
    width:100%;
  &_formArea{
    display: flex;
    justify-content: center;
    align-items: center;
    height: 36px;
    padding: 10px 0;
    &-label{
      box-sizing: border-box;
      padding-left: 6px;
      color: #606266;
      text-align: right;
      display: inline-block;
      width: 114px;
      font-size: 14px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
  }
  &_senionItems{
    transition: all .3s;
    cursor: pointer;
    text-align: left;
    display: flex;
    align-items: center;
    justify-content: flex-end;
    margin: 10px 0 10px 18px;
    &-icon{
      color: #fff;
        margin-right: 6px;
        margin-top: 2px;
    }
    &-text{
      color: #fff;
        font-size: 14px;
    }
    &-text:hover{
      color: #fff;
    }
    &-text:focus{
      color: #fff;
    }
  }
  &_extra{
    width: 150px;
    height: 30px;
    margin-left: 78px;
    margin-top: 12px;
    border-radius: 18px;
    padding: 0 20px;
  }
  :deep(.el-input){
    width: 200px;
    height: 30px;
  }
  :deep(.el-range-editor.el-input__inner){
    height: 30px;
  }

}

}
:deep(.el-collapse-item__arrow){
  display: none;
}
:deep(.el-date-editor){
  width: 280px;
}

:deep(.el-select){
  width: 200px;

}
</style>

<style>
.el-popover__title{
  color:#999 !important;
  font-size: 14px !important;
}
.el-time-spinner__item.is-disabled{
  display: none !important;
}
</style>