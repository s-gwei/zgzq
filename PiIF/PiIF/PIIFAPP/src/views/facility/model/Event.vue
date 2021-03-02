<template>
  <div class="Event">
    <!--tab头-->
    <a-row :gutter="48" type="flex"  style="border-bottom: 1px solid #f1f1f1;padding-bottom: 10px;margin: 10px 0 10px 0;">
      <a-col>
        <div style=" font-size: 18px;color:#128DFF;line-height: 40px;">事件</div>
      </a-col>
      <a-col >
        <a-radio-group style="margin-top: 4px;">
          <a-radio-button value="plus" @click="handleModel('a')"><a-icon type="plus" />&nbsp;&nbsp;&nbsp;新建</a-radio-button>
          <a-popconfirm title="确定删除吗?" @confirm="() => handleDel()">
            <a-radio-button value="delete"><a-icon type="delete" />&nbsp;&nbsp;&nbsp;删除</a-radio-button>
          </a-popconfirm>
        </a-radio-group>
      </a-col>
    </a-row>

    <div>
      <p>我的事件</p>
      <j-editable-table
        ref="editableTable"
        :loading="loading"
        :columns="columns"
        :dataSource="eventMyData"
        :rowNumber="true"
        :rowSelection="true"
        :actionButton="false"
        :dragSort="true"
        :dragSortAdd="false"
        :maxHeight="300"
        :disabled="true"
        style="margin-top: 8px;"
        @selectRowChange="handleSelectRowChange">

      </j-editable-table>
    </div>

    <div>
      <p>继承事件</p>
      <j-editable-table
        ref="editableTable"
        :loading="loading"
        :columns="columns"
        :dataSource="eventMyData"
        :rowNumber="true"
        :actionButton="false"
        :dragSort="true"
        :dragSortAdd="false"
        :maxHeight="300"
        :disabled="true"
        style="margin-top: 8px;"
        @selectRowChange="handleSelectRowChange">

      </j-editable-table>
    </div>
    <EventModel v-model="modelVisible"></EventModel>
  </div>
</template>

<script>
  import { FormTypes } from '@/utils/JEditableTableUtil'
  import JEditableTable from '@/components/jeecg/JEditableTable'
  import EventModel from './eventModel'
  import {getAction, postAction, deleteAction, putAction} from '@/api/manage'
  export default {
    data () {
      return {
        loading: false,
        eventMyData: [],
        columns: [
          {
            title: '事件名称',
            key: 'name',
            width: '22%',
            placeholder: '请输入${title}',
          },

          {
            title: '来源',
            key: 'baseType',
            // width: '18%',
            width: '200px',
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
            defaultValue: '查看'
          }
        ],
        modelVisible: false,
        selectDataList: [],
        selectDataRowIds: [],
        selectData: []
      }
    },
    components: {
      JEditableTable,
      EventModel
    },
    methods: {
      //
      handleModel (status) {
        if (status === 'a') {
          this.modelVisible = true
        }
      },
      /** 删除事件 **/
      handleDel () {},
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