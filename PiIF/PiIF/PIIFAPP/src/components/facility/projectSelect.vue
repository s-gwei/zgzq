<template>
  <div>
    <a-select  v-model="projectName" :disabled="disabled" mode="multiple">
      <a-select-option :value="item.name" :key="item.id" v-for="(item, $index) in projects">{{item.name}}</a-select-option>
    </a-select>
  </div>
</template>
<script>
  import {getAction, postAction} from '@/api/manage'
  export default {
    data () {
      return {
        visible: false,
        confirmLoading: false,
        url: {
          projectList: '/sys/visiblePermission/getByUserId'
        },
        userId: '',
        projects: [],
        // form
        projectName: []
      }
    },
    props: {
      disabled: {
        type: Boolean,
        default: false
      },
      width: {
        type: String,
        defalut: '100%'
      }
    },
    created () {
      this.userId = JSON.parse(localStorage.getItem('pro__Login_Userinfo')).value.id
    },
    mounted () {
      this.getProjects()
    },
    methods: {
      getProjects () {
        getAction(this.url.projectList,{userId: this.userId, tableName: 'iot_project'}).then(
          (res) => {
            if (res.success) {
              this.projects = res.result.records
            } else {
              this.$message.error(res.message);
            }
          })
      },
      setProjectValue (list) {
        let project = list ? list.split('-') : []
        this.projectName = project
      }
    },
  };
</script>