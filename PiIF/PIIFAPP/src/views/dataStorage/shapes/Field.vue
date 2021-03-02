<template>
  <div>

    <!--tab头-->
    <a-row :gutter="48" type="flex"  style="border-bottom: 1px solid #f1f1f1;padding-bottom: 10px;margin: 10px 0 10px 0;">
      <a-col :style="{ borderRight: '1px solid #f1f1f1'}">
        <div style=" font-size: 18px;color:#128DFF;line-height: 40px;">字段定义</div>
      </a-col>
      <a-col>
        <a-radio-group style="margin-top: 4px;">
          <a-radio-button value="plus" @click="handleModel(1)"><a-icon type="plus" />&nbsp;&nbsp;&nbsp;新建</a-radio-button>
          <a-radio-button value="file" @click="handleModel(2)"><a-icon type="file-search" />&nbsp;&nbsp;&nbsp;查看</a-radio-button>
          <a-radio-button value="edit" @click="handleModel(5)"><a-icon type="edit" />&nbsp;&nbsp;&nbsp;编辑</a-radio-button>
          <a-radio-button value="copy" @click="handleModel(4)"><a-icon type="copy" />&nbsp;&nbsp;&nbsp;复制</a-radio-button>
          <a-popconfirm title="确定删除吗?" @confirm="() => handleDel()">
            <a-radio-button value="delete"><a-icon type="delete" />&nbsp;&nbsp;&nbsp;删除</a-radio-button>
          </a-popconfirm>
<!--          <a-radio-button value="lock" @click="handlepermission"><a-icon type="lock" />&nbsp;&nbsp;&nbsp;权限</a-radio-button>-->
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
        <template slot="name" slot-scope="text,record">
          <a @click="handleModel(6, record)">{{text}}</a>
        </template>
        <span slot="action" slot-scope="text, record">
          <a @click="handleModel(3, record)">编辑</a>
        <a-divider type="vertical" />
        <a-dropdown>
          <a class="ant-dropdown-link">
            更多 <a-icon type="down" />
          </a>
          <a-menu slot="overlay">
            <a-menu-item>
              <a  @click="handleModel(6, record)">查看</a>
            </a-menu-item>
            <a-menu-item>
              <a-popconfirm title="确定删除吗?" @confirm="() => handleDel(record)">
                  <a>删除</a>
              </a-popconfirm>
            </a-menu-item>
          </a-menu>
        </a-dropdown>
      </span>
      </s-table>
    </div>


<!--    <a-modal-->
<!--      title="管理警报"-->
<!--      :visible="visible"-->
<!--      :width="1000"-->
<!--      @ok="handleOk"-->
<!--      :confirmLoading="confirmLoading"-->
<!--      @cancel="handleCancel"-->
<!--    >-->
<!--      <j-editable-table-->
<!--        :columns="columnsAlarm"-->
<!--        :dataSource="AlarmData"-->
<!--        :rowNumber="true"-->
<!--        :rowSelection="true"-->
<!--        :maxHeight="300"-->
<!--        :actionButton="true"-->
<!--      >-->
<!--        <template v-slot:action="props">-->
<!--          <a >{{ props.text }}</a>-->
<!--        </template>-->
<!--      </j-editable-table>-->
<!--    </a-modal>-->

    <FieldModel ref="PropertyModel" :type="type" @modelMethods="modelMethods"></FieldModel>
  </div>

</template>

<script>
  import moment from 'moment'
  import { FormTypes } from '@/utils/JEditableTableUtil'
  import STable from '@/components/table/'
  import JEditableTable from '@/components/jeecg/JEditableTable'
  import FieldModel from './FieldModel.vue'
  import {getAction, postAction, deleteAction, putAction} from '@/api/manage'

  export default {
    name: 'Property',
    components: { JEditableTable,STable, FieldModel },
    data() {
      return {
        loading: false,
        confirmLoading: false,
        dataShapeInfo: {},
        count: 2,
        columns: [
          {
            title: '名称',
            dataIndex: 'name',
            width: '30%',
            scopedSlots: { customRender: 'name' },
          },
          {
            title: '描述',
            dataIndex: 'description',
          },
          {
            title: '类型',
            dataIndex: 'baseType'
          },
          {
            title: '默认值',
            dataIndex: 'aspects.value',
          },
          {
            title: '操作',
            dataIndex: 'action',
            scopedSlots: { customRender: 'action' },
          },
        ],
        loadData: (parameter) => {
          if (!this.url.fieldList) {
            this.$message.error('请设置url.list属性!')
            return
          }
          let params = {
            "tableName": this.dataShapeName,
          }
          return getAction(this.url.fieldList, Object.assign(params, this.queryParam, parameter)).then((res) => {
            if (res.success) {
              if(res.result === null){
                return {
                  data: [],
                  pageNo: 1,
                  pageSize: 10,
                  totalCount: 0,
                  totalPage:0
                }
              }
              return {
                data: res.result.columnMsg,
                pageNo: res.result.current,
                pageSize: res.result.size,
                totalCount: res.result.total,
                totalPage: res.result.pages
              }
            }
          })
        },
        propertyData: [],
        productData: [],
        AlarmData: [],
        selectedRowIds: [],
        visible: false,
        userName: '',
        type: '',
        dataShapeName: '',
        fieldName:'',
        identifier: '',   /** 识别码**/
        url: {
          addField:'/tableMessage/addTableColumn',
          editField:'/tableMessage/updateTableColumn',
          fieldList: '/tableMessage/findTableAndColumnByTableName',
          Del: '/tableMessage/deleteTableColumn',
        },
        selectedRowKeys: [],
        selectedRows: []
      }
    },
    created() {
      this.dataShapeInfo = JSON.parse(sessionStorage.getItem('dataShapeInfo'))
      this.userName = this.$store.state.user.info.username
      this.dataShapeName = this.dataShapeInfo.name
      this.type = this.dataShapeInfo.type
      this.identifier = sessionStorage.getItem('identifier')
    },
    mounted () {
      this.$refs.PropertyModel.isModel = this.dataShapeInfo.type
    },
    methods: {
      /**属性**/
      handleModel (num, data) {
        switch (num) {
          case 1:   //新建
            this.$refs.PropertyModel.modelVisible = true
            this.$refs.PropertyModel.formDisabled = false
            this.$refs.PropertyModel.inputNameDisabled = false
            this.$refs.PropertyModel.baseTypeDisabled = false
            this.$refs.PropertyModel.showUnit = false
            this.$refs.PropertyModel.type = 1
            break;
          case 2:   //查看
            if (this.selectedRowKeys.length === 0) {this.$message.warning('未选中字段'); return}
            this.$refs.PropertyModel.modelVisible = true
            this.$refs.PropertyModel.formDisabled = true
            this.$refs.PropertyModel.baseTypeDisabled = true
            this.$refs.PropertyModel.inputNameDisabled = true
            if((this.selectedRows[0].baseType === 'NUMBER')||(this.selectedRows[0].baseType === 'INTEGER')||(this.selectedRows[0].baseType === 'DOUBLE')){
              this.$refs.PropertyModel.showUnit = true
            }else{
              this.$refs.PropertyModel.showUnit = false
            }
            this.$refs.PropertyModel.type = 2
            this.$refs.PropertyModel.form = this.selectedRows[0]
            this.$nextTick(() => {
              this.$refs.PropertyModel.form2.setFieldsValue({name: this.selectedRows[0].name})
            })
            break;
          case 6:   //查看
            this.$refs.PropertyModel.modelVisible = true
            this.$refs.PropertyModel.formDisabled = true
            this.$refs.PropertyModel.baseTypeDisabled = true
            this.$refs.PropertyModel.inputNameDisabled = true
            if((data.baseType === 'NUMBER')||(data.baseType === 'INTEGER')||(data.baseType === 'DOUBLE')){
              this.$refs.PropertyModel.showUnit = true
            }else{
              this.$refs.PropertyModel.showUnit = false
            }
            this.$refs.PropertyModel.type = 2
            this.$refs.PropertyModel.form = data
            this.$nextTick(() => {
              this.$refs.PropertyModel.form2.setFieldsValue({name: data.name})
            })
            break;
          case 3:   //编辑
            this.$refs.PropertyModel.modelVisible = true
            this.$refs.PropertyModel.formDisabled = false
            this.$refs.PropertyModel.baseTypeDisabled = true
            this.$refs.PropertyModel.inputNameDisabled = false
            if((data.baseType === 'NUMBER')||(data.baseType === 'INTEGER')||(data.baseType === 'DOUBLE')){
              this.$refs.PropertyModel.showUnit = true
            }else{
              this.$refs.PropertyModel.showUnit = false
            }
            this.$refs.PropertyModel.type = 3
            this.fieldName = data.name;
            this.$refs.PropertyModel.form = data
            this.$nextTick(() => {
              this.$refs.PropertyModel.form2.setFieldsValue({name:data.name})
            })
            break;
          case 5:   //编辑
            if (this.selectedRowKeys.length === 0) {this.$message.warning('未选中字段'); return}
            this.$refs.PropertyModel.modelVisible = true
            this.$refs.PropertyModel.formDisabled = false
            this.$refs.PropertyModel.inputNameDisabled = false
            this.$refs.PropertyModel.baseTypeDisabled = true
            if((this.selectedRows[0].baseType === 'NUMBER')||(this.selectedRows[0].baseType === 'INTEGER')||(this.selectedRows[0].baseType === 'DOUBLE')){
              this.$refs.PropertyModel.showUnit = true
            }else{
              this.$refs.PropertyModel.showUnit = false
            }
            this.$refs.PropertyModel.type = 3
            this.$refs.PropertyModel.form = this.selectedRows[0]
            this.$nextTick(() => {
              this.$refs.PropertyModel.form2.setFieldsValue({name: this.selectedRows[0].name})
            })
            break;
          case 4:   //复制
            if (this.selectedRowKeys.length === 0) {this.$message.warning('未选中字段'); return}
            this.$refs.PropertyModel.modelVisible = true
            this.$refs.PropertyModel.formDisabled = true
            this.$refs.PropertyModel.baseTypeDisabled = true
            this.$refs.PropertyModel.inputNameDisabled = false
            if((this.selectedRows[0].baseType === 'NUMBER')||(this.selectedRows[0].baseType === 'INTEGER')||(this.selectedRows[0].baseType === 'DOUBLE')){
              this.$refs.PropertyModel.showUnit = true
            }else{
              this.$refs.PropertyModel.showUnit = false
            }
            this.$refs.PropertyModel.type = 4
            this.$refs.PropertyModel.form = this.selectedRows[0]
            this.$nextTick(() => {
              this.$refs.PropertyModel.form2.setFieldsValue({name: this.selectedRows[0].name})
            })
            break;
        }
      },
      modelMethods (params) {
        if (params.type === 1) {   // 添加
          this.handleAdd(params.data)
        } else if (params.type === 2) {  // 查看
          this.handleLook(params.data)
        } else if (params.type === 3) {   //  编辑
          this.handleEdit(params.data)
        } else if (params.type === 4) { //  复制
          this.handleAdd(params.data)
        }
      },

      handleAdd (data) {
        let params = {
          "user":this.userName,
          "tableName":this.dataShapeName,
          "column":{
            "name":data.name,
            "aspects":data.aspects,
            "description":data.description,
            "baseType":data.baseType
          }
        }
        if(this.dataShapeName === undefined){
          this.$message.error('请先保存数据形状!');
          return
        }
        postAction(this.url.addField,params)
          .then((res) => {
            if (res.success) {
              this.$message.success(res.message);
              this.$refs.table.refresh()
              this.$refs.PropertyModel.modelVisible = false
              this.$refs.PropertyModel.reset()
            } else {
              this.$message.error(res.message);
            }
        })
      },
      handleLook (data) {
        this.$refs.PropertyModel.modelVisible = true
        this.$refs.PropertyModel.formDisabled = true
        this.$refs.PropertyModel.inputNameDisabled = true
        this.$refs.PropertyModel.type = 2
        this.$refs.PropertyModel.form = this.productData[data.index]
        this.$nextTick(() => {
          this.$refs.PropertyModel.form2.setFieldsValue({name: this.productData[data.index].name})
        })
      },
      handleEdit (data) {
        let params = {
          "user":this.userName,
          "tableName":this.dataShapeName,
          "updateColumn":this.fieldName,
          "column":{
            "name":data.name,
            "aspects":data.aspects,
            "description":data.description,
            "baseType":data.baseType
          }
        }
        postAction(this.url.editField, params)
          .then((res) => {
            if (res.success) {
              this.$message.success(res.message);
              this.$refs.table.refresh()
              this.$refs.PropertyModel.modelVisible = false
              this.$refs.PropertyModel.reset()
            } else {
              this.$message.error(res.message);
            }
          })
      },
      handleDel(data) {
        let columnName;
        if(data !== undefined){
          columnName = data.name;
        }else{
          if (this.selectedRowKeys.length === 0) {
            this.$message.warning('您未选中字段');
          } else if (this.selectedRowKeys.length >= 1) {
            columnName = this.selectedRows[0].name
          }
        }
        let formData = new FormData()
        formData.append('user',this.userName)
        formData.append('tableName',this.dataShapeName)
        formData.append('deleteColumn',columnName)
        postAction(this.url.Del, formData).then((res) => {
          if (res.success) {
            this.$message.success('删除成功');
            this.$refs.table.refresh()
          } else {
            this.$message.error(res.message);
          }
        })
      },

      onChange (row) {
        this.selectedRowKeys = row.selectedRowKeys
        this.selectedRows = row.selectedRows

      },

      handlepermission () {
        this.$emit('toPermission')
      },


      /**管理警报**/
      openModel (data) {
        this.visible = true
      },
      handleOk (data)  {
        this.visible = false
      },
      handleCancel (data) {
        this.visible = false
      }
    }
  }
</script>

