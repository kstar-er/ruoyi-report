
const menu = {
  namespaced: true,
  state: {
    tableHeader: [
      {
        title: "文案标识",
        dataKey: "menuName",
        key: 'menuName',
        width: 250,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "权限标识",
        dataKey: "perms",
        key: 'perms',
        width: 300,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "权限类型",
        dataKey: "menuType",
        key: 'menuType',
        width: 150,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "路由地址",
        dataKey: "path",
        key: 'path',
        width: 300,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "组件地址",
        dataKey: "component",
        key: 'component',
        width: 300,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "图标",
        dataKey: "icon",
        key: 'icon',
        width: 150,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "是否外链",
        dataKey: "isFrame",
        key: 'isFrame',
        width: 150,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "是否隐藏",
        dataKey: "visible",
        key: 'visible',
        width: 150,
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
        title: "创建者",
        dataKey: "createBy",
        key: 'createBy',
        width: 150,
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
        width: 150,
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
        title: "文案标识",
        dataKey: "menuName",
        key: 'menuName',
        width: 250,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "权限标识",
        dataKey: "perms",
        key: 'perms',
        width: 300,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "权限类型",
        dataKey: "menuType",
        key: 'menuType',
        width: 150,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "路由地址",
        dataKey: "path",
        key: 'path',
        width: 300,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "组件地址",
        dataKey: "component",
        key: 'component',
        width: 300,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "图标",
        dataKey: "icon",
        key: 'icon',
        width: 150,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "是否外链",
        dataKey: "isFrame",
        key: 'isFrame',
        width: 150,
        type: 'text',
        isShow: true,
        isFixed: false
      }, {
        title: "是否隐藏",
        dataKey: "visible",
        key: 'visible',
        width: 150,
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
        title: "创建者",
        dataKey: "createBy",
        key: 'createBy',
        width: 150,
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
        width: 150,
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
export default menu
