<template>
   <div class="shk_pump_station">
     <a-card :bordered="false">
       <a-tabs defaultActiveKey="1">
         <a-tab-pane tab="报表" key="1">

           <!-- 查询区域 -->
           <div class="table-page-search-wrapper">
             <a-form layout="inline" @keyup.enter.native="searchQuery">
               <a-row :gutter="24">
                 <a-form-item label="站点名称:" style="float: left;margin:0 10px 10px 15px;">
                   <a-select
                     :size="size"
                     defaultValue="shk_xdz"
                     style="width: 180px"
                   >
                     <a-select-option v-for="item in stationNameList" :key="item.value">
                       {{item.label}}
                     </a-select-option>
                   </a-select>
                 </a-form-item>
                 <a-form-item label="机组:" style="float: left;margin:0 10px 5px 10px;">
                   <a-select
                     :size="size"
                     defaultValue="unit_01"
                     style="width: 150px"
                   >
                     <a-select-option v-for="item in unitList" :key="item.value">
                       {{item.label}}
                     </a-select-option>
                   </a-select>
                 </a-form-item>
                 <a-form-item label="报表类型:" style="float: left;margin:0 10px 10px 15px;">
                   <a-select
                     :size="size"
                     defaultValue="unitRun"
                     style="width: 180px"
                   >
                     <a-select-option v-for="item in statementTypeList" :key="item.value">
                       {{item.label}}
                     </a-select-option>
                   </a-select>
                 </a-form-item>
                 <a-form-item label="起始时间:" style="float: left;margin:0 10px 10px 15px;">
                   <a-date-picker
                     showTime
                     format="YYYY-MM-DD HH:mm:ss"
                     placeholder="请选择起始时间"
                   />
                 </a-form-item>
                 <a-form-item label="间隔时间:" style="float: left;margin-right: 10px;">
                   <a-select
                     :size="size"
                     defaultValue="min_10"
                     style="width: 120px"
                   >
                     <a-select-option v-for="item in intervalTimeList" :key="item.value">
                       {{item.label}}
                     </a-select-option>
                   </a-select>
                 </a-form-item>
                 <a-button type="primary" icon="search" style="margin-right: 5px">查询</a-button>
                 <a-button type="primary" icon="download" style="margin-right: 5px">导出</a-button>
                 <a-button type="primary" icon="print">打印</a-button>
               </a-row>
             </a-form>
           </div>
           <!-- 表格区域 -->
           <div class="pump-statement-table" style="margin-bottom: 16px;">
             <a-row :gutter="24">
               <a-col :span="24" style="padding-top: 10px;">
                 <a-table :columns="pump_state_columns" :dataSource="pump_state_data" size="small" />
               </a-col>
             </a-row>
           </div>
         </a-tab-pane>

         <a-tab-pane tab="曲线" key="2">
           <line-chart-multid title="泵电站电机电流曲线"
                              :dataSource="multidlineChartSource"
                              :fields="multidlineFields"
                              :style="{ padding: '0 0 0 0' }"
                              :height="400"/>
         </a-tab-pane>
       </a-tabs>
     </a-card>
   </div>
</template>

<script>
  import LineChartMultid from '@/components/chart/LineChartMultid'
  import JInput from '@/components/jeecg/JInput'
  export default {
    name: "shk_pump_station",

    components: {
      JInput,
      LineChartMultid
    },
    data() {
      return {
        size: 'default',
        stationNameList: [
          {
            value: 'shk_xdz',
            label: '三河口现地站'
          },
          {
            value: 'hjx_xdz',
            label: '黄金峡现地站'
          }
        ],
        unitList:[
          {
            value: 'unit_01',
            label: '1#'
          },
          {
            value: 'unit_02',
            label: '2#'
          }
        ],
        statementTypeList:[
          {
            value: 'unitRun',
            label: '机组运行日报表'
          },
          {
            value: 'unitTemp',
            label: '机组温度日报表'
          }
        ],
        intervalTimeList:[
          {
          label:'10分钟',
          value:'min_10'
          },{
            label:'30分钟',
            value:'min_30'
          },{
            label:'1小时',
            value:'hours'
          },{
            label:'12小时',
            value:'hours_12'
          },{
            label:'1天',
            value:'days'
          },{
            label:'2天',
            value:'days_2'
          }],
        pump_state_columns: [
          {
            title:'时间',
            dataIndex:'time',
            width:160,
            align: 'center'
          },
          {
            title:'电机状态',
            dataIndex:'electMachineStatus'
          },
          {
            title:'止回阀(%)',
            dataIndex:'checkValve'
          },
          {
            title:'电流(A)',
            dataIndex:'electricity'
          },
          {
            title:'电压(KV)',
            dataIndex:'voltage'
          },
          {
            title:'压力(KPa)',
            dataIndex:'pressure'
          },
          {
            title: '电机定子温度(度)',
            align: 'center',
            children: [
              {
                title: 'A',
                dataIndex: 'tempA',
                align: 'center'
              },
              {
                title: 'B',
                dataIndex: 'tempB',
                align: 'center'
              },
              {
                title: 'C',
                dataIndex: 'tempC',
                align: 'center'
              }
            ]
          },
          {
            title: '电机',
            align: 'center',
            children: [
              {
                title: '轴承',
                dataIndex: 'axletree',
                align: 'center'
              }
            ]
          },
          {
            title: '水位',
            dataIndex: 'waterLevel',
            align: 'center'
          }
        ],
        pump_state_data: [],
        multidlineChartSource:[
          { type: '16:38:33', '1#电机电流': 7.0, '2#电机电流': 3.9,'3#电机电流': 19.6},
          { type: '16:38:34', '1#电机电流': 6.9, '2#电机电流': 4.2 ,'3#电机电流': 3.9},
          { type: '16:38:35', '1#电机电流': 9.5, '2#电机电流': 5.7 ,'3#电机电流': 18},
          { type: '16:38:36', '1#电机电流': 14.5, '2#电机电流': 8.5 ,'3#电机电流': 3.9},
          { type: '16:38:37', '1#电机电流': 18.4, '2#电机电流': 11.9,'3#电机电流': 12},
          { type: '16:38:38', '1#电机电流': 21.5, '2#电机电流': 15.2 ,'3#电机电流': 6.9},
          { type: '16:38:39', '1#电机电流': 25.2, '2#电机电流': 17.0 ,'3#电机电流': 22},
          { type: '16:38:40', '1#电机电流': 26.5, '2#电机电流': 16.6 ,'3#电机电流': 3.9},
          { type: '16:38:41', '1#电机电流': 23.3, '2#电机电流': 14.2 ,'3#电机电流': 42.3},
          { type: '16:38:42', '1#电机电流': 18.3, '2#电机电流': 10.3,'3#电机电流': 3.9},
          { type: '16:38:43', '1#电机电流': 13.9, '2#电机电流': 6.6 ,'3#电机电流': 6},
          { type: '16:38:44', '1#电机电流': 9.6, '2#电机电流': 4.8 ,'3#电机电流': 15.8}
        ],
        multidlineFields:['1#电机电流', '2#电机电流','3#电机电流']
      }
    },
    mounted(){
      this.getTableData()
    },
    methods: {
      getTableData(){
        const data = [];
        for (let i = 0; i < 20; i++) {
          data.push({
            key: i,
            time: '2019-10-24 12:00:00',
            electMachineStatus: i/2 ===0 ? '开启':'关闭',
            checkValve:'60',
            electricity: '1000',
            voltage:'220' ,
            pressure: '800',
            tempA: '200',
            tempB: '220',
            tempC: '250',
            axletree: '',
            waterLevel: '6000',
          });
        }
        this.pump_state_data = data;
      }
    }

  }
</script>

<style scoped>

</style>