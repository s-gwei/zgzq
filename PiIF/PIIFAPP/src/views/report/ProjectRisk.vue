<template>
  <a-card :bordered="false" style="height: 100%;background: #fff" :loading="loading">
    <div class="title"><p class="tilcontent">项目风险系数</p><a-button type="dashed" icon="download"  @click="save" id="test">保存</a-button></div>
    <!-- 气泡图区域-begin -->
    <div id="container"></div>
    <!-- 气泡图区域-end -->
    <div class="noData" v-if="!chartDateALL || !chartDateALL.length" :style="{height: adaptiveContainH +'px',lineHeight: adaptiveContainH +'px'}">暂无数据</div>
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
  //import DataSet from '@antv/data-set';
  import { Chart } from '@antv/g2';
  import html2canvas from 'html2canvas'
  const projectJson = require('@assets/json/projectJson.json')

  export default {
    name: "projectRisk",
    components: {
    },
    data() {
      return {
        projectJson,
        loading: false,
        visible: false,
        descriptionError: "Error",
        description: '项目风险报表',
        chartDateALL: [],
        url: {
          // chartDate:"/HeavyDuty/ProjectRiskCoefficient"
          chartDate: "/HeavyDutyTable/ProjectRiskTable"
        },
        simulationSata: [{
                 "Yaxis": 0,
                 "Xaxis": 0,
                 "OutputQualityRiskSum": 0,
                 "line": 0
              },{
                 "Yaxis": 1.5,
                 "Xaxis":1.5,
                 "OutputQualityRiskSum": 0,
                 "line": 1.48
              },{
                 "Yaxis": 0.93,
                 "Xaxis": 0.93,
                 "OutputQualityRiskSum": 0,
                 "line": 0.95
        },{
                 "Yaxis": 0.4,
                 "Xaxis": 0.4,
                 "OutputQualityRiskSum": 0,
                 "line": 0.43
        },{
                 "Yaxis": 0.79,
                 "Xaxis": 0.79,
                 "OutputQualityRiskSum": 0,
                 "line": 0.75
        }]
      }
    },
    computed:{
       adaptiveContainH(){
          const height = document.querySelector("#app").offsetHeight ,heightTop=document.querySelector('.header') && document.querySelector('header').offsetHeight || 60,adaptiveH = height - heightTop -116;
          return adaptiveH
       }
    },
    beforeDestroy(){
      document.querySelector("#container") && document.querySelector("#container>div") &&  document.querySelector("#container>div").remove()
    },
    mounted(){
      this.drawChart()
      window.onresize = () => {
          return (() => {
            if(this.chartDateALL && this.chartDateALL.length){
               document.querySelector("#container") && document.querySelector("#container>div")&& document.querySelector("#container>div").remove()
               this.renderChart(this.chartDateALL)
            }
          })();
       };
    },
    methods: {
      closeAlert(){
        this.visible = false
      },
       renderChart(data){
          var _this = this
          const height = document.querySelector("#app").offsetHeight - 182,
          // chartLength = data && data.length ,max = data && data.length,
                asideWidth = document.querySelector(".ant-layout-sider") && document.querySelector(".ant-layout-sider").offsetWidth || 0,
                width = (document.querySelector("#app").offsetWidth - asideWidth -40)/2 ; 
          const chart = new Chart({
              container: 'container',
              autoFit: true,
              // forceFit: true,
              height,
              width: 2 * width,
              background: '#fff',
              padding: [45, 45, 30,45]
          });
          chart.source(data);
          // 为各个字段设置别名
          chart.scale({
              Yaxis: {
                alias: '预实比',
                nice: true,
                min: 0,
                // max: 1.5
              },
              OutputQualityRiskSum: {
                 nice: true,
                alias: '项目风险系数'
              },
              Xaxis: {
                alias: '项目进展比',
                nice: true,
              },
              proName: {
                alias: '项目名称',
                nice: true
              }
          });
          // 横坐标值设置
          chart.axis('Xaxis', {
            title: {
               offset:-16,
               textStyle: {
                  fontSize: 12, // 文本大小
                  textAlign: 'center', // 文本对齐方式
                  fill: '#666', // 文本颜色
                  fontWeight: 500
               },
               position: "end",
             },
             label: {
               formatter(value) {
                 return (value * 100) + '%';
               }, // 格式化坐标轴的显示
            
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
             tickLine: {
                lineWidth: 1,
                length: -5,
                stroke: '#ccc',
                opacity: .5
             }
         });
          chart.axis('Yaxis', {
               title: {
                 offset: -24,
                 position: "end",
                 rotate: 90,
                 autoRotate:false,
                 textStyle: {
                     fontSize: 12, // 文本大小
                     textAlign: 'center', // 文本对齐方式
                     fill: '#666', // 文本颜色
                    //  fontWeight: 500,
                    //  fontFamily: "STHeitiSC-Medium, STHeitiSC;"
                 },
                 zlevel:2
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
          chart.tooltip({
             title: 'proName',
             showMarkers: false
          });
          chart.axis('line',false)
        // 是否展示图例
          chart.legend('OutputQualityRiskSum', false); 
          chart.legend('continent', false); 
          chart.point().position('Xaxis*Yaxis')
            .size('OutputQualityRiskSum', [0, 60]).opacity(0.4)
            // 气泡图圈的颜色
            .color("#0091FF")
            // .color('OutputQualityRiskSum', function(item){
            //   if(item > 10460){
            //       return '#0091FF'
            //   } else{
            //     return 'pink'
            //   }
            // })
            .shape('circle')
            .tooltip('OutputQualityRiskSum*Xaxis*Yaxis*proName')
            .style({
              lineWidth:1,
              stroke: "#0091FF",
            //   stroke: ('OutputQualityRiskSum', function(item){
            //     console.log(item,'item');
            //   if(item > 10460){
            //       return '#0091FF'
            //   } else{
            //     return '#ff3040'
            //   }
            // }),
              opacity: 0.3
            })
          chart
            .line()
            .tooltip({
              showTitle: false
            })
            .position('Xaxis*line')
            .color('#F7B500')
            .shape('smooth')
            .style({
                 stroke: '#F7B500',
                 lineDash: [5, 4],
                 opacity: 0.7
            })
          chart.render();
          // 气泡圈点击事件
          chart.on('click',ev=>{
            // console.log(ev.data,'ev');
            // return
            if(ev.data && ev.data._origin){
              _this.$router.push({name: 'singleProjectTableFixed',params:ev.data._origin})
            }
          })
       },
      //  模拟数据与后端数据返回横坐标相同时,截取模拟字段对应的数据
       getSimulationData: function(data){
        //var index = []
         for(var i = 0; i <data.length;i++){
              for(var j = 0; j <this.simulationSata.length;j++){
                if(data[i].Xaxis == this.simulationSata[j].Xaxis){
                  console.log(i,j);
                  this.simulationSata.splice(j,1)
                  j--
                }
              }
         }
         return  this.simulationSata.concat(data)
       },
      drawChart(){
         const  url = this.url.chartDate,projectIds = this.$route.query.ids || this.$route.query.id,_this = this//,data = JSON.parse(JSON.stringify(projectJson))
         //  模拟
         // this.renderChart(data)
         // return
         this.loading = true
         getAction(url,{projectIds},'get').then((res) => {
           _this.loading = false
           if(res.success && res.result){
             res.result.map(function(item){
               item.line = item.Xaxis
             })
           var  dates = _this.getSimulationData(res.result)
              // dates = _this.simulationSata.concat(res.result)
              _this.$nextTick(function(){
                _this.$set(_this,"chartDateALL",dates)
                  _this.renderChart(dates)
              })
           } else{
              _this.visible = true
              _this.descriptionError = res.message
              // setTimeout(function(){
              //   _this.visible = false
              // },3000)
               _this.$nextTick(function(){
                  // _this.renderChart(data)
              })
           }
           })
           .catch(function(){
              _this.loading = false
              _this.visible = true
              _this.descriptionError = ""
              setTimeout(function(){
                _this.visible = false
              },3000)
              // 报错模拟
            // _this.$nextTick(function(){
            //       projectJson.map(function(item){
            //       item.line = item.Xaxis
            //  })
            // var  dates = _this.getSimulationData(data)
            //       _this.$set(_this,"chartDateALL",_this.simulationSata.concat(dates))
            //         _this.renderChart(_this.simulationSata.concat(dates))
            //    })
           })
          
       },
        //  页面保存
       save(){
          html2canvas(
            document.getElementById('container'),
            {
             //  backgroundColor:null,//画出来的图片有白色的边框,不要可设置背景为透明色（null）
              useCORS: true,//支持图片跨域
              scale:1,//设置放大的倍数
            }
          ).then(canvas => {
            //截图用img元素承装，显示在页面的上
            let img = new Image();
            img.src = canvas.toDataURL('image/jpeg');// toDataURL :图片格式转成 base64
           //  document.getElementById('test').appendChild(img);
            //如果你需要下载截图，可以使用a标签进行下载
            let a = document.createElement('a');
            a.href = canvas.toDataURL('image/jpeg');
            a.download = '项目风险报表';
            a.click();
          })
       },
    }

  }
</script>
<style scoped lang="less">
  .title{
     font-size: 20px;
     font-family: PingFangSC-Medium, PingFang SC;
     font-weight: 500;
     color: #333;
     /* text-align: left; */
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
     
     /* position: fixed; */
  }
  .ant-layout-content>div {
    margin: 12px!important;
    height: 100%!important;

  }
  /* .ant-layout-content{
    background:#fff!important;
  } */

//暂时没用到，自己注释
  // .ant-layout-content>div{
  // }


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
