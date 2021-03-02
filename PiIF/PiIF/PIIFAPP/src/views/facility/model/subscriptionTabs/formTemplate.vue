<template>
    <div class="formTemplate">
      <a-form :form="form" v-for="(f, index) in formList" >
        <h3>{{f[0].classify}}</h3>
        <a-form-item
          v-for="(item, index) in f"
         :label="item.label"
         :labelCol="labelCol"
         :wrapperCol="wrapperCol">
          <a-input
            :key="item.label"
            v-decorator="[
            item.field,
            { rules: item.rules },
          ]"></a-input>
        </a-form-item>

      </a-form>
    </div>
</template>

<script>
  export default {
    name: 'formTemplate',
    data() {
      return {
        form: this.$form.createForm(this),
        labelCol: {
          xs: { span: 24 },
          sm: { span: 4 },
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 },
        },
      }
    },
    props: {
      formList: {
        type: Array,
        default: () => {
          return []
        }
      }
    },
    watch: {
      formList: function(val) {
        console.log(val)
      }
    },
    mounted () {
    },
    methods: {
      handleSubmit () {
        this.form.validateFields((err, values) => {
          if (!err) {
            console.log(values)
            this.$emit('formValues', values)
          }
        })
      }
    }
  }
</script>

<style scoped>

</style>