<template>
  <div style="height:100%;background:#fff">
    <div class="data-filter">
      <data-filter
        ref="searchInfo"
        :filter-items="dataSource.tableHeader"
        @search="doSearch"
      />
    </div>

    <div class="content">
      <div class="left">
        <div style="margin-bottom:14px">
          <el-button
            type="warning" size="small"
            @click="selectAll"
          >
            全选
          </el-button>
        </div>

        <el-tree
          ref="tree"
          :data="groupList"
          show-checkbox
          check-on-click-node
          node-key="id"
          :props="treeProps"
          @check="gruopSearch"
        >
          <template #default="{ node }">
            <span class="custom-tree-node">
              <span>{{ node.label }}</span>
            </span>
          </template>
        </el-tree>
      </div>
      <div v-loading="loading" class="right">
        <div class="extend-handle">
          <div class="extend-handle-left" />
        </div>
        <div class="list">
          <div
            class="list-item"
            style="border-top:1px solid #cacaca;background-color:#126f9e;color:white;font-size:15px;font-weight:bold"
          >
            <div class="label">
              用户名称
            </div>
            <div class="value mr6 ml6">
              绑定的方案
            </div>
          </div>
          <div
            v-for="(item,idx) in dataSource.tableData" :key="idx"
            :class="item.appendClass"
            class="list-item"
          >
            <div class="label" :title="item.userName">
              {{ item.userName }}
            </div>
            <div class="value">
              <el-tag
                v-for="(t,index) in item.schemeMainList.slice(0,item.end)"
                :key="t"
                class="mr6 ml6 "
                :class="item.appendClass ? 'mb10' : ''"
                closable
                @close="deleteScheme(t,index,item)"
              >
                {{ t.schemeName }}
              </el-tag>
            </div>
            <div class="options">
              <el-button
                v-if="item.schemeMainList.length>5"
                type="success"
                link
                @click="showAll(item)"
              >
                {{ item.appendClass ? '收起':'查看全部' }}
                <el-icon class="el-icon--right">
                  <arrow-down v-if="!item.appendClass" />
                  <arrow-up v-else />
                </el-icon>
              </el-button>
              <el-button
                type="warning"
                link
                @click="openSelectDialog('schemeName',item)"
              >
                新增方案
              </el-button>
            </div>
          </div>
        </div>
        <div class="flex-center mt20 mb20">
          <el-pagination
            v-model:current-page="dataSource.currentPage"
            :page-size="dataSource.pageSize"
            small
            background
            layout="prev, pager, next, total"
            :total="dataSource.tableData.length"
            class="mt-4"
          />
        </div>
      </div>
    </div>
    <selectDialogTable
      v-if="selectTableData.isShow"
      ref="dialogSearch"
      :use-choose="false"
      :append-table-style="{ background: '#126f9e', color: '#fff', borderColor: 'rgba(192, 192, 192,.5)' }"
      :width="'60%'"
      :search-input-placeholder="selectTableData.searchPlaceholde"
      :is-show="selectTableData.isShow"
      :data-source="selectTableData.dataSource"
      :title="'选择数据'"
      is-show-search-input
      :loading="loading"
      @close-dialog="selectTableData.isShow=false"
      @do-dialog-search="selectTableData.doSearch"
      @choose-row="selectTableData.chooseRow"
    />
  </div>
</template>

<script setup>
import { pbRequest } from "@/applications/sys/public/pbRequest/index"
import selectDialogTable from '@/components/public/selectDialogTable.vue'
import dataFilter from '@/components/public/dataFilter.vue'
import { DataSource, loading, deleteSchemeUser } from './utils/programme'
import { ref, reactive, getCurrentInstance, onBeforeMount } from 'vue'

import { useRouter } from 'vue-router'

const router = useRouter()

const { proxy } = getCurrentInstance()
const dataSource = ref(null)

const treeProps = reactive({

  label: 'tableName',
  children: 'children',
  disabled: 'disabled',
  isLeaf: 'isLeaf',
  class: 'class'
})

const groupList = reactive([])

const initDataSource = async () => {
  dataSource.value = new DataSource({
    modules: 'project',
    selectUri: '/eam/projectInformation/select',
    tableHeader: [{
      title: "用户名称",
      dataKey: "userName",
      key: 'userName',
      width: 300,
      type: 'text',
      isShow: true,
      isFixed: false
    }]
  })

  dataSource.value.getBelongTable().then(res => {
    groupList.length = 0
    groupList.push(...res.data.map(item => {
      return {
        ...item,
        id: item.table

      }
    }))
  })
}

onBeforeMount(() => {
  initDataSource()
})

let searchNode = ''

const searchInfo = ref(null)
const gruopSearch = (checkedNodes, checkedKeys) => {
  proxy.$refs.tree.setCheckedKeys([], false)
  proxy.$refs.tree.setCheckedNodes ([checkedNodes], false)
  searchNode = Object.assign({}, checkedNodes)
  dataSource.value.tableData.length = 0
  const { userName } = searchInfo.value.returnFilterValue()
  if (userName) {
    getListData(userName)
  }
}

const getListData = (userName) => {
  let params = {
    userName,
    tableName: searchNode.id
  }
  dataSource.value.getUserAndScheme(params).then(res => {
    if (res.code === 200){
      dataSource.value.tableData.length = 0
      dataSource.value.tableData.push(...res.data.map(item => {
        return {
          end: 5,
          ...item
        }
      }))
      dataSource.value.pageSize = dataSource.value.tableData.length
      dataSource.value.total = dataSource.value.tableData.length
    }
  })
}
const doSearch = (val) => {
  if (val.userName) {
    getListData(val.userName)
  }
}
const selectAll = () => {
  let keys = groupList.map(item => item.id)
  proxy.$refs.tree.setCheckedKeys(keys)

}

const showAll = (item) => {
  if (item.appendClass === 'all') {
    item.appendClass = ''
    item.end = 5
  }
  else {
    item.appendClass = 'all'
    item.end = item.schemeMainList.length
  }
}

const openSelectDialog = async (val, append) => {
  selectTableData.useChoose = true
  switch (val){
  case 'schemeName': {
    selectTableData.searchPlaceholde = '输入账单计划名称进行搜索'
    selectTableData.chooseRow = (val) => {
      if (val.children) return
      let params = {
        schemeCode: val.schemeCode,
        archiveUserCode: append.userCode,
        archiveUserName: append.userName,
        tableName: searchNode.id
      }
      dataSource.value.addSchemeToUser(params).then(res => {
        if (res.code === 200){
          const { userName } = searchInfo.value.returnFilterValue()
          getListData(userName)
          proxy.$message.success('添加成功')
          selectTableData.isShow = false
        }
      })

    }
    selectTableData.doSearch = (val) => {
      selectTableData.dataSource.searchData = { }
      if (val) selectTableData.dataSource.searchData.name = val
      selectTableData.dataSource.initData()
    }
    selectTableData.dataSource = new DataSource({
      selectUri: `/colorful-fog/schemeMain/selectMenu`,
      tableHeader: [{
        label: "账单名称",
        prop: "name",
        width: 200
      }, {
        label: "账单code",
        prop: "schemeCode",
        width: 200
      }, {
        label: "类型",
        prop: "billType",
        width: 200
      }],
      pageSize: 10
    })

    selectTableData.dataSource.forMatData = (row, column) => {
      if (!row[column.property]) return '-'
      if (column.property === 'billType') return row[column.property] === 'PAY' ? '应付' : '应收'
      return row[column.property] ?? '-'
    }
    selectTableData.dataSource.initData = async (context = selectTableData.dataSource) => {
      let { data: { data, code, message } } = await pbRequest.post(`${context.selectUri}?currentPage=${context.currentPage}&pageSize=${context.pageSize}`, context.searchData)
      if (code !== 200) return
      selectTableData.dataSource.tableData.length = 0
      let dataList = Object.entries(data).reduce((a, b, idx) => {
        let temp = { name: b[0], children: b[1], id: idx + 1, schemeCode: '-' }
        a.push(temp)
        return a
      }, [])

      selectTableData.dataSource.tableData.push(...dataList)

      selectTableData.dataSource.total = selectTableData.dataSource.tableData.length
    }
    await selectTableData.dataSource.initData()
    selectTableData.isShow = true
    return
  }

  default: {
    return
  }
  }
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

const deleteScheme = (t, index, append) => {

  proxy.$alert('是否确认移除该方案？', '提示', {
    type: 'info',
    icon: 'InfoFilled',
    confirmButtonText: '确定',
    callback: (action) => {
      if (action === 'confirm') {
        deleteSchemeUser([t.id]).then(res => {
          if (res.code === 200){
            proxy.$message.success('删除成功')
            append.schemeMainList.splice(index, 1)
            if (append.schemeMainList.length <= 5) {
              append.appendClass = ''
              append.end = 5
            }
          }
        })
      }
    }
  })

}
</script>

<style lang="less" scoped>
.table{
  padding: 10px;
  background-color: #ffffff;
}

.content {
  display: flex;
  overflow-x: hidden;
  background-color: #fff;
  .left {
    padding: 0px 0px 14px 10px;
    width: 10%;
    border-right: 1px dashed #ebeef5;
    height: 76vh;
    margin-top: 14px;
  }

  .right {
    flex: 1;
  }
}

.list{

  padding: 0 10px;

  .list-item{
    padding: 10px;
    display: flex;
    border: 1px solid ;
    border-color:transparent #cacaca #cacaca  #cacaca;
    font-size: 13px;
    height: 20px;
    letter-spacing: 1px;
    transition: .5s;
  }
  .label{
    border-right: 1px dashed #cacaca;
    width: 220px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    text-align: center;
  }
  .value {
    margin-left: 20px;
    flex:1;
  }
}

.all{
  height: 200px !important;
  overflow-y: hidden;

}

:deep(.el-tree-node__content){
  padding: 6px 8px;
}
:deep(.el-tree-node__expand-icon){
  margin-right: 8px;
}
</style>