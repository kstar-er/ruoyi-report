import { pbRequest } from "@/applications/sys/public/pbRequest/index"
import store from '../../../store/index'
import { DataTool } from '@/js/tool-class/dataTool'
const { _state } = store

/**
 * @name class:PublicDataSource
 * @param { Object } apiObject  接口和数据基本配置
 * @return { PublicDataSource } PublicDataSource 实例化对象
 * @author { 啊泽 2023-2-10 }
 * @example
 * 例:
 * new PublicDataSource({
 *    modules:'exampleModules',
 *    selectUri: '/',
 *    deleteUri: '/',
 *    selectOneUri: '/'
 *  })
 * @initData  funtion => 初始化数据
 * @currentPageChange funtion => 当前页变化
 * @pageSizeChange =>  页尺寸变化
 * @search funtion => 筛选数据
 * @delete funtion => 删除数据
 * @selectionChange funtion => 选中项改变
 * @forMatData funtion => 格式化数据
 * @initTableHeader funtion => 获取store表头
 */

class PublicDataSource {
  // selectUri默认为select接口的地址,大表格使用的是store里面的tableHeader所以无需传tableHeader值,小表或者不使用store里面的,可传入tableHeader,也可以在具体页面自己定义
  constructor({
    modules = '', // 模块名字 用于获取store
    selectUri = '/', // select接口地址
    deleteUri = '/', // 删除接口地址
    selectOneUri = '/', // 获取单个数据地址
    pageSize = 20, // 页大小
    tableHeader = []// 不读取store时的表头
  } = {}){
    this.modules = modules
    this.selectUri = selectUri
    this.selectOneUri = selectOneUri
    this.deleteUri = deleteUri
    this.searchData = {}
    this.currentPage = 1
    this.pageSize = pageSize
    this.total = 0
    this.tableData = []
    this.tableHeader = tableHeader
    this.selections = []
  }
  async initData(context = this, tableRef){
    let { data: { data, code, message } } = await pbRequest.post(`${context.selectUri}?pageNum=${context.currentPage}&pageSize=${context.pageSize}`, context.searchData)
    if (code !== 200) return
    context.total = data.total
    context.tableData.length = 0
    context.tableData.push(...data.rows)
    tableRef?.clearSelection()
  }
  currentPageChange(page, context = this, tableRef){
    context.currentPage = page
    context.initData(context, tableRef)
  }
  pageSizeChange(pageSize, context = this, tableRef){
    context.currentPage = 1
    context.pageSize = pageSize
    context.initData(context, tableRef)
  }
  search(searchData, context = this, tableRef){
    context.searchData = searchData
    context.currentPage = 1
    context.initData(context, tableRef, true)
  }
  async delete(idList, tableRef, context = this){
    let { data: { data, code, message } } = await pbRequest.post(`${context.deleteUri}`, idList)
    if (code === 200){
      context.initData(context, tableRef)
      return code
    } else return message
  }
  selectionChange(selections, context = this){
    context.selections.length = 0
    context.selections.push(...selections)
  }

  initTableHeader(context = this){
    if (context.tableHeader.length !== 0) return
    if (context.modules.length !== 0 && _state.data[context.modules]){
      context.tableHeader.length = 0
      context.tableHeader.push(..._state.data[context.modules].tableHeader.filter((item) => item.isShow))
    } else throw new Error('获取store中的表头, 请提供modules名')
  }

  async selectOne(id, context = this){
    let { data: { data, code, message } } = await pbRequest.post(`${context.selectOneUri}?id=${id}`)
    return data
  }

}

export default PublicDataSource