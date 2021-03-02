<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">

          <a-col :md="6" :sm="12">
            <a-form-item label="姓名">
              <!--<a-input placeholder="请输入账号查询" v-model="queryParam.username"></a-input>-->
<!--              <j-input placeholder="输入姓名模糊查询" v-model="queryParam.userName"></j-input>-->
              <a-input-search v-model="queryParam.userName" @search="loadData(1)" placeholder="输入姓名模糊查询"/>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="12">
            <a-form-item label="时间:">
              <a-range-picker
                :showTime="{ format: 'YYYY-MM-DD HH:mm:DD' }"
                format="YYYY-MM-DD HH:mm:DD"
                :placeholder="['开始时间', '结束时间']"
                style="width:100%"
                @change="onChange"
              />
            </a-form-item>
          </a-col>
          <template v-if="toggleSearchStatus">
            <a-col :md="6" :sm="8">
              <a-form-item label="部门">
                <a-input placeholder="请输入部门名称" v-model="queryParam.depart"></a-input>
              </a-form-item>
            </a-col>

          </template>

          <a-col :md="6" :sm="8">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
              <a @click="handleToggleSearch" style="margin-left: 8px">
                {{ toggleSearchStatus ? '收起' : '展开' }}
                <a-icon :type="toggleSearchStatus ? 'up' : 'down'"/>
              </a>
            </span>
          </a-col>

        </a-row>
      </a-form>
    </div>

    <!-- 操作按钮区域 -->
<!--    <div class="table-operator" style="border-top: 5px">-->
<!--      <a-button type="primary" icon="download" @click="handleExportXls('用户信息')">导出</a-button>-->
<!--    </div>-->

    <!-- table区域-begin -->
    <div>

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
  import { filterObj } from '@/utils/util';
  import {getAction,putAction,postAction} from '@/api/manage';
  import JInput from '@/components/jeecg/JInput'
  import moment from 'moment'

  export default {
    name: "AttendanceList",
    components: {
      JInput
    },
    data() {
      return {
        description: '这是用户管理页面',
        queryParam: {},
        departIds:[],
        toggleSearchStatus:false,
        /* table加载状态 */
        loading:false,
        /* 分页参数 */
        ipagination:{
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
        startTime:null,
        endTime:null,
        columns: [
          {
            title: '序号',
            dataIndex: '',
            key:'rowIndex',
            width:50,
            align:"center",
            customRender:function (t,r,index) {
              return parseInt(index)+1;
            }
          },
          {
            title: '头像',
            align: "center",
            width: 60,
            dataIndex: 'avatar',
            scopedSlots: {customRender: "avatarslot"}
          },
          {
            title: '用户名',
            align: "center",
            width: 80,
            dataIndex: 'userName',
          },
          {
            title: '部门',
            align: "center",
            width: 150,
            dataIndex: 'depart',
            sorter: true
          },
          {
            title: '打卡时间',
            align: "center",
            width: 180,
            dataIndex: 'checkTime'
          },
          {
            title: '打卡状态',
            align: "center",
            width: 80,
            dataIndex: 'timeResult'
          },

        ],
        dataSource:[],
        url: {
          imgerver: window._CONFIG['domianURL'] + "/sys/common/view",
          exportXlsUrl: "/sys/user/exportXls",
          list:'/dingding/getOnAndOffRecords'
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
        param.startTime = this.startTime
        param.endTime = this.endTime
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
        if(date.length>0){
          this.startTime = date[0].valueOf()
          this.endTime = date[1].valueOf()
        }else{
          this.startTime=''
          this.endTime='';
        }
      },
    }

  }
</script>
<style scoped>
  @import '~@assets/less/common.less'
</style>
