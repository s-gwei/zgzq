<template>
  <div class="main">
    <div class="content">
      <div class="left">
        <div class="header">
          <a-button @click="handleExportXmlAction"><a-icon type="vertical-align-bottom" />导出XML</a-button>
          <a-button @click="xmlVisible = !xmlVisible"><a-icon type="copy" />预览</a-button>
          <a-button @click="handleExportSvgAction"><a-icon type="vertical-align-bottom" />导出SVG</a-button>
          <a-button @click="handleClear"><a-icon type="delete" />清除</a-button>
          <a-button @click="saveXmlAction"><a-icon type="snippets" />保存</a-button>
        </div>
        <div class="control" :style="{height: getContainerHeight}">
          <div class="canvas" ref="canvas"></div>
          <a-modal :visible="xmlVisible" title="XML" @cancel="xmlVisible = false" style="top: 0px;">
            <vue-ace-editor 
              v-model="process.xml"
              @init="editorInit"
              lang="xml"
              theme="chrome"
              width="100%"
              height="400"
              :options="{wrap: true, readOnly: true}">
            </vue-ace-editor>
            <span slot="footer" class="dialog-footer">
              <a-button icon="a-icon-document" v-clipboard:copy="process.xml" v-clipboard:success="onCopy">复 制</a-button>
              <a-button icon="a-icon-close" type="primary" @click="xmlVisible = false">关闭</a-button>
            </span>
          </a-modal>
        </div>
      </div>
      <div class="widget-config-container right">
        <div>
          <div class="tabs">
            <div 
              class="config-tab" 
              :class="{active: configTab=='node'}"
              @click="handleConfigSelect('node')">节点属性</div>
            <div 
              class="config-tab" 
              :class="{active: configTab=='process'}"
              @click="handleConfigSelect('process')">流程属性</div>
          </div>
          <div class="config-content">
            <node-property-panel 
              v-if="bpmnModeler && configTab=='node'"
              :modeler="bpmnModeler"/>
            <process-property-panel 
              v-if="configTab=='process'"
              :process-data="process"></process-property-panel>
          </div>
        </div>
      </div>
    </div>
    
  </div>
</template>

<script>
  import './index.scss'
  // bpmn-js 设计器
  import BpmnModeler from "bpmn-js/lib/Modeler";
  // 对activiti的扩展
  import activitiExtensionModule from 'activiti-bpmn-moddle/lib';
  import activitiModdle from "activiti-bpmn-moddle/resources/activiti";

  import NodePropertyPanel from "./NodePropertyPanel"; // 属性面板
  import ProcessPropertyPanel from "./ProcessPropertyPanel";

  import BpmData from "./BpmData";
  import VueAceEditor from 'vue2-ace-editor'

  import {getAction,putAction,postAction} from '@/api/manage';


  export default {

    props: {
      xml:{
        type: String,
        default: '',
      },
      processInfo:{
        type:Object
      },
      isDetail:{
        type:Boolean
      },
      processName: {
        type: String,
        default: '',
      },
      processKey: {
        type: String,
        default: '',
      },
      processId: {
        type: String,
        default: '',
      },
      processType:{
        type: String,
        default: 'test',
      },
      processDescription: {
        type: String,
        default: '描述',
      },
    },

    components: {
      NodePropertyPanel,
      ProcessPropertyPanel,
      VueAceEditor
    },

    data() {
      return {
        bpmnModeler: null,
        process: {
          name: this.processName,
          id: this.processKey,
          description: this.processDescription,
          processType:this.processType,
          xml: '',
          svg: '',
        },
        configTab: 'node',
        nodeProcessSelect: null,

        xmlVisible: false,
        element: null,
        bpmData: new BpmData(),
      };
    },

    methods: {
      haha(){
        console.log("哈哈")
      },
      getContainerHeight() {
        return (document.body.offsetHeight - 75) + 'px'
      },
      /**
       * init ace editor.
       */
      editorInit: function () {
        require('brace/ext/language_tools') //language extension prerequsite...
        require('brace/mode/xml')    //language
        require('brace/theme/chrome')
      },
      /**
       * init
       */
      async createNewDiagram() {
       // 将字符串转换成图显示出来
        try {
          const result = await this.bpmnModeler.importXML(this.process.xml);
          const { warnings } = result;
          this.adjustPalette();
        } catch (err) {
          console.log(err.message, err.warnings);
        }


      },

      // 调整左侧工具栏排版
      adjustPalette() {
        try {
          // 获取 bpmn 设计器实例
          const canvas = this.$refs.canvas;
          const djsPalette = canvas.children[0].children[1].children[4];
          const djsPalStyle = {
            width: "130px",
            padding: "5px",
            background: "white",
            left: "20px",
            borderRadius: 0
          };
          for (var key in djsPalStyle) {
            djsPalette.style[key] = djsPalStyle[key];
          }
          const palette = djsPalette.children[0];
          const allGroups = palette.children;
          allGroups[0].style["display"] = "none";
          // 修改控件样式
          for (var gKey in allGroups) {
            const group = allGroups[gKey];
            for (var cKey in group.children) {
              const control = group.children[cKey];
              const controlStyle = {
                display: "flex",
                justifyContent: "flex-start",
                alignItems: "center",
                width: "100%",
                padding: "5px"
              };
              if (
                control.className &&
                control.dataset &&
                control.className.indexOf("entry") !== -1
              ) {
                const controlProps = this.bpmData.getControl(
                  control.dataset.action
                );
                control.innerHTML = `<div style='font-size: 14px;font-weight:500;margin-left:15px;'>${
                  controlProps["title"]
                }</div>`;
                if (controlProps['tooltip']) {
                  control.title = controlProps['tooltip'];
                }
                for (var csKey in controlStyle) {
                  control.style[csKey] = controlStyle[csKey];
                }
              }
            }
          }
        } catch (e) {
          console.log(e);
        }
      },

      // 当图发生改变的时候会调用这个函数，这个data就是图的xml
      setEncoded(type, data) {
        // 把xml转换为URI，下载要用到的
        const encodedData = encodeURIComponent(data);
        if (data) {
          if (type === 'XML') {
            // 获取到图的xml，保存就是把这个xml提交给后台
            this.process.xml = data;
            return {
              filename: this.process.name+'.bpmn20.xml',
              href: "data:application/bpmn20-xml;charset=UTF-8," + encodedData,
              data: data
            }
          }
          if (type === 'SVG') {
            this.process.svg = data;
            return {
              filename: this.process.name+'.svg',
              href: "data:application/text/xml;charset=UTF-8," + encodedData,
              data: data
            }
          }
        }
      },
      /**
       * 导出BPMN XML文件
       */
      async handleExportXmlAction() {
        try {
          const result = await this.bpmnModeler.saveXML({ format: true });
          const { xml } = result;
          let {filename, href} = this.setEncoded('XML', xml);
          if (href && filename) {
            let a = document.createElement('a');
            a.download = filename; //指定下载的文件名
            a.href = href; //  URL对象
            a.click(); // 模拟点击
            URL.revokeObjectURL(a.href); // 释放URL 对象
          }
        } catch (err) {
          console.log(err);
        }
      },
      /**
       * 导出BPMN SVG文件
       */
      async handleExportSvgAction() {
        try {
          const result = await this.bpmnModeler.saveSVG({ format: true });
          const { svg } = result;
          let {filename, href} = this.setEncoded('SVG', svg);
          if (href && filename) {
            let a = document.createElement('a');
            a.download = filename; //指定下载的文件名
            a.href = href; //  URL对象
            a.click(); // 模拟点击
            URL.revokeObjectURL(a.href); // 释放URL 对象
          }
        } catch (err) {
          console.log(err);
        }
      },
      /**
       * 清空设计器内容
       */
      handleClear() {
        this.process.xml =  `<?xml version="1.0" encoding="UTF-8"?><bpmn2:definitions xmlns:bpmn2="http://www.omg.org/spec/BPMN/20100524/MODEL" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI" targetNamespace="http://bpmn.io/schema/bpmn" xsi:schemaLocation="http://www.omg.org/spec/BPMN/20100524/MODEL BPMN20.xsd"><bpmn2:process id="process1567044459787" name="流程1567044459787"><bpmn2:documentation>描述</bpmn2:documentation></bpmn2:process><bpmndi:BPMNDiagram id="BPMNDiagram_1"><bpmndi:BPMNPlane id="BPMNPlane_1" bpmnElement="process1567044459787" /></bpmndi:BPMNDiagram></bpmn2:definitions>`
        this.createNewDiagram();
      },
      /**
       * 保存xml提交给后台
       */
      saveXmlAction(){
        let params = {
          processName:this.process.name,
          processKey:this.process.id,
          processStatus:this.isDetail ? this.processInfo.processStatus : false,
          processType:this.process.processType,
          processXml:this.process.xml
        }
        if(this.isDetail){
          params.id = this.processInfo.id;
          putAction('/workflow/design/process/edit',params).then((res)=>{
            if(res.success){
              this.$message.success("保存成功")
              this.$emit('loadData');
            }else{
              this.$message.error(res.message);
            }
          })
        }else{
          postAction('/workflow/design/process',params).then((res)=>{
            if(res.success){
              this.$message.success("保存成功")
              this.$emit('loadData');
            }else{
              this.$message.error(res.message);
            }
          })
        }
      },
      /**
       * 复制内容到剪切板成功回调
       */
      onCopy() {
        this.$message.success('内容复制成功');
      },
      /**
       * 配置Tab切换
       */
      handleConfigSelect(value) {
        this.configTab = value;
      },
    },

    mounted() {
      this.process.name = this.isDetail ? this.processInfo.processName : `流程${this.processId}`;
      this.process.id = this.isDetail ? this.processInfo.processKey : `process${this.processId}`;
      this.process.processType = this.isDetail ? this.processInfo.processType : this.processType
      const canvas = this.$refs.canvas;
      // 生成实例
      this.bpmnModeler = new BpmnModeler({
        container: canvas,
        additionalModules: [
          activitiExtensionModule
        ],
        moddleExtensions: {
          activiti: activitiModdle
        }
      });

      // 监听流程图改变事件
      const _this = this;
      this.bpmnModeler.on("commandStack.changed", async () => {
        try {
          const result = await _this.bpmnModeler.saveXML({format: true});
          const { xml } = result;
          _this.setEncoded('XML', xml);
        } catch (err) {
          console.log(err);
        }
        try {
          const result = await _this.bpmnModeler.saveSVG({format: true});
          const { svg } = result;
          _this.setEncoded('SVG', svg);
        } catch (err) {
          console.log(err);
        }
      });
      // 新增流程定义
      // 初始化XML文本
      this.process.xml = this.xml
      this.createNewDiagram();
    },
    watch: {
      'process.xml': {
        handler(val) {
          this.$nextTick(() => {
            //
          })
        }
      }
    }
  };
</script>

<style lang="scss" scoped>
  /*左边工具栏以及编辑节点的样式*/
  @import "~bpmn-js/dist/assets/diagram-js.css";
  @import "~bpmn-js/dist/assets/bpmn-font/css/bpmn.css";
  @import "~bpmn-js/dist/assets/bpmn-font/css/bpmn-codes.css";
  @import "~bpmn-js/dist/assets/bpmn-font/css/bpmn-embedded.css";

  $primary-color: #409EFF;
  $primary-background-color: #ffffff;
  
  .main{
    height: 100vh;
    .content{
      display: flex;
      justify-content: space-between;
      height: 100% !important;
      .left{
        width: 100%;
        border-right:1px solid #e0e0e0;
        .header{
          display: flex;
          height: 45px;
          justify-content: flex-end;
          align-items: center;
          border-bottom:1px solid #e0e0e0;
          button{
            margin-right:5px;
            border:none;
            color: $primary-color;
          }
        }
        .control{
          background-color: $primary-background-color;
          width: 100%;
          height: 100%;
          .canvas {
            width: 100%;
            height: 100%;
          }
          .panel {
            position: absolute;
            right: 0;
            top: 50px;
            width: 300px;
          }
          .bjs-powered-by {
            display: none;
          }
          .toolbar {
            position: absolute;
            top: 0;
            right: 320px;
            height: 40px;
            width:  600px;
            a {
              text-decoration: none;
              margin: 5px;
              color: $primary-color;
            }
          }
        }
      }
      .right{
        position: relative;
        width: 400px;
        .tabs{
          display: flex;
          .config-tab{
            height: 45px;
            line-height: 45px;
            width: 145px;
            text-align: center;
            font-size: 14px;
            font-weight: 500;
            position: relative;
            cursor: pointer;
            &.active{
              border-bottom: solid 2px $primary-color;
            }
          }
        }
      }
    }
  }
  .ant-drawer-body{
    padding: 0 24px 10px !important;
  }
  .ant-modal{
    width: 680px !important;
    top:20px;
  }

</style>
