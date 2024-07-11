import { pbRequest, loading } from "@/applications/sys/public/pbRequest/index"
import PublicDataSource from '@/applications/sys/utils/js/table-handle-class/publicDataSource'

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

  forMatData(key, value, rowData){
    const warehouseType = {
      warehouse: '仓库',
      area: '库区',
      frame: '货架',
      position: '仓位'

    }
    switch (key){

    case 'warehouseType': return warehouseType[value] ?? '-'

    default: {
      return value && value.toString().length > 0 ? value : typeof value === 'number' ? value : '-'
    }
    }
  }

  async searchWarehouseDataById(context = this, searchData){
    let url = `/wms/warehouse/listDemo?pageNum=${context.currentPage}&pageSize=${context.pageSize}`
    let searchKey = Object.keys(searchData)
    let searchValue = ``
    searchKey.forEach(item => {
      searchValue += `&${item}=${searchData[item]}`
    })
    url += searchValue
    let { data: { data, code, message } } = await pbRequest.get(url)
    if (code !== 200) return
    else {
      return context.createTreeData(data.rows)
    }
  }

  async addWarehouse(params){
    const { data: { code, data, msg } } = await pbRequest.post(`/wms/warehouse`, params)
    return code === 200 ? { code, data } : msg
  }

  async updateWarehouse(params){
    const { data: { code, data, msg } } = await pbRequest.put(`/wms/warehouse`, params)
    return code === 200 ? { code, data } : msg
  }

  async deleteWarehouse(id){
    const { data: { code, data, msg } } = await pbRequest.delete(`/wms/warehouse/${id}`)
    return code === 200 ? { code, data } : msg
  }

}

export { loading, DataSource }