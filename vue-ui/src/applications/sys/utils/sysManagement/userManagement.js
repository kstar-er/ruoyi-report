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
      item.checked = false
      res.push(findSon(data, item))
    })
    return res[0]
  }

  async getRole(){
    const { data: { code, data, msg } } = await pbRequest.post('/system/role/list', {})
    return code === 200 ? { code, data } : msg
  }

  async getRoleById(userId){
    const { data: { code, data, msg } } = await pbRequest.get(`/system/user/authRole/${userId}`)
    return code === 200 ? { code, data } : msg
  }

  async getDept(){
    let { data: { data, code, message } } = await pbRequest.post(`/system/dept/list`, {})
    if (code !== 200) return
    return this.createTreeData(data)
  }

  async addUser(params){
    let url = ''
    if (params.commission) url = `/system/user?commission=${params.commission}`
    else url = `/system/user`
    const { data: { code, data, msg } } = await pbRequest.post(url, params)
    return code === 200 ? { code, data } : msg
  }

  async getUser(id){
    const { data: { code, data, msg, roleIds, roles } } = await pbRequest.get(`/system/user/${id}`)
    return code === 200 ? { code, data, roleIds, roles } : msg
  }

  async deleteUser(id){
    const { data: { code, data, msg } } = await pbRequest.delete(`/system/user/${id}`)
    return code === 200 ? { code, data } : msg
  }

  async updateUser(params){
    let url = ''
    if (params.commission) url = `/system/user?commission=${params.commission}`
    else url = `/system/user`
    const { data: { code, data, msg } } = await pbRequest.put(url, params)
    return code === 200 ? { code, data } : msg
  }

  async changeStatus(params){
    const { data: { code, data, msg } } = await pbRequest.put(`system/user/changeStatus`, params)
    return code === 200 ? { code, data } : msg
  }

  async changePsw(params){
    const { data: { code, data, msg } } = await pbRequest.put(`system/user/resetPwd`, params)
    return code === 200 ? { code, data } : msg
  }

  async initData(context = this, tableRef){
    let { data: { data, code, message } } = await pbRequest.post(`${context.selectUri}?pageNum=${context.currentPage}&pageSize=${context.pageSize}`, context.searchData)
    if (code !== 200) return
    context.total = data.total
    context.tableData.length = 0
    if (data.rows){
      context.tableData.push(...data.rows.map(item => {
        item.deptName = item.dept ? item.dept.deptName : null
        return item
      }))
    }
    else {
      context.tableData.push(...data)
    }
    tableRef?.clearSelection()
  }

  async getAgencyLogin(){
    const { data: { code, data, msg } } = await pbRequest.get(`/xindian/agency/loginUser`)
    return code === 200 ? { code, data } : msg
  }
}

export { loading, DataSource }