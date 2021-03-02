<template>
  <a-card :bordered="false">
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="48">
          <a-col :md="8" :sm="24">
            <a-form-item label="活动名称">
              <a-input v-model="queryParam.name" placeholder=""/>
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <span class="table-page-search-submitButtons">
              <a-button type="primary" icon="search" @click="searchQuery">查询</a-button>
              <a-button style="margin-left: 8px"  icon="reload" @click="resetSearchForm">重置</a-button>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>

    <div class="table-operator">
      <a-button type="primary" icon="plus" @click="() => this.handleModalVisible(true)">新建</a-button>
      <a-dropdown v-if="selectedRowKeys.length > 0">
        <a-menu slot="overlay">
          <a-menu-item key="1"  @click="handleBatchDelete" ><a-icon type="delete"/>删除</a-menu-item>
        </a-menu>
        <a-button style="margin-left: 8px">
          批量操作 <a-icon type="down" />
        </a-button>
      </a-dropdown>
    </div>

    <s-table
      ref="table"
      size="default"
      :columns="columns"
      :data="loadData"
      :showAlertInfo="true"
      @onSelect="onChange"
    >
      <span slot="action" slot-scope="text, record">
        <a @click="handleEdit(record, 'e')">编辑</a>
        <a-divider type="vertical" />
                <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
                  <a href="javascript:;">删除</a>
                </a-popconfirm>
        <a-divider type="vertical" />
        <a-dropdown>
          <a class="ant-dropdown-link">
            更多 <a-icon type="down" />
          </a>
          <a-menu slot="overlay">
            <a-menu-item>
              <a href="javascript:;" @click="handleEdit(record, 'd')">详情</a>
            </a-menu-item>
            <a-menu-item>
              <a href="javascript:;" @click="gotoRelation(record.id)">评价关系维护</a>
            </a-menu-item>
            <a-menu-item>
              <a href="javascript:;" @click="issued(record.id)">一键下发</a>
            </a-menu-item>
            <a-menu-item>
              <a href="javascript:;" @click="gotoTotal(record.id)">评价统计</a>
            </a-menu-item>
            <a-menu-item>
              <a href="javascript:;" @click="gotoStatus(record.id)">评价状态</a>
            </a-menu-item>
          </a-menu>
        </a-dropdown>
      </span>
    </s-table>

    <a-modal
      title="操作"
      :width="800"
      v-model="visible"
      :confirmLoading="confirmLoading"
      :okButtonProps="{ props: {disabled: disableSubmit} }"
      @ok="handleOk"
      @cancel="close"
    >
      <a-form :form="form">
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="活动名称"
          hasFeedback
        >
          <a-input placeholder="请输入" v-decorator="['name', { rules: [{ required: true, message: '请输入活动名称' }] }]"/>
        </a-form-item>

        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="描述"
          hasFeedback
          help="请填写一段描述"
        >
          <a-textarea :rows="5" v-decorator="['description', { rules: [{ required: true, message: '请输入活动描述！' }] }]" placeholder="..."/>
        </a-form-item>


      </a-form>
    </a-modal>


  </a-card>
</template>

<script>
  import STable from '@/components/table/'
  import moment from "moment"
  import pick from 'lodash.pick'
  import { getAction, httpAction, deleteAction } from '@/api/manage'


  export default {
    name: "questionActivity",
    components: {
      STable
    },
    data () {
      return {
        visible: false,
        disableSubmit: false,
        confirmLoading: false,
        labelCol: {
          xs: { span: 24 },
          sm: { span: 5 },
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 12 },
        },
        form: this.$form.createForm(this),

        // 查询参数
        queryParam: {},
        //暂存数据
        model: {icon: ''},
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
            title: '活动名称',
            dataIndex: 'name',
          },
          {
            title: '描述',
            dataIndex: 'description',
          },
          {
            title: '操作',
            dataIndex: 'action',
            width: '200px',
            scopedSlots: { customRender: 'action' },
          }
        ],
        // 加载数据方法 必须为 Promise 对象
        loadData: parameter => {
          return getAction(this.url.list, Object.assign(parameter, this.queryParam))
            .then(res => {
              if (res.success) {
                return {
                  data: res.result.records,
                  pageNo: res.result.current,
                  pageSize: res.result.size,
                  totalCount: res.result.total,
                  totalPage: res.result.pages
                }
              } else {
                this.$message.error(res.message)
              }

            })
        },

        selectedRowKeys: [],
        selectedRows: [],
        url: {
          add: '/investigation/container/add',
          edit: '/investigation/container/edit',
          list: '/investigation/container/list',
          delete: '/investigation/container/delete',
          deleteBatch: '/investigation/container/deleteList',
          issue: '/investigation/entity/sendMessage'
        }
      }
    },
    created () {
    },
    methods: {
      //搜索
      searchQuery () {
        this.$refs.table.refresh()
      },
      //添加逻辑
      handleModalVisible(isVisible) {
        this.visible = isVisible;
      },
      handleEdit (records, v) {
        if (v == 'e') {
          this.disableSubmit = false;
        } else if (v == 'd') {
          this.disableSubmit = true;
        } else {
          this.disableSubmit = false;
        }
        this.model = records
        console.log(this.model)
        this.visible = true
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model, 'name', 'description'))
        })
      },
      handleOk () {
        this.form.validateFields((err, values) => {
          if (!err) {
            let reg = /^[\u4E00-\u9FA5A-Za-z]+[\u4E00-\u9FA5A-Za-z0-9]+$/;
            if (reg.test(values.name) === false) {
              //分类名称不能输入特殊字符
              this.$message.warning('名称不能以数字开头或带有特殊符号！')
              return;
            }
            this.confirmLoading = true;
            let httpurl = '';
            let method = '';
            if (!this.model.id) {
              httpurl += this.url.add;
              method = 'post';
            } else {
              httpurl += this.url.edit;
              method = 'put';
            }
            let formData = Object.assign(this.model, values);
            httpAction(httpurl, formData, method).then((res) => {
              if (res.success) {
                this.$message.success(res.message);
              } else {
                this.$message.error(res.message);
              }
            }).finally(() => {
              this.confirmLoading = false;
              this.$refs.table.refresh()
              this.close()
            })
          }
        });
      },
      gotoTotal (id) {
        this.$router.push({path: '/questionnaire/questionnaireStatistics', query: {id}})
      },
      // 删除
      handleDelete (id) {
        deleteAction(this.url.delete, {containerId: id}).then((res) => {
          if (res.success) {
            this.$message.success(res.message);
            this.$refs.table.refresh()
            this.$refs.table.onClearSelected()
          } else {
            this.$message.error(res.message);
          }
        })
      },
      // 批量删除
      handleBatchDelete () {
        let params = {
          containerIds: ''
        }
        this.selectedRows.forEach((v) => {
          params.containerIds = params.containerIds + v.id + ','
        })
        deleteAction(this.url.deleteBatch, params).then((res) => {
          if (res.success) {
            this.$message.success(res.message);
            this.$refs.table.refresh()
            this.$refs.table.onClearSelected()
          } else {
            this.$message.error(res.message);
          }
        })
      },
      gotoRelation (id) {
        this.$router.push({path: '/questionnaire/evaluateRelation', query: {id}})
      },
      gotoStatus (id) {
        this.$router.push({path: '/questionnaire/evaluateStatus', query: {id}})
      },
      // 下发
      issued (id) {
        getAction(this.url.issue, {id}).then((res) => {
          if (res.success) {
            this.$message.success(res.message);
          } else {
            this.$message.error(res.message);
          }
        })
      },
      close () {
        this.visible = false
        this.form.resetFields();
      },
      onChange (row) {
        this.selectedRowKeys = row.selectedRowKeys
        this.selectedRows = row.selectedRows

        console.log(this.$refs.table)
      },
      resetSearchForm () {
        this.queryParam = {
        }
        this.$refs.table.refresh()
      }
    },
    watch: {
    }
  }
</script>