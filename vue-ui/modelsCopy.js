/* eslint-disable */
const models = [
  'warehouse_pro' // 基础后台框架
]

const modelsConfig = {
  warehouse_pro: {
    ip: '192.168.28.108',
    pages: {
      index: {
        title: process.env.VUE_APP_TITLE === 'pro' ? 'Hello World' : 'Hello World',
        entry: "src/applications/sys/main.js",
        template: "src/applications/sys/index.html",
        filename: "index.html"
      }
    },
    devServer: {
      hot: true, //热更新
      // 此处开启 https
      https: false,
      port: '9999',
      historyApiFallback: true,
      allowedHosts: "all"
    }
  }
}

module.exports = {
  name: models[0],
  config: modelsConfig[models[0]]
}
