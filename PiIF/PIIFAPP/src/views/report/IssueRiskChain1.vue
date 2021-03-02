<template>
      <div>
          <div id="container"></div>
          <div id="container1"></div>
      </div>
</template>

<script>
import G6 from '@antv/g6';
  export default {
    name: "testTable",
    data () {
      return {
        data: {
          id: 'A',
          level: 'middle',
          children: [
            {
              id: 'A1',
              level: 'middle',
              children: [{ id: 'A11' ,level: 'middle',description: '介绍',description: 'This is edge from node 0 to node 2.'}, { id: 'A12',level: 'middle' }, { id: 'A13',level: 'low' }, { id: 'A14',level: 'low' }],
            },
            {
              id: 'A2',
              level: 'middle',
              children: [
                {
                  id: 'A21',
                  level: 'middle',
                  children: [{ id: 'A211',level: 'low',description: 'This is edge from node 0 to node 2.' }, { id: 'A212' ,level: 'middle'}],
                },
                {
                  id: 'A22',
                  description: '123',
                  level: 'low'
                },
              ],
            },
            {
              id: 'A3',
              level: 'middle',
              children: [
                {
                  id: 'A31',
                  level: 'middle',
                  children: [{ id: 'A311',level: 'low',description: 'This is edge from' }, { id: 'A312' ,level: 'middle'}],
                },
                {
                  id: 'A32',
                  description: '测试',
                  level: 'low',
                  children: [
                    {
                      id: 'A301',
                      level: 'middle',
                      children: [{ id: 'A3011',level: 'low',description: 'This is edge from' }, { id: 'A3120' ,level: 'middle'}],
                    },
                    {
                      id: 'A320',
                      description: '测试',
                      level: 'low',
                    },
                  ]
                },
              ],
            },
          ],
        }

         
      }
    },
    mounted(){
      G6.registerNode('card-node', {
  draw: function drawShape(cfg, group) {
    const r = 2;
    const color = '#5B8FF9';
    const w = cfg.size[0];
    const h = cfg.size[1];
    // const w = 300,h=40;
    const edgeCircleColorArr = ['#f30','#eee'];
    const colors = {
      "low": 'red',
      "middle": '#40a9ff'
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
      },
      name: 'main-box',
      draggable: true,
    });
    group.addShape('rect', {
      attrs: {
        x: -w / 2,
        y: -h / 2,
        width: w, //200,
        height: h / 2, // 60
        // fill: edgeCircleColorArr[0],
        fill: colors[cfg.level],
        radius: [r, r, 0, 0],
      },
      name: 'title-box',
      draggable: true,
    });

    // title text
    group.addShape('text', {
      attrs: {
        textBaseline: 'top',
        x: -w / 2 + 8,
        y: -h / 2 + 8,
        lineHeight: 30,
        fontSize: 16,
        text: cfg.id,
        fill: '#fff',
      },
      name: 'title',
    });
    // console.log(cfg);
    // cfg.children &&
    //   group.addShape('marker', {
    //     attrs: {
    //       x: w / 2,
    //       y: 0,
    //       r: 6,
    //       cursor: 'pointer',
    //       symbol: G6.Marker.collapse,
    //       stroke: '#666',
    //       lineWidth: 1,
    //       fill: 'green',
    //     },
    //     name: 'collapse-icon',
    //   });
    group.addShape('text', {
      attrs: {
        textBaseline: 'top',
        x: -w / 2 + 8,
        y: -h / 2 + 46,
        // text: 'description',
        text: cfg.description,
        fontSize: 14,
        fill: 'black',
      },
      name: `description`,
    });
    return shape;
  },
  setState(name, value, item) {
    if (name === 'collapsed') {
      const marker = item.get('group').find((ele) => ele.get('name') === 'collapse-icon');
      const icon = value ? G6.Marker.expand : G6.Marker.collapse;
      marker.attr('symbol', icon);
    }
  },
});

// tooltip
const tooltip = new G6.Tooltip({
  // offsetX and offsetY include the padding of the parent container
  // offsetX 与 offsetY 需要加上父容器的 padding
  offsetX: 10,
  offsetY: 50 + 10,
  // the types of items that allow the tooltip show up
  // 允许出现 tooltip 的 item 类型
  itemTypes: ['node', 'edge'],
  // custom the tooltip's content
  // 自定义 tooltip 内容
  getContent: (e) => {
    const outDiv = document.createElement('div');
    outDiv.style.width = 'fit-content';
    //outDiv.style.padding = '0px 0px 20px 0px';
    outDiv.innerHTML = `
      <h4>Custom Content</h4>
      <ul>
        <li>Label: ${e.item.getModel().id}</li>
      </ul>
      <ul>
        <li>Type: ${e.item.getModel().description}</li>
      </ul>`;
    return outDiv;
  },
  shouldBegin: (e) => {
    // console.log(e.item);
    let res = true;
    switch (e.item.getModel().id) {
      case '1':
        res = false;
        break;
      case '2':
        if (e.target.get('name') === 'text-shape') res = true;
        else res = false;
        break;
      case '3':
        if (e.target.get('name') !== 'text-shape') res = true;
        else res = false;
        break;
      default:
        res = true;
        break;
    }
    return res;
  },
});
    const container = document.getElementById('container');
    console.log(container);
    const width = document.body.clientWidth;
    const height =  document.body.clientHeight;
    console.log(document.body.clientHeight,container.scrollHeight);
    const graph = new G6.TreeGraph({
           container: 'container',
           width,
           height,
           linkCenter: true,
           plugins: [tooltip],
           modes: {
             default: ['drag-canvas'],
           },
           defaultNode: {
             type: 'card-node',
             size: [220, 70],
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
             indent: 300,
             getHeight: () => {
               return 60;
             },
           },
     });
    graph.data(this.data);
    graph.render();
    graph.fitView();
    graph.on('node:mouseenter', (e) => {
      graph.setItemState(e.item, 'active', true);
    });
    graph.on('node:mouseleave', (e) => {
      graph.setItemState(e.item, 'active', false);
    });

    if (typeof window !== 'undefined')
      window.onresize = () => {
        if (!graph || graph.get('destroyed')) return;
        if (!container || !container.scrollWidth || !container.scrollHeight) return;
        console.log(document.body.clientHeight,container.scrollHeight);
        graph.changeSize(container.scrollWidth, container.scrollHeight);
      };
    }, 
    methods: {
   
    }
  }
</script>

<style scoped>

</style>