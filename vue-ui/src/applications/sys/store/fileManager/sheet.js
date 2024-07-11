
const sheet = {
  namespaced: true,
  state: {
    tableHeader: [
      {
        title: "流程状态",
        dataKey: "processStatus",
        key: 'processStatus',
        width: 300,
        type: 'select',
        isShow: true,
        isFixed: false,
        options: [{ value: 'inventory_create',
          label: '制单' },
        { value: 'inventory_ing',
          label: '盘点中' },
        { value: 'inventory_check',
          label: '审核' },
        { value: 'inventory_doc',
          label: '归档' }]
      }, {
        title: "盘点单号",
        dataKey: "inventorySheetOrderCode",
        key: 'inventorySheetOrderCode',
        width: 300,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "盘点日期",
        dataKey: "inventorySheetDate",
        key: 'inventorySheetDate',
        width: 300,
        type: 'date',
        isShow: true,
        isFixed: false
      }, {
        title: "仓库名称",
        dataKey: "warehouseName",
        key: 'warehouseName',
        width: 300,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "盈亏情况",
        dataKey: "profitAndLossQuantitySum",
        key: 'profitAndLossQuantitySum',
        width: 300,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "备注",
        dataKey: "comment",
        key: 'comment',
        width: 300,
        type: 'none',
        isShow: true,
        isFixed: false
      }, {
        title: "创建者",
        dataKey: "createBy",
        key: 'createBy',
        width: 200,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "创建时间",
        dataKey: "createTime",
        key: 'createTime',
        width: 200,
        type: 'date',
        isShow: true,
        isFixed: false
      }, {
        title: "更新者",
        dataKey: "updateBy",
        key: 'updateBy',
        width: 200,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "更新时间",
        dataKey: "updateTime",
        key: 'updateTime',
        width: 200,
        type: 'date',
        isShow: true,
        isFixed: false
      }
    ],
    defaultTableHeader: [
      {
        title: "流程状态",
        dataKey: "processStatus",
        key: 'processStatus',
        width: 300,
        type: 'select',
        isShow: true,
        isFixed: false,
        options: [{ value: 'inventory_create',
          label: '制单' },
        { value: 'inventory_ing',
          label: '盘点中' },
        { value: 'inventory_check',
          label: '审核' },
        { value: 'inventory_doc',
          label: '归档' }]
      }, {
        title: "盘点单号",
        dataKey: "inventorySheetOrderCode",
        key: 'inventorySheetOrderCode',
        width: 300,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "盘点日期",
        dataKey: "inventorySheetDate",
        key: 'inventorySheetDate',
        width: 300,
        type: 'date',
        isShow: true,
        isFixed: false
      }, {
        title: "仓库名称",
        dataKey: "warehouseName",
        key: 'warehouseName',
        width: 300,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "盈亏情况",
        dataKey: "profitAndLossQuantitySum",
        key: 'profitAndLossQuantitySum',
        width: 300,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "备注",
        dataKey: "comment",
        key: 'comment',
        width: 300,
        type: 'none',
        isShow: true,
        isFixed: false
      }, {
        title: "创建者",
        dataKey: "createBy",
        key: 'createBy',
        width: 200,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "创建时间",
        dataKey: "createTime",
        key: 'createTime',
        width: 200,
        type: 'date',
        isShow: true,
        isFixed: false
      }, {
        title: "更新者",
        dataKey: "updateBy",
        key: 'updateBy',
        width: 200,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "更新时间",
        dataKey: "updateTime",
        key: 'updateTime',
        width: 200,
        type: 'date',
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
export default sheet
