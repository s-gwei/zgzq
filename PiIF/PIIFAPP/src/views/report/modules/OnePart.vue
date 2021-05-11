<template>
    <div class="onepart">
        <!-- <a-form-item :label="value.name" v-if="value.type!='table' && value.type!='textarea'"> -->
            <component :is="'My' + value.type.charAt(0).toUpperCase() + value.type.slice(1)" :value="value" v-if="value.type!='table' && value.type!='textarea'"></component>
        <!-- </a-form-item> -->
        <!-- <div v-else> -->
            <component :is="'My' + value.type.charAt(0).toUpperCase() + value.type.slice(1)" :value="value" v-else></component>
        <!-- </div> -->
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
    data(){
        return {
            tableColumns: {}
        }
    },
    mounted(){
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
/deep/.ant-col-sm-7{
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