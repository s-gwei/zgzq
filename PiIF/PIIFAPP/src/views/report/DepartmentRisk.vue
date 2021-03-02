<template>
  <a-card :bordered="false" :loading="loading">
    <div class="title">部门风险系数</div>
    <!-- 柱状图区域-begin -->
    <div id="container"></div>
    <!-- 气泡图区域-end -->
  </a-card>
</template>

<script>
  import {getAction} from '@/api/manage';
  import DataSet from '@antv/data-set';
  import { Chart } from '@antv/g2';
  const departmentJson = require('@assets/json/departmentJson.json')

  export default {
    name: "departmentRisk",
    components: {
    },
    data() {
      return {
        loading: false,
        description: '',
        data: {},
        average: null,
        scrollProportion: 0,
        url: {
          chartDate:"/HeavyDuty/SectorRiskFactor"
        },
      }
    },
    computed: {
    },
    mounted(){
        this.drawChart()
    },
    methods: {
      renderChart(data){
         var asideWidth = document.querySelector(".ant-layout-sider") && document.querySelector(".ant-layout-sider").offsetWidth || 0,
             width = (document.querySelector("#app").offsetWidth - asideWidth -40)/2 ,
             height = document.querySelector("#app").offsetHeight -160;
        data.map(function(item){
              item["weekLang"] = "第" + item.week + "周"
              if(item.sumBenchmark){
                item["sum"] = item.sumBenchmark
                item["benchmarkRisk"] = item.sumBenchmark
              } else{
                item["cumulativeRisk"] = item.sum
              }
            })
        const dataSorted = data.sort((a, b) => { return a.week - b.week })
            const chart = new Chart({
               container: 'container',
              //  autoFit: true,
              //  forceFit: true,
               height,
               width: 2 * width,
               background: '#fff',
               padding: [40, 40, 30, 45]
            });
            
            chart.source(dataSorted);
            chart.scale({
               ratio: {
                 alias: '工作负载量',
                 nice: true,
                //  max: 5,
                 min: 0
               },
               name: {
                 // type: 'pow',
                 alias: '姓名'
               },
               cumulativeRisk: {
                 alias: '本周完成任务累积风险'
               },
               benchmarkRisk: {
                 alias: '基准风险'
               },
               weekLang: {
                 alias: '周数'
               },
               sum: {
                 alias: '累计风险'
               }
            });
            // 横坐标值设置
            chart.axis('weekLang', {
              title: {
                 offset:-16,
                 textStyle: {
                    fontSize: 12, // 文本大小
                    textAlign: 'center', // 文本对齐方式
                    fill: '#999', // 文本颜色
                    fontWeight: 500
                 },
                 position: "end",
               },
               label: {
                 offset: 16, // 设置坐标轴文本 label 距离坐标轴线的距离
                 textStyle: {
                   textAlign: 'center', // 文本对齐方向，可取值为： start middle end
                   fill: '#999', // 文本的颜色
                   fontSize: '12', // 文本大小
                   // fontWeight: 'bold', // 文本粗细
                   rotate: 0, 
                   textBaseline: 'middle' // 文本基准线，可取 top middle bottom，默认为middle
                 } , // 文本样式，支持回调 
                 autoRotate: false// 是否需要自动旋转，默认为 true
              },
              tickLine: null
            });
            chart.tooltip({
              title: 'weekLang',
              showMarkers: false,
              // showCrosshairs: false, 
            });
           chart.axis('sum', {
              title: {
                offset: -30,
                position: "end",
                rotate: 90,
                autoRotate:false,
                textStyle: {
                    fontSize: 12, // 文本大小
                    textAlign: 'center', // 文本对齐方式
                    fill: '#999', // 文本颜色
                    fontWeight: 500,
                },
              },
              line: {
                    lineWidth: 1, // 设置线的宽度
                    stroke: '#ccc', // 设置线的颜色
                    // lineDash: [ 3, 3 ] // 设置虚线样式
              },
              tickLine: {
                 lineWidth: 1,
                 length: -5,
                 stroke: '#ccc',
                 alignWithLabel:true
              }, 
              label: {
                 offset: 16, // 设置坐标轴文本 label 距离坐标轴线的距离
                 textStyle: {
                   textAlign: 'center', // 文本对齐方向，可取值为： start middle end
                   fill: '#999', // 文本的颜色
                   fontSize: '12', // 文本大小
                   // fontWeight: 'bold', // 文本粗细
                   rotate: 0, 
                   textBaseline: 'middle' // 文本基准线，可取 top middle bottom，默认为middle
                 } , // 文本样式，支持回调 
                 autoRotate: false// 是否需要自动旋转，默认为 true
              },
              grid: {
                  type: 'line',
                  lineStyle: {
                    stroke: '#ccc',
                    lineWidth: 1,
                    // lineDash: [ 4, 0 ],
                    opacity: 0.4
                  },
              }
           });
           chart.axis('ratio', {
              line: {
                    lineWidth: 1, // 设置线的宽度
                    stroke: '#ccc', // 设置线的颜色
                    // lineDash: [ 3, 3 ] // 设置虚线样式
              },
              tickLine: {
                 lineWidth: 1,
                 length: -5,
                 stroke: '#ccc',
                 alignWithLabel:true
              },
              label: {
                 offset: 12, // 设置坐标轴文本 label 距离坐标轴线的距离
                 textStyle: {
                   textAlign: 'center', // 文本对齐方向，可取值为： start middle end
                   fill: '#999', // 文本的颜色
                   fontSize: '12', // 文本大小
                   // fontWeight: 'bold', // 文本粗细
                   rotate: 0, 
                   textBaseline: 'middle' // 文本基准线，可取 top middle bottom，默认为middle
                 } , // 文本样式，支持回调 
                 autoRotate: false// 是否需要自动旋转，默认为 true
              },
              grid: {
                 type: 'line',
                 color: '#fff',
                 lineStyle: {
                   stroke: '#fff',
                   lineWidth: 1,
                   lineDash: [ 4, 2 ]
                 },
                 align: 'center' // 网格顶点从两个刻度中间开始
              },
              title: {
                 offset: -36,
                 position: "end",
                 rotate: 90,
                 autoRotate:false,
                 textStyle: {
                     fontSize: 12, // 文本大小
                     fill: '#999', // 文本颜色
                     fontWeight: 500,
                    //  fontFamily: "STHeitiSC-Medium, STHeitiSC;"
                 }
              }
           });
           chart.tooltip({
             showMarkers: false,
             shared: true,
           });
           chart.legend('name', {
              position: 'top-center',
              itemGap: 20,
              offsetX: width - 120,
              offsetY: -6,
              width: 400,
              marker:'diamond'
           });
         
           chart
             .interval()
             .position('weekLang*sum')
             // .color('name')
             .color('name',function(name){
               if(name.indexOf('基准') > -1){
                 return '#0091FF'
               } else{
                 return '#40D5B3'
               }
             })
             .tooltip('cumulativeRisk*benchmarkRisk')
             .adjust([
                {
                  type: 'dodge',
                  marginRatio: 0
                },
             ]);
           chart
             .line()
             .position('weekLang*ratio')
             .color("#F7B500")
             .shape('smooth')
             .style({
                stroke: '#F7B500',
                lineDash: [8, 4],
                opacity: 0.6
              })
           // 动画
           // chart.interaction('element-highlight');
           chart.render();
      },
       drawChart(){
          this.chart && this.chart.destroy()
          var  url = this.url.chartDate,params={},_this = this,data = JSON.parse(JSON.stringify(departmentJson));
              params.name = this.$route.query.name,params.startTime = this.$route.query.startTime,params.endTime = this.$route.query.endTime,
          // this.renderChart(data)
          // return
          this.loading = true,
         getAction(url,params,'get').then((res) => {
             _this.loading = false
            // console.log(res,'res');
            if(res.success && res.result){
               _this.$nextTick(function(){
                   _this.renderChart(res.result)
               })
            } else {
              _this.$nextTick(function(){
                   _this.renderChart(data)
               })
                
            }
         })
         .catch(function(){
            _this.loading = false
            _this.$nextTick(function(){
                   _this.renderChart(data)
            })
         })
      },
   }
  }
</script>
<style scoped>
   .title{
     font-size: 20px;
     font-family: PingFangSC-Medium, PingFang SC;
     font-weight: 500;
     color: #333;
     margin-left: 45px;
  }
</style>
