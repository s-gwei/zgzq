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
            <tagsModel :disabled=" pro.disableTag" ref="tagsModel"></tagsModel>
          </a-form-item>

          <a-form-item
            label="基本事务模板"
            hasFeedback
            :labelCol="labelCol"
            :wrapperCol="wrapperCol"
          >
          <a-input placeholder="基本事务模板"
                   v-model="fld.thingtemplate"
                   :disabled = "true" id="no"
          />

          </a-form-item>

          <a-form-item
            label="实现形状"
            hasFeedback
            :labelCol="labelCol"
            :wrapperCol="wrapperCol"
          >
            <a-select
              v-model="form.implementedshapes"
              :disabled = "pro.disableImplementedshape">
              <a-select-option v-for ="item in mdl.thingTempList" :value="item.key">{{item.label}}</a-select-option>
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
            <a-select v-decorator="['homemashup',{}]":disabled=" pro.disableHomeMashup">
              <a-select-option value="StreamMashup">StreamMashup</a-select-option>
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
            v-if="this.pro.type == 'dataStream'"
            label="数据形状"
            hasFeedback
            :labelCol="labelCol"
            :wrapperCol="wrapperCol"
          >
            <a-select
              v-decorator="[
                'datashape',
                { rules: [{ required: true, message: '数据形状不能为空!' }] },
              ]"
              :disabled = "pro.disableDataShape">
              <a-select-option v-for ="item in mdl.dataShapeList" :value="item.key">{{item.label}}</a-select-option>
            </a-select>
          </a-form-item>

          <a-form-item
            label="价值流"
            hasFeedback
            :labelCol="labelCol"
            :wrapperCol="wrapperCol"
          >
            <a-select defaultValue="1" v-model="form.valuestream" :disabled=" pro.disableTime">
              <a-select-option v-for ="item in mdl.valueStreamList" :value="item.key">{{item.label}}</a-select-option>
            </a-select>
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
            v-show="pro.showTime"
          >
            <a-date-picker
              style="width: 100%"
              showTime
              :value="moment(form.lastmodifieddate)"
              format="YYYY-MM-DD HH:mm:ss"
              placeholder="Select Time"
              :disabled="true"
            />
          </a-form-item>

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
        StreamInfo: {},
        userId:JSON.parse(localStorage.getItem('pro__Login_Userinfo')).value.id,
        username:JSON.parse(localStorage.getItem('pro__Login_Userinfo')).value.username,
        form:{
          avatar: '',
          owner:'',
          published:false,
          enabled:false
        },
        mdl: {
          thingTempList:[],
          dataShapeList:[],
          valueStreamList:[],
          tagsList:[],
        },
        streamName:'',
        headers:{},
        pro:{
          type:'',
          showActive:true,
          showPublished:true,
          showTime:false,
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
          disableImplementedshape:false,
          disablePublished:false,
          disableActive:false,
          detail:false
        },
        fld:{
          "thingtemplate":"",
        },
        // 富文本value
//        jeditor: {
//          value: '<h2 style="text-align: center;">富文本编辑器</h2> <p>这里是富文本编辑器。</p>'
//        },
        // 头像
        loading: false,
        url:{
          dataList:'/sys/visiblePermission/getByUserId',
          baseTemplate:'/thingTemplate/findTemplateByType',
          tagList:'/thingTemplate/getThingTemplateTagsList',
          projectList: '/sys/visiblePermission/getByUserId',
          queryStreamByName:'/streamModel/findStreamByName',
          queryValueStreamByName:'/valueStreamModel/findTableAndColumnByTableName',
          addStream:'/streamModel/add',
          editStream:'/streamModel/update',
          addValueStream:'/valueStreamModel/add',
          editValueStream:'/valueStreamModel/updateTable',
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
      this.StreamInfo = JSON.parse(sessionStorage.getItem('StreamInfo'))
      let token = this.$store.state.user.token
      this.headers = {"X-Access-Token":token}
      this.queryStreamInfo();
      this.getBaseTemplate();
      this.getTags();
      this.getDataShape();
      this.getValueStream();
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

      //判断文本可不可编辑
      flagEdit(){
        this.detail = this.StreamInfo.detail;
        this.pro.type = this.StreamInfo.type;
        let copy = this.StreamInfo.copy;
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
          this.pro.disableImplementedshape = true;
          this.pro.disablePublished = true;
          this.pro.disableActive = true;
        }
        if((this.streamName !== undefined) && (this.streamName !=='')){
          this.pro.disableName = true;
          this.pro.disableBaseTemplate = true;
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
          this.pro.disableImplementedshape = true;
          this.pro.disablePublished = true;
          this.pro.disableActive = true;
          this.pro.disableName = false;
        }
        if(this.pro.disableName){
          this.pro.showTime = true;
        }
      },

      //获取基本事务模板
      getBaseTemplate(){
        if((this.pro.type !== undefined) && (this.pro.type === 'dataStream')){
          this.fld.thingtemplate = 'Stream'
        } else {
          this.fld.thingtemplate = 'ValueStream'
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

      //获取数据形状
      getDataShape(){
        let params = {
          "userId": this.userId,
          "tableName":"iot_datashape_model"
        }
        getAction(this.url.dataList, params).then((res) => {
          if (res.success) {
            let shapeList = [];
            let result = res.result.records
            if(result.length>0){
              for(let i=0;i<result.length;i++){
                shapeList.push({
                  key:result[i].name,
                  label:result[i].name
                })
              }
            }
            this.mdl.dataShapeList = shapeList
          }
        })
      },

      //获取值流
      getValueStream(){
        let params = {
          "userId": this.userId,
          "tableName":"iot_valuestream_model"
        }
        getAction(this.url.dataList, params).then((res) => {
          if (res.success) {
            let streamList = [];
            let result = res.result.records
            if(result.length>0){
              for(let i=0;i<result.length;i++){
                streamList.push({
                  key:result[i].name,
                  label:result[i].name
                })
              }
            }
            this.mdl.valueStreamList = streamList
          }
        })
      },

      //新增/修改物模型
      saveThingTemplate(){
        this.handleSubmit();
      },

      //根据名称查询流数据
      queryStreamInfo(){
        this.streamName = this.StreamInfo.name
        this.pro.type = this.StreamInfo.type
        if((this.streamName !== undefined) && (this.streamName !=='')){
          let url = '';
          let param = {};
          //根据名称查询流数据
          if((this.pro.type !== undefined) && (this.pro.type === 'dataStream')){
            url = this.url.queryStreamByName
            param = {
              "name":this.streamName
            }
          }
          if((this.pro.type !== undefined) && (this.pro.type === 'valueStream')){
            url = this.url.queryValueStreamByName
            param = {
              "tableName":this.streamName
            }
          }
          getAction(url,param).then((res =>{
            if(res.success){
              let result = res.result;
              this.form = result
              if(result.enabled === '0'){
                this.form.enabled = false;
              }else if(result.enabled === '1'){
                this.form.enabled = true;
              }
              if(result.published === '0'){
                this.form.published = false;
              }else if(result.published === '1'){
                this.form.published = true;
              }
              this.form.enabled = Boolean(result.enabled)
              this.form.published = Boolean(result.published)
              this.$nextTick(() => {
                this.form2.setFieldsValue(pick(result,'datashape','name','homemashup'))
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
            if((this.pro.type !== undefined)&&(this.pro.type === 'dataStream')){
              let param = {
                "name":values.name,
                "description":this.form.description,
                "projectname":this.$refs.projectSelect.projectName.join('-'),
                "tags": JSON.stringify(this.$refs.tagsModel.selectedItems),
                "implementedshapes":this.form.implementedshapes,
                "thingtemplate":this.fld.thingtemplate,
                "enabled":this.form.enabled,
                "avatar":this.form.avatar,
                "identifier":this.form.identifier,
                "documentationcontent":this.form.documentationcontent,
                "homemashup":values.homemashup,
                "published":this.form.published,
                "valuestream":this.form.valuestream,
                "datashape":values.datashape,
                "user":this.userId,
              }
              if(this.pro.disableName){
                //修改数据流
                postAction(this.url.editStream,param).then((res =>{
                  if(res.success === true){
                    this.$message.success('修改成功!');
                  }else{
                    this.$message.error(res.message);
                  }
                }))
              }else{
                //添加数据流
                postAction(this.url.addStream,param).then((res =>{
                  if(res.success === true){
                    this.$message.success('添加成功!');
                    this.pro.disableName = true
                    this.$emit('getThingName',values.name)
                  }else{
                    this.$message.error(res.message);
                  }
                }))
              }
            }
            if((this.pro.type !== undefined)&&(this.pro.type === 'valueStream')){
              let param = {
                "entityId":this.form.entityId,
                "name":values.name,
                "description":this.form.description,
                "projectname":this.$refs.projectSelect.projectName.join('-'),
                "tags": JSON.stringify(this.$refs.tagsModel.selectedItems),
                "implementedshapes":this.form.implementedshapes,
                "thingtemplate":this.fld.thingtemplate,
                "enabled":this.form.enabled,
                "avatar":this.form.avatar,
                "identifier":this.form.identifier,
                "documentationcontent":this.form.documentationcontent,
                "homemashup":values.homemashup,
                "published":this.form.published,
                "valuestream":this.form.valuestream,
                "user":this.userId,
              }
              if(this.pro.disableName){
                //修改价值流
                postAction(this.url.editValueStream,param).then((res =>{
                  if(res.success){
                    this.$message.success('操作成功!');
                  }else{
                    this.$message.error(res.message);
                  }
                }))
              }else{
                //添加价值流
                postAction(this.url.addValueStream,param).then((res =>{
                  if(res.success){
                    this.$message.success('操作成功!');
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
