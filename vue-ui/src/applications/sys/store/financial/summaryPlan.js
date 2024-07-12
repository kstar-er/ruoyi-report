
const summaryPlan = {
  namespaced: true,
  state: {
    tableHeader: [
      {
        title: "汇总计划名称",
        dataKey: "collectSchemeName",
        key: 'collectSchemeName',
        width: 300,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "汇总计划描述",
        dataKey: "collectSchemeDesc",
        key: 'collectSchemeDesc',
        width: 300,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "汇总账单日期始",
        dataKey: "timeFormulaStart",
        key: 'timeFormulaStart',
        width: 300,
        type: 'none',
        isShow: true,
        isFixed: false

      }, {
        title: "汇总账单日期止",
        dataKey: "timeFormulaEnd",
        key: 'timeFormulaEnd',
        width: 300,
        type: 'none',
        isShow: true,
        isFixed: false

      },
      {
        title: "账期",
        dataKey: "costTermFormula",
        key: 'costTermFormula',
        width: 300,
        type: 'none',
        isShow: true,
        isFixed: false
      },
      {
        title: "汇总类型",
        dataKey: "collectObject",
        key: 'collectObject',
        width: 300,
        type: 'none',
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

      }
    ],
    defaultTableHeader: [
      {
        title: "汇总计划名称",
        dataKey: "collectSchemeName",
        key: 'collectSchemeName',
        width: 300,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "汇总计划描述",
        dataKey: "collectSchemeDesc",
        key: 'collectSchemeDesc',
        width: 300,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "汇总账单日期始",
        dataKey: "timeFormulaStart",
        key: 'timeFormulaStart',
        width: 300,
        type: 'none',
        isShow: true,
        isFixed: false

      }, {
        title: "汇总账单日期止",
        dataKey: "timeFormulaEnd",
        key: 'timeFormulaEnd',
        width: 300,
        type: 'none',
        isShow: true,
        isFixed: false

      },
      {
        title: "账期",
        dataKey: "costTermFormula",
        key: 'costTermFormula',
        width: 300,
        type: 'none',
        isShow: true,
        isFixed: false
      },
      {
        title: "汇总类型",
        dataKey: "collectObject",
        key: 'collectObject',
        width: 300,
        type: 'none',
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
export default summaryPlan
