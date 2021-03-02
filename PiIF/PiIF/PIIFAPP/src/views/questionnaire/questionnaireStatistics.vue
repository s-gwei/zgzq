<template>
  <a-card :bordered="false">
    <div style="overflow: hidden;">
      <a-button type="primary" icon="download" @click="handleExportXls('部门评价统计')"
                style="margin-bottom: 20px;float: right;">导出
      </a-button>
    </div>
    <a-table
      :rowKey="(record,index)=> index"
      bordered
      :columns="columns"
      :dataSource="dataSource"
      :pagination="ipagination"
      @change="getDepartData"
    >
      <template slot="name" slot-scope="text,record">
        <a @click="goDetail(record.userId)">{{text}}</a>
      </template>
    </a-table>
    <!--/** 弹框 **/-->
    <screenModel ref="screenModel" :title="modelTitle + '个人评价统计'">
      <template slot="content">
        <div>
          <!--总监评价-->
          <a-row style="margin-top: 8px;background-color: #cdcdcd;font-size: 18px;font-style: italic;">
            <a-col :span="6" style="padding-left: 20px;">
              DA
            </a-col>
            <a-col :span="6" style="text-overflow: ellipsis;overflow: hidden;white-space: nowrap;">
              评价人：{{dataSourceList.evaluate1}}
            </a-col>
            <a-col :span="6">
              总监评价
            </a-col>
            <a-col :span="6">
            </a-col>
          </a-row>
          <a-table :columns="columnsList.list1" :dataSource="dataSourceList.list1" bordered :pagination="false">

            <template slot="score" slot-scope="text,record">
              <a-tooltip placement="topLeft">
                <template slot="title">
                  <span>{{record.detail}}</span>
                </template>
                <a>{{text}}</a>
              </a-tooltip>
            </template>

            <template slot="name" slot-scope="text,record">
              <a-tooltip placement="topLeft">
                <template slot="title">
                  <span>{{record.desc}}</span>
                </template>
                <a>{{text}}</a>
              </a-tooltip>
            </template>

          </a-table>
          <!--pm评价-->
          <a-row style="margin-top: 8px;background-color: #cdcdcd;font-size: 18px;font-style: italic;">
            <a-col :span="6" style="font-size: 18px;font-style: italic;padding-left: 20px;">
              PMA
            </a-col>
            <a-col :span="6" style="text-overflow: ellipsis;overflow: hidden;white-space: nowrap;">
              评价人：{{dataSourceList.evaluate2}}
            </a-col>
            <a-col :span="6">
              PMs评价
            </a-col>
            <a-col :span="6">
            </a-col>
          </a-row>
          <a-table :columns="columnsList.list1" :dataSource="dataSourceList.list2" bordered :pagination="false">

            <template slot="score" slot-scope="text,record">
              <a-tooltip placement="topLeft">
                <template slot="title">
                  <span>{{record.detail}}</span>
                </template>
                <a>{{text}}</a>
              </a-tooltip>
            </template>

            <template slot="name" slot-scope="text,record">
              <a-tooltip placement="topLeft">
                <template slot="title">
                  <span>{{record.desc}}</span>
                </template>
                <a>{{text}}</a>
              </a-tooltip>
            </template>

          </a-table>
          <!--同行评价-->
          <a-row style="margin-top: 8px;background-color: #cdcdcd;font-size: 18px;font-style: italic;">
            <a-col :span="6" style="font-size: 18px;font-style: italic;padding-left: 20px;">
              PeerA
            </a-col>
            <a-col :span="6" style="text-overflow: ellipsis;overflow: hidden;white-space: nowrap;">
              评价人：{{dataSourceList.evaluate3}}
            </a-col>
            <a-col :span="6">
              同行评价
            </a-col>
            <a-col :span="6">
            </a-col>
          </a-row>
          <a-table :columns="columnsList.list1" :dataSource="dataSourceList.list3" bordered :pagination="false">

            <template slot="score" slot-scope="text,record">
              <a-tooltip placement="topLeft">
                <template slot="title">
                  <span>{{record.detail}}</span>
                </template>
                <a>{{text}}</a>
              </a-tooltip>
            </template>

            <template slot="name" slot-scope="text,record">
              <a-tooltip placement="topLeft">
                <template slot="title">
                  <span>{{record.desc}}</span>
                </template>
                <a>{{text}}</a>
              </a-tooltip>
            </template>

          </a-table>
          <!--自我评价-->
          <a-row style="margin-top: 8px;background-color: #cdcdcd;font-size: 18px;font-style: italic;">
            <a-col :span="6" style="font-size: 18px;font-style: italic;padding-left: 20px;">
              SA
            </a-col>
            <a-col :span="6" style="text-overflow: ellipsis;overflow: hidden;white-space: nowrap;">
              评价人：{{dataSourceList.evaluate4}}
            </a-col>
            <a-col :span="6">
              自我评价
            </a-col>
            <a-col :span="6">
            </a-col>
          </a-row>
          <a-table :columns="columnsList.list1" :dataSource="dataSourceList.list4" bordered :pagination="false">

            <template slot="score" slot-scope="text,record">
              <a-tooltip placement="topLeft">
                <template slot="title">
                  <span>{{record.detail}}</span>
                </template>
                <a>{{text}}</a>
              </a-tooltip>
            </template>

            <template slot="name" slot-scope="text,record">
              <a-tooltip placement="topLeft">
                <template slot="title">
                  <span>{{record.desc}}</span>
                </template>
                <a>{{text}}</a>
              </a-tooltip>
            </template>

          </a-table>

          <a-row style="margin-top: 20px;">
            <a-col :span="6">
            </a-col>
            <a-col :span="6">
            </a-col>
            <a-col :span="6">
            </a-col>
            <a-col :span="6" style="font-size: 18px;font-style: italic;text-decoration:underline;">
              总分：{{totalScore ? totalScore : '-'}}&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;状态： {{status}}
            </a-col>
          </a-row>
        </div>
      </template>
      <template slot="footer">
        <a-button>关闭</a-button>
      </template>
    </screenModel>
  </a-card>
</template>

<script>
  import screenModel from '@/components/model/screenModel'
  import {getAction, downFile} from '@/api/manage';
  import {accAdd} from '@/utils/util'


  export default {
    name: 'questionnaireStatistics',
    data() {
      return {
        ipagination: {
          current: 1,
          pageSize: 10,
          total: 0,
        },  // 分页
        columns: [
          {
            title: '#',
            width: '180px',
            align: 'center',
            dataIndex: 'rowIndex',
            customRender: function (text, r, index) {
              return (text !== '合计') ? (parseInt(index) + 1) : text
            }
          },
          {
            title: '姓名',
            dataIndex: 'realname',
            scopedSlots: {customRender: 'name'},
          },
          {
            title: '评分',
            dataIndex: 'score',
            customRender: function (text, r, index) {
              return text ? text : '/'
            }
          }
        ],
        columnsList: {
          list1: [
            {
              title: 'ID',
              dataIndex: 'id',
              width: '100px'
            },
            {
              title: '评估项',
              dataIndex: 'name',
              width: '200px',
              scopedSlots: {customRender: 'name'}
            },
            {
              title: '评估分值',
              dataIndex: 'score',
              scopedSlots: {customRender: 'score'},
              width: '100px'
            },
            {
              title: '理由陈述',
              dataIndex: 'reason',
            },
            {
              title: '权重',
              dataIndex: 'weight',
              width: '100px'
            },
            {
              title: '加权重',
              dataIndex: 'weighting',
              width: '100px'
            }]
        },
        containerId: '',
        departId: '',
        dataSource: [],
        dataSourceList: {
          evaluate1: '',
          evaluate2: '',
          evaluate3: '',
          evaluate4: '',
          list1: [],
          list2: [],
          list3: [],
          list4: []
        },
        modelTitle: '',
        totalScore: '',
        status: '',
        url: {
          departTotal: '/investigation/entity/getScoreByDepartId',
          userTotal: '/investigation/entity/getScoreByUserId',
          depart: '/sys/user/getCurrentUserDeparts',
          exportXlsUrl: '/investigation/entity/exportXls',
          departId: '/sys/sysDepart/getByCode'
        }
      }
    },
    components: {
      screenModel
    },
    created() {
      this.containerId = this.$route.query.id
      this.getDepartId()
    },
    computed: {
      orgCode: function() {
        return this.$store.getters.userInfo.orgCode
      }
    },
    mounted() {
    },
    methods: {
      getDepartId() {
        let { orgCode } = this
        getAction(this.url.departId, { code: orgCode }).then((res) => {
          if (res.success) {
            this.departId = res.result.id
            this.getDepartData()
          } else {
            this.$message.error(res.message)
          }
        })
      },
      getDepartData(page) {
        if (this.departId) {
          getAction(this.url.departTotal, {
            departId: this.departId,
            containerId: this.containerId,
            pageNo: page ? page.current : this.ipagination.current,
            pageSize: this.ipagination.pageSize
          }).then((res) => {
            if (res.success) {
              this.dataSource = res.result.data.records
              if (this.dataSource && this.dataSource.length > 0) {
                if(res.result.data.current == res.result.data.pages){
                  this.dataSource.push({realname: '合计',score: res.result.totalScore})
                }
                // this.tableAddTotalRow(this.columns, this.dataSource)
                this.ipagination.total = res.result.data.total
                this.ipagination.current = res.result.data.current
              }
            } else {
              this.$message.error(res.message)
            }
          })
        }
      },
      goDetail(id) {
        if (id) {
          console.log(id)
          getAction(this.url.userTotal, {containerId: this.containerId, userId: id}).then((res) => {
            if (res.success) {
              console.log(res.result)
              this.$refs.screenModel.open()
              this.modelTitle = res.result.realName
              this.totalScore = res.result.score
              this.status = res.result.status
              this.dataSourceList.list1 = res.result.list[0].details
              this.dataSourceList.evaluate1 = res.result.list[0].evaluators
              this.dataSourceList.list2 = res.result.list[1].details
              this.dataSourceList.evaluate2 = res.result.list[1].evaluators
              this.dataSourceList.list3 = res.result.list[2].details
              this.dataSourceList.evaluate3 = res.result.list[2].evaluators
              this.dataSourceList.list4 = res.result.list[3].details
              this.dataSourceList.evaluate4 = res.result.list[3].evaluators

            } else {
              this.$message.error(res.message)
            }
          })
        }
      },
      /** 表格增加合计行 */
      tableAddTotalRow(columns, dataSource) {
        let numKey = 'rowIndex'
        let totalRow = {[numKey]: '合计'}
        columns.forEach(column => {
          let {key, dataIndex} = column
          if (![key, dataIndex].includes(numKey)) {

            let total = 0
            dataSource.forEach(data => {
              total = accAdd(total, /^\d+\.?\d\d?$/.test(data[dataIndex]) ? Number(data[dataIndex]) : Number.NaN)
              console.log(data[dataIndex], ':', (/^\d+\.?\d?$/.test(data[dataIndex]) ? Number(data[dataIndex]) : Number.NaN))
            })
            console.log(total)
            if (Number.isNaN(total)) {
              total = '/'
            }
            totalRow[dataIndex] = total
          }
        })
        dataSource.push(totalRow)
      },
      /** 导出 **/
      handleExportXls(fileName) {
        if (!fileName || typeof fileName != "string") {
          fileName = "导出文件"
        }
        let param = {...this.queryParam, departId: this.departId, containerId: this.containerId};
        if (this.selectedRowKeys && this.selectedRowKeys.length > 0) {
          param['selections'] = this.selectedRowKeys.join(",")
        }

        console.log("导出参数", param)
        downFile(this.url.exportXlsUrl, param).then((data) => {
          if (!data) {
            this.$message.warning("文件下载失败")
            return
          }
          if (typeof window.navigator.msSaveBlob !== 'undefined') {
            window.navigator.msSaveBlob(new Blob([data]), fileName + '.xls')
          } else {
            let url = window.URL.createObjectURL(new Blob([data]))
            let link = document.createElement('a')
            link.style.display = 'none'
            link.href = url
            link.setAttribute('download', fileName + '.xls')
            document.body.appendChild(link)
            link.click()
            document.body.removeChild(link); //下载完成移除元素
            window.URL.revokeObjectURL(url); //释放掉blob对象
          }
        })
      },

    }
  }
</script>

<style scoped>

</style>