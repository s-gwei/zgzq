<template>
  <div>

    <!--tab头-->
    <a-row :gutter="48" type="flex"  style="border-bottom: 1px solid #f1f1f1;padding-bottom: 10px;margin: 10px 0 10px 0;">
      <a-col :style="{ borderRight: '1px solid #f1f1f1'}">
        <div style=" font-size: 18px;color:#128DFF;line-height: 40px;">属性</div>
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
          <a-radio-button value="lock" @click="handlepermission"><a-icon type="lock" />&nbsp;&nbsp;&nbsp;权限</a-radio-button>
          <a-radio-button value="refresh" @click="refresh"><a-icon type="reload" />&nbsp;&nbsp;&nbsp;刷新</a-radio-button>
        </a-radio-group>
      </a-col>
    </a-row>

    <div>
      <p>我的属性</p>
      <j-editable-table
        ref="editableTable"
        :loading="loading"
        :columns="type ===  'obj' ? columns : facilityColums"
        :dataSource="propertyData"
        :rowNumber="true"
        :rowSelection="true"
        :actionButton="false"
        :dragSort="true"
        :dragSortAdd="false"
        :maxHeight="300"
        :disabled="true"
        style="margin-top: 8px;"
        @selectRowChange="handleSelectRowChange">
        <template v-slot:alarm="props">
          <a-button style="background: #F6AA14;color:#fff;font-size: 18px;" shape="circle" icon="exclamation" @click="openModel(props, 'editableTable')"></a-button>
        </template>
        <template v-slot:action="props">
          <a @click="handleModel(3, props)">{{ props.text }}</a>
        </template>
        <template v-slot:value="props">
          <a-tooltip placement="topLeft">
            <template slot="title">
              <span>{{props.value}}</span>
            </template>
            <span class="descP">{{props.value}}</span>
          </a-tooltip>
        </template>
        <template v-slot:description="props">
          <a-tooltip placement="topLeft">
            <template slot="title">
              <span>{{props.value}}</span>
            </template>
            <span class="descP">{{props.value}}</span>
          </a-tooltip>
        </template>
      </j-editable-table>
    </div>

    <div style="margin-top: 20px;" v-if="type !== 'obj'">
      <p>产品属性</p>
      <j-editable-table
        ref="editableTable2"
        :loading="loading"
        :columns="columns2"
        :dataSource="productData"
        :rowNumber="true"
        :actionButton="false"
        :dragSort="true"
        :dragSortAdd="false"
        :maxHeight="300"
        :disabled="true"
        style="margin-top: 8px;">
        <template v-slot:alarm="props">
          <a-button style="background: #F6AA14;color:#fff;font-size: 18px;" shape="circle" icon="exclamation" @click="openModel(props, 'editableTable2')"></a-button>
        </template>
        <template v-slot:action="props">
          <a @click="handleLook(props)">{{ props.text }}</a>
        </template>
        <template v-slot:value="props">
          <a-tooltip placement="topLeft">
            <template slot="title">
              <span>{{props.value}}</span>
            </template>
            <span class="descP">{{props.value}}</span>
          </a-tooltip>
        </template>
        <template v-slot:description="props">
          <a-tooltip placement="topLeft">
            <template slot="title">
              <span>{{props.value}}</span>
            </template>
            <span class="descP">{{props.value}}</span>
          </a-tooltip>
        </template>
      </j-editable-table>
    </div>


    <a-modal
      title="管理警报"
      :visible="visible"
      :width="1200"
      @ok="handleOk"
      :confirmLoading="confirmLoading"
      :ok-button-props="{ props: { disabled: buttonDisabled } }"
      :cancel-button-props="{ props: { disabled: buttonDisabled } }"
      @cancel="handleCancel"
    >
      <j-editable-table
        :columns="columnsAlarm"
        :dataSource="AlarmData"
        ref="alarmEditTable"
        :maxHeight="300"
        :actionButton="!buttonDisabled"
        :rowSelection="true"
        @deleted="handleAlarmDelete"
      >
<!--        <template v-slot:action="props">-->
<!--          <a @click="handleAlarmDelete(props)">删除</a>-->
<!--        </template>-->
      </j-editable-table>
    </a-modal>

    <PropertyModel ref="PropertyModel" :type="type" @modelMethods="modelMethods"></PropertyModel>
  </div>

</template>

<script>
  import moment from 'moment'
  import { FormTypes } from '@/utils/JEditableTableUtil'
  import JEditableTable from '@/components/jeecg/JEditableTable'
  import PropertyModel from './propertyModel.vue'
  import {getAction, postAction, deleteAction, putAction} from '@/api/manage'

  export default {
    name: 'Property',
    components: { JEditableTable, PropertyModel },
    data() {
      let disable = true
      return {
        loading: false,
        confirmLoading: false,
        propertyName: '',  // 当前属性name
        ModelInfo: {},
        columns: [
          {
            title: '属性名称',
            key: 'name',
            width: '18%',
            defaultValue: '',
            placeholder: '请输入${title}',
          },
          {
            title: '类型',
            key: 'baseType',
            // width: '18%',
            width: '200px',
            allowInput: true,
            defaultValue: '',
            placeholder: '请选择${title}',
            validateRules: [{ required: true, message: '请选择${title}' }]
          },
          {
            title: '值',
            key: 'defaultValue',
            // width: '8%',
            width: '100px',
            type: FormTypes.checkbox,
            customValue: [true, false], // true ,false
            defaultChecked: false
          },
          {
            title: '数据值',
            key: 'value',
            width: '200px',
            type: FormTypes.slot,
            slotName: 'value'
          },
          {
            title: '说明',
            key: 'description',
            width: '20%',
            type: FormTypes.slot,
            slotName: 'description'
          },
          {
            title: '操作',
            key: 'action',
            // width: '8%',
            width: '100px',
            type: FormTypes.slot,
            slotName: 'action',
            defaultValue: '编辑'
          }

        ],
        facilityColums: [
          {
          title: '属性名称',
          key: 'name',
          width: '18%',
          defaultValue: '',
          placeholder: '请输入${title}',
        },
          {
            title: '类型',
            key: 'baseType',
            // width: '18%',
            width: '200px',
            allowInput: true,
            defaultValue: '',
            placeholder: '请选择${title}',
            validateRules: [{ required: true, message: '请选择${title}' }]
          },
          {
            title: '警报',
            key: 'alarm',
            // width: '8%',
            width: '100px',
            type: FormTypes.slot,
            slotName: 'alarm',
            defaultValue: '管理警报'
          },
          {
            title: '值',
            key: 'defaultValue',
            // width: '8%',
            width: '100px',
            type: FormTypes.checkbox,
            customValue: [true, false], // true ,false
            defaultChecked: false
          },
          {
            title: '数据值',
            key: 'value',
            width: '200px',
            type: FormTypes.slot,
            slotName: 'value'
          },
          {
            title: '说明',
            key: 'description',
            width: '20%',
            type: FormTypes.slot,
            slotName: 'description'
          },
          {
            title: '操作',
            key: 'action',
            // width: '8%',
            width: '100px',
            type: FormTypes.slot,
            slotName: 'action',
            defaultValue: '编辑'
          }],
        columns2: [
          {
            title: '属性名称',
            key: 'name',
            width: '22%',
            defaultValue: '',
            placeholder: '请输入${title}',
          },

          {
            title: '类型',
            key: 'baseType',
            // width: '18%',
            width: '200px',
            allowInput: true,
            defaultValue: '',
            placeholder: '请选择${title}',
            validateRules: [{ required: true, message: '请选择${title}' }]
          },
          {
            title: '值',
            key: 'defaultValue',
            // width: '8%',
            width: '100px',
            type: FormTypes.checkbox,
            customValue: [true, false], // true ,false
            defaultChecked: false
          },
          {
            title: '数据值',
            key: 'value',
            width: '200px',
            type: FormTypes.slot,
            slotName: 'value'
          },
          {
            title: '说明',
            key: 'description',
            width: '20%',
            type: FormTypes.slot,
            slotName: 'description'
          },
          {
            title: '操作',
            key: 'action',
            // width: '8%',
            width: '100px',
            type: FormTypes.slot,
            slotName: 'action',
            defaultValue: '查看'
          }
        ],
        columnsAlarm: [
          {
            title: '名称',
            key: 'name',
            width: '200px',
            type: FormTypes.input,
            placeholder: '请输入',
            validateRules: [
              {
                pattern: /^[\u4E00-\u9FA5A-Za-z]+[\u4E00-\u9FA5A-Za-z0-9]+$/,
                message: '${title}不能以数字开头或带有特殊符号！' // 提示的文本
              },
              {
                required: true, // 必填
                message: '${title}不能为空' // 提示的文本
              },
              {
                unique: true,
                message: '${title}不能重复'
              }
            ]
          },
          {
            title: '说明',
            key: 'description',
            width: '20%',
            type: FormTypes.input,
            placeholder: '请输入'
          },
          {
            title: '值',
            key: 'operatorSchema',
            width: '240px',
            type: FormTypes.input,
            slotName: 'values',
            placeholder: '请输入 范围值请用(例：0|100)',
            validateRules: [
              {
                required: true, // 必填
                message: '${title}不能为空' // 提示的文本
              },
              {
                pattern: /^[0-9]*$|^(\d+\|\d+)?$/,
                message: '范围值请用(例：0|100)！' // 提示的文本
              },
            ]
          },
          {
            title: '警报类型',
            key: 'rule',
            width: '200px',
            type: FormTypes.select,
            options: [
              { title: '等于', value: '1' },
              { title: '不等于', value: '2' },
              { title: '以上', value: '3' },
              { title: '下面', value: '4' },
              { title: '在范围内', value: '5' },
              { title: '超出范围', value: '6' },
              { title: '上方偏差', value: '7' },
              { title: '下方偏差', value: '8' },
              { title: '异常现象', value: '9' }
            ],
            validateRules: [
              {
                required: true, // 必填
                message: '${title}不能为空' // 提示的文本
              }
            ],
            placeholder: '请输入'
          },
          {
            title: '是否启用',
            key: 'whetherStart',
            width: '100px',
            type: FormTypes.checkbox,
            customValue: ['1', '0'], // true ,false
            defaultChecked: false
          },
          // {
          //   title: '优先级',
          //   key: 'ordinal',
          //   type: FormTypes.select,
          //   options: [
          //     { title: '1', value: 1 },
          //     { title: '2', value: 2 },
          //     { title: '3', value: 3 },
          //     { title: '4', value: 4 },
          //     { title: '5', value: 5 },
          //     { title: '6', value: 6 },
          //     { title: '7', value: 7 },
          //     { title: '8', value: 8 },
          //     { title: '9', value: 9 },
          //     { title: '10', value: 10 },
          //   ],
          //   placeholder: '请选择'
          // },
          // {
          //   title: '操作',
          //   key: 'action',
          //   // width: '8%',
          //   width: '100px',
          //   type: FormTypes.slot,
          //   slotName: 'action',
          //   defaultValue: '删除'
          // }
        ],
        propertyData: [],
        productData: [],
        AlarmData: [],
        selectedRowIds: [],
        visible: false,
        userName: '',
        type: '',
        thingName: '',
        selectData: {},
        selectDataList: [],
        buttonDisabled: false,  // 管理继承属性修改
        identifier: '',   /** 识别码**/
        url: {
          list: '/thingTemplate/property/list',
          list2: '/thing/property/list2',
          Add: '/thingTemplate/addProperty',
          Add2: '/thing/addProperty',
          Edit: '/thingTemplate/updateProperty',
          Edit2: '/thing/updateProperty',
          Del: '/thingTemplate/deletePropertyList',
          Del2: '/thing/deletePropertyList',
          Plist: '/influxDBMessage/findColumnsByTableName'
        },
        alarmUrl: {
          add: '/incident/addIncident?thingName=',
          list: '/incident/listPageThing',
          del: '/incident/cancelIncident'
        }
      }
    },
    created() {
      this.ModelInfo = JSON.parse(sessionStorage.getItem('ModelInfo'))
      this.userName = this.$store.state.user.info.userName
      this.thingName = this.ModelInfo.name
      this.type = this.ModelInfo.type
      this.identifier = sessionStorage.getItem('identifier')
      this.getData()
    },
    mounted () {
      this.$refs.PropertyModel.isModel = this.ModelInfo.type
      this.getProperybing()
    },
    methods: {
      /** 属性绑定 **/
      getProperybing () {
        getAction(this.url.Plist, {tableName: this.identifier})
          .then((res) => {
            if (res.success) {
              this.$refs.PropertyModel.propertyList = res.result || []
            } else {
              this.$message.error(res.message);
            }
          })
      },
      /**查询属性**/
      getData () {
        if (this.thingName) {
          if (this.type === 'obj') {
            getAction(this.url.list, {thingTemplateName:this.thingName})
              .then((res) => {
                if (res.success) {
                  console.log(res)
                  this.propertyData = res.result
                  //  列表添加类型
//                  this.propertyData.forEach((v) => {
//                    v.dataChangeType = v.aspects.dataChangeType
//                  })
                } else {
                  this.$message.error(res.message);
                }
              })
          } else {
            getAction(this.url.list2, {thingName:this.thingName})
              .then((res) => {
                if (res.success) {
                  this.propertyData = res.result[0]
                  this.productData = res.result[1]
                } else {
                  this.$message.error(res.message);
                }
              })
          }
        }
      },
      refresh () {
        this.getData()
      },
      /**属性**/
      handleModel (num, data) {
        switch (num) {
          case 1:   //新建
            this.$refs.PropertyModel.modelVisible = true
            this.$refs.PropertyModel.formDisabled = false
            this.$refs.PropertyModel.inputNameDisabled = false
            this.$refs.PropertyModel.type = 1
            break;
          case 2:   //查看
            if (this.selectedRowIds.length === 0) {this.$message.warning('未选中属性'); return}
            this.$refs.PropertyModel.modelVisible = true
            this.$refs.PropertyModel.formDisabled = true
            this.$refs.PropertyModel.inputNameDisabled = true
            this.$refs.PropertyModel.type = 2
            this.$refs.PropertyModel.form = this.selectData
            this.$nextTick(() => {
              this.$refs.PropertyModel.form2.setFieldsValue({name: this.selectData.name})
            })
            break;
          case 6:   //查看
            this.$refs.PropertyModel.modelVisible = true
            this.$refs.PropertyModel.formDisabled = true
            this.$refs.PropertyModel.inputNameDisabled = true
            this.$refs.PropertyModel.type = 2
            this.$refs.PropertyModel.form = this.propertyData[data.index]
            this.$nextTick(() => {
              this.$refs.PropertyModel.form2.setFieldsValue({name: this.propertyData[data.index].name})
            })
            break;
          case 3:   //编辑
            this.$refs.PropertyModel.modelVisible = true
            this.$refs.PropertyModel.formDisabled = false
            this.$refs.PropertyModel.inputNameDisabled = true
            this.$refs.PropertyModel.type = 3
            console.log(this.propertyData[data.index])
            this.$refs.PropertyModel.form = this.propertyData[data.index]
            this.$nextTick(() => {
              this.$refs.PropertyModel.form2.setFieldsValue({name: this.propertyData[data.index].name})
            })
            break;
          case 5:   //编辑
            if (this.selectedRowIds.length === 0) {this.$message.warning('未选中属性'); return}
            this.$refs.PropertyModel.modelVisible = true
            this.$refs.PropertyModel.formDisabled = false
            this.$refs.PropertyModel.inputNameDisabled = true
            this.$refs.PropertyModel.type = 3
            this.$refs.PropertyModel.form = this.selectData
            this.$nextTick(() => {
              this.$refs.PropertyModel.form2.setFieldsValue({name: this.selectData.name})
            })
            break;
          case 4:   //复制
            if (this.selectedRowIds.length === 0) {this.$message.warning('未选中属性'); return}
            this.$refs.PropertyModel.modelVisible = true
            this.$refs.PropertyModel.formDisabled = false
            this.$refs.PropertyModel.inputNameDisabled = false
            this.$refs.PropertyModel.type = 4
            this.$refs.PropertyModel.form = this.selectData
            this.$nextTick(() => {
              this.$refs.PropertyModel.form2.setFieldsValue({name: this.selectData.name})
            })
            break;
        }
      },
      modelMethods (params) {
        console.log(params)
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
        let url = null
        if (this.type === 'obj') {
          if (!this.thingName) {this.$message.warning('模板未保存！'); return;}
          url = this.url.Add + '?templateName='
        } else {
          if (!this.thingName) {this.$message.warning('实例未保存！'); return;}
          url = this.url.Add2 + '?thingName='
        }
        postAction(url + this.thingName, data)
          .then((res) => {
            if (res.success) {
              this.$message.success('添加成功');
              this.getData()
              this.$refs.PropertyModel.modelVisible = false
              this.$refs.PropertyModel.reset()
            } else {
              this.$message.error(res.message);
            }
        })
      },
      handleLook (data) {
        console.log(data)
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
        let url = null
        if (this.type === 'obj') {
          url = this.url.Edit + '?templateName='
        } else {
          url = this.url.Edit2 + '?thingName='
        }
        putAction(url + this.thingName, data)
          .then((res) => {
            if (res.success) {
              this.$message.success('修改成功');
              this.getData()
              this.$refs.PropertyModel.modelVisible = false
              this.$refs.PropertyModel.reset()
            } else {
              this.$message.error(res.message);
            }
          })
      },
      handleDel (data) {
        console.log(data)
        if (this.selectedRowIds.length === 0) {
          this.$message.warning('您未选中模型');
        } else if (this.selectedRowIds.length >= 1) {
          let url = null
          let params = {}
          var ids = "";
          for (var a = 0; a < this.selectDataList.length; a++) {
            ids += this.selectDataList[a].name + ",";
          }
          if (this.type === 'obj') {
            url = this.url.Del
            params = {
              propertyNameList: ids,
              templateName: this.thingName
            }
          } else {
            url = this.url.Del2
            params = {
              propertyNameList: ids,
              thingName: this.thingName,
              username: this.userName
            }
          }
          console.log(params)
          this.selectDataList = []
          deleteAction(url, params).then((res) => {
            if (res.success) {
              this.$message.success('删除成功');
              this.getData()
              this.selectDataList = []
            } else {
              this.$message.error(res.message);
            }
          })
        }
      },
      handlepermission () {
        this.$emit('toPermission')
      },


      /**管理警报**/
      openModel (data, type) {
        this.visible = true
        if (type === 'editableTable2') {
          this.buttonDisabled = true
        } else {
          this.buttonDisabled = false
        }
        let { name, baseType } = data.getValue()
        this.propertyName = name
        this.getAlarmData(name)
        let alarm = this.columnsAlarm.find((v) => (v.title === '警报类型'))
        if (baseType === 'STRING' || baseType === 'BOOLEAN') {
          alarm.options = [
            { title: '等于', value: '1' },
            { title: '不等于', value: '2' }
          ]
        } else {
          alarm.options = [
            { title: '等于', value: '1' },
            { title: '不等于', value: '2' },
            { title: '以上', value: '3' },
            { title: '下面', value: '4' },
            { title: '在范围内', value: '5' },
            { title: '超出范围', value: '6' },
            { title: '上方偏差', value: '7' },
            { title: '下方偏差', value: '8' }
          ]
        }
      },
      //  取当前属性警报事件
      getAlarmData (PropertyName) {
        getAction(this.alarmUrl.list, { thingName: this.thingName, PropertyName }).then(
          (res) => {
            if (res.success) {
              this.AlarmData = res.result
            } else {
              this.$message.error(res.message);
            }
          }
        )
      },
      handleOk (data)  {
        let alarmList = []
        this.$refs.alarmEditTable.getValues((error, values) => {
          if (error === 0) {
            values.forEach((v) => {
              delete v.action
              delete v.id
              v.propertyName = this.propertyName
            })
            alarmList = values
            this.confirmLoading = true
            postAction(this.alarmUrl.add + this.thingName, alarmList).then((res) => {
              if (res.success) {
                this.$message.success('更新成功');
              } else {
                this.$message.error(res.message);
              }
            }).finally(() => {
              this.confirmLoading = false
              this.visible = false
            })
          }
        })

      },
      handleAlarmDelete (data) {
        console.log(data)
        if (!data) {
          return
        }
        let str = ''
        let dataStr = JSON.stringify(data)
        this.AlarmData.filter((v) => {
          if (dataStr.includes(v.id)) {
            str+= v.serialNumber + ','
          }
        })
        deleteAction(this.alarmUrl.del, { thingName: this.thingName, serialNumber: str }).then((res) => {
          if (res.success) {
            this.$message.success('删除成功');
          } else {
            this.$message.error(res.message);
          }
        })
      },
      handleCancel (data) {
        this.visible = false
      },
      /**修改EditTable**/
      handleSelectRowChange(selectedRowIds) {
        this.selectDataList = []
        this.selectedRowIds = selectedRowIds
        let tableData = []
        this.$refs.editableTable.getValues((error, values) => {
          tableData = values
        }, false)
        if (selectedRowIds.length >= 1) {
          let proName = tableData.find((v) => (selectedRowIds[0] === v.id)).name
          if (proName) {
            this.selectData = this.propertyData.find((t) => (t.name === proName)) || {}
          }
          selectedRowIds.forEach((k) => {
            tableData.forEach((t) => {
              if (k === t.id) {
                this.selectDataList.push(this.propertyData.find((j) => (j.name === t.name)) || {})
              }
            })
          })
          console.log(this.selectDataList)
        }
      }
    }
  }
</script>

<style>
  .descP{
    text-overflow: ellipsis;
    overflow: hidden;
    white-space: nowrap;
    display: inline-block;
    width:100%;
    height:100%;
  }
</style>

