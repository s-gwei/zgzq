<template>
    <div class="subscriptionModel">
      <a-drawer
        title="事件编辑"
        width="620"
        :closable="false"
        :visible="visible"
        @close="onClose"
      >
        <a-tabs  @change="callback" :activeKey="activeKey">
          <a-tab-pane key="1" tab="订阅信息" :forceRender="true">
            <information ref="information" @information="information" @informationEdit="informationEdit"></information>
          </a-tab-pane>
          <a-tab-pane key="2" tab="输入" :forceRender="true">
            <subInput></subInput>
          </a-tab-pane>
          <a-tab-pane key="3" tab="片段" :forceRender="true">
            <subFragment ref="subFragment"></subFragment>
          </a-tab-pane>
          <a-tab-pane key="4" tab="我/实体" :forceRender="true">
            <subEntity></subEntity>
          </a-tab-pane>
        </a-tabs>

        <div
          :style="{
          position: 'absolute',
          bottom: 0,
          width: '100%',
          borderTop: '1px solid #e8e8e8',
          padding: '10px 16px',
          textAlign: 'right',
          left: 0,
          background: '#fff',
          borderRadius: '0 0 4px 4px',
        }"
        >
          <a-popconfirm title="确定放弃编辑？" @confirm="onClose" okText="确定" cancelText="取消">
            <a-button style="margin-right: .8rem">取消</a-button>
          </a-popconfirm>
          <a-button type="primary" @click="handleSubmit">
            提交
          </a-button>
        </div>
      </a-drawer>
    </div>
</template>

<script>
  import information from './subscriptionTabs/information'
  import subEntity from './subscriptionTabs/subEntity'
  import subFragment from './subscriptionTabs/subFragment'
  import subInput from './subscriptionTabs/subInput'
  import {getAction, postAction} from '@/api/manage'
  export default {
    name: 'subscriptionModel',
    inject: ['ModelInfo'],
    components: {
      information,
      subEntity,
      subFragment,
      subInput
    },
    data() {
      return {
        visible: false,
        activeKey: '1',
        childrenDrawer: false,
        type: '',
        editDate: {},
        url: {
          pathUrl: 'incident/addSubscription',
          update: 'incident/updateSubscription'
        }
      }
    },
    mounted () {
    },
    methods: {
      /**  **/
      callback (key) {
        this.activeKey = key
      },
      showDrawer(type, data) {
        this.visible = true;
        if (type === 'e') {
          this.editDate = data
          this.$nextTick(() => {
            this.$refs.information.handleEditInfo(data)
            this.$refs.subFragment.emptyFragment()
            this.callback('1')
          })
        } else {
          this.$nextTick(() => {
            this.$refs.information.display = false
            this.$refs.information.emptyForm()
            this.$refs.subFragment.emptyFragment()
            this.callback('1')
          })
        }
      },
      onClose() {
        this.visible = false;
      },
      showChildrenDrawer() {
        this.childrenDrawer = true;
      },
      onChildrenDrawerClose() {
        this.childrenDrawer = false;
      },
      information (data) {
        let { name, event, facAndPreList, whetherStart, subPEvent, description, incident } = data
        postAction(this.url.pathUrl + '?thingName=' + facAndPreList[0] + '&serialNumber=' + (subPEvent.replace('|', '%7C')) + '&name=' + name + '&whetherStart=' + (whetherStart ? '1' : '0') + '&description=' + description + '&incident=' + incident, {
          subscriptionerve: {
            subscriptionEmailServe: {
              addresseeEmail: "",
              emailContent: "",
              emailTheme: "",
              loginAuthCode: "",
              mailServer: "",
              sendEmail: ""
            },
            subscriptionNoteServe: {
              receptionPhone: ""
            }
          }
        }).then((res) => {
          if (res.success) {
            this.$message.success('添加成功');
            this.$emit('success')
            this.onClose()
          } else {
            this.$message.error(res.message)
          }
        })
      },
      informationEdit (data) {
        let { whetherStart } = data
        let { subscriptionName } = this.editDate
        let { name } = this.ModelInfo
        let obj = {
          SubscriptionName: subscriptionName,
          data: {},
          thingName: name,
          type: '',
          whetherStart: whetherStart === true ? '1' : '0'
        }
        postAction(this.url.update, obj).then((res) => {
          if (res.success) {
            this.$message.success('修改成功');
            this.$emit('success')
            this.onClose()
          } else {
            this.$message.error(res.message)
          }
        })
      },
      handleSubmit () {
        this.$refs.information.handleSubmit()
        this.$refs.subFragment.handleSubmit()
      }
    }
  }
</script>

<style scoped>

</style>