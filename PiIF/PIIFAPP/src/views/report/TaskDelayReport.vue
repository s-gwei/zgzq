<template>
    <a-card :bordered="false" :loading="loading">
      <div class="table-page-search-wrapper wrapperselect">
      <a-form layout="inline">
        <a-row :gutter="48">
          <a-col :md="18" :sm="18">
            <a-form-item label="偏差范围">
              <a-input-number v-model="start" /> —
              <a-input-number v-model="end" />
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="6" class="btn">
            <span class="table-page-search-submitButtons">
              <a-button style="margin-left: 8px,color: #000" @click="resetSearchForm"  class="reset">重置</a-button>
              <a-button type="primary" style="margin-left: 8px" @click="doSearch(start,end)" >查询</a-button>
            </span>
          </a-col>
        </a-row>
      </a-form>
       </div>
       <div class="tableContainer" >
      <div class="title"><p class="tilcontent">工作任务延期报表</p><a-button type="dashed" icon="download" id="test">保存</a-button></div>
           <!-- 气泡图区域-begin -->
           <div id="container"></div>
           <!-- 气泡图区域-end -->
            <!-- <div class="noData" v-if="!chartDateALL || !chartDateALL.length" :style="{height: adaptiveContainH +'px',lineHeight: adaptiveContainH +'px'}">
             暂无数据
            </div> -->
       </div>
    </a-card>
</template>

<script>
  import {getAction} from '@/api/manage';
  import { Chart } from '@antv/g2';
  // import DataSet from '@antv/data-set';
  import html2canvas from 'html2canvas'
  const riskJson = require('@assets/json/gkRiskReport.json')
 
  export default {
    name: "taskDelayReport",
    components: {
    },
    data() {
      return {
        visible: false,
        loading: false,
        queryParam: {},
        chartDateALL: [],
        adaptiveContainH: 200,
        start: null,
        end: null,
        value: "",
        url: {
          // chartDate:"/HeavyDuty/pert"
          // 新接口
          chartDate: "/OTDrivice/WorkDelayTable"
        },
      }
    },
    computed:{
      //  adaptiveContainH(){
      //     const height = document.querySelector("#app").offsetHeight ,heightTop=document.querySelector('.header') && document.querySelector('header').offsetHeight || 60,adaptiveH = height - heightTop -118;
      //    return adaptiveH
      //  }
    },
    mounted(){
        this.$nextTick(function(){
            this.drawChart()
            // this.renderChart(riskJson)
        })
        //  this.getAdaptiveH()
        window.onresize = () => {
          return (() => {
            if(this.chartDateALL && this.chartDateALL.length){
               document.querySelector("#container") && document.querySelector("#container>div").remove()
               this.renderChart(this.chartDateALL)
              //  this.getAdaptiveH()
            }
          })();
       };
    },
    beforeDestroy(){
       document.querySelector("#container") && document.querySelector("#container>div")  && document.querySelector("#container>div").remove()
    },
    methods: {
       doSearch(s,e){
         var data
         if(typeof(s) != 'number' && typeof(e) != 'number') return
         if(typeof(s) == 'number' && typeof(e) == 'number'){
             data = this.chartDateALL.filter(function(item){
                  return item.deviation >= s && item.deviation <= e
             })
           } if(typeof(s) == 'number' && typeof(e) != 'number'){
             data = this.chartDateALL.filter(function(item){
                  return item.deviation >= s
             })
           } if(typeof(s) != 'number' && typeof(e) == 'number'){
             data = this.chartDateALL.filter(function(item){
                  return item.deviation <= e
             })
         }
         document.querySelector("#container") && document.querySelector("#container>div").remove()
          this.renderChart(data)
       },
       renderChart(data){
          var asideWidth = document.querySelector(".ant-layout-sider") && document.querySelector(".ant-layout-sider").offsetWidth || 0,
             width = (document.querySelector(".ant-card-body").offsetWidth - asideWidth -40)/2 ,
             height = document.querySelector("#app").offsetHeight -240;
          const chart = new Chart({
              container: 'container',
              forceFit: true,
              height,
              background: '#fff',
              padding: [30, 45, 30, 75]
          });
          data.map(function(item){
              // item["toolTitle"] = item.activityName
              item["sexuality"] = item.taskType === "normal" ? "正常进行任务" : (item.taskType === "finished" ? "已完成任务" :item.taskType === "overdue" ? "已逾期任务" : (item.taskType === "isoverdue" ? "可能逾期任务" : ""))
          })
          chart.source(data);
          // 为各个字段设置别名
          chart.scale({
            xaxis: { nice: true,alias: "周数" ,tickInterval: 1},
            activityName: { nice: true,alias: "任务名称"},
            deviation: { nice: true,alias: "偏差值" },
            actualStartTime: {nice: true,alias: '实际开始时间'},
            targetStartTime: {alias: '预估开始时间',nice: true},
            expectedFinishTime: { nice: true,alias: '预估完成时间'},
            actualEndTime: {nice: true,alias: '实际完成时间'},
            byTime: {nice: true,alias: '计划完成时间'}
          });
          const axiasobj = {
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
                 offset: 12, // 设置坐标轴文本 label 距离坐标轴线的距离
                 textStyle: {
                   textAlign: 'center', // 文本对齐方向，可取值为： start middle end
                   fill: '#999', // 文本的颜色
                   fontSize: '12', // 文本大小
                   // fontWeight: 'bold', // 文本粗细
                   rotate: 0, 
                   textBaseline: 'middle' // 文本基准线，可取 top middle bottom，默认为middle
                 } , // 文本样式，支持回调 
            },
            // tickLine: null
            tickLine: {
                 lineWidth: 1,
                 length: -5,
                 stroke: '#ccc',
                 opacity: 0.5,
                 alignWithLabel:true
            },
          }
          const yiasobj =  {
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
          }
          // 横坐标值设置
          chart.axis('xaxis', axiasobj);
          chart.axis('deviation', yiasobj);
          chart.tooltip({
            showTitle: true,
            title: "activityName",
            showCrosshairs: true,
            crosshairs: {
              type: 'cross',
            }
          });
          chart.legend(
            'sexuality', 
            {
            //  custom: true,
            //  flipPage: false,
              position: 'top-center',
              itemGap: 20,
              offsetX: width - 240,
              offsetY: -2,
              width: 400,
              marker:'circle'
          }
          );
          chart
            .point()
            .position('xaxis*deviation')
            .color('sexuality',function(item){
                return item === "正常进行任务" ? "#1890FF" : (item === "已完成任务" ? "black" :item === "已逾期任务" ? "#F5222D" : (item === "可能逾期任务" ? "#FABC16" : "#fff"))
            })
            .size(4)
            .shape('circle')
            .tooltip('activityName*xaxis*deviation*byTime*expectedFinishTime*actualEndTime')
            .style({
              fillOpacity: 0.85
            });
          chart.render();
      },
      drawChart(){
         const  url = this.url.chartDate,id = this.$route.query.id,_this=this 
        //  模拟数据
        // this.renderChart(data)
        // return
         this.loading = true
         var obj = {
              time: this.$route.query.startTime && this.$route.query.startTime != 'null' ? this.$route.query.startTime +","+this.$route.query.endTime : null,
              projectId: this.$route.query.projectId,
              planId: this.$route.query.planId,
              group: this.$route.query.departmentId,
              status: this.$route.query.status
          }
         getAction(url,obj,'get').then((res) => {
              _this.loading = false
           if(res.success && res.result){
              _this.$nextTick(function(){
                     _this.$set(_this,"chartDateALL", res.result)
                    !_this.loading && _this.renderChart(res.result)
              })
              // _this.renderChart(data)
           }else{
             _this.$nextTick(function(){
              _this.visible = true
              _this.descriptionError = res.message
              setTimeout(function(){
                _this.visible = false
              },4000)
             })
           }
         })
         .catch(function(err){
             _this.loading = false
              _this.visible = true
              _this.descriptionError = ""
              setTimeout(function(){
                _this.visible = false
              },4000)
            //  })
            _this.$nextTick(function(){
              // _this.$set(_this,"chartDateALL" , data)
              //  !_this.loading && _this.renderChart(data)
            })
         })
         
      },
      resetSearchForm(){
        this.start = null;
        this.end = null
        document.querySelector("#container") && document.querySelector("#container>div").remove()
        this.renderChart(this.chartDateALL)
      }
    }
  }
</script>
<style scoped lang="less">
  /deep/ .ant-card-body{
       padding: 6px 8px;
       box-sizing: border-box;
  }
  .ant-card{
        background: #F4F5F7;
  }
  .tableContainer{
         background-color: #fff;
         padding: 10px;
         border-radius: 4px;
     }
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
.wrapperselect{
       height: 55px;
       background-color: #fff;
        border-radius: 6px;
        padding: 10px;
        margin-bottom: 12px;
       form > div{
         height: 35px;
         div{
           height: 35px;
         }
       }

}
.table-page-search-submitButtons{
  float: right;
}
/deep/.ant-input-number{
  width: 100px;
}
</style>
