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
        :expandedKeys.sync="expandedKeys"
        :selectedKeys="selectedKeys"
        :treeData="treeDataSource"
        @select="handleTreeSelect"
      >
      </a-tree>
  </a-card>
</template>

<script>
  // import { queryDepartTreeList, searchByKeywords } from '@/api/api'

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
        loading: false,
        isClear: false,
        name: "",
        autoExpandParent: false,
        selectedKeys: [],
        expandedKeys: ["1"],
        chckedVal: []
      }
    },
    watch: {
       treeDataSource: {
          handler(){
              this.queryTreeData(this.treeDataSource,true)
          }
       },
       name(val){
         if(val && val.length){
            this.isClear = true
         }
       }
    },
    mounted() {
      this.queryTreeData(this.treeDataSource,true)
    },
    methods: {
      // 模糊搜索
      onSearch(){},
      clear1(){
        this.name = ""
        this.isClear = false
      },
      handleTreeSelect(selectedKeys,event) {
        if(selectedKeys && !selectedKeys.length) return
        const _this = this
         this.$confirm({
            title:"确认跳转",
            content:"您确保是否已保存",
            onOk: function(){
               console.log('确认');
               if (selectedKeys[0] == _this.treeDataSource[0].id) return;
                 _this.chckedVal=[]
                 if (selectedKeys.length > 0 && _this.selectedKeys[0] !== selectedKeys[0]) {
                   _this.selectedKeys = [selectedKeys[0]]
                   let orgCode = event.node.dataRef
                   const val = event.node.dataRef.children1 ? _this.getUnderLevel(orgCode) : [event.node.dataRef]
                   _this.emitInput(val)
                 }
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
            // 默认选中第一条数据、默认展开所有
            if (data.length > 0) {
              data.forEach((item, index) => {
                if (isFlag && index === 0) {
                  const selectedNodeKeys = [item.children && item.children[0].id || '']
                  this.$set(this,"selectedKeys",selectedNodeKeys)
                  item.children && item.children[0].children1 && this.emitInput(item.children[0].children1)
                }
                this.expandedKeys.push(item.id)
                if(item.children1 && item.children1.length){
                   this.queryTreeData(item.children1,false)
                } 
              })
            }
      },

    }
  }
</script>

<style scoped lang="scss">
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

</style>