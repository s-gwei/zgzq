<template>
    <div class="rightWrapper">
      <a-row>
         <a-card style="height: 100%">
             <list-influence :title="'源总成'"></list-influence>
         </a-card>
      </a-row>
      <a-row>
        <a-card style="height: 100%">
           <list-influence :title="'被约束总成'"></list-influence>
        </a-card>
      </a-row>
    </div>
</template>

<script>
  // import ListInfluence from './ListInfluence'
  import {ListInfluence} from '../modules/components'
// import ListInfluence from './components/ListInfluence.vue'
  export default {
    name: "TableRight",
    components: {ListInfluence},
    props: ['value'],
    data () {
      return {
      }
    },
    watch: {
        value(val){
            // console.log(val,'right');
        }
    },
    methods: {
      
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