<template>
   <div class="risktable">
      <div  class="tableContent" style="height:100%;" v-if="!loading && iNow >=99">
        <a-row type="flex" :gutter="8" style="height:100%;">
          <a-col :md="4" :sm="24" style="height: 100%">
            <table-left v-bind:treeDataSource="treeDataSource" v-model="currentVal"/>
          </a-col>
          <a-col :md="14" :sm="24" style="height: 100%">
            <table-center v-model="currentVal"/>
          </a-col>
          <a-col :md="6" :sm="24" style="height: 100%">
            <table-right v-model="currentVal"/>
          </a-col>
        </a-row>
      </div>
      <div  style="background: '#f6f6f6;height: 50%" class="loadingBox" v-else>
         <div  id="progressBox" >
            <div id="progressBar">0%</div>
            <div id="progressText">0%</div>
         </div>
      </div>
   </div>
</template>

<script>
  import TableLeft from './modules/TableLeft'
  import TableRight from './modules/TableRight'
  import TableCenter from './modules/TableCenter'
  const treeDataSource = require('@assets/json/treeDataSource.json')
  export default {
    name: 'RiskTable',
    components: { TableLeft, TableRight, TableCenter },
    data() {
      return {
          currentVal: "",
          treeDataSource: [],
          loading: true,
          iNow: 0
      }
    },
    created(){
      this.$nextTick(function(){
        this.load()
      })
    },
    watch: {
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
      // this.load()
      this.getData()
      document.querySelector(".risktable").style.height = document.querySelector("#app").offsetHeight + "px"
      window.onresize = () => {
          return (() => {
             document.querySelector(".risktable").style.height = document.querySelector("#app").offsetHeight + "px"
          })();
       };
    },
    methods: {
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
            },
      getData(){
        const _this = this
        setTimeout(function(){
          _this.loading = !_this.loading
          // console.log(_this.laodng);
          _this.treeDataSource =  JSON.parse(JSON.stringify(treeDataSource))
        },1000)
      }
    }
  }
</script>
<style scoped lang="scss">
  @import '~@assets/less/common.less';
  /deep/.ant-card-body{
    height: 100%;
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
    width: 9em;
    overflow: hidden;
  }
}
</style>