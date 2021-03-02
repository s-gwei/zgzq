<template>

    <iframe :src="iframe_src" frameborder="0" width="100%" height="800px" scrolling="auto"></iframe>

</template>

<script>

  export default {
    name: "TabIfeameView",
    props:['iframe_src'],
    data () {
      return {
        url: "",
        id:""
      }
    },
    created () {
      this.goUrl()
      let self = this;
      window.addEventListener('message', function(e) {
        self.iframeMsg(e)
      }, false)
    },
    updated () {
      this.goUrl()
    },
    watch: {
      $route(to, from) {
        this.goUrl();
      }
    },
    methods: {
      goUrl () {
        let url = this.$route.meta.url
        let id = this.$route.path
        this.id = id
        if (url !== null && url !== undefined) {
          this.url = url;
        }
      },
      iframeMsg (e) {
        this.$router.push(e.data)
      },
    }
  }
</script>

<style>
</style>