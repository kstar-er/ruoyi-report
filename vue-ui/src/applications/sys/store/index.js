
import { createStore } from 'vuex'
import createPersistedstate from 'vuex-persistedstate'
import userManagement from './sysManagement/userManagement'
import role from './sysManagement/roleManagement'
import depart from './sysManagement/departmentManagement'
import menu from './sysManagement/menuManagement'

import structure from './financial/structure'
import relationship from './financial/relationship'
import programme from './financial/programme'
import summaryPlan from './financial/summaryPlan'

import classificationError from './financial/classificationError'
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
    structure, relationship, programme, summaryPlan, classificationError
  },
  plugins: [createPersistedstate({
    storage: window.localStorage,
    key: 'client-header-store',
    paths: [
      'userManagement', 'role',
      'structure', 'relationship', 'summaryPlan', 'classificationError'
    ]
  })]
})
