<template>
  <a-drawer
    title="新字段"
    :width="700"
    @close="onClose"
    :visible="modelVisible"
    :wrapStyle="{height: 'calc(100% - 108px)',overflow: 'auto',paddingBottom: '108px'}"
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

<!--      <a-form-item-->
<!--        label="类别"-->
<!--        :labelCol="labelCol"-->
<!--        :wrapperCol="wrapperCol"-->
<!--      >-->
<!--        <a-input-->
<!--          v-model="form.category"-->
<!--          :disabled="formDisabled"-->
<!--          placeholder="请输入" />-->
<!--      </a-form-item>-->

      <a-form-item
        label="基本类型"
        hasFeedback
        :labelCol="labelCol"
        :wrapperCol="wrapperCol"
      >
        <a-select defaultValue="STRING"
                  v-model="form.baseType"
                  :disabled="baseTypeDisabled"
                  @change = "getBaseType"
                  name="classIfy"
        >
          <a-select-option value="INTEGER">INTEGER(123)</a-select-option>
          <a-select-option value="NUMBER">NUMBER</a-select-option>
          <a-select-option value="BOOLEAN">BOOLEAN</a-select-option>
          <a-select-option value="STRING">STRING</a-select-option>
        </a-select>
      </a-form-item>

      <a-form-item
        label="单位"
        :labelCol="labelCol"
        :wrapperCol="wrapperCol"
        v-show="showUnit"
      >
        <a-input
          v-model="form.aspects.unit"
          :disabled="formDisabled"
          placeholder="请输入" />
      </a-form-item>

      <a-form-item
        label="最小值"
        :labelCol="labelCol"
        :wrapperCol="wrapperCol"
        v-show="showUnit"
      >
        <a-input
          v-model="form.aspects.minValue"
          :disabled="formDisabled"
          placeholder="请输入" />
      </a-form-item>

      <a-form-item
        label="最大值"
        :labelCol="labelCol"
        :wrapperCol="wrapperCol"
        v-show="showUnit"
      >
        <a-input
          v-model="form.aspects.maxValue"
          :disabled="formDisabled"
          placeholder="请输入" />
      </a-form-item>

      <a-form-item
        label="具有默认值"
        validateStatus="success"
        :labelCol="labelCol"
        :wrapperCol="wrapperCol"
      >
        <a-checkbox
          v-model="form.aspects.isLocalOnly"
          :disabled="formDisabled"
        ></a-checkbox>
      </a-form-item>

      <a-form-item
        label="数据值"
        :labelCol="labelCol"
        :wrapperCol="wrapperCol"
        v-show="form.aspects.isLocalOnly"
      >
        <a-input
          v-model="form.aspects.value"
          :disabled="formDisabled"
          placeholder="请输入" />
      </a-form-item>

<!--      <a-form-item-->
<!--        label="重置"-->
<!--        validateStatus="success"-->
<!--        :labelCol="labelCol"-->
<!--        :wrapperCol="wrapperCol"-->
<!--      >-->
<!--        <a-checkbox-->
<!--          v-model="form.aspects.isLogged"-->
<!--          :disabled="formDisabled"-->
<!--        ></a-checkbox>-->
<!--      </a-form-item>-->


      <a-form-item
        label="是否是主键"
        validateStatus="success"
        :labelCol="labelCol"
        :wrapperCol="wrapperCol"
      >
        <a-checkbox
          v-model="form.aspects.isReadOnly"
          :disabled="formDisabled"
        ></a-checkbox>
      </a-form-item>

      <a-form-item
        label="友好名称"
        hasFeedback
        :labelCol="labelCol"
        :wrapperCol="wrapperCol"
      >
        <a-input
          v-model="form.aspects.friendlyName"
          :disabled="formDisabled"
          placeholder="请输入" />
      </a-form-item>

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
            minValue:'',
            maxValue:'',
            unit:'',
            value:'',
            friendlyName:'',
            isLogged: false,
            isReadOnly: false,
            isPersistent: false
          },
          baseType:'STRING',
        },
        form2:this.$form.createForm(this),
        isModel: null,
        type: null,
        formDisabled: false,
        baseTypeDisabled:false,
        inputNameDisabled: false,
        disableSubmit:false,
        confirmLoading: false,
        modelVisible: false,
        propertyList: [],
        showUnit:false,
        showDefaultValue:false
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
          aspects: {
            dataChangeType: '',
              isLogged: false,
              isReadOnly: false,
              isPersistent: false
          }
        }
      },

      //获取选择的基本类型
      getBaseType(){
        let baseType = this.form.baseType;
        if((baseType === 'NUMBER')||(baseType === 'INTEGER')){
          this.showUnit = true
        }else{
          this.showUnit = false
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