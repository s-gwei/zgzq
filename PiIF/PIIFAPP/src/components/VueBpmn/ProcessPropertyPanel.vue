<template>
  <div class="property-panel" ref="processPropertyPanel">
    <a-form :model="processData"  layout="horizontal" :label-col="labelCol" :wrapper-col="wrapperCol">
      <a-form-item label="流程名称">
        <a-input v-model="processData.name" disabled></a-input>
      </a-form-item>
      <a-form-item label="流程Key">
        <a-input v-model="processData.id" disabled></a-input>
      </a-form-item>
      <a-form-item label="流程类型">
        <!-- <a-input v-model="processData.description" disabled></a-input> -->
       
        <a-select placeholder="请选择" v-model="processData.processType">
            <a-select-option allowClear v-for="item in processTypeList" :key="item.id" :value="item.id">
              {{ item.name }}
            </a-select-option>
        </a-select>
           
      </a-form-item>
    </a-form>
  </div>
</template>

<script>
  export default {
    name: "ProcessPropertyPanel",
    props: {
      processData: {
        type: Object,
        required: true
      }
    },
    data() {
      return {
        labelCol: { span: 7 },
        wrapperCol: { span: 18 },
        process: {
          name: this.processData.name,
          id: '',
          description: '',
          processType:''
        },
        processTypeList:[{name:"测试流程",id:'test'},{name:"OA办公",id:'OA'},{name:"业务办理",id:'yw'}],//流程类型列表
      }
    }
  }
</script>

<style lang="scss" scoped>
  .property-panel {
    padding: 10px;
    width: 100%;
  }
  .ant-row{
    display: flex !important;
  }
  .ant-form-item{
    margin-bottom: 10px;
  }
</style>
