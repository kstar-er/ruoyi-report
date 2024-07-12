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

  forMatData(key, value) {
    let executionUnit = {
      DAILY: '每日生成一次',
      MONTHLY: '每月生成一次'

    }

    switch (key) {
    case 'executionUnit': {
      return executionUnit[value]
    }
    default: {
      if (key !== 'executionTime' && key && value && (key.indexOf('Time') !== -1 || key.indexOf('Date') !== -1)){
        return new DataTool().timeStamp2Time(value, 'yyyy-MM-dd hh:mm:ss')
      }

      return value && value.toString().length > 0 ? value : typeof value === 'number' ? value : '-'
    }
    }
  }

  createTreeData(data){
    let res = []
    Object.keys(data).forEach(key => {
      res.push({
        name: key,
        children: data[key]
      })
    })
    return res
  }

  async initData(context = this, tableRef) {
    context.tableData.length = 0
    let { data: { data, code, message } } = await pbRequest.post(`${context.selectUri}?currentPage=${context.currentPage}&pageSize=${context.pageSize}`, context.searchData)
    if (code !== 200) return
    context.tableData = context.createTreeData(data)
    context.total = context.tableData.length
    console.log(context.tableData)
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

  async getSolutionList(){
    const { data: { code, data, msg } } = await pbRequest.post(`/colorful-fog/schemeMain/getGroupType`)
    return code === 200 ? { code, data } : msg
  }

  async getDetail(id) {
    let { data: { code, data, msg } } = await pbRequest.post(`/colorful-fog/schemeMain/selectOneById?id=${id}`)
    return code === 200 ? { data, code } : msg
  }

  async deleteSchemeMain(params) {
    let { data: { code, data, msg } } = await pbRequest.post(`/colorful-fog/schemeMain/deleteSchemeMain`, params)
    return code === 200 ? { data, code } : msg
  }

  async refreshBill(params) {
    let { data: { code, data, msg } } = await pbRequest.post(`/colorful-fog/schemeMain/flash`, params)
    return code === 200 ? { data, code } : msg
  }

  async createBill(params) {
    let { data: { code, data, msg } } = await pbRequest.post(`/colorful-fog/schemeMain/create`, params)
    return code === 200 ? { data, code } : msg
  }

  async getBelongTable(params) {
    let { data: { code, data, msg } } = await pbRequest.post(`/colorful-fog/schemeUser/geBelongTable`, params)
    return code === 200 ? { data, code } : msg
  }

  async getUserAndScheme(params) {
    let { data: { code, data, msg } } = await pbRequest.post(`/colorful-fog/schemeUser/getUserAndScheme`, params)
    return code === 200 ? { data, code } : msg
  }

  async addSchemeToUser(params) {
    let { data: { code, data, msg } } = await pbRequest.post(`/colorful-fog/schemeUser/add`, params)
    return code === 200 ? { data, code } : msg
  }

}

const updateSchemeDetailBatch = async (params) => {
  let { data: { code, data, msg } } = await pbRequest.post(`/colorful-fog/schemeDetail/updateSchemeDetailBatch`, params)
  return code === 200 ? { data, code } : msg
}
const updateSchemeDetails = async (params) => {
  let { data: { code, data, msg } } = await pbRequest.post(`/colorful-fog/schemeDetail/updateDisplay`, params)
  return code === 200 ? { data, code } : msg
}
const addSchemeDetailBatch = async (params) => {
  let { data: { code, data, msg } } = await pbRequest.post(`/colorful-fog/schemeDetail/addSchemeDetailBatch`, params)
  return code === 200 ? { data, code } : msg
}

const deleteSchemeDetail = async (params) => {
  let { data: { code, data, msg } } = await pbRequest.post(`/colorful-fog/schemeDetail/deleteSchemeDetail`, params)
  return code === 200 ? { data, code } : msg
}

const updateSchemeMain = async (params) => {
  let { data: { code, data, msg } } = await pbRequest.post(`/colorful-fog/schemeMain/updateSchemeMain`, params)
  return code === 200 ? { data, code } : msg
}

const getDetail = async (id) => {
  let { data: { code, data, msg } } = await pbRequest.post(`/colorful-fog/schemeMain/selectOneById?id=${id}`)
  return code === 200 ? { data, code } : msg
}

const addSchemeMain = async (params) => {
  let { data: { code, data, msg } } = await pbRequest.post(`/colorful-fog/schemeMain/addSchemeMain`, params)
  return code === 200 ? { data, code } : msg
}

const getTheNewRow = async () => {
  let { data: { code, data, msg } } = await pbRequest.post(`/colorful-fog/schemeMain/selectMenu?currentPage=1&pageSize=1000`, {})
  return code === 200 ? { data, code } : msg
}

const addCondition = async (params) => {
  let { data: { code, data, msg } } = await await pbRequest.post(`/colorful-fog/condition/add`, params)
  return code === 200 ? { data, code } : msg
}

const deleteCondition = async (params) => {
  let { data: { data, code, message } } = await pbRequest.post(`/colorful-fog/condition/delete`, params)
  return code === 200 ? { data, code } : message
}

const getSelectUserData = async (params) => {
  let { data: { data, code, message } } = await pbRequest.post(`/colorful-fog/schemeUser/selectUserData`, params)
  return code === 200 ? { data, code } : message
}

const addSchemeUser = async (params) => {
  let { data: { data, code, message } } = await pbRequest.post(`/colorful-fog/schemeUser/add`, params)
  return code === 200 ? { data, code } : message
}

const addSchemeUserBatch = async (params) => {
  let { data: { data, code, message } } = await pbRequest.post(`/colorful-fog/schemeUser/bindUserBatch`, params)
  return code === 200 ? { data, code } : message
}

const schemeUserBatch = async (params) => {
  let { data: { data, code, message } } = await pbRequest.post(`/colorful-fog/schemeUser/addOrUpdateBatch`, params)
  return code === 200 ? { data, code } : message
}

const deleteSchemeUser = async (params) => {
  let { data: { data, code, message } } = await pbRequest.post(`/colorful-fog/schemeUser/delete`, params)
  return code === 200 ? { data, code } : message
}

const createTestData = async (params) => {
  let { data: { data, code, message } } = await pbRequest.post(`/colorful-fog/schemeMain/testScheme`, params)
  return code === 200 ? { data, code } : message
}

const copyField = async (params) => {
  let { data: { data, code, message } } = await pbRequest.post(`/colorful-fog/schemeDetail/copyField`, params)
  return code === 200 ? { data, code } : message
}

export { loading, DataSource,
  getSelectUserData,
  deleteCondition, getTheNewRow, updateSchemeDetailBatch,
  getDetail, updateSchemeDetails, addSchemeDetailBatch, deleteSchemeDetail,
  updateSchemeMain, addSchemeMain,
  addCondition, addSchemeUser, addSchemeUserBatch,
  deleteSchemeUser, createTestData, schemeUserBatch, copyField }