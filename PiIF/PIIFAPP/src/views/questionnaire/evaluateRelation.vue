<template>
  <a-card :bordered="false">

    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">

          <a-col :md="6" :sm="24">
            <a-form-item label="用户名">
              <a-input placeholder="请输入用户名" v-model="queryParam.realname"></a-input>
            </a-form-item>
          </a-col>

          <a-col :md="3" :sm="24">
            <a-form-item>
              <LSelectUserButtonDep @change="changeRealName" :multi="true"></LSelectUserButtonDep>
            </a-form-item>
          </a-col>

          <a-col :md="6" :sm="24">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
            </span>
          </a-col>

        </a-row>
      </a-form>
    </div>

    <!-- table区域-begin -->
    <div style="margin-bottom: 50px;">
      <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
        <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择 <a style="font-weight: 600">{{ selectedRowKeys.length }}</a>项
        <a style="margin-left: 24px" @click="onClearSelected">清空</a>
      </div>

      <a-table
        ref="table"
        size="middle"
        bordered
        rowKey="id"
        filterMultiple="filterMultiple"
        :columns="columns"
        :dataSource="dataSource"
        :pagination="ipagination"
        :loading="loading"
        :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange,type:type}"
        @change="handleTableChange"
        :customRow="clickThenCheck"
      >

        <span slot="action" slot-scope="text, record">
          <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
            <a>删除</a>
          </a-popconfirm>
        </span>

      </a-table>
    </div>

    <div>
      <relationList ref="relationList"></relationList>
    </div>

  </a-card>
</template>

<script>
  import {getAction, deleteAction, postAction} from '@/api/manage'
  import relationList from './relationList.vue'
  import addRelationModel from './form/addRelationModel'
  import LSelectUserButtonDep from '@/components/jeecgbiz/LSelectUserButtonDep'
  import {JeecgListMixin} from '@/mixins/JeecgListMixin'

  export default {
    name: "evaluateRelation",
    mixins: [JeecgListMixin],
    components: {
      addRelationModel,
      relationList,
      LSelectUserButtonDep
    },
    data() {
      return {
        description: '订单管理页面',
        /* 分页参数 */
        ipagination:{
          current: 1,
          pageSize: 5,
          pageSizeOptions: ['5', '10', '20'],
          showTotal: (total, range) => {
            return range[0] + "-" + range[1] + " 共" + total + "条"
          },
          showQuickJumper: true,
          showSizeChanger: true,
          total: 0
        },
        dataSource: [],
        containerId: '',
        // 表头
        columns: [{
          title: '#',
          dataIndex: '',
          key: 'rowIndex',
          width: 60,
          align: "center",
          customRender: function (t, r, index) {
            return parseInt(index) + 1;
          }
        },
          {
            title: '用户账号',
            align: "center",
            dataIndex: 'username',
            width: 120
          },
          {
            title: '用户名',
            align: "center",
            dataIndex: 'realname'
          },
          {
            title: '手机号码',
            align: "center",
            width: 100,
            dataIndex: 'phone'
          },
          {
            title: '邮箱',
            align: "center",
            dataIndex: 'email'
          },
          {
            title: '账号状态',
            align: "center",
            width: 80,
            dataIndex: 'status_dictText'
          },
          {
            title: '操作',
            key: 'operation',
            align: 'center',
            width: 130,
            scopedSlots: {customRender: 'action'},
          }
          ],
        // 分页参数
        type: "radio",
        url: {
          list: "/investigation/participant/list",
          add: '/investigation/participant/add',
          delete: '/investigation/participant/delete'
        },
      }
    },
    created () {
      this.containerId = this.$route.query.id
    },
    methods: {
      loadData(arg) {
        if (arg === 1) {
          this.ipagination.current = 1;
        }
        var params = this.getQueryParams();
        getAction(this.url.list, Object.assign({containerId: this.$route.query.id, pageNo : this.ipagination.current,
          pageSize :this.ipagination.pageSize}, params)).then((res) => {
          if (res.success) {
            this.dataSource = res.result.records;
            if (this.dataSource[0].id) {
              this.selectedRowKeys = [this.dataSource[0].id]
              this.selectionRows = [this.dataSource[0]]
              this.$refs.relationList.getOrderMain(this.selectedRowKeys[0], this.selectionRows[0].realname);
              this.ipagination.total = res.result.total;
            }
          } else {
            this.dataSource = null;
          }
        })
      },
      clickThenCheck(record) {
        return {
          on: {
            click: () => {
              this.onSelectChange(record.id.split(","), [record]);
            }
          }
        };
      },
      onSelectChange(selectedRowKeys, selectionRows) {
        this.selectedRowKeys = selectedRowKeys;
        this.selectionRows = selectionRows;
        this.$refs.relationList.getOrderMain(this.selectedRowKeys[0], this.selectionRows[0].realname);
      },
      onClearSelected() {
        this.selectedRowKeys = [];
        this.selectionRows = [];
        this.$refs.relationList.queryParam.mainId = null;
        this.$refs.relationList.loadData();
        this.$refs.relationList.selectedRowKeys = [];
        this.$refs.relationList.selectionRows = [];
      },

      handleDelete: function (id) {
        var that = this;
        deleteAction(that.url.delete, {userId: id, containerId: this.containerId}).then((res) => {
          if (res.success) {
            that.$message.success(res.message);
            that.loadData();
          } else {
            that.$message.warning(res.message);
          }
        });
      },
      searchQuery:function(){
        this.selectedRowKeys = [];
        this.selectionRows = [];
        this.$refs.relationList.queryParam.containerId = null;
        this.$refs.relationList.loadData();
        this.$refs.relationList.selectedRowKeys = [];
        this.$refs.relationList.selectionRows = [];
        this.loadData();
      },
      changeRealName (id, ids, users) {
        postAction(this.url.add + '?containerId=' + this.containerId, ids).then((res) => {
          if (res.success) {
            this.$message.success(res.message);
            this.loadData();
          } else {
            this.$message.warning(res.message);
          }
        })
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
</style>