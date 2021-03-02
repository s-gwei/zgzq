<template>
    <a-card :bordered="false">
<!-- -------------------------查询区域---------------------------- -->
        <div class="table-page-search-wrapper">
            <a-form layout="inline" @keyup.enter.native="loadData(1)">      
                <a-row :gutter="24">
                    <a-col :md="8" :sm="12">
                    <a-form-item label="流程名称">
                        <a-input v-model="queryParam.processName" allowClear @search="loadData(1)" placeholder="请输入"/>
                    </a-form-item>
                    </a-col>
                    <a-col :md="8" :sm="12">
                    <a-form-item label="流程编号">
                        <a-input v-model="queryParam.processNo" allowClear @search="loadData(1)" placeholder="请输入"/>
                    </a-form-item>
                    </a-col>
                    <a-col :md="6" :sm="8">
                    <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
                        <a-button type="primary" @click="loadData(1)" icon="search">查询</a-button>
                        <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
                    </span>
                    </a-col>
                </a-row>
            </a-form>
        </div>
<!------------------------数据展示区------------------------->
        <div>
            <a-table
                ref="table"
                size="middle"
                rowKey="processInstanceId"
                :columns="columns"
                :dataSource="dataSource"
                :pagination="ipagination"
                :loading="loading"
                :scroll="{ x: 1500}"
                bordered
                @change="handleTableChange">

                <span slot="status" slot-scope="text, record">
                    <span v-if="record.isSuspended">已暂停</span>
                    <span v-else>已启动</span>
                </span>

                <span slot="action" slot-scope="text, record">
                    <a href="javascript:;" @click="visibleDialogHistory = true">历史</a>
                    <a-divider type="vertical"/>
                    <a-dropdown>
                        <a class="ant-dropdown-link">更多 <a-icon type="down"/></a>
                        <a-menu slot="overlay">
                            <a-menu-item>
                                <a href="javascript:;" @click="handle(record.id,'作废')">作废流程</a>
                            </a-menu-item>
                            <a-menu-item>
                                <a href="javascript:;" @click="handle(record.id,'取回')">取回流程</a>
                            </a-menu-item>
                        </a-menu>
                    </a-dropdown>
                </span>

            </a-table>
        </div>
<!-- ------------------历史弹框------------------ -->
        <a-drawer title="流程历史" placement="right" :visible="visibleDialogHistory" @close="visibleDialogHistory=false;" :mask="false" :width="width">
            <history-list v-if="visibleDialogHistory"></history-list>
        </a-drawer>
  

<!-- ----------------------------------END-------------------------------------- -->
    </a-card>
</template>

<script>
import {getAction,putAction,postAction,deleteAction} from '@/api/manage';
import { filterObj } from '@/utils/util';
import HistoryList from './historyList';
export default {
    data(){
        return{
            //查询条件
            queryParam:{
                processName:'',
                processNo:'',
            },
            //表格数据展示
            columns: [
                {
                    title: '序号',
                    align: "center",
                    dataIndex: 'index',
                    key:'rowIndex',
                    width:50,
                    align:"center",
                    customRender:function (t,r,index) {
                        return parseInt(index)+1;
                    }
                },
                {
                    title: '业务标题',
                    align: "center",
                    dataIndex: 'processDefinitionName',
                    ellipsis: true,
                },
                {
                    title: '流程编号',
                    align: "center",
                    dataIndex: 'title'
                },
                {
                    title: '流程名称',
                    align: "center",
                    dataIndex: 'processDefId',
                    ellipsis: true,
                },
                {
                    title: '流程实例',
                    align: "center",
                    dataIndex: 'processInstanceId',
                    ellipsis: true,
                },
                {
                    title: '发起人',
                    align: "center",
                    dataIndex: 'startUsername',
                    ellipsis: true,
                },
                {
                    title: '开始时间',
                    align: "center",
                    dataIndex: 'startTime',
                    ellipsis: true,
                },
                {
                    title: '结束时间',
                    align: "center",
                    dataIndex: 'endTime',
                    ellipsis: true,
                },
                {
                    title: '耗时',
                    align: "center",
                    dataIndex: 'spendTimes',
                    ellipsis: true,
                },
                {
                    title: '状态',
                    align: "center",
                    dataIndex: 'isSuspended',
                    scopedSlots: {customRender: 'status'},
                    ellipsis: true,
                },
                {
                    title: '操作',
                    dataIndex: 'action',
                    scopedSlots: {customRender: 'action'},
                    align: "center",
                    width: 120,
                    fixed: 'right'
                }
            ],
            dataSource:[],//表格数据
            loading:false, //加载状态
            /* 表格分页参数 */
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
            detailId:'',//数据id
            visibleDialogHistory:false,//流程历史弹框
            width:document.body.offsetWidth,//动态获取屏幕宽度
        }
    },
    components: {
        HistoryList
    },
    mounted(){
        // this.loadData(1)
    },
    methods:{
        //获取查询条件
        getQueryParams() {
            let param = Object.assign(this.queryParam);
            param.pageNo = this.ipagination.current;
            param.pageSize = this.ipagination.pageSize;
            return filterObj(param);
        },
        //数据初始化
        loadData(arg) {
            //加载数据 若传入参数1则加载第一页的内容
            if (arg === 1) {
                this.ipagination.current = 1;
            }
            this.loading = true;
            let params = this.getQueryParams();//查询条件
            getAction('/workflow/runtime/instanceList',params).then((res) => {
                if (res.success) {
                if(res.result){
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
        //重置按钮
        searchReset(){
            this.queryParam = {}
            this.loadData(1);
        },
        // 分页、排序、筛选变化时触发
        handleTableChange(pagination, filters, sorter) {
            this.ipagination = pagination;
            this.loadData();
        },
        //作废流程  取回流程
        handle(id,val){
            this.loading = true;
            let url = ''
            val == '作废' ? url = '' : url = ''
            getAction(`${url}?processInstanceId=${id}`).then((res) => {
                if (res.success) {
                    this.$message.success("操作成功！")
                    this.loadData();
                }
                if(res.code===510){
                    this.$message.warning(res.message)
                }
                this.loading = false;
            })
        },
    }
}
</script>
