<template>
  <a-modal
    :title="title"
    :width="800"
    :visible="visible"
    :confirmLoading="confirmLoading"
    :okButtonProps="{ props: {disabled: disableSubmit} }"
    @ok="handleOk"
    @cancel="handleCancel"
    okText="确定"
    cancelText="关闭">

    <!-- 编辑 -->
      <a-form :form="form">
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="被评价人姓名"
          hasFeedback>
          <a-input :disabled="true" v-decorator="['assessorRealname', {}]"/>
        </a-form-item>

        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="评价人姓名"
          hasFeedback>
          <j-select-user-by-dep v-model="evaluatorRealname" @change="changeRealName" :multi="false"></j-select-user-by-dep>
        </a-form-item>
        <a-form-item
          label="说明"
          hasFeedback
          help="请填写一段说明"
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
        >
          <a-textarea :rows="5" v-decorator="['description', {rules: [{ required: false, message: '请填写一段说明!' }]}]"   placeholder="..." id="description"/>
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="评价关系"
          hasFeedback>
          <a-select v-decorator="['relationship', {rules: [{ required: true, message: '请选择评价关系!' }]}]" placeholder="请选择评价关系">
            <a-select-option value="individual">Self-assessment(个人)</a-select-option>
            <a-select-option value="peer">Workmate-assessment(同行)</a-select-option>
            <a-select-option value="pm">Pm-assessment(项目经理)</a-select-option>
            <a-select-option value="director">Director-assessment(总监)</a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="权重"
          hasFeedback>
          <a-input  :disabled="true" v-decorator="['qweight', {initialValue: 0}]"/>
        </a-form-item>

      </a-form>
  </a-modal>
</template>

<script>
  import JSelectUserByDep from '@/components/jeecgbiz/JSelectUserByDep'
  import {httpAction, getAction} from '@/api/manage'
  import pick from 'lodash.pick'
  import Vue from 'vue'

  export default {
    name: "addRelationModel",
    data() {
      return {
        title: "操作",
        visible: false,
        model: {},
        labelCol: {
          xs: {span: 24},
          sm: {span: 5},
        },
        wrapperCol: {
          xs: {span: 24},
          sm: {span: 16},
        },
        disableSubmit: false,
        selectedRowKeys: [],
        rId: "",
        confirmLoading: false,
        form: this.$form.createForm(this),
        evaluatorRealname: '',  //  评价人名字
        users: [],
        url: {
          add: "/investigation/relationship/add",
          edit: "/investigation/relationship/edit",
          detail: '/investigation/relationship/getById'
        }
      }
    },
    components: {
      JSelectUserByDep
    },
    computed: {
    },
    created() {
    },
    methods: {
      add(id, name) {
        console.log(id, name)
        if (id) {
          this.edit({assessorUserId: id, assessorRealname: name}, '');
        } else {
          this.$message.warning("请选择一个用户信息");
        }
      },
      detail(record) {
        this.edit(record, 'd');
      },
      edit(record, v) {
        console.log(record)
        if (v == 'e') {
          this.disableSubmit = false;
        } else if (v == 'd') {
          this.disableSubmit = true;
        } else {
          this.disableSubmit = false;
        }
        this.form.resetFields();
        this.rId = record.id;
        this.model = record
        // model 为true
        this.visible = true;
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model, 'assessorRealname'))
        });
        if (record.id) {
          this.model = record;
          this.evaluatorRealname = this.model.evaluatorUsername
          this.$nextTick(() => {
            this.form.setFieldsValue(pick(this.model, 'assessorRealname', 'description', 'relationship', 'qweight'))
          });
        }
      },
      close() {
        this.$emit('close');
        this.evaluatorRealname = ''
        this.visible = false;
      },
      handleOk() {
        const that = this;
        if (!this.evaluatorRealname) {
          this.$message.warning("请选择评价人！");
          return
        }
        // 触发表单验证
        this.form.validateFields((err, values) => {
          if (!err) {
            that.confirmLoading = true;
            let httpurl = '';
            let method = '';
            if (!this.model.id) {
              httpurl += this.url.add;
              method = 'post';
            } else {
              httpurl += this.url.edit;
              method = 'put';
            }
            let formData = Object.assign(that.model, values);
            formData.evaluatorUserId = that.users[0].id
            formData.evaluatorRealname = that.users[0].realname
            formData.evaluatorUsername = that.users[0].username
            console.log(formData);
            httpAction(httpurl, formData, method).then((res) => {
              if (res.success) {
                that.$message.success(res.message);
              } else {
                that.$message.warning(res.message);
              }
            }).finally(() => {
              that.confirmLoading = false;
              that.close();
            })
          }
        })
      },
      handleCancel() {
        this.close();
      },
      changeRealName (id, userS) {
        this.users = userS
      }
    }
  }
</script>

<style scoped>
  /* tile uploaded pictures */
  .upload-list-inline > > > .ant-upload-list-item {
    float: left;
    width: 200px;
    margin-right: 8px;
  }

  .upload-list-inline > > > .ant-upload-animate-enter {
    animation-name: uploadAnimateInlineIn;
  }

  .upload-list-inline > > > .ant-upload-animate-leave {
    animation-name: uploadAnimateInlineOut;
  }
</style>