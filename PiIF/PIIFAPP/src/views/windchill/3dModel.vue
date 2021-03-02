<template>
  <div class="page-container">
    <a-card border="false" class="left">
      <a-select
        style="width:100%"
        show-search
        :value="value"
        placeholder="input search text"
        :default-active-first-option="false"
        :show-arrow="false"
        :filter-option="false"
        :not-found-content="null"
        @search="handleSearch"
        @change="handleChange"
      >
        <a-select-option v-for="d in searchResult" :key="d.valueKey+'-'+d.type">
          <a-tag color="#87d068" v-if="d.type === 'doc'">
            文档
          </a-tag>
          <a-tag color="#108ee9" v-else>
            部件
          </a-tag>
          {{ d.text }}
        </a-select-option>
      </a-select>
      <a-tree
        v-model="checkedKeys"
        v-if="currentType === 'part' && treeData.length > 0"
        :show-line="true"
        :multiple="false"
        :auto-expand-parent="true"
        :replaceFields="replaceFields"
        :default-expanded-keys="[expandedKeys]"
        :default-selected-keys="[expandedKeys]"
        :tree-data="treeData"
        @expand="onExpand"
        @select="onSelect"
      />
    </a-card>

    <a-card class="right">
      <span class="info-title">基本信息</span>
      <a-divider></a-divider>
      <a-row type="flex" justify="space-between">
        <a-col :span="8">
          <a-statistic title="名称" :value="baseInfo.name" style="margin-right: 50px"/>
        </a-col>
        <a-col :span="8">
          <a-statistic title="编号" :value="baseInfo.number" style="margin-right: 50px"/>
        </a-col>
        <a-col :span="8">
          <a-statistic title="版本" :value="baseInfo.version" style="margin-right: 50px"/>
        </a-col>

        <div style="height:1rem;"></div>
        <a-col :span="8">
          <a-statistic title="生命周期状态" :value="baseInfo.lifecycle" style="margin-right: 50px"/>
        </a-col>
        <a-col :span="8">
          <a-statistic title="修改者" :value="baseInfo.modifier" style="margin-right: 50px"/>
        </a-col>
        <a-col :span="8">
          <a-statistic title="类型" :value="baseInfo.type" style="margin-right: 50px"/>
        </a-col>
      </a-row>

      <div class="box-files" v-if="currentType === 'doc'">
        <a-divider></a-divider>
        <div class="file-opt">
          <div class="left">
            <span class="url">{{ baseInfo.url }}</span>
          </div>

          <div class="right">
            <a-button style="margin-right:0.5rem;" @click="downloadFile">下载文件</a-button>
            <a-button>上传更新</a-button>
          </div>
        </div>
      </div>

      <div class="box-3d" v-else-if="currentType === 'part'"><!-- -->
        <div class="right">
          <a-button @click="openCreoView(baseInfo.creoUrl)">查看图纸</a-button>
        </div>
        <a-divider></a-divider>
        <!--        <model3d></model3d>-->
        <model3d :sourceUrl="sourceUrlBy3d"></model3d>
      </div>
    </a-card>
  </div>
</template>
<script>
  import Model3d from '@/components/Model3d/Model3d'
  import { queryObjects, getObjectInfo } from '@/api/windchill'

  export default {
    components: { Model3d },
    data() {
      return {
        value: '',
        searchResult: [],
        expandedKeys: [],//选择的节点
        autoExpandParent: true,
        checkedKeys: [],
        currentType: '',
        baseInfo: {},
        treeData: [],
        replaceFields: { children: 'children', title: 'name', key: 'number' },
        sourceUrlBy3d: ''
      }
    },
    methods: {
      //打开creoview
      openCreoView(creoUrl){
        debugger
          // window.open('http://train11m030.pisx.com//Windchill/wtcore/jsp/wvs/edrview.jsp?url=http://train11m030.pisx.com/Windchill/servlet/WindchillAuthGW/com.ptc.wvs.server.util.WVSContentHelper/redirectDownload/caxit-iphone3.pvs?ContentHolder=wt.viewmarkup.DerivedImage:136420&HttpOperationItem=wt.content.ApplicationData:136863&u8=1&objref=OR:wt.viewmarkup.DerivedImage:136420&ContainerOid=OR%3Awt.pdmlink.PDMLinkProduct%3A115319','')
        if(creoUrl=='' || creoUrl==undefined){
          this.$message.info('暂无图纸！')
          return false
        }
        window.protocolCheck('creoview://?wcparams=eyJhdHRyaWJ1dGVzIjp7InVybGJhc2UiOiJodHRwOi8vdHJhaW4xMW0wMzAucGlzeC5jb20vV2luZGNoaWxsIiwiYnJvd3NlciI6ImNocm9tZSIsInNlc3Npb25pZCI6IkE3YzZ6WHhQdjhmaWxNNnlhLWI5U0lXTHV5ay5jcDgiLCJ1c2VyaWQiOiJ3Y2FkbWluIn0sImlkIjoiY3YifQ==',
          function () {
            alert("检测到您电脑creoview本地客户端未安装 请下载");
            return false
          });
        window.open(creoUrl)
      },
      //获取地址栏ip
      getIp() {
        return window.location.href.split('/windchill/3dModel')[0]
      },
      //下载文件
      downloadFile() {
        if (!this.baseInfo.url) {
          this.$message.info('暂无文件！')
          return false
        }
        let url = this.baseInfo.url.replace('http://192.168.2.54', this.getIp())
        window.open(url)
      },
      queryObjects(value) {
        if (value) {
          // 模糊搜索
          queryObjects({ queryNumber: value }).then(res => {
            this.searchResult = res.data.map((item, index) => {
              //部件/文档分类中number唯一，但是部件和文档的number可能会有相同的
              return {
                valueKey: item.number,//item.number
                number: item.number,
                text: item.number,
                type: item.type
              }
            })
          })
        }
      },
      //文本框值变化时回调
      handleSearch(value) {
        this.queryObjects(value)
      },
      //选中 option，或 input 的 value 变化（combobox 模式下）时，调用此函数
      handleChange(value) {
        let _this = this
        this.value = value.split('-')[0]
        this.treeData = []
        let item = this.searchResult.filter(titem => {
          return titem.valueKey == value.split('-')[0]
        })
        this.currentType = item[0].type
        let params = {
          number: item[0].number,
          type: item[0].type
        }
        getObjectInfo(params).then(res => {
          if (!res.data.url && res.data.type == 'part') {
            this.$message.info('暂无图纸！')
            // return false
          }
          _this.sourceUrlBy3d = res.data.url.replace('http://192.168.2.54', this.getIp())
          _this.baseInfo = res.data
          _this.baseInfo.url=res.data.url.replace('http://192.168.2.54', this.getIp())
          let bomTree = _this.baseInfo.children
          if (bomTree && bomTree.length > 0) {
            _this.treeData = [{ 'name': res.data.name,'number':res.data.number,'type':res.data.type,'children':bomTree }]
          }else{
            _this.treeData = [{ 'name': res.data.name,'number':res.data.number,'type':res.data.type,'children':[] }]
          }
          this.expandedKeys = item[0].number
        })
      },
      onSearch() {
      },
      //tree 展开/收起节点时触发
      onExpand(expandedKeys) {
        // console.log('onExpand', expandedKeys)
        /*this.expandedKeys = expandedKeys
        this.autoExpandParent = false*/
      },
      onCheck(checkedKeys) {
        // console.log('onCheck', checkedKeys)
        this.checkedKeys = checkedKeys
      },
      //tree 点击树节点触发
      onSelect(selectedKeys) {
        let _this = this

        let params = {
          number: selectedKeys[0],
          type: 'part'
        }
        getObjectInfo(params).then(res => {
          // console.log(res.data)
          this.baseInfo = res.data
          if (!res.data.url) {
            this.$message.info('暂无图纸！')
            // return false
          }
          _this.sourceUrlBy3d = res.data.url.replace('http://192.168.2.54', this.getIp())
          // let bomTree = this.baseInfo.children
          // if (bomTree) {
          //   this.treeData = bomTree
          // }
        })
      }
    }
  }
</script>
<style lang="less" scoped>
  .page-container {
    background: #f0f2f5;
    display: flex;
    justify-content: space-between;

    .left {
      width: 30%;
      background: white;
      padding: 1rem;
    }

    .right {
      width: calc(70% - 1rem);
      background: white;
      padding: 1rem;

      .info-title {
        font-weight: bolder;
      }

      .box-3d {
        margin-top: 2rem;
        border-radius: 5px;
        overflow: hidden;
      }

      .file-opt {
        display: flex;

        .left {
          flex: 1;

          .url {
            display: inline-block;
            line-height: 24px;
            padding: 5px 0;
            margin-right: 10px;
            word-wrap: break-word;
            word-break: break-all;
            overflow: hidden;
          }
        }

        .right {
          width: 300px;
          text-align: right;
        }
      }
    }

    .ant-tree {
      max-height: 100%;
    }

    /deep/ .ant-statistic-content-value {
      word-wrap: break-word;
    }
  }
</style>
