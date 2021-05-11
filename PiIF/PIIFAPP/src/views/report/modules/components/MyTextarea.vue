<template>
    <div class="textareaWapper">
        <div class="label">{{formParam.name}}:</div>
        <div class="areaCon">
            <a-textarea  @blur="blur" :placeholder="'请输入' + formParam.name" v-model="formParam.value" :autoSize="{ minRows: 3, maxRows: 8 }"/>
        </div>
    </div>
</template>
<script>
export default {
    name: "MyTextarea",
    props: {
        value: {
            type: Object,
            default: ()=>{
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
        document.querySelector('.label').style.width =(document.querySelector('.textareaWapper').offsetWidth + 16 ) / 48 * 9 + "px"
        this.formParam = JSON.parse(JSON.stringify(this.value))
    },
    watch: {
        'formParam.value': {
            handler(val){
                // this.formParam = JSON.parse(JSON.stringify(val))
                // this.value = JSON.parse(JSON.stringify(val))
            },
            deep: true
        }
    },
    methods: {
        blur(){
            this.$bus.$emit('changes',this.formParam)
        }
    }
}
</script>
<style lang="scss" scoped>
.textareaWapper{
    display: flex;
    // box-sizing: border-box;
    .label{
        // width: 9/48 * 100%;
        color: rgba(0, 0, 0, 0.85);
    }
    .areaCon{
        width: 114%;
        // padding: 0 8px;
    }
}
</style>