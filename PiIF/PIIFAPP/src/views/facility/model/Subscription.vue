<template>
  <div class="Subscription">
    <!--tab头-->
    <a-row :gutter="48" type="flex"  style="border-bottom: 1px solid #f1f1f1;padding-bottom: 10px;margin: 10px 0 10px 0;">
      <a-col>
        <div style=" font-size: 18px;color:#128DFF;line-height: 40px;">订阅</div>
      </a-col>
      <a-col>
        <a-radio-group style="margin-top: 4px;">
          <a-radio-button value="plus" @click="handleButton('a')"><a-icon type="plus" />&nbsp;&nbsp;&nbsp;新建</a-radio-button>
          <a-popconfirm title="确定删除吗?" @confirm="() => handleButton('d')">
            <a-radio-button value="delete"><a-icon type="delete" />&nbsp;&nbsp;&nbsp;删除</a-radio-button>
          </a-popconfirm>
        </a-radio-group>
      </a-col>
    </a-row>


    <s-table
      ref="table"
      size="default"
      :columns="columns"
      :data="loadData"
      :showAlertInfo="true"
      rowKey="subscriptionName"
      @onSelect="onChange"
    >
      <template slot="action" slot-scope="text, record">
        <a @click="handleEdit(record)">编辑</a>
      </template>
      <template slot="start" slot-scope="text, record">
        <a-checkbox :disabled="true" :checked="text === 1 || text === '1' ? true : false">
        </a-checkbox>
      </template>
    </s-table>
    <Smodel ref="smodel" @success="refreshTable"></Smodel>
  </div>
</template>

<script>
  import STable from '@/components/table/'
  import Smodel from './subscriptionModel'
  import {getAction, deleteAction} from '@/api/manage'
  export default {
    provide () {
      return {
        ModelInfo: JSON.parse(sessionStorage.getItem('ModelInfo'))
      }
    },
    data () {
      return {
        ModelInfo: {},
        columns: [
          {
            title: '#',
            width: 100,
            customRender: function(t, r, index) {
              return parseInt(index) + 1
            }
          },
          {
            title: '订阅名称',
            dataIndex: 'name',
          },
          {
            title: '订阅描述',
            dataIndex: 'description',
          },
          {
            title: '警报事件',
            dataIndex: 'incident',
          },
          {
            title: '已启用',
            dataIndex: 'whetherStart',
            scopedSlots: { customRender: 'start' }
          },
          {
            title: '操作',
            dataIndex: 'action',
            width: '150px',
            scopedSlots: { customRender: 'action' },
          }
        ],
        selectedRowKeys: [],
        selectedRows: [],
        loadData: () => {
          if (!this.url.list) {
            this.$message.error('请设置url.list属性!')
            return
          }
          return getAction(this.url.list, {thingName: this.thingName}).then((res) => {
            if (res.success) {
              return {
                data: res.result
              }
            }
          })
        },
        url: {
          list: '/incident/listSubscription',
          del: '/incident/deleteSubscription'
        }
      }
    },
    computed: {
      thingName: function() {
        return this.ModelInfo.name
      }
    },
    components: {
      STable,
      Smodel
    },
    created () {
      this.ModelInfo = JSON.parse(sessionStorage.getItem('ModelInfo'))
    },
    methods: {
      handleButton (type) {
        switch (type) {
          case 'a':
            this.$refs.smodel.showDrawer()
            break
          case 'd':
            console.log(this.selectedRowKeys.join(','))
            deleteAction(this.url.del, {
              thingName: this.thingName,
              subscriptionName: this.selectedRowKeys.join('/')
            }).then((res) => {
              if (res.success) {
                this.$message.success('删除成功');
                this.refreshTable()
              } else {
                this.$message.error(res.message)
              }
            })
            break
        }
      },
      handleEdit (data) {
        console.log(data)
        this.$refs.smodel.showDrawer('e', data)

      },
      onChange (row) {
        this.selectedRowKeys = row.selectedRowKeys
        this.selectedRows = row.selectedRows
        console.log(row)
      },
      refreshTable () {
        this.$refs.table.refresh()
        this.selectedRowKeys = []
        this.selectedRows = []
      }
    }
  }
</script>

