<template>
    <!-- <div>{{value}}</div> -->
    <!-- <a-form-item :label="value.name"> -->
        <a-radio-group @change="handleInput" :disabled="formParam.disabled" v-model="formParam.value">
            <a-radio v-for="(item, key) in formParam.select" :key="key" :value="item.value">{{ item.text }}</a-radio>
        </a-radio-group>
    <!-- </a-form-item> -->
</template>
<script>
export default {
    name:"MyRadio",
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