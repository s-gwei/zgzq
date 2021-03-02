<template>
  <a-card :bordered="false" style="height: 100%;background: #fff" :loading="loading">
    <div class="title"><p class="tilcontent">部门风险系数</p><a-button type="dashed" icon="download" @click="save" id="test">保存</a-button></div>
    <!-- 气泡图区域-begin -->
    <div class="chart">
      <div class="chart-demo-wrap">
        <div id="container"></div>
        <div class="scrollBarWrap">
          <ScrollBar ref="scrollBar" @updatePosRate="throttleUpdateDs" />
        </div>
      </div>
    </div>
    <!-- 气泡图区域-end -->
    <div class="noData" v-if="!chartDateALL || !chartDateALL.length" :style="{height: adaptiveContainH +'px',lineHeight: adaptiveContainH +'px'}">
        暂无数据
    </div>
    <a-alert
      v-if="visible"
      message="Error"
      :description="descriptionError"
      type="error"
      show-icon
      closable
      :after-close="closeAlert"
    />
  </a-card>
</template>

<script>
import {getAction} from '@/api/manage';
import DataSet from '@antv/data-set';
import { Chart } from '@antv/g2';
import ScrollBar from "./ScrollBar"
import throttle from '@/mixins/throttle'
import html2canvas from 'html2canvas'
const departmentJson = require('@assets/json/departmentJson.json')
const BASE_LEN = 32 //默认显示数据长度
// const REFRESH_INTERVAL = 1000000 //时长

export default {
  components: {
    ScrollBar
  },
  data () {
    return {
      data: {},
      visible: false,
      loading: false,
      average: null,
      chartDateALL: [],
      departmentJson,
      url: {
          // chartDate:"/HeavyDuty/SectorRiskFactor"
          // new
          chartDate: "/HeavyDutyTable/SectorRiskFactorTable"
      },
    }
  },
  created () {
    // 不需要与视图交互的变量都定义在created这里
    this.refreshTimer = null
    this.scrollProportion = 0
    this.chart = null
    this.chartView = null
    this.ds = null
    // 获取数据
    this.$nextTick(function () {
    })
  },
  mounted(){
    this.getDataRealTime()
    window.onresize = () => {
          return (() => {
            if(this.chartDateALL && this.chartDateALL.length){
               document.querySelector("#container") && document.querySelector("#container>div")&& document.querySelector("#container>div").remove()
               this.renderChart(this.chartDateALL)
            }
          })();
     };
  },
  beforeDestroy () {
    document.querySelector("#container") && document.querySelector("#container>div") && document.querySelector("#container>div").remove()
    if (this.refreshTimer) {
      clearTimeout(this.refreshTimer)
    }
  },
  computed:{
    adaptiveContainH(){
       const height = document.querySelector("#app").offsetHeight ,heightTop=document.querySelector('.header') && document.querySelector('header').offsetHeight || 60,adaptiveH = height - heightTop -106;
      //  console.log(height,heightTop,adaptiveH);
      return adaptiveH
    }

  },
  methods: {
    closeAlert(){
        this.visible = false
    },
    getDataRealTime () {
      this.getData()
      // if (this.refreshTimer) {
      //   clearTimeout(this.refreshTimer)
      // }
      // this.refreshTimer = setTimeout(() => {
      //   // 重新设置定时器
      //   this.getDataRealTime()
      // }, REFRESH_INTERVAL)
    },
    getData () {
          var  url = this.url.chartDate,params={},
              _this = this,
              data = JSON.parse(JSON.stringify(departmentJson));
              params.userIds = this.$route.query.userIds,params.startTime = this.$route.query.startTime,params.endTime = this.$route.query.endTime,params.name=this.$route.query.name
          this.loading = true,
      getAction(url,params,'get').then((res) => {
             _this.loading = false
            if(res.success && res.result){
               _this.$nextTick(function(){
                   _this.$set(_this,"chartDateALL",res.result)
                   if (res.result && res.result.length) {
                      _this.renderChart(res.result)
                   }
               })
            } else {
                _this.visible = true
                _this.descriptionError = res.message
                setTimeout(function(){
                  _this.visible = false
                },3000)
                // _this.$nextTick(function(){
                //     _this.$set(_this,"chartDateALL",data)
                //      _this.renderChart(data)
                //  })
            }
         })
         .catch(function(err){
            _this.loading = false
            console.log(err);
             _this.visible = true
              _this.descriptionError = ""
              setTimeout(function(){
                _this.visible = false
              },3000)
            _this.$nextTick(function(){
                  //  _this.$set(_this,"chartDateALL",data)
                  //    _this.renderChart(data)
            })
         })
    },
    renderChart (data) {
      const isFirstRender = !this.chartView
      console.log(data,isFirstRender, this.createView);
      if (isFirstRender) {
        this.initChart(data)
      } else {
        this.updateChart(data)
      }
    },
    initChart (data) {
      console.log('init');
      let baseRate = 1
      if (data.length > BASE_LEN) {
        baseRate = BASE_LEN / data.length
      }
      this.scrollProportion = baseRate
      this.$nextTick(function(){
          this.$refs.scrollBar.initScroll(baseRate)
      })
      this.ds = new DataSet({
        state: {
          start: 0,
          end: baseRate
        }
      })
      var asideWidth = document.querySelector(".ant-layout-sider") && document.querySelector(".ant-layout-sider").offsetWidth || 0,
            //  width = (document.querySelector("#app").offsetWidth - asideWidth -40)/2 ,ant-card-body
             width = (document.querySelector(".ant-card-body").offsetWidth - asideWidth -40)/2 ,
             height = document.querySelector("#app").offsetHeight -180;
      data.map(function(item){
              item["weekLang"] = "第" + item.week + "周"
              // if(item.sumBenchmark){
             if("sumBenchmark" in item){
                item["sum"] = item.sumBenchmark
                item["benchmarkRisk"] = item.sumBenchmark
              } else{
                item["cumulativeRisk"] = item.sum
              }
      })
      console.log(data,'d');
      const dataSorted = data.sort((a, b) => { return a.week - b.week })
      const dv = this.ds.createView('origin').source(dataSorted)
      dv.transform({
        type: 'filter',
        callback: (obj, index, arr) => {
          return (index + 1) / arr.length  >= this.ds.state.start && (index + 1) / arr.length  <= this.ds.state.end
        }
      })
      console.log("width"+ width,"height:"+height,dv);
      const chart = new Chart({
         container: 'container',
        //  autoFit: true,
        //  forceFit: true,
         height,
         width: 2 * width,
         background: '#fff',
         padding: [40, 40, 30, 45]
      });
      chart.source(dv);
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
                //  nice: true,
                 alias: '基准风险',
                //  min: 0,
               },
               weekLang: {
                 alias: '周数'
               },
               sum: {
                //  nice: true,
                 alias: '累计风险',
                //  min: 0,
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
    updateChart (data) {
      let baseRate = 1
      // const data = departmentJson
      if (data.length > BASE_LEN) {
        baseRate = BASE_LEN / data.length
      }
      this.scrollProportion = baseRate

      this.ds.getView('origin').source(data)

      let currentPos = this.ds.state.start

      // 处理边界情况
      if (currentPos + this.scrollProportion > 1) {
        currentPos = 1 - this.scrollProportion
      }
      let endPos = Math.min(1, currentPos + this.scrollProportion)
      // 处理精度误差问题
      if (1 - endPos < 1 / data.length) {
        endPos = 1
      }
      this.ds.setState('start', currentPos)
      this.ds.setState('end', endPos)

      // this.$refs.scrollBar.initScroll(baseRate)
      this.$nextTick(function(){
          this.$refs.scrollBar.initScroll(baseRate)
      })
      this.chartView
        .guide()
        .clear()
      this.setChartViewGuide()
      // 让guide可以刷新，同时让tooltip刷新
      this.chart.repaint()
      this.$nextTick(function(){
          this.$refs.scrollBar.initScroll(this.scrollProportion, this.ds.state.start)
      })
    },
    setChartViewGuide () {
      if (this.average) {
        this.chartView
          .guide()
          .line({
            top: true,
            start: ['start', this.average],
            end: ['end', this.average]
          })
          .text({
            content: `平均好评率：${this.average}`,
            position: ['end', this.average],
            offsetY: -10,
            offsetX: -120
          })
      }
    },
    throttleUpdateDs: throttle(function updateDs (rate) {
      this.ds.setState('start', rate)
      let endPos = rate + this.scrollProportion
      // 处理精度误差问题
      if (1 - endPos < 1 / departmentJson.length) {
        endPos = 1
      }
      this.ds.setState('end', endPos)
    }, 50),
    save(){
            html2canvas(
              document.getElementById('container'),
              {
               //  backgroundColor:null,
                useCORS: true,//支持图片跨域
                scale:1,
              }
            ).then(canvas => {
              let img = new Image();
              img.src = canvas.toDataURL('image/jpeg');// toDataURL :图片格式转成 base64
             //  document.getElementById('test').appendChild(img);
              let a = document.createElement('a');
              a.href = canvas.toDataURL('image/jpeg');
              a.download = '部门风险报表';
              a.click();
            })
    },
  }
}
</script>

<style lang="scss" scoped>
.title{
     font-size: 20px;
     font-family: PingFangSC-Medium, PingFang SC;
     font-weight: 500;
     color: #333;
     margin-left: 30px;
     .tilcontent{
       display: inline-block;
       width: 92%;
     }
     #test{
       display: none;
       font-size: 10px;
       background: #eee;
       opacity: 0.5;
       color: #0091FF;
      //  &:focus {
      //  }
     }
     
  }
.chart-demo-wrap {
  position: relative;
  // width: 900px;
  margin: 0 auto;
}
.scrollBarWrap {
  position: absolute;
  bottom: 0;
  left: 0;
  z-index: 10;
  width: 100%;
}
.title {
  .tilcontent{
       display: inline-block;
       width: 92%;
     }
     #test{
       display: none;
       font-size: 10px;
       background: #eee;
       opacity: 0.5;
       color: #0091FF;
      //  &:focus {
      //  }
     }
}
.noData{
  width:100%;
  text-align: center;
  font-size: 20px;
  font-weight: 700;
}
// 错误弹框
/deep/ .ant-alert-closable{
  position: absolute;
   top: calc(50% + 30px);
    left: 50%;
    transform: translate(-50%, -50%);
}
</style>