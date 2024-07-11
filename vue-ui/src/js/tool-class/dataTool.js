import { pbRequest } from "@/applications/sys/public/pbRequest/index"

// import * as XLSX from "xlsx";
import XLSX from "xlsx-js-style"

import { ElMessageBox } from "element-plus"

/**
 * @name class:DataTool
 * @param { 无 }
 * @return { DataTool } DataTool 实例化对象
 * @author { 啊泽 2023-2-21 }
 * @example
 *
 *
 *
 * @objectListGetValueByKey  funtion => @param { list:Object[], keyName:String } @return { Array} 返回对象数组对应键名的数组
 * @readFile async function => @param { file:File } @return { string | ArrayBuffer } 读取文件流
 * @xlsx2DataObject async function @param { file:File } @return { Object } 文件流转化为对象
 * @addressParse function => @param { address:String } @return { Object } 地址解析
 * @timeStamp2Time function => @param { timeStamp:Number } @return { String } 时间戳转时间
 */

class DataTool {
  constructor() {
    // null
  }
  objectListGetValueByKey(list, keyName, returnType = "listObj") {
    if (returnType === "listObj") {
      if (typeof keyName === "string")
        return list.map((item, index) => item[keyName])
      else {
        let res = []
        for (let i = 0; i < list.length; i++) {
          let temp = {}
          for (let j = 0; j < keyName.length; j++) {
            temp[keyName[j]] = list[i][keyName[j]]
          }
          res.push(temp)
        }
        return res
      }
    } else {
      let res = []
      if (typeof keyName === "string") {
        list.forEach(item => {
          res.push(item[keyName])
        })
        return res
      } else return ["请保证keyName唯一"]
    }
  }

  readFile(file) {
    return new Promise(resolve => {
      let reader = new FileReader()
      reader.readAsBinaryString(file)
      reader.onload = ev => {
        resolve(ev.target.result)
      }
    })
  }

  timeStamp2Time(val, format = "yyyy-MM-dd") {
    //null
    let date = new Date(val)
    let y = date.getFullYear()
    let MM = date.getMonth() + 1
    MM = MM < 10 ? "0" + MM : MM
    let d = date.getDate()
    d = d < 10 ? "0" + d : d
    let h = date.getHours()
    h = h < 10 ? "0" + h : h
    let m = date.getMinutes()
    m = m < 10 ? "0" + m : m
    let s = date.getSeconds()
    s = s < 10 ? "0" + s : s
    if (format === "yyyy-MM-dd") return y + "-" + MM + "-" + d
    else return y + "-" + MM + "-" + d + " " + h + ":" + m + ":" + s
  }

  async xlsx2DataObject(file) {
    let data = await this.readFile(file)
    let workbook = XLSX.read(data, { type: "binary" }),
      worksheet = workbook.Sheets[workbook.SheetNames[0]]
    return XLSX.utils.sheet_to_json(worksheet)
  }

  exportExcel(header, data, title, style) {
    const wb = XLSX.utils.book_new()
    const ws = XLSX.utils.aoa_to_sheet([header,
      ...data])

    // 在单元格对象中添加 `s` 属性来设置该单元格的样式
    ws["!cols"] = header.map(item => { return { wpx: 150 } })
    if (style) {
      for (let i in style) {
        ws[i].s = style[i]
      }
    }
    XLSX.utils.book_append_sheet(wb, ws, title)
    XLSX.writeFile(wb, title + ".xlsx")
  }

  complexExcel(sheetList, title) {
    const wb = XLSX.utils.book_new()

    sheetList.forEach(item => {
      let { header, data, style, title } = item
      const ws = XLSX.utils.aoa_to_sheet([header,
        ...data])

      // 在单元格对象中添加 `s` 属性来设置该单元格的样式
      ws["!cols"] = header.map(item => { return { wpx: 150 } })
      if (style) {
        for (let i in style) {
          ws[i].s = style[i]
        }
      }
      XLSX.utils.book_append_sheet(wb, ws, title)
    })
    XLSX.writeFile(wb, title + ".xlsx")
  }

  // 删除对象中的某些属性
  deleteObjectKeys(source, keysList) {
    let map = new Map(Object.entries(source))
    if (typeof keysList === "string") {
      map.delete(keysList)
    } else {
      keysList.forEach(key => {
        map.delete(key)
      })
    }
    return Object.fromEntries(map.entries())
  }

  async getMenuSelect() {
    const {
      data: { code, data, msg }
    } = await pbRequest.get("/system/menu/treeselect")
    return code === 200 ? { code, data } : msg
  }

  // 比较两个对象，返回target对象对于source对象的key的补集
  keyComplementarySet(source, target) {
    let sourceKey = Object.keys(source)
    let targetKey = Object.keys(target)
    return sourceKey.filter(item => !targetKey.includes(item))
  }

  // 防抖
  debounce(cb, delay, ...args) {
    let timeout
    return () => {
      let context = this
      if (timeout) {
        clearTimeout(timeout)
      }
      timeout = setTimeout(() => {
        cb.call(context, ...args)
        timeout = null
      }, delay)
    }
  }

  // 节流
  throttle(cb, delay, ...args) {
    let lastTime = 0
    return () => {
      let context = this
      let now = Date.now()
      if (now - lastTime > 1000) {
        cb.call(context, ...args)
        lastTime = now
      } else {
        ElMessageBox.alert("点太快了，系统正在运转，休息一下吧。", "提示", {
          type: "warning",
          icon: "InfoFilled",
          confirmButtonText: "好的"
        })
      }
    }
  }
}

export { DataTool }
