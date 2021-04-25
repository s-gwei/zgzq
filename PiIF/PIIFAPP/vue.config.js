const path = require('path')

function resolve(dir) {
  return path.join(__dirname, dir)
}

// vue.config.js
module.exports = {
  /*
    Vue-cli3:
    Crashed when using Webpack `import()` #2463
    https://github.com/vuejs/vue-cli/issues/2463
   */
  // 如果你不需要生产环境的 source map，可以将其设置为 false 以加速生产环境构建。
  productionSourceMap: false,

  //打包app时放开该配置
  //publicPath:'./',
  configureWebpack: config => {
    //生产环境取消 console.log
    if (process.env.NODE_ENV === 'production') {
      config.optimization.minimizer[0].options.terserOptions.compress.drop_console = true
    }
  },
  chainWebpack: (config) => {
    config.resolve.alias
      .set('@$', resolve('src'))
      .set('@api', resolve('src/api'))
      .set('@assets', resolve('src/assets'))
      .set('@comp', resolve('src/components'))
      .set('@views', resolve('src/views'))
      .set('@layout', resolve('src/layout'))
      .set('@static', resolve('src/static'))
  },

  css: {
    loaderOptions: {
      less: {
        modifyVars: {
          /* less 变量覆盖，用于自定义 ant design 主题 */
          'primary-color': '#1890FF',
          'link-color': '#1890FF',
          'border-radius-base': '4px',
        },
        javascriptEnabled: true,
      }
    }
  },

  devServer: {
    port: 8099,
    disableHostCheck: true,
    open: true,
    proxy: {
      /* '/api': {
         target: 'https://mock.ihx.me/mock/5baf3052f7da7e07e04a5116/antd-pro', //mock API接口系统
         ws: false,
         changeOrigin: true,
         pathRewrite: {
           '/jeecg-boot': ''  //默认所有请求都加了jeecg-boot前缀，需要去掉
         }
       },*/
      '/jeecg-boot': {
        // target:'http://pioa.pisx.com:80',
        // target: 'http://139.159.193.72:9090', //请求本地 需要jeecg-boot后台项目
        // target: 'http://192.168.2.135:8081',//本地 段鑫杨
        // target: 'http://192.168.2.149:8088',//本地 张宏建
        // target: 'http://192.168.111.198:9999',
        // target: 'http://10.2.81.218:9999', //服务器
        // target: 'http://192.168.111.136:9999',//正式
        target: 'http://192.168.2.178:9995', //孙光伟
        // target: 'http://192.168.2.72:9999',
        // target: 'http://192.168.2.71:7003',//本地 服务器
        ws: false,
        changeOrigin: true
      },
      '/Windchill': {
        target: 'http://192.168.2.54',
        ws: false,
        changeOrigin: true,
        pathRewrite: {
          // '/Windchill': '' //默认所有请求都加了Windchill前缀，需要去掉
        }
      }
    }
  },

  lintOnSave: undefined
}