<template>
      <!-- <a-tabs>
          <a-tab-pane tab="方向性图标" key="1">
            <div id="container1"></div>
              <div id="container2"></div>
          </a-tab-pane>
          <a-tab-pane tab="方向性图标" key="2">
          </a-tab-pane>
      </a-tabs> -->
      <a-card :bordered="false">
        <div class="title">问题风险链</div>
        <div class="tab-box" v-if="data && data.length && data.length>1">
             <div>
               <div v-for="(item,index) in data" :key="index" class="tab-item" :class="{active: activeIdx ==index}" @click="clickTab(item,index)">tab{{index}}</div>
             </div>
        </div>
        <div>
            <div id="container"></div>
        </div>
      </a-card>
</template>

<script>
import G6 from '@antv/g6';
import {getAction} from '@/api/manage';
  export default {
    name: "testTable",
    data () {
      return {
        devicetopologyChart: null,
        tooltip: null,
        activeIdx: 0,
        data: [
          // {
          //     id: 'A',
          //     confirm_status: '1',
          //     children: [
          //       {
          //         id: 'A1',
          //         confirm_status: '1',
          //         children: [{ id: 'A11' ,confirm_status: '1',risk_name: 'This is edge from node 0 to node 2.'}, { id: 'A12',confirm_status: '1' ,risk_name: '介绍'}, { id: 'A13',confirm_status: '0',risk_name: '介绍a' }, { id: 'A14',confirm_status: '0',risk_name: '介绍' }],
          //       },
          //       {
          //         id: 'A2',
          //         confirm_status: '1',
          //         children: [
          //           {
          //             id: 'A21',
          //             confirm_status: '1',
          //             risk_name: '介绍',
          //             children: [{ id: 'A211',confirm_status: '0',risk_name: 'This is edge from node 0 to node 2.' }, { id: 'A212' ,confirm_status: '1'}],
          //           },
          //           {
          //             id: 'A22',
          //             confirm_status: '0',
          //             risk_name: '介绍A'
          //           },
          //         ],
          //       },
          //       {
          //         id: 'A3',
          //         confirm_status: '1',
          //         risk_name: '介绍',
          //         children: [
          //           {
          //             id: 'A31',
          //             confirm_status: '1',
          //             children: [{ id: 'A311',confirm_status: '0',risk_name: 'This is edge from' }, { id: 'A312' ,confirm_status: '1'}],
          //           },
          //           {
          //             id: 'A32',
          //             risk_name: '测试',
          //             confirm_status: '0',
          //             children: [
          //               {
          //                 id: 'A301',
          //                 confirm_status: '1',
          //                 children: [{ id: 'A3011',confirm_status: '0',risk_name: 'This is edge from' }, { id: 'A3120' ,confirm_status: '1'}],
          //               },
          //               {
          //                 id: 'A320',
          //                 risk_name: '测试',
          //                 confirm_status: '0',
          //               },
          //             ]
          //           },
          //         ],
          //       },
          //     ],
          // },
          // {
          //     id: 'B',
          //     confirm_status: '1',
          //     risk_name: '介绍',
          //     children: [
          //       {
          //         id: 'B1',
          //         confirm_status: '1',
          //         children: [{ id: 'B11' ,confirm_status: '1',risk_name: '介绍js'}, { id: 'A12',confirm_status: '    已确认' }, { id: 'A13',confirm_status: '0' }, { id: 'A14',confirm_status: '0' }],
          //       },
          //       {
          //         id: 'A2',
          //         confirm_status: '1',
          //         children: [
          //           {
          //             id: 'A21',
          //             confirm_status: '1',
          //             children: [{ id: 'A211',confirm_status: '0',risk_name: 'This is edge from node 0 to node 2.' }, { id: 'A212' ,confirm_status: '1'}],
          //           },
          //           {
          //             id: 'A22',
          //             risk_name: '123',
          //             confirm_status: '0'
          //           },
          //         ],
          //       }
          //     ],
          // },
          //  {
          //     id: 'C',
          //     confirm_status: '1',
          //     risk_name: '介绍C',
          //     children: [
          //       {
          //         id: 'C1',
          //         confirm_status: '1',
          //         children: [
          //           { id: 'B11' ,
          //           confirm_status: '1',
          //           risk_name: 'This is edge',
          //           children: [
          //              { id: 'C11' ,
          //               confirm_status: '1',
          //                risk_name: 'This is edge from node 0 to node 2.',
          //                children: [
          //                   { id: 'D11' ,
          //           confirm_status: '1',
          //           risk_name: 'This is edge from node 0 to node 2.',
          //           children: [
          //              { id: 'E11' ,
          //           confirm_status: '1',
          //           risk_name: 'This is edge from node 0 to node 2.'}
          //           ]
          //           }
          //                ]
          //           }
          //           ]
          //           },
          //            { id: 'A12',confirm_status: '1' }, { id: 'A13',confirm_status: '0' }, { id: 'A14',confirm_status: '0' }],
          //       },
          //       {
          //         id: 'W1',
          //         confirm_status: '1',
          //       },
          //        {
          //         id: 'q1',
          //         confirm_status: '1',
          //       }
          //     ],
          // }
        ],
        url: {
              tableDate: "/OTDrivice/problemRickChain",
        },
      }
    },
    mounted(){
         this.getChainData()
         if (typeof window !== 'undefined')
           window.onresize = () => {
             this.devicetopologyChart && this.devicetopologyChart.destroy();;
             this.getShape(this.activeIdx+1)
           };
    }, 
    methods: {
      clickTab(item,i){
          this.activeIdx = i
          this.devicetopologyChart && this.devicetopologyChart.destroy();
          this.getShape(this.activeIdx+1)
      },
       getChainData(){
          const  url = this.url.tableDate,_this=this,params={};
          params.riskId = this.$route.query.riskId
          this.isLoading = true
          this.queryParam = Object.assign({},params)
          getAction(url,this.queryParam,'get').then((res) => {
            _this.isLoading = false
           if(res.success && res.result){
              _this.dealRes(res.result)
              _this.$set(_this,'data',res.result)
              _this.$nextTick(function(){
                _this.getShape(this.activeIdx+1)
              })
           }else{
           }
         })
         .catch(function(err){
              _this.isLoading = false
         })
       },
       dealRes(data){
         var str = '0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ';
         var _this = this
         data.map(function(item){
           var random = str[Math.floor(Math.random() * str.length)] + str[Math.floor(Math.random() * str.length)] 
           item.id = item.id + random + Math.random()
           if(item.children && item.children.length){
               _this.dealRes(item.children)
           }
         })
       },
       getShape(i){
            G6.registerNode('card-node', {
              draw: function drawShape(cfg, group) {
                const r = 2;
                const color = '#5B8FF9';
                const w = cfg.size[0];
                const h = cfg.size[1];
                // const colors = {
                //   0: '#FF424C',
                //   1: '#40a9ff'
                // }
                const textStyle = {
                  textAlign: 'center',
                  fontSize: 16,
                  fill: 'rgba(0, 0, 0, 0.9)'
                }
                const shape = group.addShape('rect', {
                  attrs: {
                    x: -w / 2,
                    y: -h / 2,
                    width: w , //200,
                    height: h, // 60
                    stroke: color,
                    radius: r,
                    fill: '#fff',
                    stroke: cfg.confirm_status == 1 ? '#40a9ff' : '#FF424C',
                  },
                  name: 'main-box',
                  // draggable: false,
                });
                group.addShape('rect', {
                  attrs: {
                    x: -w / 2,
                    y: -h / 2,
                    width: w, //200,
                    height: h / 2, // 60
                    // fill: edgeCircleColorArr[0],
                    // fill: colors[cfg.confirm_status],
                    fill: cfg.confirm_status == 1 ? '#40a9ff' : '#FF424C',
                    radius: [r, r, 0, 0],
                  },
                  name: 'title-box',
                  // draggable: false,
                });
            
                // title text
                group.addShape('text', {
                  attrs: {
                    textBaseline: 'top',
                    x: -w / 2 + 8,
                    y: -h / 2 + 12,
                    lineHeight: 30,
                    fontSize: 16,
                    text: cfg.confirm_status == 1 ? "已确认" : "未确认",
                    fill: '#fff',
                  },
                  name: 'title',
                });
                group.addShape('text', {
                  attrs: {
                    textBaseline: 'top',
                    x: -w / 2 + 8,
                    y: -h / 2 + 50,
                    // text: 'risk_name',
                    text: cfg.risk_name,
                    fontSize: 16,
                    fill: 'black',
                  },
                  name: `risk_name`,
                });
                return shape;
              },
            });
             const tooltip = new G6.Tooltip({
                 offsetX: 10,
                 offsetY: 50 + 10,
                 // the types of items that al未确认 the tooltip show up
                 itemTypes: ['node', 'edge'],
                 // custom the tooltip's content
                 // 自定义 tooltip 内容
                 getContent: (e) => {
                   const outDiv = document.createElement('div');
                   outDiv.style.width = '风险';
                   //outDiv.style.padding = '0px 0px 20px 0px';
                   outDiv.innerHTML = `
                     <h4>Custom Content</h4>
                     <ul>
                       <li>名称: ${e.item.getModel().risk_name}</li>
                     </ul>
                     <ul>
                       <li>类型: ${e.item.getModel().risk_to_type}</li>
                     </ul>
                     <ul>
                       <li>描述: ${e.item.getModel().risk_description}</li>
                     </ul>`;
                   return outDiv;
                 },
                //  shouldBegin: (e) => {
                //    // console.log(e.item);
                //    let res = true;
                //    switch (e.item.getModel().id) {
                //      case '1':
                //        res = false;
                //        break;
                //      case '2':
                //        if (e.target.get('name') === 'text-shape') res = true;
                //        else res = false;
                //        break;
                //      case '3':
                //        if (e.target.get('name') !== 'text-shape') res = true;
                //        else res = false;
                //        break;
                //      default:
                //        res = true;
                //        break;
                //    }
                //    return res;
                //  },
              });
              this.tooltip = tooltip
              this.renderChart(tooltip,i)
       },
       renderChart(tooltip,i){
        //  const name = "container" + i
         const container = document.getElementById("container");
         const width = !this.data[i-1].children || !this.data[i-1].children.length ? 300 : (!this.data[i-1].children.some(function(item){ return item.children }) ? 700 : document.body.clientWidth - 20 ) 
        //  const width = document.body.clientWidth - 20;
         const height =  document.body.clientHeight - 120;
        //  console.log(document.body.clientHeight,container.scrollHeight);
        const flag = !this.data.children || this.data.children.length == 1 
         const graph = new G6.TreeGraph({
           container: container,
           width,
           height,
           linkCenter: true,
           plugins: [tooltip],
           modes: {
            //  default: ['drag-canvas'],
           },
           defaultNode: {
             type: 'card-node',
             size: [360, 80],
           },
           defaultEdge: {
             type: 'cubic-horizontal',
             style: {
               endArrow: true,
             },
           },
           layout: {
             type: 'indented',
             direction: 'LR',
             dropCap: false,
             indent: 420,
             getHeight: () => {
               return 80;
             },
           },
          });
          // graph.clear();
         this.graphData(graph,i)
       },
       graphData(graph,i){
         graph.data(this.data[i-1]);
         this.devicetopologyChart = graph;
         graph.render();
         graph.fitView();
         graph.on('node:mouseenter', (e) => {
           graph.setItemState(e.item, 'active', true);
         });
         graph.on('node:mouseleave', (e) => {
           graph.setItemState(e.item, 'active', false);
         });
         }
       }
  }
</script>

<style scoped lang="scss">
/deep/.ant-card-body{
  padding: 12px;
}
.title{
  font-weight: 700;
  font-size: 18px;
  text-align: center;
  margin-bottom: 8px;
}
.tab-box{
			display: flex;
			flex: 1;
			// width: calc(100% - 40px);
			align-items: center;
			height: 41px;
			flex-wrap: nowrap;
      border-bottom: 1px solid #e8e8e8;
			.tab-item{
        height: 40px;
        margin: 0 2px 0 0;
        background: #fafafa;
        border-radius: 4px 4px 0 0;
				flex-shrink: 0;
				line-height: 40px;
				padding: 0 16px;
				color: #333;
				font-size: 16px;
				display: inline-block;
        border: 1px solid #e8e8e8;
        border-bottom: 1px solid #fff;
        transition: all .3s cubic-bezier(.645,.045,.355,1);
        cursor: pointer;
			}
			.active{
				color: #1890ff;
        background: #fff;
        // border-color: #e8e8e8;
			}
		}
</style>