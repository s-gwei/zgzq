<template>
    <div class="influenceWrapper">
        <div class="title"><div class="image"></div>{{title}}</div>
         <div class="btn">
            <a-button type="primary" @click="add(title)" class="addBtn" v-if="$store.state.report.btnEditable || title=='被约束总成'">增加</a-button>
            <a-button @click="stop(title)" class="stopBtn" v-if="$store.state.report.btnEditable">停用</a-button>
        </div>
        <div class="influTable">
             <!-- <a-table :columns="columns" :data-source="data" :pagination="false" :bordered="true" :row-selection="rowSelection"> -->
             <!-- <a-table :columns="columns" :data-source="influenceData" :pagination="false" :bordered="true" :row-selection="rowSelection"> -->
             <a-table :columns="columns" :data-source="influenceData" :pagination="false" :bordered="true" 
             :rowSelection="{selectedRowKeys:$store.state.report.selectedRowKeys,onChange,getCheckboxProps}">
             <!-- <a-table :columns="columns" :data-source="data"  :bordered="true" :pagination="{ pageSize: 5 }" :scroll="{ y: 240 }"> -->
                 <a slot="name" slot-scope="text">{{ text }}</a>
                 <span slot="customTitle"><a-icon type="smile-o" /> Name</span>
                 <!-- <span slot="tags" slot-scope="tags">
                     {{tags ? "启用" : "停用"}}
                 </span> -->
                 <!-- <span slot="action" slot-scope="text, record">
                    <a-button :disabled="text.tags == 0 ? true : false">停用</a-button>
                    <a-button>{{text.tags ? "停用" : "启用"}}</a-button>
                 </span> -->
            </a-table>
        </div>
        <a-modal
         :title="'新增'+title"
         :visible="visible"
         :confirmLoading="confirmLoading"
         @cancel="handleCancel"
       >
      <a-form  :label-col="{ span: 8 }" :wrapper-col="{ span: 14 }">
        <a-form-item label="新增">
          <a-input placeholder="请输入需要新增的物料编码" v-model="thingNumber"></a-input>
          <!-- <a-select  mode="multiple" @change="selectTags" v-model="selectedvalue">
            <a-select-option v-for="item in tagsData" :value="item.id" :key="item.id">{{ item.name }}</a-select-option>
          </a-select> -->
        </a-form-item>
      </a-form>
      <template slot="footer">
                <a-button  @click="handleCancel">取消</a-button>
                <a-button type="primary" @click="handleSubmit" :disabled="addSure">确认</a-button>
           </template>
    </a-modal>
    </div>
</template>
<script>
import {getAction} from '@/api/manage';
export default {
    name: "ListInfluence",
    props: {
        title: {
            type: String,
            default: "影响",
        },
        influenceData: {
          type: Array,
          default: ()=>{
            return []
          }
        }
    },
    data(){
        return {
            addSure: true,
            thingNumber: "", //物料编码
            formParam: {},
            columns: [],
            data: [
            ],
            scrollHeight: 200,
            selectedRowKeys: [],
            selectedRows: [],
            tagsData: [
                {
                    name: 'name',
                    id: "1"
                },
                {
                    name: 'name1',
                    id: "2"
                },
                {
                    name: 'name2',
                    id: "3"
                }
            ],
            selectedvalue:[],
            visible: false,
            confirmLoading: false,
            affectLink: [],
            constraintLink: [],
            jkUrl: {
              judgeThingCodeExists:"/jeecg-boot/pdmReport/judgeThingCodeExists",
              modifyPartInfo :"/jeecg-boot/pdmReport/modifyPartInfo",
            },
            timer: null
        }
    },
    computed: {
      // rowSelection() {
      //   return {
      //     selectedRowKeys:selectedRowKeys,
          
          
      //   };
      // },
    },
    mounted(){
      this.columns= this.title == '源总成' ?  [
              {
                dataIndex: 'AffectPartNumber',
                key: 'AffectPartNumber',
                title: "物料号",
                align: "center",
                sorter: (a, b) => a.AffectPartNumber.length - b.AffectPartNumber.length,
              },
              {
                title: '名称',
                dataIndex: 'PartName',
                key: 'PartName',
                align: "center",
                sorter: (a, b) => a.PartName.length - b.PartName.length,
              },
              {
                title: '状态',
                key: 'LinkPhase',
                dataIndex: 'LinkPhase',
                // scopedSlots: { customRender: 'tags' },
                align: "center",
                filters: [
                  { text: '启用', value: '启用' },
                  { text: '停用', value: '停用' },
                ],
                onFilter: (value, record) => record.LinkPhase==value,
              }
            ] :  [
              {
                dataIndex: 'ConstraintPartNumber',
                key: 'ConstraintPartNumber',
                title: "物料号",
                align: "center",
              },
              {
                title: '名称',
                dataIndex: 'PartName',
                key: 'PartName',
                align: "center",
              },
              {
                title: '状态',
                key: 'LinkPhase',
                dataIndex: 'LinkPhase',
                // scopedSlots: { customRender: 'tags' },
                align: "center",
                filters: [
                  { text: '启用', value: '启用' },
                  { text: '停用', value: '停用' },
                ],
                onFilter: (value, record) => record.LinkPhase==value,
              }
            ]
    },
    watch: {
   		thingNumber: {
   	      	handler(v) {
   		        if (this.timer) {
   		          clearTimeout(this.timer)
   		        }
   		        this.timer = setTimeout(() => {
   		          if(v){
                   this.inputAdd();
                 }
   		        }, 1000)
   		      },
   		    deep: true
   	    }
   	},
    methods: {
      onChange (selectedRowKeys, selectedRows) {
              // this.selectedRowKeys = selectedRowKeys
              this.$store.commit("selectedRowKeys",selectedRowKeys)
              this.$store.commit("selectedRows",selectedRows)
              this.selectedRows = selectedRows
          },
          getCheckboxProps: record => (
            {
            props: {
              disabled: record.LinkPhase === "停用", // Column configuration not to be checked
              name: record.name,
            },
          }),
        add(){
            this.visible=true
            this.thingNumber = null
        },
        inputAdd(){
          const _this = this,url=this.jkUrl.judgeThingCodeExists
          getAction(url,{thingNumber: this.thingNumber}).then((res)=>{
                if (res.success) {
                //   this.projects = res.result.records
                  _this.$message.success('此物料编码存在');
                  _this.addSure = false
                } else {
                  _this.$message.error(res.message || "此物料编码不存在",0.1);
                   _this.addSure = true
                  // _this.addSure = false
                }
            })
        },
        handleSubmit(){
             this.$store.commit("affectLink",{
               name: this.title,
               add: [{
                  type: "I",
                  partNumber: this.thingNumber
               }]
             })
             const _this = this,url=this.jkUrl.modifyPartInfo
            //  console.log(this.$store.state.report.infoParams);
             this.getUpdateDate()
            //  getAction(url,{}).then((res) => {
            //   //  console.log(res);
            //     if(res.success){

            //     }
            //  })
        },
        handleCancel(){
            this.visible=false
            this.thingNumber = ""
        },
        selectTags(){},
        // 停用
        stop(){
            // console.log('enter',this.selectedRowKeys,this.selectedRows);
            const _this = this
            var selectedNum = []
            this.$store.state.report.selectedRows.forEach(function(item){
                const obj = {
                  type: "D",
                  partNumber: item.PartNumber
                }
                selectedNum.push(obj)
            })
            if(!this.$store.state.report.selectedRowKeys.length){
                 this.$confirm({
                      content:"至少选择一组数据进行停用",
                    });
            } else{
                this.$confirm({
                      title:"确认停用",
                      content:"是否停用选中数据?",
                      onOk: function(){
                         _this.$store.commit("affectLink",{
                            name: _this.title,
                            add:  selectedNum
                          })
                          _this.getUpdateDate()
                      }
                    });
            }
        },
        getUpdateDate(){
          const _this = this,url = this.jkUrl.modifyPartInfo
          const params = {}
          params.param = this.$store.state.report.infoParams
          params.partNumber = this.$store.state.report.infoParams.partNumber
          params.totalCarNumber = this.$store.state.report.infoParams.parentNumber
          getAction(url,params).then((res) => {
            if(res.success){
              _this.visible=false
              _this.$store.state.report.currentEditionVal = "最新版"
              _this.$message.success("修改成功")
               _this.$store.commit("changePublishBtnState", false)
                _this.$emit('getCurrentArig')
            }else{
              this.$message.error(res.message || "失败")
            }
          })
        }
    }
    
}
</script>
<style lang="scss" scoped>
// /deep/.ant-card-body{
//     padding: 10px!important;
// }
.cancel{
  border:1px solid transparent;
}
.influenceWrapper{
    height: 100%;
    // position: relative;
    display: flex;
    flex-direction: column;
    .title{
        font-weight: 600;
        margin-bottom: 10px;
        display: flex;
        align-items: center;
        color: #333333;
        font-size: 14px;
        .image{
          display: inline-block;
          width: 14px;
          height: 14px;
          background: url("../../../../assets/risktable/root.png");
          background-size: contain;
          margin-right: 6px;
        }
    }
    .influTable{
        // height: 76%;
        flex: 1;
        overflow-y: auto;
    }
    .btn{
        // position: absolute;
        // bottom: 0;
        // right: 10px;
        margin-bottom: 6px;
        .ant-btn:nth-child(2){
            margin-left: 10px;
        }
    }
}
.stopBtn{
  color: #1890FF;
}
.affectLink{
  .addBtn{
    background: #BFBFBF;
    border: 1PX solid #BFBFBF;

  }
  .stopBtn{
    color: #999999;
    background: #fff;
    border: 1px solid #D9D9D9;
  }
}
.constraintLink{
   &>.title .image{
     background: url("../../../../assets/risktable/affect.png");
     background-size: contain;
   }
}
/deep/.ant-btn{
  padding: 0 20px;
}
/deep/.ant-table-column-title{
  color: #333;
  font-size: 12px;
  font-weight: bold;
  // font-family: "Chinese Quote", -apple-system, BlinkMacSystemFont, "Segoe UI", "PingFang SC", "Hiragino Sans GB", "Microsoft YaHei", "Helvetica Neue", Helvetica, Arial, sans-serif, "Apple Color Emoji", "Segoe UI Emoji", "Segoe UI Symbol";

}
</style>