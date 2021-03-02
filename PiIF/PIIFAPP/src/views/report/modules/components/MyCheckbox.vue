<template>
    <!-- <div> -->
        <a-checkbox-group :options="formParam.select" :placeholder="'请输入' + formParam.name"  :disabled="formParam.disabled" v-model="formParam.value" @change="handleInput" />
    <!-- </div> -->
</template>
<script>
export default {
    name: "MyCkeckbox",
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
        handleInput(val){
            // console.log(this.formParam);
            this.$bus.$emit('changes',this.formParam)
        }
    }
    
}
</script>
<style lang="sass" scoped>

</style>