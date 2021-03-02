<template>
  <div>
      <a-button :disabled="disabled" :type="buttonType" @click="onSearchDepUser">添加用户</a-button>
    <j-select-user-by-dep-modal ref="selectModal" :modal-width="modalWidth" :multi="multi" @ok="selectOK" :user-ids="value" @initComp="initComp"/>
  </div>
</template>

<script>
  import JSelectUserByDepModal from './modal/JSelectUserByDepModal'

  export default {
    name: 'JSelectUserByDep',
    components: {JSelectUserByDepModal},
    props: {
      modalWidth: {
        type: Number,
        default: 1250,
        required: false
      },
      value: {
        type: String,
        required: false
      },
      disabled: {
        type: Boolean,
        required: false,
        default: false
      },
      multi: {
        type: Boolean,
        default: true,
        required: false
      },
      buttonType: {
        type: String,
        default: 'primary'
      }
    },
    data() {
      return {
        userIds: "",
        userNames: "",
        ids: []
      }
    },
    mounted() {
      this.userIds = this.value
    },
    watch: {
      value(val) {
        this.userIds = val
      }
    },
    model: {
      prop: 'value',
      event: 'change'
    },
    methods: {
      initComp(userNames) {
        this.userNames = userNames
      },
      onSearchDepUser() {
        this.$refs.selectModal.showModal()
      },
      selectOK(rows, idstr) {
        console.log("当前选中用户", rows)
        console.log("当前选中用户ID", idstr)
        if (!rows) {
          this.userNames = ''
          this.userIds = ''
          this.ids = []
        } else {
          let temp = ''
          for (let item of rows) {
            temp += ',' + item.realname
            this.ids.push(item.id)
          }
          this.userNames = temp.substring(1)
          this.userIds = idstr
        }
        this.$emit("change", this.userIds, this.ids, rows)
      }
    }
  }
</script>

<style scoped>

</style>