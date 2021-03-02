PiIF企业级快速开发平台-（创新研发业务拓展团队）

当前最新版本： 1.0.0（发布日期：2020/2/11）



#### 前端技术
 
- 基础框架：[ant-design-vue]Ant Design Of Vue 实现
- JavaScript框架：Vue
- Webpack
- node
- yarn
- eslint
- @vue/cli 3.2.1
- [vue-cropper](https://github.com/xyxiao001/vue-cropper) - 头像裁剪组件
- [@antv/g2](https://antv.alipay.com/zh-cn/index.html) - Alipay AntV 数据可视化图表
- [Viser-vue](https://viserjs.github.io/docs.html#/viser/guide/installation)  - antv/g2 封装实现



项目下载和运行
----

- 拉取项目代码
```bash
svn https://115.28.209.139:449/svn/IOT/soft/app （账号）
cd  ant-design-vue-jeecg
```

- 安装依赖
```
yarn install
```

- 开发模式运行
```
yarn run serve
```

- 编译项目
```
yarn run build
```

- Lints and fixes files
```
yarn run lint
```



其他说明
- 修改 Ant Design 配色，在文件 `vue.config.js` 中，其他 less 变量覆盖参考 [ant design](https://ant.design/docs/react/customize-theme-cn) 官方说明
```ecmascript 6
  css: {
    loaderOptions: {
      less: {
        modifyVars: {
          /* less 变量覆盖，用于自定义 ant design 主题 */

          'primary-color': '#F5222D',
          'link-color': '#F5222D',
          'border-radius-base': '4px',
        },
        javascriptEnabled: true,
      }
    }
  }


项目目录结构
node_modules  模块
public        公共
src           总目录
  api         请求接口目录
  assets      静态资源目录
  cas         单点登录
  components  组件目录
  config      router路由设置
  mixins      混入js
  router      路由文件
  store       存储
  utils       工具类
  views       业务视图
  App.vue     主页
  main.js     js主入口
  permission.js 权限入口
  defaultSettings.js 默认配置项
packjson.json 依赖
README.md    项目介绍
vue.config.js vue配置详情
