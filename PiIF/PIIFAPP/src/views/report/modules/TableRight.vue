<template>
    <div class="rightWrapper">
      <a-row>
         <a-card style="height: 100%">
             <list-influence :title="'源总成'" :influenceData="affectLink" @getCurrentArig="getCurrentArig" class="affectLink"></list-influence>
         </a-card>
      </a-row>
      <a-row>
        <a-card style="height: 100%">
           <list-influence :title="'被约束总成'" :influenceData="constraintLink" @getCurrentArig="getCurrentArig" class="constraintLink"></list-influence>
        </a-card>
      </a-row>
    </div>
</template>

<script>
  import {ListInfluence} from '../modules/components'
  export default {
    name: "TableRight",
    components: {ListInfluence},
    props: ['value'],
    data () {
      return {
        affectLink: [],
        constraintLink: [],
        index: 1
      }
    },
    watch: {
        value(val){
            // console.log(val,'right');
        }
    },
    mounted(){
      const _this = this
      this.$bus.$on('currentValue',function(val){
        _this.index ++
        val.val.affectLink && val.val.affectLink.map(function(item,index){
          item.key = index+1 + "top"
        })
        val.val.constraintLink && val.val.constraintLink.map(function(item,index){
          item.key = index+1 + "bottom"
        })
        
        _this.$set(_this,"affectLink",val.val.affectLink)
        _this.$set(_this,"constraintLink",val.val.constraintLink)
        _this.$store.commit("selectedRowKeys",[])
        _this.$store.commit("selectedRows",[])
      })
    },
    methods: {
      getCurrentArig(){
        this.$emit('getCurrentArig')
      }
    }
  }
</script>

<style lang="scss" scoped>
  .rightWrapper{
    height: 100%;
    
  }
  /deep/.ant-row{
    height: 49.5%;
    &:nth-child(1){
      margin-bottom: 2%;
    }    
  }
  /deep/.ant-card-body{
    padding: 10px;
  }
</style>