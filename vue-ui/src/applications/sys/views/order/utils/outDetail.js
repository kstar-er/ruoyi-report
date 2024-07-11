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
      out_create: '制单',
      out_wait_out: '等待出库',
      out_out_ing: '出库中',
      out_cancel: '作废',
      out_doc: '归档'
    }
    switch (key){
    case 'processStatus': return processStatus[value]
    default: {
      return value && value.toString().length > 0 ? value : typeof value === 'number' ? value : '-'
    }
    }
  }

  async addOutOrder(params){
    const { data: { code, data, msg } } = await pbRequest.post(`/wms/OutWarehouseOrderMain/saveOutWarehouseOrder`, params)
    return code === 200 ? { code, data } : msg
  }

  async updateOutOrder(params){
    const { data: { code, data, msg } } = await pbRequest.put(`/wms/OutWarehouseOrderMain`, params)
    return code === 200 ? { code, data } : msg
  }

  async getOutDetail(id){
    const { data: { code, data, msg } } = await pbRequest.put(`/wms/OutWarehouseOrderMain/${id}`)
    return code === 200 ? { code, data } : msg
  }

  async getAllocationDetail(params){
    const { data: { code, data, msg } } = await pbRequest.post(`/wms/OutWarehouseOrderMain/queryByAllocationOrderDetail`, params)
    return code === 200 ? { code, data } : msg
  }

  async deleteOutOrder(id, flag){
    const { data: { code, data, msg } } = await pbRequest.delete(`/wms/OutWarehouseOrderMain/${id}/${flag}`)
    return code === 200 ? { code, data } : msg
  }

  async getOutDetailList(id){
    const { data: { code, data, msg } } = await pbRequest.get(`/wms/OutWarehouseOrderMain/selectOutOrder?id=${id}`)
    return code === 200 ? { code, data } : msg
  }

  async changeBarCode(params){
    const { data: { code, data, msg } } = await pbRequest.post(`/wms/OutWarehouseOrderMain/changeBarCode`, params)
    return code === 200 ? { code, data } : msg
  }

  async manualOutOrder(params){
    const { data: { code, data, msg } } = await pbRequest.post(`/wms/OutWarehouseOrderMain/manualOutOrder`, params)
    return code === 200 ? { code, data } : msg
  }

}

export { loading, DataSource }