import { pbRequest, loading } from "@/applications/sys/public/pbRequest/index"
import PublicDataSource from '@/applications/sys/utils/js/table-handle-class/publicDataSource'

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

  async initData(context = this, tableRef) {
    let { data: { data, code, message } } = await pbRequest.post(`${context.selectUri}?currentPage=${context.currentPage}&pageSize=${context.pageSize}`, context.searchData)
    if (code !== 200) return
    context.total = data.pageInfo.total
    context.tableData = data.pageInfo.list
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

  async update(params) {
    let { data: { data, code, message } } = await pbRequest.post(`/colorful-fog/condition/update`, params)
    return code === 200 ? { data, code } : message
  }

  async deleteRow(params) {
    let { data: { data, code, message } } = await pbRequest.post(`/colorful-fog/condition/delete`, params)
    return code === 200 ? { data, code } : message
  }

  async add(params) {
    let { data: { data, code, message } } = await pbRequest.post(`/colorful-fog/condition/add`, params)
    return code === 200 ? { data, code } : message
  }

  async getTableFiled(tableName) {
    let { data: { data, code, message } } = await pbRequest.post(`/colorful-fog/tableFiled/select?currentPage=1&pageSize=200`, { tableName })
    if (code !== 200) return
    return data.pageInfo.list
  }

}

export { loading, DataSource }