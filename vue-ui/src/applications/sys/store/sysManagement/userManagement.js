
const user = {
  namespaced: true,
  state: {
    tableHeader: [
      {
        title: "用户账号",
        dataKey: "userName",
        key: 'userName',
        width: 300,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "用户昵称",
        dataKey: "nickName",
        key: 'nickName',
        width: 300,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "用户类型",
        dataKey: "userType",
        key: 'userType',
        width: 300,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "用户性别",
        dataKey: "sex",
        key: 'sex',
        width: 300,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "用户邮箱",
        dataKey: "email",
        key: 'email',
        width: 300,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "用户头像",
        dataKey: "avatar",
        key: 'avatar',
        width: 300,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "帐号状态",
        dataKey: "status",
        key: 'status',
        width: 300,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "部门",
        dataKey: "deptName",
        key: 'deptName',
        width: 300,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "备注",
        dataKey: "remark",
        key: 'remark',
        width: 300,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "最后登录IP",
        dataKey: "loginIp",
        key: 'loginIp',
        width: 300,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "最后登录时间",
        dataKey: "loginDate",
        key: 'loginDate',
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
        title: "用户账号",
        dataKey: "userName",
        key: 'userName',
        width: 300,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "用户昵称",
        dataKey: "nickName",
        key: 'nickName',
        width: 300,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "用户类型",
        dataKey: "userType",
        key: 'userType',
        width: 300,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "用户性别",
        dataKey: "sex",
        key: 'sex',
        width: 300,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "用户邮箱",
        dataKey: "email",
        key: 'email',
        width: 300,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "用户头像",
        dataKey: "avatar",
        key: 'avatar',
        width: 300,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "帐号状态",
        dataKey: "status",
        key: 'status',
        width: 300,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "部门",
        dataKey: "deptName",
        key: 'deptName',
        width: 300,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "备注",
        dataKey: "remark",
        key: 'remark',
        width: 300,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "最后登录IP",
        dataKey: "loginIp",
        key: 'loginIp',
        width: 300,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "最后登录时间",
        dataKey: "loginDate",
        key: 'loginDate',
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
export default user
