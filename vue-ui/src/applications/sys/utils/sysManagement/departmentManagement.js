import { pbRequest, loading } from "@/applications/sys/public/pbRequest/index"
import PublicDataSource from '../js/table-handle-class/publicDataSource'

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

  forMatData(key, value){
    switch (key){
    case 'sex': return +value ? '女' : '男'
    case 'status': return +value ? '停用' : '正常'
    case 'avatar': return '点击浏览'
    default: {
      return value && value.toString().length > 0 ? value : typeof value === 'number' ? value : '-'
    }
    }
  }

  createTreeData(data){
    let res = []
    function findSon(data, parent){
      data.forEach(item => {
        if (item.parentId === parent.deptId) parent.children.push(item)
      })
      return parent
    }
    data.forEach(item => {
      item.label = item.deptName
      item.value = item.deptId
      res.push(findSon(data, item))
    })
    return res[0]
  }

  async initData(context = this, tableRef){
    let { data: { data, code, message } } = await pbRequest.post(`${context.selectUri}?pageNum=${context.currentPage}&pageSize=${context.pageSize}`, context.searchData)
    if (code !== 200 || data.length === 0) return
    context.total = data.length
    context.tableData.length = 0
    context.tableData.push(context.createTreeData(data))
    tableRef?.clearSelection()
  }

  async updateDept(params){
    const { data: { code, data, msg } } = await pbRequest.put('/system/dept', params)
    return code === 200 ? { code, data } : msg
  }

  async getDept(deptId){
    const { data: { code, data, msg } } = await pbRequest.get(`/system/dept/${deptId}`)
    return code === 200 ? { code, data } : msg
  }

  async addDept(params){
    const { data: { code, data, msg } } = await pbRequest.post(`/system/dept`, params)
    return code === 200 ? { code, data } : msg
  }

  async deleteDept(deptId){
    const { data: { code, data, msg } } = await pbRequest.delete(`/system/dept/${deptId}`)
    return code === 200 ? { code, data } : msg
  }
}

export { loading, DataSource }