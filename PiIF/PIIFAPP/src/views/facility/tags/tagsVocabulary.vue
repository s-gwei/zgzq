<template>
  <div>
    <!--tab头-->
    <a-row :gutter="48" type="flex"  style="border-bottom: 1px solid #f1f1f1;padding-bottom: 10px;margin: 10px 0 10px 0;">
      <a-col :style="{ borderRight: '1px solid #f1f1f1'}">
        <div style=" font-size: 18px;color:#128DFF;line-height: 40px;">字段定义</div>
      </a-col>
      <a-col>
        <a-radio-group style="margin-top: 4px;">
          <a-radio-button value="plus" @click="openModel"><a-icon type="plus" />&nbsp;&nbsp;&nbsp;添加</a-radio-button>
          <a-popconfirm title="确定删除吗?" @confirm="() => handleDelBatch()">
            <a-radio-button value="delete"><a-icon type="delete" />&nbsp;&nbsp;&nbsp;删除</a-radio-button>
          </a-popconfirm>
        </a-radio-group>
      </a-col>
    </a-row>

    <div>
      <s-table
        ref="table"
        size="default"
        :columns="columns"
        :data="loadData"
        :showAlertInfo="true"
        @onSelect="onChange"
      >
        <span slot="action" slot-scope="text, record">
          <a-popconfirm title="确定删除吗?" @confirm="() => handleDel(record)">
            <a>删除</a>
          </a-popconfirm>
      </span>
      </s-table>
    </div>
    <a-modal
      title="操作"
      :visible="visible"
      @ok="handleSubmit"
      :confirmLoading="confirmLoading"
      @cancel="handleCancel"
    >
      <a-form :form="form" :label-col="{ span: 5 }" :wrapper-col="{ span: 12 }">
        <a-form-item label="词汇名称">
          <a-input
            placeholder="请输入"
            v-decorator="['vocabularyName', { rules: [{ required: true, message: '请输入词汇名称' }] }]"
          />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script>
  import moment from 'moment'
  import STable from '@/components/table/'
  import {getAction, postAction, deleteAction, putAction} from '@/api/manage'

  export default {
    name: 'vocabulary',
    components: {STable},
    data() {
      return {
        loading: false,
        ModelInfo: {},
        visible: false,
        confirmLoading: false,
        tagId: '',
        form:  this.$form.createForm(this),
        columns: [
          {
            title: '词汇',
            dataIndex: 'vocabulary',
            width: '80%'
          },
          {
            width:'10%',
            title: '操作',
            dataIndex: 'action',
            scopedSlots: { customRender: 'action' },
          },
        ],
        loadData: (parameter) => {
          if (!this.url.list) {
            this.$message.error('请设置url.list属性!')
            return
          }
          let params = {
            tagId: this.tagId,
          }
          return getAction(this.url.list, Object.assign(params, this.queryParam, parameter)).then((res) => {
            if (res.success) {
              return {
                data: res.result,
//                pageNo: res.result.current,
//                pageSize: res.result.size,
//                totalCount: res.result.total,
//                totalPage: res.result.pages
              }
            }
          })
        },

        url: {
          list: '/iot/tag/getVocabularyByTagId',
          add:'/iot/tag/addVocabulary',
          delete: '/iot/tag/deleteVocabulary',
          deleteBatch: '/iot/tag/deleteVocabularyList',
        }
      }
    },
    created() {
      this.ModelInfo = JSON.parse(sessionStorage.getItem('tagsInfo'))
      this.tagId = this.ModelInfo.id
    },
    mounted () {
    },
    methods: {
      openModel () {
        this.visible = true
      },
      /**tags**/
      handleSubmit (e) {
        e.preventDefault();
        this.confirmLoading = true
        this.form.validateFields((err, values) => {
          if (!err) {
            let reg = /^[\u4E00-\u9FA5A-Za-z]+[\u4E00-\u9FA5A-Za-z0-9]+$/;
            if (reg.test(values.vocabularyName) === false) {
              //名称不能输入特殊字符
              this.$message.warning('名称不能以数字开头或带有特殊符号！')
              this.confirmLoading = false
              return;
            }
            postAction(this.url.add, {tagId: this.tagId, vocabulary: values.vocabularyName}).then(
              (res) => {
                if (res.success) {
                  this.$message.success('添加成功')
                  this.visible = false
                  this.confirmLoading = false
                  this.$refs.table.refresh()
                } else {
                  this.$message.error(res.message)
                }
              }
            )
          }
        });
      },
      handleCancel () {
        this.visible = false
      },
      handleDel (record) {
        if (!this.url.delete) {
          this.$message.error('请设置url.delete属性!')
          return
        }
        deleteAction(this.url.delete, {id: record.id}).then(
          (res) => {
            if (res.success) {
              this.$message.success('删除成功')
              this.$refs.table.refresh()
            } else {
              this.$message.error(res.message)
            }
          }
        )
      },
      handleDelBatch () {
        if (this.selectedRowKeys.length === 0) {
          this.$message.warning('您未选中词汇');
        } else if (this.selectedRowKeys.length >= 1) {
          let params = {
            ids: ''
          }
          console.log(this.selectedRows)
          this.selectedRows.forEach((v) => {
            params.ids = params.ids + v.id + ','
          })
          deleteAction(this.url.deleteBatch, params).then((res) => {
            if (res.success) {
              this.$message.success('批量删除成功');
              this.$refs.table.refresh()
              this.$refs.table.onClearSelected()
            } else {
              this.$message.error(res.message);
            }
          })
        }
      },
      onChange (row) {
        this.selectedRowKeys = row.selectedRowKeys
        this.selectedRows = row.selectedRows
      }
    }
  }
</script>

