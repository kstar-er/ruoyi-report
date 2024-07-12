<template>
  <div>
    <div
      v-for="item in lineList"
      :id="item"
      :key="item"
      style="width:66vw;height:430px;"
      class="pt10"
    />
    <div
      v-if="lineList.length===0" class="flex-center mt50"
      style="color:#999"
    >
      暂 无 数 据
    </div>
  </div>
</template>

<script setup>
import { nextTick, onMounted, reactive, ref } from 'vue'
import * as echarts from 'echarts'
let line = null
let lineList = reactive([])
let lineContexts = []
const defaultLineOptions = {
  title: {
    text: '',
    top: 5,
    left: 5,
    textStyle: {
      fontSize: 16,
      fontWeight: 'bold'
    }

  },
  tooltip: {
    trigger: 'axis',
    axisPointer: {
      type: 'cross',
      crossStyle: {
        color: '#666'
      }
    },
    textStyle: {
      fontSize: 12
    }
  },
  grid: { top: 80, left: 100 },
  legend: {
    align: 'left',
    right: '30px',
    type: 'plain',
    top: '24px',
    itemHeight: 10,
    textStyle: {
      color: '#666',
      fontSize: 11
    },
    lineStyle: {
      width: 2
    },
    tooltip: {
      show: true
    },
    itemGap: 10,
    itemWidth: 18,
    data: []
  },
  xAxis: {
    name: "日期",
    type: 'category',
    data: [
      '商家1', '商家2', '商家3', '商家4', '商家5', '商家6', '商家7', '商家8', '商家9'
    ],
    axisLine: {
      show: true,
      lineStyle: {
        color: "rgba(171, 51, 79,1)" //X轴字体颜色
      }

    },
    label: {

      formatter(params) {
        return (
          params.value
        )
      }
    }

  },
  toolbox: {
    top: 24, left: 4,
    feature: {
      saveAsImage: {},
      magicType: {
        type: ["line", "bar"]
      }

    }
  },
  yAxis: {
    name: '数量(套/件)'
  },
  series: []
}

const defaultSeriesOptions = (color, name) => {
  return {
    name,
    type: 'bar',
    symbol: 'circle', // 默认是空心圆（中间是白色的），改成实心圆
    showAllSymbol: true,
    smooth: true,
    data: [],
    color: 'rgba(' + color.join(',') + ',.5)',
    symbolSize: 6,
    lineStyle: {
      width: 1.5,
      color: 'rgba(' + color.join(',') + ')' //线条样式
    },
    markPoint: {
      data: [{ type: 'max', name: 'Max' },
        { type: 'min', name: 'Min' }],
      symbolSize: 30
    },
    markLine: {
      data: [{ type: 'average', name: 'Avg', label: {
        formatter: (params) => {
          let val = Math.round(params.value)
          return `平均 ` + val
        }
      } }]
    },
    areaStyle: {
    //区域填充样式

      //线性渐变，前4个参数分别是x0,y0,x2,y2(范围0~1);相当于图形包围盒中的百分比。如果最后一个参数是‘true’，则该四个值是绝对像素位置。
      color: new echarts.graphic.LinearGradient(0, 0, 0, 1,
        [{
          offset: 0,
          color: 'rgba(' + color.join(',') + ',0.1)'
        },
        {
          offset: 1,
          color: 'rgba(' + color.join(',') + ',0)'
        }],
        false
      ),
      shadowColor: 'rgba(' + color.join(',') + ',1)', //阴影颜色
      shadowBlur: 20 //shadowBlur设图形阴影的模糊大小。配合shadowColor,shadowOffsetX/Y, 设置图形的阴影效果。
    }

  }
}

onMounted(() => {
  //
})

const initLine = ({ id, lineContext }) => {

  lineList.push(id)
  lineContexts.push(null)
  nextTick(() => {
    if (lineContext) line.dispose()
    let chartEle = document.getElementById(id)
    chartEle?.removeAttribute('_echarts_instance_') // 移除容器标签的 '_echarts_instance_' 实例属性，使用echarts.init重新生成一个新的实例ID
    lineContexts[lineContexts.length - 1] = echarts.init(chartEle)
    lineContexts[lineContexts.length - 1].setOption(defaultLineOptions)
  })

}

const initLineOptions = (series, title, legendData = [], id) => {
  defaultLineOptions.xAxis.name = series.x.name
  defaultLineOptions.xAxis.data = series.x.data
  defaultLineOptions.series = series.y
  defaultLineOptions.title.text = title
  defaultLineOptions.legend.data = legendData

  initLine({
    id,
    lineContext: null
  })

}

const disposeLine = (id) => {
  let index = lineList.findIndex(line => line === id)
  if (index === -1) return

  lineContexts[index].dispose()
  lineList.splice(index, 1)
  lineContexts.splice(index, 1)

}

defineExpose({
  initLineOptions,
  defaultSeriesOptions,
  disposeLine
})
</script>