<template>
    <div class="influenceWrapper">
        <div class="title">{{title}}</div>
        <div class="influTable">
             <!-- <a-table :columns="columns" :data-source="data" :pagination="false" :bordered="true" :row-selection="rowSelection"> -->
             <a-table :columns="columns" :data-source="influenceData" :pagination="false" :bordered="true" :row-selection="rowSelection">
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
        <div class="btn">
            <a-button type="primary" @click="add(title)">增加</a-button>
            <a-button type="primary" @click="stop(title)">停用</a-button>
        </div>
        <a-modal
         :title="'新增'+title"
         :visible="visible"
         :confirmLoading="confirmLoading"
         @cancel="handleCancel"
       >
      <a-form  :label-col="{ span: 8 }" :wrapper-col="{ span: 14 }">
        <a-form-item label="新增">
          <a-input placeholder="请输入需要新增的物料编码" v-model="thingNumber" @change ="inputAdd"></a-input>
          <!-- <a-select  mode="multiple" @change="selectTags" v-model="selectedvalue">
            <a-select-option v-for="item in tagsData" :value="item.id" :key="item.id">{{ item.name }}</a-select-option>
          </a-select> -->
        </a-form-item>
      </a-form>
      <template slot="footer">
                <a-button  @click="handleCancel" >取消</a-button>
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
            columns: [
              {
                dataIndex: 'PartNumber',
                key: 'PartNumber',
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
                key: 'PartPhase',
                dataIndex: 'PartPhase',
                // scopedSlots: { customRender: 'tags' },
                align: "center",
                filters: [
                  { text: '启用', value: '启用' },
                  { text: '停用', value: '停用' },
                ],
                onFilter: (value, record) => record.PartPhase==value,
              }
            ],
            data: [
              // { 
              //   key: '1',
              //   PartNumber: '1111111111111',
              //   age: 32,
              //   PartName: 'New York No. 1 Lake Park,',
              //   PartPhase: "启用"
              // },
              // {
              //   key: '2',
              //   PartNumber: '211',
              //   age: 42,
              //   PartName: 'London No. 1 Lake Park',
              //   PartPhase: "停用"
              // }
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
              judgeThingCodeExists:"/pdmReport/judgeThingCodeExists",
              modifyPartInfo :"/pdmReport/modifyPartInfo",
            }
        }
    },
    computed: {
      rowSelection() {
        return {
          onChange: (selectedRowKeys, selectedRows) => {
              this.selectedRowKeys = selectedRowKeys
              this.selectedRows = selectedRows
          },
          getCheckboxProps: record => (
            {
            props: {
              disabled: record.PartPhase === "停用", // Column configuration not to be checked
              name: record.name,
            },
          }),
        };
      },
    },
    mounted(){
    },
    methods: {
        add(){
            this.visible=true
        },
        inputAdd(){
          // console.log(this.thingNumber);
          const _this = this,url=this.jkUrl.judgeThingCodeExists
          getAction(url,{thingNumber: this.thingNumber}).then((res)=>{
                if (res.success) {
                //   this.projects = res.result.records
                  _this.$message.success('此物料编码存在');
                  _this.addSure = false
                } else {
                  _this.$message.error(res.message || "此物料编码不存在",0.1);
                  //  _this.addSure = true
                  _this.addSure = false
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
             console.log(this.$store.state.report.infoParams);
             this.getUpdateDate()
            //  getAction(url,{}).then((res) => {
            //   //  console.log(res);
            //     if(res.success){

            //     }
            //  })
        },
        handleCancel(){
            this.visible=false
        },
        selectTags(){},
        // 停用
        stop(){
            console.log('enter',this.selectedRowKeys,this.selectedRows);
            const _this = this
            var selectedNum = []
            this.selectedRows.forEach(function(item){
                const obj = {
                  type: "D",
                  partNumber: item.PartNumber
                }
                selectedNum.push(obj)
            })
            console.log(selectedNum);
            if(!this.selectedRowKeys.length){
                 this.$confirm({
                      content:"至少选择一组数据进行停用",
                    });
            } else{
              console.log(this.selectedRowKeys);
                this.$confirm({
                      title:"确认停用",
                      content:"是否停用选中数据?",
                      onOk: function(){
                         console.log('确认');
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
          const params = this.$store.state.report.infoParams
          console.log(params);
          getAction(url,params).then((res) => {
            console.log(res,'res');
            if(res.success){
               _this.$store.commit("changePublishBtnState", false)
                _this.$emit('getCurrentArig')
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
    position: relative;
    .title{
        font-weight: 600;
        font-size: 18px;
        text-align: center;
        margin-bottom: 10px;
    }
    .influTable{
        height: 76%;
        overflow-y: auto;
    }
    .btn{
        position: absolute;
        bottom: 0;
        right: 10px;
        .ant-btn:nth-child(2){
            margin-left: 10px;
        }
    }
}
</style>