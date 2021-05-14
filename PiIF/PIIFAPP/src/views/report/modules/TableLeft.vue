<template>
  <a-card  :bordered="false" style="height: 100%" class="aCard">
      <!-- <div class="notice">
        <div class="span"></div>
        计算通知单结构
      </div> -->
      <div class="search">
        <a-input-search placeholder="请输入关键词"  @search="onSearch" v-model="name"/>
        <!-- <a-input-search v-model="name" placeholder="search" enter-button="Search" @search="onSearch" /> -->
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
      
      <div class="btn" v-if="$store.state.report.btnEditable">
        <a-button type="primary" style="marginRight: 12px" @click="hitBaseline()">打基线</a-button>
        <!-- <a-button type="primary" style="marginRight: 12px" @click="publish()" :disabled="$store.state.report.publishDisable">发布</a-button> -->
        <a-select v-model="value" style="width: '120px'"  @change="handleChange">
          <!-- <a-select-option value="hitBaseline">
            打基线
          </a-select-option> -->
          <a-select-option value="baselineMes">
            基线
          </a-select-option>
          <a-select-option value="revertPublish">
            恢复至发布版
          </a-select-option>
           <a-select-option value="publish" :disabled="$store.state.report.publishDisable">
            发布
          </a-select-option>
        </a-select>
        <!-- <div class="moreBtn">
          <ul class="otherBtn" v-show="btnShow">
             <li type="primary"  @click="hitBaseline()">打基线</li>
             <li  style="marginRight: 12px" @click="baselineMes()">基线</li>
             <li  style="marginRight: 12px" @click="revertPublish()">恢复至发布版</li>
             <li  style="marginRight: 12px" @click="publish()" :disabled="$store.state.report.publishDisable">发布</li>
          </ul>
          <a-button style="marginRight: 12px" class="more" @click="more">更多<a-icon type="down"/></a-button>
        </div> -->
        <!-- <a-button type="primary" style="marginRight: 12px" @click="baselineMes()">基线</a-button>
        <a-button type="primary" style="marginRight: 12px" @click="revertPublish()">恢复至发布版</a-button>
        <a-button type="primary"  @click="publish()" :disabled="$store.state.report.publishDisable">发布</a-button> -->
      </div>
       <div class="btn" v-else>
        <a-button type="primary" style="marginRight: 12px" @click="hitBaseline()">打基线</a-button>
      </div>
      <a-modal
          v-model="baselinevisible"
          title="基线信息展示"
          @cancel="baselineCancel"
          class="exportAlert"
        >
           <template slot="footer">
             <a-button @click="baselineCancel">
               取消
             </a-button>
             <a-button key="submit" type="primary"  @click="handleOk" :disabled="submitFlag">
               提交
             </a-button>
           </template>
           <a-form-item label="请选择基线">
                    <a-select  v-model="baselineVal">
                         <a-select-option :value="item.number" :key="item.number" v-for="item in baselineDate">{{item.name}}</a-select-option>
                     </a-select>
           </a-form-item>
        </a-modal>
        <a-modal
          v-model="createBaselinevisible"
          title="创建基线"
          width="800px"
          @cancel="createBaselineCancel"
          class="exportAlert"
        >
           <template slot="footer">
             <a-button @click="createBaselineCancel">
               取消
             </a-button>
             <a-button key="submit" type="primary"  @click="infohandleOk">
               确定
             </a-button>
           </template>
           <a-form-item label="选择生成基线方式" :label-col="{ span: 5 }" :wrapper-col="{ span: 16}">
              <a-radio-group v-model="baselineCreateValue" @change="onChange">
                  <a-radio :style="radioStyle" value="1">
                    自动命名
                  </a-radio>
                  <a-radio :style="radioStyle" value="2">
                    手动命名
                    <a-input v-if="baselineCreateValue == 2" :style="{ width: 150, marginLeft: 14 }" v-model="baseName" placeholder="请输入自定义基线名称"/>
                  </a-radio>
              </a-radio-group>
           </a-form-item>
        </a-modal>
  </a-card>
</template>

<script>
   import {getAction} from '@/api/manage';
  // import { queryDepartTreeList, searchByKeywords } from '@/api/api'
  //  const testVal = require('@assets/json/testVal.json')
  //  const testVal1 = require('@assets/json/testVal1.json')
  //  const docVal = require('@assets/json/docVal.json')
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
        submitFlag: true,
        baseName: "",
        baselineCreateValue: "1",
        radioStyle: {
          display: 'block',
          height: '30px',
          lineHeight: '30px',
        },
        value: "更多",
        btnShow: false,
        data: [],
        loading: false,
        isClear: false,
        name: "",
        autoExpandParent: true,
        selectedKeys: [],
        expandedKeys: [],
        chckedVal: [],
        // 节点属性
        docVal: {},
        // 中间及右边值(接口获取)
        currentValue: {},
        baselineDate: [],
        baselinevisible: false,
        createBaselinevisible: false,
        baselineVal: null,//所选基线信息
        createBaselineVal: null,//基线生成
        url: {
          findPartInfo: "/jeecg-boot/pdmReport/findPartInfo",  
          findBaseLineInfo:"/jeecg-boot/pdmReport/findBaseLineInfoByProjectNumberAndTaskId",
          findBaseLinePartInfo: "/jeecg-boot/pdmReport/findBaseLinePartInfo",
          markBaseLine:"/jeecg-boot/pdmReport/markBaseLine", //打基线
          publishInstance: "/jeecg-boot/pdmReport/publishInstance" //发布
        },
        baselineNumber: "", //基线编码
        projectNumber: null,
        taskId: null,
        index: 1

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
      // 获取中间样式文件
      this.getStyle()
      // console.log(this.$store.state.report.btnEditable);
      this.projectNumber = this.$route.query.projectNumber || this.$route.params.projectNumber
      this.taskId = this.$route.query.taskId || this.$route.params.taskId || '0'
      this.planNumber = this.$route.query.planNumber || this.$route.params.planNumber
    }, 
    methods: {
      getStyle(){
         const _this = this
        // 项目
        // const val = require("@/assets/json/docStylejson.json")
        // _this.docVal= val
        // this.$store.commit("setdocVal",_this.docVal)
        //本地部署
        getAction('/upload/docStylejson.json').then((val) => {
          // console.log(val);
           _this.docVal = val
           //  _this.docVal = res
           _this.$store.commit("setdocVal",val)
        })
      },
      // 基线生成方式选择
      onChange(v){
        // console.log(v);
        // console.log(this.baselineCreateValue,this.baseName);
      },
      handleChange(val){
        if(val == 'hitBaseline' ){
          this.hitBaseline()
        }   else if(val == 'baselineMes'){
          this.baselineMes()

        } else if(val == 'revertPublish'){
          this.revertPublish()
        } else if(val == 'publish'){
          this.publish()
        }
      },
      more(){
        this.btnShow = true
      },
      // 打基线功能
      hitBaseline(){
        this.baselineCreateValue = "1"
        const _this = this
        this.value = "更多"
        this.createBaselinevisible = true
        return
        this.$confirm({
          title:"确认",
          content:"确认打基线吗?",
          onOk: function(){
              _this.hitBaselineSured()
              // _this.value = "hitBaseline"
          },
          onCancel:function(){
            // _this.value = "更多"
          }
        });
      },
      infohandleOk(){
        // console.log(this.baselineCreateValue,this.baseName);
        if(this.baselineCreateValue == 2 && !this.baseName){
            this.$message.warning("基线名称不能为空")
        }else{
          this.hitBaselineSured()
        }
      },
      hitBaselineSured(){
        const _this = this,url = this.url.markBaseLine, params ={}
        params.partNumberList = JSON.stringify(this.$store.state.report.partNumberSelected)
        // params.partNumberList = JSON.stringify(["ZHENGCHE001_"])
        params.planNumber = this.planNumber
        params.projectNumber = this.projectNumber
        params.taskId =this.taskId
        params.name = this.baselineCreateValue == 1 ? null : this.baseName
        getAction(url,params).then((res) => {
          if(res.success){
            _this.baselineCreateValue =1 
            _this.baseName = ""
            _this.createBaselinevisible=false
            _this.$message.success('打基线成功')
            //  this.$store.commit("currentEdition","基线版")
          }else{
            _this.$message.error(res.message || '打基线失败')
          }
        })

      },
      baselineMes(){
        const _this= this,url= this.url.findBaseLineInfo,params={}
          params.projectNumber = this.projectNumber  //项目号
          params.planNo = this.planNumber  //计划号
          params.index =this.index
          this.baselinevisible = true
          _this.submitFlag = true
          // if(this.baselineDate.length) return
          getAction(url,params).then((res) => {
             if(res.success){
               if(!res.result){
                  _this.$message.warning('查询基线信息为空')
               }else{
                  _this.submitFlag = false
                  _this.$set(_this,"baselineDate",res.result)
                  _this.baselineVal = _this.baselineDate && _this.baselineDate[0] && _this.baselineDate[0].number
               }
             }else{
               _this.$message.error(res.message || '查询所有基线信息失败')
             }
          })
      },
      baselineCancel(){
        this.baselinevisible=false
        this.value = '更多'
      },
      createBaselineCancel(){
        this.createBaselinevisible =false
        this.baseName = ""
      },
      // 获取某条基线信息后提交功能
      handleOk(){
        const _this = this,url=this.url.findBaseLinePartInfo,params={}
        params.baselineNumber = this.baselineVal
        params.partNumber = this.$store.state.report.currentTotalCarPartNumberVal
        // console.log(this.$store.state.report.currentPartNumberVal,this.$store.state.report.currentPartNumberVal);
        _this.value = "更多"
        getAction(url,params).then((res) => {
           if(res.success){
              _this.$message.success('获取信息成功')
               _this.$store.commit("currentEdition","基线版")
               _this.baselinevisible = false
               const data = res.result.filter(function(item){
                 return item.Type == _this.$store.state.report.currentNodeTitleVal
               })
                _this.$set(_this,"currentValue",data[0])
              //  _this.$bus.$emit('currentValue',_this.currentValue,_this.docVal)
                _this.$bus.$emit('currentValue',{
                 val: data[0],
                 data: _this.docVal
               })
              //  console.log(_this.$store.state.report.currentEditionVal,data[0],docVal[data[0].PartName],'test');
               _this.adminCurrentNodeVal(data[0],_this.docVal[_this.$store.state.report.currentNodeTitleVal])
           }else{
               _this.$message.error(res.message || '获取当条基线数据失败')   
           }
        })
      },
      revertPublish(){
        const _this = this
        const key = this.$store.state.report.currentNodeTitleVal
        _this.value = "更多"
        // console.log(this.$store.state.report,docVal[key]);
        this.$confirm({
          title:"确认",
          content:"确认恢复至发布版吗?",
          onOk: function(){
              // _this.value = "revertPublish"
              _this.getCenterDate(_this.$store.state.report.currentPartNumberVal,_this.docVal[key],"RELEASE")
          },
          onCancel:function(){
            // _this.value = "更多"
          }
        });
        // this.getCenterDate(this.$store.state.report.currentPartNumberVal,docVal[key],"发布版")
      },
      publish(){
        const _this = this,url = this.url.publishInstance,params={}
        params.partNumber = JSON.stringify(this.$store.state.report.partNumberSelected)
        getAction(url,params).then((res) => {
          // console.log(res);
             if(res.success){
               this.$store.commit("currentEdition","已发布")
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
        // console.log(this.$store.report.btnEditable);
        if(this.$store.state.report.btnEditable){
             _this.$confirm({
               title:"确认跳转",
               content:"您确保是否已保存",
               cancelText : '返回保存',
               okText  : '放弃保存',
               onOk: function(){
                 _this.$store.commit("changePublishBtnState", true)
                  if (selectedKeys[0] == _this.data[0].PartNumber) return;
                    _this.$store.commit("currentCenterChange", [])
                    _this.chckedVal=[]
                    if (selectedKeys.length > 0 && _this.selectedKeys[0] !== selectedKeys[0]) {
                      _this.selectedKeys = [selectedKeys[0]]
                     const key = event.node.dataRef.Type
                    //  console.log(event.node.dataRef);
                     _this.getCenterDate(_this.selectedKeys[0],_this.docVal[key])
                    }
                },
                onCancel: function(){
                  console.log('onCancel');
                }
            });
        } else{
           _this.$store.commit("changePublishBtnState", true)
            if (selectedKeys[0] == _this.data[0].PartNumber) return;
              _this.$store.commit("currentCenterChange", [])
              _this.chckedVal=[]
              if (selectedKeys.length > 0 && _this.selectedKeys[0] !== selectedKeys[0]) {
                _this.selectedKeys = [selectedKeys[0]]
               const key = event.node.dataRef.Type
              //  console.log(event.node.dataRef);
               _this.getCenterDate(_this.selectedKeys[0])
              }
        }
         
        
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
                  const key = item.children[0].Type
                  this.getCenterDate(selectedNodeKeys[0],this.docVal[key])
                }
                this.expandedKeys.push(item.PartNumber)
              })
            }
      },
      // 获取中间节点数据
      getCenterDate(selectedNodeKeys,data,edition){
        const _this = this
        // const data = {}
        // getAction('/upload/' + selectedNodeKeys + '.json').then((val) => {
        //   // console.log(val);
        //   _this.docVal[val.Type] = val
        //   //  _this.docVal = res
        //    data = val
        // })
         this.$store.commit('currentPartNumber',selectedNodeKeys)
        this.$store.commit('currentNodeTitle',data.Type)
        // console.log(this.$store.state.report.currentEditionVal);
        if(this.$store.state.report.currentEditionVal == "基线版"){
          this.handleOk()
        }else{
          const _this = this,url= this.url.findPartInfo,params={}
          _this.$store.commit("centerAndRightShow",false)
          params.totalCarPartNumber = this.$store.state.report.currentTotalCarPartNumberVal
          params.lifeCycle = edition ? edition : "EDIT"
          params.partNumber = selectedNodeKeys
          getAction(url,params,'get').then((res) => {
               if(res.success && res.result){
                //  console.log(res.result);
                 // _this.tableDate = res.result
                 _this.$store.commit("centerAndRightShow",true)
                 _this.$store.commit("affectLink",{
                   name: "全部",
                   add: [{
                      type: "I",
                      partNumber: _this.thingNumber
                   }]
                 })
                 _this.$store.commit("currentCenterChange", [])
                 if(edition == '发布版'){
                    _this.$store.commit("currentEdition","发布版")
                 } else{
                    _this.$store.commit("currentEdition","最新版")
                 }
                 _this.$store.commit("result", res.result)
                 _this.$set(_this,"currentValue",res.result)
                 _this.$bus.$emit('currentValue',{
                   val: _this.currentValue,
                   data: data
                 })
                _this.adminCurrentNodeVal(_this.currentValue,data)
               } else {
                 _this.$message.error(res.message || "获取数据失败")
                 _this.$store.commit("centerAndRightShow",true)
               }
          })

        }
        
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
.notice{
  display: flex;
  align-items: center;
  color: #424242;
  font-weight: bold;
  font-size: 14px;
  margin: 0 10px;
  height: 26px;
  // justify-content: center;
  // flex-direction: column;
  .span{
     display: inline-block;
     width: 14px;
     height: 14px;
     background: url("../../../assets/risktable/notice.png");
     background-size: contain;
     margin-right: 6px;
   }
}

     /deep/.ant-tree{
       height: calc(100% - 80px);
       overflow-y: auto;
     }
    .aCard{
        background: #fff;
        height: 100%;
        margin-left: 10px;
        .search{
          margin: 10px;
          position: relative;
        /deep/ .ant-input-search-icon{
           color: #1890FF;
         }
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
          padding: 0 14px;
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
    /deep/.ant-select-selection-selected-value{
        color:#3BA1FF;
    }
    .btn{
      // float: right;
      display: flex;
      justify-content: center;
      /deep/.ant-select{
        width: 120px;
        .ant-select-selection__rendered{
            margin-left: 33px;
        }
        .ant-select-arrow{
           right: 33px;
           color: #1890FF;
         }
      }
      .moreBtn{
        position: relative;
        .otherBtn{
          position: absolute;
          bottom: 24px;
          left: -36px;
          width: 160%;
          // .ant-btn{
          //   margin-top: 6px;
          // }
          ul,li{ padding:0;margin:0;list-style:none}
          li{
            padding: 2px 0;
            cursor: pointer;
            &:hover{
              background: #eee;
            }
          }
        }
      }
      // .more{
      //   color:#3BA1FF;
      //   border: 1px solid #D9D9D9;
      // }
    }
</style>