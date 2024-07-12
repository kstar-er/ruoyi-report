<template>
  <div class="content">
    <div v-if="!isComponent" class="left">
      <div class="left-title">
        表达式管理
      </div>
      <div class="left-body">
        <div>
          <el-input
            v-model="searchVal"
            placeholder="输入表达式名称进行搜索"
            class="mr10"
            style="width: 200px;"
          />
          <xButton type="primary" @click="search">
            搜索
          </xButton>
        </div>
        <div class="mt20">
          <el-dropdown trigger="click" @command="addConditions">
            <el-button
              type="primary" style="height:32px"
              plain
            >
              新增一条表达式 <el-icon class="ml10">
                <CirclePlus />
              </el-icon>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="[!]AND">
                  关联条件：并且
                </el-dropdown-item>
                <el-dropdown-item command="[!]OR">
                  关联条件：或者
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
          <xButton
            type="danger" style="height:30px"
            class="ml10"
            @click="deleteRows"
          >
            批量删除
          </xButton>
        </div>
        <div v-loading="loading&&!selectTableData.isShow" class="left-content mt20">
          <list :data-source="dataSource" @edit="edit" />
        </div>
      </div>
    </div>
    <div
      v-if="editItem.id" v-loading="loading&&!selectTableData.isShow"
      class="right"
    >
      <el-card class="box-card">
        <template #header>
          <div class="flex-between">
            <div class="flex-center">
              <el-popover placement="right" width="300">
                <template #reference>
                  <el-icon style="color:orange">
                    <Warning />
                  </el-icon>
                </template>
                <template #default>
                  关联条件是 ‘AND’ 的请保证每个条件以 ‘AND’ 连接，反之请保证每个条件以 ‘OR’ 连接，否则将会导致试图混乱
                </template>
              </el-popover>
              <div class="flex-center" style="color:#126f9e">
                <span style="width:100px" class="ml10">表达式名称：</span>
                <el-input
                  v-model="editItem.conditionName" style="width:200px" class="mr10"
                  type="textarea"
                />
                <span>关联条件：<span style="color:red;font-weight:800">{{ editItem.publicConnect }}</span></span>
                <span class="ml10">绑定计划：<span style="color:red;font-weight:800">{{ editItem.schemeCode }}</span></span>
              </div>
            </div>
            <div>
              <xButton class="mr10" @click="save">
                保存
              </xButton>
              <xButton
                type="danger"
                class="mr10"
                @click="deleteRow"
              >
                删除该表达式
              </xButton>
              <xButton
                v-if="!isComponent"
                type="warning"
                @click="closeDetail"
              >
                关闭
              </xButton>
            </div>
          </div>
        </template>
        <div class="card-body">
          <div class="body-left">
            关联表：
            <el-input
              v-model="editItem.tableName"
              clearable
              style="width:220px"
              placeholder="请选择关联表"
            >
              <template #append>
                <el-button @click="openSelectDialog">
                  <el-icon><Edit /></el-icon>
                </el-button>
              </template>
            </el-input>
            <el-input
              v-model="editItem.conditionField" type="textarea"
              class="mt10"
              style="width:280px;"
              :autosize="{ minRows: 6, maxRows: 10 }"
              placeholder="请输入测试 sql"
            />
            <el-button class="mt10" @click="test">
              渲染视图
            </el-button>
            <el-button class="mt10" @click="test2(expression)">
              生成语句
            </el-button>
          </div>
          <div class="body-right">
            <div class="right-content">
              <div v-if="expression.children.length===0">
                暂无任何内容 ！
              </div>
              <div v-else>
                <expressionRender :expression="expression" :selectoptions="selectoptions" />
              </div>
            </div>
            <div
              v-if="selectoptions.length!==0"
              class="right-options"
            >
              <el-tooltip
                class="box-item"
                effect="dark"
                content="添加单条件"
                placement="left"
              >
                <div style="font-size:20px;margin-top:10px;cursor: pointer;" @click="addSingle">
                  <el-icon><Plus /></el-icon>
                </div>
              </el-tooltip>
              <el-tooltip
                class="box-item"
                effect="dark"
                content="添加集条件"
                placement="left"
              >
                <el-dropdown trigger="click" @command="addSet">
                  <div style="font-size:20px;margin-top:10px;cursor: pointer; color: #000;">
                    <el-icon><CirclePlus /></el-icon>
                  </div>
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item v-if="expression.connect === '[!]OR'" command="[!]AND">
                        且
                      </el-dropdown-item>
                      <el-dropdown-item v-if="expression.connect === '[!]AND'" command="[!]OR">
                        或
                      </el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </el-tooltip>
            </div>
          </div>
        </div>
      </el-card>
    </div>
    <selectDialogTable
      v-if="selectTableData.isShow"
      ref="dialogSearch"
      :use-choose="selectTableData.useChoose"
      :append-table-style="{ background: '#126f9e', color: '#fff', borderColor: 'rgba(192, 192, 192,.5)' }"
      :width="'60%'"
      :search-input-placeholder="selectTableData.searchPlaceholde"
      :is-show="selectTableData.isShow"
      :data-source="selectTableData.dataSource"
      :title="'选择数据'"
      is-show-search-input
      :loading="loading"
      @close-dialog="selectTableData.isShow=false"
      @doDialogSearch="selectTableData.doSearch"
      @chooseRow="selectTableData.chooseRow"
    />
  </div>
</template>

<script setup>
import selectDialogTable from '@/components/public/selectDialogTable.vue'
import { reactive, onBeforeMount, h, ref, getCurrentInstance } from 'vue'
import { expressionRender, parsingExpressions, createSql } from './js/expressionScript'
import { DataSource, loading } from './utils/index'
import list from './components/list.vue'

const props = defineProps({
  isComponent: {
    type: Boolean,
    default: false
  },
  conditionItem: {
    type: Object,
    default: () => {
      return {}
    }
  }
})

const { proxy } = getCurrentInstance()

const val = ref(``)

// status = 'down' [!]AND createTime > 1697817600000 [!]AND ( number > 100 [!]OR number < 500 [!]OR ( name = 'gg' [!]AND name = 'mm' ) )
// const publicConnect = ref('')
const expression = ref({
  connect: '',
  children: []
})
const selectoptions = reactive([])

let dataSource = ref(null)
const initList = () => {
  dataSource.value = new DataSource({
    header: [],
    selectUri: '/colorful-fog/condition/select'
  })
  if (!props.isComponent) dataSource.value.initData(this, proxy.$refs.table)
}
const searchVal = ref('')
const search = () => {
  dataSource.value.searchData = {}
  if (searchVal.value.length !== 0) dataSource.value.searchData.conditionName = searchVal.value
  dataSource.value.initData(this, proxy.$refs.table)

}

onBeforeMount(() => {
  initList()
  if (props.isComponent) edit(props.conditionItem)
})

const editItem = reactive({})

const edit = (val) => {
  console.log(val)
  Object.assign(editItem, val)
  selectoptions.length = 0
  if (editItem.tableName) {
    dataSource.value.getTableFiled(editItem.tableName).then(res => {
      let temp = res.map(item => {
        return {
          value: item.field,
          label: item.fieldName,
          type: item.fieldName.indexOf('时间') !== -1 ? 'time' : null
        }
      })
      selectoptions.push(...temp.filter((item, index, arr) => {
        return index === arr.findIndex(a => a.value === item.value)
      }))
      expression.value = parsingExpressions(editItem.conditionField, editItem.publicConnect)

    })
  } else expression.value = parsingExpressions(editItem.conditionField, editItem.publicConnect)
}

const addConditions = (command) => {
  proxy.$prompt('请输入表达式名称', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  })
    .then(({ value }) => {
      dataSource.value.add({ conditionField: '', publicConnect: command, conditionName: value }).then(res => {
        if (res.code === 200){
          proxy.$message.success('新增成功')
          dataSource.value.initData(this, proxy.$refs.table)
        }
      })
    })
    .catch(() => {
      // null
    })

  return
}

const addSet = (command) => {

  expression.value.children.push({
    children: [{
      key: '',
      value: '',
      operator: ''
    }, {
      key: '',
      value: '',
      operator: ''
    }],
    connect: command
  })
}

const addSingle = () => {

  expression.value.children.push({
    key: '',
    value: '',
    operator: ''
  })
}

const test = () => {
  expression.value.children.length = 0
  if (editItem.conditionField.trim().length === 0) return
  let str = editItem.conditionField
  str = str.replace(/\n/g, "")
  expression.value = parsingExpressions(str, editItem.publicConnect)

}

const test2 = (expression) => {
  let a = createSql(expression, selectoptions)
  editItem.conditionField = a ? a : ''
}

const selectTableData = reactive({
  isShow: false,
  dataSource: null,
  searchPlaceholde: '',
  doSearch: (val) => {
    console.log(val)
  },
  chooseRow: (val) => {
    console.log(val)
  }
})

const chooseRow = (val) => {
  editItem.tableName = val.orderTable
  dataSource.value.getTableFiled(editItem.tableName).then(res => {
    let temp = res.map(item => {
      return {
        value: item.field,
        label: item.fieldName,
        type: item.fieldName.indexOf('时间') !== -1 ? 'time' : null
      }
    })
    selectoptions.push(...temp.filter((item, index, arr) => {
      return index === arr.findIndex(a => a.value === item.value)
    }))
    expression.value = parsingExpressions(editItem.conditionField, editItem.publicConnect)
    selectTableData.isShow = false
  })
}

const openSelectDialog = async (val) => {
  switch (val){
  default: {
    selectTableData.searchPlaceholde = '输入单据表名称搜索'
    selectTableData.chooseRow = (val) => {
      if (editItem.conditionField.length === 0){
        chooseRow(val)
        return
      }
      proxy.$alert('更改数据表会清空之前创建的条件，是否继续？', '提示', {
        type: 'error',
        icon: 'InfoFilled',
        confirmButtonText: '确定',
        callback: action => {
          if (action === 'confirm') {
            editItem.conditionField = ''
            selectoptions.length = 0
            chooseRow(val)
          }
        }
      })
    }
    selectTableData.doSearch = (val) => {
      selectTableData.dataSource.searchData = { }
      if (val) selectTableData.dataSource.searchData.orderName = val
      selectTableData.dataSource.initData()
    }
    selectTableData.dataSource = new DataSource({
      selectUri: 'colorful-fog/orderTable/select',
      tableHeader: [{
        label: "单据表名称",
        prop: 'orderName',
        width: 200
      }, {
        label: "单据表key",
        prop: 'orderTable',
        width: 200
      }, {
        label: "备注",
        prop: 'comment',
        width: 200
      }],
      pageSize: 10
    })
    selectTableData.dataSource.forMatData = (row, column) => {
      return row[column.property] ?? '-'
    }
    selectTableData.dataSource.searchData = { }
    await selectTableData.dataSource.initData()
    selectTableData.isShow = true
    return

  }
  }
}

const save = () => {
  test2(expression.value)
  dataSource.value.update([editItem]).then(res => {
    if (res && res.code === 200){
      proxy.$message.success('更新成功')
      dataSource.value.initData(this, proxy.$refs.table)
    }
  })

}

const deleteRow = () => {
  proxy.$alert('是否确认删除当前数据？', '提示', {
    type: 'error',
    icon: 'InfoFilled',
    confirmButtonText: '确定',
    callback: action => {
      if (action === 'confirm') {
        dataSource.value.deleteRow([editItem.id]).then(res => {
          if (res.code === 200){
            proxy.$message.success('删除成功')
            dataSource.value.initData(this, proxy.$refs.table)
            closeDetail()
          }
        })
      }
    }
  })
}
const deleteRows = () => {
  let params = dataSource.value.tableData.filter(item => item.checked)
  if (params.length === 0){
    proxy.$message.error('请先选择数据')
    return
  }
  proxy.$alert('是否确认删除当前数据？', '提示', {
    type: 'error',
    icon: 'InfoFilled',
    confirmButtonText: '确定',
    callback: action => {
      if (action === 'confirm') {
        dataSource.value.deleteRow(params.map(item => item.id)).then(res => {
          if (res.code === 200){
            proxy.$message.success('删除成功')
            dataSource.value.initData(this, proxy.$refs.table)
            closeDetail()
          }
        })
      }
    }
  })
}

const closeDetail = () => {
  expression.value = {
    connect: '',
    children: []
  }
  Object.assign(editItem, { id: null })
  console.log(editItem)
}
</script>

<style lang="less" scoped>
.content{
  padding: 10px;
  background: #fff;
  display: flex;
  font-size: 15px;

}

.left{
  background: rgb(255, 255, 255);
  min-width: 300px;
  margin-right: 10px;
  border: 1px solid #f3f3f3;
  box-shadow: 0px 0px 12px rgba(0, 0, 0, 0.12);
  height: calc(100vh - 40px - 30px - 56px);
  .left-title{
    background: #126f9e;
    padding:20px;
    color: #fff;
    border-bottom: 1px solid rgb(222, 225, 230);
  }
  .left-body{
    padding:20px;
    box-sizing: border-box;
  }
}
.right{
  flex:1;
}

.card-body{
  font-size: 14px;
  font-weight: 400;
  display: flex;
  .body-left{
    width: 300px;
  }
  .body-right{
    flex:1;
    border: 1px solid rgb(222, 225, 230);
    padding: 10px;
    box-sizing: border-box;
    display: flex;
    .right-content{
      margin: auto;
      padding: 10px;
      box-sizing: border-box;
      flex: 1;

    }
    .right-options{
      border: 1px solid rgb(222, 225, 230);
      width: 60px;
      padding:0 10px;
      background: #f3f3f3;
      color: #000;
      font-size: 20px;
      box-sizing: border-box;
      display: flex;
      align-items: center;
      flex-direction: column;
      justify-content: center;
    }
  }
}

:deep(.el-divider__text){
  color:rgb(226, 174, 77);
  font-size: 12px;
}
:deep(.el-divider--horizontal){
  margin: 18px 0;
}
:deep(.list){
  display: flex;
  width: 100%;
  border: 1px solid rgb(222, 225, 230);
}
:deep(.list-left){
  width:50px;
  border-right: 1px solid rgb(222, 225, 230);
  display: flex;
  justify-content: center;
  align-items: center;
  color:green;
  font-weight: 800;
  background: #f3f3f3;

}
:deep(.list-right){
  padding: 10px;
  flex:1
}
:deep(.list-right-Internal){
  padding: 10px 10px 0 10px ;
  flex:1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}
:deep(.list-right-handle){
  border-left: 1px solid rgb(222, 225, 230);
  display: flex;
  flex-direction: column;
  padding: 10px 20px;
  justify-content: center;
  font-size:18px;
  background: #f3f3f3;
  color:#000;
}
:deep(.and-pic){
  background: url('./pic/and.png');
  background-size:100% 100%;
  width: 17px;
  height: 17px;
  margin-bottom:8px;
  cursor: pointer;
}
:deep(.or-pic){
  background: url('./pic/or.png');
  background-size:100% 100%;
  width: 17px;
  height: 17px;
  margin-bottom:8px;
  cursor: pointer;
}
:deep(.el-select){
  width:220px;
}
</style>