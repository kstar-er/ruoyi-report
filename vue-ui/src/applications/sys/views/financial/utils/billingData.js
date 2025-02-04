import { pbRequest, loading } from "@/applications/sys/public/pbRequest/index"
import PublicDataSource from '@/applications/sys/utils/js/table-handle-class/publicDataSource'
import { DataTool } from '@/js/tool-class/dataTool'
class DataSource extends PublicDataSource {
  constructor({
    modules = '',
    selectUri = '/',
    deleteUri = '/',
    selectOneUri = '/',
    pageSize = 20,
    tableHeader
  } = {}) {
    super({
      modules,
      selectUri,
      deleteUri,
      selectOneUri,
      pageSize,
      tableHeader
    })
  }

  async initTableHeader(context = this) {
    let { data: { data, code, message } } = await pbRequest.post(`${context.selectUri}?currentPage=${context.currentPage}&pageSize=1`, context.searchData)
    if (code !== 200 || !data) return

    context.tableHeader = [
      {
        title: "批次号",
        dataKey: "batchCode",
        key: 'batchCode',
        width: 300,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "状态",
        dataKey: "status",
        key: 'status',
        width: 300,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "所属经销商",
        dataKey: "belongArchiveName",
        key: 'belongArchiveName',
        width: 300,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "账单编码",
        dataKey: "billCode",
        key: 'billCode',
        width: 300,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "账单类型",
        dataKey: "billType",
        key: 'billType',
        width: 300,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "账期",
        dataKey: "costTerm",
        key: 'costTerm',
        width: 300,
        type: 'text',
        isShow: true,
        isFixed: false
      }
    ]

    let tempHeader = data.resultNameList.map(item => {return {
      title: item.resultName,
      dataKey: item.resultCode,
      key: item.resultCode,
      dataType: item.resultType,
      decimal: item.decimal,
      width: 300,
      type: 'text',
      isShow: true,
      isFixed: false
    }})
    context.tableHeader.push(...tempHeader)
    return context.tableHeader
  }

  async initData(context = this, tableRef) {
    let { data: { data, code, message } } = await pbRequest.post(`${context.selectUri}?currentPage=${context.currentPage}&pageSize=${context.pageSize}`, context.searchData)
    if (code !== 200 || !data) return
    context.total = data.resultDataList.total

    context.tableData.length = 0
    context.tableData.push(...data.resultDataList.list.map(item => {
      let temp = { ...item, ...item.data }
      delete temp.data
      return temp
    }))

  }
  currentPageChange(page, context = this, tableRef) {
    context.currentPage = page
    context.initData(context, tableRef)
  }
  pageSizeChange(pageSize, context = this, tableRef) {
    context.currentPage = 1
    context.pageSize = pageSize
    context.initData(context, tableRef)
  }
  search(searchData, context = this, tableRef) {
    context.searchData = searchData
    context.currentPage = 1
    context.initData(context, tableRef, true)
  }

  async updateBill(params) {
    let { data: { code, data, msg } } = await pbRequest.post(`/colorful-fog/CollectResultMain/manualUpdate`, params)
    return code === 200 ? { data, code } : msg
  }

  async checkUpdateData(params) {
    let { data: { code, data, msg } } = await pbRequest.post(`/colorful-fog/UpdateBillLog/selectOneFieldLog`, params)
    return code === 200 ? { data, code } : msg
  }

  async getCollectExportData(params) {
    let { data: { code, data, msg } } = await pbRequest.post(`/colorful-fog/CollectResultMain/exportExcel`, params)
    return code === 200 ? { data, code } : msg
  }

  async updateBillFlash(params) {
    let { data: { code, data, msg } } = await pbRequest.post(`/colorful-fog/schemeMain/flash`, params)
    return code === 200 ? { data, code } : msg
  }

  async setInValid(params) {
    let { data: { code, data, msg } } = await pbRequest.post(`/colorful-fog/billResult/setInValid`, params)
    return code === 200 ? { data, code } : msg
  }

}

export { loading, DataSource }