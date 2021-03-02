<template>
  <div class="EmployeeFlow">
    <a-card :bordered="false">

      <!-- 查询区域 -->
      <div class="table-page-search-wrapper">
        <a-form layout="inline">
          <a-row :gutter="24">
            <a-col :md="7" :sm="12">
              <a-form-item label="时间:">
                <a-range-picker
                  :showTime="{ format: 'YYYY-MM-DD' }"
                  format="YYYY-MM-DD"
                  style="width: 100%"
                  :placeholder="['开始时间', '结束时间']"
                  @change="onChange"
                />
              </a-form-item>
            </a-col>
            <a-col :md="5" :sm="12">
              <a-form-item label="姓名:">
                <a-select
                  placeholder="请输入姓名"
                  :value="selectedItems"
                  @change="handleChange"
                  style="width: 100%"
                >
                  <a-select-option v-for="item in filteredOptions" :key="item" :value="item">
                    {{ item }}
                  </a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="8">
              <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
                <a-button type="primary" icon="search" @click="queryUser(1)">查询</a-button>
                <a-button type="primary" icon="reload" style="margin-left: 8px" @click="searchReset">重置</a-button>
              </span>
            </a-col>

          </a-row>
        </a-form>
      </div>

      <div class="employee_flow_map" style="height: 100%;min-height: 700px;">
        <div id="EmployeeFlow" style="margin-top: 35px;"></div>
      </div>
    </a-card>
  </div>
</template>
<script>
  import { Scene, LineLayer,Popup } from '@antv/l7';
  import { GaodeMap } from '@antv/l7-maps';
  import { filterObj } from '@/utils/util';
  import {getAction, postAction, deleteAction} from '@/api/manage'
  import JInput from '@/components/jeecg/JInput'
  import moment from 'moment'
  const cityJson = require('@assets/json/city.json')
  export default {
    name:'EmployeeFlow',
    components:{
      JInput
    },
    data() {
      return {
        sTime: moment(new Date(), 'YYYY-MM-DD'),
        eTime: moment(new Date(), 'YYYY-MM-DD'),
        startTimeStr:'',
        endTimeStr:'',
        layer:null,
        users:[],
        userList:[],
        selectedItems: [],
        url: {
          list: "/sys/user/list",
          userList: '/online/cgform/api/getData/30237cf3cd344143953d8eea28554fd6',
        },
        isorter:{
          column: 'createTime',
          order: 'desc',
        },
      };
    },
    computed: {
      filteredOptions() {
        return this.userList.filter(o => !this.selectedItems.includes(o));
      },
    },
    mounted() {
      this.getUserList()
      this.queryUser()
      let data=[]
      const scene = new Scene({
        id: 'EmployeeFlow',
        map: new GaodeMap({
          style: 'dark',
          pitch: 0,
          center: [103.99215001469588, 37.281597225674773 ],
          zoom: 4.5,
        })
      });
      this.layer = new LineLayer({})
        .source(data, {
          parser: {
            type: 'json',
            x: 'lng1',
            y: 'lat1',
            x1: 'lng2',
            y1: 'lat2',
          },
        })
        .size(1)
        .shape('arc')
        .color('#8C1EB2')
        .style({
          opacity: 0.8,
          blur: 0.99
        });
      scene.addLayer(this.layer);
      this.layer.on('mousemove', e => {
        const popup = new Popup({
          offsets: [ 0, 0 ],
          closeButton: false
        })
          .setLnglat(e.lngLat)
          .setHTML(`<span>${e.feature.city1} → ${e.feature.city}</span>`);
        scene.addPopup(popup);
      });
    },
    methods:{
      handleChange(selectedItems) {
        this.selectedItems = selectedItems;
      },
      onChange(date, dateString) {
        this.startTimeStr = dateString[0]
        this.endTimeStr = dateString[1]
      },
      queryUser(num){
        this.users = [];

        //1、根据查询条件查询数据
        let param = {}
        param = this.getQueryParams()//查询条件
        param['pageSize'] = -521;
        getAction(this.url.userList,filterObj(param)).then(
          (res) => {
            if (res.success) {
              let record = res.result.records;
              if(record !== undefined){
                for(let i=0;i<record.length;i++){
                  //2、将查询出来的所有用户和城市的数据装在一起
                  this.users.push({
                    name:record[i].commit_name,
                    city:record[i].commit_city_day
                  })
                }
              }
              //3、所有的数据去重
              let userData = this.users;
              let array = [];
              for(let j = 0; j < userData.length; j++) {
                let isChage = true;
                for(let k = 0; k < array.length; k++) {
                  if((userData[j]['name'] === array[k]['name'])&&(userData[j]['city'] === array[k]['city'])) {
                    isChage = false;
                  }
                }
                if(isChage) {
                  array.push(userData[j]);
                }
              }
              //4、根据用户重新拼接城市数据
              let allData = array;
              var payArr = [allData[0]];
              for (var p = 1; p < allData.length; p++) {
                let payItem = allData[p];
                let repeat = false;
                for (var q = 0; q < payArr.length; q++) {
                  if (payItem.name === payArr[q].name) {
                    payArr[q].city = payArr[q].city + ',' + payItem.city;
                    repeat = true;
                    break;
                  }
                }
                if (!repeat) {
                  payArr.push(payItem);
                }
              }
              //5、对用户去重,一个用户对应一条数据
              let city = [];
              for(let x = 0; x < payArr.length; x++) {
                let flag = true;
                for(let y = 0; y < city.length; y++) {
                  if(payArr[x]['name'] === city[y]['name']) {
                    isChage = false;
                  }
                }
                if(flag) {
                  city.push(payArr[x]);
                }
              }
              //初始化场景划线
              this.init(city)
            } else {
              this.$message.error(res.message);
            }
          })

      },

      getUserList(){
        if(!this.url.list){
          this.$message.error("请设置url.list属性!")
          return
        }
        let params = {
          column: 'createTime',
          order: 'desc',
          field: 'username',
          pageNo: 1,
          pageSize: 300,
        }
        getAction(this.url.list, params).then((res) => {
          if (res.success) {
            let record = res.result.records
            for(let i=1;i<record.length;i++){
              this.userList.push(record[i].realname)
            }
          }
          if(res.code===510){
            this.$message.warning(res.message)
          }
        })
      },
      getQueryParams() {
        let param = Object.assign({},this.isorter);
        param.superQueryMatchType = 'and'
        let superQueryParamsStr = '';
        if((this.selectedItems.length >0)&&(this.startTimeStr !== '')&&(this.endTimeStr !== '')){
          superQueryParamsStr = '[{"type":"string","val":"'+this.startTimeStr+'","field":"commit_time","rule":"ge"},{"val":"'+this.endTimeStr+'","type":"string","field":"commit_time","rule":"le"},{"type":"string","val":"'+this.selectedItems+'","field":"commit_name","rule":"eq"}]'
        }else if((this.startTimeStr !== '')&&(this.endTimeStr !== '')){
          superQueryParamsStr = '[{"type":"string","val":"'+this.startTimeStr+'","field":"commit_time","rule":"ge"},{"type":"string","val":"'+this.endTimeStr+'","field":"commit_time","rule":"le"}]'
        }else if(this.selectedItems.length >0){
          superQueryParamsStr = '[{"type":"string","val":"'+this.selectedItems+'","field":"commit_name","rule":"eq"}]'
        }else{
          superQueryParamsStr = ''
        }
        param.superQueryParams = encodeURIComponent(superQueryParamsStr)
        return filterObj(param);
      },
      searchReset(){
        this.selectedItems = [];
      },
      init(args){
        let citys = [];
        let data = [];
        let localtion = cityJson.mapPlace
        if(args.length >0){
          for(let i = 0;i<args.length;i++){
            let str = args[i].city
            let strs = new Array();
            strs = str.split(",")
            citys.push({
              name:args[i].name,
              city:strs
            })
          }
        }
        if(citys.length>0){
          for(let j=0;j<citys.length;j++){
            if(citys[j].city.length>1){
              let cityArr = citys[j].city;
              for(let k=0;k<cityArr.length-1;k++){
                let cityName = cityArr[k];
                let cityName2 =  cityArr[k+1]
                let loc = localtion[cityName]
                let loc2 = localtion[cityName2]
                if((loc!==undefined)&&(loc2!==undefined)){
                  data.push({
                    lng1: loc[0],
                    lat1: loc[1],
                    lng2: loc2[0],
                    lat2: loc2[1],
                    city1:cityName,
                    city:cityName2
                  })
                }
              }
            }
          }
        }
        //更新场景数据
        this.layer.setData(data);
      }
    }
  };
</script>
<style>
  .EmployeeFlow .ant-card-body{
    padding-top: 0;
  }

  #EmployeeFlow {
    position: absolute;
    top: 0;
    bottom: 0;
    width: 100%;
  }
</style>