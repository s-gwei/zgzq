<template>
    <div class="xncsWrapper">
        <a-card style="height: 100%">
            <a-row :gutter="12" style="height: 100%">
                <a-col :md="6" :sm="24" style="height: 100%">
                    <a-card>
                        <div class="title">模板列表</div>
                        <div class="list">
                            <div v-for="(item,index) in data" :key="index" class="mbDetail"><span>{{item.title}}</span><a-button type="primary" size="small" @click="geneInstance(item,index)">生成实例</a-button></div>
                        </div>
                    </a-card>
                </a-col>
                <a-col :md="18" :sm="24" style="height: 100%">
                    <a-card>
                        <div class="plan" v-if="!btnVisible">
                            <div class="planParameter">当前计划不支持的性能参数</div>
                            <div class="planList">
                                <div class="planDetail" v-for="(item,index) in parameter" :key="index">{{item}}</div>
                            </div>
                        </div>
                        <div v-else>
                            <a-button type="primary" @click="admainPlanFrom">生成本计划参数表</a-button>
                        </div>
                    </a-card>
                </a-col>
            </a-row>
        </a-card>
    </div>
</template>
<script>
export default {
    name: "sncsList",
    data(){
        return {
            data: [
                {id: '1', title: "模板模板1"},
                {id: '2', title: "模板模板2"},
                {id: '3', title: "模板模板3"},
                {id: '4', title: "模板模板4"},
            ],
            parameter: [],
            planIdObj: {},
            btnVisible: false
        }
    },
    mounted(){
        document.querySelector(".xncsWrapper").style.height = document.querySelector("#app").offsetHeight + "px"
    },
    methods: {
        geneInstance(item,index){
            // console.log(item);
            // this.$confirm({
            //   title:"生成实例",
            //   content:"是否确认生成实例",
            //   onOk: function(){
            //      console.log('ok');
            //   }
            // });
           if(index % 2 == 0)  {this.parameter = ["参数1","参数2","参数3","参数4","参数5","参数6","参数7","参数8"], this.btnVisible = false}
           else{
               this.parameter = [],
               this.planIdObj = item
               this.btnVisible = true
           }
        },
        admainPlanFrom(){
            console.log(this.planIdObj);
        }

    }
    
}
</script>
<style lang="scss" scoped>
/deep/.ant-card-body,.ant-card{
    height: 100%;
    .title{
        font-weight: 700;
        font-size: 18px;
        // text-align: center;
        margin-bottom: 8px;
        padding-left: 10px;
    }
    .list{
        padding: 0 10px;
        // display: flex;
        // flex-direction: column;
        // align-items: center;
        .mbDetail{
          padding: 4px 20px;
          padding-bottom: 8px;   
          cursor: pointer;    
          &>span{
              height: 24px;
             line-height: 24px;
             padding-right: 20px;
          }
          button{
              display: none;
              opacity: 0;
          }
          &:hover{
              border-bottom: 1px solid rgba(231, 218, 218,.4);
              button{
                  display: inline-block;
                  opacity: 1;
                  transition: all .3s;
              }
          }
        }
    }
    .plan{
        .planParameter{
            font-weight: 700;
        }
        .planList{
            margin-top: 20px;
            padding-left: 20px;
             .planDetail{
                 display: inline-block;
                 padding: 4px 8px;
                 border: 1px solid rgba(231, 218, 218,.6);
                 border-radius: 4px;
                 margin-right: 10px
             }
         }
    }
}
</style>