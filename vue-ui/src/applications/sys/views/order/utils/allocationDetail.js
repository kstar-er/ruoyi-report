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
      allocation_create: '制单',
      allocation_wait_out: '等待出库',
      allocation_out_ing: '出库中',
      allocation_ing: '调拨中',
      allocation_in_ing: '入库中',
      allocation_doc: '归档'

    }
    switch (key){
    case 'processStatus': return processStatus[value]
    default: {
      return value && value.toString().length > 0 ? value : typeof value === 'number' ? value : '-'
    }
    }
  }

  async addAllocation(params){
    const { data: { code, data, msg } } = await pbRequest.post(`/wms/allocationOrderMain`, params)
    return code === 200 ? { code, data } : msg
  }

  async updateAllocation(params){
    const { data: { code, data, msg } } = await pbRequest.put(`/wms/allocationOrderMain`, params)
    return code === 200 ? { code, data } : msg
  }

  async getAllocationDetail(id){
    const { data: { code, data, msg } } = await pbRequest.get(`/wms/allocationOrderMain/${id}`)
    return code === 200 ? { code, data } : msg
  }

  async getAllocationListDetail(allocationCode){
    const { data: { code, rows, msg } } = await pbRequest.get(`/wms/allocationOrderDetail/list?pageSize=1000000&pageNum=1&allocationCode=${allocationCode}`)
    return code === 200 ? { code, rows } : msg
  }

  async deleteAllocationOrder(id){
    const { data: { code, data, msg } } = await pbRequest.delete(`/wms/allocationOrderMain/${id}`)
    return code === 200 ? { code, data } : msg
  }

}

export { loading, DataSource }