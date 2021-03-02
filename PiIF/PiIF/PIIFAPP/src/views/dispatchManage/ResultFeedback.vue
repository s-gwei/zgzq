<template>
  <a-card :bordered="false">
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">

          <a-col :md="5" :sm="24">
          <a-form-item label="状态:">
            <a-select
              :size="size"
              defaultValue="confirmed"
            >
              <a-select-option v-for="item in statusList" :key="item.value">
                {{item.label}}
              </a-select-option>
            </a-select>
          </a-form-item>
          </a-col>

          <a-col :md="5" :sm="24">
          <a-form-item label="接收人员:">
            <a-select
              :size="size"
              defaultValue="user_01"
            >
              <a-select-option v-for="item in receiveUserList" :key="item.value">
                {{item.label}}
              </a-select-option>
            </a-select>
          </a-form-item>
          </a-col>

          <a-col :md="8" :sm="24">
          <a-form-item label="时间:">
            <a-range-picker
              :showTime="{ format: 'YYYY-MM-DD HH:mm:DD' }"
              format="YYYY-MM-DD HH:mm:DD"
              :placeholder="['开始时间', '结束时间']"
              :defaultValue="[sTime, eTime]"
              @change="onChange"
            />
          </a-form-item>
          </a-col>

          <a-col :md="6" :sm="24">
            <span class="table-page-search-submitButtons" :style="{ float: 'left', overflow: 'hidden' } || {} ">
              <a-button type="primary" icon="search" style="margin-left: 8px">查询</a-button>
              <a-button type="primary" icon="arrow-down" style="margin-left: 8px">下发</a-button>
              <a-button type="primary" icon="download" style="margin-left: 8px">导出</a-button>
              <a-button type="primary" icon="print" style="margin-left: 8px">打印</a-button>
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
  </a-card>
</template>

<script>
  import TabIframeView from '@/components/layouts/TabIframeView'
  import moment from 'moment'

  export default {
    name: 'result_feedback',
    components: {
      TabIframeView,
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
            title:'来源',
            dataIndex:'source'
          },
          {
            title:'指令内容',
            dataIndex:'orderContent'
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
            title:'分解时间',
            dataIndex:'decomposeTime'
          },
          {
            title: '分解人员',
            dataIndex: 'decomposeUser',
          },
          {
            title: '下发时间',
            dataIndex: 'issueTime',
          },
          {
            title: '下发人员',
            dataIndex:'issueUser'
          }
        ],
        order_receive_data: [],
        statusList: [
          {
            value: 'confirmed',
            label: '已确认'
          },
          {
            value: 'unconfirmed',
            label: '未确认'
          }
        ],
        receiveUserList: [
          {
            value: 'user_01',
            label: '用户A'
          },
          {
            value: 'user_02',
            label: '用户B'
          },
          {
            value: 'user_03',
            label: '用户C'
          },
          {
            value: 'user_04',
            label: '用户D'
          }
        ],
        sTime: moment(new Date(), 'YYYY-MM-DD HH:mm:ss'),
        eTime: moment(new Date(), 'YYYY-MM-DD HH:mm:ss'),
      }
    },
    created() {

    },
    mounted(){
      this.getTableData()
    },
    methods: {
      onChange(date, dateString) {
        console.log(date, dateString);
      },
      getTableData(){
        const data = [];
        for (let i = 0; i < 20; i++) {
          data.push({
            key: i,
            dispatchNumber: 'SHK20190818',
            source: '来源',
            orderContent:'三河口现地站',
            receiveTime: '2019-12-23 09:00:00',
            receiveUser:'接收员A' ,
            decomposeTime: '2019-12-23 12:00:00',
            decomposeUser: '分解员A',
            issueTime: '2019-12-23 12:00:00',
            issueUser:'下发员C'
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