<template>
  <div class="pt10 pl10 pr10" style="background: #fff">
    <div class="chart-container">
      <div class="chart-container-left">
        <div class="input-content-header">
          <span>数据展示</span>
        </div>
        <div v-loading="loading && !showSettings" class="charts-content">
          <lineCharts ref="charts" />
        </div>
      </div>
      <div class="chart-container-right">
        <div class="chart-container-right-header">
          <span class="mr10">我的图表</span>
          <el-tooltip
            class="box-item" content="添加新图表"
            placement="bottom"
          >
            <el-button
              icon="Plus"
              circle
              type="primary"
              size="small"
              @click="addNewReport"
            />
          </el-tooltip>
        </div>
        <el-tabs v-model="activeName" class="demo-tabs">
          <el-tab-pane label="我的图表" name="first">
            <div class="settings-content">
              <div class="pane">
                <el-card
                  v-for="(card, index) in list"
                  :key="index"
                  class="box-card mb20"
                >
                  <template #header>
                    <div class="card-header">
                      <div class="flex-center">
                        <span class="com-span" :title="card.title">{{ card.title }}</span>
                        <span
                          class="tag"
                          title="绘制图表"
                          @click="showPic(card)"
                        >{{ "绘制图表" }}</span>
                      </div>
                      <div>
                        <el-button
                          size="small"
                          type="danger"
                          class="ml10"
                          @click="deleteSetting(index)"
                        >
                          删除该配置
                        </el-button>
                      </div>
                    </div>
                  </template>

                  <div class="card-content">
                    <div class="content-item">
                      <div class="com-span">
                        Y 轴：
                      </div>
                      <div style="flex: 1">
                        <el-tag
                          v-for="tag in card.yAxis.list"
                          :key="tag"
                          class="mr10 mb10"
                          :disable-transitions="false"
                          style="cursor: pointer"
                          size="small"
                          type="success"
                        >
                          {{ tag.split("-")[0] }}
                        </el-tag>
                      </div>
                    </div>
                    <div class="content-item">
                      <div class="com-span">
                        X 轴：
                      </div>
                      <div style="flex: 1">
                        <el-tag
                          :disable-transitions="false"
                          style="cursor: pointer"
                          size="small"
                          type="info"
                          class="mr10"
                        >
                          {{
                            card.xAxis.fieldName.split("-")[0]
                          }}
                        </el-tag>
                      </div>
                    </div>
                    <div v-if="card.year" class="content-item">
                      <div class="com-span">
                        统计时间：
                      </div>
                      <div style="flex: 1">
                        <el-tag
                          class="mr10 "
                          :disable-transitions="false"
                          style="cursor: pointer"
                          size="small"
                          type="primary"
                        >
                          {{ card.year + "-" + card.month }}
                        </el-tag>
                      </div>
                    </div>
                    <div class="content-item">
                      <div class="com-span">
                        类型：
                      </div>
                      <div style="flex: 1">
                        <el-tag
                          :disable-transitions="false"
                          style="cursor: pointer"
                          size="small"
                          type="warning"
                          class="mr10 "
                        >
                          {{ card.chartsType.join("-") }}
                        </el-tag>
                      </div>
                    </div>
                    <div v-if="card.filterCriteria.length!==0" class="content-item mb10">
                      <div class="com-span">
                        过滤：
                      </div>
                      <div style="flex: 1">
                        <el-tag
                          v-for="tag in card.filterCriteria"
                          :key="tag"
                          class="mr10 "
                          :disable-transitions="false"
                          style="cursor: pointer"
                          size="small"
                          type="danger"
                        >
                          {{ tag.fieldNames.split("-")[0] + "..." }}
                        </el-tag>
                      </div>
                    </div>
                    <div
                      v-if="card.compare.list.length !== 0"
                      class="content-item"
                    >
                      <div class="com-span">
                        对比曲线：
                      </div>
                      <div style="flex: 1">
                        <el-tag size="small" class="mr10 mb10">
                          {{ card.compare.fieldName.split("-")[0] + "等于" }}
                        </el-tag>
                        <br>
                        <el-tag
                          v-for="tag in card.compare.list"
                          :key="tag"
                          class="mr10 mb10"
                          :disable-transitions="false"
                          style="cursor: pointer"
                          size="small"
                          type="danger"
                        >
                          {{ tag }}
                        </el-tag>
                      </div>
                    </div>
                  </div>
                  <div class="options">
                    <el-button
                      size="small" type="success"
                      @click="exportData(card, index)"
                    >
                      导出excel
                    </el-button>
                    <el-button
                      size="small"
                      type="primary"
                      @click="updateSettings(card, index)"
                    >
                      修改配置
                    </el-button>
                  </div>
                </el-card>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
    <settings
      :is-show="showSettings"
      :default="setting"
      @closeDialog="showSettings = false"
      @saveReportJSON="saveReportJSON"
    />
  </div>
</template>

<script setup>
import { DataSource, loading } from "./utils/report"
import settings from "./components/settings.vue"
import { reactive, ref, onBeforeMount, getCurrentInstance, provide } from "vue"
import lineCharts from "./charts/line.vue"
import { collapseItemProps } from "element-plus"

const { proxy } = getCurrentInstance()
const dataSource = ref(null)

onBeforeMount(() => {
  dataSource.value = new DataSource()
  getSettingList()
})

const getSettingList = () => {
  dataSource.value.getRepoJson().then(res => {
    if (res.code === 200) {
      list.length = 0
      list.push(...JSON.parse(res.data))
    }
  })
}

const randomInteger = max => {
  return Math.floor(Math.random() * (max + 1))
}

const randomRgbColor = () => {
  let r = randomInteger(255)
  let g = randomInteger(255)
  let b = randomInteger(255)
  return [r, g, b]
}

const exportExcel = (series, title) => {
  let header = [`统计数据`, ...series.x.data]
  let data = []
  series.y.forEach(y => {
    data.push([y.name, ...y.data])
  })
  let style = {
    A1: {
      font: { bold: true },
      alignment: { horizontal: "center" },
      fill: { fgColor: { rgb: "FFFF00" } }
    }
  }
  new proxy.$DataTool().exportExcel(header, data, title, style)
}

const activeName = ref("first")

const showSettings = ref(false)
const setting = ref({})

const addNewReport = () => {
  editIndex = -1
  setting.value = {
    sourceInterfaceName: "billMain",
    title: "我的报表",
    chartsType: ["折线图 & 柱状图"],
    year: null,
    timeUnit: null,
    month: null,
    xAxis: {
      fieldName: "",
      resultType: "",
      value: ""
    },
    yAxis: {
      list: []
    },
    filterCriteria: [],
    compare: { list: [], fieldName: "" }
  }
  showSettings.value = true
}

let editIndex = -1

const updateSettings = (item, index) => {
  editIndex = index
  setting.value = {
    ...JSON.parse(JSON.stringify(item))
  }

  showSettings.value = true
}

let list = reactive([])

const handleChartsData = (xaxis, yaxis, data, { title, id, type }) => {
  let resData = Object.entries(data)
  let series = {
    x: {
      name: xaxis,
      data: []
    },
    y: []
  }
  let temp = {}
  let legendData = []
  resData.forEach((item, index) => {
    series.x.data.push(item[0])
    item[1].forEach((i, idx) => {
      let keys = Object.keys(i)[0]
      if (temp.hasOwnProperty(keys)) {
        series.y[temp[keys]].data.push(Number(i[keys].toFixed(2)))
      } else {
        series.y.push({
          name: yaxis[idx].split("-")[0],
          data: [],
          ...proxy.$refs.charts.defaultSeriesOptions(
            randomRgbColor(),
            yaxis[idx].split("-")[0]
          )
        })
        legendData.push(yaxis[idx].split("-")[0])
        temp[keys] = idx
        series.y[temp[keys]].data.push(Number(i[keys].toFixed(2)))

      }
    })
  })
  if (type === "export") {
    exportExcel(series, title)
    return
  }
  proxy.$refs.charts.initLineOptions(series, title, legendData, id)
  return series
}

const handleChartsDataFromDate = (
  xaxis,
  yaxis,
  data,
  { timeUnit, year, month, title, id, type }
) => {
  let date = Object.keys(data).sort((a, b) => {
    if (a.indexOf("-") !== -1) {
      return +a.split("-")[1] - +b.split("-")[1]
    } else {
      return +a - +b
    }
  })
  let series = {
    x: {
      name: xaxis,
      data: []
    },
    y: []
  }
  let temp = {}
  let legendData = []
  date.forEach(item => {
    if (timeUnit === "天") series.x.data.push(year + "-" + month + "-" + item)
    else series.x.data.push(year + "-" + item)
    data[item].forEach((i, idx) => {
      let keys = Object.keys(i)[0]
      if (temp.hasOwnProperty(keys)) {
        series.y[temp[keys]].data.push(Number(i[keys].toFixed(2)))
      } else {
        series.y.push({
          name: yaxis[idx].split("-")[0],
          data: [],
          ...proxy.$refs.charts.defaultSeriesOptions(
            randomRgbColor(),
            yaxis[idx].split("-")[0]
          )
        })
        legendData.push(yaxis[idx].split("-")[0])
        temp[keys] = idx
        series.y[temp[keys]].data.push(Number(i[keys].toFixed(2)))
      }
      return
    })
  })
  if (type === "export") {
    exportExcel(series, title)
    return
  }
  proxy.$refs.charts.initLineOptions(series, title, legendData, id)
  return series
}

const showPicFromCompare = async (item, type) => {
  let params = {
    sourceValue: item.sourceValue,
    sourceInterfaceName: item.sourceInterfaceName,
    yaxis: item.yAxis.list.map(item => item.split("-")[1]),
    xaxis: item.xAxis.fieldName.split("-")[1],
    xaxisIsTime: item.xAxis.resultType === "DATE"
  }
  if (item.year) params.year = item.year
  if (item.month) params.month = item.month
  if (item.timeUnit) params.timeUnit = item.timeUnit
  let seriesData = []
  for (let i = 0; i < item.compare.list.length; i++) {
    let filterCriteria = [{
      fieldName: item.compare.fieldName.split("-")[1],
      value: [item.compare.list[i]],
      operator: "等于"
    }]
    params.filterCriteria = filterCriteria

    await dataSource.value.getReportData(params).then(res => {
      if (res.code === 200) {
        seriesData.push(res.data)
      }
    })
  }
  if (seriesData.length === item.compare.list.length) {
    let date = Object.keys(seriesData[0]).sort((a, b) => {
      if (a.indexOf("-") !== -1) {
        return +a.split("-")[1] - +b.split("-")[1]
      } else {
        return +a - +b
      }
    })
    let series = {
      x: {
        name: item.xAxis.fieldName.split("-")[0],
        data: []
      },
      y: []
    }
    let legendData = []
    item.compare.list.forEach(compare => {
      item.yAxis.list.forEach(y => {
        let name = compare + "-" + y.split("-")[0]
        series.y.push({
          name,
          data: [],
          ...proxy.$refs.charts.defaultSeriesOptions(randomRgbColor(), name)
        })
        legendData.push(name)
      })
    })
    let temp = Array.from(
      { length: item.compare.list.length * item.yAxis.list.length },
      (v, i) => {
        return i === 0
          ? {
            isPush: true
          }
          : {
            isPush: false
          }
      }
    )
    date.forEach(j => {
      if (item.timeUnit === "天")
        series.x.data.push(item.year + "-" + item.month + "-" + j)
      else series.x.data.push(item.year + "-" + j)
      seriesData.forEach(dataObj => {
        dataObj[j].forEach((data, index) => {
          let key = Object.keys(data)[0]
          let pushIndex = temp.findIndex(i => i.isPush)
          series.y[pushIndex].data.push(Number(data[key].toFixed(2)))
          temp[pushIndex].isPush = false
          if (temp[pushIndex + 1]) {
            temp[pushIndex + 1].isPush = true
          } else {
            temp[0].isPush = true
          }
        })
      })
      return
    })
    if (type === "export") {
      exportExcel(series, title)
      return
    }
    proxy.$refs.charts.initLineOptions(series, item.title, legendData, item.id)
  }
}

const showPic = item => {
  proxy.$refs.charts.disposeLine(item.id)
  if (item.compare.list.length !== 0) {
    showPicFromCompare(item)
    return
  }
  let params = {
    sourceValue: item.sourceValue,
    sourceInterfaceName: item.sourceInterfaceName,
    yaxis: item.yAxis.list.map(item => item.split("-")[1]),
    xaxis: item.xAxis.fieldName.split("-")[1],
    filterCriteria: item.filterCriteria,
    xaxisIsTime: item.xAxis.resultType === "DATE"
  }
  if (item.year) params.year = item.year
  if (item.month) params.month = item.month
  if (item.timeUnit) params.timeUnit = item.timeUnit
  dataSource.value.getReportData(params).then(res => {
    if (res.code === 200) {
      if (Object.keys(res.data).length === 0) {
        proxy.$message.error("获取数据失败，请检查配置或源数据")
        return
      }
      if (item.xAxis.resultType === "DATE")
        handleChartsDataFromDate(
          item.xAxis.fieldName.split("-")[0],
          item.yAxis.list,
          res.data,
          {
            timeUnit: item.timeUnit,
            year: item.year,
            month: item.month,
            title: item.title,
            id: item.id
          }
        )
      else
        handleChartsData(
          item.xAxis.fieldName.split("-")[0],
          item.yAxis.list,
          res.data,
          { title: item.title, id: item.id }
        )
    }
  })
}

const exportData = item => {

  if (item.compare.list.length !== 0) {
    showPicFromCompare(item, 'export')
    return
  }

  let params = {
    sourceValue: item.sourceValue,
    sourceInterfaceName: item.sourceInterfaceName,
    yaxis: item.yAxis.list.map(item => item.split("-")[1]),
    xaxis: item.xAxis.fieldName.split("-")[1],
    filterCriteria: item.filterCriteria,
    xaxisIsTime: item.xAxis.resultType === "DATE"
  }
  if (item.year) params.year = item.year
  if (item.month) params.month = item.month
  if (item.timeUnit) params.timeUnit = item.timeUnit
  dataSource.value.getReportData(params).then(res => {
    if (res.code === 200) {
      if (Object.keys(res.data).length === 0) {
        proxy.$message.error("获取数据失败，请检查配置或源数据")
        return
      }

      if (item.xAxis.resultType === "DATE")
        handleChartsDataFromDate(
          item.xAxis.fieldName.split("-")[0],
          item.yAxis.list,
          res.data,
          {
            timeUnit: item.timeUnit,
            year: item.year,
            month: item.month,
            title: item.title,
            id: item.id,
            type: "export"
          }
        )
      else
        handleChartsData(
          item.xAxis.fieldName.split("-")[0],
          item.yAxis.list,
          res.data,
          { title: item.title, id: item.id, type: "export" }
        )
    }
  })
}

const saveReportJSON = params => {

  if (editIndex !== -1) list.splice(editIndex, 1, params)
  else list.push(params)
  let reportJSON = JSON.stringify(list)
  dataSource.value
    .saveReportJson({
      chartJSON: reportJSON
    })
    .then(res => {
      if (res.code === 200) {
        proxy.$message.success("配置保存成功，请重新绘制数据")
        showSettings.value = false
      }
    })
}

const deleteSetting = index => {
  proxy.$alert("是否确认删除此报表配置，删除后无法恢复", "提示", {
    type: "info",
    icon: "InfoFilled",
    showCancelButton: true,
    cancelButtonText: "再想想",
    confirmButtonText: "确定",
    callback: action => {
      if (action === "confirm") {
        list.splice(index, 1)
        let reportJSON = JSON.stringify(list)
        dataSource.value
          .saveReportJson({
            chartJSON: reportJSON
          })
          .then(res => {
            if (res.code === 200) {
              proxy.$message.success("删除成功")
              showSettings.value = false
            }
          })
      }
    }
  })
}

provide("handleChartsData", handleChartsData)
provide("handleChartsDataFromDate", handleChartsDataFromDate)
</script>

<style lang="less" scoped>
.chart-container {
  position: relative;
  display: flex;
  .input-content-header {
    background: rgb(233, 235, 236);
    border-bottom: 1px dashed rgb(222, 225, 230);
    color: #000;
    padding: 14px 16px;
    box-sizing: border-box;
    font-size: 14px;
    font-weight: 800;
    letter-spacing: 2px;
  }
  .chart-container-left {
    flex: 1;
  }
  .chart-container-right {
    width: 20vw;
    .chart-container-right-header {
      background: rgb(31, 36, 48);
      color: #fff;
      padding: 12px 16px;
      box-sizing: border-box;
      font-size: 14px;
      font-weight: 800;
      letter-spacing: 2px;
    }
  }
}

:deep(#tab-first) {
  padding-left: 10px;
}
:deep(#tab-fourth) {
  padding-right: 10px;
}
.com-span {
  display: inline-block;
  padding: 8px 0;
  font-size: 14px;
  color: rgb(96, 98, 102);
  max-width: 100px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  cursor: default;
}
.pane {
  padding: 10px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

:deep(.el-card__header) {
  padding: 10px 16px;
}

:deep(.el-card__body) {
  overflow: hidden;
  padding: 10px 20px 10px 20px;
}
:deep(.el-checkbox--small) {
  margin-right: 10px;
}
.options {
  margin-top: 10px;
  border-top: 1px dashed rgb(222, 225, 230);
  padding-top: 10px;
  display: flex;
  justify-content: flex-end;
}

.content-item {
  display: flex;
  padding: 0 2px;
  align-items: center;
  .com-span {
    width: 80px;
  }
}
.tag {
  cursor: pointer;
  padding: 2px 10px;
  border-radius: 4px;
  font-size: 12px;
  color: #fff;
  background: linear-gradient(to right, #f82fee, #f18226);
  margin-left: 10px;
}
</style>
