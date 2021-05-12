<template>
    <div>
        <div class="mytableBtn">
            <button v-for="(item,index) in button" :key="index" :class="item.icon" @click="clickBtn(item)" type="button">
                <a-icon :type="item.icon" theme="filled" />
                <span>{{item.text}}</span>
            </button>
        </div>
        <div class="myTable">
            <div class="mythead">
                <table>
                    <thead>
                        <tr>
                            <th></th>
                            <template v-for="(item,index) in tableColumns">
                                <th  :colspan="item.children.length" v-if="item.children && item.children.length">{{item.name}}</th>
                                <th :key="index" :rowspan="2" v-else>{{item.name}}</th>
                            </template>
                        </tr>
                        <tr>
                            <th><input type="checkbox" @change="changeAll('all')" id="all"/></th>
                            <th v-for="(item,index) in titleRows" :key="index">{{item.name}}</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-for="(item,index) in rowsData" :key="index">
                            <td>
                                <input type="checkbox" @change="change(index)" :checked="checked.index" id="checkinput1"/>
                            </td>
                            <td v-for="(itm,idx) in item" :key="idx">
                                <a-input :value="itm['value']" v-model="rowsData[index][idx]['value']"/>
                            </td>
                        </tr>
                    </tbody>
            </table>
            </div>
            <div class="mybody">
                <table>
                    <!-- <thead>
                        <tr>
                            <th></th>
                            <template v-for="(item,index) in tableColumns">
                                <th  :colspan="item.children.length" v-if="item.children && item.children.length">{{item.name}}</th>
                                <th :key="index" :rowspan="2" v-else>{{item.name}}</th>
                            </template>
                        </tr>
                        <tr>
                            <th><input type="checkbox" @change="changeAll('all')" id="all"/></th>
                            <th v-for="(item,index) in titleRows" :key="index">{{item.name}}</th>
                        </tr>
                    </thead> -->
                    <tbody>
                        <tr v-for="(item,index) in rowsData" :key="index">
                            <td>
                                <input type="checkbox" @change="change(index)" :checked="checked.index" class="checkinput"/>
                            </td>
                            <td v-for="(itm,idx) in item" :key="idx">
                                <!-- <a-input :value="itm['value']" type="number" v-model="rowsData[index][idx]['value']" @input="input"/> -->
                                <a-input :value="itm['value']" v-model="rowsData[index][idx]['value']" @input="input"/>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</template>
<script>
// const docVal = require('@assets/json/docVal.json')
export default {
    data(){
        return {
            docVal: {},
            button: [
                {
                    value: "01",
                    text: "添加行",
                    icon: "setting"
                },
                // {
                //     value: "02",
                //     text: "添加补气",
                //     icon: "setting"
                // },
                {
                    value: "03",
                    text: "删除行",
                    icon: "delete"
                }
            ],
            tableColumns: [
                // {
                //     name: "排汽",
                //     children: [
                //         {name: "流量"},
                //         {name: "压力"},
                //         {name: "温度"}
                //     ] 
                // },
                // {
                //     name: "可调抽汽",
                //     children: [
                //         {name: "流量"},
                //         {name: "压力"},
                //         {name: "温度"}
                //     ] 
                // },
                // {
                //     name: "补汽I",
                //     children: [
                //         {name: "流量"},
                //         {name: "压力"},
                //         {name: "温度"},
                //         {name: "焓"}
                //     ] 
                // }
            ],
            titleRows: [],
            length: 0,
            rowsData: [],
            checked: [],
            currentNodeTitleVal: "", //发动机,前车...
            dataParams: {},
            dataArrayFinal: [], //处理后的数据
            PartNumberDate: []
        }
    },
    mounted(){
        this.currentNodeTitleVal = this.$store.state.report.currentTableType
       this.docVal = this.$store.state.report.docVal
       // console.log( this.docVal,this.currentNodeTitleVal);
        const tableData = this.docVal[this.currentNodeTitleVal]
        this.$set(this,"tableColumns",tableData.html[0].params)
        this.getTitle()
        this.getDealDate(this.$store.state.report.result.children)
        this.dataParams = this.$store.state.report.result.children[0]
    },
    watch: {
        // rowsData: {
        //     handler(v){
        //         console.log('enter');
        //         // this.dealArray(v)
        //     }
            
        // }
    },
    methods: {
        input(){
            this.dealArray(this.rowsData)
        },
        getDealDate(data){
            const _this = this
            // console.log(data,'data');
            this.PartNumberDate =new Array(100).fill("")
            data.forEach(function(item,index){
                _this.PartNumberDate[index] = item.PartNumber
                // realArray.push(item.classification)
            })
            const newArr = []
            var titleRows = JSON.parse(JSON.stringify(this.titleRows))
            var rowsData = []
            // console.log(titleRows,'title');
             for(let i = 0;i<data.length;i++){
                 var columnData =[]
                titleRows.forEach(function(itm,index){
                     var obj={}
                     obj["value"] = data[i].classification[itm.field]
                     obj["name"] = itm["name"]
                     obj["field"] = itm["field"]
                     columnData.push(obj)
                  })
                //   console.log(columnData,i);
                rowsData.push(columnData)
            }
            // console.log(newArr,rowsData,this.titleRows);
            this.$set(this,"rowsData",rowsData)
        },
        getWidth(){
            var mybodyHeight = document.querySelector(".mybody").offsetHeight
            if(mybodyHeight >= 240){
                // console.log('enter',document.querySelector(".mybody").offsetWidth - 7 + "px");
                document.querySelector(".mythead").style.width = document.querySelector(".mybody").offsetWidth - 7 + "px"
            } else{
                document.querySelector(".mythead").style.width = document.querySelector(".mybody").offsetWidth + "px"
            }
        },
        change(index){
            const val = this.checked[index],dom= document.querySelectorAll(".checkinput")
            this.checked.splice(index,1 ,!val)
            const isCheckedAll = [...dom].every(item => item.checked == true)
            document.getElementById('all').checked = isCheckedAll
        },
        changeAll(id){
            var chk = document.getElementById(id).checked,_this=this,
                dom= document.querySelectorAll(".checkinput")
                // console.log(_this.checked,dom);
            for (var i = 0; i < dom.length; i++) {
                  dom[i].checked = chk;
                  _this.checked[i] =chk
             }
            //  _this.checked = new Array(_this.checked).fill(chk)
        },
        clickBtn(info){
            const _this = this
            // const array =new Array(this.length).fill('')
            const array = JSON.parse(JSON.stringify(this.titleRows))
            array.map(function(item){
                // console.log(item,'item');
                item.value = ""
            })
            // console.log(info);
            // 增加行
            if(info.value =="01"){
                // console.log(document.getElementById('all').checked);
                var chk =document.getElementById('all').checked
                this.checked.push(chk)
                this.rowsData.push(array)
                var dom = document.querySelectorAll(".checkinput")
                for (var i = 0; i < dom.length; i++) {
                   dom[i].checked = chk;
                   _this.checked[i] =chk
                }
                this.$nextTick(function(){
                    this.getWidth()
                })
            } if(info.value =="03"){
                const dom= document.querySelectorAll(".checkinput")
                var isChecked = [...dom].some(item => item.checked == true)
                // var index = [...dom].findIndex(item => item.checked == true)
                // console.log(index);
                if(isChecked){
                    if(dom.length == 1){
                        this.$message.warning("请至少保留一组数据")
                        return
                    }
                    // console.log(_this.checked,_this.rowsData);
                     this.$confirm({
                      title:"确认删除",
                      content:"是否删除选中数据?",
                      onOk: function(){
                          document.getElementById('all').checked = false
                       for(var i = 0;i<_this.checked.length;i++){
                            if(_this.checked[i]){
                                _this.rowsData.splice(i,1)
                                _this.checked.splice(i,1)
                                i--
                            } else{
                                document.querySelectorAll(".checkinput")[i].checked =false
                            }
                            _this.dealArray(_this.rowsData)
                       }
                      }
                    });
                } else{
                    // alert('至少选择一行删除');
                    this.$confirm({
                    //   title:"确认删除",
                      content:"至少选择一组数据进行删除",
                    //   onOk: function(){
                    //     console.log('ok');
                    //   }
                    });
                }
            }
        },
        getTitle(){
            const _this = this
            this.tableColumns.map(function(item){
                if(!item.children){
                    _this.length ++ 
                } else{
                    item.children.forEach(element => {
                        _this.length ++ 
                        _this.titleRows.push({name: element.name,field: element.field})
                    });
                }
                 
            })
            this.checked =new Array(this.rowsData).fill(false)
        },
        dealArray(arr){
            // console.log(arr,this.dataParams,'arr');
            const dealArrayVal = []
            arr.forEach(function(item,index){
                item.map(function(itm,index){
                    // obj[itm.field] = itm.value ? Number(itm.value) : null
                    itm.name = itm.field
                })
                // console.log(arr,'obj');
                // for(var key in item[0]){
                //     // obj[field] = item[0].value
                //     obj[item[0].field] = item[0].value
                // }
            //     obj[item[0].field] = item.value
                dealArrayVal.push(item)
               }

            )
            this.final(dealArrayVal)
        },
        final(dealArrayVal){
            this.$store.commit("isClisked", true)
            this.$set(this,"dataArrayFinal", [])
            const _this = this
            dealArrayVal.map(function(item,index){
                const objs = {}
                for(const key in _this.dataParams){
                    if(key == 'classification'){
                        objs["attribute"] = item
                    }  if(key == 'PartNumber'){
                        objs[key] = _this.PartNumberDate[index]
                    }else{
                       objs[key] = _this.dataParams[key]
                    }
                }
                _this.dataArrayFinal.push(objs)
            })
            console.log(_this.dataArrayFinal);
            this.$store.commit("dataTableFinal", _this.dataArrayFinal)
        }
    }
    
}
</script>
<style lang="scss" scoped>
.mytableBtn{
    position: relative;
}
button{
    background:#fff;
    text-shadow: 0 -1px 0 rgba(0, 0, 0, 0.12);
    box-shadow: 0 2px 0 rgba(0, 0, 0, 0.025);
    border: 1px solid #ccc;
    line-height: 32px;
    font-weight: 400;
    text-align: center;
    height: 32px;
    padding: 0 10px;
    font-size: 14px;
    border-radius: 4px;
    cursor: pointer;
    outline:none;
    i{
        color:green;
        margin-right: 5px;
        font-size: 16px;
    }
    &:nth-child(n + 1){
        margin-right: 10px;
    }
    &:hover{
        background: #fdfcfc;
        text-shadow: 0 -1px 0 rgba(0, 0, 0, 0.2);
        box-shadow: 0 2px 0 rgba(0, 0, 0, 0.06);
    }
}
.delete{
    // float: right;
}
.myTable{
    margin: 10px 0;
    // position: relative;
    box-sizing: border-box;
    table{
        width: 100%;
        text-align: center;
    }
    .mythead{
        // position: absolute;
        // left: 0;
        // top: 0;
        height: 48px;
        overflow: hidden;
        z-index: 2;
    }
    .mybody{
        max-height: 240px;
        overflow-y: auto;
    }
}
table,table tr th, table tr td { border:1px solid #E9E9E9; }
tbody>tr{
    height: 24px;
    td{
       padding: 4px;
    }
   /deep/.ant-input{
        border: none;
       text-align: center;
       &:hover{
            border: none!important;
        }
        &:active{
            border: none!important;
        }
    }
}
</style>