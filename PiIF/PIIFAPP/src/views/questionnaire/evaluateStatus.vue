<template>
  <div>
    <a-card :bordered="false">
      <a-row>
        <a-col :sm="6" :xs="24">
          <head-info title="问券总数" :content="(total.count ? total.count : 0) + '个'" :bordered="true"/>
        </a-col>
        <a-col :sm="6" :xs="24">
          <head-info title="待办评价数" :content="(total.published ? total.published : 0) + '个'" :bordered="true"/>
        </a-col>
        <a-col :sm="6" :xs="24">
          <head-info title="编辑评价数" :content="(total.edit ? total.edit : 0) + '个'" :bordered="true"/>
        </a-col>
        <a-col :sm="6" :xs="24">
          <head-info title="完成评价数" :content="(total.complete ? total.complete : 0) + '个'" :bordered="false"/>
        </a-col>
      </a-row>
    </a-card>

    <a-card
      style="margin-top: 24px"
      :bordered="false"
      title="问卷列表">

      <div slot="extra">
        <a-radio-group @change="changeStatus" v-model="status">
          <a-radio-button value="all">全部</a-radio-button>
          <a-radio-button value="published">待办</a-radio-button>
          <a-radio-button value="edit">编辑</a-radio-button>
          <a-radio-button value="complete">完成</a-radio-button>
          <!--<a-radio-button value="reject">驳回</a-radio-button>-->
        </a-radio-group>
        <a-select  v-model="queryParams.realname" style="width: 120px;margin-left: 20px;">
          <a-select-option value="assessorRealname">
            被评价人
          </a-select-option>
          <a-select-option value="evaluatorRealname">
            评价人
          </a-select-option>
        </a-select>
        <a-input-search style="margin-left: 16px; width: 272px;" v-model="queryParams.searchValue" @search="getData()" />
      </div>


      <a-list size="large" :pagination="false">
        <a-list-item :key="index" v-for="(item, index) in qData">
          <a-list-item-meta :description="item.description">
            <a-avatar slot="avatar" size="large" shape="square" :src="imgurl"/>
            <a slot="title">{{ item.name }}</a>
          </a-list-item-meta>
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
    data () {
      return {
        ispagination: {
          pageSize: 10,
          pageNo: 1,
          total: 0,
          pageSizeOptions: ['10', '20', '30']
        },
        queryParams:{
          realname: 'assessorRealname'
        },
        total: {},
        qData: [],
        departId: '',
        imgurl: require("../../assets/questionnaire.png"),
        status: 'all',
        url: {
          depart: '/sys/user/getCurrentUserDeparts',
          total: '/investigation/entity/countEntity',
          list: '/investigation/entity/queryEntity'
        }
      }
    },
    created () {
      this.containerId = this.$route.query.id
      this.getDepartId()
    },
    methods: {
      getDepartId () {
        getAction(this.url.depart).then((res) => {
          if (res.success) {
            this.departId = res.result.list[0].id
            this.getData()
            this.getTotal()
          }
        })
      },
      getData (pageNo, pageSize) {
        let params = {}
        if (this.queryParams.realname === 'assessorRealname') {
          params = {
            assessorRealname: this.queryParams.searchValue,  // 被评价人
            containerId: this.containerId,
            departId: this.departId,
            status: this.status !== 'all' ? this.status : '',
            pageNo: pageNo ? pageNo : this.ispagination.pageNo,
            pageSize: pageSize ? pageSize : this.ispagination.pageSize}
        } else {
          params = {
            evaluatorRealname: this.queryParams.searchValue,  // 评价人
            containerId: this.containerId,
            departId: this.departId,
            status: this.status !== 'all' ? this.status : '',
            pageNo: pageNo ? pageNo : this.ispagination.pageNo,
            pageSize: pageSize ? pageSize : this.ispagination.pageSize}
        }
        getAction(this.url.list, params).then((res) => {
          if (res.success) {
            this.qData = res.result.records
            this.ispagination.total = res.result.total
            this.ispagination.pageNo = res.result.current
          } else {
            this.qData = null;
          }
        })
      },
      getTotal () {
        getAction(this.url.total, {containerId: this.containerId,departId: this.departId}).then((res) => {
          if (res.success) {
            this.total = res.result
          } else {
            this.qData = null;
          }
        })
      },
      changeStatus () {
        this.ispagination.pageNo = 1
        this.getData()
      },
      //  分页
      onShowSizeChange (page, size) {
        console.log(page + '----' + size)
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