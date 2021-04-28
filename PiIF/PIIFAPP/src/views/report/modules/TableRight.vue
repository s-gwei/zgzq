<template>
    <div class="rightWrapper">
      <a-row>
         <a-card style="height: 100%">
             <list-influence :title="'源总成'" :influenceData="affectLink" @getCurrentArig="getCurrentArig"></list-influence>
         </a-card>
      </a-row>
      <a-row>
        <a-card style="height: 100%">
           <list-influence :title="'被约束总成'" :influenceData="constraintLink" @getCurrentArig="getCurrentArig"></list-influence>
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
        val.val.affectLink.map(function(item,index){
          item.key = index+1
        })
        val.val.constraintLink.map(function(item,index){
          item.key = index+1
        })
        _this.$set(_this,"affectLink",val.val.affectLink)
        _this.$set(_this,"constraintLink",val.val.constraintLink)
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