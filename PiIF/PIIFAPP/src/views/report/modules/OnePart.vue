<template>
    <div class="onepart">
        <a-form-item :label="value.name" v-if="value.type!='table' && value.type!='textarea'">
            <component :is="'My' + value.type.charAt(0).toUpperCase() + value.type.slice(1)" :value="value"></component>
        </a-form-item>
        <div v-else>
            <component :is="'My' + value.type.charAt(0).toUpperCase() + value.type.slice(1)" :value="value"></component>
        </div>
    </div>
</template>
<script>
import {MyInput,MyRadio,MySelect, MyCheckbox,MyTable,MyTextarea} from './components'
export default {
    props: {
        value: {

        }
    },
    components: {
         MyInput,
         MyRadio,
         MySelect,
         MyCheckbox,
         MyTable,
         MyTextarea
    },
    mounted(){
        // console.log(this.value,'value');
        this.value.name.length >=8 ? this.value = Object.assign(this.value,{fontSize: "12px"}) : this.value

    },
    watch: {
        // "value.name"(){
        //     console.log(val);
        // }
    }
    
}
</script>
<style lang="scss" scoped>
/deep/.ant-col-sm-9{
    // padding-left: 6px;
    text-align:left;
    font-size: 14px;
}
.onepart{
    /deep/ .ant-form-item{
         margin-bottom: 10px;
    }
}

</style>