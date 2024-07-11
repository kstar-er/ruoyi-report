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
    const orderType = {
      in_warehouse_order: '入库单',
      out_warehouse_order: '出库单'
    }
    const warehouseType = {
      warehouse: '仓库',
      area: '库区',
      frame: '货架',
      position: '仓位'

    }
    switch (key){
    case 'isFrozen': return value ? '是' : '否'
    case 'optType': return value === 'plus' ? '增加' : '减少'
    case 'orderType': return orderType[value]
    case 'warehouseType': return warehouseType[value]
    case 'recordType': return value === 'stock_main' ? '仓库' : '库位'

    default: {
      return value && value.toString().length > 0 ? value : typeof value === 'number' ? value : '-'
    }
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

  async batchReset(params){
    const { data: { code, data, msg } } = await pbRequest.post(`/wms/positionReset/batchReset`, params)
    return code === 200 ? { code, data } : msg
  }

}

export { loading, DataSource }