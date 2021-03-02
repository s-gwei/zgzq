<template>
  <div>
      <a-tabs default-active-key="1" @change="tabCallback" tabPosition="left">
        <a-tab-pane key="1" tab="流程节点">
          <div class="right-content">
            <a-button type="primary" @click="add"><a-icon type="plus" />新增</a-button>
            <div class="ant-alert ant-alert-info" style="margin:10px auto;">
              <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择 <a style="font-weight: 600">{{ selectedRowKeys.length }}</a>项
              <a style="margin-left: 24px" @click="onClearSelected">清空</a>
            </div>
            <a-table
                ref="table"
                size="middle"
                bordered
                rowKey="id"
                :columns="columns"
                :dataSource="dataSource"
                :pagination="ipagination"
                :loading="loading"
                :scroll="{y: 230 }">
                <!-- :rowSelection="{fixed:true,selectedRowKeys: selectedRowKeys, onChange: onSelectChange}" -->
                    <span slot="action" slot-scope="text, record">
                        <!-- <a @click="handleEdit(record)" class="distance">编辑</a> -->
                        <!-- <a @click="handleAuthority(record)" class="distance">权限设置</a> -->
                        <a @click="handleDelete(record.id)">删除</a>
                    </span>
            </a-table>
          </div>
        </a-tab-pane>
        <a-tab-pane key="2" tab="业务关联" force-render>
          <div class="right-content">
            <a-button type="primary" @click="add"><a-icon type="plus" />新增</a-button>
            <div class="ant-alert ant-alert-info" style="margin:10px auto;">
              <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择 <a style="font-weight: 600">{{ selectedRowKeys.length }}</a>项
              <a style="margin-left: 24px" @click="onClearSelected">清空</a>
            </div>
            <a-table
                ref="table"
                size="middle"
                bordered
                rowKey="id"
                :columns="columns"
                :dataSource="dataSource"
                :pagination="ipagination"
                :loading="loading"
                :scroll="{y: 230 }">
                <template slot="formTypeSolt" slot-scope="text, record, index">
                    <span v-if="record.formType==1">online表单</span>
                    <span v-if="record.formType==2">自定义表单</span>
                    <span v-if="record.formType==3">自定义开发</span>
                </template>
                <!-- :rowSelection="{fixed:true,selectedRowKeys: selectedRowKeys, onChange: onSelectChange}" -->
                    <span slot="action" slot-scope="text, record">
                        <!-- <a @click="handleEdit(record)" class="distance">编辑</a> -->
                        <a @click="handleDelete(record.id)">删除</a>
                    </span>
            </a-table>
          </div>
        </a-tab-pane>
      </a-tabs>
<!-- --------------------------新增流程节点-------------------------------- -->
        <a-modal 
            title="新增" 
            :visible="visibleAdd" 
            @ok="handleOk" 
            @cancel="visibleAdd=false" 
            :centered="true" 
            width="800px">
            <a-form-model ref="form" :model="form" :label-col="labelCol" :wrapper-col="wrapperCol">
                <a-form-model-item label="节点名称">
                    <a-select 
                        placeholder="请选择节点名称" 
                        allowClear 
                        v-model="form.processNodeName" 
                        :getPopupContainer="triggerNode => {
                            return triggerNode.parentNode || document.body;
                        }" 
                        @change="chooseNode(form.processNodeName)">
                        <a-select-option v-for="item in nodeLists" :key="item.nodeId" :value="item.nodeName">
                            {{ item.nodeName || item.nodeId }}
                        </a-select-option>
                    </a-select>
                </a-form-model-item>
                <a-form-model-item label="节点编码">
                    <a-input v-model="form.processNodeCode" disabled placeholder="请输入节点编码"/>
                </a-form-model-item>
                <a-form-model-item label="PC表单地址">
                    <a-input v-model="form.modelAndView" placeholder="请输入PC表单地址"/>
                </a-form-model-item>
                <a-form-model-item label="移动表单地址">
                    <a-input v-model="form.modelAndViewMobile" placeholder="请输入移动表单地址"/>
                </a-form-model-item>
                <a-form-model-item label="超时提醒（时）">
                    <a-input-number v-model="form.nodeTimeout" :min="0" />（单位小时，0表示不提醒）
                </a-form-model-item>
            </a-form-model>
        </a-modal>    
      
<!-- -----------------------------流程节点权限设置--------------------------------- -->
        <a-drawer title="节点权限设置" placement="right"  v-if="visibleAuthority" :visible="visibleAuthority" @close="visibleAuthority=false;" width="900px">
            <!-- -------------------------查询区域---------------------------- -->
            <div class="table-page-search-wrapper">
                <a-form layout="inline" @keyup.enter.native="loadDataAuthority(1)">      
                    <a-row :gutter="24">
                        <a-col :md="8" :sm="12">
                            <a-form-item label="表单类型">
                                <a-select placeholder="请选择" allowClear v-model="queryParam.processType" :getPopupContainer="(triggerNode)=>{ return triggerNode.parentNode}" >
                                    <a-select-option v-for="item in processTypeList" :key="item.id" :value="item.id">
                                        {{ item.name }}
                                    </a-select-option>
                                </a-select>
                            </a-form-item>
                        </a-col>
                        <a-col :md="8" :sm="12">
                            <a-form-item label="规则编码">
                                <a-input v-model="queryParam.processKey" allowClear @search="loadDataAuthority(1)" placeholder="输入规则编码"/>
                            </a-form-item>
                        </a-col>
                        <a-col :md="6" :sm="8">
                            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
                                <a-button type="primary" @click="loadDataAuthority(1)" icon="search">查询</a-button>
                                <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
                            </span>
                        </a-col>
                    </a-row>
                </a-form>
            </div>
            <a-button type="primary" style="margin-bottom:10px;" @click="addAut"><a-icon type="plus" />新增</a-button>
            <a-table
                ref="tables"
                size="middle"
                bordered
                rowKey="id"
                :columns="columnsAut"
                :dataSource="dataSourceAut"
                :pagination="ipaginationAut"
                :loading="loading"
                :scroll="{y: 300 }">
                    <span slot="action" slot-scope="text, record">
                        <a @click="handleAutEdit(record)" class="distance">编辑</a>
                        <a @click="handleAutDelete(record.id)">删除</a>
                    </span>
            </a-table>
        </a-drawer>
<!-- ------------------------------新增节点页面权限表单------------------------- -->
        <a-modal 
            title="新增" 
            :visible="visibleAddAut" 
            @ok="handleOkAut" 
            @cancel="visibleAddAut = false;" 
            :centered="true" 
            width="800px"
            :zIndex="99999">
            <a-form-model ref="formModel" :model="formAut" :label-col="labelCol" :wrapper-col="wrapperCol">
                <a-form-model-item label="表单类型">
                    <a-input v-model="formAut.y1" placeholder="请输入节点名称"/>
                </a-form-model-item>
                <a-form-model-item label="表名">
                    <a-input v-model="formAut.y2" placeholder="请输入节点编码"/>
                </a-form-model-item>
                <a-form-model-item label="规则编码">
                    <a-input v-model="formAut.y3" placeholder="请输入PC表单地址"/>
                </a-form-model-item>
                <a-form-model-item label="规则名称">
                    <a-input v-model="formAut.y4" placeholder="请输入移动表单地址"/>
                </a-form-model-item>
                <a-form-model-item label="超时提醒（时）">
                    <a-radio-group v-model="formAut.y5">
                        <a-radio :value="1">显示</a-radio>
                        <a-radio :value="2">禁用</a-radio>
                    </a-radio-group>
                </a-form-model-item>
                <a-form-model-item label="状态">
                    <a-radio-group v-model="formAut.y6">
                        <a-radio :value="1">正向有效</a-radio>
                        <a-radio :value="2">反向有效</a-radio>
                    </a-radio-group>
                </a-form-model-item>
            </a-form-model>
        </a-modal>    
       
<!-- -------------------------业务关联新增弹框---------------------------- -->
        <a-modal 
            title="新增" 
            :visible="visibleAddBus" 
            @ok="handleOk" 
            @cancel="visibleAddBus=false" 
            :centered="true" 
            width="800px">
            <a-form-model ref="ruleForm" :model="formBus" :rules="rules" :label-col="labelCol" :wrapper-col="wrapperCol">
                <a-form-model-item label="表单类型" prop="formType">
                    <a-select placeholder="请选择" allowClear v-model="formBus.formType">
                        <a-select-option v-for="item in processTypeList" :key="item.id" :value="item.id">
                            {{ item.name }}
                        </a-select-option>
                    </a-select>
                </a-form-model-item>
                <a-form-model-item label="表名" prop="formTableName">
                    <a-input v-model="formBus.formTableName" placeholder="请输入表名"/>
                </a-form-model-item>
                <a-form-model-item label="唯一编码" prop="relationCode">
                    <a-input v-model="formBus.relationCode" placeholder="请输入唯一编码"/>
                </a-form-model-item>
                <a-form-model-item label="流程状态列名" prop="flowStatusCol">
                    <a-input v-model="formBus.flowStatusCol" placeholder="请输入流程状态列名"/>
                </a-form-model-item>
                <a-form-model-item label="标题表达式" prop="titleExp">
                    <a-input v-model="formBus.titleExp" placeholder="请输入标题表达式"/>
                    <a-row style="color:red;font-size:12px;">参考：XXXX【${busname}】-XXXX【${name}】；其中${}表达式取流程变量的值</a-row>
                </a-form-model-item>
            </a-form-model>
        </a-modal>    
<!-- -----------------------------END--------------------------------- -->
  </div>
</template>

<script>
import {getAction,putAction,postAction,deleteAction} from '@/api/manage';
import { filterObj } from '@/utils/util';
export default {
    name: "processSetting",
    props:['detailId'],
    data(){
        return{
            tabKey:1,
            columns:[],
            dataSource:[],
            selectedRowKeys: [],
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
            loading:false, //加载状态
            visibleAdd:false,//新增弹框是否显示
            labelCol: { span: 6 },
            wrapperCol: { span: 14 },
            form: {
                processNodeName:'',
                processNodeCode:'',
                modelAndView:'',
                modelAndViewMobile:'',
                nodeTimeout:''
            },
            nodeLists:[],//节点列表
            //------节点权限设置-----------
            visibleAuthority:false,//节点权限设置弹框
            //查询条件
            queryParam:{
                processKey:'',
                processType:undefined
            },
            processTypeList:[{name:"online表单",id:'1'},{name:"自定义表单",id:'2'},{name:"自定义开发",id:'3'}],//流程类型列表
             /* 分页参数 */
            ipaginationAut:{
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
            dataSourceAut:[{id:1,y1:'w',y2:'e',y3:'r',y4:'t',y5:1,y6:1}],
            columnsAut:[
                {
                    title: '序号',
                    dataIndex: '',
                    key:'rowIndex',
                    width:60,
                    align:"center",
                    customRender:function (t,r,index) {
                        return parseInt(index)+1;
                    }
                },
                {
                    title: '规则名称',
                    align: "center",
                    dataIndex: 'x11',
                    ellipsis: true,
                },
                {
                    title: '规则编码',
                    align: "center",
                    dataIndex: 'x22',
                    ellipsis: true,
                },{
                    title: '表单类型',
                    align: "center",
                    dataIndex: 'x33',
                    ellipsis: true,
                },{
                    title: '策略',
                    align: "center",
                    dataIndex: 'x44',
                    ellipsis: true,
                },{
                    title: '状态',
                    align: "center",
                    dataIndex: 'x55',
                    ellipsis: true,
                },{
                    title: '操作',
                    dataIndex: 'action',
                    scopedSlots: {customRender: 'action'},
                    align: "center",
                    // width: 100
                }
            ],
            visibleAddAut:false,
            formAut:{
                y1:'',
                y2:'',
                y3:'',
                y4:'',
                y5:1,
                y6:1
            },
            //业务关联
            visibleAddBus:false,
            formBus:{
                formType:'',
                formTableName:'',
                relationCode:'',
                flowStatusCol:'',
                titleExp:''
            },
            rules: {
                formType: [{ required: true, message: '请选择表单类型', trigger: 'change' }],
                formTableName: [{ required: true, message: '请输入表名', trigger: 'blur' }],
                relationCode: [{ required: true, message: '请输入唯一编码', trigger: 'blur' }],
                flowStatusCol: [{ required: true, message: '请输入流程状态列名', trigger: 'blur' }],
                titleExp: [{ required: true, message: '请输入标题表达式', trigger: 'blur' }],
            },
        }
    },
    mounted(){
        this.tabCallback(1)
    },
    methods:{
// ---------------------------流程节点------------------------------------------- 
        //获取流程节点数据
        getNodes(){
            this.loading = true;
            getAction(`/workflow/design/process/node?id=${this.detailId}`).then((res) => {
                if (res.success) {
                    this.nodeLists = res.result;
                    this.visibleAdd = true;
                }
                if(res.code===510){
                this.$message.warning(res.message)
                }
                this.loading = false;
            })
        },
        //根据流程名称获取编码
        chooseNode(val){
            this.nodeLists.find((item)=>{
                if(item.nodeName == val || item.nodeId == val){
                    this.form.processNodeCode = item.nodeId
                }
            })
        },
        //流程配置
        tabCallback(key) {
            this.tabKey = key;
            if(key == 1){
                this.columns = [
                    {
                        title: '序号',
                        dataIndex: '',
                        key:'rowIndex',
                        width:60,
                        align:"center",
                        customRender:function (t,r,index) {
                            return parseInt(index)+1;
                        }
                    },
                    {
                        title: '节点名称',
                        align: "center",
                        dataIndex: 'processNodeName',
                        ellipsis: true,
                    },
                    {
                        title: '节点编码',
                        align: "center",
                        dataIndex: 'processNodeCode',
                        ellipsis: true,
                    },{
                        title: 'PC表单地址',
                        align: "center",
                        dataIndex: 'modelAndView',
                        ellipsis: true,
                    },{
                        title: '移动表单地址',
                        align: "center",
                        dataIndex: 'modelAndViewMobile',
                        ellipsis: true,
                    },{
                        title: '超时提醒（时）',
                        align: "center",
                        dataIndex: 'nodeTimeout',
                        ellipsis: true,
                    },{
                        title: '操作',
                        dataIndex: 'action',
                        scopedSlots: {customRender: 'action'},
                        align: "center",
                        // width: 200
                    }
                ]
            }else{
                this.columns = [
                    {
                        title: '序号',
                        dataIndex: '',
                        key:'rowIndex',
                        width:60,
                        align:"center",
                        customRender:function (t,r,index) {
                            return parseInt(index)+1;
                        }
                    },
                    {
                        title: '唯一编码',
                        align: "center",
                        dataIndex: 'relationCode',
                        ellipsis: true,
                    },
                    {
                        title: '表名/自定义表单CODE',
                        align: "center",
                        dataIndex: 'formTableName',
                        ellipsis: true,
                    },{
                        title: '表单类型',
                        align: "center",
                        dataIndex: 'formType',
                        ellipsis: true,
                        scopedSlots: {customRender: "formTypeSolt"},
                        width: 100
                    },{
                        title: '业务标题表达式',
                        align: "center",
                        dataIndex: 'titleExp',
                        ellipsis: true,
                    },{
                        title: '流程状态列名',
                        align: "center",
                        dataIndex: 'flowStatusCol',
                        ellipsis: true,
                    },{
                        title: '操作',
                        dataIndex: 'action',
                        scopedSlots: {customRender: 'action'},
                        align: "center",
                        // width: 100
                    }
                ]
            }
            this.loadData(1);
        },  
        //获取查询条件
        getQueryParams() {
            let param = {}
            param.processId = this.detailId;
            param.pageNo = this.ipagination.current;
            param.pageSize = this.ipagination.pageSize;
            return filterObj(param);
        },    
        //数据初始化
        loadData(arg) {
            if (arg === 1) {
                this.ipagination.current = 1;
            }
            let url = '';
            this.tabKey == 1 ? url = '/workflow/config/node/list' : url = '/workflow/config/form/list'
            this.loading = true;
            let params = this.getQueryParams();//查询条件
            getAction(url,params).then((res) => {
                if (res.success) {
                    if(res.result){
                        this.dataSource = res.result.records;
                        this.ipagination.total = res.result.total;
                    }else{
                        this.dataSource = [];
                    }
                }
                if(res.code===500){
                    this.$message.warning(res.message)
                }
                this.loading = false;
            })
        },
        add () {
            if(this.tabKey == 1){
                this.form = {};
                this.getNodes();
            }else{
                this.formBus = {};
                this.visibleAddBus = true;
            }
        },
        //多选
        onSelectChange(selectedRowKeys) {
            console.log('selectedRowKeys changed: ', selectedRowKeys);
            this.selectedRowKeys = selectedRowKeys;
        },
        //清空已选项
        onClearSelected(){
            this.selectedRowKeys = [];
        },
        //新增确定
        handleOk(){
            this.loading = true;
            if(this.tabKey==1){
                this.form.processId = this.detailId;
                postAction('/workflow/config/node',this.form).then((res) => {
                    if (res.success) {
                        this.$message.success("新增成功！")
                        this.visibleAdd = false;
                        this.loadData(1)
                    }
                    if(res.code===500){
                        this.$message.warning(res.message)
                    }
                    this.loading = false;
                })
            }else{
                this.$refs.ruleForm.validate(valid => {
                    if (valid) {
                        this.formBus.processId = this.detailId;
                        postAction('/workflow/config/form/',this.formBus).then((res) => {
                            if (res.success) {
                                this.$message.success("新增成功！")
                                this.visibleAddBus = false;
                                this.loadData(1)
                            }
                            if(res.code===500){
                                this.$message.warning(res.message)
                            }
                            this.loading = false;
                        })
                        this.loading = false;
                    } else {
                        return false;
                    }
                });
            }
        },
        //编辑
        handleEdit(row){
            if(this.tabKey == 1){
                this.form = Object.assign(this.form,row);
                this.visibleAdd = true;
            }else{
                this.formBus = Object.assign(this.formBus,row);
                this.visibleAddBus = true;
            }
        },
        //删除
        handleDelete(row){
            let _this = this;
            this.$confirm({
                title: '删除',
                content: '确认删除此流程节点吗？',
                okText: '确定',
                okType: 'danger',
                cancelText: '取消',
                width: "400px !important",
                onOk() {
                    _this.loading = true;
                    let url = '';
                    _this.tabKey == 1 ? url = '/workflow/config/nodedelete' : url = '/workflow/config/form/delete'
                    deleteAction(url,{id:row}).then((res) => {
                        if (res.success) {
                            _this.loadData();
                            _this.$message.success("删除成功")
                        }
                        if(res.code===510){
                            _this.$message.warning(res.message)
                        }
                        _this.loading = false;
                    })
                },
                onCancel() {
                console.log('Cancel');
                },
            });
        },
        //权限设置
        handleAuthority(){
            this.visibleAuthority = true;
        },
        //权限设置数据展示
        loadDataAuthority(arg){
            if (arg === 1) {
                this.ipaginationAut.current = 1;
            }
            this.loading = true;
            getAction('',params).then((res) => {
                if (res.success) {
                    if(res.result){
                        this.dataSourceAut = res.result.records;
                        this.ipaginationAut.total = res.result.total;
                    }else{
                        this.dataSourceAut = [];
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
            this.loadDataAuthority(1);
        },
        //新增节点权限弹框
        addAut(){
            this.formAut = {y1:'',y2:'',y3:'',y4:'',y5:1,y6:1}
            this.visibleAddAut = true;
        },
        //新增节点权限确定
        handleOkAut(){
            this.loading = true;
            putAction('',this.formAut).then((res) => {
                if (res.success) {
                    this.$message.success("新增成功！")
                    this.visibleAddAut = false;
                }
                this.loading = false;
            })
        },
        //编辑节点权限设置
        handleAutEdit(row){
            this.formAut = Object.assign(this.formAut,row);
            this.visibleAddAut = true;
        },
        //删除节点权限设置
        handleAutDelete(row){
            let _this = this;
            this.$confirm({
                title: '删除',
                content: '确认删除此节点权限吗？',
                okText: '确定',
                okType: 'danger',
                cancelText: '取消',
                width: "400px !important",
                zIndex: 9999,
                onOk() {
                    _this.loading = true;
                    deleteAction('',{id:row}).then((res) => {
                        if (res.success) {
                            _this.loadData();
                            _this.$message.success("删除成功")
                        }
                        if(res.code===510){
                            _this.$message.warning(res.message)
                        }
                        _this.loading = false;
                    })
                },
                onCancel() {
                    console.log('Cancel');
                },
            });
        },
// --------------------------------流程节点END-------------------------
    }

}
</script>

<style lang="scss" scoped>
    .right-content{
        margin-top: 10px;
    }
    .distance{
        margin-right: 5px;
    }
</style>