<template>
  <a-card :bordered="false">
    <a-row :gutter="48" type="flex"  style="border-bottom: 1px solid #f1f1f1;padding-bottom: 20px;">
      <a-col :style="{ borderRight: '1px solid #f1f1f1',paddingLeft: '52px'}">
        <span :style="{fontSize: '24px', lineHeight: '24px', whiteSpace: 'nowrap'}">{{thingName}}</span>
      </a-col>
      <a-col>
        <a-button type="primary" style="margin-right: 8px;" @click="saveModelInfo">保存</a-button>
        <a-button type="primary" @click="goback">取消</a-button>
      </a-col>
    </a-row>
    <!--菜单-->
    <a-layout style="padding: 24px 0 24px -24px; background: #fff">
      <a-layout-sider width="150" style="background: #fff">
        <a-menu
          mode="inline"
          :defaultSelectedKeys="['1']"
          :selectedKeys="selectedKeys"
          :defaultOpenKeys="['sub1']"
          style="height: 100%;color:#262626;"
          @click="handleClick"
        >
          <a-sub-menu key="sub1">
            <span slot="title" ><a-icon style="font-size: 16px;" type="inbox" /><span>标签</span></span>
            <a-menu-item key="1" style="color:#262626;">基础信息</a-menu-item>
            <a-menu-item key="2"style="color:#262626;">新增词汇</a-menu-item>
          </a-sub-menu>
          <!--<a-sub-menu key="sub2">-->
            <!--<span slot="title" ><a-icon style="font-size: 16px;" type="lock" /><span>权限</span></span>-->
            <!--<a-menu-item key="6"  style="color:#262626;">可见性</a-menu-item>-->
            <!--<a-menu-item key="7"  style="color:#262626;">运行时</a-menu-item>-->
          <!--</a-sub-menu>-->
        </a-menu>
      </a-layout-sider>
      <a-layout-content :style="{ padding: '0 24px', minHeight: '280px' }">
        <!--基础信息-->
        <Detail v-if="tabKey === 1" ref="detail" @getThingName = "getThingName"></Detail>
        <!--字段定义-->
        <Vocabulary v-if="tabKey === 2" @toPermission="toPermission"></Vocabulary>


        <!--&lt;!&ndash;可见性&ndash;&gt;-->
        <!--<DesignTime v-if="tabKey === 6"></DesignTime>-->
        <!--&lt;!&ndash;运行时&ndash;&gt;-->
        <!--<RunTime v-if="tabKey === 7"></RunTime>-->
      </a-layout-content>
    </a-layout>
  </a-card>
</template>

<script>
  import Detail from '@views/facility/tags/Detail'
  import Vocabulary from '@views/facility/tags/tagsVocabulary'
  export default {
    name: 'tagsDetail',
    data () {
      return {
        tabKey: 1,
        selectedKeys: ['1'],
        thingName:'',
        ModelInfo: {}
      }
    },
    components: {
      Detail,
      Vocabulary
    },
    created () {
      this.ModelInfo = JSON.parse(sessionStorage.getItem('tagsInfo'))
      let tabKey = String(this.ModelInfo.tabKey) || '1'
      this.$nextTick(() => {
        this.selectedKeys = [tabKey]
        this.tabKey = parseInt(tabKey)
      })
    },
    mounted(){
      if(this.ModelInfo.name !== undefined){
        this.thingName = this.ModelInfo.name
      }else{
        this.thingName = '新标签-1'
      }
    },
    methods: {
      handleClick (openKeys) {
        this.selectedKeys = [openKeys.key]
        this.tabKey = Number(openKeys.key)
      },
      toPermission () {
        this.tabKey = 6
        this.selectedKeys = ['6']
      },
      saveModelInfo(){
        let self = this;
        if(self.tabKey === 1){
          this.$refs.detail.handleSubmit();
        }
      },
      getThingName(data, id){
        console.log(data, id)
        this.thingName = data;
        this.ModelInfo.name = data
        this.ModelInfo.id = id
        sessionStorage.setItem('tagsInfo', JSON.stringify(this.ModelInfo))
      },
      goback () {
        this.$router.go(-1)
      }
    }
  }
</script>


