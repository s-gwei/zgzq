<template>
    <a-card :bordered="false" :loading="loading">
      <div class="table-page-search-wrapper wrapperselect">
      <a-form layout="inline">
        <a-row :gutter="48">
          <a-col :md="18" :sm="18" >
            <a-form-item label="偏差范围" :class="btnDisabled ? 'none': ''">
              <a-input-number v-model="start" /> —
              <a-input-number v-model="end" />
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="6" class="btn">
            <span class="table-page-search-submitButtons">
              <!-- <a class="help" @click="visibleCreateModal = true"><a-icon type="question-circle-o"></a-icon><span>帮助</span></a> -->
              <a-button style="margin-left: 8px,color: #000" @click="resetSearchForm"  class="reset">重置</a-button>
              <a-button type="primary" style="margin-left: 8px"  :class="btnDisabled ? 'none': ''" @click="doSearch(start,end)" >查询</a-button>
              <a-button type="primary" style="margin-left: 8px" @click="detailBtn(getCurrentData)">查看详情</a-button>
            </span>
          </a-col>
        </a-row>
      </a-form>
       </div>
       <div class="tableContainer" >
      <div class="title"><p class="tilcontent">工作任务延期报表</p><a-button type="dashed" icon="download" id="test">保存</a-button></div>
           <!-- 气泡图区域-begin -->
           <div id="container" ></div>
           <div class="scrollBarWrap">
             <ScrollBar ref="scrollBar" @updatePosRate="throttleUpdateDs" />
           </div>
           <!-- 气泡图区域-end -->
            <!-- <div class="noData" v-if="!chartDateALL || !chartDateALL.length" :style="{height: adaptiveContainH +'px',lineHeight: adaptiveContainH +'px'}">
             暂无数据
            </div> -->
       </div>
        <a-modal :title="title" destroyOnClose :visible="visibleCreateModal" @cancel="detailCancel" class="detail">
            <!---->
            <div class="mainMadal">
              <a-button type="primary" class="export" @click="exportExcel(getCurrentDatas,columns)">导出数据</a-button>
              <a-table id="table"
               :columns="columns"
               :data-source="getCurrentDatas"
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
        <a-modal
          v-model="exportvisible"
          title="导出"
          @cancel="exportvisible=false"
          class="exportAlert"
        >
           <template slot="footer">
             <a-button key="back" @click="handleCancel">
               Return
             </a-button>
             <a-button key="submit" type="primary"  @click="handleOk">
               Submit
             </a-button>
           </template>
           <a-form-item label="请选择导出数据范围">
                <a-radio-group name="radioGroup"  @change="handleInput" default-value="exportvalue" v-model="exportvalue">
                   <a-radio :value="1">
                     当前页
                   </a-radio>
                   <a-radio :value="2">
                     全部
                   </a-radio>
                </a-radio-group>
           </a-form-item>
        </a-modal>
    </a-card>
</template>

<script>
  import {getAction} from '@/api/manage';
  import { Chart } from '@antv/g2';
  import ScrollBar from "./ScrollBar"
  import throttle from '@/mixins/throttle'
  import DataSet from '@antv/data-set';
  import XLSX from 'xlsx'
  import html2canvas from 'html2canvas'
  const gkRiskReport = require('@assets/json/gkRiskReport.json')
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
  // const BASE_LEN = 32 
  export default {
    name: "taskDelayReport",
    components: {
      ScrollBar
    },
    data() {
      return {
        isExport: false,
        exportvalue: 1,
        exportvisible: false,
        min: 0,
        max: 100,
        allMax: 200,
        btnDisabled: false,
        title: "工作任务延期详情信息",
        getCurrentData:[],//当前页面数据
        getCurrentDatas: [],
        columns:columns,
        yScroll: 300,
        baseRate: 1,
        i: 1,
        visibleCreateModal: false,
        visible: false,
        loading: false,
        queryParam: {},
        chartDateALL:[],
        adaptiveContainH: 200,
        start: null,
        end: null,
        value: "",
        baseRate: 1,
        url: {
          // chartDate:"/HeavyDuty/pert"
          // 新接口
          chartDate: "/OTDrivice/WorkDelayTable"
        },
        pagination: { pageSize: 100 ,pageNo: 1,current: 1}
      }
    },
    computed:{
    },
    mounted(){
        this.$nextTick(function(){
            this.drawChart()
            // this.renderChart(riskJson)
        })
        this.yScroll = document.body.clientHeight - 260
        //  this.getAdaptiveH()
        window.onresize = () => {
          return (() => {
            if(this.chartDateALL && this.chartDateALL.length){
              this.yScroll = document.body.clientHeight - 260
                  this.start = null
             this.end = null
             this.$bus.$emit('changeTranx', 0)
               document.querySelector("#container") && document.querySelector("#container>div") && document.querySelector("#container>div").remove()
               this.renderChart(this.chartDateALL)
              //  this.getAdaptiveH()
            }
          })();
       };
    },
    beforeDestroy(){
       document.querySelector("#container") && document.querySelector("#container>div")  && document.querySelector("#container>div").remove()
    },
    watch: {
        'chartDateALL'(v){
          if(v.length && this.i <= 1){
               this.i ++
               document.querySelector("#container") &&  document.querySelector("#container>div") && document.querySelector("#container>div").remove()
               this.renderChart(this.chartDateALL)
              //  this.renderChart(gkRiskReport)
          }
        }
    },
    methods: {
      detailCancel(){
        this.visibleCreateModal = false;
        this.isExport = false
      },
      handleTableChange(pagination){
        const pager = { ...this.pagination };
        pager.current = pagination.current;
        this.pagination = pager;
      },
      handleCancel(){
        this.exportvisible = false
      },
      handleOk(){
        const getCurrentData = JSON.parse(JSON.stringify(this.getCurrentDatas))
        if(this.exportvalue == 1){
          const start = 100*(this.pagination.current-1)
          const data = getCurrentData.slice(start, start+100)
          this.exportExcelFin(data)
        } else{
          this.exportExcelFin(getCurrentData)
        }
        this.exportvisible = false
      },
      handleInput(e){
      },
      detailBtn(data){
        this.visibleCreateModal = true
        this.pagination.current =1
        this.pagination.pageNo =1
        this.getCurrentDatas = JSON.parse(JSON.stringify(data))
        this.getCurrentDatas.map(function (record) {
                record.targetStartTime = record.targetStartTime ? record.targetStartTime.split(' ')[0] : ''
                 record.actualEndTime = record.actualEndTime ? record.actualEndTime.split(' ')[0] : ''
                 record.actualStartTime = record.actualStartTime ? record.actualStartTime.split(' ')[0] : ''
                 record.expectedFinishTime = record.expectedFinishTime ? record.expectedFinishTime.split(' ')[0] : ''
                 record.byTime = record.byTime ? record.byTime.split(' ')[0] : ''
        })
      },
      exportExcel(){
        this.exportvalue = 1
        if(this.getCurrentDatas.length < 100){
          this.exportvisible = false
          this.exportExcelFin(this.getCurrentDatas)
        } else{
          this.exportvisible = true
        }
        // let table = document.getElementById('table');
        // let worksheet = XLSX.utils.table_to_sheet(table);
        // let workbook = XLSX.utils.book_new();
        // XLSX.utils.book_append_sheet(workbook, worksheet, 'sheet');
        // 以上四行也可以直接一行搞定，如果不需要对表格数据进行修改的话
        // let workbook = XLSX.utils.table_to_book(document.getElementById('table'))
        // try {
        // 	XLSX.writeFile(workbook, '工作任务延期详情信息.xlsx');
        // } catch(e) {
        // 	console.log(e, workbook);
        // }
        
      },
      exportExcelFin(data){
        let Parser = require('json2csv').Parser
         let fields = []
         this.columns.map(col => {
           if (col.title && col.dataIndex) {
             let obj = {
               label: col.title,  // 表头名称
               value: col.dataIndex     // 表取值字段key
             }
             fields.push(obj)
           }
         })
        data.map(function(record){
           record.taskType = record.taskType === "normal" ? "正常进行任务" : (record.taskType === "finished" ? "已完成任务" :record.taskType === "overdue" ? "逾期未完成" : (record.taskType === "isoverdue" ? "可能逾期任务" : (record.taskType === "red" ? "逾期已完成" : "" )))
        })
         let fileName = '工作任务延期详情信息导出_' + new Date().toLocaleString('zh-CN')
         let json2csvParser = new Parser({fields})
         let result = json2csvParser.parse(data)
         let blob = new Blob(['\ufeff' + result], {type: 'text/csv'})
         let a = document.createElement('a')
         a.setAttribute('href', URL.createObjectURL(blob))
         a.setAttribute('download', `${fileName}.csv`)
        //  console.log(this.baseRate,this.scrollProportion);
        this.isExport = true
         a.click()
        //  console.log(this.baseRate);
      },
       doSearch(s,e){
         var data
         this.$bus.$emit('changeTranx', 0)
         this.$set(this,"getCurrentData", [])
         if(typeof(s) != 'number' && typeof(e) != 'number') return
         if(typeof(s) == 'number' && typeof(e) == 'number'){
             this.start = s
             this.end = e
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
             this.$set(this,"chartDateALL",data)
         }
           document.querySelector("#container") && document.querySelector("#container>div").remove()
          this.renderChart(data)
       },
       renderChart (data) {
          const isFirstRender = !this.chartView
          if (isFirstRender) {
            this.initChart(data)
          } else {
            this.updateChart(data)
          }
       },
       initChart(data){
         if(!data.length) return
          const dateSNew = []
          var dataSorted = data.sort((a, b) => { return a.xaxis - b.xaxis })
          // this.min = Math.floor(dataSorted[0].xaxis)
          this.min = 0
          dataSorted.forEach((element,index) => {
               if(element.xaxis < this.min + 54){
                 dateSNew.push(element)
               }
          });
          // this.max = this.min + 53
          this.max = 54
          // let baseRate = 1
          if ( this.allMax - this.min > 54) {
            this.baseRate = (this.max - this.min) / (this.allMax - this.min)
          }
          if(this.baseRate < 1){
            this.btnDisabled = true
          }
          // console.log(baseRate,'baseRate');
          this.scrollProportion = this.baseRate
          this.$nextTick(function(){
              if(!this.isExport) {
                this.$refs.scrollBar.initScroll(this.baseRate)
                }
          })
          this.ds = new DataSet({
            state: {
              start: 0,
              end: this.baseRate
            }
          })
        //  console.log(dataSorted,'data');
          this.getCurrentData = []
          const dv = this.ds.createView('origin').source(dataSorted)
          if(this.baseRate != 1){
            dv.transform({
            type: 'filter',
            callback: (obj, index, arr) => {
              // this.start = null
              // this.end = null
              // console.log(arr,this.getCurrentData);
              if(obj.xaxis   >= this.ds.state.start * (this.allMax - this.min) + this.min && obj.xaxis  <= this.ds.state.end * (this.allMax - this.min) + this.min){
                this.getCurrentData.push(obj)
              }
              this.getCurrentData = this.getCurrentData.filter(function(item){
                return String(item.deviation)  != 'null'
              })
              this.getCurrentData.map(function(item,index){
                 item.index = index + 1
              })
              this.title ="工作任务延期详情信息 共(" + this.getCurrentData.length + ")条数据"
              return obj.xaxis   >= this.ds.state.start * (this.allMax - this.min) + this.min && obj.xaxis  <= this.ds.state.end * (this.allMax - this.min) + this.min
            }
          })
          } else{
              this.getCurrentData = JSON.parse(JSON.stringify(dataSorted))
              this.getCurrentData = this.getCurrentData.filter(function(item){
                return String(item.deviation)  != 'null'
              })
               this.getCurrentData.map(function(item,index){
                 item.index = index + 1
              })
             this.title ="工作任务延期详情信息 共(" + this.getCurrentData.length + ")条数据"
          }
          var asideWidth = document.querySelector(".ant-layout-sider") && document.querySelector(".ant-layout-sider").offsetWidth || 0,
             width = (document.querySelector(".ant-card-body").offsetWidth - asideWidth -40)/2 ,
             height = document.querySelector("#app").offsetHeight -240;
            //  height = this.screenHeight - 240
          const chart = new Chart({
              container: 'container',
              forceFit: true,
              height,
              background: '#fff',
              padding: [30, 45, 30, 75]
          });
          data.map(function(item){
              // item["toolTitle"] = item.activityName
              item["sexuality"] = item.taskType === "normal" ? "正常进行任务" : (item.taskType === "finished" ? "已完成任务" :item.taskType === "overdue" ? "逾期未完成" : (item.taskType === "isoverdue" ? "可能逾期任务" : (item.taskType === "red" ? "逾期已完成" : "" )))
          })
          chart.source(dv);
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
              offsetX: width - 270,
              offsetY: -2,
              width: 500,
              marker:'circle'
          }
          );
          chart
            .point()
            .position('xaxis*deviation')
            .color('sexuality',function(item){
                return item === "正常进行任务" ? "#1890FF" : (item === "已完成任务" ? "black" :item === "逾期未完成" ? "#F5222D" : (item === "可能逾期任务" ? "#FABC16" : (item === "逾期已完成" ? "#ccc" : "#fff")))
            })
            .size(4)
            .shape('circle')
            .tooltip('projectName*activityName*xaxis*deviation*targetStartTime*byTime*expectedFinishTime*actualEndTime*actualStartTime')
            // .tooltip('projectName*activityName*xaxis*deviation')
            .style({
              fillOpacity: 0.85
            });
          chart.render();
          // chart.repaint();
      },
      drawChart(){
         const  url = this.url.chartDate,id = this.$route.query.id,_this=this 
        //  模拟数据
        // this.renderChart(gkRiskReport)
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
                  _this.chartDateALL = res.result.sort(function(a,b){
                    return a.xaxis - b.xaxis
                  })
                  this.allMax = Math.ceil(_this.chartDateALL[_this.chartDateALL.length -1].xaxis)
                  const array = []
                  for(var i= 0; i<=this.allMax;i++){
                    const obj = {}
                      obj.xaxis = i,
                      obj.deviation = null
                      array.unshift(obj)
                  }
                  _this.chartDateALL = _this.chartDateALL.concat(array)
                    !_this.loading && _this.renderChart(_this.chartDateALL)
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
              //  _this.$set(_this,"chartDateALL" , gkRiskReport)
              //  !_this.loading && _this.renderChart(data)
            })
         })
         
      },
      updateChart (data) {
         let baseRate = 1
         // const data = departmentJson
         if ( this.allMax > this.max - this.min) {
               baseRate = (this.max - this.min) / this.allMax
               this.baseRate = baseRate
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
         // this.setChartViewGuide()
         // 让guide可以刷新，同时让tooltip刷新
         this.chart.repaint()
         this.$nextTick(function(){
             this.$refs.scrollBar.initScroll(this.scrollProportion, this.ds.state.start)
         })
      },
      throttleUpdateDs: throttle(function updateDs (rate) {
        this.$set(this,"getCurrentData", [])
        this.ds.setState('start', rate)
        let endPos = rate + this.scrollProportion
        // 处理精度误差问题
        if (1 - endPos < 1 / this.chartDateALL.length) {
          endPos = 1
        }
        this.ds.setState('end', endPos)
      }, 500),
      resetSearchForm(){
        this.start = null;
        this.end = null;
        this.scrollProportion = this.baseRate
        this.$bus.$emit('changeTranx', 0)
        document.querySelector("#container") && document.querySelector("#container>div").remove()
        this.renderChart(this.chartDateALL)
      }
    }
  }
</script>
<style scoped lang="less">
// return item === "正常进行任务" ? "#1890FF" : (item === "已完成任务" ? "black" :item === "逾期未完成" ? "#F5222D" : (item === "可能逾期任务" ? "#FABC16" : (item === "逾期已完成" ? "#ccc" : "#fff")))
//  {{record === "normal" ? "正常进行任务" : (record === "finished" ? "已完成任务" :record === "overdue" ? "逾期未完成" : (record === "isoverdue" ? "可能逾期任务" : (record === "red" ? "逾期已完成" : "" )))}}
   
  // 弹窗样式
    .normal{
      color: #1890FF
    }
    .finished{
      color: black
    }
    .overdue{
      color: #F5222D;
    }
    .isoverdue{
      color: #FABC16;
    }
    .red{
      color: #ccc;
    }
.detail {
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
        .export{
           position: absolute;
           top: 14px;
           right: 28px;
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
    }
    .mainMadal .madalContent:nth-child(n+2){
         margin-top: 18px;
    }
  /deep/ .ant-card-body{
       padding: 6px 8px;
       box-sizing: border-box;
  }
  /deep/.ant-spin-container{
    ul>li{
      // height: 16px;
      // line-height: 16px;
    }
  }
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
//导出弹窗
.exportAlert{
  /deep/.ant-form-item{
    margin-bottom: 0;
  }
  /deep/.ant-form-item-control{
    padding-left: 24px;
  }
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
.none{
  // display: none;
  // opacity: 0;
}
</style>
