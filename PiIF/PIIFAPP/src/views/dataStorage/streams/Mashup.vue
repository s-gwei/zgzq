<template>
  <div class="StreamMashup">
    <a-card :bordered="false" style="border-bottom: 1px solid #f1f1f1;">
      <a-row :gutter="48" type="flex" justify="start" style="margin-bottom: 20px;">
        <a-col :style="{ borderRight: '1px solid #f1f1f1'}">
          <span :style="{fontSize: '24px', lineHeight: '40px', whiteSpace: 'nowrap',margin:'0 24px'}"> 混搭</span>
        </a-col>
        <a-col >
          <a-form layout="inline">
<!--            <a-form-item label="">-->
<!--              <a-input-search v-model="queryParam.thingName" @search="editData" placeholder="输入查询条件查询"/>-->
<!--            </a-form-item>-->

            <a-form-item label="时间:">
              <a-range-picker
                showTime
                :defaultValue="[defaultTime1, defaultTime2]"
                format='YYYY-MM-DD HH:mm:DD'
                :placeholder="['开始时间', '结束时间']"
                @change="changeDate"
              />
              <a-switch  style="margin-left: 10px" checkedChildren="开启" unCheckedChildren="关闭" @change="switchChange"/>
              <a-button type="primary" style="margin-left: 8px" icon="reload" @click="updateData">{{refreshText}}</a-button>
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
              <a-button type="primary" style="margin-left: 8px" icon="plus" @click="addFilter">添加筛选器</a-button>
              <a-upload name="file" style="margin-left: 8px" :showUploadList="false" :multiple="false" :headers="headers" :action="importExcelUrl" @change="handleImportExcel">
                <a-button type="primary" icon="import">导入</a-button>
              </a-upload>
            </a-form-item>
          </a-form>
        </a-col>
      </a-row>
      <div class="table-page-search-wrapper">
        <div style="margin-left: 146px;">

        </div>
      </div>
    </a-card>
    <a-card :bordered="false">
      <a-tabs defaultActiveKey="1">
        <a-tab-pane tab="数据" key="1">
          <s-table
            ref="table"
            size="default"
            :columns="columns"
            :data="loadData"
            :showAlertInfo="true"
            @onSelect="onChange"
          >
          </s-table>
        </a-tab-pane>

        <a-tab-pane tab="图表" key="2">
          <line-chart-multid
             :dataSource="streamChart"
             :fields="streamChartFields"
             :style="{ padding: '0 0 0 0' }"
             :height="420"/>
        </a-tab-pane>
      </a-tabs>
    </a-card>

    <FilterModel ref="FilterModel" :msg="colsData"></FilterModel>
  </div>
</template>

<script>
  import STable from '@/components/table/'
  import ATextarea from "ant-design-vue/es/input/TextArea"
  import AInput from "ant-design-vue/es/input/Input"
  import FilterModel from './FilterModel.vue'
  import moment from "moment"
  import axios from 'axios';
  import { getRoleList, getServiceList } from '@/api/manage'
  import {getAction, postAction, deleteAction} from '@/api/manage'
  import LineChartMultid from '@/components/chart/LineChartMultid'
  export default {
    name: "StreamMashup",
    components: {
      AInput,
      FilterModel,
      ATextarea,
      STable,
      LineChartMultid
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

        userId: JSON.parse(localStorage.getItem('pro__Login_Userinfo')).value.id,
        //token header
        headers:{},
        StreamInfo: {},
        type: '',  // 区分实例和模板
        streamName: '',
        objectPk:'',

        moment,

        defaultTime1:moment(new Date()),
        defaultTime2:moment(new Date()),

        startTime:'',

        endTime:'',

        visible: false,  // 高级筛选

        switch:false,

        refreshText:'更新数据',
        // 查询参数
        queryParam: {},
        // 表头
        columns: [],

        colsData:[],
        loadData: (parameter) => {
          if (!this.url.list) {
            this.$message.error('请设置url.list属性!')
            return
          }
          let params = {
            "tableName":this.streamName
          }
          return getAction(this.url.list, Object.assign(params, this.queryParam, parameter)).then((res) => {
            console.log(res)
            if (res.success) {
              this.colsData = res;
              if(res.result === null){
                return {
                  data: [],
                  pageNo: 1,
                  pageSize: 10,
                  totalCount: 0,
                  totalPage:0
                }
              }
              if(res.result !== null){
                this.getChartData(res)
                return {
                  data: res.result.data.records,
                  pageNo: res.result.data.current,
                  pageSize: res.result.data.size,
                  totalCount: res.result.data.total,
                  totalPage: res.result.data.pages
                }
              }
            }else{
              return {
                data: [],
                pageNo: 1,
                pageSize: 10,
                totalCount: 0,
                totalPage:0
              }
            }
          })
        },

        tags: [],
        url: {
          list: '/streamModel/findMashUpData',
          tags: '/thingTemplate/getThingTemplateTagsList',
          delete:'/tableMessage/deleteTable',
          importExcelUrl: "sys/user/importExcel",
        },
        selectedRowKeys: [],
        selectedRows: [],

        streamChart:[],
        streamChartFields:[]
      }
    },
    created () {
      this.userId = JSON.parse(localStorage.getItem('pro__Login_Userinfo')).value.id
      this.StreamInfo = JSON.parse(sessionStorage.getItem('StreamInfo'))
      this.streamName = this.StreamInfo.name
      this.type = this.StreamInfo.type
      let token = this.$store.state.user.token
      this.headers = {"X-Access-Token":token}
      this.getTags();
      this.getColums();

      getRoleList({ t: new Date()})
    },
    computed: {
      importExcelUrl: function(){
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
      }
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

      //查询列
      getColums(){
        if (!this.url.list) {
          this.$message.error('请设置url.list属性!')
          return
        }
        if(this.type === 'valueStream'){
          this.columns = [];
          return
        }
        let params = {
          "tableName":this.streamName
        }
        return getAction(this.url.list, params).then((res) => {
            if (res.result !== null) {
              let colArray = [];
              if(res.result){
                let cols = res.result.columns;
                for(let i=0;i<cols.length;i++){
                  if(cols[i].name === 'time'){
                    colArray.push({
                      title: cols[i].name,
                      dataIndex: cols[i].name,
                      sorter: true,
                      customRender: (text) => {
                        return moment(text).format('YYYY-MM-DD HH:mm:ss')
                      }
                    })
                  }else{
                    colArray.push({
                      title: cols[i].name,
                      dataIndex: cols[i].name
                    })
                  }
                }
              }
              this.columns = colArray;
            }
        })
      },

      //获取图表数据
      getChartData(res){
        let cols = res.result.columns;
        let colsArray = [];
        if(cols.length >0){
          for(let i=0;i<cols.length;i++){
            if(cols[i].type === 'DOUBLE'){
              colsArray.push(cols[i].name)
            }
          }
        }
        this.streamChartFields = colsArray
        let resData = res.result.data.records
        let chartData = [];
        if(resData.length >0){
          for(let j = 0;j<resData.length;j++){
            let date = resData[j].time
            let time;
            if(date.indexOf('T') > -1){
              time = date.replace(/T/g,'').replace(/\.[\d]{3}Z/,'')
            }else{
              time = date
            }
            let obj = {
              type:time,
            };
            for(let k =0;k<colsArray.length;k++){
              let key = colsArray[k];
              let value = resData[j][colsArray[k]];
              obj[key] = value;
            }
            chartData.push(obj)
          }
          this.streamChart = chartData;
        }
      },
      // openModelSearch  高级查询
      openModelSearch (flag) {
        this.visible = flag
      },

      //添加筛选器
      addFilter(){
        this.$refs.FilterModel.modelVisible = true
      },

      /* 导入 */
      handleImportExcel(info){
        console.log(info)
        if (info.file.status !== 'uploading') {
          console.log(info.file, info.fileList);
        }
        if (info.file.status === 'done') {
          if (info.file.response.success) {
            // this.$message.success(`${info.file.name} 文件上传成功`);
            if (info.file.response.code === 201) {
              let { message, result: { msg, fileUrl, fileName } } = info.file.response
              let href = window._CONFIG['domianURL'] + fileUrl
              this.$warning({
                  title: message,
                  content: (
                    <div>
                    <span>{msg}</span><br/>
                    <span>具体详情请 <a href={href} target="_blank" download={fileName}>点击下载</a> </span>
                </div>
              )
            })
            } else {
              this.$message.success(info.file.response.message || `${info.file.name} 文件上传成功`)
            }
          } else {
            this.$message.error(`${info.file.name} ${info.file.response.message}.`);
          }
        } else if (info.file.status === 'error') {
          this.$message.error(`文件上传失败: ${info.file.msg} `);
        }
      },
      onChange (row) {
        this.selectedRowKeys = row.selectedRowKeys
        this.selectedRows = row.selectedRows

      },

      switchChange(checked){
        if(checked){
          this.refreshText = '立即刷新'
          this.$refs.table.refresh()
        }else{
          this.refreshText = '更新数据'
        }
      },

      //更新stream数据
      updateData(){
        if(this.refreshText === '立即刷新'){
        }
        this.$refs.table.refresh()
      },

      changeDate(date,dateString){
        this.startTime = dateString[0]
        this.endTime = dateString[1]
        console.log(this.startTime)
        console.log(moment(dateString))
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