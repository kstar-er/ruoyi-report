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

    let processStatus = {
      in_create: '制单',
      in_wait_out: '等待入库',
      in_ing: '入库中',
      in_doc: '归档',
      in_cancel: '作废'
    }

    switch (key){
    case 'processStatus': return processStatus[value]
    default: {
      return value && value.toString().length > 0 ? value : typeof value === 'number' ? value : '-'
    }
    }
  }

  async addInOrder(params){
    const { data: { code, data, msg } } = await pbRequest.post(`/wms/InWarehouseOrderMain/create`, params)
    return code === 200 ? { code, data } : msg
  }

  async updateInOrder(params){
    const { data: { code, data, msg } } = await pbRequest.post(`/wms/InWarehouseOrderMain/updateDetail`, params)
    return code === 200 ? { code, data } : msg
  }

  async deleteInOrder(id){
    const { data: { code, data, msg } } = await pbRequest.delete(`/wms/InWarehouseOrderMain/${id}`)
    return code === 200 ? { code, data } : msg
  }

  async getInDetail(id){
    const { data: { code, data, msg } } = await pbRequest.get(`/wms/InWarehouseOrderMain/${id}`)
    return code === 200 ? { code, data } : msg
  }

  async getInProductList(inWarehouseCode){
    const { data: { code, data, msg, rows } } = await pbRequest.get(`/wms/InWarehouseOrderDetail/list?pageNum=1&pageSize=2000&inWarehouseCode=${inWarehouseCode}`)
    return code === 200 ? { code, data, rows } : msg
  }

  async getInProductDetailList(inWarehouseCode){
    const { data: { code, data, msg, rows } } = await pbRequest.get(`/wms/InWarehouseOrderProductDetail/list?pageNum=1&pageSize=2000&inWarehouseCode=${inWarehouseCode}`)
    return code === 200 ? { code, data, rows } : msg
  }

  async getInPositionList(inWarehouseCode){
    const { data: { code, data, msg, rows } } = await pbRequest.get(`/wms/InWarehouseOrderPositionDetail/list?pageNum=1&pageSize=2000&inWarehouseCode=${inWarehouseCode}`)
    return code === 200 ? { code, data, rows } : msg
  }

  async getInScanList(inWarehouseCode){
    const { data: { code, data, msg, rows } } = await pbRequest.get(`/wms/InWarehouseOrderScanDetail/list?pageNum=1&pageSize=2000&inWarehouseCode=${inWarehouseCode}`)
    return code === 200 ? { code, data, rows } : msg
  }

  async getAllocationDetail(params){
    const { data: { code, data, msg } } = await pbRequest.post(`/wms/OutWarehouseOrderMain/queryByAllocationOrderDetail`, params)
    return code === 200 ? { code, data } : msg
  }

  async manualInWarehouse(params){
    const { data: { code, data, msg } } = await pbRequest.post(`/wms/InWarehouseOrderMain/manualInWarehouse`, params)
    return code === 200 ? { code, data } : msg
  }

  async completeButLack(params){
    const { data: { code, data, msg } } = await pbRequest.post(`/wms/InWarehouseOrderMain/completeButLack`, params)
    return code === 200 ? { code, data } : msg
  }

}

export { loading, DataSource }