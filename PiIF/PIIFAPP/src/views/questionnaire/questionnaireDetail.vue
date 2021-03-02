<template>
  <a-card :body-style="{padding: '24px 32px'}" :bordered="false" style="position: relative;">

    <a-divider>问卷调查</a-divider>
    <!--:paragraph="{row: 20}"-->
    <span><b>被评价人：{{model ? model.assessorRealname : ''}}</b></span>
    <a-skeleton active :loading="loading">
      <a-list itemLayout="vertical" size="large" :dataSource="template.lists">

        <div slot="footer"><b>{{model.name ? model.name : ''}}</b>
          &nbsp;&nbsp;&nbsp;被评价人：{{model.assessorRealname ? model.assessorRealname : ''}}
        </div>
        <a-list-item slot="renderItem" slot-scope="item, index" :key="index" style="white-space: pre-line;" :id="item.id">
          <template slot="actions">
<!--            <div style="display: flex;justify-content: flex-start;overflow: hidden;">-->
            <a-row>
              <a-col :xs="24" :sm="8">
                <a-input-number style="width:100%;" placeholder="请输入分数" :class="Qindex === index ? 'score' : ''"  :min="1"
                                :max="100" :formatter="value => `${value ? parseInt(value) : ''}`" v-model="item.score"/>
              </a-col>
              <a-col :xs="24" :sm="4">
                <a-divider type="vertical" style="margin: 0 30px;height:30px;" />
              </a-col>
              <a-col :xs="24" :sm="12">
                <a-textarea style="width:100%;" :auto-size="{ minRows: 3, maxRows: 8 }" placeholder="理由陈述" v-model="item.reason" :class="Rindex === index ? 'score' : ''"/>

              </a-col>
            </a-row>


<!--            </div>-->

          </template>
          <a-list-item-meta :description="item.name | formatDesc">
<!--            <p slot="title">{{ item.name ? (index + 1) + '.' + item.name | formatTitle : ''}}</p>-->
            <p slot="title">{{ (index + 1) + '.' + item.name | formatTitle}}</p>
          </a-list-item-meta>
          {{stringFormat(item.desc)}}
          <!--{{item.desc}}-->
        </a-list-item>
      </a-list>

    </a-skeleton>

    <a-divider>over</a-divider>

    <div style="text-align: center;" v-if="this.status != 1">
      <a-button type="primary" @click="submitQuestionnaire(1)">提交</a-button>
      <a-button style="margin-left: 8px" @click="submitQuestionnaire(2)">保存</a-button>
    </div>
    <div style="text-align: center;" v-if="this.status == 1">
      <a-button type="primary" @click="goback()">返回</a-button>
    </div>

    <!--回到顶部-->
    <a-back-top />

    <!--锚点-->
    <a-anchor  style="position: fixed;top:200px;right:30px;">
      <a-anchor-link :href="'#' + item.id" :title="item.name | formatTitle" v-for="item in template.lists" />
    </a-anchor>
  </a-card>
</template>

<script>
  import {getAction, putAction} from '@/api/manage'

  export default {
    name: 'questionnaireDetail',
    data() {
      return {
        loading: false,
        // form
        form: this.$form.createForm(this),
        model: {},
        qId: '',
        status: null,
        template: {},
        Qindex: null,
        Rindex: null,
        listData: [],
        className: 'score',
        url: {
          detail: '/investigation/entity/getEntity',
          edit: '/investigation/entity/edit'
        }

      }
    },
    filters: {
      formatTitle: function(str) {
          return str ? str.split('-')[0] : ''
      },
      formatDesc: function(str) {
          return str ? str.split('-')[1] : ''
      }
    },
    created() {
      this.qId = this.$route.query.id
      this.status = this.$route.query.status
      this.getDetail(this.qId)
    },
    methods: {
      stringFormat(str) {
        return str.replace(/。/g, "；\n")
//        return str
      },
      /** 问卷详情页 **/
      getDetail(id) {
        this.loading = true
        getAction(this.url.detail, {entityId: id}).then((res) => {
          if (res.success) {
            this.model = res.result
            this.template = JSON.parse(res.result.template)
          } else {
            this.$message.error(res.message);
          }
        }).finally(() => {
          this.loading = false
        })
      },
      /** 提交 **/
      submitQuestionnaire(type) {
        let isCommit = null
        let msg = ''
        if (type === 1) {
          isCommit = true
          msg = '提交成功'
          this.getItemValue(this.template.lists)
          console.log(this.Qindex  , this.Rindex)
          if (this.Qindex === null && this.Rindex === null) {
            console.log('提交成功')
            this.model.template = JSON.stringify(this.template)
            putAction(this.url.edit + '?isCommit=' + isCommit, this.model).then((res) => {
              if (res.success) {
                this.$router.push({path: '/questionnaire/questionnaireList'})
                this.$message.success(msg);
              } else {
                this.$message.warning(res.message);
              }
            })
          } else {
            this.$message.warning('请完善问卷信息');
          }
        } else {
          isCommit = false
          msg = '保存成功'
            this.model.template = JSON.stringify(this.template)
            putAction(this.url.edit + '?isCommit=' + isCommit, this.model).then((res) => {
              if (res.success) {
                this.$router.push({path: '/questionnaire/questionnaireList'})
                this.$message.success(msg);
              } else {
                this.$message.warning(res.message);
              }
            })
        }

      },
      goback() {
        this.$router.push({path: '/questionnaire/questionnaireList'})
      },
      getItemValue(arr) {
        for (var i = 0; i < arr.length; i++) {
          if (arr[i].score == '' || !arr[i].score) {
              this.Rindex = null
              this.Qindex = i
            break;
          }
          if (arr[i].reason == '' || !arr[i].reason) {
            this.Qindex = null
            this.Rindex = i
            break;
          }
          if (i === arr.length - 1) {
            this.Qindex = null
            this.Rindex = null
          }
        }
      }
    }
  }
</script>
<style scoped>
  .score {
    border: 1px solid #F5222D;
  }
</style>