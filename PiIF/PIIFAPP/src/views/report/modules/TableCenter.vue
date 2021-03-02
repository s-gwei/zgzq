<template>
    <a-card  style="background: '#fff';height: 100%;overflow:auto;" >
        <div class="content" v-if="value && value.length">
            <template v-for="(item,index) in value">
                <part-forms :item = "item" :key="index"></part-forms>
            </template>
        </div>
        <div class="centerBtn">
           <a-button type="primary" @click="save">保存</a-button>
        </div>
    </a-card>
</template>

<script>
  import PartForms from "./PartForms"
  export default {
    name: "TableCenter",
    props: ['value'],
    components: {PartForms},
    data () {
      return {
        loading: false,
        rawData: [],
        changesData: [],
        // table
        columns: [
          {
            title: '成员姓名',
            dataIndex: 'name',
            key: 'name',
            width: '20%',
            scopedSlots: { customRender: 'name' }
          },
          {
            title: '工号',
            dataIndex: 'workId',
            key: 'workId',
            width: '20%',
            scopedSlots: { customRender: 'workId' }
          },
          {
            title: '所属部门',
            dataIndex: 'department',
            key: 'department',
            width: '40%',
            scopedSlots: { customRender: 'department' }
          },
          {
            title: '操作',
            key: 'action',
            scopedSlots: { customRender: 'operation' }
          }
        ],
        data: [
          {
            key: '1',
            name: '小明',
            workId: '001',
            editable: false,
            department: '行政部'
          },
          {
            key: '2',
            name: '李莉',
            workId: '002',
            editable: false,
            department: 'IT部'
          },
          {
            key: '3',
            name: '王小帅',
            workId: '003',
            editable: false,
            department: '财务部'
          }
        ]
      }
    },
    watch: {
    },
    methods: {
        save(){
          console.log(this.changesData);
        },
        
    },
    mounted(){
      const _this = this
      this.$bus.$on('changes',function(val){
        var isFlag = true
        if(_this.changesData.length){
          _this.changesData.forEach(function(item,index){
              if(item.id == val.id){
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
/deep/.ant-row .ant-col-lg-8{
  // line-height: 1;
  // height: 40px;
}
.content{
  padding-bottom: 10px;
  min-height: calc(100% - 30px);
}
.centerBtn{
  // // position: absolute;
  // bottom: 10px;
  // right: 10px;
  float: right;
  margin-bottom: 10px;
}
</style>