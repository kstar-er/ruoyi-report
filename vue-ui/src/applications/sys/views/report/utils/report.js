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
    switch (key){
    default: {
      if (key && value && (key.indexOf('Time') !== -1 || key.indexOf('Date') !== -1)){
        return new DataTool().timeStamp2Time(value, 'yyyy-MM-dd hh:mm:ss')
      }
      return value && value.toString().length > 0 ? value : typeof value === 'number' ? value : '-'
    }
    }
  }

  async getReportData(params) {
    let { data: { code, data, msg } } = await pbRequest.post(`/colorful-fog/RepoController/getData`, params)
    return code === 200 ? { data, code } : msg
  }

  async getSchemeHeader(params) {
    let { data: { code, data, msg } } = await pbRequest.post(`/colorful-fog/billResult/list?currentPage=1&pageSize=1`, params)
    let defaultName = [
      {
        resultName: "批次号",
        resultCode: "batchCode",
        resultType: 'STRING'
      }, {
        resultName: "状态",
        resultCode: "status",
        resultType: 'STRING'

      }, {
        resultName: "所属经销商",
        resultCode: "belongArchiveName",
        resultType: 'STRING'

      }, {
        resultName: "账单编码",
        resultCode: "billCode",
        resultType: 'STRING'

      }, {
        resultName: "账单类型",
        resultCode: "billType",
        resultType: 'STRING'
      }, {
        resultName: "账期",
        resultCode: "costTerm",
        resultType: 'STRING'
      }
    ]
    let resultNameList = [...defaultName, ...data.resultNameList]
    return code === 200 ? resultNameList : msg
  }

  async getCollectHeader(params) {
    let { data: { code, data, msg } } = await pbRequest.post(`/colorful-fog/CollectResultMain/list?currentPage=1&pageSize=1`, params)
    let defaultName = [{
      resultName: "所属经销商",
      resultCode: "belongArchiveName",
      resultType: 'STRING'

    }, {
      resultName: "账单编码",
      resultCode: "billCode",
      resultType: 'STRING'

    }, {
      resultName: "账单类型",
      resultCode: "billType",
      resultType: 'STRING'

    }, {
      resultName: "账期",
      resultCode: "costTerm",
      resultType: 'STRING'

    }]
    let resultNameList = [...defaultName, ...data.resultNameList]
    return code === 200 ? resultNameList : msg
  }

  async saveReportJson(params) {
    let { data: { code, data, msg } } = await pbRequest.post(`/colorful-fog/RepoController/saveRepoJson`, params)
    return code === 200 ? { data, code } : msg
  }

  async getRepoJson(params) {
    let { data: { code, data, msg } } = await pbRequest.post(`/colorful-fog/RepoController/getRepoJson`, params)
    return code === 200 ? { data, code } : msg
  }

}

export { loading, DataSource }