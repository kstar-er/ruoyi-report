<template>
  <!-- 单据结构  -->
  <div>
    <div class="data-filter">
      <data-filter
        :filter-items="dataSource.tableHeader"
        @search="doSearch"
      />
    </div>
    <div class="extend-handle">
      <div class="extend-handle-left">
        <el-button
          class="handle-btn "
          color="#4a78bd"
          style="color: #666"
          plain
          @click="exportData"
        >
          导出错误数据
        </el-button>
        <el-upload
          class="inline-block ml10"
          accept=".xlsx, .xls"
          :on-change="fileChoice"
          :show-file-list="false"
          :auto-upload="false"
        >
          <el-button
            class="handle-btn"
            color="#4a78bd"
            style="color: #666"
            plain
          >
            导入Excel更新
          </el-button>
        </el-upload>
        <el-button
          class="handle-btn ml10"
          color="#4a78bd"
          style="color: #666"
          plain
          @click="handleDeal"
        >
          已处理
        </el-button>
      </div>
    </div>
    <v-table
      ref="table"
      need-end-handle
      need-check-box
      need-customize-cell-renderer
      row-key="roleId"
      :end-handle-width="160"
      :total="dataSource.total"
      :current-page="dataSource.currentPage"
      :page-size="dataSource.pageSize"
      :table-data="dataSource.tableData"
      :columns="dataSource.tableHeader"
      :loading="loading"
      :default-end-handle="false"
      :customize-end-handle="customizeEndHandle"
      :for-mat-data="dataSource.forMatData"
      :customize-cell-renderer="customizeCellRenderer"
      @selection-change="dataSource.selectionChange($event,dataSource,proxy.$refs.table)"
      @refresh="dataSource.initData(dataSource, proxy.$refs.table)"
      @current-change="dataSource.currentPageChange($event,dataSource,proxy.$refs.table)"
      @size-change="dataSource.pageSizeChange($event,dataSource,proxy.$refs.table)"
      @edit-table-header="isShowEditTableHeader = true"
    />
    <el-drawer
      v-model="isShowEditTableHeader"
      :show-close="false"
    >
      <template #header="{ titleId, titleClass }">
        <h4 :id="titleId" :class="titleClass">
          通过拖拽设置您喜欢的表头顺序
        </h4>
        <el-button type="danger" @click="isShowEditTableHeader = false">
          <el-icon class="el-icon--left">
            <CircleCloseFilled />
          </el-icon>
          关闭
        </el-button>
      </template>
      <template #default>
        <div>
          <dynamicHeader
            :is-show-edit-table-header="isShowEditTableHeader" :modules="'classificationError'"
          />
        </div>
      </template>
    </el-drawer>
  </div>
</template>

<script setup>
import dynamicHeader from '@/components/public/dynamicHeader.vue'
import VTable from '@/components/public/virtualTable.vue'
import dataFilter from '@/components/public/dataFilter.vue'
import { DataSource, loading } from './utils/classificationError'
import { ref, reactive, getCurrentInstance, onBeforeMount, h, resolveDirective, withDirectives } from 'vue'

import { ElButton } from 'element-plus'

const { proxy } = getCurrentInstance()
const isShowEditTableHeader = ref(false)
const dataSource = ref(null)

const initDataSource = () => {
  dataSource.value = new DataSource({
    modules: 'classificationError',
    selectUri: '/colorful-fog/errReason/list'
  })
  dataSource.value.searchData.dealFlag = 0
  dataSource.value.initTableHeader()
  dataSource.value.initData(this, proxy.$refs.table)
}

onBeforeMount(() => {
  initDataSource()
})

const doSearch = (data) => {
  dataSource.value.search(data, this, proxy.$refs.table)
}

const customizeEndHandle = (rowData) => {
  return [h('div', { class: 'table-handel-div no-options' }, '暂无操作')]
}

const customizeCellRenderer = ({ forMatValue, rowIndex, column, rowData }) => {
  return forMatValue
}

const getHeader1 = (val) => {
  return {
    title: '分类错误-数据缺失',
    header: [
      '记录id', '账单编码', '方案类型', '方案分类', '方案名称', '依赖名称', '方案编码', '依赖Code', '分类数据', '分类值', '分类失败理由', '是否已处理'
    ],
    style: {
      A1: {
        font: { bold: true },
        alignment: { horizontal: "center" },
        fill: { fgColor: { rgb: "FFFF00" } }
      },
      D1: {
        font: { bold: true },
        alignment: { horizontal: "center" },
        fill: { fgColor: { rgb: "FFFF00" } }
      },
      C1: {
        font: { bold: true },
        alignment: { horizontal: "center" },
        fill: { fgColor: { rgb: "FFFF00" } }
      },
      B1: {
        font: { bold: true },
        alignment: { horizontal: "center" },
        fill: { fgColor: { rgb: "FFFF00" } }
      },
      E1: {
        font: { bold: true },
        alignment: { horizontal: "center" },
        fill: { fgColor: { rgb: "FFFF00" } }
      },
      F1: {
        font: { bold: true },
        alignment: { horizontal: "center" },
        fill: { fgColor: { rgb: "FFFF00" } }
      }
    }
  }
}

const handleDeal = async () => {
  if (dataSource.value.selections.length) {
    dataSource.value.selections.forEach(item => {
      item.dealFlag = 1
    })
    dataSource.value.updateErrorData(dataSource.value.selections).then(res => {
      if (res.code === 200){
        proxy.$alert('处理成功', '提示', {
          type: 'success',
          icon: 'InfoFilled',
          showCancelButton: false,
          callback: async () => {
            dataSource.value.tableData.length = 0
            dataSource.value.initData(this, proxy.$refs.table)
          }
        })
      }
    })
  }
  else {
    proxy.$message.error("请先选择方案")
  }
}

const exportData = () => {
  if (dataSource.value.selections.length === 0) return
  let params = dataSource.value.selections.map(item => item.id)
  let sheetList = []
  let { title, header, style } = getHeader1()
  let data = []
  dataSource.value.exportAllData(params).then(res => {
    res.data.errReasonList.forEach(item => {
      let temp = [
        item.id, item.billCode, item.billType, item.sort, item.schemeName, item.dependName, item.schemeCode, item.dependCode, item.key, item.value, item.reason, item.dealFlag ? '是' : '否'
      ]
      data.push(temp)
    })
    sheetList.push({ title, header, style, data })
    sheetList.push(...handleBillData(res.data.exportExcelVOList))
    new proxy.$DataTool().complexExcel(sheetList, '错误及账单数据')
  })

}

const isTimestamp = (timestamp) => {

  let regex = /^\d{13}$/
  if (regex.test(timestamp)) {
    let date = new Date(+timestamp)
    return !isNaN(date.getTime())
  }
  return false
}

const handleBillData = (list) => {
  return list.reduce((a, b) => {
    let title = b.schemeName
    let header = [`所属经销商Code`, `所属经销商名称`, `账单编码`, `账期`]
    let headerCode = [`belongArchiveCode`, `belongArchiveName`, `billCode`, `costTerm`]
    let data = []
    let style = {}
    b.billResultVO.resultNameList.map(data => {
      header.push(data.resultName)
      headerCode.push(data.resultCode)
    })
    b.billResultVO.resultDataList.list.map(item => {
      item = { ...item, ...item.data }
      let temp = []
      headerCode.forEach(key => {
        if (isTimestamp(item[key])){
          temp.push(new proxy.$DataTool().timeStamp2Time(+item[key], 'yyyy-MM-dd hh:mm:ss'))
        } else temp.push(item[key])
      })
      data.push(temp)
    })
    a.push({ title, header, style, data })
    return a
  }, [])
}

const fileChoice = async (file) => {
  let dataTool = new proxy.$DataTool()
  let xlsxData = await dataTool.xlsx2DataObject(file.raw)
  let data = handleXlsxData(xlsxData)
  proxy.$alert('是否确认导入更新分类？', '提示', {
    type: 'info',
    icon: 'InfoFilled',
    confirmButtonText: '确定',
    callback: (action) => {
      if (action === 'confirm') {
        dataSource.value.updateErrorData(data).then(res => {
          if (res.code === 200){
            proxy.$alert('更新成功', '提示', {
              type: 'success',
              icon: 'InfoFilled',
              showCancelButton: false,
              callback: async () => {
                dataSource.value.tableData.length = 0
                dataSource.value.initData(this, proxy.$refs.table)
              }
            })
          }
        })
      }
    }
  })
}

const handleXlsxData = (file) => {
  let updateList = []
  file.forEach(item => {
    updateList.push({
      "id": item['记录id'],
      "dependCode": item['依赖Code'],
      "key": item['分类数据'],
      "value": item['分类值'],
      "reason": item['分类失败理由']
    })
  })
  return updateList
}
</script>

<style lang="less" scoped>
.table{
  padding: 10px;
  background-color: #ffffff;
}
</style>
