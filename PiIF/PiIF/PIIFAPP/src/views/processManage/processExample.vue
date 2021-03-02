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
                    <a-form-item label="流程发起人">
                        <a-input v-model="queryParam.realname" @click="inputClick" aria-readonly placeholder="请选择"/>
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
                    <a href="javascript:;" v-if="!record.isSuspended" @click="handleHangUp(record.processInstanceId,0)">挂起</a>
                    <a href="javascript:;" v-if="record.isSuspended" @click="handleHangUp(record.processInstanceId,1)">启动</a>
                    <a-divider type="vertical"/>
                    <a-dropdown>
                        <a class="ant-dropdown-link">更多 <a-icon type="down"/></a>
                        <a-menu slot="overlay">
                            <a-menu-item>
                                <a href="javascript:;" @click="handleClose(record.processInstanceId)">关闭</a>
                            </a-menu-item>
                            <a-menu-item>
                                <a href="javascript:;" @click="handleDelegate(record.taskId)">委派</a>
                            </a-menu-item>
                            <a-menu-item>
                                <a href="javascript:;" @click="handleLoop(record.processInstanceId)">跳转</a>
                            </a-menu-item>
                            <a-menu-item>
                                <a href="javascript:;" @click="visibleDialogHistory = true">历史</a>
                            </a-menu-item>
                        </a-menu>
                    </a-dropdown>
                </span>

            </a-table>
        </div>

<!-- ------------------委派人弹框------------------ -->
        <a-modal 
            title="选择委托人提交流程" 
            :visible="visibleDialog" 
            @ok="handleOkDeep"
            @cancel="visibleDialog=false;" 
            width="600px" 
            :centered="true">
                <span style="padding-left:50px;">用户名：</span>
                <a-cascader
                    style="width:300px;"
                    allowClear
                    :options="options"
                    :field-names="{ label: 'departName', value: 'id', children: 'children' }"
                    :display-render="displayRender"
                    :show-search="{ filter }"
                    placeholder="请选择"
                    @change="onChange"/>
        </a-modal>
<!-- ------------------跳转节点弹框------------------ -->
        <a-modal 
            title="请选择节点" 
            :visible="visibleDialogLoop" 
            @ok="handleOkNode"
            @cancel="visibleDialogLoop=false;" 
            width="600px" 
            :centered="true">
                <span style="padding-left:50px;">请选择跳转节点：</span>
                <a-select placeholder="请选择" v-model="chooseLoop" style="width:260px;">
                    <a-select-option allowClear v-for="item in nodeList" :key="item.id" :value="item.id">
                      {{ item.name }}
                    </a-select-option>
                </a-select>
        </a-modal>
<!-- ------------------流程发起人弹框------------------ -->
        <a-modal 
            title="请选择人员" 
            :visible="visiblePeople" 
            @ok="handleOkPeople"
            @cancel="visiblePeople=false;" 
            width="700px" 
            :centered="true">
            <span>请选择部门：</span>
            <a-cascader
                allowClear
                v-model="chooseSelectedValue"
                style="width:500px"
                :options="options"
                :field-names="{ label: 'departName', value: 'id', children: 'children' }"
                :show-search="{ filter }"
                placeholder="请选择"
                @change="onChange"/>
            
            <div style="margin-top:10px;">
                <a-table
                    ref="table"
                    size="middle"
                    bordered
                    rowKey="id"
                    :columns="columnsUser"
                    :dataSource="dataUser"
                    :pagination="ipaginationUser"
                    :loading="loading"
                    @change="handleTableChangeUser"
                    :rowSelection="{ selectedRowKeys: selectedRowKeysArray, onChange: onSelectChange, type: 'radio' }">
                </a-table>
            </div>

            
        </a-modal>

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
                startUsername:'',
                realname:''
            },
            //发起人级联展示
            options: [],//级联猎豹
            selectedValue:'',//最终选择的值
            chooseSelectedValue:[],//弹框中级联选中的值
            visiblePeople:false,//流程发起人弹框
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
                    title: '流程名称',
                    align: "center",
                    dataIndex: 'processDefinitionName',
                    ellipsis: true,
                },
                {
                    title: '业务标题',
                    align: "center",
                    dataIndex: 'title'
                },
                {
                    title: '流程ID',
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
                    title: '当前任务',
                    align: "center",
                    dataIndex: 'taskName',
                    ellipsis: true,
                },
                {
                    title: '办理人',
                    align: "center",
                    dataIndex: 'assigneeName',
                    ellipsis: true,
                },
                {
                    title: '开始时间',
                    align: "center",
                    dataIndex: 'startTime',
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
            visibleDialog:false,//委派弹框
            visibleDialogLoop:false,//请选择跳转节点
            chooseLoop:'',//选择跳转节点的值
            nodeList:[],//节点列表
            visibleDialogHistory:false,//流程历史弹框
            width:document.body.offsetWidth,//动态获取屏幕宽度
            //用户
            dataUser:[],
            columnsUser: [
                {
                    title: '用户账号',
                    align: "center",
                    dataIndex: 'username'
                },
                {
                    title: '用户名称',
                    align: "center",
                    dataIndex: 'realname'
                }
            ],
            /* 用户列表分页参数 */
            ipaginationUser:{
                current: 1,
                pageSize: 10,
                total: 0
            },
            selectedRowKeysArray:[],
            selectedRows:[],
            isDelegate:false,//是否委派
        }
    },
    components: {
        HistoryList
    },
    mounted(){
        this.getDepList();
        this.loadData(1)
    },
    methods:{
        //递归
        getArrayList(datas) {
            for (var i in datas) {
                if (!datas[i].children) {
                    delete datas[i].children
                } else {
                    this.getArrayList(datas[i].children);
                }
            }
        },
        //获取部门
        getDepList(){
            this.loading = true;
            getAction('/sys/sysDepart/queryTreeList').then((res) => {
                if (res.success) {
                   this.options = res.result;
                   this.getArrayList(this.options)
                }
                if(res.code===510){
                    this.$message.warning(res.message)
                }
                this.loading = false;
            })
        },
        //级联 筛选
        filter(inputValue, path) {
            return path.some(option => option.label.toLowerCase().indexOf(inputValue.toLowerCase()) > -1);
        },
        //级联 选择
        onChange(value, selectedOptions) {
            this.selectedValue = value[value.length-1] || '';
            this.loading = true;
            this.userList(1)
        },
        //用户列表
        userList(arg){
            if (arg === 1) {
                this.ipaginationUser.current = 1;
            }
            let params = {}
            params.pageNo = this.ipaginationUser.current;
            params.pageSize = this.ipaginationUser.pageSize;
            params.depId = this.selectedValue;
            getAction("/sys/user/departUserList",params).then((res) => {
                if (res.success) {
                  this.dataUser = res.result.records
                  this.ipaginationUser.total = res.result.total;
                }
                if(res.code===510){
                    this.$message.warning(res.message)
                }
                this.loading = false;
            })
        },
        //选择
        onSelectChange(selectedRowKeys, selectedRows) {
          this.selectedRowKeysArray = selectedRowKeys;
          this.selectedRows = selectedRows;
          console.log(this.selectedRowKeysArray)
          console.log(this.selectedRows)
        },
        //点击选择搜索框
        inputClick(){
            this.isDelegate=false;
            this.chooseSelectedValue=[];
            this.visiblePeople = true;
            this.selectedRowKeysArray = [];
            this.selectedRows = [];
            this.dataUser = [];
        },
        //确定选择的用户
        handleOkPeople(){
            if(this.isDelegate){ //委派
                this.loading = true;
                getAction('/workflow/runtime/task/taskEntrust',{taskId:this.detailId,username:this.selectedRows[0].username}).then((res) => {
                    if (res.success) {
                        this.$message.success("委派成功！")
                        this.loadData();
                    }
                    if(res.code===510){
                        this.$message.warning(res.message)
                    }
                    this.loading = false;
                })
            }else{ //选择查询
                this.queryParam.realname = this.selectedRows[0].realname;
                this.queryParam.startUsername = this.selectedRows[0].username;
                this.loadData(1);
            }
            this.visiblePeople = false;
        },
        //只展示最后选项
        displayRender({ labels }) {
            return labels[labels.length - 1];
        },
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
        handleTableChangeUser(pagination, filters, sorter) {
            this.ipaginationUser = pagination;
            this.userList();
        },
        //挂起
        handleHangUp(id,flag){
            this.loading = true;
            let url = '';
            flag == 1 ? url = '/workflow/runtime/active' : url = '/workflow/runtime/suspended'
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
        //关闭
        handleClose(id){
            this.loading = true;
            getAction(`/workflow/runtime/close?processInstanceId=${id}`).then((res) => {
                if (res.success) {
                    this.$message.success("关闭成功！")
                    this.loadData();
                }
                if(res.code===510){
                    this.$message.warning(res.message)
                }
                this.loading = false;
            })
        },
        //委派
        handleDelegate(id){
            this.detailId = id;
            // this.visibleDialog = true;
            this.dataUser = [];
            this.chooseSelectedValue = [];
            this.selectedRowKeysArray = [],
            this.selectedRows = [],
            this.isDelegate = true;
            this.visiblePeople = true;
        },
        //确定委派
        handleOkDeep(){
            this.loading = true;
            getAction(`/workflow/runtime/task/taskEntrust?taskId=${id} & username=${username}`).then((res) => {
                if (res.success) {
                    this.$message.success("操作成功！")
                }
                if(res.code===510){
                    this.$message.warning(res.message)
                }
                this.loading = false;
            })
        },
        //跳转
        handleLoop(id){
            this.detailId = id;
            this.visibleDialogLoop = true;
        },
        //确定选择节点 跳转
        handleOkNode(){
            this.loading = true;
            getAction('',{}).then((res) => {
                if (res.success) {
                    this.$message.success("操作成功！")
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
