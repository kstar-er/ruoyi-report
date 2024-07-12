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

    let forMatTimeFormula = (value1) => {
      let now = new DataTool().timeStamp2Time(Date.parse(new Date())).split('-')
      let value = value1.toString().split('.')

      if (Number(now[1]) - Math.abs(Number(value[0])) <= 0){
        now[0] = now[0] - Math.abs(Number(now[1]) - Math.abs(Number(value[0])))
      }
      return now[0] + '-' + (Number(now[1]) - Math.abs(Number(value[0]))) + '-' + value[1]
    }

    switch (key){
    case 'costTermFormula': {
      let now = new DataTool().timeStamp2Time(Date.parse(new Date())).slice(0, 7)
      return new DataTool().timeStamp2Time(Date.parse(now) + value * 2592000000).slice(0, 7)
    }
    case 'timeFormulaStart': return forMatTimeFormula(value)
    case 'timeFormulaEnd': return forMatTimeFormula(value)

    // case 'costTermFormula': return '' + (Math.abs(value) + 1) + ' 个月'
    case 'collectObject': return value === 'BILL' ? '汇总原始数据' : '二次汇总'
    case 'billType': return value === 'COST' ? '应收' : '应付'
    default: {
      if (key && value && (key.indexOf('Time') !== -1 || key.indexOf('Date') !== -1)){
        return new DataTool().timeStamp2Time(value, 'yyyy-MM-dd hh:mm:ss')
      }
      return value && value.toString().length > 0 ? value : typeof value === 'number' ? value : '-'
    }
    }
  }

  async addCollent(params) {
    let { data: { code, data, msg } } = await pbRequest.post(`/colorful-fog/collectSchemeMain/add`, params)
    return code === 200 ? { data, code } : msg
  }

  async updateCollent(params) {
    let { data: { code, data, msg } } = await pbRequest.post(`/colorful-fog/collectSchemeMain/update`, params)
    return code === 200 ? { data, code } : msg
  }

  async deleteCollent(params) {
    let { data: { code, data, msg } } = await pbRequest.post(`/colorful-fog/collectSchemeMain/delete`, params)
    return code === 200 ? { data, code } : msg
  }

  async addCollectDetails(params) {
    let { data: { code, data, msg } } = await pbRequest.post(`/colorful-fog/CollectSchemeDetail/addBatch`, params)
    return code === 200 ? { data, code } : msg
  }
  async updateCollectDetails(params) {
    let { data: { code, data, msg } } = await pbRequest.post(`/colorful-fog/CollectSchemeDetail/update`, params)
    return code === 200 ? { data, code } : msg
  }

  async deleteCollectDetails(params) {
    let { data: { code, data, msg } } = await pbRequest.post(`/colorful-fog/CollectSchemeDetail/delete`, params)
    return code === 200 ? { data, code } : msg
  }

  async getMainDetail(id) {
    let { data: { code, data, msg } } = await pbRequest.post(`/colorful-fog/collectSchemeMain/select?currentPage=1&pageSize=1`, { id })
    return code === 200 ? { data, code } : msg
  }

  async createCollect(params) {
    let { data: { code, data, msg } } = await pbRequest.post(`/colorful-fog/collectSchemeMain/create`, params)
    return code === 200 ? { data, code } : msg
  }

  async createOneUser(params) {
    let { data: { code, data, msg } } = await pbRequest.post(`/colorful-fog/collectSchemeMain/createOneUser`, params)
    return code === 200 ? { data, code } : msg
  }

  async createAndExport(params) {
    let { data: { code, data, msg } } = await pbRequest.post(`/colorful-fog/collectSchemeMain/createAndExport`, params)
    return code === 200 ? { data, code } : msg
  }

}

export { loading, DataSource }