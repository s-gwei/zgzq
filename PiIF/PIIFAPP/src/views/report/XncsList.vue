<template>
    <div class="xncsWrapper">
        <a-card style="height: 100%">
            <a-row :gutter="12" style="height: 100%">
                <a-col :md="6" :sm="24" style="height: 100%">
                    <a-card>
                        <div class="title">模板列表</div>
                        <div class="list">
                            <div v-for="(item,index) in paramsData" :key="index" class="mbDetail"><span>{{item.partName}}</span><a-button type="primary" size="small" @click="geneInstance(item,index)">生成实例</a-button></div>
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
import {getAction} from '@/api/manage';
export default {
    name: "sncsList",
    data(){
        return {
            paramsData: [
                // {partNumber: '1', partName: "模板模板1"},
                // {partNumber: '2', partName: "模板模板2"},
                // {partNumber: '3', partName: "模板模板3"},
                // {partNumber: '4', partName: "模板模板4"},
            ],
            parameter: [],
            planIdObj: {},
            btnVisible: false,
            requesturl: {
                list: "/pdmReport/findAllTemplateByLifeAndLevel",
                judgeTemplateUseful: "/pdmReport/judgeTemplateUseful",
                generateInstanceByTemplateCode: "/pdmReport/generateInstanceByTemplateCode"
            },
        }
    },
    mounted(){
        document.querySelector(".xncsWrapper").style.height = document.querySelector("#app").offsetHeight + "px"
        this.getParamsList()
    },
    methods: {
        getParamsList(){
            const _this = this,url = this.requesturl.list,params={}
            // params.level = this.$route.query.level || "整车"
            params.level = "整车"
            // params.lifeCycle = this.$route.query.lifeCycle || 1
            params.lifeCycle = "INWORK"
            getAction(url,params,'get').then((res) => {
                if(res.success && res.result){
                  // _this.tableDate = res.result
                  _this.$set(_this,"paramsData",res.result)
                } else {
                  // _this.$nextTick(function(){
                  //      _this.renderChart(data)
                  //  })
                }
             })
        },
        geneInstance(item,index){
             const _this = this,url = this.requesturl.judgeTemplateUseful,params={}
             params.stList = JSON.stringify(this.$route.query.stList.split(","))
             params.state = this.$route.query.state //st集合
             params.templatePartNumber = item.partNumber //模板编码
            // this.$confirm({
            //   title:"生成实例",
            //   content:"是否确认生成实例",
            //   onOk: function(){
            //      console.log('ok');
            //   }
            // });

            getAction(url,params,'get').then((res)=>{
                if(res.success){
                    _this.parameter = [],
                    _this.planIdObj = item
                    _this.btnVisible = true

                } else{
                    _this.$set(_this,"parameter",res.result)
                }
            })
        },
        admainPlanFrom(){
            // console.log(this.planIdObj);
            const _this = this,url = this.requesturl.generateInstanceByTemplateCode,params={}
            params.templatePartNumber = this.planIdObj.partNumber
            params.lifeCycle = "INWORK"
            getAction(url,params).then((res)=>{
                console.log(res);
                if (res.success) {
                //   this.projects = res.result.records
                const params = {totalCarPartNumber: res.result || "AZ00000001"}
                  this.$router.push({name: "riskTable",params});
                } else {
                  this.$message.error(res.message);
                }
            })
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