<template>
    <a-card  style="background: '#fff';height: 100%;overflow:auto;" >
        <div class="currentBb" :class="$store.state.report.currentEditionVal=='基线版' ? '' : 'baseline'">
          <a-button type="primary" style="marginRight: 12px" @click="exitBaseline" v-show="$store.state.report.currentEditionVal=='基线版'">退出基线视图</a-button>当前版本: <span> {{$store.state.report.currentEditionVal}}</span></div>
        <div class="content" v-if="data && data.length">
            <template v-for="(item,index) in data">
                <part-forms :item ="item" :key="index"></part-forms>
            </template>
            <div v-show="isTable">
               <part-forms :item ="tableLayout" :key="99999"></part-forms>
            </div>
        </div>
        <div class="centerBtn" v-if="$store.state.report.btnEditable && $store.state.report.isClisked">
           <a-button type="primary" @click="save" :disabled="saveDisabled">保存</a-button>
        </div>
    </a-card>
</template>

<script>
 import {getAction} from '@/api/manage';
  import PartForms from "./PartForms"
  export default {
    name: "TableCenter",
    props: ['value'],
    components: {PartForms},
    data () {
      return {
        saveDisabled: false,//保存点击之后禁点
        isTable: false,//是否存在table布局
        tableLayout: {},
        data:[],
        currentBb: "",
        loading: false,
        rawData: [],
        changesData: [],
        currentAttribute: [], //当前页面所有的字段内容
        // table
        jkUrl: {
          modifyPartInfo :"/jeecg-boot/pdmReport/modifyPartInfo",
        }
      }
    },
    watch: {
      "changesData": {
        handler(val){
          // console.log(this.currentAttribute,val);
          this.$store.commit("isClisked", true)
          const _this =this
          const changesData = val.filter(function (itm) {
          // console.log(_this.currentAttribute,itm.field);
              return _this.currentAttribute.indexOf(itm.field) > -1
          })
           const paramsVal = []
          if(val.length){
              changesData.forEach(function (item,index) {
                paramsVal.push({
                    name : item.field,
                    value: Array.isArray(item.value) ? item.value.toString() : item.value
                })
             })
            //  console.log(changesData,paramsVal);
             _this.$store.commit("currentCenterChange", paramsVal)
          }
          // console.log(paramsVal);
        },
        deep:true
      }
    },
    methods: {
        exitBaseline(){
          this.$store.state.report.currentEditionVal ="最新版"
          // this.$parent.getData();
          this.$emit('getCurrentArig');
        },
         getLoadingHide(v){
          this.$emit('getLoadingHide',v)
        },
        save(){
            const _this = this,url = this.jkUrl.modifyPartInfo
            const params = {}
            this.saveDisabled = true
            params.param = this.$store.state.report.infoParams
            params.partNumber = this.$store.state.report.infoParams.partNumber
            params.totalCarNumber = this.$store.state.report.infoParams.parentNumber
             this.getLoadingHide('block')
            // console.log(params);
            getAction(url,params,'get').then((res) => {
              _this.getLoadingHide('none')
            // 保存成功发布按钮可点击
              if(res.success){
                 _this.saveDisabled = false
                _this.$store.commit("isClisked", false)
                _this.$message.success("保存成功,可发布")
                _this.$store.commit("changePublishBtnState", false)
                _this.$store.state.report.currentEditionVal = "最新版"
                _this.$emit('getCurrentArig')
              }else{
                _this.saveDisabled = false
                _this.$message.error(res.message || '保存失败')
              }

            })
          // this.changesData = this.changesData.filter(function (itm) {
          //     return _this.currentAttribute.indexOf(itm.field) > -1
          // })
          // console.log(this.changesData);
          // const paramsVal = []
          // if(this.changesData.length){
          //     this.changesData.forEach(function (item,index) {
          //       paramsVal.push({
          //           name : item.name,
          //           value: Array.isArray(item.value) ? item.value.toString() : item.value
          //       })
          //    })
          //    this.$store.commit("currentCenterChange", paramsVal)
          // }
        },
    },
    mounted(){
      const _this = this
      this.$bus.$on("currentValNew", function(v){
        _this.$set(_this,"data",v)
      })
      this.$bus.$on('currentValue',function(val){
         if(val.val.children && val.val.children.length){
           _this.isTable = true
           const type = val.val.children[0].Type
           _this.$store.commit("currentTableType", type)
          //  _this.tableLayout = _this.$store.state.report.docVal[type]
           _this.$set(_this,"tableLayout",_this.$store.state.report.docVal[type])

         } else{
           _this.isTable = false
         }
              _this.$set(_this,"data",[])
            //  _this.$set(_this,"currentAttribute",val)
            // _this.addValueToTree(val.val.classification,val.data.children)
             _this.$set(_this,"currentAttribute",[])
             for(var key in val.val.classification){
               _this.currentAttribute.push(key)
            }
      })
      this.$bus.$on('changes',function(val){
        // console.log(val,_this.changesData);
        var isFlag = true
        if(_this.changesData.length){
          _this.changesData.forEach(function(item,index){
              if(item.field == val.field){
                isFlag = false
                _this.changesData.splice(index,1,val)
                return
              }
          })
          if(isFlag){
             _this.changesData.push(val)
          }
        } else{
          _this.changesData.push(val)
        }
        // console.log(_this.changesData);
      })
    }
  }
</script>

<style lang="scss" scoped>
.currentBb{
  font-size: 16px;
  font-weight: normal;
  margin-left: 70%;
  span{
    font-weight: bold;
    color: indianred;
  }
}
.baseline{
  margin-left: 84%;
}
/deep/.ant-row .ant-col-lg-8{
  // line-height: 1;
  // height: 40px;
}
.content{
  padding-bottom: 10px;
  min-height: calc(100% - 60px);
}
.centerBtn{
  // // position: absolute;
  // bottom: 10px;
  // right: 10px;
  // float: right;
  margin-bottom: 10px;
  text-align: center;
}
/deep/.ant-card-body{
    padding: 12px;
  }
</style>