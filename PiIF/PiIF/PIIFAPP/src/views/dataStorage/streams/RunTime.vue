<template>
  <div class="RunTime">
    <!--tab头-->
    <a-row :gutter="48" align="middle" type="flex"  style="border-bottom: 1px solid #f1f1f1;padding-bottom: 10px;margin: 10px 0 10px 0;">
      <a-col>
        <div style=" font-size: 18px;color:#128DFF;line-height: 40px;">运行时</div>
      </a-col>
      <a-col>
        <a-select style="width: 300px" @change="handleChange"  v-model="user">
          <a-select-opt-group>
            <span slot="label"><a-icon type="user" />用户</span>
            <a-select-option :value="item.id + '+user'" v-for="item in userList">{{item.realname}}</a-select-option>
          </a-select-opt-group>
          <a-select-opt-group >
            <span slot="label"><a-icon type="team" />角色</span>
            <a-select-option :value="item.id + '+team'"  v-for="item in roleList">{{item.roleName}}</a-select-option>
          </a-select-opt-group>
        </a-select>
      </a-col>
    </a-row>


    <s-table
      ref="table"
      size="default"
      :columns="columns"
      :data="loadData"
    >
      <template slot="switchEdit" slot-scope="select, record">
        <a-switch checkedChildren="开" unCheckedChildren="关"  :checked="select" @change="changeSwitchEdit(1, record)"/>
      </template>
      <template slot="switchDel" slot-scope="select, record">
        <a-switch checkedChildren="开" unCheckedChildren="关"  :checked="select" @change="changeSwitchEdit(2, record)"/>
      </template>
      <span slot="action" slot-scope="text, record">
        <a @click="changeSwitchEdit(3, record)">删除</a>
      </span>
    </s-table>

  </div>
</template>

<script>
  import STable from '@/components/table/'
  import {getAction, postAction, deleteAction, putAction} from '@/api/manage'
  import {getUserList, getRoleList} from '@/api/api'

  export default {
    data () {
      return {
        StreamInfo: {},
        type: '',
        streamName: '',
        objectPk:'',
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
            title: '组或用户',
            dataIndex: 'name',
          },
          {
            title: '修改',
            dataIndex: 'changeable',
            scopedSlots: { customRender: 'switchEdit' },
          },
          {
            title: '删除',
            dataIndex: 'deleteable',
            scopedSlots: { customRender: 'switchDel' },
          },
          {
            title: '操作',
            dataIndex: 'action',
            width: 200,
            scopedSlots: { customRender: 'action' },
          }
        ],
        user: '',
        userList: [],
        roleList: [],
        // 加载数据方法 必须为 Promise 对象
        loadData: (parameter) => {
          if (!this.url.list) {
            this.$message.error('请设置url.list属性!')
            return
          }
          let tableName = ''
          if (this.type === 'dataStream') {
            tableName = 'iot_stream_model'
          } else {
            tableName = 'iot_valuestream_model'
          }
          let params = {
            thingId: this.objectPk,
            tableName: tableName
          }
          return getAction(this.url.list, Object.assign(parameter ,params)).then((res) => {
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
        url: {
          list: '/iot/queryIotEditRightUserList',
          edit: '/iot/changeIotEditRight',
        }
      }
    },
    components: {
      STable
    },
    created () {
      this.StreamInfo = JSON.parse(sessionStorage.getItem('StreamInfo'))
      this.type = this.StreamInfo.type
      this.streamName = this.StreamInfo.name
      this.objectPk = this.StreamInfo.object_pk
    },
    mounted () {
      this.getUserListData()
      this.getRoleListData()
    },
    methods: {
      /** 用户**/
      getUserListData () {
        getUserList({pageSize: 10000}).then((res) => {
          if (res.success) {
            this.userList = res.result.records;
          } else {
            this.$message.error(res.message);
          }
        })
      },
      /** 角色**/
      getRoleListData () {
        getRoleList({pageSize: 10000}).then((res) => {
          if (res.success) {
            this.roleList = res.result.records;
          } else {
            this.$message.error(res.message);
          }
        })
      },
      filterOption(input, option) {
        return (
          option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0
        );
      },
      handleChange (value, option) {
        let tableName = ''
        if (this.type === 'dataStream') {
          tableName = 'iot_stream_model'
        } else {
          tableName = 'iot_valuestream_model'
        }
        if (!this.url.edit) {
          this.$message.error('请设置url.edit属性!');
          return
        }
        let id = value.split("+")[0]
        let isUser = value.split("+")[1] === 'user' ? true : false
        let params = {
          isUser: isUser,
          personId: id,
          tableName: tableName,
          thingId: this.objectPk,
          isChangeable: true,
          isDeleteable: true,
          isExtend: false
        }
       putAction(this.url.edit, params).then((res) => {
          if (res.success) {
            this.$message.success('添加成功');
            this.$refs.table.refresh()
            this.user = ''
          } else {
            this.$message.error(res.message);

          }
        })
      },
      changeSwitchEdit (num, record) {
        let msg = ''
        let params = {}
        let tableName = ''
        if (record.isExtend) {
          this.$message.warning('该权限属于继承权限！')
          return
        }
        if (this.type === 'dataStream') {
          tableName = 'iot_stream_model'
        } else {
          tableName = 'iot_valuestream_model'
        }
        if (num === 1) {
          record.changeable = !record.changeable
          params = {
            isUser: record.user,
            personId: record.id,
            tableName: tableName,
            thingId: this.objectPk,
            isChangeable: record.changeable,
            isDeleteable: null,
            isExtend: record.isExtend
          }
          msg = '修改成功'
        } else if (num === 2){
          record.deleteable = !record.deleteable
          params = {
            isUser: record.user,
            personId: record.id,
            tableName: tableName,
            thingId: this.objectPk,
            isChangeable: null,
            isDeleteable: record.deleteable,
            isExtend: record.isExtend
          }
          msg = '修改成功'
        } else if (num === 3) {
          params = {
            isUser: record.user,
            personId: record.id,
            tableName: tableName,
            thingId: this.objectPk,
            isChangeable: false,
            isDeleteable: false,
            isExtend: record.isExtend
          }
          msg = '删除成功'
        }
        putAction(this.url.edit, params).then((res) => {
          if (res.success) {
            this.$message.success(msg);
            this.$refs.table.refresh()
          } else {
            this.$message.error(res.message);

          }
        })
      }
    }
  }
</script>
