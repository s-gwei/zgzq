<template>
    <a-card :bordered="false" :style="{'width':screenWidth + 'px',height:screenHeight + 'px'}" style='{"backgroundColor":"pink"}'>
       <div class="tableContainer">
           <div class="title">
               <p>IN评定报表</p>
               <a-button type="primary" style="margin-left: 8px"  @click="exportExcel" :disabled="isLoading">导出为Excel</a-button>
           </div>
           <!-- table部分start -->
           <div class="table-container table" id="table" >
                <!-- header -->
          <div class="table-child"  id="theadDiv" >
             <table id="tableId1" cellpadding="0" cellspacing="0">
               <thead>
                   <tr>
                        <th>序号</th>
                        <th>任务名称</th>
                        <th>指标编码</th>
                        <th>指标描述</th>
                        <th>权重</th>
                        <th>指标评定值</th>
                        <th>评定描述</th>
                        <th>汇报时间</th>
                        <!-- <th>是否删除</th>
                        <th>是否存在</th> -->
                        <th>更改次数</th>
                   </tr>
               </thead>
               <tbody>
                   <template v-for="(item,index) in tableDate" >
                           <tr  class="otherRow" :key="index">
                                <td>{{index + 1}}</td>
                                <td><div class="taskName">{{item.name}}</div></td>
                                <td>{{item.code}}</td>
                                <td>{{item.codeDescription}}</td>
                                <td>{{item.weights}}</td>
                                <td>{{item.ot_rating}}</td>
                                <td>{{item.description}}</td>
                                <td>{{item.reportTime}}</td>
                                <!-- <td>{{item.is_deleted == 1 ? "删除" : (item.is_deleted == 0 ? "未删除" : "")}}</td>
                                <td>{{item.is_persisted ? "存在" : (item.is_persisted == 0 ? "不存在" : "")}}</td> -->
                                <td>{{item.update_count}}</td>
                           </tr>
                   </template>
               </tbody>
            </table>
          </div>
          <!-- header ending -->
          <!-- body -->
          <div class="table-next-child" id="tbodyDiv">
             <table id="tableId2" cellpadding="0" cellspacing="0" style="height: 100%">
               <thead>
                   <tr>
                       <th>序号</th>
                        <th>任务名称</th>
                        <th>指标编码</th>
                        <th>指标描述</th>
                        <th>权重</th>
                        <th>指标评定值</th>
                        <th>评定描述</th>
                        <th>汇报时间</th>
                        <th>更改次数</th>
                   </tr>
               </thead>
               <tbody>
                   <template v-for="(item,index) in tableDate" >
                           <tr  class="otherRow" :key="index">
                                <td>{{index + 1}}</td>
                                <td><div class="taskName">{{item.name}}</div></td>
                                <td>{{item.code}}</td>
                                <td>{{item.codeDescription}}</td>
                                <td>{{item.weights}}</td>
                                <td>{{item.ot_rating}}</td>
                                <td>{{item.description}}</td>
                                <td>{{item.reportTime}}</td>
                                <!-- <td>{{item.is_deleted == 1 ? "删除" : (item.is_deleted == 0 ? "未删除" : "")}}</td>
                                <td>{{item.is_persisted ? "存在" : (item.is_persisted == 0 ? "不存在" : "")}}</td> -->
                                <td>{{item.update_count}}</td>
                           </tr>
                   </template>
                   <template v-if="isLoading">
                      <tr class="tr999">
                        <td colspan="999" style="text-align: center">
                            <div class='load-container' style="{height: adaptiveload+'px',width: loadingwidth}">
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
            <a-pagination
              style="float: right;margin-top: 12px"
              show-quick-jumper
              v-model="ispagination.pageNo"
              show-size-changer
              :page-size.sync="ispagination.pageSize"
              :pageSizeOptions="ispagination.pageSizeOptions"
              :total="ispagination.total"
              :showTotal="total => `总共有  ${total}  条数据`"
              @showSizeChange="onShowSizeChange"
              @change="onChange"
            />
       </div>
  </a-card>
</template>
<script>
import {getAction} from '@/api/manage';
import moment from 'moment';
import 'moment/locale/zh-cn';
const singleProject = require('@assets/json/ot.json')
const data = []
 
export default {
  name: "inRatingList",
  components: {},
  data() {
      return{
          time: [],
          group: [],
          ispagination: {
            pageSize: 20,
            pageNo: 1,
            total: 0,
            pageSizeOptions: ['20', '50', '100']
          },
          tableDate: [],
          queryParam: {},
          loadingwidth: '100%',
          singleProject,
          url: {
              tableDate: "/OTDrivice/selectINTable",
              export: "/OTDrivice/exportINExcel",
              groupDate: "/OTDrivice/selectGroup"
           },
           isLoading: false,
           screenWidth: null,
           screenHeight: null
      }
  },
  watch: {
  },
  mounted(){
     this.screenWidth = document.body.clientWidth;
     this.screenHeight = document.body.clientHeight;
    //  this.getgroupDate()
      this.getData()
      window.onresize = () => {
         return (() => {
           this.screenWidth = document.body.clientWidth;
           this.screenHeight = document.body.clientHeight;
         })();
       };
  },
  beforeDestroy(){
  },
  methods:{
      getData(pageNo, pageSize){
          const  url = this.url.tableDate,_this=this,params={};
          params.group = this.$route.query.departmentId,params.time = this.$route.query.startTime ? this.$route.query.startTime +","+this.$route.query.endTime : null,params.planId=this.$route.query.planId
          this.isLoading = true
          var obj = {
              pageNo: pageNo ? pageNo : this.ispagination.pageNo,
              pageSize: pageSize ? pageSize : this.ispagination.pageSize
          }
          this.queryParam = Object.assign({},params,obj)
          // delete this.queryParam.name
          getAction(url,this.queryParam,'get').then((res) => {
            _this.isLoading = false
           if(res.success && res.result){
              _this.$set(_this,'tableDate',res.result.records)
              this.ispagination.total = res.result.total
              this.ispagination.pageNo = res.result.current
              _this.$set(_this,'tableDate',res.result.records)
           }else{
              _this.$nextTick(function(){
              })
           }
         })
         .catch(function(err){
              _this.isLoading = false
             _this.$nextTick(function(){
                //  _this.getAdaptiveH()
              })
         })
      },
       //  分页
      onShowSizeChange (page, size) {
        this.$set(this,'tableDate',[])
        this.getData(page, size)
      },
      onChange (page, size) {
        this.$set(this,'tableDate',[])
        this.getData(page, size)
      },
      // 重置
      resetSearchForm(){
        //   this.ontimeChange([],[])
          this.createValue = []
          this.time = []
          this.group = []
          this.getData(1,20)
      },
      // 导出
      exportExcel(){
        const  url = this.url.export,_this=this;
        var paramsUrl = "?";
        for(let key in this.queryParam){
          if(this.queryParam[key]){
            paramsUrl += key + "=" + this.queryParam[key] + "&"
          }
        }
        paramsUrl =  paramsUrl.substr(0,paramsUrl.length - 1)
        // window.open('http://192.168.2.176:9999/jeecg-boot'+ this.url.export, "_blank");
        window.open( window._CONFIG['domianURL']+ url+ paramsUrl, "_blank");
      },
    }
};
</script>
<style scoped lang="less">
     /deep/ .ant-card-body{
       height: 100%;
       padding: 14px;
       box-sizing: border-box;
     }
     body{
       height: 100%;
       background: #F4F5F7;
     }
     .ant-card{
        background: #F4F5F7;
     }
     .table-page-search-wrapper .table-page-search-submitButtons {
         float: right;
        //  padding-right: 10%;
        
     }
     .reset{
           margin-left: 10px;
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
     .btn{
       padding-right: 5%!important;
     }
     .tableContainer{
        //  height: calc(100% - 60px);
         height: 100%;
         background-color: #fff;
         padding: 10px;
         border-radius: 4px;
     }
     .title{
         display: flex;
         justify-content: space-between;
         width: 99%;
         p{
             line-height: 34px;
             color: #000;
             font-size: 16px;
         }
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
        height: calc(100% - 200px);
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
    .fixed-table-body::-webkit-scrollbar{
      width: 6px;
      height: 6px;
    }
    .fixed-table-body::-webkit-scrollbar-thumb{
      background-color:rgb(193, 193, 193);
      background-clip:padding-box;
      -webkit-border-radius: 4px;
      -moz-border-radius: 4px;
      border-radius: 4px;
    }

    .fixed-table-body::-webkit-scrollbar-thumb:hover {
      // background-color:rgb(168,168,168);
      background-color:#aaa;
    }
    .fixed-table-body::-webkit-scrollbar-track-piece { //滚动条凹槽的颜色，还可以设置边框属性
      // background-color:rgb(241, 241, 241);
      background-color:rgb(253, 250, 250);
    }

    // table
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
     table thead tr{
      //  background: #DEDEDE;
      background: #ccc;
       th{
       border: 1px solid #DEDEDE;
       border-right: 1px solid #E9E9E9;
       border-left: 1px solid transparent
       }
     }
     table tbody tr:hover{
         background: rgba(160,214,245,.3);
     }
     .table{
          min-width: 100%;
         max-height: 800px;
         height: calc(100% - 100px);
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
                     height: 35px;
                     overflow: hidden;
                     width: calc(100% - 8px);
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
            height: 100%;
            max-height: 100%;
            overflow-y: scroll;
            overflow-x: auto;
         }
     }
     tbody td{
         text-align: center;
         padding: 6px 10px;
         color: rgba(0,0,0,.7)
     }
     .tableContainer #table tr th:nth-child(1),
     .tableContainer #table tr th:nth-child(2){
        width: 100px;
        min-width: 100px;
        word-wrap:break-word;
      }
      .tr999{
        //   height: calc(100% - 200px);
        height: 300px;
      }
     .no-records-found{
       text-align: center;
     }
     .description{
         width: 360px !important;
     }
     .taskName{
       min-width: 160px;
     }
</style>
