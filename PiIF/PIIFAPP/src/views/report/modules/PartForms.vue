<template>
    <div class="form-container">
        <div class="form" >
            <div class="title">{{item.title}}</div>
            <div class="content">
            <a-form @submit="handleSubmit" :form="form" :label-col="labelCol" :wrapper-col="wrapperCol" >
              <a-row class="form-row" :gutter="16">
                   <template v-for="value in item.html">
                      <a-col :lg="12" :md="12" :sm="24" v-if="value.type !== 'table'  && value.type!='textarea'" :key="value.id" >
                          <one-part :value="value"></one-part>
                      </a-col>
                      <a-col :lg="24" :md="24" :sm="24" v-else :key="value.id">
                          <one-part :value="value"></one-part>
                      </a-col>
                  </template>
              </a-row>
            </a-form>
            </div>
        </div>
    </div>
</template>

<script>
import OnePart from './OnePart'
  export default {
    name: "PartForms",
    components: {OnePart},
    props: {
        item: {
            
        }
    },
    data () {
      return {
          form: this.$form.createForm(this),
          labelCol: {
            sm: { span: 7 },
          },
          wrapperCol: {
            sm: { span: 16 },
          },
          val: "14px",
          tableColumns: {}
      }
    },
    watch: {
      item: {
        handler(v){
        },
        deep: true
      }
     
    },
    mounted(){
    },
    methods: {
        handleSubmit(){

        },
        fontSize(val){
          this.val = "12px"
        }
    }
  }
</script>

<style lang="scss" scoped>
 .form-row{
     border: 1px solid rgba(216, 213, 213,.4);
     border-radius: 4px;
     margin-bottom: 6px;
     padding: 12px;
 }
 .title{
   font-weight: 700;
   margin: 8px 0 4px 0
 }
 .content{
   padding: 0 16px;
 }
 /deep/.ant-card-body{
   padding: 12px;
 }

</style>