<template>
      <a-card :bordered="false">
        <div class="title">问题风险链</div>
        <!-- <div class="tab-box" v-if="data && data.length && data.length>1">
             <div>
               <div v-for="(item,index) in data" :key="index" class="tab-item" :class="{active: activeIdx ==index}" @click="clickTab(item,index)">tab{{index}}</div>
             </div>
        </div> -->
        <div>
            <div id="container"></div>
        </div>
      </a-card>
</template>

<script>
import G6 from '@antv/g6';
import {getAction} from '@/api/manage';
// const data = {
//   nodes: [
//     {
//       id: '0',
//       confirm_status: '1',
//       risk_name: '',
//       risk_description: 'risk_description'
//     },
//     {
//       id: '1',
//        confirm_status: '0',
//       risk_name: '1我当初',
//     },
//     {
//       id: '2',
//       confirm_status: '1',
//       risk_name: '2要不',
//     },
//     {
//       id: '3',
//       confirm_status: '1',
//       risk_name: '3介绍',
//     },
//     {
//       id: '4',
//       confirm_status: '0',
//       risk_name: '444',
//     },
//     {
//       id: '5',
//       confirm_status: '1',
//       risk_name: '5介绍5',
//     },
//     {
//       id: '6',
//       risk_name: '6介绍',
//     },
//     {
//       id: '7',
//       risk_name: '7介绍',
//     },
//     {
//       id: '8',
//       risk_name: '8介绍',
//     },
//     {
//       id: '9',
//       risk_name: '9介绍',
//     },
//     {
//       id: '10',
//       risk_name: '10te',
//     }
//   ],
//   edges: [
//     {
//       source: '0',
//       target: '1',
//     },
//     {
//       source: '0',
//       target: '2',
//     },
//     {
//       source: '0',
//       target: '3',
//     },
//     // {
//     //   source: '0',
//     //   target: '4',
//     // },
//     {
//       source: '0',
//       target: '5',
//     },
//     {
//       source: '0',
//       target: '7',
//     },
//     {
//       source: '0',
//       target: '8',
//     },
//     {
//       source: '0',
//       target: '9',
//     },
//     {
//       source: '0',
//       target: '10',
//     },
//     {
//       source: '2',
//       target: '3',
//     },
//     {
//       source: '4',
//       target: '5',
//     },
//     {
//       source: '5',
//       target: '6',
//     },
//     {
//       source: '6',
//       target: '7',
//     },
//     {
//       source: '7',
//       target: '10',
//     },
//     {
//       source: '8',
//       target: '10',
//     },
//     {
//       source: '9',
//       target: '10',
//     },
//   ],
// };
  export default {
    name: "testTable",
    data () {
      return {
        devicetopologyChart: null,
        tooltip: null,
        activeIdx: 0,
        data: [],
        url: {
              tableDate: "/OTDrivice/problemRickChain",
        },
      }
    },
    mounted(){
         this.getChainData()
         if (typeof window !== 'undefined')
           window.onresize = () => {
             this.devicetopologyChart && this.devicetopologyChart.destroy();
             this.getShape()
           };
    },
    watch: {
      'data': {
        handler(v){
          if(v){
            //  this.devicetopologyChart && this.devicetopologyChart.destroy();
            //  this.getShape()
          }
        }
      }
    },
    methods: {
       getChainData(){
          const  url = this.url.tableDate,_this=this,params={};
          params.riskId = this.$route.query.riskId
          this.isLoading = true
          this.queryParam = Object.assign({},params)
          getAction(url,this.queryParam,'get').then((res) => {
            _this.isLoading = false
           if(res.success && res.result){
              res.result.nodes.map(function(item){
                item.id = String(item.id)
              })
              res.result.edges.map(function(item){
                item.source = String(item.source)
                item.target = String(item.target)
              })
              _this.$set(_this,'data',res.result)
              _this.$nextTick(function(){
                _this.getShape()
              })
           }else{
           }
         })
         .catch(function(err){
              _this.isLoading = false
         })
       },
       getShape(){
            G6.registerNode('card-node', {
              draw: function drawShape(cfg, group) {
                const r = 2;
                const color = '#5B8FF9';
                const w = cfg.size[0];
                const h = cfg.size[1];
                const shape = group.addShape('rect', {
                  attrs: {
                    x: -w / 2 ,
                    y: -h / 2,
                    width: w , 
                    height: h, 
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
                    width: w, 
                    height: h / 2, 
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
                    x: -w / 2 + 70,
                    y: -h / 2 + 12,
                    // lineHeight: 30,
                    fontSize: 14,
                    text: cfg.confirm_status == 1 ? "已确认" : "未确认",
                    fill: '#fff',
                  },
                  name: 'title',
                });
                group.addShape('text', {
                  attrs: {
                    textBaseline: 'top',
                    x: -w / 2 + 8,
                    y: -h / 2 + 44,
                    text: cfg.risk_name,
                    fill: 'black',
                    fontSize: 14,
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
              });
              this.tooltip = tooltip
              this.renderChart(tooltip)
       },
       renderChart(tooltip){
        //  const container = document.getElementById("container");
         const width = document.body.scrollWidth - 40;
         const height =  document.body.clientHeight - 50;
         console.log(width);
         const graph = new G6.Graph({
              container: 'container',
              width,
              height,
              plugins: [tooltip],
              fitView: true,
              // controlPoints: false,
              modes: {
                default: ['drag-canvas', 'drag-node'],
              },
              layout: {
                rankdir: 'TB',
                type: 'dagre',
                // rankdir: 'TB',
                // align: 'UL',
                controlPoints: true,
                // nodesepFunc: () => 20,
                // ranksepFunc: () => 20,
              },
              animate: true,
              defaultNode: {
                size: [198, 68],
                type: 'card-node',
                style: {
                  lineWidth: 2,
                  stroke: '#5B8FF9',
                  fill: '#C6E5FF',
                },
              },
              defaultEdge: {
                type: 'polyline',
                size: 3,
                color: '#e2e2e2',
                style: {
                  endArrow: {
                    path: 'M 0,0 L 16,8 L 16,-8 Z',
                    fill: '#e2e2e2',
                  },
                  radius: 30,
                },
              },
          });
          // graph.clear();
         this.graphData(graph)
       },
       graphData(graph){
         graph.data(this.data);
         this.devicetopologyChart = graph;
         graph.render();
         graph.fitView();
        //  graph.on('node:mouseenter', (e) => {
        //    graph.setItemState(e.item, 'active', true);
        //  });
        //  graph.on('node:mouseleave', (e) => {
        //    graph.setItemState(e.item, 'active', false);
        //  });
        },
    },
       
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