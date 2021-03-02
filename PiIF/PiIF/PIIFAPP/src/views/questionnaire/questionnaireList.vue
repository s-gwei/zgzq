<template>
  <div>
    <a-card :bordered="false">
      <a-row>
        <a-col :sm="12" :xs="24">
          <head-info title="我的待办" :content="ispagination.total + '个任务'" :bordered="true"/>
        </a-col>
        <a-col :sm="12" :xs="24">
          <head-info title="本周完成评价数" :content="ispagination.total + '个'"/>
        </a-col>
      </a-row>
    </a-card>

    <a-card
      style="margin-top: 24px"
      :bordered="false"
      title="问卷列表">

      <div slot="extra">
        <a-radio-group @change="changeStatus" v-model="status">
          <a-radio-button value="published">待办</a-radio-button>
          <a-radio-button value="edit">编辑</a-radio-button>
          <a-radio-button value="complete">完成</a-radio-button>
          <!--<a-radio-button value="reject">驳回</a-radio-button>-->
        </a-radio-group>
        <a-input-search style="margin-left: 16px; width: 272px;" v-model="queryParams.searchValue" @search="getData(ispagination.pageNo, ispagination.pageSize, status)"/>
      </div>


      <a-list size="large" :pagination="false">
        <a-list-item :key="index" v-for="(item, index) in qData">
          <a-list-item-meta :description="item.description">
            <a-avatar slot="avatar" size="large" shape="square" :src="imgurl"/>
            <a slot="title">{{ item.name }}</a>
          </a-list-item-meta>
          <div slot="actions">
            <a-button type="primary" @click="gotoDetail(item)">{{item.status == 'complete' ? '查   看' : '开始评估'}}
            </a-button>
          </div>
          <div class="list-content-item">
            <span>被评价人</span>
            <p>{{ item.assessorRealname }}</p>
          </div>
          <div class="list-content">
            <div class="list-content-item">
              <span>状态</span>
              <p>
                <a-tag color="#108ee9" v-if="item.status == 'published'">待办</a-tag>
                <a-tag color="#FAAD14" v-if="item.status == 'edit'">编辑</a-tag>
                <a-tag color="#87d068" v-if="item.status == 'complete'">完成</a-tag>
                <!--<a-tag color="#f50" v-if="item.status == 'reject'">驳回</a-tag>-->
              </p>
            </div>
          </div>
        </a-list-item>
      </a-list>
      <a-pagination
        style="float: right;"
        v-model="ispagination.pageNo"
        show-size-changer
        :page-size.sync="ispagination.pageSize"
        :pageSizeOptions="ispagination.pageSizeOptions"
        :total="ispagination.total"
        @showSizeChange="onShowSizeChange"
        @change="onChange"
      />
    </a-card>
  </div>
</template>

<script>
  import HeadInfo from '@/components/tools/HeadInfo'
  import {getAction} from '@/api/manage'

  export default {
    name: "questionnaireList",
    components: {
      HeadInfo
    },
    data() {
      return {
        userId: '',
        queryParams: {},
        ispagination: {
          pageSize: 10,
          pageNo: 1,
          total: 0,
          pageSizeOptions: ['10', '20', '30']
        },
        qData: [],
        imgurl: require("../../assets/questionnaire.png"),
        status: 'published',
        url: {
          list: '/investigation/entity/getEntityListByUserId'
        }
      }
    },
    created() {
      this.userId = JSON.parse(localStorage.getItem('pro__Login_Userinfo')).value.id
      this.getData()
    },
    methods: {
      getData(pageNo, pageSize, status) {
        getAction(this.url.list, {
          userId: this.userId,
          status: status ? status : this.status,
          pageNo: pageNo ? pageNo : this.ispagination.pageNo,
          pageSize: pageSize ? pageSize : this.ispagination.pageSize,
          qName: this.queryParams.searchValue,  // 问券名称
        }).then((res) => {
          if (res.success) {
            this.qData = res.result.records
            this.ispagination.total = res.result.total
            this.ispagination.pageNo = res.result.current
          } else {
            this.qData = null;
          }
        })
      },
      changeStatus(e) {
        this.getData(1, 10, e.target.value)
      },
      gotoDetail(records) {
        console.log(records)
        if (records.status === 'complete') {
          this.$router.push({path: '/questionnaire/questionnaireDetail', query: {id: records.id, status: 1}})
        } else {
          this.$router.push({path: '/questionnaire/questionnaireDetail', query: {id: records.id}})
        }
      },

      //  分页
      onShowSizeChange (page, size) {
        this.getData(page, size)
      },
      onChange (page, size) {
        this.getData(page, size)
      }
    }
  }
</script>

<style lang="scss" scoped>
  .ant-avatar-lg {
    width: 48px;
    height: 48px;
    line-height: 48px;
  }

  .list-content-item {
    color: rgba(0, 0, 0, .45);
    display: inline-block;
    vertical-align: middle;
    font-size: 14px;
    margin-left: 40px;
    span {
      line-height: 20px;
    }
    p {
      margin-top: 4px;
      margin-bottom: 0;
      line-height: 22px;
    }
  }
</style>