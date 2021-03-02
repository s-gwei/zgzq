<template>
  <a-modal
    :visible="visible"
    :width="modal.width"
    :style="modal.style"
    :footer="null"
    @cancel="handelCances">

    <template slot="title">
      <div style="width: 100%;height:20px;padding-right:32px;">
        <div style="float: left;">{{ title }}</div>
        <div style="float: right;">
          <a-button
            icon="fullscreen"
            style="width:56px;height:100%;border:0"
            @click="handleClickToggleFullScreen"/>
        </div>
      </div>
    </template>
    <slot name="content"></slot>
    <template slot="footer"></template>
  </a-modal>
</template>


<script>
  export default {
    data () {
      return {
        visible: false,
        modal: {
          width: '100%',
          style: { top: '0px' },
          fullScreen: true
        },
      }
    },
    props: {
      title: {
        type: String,
        default: '操作'
      }
    },
    methods: {
      open: function () {
        this.visible = true
      },
      handelCances: function () {
        this.visible = false
      },
      /** 切换全屏显示 */
      handleClickToggleFullScreen() {
        let mode = !this.modal.fullScreen
        if (mode) {
          this.modal.width = '100%'
          this.modal.style.top = '0px'
        } else {
          this.modal.width = '1200px'
          this.modal.style.top = '50px'
        }
        this.modal.fullScreen = mode
      }
    }
  }
</script>