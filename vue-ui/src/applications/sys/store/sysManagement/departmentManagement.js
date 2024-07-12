
const depart = {
  namespaced: true,
  state: {
    tableHeader: [
      {
        title: "部门名称",
        dataKey: "deptName",
        key: 'deptName',
        width: 300,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "部门负责人",
        dataKey: "leader",
        key: 'leader',
        width: 300,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "负责人联系电话",
        dataKey: "phone",
        key: 'phone',
        width: 300,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "邮箱",
        dataKey: "email",
        key: 'email',
        width: 300,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "状态",
        dataKey: "status",
        key: 'status',
        width: 300,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "创建者",
        dataKey: "createBy",
        key: 'createBy',
        width: 300,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "创建时间",
        dataKey: "createTime",
        key: 'createTime',
        width: 300,
        type: 'date',
        isShow: true,
        isFixed: false
      }, {
        title: "更新者",
        dataKey: "updateBy",
        key: 'updateBy',
        width: 300,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "更新时间",
        dataKey: "updateTime",
        key: 'updateTime',
        width: 300,
        type: 'date',
        isShow: true,
        isFixed: false
      }
    ],
    defaultTableHeader: [
      {
        title: "部门名称",
        dataKey: "deptName",
        key: 'deptName',
        width: 300,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "部门负责人",
        dataKey: "leader",
        key: 'leader',
        width: 300,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "负责人联系电话",
        dataKey: "phone",
        key: 'phone',
        width: 300,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "邮箱",
        dataKey: "email",
        key: 'email',
        width: 300,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "状态",
        dataKey: "status",
        key: 'status',
        width: 300,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "创建者",
        dataKey: "createBy",
        key: 'createBy',
        width: 300,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "创建时间",
        dataKey: "createTime",
        key: 'createTime',
        width: 300,
        type: 'date',
        isShow: true,
        isFixed: false
      }, {
        title: "更新者",
        dataKey: "updateBy",
        key: 'updateBy',
        width: 300,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "更新时间",
        dataKey: "updateTime",
        key: 'updateTime',
        width: 300,
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
export default depart
