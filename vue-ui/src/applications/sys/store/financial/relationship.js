
const relationship = {
  namespaced: true,
  state: {
    tableHeader: [{
      title: "分类Code",
      dataKey: "dependCode",
      key: 'dependCode',
      width: 330,
      type: 'text',
      isShow: true,
      isFixed: false
    }, {
      title: "分类名称",
      dataKey: "name",
      key: 'name',
      width: 330,
      type: 'text',
      isShow: true,
      isFixed: false
    }, {
      title: "依赖类型",
      dataKey: "dependType",
      key: 'dependType',
      width: 330,
      type: 'text',
      isShow: true,
      isFixed: false
    }, {
      title: "是否失效",
      dataKey: "isLoseEfficacy",
      key: 'isLoseEfficacy',
      width: 330,
      type: 'none',
      isShow: true,
      isFixed: false,
      options: []
    }],
    defaultTableHeader: [{
      title: "分类Code",
      dataKey: "dependCode",
      key: 'dependCode',
      width: 330,
      type: 'text',
      isShow: true,
      isFixed: false
    }, {
      title: "分类名称",
      dataKey: "name",
      key: 'name',
      width: 330,
      type: 'text',
      isShow: true,
      isFixed: false
    }, {
      title: "依赖类型",
      dataKey: "dependType",
      key: 'dependType',
      width: 330,
      type: 'text',
      isShow: true,
      isFixed: false
    }, {
      title: "是否失效",
      dataKey: "isLoseEfficacy",
      key: 'isLoseEfficacy',
      width: 330,
      type: 'none',
      isShow: true,
      isFixed: false,
      options: []
    }]
  },
  getters: {
  },
  mutations: {
    updateTableHeader(state, newTableHeader) {
      state.tableHeader.length = 0
      state.tableHeader.push(...newTableHeader)
    }
  },
  actions: {
  }
}
export default relationship
