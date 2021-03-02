<template>
  <div class="property-panel" ref="propertyPanel">
    <a-form :model="form" layout="horizontal" :label-col="labelCol" :wrapper-col="wrapperCol">
      <a-form-item label="节点类型">
        <a-input v-model="form.$type" disabled allowClear></a-input>
      </a-form-item>
      <a-form-item label="节点ID">
        <a-input v-model="form.id" disabled allowClear></a-input>
      </a-form-item>
      <a-form-item label="节点名称">
        <a-input v-model="form.name" @input="nameChange" allowClear></a-input>
      </a-form-item>
      <!-- <a-form-item label="节点颜色">
        <colorPicker v-model="form.color" @change="colorChange"></colorPicker>
      </a-form-item> -->
      <template v-if="element && userTask">
        <!-- 指定节点表单 -->
        <a-form-item label="表单类型">
          <a-input v-model="form.formCategory" allowClear></a-input>
        </a-form-item>
        <a-form-item label="表单KEY">
          <a-input v-model="form.formKey" @input="formKeyChange" allowClear></a-input>
        </a-form-item>
        <!-- 任务节点允许选择人员 -->
        <!-- <a-form-item label="节点人员">
          <a-select v-model="form.userType" placeholder="请选择" @change="typeChange" style="width: 212px">
            <a-select-option value="assignee">指定人员</a-select-option>
            <a-select-option value="candidateUsers">候选人员</a-select-option>
            <a-select-option value="candidateGroups">角色/岗位</a-select-option>
          </a-select>
        </a-form-item> -->
        <!-- 指定人员 -->
        <a-form-item label="指定人员">
          <a-select v-model="form.assignee" :filter-option="filterOption" show-search allowClear placeholder="请选择" key="1" @change="(value) => addUser({assignee: value})" style="width: 212px">
            <a-select-option v-for="item in users" :key="item.id" :value="item.id">{{ item.username }}</a-select-option>
          </a-select>
        </a-form-item>
        <!-- 候选人员 -->
        <a-form-item label="候选人员">
          <a-select v-model="form.candidateUsers" :filter-option="filterOption" show-search allowClear placeholder="请选择" key="2" mode="multiple" style="width: 212px" @change="(value) => addUser({candidateUsers: value.join(',') || value})">
            <a-select-option v-for="item in users" :key="item.id" :value="item.id">{{ item.username }}</a-select-option>
          </a-select>
        </a-form-item>
        <!-- 角色/岗位 -->
        <a-form-item label="角色/岗位">
          <a-select v-model="form.candidateGroups" :filter-option="filterOption" show-search allowClear placeholder="请选择" style="width: 212px" @change="(value) => addUser({candidateGroups: value})">
            <a-select-option v-for="item in roles" :key="item.id" :value="item.id">{{ item.roleName }}</a-select-option>
          </a-select>
        </a-form-item>
      </template>

      <!-- 分支允许添加条件 -->
      <a-form-item label="分支条件" v-if="sequenceFlow">
        <a-select v-model="form.user" :filter-option="filterOption" show-search allowClear placeholder="请选择">
          <a-select-option v-for="item in users" :key="item.id" :value="item.id">{{ item.uesename }}</a-select-option>
        </a-select>
      </a-form-item>
    </a-form>
  </div>
</template>

<script>
import {getAction,putAction,postAction} from '@/api/manage';
export default {
  name: "NodePropertyPanel",
  props: {
    /**
     * BpmnModeler设计器对象
     */
    modeler: {
      type: Object,
      required: true
    },

  },
  computed: {
    userTask() {
      if (!this.element) {
        return;
      }
      return this.element.type === "bpmn:UserTask";
    },
    sequenceFlow() {
      if (!this.element) {
        return;
      }
      return this.element.type === "bpmn:SequenceFlow";
    }
  },
  data() {
    return {
      labelCol: { span: 7 },
      wrapperCol: { span: 18 },
      form: {
        id: "",
        name: "",
        color: "#000"
      },
      element: {},
      users: [],
      roles: []
    };
  },
  mounted() {
    this.getAllUser();
    this.getAllRole();
    this.handleModeler();
  },
  methods: {
    //获取所有角色
    getAllUser(){
      this.loading = true;
      getAction('/sys/user/listAll',{}).then((res) => {
        if (res.success) {
          this.users = res.result;
        }
        if(res.code===510){
          this.$message.warning(res.message)
        }
        this.loading = false;
      })
    },
    //获取所有角色
    getAllRole(){
      this.loading = true;
      getAction('/sys/role/queryall',{}).then((res) => {
        if (res.success) {
          this.roles = res.result;
        }
        if(res.code===510){
          this.$message.warning(res.message)
        }
        this.loading = false;
      })
    },
    //过滤
    filterOption(input, option) {
      return (
        option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0
      );
    },
    // 监听节点选择变化
    handleModeler() {
      this.modeler.on("selection.changed", e => {
        const element = e.newSelection[0];
        if (!element) {
          return;
        }
        this.element = element;
        this.form = {
          ...element.businessObject,
          ...element.businessObject.$attrs
        };
        // this.form.color = "#000"
        console.log(this.form)
        if (this.form.candidateUsers && this.form.candidateUsers.length) {
          this.form["candidateUsers"] = this.form["candidateUsers"].split(",") || [];
        }else{
          this.form["candidateUsers"] = []
        }
      });

      //  监听节点属性变化
      this.modeler.on("element.changed", e => {
        const { element } = e;
        if (!element) {
          return;
        }
        //  新增节点需要更新回属性面板
        if (element.id === this.form.id) {
          this.form.name = element.businessObject.name;
          this.form = { ...this.form };
        }
      });
    },

    // 属性面板名称，更新回流程节点
    nameChange(name) {
      const modeling = this.modeler.get("modeling");
      modeling.updateLabel(this.element, this.form.name);
    },

    //formKey,更新回流程节点
    formKeyChange(formKey){
      const modeling = this.modeler.get("modeling");
      modeling.updateProperties(this.element, { formKey: this.form.formKey });
    },

    // 属性面板颜色，更新回流程节点
    colorChange(color) {
      this.form.color = color
      const modeling = this.modeler.get("modeling");
      modeling.setColor(this.element, {
        fill: null,
        stroke: color
      });
      modeling.updateProperties(this.element, { color: this.form.color });
    },

    // 任务节点配置人员
    addUser(properties) {
      if(this.form.assignee && ((this.form.candidateUsers&&this.form.candidateUsers.length) || this.form.candidateGroups)){
        if(Object.keys(properties)[0]=='assignee'){
          this.form.assignee = ''
          this.$message.error('已有其他人员，暂不可选指定人员');
        }else{
          this.form.candidateUsers = []
          this.form.candidateGroups = ''
          this.$message.error('已有指定人员，暂不可选其他人员');
        }
      }
      this.updateProperties(
        Object.assign(properties, {
          // userType: Object.keys(properties)[0]
        })
      );
    },

    // 切换人员类型
    typeChange() {
      this.form.assignee = ''
      this.form.candidateUsers = []
      this.form.candidateGroups = ''
      const types = ["assignee", "candidateUsers", "candidateGroups"];
      types.forEach(type => {
        delete this.element.businessObject.$attrs[type];
        delete this.form[type];
      });
    },

    // 更新节点属性的方法
    updateProperties(properties) {
      const modeling = this.modeler.get("modeling");
      modeling.updateProperties(this.element, properties);
    }
  },

  watch: {
    'form.name': {
      handler(val) {
        if (this.element) {
          this.element.businessObject.name = val;
        }
      }
    }
  }
};
</script>

<style lang="scss" scoped>
.property-panel {
  padding: 10px;
  width: 100%;
}
.ant-row{
  display: flex !important;
}
.ant-form-item{
  margin-bottom: 10px;
}
</style>
