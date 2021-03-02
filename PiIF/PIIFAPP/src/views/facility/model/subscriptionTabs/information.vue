<template>
    <div class="information">
      <a-form :form="form" @submit="handleSubmit" ref="subForm">
        <a-form-item
          label="订阅名称"
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
        >
          <a-input
            placeholder="请输入订阅名称"
            v-decorator="['name', { rules: [{ required: true, message: '请输入订阅名称' }] }]"
            :disabled="display"
          />
        </a-form-item>
        <a-form-item
          label="订阅描述"
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
        >
          <a-textarea :disabled="display"  v-decorator="['description']" placeholder="请输入描述" :rows="3" />
        </a-form-item>
        <a-form-item
          label="选择设备和属性"
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          v-show="!display"
        >
          <a-cascader
            :options="thingList"
            :load-data="loadData"
            placeholder="请选择设备和属性"
            change-on-select
            v-decorator="[
            'facAndPreList',
            { rules: [{ required: true, message: '请选择设备和属性' }] },
          ]"
            @change="infoOnChange"
          />
        </a-form-item>
        <a-form-item
          label="触发方式"
          hasFeedback
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
        >
          <a-select
            :disabled="display"
            placeholder="请选择触发方式"
            v-decorator="[
            'event',
            { rules: [{ required: true, message: '请选择触发方式' }] },
          ]"
            >
            <a-select-option v-for="(item, idx) in eventList" :key="idx" :value="item.value">{{item.name}}</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item
          label="事件"
          hasFeedback
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
        >
          <a-select
            :disabled="display"
            placeholder="请选择订阅事件"
            v-decorator="[
            'subPEvent',
            { rules: [{ required: true, message: '请选择订阅事件' }] },
          ]"
            show-search>
            <a-select-option v-for="(item, idx) in proEventList" :key="item.serialNumber" :value="item.serialNumber">{{item.name}}</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item
          label="启用"
          validateStatus="success"
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
        >
          <a-checkbox
                      v-decorator="[
            'whetherStart',
            { rules: [{ required: false }], valuePropName: 'checked' }
          ]"
          ></a-checkbox>
        </a-form-item>
      </a-form>
    </div>
</template>

<script>
  import {getAction} from '@/api/manage'
  import pick from 'lodash.pick'
  export default {
    name: 'information',
    inject: ['ModelInfo'],
    data() {
      return {
        labelCol: {
          xs: { span: 24 },
          sm: { span: 5 },
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 },
        },
        eventList: [
          {name: 'Alert', value: 'Alert'},
        ],
        thingList: [],
        proEventList: [],
        display: false,
        url: {
          thingList: '/thing/list',
          propertyList: '/thing/property/list2',
          propertyEvent: '/incident/listPageThing'
        },
        form: this.$form.createForm(this)
      }
    },
    computed: {
      userId () {
        return this.$store.state.user.info.id
      }
    },
    created () {
      this.getFacilityList()
    },
    methods: {
      emptyForm () {
        this.$refs.subForm.form.resetFields()
      },
      /** thing **/
      getFacilityList () {
        this.thingList = []
        getAction(this.url.thingList, {userId: this.userId}).then((res) => {
          if (res.success) {
            res.result.map((v) => {
              this.thingList.push({ value: v.name, label: v.name, isLeaf: false})
            })
          } else {
            this.$message.error(res.message)
          }
        })
      },
      infoOnChange (value) {
        if (value && value.length > 1) {
          this.getAlarmEvent(value[0], value[1])
        }
      },
      /** 动态取属性 **/
      loadData(selectedOptions) {
        const targetOption = selectedOptions[selectedOptions.length - 1];
        targetOption.loading = true;

        getAction(this.url.propertyList, {thingName: targetOption.value})
          .then((res) => {
            if (res.success) {
              let pArr = []
              res.result[0].map((v) => {
                pArr.push({ value: v.name, label: v.name})
              })
              targetOption.children = [...pArr]
              targetOption.loading = false;
            } else {
              this.$message.error(res.message);
            }
          })
      },
      /** 取属性下事件 **/
      getAlarmEvent (thingName, PropertyName) {
        getAction(this.url.propertyEvent, { thingName, PropertyName }).then(
          (res) => {
            if (res.success) {
              this.proEventList = res.result
            } else {
              this.$message.error(res.message);
            }
          }
        )
      },
      handleEditInfo (data) {
        this.display = true
        let { name, incident, description, whetherStart } = data
        let obj = {
          name,
          description,
          subPEvent: incident,
          event: 'Alert',
          whetherStart: whetherStart === '1' ? true : false
        }
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(obj, 'name', 'description', 'subPEvent', 'event', 'whetherStart'))
        })
      },
      handleSubmit () {
        this.form.validateFields((err, values) => {
          if (this.display) {
            this.$emit('informationEdit', values)
          }
          if (!err) {
            let incident = this.proEventList.find((v) => (v.serialNumber === values.subPEvent)).name
            values.incident = incident
              this.$emit('information', values)
          }
        });
      }
    }
  }
</script>

<style scoped>

</style>