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
      inventory_create: '制单',
      inventory_reject: '驳回',
      inventory_ing: '盘点中',
      inventory_check: '审核中',
      inventory_doc: '归档',

      inventory_task_create: '制单',
      inventory_task_start: '盘点中',
      inventory_task_audit: '审核',
      inventory_task_doc: '归档',

      inventory_task_allocation_not_started: '未盘点',
      inventory_task_allocation_end: '盘点完成',
      inventory_task_allocation_reviewed: '待复盘',

      inventory_task_detail_not_started: "汇总失败",
      inventory_task_detail_end: '汇总完成',
      inventory_task_detail_err: '汇总异常'

    }

    switch (key){
    case 'processStatus': return processStatus[value]
    case 'inventoryType': return value === 'CW' ? '按仓位盘' : '按产品盘'
    case 'isOpenCheck': return value === 1 ? '明盘' : '暗盘'
    case 'userObject': return '点击查看'
    default: {
      return value && value.toString().length > 0 ? value : typeof value === 'number' ? value : '-'
    }
    }
  }

  async addSheet(params){
    const { data: { code, data, msg } } = await pbRequest.post(`/wms/sheet`, params)
    return code === 200 ? { code, data } : msg
  }

  async updateSheet(params){
    const { data: { code, data, msg } } = await pbRequest.put(`/wms/sheet`, params)
    return code === 200 ? { code, data } : msg
  }

  async getSheet(id){
    const { data: { code, data, msg } } = await pbRequest.get(`/wms/sheet/${id}`)
    return code === 200 ? { code, data } : msg
  }

  async nextProcess(id){
    const { data: { code, data, msg } } = await pbRequest.put(`/wms/sheet/nextProcess/${id}`)
    return code === 200 ? { code, data } : msg
  }

  async deleteSheet(id){
    const { data: { code, data, msg } } = await pbRequest.delete(`/wms/sheet/${id}`)
    return code === 200 ? { code, data } : msg
  }

  async backProcess(id){
    const { data: { code, data, msg } } = await pbRequest.put(`/wms/sheet/backProcess/${id}`)
    return code === 200 ? { code, data } : msg
  }

  async getTaskDetail({ warehouseCode, inventoryType, list }){
    const { data: { code, data, msg } } = await pbRequest.post(`/wms/taskDetail/obtainInventoryTaskDetailList?warehouseCode=${warehouseCode}&inventoryType=${inventoryType}`, list)
    return code === 200 ? { code, data } : msg
  }

  async getUserList(){
    const { data: { code, data, msg, rows } } = await pbRequest.get(`/wms/employee/list?pageNum=1&pageSize=10000`)
    return code === 200 ? { code, data, rows } : msg
  }

  async addCheckMissions(params){
    const { data: { code, data, msg } } = await pbRequest.post(`/wms/taskMain`, params)
    return code === 200 ? { code, data, msg } : msg
  }

  async updateCheckMissions(params){
    const { data: { code, data, msg } } = await pbRequest.put(`/wms/taskMain`, params)
    return code === 200 ? { code, data, msg } : msg
  }

  async getTask(id){
    const { data: { code, data, msg } } = await pbRequest.get(`/wms/taskMain/${id}`)
    return code === 200 ? { code, data } : msg
  }

  async deleteTask(id){
    const { data: { code, data, msg } } = await pbRequest.delete(`/wms/taskMain/${id}`)
    return code === 200 ? { code, data } : msg
  }

  async nextStatusTask(inventoryTaskOrderCode){
    const { data: { code, data, msg } } = await pbRequest.post(`/wms/taskMain/nextStatus?inventoryTaskOrderCode=${inventoryTaskOrderCode}`)
    return code === 200 ? { code, data } : msg
  }

  async setIsOpenCheck(params){
    const { data: { code, data, msg } } = await pbRequest.post(`/wms/taskMain/setIsOpenCheck`, params)
    return code === 200 ? { code, data } : msg
  }

  async oneClickSummary(inventoryTaskOrderCode){
    const { data: { code, data, msg } } = await pbRequest.post(`/wms/taskMain/oneClickSummary?inventoryTaskOrderCode=${inventoryTaskOrderCode}`)
    return code === 200 ? { code, data } : msg
  }

  async updateRemark(params){
    const { data: { code, data, msg } } = await pbRequest.put(`/wms/taskDetailAllocation/updateComment`, params)
    return code === 200 ? { code, data } : msg
  }

  async updateCheckWorks(params){
    const { data: { code, data, msg } } = await pbRequest.put(`/wms/taskDetailAllocation/updateBatch`, params)
    return code === 200 ? { code, data } : msg
  }

  async finishTaskMain(params){
    const { data: { code, data, msg } } = await pbRequest.post(`/wms/taskMainAllocation/updateBatch`, params)
    return code === 200 ? { code, data } : msg
  }

}

export { loading, DataSource }