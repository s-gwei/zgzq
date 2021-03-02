<template>
  <a-modal
    title="修改权重"
    :visible="visible"
    :width="600"
    @ok="handleOk"
    :confirmLoading="confirmLoading"
    @cancel="cancel"
  >
    <a-table :columns="columns" rowKey="id" :dataSource="PmList" :pagination="false">
      <span slot="qweight" slot-scope="text, record">
          <a-input-number
            v-model="record.qweight"
            :min="0"
            :max="100"
            :step="0.01"
            :precision="2"
            :formatter="value => `${value ? value : 0}%`"
            :parser="value => value.replace('%', '')"
          />
      </span>
    </a-table>
  </a-modal>

</template>

<script>
  import {putAction} from '@/api/manage'
  import { accAdd } from '@/utils/util'
  export default {
    data () {
      return {
        visible: false,
        confirmLoading: false,
        columns: [
          {
            title: '#',
            dataIndex: '',
            key: 'rowIndex',
            width: 60,
            align: "center",
            customRender: function (t, r, index) {
              return parseInt(index) + 1;
            }
          },
          {
          title: 'PM',
          dataIndex: 'evaluatorRealname',
          key: 'id',
          width: '70%'
        },
          {
            title: '权重',
            dataIndex: 'qweight',
            key: 'qweight',
            scopedSlots: { customRender: 'qweight' },
          }],
        PmList: [],
        url: {
          edit: '/investigation/relationship/editQWeight'
        }
      }
    },
    methods: {
      edit (list) {
        if (list && list.length > 0) {
          this.PmList = list
          this.visible = true
        } else {
          this.$message.waiting('暂无pm评价关系')
        }
      },
      handleOk: function () {
        let sum = 0
        this.PmList.forEach((v) => {
            sum = accAdd(sum , v.qweight)
        })
       if (sum == 100) {
          let arr = []
         this.PmList.forEach((v) => {
            arr.push({id: v.id, qweight: v.qweight})
         })
         putAction(this.url.edit, arr).then((res) => {
           if (res.success) {
             this.$message.success(res.message)
             this.cancel()
           } else {
             this.$message.error(res.message)
           }
          }
         )
       } else {
          this.$message.warning('PM权限相加应为100%')
       }
      },
      cancel: function () {
        this.PmList = []
        this.visible = false
        this.$emit('refresh')
      },
    }
  }
</script>