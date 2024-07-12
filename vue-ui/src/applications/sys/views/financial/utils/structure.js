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
  } = {}){
    super({
      modules,
      selectUri,
      deleteUri,
      selectOneUri,
      pageSize,
      tableHeader
    })
  }

  async initData(context = this, tableRef){
    let { data: { data, code, message } } = await pbRequest.post(`${context.selectUri}?currentPage=${context.currentPage}&pageSize=${context.pageSize}`, context.searchData)
    if (code !== 200) return
    context.total = data.pageInfo.total
    context.tableData.length = 0
    context.tableData.push(... data.pageInfo.list)
    tableRef?.clearSelection()
  }
  currentPageChange(page, context = this, tableRef){
    context.currentPage = page
    context.initData(context, tableRef)
  }
  pageSizeChange(pageSize, context = this, tableRef){
    context.currentPage = 1
    context.pageSize = pageSize
    context.initData(context, tableRef)
  }
  search(searchData, context = this, tableRef){
    context.searchData = searchData
    context.currentPage = 1
    context.initData(context, tableRef, true)
  }

  forMatData(key, value){
    let type = { MAIN: '主表', DETAIL: '明细' }
    switch (key){

    case 'type': return type[value] ? type[value] : value

    default: {
      return value && value.toString().length > 0 ? value : typeof value === 'number' ? value : '-'
    }
    }
  }

  async getStructure(id) {
    let { data: { code, data, msg } } = await pbRequest.post(`/colorful-fog/orderTable/select?currentPage=1&pageSize=1`, { tableId: id })
    return code === 200 ? { data, code } : msg
  }

  async deleteStructure(params) {
    let { data: { code, data, msg } } = await pbRequest.post(`/colorful-fog/orderTable/delete`, params)
    return code === 200 ? { data, code } : msg
  }

  async addStructure(params) {
    let { data: { code, data, msg } } = await pbRequest.post(`/colorful-fog/orderTable/add`, params)
    return code === 200 ? { data, code } : msg
  }

  async addStructureBatch(params) {
    let { data: { code, data, msg } } = await pbRequest.post(`/colorful-fog/orderTable/addBatch`, params)
    return code === 200 ? { data, code } : msg
  }

  async updateStructure(params) {
    let { data: { code, data, msg } } = await pbRequest.post(`/colorful-fog/orderTable/update`, params)
    return code === 200 ? { data, code } : msg
  }

  async getDetail(params) {
    let { data: { code, data, msg } } = await pbRequest.post(`/colorful-fog/tableFiled/select?currentPage=1&pageSize=10`, { id: params.id })
    return code === 200 ? { data, code } : msg
  }

  async addTableField(params) {
    let { data: { code, data, msg } } = await pbRequest.post(`/colorful-fog/tableFiled/addTableField`, params)
    return code === 200 ? { data, code } : msg
  }

  async addTableFields(params) {
    let { data: { code, data, msg } } = await pbRequest.post(`/colorful-fog/tableFiled/addTableFieldList`, params)
    return code === 200 ? { data, code } : msg
  }

  async updateTableFiled(params) {
    let { data: { code, data, msg } } = await pbRequest.post(`/colorful-fog/tableFiled/updateTableField`, params)
    return code === 200 ? { data, code } : msg
  }
  async addForeignKeys(params) {
    let { data: { code, data, msg } } = await pbRequest.post(`/colorful-fog/ForeignKey/addBatch`, params)
    return code === 200 ? { data, code } : msg
  }

  async deleteForeignKeys(params) {
    let { data: { code, data, msg } } = await pbRequest.post(`/colorful-fog/ForeignKey/delete`, params)
    return code === 200 ? { data, code } : msg
  }

  async getTable(params) {
    let { data: { code, data, msg } } = await pbRequest.post(`/colorful-fog/orderTable/getTableNameByDataSourceId`, params)
    return code === 200 ? { data, code } : msg
  }

}

export { loading, DataSource }