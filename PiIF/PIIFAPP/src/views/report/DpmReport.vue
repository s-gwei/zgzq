<template>
    <div class="content-box" :style="{'width':screenWidth + 'px',height:screenHeight + 'px'}">
        <div class="title">
            <!-- <div class="srcImag"></div> -->
            <div class="text">研发项目管理可视化</div>
            <div class="mainText">汽车研究总院项目管理总览</div>
            <div class="time">
                <div class="currenttime">{{time}}</div>
                <div class="line"></div>
                <div class="dates">
                    <div>{{weekend}}</div>
                    <div>{{date}}</div>
                </div>
            </div>
        </div>
        <div class="perspective">
            <div class="item item01">
                <div class="header">项目类别统计</div>
                <div class="content">
                   <div id="container1"></div>
                    <div class="table">
                    <div class="chartTitle">
                        <i></i>
                        <span class="proItem">项目类别名称</span>
                        <span>数量</span>
                        <span>百分比</span>
                    </div>
                    <div class="chartCon">
                        <div class="chartConPer" v-for="(item,index) in categorySum" :key="index" @click="toItemSelect(item)">
                            <i :class="item.itemName" :style="{background: color[index]}"></i>
                            <span class="proItem">{{item.name}}</span>
                            <span>{{item.count}}</span>
                            <span>{{getDateToFixed(item.percentage * 100)}}</span>
                        </div>
                    </div>
             </div>
                </div>
            </div>
            <div class="item item02">
                <div class="header header02">项目级别统计</div>
                 <div class="content content2">
                    <div class="tableBox">
                      <table>
                      <thead>
                          <tr>
                            <th></th>
                            <th> </th>
                            <th></th>
                         </tr>
                      </thead>
                      <tbody>
                        <tr v-for="(item,index) in levelData" :key="index">
                          <td>
                            <div>{{item.name}} :</div>
                          </td>
                          <td>
                            <div class="progressBar">
                               <div class="progress" :style="{width: item.percentage*100+'%'}"></div>
                               <div class="propercentage" :style="{left: (item.percentage*100 +2)+'%'}" v-if="item.percentage != 0 && item.percentage < 0.9">{{getDateToFixed(item.percentage * 100)}}</div>
                               <div class="propercentage" :style="{left: (item.percentage*100 - 10)+'%'}" v-if="item.percentage >= 0.9">{{getDateToFixed(item.percentage * 100)}}</div>
                            </div>
                          </td>
                          <td>
                            <div>{{item.count}}</div>
                          </td>
                        </tr>
                      </tbody>
                    </table>
                    </div>
                </div>
            </div>
            <div class="item item03">
                <div class="header header03"  @click="visibleCreateModal=true">任务延期散点图</div>
                <div class="content">
                    <!-- <a-button type="primary">点击</a-button> -->
                    <div id="container3"></div>
                </div>
            </div>
            <div class="item item04">
                <div class="header header04">项目风险气泡图</div>
                <div class="content">
                    <div id="container4"></div>
                </div>
            </div>
            <div class="item item05">
                <div class="header header05">风险预防措施统计(全系统)</div>
                <div class="content">
                    <div id="container5"></div>
                </div>
            </div>
            <div class="item item06">
                <div class="header header06">预防措施认可数与未认可数统计</div>
                <div class="content">
                    <div id="container6"></div>
                </div>
            </div>
        </div>
        <a-drawer
          :title="drawTitle"
          placement="left"
          :drawerStyle="{
           border: '1px solid #ebedf0',
           borderRadius: '2px',
         }"
          :closable="false"
          :visible="visible"
          :after-visible-change="afterVisibleChange"
          :width="880"
          @close="onClose"
        >
          <!-- table部分start -->
           <div class="table-container table1" id="table" >
                <!-- header -->
          <div class="table-child"  id="theadDiv" >
             <table cellpadding="0" cellspacing="0" v-if="drawTitle == '风险列表'">
               <thead>
                   <tr>
                        <th class="index">序号</th>
                        <th>风险名称</th>
                        <th>风险描述</th>
                        <th>提出人</th>
                        <th>提出时间</th>
                   </tr>
               </thead>
               <tbody>
                   <template v-for="(item,index) in tableDate" >
                           <tr :key="index">
                                <td>{{index + 1}}</td>
                                <td><div>{{item.riskName}}</div></td>
                                <td>{{item.riskDescription}}</td>
                                <td>{{item.userName}}</td>
                                <td>{{item.createStamp}}</td>
                           </tr>
                   </template>
               </tbody>
            </table>
            <table  cellpadding="0" cellspacing="0" v-if="drawTitle == '预防措施列表'">
               <thead>
                   <tr>
                        <th class="index">序号</th>
                        <th>预防措施名称</th>
                        <th>涉及部门</th>
                        <th>预防措施描述</th>
                        <th>责任人</th>
                   </tr>
               </thead>
               <tbody>
                   <template v-for="(item,index) in tableDate" >
                           <tr :key="index" >
                                <td>{{index + 1}}</td>
                                <td>{{item.name}}</td>
                                <td>{{item.groupName}}</td>
                                <td>{{item.precaution}}</td>
                                <td>{{item.user_name}}</td>
                           </tr>
                   </template>
               </tbody>
            </table>
          </div>
          <!-- header ending -->
          <!-- body -->
          <div class="table-next-child" id="tbodyDiv">
             <table cellpadding="0" cellspacing="0" v-if="drawTitle == '风险列表'">
               <thead>
                   <tr>
                        <th class="index">序号</th>
                        <th>风险名称</th>
                        <th>风险描述</th>
                        <th>提出人</th>
                        <th>提出时间</th>
                   </tr>
               </thead>
               <tbody>
                   <template v-for="(item,index) in tableDate" >
                           <tr :key="index" @click="toRiskDetail(item,1)">
                                <td>{{index + 1}}</td>
                                <td><div>{{item.riskName}}</div></td>
                                <td>{{item.riskDescription}}</td>
                                <td>{{item.userName}}</td>
                                <td>{{item.createStamp}}</td>
                           </tr>
                   </template>
               </tbody>
            </table>
            <table  cellpadding="0" cellspacing="0" v-if="drawTitle != '风险列表'">
               <thead>
                   <tr>
                        <th class="index">序号</th>
                        <th>预防措施名称</th>
                        <th>涉及部门</th>
                        <th>预防措施描述</th>
                        <th>责任人</th>
                   </tr>
               </thead>
               <tbody v-if="drawTitle == '预防措施列表'">
                   <template v-for="(item,index) in tableDate" >
                           <tr :key="index"  @click="toRiskDetail(item,2)">
                                <td>{{index + 1}}</td>
                                <td>{{item.name}}</td>
                                <td>{{item.groupName}}</td>
                                <td>{{item.precaution}}</td>
                                <td>{{item.user_name}}</td>
                           </tr>
                   </template>
               </tbody>
                <tbody v-else>
                   <template v-for="(item,index) in tableDateApprove" >
                           <tr :key="index"  @click="toRiskDetail(item,2)">
                                <td>{{index + 1}}</td>
                                <td>{{item.name}}</td>
                                <td>{{item.groupName}}</td>
                                <td>{{item.precaution}}</td>
                                <td>{{item.user_name}}</td>
                           </tr>
                   </template>
               </tbody>
            </table>
          </div>
          <!-- body ending-->
           </div>
        </a-drawer>
        <a-modal :title="title" destroyOnClose :visible="visibleCreateModal" @cancel="detailCancel" class="detail">
            <!---->
            <div class="mainMadal" style="width=100%">
              <a-table id="table"
               :columns="columns"
               :data-source="gkRiskReport"
               rowKey="index"
               :pagination="pagination"
               :scroll="{ y: yScroll }"
               @change="handleTableChange"
             >
               <p slot="taskType" slot-scope="record" style="margin: 0" :class="record">
                 {{record === "normal" ? "正常进行任务" : (record === "finished" ? "已完成任务" :record === "overdue" ? "逾期未完成" : (record === "isoverdue" ? "可能逾期任务" : (record === "red" ? "逾期已完成" : "" )))}}
               </p>
              </a-table>
            </div>
            <template slot="footer">
                <a-button style="display: none">关闭</a-button>
           </template>
        </a-modal>
    </div>
</template>
<script>
import {getAction} from '@/api/manage';
import { Chart, Util } from '@antv/g2';
const gkRiskReport = require('@assets/json/gkRiskReport.json')
const projectJson = require('@assets/json/projectJson.json')
const departmentJson = require('@assets/json/departmentJson.json')
const columns=[
          {
            title: '序号',
            dataIndex: 'index',
            width: 60,
            align: 'center'
          },
          {
            title: '项目名称',
            dataIndex: 'projectName',
            width: 250,
            align: 'center'
          },
          {
            title: '任务名称',
            dataIndex: 'activityName',
            width: 200,
            align: 'center'
          },
          {
            title: '工作任务状态',
            dataIndex: 'taskType',
            width: 112,
            filters: [
              { value: 'normal', text: '正常进行任务' },
              { value: 'finished', text: '已完成任务' },
              { value: 'overdue', text: '逾期未完成' },
              { value: 'isoverdue', text: '可能逾期任务' },
              { value: 'red', text: '逾期已完成' }
            ],
            // filteredValue: filteredInfo.taskType || null,
            onFilter: function (value, record) {
              return value == record.taskType
            },
            scopedSlots: { customRender: 'taskType' },
            align: 'center'
          },
          {
            title: '周数',
            dataIndex: 'xaxis',
            width: 80,
            align: 'center',
            sorter: (a, b) => a.xaxis - b.xaxis,
          },
          {
            title: '偏差值',
            dataIndex: 'deviation',
            width: 80,
            align: 'center',
            sorter: (a, b) => a.deviation - b.deviation,
          },
          {
            title: '计划开始时间',
            dataIndex: 'targetStartTime',
            width: 132,
            align: 'center',
            sorter:function(a,b){
              const aSort = a.targetStartTime.split('-').join('')
              const bSort = b.targetStartTime.split('-').join('')
              return Number(aSort) - Number(bSort)
            },
          },
          {
            title: '计划完成时间',
            dataIndex: 'byTime',
            width: 132,
            align: 'center',
            sorter:function(a,b){
              const aSort = a.byTime.split('-').join('')
              const bSort = b.byTime.split('-').join('')
              return Number(aSort) - Number(bSort)
            },
          },
          {
            title: '预估完成时间',
            dataIndex: 'expectedFinishTime',
            width: 132,
            align: 'center',
            sorter:function(a,b){
              const aSort = a.expectedFinishTime.split('-').join('')
              const bSort = b.expectedFinishTime.split('-').join('')
              return Number(aSort) - Number(bSort)
            }
          },
          {
            title: '实际开始时间',
            dataIndex: 'actualStartTime',
            width: 132,
            align: 'center',
            sorter:function(a,b){
              const aSort = a.expectedFinishTime.split('-').join('')
              const bSort = b.expectedFinishTime.split('-').join('')
              return Number(aSort) - Number(bSort)
            }
          },
          {
            title: '实际完成时间',
            dataIndex: 'actualEndTime',
            width: 132,
            align: 'center',
            sorter:function(a,b){
              const aSort = a.actualEndTime.split('-').join('')
              const bSort = b.actualEndTime.split('-').join('')
              return Number(aSort) - Number(bSort)
            }
          }
        ]
export default {
    name: "dpmReport",
    data(){
        return {
            drawTitle: "预防措施列表",
            screenWidth: 1000,
            screenHeight: 500,
            scrollHeight: 500,
            visible: false,
            time: "",
            date: "",
            weekend: "",
            gkRiskReport:[],//任务延期
            projectJson:[],//项目风险系数气泡数据
            departmentJson:[],//部门风险系数
            color: ['#22a668','#147dde','#ff8303','#ffcc29','#8ab6d6',"#eabf9f",'#40D5B3',"#f05945",],
            categorySum: [],
            visibleCreateModal: false,
            title: "工作任务延期详情信息",
            pagination: { pageSize: 100 ,pageNo: 1,current: 1},
            yScroll: 300,
            columns,
            // categorySum: [
            //   {
            //      "id": "509981",
            //      "count": 10,
            //      "name": "产品产品开发产品开发产品开发开发",
            //      "percentage": 0.2100,
            //      "index": 1

            //   },
            //   {
            //      "id": "509992",
            //      "count": 15,
            //      "name": "研发能力",
            //      "percentage": 0.3450,
            //      "index": 2

            //   },
            //   {
            //      "id": "50983",
            //      "count": 15,
            //      "name": "商品开发",
            //      "percentage": 0.3345,
            //      "index": 3

            //   },
            //   {
            //      "id": "59984",
            //      "count": 10,
            //      "name": "零部件开发",
            //      "percentage": 0.2,
            //      "index": 4

            //   }
            // ],
            allNum: null,
            // 级别统计
            levelColumns: [
              {
                  title: 'Name',
                  dataIndex: 'name',
                },
                {
                  title: 'percentage',
                  dataIndex: 'percentage',
                },
                {
                  title: 'count',
                  dataIndex: 'count',
                }
            ],
            levelData:  [
            ],
            // 认可与未认可
            isApproveData: [
              // {
              //   name: '未认可',
              //   count: 35,
              //   proportion: 0.35
              // },
              // {
              //   name: '已认可',
              //   count: 65,
              //   proportion: 0.65
              // }
            ],
            url: {
               projectType: "/HomeTable/projectType",  //项目类别统计
               projectLevel: "/HomeTable/projectLevel",  //项目级别展示
               taskDelay: "/HomeTable/WorkDelayTable",  //任务延期
               projectRisk: "/HeavyDutyTable/ProjectRiskTable",  //项目风险
               riskFactor: "/HomeTable/selectRiskTable", //风险预防措施
               riskPrevention: "/HomeTable/riskPrevention"  //风险预防措施认可数和未认可数
            },
            tableDate: [],
            tableDateApprove: []
        }
    },
    mounted(){
        this.screenWidth = document.querySelector("#app").clientWidth
        this.screenHeight = document.querySelector("#app").clientHeight;
        this.yScroll =  this.screenHeight .clientHeight - 260
        this.drawChart()
        const _this = this
        setInterval(function () {
            _this.getCurrentTime()
        },1000)
        window.onresize = () => {
         return (() => {
            this.screenWidth = document.querySelector("#app").clientWidth
            this.screenHeight = document.querySelector("#app").clientHeight;
            this.yScroll =  this.screenHeight .clientHeight - 260
            if(document.querySelector("#container3")){
              document.querySelector("#container3>div").remove()
              this.renderTaskDelay(this.gkRiskReport)
            }
            if(document.querySelector("#container4")){
              document.querySelector("#container4>div").remove()
              this.renderProjectRisk(this.projectJson)
            }
            if(document.querySelector("#container5")){
              document.querySelector("#container5>div").remove()
              this.renderDepartmentRisk(this.departmentJson)
            }
            if(document.querySelector("#container1")){
              document.querySelector("#container1>div").remove()
              this.renderCaterorySum(this.categorySum)
            }
            if(document.querySelector("#container6")){
              document.querySelector("#container6>div").remove()
              this.renderApproveSum(this.isApproveData)
            }
         })();
       };

    },
    methods: {
      detailCancel(){
        this.visibleCreateModal = false;
      },
      handleTableChange(pagination){
        const pager = { ...this.pagination };
        pager.current = pagination.current;
        this.pagination = pager;
      },
        getDateToFixed(data){
          return data.toFixed(2)+"%"
        },
        getCurrentTime(){
            const myDate = new Date()
            const hour=myDate.getHours();
		      	const minutes=myDate.getMinutes();
		      	const seconds=myDate.getSeconds();
            const month = myDate.getMonth() + 1;
            const strDate = myDate.getDate();
            const year = myDate.getFullYear()
            const days = myDate.getDay() 
            const week = days == 0 ? "星期日" : (days == 1 ? "星期一" : (days == 2 ? "星期二" : (days == 3 ? "星期三" : (days == 4 ? "星期四" : (days== 5? "星期五" : "星期六")))))
            this.weekend = week
            this.time = String(hour).padStart(2, '0') + ":" + String(minutes).padStart(2, '0') + ":" + String(seconds).padStart(2, '0')
            this.date = year + "-" + String(month).padStart(2, '0')  + "-" + String(strDate).padStart(2, '0') 
        },
        //任务延期
        drawChart(){
           const _this = this
           for(let key in this.url){
              getAction(this.url[key],{},'get').then((res) => {
                if(res.success && res.result){
                   _this.$nextTick(function(){
                      //  _this.renderChart(res.result)
                      if(key == 'projectType'){
                        res.result.list.map(function(itm,index){
                          itm.index = index +1
                        })
                        _this.$set(_this,"categorySum",res.result.list)
                        _this.$set(_this,"allNum",res.result.total)
                        _this.renderCaterorySum(_this.categorySum)
                      } if(key == 'taskDelay'){
                        res.result.map(function(item,index){
                          item.index = index+1
                        })
                        _this.$set(_this,"gkRiskReport",res.result)
                        _this.renderTaskDelay(_this.gkRiskReport)
                      } if(key == 'projectRisk'){
                        res.result.map(function(item){
                          item.line = item.Xaxis
                        })
                        _this.$set(_this,"projectJson",res.result)
                        _this.renderProjectRisk(_this.projectJson)
                      } if(key == 'riskPrevention'){
                         _this.$set(_this,"isApproveData",res.result)
                         _this.isApproveData = _this.isApproveData.filter(function(item){
                            return item.proportion
                         })
                         _this.renderApproveSum(_this.isApproveData)
                      }if(key == 'riskFactor'){
                        _this.$set(_this,"departmentJson",res.result)
                        _this.renderDepartmentRisk(_this.departmentJson)

                      }if(key == "projectLevel"){
                        _this.$set(_this,"levelData",res.result)
                      }
                   })
                } else {
                  // _this.$nextTick(function(){
                  //      _this.renderChart(data)
                  //  })
                }
             })
           }
            // this.renderTaskDelay(this.gkRiskReport)
            // this.renderProjectRisk(this.projectJson)
            // this.renderDepartmentRisk(this.departmentJson)
            // this.(this.categorySum)
            // this.renderApproveSum(this.isApproveData)
            this.levelSum()
        },
        renderTaskDelay(data){
            const width = this.screenWidth * 0.39,
             height = this.screenHeight * 0.45 - 80 ;
          const chart = new Chart({
              container: 'container3',
            //   forceFit: true,
              height,
              width,
              background: '#fff',
              padding: [30, 45, 30, 75]
          });
          data.map(function(item){
              // item["toolTitle"] = item.activityName
              item["sexuality"] = item.taskType === "normal" ? "正常进行任务" : (item.taskType === "finished" ? "已完成任务" :item.taskType === "overdue" ? "逾期未完成" : (item.taskType === "isoverdue" ? "可能逾期任务" : (item.taskType === "red" ? "逾期已完成" : "" )))
          })
          chart.source(data);
          // 为各个字段设置别名
          chart.scale({
            projectName: {nice:true,alias: "项目名称"},
            // xaxis: { nice: true,alias: "周数" ,tickInterval: 1,min: 0,tickInterval: 1},
            xaxis: { nice: true,alias: "周数" ,tickInterval: 1},
            activityName: { nice: true,alias: "任务名称"},
            deviation: { nice: true,alias: "偏差值" },
            actualStartTime: {nice: true,alias: '实际开始时间'},
            targetStartTime: {alias: '计划开始时间',nice: true},
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
                 fill: '#8ab6d6', // 文本颜色
                 fontWeight: 500
              },
              position: "end",
            },
            label: {
                 offset: 12, // 设置坐标轴文本 label 距离坐标轴线的距离
                 textStyle: {
                   textAlign: 'center', // 文本对齐方向，可取值为： start middle end
                   fill: '#8ab6d6', // 文本的颜色
                   fontSize: '12', // 文本大小
                   // fontWeight: 'bold', // 文本粗细
                   rotate: 0, 
                   textBaseline: 'middle' // 文本基准线，可取 top middle bottom，默认为middle
                 } , // 文本样式，支持回调 
            },
            line: {
                    lineWidth: 1, // 设置线的宽度
                    stroke: '#8ab6d6', // 设置线的颜色
                    // lineDash: [ 3, 3 ] // 设置虚线样式
              },
            // tickLine: null
            tickLine: {
                 lineWidth: 1,
                 length: -5,
                 stroke: '#8ab6d6',
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
                     fill: '#fff', // 文本颜色
                    //  fontWeight: 500,
                    //  fontFamily: "STHeitiSC-Medium, STHeitiSC;"
                 },
                 zlevel:2
               }, 
               line: {
                    lineWidth: 1, // 设置线的宽度
                    stroke: '#fff', // 设置线的颜色
                    // lineDash: [ 3, 3 ] // 设置虚线样式
              },
              tickLine: {
                 lineWidth: 1,
                 length: -5,
                 stroke: '#fff',
                 alignWithLabel:true
              }, 
              label: {
                 offset: 16, // 设置坐标轴文本 label 距离坐标轴线的距离
                 textStyle: {
                   textAlign: 'center', // 文本对齐方向，可取值为： start middle end
                   fill: '#fff', // 文本的颜色
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
              offsetX: 60,
              offsetY: 4,
              width: 500,
              marker:'circle'
          }
          );
          chart
            .point()
            .position('xaxis*deviation')
            .color('sexuality',function(item){
                return item === "正常进行任务" ? "#1890FF" : (item === "已完成任务" ? "red" :item === "逾期未完成" ? "#ffcc29" : (item === "可能逾期任务" ? "#dbf6e9" : (item === "逾期已完成" ? "#e36bae" : "#fff")))
            })
            .size(4)
            .shape('circle')
            .tooltip('projectName*activityName*xaxis*deviation*targetStartTime*byTime*expectedFinishTime*actualEndTime*actualStartTime')
            // .tooltip('projectName*activityName*xaxis*deviation')
            .style({
              fillOpacity: 0.85
            });
          chart.render();

        },
        // 项目风险
        renderProjectRisk(data){
            var _this = this
        const width = this.screenWidth * 0.39,
             height = this.screenHeight * 0.5 - 90 ;
          const chart = new Chart({
              container: 'container4',
              autoFit: true,
              // forceFit: true,
              height,
              width,
              background: '#fff',
               padding: [30, 45, 30, 75]
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
                  fill: '#8ab6d6', // 文本颜色
                  fontWeight: 500
               },
               position: "end",
             },
             label: {
               formatter(value) {
                 return (value * 100) + '%';
               } // 格式化坐标轴的显示
             },
             label: {
               offset: 12, // 设置坐标轴文本 label 距离坐标轴线的距离
               textStyle: {
                 textAlign: 'center', // 文本对齐方向，可取值为： start middle end
                 fill: '#8ab6d6', // 文本的颜色
                 fontSize: '12', // 文本大小
                 // fontWeight: 'bold', // 文本粗细
                 rotate: 0, 
                 textBaseline: 'middle' // 文本基准线，可取 top middle bottom，默认为middle
               } , // 文本样式，支持回调 
               autoRotate: false// 是否需要自动旋转，默认为 true
             },
             line: {
                    lineWidth: 1, // 设置线的宽度
                    stroke: '#8ab6d6', // 设置线的颜色
                    // lineDash: [ 3, 3 ] // 设置虚线样式
              },
             tickLine: {
                lineWidth: 1,
                length: -5,
                stroke: '#8ab6d6',
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
                     fill: '#fff', // 文本颜色
                    //  fontWeight: 500,
                    //  fontFamily: "STHeitiSC-Medium, STHeitiSC;"
                 },
                 zlevel:2
               }, 
               line: {
                    lineWidth: 1, // 设置线的宽度
                    stroke: '#fff', // 设置线的颜色
                    // lineDash: [ 3, 3 ] // 设置虚线样式
              },
              tickLine: {
                 lineWidth: 1,
                 length: -5,
                 stroke: '#fff',
                 alignWithLabel:true
              }, 
              label: {
                 offset: 16, // 设置坐标轴文本 label 距离坐标轴线的距离
                 textStyle: {
                   textAlign: 'center', // 文本对齐方向，可取值为： start middle end
                   fill: '#fff', // 文本的颜色
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
            .size('OutputQualityRiskSum', [0, 40]).opacity(0.4)
            // 气泡图圈的颜色
            .color("#0091FF")
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
        //部门风险双柱状图
        renderDepartmentRisk(data){
          const _this = this
           const width = this.screenWidth * 0.295,
             height = this.screenHeight * 0.5 - 80 ;
        data.map(function(item){
              item["weekLang"] = item.week
              // item["weekLang"] = "第" + item.week + "周"
              // if(item.sumBenchmark)
              if(item.hasOwnProperty('sumBenchmark')){
                item["sum"] = item.sumBenchmark
                item["benchmarkRisk"] = item.sumBenchmark
              } else{
                item["cumulativeRisk"] = item.sum
              }
            })
        const dataSorted = data.sort((a, b) => { return a.week - b.week })
            const chart = new Chart({
               container: 'container5',
              //  autoFit: true,
              //  forceFit: true,
               height,
               width: width,
               background: '#fff',
               padding: [40, 40, 30, 45]
            });
            chart.source(dataSorted);
            chart.scale({
               name: {
                 // type: 'pow',
                 alias: '姓名'
               },
               cumulativeRisk: {
                 alias: '本周风险'
               },
               benchmarkRisk: {
                 alias: '本周风险预防措施'
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
                    fill: '#8ab6d6', // 文本颜色
                    fontWeight: 500
                 },
                 position: "end",
               },
               line: {
                    lineWidth: 1, // 设置线的宽度
                    stroke: '#8ab6d6', // 设置线的颜色
                    // lineDash: [ 3, 3 ] // 设置虚线样式
              },
               label: {
                 offset: 16, // 设置坐标轴文本 label 距离坐标轴线的距离
                 textStyle: {
                   textAlign: 'center', // 文本对齐方向，可取值为： start middle end
                   fill: '#8ab6d6', // 文本的颜色
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
                    fill: '#fff', // 文本颜色
                    fontWeight: 500,
                },
              },
              line: {
                    lineWidth: 1, // 设置线的宽度
                    stroke: '#fff', // 设置线的颜色
                    // lineDash: [ 3, 3 ] // 设置虚线样式
              },
              tickLine: {
                 lineWidth: 1,
                 length: -5,
                 stroke: '#fff',
                 alignWithLabel:true
              }, 
              label: {
                 offset: 16, // 设置坐标轴文本 label 距离坐标轴线的距离
                 textStyle: {
                   textAlign: 'center', // 文本对齐方向，可取值为： start middle end
                   fill: '#fff', // 文本的颜色
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
               if(name.indexOf('预防措施') > -1){
                 return '#0091FF'
               } else{
                 return '#40D5B3'
               }
             })
             .tooltip('cumulativeRisk*benchmarkRisk')
            //  .adjust([
            //     {
            //       type: 'dodge',
            //       marginRatio: 0
            //     },
            //  ]);
          //  chart
          //    .line()
          //    .position('weekLang*ratio')
          //    .color("#F7B500")
          //    .shape('smooth')
          //    .style({
          //       stroke: '#F7B500',
          //       lineDash: [8, 4],
          //       opacity: 0.6
          //     })
           chart.render();
           chart.on('click',ev=>{
            // console.log(ev.data._origin,'ev');
            // _this.visible = true
            _this.getRiskAndMeasureDate(ev.data._origin)
          })
        },
        getRiskAndMeasureDate(data){
          const _this = this,params={}
          var url = data.name.indexOf("预防措施") > -1 ? "/HomeTable/selectMeaTable" : "/HomeTable/selectRisk"
          this.$set(this,"tableDate",[])
          params.startTime = data.startTime
          params.endTime = data.endTime
          if(data.name.indexOf("预防措施") > -1){
            _this.drawTitle = "预防措施列表"
          } else{
            _this.drawTitle = "风险列表"
          }
          _this.visible = true
          getAction(url,params,'get').then((res) => {
                if(res.success && res.result){
                  // console.log(res.result);
                  // _this.tableDate = res.result
                  _this.$set(_this,"tableDate",res.result)
                } else {
                  // _this.$nextTick(function(){
                  //      _this.renderChart(data)
                  //  })
                }
             })
        },
        toRiskDetail(item,index){
          var url = null
          if(index == 1){
            url = "http://10.2.81.218:9998/invokeAction?actionsGroup=object&actionName=infoPage&oid=OR:ext.st.pmgt.issue.model.STProjectRisk:"+item.riskId+"&ContainerOid=com.pisx.tundra.pmgt.project.model.PIProjectContainer:"+item.containerRefId
          } else{
            url = "http://10.2.81.218:9998/invokeAction?actionsGroup=object&actionName=infoPage&oid=OR:ext.st.pmgt.issue.model.STProjectMeasures:"+item.id+"&ContainerOid=com.pisx.tundra.pmgt.project.model.PIProjectContainer:"+item.meaontainerRefId
          }
          window.top.location.href =url
        },
        // 项目类别统计
        renderCaterorySum(data){
             const width = this.screenWidth * 0.3,
               height = this.screenHeight * 0.5 - 120 ;
             const chart = new Chart({
                 container: 'container1',
               //   forceFit: true,
                 height,
                 width: width* 0.6,
                 background: '#fff',
                 padding: [0, 25, 20, 25]
             });
             const color = this.color
                  chart.source(data);
                  chart.scale('percentage', {
                    formatter: (val) => {
                      val = val * 100 + '%';
                   return val;
                 },
               });
               chart.coord('theta', {
                 radius: 0.75,
                 innerRadius: 0.6,
               });
              // 辅助文本
              chart
                .guide()
                .text({
                  position: ['50%', '50%'],
                  content: '总数',
                  style: {
                    fontSize: 16,
                    fill: '#fff',
                    textAlign: 'center',
                  },
                  offsetY: -12
                })
                .text({
                  position: ['50%', '50%'],
                  content: this.allNum,
                  style: {
                    fontSize: 24,
                    fill: '#fff',
                    textAlign: 'center',
                  },
                  offsetX: 0,
                  offsetY: 16,
                })
                // chart.legend('name', {
                //     position: 'bottom-center',
                //     itemGap: 20,
                //     offsetX: 10 ,
                //     offsetY: -60,
                //     width,
                //     marker:'diamond'
                //  });
                 chart.legend('name', false); 
                 // 为各个字段设置别名
               chart.scale({
                   name: {
                      nice: true,
                      alias: '项目类别'
                   },
                   count: {
                     alias: '数量',
                     nice: true,
                   },
                   percentage: {
                     nice: true,
                     alias: '百分比',
                     formatter: (val) => {
                         val = val * 100 + '%';
                         return val;
                     },
                   }
               });
               chart.tooltip({ 
                  title: 'name',
                  showMarkers: false
               });
              chart
                .intervalStack()
                .position('percentage')
                .color('index',function(item){
                      return color[item-1]
                })
                .label('name', function(){
                  return {
                    useHtml: true,
                    htmlTemplate: function(text,item){
                      var d = item.point
                        return (
                          `<div class="g2-label" style="color:${item.color};font-size:10px;width: "60px" >` +
                           text + ":" + d.count +
                          "</div > "
                        )
                    },
                    textStyle: {
                      // textAlign: 'right',
                      // textBaseline: 'center',
                      fontSize: 10
                    },
                    offset: 14,//偏移量
                    // rotate: 270,
                    position:'end'//label的展示位置
                  }
                })
                .tooltip('name*count*percentage')
              // chart.interact('element-active');
              chart.render();
        },
        // 项目级别统计
        levelSum(){

        },
        // 认可与未认可
        renderApproveSum(data){
          const _this = this
           const width = this.screenWidth * 0.295,
               height = this.screenHeight * 0.5 - 80 ;
           const chart = new Chart({
               container: 'container6',
               height,
               width: width,
               padding: [40, 40, 30, 45]
           });
           chart.source(data);
           chart.coord('theta', {
             radius: 0.75
           });
           chart.scale({
             count:{
                 nice: true,
                alias: '数量'
              },
              name:{
                nice: true,
                alias: '预防措施类别'
              }
           })
           chart.legend('name', {
                position: 'bottom-center',
                itemGap: 20,
                offsetX: 10 ,
                offsetY: -30,
                width,
                marker:'diamond'
             });
          chart.tooltip({
              title: 'name',
              // showMarkers: false,
              // showCrosshairs: false, 
            });
           const interval = chart
               .intervalStack()
               .position('proportion')
               .tooltip('count*name')
               .color('name', [ 'rgb(0, 78, 189)', 'rgb(0, 184, 124)'])
               .style({ opacity: 0.3 })
               .label('name', function(){
                  return {
                    useHtml: true,
                    htmlTemplate: function(text,item){
                      var d = item.point
                      var classes = text == '已认可' ? 'approve' : 'disApprove'
                        return (
                          `<div class="g2-label${classes}" :class="${classes}" style="color:"#fff";font-size:10px;width: "100px";>` +
                           text + ":" + d.count +
                          "</div > "
                        )
                    },
                    textStyle: {
                      textAlign: 'center',
                      textBaseline: 'middle',
                      // fontSize: 10
                    },
                    offset: -20,//偏移量
                    // rotate: 270,
                    position:'center'//label的展示位置
                  }
             });
            chart.render();
            chart.on('click',ev=>{
            // _this.visible = true
            _this.getApproveSum(ev.data._origin)
          })
        },
        getApproveSum(data){
          const _this = this
          var params = {}
          _this.drawTitle = data.name+ "列表"
          params.state = data.name == "未认可" ? 0 : 1
          this.visible = true
          getAction("HomeTable/riskPreventionetails",params,'get').then((res) => {
                if(res.success && res.result){
                  // console.log(res.result);
                  _this.tableDateApprove = res.result
                  _this.$set(_this,"tableDateApprove",res.result)
                } else {
                  // _this.$nextTick(function(){
                  //      _this.renderChart(data)
                  //  })
                }
          })
        },
        // 点击标题跳转
        toItemSelect(item){
          // const url = "http://localhost:9998/invokeAction?actionsGroup=pi-pmgt-project&actionName=projectViewsList&name="+item.name 
          // const url = "http://192.168.111.137:9998/invokeAction?actionsGroup=pi-pmgt-project&actionName=projectViewsList&name="+item.name  //正式机项目ip
          const url = "http://10.2.81.218:9998/invokeAction?actionsGroup=pi-pmgt-project&actionName=projectViewsList&name="+item.name 
          // console.log(url);
          window.top.location.href = url
          // window.open(url,'_blank')
        },
        showDrawer() {
          this.visible = true;
        },
        onClose() {
          this.visible = false;
        },
        afterVisibleChange(val) {
          console.log('visible', val);
        }
    }
}
</script>
<style lang="less" scoped>
.content-box{
    // margin: 0 auto;
    // position: relative;
    overflow: hidden;
    box-sizing: border-box;
    transform-origin: 0 0;
    background: url("../../assets/reportDpbg.png") no-repeat;
    // background: #030F27;
    background-size: 100% 100%;
    .title{
        text-align: center;
        color: #fff;
        // background: url("../../assets/title_bg.png") no-repeat;
        background-size:100% 90px;
        position: relative;
        // .srcImag{
            // width: 71px;
            // height: 94px;
            // position: absolute;
            // background: url("../../assets/headerHome.png") no-repeat;
            // background-size: 80%;
            // left: 20px;
            // top: -4px;
        // }
        .time{
            display: flex;
            position: absolute;
            right: 30px;
            top: 0;
            // line-height: 60px;
            .currenttime{
                line-height: 72px;
                margin-right: 10px;
                font-size: 22px;
            }
            .line{
                    width: 2px;
                    height: 44px;
                    position: absolute;
                    top: 14px;
                    right: 84px;
                    background:url("../../assets/line.png") no-repeat;
                    background-size: 100% 100%;
            }
            .dates{
                padding-left: 20px;
                >div:nth-child(1){
                    padding-top: 6px;
                    font-size: 12px;
                    line-height: 22px;
                }
                
            }
        }
        .text{
            line-height: 60px;
            font-size: 28px;
        }
        .mainText{
            font-size: 24px;
            color: #38e3fd;
        }
    }
    .perspective{
        height: calc(100% - 96px);
        color: #fff;
        position: relative;
        .item{
            position: absolute;
            height: 49%;
            margin-left: 6px;
            
        }
        .header{
            height: 40px;
            background: url("../../assets/title_border.png") no-repeat center center;
            background-size:  280px 36px;
            text-align: center;
            line-height: 40px;
            font-size: 18px;
            font-weight: bold;
            // cursor: pointer;
        }
        .header05{
            background-size:  400px 36px;
        }
        .header06{
            background-size:  480px 36px;
        }
        .content{
            height: calc(100% - 40px);
            background: url("../../assets/partBg.png") no-repeat;
            background-size: 100% 100%;
        }
        .item01,.item05{
            top: 0;
        }
        .item04,.item06{
            top: 50.5%;
        }
        .item01,.item02{
            left: 0;
            width: 30%;
        }
        .item03,.item04{
            left: 30.5%;
            width: 39%;
        }
        .item05,.item06{
            left: 70%;
            width: 29.5%;
        }
        .item01{
            height: 44%;
        }
        .item02{
            top: 44.5%;
            height: 55%;
        }
        .item03{
            top: 4%;
            height: 45%;
        }
        .item04{
            top: 49.5%;
            height: 50%;
        }
    }
    .table{
                position: absolute;
                top: 55%;
                transform: translateY(-50%);
                right: 10px;
                max-height: 70%;
                  overflow: auto;
                .chartTitle{
                    font-size: 14px;
                    color: rgba(228, 189, 15, 0.753);
                }
                span{
                    display: inline-block;
                    width: 55px;
                    font-weight: bold;
                    text-align: center;
                }
                i{ 
                     display: inline-block;
                     position: absolute;
                     width: 8px;
                     height: 8px;
                     border-radius: 50%;
                     line-height: 30px;
                }
                .chartCon{
                  max-height: calc(100% - 30px);
                }
                .chartConPer{
                    height: 26px;
                    line-height: 26px;
                    position: relative;
                    color: #c3ceda;
                    cursor: pointer;
                    span{
                        font-weight: normal;
                    }
                    i{
                        position: absolute;
                        top: 50%;
                        left: 0;
                        transform: translateY(-50%);
                    }
                }
                .chartTitle,.chartConPer{
                      .proItem{
                        width: 120px;
                      }
                }
            }
    .content2{
      padding-top: 20px;
      height: 100%;
      // overflow: auto;
      box-sizing: border-box;
      .tableBox{
        height: 94%;
        overflow: auto;
      }
      table{
        width: 100%;
        height: 100%;
        overflow: auto;
        text-align: left;
        font-size: 14px;
        thead{
          width: 100%;
          // display: none;
          font-size: 10px;
          visibility: hidden;
          tr{
            width: 100%;
            height: 0;
          }
          th:nth-child(1) {
            // width: 10%;
            width: 130px;
          }
          th:nth-child(2) {
            width: calc(90% - 130);
          }
          th:nth-child(3) {
            width: 10%;
          }
        }
        tbody{
          // height: 100%;
          overflow: auto;
          tr{
            &:hover{
              background: rgba(200, 233, 232, 0.3);
            }
          }
          td{
            padding: 6px 10px;
            text-align: left;
            font-size: 16px;
            &:nth-child(1){
              color: rgb(195, 206, 218);
            }
            .progressBar{
              width: 100%;
              height: 16px;
              background: rgba(21, 35, 48,1);
              border-radius: 20px;
              position: relative;
              .progress{
                background: -webkit-linear-gradient(left, rgb(2, 205, 254) , rgb(17, 54, 80));
                height: 16px;
                border-radius: 20px;
              }
              .propercentage{
                position: absolute;
                top: -2px;
                font-size: 12px;
              }
            }
          }
        }
      }
    }
}
/deep/.g2-labelapprove,/deep/.g2-labeldisApprove{
  width: 80px!important;
  text-align: center;
  position:absolute;
}
/deep/.g2-label{
  width: 80px;
  text-align: center;
  // left: 50%;
  // transform: translate(-50%);
  // top: 30px;
  // top: 50%;
}
/deep/.g2-labelapprove{
  // left: -50px;
  left: 50%;
  transform: translate(-50%,-50%);
  // top: 10px;
  top: 50%;
}
/deep/.g2-labeldisApprove{
  text-align: center;
  // left: 30px;
  left: 50%;
  transform: translate(-50%,-50%);
  // top: -10px;
  top: 50%;
}
#table{
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

}

     .table1{
          min-width: 100%;
         max-height: 800px;
         height: 100%;
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
         tbody td{
             text-align: center;
             padding: 6px 10px;
             color: rgba(0,0,0,.7)
         }
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
    #table tbody tr{
      &:hover{
        background: rgb(248, 247, 247);
      }
      cursor: pointer;
    }
      /deep/.ant-drawer-content{
        height: 100%;
      }
      /deep/.ant-drawer-body{
        height: calc(100% - 58px);
      }
      .index{
        width: 70px;
      }
      /deep/.ant-drawer-title{
        text-align: center;
        font-weight: 700;
        font-size: 18px;
      }

  .item03 {
    .header03{
      cursor: pointer;
    }
    /deep/.ant-btn-primary{
      height: 28px;
      line-height: 28px;
      padding: 0 12px;
      float: right;
      font-size: 14px;
    }
  }
  /deep/ .ant-modal-content{
      height: calc(100% - 20px);
    }
    /deep/ .ant-modal-footer{
      border-top: 1px solid transparent;
      padding: 0;
    }  
    /deep/ .ant-modal-body{
          // max-height: 820px;
          // height: 80%;
           height: calc(100% - 60px);
          overflow: auto;
          padding: 0 24px 10px;
          font-size: 12px;
          .ant-table-tbody .ant-table-row td {
              padding-top: 10px;
              padding-bottom: 10px;
          }
       }
       /deep/ .ant-modal-header{
         border-bottom: 1px solid transparent
       }
      /deep/ .ant-modal-close{
              position: absolute;
              top: -20px;
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
         padding-top: 2px;
       }
      /deep/ .ant-modal-header .ant-modal-title{
          text-align: center;
          border-bottom: 1px solid transparent;
          font-weight: bold;
          font-size: 18px;
          color: #333;
       }
       /deep/ .ant-modal{
      position: relative;
      top: 50px;
      height: calc(100% - 30px);
      width: 98%!important;
      td{
        font-size: 12px;
      }
      th{
        font-size: 12px;
        padding: 13px 8px;
        font-weight: 600;
      }
    }
</style>