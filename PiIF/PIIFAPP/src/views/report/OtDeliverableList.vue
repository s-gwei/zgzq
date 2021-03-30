<template>
    <a-card :bordered="false" :style="{'width':screenWidth + 'px',height:screenHeight + 'px'}" style='{"backgroundColor":"pink"}'>
       <!-- <div class="table-page-search-wrapper wrapperselect">
            <a-form layout="inline">
                <a-row>
                <a-col :md="6" :sm="16">
                  <a-form-item label="时间">
                    <a-range-picker
                      :showTime="{ format: 'YYYY-MM-DD HH:mm:DD' }"
                      format="YYYY-MM-DD HH:mm:DD"
                      :placeholder="['开始时间', '结束时间']"
                      style="width:80%"
                      :value="createValue"
                      @change="ontimeChange"
                    />
                  </a-form-item>
                </a-col>
                <a-col :md="6" :sm="16">
                  <a-form-item label="部门">
                     <a-select v-model="group" placeholder="请选择" mode="multiple">
                        <a-select-option :value="item.id" v-for="(item,index) in resultgroupData" :key="index">{{item.name}}</a-select-option>   
                    </a-select>
                  </a-form-item>
                </a-col>
                <a-col :md="12" :sm="16" class="btn">
                  <span class="table-page-search-submitButtons">
                    <a-button style="margin-left: 8px,color: #000" @click="resetSearchForm" :disabled="isLoading" class="reset">重置</a-button>
                    <a-button type="primary" style="margin-left: 8px" @click="getData()" :disabled="isLoading">查询</a-button>
                  </span>
                </a-col>
              </a-row>
            </a-form>
       </div> -->
       <div class="tableContainer">
           <div class="title">
               <p>OT指标交付报表</p>
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
                        <th class="description">描述</th>
                        <th>汇报偏差</th>
                        <th>汇报困难度</th>
                        <th>汇报时间</th>
                        <th>广度</th>
                        <th>关键度</th>
                        <th>标准偏差</th>
                        <th >标准困难度</th>
                   </tr>
               </thead>
               <tbody>
                   <template v-for="(item,index) in tableDate" >
                           <tr  class="otherRow" :key="index">
                               <td>{{index + 1}}</td>
                                <td>{{item.description}}</td>
                                <td>{{item.deviation_report}}</td>
                                <td>{{item.difficulty_report}}</td>
                                <td>{{item.reportTime}}</td>
                                <td>{{item.breadth}}</td>
                                <td>{{item.criticiailty}}</td>
                                <td>{{item.standard_deviation_value}}</td>
                                <td>{{item.standard_difficulty_value}}</td>
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
                        <th class="description">描述</th>
                        <th>汇报偏差</th>
                        <th>汇报困难度</th>
                        <th>汇报时间</th>
                        <th>广度</th>
                        <th>关键度</th>
                        <th>标准偏差</th>
                        <th >标准困难度</th>
                   </tr>
               </thead>
               <tbody>
                   <template v-for="(item,index) in tableDate" >
                           <tr  class="otherRow" :key="index">
                               <td>{{index + 1}}</td>
                                <td>{{item.description}}</td>
                                <td>{{item.deviation_report}}</td>
                                <td>{{item.difficulty_report}}</td>
                                <td>{{item.reportTime}}</td>
                                <td>{{item.breadth}}</td>
                                <td>{{item.criticiailty}}</td>
                                <td>{{item.standard_deviation_value}}</td>
                                <td>{{item.standard_difficulty_value}}</td>
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
const data = []
 
export default {
  name: "otDeliverableList",
  components: {},
  data() {
      return{
          // labelCol: {
          //   xs: { span: 24 },
          //   sm: { span: 5 },
          // },
          // wrapperCol: {
          //   xs: { span: 24 },
          //   sm: { span: 16 },
          // },
          // createValue: [],
          time: [],
          group: [],
          resultgroupData: [],
          ispagination: {
            pageSize: 20,
            pageNo: 1,
            total: 0,
            pageSizeOptions: ['20', '50', '100']
          },
          tableDate: [],
          queryParam: {},
          queryParamName: "",
          loadingwidth: '100%',
          url: {
              tableDate: "/OTDrivice/OTTable",
              export: "/OTDrivice/exportOTExcel",
              groupDate: "/OTDrivice/selectGroup"
           },
           isLoading: false,
           screenWidth: null,
           screenHeight: null,
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
    //  ontimeChange(date, dateString) {
    //     if(date.length>0){
    //         this.createValue = date
    //       this.time[0] = dateString[0]
    //       this.time[1] = dateString[1]
    //     }else{
    //       this.time=[];
    //     }
    //   },
      // getgroupDate(){
      //     const  url = this.url.groupDate,_this=this;
      //      getAction(url,this.queryParam,'get').then((res) => {
      //         //  _this.isLoading = false
      //         if(res.success && res.result){
      //             _this.resultgroupData = res.result
      //         }
      //      })
      // },
      getData(pageNo, pageSize){
          const  url = this.url.tableDate,_this=this,params={};
          params.group = this.$route.query.departmentId,params.time =  this.$route.query.startTime ? this.$route.query.startTime +","+this.$route.query.endTime : null,params.planId=this.$route.query.planId
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
            paramsUrl += key + "=" + this.queryParam[key] + "&"
        }
        paramsUrl =  paramsUrl.substr(0,paramsUrl.length - 1)
        // window.open('http://192.168.1.124:9999/jeecg-boot'+ this.url.export, "_blank");
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
     .tdBorderRight{
       border-right: 1px solid #E9E9E9;
       border-left: 1px solid transparent
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
      .tr999{
        //   height: calc(100% - 200px);
        height: 300px;
      }
     .no-records-found{
       text-align: center;
     }
     .description{
         width: 400px !important;
     }
</style>
