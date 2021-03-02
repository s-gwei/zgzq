<template>
  <div class="EmployeeDistribute">
    <a-card :bordered="false">

      <!-- 查询区域 -->
      <div class="table-page-search-wrapper">
        <a-form layout="inline">
          <a-row :gutter="24">
            <a-col :md="5" :sm="12">
              <a-form-item label="时间:">
                <a-date-picker
                  :showTime="{ format: 'YYYY-MM-DD' }"
                  format="YYYY-MM-DD"
                  style="width: 100%"
                  :defaultValue="sTime"
                  @change="onChange" />
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
      <div class="employee_map" style="height: 100%;min-height: 700px;">
        <div id="EmployeeDistribute" style="margin-top: 35px;"></div>
      </div>
    </a-card>
  </div>
</template>
<script>
  import { Scene, PointLayer,Popup } from '@antv/l7';
  import { GaodeMap } from '@antv/l7-maps';
  import {getAction, postAction, deleteAction} from '@/api/manage'
  import { filterObj } from '@/utils/util';
  import moment from 'moment'
  const cityJson = require('@assets/json/city.json')
  export default {
    name:'EmployeeDistribute',
    data() {
      return {
        sTime: moment(new Date(), 'YYYY-MM-DD HH:mm:ss'),
        eTime: moment(new Date(), 'YYYY-MM-DD HH:mm:ss'),
        timeStr:'',
        citys:[],
        selectedItems: [],
        url: {
          list: "/sys/user/list",
          userList: '/online/cgform/api/getData/30237cf3cd344143953d8eea28554fd6',
        },
        userList:[],
        isorter:{
          column: 'createTime',
          order: 'desc',
        },
        pointLayer:null,
      };
    },
    mounted() {
      this.getUserList();
      this.queryUser(0);
      let data1 = {
        type:"FeatureCollection",
        features: []
      }
      const scene = new Scene({
        id: 'EmployeeDistribute',
        map: new GaodeMap({
          pitch: 0,
          style: 'dark',
          center: [ 103.99215001469588, 37.281597225674773 ],
          zoom: 4.5,
          maxZoom: 10
        })
      });
       this.pointLayer = new PointLayer({})
        .source(data1)
        .shape('circle')
        .size('capacity', [20, 60])
        .color('#7BE39E')
        .active(true)
        .animate(true)
        .style({
          opacity: 0.5,
          strokeWidth: 0
        });
      scene.addLayer(this.pointLayer);
      this.pointLayer.on('click', e => {
        const popup = new Popup({
          offsets: [ 0, 0 ],
          closeButton: false
        })
          .setLnglat(e.lngLat)
          .setHTML(`<span>${e.feature.properties.city}: ${e.feature.properties.capacity}人</span>`);
        scene.addPopup(popup);
      });
    },
    methods:{
      queryUser(num){
        this.citys =[];
        let param = {}
        param = this.getQueryParams()//查询条件
        param['pageSize'] = -521;
        getAction(this.url.userList,filterObj(param)).then(
          (res) => {
            if (res.success) {
              let record = res.result.records;
              if(record !== undefined){
                for (let i =0; i<record.length;i++){
                  this.citys.push(record[i].commit_city_day)
                }
              }
              this.init();
            } else {
              this.$message.error(res.message);
            }
          })
      },
      init(){
        let data = {}
        let localtion = cityJson.mapPlace
        let features = [];
        if(this.citys.length >0){
          let cityData = this.citys;
          for(let i=0;i<cityData.length;i++){
            let cityName = cityData[i]
            //计算数组中每个元素出现的次数
            const countOccurences = (cityData, value) => cityData.reduce((a, v) => v === value ? a + 1 : a + 0, 0);
            let jwd = localtion[cityName]
            if((jwd[0]!== undefined)&&(jwd[1] !== undefined))
            features.push(
              {
                geometry:{
                  coordinates:[jwd[0], jwd[1]],
                  type: "Point",
                },
                properties:{
                  city:cityData[i],
                  capacity: countOccurences(cityData,cityData[i]),
                },
                type: "Feature"
              },
            )
          }
          data = {
            type:"FeatureCollection",
            features:features
          }
        }else{
          data = {
            type:"FeatureCollection",
            features: []
          }
        }
        this.pointLayer.setData(data);
        this.pointLayer.size('capacity', [20, 60])
      },
      getQueryParams() {
        let param = Object.assign({},this.isorter);
        param.superQueryMatchType = 'and'
        let time = '';
        if(this.timeStr !== ''){
          time = this.timeStr;
        }else{
          let date = new Date()
          time = date.getFullYear() + '-' + ((date.getMonth() + 1) < 10 ? '0'+ (date.getMonth() + 1):(date.getMonth() + 1)) + '-' + (date.getDate() < 10 ? '0' + (date.getDate()-1) : (date.getDate()-1));
        }
        let superQueryParamsStr = '';
        if(this.selectedItems.length >0){
          superQueryParamsStr = '[{"type":"string","val":"'+time+'","field":"commit_time","rule":"eq"},{"type":"string","val":"'+this.selectedItems+'","field":"commit_name","rule":"eq"}]'
        }else{
          superQueryParamsStr = '[{"type":"string","val":"'+time+'","field":"commit_time","rule":"eq"}]'
        }
        param.superQueryParams = encodeURIComponent(superQueryParamsStr)
        return filterObj(param);
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
      onChange(date, dateString) {
        this.timeStr = dateString
      },
      handleChange(selectedItems) {
        this.selectedItems = selectedItems;
      },
      searchReset(){
        this.selectedItems = [];
      }
    },
    computed: {
      filteredOptions() {
        return this.userList.filter(o => !this.selectedItems.includes(o));
      },
    },
  };
</script>
<style scope>
  .EmployeeDistribute .ant-card-body{
    padding-top: 0;
  }

  #EmployeeDistribute {
    position: absolute;
    top: 0;
    bottom: 0;
    width: 100%;
  }
</style>