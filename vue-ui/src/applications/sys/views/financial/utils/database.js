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

  async initData(context = this) {
    let { data: { data, code, message } } = await pbRequest.post(`${context.selectUri}?currentPage=${context.currentPage}&pageSize=${context.pageSize}`, context.searchData)
    if (code !== 200) return
    context.total = data.length
    context.tableData = data
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

  forMatData(key, value) {
    let type = { MAIN: '主表', DETAIL: '明细' }
    switch (key) {
    case 'type': return type[value] ? type[value] : value
    default: {
      return value && value.toString().length > 0 ? value : typeof value === 'number' ? value : '-'
    }
    }
  }

  async update(params) {
    let { data: { code, data, msg } } = await pbRequest.post(`/colorful-fog/dataSource/update`, params)
    return code === 200 ? { data, code } : msg
  }

  async add(params) {
    let { data: { code, data, msg } } = await pbRequest.post(`/colorful-fog/dataSource/add`, params)
    return code === 200 ? { data, code } : msg
  }

  async deleteRow(params) {
    let { data: { code, data, msg } } = await pbRequest.post(`/colorful-fog/dataSource/delete`, params)
    return code === 200 ? { data, code } : msg
  }

  async testConnect(params) {
    let { data: { code, data, msg } } = await pbRequest.post(`/colorful-fog/dataSource/testConnection`, params)
    return code === 200 ? { data, code } : msg
  }

}

export { loading, DataSource }