<template>
  <div :id="id" :class="className" :style="{height:height,width:width}" />
</template>

<script>
  import echarts from 'echarts'
  import resize from '@/mixins/resize'

  export default {
    mixins: [resize],
    props: {
      className: {
        type: String,
        default: 'chart'
      },
      id: {
        type: String,
        default: 'chart'
      },
      width: {
        type: String,
        default: '100%'
      },
      height: {
        type: String,
        default: '500px'
      },
      chartsData: {}
    },
    watch: {
      chartsData: {
        handler (nValue,oValue){
          console.log(nValue)
          this.chartsData = nValue
          this.initChart()
        }
      }
    },
    data() {
      return {
        chart: null
      }
    },
    mounted() {
      this.initChart()
    },
    beforeDestroy() {
      if (!this.chart) {
        return
      }
      this.chart.dispose()
      this.chart = null
    },
    methods: {
      initChart() {
        this.chart = echarts.init(document.getElementById(this.id))

        this.chart.setOption({
          tooltip: {
            // show：'true',//默认：true；是否显示提示框组件，包括提示框浮层和 axisPointer。
            trigger: 'item',//默认：item；触发类型。item：数据项图形触发，主要在散点图，饼图等无类目轴的图表中使用。'axis'：坐标轴触发，主要在柱状图，折线图等会使用类目轴的图表中使用。'none':什么都不触发。
            triggerOn: 'mousemove'
          },
          series:[
            {
              type: 'tree',

              data: [this.chartsData],

              left: '10%',
              right: '10%',
              top: '10%',
              bottom: '10%',

              symbol: 'diamond',
              symbolSize: '15',
              itemStyle: {
                color: '#ffffff',
                borderColor: '#67BC4A'
              },
              lineStyle: {
                color: '#67BC4A'
              },
              orient: 'horizontal',

              expandAndCollapse: true,//默认：true；子树折叠和展开的交互，默认打开 。

              initialTreeDepth:2,//默认：2，树图初始展开的层级（深度）。根节点是第 0 层，然后是第 1 层、第 2 层，... ，直到叶子节点

              label: {
                normal: {
                  position: 'bottom',//标签的位置。
                  verticalAlign: 'top',//文字垂直对齐方式，默认自动。
                  align: 'center',//文字水平对齐方式，默认自动。
                  fontSize: 15,//文字的字体大小
                }
              },

              leaves: {
                label: {
                  normal: {
                    position: 'bottom',
                    verticalAlign: 'top',
                    align: 'center',
                    fontSize: 12,//文字的字体大小
                  }
                }
              },

              animationDurationUpdate: 750

            }
          ]
        })
      }
    }
  }
</script>