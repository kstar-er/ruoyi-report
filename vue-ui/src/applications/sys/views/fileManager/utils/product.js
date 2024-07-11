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

  forMatData(key, value){
    switch (key){
    case 'productBarcodeAlways': return value ? '是' : '否'
    case 'canBeBreak': return value ? '是' : '否'

    default: {
      return value && value.toString().length > 0 ? value : typeof value === 'number' ? value : '-'
    }
    }
  }

  async addProduct(params){
    const { data: { code, data, msg } } = await pbRequest.post(`/wms/product`, params)
    return code === 200 ? { code, data } : msg
  }

  async deleteProduct(id){
    const { data: { code, data, msg } } = await pbRequest.delete(`/wms/product/${id}`)
    return code === 200 ? { code, data } : msg
  }

  async getProductSon(id){
    const { data: { code, data, msg } } = await pbRequest.get(`/wms/product/queryProductList?id=${id}`)
    return code === 200 ? { code, data } : msg
  }

  async getProductDetail(id){
    const { data: { code, data, msg } } = await pbRequest.get(`/wms/product/${id}`)
    return code === 200 ? { code, data } : msg
  }
}

export { loading, DataSource }