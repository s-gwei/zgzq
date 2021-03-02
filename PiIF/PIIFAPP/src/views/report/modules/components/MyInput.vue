<template>
    <!-- <a-form-item :label="value.name"> -->
      <a-input
       @blur="keyup"
        :disabled="value.disabled ? value.disabled : false"
        :placeholder="'请输入' + value.name"
         v-model="formParam.value"
            :addonAfter="value.dw"
        />
    <!-- </a-form-item> -->
</template>
<script>
export default {
    name: "MyInput",
    props: {
        value: {
            type: Object,
            default(){
                return {}
            }
        }
    },
    data(){
        return {
            formParam: {}
        }
    },
     mounted(){
        if(this.value){
            // console.log('enter');
            // this.$set(this,"formParam",this.value)
            this.formParam = JSON.parse(JSON.stringify(this.value))
        }
    },
    watch: {
        value: {
            handler(val){
                this.formParam = JSON.parse(JSON.stringify(val))
            },
            deep: true
        }
    },
    methods: {
        keyup(){
            this.$bus.$emit('changes',this.formParam)
        }
    }
}
</script>
<style lang="scss" scoped>

</style>