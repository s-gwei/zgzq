<template>
   <div class="risktable">
      <div  class="tableContent" style="height:100%;"  v-show="!loading && iNow >=99">
      <!-- <div  class="tableContent" style="height:100%;"> -->
        <a-row type="flex" :gutter="8" style="height:100%;">
          <a-col :md="4" :sm="24" style="height: 100%" class="pageLeft">
            <table-left v-bind:treeDataSource="treeDataSource"  v-model="currentVal" @fuzzySearch="fuzzySearch" @getLoadingHide="getLoadingHide" ref="child" />
          </a-col>
          <a-col :md="14" :sm="24" style="height: 100%" v-show="$store.state.report.centerAndRightShow">
            <table-center @getCurrentArig="getCurrentArig"  @getLoadingHide="getLoadingHide"/>
          </a-col>
          <a-col :md="6" :sm="24" style="height: 100%" v-show="$store.state.report.centerAndRightShow"> 
            <table-right @getCurrentArig="getCurrentArig"/>
          </a-col>
          <div class='load-container' id="loading">
            <div class='loader'></div>
        </div>
        </a-row>
      </div>
      <div  style="background: '#f6f6f6;height: 50%" class="loadingBox" v-if="loading || iNow < 99" >
         <div  id="progressBox" >
            <div id="progressBar">0%</div>
            <div id="progressText">0%</div>
         </div>
         <div class="notice">受集团服务器协议影响,跨系统传输文件,加载较慢,请稍等</div>
      </div>
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
      this.load()
      this.$nextTick(function(){
          this.$store.commit("currentVal", this.$router)
      })
      this.$store.commit("btnEditable", localStorage.getItem("btnEditable"))
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
        },
      iNow(val){
        if(val>= 99){
          this.iNow = 99
        }
      },
      loading(val){
          if(!val){
             const _this = this
            const times = setInterval(function(){
                 if(_this.iNow <99){
                    _this.iNow += 2
                    _this.progressFn(_this.iNow)
                 } else {
                   clearInterval(times)
                 }
               },100)
            }
      }
    },
    mounted(){
      document.getElementById("loading").style.display="none";
      // this.$store.commit('currentTotalCarPartNumber',this.$route.params.totalCarPartNumber || this.$route.query.totalCarPartNumber || "")
       this.$store.commit('currentTotalCarPartNumber',localStorage.getItem("totalCarPartNumber") || "" )
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
        // params.totalCarPartNumber = this.$route.params.totalCarPartNumber || this.$route.query.totalCarPartNumber
        params.totalCarPartNumber = localStorage.getItem("totalCarPartNumber") 
        params.lifeCycle = "EDIT"
        // params.baselineNumber = 1
        getAction(url,params,'get').then((res) => {
             if(res.success){
               _this.loading = false
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
               console.log(_this.treeDataSource);
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
      },
      getLoadingHide(v){
          document.getElementById("loading").style.display=v;
      },
      load(){
          //  var _this.iNow = 0;
            // 设定定时器
            const _this = this
            var timer = setInterval(function () {
                // 如果当前的值为100
                if (_this.iNow == 60 ) {
                    // 清除定时器
                    clearInterval(timer);
                }
                if(_this.iNow < 60) {
                    // 将当前状态值累加1
                    _this.iNow += 4;
                    // 调用执行状态的函数,传入状态值
                    _this.loading && _this.progressFn(_this.iNow);
                }
            }, 60);
            var timer2 = setInterval(function(){
                  if(_this.iNow == 70){
                    clearInterval(timer2);
                  }  
                  if(_this.iNow >= 60 && _this.iNow < 70) {
                    _this.iNow += 2;
                    _this.loading && _this.progressFn(_this.iNow);
                  }
            },250)
            var timer3 = setInterval(function(){
                  if(_this.iNow == 93){
                    // clearInterval(timer3);
                  } 
                  if(_this.iNow >= 70 && _this.iNow < 93){
                    _this.iNow += 1;
                    _this.loading && _this.progressFn(_this.iNow);
                  }
            },80)
             var timer4 = setInterval(function(){
                  if(_this.iNow == 99){
                       clearInterval(timer4);
                  } 
                  if(_this.loading){
                     if(_this.iNow >= 93 && _this.iNow < 99){
                       var n = parseInt(3*Math.random())+ 1
                       if(_this.iNow % 2 == 0 ){
                         _this.iNow = _this.iNow - (2*n+1)
                       } else{
                         console.log(_this.iNow,n);
                         _this.iNow = _this.iNow +  (2*n-1) == 100 ? 98 : _this.iNow +  (2*n-1)
                       }
                        _this.progressFn(_this.iNow);
                     }
                  }
            },500)
      },
      progressFn(cent) {
                // 获取最外层的div
                var oDiv1 = document.getElementById('progressBox');
                // 获取内层进度条的div
                var oDiv2 = document.getElementById('progressBar');
                // 获取内层文字发生变化时的div
                var oDiv3 = document.getElementById('progressText');
                // 获取总进度条的宽度
                var allWidth = parseInt(getStyle(oDiv1, 'width'));
 
                // 设定内层两个div的文字内容一样
                oDiv2.innerHTML = cent + '%';
                oDiv3.innerHTML = cent + '%';
 
                // 修改clip的的宽度值
                oDiv2.style.clip = 'rect(0px, ' + cent / 100 * allWidth + 'px, 40px, 0px)';
 
                // 获取当前元素的属性值
               function getStyle(obj, attr) {
                    // 兼容IE
                    if (obj.currentStyle) {
                        return obj.currentStyle[attr];
                    }else {
                        // 第二个参数为false是通用的写法，目的是为了兼容老版本
                        return getComputedStyle(obj, false)[attr];
                    }
                }
      }
    }
  }
</script>
<style scoped lang="scss">
  @import '~@assets/less/common.less';
  /deep/.ant-card-body{
    height: 100%;
  }
  .notice{
    text-align: center;
    font-size: 20px;
    color: rgb(255, 8, 0);
    margin-top: 10px;
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
    .load-container {
        background-color: #fff;
        width: auto;
        position: relative;
        overflow: hidden;
        -moz-box-sizing: border-box;
        box-sizing: border-box;
     }
     
#loading {
	position: fixed;
	top: 0;
	left: 0;
	width: 100%;
	height: 100%;
	background: rgba(0, 0, 0, 0.7);
	z-index: 15000;
}
   #loading  .loader  {
	     position: absolute;
        font-size: 14px;
        // margin-left: 42%;
        top: 50%;
        left: 50%;
        transform: translate(-50%,-50%);
        width: 1em;
        height: 1em;
        border-radius: 50%;
        // text-indent: -9999em;
        position: relative;
        animation: mymove 1.5s infinite linear;
        -webkit-animation: mymove 1.5s infinite linear;
        -webkit-transform: translateZ(0);
        -ms-transform: translateZ(0);
        transform: translateZ(0);
     }
     .loader::before{
        content: '加载中';
        display: block;
        color: #fff;
        font-size: 14px;
        width: 45px;
        left: -15px;
        position: absolute;
    }
    @keyframes mymove {
        0%,
        100% {
            box-shadow: 0 -3em 0 0.2em #DEDEDE, 2em -2em 0 0em #DEDEDE, 3em 0 0 -1em #DEDEDE, 2em 2em 0 -1em #DEDEDE, 0 3em 0 -1em #DEDEDE, -2em 2em 0 -1em #DEDEDE, -3em 0 0 -1em #DEDEDE, -2em -2em 0 0 #DEDEDE;
        }
        12.5% {
            box-shadow: 0 -3em 0 0 #DEDEDE, 2em -2em 0 0.2em #DEDEDE, 3em 0 0 0 #DEDEDE, 2em 2em 0 -1em #DEDEDE, 0 3em 0 -1em #DEDEDE, -2em 2em 0 -1em #DEDEDE, -3em 0 0 -1em #DEDEDE, -2em -2em 0 -1em #DEDEDE;
        }
        25% {
            box-shadow: 0 -3em 0 -0.5em #DEDEDE, 2em -2em 0 0 #DEDEDE, 3em 0 0 0.2em #DEDEDE, 2em 2em 0 0 #DEDEDE, 0 3em 0 -1em #DEDEDE, -2em 2em 0 -1em #DEDEDE, -3em 0 0 -1em #DEDEDE, -2em -2em 0 -1em #DEDEDE;
        }
        37.5% {
            box-shadow: 0 -3em 0 -1em #DEDEDE, 2em -2em 0 -1em #DEDEDE, 3em 0em 0 0 #DEDEDE, 2em 2em 0 0.2em #DEDEDE, 0 3em 0 0em #DEDEDE, -2em 2em 0 -1em #DEDEDE, -3em 0em 0 -1em #DEDEDE, -2em -2em 0 -1em #DEDEDE;
        }
        50% {
            box-shadow: 0 -3em 0 -1em #DEDEDE, 2em -2em 0 -1em #DEDEDE, 3em 0 0 -1em #DEDEDE, 2em 2em 0 0em #DEDEDE, 0 3em 0 0.2em #DEDEDE, -2em 2em 0 0 #DEDEDE, -3em 0em 0 -1em #DEDEDE, -2em -2em 0 -1em #DEDEDE;
        }
        62.5% {
            box-shadow: 0 -3em 0 -1em #DEDEDE, 2em -2em 0 -1em #DEDEDE, 3em 0 0 -1em #DEDEDE, 2em 2em 0 -1em #DEDEDE, 0 3em 0 0 #DEDEDE, -2em 2em 0 0.2em #DEDEDE, -3em 0 0 0 #DEDEDE, -2em -2em 0 -1em #DEDEDE;
        }
        75% {
            box-shadow: 0em -3em 0 -1em #DEDEDE, 2em -2em 0 -1em #DEDEDE, 3em 0em 0 -1em #DEDEDE, 2em 2em 0 -1em #DEDEDE, 0 3em 0 -1em #DEDEDE, -2em 2em 0 0 #DEDEDE, -3em 0em 0 0.2em #DEDEDE, -2em -2em 0 0 #DEDEDE;
        }
        87.5% {
            box-shadow: 0em -3em 0 0 #DEDEDE, 2em -2em 0 -1em #DEDEDE, 3em 0 0 -1em #DEDEDE, 2em 2em 0 -1em #DEDEDE, 0 3em 0 -1em #DEDEDE, -2em 2em 0 0 #DEDEDE, -3em 0em 0 0 #DEDEDE, -2em -2em 0 0.2em #DEDEDE;
        }
    }

    @-webkit-keyframes mymove
    /*Safari and Chrome*/
    {
        0%,
        100% {
            box-shadow: 0 -3em 0 0.2em #DEDEDE, 2em -2em 0 0em #DEDEDE, 3em 0 0 -1em #DEDEDE, 2em 2em 0 -1em #DEDEDE, 0 3em 0 -1em #DEDEDE, -2em 2em 0 -1em #DEDEDE, -3em 0 0 -1em #DEDEDE, -2em -2em 0 0 #DEDEDE;
        }
        12.5% {
            box-shadow: 0 -3em 0 0 #DEDEDE, 2em -2em 0 0.2em #DEDEDE, 3em 0 0 0 #DEDEDE, 2em 2em 0 -1em #DEDEDE, 0 3em 0 -1em #DEDEDE, -2em 2em 0 -1em #DEDEDE, -3em 0 0 -1em #DEDEDE, -2em -2em 0 -1em #DEDEDE;
        }
        25% {
            box-shadow: 0 -3em 0 -0.5em #DEDEDE, 2em -2em 0 0 #DEDEDE, 3em 0 0 0.2em #DEDEDE, 2em 2em 0 0 #DEDEDE, 0 3em 0 -1em #DEDEDE, -2em 2em 0 -1em #DEDEDE, -3em 0 0 -1em #DEDEDE, -2em -2em 0 -1em #DEDEDE;
        }
        37.5% {
            box-shadow: 0 -3em 0 -1em #DEDEDE, 2em -2em 0 -1em #DEDEDE, 3em 0em 0 0 #DEDEDE, 2em 2em 0 0.2em #DEDEDE, 0 3em 0 0em #DEDEDE, -2em 2em 0 -1em #DEDEDE, -3em 0em 0 -1em #DEDEDE, -2em -2em 0 -1em #DEDEDE;
        }
        50% {
            box-shadow: 0 -3em 0 -1em #DEDEDE, 2em -2em 0 -1em #DEDEDE, 3em 0 0 -1em #DEDEDE, 2em 2em 0 0em #DEDEDE, 0 3em 0 0.2em #DEDEDE, -2em 2em 0 0 #DEDEDE, -3em 0em 0 -1em #DEDEDE, -2em -2em 0 -1em #DEDEDE;
        }
        62.5% {
            box-shadow: 0 -3em 0 -1em #DEDEDE, 2em -2em 0 -1em #DEDEDE, 3em 0 0 -1em #DEDEDE, 2em 2em 0 -1em #DEDEDE, 0 3em 0 0 #DEDEDE, -2em 2em 0 0.2em #DEDEDE, -3em 0 0 0 #DEDEDE, -2em -2em 0 -1em #DEDEDE;
        }
        75% {
            box-shadow: 0em -3em 0 -1em #DEDEDE, 2em -2em 0 -1em #DEDEDE, 3em 0em 0 -1em #DEDEDE, 2em 2em 0 -1em #DEDEDE, 0 3em 0 -1em #DEDEDE, -2em 2em 0 0 #DEDEDE, -3em 0em 0 0.2em #DEDEDE, -2em -2em 0 0 #DEDEDE;
        }
        87.5% {
            box-shadow: 0em -3em 0 0 #DEDEDE, 2em -2em 0 -1em #DEDEDE, 3em 0 0 -1em #DEDEDE, 2em 2em 0 -1em #DEDEDE, 0 3em 0 -1em #DEDEDE, -2em 2em 0 0 #DEDEDE, -3em 0em 0 0 #DEDEDE, -2em -2em 0 0.2em #DEDEDE;
        }
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