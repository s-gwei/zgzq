<template>
  <a-drawer
    title="配置筛选项"
    :width="700"
    @close="onClose"
    :visible="modelVisible"
    :wrapStyle="{height: 'calc(100% - 108px)',overflow: 'auto',paddingBottom: '108px'}"
  >
    <a-form @submit="handleSubmit" :form="form2">
      <a-form-item
        label="过滤项"
        :labelCol="labelCol"
        :wrapperCol="wrapperCol">
        <a-select
          mode="multiple"
          placeholder="请选择过滤条件"
          :value="selectedItems"
          @change="handleChange"
          @select = "selectChange"
        >
          <a-select-option v-for="item in filteredOptions" :key="item" :value="item">
            {{ item }}
          </a-select-option>
        </a-select>
      </a-form-item>
    </a-form>

    <div>
      <j-editable-table
        ref="editableTable"
        :loading="loading"
        :columns="columns"
        :dataSource="filterData"
        :rowNumber="true"
        :rowSelection="true"
        :actionButton="false"
        :dragSort="true"
        :dragSortAdd="false"
        :maxHeight="300"
        :disabled="false"
        style="margin-top: 8px;">
        <template v-slot:action="props">
          <a @click="deleteFilterData(props)">{{ props.text }}</a>
        </template>

      </j-editable-table>
    </div>

    <div class="drawer-bootom-button" v-show="!formDisabled">
      <a-popconfirm title="确定放弃编辑？" @confirm="handleCancel" okText="确定" cancelText="取消">
        <a-button style="margin-right: .8rem">取消</a-button>
      </a-popconfirm>
      <a-button @click="handleSubmit" type="primary" :loading="confirmLoading">提交</a-button>
    </div>
  </a-drawer>
</template>

<script>

  import JEditableTable from '@/components/jeecg/JEditableTable'
  import { FormTypes } from '@/utils/JEditableTableUtil'
  import moment from 'moment'
  export default {
    name: "FilterModel",
    components: {
      JEditableTable
    },
    data () {
      return {
        loading: false,
        labelCol: {
          xs: { span: 24 },
          sm: { span: 5 },
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 },
        },
        columns: [
          {
            title: '字段名',
            key: 'name',
            width: '18%',
            defaultValue: '',
            placeholder: '请输入${title}',
          },
          {
            title: '类型',
            key: 'type',
            width: '80px'
          },
          {
            title: '条件',
            key: 'condition',
            width: '150px',
            type: FormTypes.select,
            options: [ // 下拉选项
              { title: '字符串', value: 'String' },
              { title: '数值', value: 'Number' },
              { title: '对象', value: 'Object' }
            ],
            allowInput: true,
            defaultValue: '',
            placeholder: '请选择${title}',
            validateRules: [{ required: true, message: '请选择${title}' }]
          },
          {
            title: '值',
            key: 'value',
            type: FormTypes.input,
            width: '100px',
          },
          {
            title: '操作',
            key: 'action',
            width: '100px',
            type: FormTypes.slot,
            slotName: 'action',
            defaultValue: '删除'
          }
        ],

        selectCol:[],

        filterData:[],

        tableData:[],
        selectedItems: [],
        OPTIONS:['Apples', 'Nails', 'Bananas', 'Helicopters'],
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
    props:["msg"],
    computed:{
      filteredOptions() {
        this.selectCol = this.msg
        if((this.selectCol.result !== undefined)&&(this.selectCol.result !== null)){
          let cols = this.selectCol.result.columns
          let options = [];
          if(cols !== undefined){
            for(let i=0; i<cols.length; i++){
              options.push(cols[i].name)
            }
          }
          return options.filter(o => !this.selectedItems.includes(o));
        }
      },
    },
    mounted(){

    },
    methods: {
      showDrawer() {
        this.modelVisible = true;
      },
      onClose() {
        this.modelVisible = false;
        this.reset()
      },
      handleChange(selectedItems) {
        this.selectedItems = selectedItems;
      },

      selectChange(select){
        let columns = this.msg.result.columns
        for (let i = 0;i<columns.length;i++){
          if(columns[i].name === select){
            if((columns[i].type === 'STRING')||(columns[i].type === 'JSON')){
              this.columns[2] = {
                title: '条件',
                key: 'condition',
                width: '150px',
                type: FormTypes.select,
                options: [ // 下拉选项
                  { title: '开头是', value: 'begin_is' },
                  { title: '包含', value: 'contain' },
                  { title: '末尾是', value: 'end_is' },
                  { title: '正好是', value: 'right_is' }
                ],
                allowInput: true,
                defaultValue: '',
                placeholder: '请选择${title}',
                validateRules: [{ required: true, message: '请选择${title}' }]
              }
              this.tableData.push({
                name:columns[i].name,
                type:columns[i].type,
                condition:'begin_is',
                value:'',
              })
            }else if(columns[i].type === 'DOUBLE'){
              this.columns[2] = {
                title: '条件',
                key: 'condition',
                width: '150px',
                type: FormTypes.select,
                options: [ // 下拉选项
                  { title: '等于', value: 'equal_to' },
                  { title: '不等于', value: 'unequal_to' },
                  { title: '大于', value: 'greater' },
                  { title: '大于等于', value: 'equal_greater' },
                  { title: '小于等于', value: 'less_equal' }
                ],
                allowInput: true,
                defaultValue: '',
                placeholder: '请选择${title}',
                validateRules: [{ required: true, message: '请选择${title}' }]
              }
              this.tableData.push({
                name:columns[i].name,
                type:columns[i].type,
                condition:'equal_to',
                value:'',
              })
            }
          }
        }
        this.filterData = this.tableData
      },

      deleteFilterData(record){
        this.filterData.splice(record.index,1)
        this.selectedItems.splice(record.index,1)
      },
      // handler
      handleSubmit (e) {
        e.preventDefault()
        // this.form2.validateFields((err, values) => {
        //   console.log(values)
        //   if (!err) {
        //     this.$emit('modelMethods',{type: this.type, data: Object.assign(this.form, values)})
        //     console.log('成功验证')
        //   }
        // })
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