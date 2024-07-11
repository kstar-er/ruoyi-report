
const role = {
  namespaced: true,
  state: {
    tableHeader: [
      {
        title: "角色名称",
        dataKey: "roleName",
        key: 'roleName',
        width: 200,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "权限标识",
        dataKey: "roleKey",
        key: 'roleKey',
        width: 200,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "显示顺序",
        dataKey: "roleSort",
        key: 'roleSort',
        width: 200,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "状态",
        dataKey: "status",
        key: 'status',
        width: 200,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "数据权限范围",
        dataKey: "dataScope",
        key: 'dataScope',
        width: 200,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "备注",
        dataKey: "remark",
        key: 'remark',
        width: 200,
        type: 'text',
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
        title: "角色名称",
        dataKey: "roleName",
        key: 'roleName',
        width: 200,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "权限标识",
        dataKey: "roleKey",
        key: 'roleKey',
        width: 200,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "显示顺序",
        dataKey: "roleSort",
        key: 'roleSort',
        width: 200,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "状态",
        dataKey: "status",
        key: 'status',
        width: 200,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "数据权限范围",
        dataKey: "dataScope",
        key: 'dataScope',
        width: 200,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "备注",
        dataKey: "remark",
        key: 'remark',
        width: 200,
        type: 'text',
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
export default role
