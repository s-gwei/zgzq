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
            label="模型名称"
            hasFeedback
            :labelCol="labelCol"
            :wrapperCol="wrapperCol"
          >
            <a-input placeholder="模型名称"
               v-decorator="['name', { rules: [{ required: true, message: '请输入模型名称!' }] }]"
               :disabled = "pro.disableName" id="no"
            />
          </a-form-item>

          <a-form-item
            label="模型描述"
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
            label="标签"
            hasFeedback
            :labelCol="labelCol"
            :wrapperCol="wrapperCol"
          >
            <!--<a-select defaultValue="" v-model="form.tags" :disabled=" pro.disableTag">-->
              <!--<a-select-option v-for = "item in mdl.tagsList" :value="item">{{item}}</a-select-option>-->
            <!--</a-select>-->
            <tagsModel :disabled="pro.disableTag" ref="tagsModel"></tagsModel>
          </a-form-item>

          <a-form-item
            label="物模板"
            hasFeedback
            :labelCol="labelCol"
            :wrapperCol="wrapperCol"
          >
            <a-select
              v-decorator="[
                'baseThingTemplate',
                { rules: [{ required: true, message: '事务模板不能为空!' }] },
              ]"
              :disabled = "pro.disableBaseTemplate">
              <a-select-option v-for ="item in mdl.thingTempList" :value="item">{{item}}</a-select-option>
            </a-select>

          </a-form-item>

          <a-form-item
            label="活跃状态"
            validateStatus="success"
            v-show="pro.showActive"
            :labelCol="labelCol"
            :wrapperCol="wrapperCol"
          >
            <a-checkbox v-model = "form.enabled" :disabled = "pro.disableActive"></a-checkbox>
          </a-form-item>

        </a-col>
        <a-col :md="10" :sm="24">

          <a-form-item
            label="主页"
            hasFeedback
            :labelCol="labelCol"
            :wrapperCol="wrapperCol"
          >
            <a-select defaultValue="1" v-model="form.homeMashup" :disabled=" pro.disableHomeMashup">
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
            label="识别"
            hasFeedback
            :labelCol="labelCol"
            :wrapperCol="wrapperCol"
            v-show="pro.showActive"
          >
            <a-input   :disabled="true" v-model="form.identifier">
            </a-input>
          </a-form-item>

          <a-form-item
            label="更新时间"
            hasFeedback
            :labelCol="labelCol"
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

          <a-form-item
            label="价值流"
            hasFeedback
            :labelCol="labelCol"
            :wrapperCol="wrapperCol"
          >
            <a-select v-model="form.valueStream" :disabled=" pro.disableTime">
              <a-select-option :value="item.name" v-for="item in Valuestream">{{item.name}}</a-select-option>
            </a-select>
          </a-form-item>

<!--          <a-form-item-->
<!--            label="数据形状"-->
<!--            hasFeedback-->
<!--            validateStatus="warning"-->
<!--            :labelCol="labelCol"-->
<!--            :wrapperCol="wrapperCol"-->
<!--          >-->
<!--            <a-select defaultValue="" v-model="mdl.datashape" :disabled = "pro.disableDataShape">-->
<!--              <a-select-option v-for ="item in mdl.thingTempList" :value="item">{{item}}</a-select-option>-->
<!--            </a-select>-->
<!--          </a-form-item>-->

          <a-form-item
            label="发行"
            validateStatus="success"
            v-show="pro.showPublished"
            :labelCol="labelCol"
            :wrapperCol="wrapperCol"
          >
            <a-checkbox v-model="form.published" :disabled = "pro.disablePublished"></a-checkbox>
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
            <a-textarea :rows="5" v-model="form.documentationContent" :disabled = "pro.disableInstructions" placeholder="..." id="description"/>
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
        ModelInfo: {},
        userId:JSON.parse(localStorage.getItem('pro__Login_Userinfo')).value.id,
        username:JSON.parse(localStorage.getItem('pro__Login_Userinfo')).value.username,
        form:{
          avatar: '',
          owner:''
        },
        mdl: {
          thingTempList:[],
        },
        templateName:'',
        headers:{},
        Valuestream: [],
        pro:{
          type:'',
          showActive:false,
          showPublished:false,
          disableName:false,
          disableDesciption:false,
          disableProject:false,
          disableTag:false,
          disableInstructions:false,
          disableHomeMashup:false,
          disableAvatar:false,
          disableTime:false,
          disableValueStream:false,
          disableBaseTemplate:false,
          disableDataShape:false,
          disablePublished:false,
          disableActive:false,
          detail:false
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
          baseTemplate:'/thingTemplate/findTemplateByType',
          queryTemplateByName:'/thingTemplate/findTemplateByName',
          queryThing:'/thing/findByName',
          queryTemplate:'/thingTemplate/list',
          addTemplate:'/thingTemplate/add',
          editTemplate:'/thingTemplate/updateByName',
          addThing:'/thing/add',
          editThing:'/thing/update',
          projectList: '/sys/visiblePermission/getByUserId',
          fileUpload: window._CONFIG['domianURL']+"/sys/common/upload",
          imgerver: window._CONFIG['domianURL']+"/sys/common/view",
        }
      }
    },
    components: {
//      JEditor
      tagsModel,
      projectSelect
    },
    beforeCreate() {
      this.form2 = this.$form.createForm(this);
    },
    created(){
    },
    mounted(){
      this.ModelInfo = JSON.parse(sessionStorage.getItem('ModelInfo'))
      let token = this.$store.state.user.token
      this.headers = {"X-Access-Token":token}
      this.queryTemplateInfo();
      this.getBaseTemplate();
      this.flagEdit();
      this.getValuestream()
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
        console.log(file.type)
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

      getValuestream () {
        getAction(this.url.projectList,{userId: this.userId, tableName: 'iot_valuestream_model'}).then(
          (res) => {
            if (res.success) {
              this.Valuestream = res.result.records
            } else {
              this.$message.error(res.message);
            }
          })
      },
      //判断文本可不可编辑
      flagEdit(){
        this.detail = this.ModelInfo.detail;
        this.pro.type = this.ModelInfo.type;
        let copy = this.ModelInfo.copy;
        if(this.detail){
          this.pro.disableDesciption = true;
          this.pro.disableProject = true;
          this.pro.disableTag = true;
          this.pro.disableInstructions = true;
          this.pro.disableHomeMashup = true;
          this.pro.disableAvatar = true;
          this.pro.disableTime = true;
          this.pro.disableValueStream = true;
          this.pro.disableDataShape = true;
          this.pro.disablePublished = true;
          this.pro.disableActive = true;
        }
        if((this.templateName !== undefined) && (this.templateName !=='')){
          this.pro.disableName = true;
          this.pro.disableBaseTemplate = true;
        }
        if((this.pro.type !== undefined) && (this.pro.type === 'facility')){
          this.pro.showActive = true;
          this.pro.showPublished = true;
        }
        if((copy !== undefined)&&(copy === true)){
          this.pro.disableDesciption = true;
          this.pro.disableProject = true;
          this.pro.disableTag = true;
          this.pro.disableInstructions = true;
          this.pro.disableHomeMashup = true;
          this.pro.disableAvatar = true;
          this.pro.disableTime = true;
          this.pro.disableValueStream = true;
          this.pro.disableDataShape = true;
          this.pro.disablePublished = true;
          this.pro.disableActive = true;
          this.pro.disableName = false;
        }
      },
      //获取事务模板
      getBaseTemplate(){
        let param = {}
        if((this.pro.type !== undefined) && (this.pro.type === 'facility')){
          param = {
            "type":"2001"
          }
        } else {
          param = {
            "type":"2000"
          }
        }

        getAction(this.url.baseTemplate,param).then((res =>{
          this.mdl.thingTempList = res
        }))
      },
      //新增/修改物模型
      saveThingTemplate(){
        this.handleSubmit();
      },

      //根据名称查询物模型
      queryTemplateInfo(){
        this.templateName = this.ModelInfo.name
        this.pro.type = this.ModelInfo.type
        if((this.templateName !== undefined) && (this.templateName !=='')){
          if((this.pro.type !== undefined) && (this.pro.type === 'obj')){
            //根据名称查询物模型
            let param = {
              "name":this.templateName
            }
            getAction(this.url.queryTemplateByName,param).then((res =>{
              if(res.code === 200){
                let result = res.result;
                this.form = result
                let list = JSON.parse(result.tags ? result.tags : "[]")
                this.$refs.projectSelect.setProjectValue(result.projectName)
                this.$refs.tagsModel.setTagsList(list)
                this.$nextTick(() => {
                  this.form2.setFieldsValue(pick(result,'baseThingTemplate','name'))
                });

              }
            }))
          }
          if((this.pro.type !== undefined) && (this.pro.type === 'facility')){
            //根据名称查询设备
            let param = {
              "thingName":this.templateName
            }
            getAction(this.url.queryThing,param).then((res =>{
              if(res.code === 200){
                let result = res.result;
                this.form = result
                let list = JSON.parse(result.tags ? result.tags : "[]")
                this.$refs.tagsModel.setTagsList(list)
                this.$refs.projectSelect.setProjectValue(result.projectName)
                this.$nextTick(() => {
                  this.form2.setFieldsValue({
                    name:result.name,
                    baseThingTemplate:result.thingTemplate
                  })
                });
                this.fld.alertConfigurations = result.alertConfigurations
                this.fld.className = result.className
                this.fld.configurationChanges = result.configurationChanges
                this.fld.configurationTables = result.configurationTables
                this.fld.designTimePermissions = result.designTimePermissions
                this.fld.identifier = result.identifier
                this.fld.implementedShapes = result.implementedShapes
                this.fld.owner = result.owner
                this.fld.propertyBindings = result.propertyBindings
                this.fld.remoteEventBindings = result.remoteEventBindings
                this.fld.remotePropertyBindings = result.remotePropertyBindings
                this.fld.remoteServiceBindings = result.remoteServiceBindings
                this.fld.runTimePermissions = result.runTimePermissions
                this.fld.tenantId = result.tenantId
                this.fld.thingShape = result.thingShape
                this.fld.type = result.type
                this.fld.visibilityPermissions = result.visibilityPermissions
                console.log(result.identifier)
                sessionStorage.setItem('identifier', result.identifier)
              }
            }))
          }
        }
      },

      handleSubmit(e) {
        // e.preventDefault();
        this.form2.validateFields((err, values) => {
          if (!err) {
            let reg = /^[\u4E00-\u9FA5A-Za-z]+[\u4E00-\u9FA5A-Za-z0-9]+$/;
            if (reg.test(values.name) === false) {
              //分类名称不能输入特殊字符
              this.$message.warning('名称不能以数字开头或带有特殊符号！')
              return;
            }
            if((this.pro.type !== undefined)&&(this.pro.type === 'obj')){
              let param = {
//                "thingTemplate":Object.assign(this.form,values,{}),
                "avatar":this.form.avatar,
                "baseThingTemplate":this.form.baseThingTemplate,
                "description":this.form.description,
                "documentationContent":this.form.documentationContent,
                "homeMashup":this.form.homeMashup,
                "name":this.form.name,
                "owner":this.form.owner,
                "projectName":this.$refs.projectSelect.projectName.join('-'),
                "tags": JSON.stringify(this.$refs.tagsModel.selectedItems),
                ...values
              }
              if(this.pro.disableName){
                putAction(this.url.editTemplate,param).then((res =>{
                  if(res.success === true){
                    this.$message.success(res.message);
                  }else{
                    this.$message.error(res.message);
                  }
                }))
              }else{
                postAction(this.url.addTemplate,param).then((res =>{
                  if(res.success === true){
                    this.$message.success(res.message);
                    this.pro.disableName = true
                    this.$emit('getThingName',values.name)
                  }else{
                    this.$message.error(res.message);
                  }
                }))
              }
            }
            if((this.pro.type !== undefined)&&(this.pro.type === 'facility')){
              let param = {
                  "alertConfigurations":this.fld.enabled,
                  "avatar":this.form.avatar,
                  "className":this.fld.className,
                  "configurationChanges":this.fld.configurationChanges,
                  "configurationTables":this.fld.configurationTables,
                  "designTimePermissions":this.fld.designTimePermissions,
                  "enabled":this.form.enabled,
                  "thingTemplate":this.form.baseThingTemplate,
                  "description":this.form.description,
                  "documentationContent":this.form.documentationContent,
                  "homeMashup":this.form.homeMashup,
                  "identifier":this.fld.identifier,
                  "implementedShapes":this.fld.implementedShapes,
                  "lastModifiedDate":moment(this.form.lastModifiedDate).format('YYYY-MM-DD HH:mm:ss'),
                  "name":this.form.name,
                  "owner":this.fld.owner,
                  "projectName":this.$refs.projectSelect.projectName.join('-'),
                  "propertyBindings":this.fld.propertyBindings,
                  "published":this.form.published,
                  "remoteEventBindings":this.fld.remoteEventBindings,
                  "remotePropertyBindings":this.fld.remotePropertyBindings,
                  "remoteServiceBindings":this.fld.remoteServiceBindings,
                  "runTimePermissions":this.fld.runTimePermissions,
                  "tenantId":this.fld.tenantId,
                  "thingShape":this.fld.thingShape,
                  "type":this.fld.type,
                  "tags":JSON.stringify(this.$refs.tagsModel.selectedItems),
                  "valueStream":this.form.valueStream,
                  "visibilityPermissions":this.fld.visibilityPermissions
              }
              console.log(values)
              param.thingTemplate = values.baseThingTemplate
              param.name = values.name
              if(this.pro.disableName){
                putAction(this.url.editThing,param).then((res =>{
                  if(res.success === true){
                    this.$message.success(res.message);
                  }else{
                    this.$message.error(res.message);
                  }
                }))
              }else{
                postAction(this.url.addThing + '?username=' + this.username ,param).then((res =>{
                  if(res.success === true){
                    this.$message.success(res.message);
                    this.pro.disableName = true
                    this.$emit('getThingName',values.name)
                  }else{
                    this.$message.error(res.message);
                  }
                }))
              }
            }
          }
        });
      }
    }
  }
</script>
