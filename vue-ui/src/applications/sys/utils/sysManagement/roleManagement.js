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
    case 'dataScope': {
      switch (value){
      case '1': return '所有数据范围'
      case '2': return '自定义数据权限'
      case '3': return '本部门数据权限'
      case '4': return '本部门及以下数据权限'
      case '5': return '仅本人数据权限'
      default: return ''
      }
    }
    default: {
      return value && value.toString().length > 0 ? value : typeof value === 'number' ? value : '-'
    }
    }
  }

  async addRole(params){
    const { data: { code, data, msg } } = await pbRequest.post(`/system/role`, params)
    return code === 200 ? { code, data } : msg
  }

  async upDateRole(params){
    const { data: { code, data, msg } } = await pbRequest.put(`/system/role`, params)
    return code === 200 ? { code, data } : msg
  }

  async deleteRole(roleIds){
    const { data: { code, data, msg } } = await pbRequest.delete(`/system/role/${roleIds}`)
    return code === 200 ? { code, data } : msg
  }

  async getRoleMenu(roleId){
    const { data: { code, data, msg } } = await pbRequest.get(`/system/menu/roleMenuTreeselect/${roleId}`)
    return code === 200 ? { code, data } : msg
  }

  async getRoleInfo(roleId){
    const { data: { code, data, msg } } = await pbRequest.get(`/system/role/${roleId}`)
    return code === 200 ? { code, data } : msg
  }

  async getDeptTree(roleId){
    const { data: { code, data, msg } } = await pbRequest.get(`system/role/deptTree/${roleId}`)
    return code === 200 ? { code, data } : msg
  }

  async setDataScope(params){
    const { data: { code, data, msg } } = await pbRequest.put(`system/role/dataScope`, params)
    return code === 200 ? { code, data } : msg
  }

  async changeStatus(params){
    const { data: { code, data, msg } } = await pbRequest.put(`system/role/changeStatus`, params)
    return code === 200 ? { code, data } : msg
  }

}

export { loading, DataSource }