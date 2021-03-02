<template>
  <a-card :bordered="false">
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :md="8" :sm="24">
            <a-form-item label="搜索条件">
              <a-input placeholder="请输入关键字搜索"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <span class="table-page-search-submitButtons" :style="{ float: 'left', overflow: 'hidden' } || {} ">
              <a-button type="primary" icon="search">查询</a-button>
              <a-button type="primary" style="margin-left: 8px" icon="reload">重置</a-button>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <!-- 表格区域 -->
    <div class="order-receive-table" style="margin-bottom: 16px;">
      <a-row :gutter="24">
        <a-col :span="24" style="padding-top: 10px;">
          <a-table :columns="order_receive_columns" :dataSource="order_receive_data" size="small" />
        </a-col>
      </a-row>
    </div>
    <!-- 图表区域 -->
    <div class="order-receive-chart" style="margin-bottom: 16px;">
      <line-chart-multid title="SHK20190818调度指令内容" :dataSource="multidlineChartSource" :fields="multidlineFields" :style="{ padding: '0 0 0 0' }" :height="400"/>
    </div>
  </a-card>
</template>

<script>
  import TabIframeView from '@/components/layouts/TabIframeView'
  import LineChartMultid from '@/components/chart/LineChartMultid'
  export default {
    name: 'order_receive',
    components: {
      TabIframeView,
      LineChartMultid
    },
    data() {
      return {
        size: 'default',
        order_receive_columns: [
          {
            title:'调度编号',
            dataIndex:'dispatchNumber',
            width:160,
          },
          {
            title:'分调中心名称',
            dataIndex:'adjutCenterName'
          },
          {
            title:'现地站名称',
            dataIndex:'stationName'
          },
          {
            title:'调度开始时间',
            dataIndex:'startTime'
          },
          {
            title:'调度结束时间',
            dataIndex:'endTime'
          },
          {
            title:'接收时间',
            dataIndex:'receiveTime'
          },
          {
            title: '接收人员',
            dataIndex: 'receiveUser',
          },
          {
            title: '当前状态',
            align: 'center',
            dataIndex:'status'
          }
        ],
        order_receive_data: [],
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
    created() {

    },
    mounted(){
      this.getTableData()
    },
    methods: {
      getTableData(){
        const data = [];
        for (let i = 0; i < 6; i++) {
          data.push({
            key: i,
            dispatchNumber: 'SHK20190818',
            adjutCenterName: '三河口分调中心',
            stationName:'三河口现地站',
            startTime: '2019-12-23 09:00:00',
            endTime:'2019-12-23 12:00:00' ,
            receiveTime: '2019-12-23 12:00:00',
            receiveUser: '接收员A',
            status: '未确认'
          });
        }
        this.order_receive_data = data;
      }
    }
  }
</script>
<style scoped>
  .ant-card-body .table-operator {
    margin-bottom: 18px;
  }

  .ant-table-tbody .ant-table-row td {
    padding-top: 15px;
    padding-bottom: 15px;
  }

  .anty-row-operator button {
    margin: 0 5px
  }

  .ant-btn-danger {
    background-color: #ffffff
  }

  .ant-modal-cust-warp {
    height: 100%
  }

  .ant-modal-cust-warp .ant-modal-body {
    height: calc(100% - 110px) !important;
    overflow-y: auto
  }

  .ant-modal-cust-warp .ant-modal-content {
    height: 90% !important;
    overflow-y: hidden
  }

  .statistic {
    padding: 0px !important;
    margin-top: 50px;
  }

  .statistic h4 {
    margin-bottom: 20px;
    text-align: center !important;
    font-size: 24px !important;;
  }

  .statistic #canvas_1 {
    width: 100% !important;
  }
</style>