/* eslint-disable */
const models = [
  'financial' // 财务系统

]

const modelsConfig = {
  financial: {
    pages: {
      index: {
        title: process.env.VUE_APP_TITLE === 'pro' ? '财务' : '测试',
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
