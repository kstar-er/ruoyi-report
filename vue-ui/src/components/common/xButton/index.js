import xButton from './xButton.vue'
xButton.install = function(Vue){
  Vue.component('XButton', xButton) // (组件名称，对应组件)
}
export default xButton
