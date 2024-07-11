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
    switch (key){

    case 'place': return `${rowData.province ?? '-'}/${rowData.city ?? '-'}/${rowData.region ?? '-'}/${rowData.street ?? '-'}`

    default: {
      return value && value.toString().length > 0 ? value : typeof value === 'number' ? value : '-'
    }
    }
  }

  async addMerchant(params){
    const { data: { code, data, msg } } = await pbRequest.post(`/wms/merchant`, params)
    return code === 200 ? { code, data } : msg
  }

  async updateMerchant(params){
    const { data: { code, data, msg } } = await pbRequest.put(`/wms/merchant`, params)
    return code === 200 ? { code, data } : msg
  }

  async deleteMerchant(id){
    const { data: { code, data, msg } } = await pbRequest.delete(`/wms/merchant/${id}`)
    return code === 200 ? { code, data } : msg
  }

}

export { loading, DataSource }