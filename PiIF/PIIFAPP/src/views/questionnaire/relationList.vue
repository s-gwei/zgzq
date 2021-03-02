<template>
  <a-card :bordered="false">

    <!-- 操作按钮区域 -->
    <div class="table-operator" :md="24" :sm="24" style="margin: -25px 0px 10px 0px">
      <a-button @click="handleAdd" type="primary" icon="plus" style="margin-right: 8px;">新增</a-button>
      <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl" @change="handleImportExcel">
        <a-button type="primary" icon="import">导入</a-button>
      </a-upload>
      <a-button type="primary" icon="download" @click="uploadFile('files/excel/template.xlsx')" style="margin-left: 8px;">关系模板下载</a-button>
      <a-dropdown v-if="selectedRowKeys.length > 0">
        <a-menu slot="overlay">
          <a-menu-item key="1" @click="batchDel">
            <a-icon type="delete"/>
            删除
          </a-menu-item>
        </a-menu>
        <a-button style="margin-left: 8px"> 批量操作
          <a-icon type="down"/>
        </a-button>
      </a-dropdown>
    </div>

    <!-- table区域-begin -->
    <div>
      <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
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
        :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
        @change="handleTableChange">

        <template slot="relationship" slot-scope="text, record">
          <a v-if="record.relationship == 'pm'" @click="openEditModel(record)">{{text}}</a>
          <span v-else>{{text}}</span>
        </template>

        <span slot="action" slot-scope="text, record">
          <a @click="handleEdit(record)">编辑</a>
          <a-divider type="vertical"/>
          <a-dropdown>
            <a class="ant-dropdown-link">
              更多 <a-icon type="down"/>
            </a>
            <a-menu slot="overlay">
              <a-menu-item>
                <a href="javascript:;" @click="handleDetail(record)">详情</a>
              </a-menu-item>
              <a-menu-item>
                <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
                  <a>删除</a>
                </a-popconfirm>
              </a-menu-item>
            </a-menu>
          </a-dropdown>
        </span>

      </a-table>
    </div>
    <!-- table区域-end -->
    <!-- 表单区域 -->
    <addRelationModel ref="modalForm" @ok="modalFormOk" @close="close"></addRelationModel>
    <EditRelationModel ref="EditRelationModel" @refresh="loadData(1)"></EditRelationModel>
  </a-card>
</template>

<script>
  import Vue from 'vue'
  import { ACCESS_TOKEN } from "@/store/mutation-types"

  import addRelationModel from './form/addRelationModel'
  import EditRelationModel from './form/editRelationModel'
  import {JeecgListMixin} from '@/mixins/JeecgListMixin'
  import {getAction, deleteAction, downFile} from '@/api/manage'

  export default {
    name: "relationList",
    mixins: [JeecgListMixin],
    components: {
      addRelationModel,
      EditRelationModel
    },
    data() {
      return {
        tokenHeader: {'X-Access-Token': Vue.ls.get(ACCESS_TOKEN)},
        description: '评价关系列表',
        // 表头
        columns: [
          {
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
            title: '评价类型',
            align: "center",
            width: 200,
            dataIndex: 'relationship',
            customRender: function (text) {
              if (text == 'individual') {
                return "Self-assessment(个人)";
              } else if (text == 'peer') {
                return "Workmate-assessment(同行)";
              } else if (text == 'pm') {
                return "Pm-assessment(项目经理)";
              } else {
                return "Director-assessment(总监)";
              }
            },
          },
            {
                title: '被评价人',
                align: "center",
                dataIndex: 'assessorRealname',
            },
          {
            title: '评价人',
            align: "center",
            dataIndex: 'evaluatorRealname',
          },
          {
            title: '评价权重',
            align: "center",
            dataIndex: 'qweight',
            scopedSlots: {customRender: 'relationship'},
          },
            {
                title: '说明',
                dataIndex: 'description',
                align: "center",
            },
          {
            title: '操作',
            key: 'operation',
            align: 'center',
            width: 130,
            scopedSlots: {customRender: 'action'},
          },
        ],
        pmList: [],
        containerId: '',
        url: {
          list: "/investigation/relationship/list",
          delete: "/investigation/relationship/delete",
          deleteBatch: "/investigation/relationship/deleteByList",
          editBatch: '/investigation/relationship/editQWeight',
          importExcelUrl: '/investigation/relationship/importExcel',
          exportXlsUrl: '/investigation/relationship/exportXls'
        }
      }
    },
    computed: {
      importExcelUrl: function(){
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}?id=${this.containerId}`;
//        return `http://192.168.1.24/jeecg-boot${this.url.importExcelUrl}?id=${this.containerId}`;
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
        //update-begin--Author:kangxiaolin  Date:20190905 for：[442]主子表分开维护，生成的代码子表的分页改为真实的分页--------------------
        var params = this.getQueryParams();
        if (params.assessorUserId) {
          getAction(this.url.list, {assessorUserId: params.assessorUserId, pageNo : this.ipagination.current,
            pageSize :this.ipagination.pageSize}).then((res) => {
            if (res.success) {
              this.dataSource = res.result.records;
              this.ipagination.total = res.result.total;
            } else {
              this.dataSource = null;
            }
          })
        }
      },
      getOrderMain(orderId, name) {
        this.queryParam.assessorUserId = orderId;
        this.queryParam.assessorName = name;
        this.loadData(1);
      },
      handleAdd: function () {
        this.$refs.modalForm.add(this.queryParam.assessorUserId, this.queryParam.assessorName);
        this.$refs.modalForm.title = "添加评价关系";
      },
      handleDelete: function (id) {
        if(!this.url.delete){
          this.$message.error("请设置url.delete属性!")
          return
        }
        var that = this;
        deleteAction(that.url.delete, {relationshipId: id}).then((res) => {
          if (res.success) {
            that.$message.success(res.message);
            that.loadData();
          } else {
            that.$message.warning(res.message);
          }
        });
      },
      batchDel: function () {
        if(!this.url.deleteBatch){
          this.$message.error("请设置url.deleteBatch属性!")
          return
        }
        if (this.selectedRowKeys.length <= 0) {
          this.$message.warning('请选择一条记录！');
          return;
        } else {
          var ids = "";
          for (var a = 0; a < this.selectedRowKeys.length; a++) {
            ids += this.selectedRowKeys[a] + ",";
          }
          var that = this;
          this.$confirm({
            title: "确认删除",
            content: "是否删除选中数据?",
            onOk: function () {
              that.loading = true;
              deleteAction(that.url.deleteBatch, {relationshipIds: ids}).then((res) => {
                if (res.success) {
                  that.$message.success(res.message);
                  that.loadData();
                  that.onClearSelected();
                } else {
                  that.$message.warning(res.message);
                }
              }).finally(() => {
                that.loading = false;
              });
            }
          });
        }
      },
      openEditModel (record) {
        this.pmList = []
        this.dataSource.forEach((v) => {
          if (v.relationship == 'pm') {
            this.pmList.push(v)
          }
        })
        this.$refs.EditRelationModel.edit(this.pmList)
      },
      close () {
        this.loadData(1);
      },
      /** 导入 **/
      handleImportExcel(info){
        if (info.file.status !== 'uploading') {
          console.log(info.file, info.fileList);
        }
        if (info.file.status === 'done') {
          if (info.file.response.success) {
            // this.$message.success(`${info.file.name} 文件上传成功`);
            if (info.file.response.code === 201) {
              let { message, result: { msg, fileUrl, fileName } } = info.file.response
              let href = window._CONFIG['domianURL'] + fileUrl
              this.$warning({
                  title: message,
                  content: (
                  <div>
                  <span>{msg}</span><br/>
                  <span>具体详情请 <a href={href} target="_blank" download={fileName}>点击下载</a> </span>
                </div>
              )
            })
            } else {
              this.$message.success(info.file.response.message || `${info.file.name} 文件上传成功`)
            }
            this.loadData(1)
          } else {
            this.$message.error(`${info.file.name} ${info.file.response.message}.`);
          }
        } else if (info.file.status === 'error') {
          this.$message.error(`文件上传失败: ${info.file.msg} `);
        }
      },
      /** 模板下载 **/
      uploadFile(text){
        if(!text){
          this.$message.warning("未知的文件")
          return;
        }
        if(text.indexOf(",")>0){
          text = text.substring(0,text.indexOf(","))
        }
        window.open(window._CONFIG['domianURL'] + "/sys/common/download/"+text);
      }
    }
  }
</script>
<style scoped>
  .ant-card {
    margin-left: -30px;
    margin-right: -30px;
  }
</style>