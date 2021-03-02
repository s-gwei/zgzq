<template>
  <div>
    <a-tabs default-active-key="1" @change="tabCallback" tabPosition="left">
        <a-tab-pane key="1" tab="附加单据">
            111
        </a-tab-pane>
        <a-tab-pane key="2" tab="任务处理">
            222
        </a-tab-pane>
        <a-tab-pane key="3" tab="流程图">
            333
        </a-tab-pane>
    </a-tabs>
  </div>
</template>

<script>
export default {
    data(){
        return{
            tabKey:1,
        }
    },
    methods:{
        //历史tab
        tabCallback(key){
            this.tabKey = key;
        }
    }

}
</script>

<style>

</style>