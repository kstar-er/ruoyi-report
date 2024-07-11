
import { createStore } from 'vuex'
import createPersistedstate from 'vuex-persistedstate'
import userManagement from './sysManagement/userManagement'
import role from './sysManagement/roleManagement'
import depart from './sysManagement/departmentManagement'
import menu from './sysManagement/menuManagement'
import shoper from './fileManager/shoper'
import warehouse from './fileManager/warehouse'
import product from './fileManager/product'
import productType from './fileManager/productType'
import stock from './fileManager/stock'
import position from './fileManager/position'
import flow from './fileManager/flow'
import inOrder from './fileManager/in'
import out from './fileManager/out'
import allocation from './fileManager/allocation'
import worker from './fileManager/worker'
import checkMissions from './fileManager/checkMissions'
import checkWorks from './fileManager/checkWorks'
import sheet from './fileManager/sheet'
import batch from './fileManager/batch'
export default createStore({
  state: {
    isLoadRouter: false,
    routerInfo: []
  },
  getters: {
  },
  mutations: {
    updateRouterInfo(state, { data, sign }) {
      state.isLoadRouter = sign
      state.routerInfo = data
    }
  },
  actions: {
  },
  modules: {
    userManagement, role, depart, menu,
    shoper, warehouse, product, productType,
    stock, position, flow,
    allocation, out, inOrder,
    worker, sheet, checkMissions, checkWorks, batch

  },
  plugins: [createPersistedstate({
    storage: window.localStorage,
    key: 'client-header-store',
    paths: [
      'userManagement', 'role', 'depart', 'menu',
      'shoper', 'warehouse', 'product', 'productType',
      'stock', 'position', 'flow',
      'allocation', 'out', 'inOrder', 'worker', 'sheet', 'checkMissions', 'checkWorks', 'batch'
    ]
  })]
})
