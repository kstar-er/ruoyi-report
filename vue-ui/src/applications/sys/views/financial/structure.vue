<template>
  <!-- 单据结构  -->
  <div>
    <div class="data-filter">
      <data-filter :filter-items="dataSource.tableHeader" @search="doSearch" />
    </div>
    <div class="extend-handle">
      <div class="extend-handle-left">
        <el-button
          v-authorityHandle="'structure:add'"
          class="handle-btn"
          color="#4a78bd"
          style="color: #666"
          plain
          @click="addRow"
        >
          新增
        </el-button>

        <el-upload
          v-authorityHandle="'structure:importExcel'"
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
            导入Excel
          </el-button>
        </el-upload>
        <el-button
          v-authorityHandle="'structure:delete'"
          class="handle-btn-delete ml12"
          color="#f56c6c"
          plain
          style="color: #666"
          @click="deleteRow"
        >
          删除
        </el-button>

        <el-tooltip
          class="box-item"
          effect="dark"
          content="下载导入模板"
          placement="top"
        >
          <el-button
            color="#5f6368"
            style="color: #ffffff"
            size="small"
            icon="Download"
            circle
            @click="downloadTemplate"
          />
        </el-tooltip>
      </div>
    </div>
    <v-table
      ref="table"
      need-end-handle
      need-check-box
      need-customize-cell-renderer
      row-key="roleId"
      :end-handle-width="400"
      :total="dataSource.total"
      :current-page="dataSource.currentPage"
      :page-size="dataSource.pageSize"
      :table-data="dataSource.tableData"
      :columns="dataSource.tableHeader"
      :loading="
        loading &&
          !editControlCommon.isShow &&
          !editControlCommonForeign.isShow &&
          !selectTableData.isShow &&
          !isShowList
      "
      :default-end-handle="false"
      :customize-end-handle="customizeEndHandle"
      :for-mat-data="dataSource.forMatData"
      :customize-cell-renderer="customizeCellRenderer"
      @selectionChange="
        dataSource.selectionChange($event, dataSource, proxy.$refs.table)
      "
      @refresh="dataSource.initData(dataSource, proxy.$refs.table)"
      @editRow="goCompile"
      @current-change="
        dataSource.currentPageChange($event, dataSource, proxy.$refs.table)
      "
      @sizeChange="
        dataSource.pageSizeChange($event, dataSource, proxy.$refs.table)
      "
      @editTableHeader="isShowEditTableHeader = true"
    />
    <el-drawer v-model="isShowEditTableHeader" :show-close="false">
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
            :is-show-edit-table-header="isShowEditTableHeader"
            :modules="'role'"
          />
        </div>
      </template>
    </el-drawer>

    <el-dialog
      v-model="isShowList"
      lock-scroll
      title="外键列表"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :show-close="false"
      top="4%"
      destroy-on-close
    >
      <template #header="{ titleId, titleClass }">
        <div class="jx-dialog-header">
          <div :id="titleId" :class="titleClass">
            <h4 class="inline-block">
              外键列表
            </h4>
            <el-button
              class="dialog-close-btn ml10"
              type="success"
              @click="addForeign"
            >
              新增外键
            </el-button>
          </div>
          <div>
            <el-button
              class="dialog-close-btn"
              type="danger"
              icon="CloseBold"
              circle
              @click="isShowList = false"
            />
          </div>
        </div>
      </template>
      <div>
        <el-table
          stripe
          :data="foreignDetail.foreignKeyList"
          border
          style="width: 100%"
        >
          <el-table-column prop="foreignTableName" label="关联表" />
          <el-table-column prop="foreignKey" label="外键" />
          <el-table-column prop="tableName" label="当前表" />
          <el-table-column
            fixed="right" label="操作"
            width="120"
          >
            <template #default="{ row }">
              <el-button
                link
                type="danger"
                size="small"
                @click="deleteForeignKey(row)"
              >
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-dialog>

    <selectDialogForm
      v-if="editControlCommon.isShow"
      ref="dialogForm"
      :width="'60%'"
      :loading="loading"

      :my-client="myClient"
      :form-data="formData"
      :is-show="editControlCommon.isShow"
      :title="'请填写相关信息'"
      :form-input-el="editControlCommon.formInputEl"
      :form-select-el="editControlCommon.formSelectEl"
      :form-text-area-el="editControlCommon.formTextAreaEl"
      :form-upload-el="editControlCommon.formUploadEl"
      :form-time-and-number="editControlCommon.formTimeAndNumber"
      @close-dialog="editControlCommon.isShow = false"
      @emitOpenDialog="editControlCommon.emitOpenDialog"
      @inputDone="editControlCommon.inputDone"
    />

    <selectDialogForm
      v-if="editControlCommonForeign.isShow"
      ref="dialogForm"
      :width="'50%'"
      :loading="loading"
      :my-client="myClient"
      :form-data="formData"
      :is-show="editControlCommonForeign.isShow"
      :title="'请填写相关信息'"
      :form-input-el="editControlCommonForeign.formInputEl"
      :form-select-el="editControlCommonForeign.formSelectEl"
      :form-text-area-el="editControlCommonForeign.formTextAreaEl"
      :form-upload-el="editControlCommonForeign.formUploadEl"
      :form-time-and-number="editControlCommonForeign.formTimeAndNumber"
      @close-dialog="editControlCommonForeign.isShow = false"
      @emitOpenDialog="openSelectDialog"
      @inputDone="editControlCommonForeign.inputDone"
    />

    <selectDialogTable
      v-if="selectTableData.isShow"
      ref="dialogSearch"
      :is-show-search-input="false"
      :need-selection="selectTableData.useChoose"
      :append-table-style="{
        background: '#126f9e',
        color: '#fff',
        borderColor: 'rgba(192, 192, 192,.5)'
      }"
      :width="'60%'"
      :search-input-placeholder="selectTableData.searchPlaceholde"
      :is-show="selectTableData.isShow"
      :data-source="selectTableData.dataSource"
      :title="'选择数据'"
      :loading="loading"
      @close-dialog="selectTableData.isShow = false"
      @doDialogSearch="selectTableData.doSearch"
      @chooseRow="selectTableData.chooseRow"
      @submit="selectTableData.submit"
    />
  </div>
</template>

<script setup>
import { pbRequest } from "@/applications/sys/public/pbRequest/index"
import selectDialogForm from "@/components/public/selectDialogForm.vue"
import selectDialogTable from "@/components/public/selectDialogTable.vue"
import dynamicHeader from "@/components/public/dynamicHeader.vue"
import VTable from "@/components/public/virtualTable.vue"
import dataFilter from "@/components/public/dataFilter.vue"
import { DataSource, loading } from "./utils/structure"
import {
  ref,
  reactive,
  getCurrentInstance,
  onBeforeMount,
  h,
  resolveDirective,
  withDirectives
} from "vue"
import { useRouter, useRoute } from "vue-router"
import { ElButton } from "element-plus"
import client from "@/utils/upload/upLoadClient"
const router = useRouter()
const route = useRoute()
const authority = resolveDirective("authority")
const myClient = ref(client)
const { proxy } = getCurrentInstance()
const isShowEditTableHeader = ref(false)
const dataSource = ref(null)
const isShowList = ref(false)
const initDataSource = () => {
  dataSource.value = new DataSource({
    modules: "structure",
    selectUri: "/colorful-fog/orderTable/select"
  })
  dataSource.value.initTableHeader()
  dataSource.value.initData(this, proxy.$refs.table)
}

onBeforeMount(() => {
  initDataSource()
})

const addRow = async () => {
  formData.value = {}
  editControlCommon.formInputEl[0].disabled = false
  editControlCommon.isShow = true
}

const goCompile = async rowData => {
  formData.value = rowData
  editControlCommon.formInputEl[0].disabled = true
  editControlCommon.formInputEl[1].disabled = true
  editControlCommon.isShow = true
}

const doSearch = data => {
  dataSource.value.search(data, this, proxy.$refs.table)
}

const customizeEndHandle = rowData => {
  if (rowData.admin) {
    return [h("div", { class: "table-handel-div no-options" }, "暂无操作")]
  }
  return [h("div", { class: "table-handel-div" }, [
    withDirectives(
      h(
        ElButton,
        {
          class: "hover-animation",
          onClick: () => {
            goCompile(rowData)
          },
          title: "编辑",
          style:
              "padding:0;margin:0;background:transparent;width:80px;color:#0c81b1;margin-right:10px"
        },
        { default: () => "编辑" }
      ),
      [[authority, "structure:update"]]
    ),
    withDirectives(
      h(
        ElButton,
        {
          class: "hover-animation",
          onClick: () => {
            deleteRow(rowData)
          },
          title: "删除",
          style:
              "padding:0;margin:0;background:transparent;width:80px;color:red;margin-right:10px"
        },
        { default: () => "删除" }
      ),
      [[authority, "structure:update"]]
    ),
    withDirectives(
      h(
        ElButton,
        {
          class: "hover-animation",
          onClick: () => {
            updateDetail(rowData)
          },
          title: "更新明细",
          style:
              "padding:0;margin:0;background:transparent;width:80px;color:#0c81b1;margin-right:10px"
        },
        { default: () => "更新明细" }
      ),
      [[authority, "structure:update"]]
    ),
    withDirectives(
      h(
        ElButton,
        {
          class: "hover-animation",
          onClick: () => {
            checkDetail(rowData)
          },
          title: "查看明细",
          style:
              "padding:0;margin:0;background:transparent;width:80px;color:#0c81b1;margin-right:10px"
        },
        { default: () => "查看明细" }
      ),
      [[authority, "structure:check"]]
    ),
    withDirectives(
      h(
        ElButton,
        {
          class: "hover-animation",
          onClick: () => {
            checkForeignDetail(rowData)
          },
          title: "外键列表",
          style:
              "padding:0;margin:0;background:transparent;width:80px;color:#0c81b1"
        },
        { default: () => "外键列表" }
      ),
      [[authority, "structure:check"]]
    )
  ])]
}

let row = null
let foreignDetail = ref(null)

const checkForeignDetail = rowData => {
  foreignDetail.value = rowData
  if (!foreignDetail.value.foreignKeyList)
    foreignDetail.value.foreignKeyList = []
  isShowList.value = true
}

const deleteForeignKey = row => {
  proxy.$alert("是否确认删除外键？", "提示", {
    type: "info",
    icon: "InfoFilled",
    confirmButtonText: "确定",
    callback: action => {
      if (action === "confirm") {
        dataSource.value.deleteForeignKeys([row.id]).then(res => {
          if (res.code === 200) {
            editControlCommon.isShow = false
            proxy.$message.success("删除成功")
            dataSource.value.initData(this, proxy.$refs.table)
            dataSource.value.getStructure(foreignDetail.value.id).then(res => {
              foreignDetail.value = res.data.pageInfo.list[0]
              editControlCommonForeign.isShow = false
              selectTableData.isShow = false
            })
          }
        })
      }
    }
  })
}

const updateDetail = rowData => {
  if (!rowData.id && dataSource.value.selections.length === 0) {
    proxy.$message.error("请选择数据")
    return
  }
  row = rowData
  openSelectDialog("dataSource", "update")
}

const checkDetail = rowData => {
  router.push(
    `/detail/strucitureDetail?id=${rowData.id}&orderTable=${rowData.orderTable}&title=${route.meta.title}`
  )
}

const formData = ref({})

const editControlCommon = reactive({
  isShow: false,
  formInputEl: [{
    title: "选择数据库表",
    key: "orderTable",
    element: "input",
    type: "dialog",
    rules: [{
      required: true,
      message: "该项不能为空",
      trigger: "blur"
    }]
  },
  {
    title: "表名称",
    key: "orderTableName",
    element: "input",
    rules: [{
      required: true,
      message: "该项不能为空",
      trigger: "blur"
    }]
  }],
  formSelectEl: [{
    title: "类型",
    key: "type",
    element: "radio",
    options: [{
      label: "主表",
      value: "MAIN"
    },
    {
      label: "明细",
      value: "DETAIL"
    }],
    rules: [{
      required: true,
      message: "该项不能为空",
      trigger: "blur"
    }]
  }],
  formTextAreaEl: [{
    title: "备注",
    key: "comment",
    element: "input",
    type: "textarea"
  }],
  formTimeAndNumber: [],
  emitOpenDialog: val => {
    openSelectDialog(val)
  },
  inputDone: val => {
    val.dataSourceId = dataSourceId
    val.dataSourceName = dataSourceName

    if (val.id) {
      dataSource.value.updateStructure([val]).then(res => {
        if (res.code === 200) {
          editControlCommon.isShow = false
          proxy.$message.success("更新成功")
          dataSource.value.initData(this, proxy.$refs.table)
        }
      })
    } else {
      dataSource.value.addStructure(val).then(res => {
        if (res.code === 200) {
          editControlCommon.isShow = false
          proxy.$message.success("新增成功")
          dataSource.value.initData(this, proxy.$refs.table)
        }
      })
    }
  }
})

const editControlCommonForeign = reactive({
  isShow: false,
  formInputEl: [{
    title: "当前表",
    key: "tableName",
    element: "input",
    rules: [{
      required: true,
      message: "该项不能为空",
      trigger: "blur"
    }],
    disabled: true
  },
  {
    title: "当前表外键",
    key: "foreignKey",
    type: "dialog",
    element: "input",
    rules: [{
      required: true,
      message: "该项不能为空",
      trigger: "blur"
    }]
  },
  {
    title: "关联表",
    key: "foreignTableName",
    element: "input",
    type: "dialog",
    rules: [{
      required: true,
      message: "该项不能为空",
      trigger: "blur"
    }]
  },
  {
    title: "关联表外键",
    key: "relTableForeignKey",
    element: "input",
    type: "dialog",
    rules: [{
      required: true,
      message: "该项不能为空",
      trigger: "blur"
    }]
  }],
  formSelectEl: [],
  formTextAreaEl: [],
  formTimeAndNumber: [],
  emitOpenDialog: val => {
    console.log(val)
  },
  inputDone: val => {
    proxy.$alert("是否确认提交？", "提示", {
      type: "info",
      icon: "InfoFilled",
      confirmButtonText: "确定",
      callback: action => {
        if (action === "confirm") {
          if (val.id) {
            dataSource.value.updateStructure([val]).then(res => {
              if (res.code === 200) {
                editControlCommon.isShow = false
                proxy.$message.success("更新成功")
                dataSource.value.initData(this, proxy.$refs.table)
              }
            })
          } else {
            dataSource.value.addStructure(val).then(res => {
              if (res.code === 200) {
                editControlCommon.isShow = false
                proxy.$message.success("新增成功")
                dataSource.value.initData(this, proxy.$refs.table)
              }
            })
          }
        }
      }
    })
  }
})

const addForeign = () => {
  formData.value = {
    tableId: foreignDetail.value.id,
    tableName: foreignDetail.value.orderTable
  }
  editControlCommonForeign.isShow = true
}

const customizeCellRenderer = ({ forMatValue, rowIndex, column, rowData }) => {
  return forMatValue
}

const deleteRow = async rowData => {
  if (!rowData.id && dataSource.value.selections.length === 0) {
    proxy.$message.error("请选择数据")
    return
  }
  proxy.$alert("是否确认删除？", "提示", {
    type: "error",
    icon: "InfoFilled",
    confirmButtonText: "确定",
    callback: action => {
      if (action === "confirm") {
        let params = rowData.id
          ? [rowData.id]
          : dataSource.value.selections.map(item => item.id)
        dataSource.value.deleteStructure(params).then(res => {
          if (res.code === 200) {
            proxy.$alert("删除成功", "提示", {
              type: "success",
              icon: "InfoFilled",
              showCancelButton: false,
              callback: () => {
                dataSource.value.initData(this, proxy.$refs.table)
              }
            })
          }
        })
      }
    }
  })
}

const handleXlsxData = file => {
  return file.map(item => {
    return {
      orderTable: item["数据库表"],
      orderTableName: item["表名称"],
      dataSourceId: item["数据库ID"],
      dataSourceName: item["数据库"],
      type: item["类型"] === "主表" ? "MAIN" : "DETAIL",
      comment: item["备注"]
    }
  })
}

const fileChoice = async file => {
  let dataTool = new proxy.$DataTool()
  let xlsxData = await dataTool.xlsx2DataObject(file.raw)
  let data = handleXlsxData(xlsxData)
  proxy.$alert("是否确认导入？", "提示", {
    type: "info",
    icon: "InfoFilled",
    confirmButtonText: "确定",
    callback: action => {
      if (action === "confirm") {
        dataSource.value.addStructureBatch(data).then(res => {
          if (res.code === 200) {
            proxy.$alert("导入成功", "提示", {
              type: "success",
              icon: "InfoFilled",
              showCancelButton: false,
              callback: () => {
                dataSource.value.initData(this, proxy.$refs.table)
              }
            })
          }
        })
      }
    }
  })
}

let dataSourceId = -1
let dataSourceName = ""

const openSelectDialog = async (name, type) => {
  switch (name) {
  case "dataSource": {
    selectTableData.useChoose = false
    selectTableData.chooseRow = val => {
      if (type === "update") {
        proxy.$alert("是否确认更新明细？", "提示", {
          type: "info",
          icon: "InfoFilled",
          confirmButtonText: "确定",
          callback: action => {
            if (action === "confirm") {
              let params = {
                tableNameList: row.orderTable
                  ? [row.orderTable]
                  : dataSource.value.selections.map(item => item.orderTable),
                dataSourceId: val.id
              }
              dataSource.value.updateTableFiled(params).then(res => {
                if (res.code === 200) {
                  proxy.$alert("更新成功", "提示", {
                    type: "success",
                    icon: "InfoFilled",
                    showCancelButton: false,
                    callback: () => {
                      dataSource.value.initData(this, proxy.$refs.table)
                    }
                  })
                }
              })
            }
          }
        })
        selectTableData.isShow = false
        return
      }

      selectTableData.isShow = false
    }
    selectTableData.doSearch = val => {
      selectTableData.dataSource.initData()
    }
    selectTableData.dataSource = new DataSource({
      selectUri: `/colorful-fog/dataSource/list`,
      tableHeader: [{
        label: "数据库名称",
        prop: "sourceName",
        width: 200
      },
      {
        label: "数据库Code",
        prop: "sourceCode",
        width: 200
      }],
      pageSize: 10
    })

    selectTableData.dataSource.forMatData = (row, column) => {
      return row[column.property] ?? "-"
    }
    selectTableData.dataSource.initData = async (
      context = selectTableData.dataSource
    ) => {
      let {
        data: { data, code, message }
      } = await pbRequest.post(
        `${context.selectUri}?currentPage=${context.currentPage}&pageSize=${context.pageSize}`,
        context.searchData
      )
      if (code !== 200) return
      context.total = data.length
      context.tableData = data
    }
    await selectTableData.dataSource.initData()
    selectTableData.isShow = true
    return
  }

  case "foreignTableName": {
    selectTableData.useChoose = false
    selectTableData.chooseRow = val => {
      formData.value.foreignTableId = val.id
      formData.value.foreignTableName = val.orderTable
      proxy.$refs.dialogForm.updateDialogInput({
        foreignTableName: val.orderTable,
        foreignTableId: val.id
      })
      selectTableData.isShow = false
      openSelectDialog("relTableForeignKey")
    }
    selectTableData.doSearch = val => {
      selectTableData.dataSource.initData()
    }
    selectTableData.dataSource = new DataSource({
      selectUri: `/colorful-fog/orderTable/select`,
      tableHeader: [{
        label: "表名称  ",
        prop: "orderTable",
        width: 200
      },
      {
        label: "备注",
        prop: "orderTableName",
        width: 200
      },
      {
        label: "类型",
        prop: "type",
        width: 200
      }],
      pageSize: 10
    })
    selectTableData.dataSource.forMatData = (row, column) => {
      return row[column.property] ?? "-"
    }
    await selectTableData.dataSource.initData()
    selectTableData.isShow = true
    return
  }
  case "relTableForeignKey":
  case "foreignKey": {
    selectTableData.useChoose = false
    selectTableData.chooseRow = val => {
      if (name === "foreignKey") {
        formData.value.foreignKey = val.field
        proxy.$refs.dialogForm.updateDialogInput({ foreignKey: val.field })
        selectTableData.isShow = false
        return
      } else {
        let params = {
          foreignKey: formData.value.foreignKey,
          tableName: formData.value.tableName,
          tableId: formData.value.tableId,
          foreignTableName: formData.value.foreignTableName,
          foreignTableId: formData.value.foreignTableId,
          relTableForeignKey: val.field
        }
        proxy.$alert("是否确认提交？", "提示", {
          type: "info",
          icon: "InfoFilled",
          confirmButtonText: "确定",
          callback: action => {
            if (action === "confirm") {
              dataSource.value.addForeignKeys([params]).then(res => {
                if (res.code === 200) {
                  editControlCommon.isShow = false
                  proxy.$message.success("添加成功")
                  dataSource.value.initData(this, proxy.$refs.table)
                  dataSource.value
                    .getStructure(formData.value.tableId)
                    .then(res => {
                      foreignDetail.value = res.data.pageInfo.list[0]
                      editControlCommonForeign.isShow = false
                      selectTableData.isShow = false
                    })
                }
              })
            }
          }
        })
      }
    }
    selectTableData.doSearch = val => {
      selectTableData.dataSource.initData()
    }
    selectTableData.dataSource = new DataSource({
      selectUri: `/colorful-fog/tableFiled/select`,
      tableHeader: [{
        label: "字段名",
        prop: "fieldName",
        width: 200
      },
      {
        label: "字段key",
        prop: "field",
        width: 200
      }],
      pageSize: 10
    })
    if (name === "relTableForeignKey") {
      selectTableData.dataSource.searchData = {
        tableName: formData.value.foreignTableName
      }
    } else {
      selectTableData.dataSource.searchData = {
        tableName: formData.value.tableName
      }
    }
    selectTableData.dataSource.forMatData = (row, column) => {
      return row[column.property] ?? "-"
    }
    await selectTableData.dataSource.initData()
    selectTableData.isShow = true
    return
  }

  case "orderTable": {
    selectTableData.useChoose = false
    selectTableData.chooseRow = val => {
      selectTableData.isShow = false
      dataSourceId = val.id
      dataSourceName = val.sourceName
      openSelectDialog("selectTable")
    }
    selectTableData.doSearch = val => {
      selectTableData.dataSource.initData()
    }
    selectTableData.dataSource = new DataSource({
      selectUri: `/colorful-fog/dataSource/list`,
      tableHeader: [{
        label: "数据库ID",
        prop: "id",
        width: 200
      },
      {
        label: "数据库名称",
        prop: "sourceName",
        width: 200
      },
      {
        label: "数据库类型",
        prop: "sourceType",
        width: 200
      }],
      pageSize: 10
    })

    selectTableData.dataSource.forMatData = (row, column) => {
      return row[column.property] ?? "-"
    }

    selectTableData.dataSource.initData = async (
      context = selectTableData.dataSource
    ) => {
      let {
        data: { data, code, message }
      } = await pbRequest.post(
        `${context.selectUri}?currentPage=${context.currentPage}&pageSize=${context.pageSize}`,
        context.searchData
      )
      if (code !== 200) return
      context.total = data.length
      context.tableData = data
    }

    await selectTableData.dataSource.initData()
    selectTableData.isShow = true
    return
  }

  case "selectTable": {
    dataSource.value.getTable(dataSourceId).then(res => {
      if (res.code === 200) {
        selectTableData.dataSource = new DataSource({
          tableHeader: [{
            label: "数据库表",
            prop: "orderTable",
            width: 200
          },
          {
            label: "表名",
            prop: "orderTableName",
            width: 200
          },
          {
            label: "备注",
            prop: "comment",
            width: 200
          }],
          pageSize: 100000
        })
        selectTableData.chooseRow = val => {
          proxy.$refs.dialogForm.updateDialogInput({
            comment: val.comment,
            orderTable: val.orderTable,
            orderTableName: val.orderTableName
          })
          selectTableData.isShow = false
        }
        selectTableData.dataSource.forMatData = (row, column) => {
          return row[column.property] ?? "-"
        }
        selectTableData.dataSource.tableData = res.data.filter(
          item => item.orderTable
        )
        selectTableData.dataSource.total = res.data.length
        selectTableData.isShow = true
      }
    })
    return
  }
  default:
    return
  }
}

const downloadTemplate = () => {
  window.location =
    "https://jxwlapp.oss-cn-guangzhou.aliyuncs.com/excelTemplate/导入数据库表模板.xlsx?download"
}

const selectTableData = reactive({
  isShow: false,
  dataSource: null,
  searchPlaceholde: "",
  doSearch: val => {
    console.log(val)
  },
  chooseRow: val => {
    console.log(val)
  }
})
</script>

<style lang="less" scoped>
.table {
  padding: 10px;
  background-color: #ffffff;
}
</style>
