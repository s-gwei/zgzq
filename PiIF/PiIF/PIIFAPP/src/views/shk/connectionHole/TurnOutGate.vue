<template>
  <a-card :bordered="false">
    <a-tabs defaultActiveKey="1">
      <a-tab-pane tab="过程监控" key="1">
        <TabIframeView src="../../3DRoom/kongzhizha_graph.html"></TabIframeView>
      </a-tab-pane>

      <a-tab-pane tab="控制" key="2">
        <TabIframeView src="../../3DRoom/fsz_control.html"></TabIframeView>
      </a-tab-pane>
    </a-tabs>
  </a-card>
</template>

<script>
  import TabIframeView from '@/components/layouts/TabIframeView'
  export default {
    name: 'TurnOutGate',
    components:{
      TabIframeView
    }
  }
</script>

<style scoped>

</style>