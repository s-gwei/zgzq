<template>
    <a-card  style="background: '#fff';height: 100%;overflow:auto;" >
        <div class="currentBb">当前版本: <span> {{$store.state.report.currentEditionVal}}</span></div>
        <div class="content" v-if="data && data.length">
            <template v-for="(item,index) in data">
                <part-forms :item = "item" :key="index"></part-forms>
            </template>
        </div>
        <div class="centerBtn">
           <a-button type="primary" @click="save">保存</a-button>
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
        data:[],
        currentBb: "",
        loading: false,
        rawData: [],
        changesData: [],
        currentAttribute: [], //当前页面所有的字段内容
        // table
        jkUrl: {
          modifyPartInfo :"/pdmReport/modifyPartInfo",
        }
      }
    },
    watch: {
      "changesData": {
        handler(val){
          console.log(this.currentAttribute,val);
          const _this =this
          const changesData = val.filter(function (itm) {
              return _this.currentAttribute.indexOf(itm.field) > -1
          })
           const paramsVal = []
          if(val.length){
              changesData.forEach(function (item,index) {
                paramsVal.push({
                    name : item.name,
                    value: Array.isArray(item.value) ? item.value.toString() : item.value
                })
             })
             _this.$store.commit("currentCenterChange", paramsVal)
          }
          // console.log(paramsVal);
        },
        deep:true
      }
    },
    methods: {
        save(){
            const _this = this,url = this.jkUrl.modifyPartInfo
            const params = {}
            params.param = this.$store.state.report.infoParams
            params.partNumber = this.$store.state.report.infoParams.partNumber
            params.totalCarNumber = this.$store.state.report.infoParams.parentNumber
            console.log(params);
            getAction(url,params,'get').then((res) => {
            // 保存成功发布按钮可点击
              if(res.success){
                _this.$message.success("保存成功,可发布")
                _this.$store.commit("changePublishBtnState", false)
                _this.$emit('getCurrentArig')
              }else{
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
              _this.$set(_this,"data",[])
            //  _this.$set(_this,"currentAttribute",val)
            // _this.addValueToTree(val.val.classification,val.data.children)
             _this.$set(_this,"currentAttribute",[])
             for(var key in val.val.classification){
               _this.currentAttribute.push(key)
            }
      })
      this.$bus.$on('changes',function(val){
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
      })
    }
  }
</script>

<style lang="scss" scoped>
.currentBb{
  font-size: 16px;
  font-weight: normal;
  margin-left: 85%;
  span{
    font-weight: bold;
    color: indianred;
  }
}
/deep/.ant-row .ant-col-lg-8{
  // line-height: 1;
  // height: 40px;
}
.content{
  padding-bottom: 10px;
  min-height: calc(100% - 54px);
}
.centerBtn{
  // // position: absolute;
  // bottom: 10px;
  // right: 10px;
  float: right;
  margin-bottom: 10px;
}
</style>