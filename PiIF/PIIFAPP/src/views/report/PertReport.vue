<template>
  <div>
    <a-card :bordered="false" :loading="loading">
      <div class="title"><p class="tilcontent">PERT</p><a-button type="dashed" icon="download" @click="save" id="test">保存</a-button></div>
      <!-- 气泡图区域-begin -->
      <div id="container"></div>
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
    
  </div>
</template>

<script>
  import {getAction} from '@/api/manage';
  import { Chart } from '@antv/g2';
  // import DataSet from '@antv/data-set';
  import html2canvas from 'html2canvas'
  //const pertJson = require('@assets/json/pertJson.json')
 
  export default {
    name: "pertReport",
    components: {
    },
    data() {
      return {
        visible: false,
        descriptionError: "",
        loading: false,
        description: '这是用户管理页面',
        queryParam: {},
        chartDateALL: [],
        url: {
          // chartDate:"/HeavyDuty/pert"
          // 新接口
          chartDate: "/HeavyDutyTable/pertTable"
          //chartDate: "/ZLTable/pertTable"
        },
      }
    },
    computed:{
       adaptiveContainH(){
          const height = document.querySelector("#app").offsetHeight ,heightTop=document.querySelector('.header') && document.querySelector('header').offsetHeight || 60,adaptiveH = height - heightTop -118;
         return adaptiveH
       }
    },
    mounted(){
        this.$nextTick(function(){
          this.drawChart()
        })
        window.onresize = () => {
          return (() => {
            if(this.chartDateALL && this.chartDateALL.length){
               document.querySelector("#container") && document.querySelector("#container>div").remove()
               this.renderChart(this.chartDateALL)
            }
          })();
       };
    },
    beforeDestroy(){
       document.querySelector("#container") && document.querySelector("#container>div")  && document.querySelector("#container>div").remove()
    },
    methods: {
      closeAlert(){
        this.visible = false
      },
       renderChart(data){
          const height = document.querySelector("#app").offsetHeight - 160,
                 asideWidth = document.querySelector(".ant-layout-sider") && document.querySelector(".ant-layout-sider").offsetWidth || 0,
                 width = (document.querySelector("#app").offsetWidth - asideWidth -40)/2 ; 
          data.map(function(item,idx){
              item["index"] = idx + 1
              item["toolTitle"] = "第" + (idx + 1) + "次汇报"
          })
          const chart = new Chart({
              container: 'container',
              autoFit: true,
              forceFit: true,
              height,
              width: 2 * width,
              background: '#fff',
              padding: [30, 45, 30, 75]
          });
          chart.source(data);
          // 为各个字段设置别名
          chart.scale({
              index: {
                alias: '',
                nice: true,
                // min: 0,
                // max: max ,
                // tickCount: max - 1 < 2 ? 2 : max-1,
              },
              pert: {
                alias: 'PERT',
                nice: true,
                // min: 0,
              },
              actualStart: {
                 nice: true,
                alias: '实际开始时间'
              },
              taskStart: {
                alias: '预估开始时间',
                nice: true,
              },
              estimatedCompletion: {
                 nice: true,
                alias: '预估完成时间'
              },
              taskComplete: {
                 nice: true,
                alias: '实际完成时间'
              },
              toolTitle: {
                  nice: true,
                  alias: '汇报'
              }
          });
          // 横坐标值设置
          chart.axis('index', {
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
                 opacity: 0.5
                //  alignWithLabel:true
            },
          });
          chart.axis('pert', {
             title: {
                offset: -18,
                position: "end",
                rotate: 90,
                autoRotate:false,
                textStyle: {
                    fontSize: 12, // 文本大小
                    textAlign: 'center', // 文本对齐方式
                    fill: '#999', // 文本颜色
                },
                zlevel:2
             },
            label: {
                 offset: 20, // 设置坐标轴文本 label 距离坐标轴线的距离
                 textStyle: {
                   textAlign: 'center', // 文本对齐方向，可取值为： start middle end
                   fill: '#999', // 文本的颜色
                   fontSize: '12', // 文本大小
                   // fontWeight: 'bold', // 文本粗细
                   rotate: 0, 
                   textBaseline: 'middle' // 文本基准线，可取 top middle bottom，默认为middle
                 } , // 文本样式，支持回调 
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
                 opacity: .5,
                 alignWithLabel:true
              },
          });
          chart.tooltip({ 
             title: 'toolTitle',
             showMarkers: false
          });
          chart
            .line()
            .tooltip({ 
              showTitle: false
            })
            .tooltip('pert*taskStart*estimatedCompletion*toolTitle')
            .position('index*pert')
            .color('#0091FF')
           chart
              .area()
              .tooltip(false)
              .position('index*pert')
              .color('l(100) 0:rgba(0, 145, 255, 0.4) 1:rgba(0, 145, 255, 0)')
              // .shape('smooth')
          //  chart.interaction('element-highlight-by-x');
           chart.render();
      },
       drawChart(){
        const url = this.url.chartDate,id = this.$route.query.id,_this=this //我修改的,data = JSON.parse(JSON.stringify(pertJson))
        //  模拟数据
         //this.renderChart(data)
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
         .catch(function(){
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
      save(){
            html2canvas(
              document.getElementById('container'),
              {
               //  backgroundColor:null,
                useCORS: true,//支持图片跨域
                scale:1,//设置放大的倍数
              }
            ).then(canvas => {
              //截图用img元素承装，显示在页面的上
              let img = new Image();
              img.src = canvas.toDataURL('image/jpeg');// toDataURL :图片格式转成 base64
             //  document.getElementById('test').appendChild(img);
              let a = document.createElement('a');
              a.href = canvas.toDataURL('image/jpeg');
              a.download = 'PERT报表';
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
</style>
