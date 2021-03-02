<template>
  <a-card :bordered="false">
    <a-tabs defaultActiveKey="1">
      <a-tab-pane tab="过程监控" key="1">
<!--        <iframe src="../../3DRoom/lianjiedong_main.html" frameborder="0" width="100%" height="800px" scrolling="auto"></iframe>-->
      <TabIframeView src="../../3DRoom/lianjiedong_main.html"></TabIframeView>
      </a-tab-pane>

      <a-tab-pane tab="故障统计分析" key="2">
        <div class="fault_analyze_operator">
          <a-form layout="inline">
            <a-row :gutter="24">
              <a-col :md="8" :sm="12">
                <a-form-item label="闸名称:">
                  <a-select
                    :size="size"
                    defaultValue="ljd_fsz"
                    style="width: 200px"
                    @change="handleChange"
                  >
                    <a-select-option v-for="item in brakeNameList" :key="item.value">
                      {{item.label}}
                    </a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
              <a-col :md="8" :sm="12">
                <a-form-item label="故障设备分类:">
                  <a-select
                    :size="size"
                    defaultValue="A"
                    style="width: 200px"
                    @change="handleChange"
                  >
                    <a-select-option v-for="item in faultTypeList" :key="item.value">
                      {{item.label}}
                    </a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
              <a-col :md="6" :sm="8">
                <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
                  <a-button type="primary" @click="" icon="search">查询</a-button>
                </span>
              </a-col>
            </a-row>
          </a-form>
        </div>
        <a-row :gutter="24">
          <a-col :span="9" style="padding-top: 10px;">
            <pie class="statistic" title="设备分类" :pieStyle="faultPieStyle" :dataSource="pieChartData" :height="180"/>
          </a-col>
          <a-col :span="15" style="padding-top: 10px;">
            <bar class="statistic" title="设备故障分类" :style="{ padding: '0 0 0 0' }" :dataSource="barChartSource" :height="180"/>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :span="24" style="padding-top: 10px;">
            <bar-multid title="故障分类" :style="{ padding: '0 0 0 0' }" :sourceData="multidbarChartSource" :fields="multBarFields" :height="160"/>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :span="24" style="padding-top: 10px;">
            <a-table :columns="checkColumns" :dataSource="checkData" size="small" />
          </a-col>
        </a-row>
      </a-tab-pane>
    </a-tabs>
  </a-card>
</template>

<script>
  import Pie from '@/components/chart/Pie'
  import Bar from '@/components/chart/Bar'
  import BarMultid from '@/components/chart/BarMultid'
  import TabIframeView from '@/components/layouts/TabIframeView'
  export default {
    name: '',
    components:{
      Pie,
      Bar,
      BarMultid,
      TabIframeView
    },
    data(){
      return{
        queryParam: {},
        size: 'default',
        loading: false,
        brakeNameList: [
          {
            value: 'ljd_fsz',
            label: '连接洞分水闸'
          },
          {
            value: 'hsd_csz',
            label: '黄三段出水闸'
          },
          {
            value: 'yld_sz',
            label: '越岭段水闸'
          }
        ],
        faultTypeList:[
          {
            value: 'A',
            label: 'A类故障设备'
          },
          {
            value: 'B',
            label: 'B类故障设备'
          },
          {
            value: 'C',
            label: 'C类故障设备'
          }
        ],
        pieChartData:[{
          count: 335,
          item: "A类设备"
        },{
          count: 310,
          item: "B类设备"
        },{
          count: 234,
          item: "C类设备"
        }],
        faultPieStyle:{
          stroke: '#fff',
          lineWidth: 1,
          center:['10%','30%']
        },
        barChartSource:[{
          x:'7月',
          y:40
        },{
          x:'8月',
          y:35
        },{
          x:'9月',
          y:63
        },{
          x:'10月',
          y:18
        },{
          x:'11月',
          y:26
        }],
        multidbarChartSource:[
          { type: 'bar1', '1月': 18.9, '2月': 28.8, '3月': 39.3, '4月': 81.4, '5月': 47, '6月': 20.3, '7月': 24, '8月': 35.6 },
          { type: 'bar2', '1月': 12.4, '2月': 23.2, '3月': 34.5, '4月': 99.7, '5月': 52.6, '6月': 35.5, '7月': 37.4, '8月': 42.4 },
          { type: 'bar3', '1月': 12.4, '2月': 23.2, '3月': 34.5, '4月': 99.7, '5月': 52.6, '6月': 35.5, '7月': 37.4, '8月': 42.4 },
          { type: 'bar4', '1月': 12.4, '2月': 23.2, '3月': 34.5, '4月': 99.7, '5月': 52.6, '6月': 35.5, '7月': 37.4, '8月': 42.4 }
        ],
        multBarFields:["1月","2月","3月","4月","5月","6月","7月","8月"],
        checkColumns:[
          {
            title: '故障设备',
            dataIndex: 'name'
          },
          {
            title: '故障分类',
            dataIndex: 'fault_type'
          },
          {
            title: '故障点',
            dataIndex: 'fault_point'
          },
          {
            title: '故障次数',
            dataIndex: 'fault_count'
          },
          {
            title: '故障评级',
            dataIndex: 'fault_level',
            render: (h, params) => {
              if(params.row.fault_level === '低'){
                return h('div', [
                  h('span', {
                    domProps: { // 添加标签,使用自己引入的iconfont图标
                      innerHTML: "<div style='width:8px;height: 8px;border-radius: 100%;background: #3ecdaf;float: left;margin-top: 5px;margin-right: 3px'></div><span>"+params.row.fault_level+"</span>"
                    }
                  })
                ])
              }else if(params.row.fault_level === '高'){
                return h('div', [
                  h('span', {
                    domProps: { // 添加标签,使用自己引入的iconfont图标
                      innerHTML: "<div style='width:8px;height: 8px;border-radius: 100%;background: #f6035d;float: left;margin-top: 5px;margin-right: 3px'></div><span>"+params.row.fault_level+"</span>"
                    }
                  })
                ])
              }else if(params.row.fault_level === '中'){
                return h('div', [
                  h('span', {
                    domProps: { // 添加标签,使用自己引入的iconfont图标
                      innerHTML: "<div style='width:8px;height: 8px;border-radius: 100%;background: #f69517;float: left;margin-top: 5px;margin-right: 3px'></div><span>"+params.row.fault_level+"</span>"
                    }
                  })
                ])
              }
            }
          },
          {
            title: '故障建议',
            dataIndex: 'fault_offer'
          }
        ],
        checkData: [
          {
            key:'1',
            name: '泵站电机',
            fault_type: '电气故障',
            fault_point: '欠压故障',
            fault_count:'7',
            fault_level:'高',
            fault_offer: '今日班次该故障较多，建议尽快对供电线路进行检查。'
          },
          {
            key:'2',
            name: '泵站电机',
            fault_type: '护故障',
            fault_point: '接地故障',
            fault_level:'中',
            fault_count:'1',
            fault_offer: '日常维护'
          },
          {
            key:'3',
            name: '泵站电机',
            fault_type: '保护故障',
            fault_point: '电机前轴超温报警',
            fault_count:'7',
            fault_level:'低',
            fault_offer: '尽快检查'
          }
        ]
      }
    },
    methods:{
      handleChange(value) {
        console.log(`Selected: ${value}`);
      },
    }
  }
</script>

<style scoped>

</style>