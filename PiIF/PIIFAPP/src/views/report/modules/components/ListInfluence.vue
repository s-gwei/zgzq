<template>
    <div class="influenceWrapper">
        <div class="title">{{title}}</div>
        <div class="influTable">
             <a-table :columns="columns" :data-source="data" :pagination="false" :bordered="true" :row-selection="rowSelection">
             <!-- <a-table :columns="columns" :data-source="data"  :bordered="true" :pagination="{ pageSize: 5 }" :scroll="{ y: 240 }"> -->
                 <a slot="name" slot-scope="text">{{ text }}</a>
                 <span slot="customTitle"><a-icon type="smile-o" /> Name</span>
                 <span slot="tags" slot-scope="tags">
                     {{tags ? "启用" : "停用"}}
                 </span>
                 <!-- <span slot="action" slot-scope="text, record">
                    <a-button :disabled="text.tags == 0 ? true : false">停用</a-button>
                    <a-button>{{text.tags ? "停用" : "启用"}}</a-button>
                 </span> -->
            </a-table>
        </div>
        <div class="btn">
            <a-button type="primary" @click="add(title)">增加</a-button>
            <a-button type="primary" @click="stop">停用</a-button>
        </div>
        <a-modal
      :title="'新增'+title"
      :visible="visible"
      @ok="handleSubmit"
      :confirmLoading="confirmLoading"
      @cancel="handleCancel"
    >
      <a-form  :label-col="{ span: 8 }" :wrapper-col="{ span: 14 }">
        <a-form-item label="新增">
          <a-select  mode="multiple" @change="selectTags" v-model="selectedvalue">
            <a-select-option v-for="item in tagsData" :value="item.id" :key="item.id">{{ item.name }}</a-select-option>
          </a-select>
        </a-form-item>
      </a-form>
    </a-modal>
    </div>
</template>
<script>
export default {
    name: "ListInfluence",
    props: {
        title: {
            type: String,
            default: "影响",
        }
    },
    data(){
        return {
            formParam: {},
            columns: [
              {
                dataIndex: 'num',
                key: 'num',
                title: "编号",
                align: "center",
              },
              {
                title: '链接地址',
                dataIndex: 'address',
                key: 'address',
                align: "center",
              },
              {
                title: '状态',
                key: 'tags',
                dataIndex: 'tags',
                scopedSlots: { customRender: 'tags' },
                align: "center",
                filters: [
                  { text: '启用', value: 1 },
                  { text: '禁用', value: 0 },
                ],
                onFilter: (value, record) => record.tags==value,
              }
            ],
            data: [
              { 
                key: '1',
                num: '111',
                age: 32,
                address: 'New York No. 1 Lake Park,',
                tags: 0,
              },
              {
                key: '2',
                num: '211',
                age: 42,
                address: 'London No. 1 Lake Park',
                tags: 1,
              },
               {
                key: '3',
                num: '111',
                age: 32,
                address: 'New York No. 1 Lake Park,',
                tags: 0,
              },
              { 
                key: '4',
                num: '211',
                age: 42,
                address: 'London No. 1 Lake Park',
                tags: 1,
              },
               {
                key: '5',
                num: '111',
                age: 32,
                address: 'New York No. 1 Lake Park,',
                tags: 0,
              },
              {
                key: '6',
                num: '211',
                age: 42,
                address: 'London No. 1 Lake Park',
                tags: 1,
              }
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
        }
    },
    computed: {
      rowSelection() {
        return {
          onChange: (selectedRowKeys, selectedRows) => {
              this.selectedRowKeys = selectedRowKeys
              this.selectedRows = selectedRows
          },
          getCheckboxProps: record => ({
            props: {
              disabled: record.tags === 0, // Column configuration not to be checked
              name: record.name,
            },
          }),
        };
      },
    },
    mounted(){
        // if(this.value){
        //     this.$set(this,"formParam",this.value)
        // }
    },
    methods: {
        add(){
            this.visible=true
        },
        handleSubmit(){
            console.log(this.title,this.selectedvalue);
        },
        handleCancel(){
            this.visible=false
        },
        selectTags(){},
        stop(){
            console.log('enter',this.selectedRowKeys,this.selectedRows);
            if(!this.selectedRowKeys.length){
                 this.$confirm({
                      content:"至少选择一组数据进行停用",
                    });
            } else{
                this.$confirm({
                      title:"确认停用",
                      content:"是否停用选中数据?",
                      onOk: function(){
                         console.log('确认');
                      }
                    });
            }
        }
    }
    
}
</script>
<style lang="scss" scoped>
// /deep/.ant-card-body{
//     padding: 10px!important;
// }
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