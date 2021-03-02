<template>
  <a-card :bordered="false">
  <div class="page-header-index-wide">
    <a-row :gutter="24">
      <a-col :sm="24" :md="12" :xl="6" :style="{ marginBottom: '24px' }">
        <chart-card :loading="loading" title="防控上报" :total="count">
          <a-tooltip title="防控上报说明" slot="action">
            <a-icon type="info-circle-o" />
          </a-tooltip>
          <div>
            <trend flag="up" style="margin-right: 16px;">
              <span slot="term">未上报</span>
              5%
            </trend>
            <trend flag="down">
              <span slot="term">日同比</span>
              11%
            </trend>
          </div>
          <template slot="footer">日防控上报<span>213</span></template>
        </chart-card>
      </a-col>
      <a-col :sm="24" :md="12" :xl="6" :style="{ marginBottom: '24px' }">
        <chart-card :loading="loading" title="员工流动" :total="549 | NumberFormat">
          <a-tooltip title="员工流动说明" slot="action">
            <a-icon type="info-circle-o" />
          </a-tooltip>
          <div>
            <mini-area />
          </div>
          <template slot="footer">日平均流量<span> {{ '15' | NumberFormat }}</span></template>
        </chart-card>
      </a-col>
      <a-col :sm="24" :md="12" :xl="6" :style="{ marginBottom: '24px' }">
        <chart-card :loading="loading" title="交通开通" :total="205 | NumberFormat">
          <a-tooltip title="交通开通说明" slot="action">
            <a-icon type="info-circle-o" />
          </a-tooltip>
          <div>
            <mini-bar :height="40" :dataSource="multidlineChartSource" />
          </div>
          <template slot="footer">完全开通占比 <span>95%</span></template>
        </chart-card>
      </a-col>
      <a-col :sm="24" :md="12" :xl="6" :style="{ marginBottom: '24px' }">
        <chart-card :loading="loading" title="复工情况" total="90%">
          <a-tooltip title="复工情况说明" slot="action">
            <a-icon type="info-circle-o" />
          </a-tooltip>
          <div>
            <mini-progress color="rgb(19, 194, 194)" :target="99" :percentage="90" :height="8" />
          </div>
          <template slot="footer">
            <trend flag="down" style="margin-right: 16px;">
              <span slot="term">同周比</span>
              12%
            </trend>
          </template>
        </chart-card>
      </a-col>
    </a-row>
  </div>
  <div style="margin-top: 20px;">
      <a-table
        ref="table"
        size="middle"
        rowKey="id"
        :columns="columns"
        :dataSource="dataSource"
        :pagination="ipagination"
        :loading="loading"
        @change="handleTableChange">
        <template slot="avatarslot" slot-scope="text, record, index">
          <div class="anty-img-wrap">
            <a-avatar shape="square" :src="getAvatarView(record.avatar)" icon="user"/>
          </div>
        </template>
      </a-table>
    </div>
  </a-card>
</template>

<script>
  import ChartCard from '@/components/ChartCard'
  import ACol from "ant-design-vue/es/grid/Col"
  import ATooltip from "ant-design-vue/es/tooltip/Tooltip"
  import MiniBar from '@/components/chart/MiniBar'
  import MiniProgress from '@/components/chart/MiniProgress'
  import Bar from '@/components/chart/Bar'
  import MiniArea from '@/components/chart/MiniArea'
  import Trend from '@/components/Trend'
  import { getLoginfo,getVisitInfo } from '@/api/api'
  import moment from 'moment'
  import {getAction, postAction, deleteAction} from '@/api/manage'
  import { filterObj } from '@/utils/util';
  export default {
    name: "PISXEpidemicSituation",
    components: {
      ATooltip,
      ACol,
      ChartCard,
      MiniBar,
      MiniProgress,
      Bar,
      Trend,
      MiniArea
    },
    data() {
      return {
        description: '这是用户管理页面',
        queryParam: {},
        multidlineChartSource:[
          { x: '16:38:33', y: 7.0},
          { x: '16:38:34', y: 6.9},
          { x: '16:38:35', y: 9.5},
          { x: '16:38:36', y: 14.5},
          { x: '16:38:37', y: 18.4},
          { x: '16:38:38', y: 21.5},
          { x: '16:38:39', y: 25.2},
          { x: '16:38:40', y: 26.5},
          { x: '16:38:41', y: 23.3},
          { x: '16:38:42', y: 18.3},
          { x: '16:38:43', y: 13.9},
          { x: '16:38:44', y: 9.6}
        ],
        toggleSearchStatus: false,
        /* table加载状态 */
        loading: false,
        /* 分页参数 */
        ipagination: {
          current: 1,
          pageSize: 10,
          pageSizeOptions: ['10', '20', '30'],
          showTotal: (total, range) => {
            return range[0] + "-" + range[1] + " 共" + total + "条"
          },
          showQuickJumper: true,
          showSizeChanger: true,
          total: 0
        },
        sTime: moment(new Date(), 'YYYY-MM-DD HH:mm:ss'),
        eTime: moment(new Date(), 'YYYY-MM-DD HH:mm:ss'),
        columns: [
          {
            title: '提交时间',
            dataIndex: 'commit_time',
            width: 110,
            align: "center",
          },
          {
            title: '姓名',
            align: "center",
            width: 60,
            dataIndex: 'commit_name',
          },
          {
            title: '所在部门',
            align: "center",
            width: 80,
            dataIndex: 'commit_depart'
          },
          {
            title: '今日体温',
            align: "center",
            width: 60,
            dataIndex: 'commit_temp'
          },
          {
            title: '14天去过的城市',
            align: "center",
            width: 120,
            dataIndex: 'commit_city'
          },
          {
            title: '目前所在地',
            align: "center",
            width: 120,
            dataIndex: 'commit_city_day'
          },
          {
            title: '所在地停留天数',
            align: "center",
            width: 85,
            dataIndex: 'commit_day'
          },
          {
            title: '所在地公共交通',
            align: "center",
            width: 85,
            dataIndex: 'commit_traffic'
          },
          {
            title: '所在地交通管制',
            align: "center",
            width: 80,
            dataIndex: 'commit_traffic_desc'
          },{
            title: '是否确诊',
            align: "center",
            width: 60,
            dataIndex: 'commit_diagnosis'
          },
          {
            title: '是否疑似',
            align: "center",
            width: 60,
            dataIndex: 'commit_doubt'
          },
          {
            title: '密切接触史',
            align: "center",
            width: 80,
            dataIndex: 'contact'
          },
          {
            title: '出现的症状',
            align: "center",
            width: 80,
            dataIndex: 'commit_symptom'
          },
        ],
        startTime:null,
        endTime:null,
        dataSource:[],
        count:0,
        url: {
          imgerver: window._CONFIG['domianURL'] + "/sys/common/view",
          exportXlsUrl: "/sys/user/exportXls",
          list:'/online/cgform/api/getData/30237cf3cd344143953d8eea28554fd6'
        },
      }
    },
    computed: {
      importExcelUrl: function(){
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
      }
    },
    mounted(){
      this.loadData(1);
    },
    methods: {
      getAvatarView: function (avatar) {
        return this.url.imgerver + "/" + avatar;
      },
      loadData(arg) {
        if(!this.url.list){
          this.$message.error("请设置url.list属性!")
          return
        }
        //加载数据 若传入参数1则加载第一页的内容
        if (arg === 1) {
          this.ipagination.current = 1;
        }
        this.loading = true;
        let params = this.getQueryParams();//查询条件
        getAction(this.url.list,params).then((res) => {
          if (res.success) {
            if(res.result !== null){
              this.dataSource = res.result.records;
              this.ipagination.total = res.result.total;
              this.count = res.result.total
            }else{
              this.dataSource = [];
            }
          }
          if(res.code===510){
            this.$message.warning(res.message)
          }
          this.loading = false;
        })
      },
      searchQuery() {
        this.loadData(1);
      },
      searchReset() {
        this.queryParam = {}
        this.loadData(1);
      },
      getQueryParams() {
        //获取查询条件
        var param = Object.assign(this.queryParam);
        param.pageNo = this.ipagination.current;
        param.pageSize = this.ipagination.pageSize;
        param.column = 'createTime'
        param.order = 'desc'
        param.superQueryMatchType = 'and'
        return filterObj(param);
      },
      handleTableChange(pagination, filters, sorter) {
        //分页、排序、筛选变化时触发
        //TODO 筛选
        if (Object.keys(sorter).length > 0) {
          this.isorter.column = sorter.field;
          this.isorter.order = "ascend" == sorter.order ? "asc" : "desc"
        }
        this.ipagination = pagination;
        this.loadData();
      },
      handleToggleSearch(){
        this.toggleSearchStatus = !this.toggleSearchStatus;
      },
      onChange(date, dateString) {
        this.startTime = date[0].valueOf()
        this.endTime = date[1].valueOf()
      },
    }
  }
</script>
