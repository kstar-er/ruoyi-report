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

  async initData(context = this, tableRef) {
    let { data: { data, code, message } } = await pbRequest.post(`${context.selectUri}?currentPage=${context.currentPage}&pageSize=${context.pageSize}`, context.searchData)
    if (code !== 200) return
    context.total = data.pageInfo.total
    context.tableData.length = 0
    context.tableData.push(...data.pageInfo.list)
    tableRef?.clearSelection()
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
    let dependType = {
      SORT: '名词分类',
      RANGE: '范围分类',
      DEPEND: '二次分类',
      OTHER: '其他'
    }

    switch (key) {
    case 'dependType': {
      return dependType[value]
    }
    default: {
      if (key && value && (key.indexOf('Time') !== -1 || key.indexOf('Date') !== -1)){
        return new DataTool().timeStamp2Time(value, 'yyyy-MM-dd hh:mm:ss')
      }

      return value && value.toString().length > 0 ? value : typeof value === 'number' ? value : '-'
    }
    }
  }

  async addDependMain(params) {
    let { data: { code, data, msg } } = await pbRequest.post(`/colorful-fog/dependMain/addDependMain`, params)
    return code === 200 ? { data, code } : msg
  }

  async updateDependMain(params) {
    let { data: { code, data, msg } } = await pbRequest.post(`/colorful-fog/dependMain/updateDependMain`, params)
    return code === 200 ? { data, code } : msg
  }

  async deleteDependMain(params) {
    let { data: { code, data, msg } } = await pbRequest.post(`/colorful-fog/dependMain/deleteDependMain`, params)
    return code === 200 ? { data, code } : msg
  }

  async addDependRule(params) {
    let { data: { code, data, msg } } = await pbRequest.post(`/colorful-fog/dependRule/addDependRule`, params)
    return code === 200 ? { data, code } : msg
  }

  async updateDependRule(params) {
    let { data: { code, data, msg } } = await pbRequest.post(`/colorful-fog/dependRule/updateDependRule`, params)
    return code === 200 ? { data, code } : msg
  }

  async deleteDependRule(params) {
    let { data: { code, data, msg } } = await pbRequest.post(`/colorful-fog/dependRule/deleteDependRule`, params)
    return code === 200 ? { data, code } : msg
  }

  async addDependDataBatch(params) {
    let { data: { code, data, msg } } = await pbRequest.post(`/colorful-fog/dependData/addDependDataBatch`, params)
    return code === 200 ? { data, code } : msg
  }

  async updateDependData(params) {
    let { data: { code, data, msg } } = await pbRequest.post(`/colorful-fog/dependData/updateDependData`, params)
    return code === 200 ? { data, code } : msg
  }

  async deleteDependData(params) {
    let { data: { code, data, msg } } = await pbRequest.post(`/colorful-fog/dependData/deleteDependData`, params)
    return code === 200 ? { data, code } : msg
  }

  async deleteDependData(params) {
    let { data: { code, data, msg } } = await pbRequest.post(`/colorful-fog/dependData/deleteDependData`, params)
    return code === 200 ? { data, code } : msg
  }
}

export { loading, DataSource }