<template>
  <div class="DesignTime">
    <!--tab头-->
    <a-row align="middle" :gutter="48" type="flex"  style="border-bottom: 1px solid #f1f1f1;padding-bottom: 10px;margin: 10px 0 10px 0;">
      <a-col>
        <div style=" font-size: 18px;color:#128DFF;line-height: 40px;">可见性</div>
      </a-col>
      <a-col>
        <j-select-depart style="width: 300px" ref="selectDepart" :modalWidth="600" v-model="bumen" :trigger-change="true" :rootOpened="true" customReturnField="departName" @change="handleOK"></j-select-depart>
      </a-col>
    </a-row>


    <s-table
      ref="table"
      size="default"
      :columns="columns"
      :data="TableData"
      :pagination="pagination "
    >
      <template slot="visible" slot-scope="visible">
        <a-badge status="success" :text="visible ? '允许' : '未允许'" />
      </template>
      <span slot="action" slot-scope="text, record">
        <a @click="handDel(record)">删除</a>
      </span>
    </s-table>

    <!--model-->
  </div>
</template>

<script>
  import STable from '@/components/table/'
  import JSelectDepart from '@/components/jeecgbiz/JSelectDepart'
  import {queryDepartTreeList, searchByKeywords} from '@/api/api'
  import {getAction, postAction, deleteAction, putAction} from '@/api/manage'


  export default {
    data () {
      return {
        loading: false,
        ModelInfo: {},
        type: '',  // 区分实例和模板
        thingName: '',
        pagination: {
          totalCount: 0,
          totalPage: 0
        },
        columns: [
          {
            title: '#',
            dataIndex: 'key',
            width: 100,
            customRender: function(t, r, index) {
              return parseInt(index) + 1
            }
          },
          {
            title: '单位/单位组织',
            dataIndex: 'departName',
          },
          {
            title: '可见性',
            dataIndex: 'id',
            scopedSlots: { customRender: 'visible' }
          },
          {
            title: '操作',
            dataIndex: 'action',
            width: 200,
            scopedSlots: { customRender: 'action' },
          }
        ],
        // 加载数据方法 必须为 Promise 对象
        TableData: (parameter) => {
          if (!this.url.list) {
            this.$message.error('请设置url.list属性!')
            return
          }
          let url = ''
          let params = {}
          if (this.type === 'obj') {
            url = this.url.list
            params =  {thingtemplateId: this.thingName}
          } else {
            url = this.url.list2
            params =  {thingId: this.thingName}
          }
          return getAction(url, Object.assign(parameter, params)).then((res) => {
            if (res.success) {
              return {
                data: res.result.records,
                pageNo: res.result.current,
                pageSize: res.result.size,
                totalCount: res.result.total,
                totalPage: res.result.pages
              }
            }
          })
        },
        bumen: '',
        url: {
          list: '/iot/thingtemplateDepartList',
          list2: '/iot/thingDepartList',
          Add: '/iot/addThingTemplateDepart',
          Add2: '/iot/addThingDepart',
          del: '/iot/deleteDepartInThingtemplate',
          del2: '/iot/deleteDepartInThing'
        }

      }
    },
    components: {
      STable,
      JSelectDepart
    },
    created () {
      this.ModelInfo = JSON.parse(sessionStorage.getItem('ModelInfo'))
      this.type = this.ModelInfo.type
      this.thingName = this.ModelInfo.name
    },
    methods: {
      handleOK () {
        if (!this.bumen) {return;}
        if (!this.url.Add) {
          this.$message.error('请设置url.Add属性!')
          return
        }
        let params = {}
        let url = ''
        if (this.type === 'obj') {
          url = this.url.Add
          params = {
            departId: this.$refs.selectDepart.departIds,
            thingtemplateId: this.thingName
          }
        } else {
          url = this.url.Add2
          params = {
            departId: this.$refs.selectDepart.departIds,
            thingId: this.thingName
          }
        }
        postAction(url, params).then((res) => {
          if (res.success) {
            this.$message.success('添加成功');
            this.$refs.selectDepart.handleOK('')
            this.$refs.table.refresh()
          } else {
            this.$message.error(res.message);

          }
        })
      },
      handDel (record) {
        console.log(record)
        if (!this.url.Add) {
          this.$message.error('请设置url.del属性!')
          return
        }
        if (record.isExtend) {
          this.$message.warning('存在继承关系！')
          return
        }
        let params = {}
        let url = ''
        if (this.type === 'obj') {
          url = this.url.del
          params = {
            departId: record.id,
            thingtemplateId: this.thingName,
          }
        } else {
          url = this.url.del2
          params = {
            departId: record.id,
            thingId: this.thingName,
            isExtend: record.isExtend
          }
        }
        deleteAction(url, params).then((res) => {
          if (res.success) {
            this.$message.success('删除成功');
            this.$refs.table.refresh()
          } else {
            this.$message.error(res.message);

          }
        })
      },
      refresh() {
        this.loading = true
      }
    }
  }
</script>
