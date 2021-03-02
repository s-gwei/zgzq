<template>
    <a-card :bordered="false">
    <div class="table-page-search-wrapper wrapperselect">
      <a-form layout="inline">
        <a-row :gutter="48">
          <a-col :md="6" :sm="16">
            <a-form-item label="资源部门">
              <a-select v-model="queryParam.ResourceSector" placeholder="请选择" default-value="0">
                <a-select-option value="0">全部</a-select-option>
                <a-select-option value="1">关闭</a-select-option>
                <a-select-option value="2">运行中</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="16">
            <a-form-item label="员工姓名">
              <a-select v-model="queryParam.name" placeholder="请选择" default-value="0">
                <a-select-option value="0">全部</a-select-option>
                <a-select-option value="1">关闭</a-select-option>
                <a-select-option value="2">运行中</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="16">
            <!-- <a-form-item label="项目">
              <a-select v-model="queryParam.project" placeholder="请选择" default-value="0">
                <a-select-option value="0">全部</a-select-option>
                <a-select-option value="1">关闭</a-select-option>
                <a-select-option value="2">运行中</a-select-option>
              </a-select>
            </a-form-item> -->
          </a-col>
          <a-col :md="6" :sm="16" class="btn">
            <span class="table-page-search-submitButtons">
              <a-button style="margin-left: 8px" @click="resetSearchForm">重置</a-button>
              <a-button type="primary" style="margin-left: 8px" @click="getData()">查询</a-button>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <div class="tableContainer" :style="{height: adaptiveContainH +'px'}">
      <div class="title">
        <p>任务执行风险表</p>
        <a-button type="primary" style="margin-left: 8px"  @click="exportExcel">导出为Excel</a-button>
    </div>
    <!-- table部分start -->
    <div class="table-container table" id="table" :style="{height: adaptiveH+'px'}">
          <!-- header -->
         <div class="table-child"  id="theadDiv" >
             <table id="tableId1">
               <thead>
                   <tr>
                       <th>执行职能</th>
                       <th>任务名</th>
                       <th colspan="4">数据准备</th>
                       <th colspan="3">计划环境</th>
                       <th colspan="6">输出质量</th>
                       <th colspan="2">输出加权</th>
                       <th >项目风险</th>
                       <th>输出评定</th>
                       <th >发布次数</th>
                   </tr>
                   <tr>
                        <th></th>
                        <th></th>
                        <th>权重</th>
                        <th>标准偏差</th>
                        <th>汇报偏差</th>
                        <th class="qualityInfactor">质量影响因子</th>
                        <th>标准工期</th>
                        <th>项目工期</th>
                        <th>风险KPI</th>
                        <th>标准偏差</th>
                        <th>汇报偏差</th>
                        <th>标准困难度</th>
                        <th>汇报困难度</th>
                        <th>输出质量KPI</th>
                        <th>质量风险</th>
                        <th>广度</th>
                        <th>关键度</th>
                        <th></th>
                        <th></th>
                        <th></th>
                   </tr>
               </thead>
               <tbody>
                   <template v-for="(itm,idx) in tableDate">
                 <template v-for="(item,index) in itm">
                    <tr v-if="!index" class="firstRow">
                    <!-- <td >z职能</td> -->
                    <td :rowspan="getrowNum(itm)">{{item['任务名称']}}</td>
                    <td :rowspan="getrowNum(itm)">{{item['任务名称']}}</td>
                    <td>{{item.weight}}</td>
                    <td>{{item.standardDeviation}}</td>
                    <td>{{item.reportingDeviations}}</td>
                    <td>{{item.qualityInfluenceFactor}}</td>
                    <td>{{item.StandardPeriod}}</td>
                    <td>{{item.reportPeriod}}</td>
                    <td>风险KPI{{item.reportPeriod}}</td>
                    <td>{{item.standardDeviation}}</td>
                    <td>{{item.reportingDeviations}}</td>
                    <td>{{item.standardDifficulty}}</td>
                    <td>{{item.reportingDifficulty}}</td>
                    <td>{{item.OutputaualityKPI}}</td>
                    <td>{{item.OutputaualityKPI}}</td>
                    <td>{{item.OutputaualityKPI}}</td>
                    <td>{{item.OutputaualityKPI}}</td>
                    <td>{{item.OutputaualityKPI}}</td>
                    <td>{{item.OutputaualityKPI}}</td>
                    <td>end</td>
                    </tr>
                    <tr  v-if="index && index != itm.length -1" class="otherRow">
                    <!-- <td >z职能</td> -->
                    <td>{{item.weight}}</td>
                    <td>{{item.standardDeviation}}</td>
                    <td>{{item.reportingDeviations}}</td>
                    <td>{{item.qualityInfluenceFactor}}</td>
                    <td>{{item.StandardPeriod}}</td>
                    <td>{{item.reportPeriod}}</td>
                    <td>风险KPI{{item.reportPeriod}}</td>
                    <td>{{item.standardDeviation}}</td>
                    <td>{{item.reportingDeviations}}</td>
                    <td>{{item.standardDifficulty}}</td>
                    <td>{{item.reportingDifficulty}}</td>
                    <td>{{item.OutputaualityKPI}}</td>
                    <td>{{item.OutputaualityKPI}}</td>
                    <td>{{item.OutputaualityKPI}}</td>
                    <td>{{item.OutputaualityKPI}}</td>
                    <td>{{item.OutputaualityKPI}}</td>
                    <td>{{item.OutputaualityKPI}}</td>
                    <td>end</td>
                    </tr>
                </template>
                <tr class="trtotal" :key="idx">
                    <td colspan="3">环境质量指标(预设值基准:0)</td>
                     <td class="tdBorderRight">{{getcolcontent('qualityIndex',itm)}}</td>
                    <td colspan="2">工期风险KPI(预设值基准:1)</td>
                    <td class="tdBorderRight">{{getcolcontent('riskKPI',itm)}}</td>
                    <td colspan="4">总质量指标(预设值基准:1)</td>
                     <td class="tdBorderRight">{{getcolcontent('totalQualityKpi',itm)}}</td>
                    <td colspan="5">平均发布次数</td>
                    <td class="tdBorderRight">average5</td>
                  </tr>
                   </template>
               </tbody>
            </table>
         </div>
         <!-- header ending -->
         <!-- body -->
         <div class="table-next-child" id="tbodyDiv">
             <table id="tableId2">
               <thead>
                   <tr>
                       <th>执行职能</th>
                       <th>任务名</th>
                       <th colspan="4">数据准备</th>
                       <th colspan="3">计划环境</th>
                       <th colspan="6">输出质量</th>
                       <th colspan="2">输出加权</th>
                       <th >项目风险</th>
                       <th>输出评定</th>
                       <th >发布次数</th>
                   </tr>
                   <tr>
                        <th></th>
                        <th></th>
                        <th>权重</th>
                        <th>标准偏差</th>
                        <th>汇报偏差</th>
                        <th class="qualityInfactor">质量影响因子</th>
                        <th>标准工期</th>
                        <th>项目工期</th>
                        <th>风险KPI</th>
                        <th>标准偏差</th>
                        <th>汇报偏差</th>
                        <th>标准困难度</th>
                        <th>汇报困难度</th>
                        <th>输出质量KPI</th>
                        <th>质量风险</th>
                        <th>广度</th>
                        <th>关键度</th>
                        <th></th>
                        <th></th>
                        <th></th>
                   </tr>
               </thead>
               <tbody>
                   <template v-if="tableDate && tableDate.length && !isLoading" v-for="itm in tableDate">
                      <template v-for="(item,index) in itm">
                          <tr v-if="!index" class="firstRow">
                    <!-- <td >z职能</td> -->
                    <td :rowspan="getrowNum(itm)">{{item['任务名称']}}</td>
                    <td :rowspan="getrowNum(itm)">{{item['任务名称']}}</td>
                    <td>{{item.weight}}</td>
                    <td>{{item.standardDeviation}}</td>
                    <td>{{item.reportingDeviations}}</td>
                    <td>{{item.qualityInfluenceFactor}}</td>
                    <td>{{item.StandardPeriod}}</td>
                    <td>{{item.reportPeriod}}</td>
                    <td>风险KPI{{item.reportPeriod}}</td>
                    <td>{{item.standardDeviation}}</td>
                    <td>{{item.reportingDeviations}}</td>
                    <td>{{item.standardDifficulty}}</td>
                    <td>{{item.reportingDifficulty}}</td>
                    <td>{{item.OutputaualityKPI}}</td>
                    <td>{{item.OutputQualityRisk}}</td>
                    <td>{{item.span}}</td>
                    <td>{{item.Criticality}}</td>
                    <td>{{item.ProjectRiskIndicators}}</td>
                    <td>{{item.OutputaualityKPI}}输出评定</td>
                    <td>end</td>
                          </tr>
                          <tr v-if="index && index != itm.length -1" class="otherRow">
                    <!-- <td >z职能</td> -->
                    <td>{{item.weight}}</td>
                    <td>{{item.standardDeviation}}</td>
                    <td>{{item.reportingDeviations}}</td>
                    <td>{{item.qualityInfluenceFactor}}</td>
                    <td>{{item.StandardPeriod}}</td>
                    <td>{{item.reportPeriod}}</td>
                    <td>风险KPI{{item.reportPeriod}}</td>
                    <td>{{item.standardDeviation}}</td>
                    <td>{{item.reportingDifficulty}}</td>
                    <td>{{item.standardDifficulty}}</td>
                    <td>{{item.reportingDifficulty}}</td>
                    <td>{{item.OutputaualityKPI}}</td>
                    <td>{{item.OutputQualityRisk}}</td>
                    <td>{{item.span}}</td>
                    <td>{{item.Criticality}}</td>
                    <td>{{item.ProjectRiskIndicators}}</td>
                    <td>{{item.OutputaualityKPI}}输出评定</td>
                    <td>end</td>
                          </tr>
                     </template>
                <!-- <template v-if="index && index == itm.length -1"> -->
                     <tr class="trtotal">
                    <td colspan="3">环境质量指标(预设值:0)</td>
                    <td class="tdBorderRight">{{getcolcontent('qualityIndex',itm)}}</td>
                    <td colspan="2">工期风险KPI(预设值:1)</td>
                    <td class="tdBorderRight">{{getcolcontent('riskKPI',itm)}}</td>
                    <td colspan="4">总质量指标(预设值:1)</td>
                    <td class="tdBorderRight">{{getcolcontent('totalQualityKpi',itm)}}</td>
                    <td colspan="5">平均发布次数</td>
                    <td class="tdBorderRight">vaerage5</td>
                     </tr>
                <!-- <tr class="trtotal">
                    <td colspan="3">预设值</td>
                    <td>123</td>
                    <td colspan="2">预设值</td>
                    <td>风险</td>
                    <td colspan="4">预设值</td>
                    <td>指标</td>
                    <td colspan="3"></td>
                    <td></td>
                    <td></td>
                    <td></td>
                </tr> -->
                <!-- </template> -->
                   </template>
                   <template v-if="isLoading">
                      <!-- <tr class="no-records-found"><td colspan="999" style="text-align: center">正在加载请稍等</td></tr> -->
                      <tr>
                        <td colspan="999" style="text-align: center">
                            <div class='load-container' :style="{height: adaptiveload+'px',width: loadingwidth}">
                                  <div class='loader'></div>
                            </div>
                        </td>
                      </tr>
                   </template>
                   <template v-if="tableDate && !tableDate.length && !isLoading">
                      <tr class="no-records-found" style="text-align: center"><td colspan="999" style="text-align: center">没有找到匹配的数据</td></tr>
                   </template>
               </tbody>
            </table>
         </div>
         <!-- body ending-->
    </div>
    </div>
  </a-card>
</template>
<script>
import {getAction} from '@/api/manage';
const pertJson = require('@assets/json/singleProject.json')
const data = []
 
export default {
  name: "singleProjectTableNew",
  data() {
      return{
          tableDate: [],
          queryParam: {},
          adaptiveH: 400,
          adaptiveContainH: 500,
          flag: true,
          // pertJson,
          url: {
              tableDate:"/HeavyDuty/TaskExecutionTable",
              export: "/HeavyDuty/exportExcel"
           },
           isLoading: true
      }
  },
  mounted(){
      this.queryParam.fileName = this.$route.params.fileName ? this.$route.params.fileName : ''
      this.queryParam.planId = this.$route.params.planId ? this.$route.params.OutputQualityRiskSum : ''
      // 监听x轴滚动事件
       this.getAdaptiveH()
       var dom = document.querySelector('#tbodyDiv')
       dom.addEventListener('scroll', this.scrollX);
       this.getData()
  },
  beforeDestroy(){
      var dom = document.querySelector('#tbodyDiv')
      dom.removeEventListener('scroll', this.scrollX);
  },
  methods:{
      
     // 获取table高度,判断滚动条位置
      getAdaptiveH: function getAdaptiveH() {
         const height = document.querySelector("#app").offsetHeight ,top=document.querySelector('#table').offsetTop,tableHeight = document.querySelector("#tableId2").offsetHeight,adaptiveH = height - top;
        //  console.log({'table':tableHeight,'top':top,'剩余空间':adaptiveH});
         this.adaptiveContainH = height - top + 46;
         if(adaptiveH > tableHeight ) {
           this.adaptiveH = tableHeight + 10
         } else {
             this.adaptiveH = adaptiveH - 20
         }
      },
      scrollX(){
         var dom = document.querySelector('#tbodyDiv')
         var dom1 = document.querySelector('#theadDiv')
         dom.addEventListener('scroll', function () {
             dom1.scrollLeft = dom.scrollLeft;
         });
      },
      getcolcontent(key,arr){
        var i = arr.length -1
        return arr[i][key]
      },
      getrowNum(data){
          return data.length
      },
      getData(){
          const  url = this.url.tableDate,_this=this;
          this.isLoading = true
          getAction(url,this.queryParam,'get').then((res) => {
            _this.isLoading = false
           if(res.success && res.result){
              // _this.$set(_this,'tableDate',res.result)
              _this.$set(_this,'tableDate',pertJson)
              _this.$nextTick(function(){
                 _this.getAdaptiveH()
              })
           }else{
               _this.$set(_this,'tableDate',pertJson)
              _this.$nextTick(function(){
                 _this.getAdaptiveH()
              })
           }
         })
         .catch(function(err){
              _this.isLoading = false
             _this.$set(_this,"tableDate",pertJson)
             _this.$nextTick(function(){
                 _this.getAdaptiveH()
              })
         })
      },
      // 重置
      resetSearchForm(){
          this.queryParam.ResourceSector = this.$route.params.ResourceSector
          this.queryParam.name = this.$route.params.name
          this.getData()
      },
      // 导出
      exportExcel(){
        const  url = this.url.export,_this=this;
        console.log(this.queryParam);
        var paramsUrl = "?";
        for(let key in this.queryParam){
            paramsUrl += key + "=" + this.queryParam[key] + "&"
        }
       paramsUrl =  paramsUrl.substr(0,paramsUrl.length - 1)
        // window.open('http://192.168.2.162:8080/jeecg-boot'+ this.url.export, "_blank");
        window.open( window._CONFIG['domianURL']+ url + paramsUrl, "_blank");
      },
      onBodyScroll(e) {
          var self = this;
          this.flag = false;
          document.querySelector('.fix-column .fixed-table-body').addEventListener('scroll', function () {
        console.log(e);
              if (!self.flag) {
                  var scrollTop = event.target.scrollTop;
                  document.querySelector('.table-container .table-next-child').scrollTop = scrollTop;
                  
              }
          });
      },
      colScrollUpdate(){
                var self = this;
                this.flag = true;
                document.querySelector('.table-container .table-next-child').addEventListener('scroll', function () {
                  console.log('scroll',event.target.scrollTop);
                    // document.querySelector('.fixed-header').scrollLeft = this.scrollLeft;
                    if (self.flag) {
                        document.querySelector('.fix-column .fixed-table-body').scrollTop = event.target.scrollTop;
                        document.querySelector('.fix-column .fixed-table-body').style.top = - event.target.scrollTop + 'px';
                    }
                });
                var left = document.querySelector('.table-container #tbodyDiv').scrollLeft;
                document.querySelector('.table-container #theadDiv').scrollLeft = left
                // $(this.$refs.headerWrap).scrollLeft(left);
      }
    }
};
</script>
<style scoped lang="less">
     /deep/ .ant-card-body{
       padding: 14px;
       box-sizing: border-box;
      //  width: calc(100% - 2px);
     }
     body{
       height: 100%;
       background: #F4F5F7;
     }
     .ant-card{
        background: #F4F5F7;
     }
     .wrapperselect{
       height: 55px;
       background-color: #fff;
        border-radius: 6px;
        padding: 10px;
        margin-bottom: 14px;
       form > div{
         height: 35px;
         div{
           height: 35px;
         }
       }

     }
     .btn{
                 padding-left: 8%!important;
     }
     .tableContainer{
         background-color: #fff;
         padding: 10px;
         border-radius: 4px;
     }
     .title{
         display: flex;
         width: 100%;
         
         p{
             width: 85%;
             line-height: 34px;
             color: #000;
             font-size: 16px;
         }
     }
     table,table tr th, table tr td { border:1px solid #E9E9E9; }
     table thead tr {
         background: #F4F5F7;
         padding: 6px 12px;
         color: #000;
         font-size: 14px;
        //  font-family: PingFangSC-Medium, PingFang SC;
         th{
           font-weight: 540;
           color: #000;
           min-width: 80px;
          padding: 3px 8px;
          text-align: center;
          white-space: nowrap;
         }
     }
     table thead tr:nth-child(1) {
       background: #E9E9E9;
       th{
       border: 1px solid #DEDEDE
       }
     }
     .tdBorderRight{
       border-right: 1px solid #DEDEDE
     }
     .table{
         width: 100%;
         max-height: 400px;
         height: 300px;
         position: relative;
        //  overflow-y: auto;
         overflow-y: hidden;
         .table-child,
         .table-next-child{
             min-width: 800px;
             table{
                 width: calc(100% - 80px);
                 th{
                 padding: 6px 8px;
                 }
             }
                     
         }
         .table-child{
                     position: absolute;
                     top: 0;
                     left: 0;
                     height: 68px;
                     overflow: hidden;
                     width: calc(100% - 88px);
                    //  z-index: 2;
                     th{
                        //  background: #F4F5F7;
                         padding: 3px 6px;
                     }
                     th:nth-child(1){
                         width: 80px;
                     }
         }
         .table-next-child{
                    //  overflow-y: auto;
                    //  max-height: 300px;

                     height: 100%;
                    max-height: 100%;
                    overflow-y: scroll;
                    overflow-x: auto;
         }
     }
     
     .firstRow td:nth-child(4),
     .firstRow td:nth-child(8),
     .otherRow td:nth-child(7),
     .firstRow td:nth-last-child(2),
     .firstRow td:nth-last-child(5),
     .firstRow td:nth-last-child(6),
     .otherRow td:nth-last-child(2),
     .otherRow td:nth-last-child(1),
     .firstRow td:nth-last-child(1),
     .otherRow td:nth-last-child(5),
     .otherRow td:nth-last-child(6),
     .otherRow td:nth-child(4){
         background: #F4F5F7;
     }
     tbody td{
       text-align: right;
         padding: 6px 8px;
     }
     #table tbody .trtotal{
       background: #e9e9e9;
       td:nth-child(2n){
         text-align: right;
         color: #333333
       }
       td:nth-child(2n + 1){
         text-align: right;
       }
     }
     .firstRow  td:nth-child(1),
     .trtotal td{
       font-weight: 540;
      //  color: #000;
       text-align: center;
     }
     .no-records-found{
       text-align: center;
     }
     .loading{
       margin-top: 80px;
      width: 80px;
    height: 80px;
    -webkit-animation: pl_a 1s steps(12) infinite;
    animation: pl_a 1s steps(12) infinite;
        background: url(data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIxMjAiIGhlaWdodD0iMTIwIiB2aWV3Qm94PSIwIDAgMTAwIDEwMCI+PHBhdGggZmlsbD0ibm9uZSIgZD0iTTAgMGgxMDB2MTAwSDB6Ii8+PHJlY3Qgd2lkdGg9IjciIGhlaWdodD0iMjAiIHg9IjQ2LjUiIHk9IjQwIiBmaWxsPSIjRTlFOUU5IiByeD0iNSIgcnk9IjUiIHRyYW5zZm9ybT0idHJhbnNsYXRlKDAgLTMwKSIvPjxyZWN0IHdpZHRoPSI3IiBoZWlnaHQ9IjIwIiB4PSI0Ni41IiB5PSI0MCIgZmlsbD0iIzk4OTY5NyIgcng9IjUiIHJ5PSI1IiB0cmFuc2Zvcm09InJvdGF0ZSgzMCAxMDUuOTggNjUpIi8+PHJlY3Qgd2lkdGg9IjciIGhlaWdodD0iMjAiIHg9IjQ2LjUiIHk9IjQwIiBmaWxsPSIjOUI5OTlBIiByeD0iNSIgcnk9IjUiIHRyYW5zZm9ybT0icm90YXRlKDYwIDc1Ljk4IDY1KSIvPjxyZWN0IHdpZHRoPSI3IiBoZWlnaHQ9IjIwIiB4PSI0Ni41IiB5PSI0MCIgZmlsbD0iI0EzQTFBMiIgcng9IjUiIHJ5PSI1IiB0cmFuc2Zvcm09InJvdGF0ZSg5MCA2NSA2NSkiLz48cmVjdCB3aWR0aD0iNyIgaGVpZ2h0PSIyMCIgeD0iNDYuNSIgeT0iNDAiIGZpbGw9IiNBQkE5QUEiIHJ4PSI1IiByeT0iNSIgdHJhbnNmb3JtPSJyb3RhdGUoMTIwIDU4LjY2IDY1KSIvPjxyZWN0IHdpZHRoPSI3IiBoZWlnaHQ9IjIwIiB4PSI0Ni41IiB5PSI0MCIgZmlsbD0iI0IyQjJCMiIgcng9IjUiIHJ5PSI1IiB0cmFuc2Zvcm09InJvdGF0ZSgxNTAgNTQuMDIgNjUpIi8+PHJlY3Qgd2lkdGg9IjciIGhlaWdodD0iMjAiIHg9IjQ2LjUiIHk9IjQwIiBmaWxsPSIjQkFCOEI5IiByeD0iNSIgcnk9IjUiIHRyYW5zZm9ybT0icm90YXRlKDE4MCA1MCA2NSkiLz48cmVjdCB3aWR0aD0iNyIgaGVpZ2h0PSIyMCIgeD0iNDYuNSIgeT0iNDAiIGZpbGw9IiNDMkMwQzEiIHJ4PSI1IiByeT0iNSIgdHJhbnNmb3JtPSJyb3RhdGUoLTE1MCA0NS45OCA2NSkiLz48cmVjdCB3aWR0aD0iNyIgaGVpZ2h0PSIyMCIgeD0iNDYuNSIgeT0iNDAiIGZpbGw9IiNDQkNCQ0IiIHJ4PSI1IiByeT0iNSIgdHJhbnNmb3JtPSJyb3RhdGUoLTEyMCA0MS4zNCA2NSkiLz48cmVjdCB3aWR0aD0iNyIgaGVpZ2h0PSIyMCIgeD0iNDYuNSIgeT0iNDAiIGZpbGw9IiNEMkQyRDIiIHJ4PSI1IiByeT0iNSIgdHJhbnNmb3JtPSJyb3RhdGUoLTkwIDM1IDY1KSIvPjxyZWN0IHdpZHRoPSI3IiBoZWlnaHQ9IjIwIiB4PSI0Ni41IiB5PSI0MCIgZmlsbD0iI0RBREFEQSIgcng9IjUiIHJ5PSI1IiB0cmFuc2Zvcm09InJvdGF0ZSgtNjAgMjQuMDIgNjUpIi8+PHJlY3Qgd2lkdGg9IjciIGhlaWdodD0iMjAiIHg9IjQ2LjUiIHk9IjQwIiBmaWxsPSIjRTJFMkUyIiByeD0iNSIgcnk9IjUiIHRyYW5zZm9ybT0icm90YXRlKC0zMCAtNS45OCA2NSkiLz48L3N2Zz4=),no-repeat;
        background-size: 80px 80px;
     }
     @keyframes pl_a {
        100% {
           transform: rotate(360deg);
        }
     }
     .load-container {
        width: 86%;
        height: 300px;
        overflow: hidden;
        -moz-box-sizing: border-box;
        box-sizing: border-box;
        display: flex;
        flex-direction: column;
        align-items: center;
     }
     .loader  {
        font-size: 10px;
        // margin: 0 auto;
        margin-left: 42%;
        margin-top: 100px;
        width: 1em;
        height: 1em;
        border-radius: 50%;
        // text-indent: -9999em;
        // position: relative;
        // animation: mymove 1.5s infinite linear;
        // -webkit-animation: mymove 1.5s infinite linear;
        // -webkit-transform: translateZ(0);
        // -ms-transform: translateZ(0);
        // transform: translateZ(0);
     }
    .table-next-child::-webkit-scrollbar{
      width: 8px;
      height: 8px;
    }
    .table-next-child::-webkit-scrollbar-thumb{
      background-color:rgb(204, 200, 200);
      background-clip:padding-box;
      -webkit-border-radius: 4px;
      -moz-border-radius: 4px;
      border-radius: 4px;
    }

    .table-next-child::-webkit-scrollbar-thumb:hover {
      // background-color:rgb(168,168,168);
      background-color:#aaa;
    }
    .table-next-child::-webkit-scrollbar-track-piece { //滚动条凹槽的颜色，还可以设置边框属性
      // background-color:rgb(241, 241, 241);
      background-color:rgb(253, 250, 250);
    }
    // 固定首列
    .fix-column {
        width: 80px;
        overflow: hidden;
        position: absolute;
        top: 0;
        left: 0;
        box-shadow: 0 5px 6px #ccc;
        z-index: 2;
        th{
          height: 33.33px;
        }
     }
     .fix-column .fixed-table-body {
    overflow-x: hidden;
    overflow-y: scroll;
    position: absolute;
    left: 0;
    z-index: 1;
    width: 81px;
}
.fixed-table-header {
    position: absolute;
    overflow: hidden;
    left: 0;
    top: 0;
    z-index: 2;
    width: 81px;
}
</style>