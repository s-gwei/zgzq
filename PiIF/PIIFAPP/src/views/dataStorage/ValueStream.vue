<template>
  <div class="ValueStream">
    <a-card :bordered="false" style="border-bottom: 1px solid #f1f1f1;">
      <a-row :gutter="48" type="flex" justify="start" style="margin-bottom: 20px;">
        <a-col :style="{ borderRight: '1px solid #f1f1f1'}">
          <span :style="{fontSize: '24px', lineHeight: '40px', whiteSpace: 'nowrap'}">价值流</span>
        </a-col>
        <a-col >
          <a-form layout="inline">
            <a-form-item label="">
              <a-input-search v-model="queryParam.name" @search="editData" placeholder="输入价值流名称查询"/>
            </a-form-item>

            <a-form-item>
              <a-popover placement="bottomLeft" title="高级筛选" trigger="click" :visible="visible">
                <template slot="content">
                  <a-form style="width:400px;margin-top: 20px;">
                    <a-form-item label="标签:" :labelCol="labelCol" :wrapperCol="wrapperCol">
                      <a-select v-model="queryParam.tags" placeholder="请选择" :default-value="0">
                        <a-select-option :value="item" v-for="(item, $index) in tags">{{item}}</a-select-option>
                      </a-select>
                    </a-form-item>
                    <a-form-item label="项目:" :labelCol="labelCol" :wrapperCol="wrapperCol">
                      <a-input placeholder="请输入项目名称" v-model="queryParam.projectName" />
                    </a-form-item>
                    <a-form-item style="overflow: hidden;border-top:1px solid #f1f1f1;padding-top:10px;margin-bottom: 0;">
                      <a-button type="primary" style="float:right;" @click="editData">确认</a-button>
                      <a-button style="margin-right: 8px;float:right;"  @click="visible = false">关闭</a-button>
                    </a-form-item>
                  </a-form>
                </template>

                <a-button type="primary" icon="search" @click="openModelSearch(true)">高级</a-button>
              </a-popover>
              <a-button type="primary" style="margin-left: 8px" icon="reload" @click="resetSearchForm">重置</a-button>
            </a-form-item>
          </a-form>
        </a-col>
      </a-row>
      <div class="table-page-search-wrapper">
        <div style="margin-left: 122px;">
          <a-radio-group>
            <a-radio-button value="plus" @click="goDetail(1)"><a-icon type="plus" />&nbsp;&nbsp;&nbsp;新建</a-radio-button>
            <a-radio-button value="file" @click="handleFile()"><a-icon type="file-search" />&nbsp;&nbsp;&nbsp;查看</a-radio-button>
            <a-radio-button value="edit" @click="handleEdit()"><a-icon type="edit" />&nbsp;&nbsp;&nbsp;编辑</a-radio-button>
            <a-radio-button value="copy" @click="goDetail(4)"><a-icon type="copy" />&nbsp;&nbsp;&nbsp;复制</a-radio-button>
            <a-popconfirm title="确定删除吗?" @confirm="() => handleDel()">
              <a-radio-button value="delete"><a-icon type="delete" />&nbsp;&nbsp;&nbsp;删除</a-radio-button>
            </a-popconfirm>
            <a-radio-button value="lock" @click="goDetail(6)"><a-icon type="lock" />&nbsp;&nbsp;&nbsp;权限</a-radio-button>
          </a-radio-group>
          <!--          <a-dropdown v-if="selectedRowKeys.length > 0">-->
          <!--            <a-menu slot="overlay">-->
          <!--              <a-menu-item key="1"><a-icon type="delete" />删除</a-menu-item>-->
          <!--              &lt;!&ndash; lock | unlock &ndash;&gt;-->
          <!--              <a-menu-item key="2"><a-icon type="lock" />锁定</a-menu-item>-->
          <!--            </a-menu>-->
          <!--            <a-button style="margin-left: 8px">-->
          <!--              批量操作 <a-icon type="down" />-->
          <!--            </a-button>-->
          <!--          </a-dropdown>-->
        </div>
      </div>
    </a-card>
    <a-card :bordered="false">

      <s-table
        ref="table"
        size="default"
        :columns="columns"
        :data="loadData"
        :showAlertInfo="true"
        @onSelect="onChange"
      >
        <template slot="name" slot-scope="text,record">
          <a @click="goDetail(3, record)">{{text}}</a>
        </template>
        <span slot="action" slot-scope="text, record">
          <a @click="goDetail(3, record)">编辑</a>
        <a-divider type="vertical" />
        <a-dropdown>
          <a class="ant-dropdown-link">
            更多 <a-icon type="down" />
          </a>
          <a-menu slot="overlay">
            <a-menu-item>
              <a  @click="goDetail(2, record)">查看</a>
            </a-menu-item>
            <a-menu-item>
              <a-popconfirm title="确定删除吗?" @confirm="() => goDetail(5, record)">
                  <a>删除</a>
              </a-popconfirm>
            </a-menu-item>
          </a-menu>
        </a-dropdown>
      </span>
      </s-table>

    </a-card>
  </div>
</template>

<script>
  import pageLayout from '@/components/page/PageLayout'
  import STable from '@/components/table/'
  import ATextarea from "ant-design-vue/es/input/TextArea"
  import AInput from "ant-design-vue/es/input/Input"
  import moment from "moment"
  import axios from 'axios';
  import { getRoleList, getServiceList } from '@/api/manage'
  import {getAction, postAction, deleteAction} from '@/api/manage'
  export default {
    name: "ValueStream",
    components: {
      AInput,
      ATextarea,
      STable
    },
    data () {
      return {
        userId: JSON.parse(localStorage.getItem('pro__Login_Userinfo')).value.id,
        labelCol: {
          xs: { span: 24 },
          sm: { span: 5 },
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 },
        },
        form: null,
        mdl: {},

        visible: false,  // 高级筛选

        // 查询参数
        queryParam: {},
        // 表头
        columns: [
          {
            title: '模型名称',
            dataIndex: 'name',
            scopedSlots: { customRender: 'name' },
          },
          {
            title: '描述',
            dataIndex: 'description'
          },
          {
            title: '创建时间',
            dataIndex: 'lastModifiedDate',
            width: '400px',
            sorter: true,
            customRender: (text) => {
              return moment(text).format('YYYY-MM-DD HH:mm:ss')
            }
          },
          {
            table: '操作',
            dataIndex: 'action',
            width: '150px',
            scopedSlots: { customRender: 'action' },
          }
        ],
        // 加载数据方法 必须为 Promise 对象
        loadData: (parameter) => {
          if (!this.url.list) {
            this.$message.error('请设置url.list属性!')
            return
          }
          let params = {
            "userId": this.userId,
            "tableName":"iot_valuestream_model"}
          return getAction(this.url.list, Object.assign(params, this.queryParam,parameter)).then((res) => {
            console.log(this.queryParam.templateName)
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
        tags: [],
        url: {
          list: '/valueStreamModel/query',
          tags: '/thingTemplate/getThingTemplateTagsList',
          query:'/thing/query',
          delete: "/valueStreamModel/delete",
          deleteBatch: "/thing/deleteByNameList",
        },
        selectedRowKeys: [],
        selectedRows: []
      }
    },
    created () {
      this.userId = JSON.parse(localStorage.getItem('pro__Login_Userinfo')).value.id
      this.getTags();
      getRoleList({ t: new Date()})
    },
    methods: {
      editData () {
        this.$refs.table.refresh()
      },
      // 查询标签
      getTags () {
        getAction(this.url.tags).then(
          (res) => {
            this.tags = res.result
          })
      },

      // openModelSearch  高级查询
      openModelSearch (flag) {
        this.visible = flag
      },
      // // 添加逻辑
      // handleAdd (flag) {
      //   this.$router.push({path: '/facility/ModelDetail'})
      //   this.$bus.$emit('setCollapsed', true)
      // },

      goDetail (num, record) {
        switch (num) {
          case 1:      // 添加逻辑
            this.$router.push({path: '/dataStorage/StreamDetail'})
            sessionStorage.setItem("StreamInfo",JSON.stringify({tabKey: 1,type:'valueStream'}));
            this.$bus.$emit('setCollapsed', true)
            break;
          case 2:      // 查看逻辑
            this.$router.push({path: '/dataStorage/StreamDetail'})
            sessionStorage.setItem("StreamInfo",JSON.stringify({
              tabKey: 1,
              type:'valueStream',
              name: record.name,
              object_pk:record.object_pk,
              detail:true}));
            this.$bus.$emit('setCollapsed', true)
            break;
          case 3:      // 编辑逻辑
            this.$router.push({path: '/dataStorage/StreamDetail'})
            sessionStorage.setItem("StreamInfo",JSON.stringify({
              tabKey: 1,
              type:'valueStream',
              object_pk:record.object_pk,
              name: record.name}));
            this.$bus.$emit('setCollapsed', true)
            break;
          case 4:      // 复制逻辑
            if (this.selectedRowKeys.length === 0) {
              this.$message.warning('您未选中流数据');
            } else if (this.selectedRowKeys.length >= 1) {
              this.$router.push({path: '/dataStorage/StreamDetail'});
              sessionStorage.setItem("StreamInfo",JSON.stringify({
                tabKey: 1,
                type:'valueStream',
                name: this.selectedRows[0].name,
                object_pk:this.selectedRows[0].object_pk,
                copy:true}));
              this.$bus.$emit('setCollapsed', true)

            }
            break;
          case 5:      // 删除逻辑
            let params = new FormData()
            params.append('tableName',record.name)
            postAction(this.url.delete, params).then((res) => {
              if (res.success) {
                this.$message.success('删除成功');
                this.$refs.table.refresh()
              } else {
                this.$message.error(res.message);
              }
            })
            break;
          case 6:      // 权限逻辑
            if (this.selectedRowKeys.length === 0) {
              this.$message.warning('您未选中流数据');
            } else if (this.selectedRowKeys.length >= 1) {
              this.$router.push({path: '/dataStorage/StreamDetail'})
              sessionStorage.setItem("StreamInfo",JSON.stringify({
                tabKey: 6,
                type:'valueStream',
                object_pk:this.selectedRows[0].object_pk,
                name: this.selectedRows[0].name}));
              this.$bus.$emit('setCollapsed', true)
            }
            break;
        }
      },
      //查看逻辑
      handleFile () {
        if (this.selectedRowKeys.length === 0) {
          this.$message.warning('您未选中流数据');
        } else if (this.selectedRowKeys.length >= 1) {
          this.$router.push({path: '/dataStorage/StreamDetail'})
          sessionStorage.setItem("StreamInfo",JSON.stringify({
            tabKey: 1,
            type:'valueStream',
            name: this.selectedRows[0].name,
            object_pk:this.selectedRows[0].object_pk,
            detail: true}));
          this.$bus.$emit('setCollapsed', true)
        }
      },

      // 修改逻辑
      handleEdit (record) {
//        this.mdl = Object.assign({}, record)
        if (this.selectedRowKeys.length === 0) {
          this.$message.warning('您未选中流数据');
        } else if (this.selectedRowKeys.length >= 1) {
          this.$router.push({path: '/dataStorage/StreamDetail'})
          sessionStorage.setItem("StreamInfo",JSON.stringify({
            tabKey: 1,
            type:'valueStream',
            name: this.selectedRows[0].name,
            object_pk:this.selectedRows[0].object_pk
          }));
          this.$bus.$emit('setCollapsed', true)
        }
        this.$bus.$emit('setCollapsed', true)
      },
      handleOk () {

      },
      handleDel () {
        if (this.selectedRowKeys.length === 0) {
          this.$message.warning('您未选中流数据');
        } else if (this.selectedRowKeys.length >= 1) {
          // let params = {
          //   nameList: ''
          // }
          // this.selectedRows.forEach((v) => {
          //   params.nameList = params.nameList + v.name + ','
          // })
          let params = new FormData()
          params.append('tableName',this.selectedRows[0].name)
          postAction(this.url.delete, params).then((res) => {
            if (res.success) {
              this.$message.success('批量删除成功');
              this.$refs.table.refresh()
            } else {
              this.$message.error(res.message);
            }
          })
        }
      },

      onChange (row) {
        this.selectedRowKeys = row.selectedRowKeys
        this.selectedRows = row.selectedRows

      },
      toggleAdvanced () {
        this.advanced = !this.advanced
      },

      resetSearchForm () {
        this.queryParam = {}
        this.$refs.table.refresh()
      }
    },
    watch: {
    }
  }
</script>