/**
 *
 * 1、注册
 * 2、分发
 * 3、取消
 */

class EventBus {
  constructor(showTips = true){
    this.events = {}
    this.showLogTips = showTips // if show control message
  }
  $on(eventName, cb){
    if (this.events.hasOwnProperty(eventName)){
      this.events[eventName].push(cb)
    } else this.events[eventName] = [cb]
    if (this.showLogTips) console.log(eventName + ' bind a callback -- logTips')
  }
  $emit(eventName, ...args){
    if (this.events.hasOwnProperty(eventName)){
      this.events[eventName].forEach(cb => {
        cb.apply(this, args)
      })
    } else {
      console.warn(`This event is not bound to any callback function`)
    }
  }
  $cancel(eventName, cb){
    if (this.events.hasOwnProperty(eventName)){
      let length = this.events[eventName].length
      for (let i = 0; i < length; i++){
        if (this.events[eventName][i].toString() === cb.toString()){
          this.events[eventName].splice(i, 1)
          if (this.showLogTips) console.log(eventName + ' remove a callback -- logTips')
          return
        }
      }
    } else return
  }
}

const useEventBus = () => {
  // 创建 eventBus 实例，防止多个 eventBus 造成事件总线混乱，此处采用单例模式
  if (window.$eventBus){
    console.warn(`The current window already contains an eventBus instance，Please ensure that only one event bus instance is used`)
    return
  } else {
    window.$eventBus = new EventBus()
    console.log(`eventBus is ready`)
  }
}

export default useEventBus