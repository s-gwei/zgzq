<template>
  <div class="Detail">
     <!--tab头-->
    <a-row :gutter="48" type="flex"  style="border-bottom: 1px solid #f1f1f1;padding-bottom: 10px;margin: 10px 0 10px 0;">
      <a-col>
        <div style=" font-size: 18px;color:#128DFF;line-height: 40px;">基本信息</div>
      </a-col>
    </a-row>

    <a-form :form="form2"
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
               :disabled = "pro.disableName" id="no"
            />
          </a-form-item>

          <a-form-item
            label="描述"
            hasFeedback
            help="请填写一段描述"
            :labelCol="labelCol"
            :wrapperCol="wrapperCol"
          >
            <a-textarea :rows="5" v-model="form.description" :disabled=" pro.disableDesciption" placeholder="..." id="description"/>
          </a-form-item>

          <a-form-item
            label="项目"
            hasFeedback
            :labelCol="labelCol"
            :wrapperCol="wrapperCol"
          >
            <projectSelect :disabled="pro.disableProject" ref="projectSelect"></projectSelect>
          </a-form-item>

          <a-form-item
            label="更新时间"
            hasFeedback
            :labelCol="labelCol"
            v-show="pro.showUpdateTime"
            :wrapperCol="wrapperCol"
          >
            <a-date-picker
              style="width: 100%"
              showTime
              :value="moment(form.lastModifiedDate)"
              format="YYYY-MM-DD HH:mm:ss"
              placeholder="Select Time"
              :disabled="true"
            />
          </a-form-item>

        </a-col>
        <a-col :md="10" :sm="24">

          <a-form-item
            label="主页"
            hasFeedback
            :labelCol="labelCol"
            :wrapperCol="wrapperCol"
          >
            <a-select v-decorator="['homemashup',{}]" :disabled=" pro.disableHomeMashup">
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
              :disabled=" pro.disableAvatar"
              @change="handleChange"
            >
              <img v-if="form.avatar" :src="form.avatar" alt="avatar" style="max-width: 64px;max-height: 64px;" />
              <div v-else>
                <a-icon :type="loading ? 'loading' : 'plus'" />
                <div class="ant-upload-text">Upload</div>
              </div>
            </a-upload>
          </a-form-item>

          <a-form-item
            label="标签"
            hasFeedback
            :labelCol="labelCol"
            :wrapperCol="wrapperCol"
          >
            <!--<a-select defaultValue="" v-model="form.tags" :disabled=" pro.disableTag">-->
            <!--<a-select-option v-for = "item in mdl.tagsList" :value="item">{{item}}</a-select-option>-->
            <!--</a-select>-->
            <tagsModel :disabled=" pro.disableTag" ref="tagsModel"></tagsModel>
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
            <a-textarea :rows="5" v-model="form.documentationcontent" :disabled = "pro.disableInstructions" placeholder="..." id="description"/>
          </a-form-item>

        </a-col>
      </a-row>

    </a-form>
  </div>
</template>

<script>
//  import JEditor from '@/components/jeecg/JEditor'
import {getAction,postAction,putAction,deleteAction} from '@/api/manage'
import tagsModel from '@/components/facility/tagsModel'
import projectSelect from '@/components/facility/projectSelect'
import moment from 'moment';
import 'moment/locale/zh-cn';
import pick from 'lodash.pick'
  export default {
    data () {
      return {
        labelCol: {md: {span:4}, sm: {span: 6}, xs: {span: 24}},
        wrapperCol: {md: {span: 20}, sm: {span: 18},xs: {span: 24} },
        moment,
        dataShapeInfo: {},
        userId:JSON.parse(localStorage.getItem('pro__Login_Userinfo')).value.id,
        username:JSON.parse(localStorage.getItem('pro__Login_Userinfo')).value.username,
        form:{
          avatar: '',
          owner:''
        },
        mdl: {
          thingTempList:[],
          tagsList:[],
        },
        dataShapeName:'',
        shapeId:'',
        headers:{},
        pro:{
          type:'',
          showActive:true,
          showPublished:true,
          showUpdateTime:false,
          disableName:false,
          disableDesciption:false,
          disableProject:false,
          disableTag:false,
          disableInstructions:false,
          disableHomeMashup:false,
          disableAvatar:false,
          disableTime:false,
          disableDataShape:false,
          disablePublished:false,
          disableActive:false,
          detail:false,
          edit:false,
        },
        fld:{
          "alertConfigurations":'',
          "className":'',
          "configurationChanges":'',
          "configurationTables":'',
          "designTimePermissions":'',
          "identifier":"",
          "implementedShapes":'',
          "owner":"",
          "propertyBindings":'',
          "remoteEventBindings":'',
          "remotePropertyBindings":'',
          "remoteServiceBindings":'',
          "runTimePermissions":'',
          "tenantId":'',
          "thingShape":'',
          "type":null,
          "visibilityPermissions":'',
          'identifer': ''
        },
        // 富文本value
//        jeditor: {
//          value: '<h2 style="text-align: center;">富文本编辑器</h2> <p>这里是富文本编辑器。</p>'
//        },
        // 头像
        loading: false,
        url:{
          addDataShape:'/tableMessage/addTable',
          editDataShape:'/tableMessage/updateTable',
          tagList:'/thingTemplate/getThingTemplateTagsList',
          projectList: '/sys/visiblePermission/getByUserId',
          queryDataShapeByName:'/tableMessage/findTableAndColumnByTableName',
          fileUpload: window._CONFIG['domianURL']+"/sys/common/upload",
          imgerver: window._CONFIG['domianURL']+"/sys/common/view",
        }
      }
    },
    components: {
//      JEditor,
      tagsModel,
      projectSelect
    },
    beforeCreate() {
      this.form2 = this.$form.createForm(this);
    },
    created(){

    },
    mounted(){
      this.dataShapeInfo = JSON.parse(sessionStorage.getItem('dataShapeInfo'))
      this.shapeId = this.dataShapeInfo.id;
      let token = this.$store.state.user.token
      this.headers = {"X-Access-Token":token}
      this.queryDataShapeInfo();
      this.getTags();
      this.flagEdit();
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
            this.form.avatar = this.url.imgerver +"/"+ response.message;
          }else{
            this.$message.warning(response.message);
          }
        }
      },
      getAvatarView(){
        return this.url.imgerver +"/"+ this.model.avatar;
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

      //判断文本可不可编辑
      flagEdit(){
        this.pro.detail = this.dataShapeInfo.detail;
        this.pro.edit = this.dataShapeInfo.edit;
        this.pro.type = this.dataShapeInfo.type;
        let copy = this.dataShapeInfo.copy;
        if(this.pro.detail){
          this.pro.disableName = true;
          this.pro.showUpdateTime = true;
          this.pro.disableDesciption = true;
          this.pro.disableProject = true;
          this.pro.disableTag = true;
          this.pro.disableInstructions = true;
          this.pro.disableHomeMashup = true;
          this.pro.disableAvatar = true;
          this.pro.disableTime = true;
          this.pro.disableDataShape = true;
          this.pro.disablePublished = true;
          this.pro.disableActive = true;
        }
        if(this.pro.edit){
          this.pro.disableName = true;
        }
        if((copy !== undefined)&&(copy === true)){
          this.pro.disableDesciption = true;
          this.pro.disableProject = true;
          this.pro.disableTag = true;
          this.pro.disableInstructions = true;
          this.pro.disableHomeMashup = true;
          this.pro.disableAvatar = true;
          this.pro.disableTime = true;
          this.pro.disableDataShape = true;
          this.pro.disablePublished = true;
          this.pro.disableActive = true;
          this.pro.disableName = false;
        }
      },
      //获取标签
      getTags(){
        getAction(this.url.tagList).then((res =>{
          if(res.code === 200){
            this.mdl.tagsList = res.result
          }
        }))
      },

      // 新增/修改数据形状
      saveDataShape(){
        this.handleSubmit();
      },

      //获取数据形状信息
      queryDataShapeInfo(){
        this.dataShapeName = this.dataShapeInfo.name
        this.pro.type = this.dataShapeInfo.type
        if((this.dataShapeName !== undefined) && (this.dataShapeName !=='')){
          //根据名称查询数据形状
          let param = {
            "tableName":this.dataShapeName
          }
          getAction(this.url.queryDataShapeByName,param).then((res =>{
            if(res.success){
              let result = res.result;
              this.form = result
              this.$nextTick(() => {
                this.form2.setFieldsValue(pick(result,'name','homemashup'))
              });
              this.$refs.projectSelect.setProjectValue(result.projectname)
              let list = JSON.parse(result.tags ? result.tags : "[]")
              this.$refs.tagsModel.setTagsList(list)
            }
          }))
        }
      },

      handleSubmit(e) {
        // e.preventDefault();
        this.form2.validateFields((err, values) => {
          let reg = /^[\u4E00-\u9FA5A-Za-z]+[\u4E00-\u9FA5A-Za-z0-9]+$/;
          if (reg.test(values.name) === false) {
            //分类名称不能输入特殊字符
            this.$message.warning('名称不能以数字开头或带有特殊符号！')
            return;
          }
          if (!err) {
            let param;
            let url;
            if(this.pro.disableName){
              param = {
                "id":this.shapeId,
                "name":this.form.name,
                "user":this.username,
                "description":this.form.description,
                "projectname":this.$refs.projectSelect.projectName.join('-'),
                "tags": JSON.stringify(this.$refs.tagsModel.selectedItems),
                "documentationcontent":this.form.documentationcontent,
                "homemashup":values.homemashup,
                "avatar":this.form.avatar
              };
              url = this.url.editDataShape
              postAction(url,param).then((res =>{
                if(res.success === true){
                  this.$message.success('修改成功!');
                  this.pro.disableName = true
                  this.$emit('getThingName',values.name)
                }else{
                  this.$message.error(res.message);
                }
              }))
            }else{
              param = {
                "name":values.name,
                "user":this.username,
                "description":this.form.description,
                "projectname":this.$refs.projectSelect.projectName.join('-'),
                "tags": JSON.stringify(this.$refs.tagsModel.selectedItems),
                "documentationcontent":this.form.documentationcontent,
                "homemashup":values.homemashup,
                "avatar":this.form.avatar
              };
              url = this.url.addDataShape
              postAction(url,param).then((res =>{
                if(res.success === true){
                  this.$message.success('添加成功!');
                  this.pro.disableName = true
                  this.$emit('getThingName',values.name)
                  this.$emit('getDataShapeName',values.name)
                }else{
                  this.$message.error(res.message);
                }
              }))
            }
          }
        });
      }
    }
  }
</script>
