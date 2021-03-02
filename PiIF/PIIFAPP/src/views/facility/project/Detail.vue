<template>
  <div class="Detail">
    <!--tab头-->
    <a-row :gutter="48" type="flex"  style="border-bottom: 1px solid #f1f1f1;padding-bottom: 10px;margin: 10px 0 10px 0;">
      <a-col>
        <div style=" font-size: 18px;color:#128DFF;line-height: 40px;">基本信息</div>
      </a-col>
    </a-row>

    <a-form :form="form"
            @submit="handleSubmit">
      <a-row type="flex" justify="space-between">
        <a-col :md="10" :sm="24">
          <a-form-item
            label="名称"
            hasFeedback
            :labelCol="labelCol"
            :wrapperCol="wrapperCol"
          >
            <a-input placeholder="名称"
                     v-decorator="['name', { rules: [{ required: true, message: '请输入数据流名称!' }] }]"
                     :disabled = "pro.tagsName" id="no"
            />
          </a-form-item>

          <a-form-item
            label="描述"
            hasFeedback
            help="请填写一段描述"
            :labelCol="labelCol"
            :wrapperCol="wrapperCol"
          >
            <a-textarea :rows="5"
                        v-decorator="['desciption', {}]"
                        :disabled=" pro.textInput" placeholder="..." id="description"/>
          </a-form-item>

          <a-form-item
            label="项目"
            hasFeedback
            :labelCol="labelCol"
            :wrapperCol="wrapperCol"
          >
            <a-select  v-decorator="['projectPk', {}]" :disabled=" pro.textInput">
              <a-select-option :value="item.id" v-for="(item, $index) in projects">{{item.name}}</a-select-option>
            </a-select>
          </a-form-item>


        </a-col>
        <a-col :md="10" :sm="24">

          <a-form-item
            label="主页"
            hasFeedback
            :labelCol="labelCol"
            :wrapperCol="wrapperCol"
          >
            <a-select  v-decorator="['mashupName', {}]"  :disabled=" pro.textInput">
              <a-select-option value="ApplicationMashup">ApplicationMashup</a-select-option>
            </a-select>
          </a-form-item>

          <a-form-item
            label="头像"
            :labelCol="labelCol"
            :wrapperCol="wrapperCol"
            help="请上传jpg、png格式图片"
          >
            <a-upload
              name="file"
              listType="picture-card"
              class="avatar-uploader"
              :showUploadList="false"
              :action="uploadAction"
              :data="{'isup':1}"
              :headers="headers"
              :beforeUpload="beforeUpload"
              :disabled=" pro.textInput"
              @change="handleChange"
            >
              <img v-if="img" :src="img" alt="avatar" style="max-width: 64px;max-height: 64px;" />
              <div v-else>
                <a-icon :type="loading ? 'loading' : 'plus'" />
                <div class="ant-upload-text">Upload</div>
              </div>
            </a-upload>
          </a-form-item>

        </a-col>
        <a-col :span="24">
          <a-form-item
            style="text-align: left;"
            label="使用说明:"
            hasFeedback
            help="请填写一段使用说明"
            :labelCol="{md: {span:2},xs: {span: 24}}"
            :wrapperCol="{md: {span: 22},xs: {span: 24} }"
          >
            <a-textarea :rows="5" v-decorator="['document', {}]" :disabled = "pro.textInput" placeholder="..." id="description"/>
          </a-form-item>

        </a-col>
      </a-row>

    </a-form>
  </div>
</template>

<script>
  //  import JEditor from '@/components/jeecg/JEditor'
  import {getAction,postAction,putAction,deleteAction} from '@/api/manage'
  import moment from 'moment';
  import 'moment/locale/zh-cn';
  import pick from 'lodash.pick'
  export default {
    name: 'projectDetail',
    data () {
      return {
        labelCol: {md: {span:4}, sm: {span: 6}, xs: {span: 24}},
        wrapperCol: {md: {span: 20}, sm: {span: 18},xs: {span: 24} },
        moment,
        ModelInfo: {},
        userId:JSON.parse(localStorage.getItem('pro__Login_Userinfo')).value.id,
        username:JSON.parse(localStorage.getItem('pro__Login_Userinfo')).value.username,
        form: this.$form.createForm(this),
        editProject: {},
        headers: {},
        pro: {
          tagsName: false,
          textInput: false
        },
        // 头像
        img: '',
        loading: false,
        // 项目id
        projectId: '',
        projects: [],
        url:{
          add: '/iot/project/add',
          edit: '/iot/project/editProject',
          file: '/iot/project/getProject',
          searchList: '/iot/project/query',
          projectList: '/sys/visiblePermission/getByUserId',
          fileUpload: window._CONFIG['domianURL']+"/sys/common/upload",
          imgerver: window._CONFIG['domianURL']+"/sys/common/view",
        }
      }
    },
    components: {
    },
    created(){
      this.getProjects()

    },
    mounted(){
      this.ModelInfo = JSON.parse(sessionStorage.getItem('projectInfo'))
      this.projectId = this.ModelInfo.id;

      switch (this.ModelInfo.type) {
        case 'file':
          this.pro.tagsName = true
          this.pro.textInput = true
          break;
        case 'edit':
          this.pro.tagsName = false
          this.pro.textInput = false
          break;
        case 'copy':
          this.pro.tagsName = false
          this.pro.textInput = false
          break;
      }
      let token = this.$store.state.user.token
      this.headers = {"X-Access-Token":token}
      this.getTagsValue()
    },
    computed:{
      uploadAction:function () {
        return this.url.fileUpload;
      }
    },
    methods: {
      //  上传图片逻辑
      handleChange(info) {
        if (info.file.status === 'uploading') {
          this.loading = true;
          return
        }
        if (info.file.status === 'done') {
          var response = info.file.response;
          this.loading = false;
          if(response.success){
            this.img = this.url.imgerver +"/"+ response.message;
          }else{
            this.$message.warning(response.message);
          }
        }
      },
      beforeUpload(file) {
        const isJPG = file.type === 'image/jpg' || file.type === 'image/png' || file.type === 'image/jpeg';
        if (!isJPG) {
          this.$message.error('图片必须是jpg、png!');
        }
        const isLt2M = file.size / 1024 / 1024 < 2;
        if (!isLt2M) {
          this.$message.error('图片大小须小于2MB!');
        }
        return isJPG && isLt2M;
        return isLt2M
      },

      // 查询项目
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
      refresh () {
        this.form = this.$form.createForm(this)
      },
      /** 取详情信息**/
      getTagsValue () {
        if (this.projectId) {
          getAction(this.url.file, {id: this.projectId}).then(
            (res) => {
              if (res.success) {
                this.editProject = res.result
                this.$nextTick(() => {
                  this.form.setFieldsValue(pick(this.editProject, 'name','desciption', 'document', 'mashupName', 'objectPk', 'projectPk', 'tagPk'));
                  this.img = this.editProject.icon
                })
              } else {
                this.$message.error(res.message);
              }
            }
          )

        }
      },
      /** 保存信息 **/

      handleSubmit(e) {
        // e.preventDefault();
        this.form.validateFields((err, values) => {
          let reg = /^[\u4E00-\u9FA5A-Za-z]+[\u4E00-\u9FA5A-Za-z0-9]+$/;
          if (reg.test(values.name) === false) {
            //分类名称不能输入特殊字符
            this.$message.warning('名称不能以数字开头或带有特殊符号！')
            return;
          }
          if (!err) {
            console.log(values)
            if (!this.projectId) {
              if (!this.url.add) {
                this.$message.error('请设置url.add属性!')
                return
              }
              let params = {
                desciption: values.desciption  ? values.desciption : '',
                document: values.document  ? values.document : '',
                icon: this.img,
                mashupName: values.mashupName  ? values.mashupName : '',
                name: values.name  ? values.name : '',
                objectPk: values.objectPk ? values.objectPk : '',
                projectPk: values.projectPk  ? values.projectPk : '',
                tagPk: values.tagPk  ? values.tagPk : ''
              }
              postAction(this.url.add, params).then(
                (res) => {
                  if (res.success) {
                    this.$message.success('添加成功');
                    this.$emit('getThingName',values.name, res.result.id)
                  } else {
                    this.$message.error(res.message);
                  }
                })
            } else {
              if (!this.url.add) {
                this.$message.error('请设置url.add属性!')
                return
              }
              let params = {
                id: this.projectId,
                desciption: values.desciption  ? values.desciption : '',
                document: values.document  ? values.document : '',
                icon: this.img,
                mashupName: values.mashupName  ? values.mashupName : '',
                name: values.name  ? values.name : '',
                objectPk: this.editProject.objectPk ? this.editProject.objectPk : '',
                projectPk: values.projectPk  ? values.projectPk : '',
                tagPk: this.editProject.tagPk  ? this.editProject.tagPk : ''
              }
              putAction(this.url.edit, params).then(
                (res) => {
                  if (res.success) {
                    this.$message.success('修改成功');
                    this.$emit('getThingName',values.name, this.projectId)
                    this.editProject = {}
                  } else {
                    this.$message.error(res.message);
                  }
                })
            }
          }
        });
      }
    }
  }
</script>
