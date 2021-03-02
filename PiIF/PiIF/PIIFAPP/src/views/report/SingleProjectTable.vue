<template>
    <a-card :bordered="false">
       <div class="table-page-search-wrapper wrapperselect">
          <a-form layout="inline">
            <a-row :gutter="48">
                <a-col :md="6" :sm="16">
                    <a-form-item label="资源部门">
                      <a-select v-model="queryParam.ResourceSector" placeholder="请选择" default-value="0">
                        <a-select-option :value="item.value" v-for="(item,index) in resourceDepartment" :key="index">{{item.name}}</a-select-option>  
                      </a-select>
                    </a-form-item>
                </a-col>
                <a-col :md="6" :sm="16">
                    <a-form-item label="员工姓名">
                      <a-select v-model="queryParam.name" placeholder="请选择" default-value="0">
                        <a-select-option :value="itm.value"  v-for="(itm,idx) in staffName" :key="idx">{{itm.name}}</a-select-option>
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
                      <a-button style="margin-left: 8px,color: #000" @click="resetSearchForm" :disabled="isLoading">重置</a-button>
                      <a-button type="primary" style="margin-left: 8px" @click="getData()" :disabled="isLoading">查询</a-button>
                    </span>
                </a-col>
            </a-row>
          </a-form>
       </div>
       <div class="tableContainer" :style="{height: adaptiveContainH +'px'}">
       <div class="title">
          <p>任务执行风险表</p>
          <!-- <a-button shape="circle" type="primary" style="margin-left: 8px"  @click="showRuler">?</a-button> -->
          <a class="help" @click="visibleCreateModal = true"><a-icon type="question-circle-o"></a-icon><span>帮助</span></a>
          <a-button type="primary" style="margin-left: 8px"  @click="exportExcel" :disabled="isLoading">导出为Excel</a-button>
       </div>
       <!-- table部分start -->
       <div class="table-container table" id="table" :style="{height: adaptiveH+'px'}">
          <!-- header -->
          <div class="table-child"  id="theadDiv" >
             <table id="tableId1" cellpadding="0" cellspacing="0">
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
                                <td :rowspan="getrowNum(itm)">{{item['Executive']}}</td>
                                <td :rowspan="getrowNum(itm)">{{item['taskname']}}</td>
                                <td>{{item.weight}}</td>
                                <td>{{item.standardDeviation}}</td>
                                <td>{{item.reportingDeviations}}</td>
                                <td>{{item.qualityInfluenceFactor}}</td>
                                <td>{{item.StandardPeriod}}</td>
                                <td>{{item.reportPeriod}}</td>
                                <td>{{item.riskKPI}}</td>
                                <td>{{item.standardDeviation}}</td>
                                <td>{{item.reportingDeviations}}</td>
                                <td>{{item.standardDifficulty}}</td>
                                <td>{{item.reportingDifficulty}}</td>
                                <td>{{item.OutputaualityKPI}}</td>
                                <td>{{item.OutputQualityRisk}}</td>
                                <td>{{item.span}}</td>
                                <td>{{item.Criticality}}</td>
                                <td>{{item.ProjectRiskIndicators}}</td>
                                <td>{{item.OutputEvalua}}</td>
                                <td>{{item.Numbereleases}}</td>
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
                           <td class="tdBorderRight">{{getcolcontent('NumbereleasesAvg',itm)}}</td>
                        </tr>
                   </template>
               </tbody>
            </table>
          </div>
          <!-- header ending -->
          <!-- body -->
          <div class="table-next-child" id="tbodyDiv">
             <table id="tableId2" cellpadding="0" cellspacing="0">
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
                              <td :rowspan="getrowNum(itm)">{{item['Executive']}}</td>
                              <td :rowspan="getrowNum(itm)">{{item['taskname']}}</td>
                              <td>{{item.weight}}</td>
                              <td>{{item.standardDeviation}}</td>
                              <td>{{item.reportingDeviations}}</td>
                              <td>{{item.qualityInfluenceFactor}}</td>
                              <td>{{item.StandardPeriod}}</td>
                              <td>{{item.reportPeriod}}</td>
                              <td>{{item.reportPeriod}}</td>
                              <td>{{item.standardDeviation}}</td>
                              <td>{{item.reportingDeviations}}</td>
                              <td>{{item.standardDifficulty}}</td>
                              <td>{{item.reportingDifficulty}}</td>
                              <td>{{item.OutputaualityKPI}}</td>
                              <td>{{item.OutputQualityRisk}}</td>
                              <td>{{item.span}}</td>
                              <td>{{item.Criticality}}</td>
                              <td>{{item.ProjectRiskIndicators}}</td>
                              <td>{{item.OutputEvalua}}</td>
                              <td>{{item.Numbereleases}}</td>
                          </tr>
                          <tr v-if="index && index != itm.length -1" class="otherRow">
                               <!-- <td >z职能</td> -->
                               <td>{{item.weight}}</td>
                               <td>{{item.standardDeviation}}</td>
                               <td>{{item.reportingDeviations}}</td>
                               <td>{{item.qualityInfluenceFactor}}</td>
                               <td>{{item.StandardPeriod}}</td>
                               <td>{{item.reportPeriod}}</td>
                               <td>{{item.riskKPI}}</td>
                               <td>{{item.standardDeviation}}</td>
                               <td>{{item.reportingDifficulty}}</td>
                               <td>{{item.standardDifficulty}}</td>
                               <td>{{item.reportingDifficulty}}</td>
                               <td>{{item.OutputaualityKPI}}</td>
                               <td>{{item.OutputQualityRisk}}</td>
                               <td>{{item.span}}</td>
                               <td>{{item.Criticality}}</td>
                               <td>{{item.ProjectRiskIndicators}}</td>
                               <td>{{item.OutputEvalua}}</td>
                               <td>{{item.Numbereleases}}</td>
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
                           <td class="tdBorderRight">{{getcolcontent('NumbereleasesAvg',itm)}}</td>
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
       <a-modal title="任务执行评价表" destroyOnClose :width="800" :visible="visibleCreateModal" @cancel="visibleCreateModal=false">
            <!---->
            <div class="mainMadal">
              <div class="madalContent" v-for="(item,index) in modalContent" :key="index">
                <div class="title">{{item.title}}</div>
                <div class="rulers">{{item.rulers}}</div>
                <div  class="contents">
                    <div v-for="(itm,idx) in item.content" :key="idx" class="contentItem">{{itm}}</div>
                </div>
               </div>
            </div>
            <template slot="footer">
                <a-button style="display: none">关闭</a-button>
           </template>

       </a-modal>
  </a-card>
</template>
<script>
import {getAction} from '@/api/manage';
const pertJson = require('@assets/json/singleProject.json')
const modalContent = require('@assets/json/modalContent.json')
 
export default {
  name: "singleProjectTable",
  data() {
      return{
          tableDate: [],
          visibleCreateModal:false,
          queryParam: {},
          dom: null,
          adaptiveH: 400,
          adaptiveload: 400,
          loadingwidth: '100%',
          adaptiveContainH: 500,
          // 资源部门:
          resourceDepartment: [
            {name: "资源one",value: "1"},
            {name: "资源two",value: "2"},
            {name: "资源three",value: "3"},
            {name: "资源four",value: "4"},
            {name: "资源five",value: "5"}
          ],
          // 员工姓名查询字段
          staffName: [
            {name: "员工one",value: "01"},
            {name: "员工two",value: "02"},
            {name: "员工three",value: "03"},
            {name: "员工four",value: "04"},
            {name: "员工five",value: "05"}
          ],
          // pertJson,
          modalContent,
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
       this.dom = document.querySelector('#tbodyDiv')
       this.dom.addEventListener('scroll', this.scrollX);
       this.getData()
  },
  beforeDestroy(){
      this.dom.removeEventListener('scroll', this.scrollX);
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
        //  loding加载的高度与table剩余等高,宽度与屏幕等宽
         this.adaptiveload = this.adaptiveH -68
         this.loadingwidth = document.querySelector("#app").clientWidth + 'px'
      },
      scrollX(){
        console.log('enter');
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
              console.log(err);
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
        const  url = this.url.export_this=this;
        console.log(this.queryParam);
        var paramsUrl = "?";
        for(let key in this.queryParam){
            paramsUrl += key + "=" + this.queryParam[key] + "&"
        }
        paramsUrl =  paramsUrl.substr(0,paramsUrl.length - 1)
        // window.open('http://192.168.2.162:8080/jeecg-boot'+ this.url.export, "_blank");
        window.open( window._CONFIG['domianURL']+ url+ paramsUrl, "_blank");
      },
      // 查看计算规则
      showRuler(){
        console.log('enter');
        this.visibleCreateModal = true
      },
      // 弹框
      handleCreateModalOk(){

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
             width: 82.45%;
             line-height: 34px;
             color: #000;
             font-size: 16px;
         }
         .help{
           color: #999;
           font-size: 12px;
           line-height: 34px;
           span{
             padding-right: 10px;
             padding-left: 2px;
           }
         }
     }
     table{
         table-layout:fixed;
          word-wrap: break-word; 
          word-break: break-all;
          min-width: 100%;
          border-collapse: collapse; 
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
           min-width: 90px;
          padding: 3px 8px;
          text-align: center;
          white-space: nowrap;
         }
     }
     table thead tr:nth-child(1){
      //  background: #DEDEDE;
      background: #ccc;
       th{
       border: 1px solid #DEDEDE;
       border-right: 1px solid #E9E9E9;
       border-left: 1px solid transparent
       }
     }
     .tdBorderRight{
       border-right: 1px solid #E9E9E9;
       border-left: 1px solid transparent
     }
     .table{
          min-width: 100%;
         max-height: 800px;
         height: 300px;
         position: relative;
         overflow-x: auto;
        //  overflow-y: hidden;
         .table-child,
         .table-next-child{
             min-width: 800px;
             table{
                //  width: 100%;
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
                     width: calc(100% - 6px);
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
     
     .firstRow td:nth-child(6),
     .firstRow td:nth-child(9),
     .otherRow td:nth-child(7),
     .firstRow td:nth-last-child(1),
     .firstRow td:nth-last-child(6),
     .firstRow td:nth-last-child(3),
     .firstRow td:nth-last-child(7),
      .otherRow td:nth-last-child(1),
     .otherRow td:nth-last-child(7),
     .otherRow td:nth-last-child(3),
     .otherRow td:nth-last-child(6),
     .otherRow td:nth-child(4){
         background: #F4F5F7;
         font-weight: 540;
     }
     tbody td{
       text-align: right;
         padding: 6px 10px;
         color: rgba(0,0,0,.7)
     }
     #table tbody .trtotal{
      //  background: #DEDEDE;
      background: #ccc;
       td:nth-child(2n){
         text-align: right;
         color: #333333
       }
       td:nth-child(2n + 1){
         text-align: right;
         border-right: none
       }
     }
     .firstRow  td:nth-child(1),
     .firstRow  td:nth-child(2),
     .trtotal td{
       font-weight: 540;
       color: #000;
       text-align: center;
     }
     .tableContainer #table tr th:nth-child(1),
     .tableContainer #table tr th:nth-child(2){
        width: 100px;
        min-width: 100px;
        word-wrap:break-word;
      }
      #table .firstRow td:nth-child(-n+2){
        width: 100px;
        word-wrap:break-word;
      }
     .no-records-found{
       text-align: center;
     }
     .load-container {
        background-color: #fff;
        width: auto;
        position: relative;
        overflow: hidden;
        -moz-box-sizing: border-box;
        box-sizing: border-box;
     }
     .loader  {
        font-size: 10px;
        // margin-left: 42%;
        top: 50%;
        left: 50%;
        transform: translate(-50%,-50%);
        width: 1em;
        height: 1em;
        border-radius: 50%;
        // text-indent: -9999em;
        position: relative;
        animation: mymove 1.5s infinite linear;
        -webkit-animation: mymove 1.5s infinite linear;
        -webkit-transform: translateZ(0);
        -ms-transform: translateZ(0);
        transform: translateZ(0);
     }
     .loader::before{
        content: '加载中';
        display: block;
        color: #000;
        font-size: 10px;
        width: 45px;
        left: -15px;
        position: absolute;
    }
    @keyframes mymove {
        0%,
        100% {
            box-shadow: 0 -3em 0 0.2em #DEDEDE, 2em -2em 0 0em #DEDEDE, 3em 0 0 -1em #DEDEDE, 2em 2em 0 -1em #DEDEDE, 0 3em 0 -1em #DEDEDE, -2em 2em 0 -1em #DEDEDE, -3em 0 0 -1em #DEDEDE, -2em -2em 0 0 #DEDEDE;
        }
        12.5% {
            box-shadow: 0 -3em 0 0 #DEDEDE, 2em -2em 0 0.2em #DEDEDE, 3em 0 0 0 #DEDEDE, 2em 2em 0 -1em #DEDEDE, 0 3em 0 -1em #DEDEDE, -2em 2em 0 -1em #DEDEDE, -3em 0 0 -1em #DEDEDE, -2em -2em 0 -1em #DEDEDE;
        }
        25% {
            box-shadow: 0 -3em 0 -0.5em #DEDEDE, 2em -2em 0 0 #DEDEDE, 3em 0 0 0.2em #DEDEDE, 2em 2em 0 0 #DEDEDE, 0 3em 0 -1em #DEDEDE, -2em 2em 0 -1em #DEDEDE, -3em 0 0 -1em #DEDEDE, -2em -2em 0 -1em #DEDEDE;
        }
        37.5% {
            box-shadow: 0 -3em 0 -1em #DEDEDE, 2em -2em 0 -1em #DEDEDE, 3em 0em 0 0 #DEDEDE, 2em 2em 0 0.2em #DEDEDE, 0 3em 0 0em #DEDEDE, -2em 2em 0 -1em #DEDEDE, -3em 0em 0 -1em #DEDEDE, -2em -2em 0 -1em #DEDEDE;
        }
        50% {
            box-shadow: 0 -3em 0 -1em #DEDEDE, 2em -2em 0 -1em #DEDEDE, 3em 0 0 -1em #DEDEDE, 2em 2em 0 0em #DEDEDE, 0 3em 0 0.2em #DEDEDE, -2em 2em 0 0 #DEDEDE, -3em 0em 0 -1em #DEDEDE, -2em -2em 0 -1em #DEDEDE;
        }
        62.5% {
            box-shadow: 0 -3em 0 -1em #DEDEDE, 2em -2em 0 -1em #DEDEDE, 3em 0 0 -1em #DEDEDE, 2em 2em 0 -1em #DEDEDE, 0 3em 0 0 #DEDEDE, -2em 2em 0 0.2em #DEDEDE, -3em 0 0 0 #DEDEDE, -2em -2em 0 -1em #DEDEDE;
        }
        75% {
            box-shadow: 0em -3em 0 -1em #DEDEDE, 2em -2em 0 -1em #DEDEDE, 3em 0em 0 -1em #DEDEDE, 2em 2em 0 -1em #DEDEDE, 0 3em 0 -1em #DEDEDE, -2em 2em 0 0 #DEDEDE, -3em 0em 0 0.2em #DEDEDE, -2em -2em 0 0 #DEDEDE;
        }
        87.5% {
            box-shadow: 0em -3em 0 0 #DEDEDE, 2em -2em 0 -1em #DEDEDE, 3em 0 0 -1em #DEDEDE, 2em 2em 0 -1em #DEDEDE, 0 3em 0 -1em #DEDEDE, -2em 2em 0 0 #DEDEDE, -3em 0em 0 0 #DEDEDE, -2em -2em 0 0.2em #DEDEDE;
        }
    }

    @-webkit-keyframes mymove
    /*Safari and Chrome*/
    {
        0%,
        100% {
            box-shadow: 0 -3em 0 0.2em #DEDEDE, 2em -2em 0 0em #DEDEDE, 3em 0 0 -1em #DEDEDE, 2em 2em 0 -1em #DEDEDE, 0 3em 0 -1em #DEDEDE, -2em 2em 0 -1em #DEDEDE, -3em 0 0 -1em #DEDEDE, -2em -2em 0 0 #DEDEDE;
        }
        12.5% {
            box-shadow: 0 -3em 0 0 #DEDEDE, 2em -2em 0 0.2em #DEDEDE, 3em 0 0 0 #DEDEDE, 2em 2em 0 -1em #DEDEDE, 0 3em 0 -1em #DEDEDE, -2em 2em 0 -1em #DEDEDE, -3em 0 0 -1em #DEDEDE, -2em -2em 0 -1em #DEDEDE;
        }
        25% {
            box-shadow: 0 -3em 0 -0.5em #DEDEDE, 2em -2em 0 0 #DEDEDE, 3em 0 0 0.2em #DEDEDE, 2em 2em 0 0 #DEDEDE, 0 3em 0 -1em #DEDEDE, -2em 2em 0 -1em #DEDEDE, -3em 0 0 -1em #DEDEDE, -2em -2em 0 -1em #DEDEDE;
        }
        37.5% {
            box-shadow: 0 -3em 0 -1em #DEDEDE, 2em -2em 0 -1em #DEDEDE, 3em 0em 0 0 #DEDEDE, 2em 2em 0 0.2em #DEDEDE, 0 3em 0 0em #DEDEDE, -2em 2em 0 -1em #DEDEDE, -3em 0em 0 -1em #DEDEDE, -2em -2em 0 -1em #DEDEDE;
        }
        50% {
            box-shadow: 0 -3em 0 -1em #DEDEDE, 2em -2em 0 -1em #DEDEDE, 3em 0 0 -1em #DEDEDE, 2em 2em 0 0em #DEDEDE, 0 3em 0 0.2em #DEDEDE, -2em 2em 0 0 #DEDEDE, -3em 0em 0 -1em #DEDEDE, -2em -2em 0 -1em #DEDEDE;
        }
        62.5% {
            box-shadow: 0 -3em 0 -1em #DEDEDE, 2em -2em 0 -1em #DEDEDE, 3em 0 0 -1em #DEDEDE, 2em 2em 0 -1em #DEDEDE, 0 3em 0 0 #DEDEDE, -2em 2em 0 0.2em #DEDEDE, -3em 0 0 0 #DEDEDE, -2em -2em 0 -1em #DEDEDE;
        }
        75% {
            box-shadow: 0em -3em 0 -1em #DEDEDE, 2em -2em 0 -1em #DEDEDE, 3em 0em 0 -1em #DEDEDE, 2em 2em 0 -1em #DEDEDE, 0 3em 0 -1em #DEDEDE, -2em 2em 0 0 #DEDEDE, -3em 0em 0 0.2em #DEDEDE, -2em -2em 0 0 #DEDEDE;
        }
        87.5% {
            box-shadow: 0em -3em 0 0 #DEDEDE, 2em -2em 0 -1em #DEDEDE, 3em 0 0 -1em #DEDEDE, 2em 2em 0 -1em #DEDEDE, 0 3em 0 -1em #DEDEDE, -2em 2em 0 0 #DEDEDE, -3em 0em 0 0 #DEDEDE, -2em -2em 0 0.2em #DEDEDE;
        }
    }
    .table-next-child::-webkit-scrollbar{
      width: 6px;
      height: 6px;
    }
    .table-next-child::-webkit-scrollbar-thumb{
      background-color:rgb(193, 193, 193);
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


    // 弹窗样式
    /deep/ .ant-modal{
      position: relative;
      top: 50px;
      height: calc(100% - 50px);
      width: 60%!important;
    }
    /deep/ .ant-modal-footer{
      border-top: 1px solid transparent;
      padding: 6px 16px;
    }
    /deep/ .ant-modal-content{
      height: calc(100% - 60px);
    }
    //  ant-modal-content{
       /deep/ .ant-modal-body{
          // max-height: 420px;
          // height: 80%;
          overflow: auto;
          height: calc(100% - 76px);
          padding: 0 24px 16px;
       }
       /deep/ .ant-modal-header{
         border-bottom: 1px solid transparent
       }
      /deep/ .ant-modal-close{
              position: absolute;
              top: -24px;
              right: 4px;
              color: #fff;
               border: 1px solid #fff;
              border-radius: 50%;
       }
       /deep/ .ant-modal-close-x{
         width: 10px;
         height: 10px;
         line-height: 0;
        
       }
       /deep/ .ant-modal-close svg {
         width: 8px;
         height: 8px;
       }
      /deep/ .ant-modal-header .ant-modal-title{
          text-align: center;
          border-bottom: 1px solid transparent;
          font-weight: bold;
          font-size: 18px;
          color: #333;
       }
       

    .mainMadal .madalContent{
         color: #333;
         font-weight: 500;
       .title{
         font-size: 16px;
         font-weight: 600;
         position: relative;
         &::before{
           display: block;
           content: '';
           position: absolute;
           left: -8px;
           top: 7px;
           width: 2px;
           height: 12px;
           background: #1890FF;
           display: block;
         }
       }
        
       .rulers{
         font-size: 14px;
         padding-top: 4px;
         padding-left: 4px;
         margin-bottom: 10px;
       }
       .contents{
         border: 1px solid #E9E9E9;
         border-radius: 4px;
         box-sizing: border-box;
         padding: 12px 20px;
         .contentItem{
           font-size: 12px;
           position: relative;
           line-height: 22px;
           &::before{
             display: block;
             content: "";
             position: absolute;
             left: -10px;
             top: 10px;
             background: #333;
             border-radius: 50%;
             border: 2px solid rgb(170, 160, 160);
             opacity: 0.6;
             display: block;
             box-sizing: border-box;
           }
           &:hover{
             background: rgba(233, 233, 233, .5);
             cursor: default;
           }
         }
       }

    }
    .mainMadal .madalContent:nth-child(n+2){
         margin-top: 18px;
    }
    // tr:hover{
    //   background: #F4F5F7;
    // }
</style>