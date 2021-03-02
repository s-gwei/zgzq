<template>
    <div class="entity">
      <a-radio-group name="radioGroup" :default-value="1" class="entityBottom">
        <a-radio :value="1">
          我
        </a-radio>
        <a-radio :value="2">
          其他实体
        </a-radio>
      </a-radio-group>

      <a-select
        style="width:100%"
        class="entityBottom"
        show-search
        :value="value"
        placeholder="搜索"
        :default-active-first-option="false"
        :show-arrow="false"
        :filter-option="false"
        :not-found-content="null"
      >
      </a-select>

      <a-collapse v-model="activeKey">
        <a-collapse-panel key="1" header="属性">
          <p> description <a-icon type="arrow-right" style="cursor: pointer;float:right;" /></p>
          <p> name <a-icon type="arrow-right" style="cursor: pointer;float:right;" /></p>
          <p> tags <a-icon type="arrow-right" style="cursor: pointer;float:right;" /></p>
        </a-collapse-panel>
        <a-collapse-panel key="2" header="服务">
          <p> AcknowledgeAlert <a-icon type="arrow-right" style="cursor: pointer;float:right;" /></p>
          <p> AddBooleanValueStreamEntry <a-icon type="arrow-right" style="cursor: pointer;float:right;" /></p>
        </a-collapse-panel>
        <a-collapse-panel key="3" header="事件">
          <p> alert <a-icon type="arrow-right" style="cursor: pointer;float:right;" /></p>
          <p> add <a-icon type="arrow-right" style="cursor: pointer;float:right;" /></p>
          <p> update <a-icon type="arrow-right" style="cursor: pointer;float:right;" /></p>
        </a-collapse-panel>
      </a-collapse>
    </div>
</template>

<script>
  export default {
    name: 'entity',
    data() {
      return {
        value: '',
        activeKey: ''
      }
    },
    methods: {}
  }
</script>

<style scoped>
  .entityBottom{
    margin-bottom: 20px;
  }
</style>