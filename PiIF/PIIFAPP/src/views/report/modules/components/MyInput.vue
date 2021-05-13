<template>
    <a-form-item :label="value.name">
      <a-input
      v-if="this.value.validator"
      :class="value.field"
       @blur="keyup"
        :disabled="value.disabled ? value.disabled : false"
        :placeholder="'请输入'+value.name"
         v-model="formParam.value"
          :addonAfter="value.unit"
          v-decorator="[value.field, { rules }]"
        />
        <a-input v-else
          :class="value.field"
          @blur="keyup"
          :disabled="value.disabled ? value.disabled : false"
          :placeholder="'请输入'+value.name"
          v-model="formParam.value"
          :addonAfter="value.unit"
        />
    </a-form-item>
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
            formParam: {},
            rules:  [
                { 
                    validator: (rule, values, cbfn) => {
                        const validator = this.value.validator
                        console.log(values,validator);
                        if(!values){
                        } else{
                            if(validator.type == "Number"){
                                if(validator.regex && validator.regex.length){
                                    var min = validator.regex[0] || 0
                                    var max = validator.regex[1] || 100000
                                     if(/^[+-]?\d+(\.\d+)?$|^$|^(\d+|\-){7,}$/.test(values)){
                                         if(values < min || values > max){
                                             cbfn('请输入' + min + '-' + max + '范围的数')
                                         }
                                     } else{
                                         cbfn('请输入' + min + '-' + max + '范围的数')
                                     }

                                } else{
                                    if(/^[+-]?\d+(\.\d+)?$|^$|^(\d+|\-){7,}$/.test(values)){
                                    //   cbfn()
                                    } else{
                                      cbfn('请输入浮点数')
                                    }
                                }
                            }if(validator.type == "String"){
                                const max  = validator.regex
                                if(values.length > max){
                                    cbfn('请输入最大长度为'+max+'的字符串')
                                }
                            }
                        }
                       cbfn()
                     }
                }
            ]
        }
    },
     mounted(){
        console.log('value');
        if(this.value){
            // console.log('enter');
            // this.$set(this,"formParam",this.value)
            this.formParam = JSON.parse(JSON.stringify(this.value))
        }
    },
    watch: {
        value: {
            handler(val){
                console.log(val,'v');
                this.formParam = JSON.parse(JSON.stringify(val))
            },
            deep: true
        }
    },
    methods: {
        keyup(){
            this.$bus.$emit('changes',this.formParam)
        },
    }
}
</script>
<style lang="scss" scoped>

</style>