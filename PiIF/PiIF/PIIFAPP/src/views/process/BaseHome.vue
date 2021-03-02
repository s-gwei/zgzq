<template>
  <a-card :bordered="false">
<!-- -------------------------查询区域---------------------------- -->
      <div class="table-page-search-wrapper">
        <a-form layout="inline" @keyup.enter.native="loadData(1)">      
          <a-row :gutter="24">
            <a-col :md="6" :sm="12">
              <a-form-item label="流程名称">
                <a-input v-model="queryParam.processName" allowClear @search="loadData(1)" placeholder="输入流程名称"/>
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="12">
              <a-form-item label="流程编码">
                <a-input v-model="queryParam.processKey" allowClear @search="loadData(1)" placeholder="输入流程编码"/>
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="12">
              <a-form-item label="流程类型">
                <a-select placeholder="请选择" v-model="queryParam.processType">
                    <a-select-option allowClear v-for="item in processTypeList" :key="item.id" :value="item.id">
                      {{ item.name }}
                    </a-select-option>
                </a-select>
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
        <a-button type="primary" @click="addProcess"><a-icon type="plus" />新建流程</a-button>
        <a-drawer title="流程设计" placement="right" :visible="isShow" @close="isShow=false;" :mask="false" :width="width">
          <vue-bpmn v-if="isShow" @loadData="loadData" :xml="xml" :isDetail="isDetail" :processInfo="processInfo" :processId="processId"/>
        </a-drawer>
      </div>

<!------------------------数据展示区------------------------->
      <div>
        <a-table
          ref="table"
          size="middle"
          rowKey="id"
          :columns="columns"
          :dataSource="dataSource"
          :pagination="ipagination"
          :loading="loading"
          bordered
          @change="handleTableChange">
           <template slot="processStatusSolt" slot-scope="text, record, index">
              <span v-if="record.processStatus">已发布</span>
              <span v-else>未发布</span>
            </template>

            <span slot="action" slot-scope="text, record">
              <a @click="handleSet(record)">设计流程</a>
              <a-divider type="vertical"/>
              <a @click="handleSend(record.id)">发布</a>
              <a-divider type="vertical"/>
              <a-dropdown>
                <a class="ant-dropdown-link">更多 <a-icon type="down"/></a>
                <a-menu slot="overlay">
                  <a-menu-item v-if="record.processStatus">
                    <a href="javascript:;" @click="handleSetting(record)">流程配置</a>
                  </a-menu-item>
                  <a-menu-item v-if="record.processStatus">
                    <a href="javascript:;" @click="handleWatching(record)">版本监控</a>
                  </a-menu-item>
                  <a-menu-item>
                    <a-upload
                      name="file"
                      accept=".xml"
                      :action="uploadAction"
                      :headers="headers"
                      :data="{'id':record.id}"
                      :fileList="fileList"
                      :beforeUpload="beforeUpload"
                      :showUploadList="false"
                      @change="handleChange">
                      <a href="javascript:;" style="color:#636363;">上传流程</a>
                    </a-upload>
                  </a-menu-item>
                  <a-menu-item>
                    <a href="javascript:;" @click="handleDeleteProcess(record.id)">删除流程</a>
                  </a-menu-item>
                  <!-- <a-menu-item>
                    <a href="javascript:;" @click="iconBtn">表单图标</a>
                  </a-menu-item> -->

                </a-menu>
              </a-dropdown>
            </span>

        </a-table>
      </div>
<!------------------------ 表单图标弹框 ----------------------->
    <a-modal title="选择" :visible="visible" @ok="handleOk" @cancel="visible=false" :width="700">
       <a-form layout="inline" labelAlign="right">
          <a-form-item v-bind="formItemLayout" label="表单图标(PC):" style="margin-bottom:10px;">
            <a-input placeholder="点击右侧按钮选择图标" v-model="formIcon.pc" readOnly style="width:400px">
              <a-icon slot="addonAfter" type="setting" @click="selectIcons('pc')" />
            </a-input>
          </a-form-item>
          <a-form-item v-bind="formItemLayout" label="表单图标(APP):">
            <a-input placeholder="点击右侧按钮选择图标" v-model="formIcon.icon" readOnly style="width:400px">
              <a-icon slot="addonAfter" type="setting" @click="selectIcons('app')" />
            </a-input>
          </a-form-item>
       </a-form>
    </a-modal>

<!-------------------- 选择图标 ------------------------------------->
    <icons @choose="handleIconChoose" @close="iconChooseVisible = false" :iconChooseVisible="iconChooseVisible"></icons>

<!-----------------------流程配置弹框--------------------------------->
    <div ref="processSetting">
      <a-modal 
        :title="processTitle" 
        :visible="visibleSetting" 
        @cancel="detailId = '';visibleSetting=false;" 
        :width="width-100" 
        :centered="true" 
        :footer="null"
        :getContainer="()=>this.$refs.processSetting">
        <process-Setting  v-if="visibleSetting" :detailId="detailId"></process-Setting>
      </a-modal>
    </div>

<!-- -------------------------版本监控弹框------------------------------- -->
    <div ref="processWatching">
      <a-modal 
        title="版本监控" 
        :visible="visibleWatching" 
        @cancel="detailId = '';visibleWatching=false;" 
        :width="width-100" 
        :centered="true" 
        :footer="null"
        :getContainer="()=>this.$refs.processWatching">
          <a-table
              ref="tableWatching"
              size="middle"
              bordered
              rowKey="id"
              :columns="columnsWatching"
              :dataSource="dataSourceWatching"
              :pagination="ipaginationWatching"
              :loading="loading">
                  <span slot="status" slot-scope="text, record">
                      <span v-if="record.state">挂起</span>
                      <span v-else>激活</span>
                  </span>
                  <span slot="actionWatching" slot-scope="text, record">
                      <a @click="downLoanVersionSvg(record)">流程图</a>
                      <a-divider v-if="record.state"  type="vertical"/>
                      <a v-if="record.state" @click="handleActivate(record.id)">激活</a>
                      <a-divider v-if="!record.state" type="vertical"/>
                      <a v-if="!record.state" @click="handleHangUp(record.id)">挂起</a>
                      <a-divider type="vertical"/>
                      <!-- <a class="distance">流程节点</a> -->
                      <a @click="handleDeleteWatch(record.id)">删除</a>
                      <a-divider type="vertical"/>
                      <a @click="downLoanVersionXml(record)">下载</a>
                  </span>
          </a-table>
      </a-modal>
    </div>

    <div class="canvas" ref="canvas" v-show="false"></div>

<!-------------------------END----------------------------------------->

  </a-card>
    
</template>

<script>
import Vue from 'vue'
import { ACCESS_TOKEN } from "@/store/mutation-types"
import VueBpmn from "@/components/VueBpmn";
import Icons from '../system/modules/icon/Icons'
import ProcessSetting from "./processSetting"
import {getAction,putAction,postAction,deleteAction} from '@/api/manage';
import { filterObj } from '@/utils/util';
const FILE_TYPE_ALL = "all"
const FILE_TYPE_IMG = "image"
const FILE_TYPE_TXT = "file"


  // bpmn-js 设计器
  import BpmnModeler from "bpmn-js/lib/Modeler";
  // 对activiti的扩展
  import activitiExtensionModule from 'activiti-bpmn-moddle/lib';
  import activitiModdle from "activiti-bpmn-moddle/resources/activiti";
  import VueAceEditor from 'vue2-ace-editor'



export default {
  components: {
    VueBpmn,
    Icons,
    ProcessSetting
  },
  data(){
    return{
      isShow:false, //是否展示设计面板
      width:document.body.offsetWidth,//动态获取屏幕宽度
      xml:'',//初始化xml
      processId:'',//流程id
      processInfo:{},//详情信息
      isDetail:false, //是否是详情页面
      svg:'',
      bpmnModeler:null,
      //查询条件
      queryParam:{
        processName:'',
        processKey:'',
        processType:''
      },
      processTypeList:[{name:"测试流程",id:'test'},{name:"OA办公",id:'OA'},{name:"业务办理",id:'yw'}],//流程类型列表
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
          dataIndex: 'processName',
          ellipsis: true,
        },
        {
          title: '流程编码',
          align: "center",
          dataIndex: 'processKey',
          ellipsis: true,
        },
        {
          title: '流程类型',
          align: "center",
          width: 100,
          dataIndex: 'processType'
        },
        {
          title: '发布状态',
          align: "center",
          width: 100,
          dataIndex: 'processStatus',
          scopedSlots: {customRender: "processStatusSolt"}
        },
        {
          title: '创建时间',
          align: "center",
          width: 100,
          dataIndex: 'createTime',
          // sorter: true,
          ellipsis: true,
        },
        {
          title: '操作',
          dataIndex: 'action',
          scopedSlots: {customRender: 'action'},
          align: "center",
          width: 200
        }
      ],
      isorter: {
        column: 'createTime',
        order: 'desc'
      },
      dataSource:[],
      loading:false, //加载状态
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
      //文件上传
      fileList:[],
      uploadAction:window._CONFIG['domianURL']+"/workflow/design/process/upload",
      headers:{},
      //表单图标
      formIcon:{
        pc:'',
        app:''
      },
      formItemLayout: {
        labelCol: {
          xs: { span: 24 },
          sm: { span: 8 },
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 },
        },
      },
      iconChooseVisible:false,
      visible:false,
      whatModel:'',
      //流程配置
      processTitle:'',//title
      visibleSetting:false,
      detailId:'',//详情id
      //版本监控
      visibleWatching:false,
      columnsWatching:[
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
            title: '名称',
            align: "center",
            dataIndex: 'name',
            ellipsis: true,
        },
        {
            title: '流程KEY',
            align: "center",
            dataIndex: 'key',
            ellipsis: true,
        },{
            title: '版本',
            align: "center",
            dataIndex: 'version',
            ellipsis: true,
            // width: 100
        },{
            title: '状态',
            align: "center",
            dataIndex: 'status',
            scopedSlots: {customRender: 'status'},
            ellipsis: true,
            // width: 100
        },{
            title: '操作',
            dataIndex: 'actionWatching',
            scopedSlots: {customRender: 'actionWatching'},
            align: "center",
            width: 200
        }
      ],
      dataSourceWatching:[],
      ipaginationWatching:{
        current: 1,
        pageSize: 10,
        pageSizeOptions: ['10', '20', '30'],
        showTotal: (total, range) => {
          return range[0] + "-" + range[1] + " 共" + total + "条"
        },
        showQuickJumper: true,
        showSizeChanger: true,
        total: 0,
      },
      xmlName:'',

    }
  },
  created(){
    const token = Vue.ls.get(ACCESS_TOKEN);
    this.headers = {"X-Access-Token":token}
  },
  mounted(){
    this.loadData(1)
  },
  methods: {
    //新建流程
    addProcess(){
      this.xml = `<?xml version="1.0" encoding="UTF-8"?><bpmn2:definitions xmlns:bpmn2="http://www.omg.org/spec/BPMN/20100524/MODEL" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI" targetNamespace="http://bpmn.io/schema/bpmn" xsi:schemaLocation="http://www.omg.org/spec/BPMN/20100524/MODEL BPMN20.xsd"><bpmn2:process id="process1567044459787" name="流程1567044459787"><bpmn2:documentation>描述</bpmn2:documentation></bpmn2:process><bpmndi:BPMNDiagram id="BPMNDiagram_1"><bpmndi:BPMNPlane id="BPMNPlane_1" bpmnElement="process1567044459787" /></bpmndi:BPMNDiagram></bpmn2:definitions>`
      this.isDetail = false;
      this.processId = this.$md5(new Date().getTime().toString())
      this.isShow = true;
    },
    //重置按钮
    searchReset(){
      this.queryParam = {}
      this.loadData(1);
    },
    //获取查询条件
    getQueryParams() {
      let param = Object.assign(this.queryParam);
      param.pageNo = this.ipagination.current;
      param.pageSize = this.ipagination.pageSize;
      param.isorter = this.isorter
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
      getAction('/workflow/design/process/query',params).then((res) => {
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
    //流程设计 查看流程
    handleSet(row){
      this.xml = row.processXml
      this.processInfo = row
      this.isDetail = true;
      this.isShow = true;
    },
    //流程发布
    handleSend(row){
      this.loading = true;
      putAction(`/workflow/design/process/deploy?id=${row}`).then((res) => {
        if (res.success) {
          this.loadData();
          this.detailId ? this.visibleSetting = true : this.$message.success("发布成功")
        }
        if(res.code===510){
          this.$message.warning(res.message)
        }
        this.loading = false;
      })
    },
    //流程配置
    handleSetting(row){
      this.processTitle = `${row.processName}配置`
      this.detailId = row.id
      this.handleSend(row.id)
    },
    //版本监控---数据加载
    watchData(val){
      this.loading = true;
      let params = {
        pageNo:this.ipaginationWatching.current,
        pageSize:this.ipaginationWatching.pageSize,
        processId:val
      };
      getAction('/workflow/design/processDef/version',params).then((res) => {
          if (res.success) {
            if(res.result){
              this.dataSourceWatching = res.result.records;
              this.ipaginationWatching.total = res.result.total;
              this.visibleWatching = true;
            }else{
              this.dataSourceWatching = [];
            }
          }
          if(res.code===510){
          this.$message.warning(res.message)
          }
          this.loading = false;
      })
    },
    //点击版本监控
    handleWatching(row){
      this.detailId = row.id
      this.watchData(row.id)
    },
    //流程删除
    handleDeleteProcess(row){
      let _this = this;
      this.$confirm({
        title: '删除',
        content: '确认删除此流程吗？',
        okText: '确定',
        okType: 'danger',
        cancelText: '取消',
        width: "400px !important",
        onOk() {
          _this.loading = true;
          deleteAction('/workflow/design/process/delete',{id:row}).then((res) => {
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
    //版本监控---挂起
    handleHangUp(id){
      this.loading = true;
      getAction(`/workflow/design/processDef/state?processDefId=${id}`).then((res) => {
          if (res.success) {
              this.$message.success("操作成功！")
              this.watchData(this.detailId);
          }
          if(res.code===510){
          this.$message.warning(res.message)
          }
          this.loading = false;
      })
    },
    //版本监控---激活
    handleActivate(id){
      this.loading = true;
      getAction(`/workflow/design/processDef/active?processDefId=${id}`).then((res) => {
          if (res.success) {
              this.$message.success("操作成功！")
              this.watchData(this.detailId);
          }
          if(res.code===510){
          this.$message.warning(res.message)
          }
          this.loading = false;
      })
    },
    //版本监控---删除
    handleDeleteWatch(row){
      let _this = this;
      this.$confirm({
          title: '删除',
          content: '确认删除此流程版本吗？',
          okText: '确定',
          okType: 'danger',
          cancelText: '取消',
          width: "400px !important",
          onOk() {
              _this.loading = true;
              deleteAction('/workflow/design/processDef/delete',{processDefId:row}).then((res) => {
                  if (res.success) {
                      _this.loadData();
                      _this.$message.success("删除成功")
                      _this.watchData(_this.detailId);
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
    //版本监控---下载xml
    downLoanVersionXml(row){
      this.loading = true;
      this.xmlName = row.name
      getAction(`/workflow/design/processDef/processXml?processDefId=${row.id}`).then((res) => {
          if (res.success) {
              let {filename, href} = this.setEncoded('XML', res.result);
              if (href && filename) {
                let a = document.createElement('a');
                a.download = filename; //指定下载的文件名
                a.href = href; //  URL对象
                a.click(); // 模拟点击
                URL.revokeObjectURL(a.href); // 释放URL 对象
              }
              this.loading = false;
          }
          if(res.code===510){
            this.$message.warning(res.message)
          }
          this.loading = false;
      })
    },
    //版本监控--下载svg图片
    downLoanVersionSvg(row){
      this.loading = true;
      this.xmlName = row.name
      getAction(`/workflow/design/processDef/processXml?processDefId=${row.id}`).then((res) => {
          if (res.success) {
            this.bpmnModeler = new BpmnModeler({
              container: this.$refs.canvas,
              additionalModules: [
                activitiExtensionModule
              ],
              moddleExtensions: {
                activiti: activitiModdle
              }
            });
            this.bpmnModeler.importXML(res.result, err => {
                const _this = this;
                this.bpmnModeler.saveSVG(function (err, svg) {
                  let {filename, href} = _this.setEncoded('SVG', svg);
                  if (href && filename) {
                    let a = document.createElement('a');
                    a.download = filename;
                    a.href = href;
                    a.click();
                    URL.revokeObjectURL(a.href);
                  }
                });
            });
            this.loading = false;
          }
          if(res.code===510){
            this.$message.warning(res.message)
          }
          this.loading = false;
      })
    },
    // 分页、排序、筛选变化时触发
    handleTableChange(pagination, filters, sorter) {
      if (Object.keys(sorter).length > 0) {
        this.isorter.column = sorter.field;
        this.isorter.order = "ascend" == sorter.order ? "asc" : "desc"
      }
      this.ipagination = pagination;
      this.loadData();
    },
    //表单选择是否出现
    iconBtn(){
      this.formIcon.pc = '';
      this.formIcon.app = '';
      this.visible = true
    },
    //点击选择图标
    selectIcons(val){
      this.whatModel = val;
      this.iconChooseVisible = true
    },
    //选择图标并赋值
    handleIconChoose (value) {
      if(this.whatModel == 'pc'){
        this.formIcon.pc = value
      }else{
        this.formIcon.app = value
      }
      this.iconChooseVisible = false
    },
    //确定选择的图标接口
    handleOk(){
        this.loading = true;
        let params = {};//查询条件
        getAction('',params).then((res) => {
          if (res.success) {
            this.$message.success("操作成功！")
          }
          if(res.code===510){
            this.$message.warning(res.message)
          }
          this.loading = false;
        })
    },
    //上传文件
    handleChange(info) {
      console.log("--文件列表改变--")
      let fileList = info.fileList
      if(info.file.status==='done'){
        if(info.file.response.success){
          fileList = fileList.map((file) => {
            if (file.response) {
              file.url = this.urlDownload+file.response.message;
            }
            return file;
          });
        }
        this.$message.success(`${info.file.name} 上传成功!`);
        this.loadData();
      }else if (info.file.status === 'error') {
        this.$message.error(`${info.file.name} 上传失败.`);
      }else if(info.file.status === 'removed'){
        this.handleDelete(info.file)
      }
      this.fileList = fileList
      if(info.file.status==='done' || info.file.status === 'removed'){
        this.handlePathChange()
      }
    },
    handleDelete(file){
      console.log(file)
    },
    handlePathChange(){
      let uploadFiles = this.fileList
      let path = ''
      if(!uploadFiles || uploadFiles.length==0){
        path = ''
      }
      let arr = [];

      for(var a=0;a<uploadFiles.length;a++){
        arr.push(uploadFiles[a].response.message)
      }
      if(arr.length>0){
        path = arr.join(",")
      }
      this.$emit('change', path);
    },
    beforeUpload(row,file){
      var fileType = file.type;
      if(fileType===FILE_TYPE_IMG){
        if(fileType.indexOf('image')<0){
          this.$message.warning('请上传图片');
          return false;
        }
      }else if(fileType===FILE_TYPE_TXT){
        if(fileType.indexOf('image')>=0){
          this.$message.warning('请上传文件');
          return false;
        }
      }
      //TODO 扩展功能验证文件大小
      return true
    },
    // 下载xml svg图片
    setEncoded(type, data) {
      const encodedData = encodeURIComponent(data);
      if (data) {
        if (type === 'XML') {
          return {
            filename: this.xmlName+'.bpmn20.xml',
            href: "data:application/bpmn20-xml;charset=UTF-8," + encodedData,
            data: data
          }
        }
        if (type === 'SVG') {
          return {
            filename: this.xmlName+'.svg',
            href: "data:application/text/xml;charset=UTF-8," + encodedData,
            data: data
          }
        }
      }
    },
  }
}
</script>

<style lang="scss" scoped>
   @import '~@assets/less/common.less';

  .ant-drawer-content-wrapper{
    width: 1000px !important;
  }
  .right-content{
    margin-top: 10px;
  }
  .distance{
    margin-right: 5px;
  }
</style>
