import { pbRequest, loading } from "@/applications/sys/public/pbRequest/index"
import PublicDataSource from '../js/table-handle-class/publicDataSource'

class DataSource extends PublicDataSource {
  constructor({
    modules = '',
    selectUri = '/',
    deleteUri = '/',
    selectOneUri = '/',
    pageSize = 20,
    tableHeader,
    listMethod = 'post' // 数据请求的方法 post 或 get
  } = {}){
    super({
      modules,
      selectUri,
      deleteUri,
      selectOneUri,
      pageSize,
      tableHeader,
      listMethod
    })
  }

  forMatData(key, value){
    let menuType = {
      'M': '目录',
      'C': '菜单',
      'F': '按钮'
    }
    switch (key){
    case 'menuType': {
      return menuType[value]
    }
    case 'isCache': {
      return +value ? '不缓存' : '缓存'
    }
    case 'isFrame': {
      return +value ? '否' : '是'
    }
    case 'visible': {
      return +value ? '隐藏' : '显示'
    }
    default: {
      return value && value.toString().length > 0 ? value : typeof value === 'number' ? value : '-'
    }
    }
  }

  createTreeData(data){
    let res = []
    function findSon(data, parent){
      data.forEach(item => {
        if (item.parentId === parent.menuId) parent.children.push(item)
      })
      return parent
    }
    data.forEach(item => {
      item.label = item.menuName
      item.value = item.menuId
      res.push(findSon(data, item))
    })
    return res.filter(item => item.parentId === 0)
  }

  async initData(context = this, tableRef){
    let { data: { data, code, message } } = await pbRequest.post(`${context.selectUri}?pageNum=${context.currentPage}&pageSize=${context.pageSize}`, context.searchData)
    if (code !== 200) return
    context.total = data.length
    context.tableData.length = 0
    context.noHandleData = data
    context.tableData.push(...context.createTreeData(data))

    tableRef?.clearSelection()
  }

  async addMenu(params){
    const { data: { code, data, msg } } = await pbRequest.post(`/system/menu`, params)
    return code === 200 ? { code, data } : msg
  }

  async updateMenu(params){
    const { data: { code, data, msg } } = await pbRequest.put(`/system/menu`, params)
    return code === 200 ? { code, data } : msg
  }

  async getMenuInfo(menuId){
    const { data: { code, data, msg } } = await pbRequest.get(`/system/menu/${menuId}`)
    return code === 200 ? { code, data } : msg
  }

  async deleteMenu(menuId){
    const { data: { code, data, msg } } = await pbRequest.delete(`/system/menu/${menuId}`)
    return code === 200 ? { code, data } : msg
  }

}

export { loading, DataSource }