<template>
    <div id="container"></div>
</template>

<script>
import G6 from '@antv/g6';
  export default {
    name: "testTable",
    data () {
      return {
        data: {
  "id": "Modeling Methods",
  "children": [
    {
      "id": "Classification",
      "children": [
        { "id": "Logistic regression" },
        { "id": "Linear discriminant analysis" },
        { "id": "Rules" },
        { "id": "Decision trees" },
        { "id": "Naive Bayes" },
        { "id": "K nearest neighbor" },
        { "id": "Probabilistic neural network" },
        { "id": "Support vector machine" }
      ]
    },
    {
      "id": "Consensus",
      "children": [
        {
          "id": "Models diversity",
          "children": [
            { "id": "Different initializations","deep":true },
            { "id": "Different parameter choices" ,"deep":true},
            { "id": "Different architectures","deep":true },
            { "id": "Different modeling methods" ,"deep":true},
            { "id": "Different training sets","deep":true },
            { "id": "Different feature sets","deep":true }
          ]
        },
        {
          "id": "Methods",
          "children": [
            { "id": "Classifier selection" ,"deep":true},
            { "id": "Classifier fusion" ,"deep":true}
          ]
        },
        {
          "id": "Common",
          "children": [
            { "id": "Bagging" ,"deep":true},
            { "id": "Boosting" ,"deep":true},
            { "id": "AdaBoost" ,"deep":true}
          ]
        }
      ]
    },
    {
      "id": "Regression",
      "children": [
        { "id": "Multiple linear regression" ,"deep":true},
        { "id": "Partial least squares" ,"deep":true},
        { "id": "Multi-layer feedforward neural network" ,"deep":true},
        { "id": "General regression neural network" ,"deep":true},
        { "id": "Support vector regression","deep":true }
      ]
    }
  ]
}

         
      }
    },
    mounted(){
      const container = document.getElementById('container');
    const width = container.scrollWidth;
    const height = container.scrollHeight || 500;
    const graph = new G6.TreeGraph({
      container: 'container',
      width,
      height,
      linkCenter: true,
      modes: {
        // default: [
        //   {
        //     // type: 'collapse-expand',
        //     // onChange: function onChange(item, collapsed) {
        //     //   const data = item.get('model');
        //     //   data.collapsed = collapsed;
        //     //   return true;
        //     // },
        //     type: 'toltip',
        //     formatText: (data) => {
        //       // data 对应后台返回对应节点的数据，根据需要做展示
        //         return `<div>ID: 12</div>
        //               <div>Name: 34</div>`
        //      }
        //   },
        //   'drag-canvas',
        //   'zoom-canvas',
        // ],
        default: ['drag-node', 'zoom-canvas', {
      // 以下是自定义提示的重点
      type: 'tooltip',
      formatText: (data) => {
        console.log(data);
       // data 对应后台返回对应节点的数据，根据需要做展示
         if(data.deep){
           return `<div>ID: ${data.id}</div>
               <div>Name:</div>`
         }
      }
    }]
      },
      defaultNode: {
        size: 26,
      },
      layout: {
        type: 'dendrogram',
        direction: 'LR',
        nodeSep: 20,
        rankSep: 100,
        radial: true,
      },
    });

    graph.node(function (node) {
      return {
        label: node.id,
      };
    });

    graph.data(this.data);
    graph.render();
    graph.fitView();

    if (typeof window !== 'undefined')
      window.onresize = () => {
        if (!graph || graph.get('destroyed')) return;
        if (!container || !container.scrollWidth || !container.scrollHeight) return;
        graph.changeSize(container.scrollWidth, container.scrollHeight);
      };
    }, 
    methods: {
   
    }
  }
</script>

<style scoped>

</style>