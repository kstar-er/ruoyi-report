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

  forMatData(key, value, rowData, column) {

    switch (key) {
    case 'dealFlag': {
      return value ? '是' : '否'
    }
    default: {

      if (key && value && (key.indexOf('Time') !== -1 || key.indexOf('Date') !== -1)){
        return new DataTool().timeStamp2Time(value, 'yyyy-MM-dd hh:mm:ss')
      } else return value ?? '-'
    }
    }
  }

  async initData(context = this) {
    let { data: { data, code, message } } = await pbRequest.post(`${context.selectUri}?currentPage=${context.currentPage}&pageSize=${context.pageSize}`, context.searchData)
    if (code !== 200) return

    context.total = data.pageInfo.total
    context.tableData.length = 0
    context.tableData.push(...data.pageInfo.list)

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

  async exportAllData(params) {
    let { data: { code, data, msg } } = await pbRequest.post(`/colorful-fog/errReason/exportErrReason`, params)
    return code === 200 ? { data, code } : msg
  }

  async updateErrorData(params) {
    let { data: { code, data, msg } } = await pbRequest.post(`/colorful-fog/errReason/dealErr`, params)
    return code === 200 ? { data, code } : msg
  }

}

export { loading, DataSource }