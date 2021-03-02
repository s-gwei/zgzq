<template>
  <a-drawer
    title="新属性"
    :width="700"
    @close="onClose"
    :visible="modelVisible"
  >
    <a-form @submit="handleSubmit" :form="form">
      <a-form-item
        label="事件名称"
        :labelCol="labelCol"
        :wrapperCol="wrapperCol">
        <a-input
          v-decorator="[
          'name',{ rules: [{ required: true, message: '请输入名称', whitespace: true }] },
          ]"
          placeholder="请输入"/>
      </a-form-item>

    </a-form>
    <div class="drawer-bootom-button">
      <a-popconfirm title="确定放弃编辑？" @confirm="handleCancel" okText="确定" cancelText="取消">
        <a-button style="margin-right: .8rem">取消</a-button>
      </a-popconfirm>
      <a-button @click="handleSubmit" type="primary" :loading="confirmLoading">提交</a-button>
    </div>
  </a-drawer>
</template>

<script>
  import moment from 'moment'

  export default {
    name: 'eventModel',
    data() {
      return {
        modelVisible: this.value,
        confirmLoading: false,
        labelCol: {
          xs: { span: 24 },
          sm: { span: 5 }
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 }
        },
        form: {},
        url: {
          add: '',
          search: ''
        }
      }
    },
    props: {
      value: {
        type: Boolean,
        default: false
      }
    },
    watch: {
      value: function(val) {
        this.modelVisible = val
      },
      modelVisible: function(val) {
        this.$emit('input', val)
      }
    },
    computed: {},
    methods: {
      onClose() {
        this.modelVisible = false
      },
      showDrawer() {
        this.modelVisible = true
      },
      // 取消
      handleCancel() {
        this.modelVisible = false
      },
      // 提交
      handleSubmit() {
      },
      reset() {
        this.form.setFieldsValue({ name: '' })
      }
    }
  }
</script>

