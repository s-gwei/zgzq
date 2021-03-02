<template>
  <a-drawer
    title="新属性"
    :width="700"
    @close="onClose"
    :visible="modelVisible"
  >
    <a-form @submit="handleSubmit" :form="form2">
      <a-form-item
        label="名称"
        :labelCol="labelCol"
        :wrapperCol="wrapperCol">
        <a-input
          v-decorator="[
          'name',
          {
            rules: [{ required: true, message: '请输入名称', whitespace: true }],
          },
          ]"
          :disabled="inputNameDisabled"
          placeholder="请输入" />
      </a-form-item>
      <a-form-item
        label="描述"
        :labelCol="labelCol"
        :wrapperCol="wrapperCol">
        <a-textarea
          v-model="form.description"
          :disabled="formDisabled"
          rows="4"
          placeholder="请输入"
        />
      </a-form-item>

      <a-form-item
        label="类别"
        :labelCol="labelCol"
        :wrapperCol="wrapperCol"
      >
        <a-input
          v-model="form.category"
          :disabled="formDisabled"
          placeholder="请输入" />
      </a-form-item>

      <a-form-item
        label="基本类型"
        hasFeedback
        :labelCol="labelCol"
        :wrapperCol="wrapperCol"
      >
        <a-select
                  v-model="form.baseType"
                  :disabled="formDisabled"
                  name="classIfy"
        >
          <a-select-option value="INTEGER">INTEGER（123）</a-select-option>
          <a-select-option value="NUMBER">NUMBER</a-select-option>
          <a-select-option value="BOOLEAN">BOOLEAN</a-select-option>
          <a-select-option value="STRING">STRING</a-select-option>
        </a-select>
      </a-form-item>

      <a-form-item
        label="具有默认值"
        validateStatus="success"
        :labelCol="labelCol"
        :wrapperCol="wrapperCol"
      >
        <a-checkbox
          v-model="form.defaultValue"
          :disabled="formDisabled"
        ></a-checkbox>
      </a-form-item>

      <a-form-item
        label="数据值"
        :labelCol="labelCol"
        :wrapperCol="wrapperCol"
        v-show="form.defaultValue"
      >
        <a-input
          v-model="form.value"
          :disabled="formDisabled"
          placeholder="请输入" />
      </a-form-item>

      <a-form-item
        label="重置"
        validateStatus="success"
        :labelCol="labelCol"
        :wrapperCol="wrapperCol"
        v-show="isModel !== 'obj'"
      >
        <a-checkbox
          v-model="form.aspects.isLogged"
          :disabled="formDisabled"
        ></a-checkbox>
      </a-form-item>


      <a-form-item
        label="只读"
        validateStatus="success"
        :labelCol="labelCol"
        :wrapperCol="wrapperCol"
        v-show="isModel !== 'obj'"
      >
        <a-checkbox
          v-model="form.aspects.isReadOnly"
          :disabled="formDisabled"
        ></a-checkbox>
      </a-form-item>

      <a-form-item
        label="值变化记录"
        validateStatus="success"
        :labelCol="labelCol"
        :wrapperCol="wrapperCol"
        v-show="isModel !== 'obj'"
      >
        <a-checkbox
          v-model="form.aspects.isPersistent"
          :disabled="formDisabled"
        ></a-checkbox>
      </a-form-item>

      <a-form-item
        label="数据更改类型"
        hasFeedback
        :labelCol="labelCol"
        :wrapperCol="wrapperCol"
      >
        <a-select
          v-model="form.aspects.dataChangeType"
          :disabled="formDisabled"
          placeholder="请输入" >
          <a-select-option :value="item.value" v-for="item in changeTypeData">{{item.name}}</a-select-option>
        </a-select>
      </a-form-item>

      <div v-show="isModel !== 'obj'" style="height: 320px;">
        <p style="font-size: 16px;">属性绑定</p>
        <a-divider />
        <a-form-item
          label="属性"
          hasFeedback
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
        >
          <a-select v-model="form.routeColumn"
                    :disabled="formDisabled"
          >
            <a-select-option :value="item" v-for="item in propertyList">{{item}}</a-select-option>
          </a-select>
        </a-form-item>
      </div>
    </a-form>
    <div class="drawer-bootom-button" v-show="!formDisabled">
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
    name: "PropertyModel",
    components: {

    },
    data () {
      return {
        labelCol: {
          xs: { span: 24 },
          sm: { span: 5 },
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 },
        },
        form: {
          aspects: {
            dataChangeType: '',
            isLogged: false,
            isReadOnly: false,
            isPersistent: false
          }
        },
        changeTypeData: [
          {name: '总是', value: 'Always'},
          {name: '绝不', value: 'Never'},
          {name: '关', value: 'Off'},
          {name: '开', value: 'On'},
          {name: '值', value: 'Value'},
        ],
        form2:this.$form.createForm(this),
        isModel: null,
        type: null,
        formDisabled: false,
        inputNameDisabled: false,
        disableSubmit:false,
        confirmLoading: false,
        modelVisible: false,
        propertyList: []
      }
    },
    computed:{

    },
    methods: {
      showDrawer() {
        this.modelVisible = true;
      },
      onClose() {
        this.modelVisible = false;
        this.reset()
      },
      // handler
      handleSubmit (e) {
        e.preventDefault()
        this.form2.validateFields((err, values) => {
          console.log(values)
          if (!err) {
            let reg = /^[\u4E00-\u9FA5A-Za-z]+[\u4E00-\u9FA5A-Za-z0-9]+$/;
            if (reg.test(values.name) === false) {
              //分类名称不能输入特殊字符
              this.$message.warning('名称不能以数字开头或带有特殊符号！')
              return;
            }
            console.log(this.form)
//            baseType value
            if (this.form.baseType && this.form.defaultValue) {
              switch (this.form.baseType) {
                case 'NUMBER':
                  let reg = /^[0-9]*$/;
                  if (reg.test(this.form.value) === false) {
                    //分类名称不能输入特殊字符
                    this.$message.warning('该值应为数字类型！')
                    return;
                  }
                  break;
                case 'BOOLEAN':
                  if (this.form.value === 'true' || this.form.value === 'false') {

                  } else {
                    this.$message.warning('该值应为布尔类型！')
                    return;
                  }
                  break;
              }
            }
            if (this.form.routeColumn) {
              this.form.isLocalOnly = false
            }
            this.$emit('modelMethods',{type: this.type, data: Object.assign(this.form, values)})
            console.log('成功验证')
          }
        })
      },
      handleCancel () {
        this.onClose()
      },
      reset () {
        this.form2.setFieldsValue({name: ''})
        this.form = {
          isLocalOnly: false,
          aspects: {
            dataChangeType: '',
              isLogged: false,
              isReadOnly: false,
              isPersistent: false
          }
        }
      }
    }
  }
</script>

<style scoped>
  .avatar-uploader > .ant-upload {
    width:104px;
    height:104px;
  }
  .ant-upload-select-picture-card i {
    font-size: 49px;
    color: #999;
  }

  .ant-upload-select-picture-card .ant-upload-text {
    margin-top: 8px;
    color: #666;
  }

  .ant-table-tbody .ant-table-row td{
    padding-top:10px;
    padding-bottom:10px;
  }

  .drawer-bootom-button {
    position: absolute;
    bottom: -8px;
    width: 100%;
    border-top: 1px solid #e8e8e8;
    padding: 10px 16px;
    text-align: right;
    left: 0;
    background: #fff;
    border-radius: 0 0 2px 2px;
  }
</style>