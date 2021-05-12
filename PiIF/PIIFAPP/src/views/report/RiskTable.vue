<template>
   <div class="risktable">
      <!-- <div  class="tableContent" style="height:100%;" v-if="!loading && iNow >=99"> -->
      <div  class="tableContent" style="height:100%;">
        <a-row type="flex" :gutter="8" style="height:100%;">
          <a-col :md="4" :sm="24" style="height: 100%" class="pageLeft">
            <table-left v-bind:treeDataSource="treeDataSource"  v-model="currentVal" @fuzzySearch="fuzzySearch" ref="child"/>
          </a-col>
          <a-col :md="14" :sm="24" style="height: 100%">
            <table-center @getCurrentArig="getCurrentArig"/>
          </a-col>
          <a-col :md="6" :sm="24" style="height: 100%">
            <table-right @getCurrentArig="getCurrentArig"/>
          </a-col>
        </a-row>
      </div>
      <!-- <div  style="background: '#f6f6f6;height: 50%" class="loadingBox" v-else>
         <div  id="progressBox" >
            <div id="progressBar">0%</div>
            <div id="progressText">0%</div>
         </div>
      </div> -->
   </div>
</template>

<script>
  import {getAction} from '@/api/manage';
  // import store from '@/store'
  import TableLeft from './modules/TableLeft'
  import TableRight from './modules/TableRight'
  import TableCenter from './modules/TableCenter'
  export default {
    name: 'RiskTable',
    components: { TableLeft, TableRight, TableCenter },
    data() {
      return {
          currentVal: [],
          treeDataSource: [],
          treeDataSourceAll: [],
          loading: true,
          iNow: 0,
          url: {
            findThingTreeInfo: "/jeecg-boot/pdmReport/findThingTreeInfo"
          },
          centerVal: [],
      }
    },
    created(){
      this.$nextTick(function(){
          this.$store.commit("currentVal", this.$router)
      })
    },
    watch: {
      currentVal: {
            handler(val){
              this.$set(this,"centerVal", val)
              this.$store.commit("currentVal", val)
              // this.$set(this,"currentVal",[])
              this.$set(this,"currentVal",val)
              // this.$store.commit("currentVal", [])
              // this.$store.commit("currentVal", val)
              // this.getCurrentVal(val)
            },
            deep: true
        }
    },
    mounted(){
      this.$store.commit('currentTotalCarPartNumber',this.$route.params.totalCarPartNumber || this.$route.query.totalCarPartNumber || "")
      this.getData()
      document.querySelector(".risktable").style.height = document.querySelector("#app").offsetHeight + "px"
      window.onresize = () => {
          return (() => {
             document.querySelector(".risktable").style.height = document.querySelector("#app").offsetHeight + "px"
          })();
       };
    },
    methods: {
      fuzzySearch(v){
        const _this = this
        // this.getData()
        this.$set(this,"treeDataSource",this.treeDataSourceAll)
           var data = [
           {
              "id": null,
              "key": null,
              "departname": null,
              "level": null,
              "title": null,
              "children": [],
        }
        ];
        var treeDataSource1 = JSON.parse(JSON.stringify(_this.treeDataSource))
         treeDataSource1.forEach(function(item,i){
            if(!i){
              data[0].PartName = item.PartName,
              data[0].PartNumber = item.PartNumber,
              data[0].PartPhase = item.PartPhase,
              data[0].PartNumber = item.PartNumber,
              data[0].level = item.level,
              data[0].children = item.children.filter(function(itm){
               return (itm.departname || itm.PartName).indexOf(v) != -1
              })
            }
        })
       if(!v){
          _this.$set(_this,"treeDataSource",_this.treeDataSource)
       } else{
          if(data[0].children.length){
             _this.$set(_this,"treeDataSource",data)
          }else{
            _this.$set(_this,"treeDataSource",[])
          }
       }
      },
      //根据整车部件编码和生命周期或者根据基线编码获取页面左侧显示的树状结构数据
      getData(){
        const _this = this,url= this.url.findThingTreeInfo,params={}
        params.totalCarPartNumber = this.$route.params.totalCarPartNumber || this.$route.query.totalCarPartNumber
        params.lifeCycle = "EDIT"
        // params.baselineNumber = 1
        getAction(url,params,'get').then((res) => {
             if(res.success){
               _this.loading = !_this.loading
              //  console.log(res.result);
               // _this.tableDate = res.result
                const partNumberSelected = []
               res.result.children.forEach(function(item){
                 partNumberSelected.push(item.PartNumber)
               })
               _this.$store.commit("partNumberSelected",partNumberSelected)
               res.result.children.map(function(item){
                 if(item.children && item.children.length){
                    item.children = []
                 }
               })
               _this.$set(_this,"treeDataSource",[res.result])
               _this.$set(_this,"treeDataSourceAll",[res.result])
             } else {
               _this.$message.error(res.message)
               // _this.$nextTick(function(){
               //      _this.renderChart(data)
               //  })
             }
        })
      },
      fatherMethod(){

      },
      getCurrentArig(){
        const key = this.$store.state.report.currentNodeTitleVal
        this.$refs.child.getCenterDate(this.$store.state.report.currentPartNumberVal,this.$store.state.report.docVal[key])
      }
    }
  }
</script>
<style scoped lang="scss">
  @import '~@assets/less/common.less';
  /deep/.ant-card-body{
    height: 100%;
  }
  .pageLeft {
    padding-right: 10px!important;
  }
  .risktable{
    background: #f6f6f6;
    // box-sizing: border-box;
      padding-bottom: 10px;
    .tableContent{
      overflow-y: auto;
      overflow-x: hidden;
      // height: 100%;

    }
  }
  .loadingBox{
    padding: 100px;
  }
  #progressBox {
    // box-sizing: border-box;
      overflow: hidden;
      width: 300px;
      height: 40px;
      border: 1px solid #C8C8C8;
      background: white;
      position: relative;
      margin: 0 auto;
  }
  #progressBar {
      position: absolute;
      left: 0;
      top: 0;
      z-index: 2;
      height: 40px;
      width: 100%;
      line-height: 40px;
      color: white;
      text-align: center;
      font-size: 20px;
      font-weight: bold;
      font-family: Georgia;
      clip: rect(0px, 0, 40px, 0px);
      background: #00A1F5;
  }
  #progressText {
      position: absolute;
      left: 0;
      top: 0;
      z-index: 1;
      width: 100%;
      height: 40px;
      line-height: 40px;
      color: black;
      text-align: center;
      font-size: 20px;
      font-weight: bold;
      font-family: Georgia;
  }
</style>
<style lang="scss" scoped>
/deep/.ant-form-item-label{
  height: 40px;
  &>label{
    display: inline-block;
    // width: 98%;
    max-width: 12em;
    overflow: hidden;
  }
}
</style>