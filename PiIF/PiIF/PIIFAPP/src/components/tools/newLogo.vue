<template>
  <div class="logo">
      <!-- update-begin- author:sunjianlei --- date:20190814 --- for: logo颜色根据主题颜色变化 -->
      <img v-if="navTheme === 'dark'" src="~@/assets/logo-white.png" alt="logo">
      <img v-else src="~@/assets/logo-white.png" alt="logo">
      <!-- update-begin- author:sunjianlei --- date:20190814 --- for: logo颜色根据主题颜色变化 -->
      <h1 v-if="showTitle">{{ title }}</h1>
  </div>
</template>

<script>
  import { mixin } from '@/utils/mixin.js'

  export default {
    name: 'Logo',
    mixins: [mixin],
    props: {
      title: {
        type: String,
        default: 'Report',
        required: false
      },
      showTitle: {
        type: Boolean,
        default: false,
        required: false
      }
    }
  }
</script>
<style lang="less" scoped>
  /*缩小首页布 局顶部的高度*/
  @height: 59px;

  div {
    box-shadow: none !important;
    .logo {
      height: @height !important;
      line-height: @height !important;
      box-shadow: none !important;
      transition: background 300ms;
      display: inline-block;
      a {
        color: white;
        // &:hover {
        //   color: rgba(255, 255, 255, 0.8);
        // }
      }
      img{
        display: inline-block;
        vertical-align: middle;
        height: 32px;
        padding-left: 24px ;

      }
      h1{
        display: inline-block;
        line-height: @height;
        color: #fff;
        font-size: 18px;
        margin: 0 0 0 8px;
        font-weight: 600;
      }
    }
    &.light .logo {
      background-color: @primary-color;
    }
  }
</style>