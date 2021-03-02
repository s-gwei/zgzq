<template>
  <div :id="_uid" :style="{ width: width, height: height }"></div>
</template>
<script>
  export default {
    props: {
      // 模型文件路径
      sourceUrl: {
        type: String,
        // default: '/windchill/caxit-iphone3.pvz'
        default: 'http://192.168.2.173/Windchill/pvzDoc/caxit-iphone3.pvz'
        // default: '/0000000003.prt'
      },
      height: {
        type: [String],
        default: '400px'
      },
      width: {
        type: String,
        default: '100%'
      }
    },
    mounted() {
    },
    methods: {
      showModel(sourceUrlBy3d) {
        console.log('sourceUrlBy3d:', sourceUrlBy3d)
        let thingView, session, model
        let _this = this

        ThingView.init('/windchill/lib', function() {
          console.log('creo view webGL viewer is now initialized')
          session = ThingView.CreateSession(_this._uid)
          model = session.MakeModel()
          model.LoadFromURLWithCallback(sourceUrlBy3d, true, true, false, function(success, isstructure, errorstack) {
            console.log('Model LoadFromURLWithcallback - structure: ' + isstructure + ',success: ' + success)
          })
        })
      }
    },
    watch:{
      sourceUrl(val){
        this.showModel(val)
      }
    }
  }
</script>
