<template>
    <div>
        <a-card>
            <div class='title'>
                IN指标评定
                <a-button type="primary" class="exportExcel" style="float: right;" @click="requireData">
                    导出为Excel
                </a-button>
            </div>
        
            <div>
            <a-table
                :columns="columns"
                :data-source="data"
                :scroll="{ y: height }"
                :pagination="pagination"
                :bordered="true"
                size="small"
            />
            <!-- <a-pagination
                class="paginationStyle"
                show-size-changer
                :default-current="1"
                :total="4000"
                :show-total="total => `总共有 ${total} 条数据`"
                @showSizeChange="onShowSizeChange"
                show-quick-jumper
            /> -->
            </div>
        </a-card>
    </div>
    
</template>

<script>
const columns = [
  {
    title: '序号',
    dataIndex: '序号',
    rowClassName: 'fontColor',
    width: 30,
    align: 'center',
  },
  {
    title: '任务ID',
    dataIndex: '任务ID',
    width: 30,
    align: 'center',
  },
  {
    title: '权重',
    dataIndex: '权重',
    width:80,
    align: 'center',
  },
  {
      title:'是否删除',
      dataIndex: '是否删除',
      width:60,
      align: 'center',
  },
  {
      title:'是否存在',
      dataIndex: '是否存在',
      width:60,
      align: 'center',
  },
  {
      title:'更改次数',
      dataIndex: '更改次数',
      width:60,
      align: 'center',
  },
];

const data = [];
for (let i = 0; i < 4000; i++) {
    data.push({
        序号:i,
        任务ID:`${1383}${i}`,
        权重:32,
        是否删除:'未删除',
        是否存在:'否',
        更改次数:0,
    });
    
}
export default {
    data() {
        return {
            data,
            columns,
            pagination: {
                //current: 1, // 当前页面
                pageSize: 10, // 默认每页显示数量
                showSizeChanger: true, // 显示可改变每页数量
                defaultPageSize: 10, // 默认的每页条数
                pageSizeOptions: ['10', '20', '40', '50', '100'], // 每页数量选项
                showTotal: total => `共有 ${total} 条数据`, // 显示总数
                showSizeChange: (current, pageSize) => this.pageSize = pageSize, // 改变每页数量时更新显示
                showQuickJumper: true
            },
            height: 500,
            screenHeight:null,
        };
    },
    mounted() {
        this.screenHeight = document.body.clientHeight;//获取当前屏幕的高度
        this.height = this.screenHeight - document.querySelector(".title").offsetHeight - 140;

        //屏幕高度调整
        window.onresize = () => {
            return (() => {
                //console.log(12);
                this.height = document.body.clientHeight - document.querySelector(".title").offsetHeight - 140;
            })()
        }
    },
    methods: {
        onShowSizeChange(){

        },
        async requireData(){
            console.log('点击了');
        }
    },
}
</script>

<style lang="less" scoped>
.title {
    //margin-top: 15px;
    //margin-left: 15px;
    margin-bottom: 15px;
    font-family: '微软雅黑';
    font-size: 16px;
}

.paginationStyle {
    margin-top: 15px;
    float: right;
}

.fontColor {
    background-color: #F5222D;
}

</style>