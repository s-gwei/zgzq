<template>
  <a-card :bordered="false">
    <a-tabs defaultActiveKey="1">
      <a-tab-pane tab="过程监控" key="1">
<!--        <iframe src="../3DRoom/shk_mainGraph.html" frameborder="0" width="100%" height="800px" scrolling="auto"></iframe>-->
        <TabIframeView src="../3DRoom/shk_mainGraph.html"></TabIframeView>
      </a-tab-pane>

      <a-tab-pane tab="供电总图" key="2">

      </a-tab-pane>
    </a-tabs>
  </a-card>
</template>

<script>
  import TabIframeView from '@/components/layouts/TabIframeView'
  export default {
    name: 'shk_main',
    components: {
      TabIframeView
    },
    data() {
      return {

      }
    },
    created() {

    },
    methods: {

    }
  }
</script>
<style scoped>
  .ant-card-body .table-operator {
    margin-bottom: 18px;
  }

  .ant-table-tbody .ant-table-row td {
    padding-top: 15px;
    padding-bottom: 15px;
  }

  .anty-row-operator button {
    margin: 0 5px
  }

  .ant-btn-danger {
    background-color: #ffffff
  }

  .ant-modal-cust-warp {
    height: 100%
  }

  .ant-modal-cust-warp .ant-modal-body {
    height: calc(100% - 110px) !important;
    overflow-y: auto
  }

  .ant-modal-cust-warp .ant-modal-content {
    height: 90% !important;
    overflow-y: hidden
  }

  .statistic {
    padding: 0px !important;
    margin-top: 50px;
  }

  .statistic h4 {
    margin-bottom: 20px;
    text-align: center !important;
    font-size: 24px !important;;
  }

  .statistic #canvas_1 {
    width: 100% !important;
  }
</style>