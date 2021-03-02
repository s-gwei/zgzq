<template>
  <div>
    <a-select :style="`{'width': ${width}px}`" :disabled="disabled" mode="multiple" placeholder="请选择" @change="changeTags" @dropdownVisibleChange="getTagsData" v-model="tagIdList">
      <div slot="dropdownRender" slot-scope="menu">
        <v-nodes :vnodes="menu" />
        <a-divider style="margin: 4px 0;" />
        <div
          style="padding: 4px 8px; cursor: pointer;text-align: center;"
          @mousedown="e => e.preventDefault()"
          @click="addItem"
        >
          <a-icon type="plus" /> 添加
        </div>
      </div>
      <a-select-option v-for="item in tagsStrData" :value="item.id" :key="item.id">{{ item.vocabularyStr}}</a-select-option>
    </a-select>
    <a-modal
      title="操作"
      :visible="visible"
      @ok="handleSubmit"
      :confirmLoading="confirmLoading"
      @cancel="handleCancel"
    >
      <a-form :form="form" :label-col="{ span: 8 }" :wrapper-col="{ span: 14 }">
        <a-form-item label="词汇名称">
          <a-select v-decorator="['tagsName', {}]" @change="selectTags">
            <a-select-option v-for="item in tagsData" :value="item.id" :key="item.id">{{ item.name }}</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="词汇名称">
          <a-input
            placeholder="请输入"
            v-decorator="['vocabularyName', {}]"
          />
        </a-form-item>
        <a-form-item :label="tagName + '词汇'">
            <a-tag v-for="item in vocabularyList" :key="item.id">{{item.vocabulary}}</a-tag>
        </a-form-item>
      </a-form>
    </a-modal>
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
          list: '/sys/visiblePermission/getByUserId',
          list2: '/iot/tag/getVocabularyByUserId',
          tagList: '/iot/tag/getVocabularyByTagId',
          add:'/iot/tag/addVocabulary',
        },
        tagsStrData: [],
        tagsData: [],
        tagName: '',
        tagId: '',
        tagIdList: [],
        vocabularyList: [],
        selectedItems: [],
        // form
        form: this.$form.createForm(this)
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
    components: {
      VNodes: {
        functional: true,
        render: (h, ctx) => ctx.props.vnodes,
      },
    },
    created () {
      this.getTagsData()
    },
    methods: {
      getTagsData () {
          if (!this.url.list) {
            this.$message.error('请设置url.list属性!')
            return
          }
          let params = {
            userId: this.$store.state.user.info.id,
            tableName: 'iot_tag',
            pageNo: 1,
            pageSize: 10000
          }
          getAction(this.url.list, params).then((res) => {
            if (res.success) {
             this.tagsData = res.result.records
            }
          })
          getAction(this.url.list2, {userId: this.userId}).then((res) => {
            if (res.success) {
             this.tagsStrData = res.result
            }
          })
      },
      changeTags (value) {
        this.selectedItems = []
        value.forEach((t) => {
          let obj = this.tagsStrData.find((v) => (v.id === t))
          if (obj) {
            this.selectedItems.push(obj)
          }
        })
      },
      /***  弹框操作*/
      handleSubmit (e) {
        e.preventDefault();
        this.confirmLoading = true
        this.form.validateFields((err, values) => {
          if (!values.tagsName) {
            this.$message.warning('请选择标签');
            this.confirmLoading = false
            return;
          }
          if (!err) {
            let reg = /^[\u4E00-\u9FA5A-Za-z]+[\u4E00-\u9FA5A-Za-z0-9]+$/;
            if (reg.test(values.vocabularyName) === false) {
              //名称不能输入特殊字符
              this.$message.warning('名称不能以数字开头或带有特殊符号！')
              this.confirmLoading = false
              return;
            }
            postAction(this.url.add, {tagId: this.tagId, vocabulary: values.vocabularyName}).then(
              (res) => {
                if (res.success) {
                  this.$message.success('添加成功')
                  this.visible = false
                  this.confirmLoading = false
                  this.$refs.table.refresh()
                } else {
                  this.$message.error(res.message)
                }
              }
            )
          }
        });
      },
      handleCancel () {
        this.visible = false
        this.form.resetFields();
        this.vocabularyList = []
        this.tagName = ''
        this.tagId = ''
      },
      selectTags (data) {
        if (!this.url.tagList) {
          this.$message.error('请设置url.tagList属性!')
          return
        }
        let params = {
          tagId: data,
        }
        this.tagId = data
        this.tagName = this.tagsData.find((v) => (v.id === data)).name
        getAction(this.url.tagList, params).then((res) => {
          if (res.success) {
              this.vocabularyList = res.result
          }
        })
      },
      setTagsList (list) {
        this.tagIdList = []
        this.selectedItems = list
        list.forEach((v) => {
          this.tagIdList.push(v.id)
        })
        return this.tagIdList
      },
      /** 添加**/
      addItem() {
        this.visible = true
      },
    },
  };
</script>