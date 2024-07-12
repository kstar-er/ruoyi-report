<!--  RE图-->
<template>
  <div class="fangan">
    <div class="settings">
      配置
      <el-button @click="undo">
        撤销
      </el-button>
      <el-button @click="save">
        转化为json
      </el-button>
    </div>
    <div class="container" style="width: 84%; min-height: 1000px">
      <div id="container" />
      <div class="contextmenu-content">
        <div class="list">
          <div
            v-for="item in menuOptions"
            :key="item.text"
            class="item"
            @click="item.cb"
          >
            {{ item.text }}
          </div>
        </div>
      </div>
    </div>
    <selectDialogTable
      v-if="selectTableData.isShow"
      ref="dialogSearch"
      :need-selection="selectTableData.useChoose"
      :use-choose="selectTableData.useChoose"
      :append-table-style="{
        background: '#126f9e',
        color: '#fff',
        borderColor: 'rgba(192, 192, 192,.5)'
      }"
      :width="'60%'"
      :search-input-placeholder="selectTableData.searchPlaceholde"
      :is-show="selectTableData.isShow"
      :data-source="selectTableData.dataSource"
      :title="'选择数据'"
      is-show-search-input
      :loading="loading"
      @close-dialog="selectTableData.isShow = false"
      @do-dialog-search="selectTableData.doSearch"
      @choose-row="selectTableData.chooseRow"
      @submit="selectTableData.submit"
    />
  </div>
</template>

<script setup>
import {
  onMounted,
  ref,
  onBeforeMount,
  reactive,
  getCurrentInstance
} from "vue"
import selectDialogTable from "@/components/public/selectDialogTable.vue"
import { Graph, Shape } from "@antv/x6"
import { Selection } from "@antv/x6-plugin-selection"
import { DataSource, loading } from "./utils/structure"
const { proxy } = getCurrentInstance()
const dataSource = ref(null)

const initDataSource = () => {
  dataSource.value = new DataSource({
    modules: "structure",
    selectUri: "/colorful-fog/orderTable/select",
    pageSize: 100000
  })

  let keyFilter = []

  dataSource.value.initData(this).then(res => {
    let data = {
      nodes: [],
      edges: []
    }
    data.nodes = dataSource.value.tableData.map((item, index) => {
      let ports = !item.fieldList
        ? [{
          id: item.orderTable + "-" + "none",
          group: "list",
          attrs: {
            portNameLabel: {
              text: "none"
            },
            portTypeLabel: {
              text: ""
            }
          }
        }]
        : item.fieldList.map(field => {
          return {
            id: item.orderTable + "-" + field,
            group: "list",
            attrs: {
              portNameLabel: {
                text: field
              },
              portTypeLabel: {
                text: ""
              }
            }
          }
        })

      if (item.foreignKeyList) {
        item.foreignKeyList.forEach(foreign => {
          if (
            !keyFilter.includes(
              foreign.tableName + "-" + foreign.relTableForeignKey
            )
          ) {
            keyFilter.push(foreign.tableName + "-" + foreign.relTableForeignKey)
            keyFilter.push(foreign.relTableForeignKey + "-" + foreign.tableName)
            data.edges.push({
              attrs: {
                line: {
                  stroke: "#a2b1c3" // 指定 path 元素的填充色
                }
              },
              source: {
                cell: foreign.tableName,
                port: foreign.tableName + "-" + foreign.relTableForeignKey
              },
              target: {
                cell: foreign.foreignTableName,
                port: foreign.foreignTableName + "-" + foreign.foreignKey
              }
            })
          }
        })
      }

      return {
        id: item.orderTable, // String，可选，节点的唯一标识
        shape: "er-rect", // 使用 rect 渲染
        label: item.orderTableName ?? item.orderTable, // String，节点标签
        x: 50 + 10 * index, // Number，必选，节点位置的 x 值
        y: 50 + 30 * index, // Number，必选，节点位置的 y 值
        width: 260, // Number，可选，节点大小的 width 值
        height: 34, // Number，可选，节点大小的 height 值
        ports
      }
    })

    init(data)
  })
}

const graph = ref(null)

const init = data => {
  const LINE_HEIGHT = 34
  const NODE_WIDTH = 260

  Graph.registerPortLayout(
    "erPortPosition",
    portsPositionArgs => {
      return portsPositionArgs.map((_, index) => {
        return {
          position: {
            x: 0,
            y: (index + 1) * LINE_HEIGHT
          },
          angle: 0
        }
      })
    },
    true
  )
  Graph.registerNode(
    "er-rect",
    {
      inherit: "rect",
      markup: [{
        tagName: "rect",
        selector: "body"
      },
      {
        tagName: "text",
        selector: "label"
      }],
      attrs: {
        rect: {
          strokeWidth: 1,
          stroke: "#5F95FF",
          fill: "#5F95FF"
        },
        label: {
          fontWeight: "bold",
          fill: "#ffffff",
          fontSize: 14
        }
      },
      ports: {
        groups: {
          list: {
            markup: [{
              tagName: "rect",
              selector: "portBody"
            },
            {
              tagName: "text",
              selector: "portNameLabel"
            },
            {
              tagName: "text",
              selector: "portTypeLabel"
            }],
            attrs: {
              portBody: {
                width: NODE_WIDTH,
                height: LINE_HEIGHT,
                strokeWidth: 1,
                stroke: "#5F95FF",
                fill: "#EFF4FF",
                magnet: true
              },
              portNameLabel: {
                ref: "portBody",
                refX: 12,
                refY: 12,
                fontSize: 12
              },
              portTypeLabel: {
                ref: "portBody",
                refX: 95,
                refY: 12,
                fontSize: 12
              }
            },
            position: "erPortPosition"
          }
        }
      }
    },
    true
  )

  graph.value = new Graph({
    container: document.getElementById("container"),

    panning: {
      enabled: true,
      modifiers: "ctrl"
    },
    scroller: {
      enabled: true,
      pannable: true,
      pageVisible: true,
      pageBreak: false
    },
    mousewheel: {
      enabled: true,
      modifiers: ["ctrl", "meta"]
    },
    grid: {
      visible: true,
      size: 10 // 网格大小 10px
    },
    width: "80%",
    height: 1000,
    background: {
      color: "#ffffff" // 设置画布背景颜色
    },
    connecting: {
      allowBlank: false,
      snap: true,
      allowNode: false,
      router: {
        name: "er",
        args: {
          offset: 25,
          direction: "H"
        }
      },
      createEdge() {
        return new Shape.Edge({
          attrs: {
            line: {
              stroke: "#A2B1C3",
              strokeWidth: 2
            }
          }
        })
      }
    }
  })

  graph.value.use(
    new Selection({
      enabled: true,
      rubberband: true, // 启用框选
      showNodeSelectionBox: true,
      showEdgeSelectionBox: true,
      pointerEvents: `none`
    })
  )

  graph.value.fromJSON(data)

  graph.value.on("edge:click", ({ e, x, y, edge, view }) => {
    contextMenu.style.display = "none"
    contextMenu.style.display = "block"
    contextMenu.style.left = x + "px"
    contextMenu.style.top = y + "px"
    editEdge = edge
  })

  graph.value.on("node:click", ({ e, x, y, node, view }) => {
    contextMenu.style.display = "none"
    contextMenu.style.display = "block"
    contextMenu.style.left = x + "px"
    contextMenu.style.top = y + "px"
    editNode = node
  })

  graph.value.on("blank:click", ({ e, x, y, edge, view }) => {
    contextMenu.style.display = "none"
  })

  graph.value.on("edge:connected", ({ e, x, y, edge, view }) => {
    if (edge.store.data.source.port.indexOf("none") !== -1) {
      proxy.$message.error("不支持添加空白外键！")
      edge.remove()
      return false
    }
  })

  // graph.value.on('cell:click', ({ e, x, y, cell, view }) => {
  //   console.log(`edge:connected`)
  //   console.log(cell)

  graph.value.on("cell:mouseenter", ({ cell }) => {
    if (cell.isNode()) {
      cell.addTools([{
        name: "boundary",
        args: {
          attrs: {
            fill: "#7c68fc",
            stroke: "#feb663",
            "stroke-width": 1,
            "fill-opacity": 0.2
          }
        }
      }])
    } else {
      cell.removeTools()
    }
  })

  graph.value.on("cell:mouseleave", ({ cell }) => {
    cell.removeTools()
  })
}

let editEdge = null
let editNode = null
let contextMenu = null

const undo = () => {
  // graph.value.undo()
}

const romoveEdig = () => {
  contextMenu.style.display = "none"
  editEdge.remove()
}

const addForeign = () => {
  contextMenu.style.display = "none"

  openSelectDialog("addForeign", { tableName: editNode.id })
}

const save = () => {
  console.log(graph.value.toJSON())
}

const menuOptions = reactive([{
  cb: romoveEdig,
  text: "删除"
},
{
  cb: addForeign,
  text: "添加外键"
}])

const selectTableData = reactive({
  isShow: false,
  dataSource: null,
  searchPlaceholde: "",
  doSearch: val => {
    console.log(val)
  },
  chooseRow: val => {
    console.log(val)
  }
})

const openSelectDialog = async (name, { tableName }) => {
  switch (name) {
  case "addForeign": {
    selectTableData.useChoose = false
    selectTableData.searchPlaceholde = "输入数据库名称进行搜索"
    selectTableData.chooseRow = val => {
      if (editNode.port.ports[0].id.indexOf('none') !== -1){
        editNode.removePort(editNode.port.ports[0].id)
      }
      editNode.addPort({
        id: tableName + val.field,
        group: "list",
        attrs: {
          portNameLabel: {
            text: val.field
          }
        }
      })
      selectTableData.isShow = false
    }
    selectTableData.doSearch = val => {
      selectTableData.dataSource.searchData = { tableName }
      if (val) selectTableData.dataSource.searchData.fieldName = val
      selectTableData.dataSource.initData()
    }
    selectTableData.dataSource = new DataSource({
      selectUri: `/colorful-fog/tableFiled/select`,
      tableHeader: [{
        label: "字段名",
        prop: "fieldName",
        width: 200
      },
      {
        label: "字段",
        prop: "field",
        width: 200
      }],
      pageSize: 10
    })
    selectTableData.dataSource.searchData = { tableName }
    selectTableData.dataSource.forMatData = (row, column) => {
      return row[column.property] ?? "-"
    }

    await selectTableData.dataSource.initData()
    selectTableData.isShow = true
    return
  }

  default: {
    return
  }
  }
}

onMounted(() => {
  contextMenu = document.querySelector(".contextmenu-content")
  contextMenu.style.display = "none"
  initDataSource()
})
</script>

<style scoped lang="less">
.fangan {
  display: flex;
}
.settings {
  width: 16%;
}

.container {
  position: relative;
}

.contextmenu-content {
  position: absolute;
  top: 20%;
  left: 20%;
  z-index: 1000000;
  user-select: none;
  display: none;
  font-size: 12px;
  color: rgb(96, 98, 102);
  box-shadow: 0px 4px 11px 4px rgb(221, 221, 221);
  .list {
    border: 1px solid #dadce0;
    min-width: 160px;
    overflow: hidden; /* 处理圆角 */
  }
  .item {
    box-sizing: border-box;
    padding: 0 5px;
    height: 30px;
    line-height: 30px;
    padding-left: 20px;
    word-break: keep-all; /* 很重要，否则会换行 */
    background-color: #fff;
    cursor: default;
  }
  .item:hover {
    background-color: rgb(197, 197, 197);
    color: #000;
  }
}

.my-selecting {
  background: red !important;
  border: 3px solid red;
}
</style>
