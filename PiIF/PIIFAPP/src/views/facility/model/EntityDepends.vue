<template>
  <div class="EntityDepends">
    <!--tab头-->
    <a-row :gutter="48" type="flex"  style="border-bottom: 1px solid #f1f1f1;padding-bottom: 10px;margin: 10px 0 10px 0;">
      <a-col>
        <div style=" font-size: 18px;color:#128DFF;line-height: 40px;">使用关系</div>
      </a-col>
    </a-row>
    <a-row>
      <a-col :span="24">
        <relationCharts id="EntityDepends" :chartsData="chartsData"></relationCharts>
      </a-col>
    </a-row>
  </div>
</template>

<script>
  import {getAction} from '@/api/manage'
  import relationCharts from '@/components/echarts/relationCharts'
  export default {
    data () {
      return {
        ModelInfo: {},
        type: '',
        thingName: '',
        chartsData: {},
        url: {
          data: '/thingTemplate/getTree',
          data1: '/thing/getTree'
        }
      }
    },
    components: {
      relationCharts
    },
    created () {
      this.ModelInfo = JSON.parse(sessionStorage.getItem('ModelInfo'))
      this.type = this.ModelInfo.type
      this.thingName = this.ModelInfo.name
      this.getChartsData()
    },
    methods: {
      getChartsData () {
        let params = {}
        let url = ''
        if (this.type === 'obj') {
          url = this.url.data
          params = {
            templateName: this.thingName,
            flag: true
          }
        } else {
          url = this.url.data1
          params = {
            thingName: this.thingName,
            flag: true
          }
        }
        getAction(url, params).then((res) => {
          if (res.success) {
            let charts = JSON.parse(JSON.stringify(res.result).replace(/nodes/g, 'children'))
            console.log(charts)
            this.chartsData = charts
          } else {
            this.$message.error(res.message);

          }
        })
      }
    }
  }
</script>
