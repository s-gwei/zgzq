<template>
    <a-form-item :label="value.name">
        <a-select :placeholder="'请输入' + formParam.name"  :disabled="formParam.disabled" v-model="formParam.value" @change="handleInput">
            <a-select-option value="">请选择</a-select-option>
            <a-select-option v-for="(item, key) in formParam.select" :key="key" :value="item.value">
              <span style="display: inline-block;width: 100%" :title=" item.text || item.label ">
                {{ item.text || item.label }}
              </span>
            </a-select-option>
        </a-select>
    </a-form-item>
</template>
<script>
export default {
    name: "MySelect",
    props: {
        value: {

        }
    },
    data(){
        return {
            formParam: {}
        }
    },
    mounted(){
        if(this.value){
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
        handleInput(val){
            // console.log(this.formParam);
            this.$bus.$emit('changes',this.formParam)
        }
    }
    
}
</script>
<style lang="sass" scoped>

</style>