
const structure = {
  namespaced: true,
  state: {
    tableHeader: [{
      title: "数据库表",
      dataKey: "orderTable",
      key: 'orderTable',
      width: 300,
      type: 'text',
      isShow: true,
      isFixed: false
    }, {
      title: "表名称",
      dataKey: "orderTableName",
      key: 'orderTableName',
      width: 300,
      type: 'text',
      isShow: true,
      isFixed: false
    }, {
      title: "类型",
      dataKey: "type",
      key: 'type',
      width: 300,
      type: 'select',
      isShow: true,
      isFixed: false,
      options: []
    }, {
      title: "备注",
      dataKey: "comment",
      key: 'comment',
      width: 300,
      type: 'text',
      isShow: true,
      isFixed: false
    }],
    defaultTableHeader: [{
      title: "数据库表",
      dataKey: "orderTable",
      key: 'orderTable',
      width: 300,
      type: 'text',
      isShow: true,
      isFixed: false
    }, {
      title: "表名称",
      dataKey: "orderTableName",
      key: 'orderTableName',
      width: 300,
      type: 'text',
      isShow: true,
      isFixed: false
    }, {
      title: "类型",
      dataKey: "type",
      key: 'type',
      width: 300,
      type: 'select',
      isShow: true,
      isFixed: false,
      options: []
    }, {
      title: "备注",
      dataKey: "comment",
      key: 'comment',
      width: 300,
      type: 'text',
      isShow: true,
      isFixed: false
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
export default structure
