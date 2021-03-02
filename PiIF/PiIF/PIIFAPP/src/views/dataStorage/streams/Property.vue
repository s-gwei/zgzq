<template>
  <div class="Property">
    <!--tab头-->
    <a-row :gutter="48" type="flex"  style="border-bottom: 1px solid #f1f1f1;padding-bottom: 10px;margin: 10px 0 10px 0;">
      <a-col>
        <div style=" font-size: 18px;color:#128DFF;line-height: 40px;">属性</div>
      </a-col>
    </a-row>
  </div>
</template>

<script>
  import STable from '@/components/table/'
  export default {
    data () {
      return {

      }
    },
    components: {
      STable
    }
  }
</script>


