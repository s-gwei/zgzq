<template>
    <a-card :bordered="false">
<!-- -------------------------查询区域---------------------------- -->
      <div class="table-page-search-wrapper">
          <a-form layout="inline" @keyup.enter.native="loadData(1)">      
              <a-row :gutter="24">
                  <a-col :md="8" :sm="12">
                  <a-form-item label="表达式名称">
                      <a-input v-model="queryParam.processName" allowClear @search="loadData(1)" placeholder="请输入"/>
                  </a-form-item>
                  </a-col>
                  <a-col :md="8" :sm="12">
                  <a-form-item label="表达式">
                      <a-input v-model="queryParam.processExpress" allowClear @search="loadData(1)" placeholder="请输入"/>
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
<!----------------------- 操作按钮区域 -------------------->
      <div class="table-operator" style="border-top: 5px">
        <a-button type="primary" @click="addProcess"><a-icon type="plus" />新增</a-button>
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
                bordered
                @change="handleTableChange">
                <span slot="action" slot-scope="text, record">
                    <a href="javascript:;" @click="handleEdit(record.id)">编辑</a>
                    <a href="javascript:;" @click="handleDelete(record.id)">删除</a>
                </span>
            </a-table>
        </div>
<!-- --------------------------新增-------------------------------- -->
        <a-modal 
            title="新增" 
            :visible="visibleAdd" 
            @ok="handleOk" 
            @cancel="visibleAdd=false" 
            :centered="true" 
            width="800px">
            <a-form-model ref="form" :model="form" :label-col="labelCol" :wrapper-col="wrapperCol">
                <a-form-model-item label="表达式名称">
                    <a-input v-model="form.processName" allowClear placeholder="请输入表达式名称"/>
                </a-form-model-item>
                <a-form-model-item label="表达式">
                    <a-input v-model="form.processExpress" placeholder="请输入表达式"/>
                </a-form-model-item>
            </a-form-model>
        </a-modal>    
<!-- ----------------------------------END-------------------------------------- -->
    </a-card>
</template>

<script>
import {getAction,putAction,postAction,deleteAction} from '@/api/manage';
import { filterObj } from '@/utils/util';
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
                    title: '表达式名称',
                    align: "center",
                    dataIndex: 'processDefinitionName',
                    ellipsis: true,
                },
                {
                    title: '表达式',
                    align: "center",
                    dataIndex: 'title',
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
            visibleAdd:false,//新增
            form: {
                processName:'',
                processExpress:'',
            },
            labelCol: { span: 6 },
            wrapperCol: { span: 14 },
            processTypeList:[],//Event属性列表
        }
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
            getAction('/workflow/runtime/instanceList',{}).then((res) => {
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
        //新增按钮
        addProcess(){
            this.form = {};
            this.visibleAdd = true;
        },
        //新增确定
        handleOk(){
            this.loading = true;
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
        },
        //编辑
        handleEdit(){
          this.form = Object.assign(this.form,row);
          this.visibleAdd = true;
        },
        //删除
        handleDelete(row){
            let _this = this;
            this.$confirm({
                title: '删除',
                content: '确认删除此流程监听吗？',
                okText: '确定',
                okType: 'danger',
                cancelText: '取消',
                width: "400px !important",
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
    }
}
</script>
