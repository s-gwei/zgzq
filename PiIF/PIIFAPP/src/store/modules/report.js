// import Vue from 'vue'
// import { login, logout, phoneLogin } from "@/api/login"
// import { ACCESS_TOKEN, USER_NAME,USER_INFO,USER_AUTH,SYS_BUTTON_AUTH } from "@/store/mutation-types"
// import { welcome } from "@/utils/util"
// import { queryPermissionsByUser } from '@/api/api'
// import { getAction } from '@/api/manage'

const user = {
  state: {
    treeData: [],
    currentNodeTitleVal: "",
    currentEditionVal: "最新版",
    currentPartNumberVal: "", //部件编码
    currentTotalCarPartNumberVal: "",//整车编码,
    currentCenterChangeVal: {},
    infoParams: {
      parentNumber: "",
      partNumber: "",
      attribute: [],
      affectLink: [],
      constraintLink: [],
      children: []
    },
    publishDisable: true,
    currentVal: [],
    partNumberSelected: [],//零部件编码集合
    dataTableFinal: [],
    selectedRowKeys: [],
    selectedRows: [],
    result: {},
    docVal: {}, //样式文件
    currentTableType: {},//当前列表type
    btnEditable: null, //权限控制
    isClisked: false, //是否修改控制保存
    centerAndRightShow: false,
  },
  mutations: {
    SET_INFO: (state, info) => {
      state.info = info
    },
    treeDataSource: (state,info) => {
      // console.log(state,info,1);
      state.treeData = info
    },
    currentNodeTitle: (state,info) => {
      // console.log(info);
      state.currentNodeTitleVal = info
    },
    currentEdition: (state,info) => {
      state.currentEditionVal = info
    },
    currentPartNumber: (state,info) => {
      state.currentPartNumberVal = info
      state.infoParams.partNumber = info
    },
    currentTotalCarPartNumber: (state,info) => {
      state.currentTotalCarPartNumberVal = info
      state.infoParams.parentNumber = info
    },
    currentCenterChange: (state,info) => {
      state.currentCenterChangeVal = info
      state.infoParams.attribute = info
    },
    affectLink: (state,info) => {
      if(info.name == "源总成"){
        state.infoParams.affectLink = info.add
        state.infoParams.constraintLink = []
      } else if(info.name == "被约束总成"){
        state.infoParams.constraintLink = info.add
        state.infoParams.affectLink = []
      }else if(info.name == "全部"){
        state.infoParams.constraintLink = []
        state.infoParams.affectLink = []
      }
      // console.log(state.infoParams);
    },
    currentVal: (state,info) => {
      state.currentVal = info
    },
    partNumberSelected: (state,info) => {
      state.partNumberSelected = info
    },
    changePublishBtnState:  (state,info) => {
      state.publishDisable = info
    },
    selectedRowKeys: (state,info) => {
      state.selectedRowKeys = info
    },
    selectedRows: (state,info) => {
      state.selectedRows = info
    },
    result: (state,info) => {
      state.result = info
      // state.infoParams.children = info
    },
    btnEditable: (state,info) => {
      state.btnEditable = info == 'true' || info == true ? true : false
    },
    dataTableFinal: (state,info) => {
      state.dataTableFinal = info
      state.infoParams.children = info
    },
    isClisked: (state,info) => {
      state.isClisked = info
    },
    setdocVal: (state,info) => {
      state.docVal = info
    },
    currentTableType: (state,info) => {
      state.currentTableType = info
    },
    centerAndRightShow: (state,info) => {
      state.centerAndRightShow = info
    }
  },

  actions: {
  }
}

export default user