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
    partNumberSelected: []//零部件编码集合
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
      console.log(info,'info');
      if(info.name == "源总成"){
        state.infoParams.affectLink = info.add
        state.infoParams.constraintLink = []
      } else{
        state.infoParams.constraintLink = info.add
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
  },

  actions: {
  }
}

export default user