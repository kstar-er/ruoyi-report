
const classificationError = {
  namespaced: true,
  state: {
    tableHeader: [
      {
        title: "方案类型",
        dataKey: "billType",
        key: 'billType',
        width: 300,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "方案分类",
        dataKey: "sort",
        key: 'sort',
        width: 300,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "方案名称",
        dataKey: "schemeName",
        key: 'schemeName',
        width: 300,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "分类名称",
        dataKey: "dependName",
        key: 'dependName',
        width: 300,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "分类数据",
        dataKey: "key",
        key: 'key',
        width: 300,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "分类值",
        dataKey: "value",
        key: 'value',
        width: 300,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "分类失败理由",
        dataKey: "reason",
        key: 'reason',
        width: 300,
        type: 'none',
        isShow: true,
        isFixed: false
      }, {
        title: "是否已处理",
        dataKey: "dealFlag",
        key: 'dealFlag',
        width: 300,
        type: 'select',
        isShow: true,
        isFixed: false,
        options: [{
          value: 1,
          label: '是'
        }, {
          value: 0,
          label: '否'
        }]
      }
    ],
    defaultTableHeader: [
      {
        title: "方案类型",
        dataKey: "billType",
        key: 'billType',
        width: 300,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "方案分类",
        dataKey: "sort",
        key: 'sort',
        width: 300,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "方案名称",
        dataKey: "schemeName",
        key: 'schemeName',
        width: 300,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "分类名称",
        dataKey: "dependName",
        key: 'dependName',
        width: 300,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "分类数据",
        dataKey: "key",
        key: 'key',
        width: 300,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "分类值",
        dataKey: "value",
        key: 'value',
        width: 300,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "分类失败理由",
        dataKey: "reason",
        key: 'reason',
        width: 300,
        type: 'none',
        isShow: true,
        isFixed: false
      }, {
        title: "是否已处理",
        dataKey: "dealFlag",
        key: 'dealFlag',
        width: 300,
        type: 'select',
        isShow: true,
        isFixed: false,
        options: [{
          value: 1,
          label: '是'
        }, {
          value: 0,
          label: '否'
        }]
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
export default classificationError
