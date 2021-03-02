<template>
  <a-card :bordered="false">
<!--    <div style="height: 60px;background-image: url('~@/assets/coronavirus_head.jpg')"></div>-->
<!--    <img src="~@/assets/coronavirus_head.jpg" style="height: 80px;width: 100%">-->
    <a-tabs defaultActiveKey="1">
      <a-tab-pane tab="疫情实时动态地图" key="1">
        <!--        <iframe src="../3DRoom/shk_mainGraph.html" frameborder="0" width="100%" height="800px" scrolling="auto"></iframe>-->
        <TabIframeView src="../3DRoom/coronavirus.html"></TabIframeView>
      </a-tab-pane>

      <a-tab-pane tab="PISX新冠疫情防控" key="2">
        <PISXEpi></PISXEpi>
      </a-tab-pane>
      <a-tab-pane tab="疫情期间员工分布图" key="3">
        <EmployeeDis></EmployeeDis>
      </a-tab-pane>
      <a-tab-pane tab="疫情期间员工流动图" key="4">
        <EmployeeFlow></EmployeeFlow>
      </a-tab-pane>
    </a-tabs>
  </a-card>
</template>

<script>
  import TabIframeView from '@/components/layouts/TabIframeView'
  import EmployeeDis from '@views/coronavirus/EmployeeDistribute'
  import EmployeeFlow from './EmployeeFlow.vue'
  import PISXEpi from './PISXEpidemicSituation.vue'
  import { Scene, LineLayer } from '@antv/l7';
  import { Mapbox } from '@antv/l7-maps';
  export default {
    name: 'DynamicMap',
    components: {
      TabIframeView,
      EmployeeDis,
      EmployeeFlow,
      PISXEpi,
    },
    data() {
      return {
      }
    },
    created() {

    },
    mounted(){

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