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

  createTreeData(val){

    let temp = [...val]
    temp.unshift({ id: '0' }) // 因为是多层级树状，顶级树状是0，我们给顶级树状树立一个父亲，就变成了顶级只有一个，利用浅拷贝就可以很快得出结果
    let idPosition = {}
    temp.forEach((item, idx) => {
      item.id = item.id.toString()
      item.children = []
      idPosition[item.id] = idx // 记录这个id出现在哪个位置
    })
    for (let i = 0; i < temp.length; i++){
      temp[i].value = temp[i].id.toString() // 顺便把表单的树状结构需要的数据也填上
      temp[i].label = temp[i].warehouseName // 顺便把表单的树状结构需要的数据也填上

      if (temp[i].id === '0') continue // id为 0 是我们自己插入的，不用管
      if (idPosition[temp[i].parentId]) temp[idPosition[temp[i].parentId]].children.push(temp[i]) // 父id在哪里，然后就插入一个孩子给父亲
      else temp[idPosition[0]].children.push(temp[i]) // 没有父亲丢到最顶层
    }
    console.log(temp)
    return temp[0].children // 最后返回我们构建的祖先的孩子，就是所有的树状结构
  }

  async initData(context = this, tableRef){
    let url = `${context.selectUri}?pageNum=${context.currentPage}&pageSize=${context.pageSize}`
    let searchKey = Object.keys(context.searchData)
    let searchValue = ``
    searchKey.forEach(item => {
      searchValue += `&${item}=${context.searchData[item]}`
    })
    url += searchValue
    let { data: { data, code, message } } = await pbRequest.get(url)
    if (code !== 200) return
    context.total = data.total
    context.tableData.length = 0
    context.tableData.push(...context.createTreeData(data.rows))
    tableRef?.clearSelection()
    return context.tableData
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