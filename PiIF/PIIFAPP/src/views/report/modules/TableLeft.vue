<template>
  <a-card  :bordered="false" style="height: 100%" class="aCard">
      <div>计算通知单结构</div>
      <div class="search">
        <a-input-search v-model="name" placeholder="search" enter-button="Search" @search="onSearch" />
        <a class="clear" @click="clear1()" v-show="isClear">x</a>
      </div>
      <a-tree
        checkStrictly
        showLine
        :auto-expand-parent="autoExpandParent"
        :expandedKeys.sync="expandedKeys"
        :selectedKeys="selectedKeys"
        :treeData="data"
        @select="handleTreeSelect"
      >
      </a-tree>
      <div class="btn">
        <a-button type="primary" style="marginRight: 12px" @click="hitBaseline()">打基线</a-button>
        <a-button type="primary" style="marginRight: 12px" @click="baselineMes()">基线</a-button>
        <a-button type="primary" style="marginRight: 12px" @click="revertPublish()">恢复至发布版</a-button>
        <a-button type="primary"  @click="publish()" :disabled="$store.state.report.publishDisable">发布</a-button>
      </div>
      <a-modal
          v-model="baselinevisible"
          title="基线信息展示"
          @cancel="baselinevisible=false"
          class="exportAlert"
        >
           <template slot="footer">
             <a-button @click="baselinevisible=false">
               取消
             </a-button>
             <a-button key="submit" type="primary"  @click="handleOk">
               提交
             </a-button>
           </template>
           <a-form-item label="请选择基线">
                 <!-- <a-select-option value="lucy">
                   Lucy
                 </a-select-option>
                 <a-select-option value="disabled">
                   Disabled
                 </a-select-option>
                 <a-select-option value="Yiminghe">
                   yiminghe
                 </a-select-option> -->
                      <a-select  v-model="baselineVal">
                         <a-select-option :value="item.number" :key="item.number" v-for="item in baselineDate">{{item.name}}</a-select-option>
                     </a-select>
           </a-form-item>
        </a-modal>
  </a-card>
</template>

<script>
   import {getAction} from '@/api/manage';
  // import { queryDepartTreeList, searchByKeywords } from '@/api/api'
  //  const testVal = require('@assets/json/testVal.json')
  //  const testVal1 = require('@assets/json/testVal1.json')
   const docVal = require('@assets/json/docVal.json')

import store from '@/store'
  export default { 
    name: 'TableLeft',
    props: {
        treeDataSource : {
            type: Array,
            default: () => {
               return []
            }
        }
    },
    data() {
      return {
        data: [],
        loading: false,
        isClear: false,
        name: "",
        autoExpandParent: true,
        selectedKeys: [],
        expandedKeys: [],
        chckedVal: [],
        // 节点属性
        docVal,
        // 中间及右边值(接口获取)
        currentValue: {},
        baselineDate: [],
        baselinevisible: false,
        baselineVal: null,//所选基线信息
        url: {
          findPartInfo: "/pdmReport/findPartInfo",  
          findBaseLineInfo:"/pdmReport/findBaseLineInfoByProjectNumberAndTaskId",
          findBaseLinePartInfo: "/pdmReport/findBaseLinePartInfo",
          markBaseLine:"/pdmReport/markBaseLine", //打基线
          publishInstance: "/pdmReport/publishInstance" //发布
        },
        baselineNumber: "", //基线编码
        projectNumber: null,
        taskId: null

      }
    },
    watch: {
       treeDataSource: {
          handler(data){
              this.baselineNumber = data[0].PartNumber
              // this.queryTreeData(this.treeDataSource,true)
              this.data = this.getDate(this.treeDataSource,true)
              this.queryTreeData(this.data,true)
          },
          deep:true
       },
       name(val){
         if(val && val.length){
            this.isClear = true
         }
       }
    },
    mounted() {
      // console.log(this.treeDataSource);
      this.projectNumber = this.$route.query.projectNumber
      this.taskId = this.$route.query.taskId
      this.planNumber = this.$route.query.planNumber
    },
    methods: {
      // 打基线功能
      hitBaseline(){
        const _this = this
        this.$confirm({
          title:"确认",
          content:"确认打基线吗?",
          onOk: function(){
              _this.hitBaselineSured()
          }
        });
      },
      hitBaselineSured(){
        const _this = this,url = this.url.markBaseLine, params ={}
        // params.partNumberList = JSON.stringify(this.$store.state.report.partNumberSelected)
        params.partNumberList = JSON.stringify(["ZHENGCHE001_"])
        params.planNumber = this.planNumber
        params.projectNumber = this.projectNumber
        params.taskId =this.taskId
        getAction(url,params).then((res) => {
          if(res.success){
             this.$message.success('打基线成功')
          }else{
            this.$message.error(res.message || '打基线失败')
          }
        })

      },
      baselineMes(){
        const _this= this,url= this.url.findBaseLineInfo,params={}
          params.projectNumber = this.projectNumber  //项目号
          params.planNo = this.planNumber  //计划号
          this.$store.commit("currentEdition","基线版")
          this.baselinevisible = true
          if(this.baselineDate.length) return
          getAction(url,params).then((res) => {
             if(res.success){
                _this.$set(_this,"baselineDate",res.result)
                _this.baselineVal = _this.baselineDate[0].number
             }
          })
      },
      // 获取某条基线信息后提交功能
      handleOk(){
        const _this = this,url=this.url.findBaseLinePartInfo,params={}
        params.baselineNumber = this.baselineVal
        params.partNumber = this.$store.state.report.currentPartNumberVal
        getAction(url,params).then((res) => {
           if(res.success){
               this.baselinevisible = false
                _this.$set(_this,"currentValue",res.result)
              //  _this.$bus.$emit('currentValue',_this.currentValue)
                _this.$bus.$emit('currentValue',{
                 val: res.result,
                 data: docVal[res.result.PartName]
               })
               this.adminCurrentNodeVal(res.result,docVal[res.result.PartName])
           }
        })
      },
      revertPublish(){
        this.$store.commit("currentEdition","发布版")
        const key = this.$store.state.report.currentNodeTitleVal
        // console.log(this.$store.state.report.currentPartNumberVal,docVal[key]);
        this.getCenterDate(this.$store.state.report.currentPartNumberVal,docVal[key],"发布版")
      },
      publish(){
        this.$store.commit("currentEdition","已发布")
        const _this = this,url = this.url.publishInstance,params={}
        params.partNumber = JSON.stringify(this.$store.state.report.partNumberSelected)
        getAction(url,params).then((res) => {
          // console.log(res);
             if(res.success){
                this.$message.success('发布成功')
             }else{
               this.$message.error('发布失败')
             }
        })
      },
       getDate(data){
          const newDateArr = [],_this = this
             data.map(function(item,i){
               var newData ={}
               for(var key in item){
                 if(!item.children || !item.children.length) {
                   newData[key] = item[key]
                   }
               }
               newData.PartNumber = item.PartNumber
               newData.key = item.PartNumber
               newData.title = item.PartName
               newData.PartName = item.PartName
               newData.children = item.children  ? _this.getDate(item.children) : [];
               newDateArr.push(newData);
            })
            return newDateArr
      },
      // 模糊搜索
      onSearch(){
        this.$emit('fuzzySearch',this.name)
      },
      clear1(){
        this.name = ""
        this.isClear = false
        this.$emit('fuzzySearch','')
      },
      handleTreeSelect(selectedKeys,event) {
        if(selectedKeys && !selectedKeys.length || selectedKeys[0]== this.treeDataSource[0].PartNumber) return
        const _this = this
         this.$confirm({
            title:"确认跳转",
            content:"您确保是否已保存",
            onOk: function(){
              _this.$store.commit("changePublishBtnState", true)
               if (selectedKeys[0] == _this.data[0].PartNumber) return;
                 _this.$store.commit("currentCenterChange", [])
                 _this.chckedVal=[]
                 if (selectedKeys.length > 0 && _this.selectedKeys[0] !== selectedKeys[0]) {
                   _this.selectedKeys = [selectedKeys[0]]
                  const key = event.node.dataRef.title
                  _this.getCenterDate(_this.selectedKeys[0],docVal[key])
                 }
             },
             onCancel: function(){
               console.log('onCancel');
             }
         });
        
      },
      getUnderLevel(data){
          data && data.children1 && data.children1.map(item => {
              if(item.children1 && item.children1.length){
                  this.getUnderLevel(item);
              } else{
                  this.chckedVal.push(item)
                  this.selectedKeys.push(item.keys)
              }
              return null
          })
          return this.chckedVal
      },
      emitInput(orgCode) {
        this.$emit('input', orgCode)
      },
      queryTreeData(data,isFlag) {
        // console.log(data,isFlag);
            // 默认选中第一条数据、默认展开所有
            if (data.length > 0) {
              data.forEach((item, index) => {
                if (isFlag && index === 0) {
                  const selectedNodeKeys = [item.children && item.children[0].PartNumber || '']
                  this.$set(this,"selectedKeys",selectedNodeKeys)
                  const key = item.children[0].PartName
                  this.getCenterDate(selectedNodeKeys[0],docVal[key])
                }
                this.expandedKeys.push(item.PartNumber)
              })
            }
      },
      // 获取中间节点数据
      getCenterDate(selectedNodeKeys,data,edition){
        // console.log(selectedNodeKeys,data,'00');
         this.$store.commit('currentPartNumber',selectedNodeKeys)
        // console.log(data,'data');
        // this.$bus.$emit('currentNode',data.title || data.departname)
        this.$store.commit('currentNodeTitle',data.title || data.PartName)
         const _this = this,url= this.url.findPartInfo,params={}
        params.totalCarPartNumber = this.$store.state.report.currentTotalCarPartNumberVal
        params.lifeCycle = edition ? edition : "INWORK"
        params.partNumber = selectedNodeKeys
        getAction(url,params,'get').then((res) => {
             if(res.success && res.result){
              //  console.log(res.result);
               // _this.tableDate = res.result
               _this.$set(_this,"currentValue",res.result)
               _this.$bus.$emit('currentValue',{
                 val: _this.currentValue,
                 data: data
               })
              _this.adminCurrentNodeVal(_this.currentValue,data)
             } else {
               _this.$message.error(res.message || "获取数据失败")
             }
        })
        // 通过key获取当前值
          // this.$set(this,"currentValue",currentValue)
          // this.$bus.$emit('currentValue',this.currentValue)
          // this.adminCurrentNodeVal(this.currentValue,data)

      },
      // 点击节点
      adminCurrentNodeVal(item,data){
        const _this = this
        setTimeout(function (params) {
          // const item = testVal
          _this.addValueToTree(item.classification,data.children)
        },100)
      },
      addValueToTree(val,data){
        data.forEach(function(item){
          item.html.map(function (ele) {
            if(ele.type == 'checkbox'){
              ele.value = Array[val[ele.field]]
            }else{
              ele.value = val[ele.field]
            }
            
            
          })
        })
        this.$bus.$emit("currentValNew", data)
        this.emitInput(data)
      }
    }
  }
</script>

<style scoped lang="scss">
     /deep/.ant-tree{
       height: calc(100% - 110px);
       overflow-y: auto;
     }
    .aCard{
        background: #fff;
        height: 100%;
        margin-left: 10px;
        .search{
          margin: 10px;
          position: relative;
        
          a{
             position: absolute;
             right: 70px;
            //  color: #fff;
            top: 3px;
            font-size: 12px;
            border-radius: 4px;
            z-index: 9;
            padding: 3px;
          }
        }
    }
    /deep/.ant-btn{
          padding: 0 8px;
    }
    /deep/.ant-tree>li:nth-child(1)>.ant-tree-node-content-wrapper{
       cursor: default;
       &:hover{
         background: transparent;
       }
    }
    /deep/.ant-card-body{
      padding: 10px;
    }
    .btn{
      float: right;
    }
</style>