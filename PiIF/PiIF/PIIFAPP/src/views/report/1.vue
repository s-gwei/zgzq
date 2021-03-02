<template>
  <div class="jkriskContainer">
    <a-card :bordered="false" :loading="loading">
      <div class="table-page-search-wrapper wrapperselect">
      <a-form layout="inline">
        <a-row :gutter="48">
          <a-col :md="6" :sm="16">
            <a-form-item label="偏差值">
              <a-select v-model="value" placeholder="请选择">
                  <a-select-option value="01">资源部门</a-select-option>   
                  <a-select-option value="02">员工姓名</a-select-option>   
              </a-select>
            </a-form-item>
          </a-col>
          <!-- <a-col :md="6" :sm="16"> -->
            <!-- <a-form-item label="项目">
              <a-select v-model="queryParam.project" placeholder="请选择" default-value="0">
                <a-select-option value="0">全部</a-select-option>
                <a-select-option value="1">关闭</a-select-option>
                <a-select-option value="2">运行中</a-select-option>
              </a-select>
            </a-form-item> -->
          <!-- </a-col> -->
          <a-col :md="12" :sm="16" class="btn">
            <span class="table-page-search-submitButtons">
              <!-- <a-date-picker placeholder="请选择截止日期" :defaultValue="null" :showToday="false" @change="handleDateChange" :disabledDate="disabledDate" v-model="queryParam.endTime" format="YYYY-MM-DD HH:mm:ss"/> -->
              <a-button style="margin-left: 8px,color: #000" @click="resetSearchForm"  class="reset">重置</a-button>
              <a-button type="primary" style="margin-left: 8px" @click="getData()" >查询</a-button>
            </span>
          </a-col>
        </a-row>
      </a-form>
       </div>
       <div class="tableContainer" :style="{height: adaptiveContainH +'px'}">
      <div class="title"><p class="tilcontent">监控</p><a-button type="dashed" icon="download" id="test">保存</a-button></div>
           <!-- 气泡图区域-begin -->
           <div id="container"></div>
           <!-- 气泡图区域-end -->
            <!-- <div class="noData" v-if="!chartDateALL || !chartDateALL.length" :style="{height: adaptiveContainH +'px',lineHeight: adaptiveContainH +'px'}">
             暂无数据
            </div> -->
       </div>
    </a-card>
    
  </div>
</template>

<script>
  import {getAction} from '@/api/manage';
  import { Chart } from '@antv/g2';
  // import DataSet from '@antv/data-set';
  import html2canvas from 'html2canvas'
  const riskJson = require('@assets/json/gkRiskReport.json')
 
  export default {
    name: "jkRiskReport",
    components: {
    },
    data() {
      return {
        visible: false,
        loading: false,
        queryParam: {},
        chartDateALL: [],
        adaptiveContainH: 200,
        queryParamName: "",
        value: "",
        url: {
          // chartDate:"/HeavyDuty/pert"
          // 新接口
          chartDate: "/HeavyDutyTable/pertTable"
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
        //   this.drawChart()
            this.renderChart(riskJson)
        })
         this.getAdaptiveH()
        window.onresize = () => {
          return (() => {
            // if(this.chartDateALL && this.chartDateALL.length){
               document.querySelector("#container") && document.querySelector("#container>div").remove()
               this.renderChart(this.chartDateALL)
               this.getAdaptiveH()
            // }
          })();
       };
    },
    beforeDestroy(){
       document.querySelector("#container") && document.querySelector("#container>div")  && document.querySelector("#container>div").remove()
    },
    methods: {
      getAdaptiveH: function getAdaptiveH() {
         const height = document.querySelector("#app").offsetHeight ,top=document.querySelector('.wrapperselect').offsetHeight;
         this.adaptiveContainH = height - top - 60;
         console.log(this.adaptiveContainH + "px");
      },
       renderChart(data){
          var asideWidth = document.querySelector(".ant-layout-sider") && document.querySelector(".ant-layout-sider").offsetWidth || 0,
             width = (document.querySelector(".ant-card-body").offsetWidth - asideWidth -40)/2 ,
             height = document.querySelector("#app").offsetHeight -180;
          const chart = new Chart({
              container: 'container',
            //   forceFit: true,
              height,
              background: '#fff',
              padding: [30, 45, 30, 75]
          });
          data.map(function(item){
              item["toolTitle"] = "信息"
              item["sexuality"] = item.gender === 'female' ? "女性" : "男性"
          })
          chart.source(data);
          // 为各个字段设置别名
          chart.scale({
            height: { nice: true,alias: "身高" },
            weight: { nice: true,alias: "体重" },
            sexuality: { alias: "性别" },
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
          console.log(yiasobj);
          // 横坐标值设置
          chart.axis('height', axiasobj);
          chart.axis('weight', yiasobj);
          chart.tooltip({
            showTitle: true,
            title: "toolTitle",
            showCrosshairs: true,
            crosshairs: {
              type: 'cross',
            }
          });
          chart.legend('gender', {
              position: 'top-center',
              itemGap: 20,
              offsetX: width - 120,
              offsetY: 0,
              width: 400,
              marker:'diamond'
          });
          chart
            .point()
            .position('height*weight')
            .color('gender',function(gender){
                return gender === "female" ? "#F5222D" : "#1890FF"
            })
            .size(5)
            .shape('circle')
            .tooltip('toolTitle*sexuality*height*weight')
            .style({
              fillOpacity: 0.85
            });
          chart.render();
      },
       drawChart(){
         const  url = this.url.chartDate,id = this.$route.query.id,_this=this ,data = JSON.parse(JSON.stringify(pertJson))
        //  模拟数据
        // this.renderChart(data)
        // return
         this.loading = true
         getAction(url,{activeId: id},'get').then((res) => {
              _this.loading = false
           if(res.success && res.result){
                 _this.$nextTick(function(){
                    res.result.map(function(item){
                       item.pert = Number(item.pert)
                    })
                     _this.$set(_this,"chartDateALL", res.result)
                    !_this.loading && _this.renderChart(res.result)
              })
              // _this.renderChart(data)
           }else{
             _this.$nextTick(function(){
              //  _this.$set(_this,"chartDateALL" , res.result)
              //  !_this.loading && _this.renderChart(data)
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
      resetSearchForm(){}
    //   save(){
    //         html2canvas(
    //           document.getElementById('container'),
    //           {
    //            //  backgroundColor:null,
    //             useCORS: true,//支持图片跨域
    //             scale:1,//设置放大的倍数
    //           }
    //         ).then(canvas => {
    //           //截图用img元素承装，显示在页面的上
    //           let img = new Image();
    //           img.src = canvas.toDataURL('image/jpeg');// toDataURL :图片格式转成 base64
    //          //  document.getElementById('test').appendChild(img);
    //           let a = document.createElement('a');
    //           a.href = canvas.toDataURL('image/jpeg');
    //           a.download = 'PERT报表';
    //           a.click();
    //         })
    //     },
    }
  }
</script>
<style scoped lang="less">
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
        margin-bottom: 8px;
       form > div{
         height: 35px;
         div{
           height: 35px;
         }
       }

     }
</style>
