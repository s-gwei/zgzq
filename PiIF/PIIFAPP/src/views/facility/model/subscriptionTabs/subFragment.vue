<template>
    <div class="subFragment">
      <a-input placeholder="搜索" @change="subRearch" v-model="subValue" style="margin-bottom: 20px;"/>

      <div class="listbox">
        <a-list size="small" bordered :data-source="serviceList">
          <a-list-item slot="renderItem" slot-scope="item, index">
            <a-checkbox @change="addFormInput(item.type, item.checked)" :checked="item.checked">
              {{ item.hint }}
            </a-checkbox>
          </a-list-item>
        </a-list>
      </div>
      <div class="inputbox">
        <formTemplate :formList="formList" ref="formTemplate" @formValues="formValues"></formTemplate>
      </div>
    </div>
</template>

<script>
  import formTemplate from './formTemplate'
  import {getAction, deleteAction} from '@/api/manage'
  export default {
    name: 'subFragment',
    data() {
      return {
        value: '',
        serviceList: [],
        subValue: '',
        formList: [],
        formObj: {
          phone: [{type: 'input', label: '手机号', classify: 'phone', field: 'receptionPhone', rules: [
              {
                pattern: /^1[3-9]\d{9}$/,
                message: '请输入正确手机号',
              },
              {
                required: true,
                message: '请输入手机号',
              },
            ]}],
          email: [{type: 'input', label: '邮箱', classify: 'email', field: 'addresseeEmail', rules: [
              {
                type: 'email',
                message: '请输入正确邮箱',
              },
              {
                required: true,
                message: '请输入邮箱',
              },
            ]}]
        },
        url: {
          list: '/incident/listServeInformation'
        }
      }
    },
    components: {
      formTemplate
    },
    mounted () {
      this.formList = []
      this.subRearch()
    },
    methods: {
      subRearch () {
        getAction(this.url.list, { hint: this.subValue}).then((res) => {
          if (res.success) {
            let subList = res.result.records
            subList.forEach((v) => (v.checked = false))
            this.serviceList = subList
          } else {
            this.$message.error(res.message)
          }
        })
      },
      addFormInput (value, c) {
        let checked = this.serviceList.find((t) => (t.type === value))
        this.$set(checked, 'checked', !c)
        const formObj = this.formList.find((v) => (v.classify === value))
        const flag = this.formList.indexOf(formObj)
        if (!checked.checked) {
             this.formList.splice(flag, 1)
        } else {
          if (this.formList.length > 0 && formObj) {
            return
          } else {
            this.formList.push(this.formObj[value])
          }
        }
      },
      handleSubmit () {
        this.$refs.formTemplate.handleSubmit()
      },
      formValues (values) {
        console.log(values)
      },
      emptyFragment () {
        this.formList = []
      }
    }
  }
</script>

<style scoped>
  .listbox{
    height:260px;
    overflow-y: auto;
  }
  .inputbox{

  }
</style>