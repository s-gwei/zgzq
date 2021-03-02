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
          :defaultOpenKeys="['sub1', 'sub2', 'sub3']"
          style="height: 100%;color:#262626;"
          @click="handleClick"
        >
          <a-sub-menu key="sub1">
            <span slot="title" ><a-icon style="font-size: 16px;" type="inbox" /><span>数据形状</span></span>
            <a-menu-item key="1" style="color:#262626;">基础信息</a-menu-item>
            <a-menu-item key="2"style="color:#262626;">字段定义</a-menu-item>
<!--            <a-menu-item key="3"style="color:#262626;">服务</a-menu-item>-->
<!--            <a-menu-item key="4"style="color:#262626;">事件</a-menu-item>-->
<!--            <a-menu-item key="5"style="color:#262626;">订阅</a-menu-item>-->
          </a-sub-menu>
          <a-sub-menu key="sub2" v-show="showPermission">
            <span slot="title" ><a-icon style="font-size: 16px;" type="lock" /><span>权限</span></span>
            <a-menu-item key="6"  style="color:#262626;">可见性</a-menu-item>
            <a-menu-item key="7"  style="color:#262626;">运行时</a-menu-item>

          </a-sub-menu>
          <a-sub-menu key="sub3" v-show="showRelation">
            <span slot="title" ><a-icon style="font-size: 16px;" type="fork" /><span>存在关系</span></span>
            <a-menu-item key="8"  style="color:#262626;">使用关系</a-menu-item>
            <a-menu-item key="9"  style="color:#262626;">被用关系</a-menu-item>
          </a-sub-menu>
        </a-menu>
      </a-layout-sider>
      <a-layout-content :style="{ padding: '0 24px', minHeight: '280px' }">
        <!--基础信息-->
        <Detail v-if="tabKey === 1" ref="detail" @getThingName = "getThingName"></Detail>
        <!--字段定义-->
        <Field v-if="tabKey === 2" ref = "field" @getDataShapeName="getDataShapeName"></Field>
        <!--服务-->
<!--        <Server v-if="tabKey === 3"></Server>-->
<!--        &lt;!&ndash;事件&ndash;&gt;-->
<!--        <Event v-if="tabKey === 4"></Event>-->
<!--        &lt;!&ndash;订阅&ndash;&gt;-->
<!--        <Subscription v-if="tabKey === 5"></Subscription>-->
        <!--可见性-->
        <DesignTime v-if="tabKey === 6"></DesignTime>
        <!--运行时-->
        <RunTime v-if="tabKey === 7"></RunTime>
        <!--使用关系-->
        <EntityDepends v-if="tabKey === 8"></EntityDepends>
        <!--被用关系-->
        <UserEntity v-if="tabKey === 9"></UserEntity>
      </a-layout-content>
    </a-layout>
  </a-card>
</template>

<script>
  import Detail from '@views/dataStorage/shapes/Detail'
  import Field from '@views/dataStorage/shapes/Field'
  import RunTime from '@views/dataStorage/shapes/RunTime'
  import DesignTime from '@views/dataStorage/shapes/DesignTime'
  import EntityDepends from '@views/dataStorage/shapes/EntityDepends'
  import UserEntity from '@views/dataStorage/shapes/UserEntity'
  import { mapState, mapActions } from 'vuex'
  export default {
    name: 'StreamDetail',
    data () {
      return {
        tabKey: 1,
        selectedKeys: ['1'],
        thingName:'',
        dataShapeInfo: {},
        showRelation:true,
        showPermission:true
      }
    },
    components: {
      Detail,
      Field,
      RunTime,
      DesignTime,
      EntityDepends,
      UserEntity
    },
    created () {
      this.dataShapeInfo = JSON.parse(sessionStorage.getItem('dataShapeInfo'))
      let tabKey = String(this.dataShapeInfo.tabKey) || '1'
      this.$nextTick(() => {
        this.selectedKeys = [tabKey]
        this.tabKey = parseInt(tabKey)
      })
      if(this.dataShapeInfo.object_pk !== undefined){
        this.showRelation = true;
        this.showPermission = true;
      }else{
        this.showRelation = false;
        this.showPermission = false;
      }
    },
    mounted(){
      if(this.dataShapeInfo.name !== undefined){
        this.thingName = this.dataShapeInfo.name
      }else{
        this.thingName = '新数据形状-1'
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
          this.$refs.detail.saveDataShape();
        }
      },
      getThingName(data){
        this.thingName = data;
        this.dataShapeInfo.name = data
        sessionStorage.setItem('dataShapeInfo', JSON.stringify(this.dataShapeInfo))
      },
      getDataShapeName(data){
        this.dataShapeInfo.name = data
        sessionStorage.setItem('dataShapeInfo', JSON.stringify(this.dataShapeInfo))
      },
      goback () {
        this.$router.go(-1)
      }
    }
  }
</script>


