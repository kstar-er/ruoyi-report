import { getCurrentInstance } from 'vue'

/**
 * @name function:usePopup
 * @info  定义一个函数来处理模态对话框的公共逻辑
 * @returns  {object} 包含 handleAlert(普通提示框) 和 handlePrompt(带输入内容的提示框) 方法的对象
 * @param {title,message,options,onConfirm}
 * @example
 * 例:
 * handleAlert('提示', '是否取消修改', { type: 'error'}, () => {
 *   initData()
 * })
 *
 * handlePrompt('提示', '是否保存当前的展示顺序', { confirmButtonText: '审核' }, (value) => {
 *    console.log("输入的内容为：", value)
 * })
 *
 * @title  String => 提示框的标题
 * @message String => 提示框内容
 * @options Object =>  提示框的行为，可覆盖默认行为
 * @onConfirm function => 点击确认按钮触发的方法
 */
export function usePopup() {
  const { proxy } = getCurrentInstance()
  const handleAlert = (title, message, options, onConfirm) => {
    // 默认选项
    const defaultOptions = {
      type: 'info',
      showCancelButton: true,
      cancelButtonText: '再想想',
      confirmButtonText: '确认',
      callback: (action) => {
        if (action === 'confirm') {
          onConfirm()
        }
      }
    }

    // 合并默认选项和传入的选项
    const mergedOptions = { ...defaultOptions, ...options }

    // 使用合并后的选项调用 $alert
    proxy.$alert(message, title, mergedOptions)
  }
  const handlePrompt = (title, message, options, onConfirm) => {
    // 默认选项
    const defaultOptions = {
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    }

    // 合并默认选项和传入的选项
    const mergedOptions = { ...defaultOptions, ...options }

    // 使用合并后的选项调用 $prompt
    proxy.$prompt(message, title, mergedOptions).then(({ value }) => {
      onConfirm(value)
    }).catch(() => {
    //null
    })
  }
  return {
    popup: {
      handleAlert,
      handlePrompt
    }
  }
}