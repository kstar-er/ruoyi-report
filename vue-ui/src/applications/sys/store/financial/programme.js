
const programme = {
  namespaced: true,
  state: {
    tableHeader: [
      {
        title: "计划名称",
        dataKey: "name",
        key: 'name',
        width: 300,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "数据库名称",
        dataKey: "dataSourceName",
        key: 'dataSourceName',
        width: 300,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "账单类型",
        dataKey: "billType",
        key: 'billType',
        width: 300,
        type: 'none',
        isShow: true,
        isFixed: false
      }, {
        title: "账单生成计划",
        dataKey: "executionUnit",
        key: 'executionUnit',
        width: 300,
        type: 'none',
        isShow: true,
        isFixed: false
      }, {
        title: "账单生成时间",
        dataKey: "executionTime",
        key: 'executionTime',
        width: 300,
        type: 'none',
        isShow: true,
        isFixed: false
      }, {
        title: "表名",
        dataKey: "belongRelTableName",
        key: 'belongRelTableName',
        width: 300,
        type: 'none',
        isShow: true,
        isFixed: false
      }, {
        title: "字段名",
        dataKey: "belongRelTableField",
        key: 'belongRelTableField',
        width: 300,
        type: 'none',
        isShow: true,
        isFixed: false
      }, {
        title: "账单粒度",
        dataKey: "granularity",
        key: 'granularity',
        width: 300,
        type: 'none',
        isShow: true,
        isFixed: false
      }
    ],
    defaultTableHeader: [
      {
        title: "计划名称",
        dataKey: "name",
        key: 'name',
        width: 300,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "数据库名称",
        dataKey: "dataSourceName",
        key: 'dataSourceName',
        width: 300,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "账单类型",
        dataKey: "billType",
        key: 'billType',
        width: 300,
        type: 'none',
        isShow: true,
        isFixed: false
      }, {
        title: "账单生成计划",
        dataKey: "executionUnit",
        key: 'executionUnit',
        width: 300,
        type: 'none',
        isShow: true,
        isFixed: false
      }, {
        title: "账单生成时间",
        dataKey: "executionTime",
        key: 'executionTime',
        width: 300,
        type: 'none',
        isShow: true,
        isFixed: false
      }, {
        title: "表名",
        dataKey: "belongRelTableName",
        key: 'belongRelTableName',
        width: 300,
        type: 'none',
        isShow: true,
        isFixed: false
      }, {
        title: "字段名",
        dataKey: "belongRelTableField",
        key: 'belongRelTableField',
        width: 300,
        type: 'none',
        isShow: true,
        isFixed: false
      }, {
        title: "账单粒度",
        dataKey: "granularity",
        key: 'granularity',
        width: 300,
        type: 'none',
        isShow: true,
        isFixed: false
      }
    ]
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
export default programme
