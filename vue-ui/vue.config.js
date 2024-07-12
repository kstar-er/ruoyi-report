
const { defineConfig } = require('@vue/cli-service')
const path = require('path')
const UglifyPlugin = require("uglifyjs-webpack-plugin")
const CompressionPlugin = require('compression-webpack-plugin')
const FriendlyErrorsWebpackPlugin = require('friendly-errors-webpack-plugin')
const zlib = require('zlib')
const runningProject = require('./models')

function addStyleResource(rule) {
  rule.use('style-resource')
    .loader('style-resources-loader')
    .options({
    // 需要引入的公共文件
      patterns: [path.resolve(__dirname, './public/assets/scss/public.less')]
    })
}

if (process.env.VUE_APP_TITLE === "pro") {
  console.log("生产环境 - 正在打包:" + runningProject.name)
} else if (process.env.VUE_APP_TITLE === "dev"){
  console.log("开发环境")
} else console.log("测试环境 - 正在打包:" + runningProject.name)

const publicPath = process.env.VUE_APP_TITLE === "pro" ? `/${runningProject.name}/` : `/test/${runningProject.name}/`

module.exports = defineConfig({
  ...runningProject.config,
  outputDir: process.env.VUE_APP_TITLE === 'pro' ? 'dist/' + runningProject.name + '/' : 'test/' + runningProject.name + '/',
  publicPath: process.env.NODE_ENV === "development" ? '/' : publicPath,
  transpileDependencies: true,
  productionSourceMap: false,
  lintOnSave: true,
  configureWebpack: process.env.NODE_ENV === "development" ? {
    plugins: [new FriendlyErrorsWebpackPlugin({
      compilationSuccessInfo: {
        messages: [`your application is : ${runningProject.name}`],
        notes: [`if u want to build project, please use: npm run bulid : your project's name`]
      },
      clearConsole: true,
      additionalFormatters: [],
      additionalTransformers: []
    })]

  } : {
    resolve: {
      alias: {
        '@': path.resolve(__dirname, './src'),
        '@i': path.resolve(__dirname, './src/assets')
      }
    },

    // 下面两项配置才是 compression-webpack-plugin 压缩配置
    // 压缩成 .gz 文件
    plugins: [new CompressionPlugin({
      filename: '[path][base].gz',
      algorithm: 'gzip',
      test: /\.js$|\.css$|\.html$/,
      threshold: 10240,
      minRatio: 0.8
    }),

    // 压缩成 .br 文件，如果 zlib 报错无法解决，可以注释这段使用代码，一般本地没问题，需要注意线上服务器会可能发生找不到 zlib 的情况。
    new CompressionPlugin({
      filename: '[path][base].br',
      algorithm: 'brotliCompress',
      test: /\.(js|css|html|svg)$/,
      compressionOptions: {
        params: {
          [zlib.constants.BROTLI_PARAM_QUALITY]: 11
        }
      },
      threshold: 10240,
      minRatio: 0.8
    }),

    // 代码优化
    new UglifyPlugin({
      uglifyOptions: {
        compress: {
          drop_debugger: true,
          drop_console: true //生产环境自动删除console
        },
        warnings: false
      },
      sourceMap: false,
      parallel: true//使用多进程并行运行来提高构建速度。默认并发运行数：os.cpus().length - 1。
    })]

    // 开启分离 js
    // optimization: {
    //   runtimeChunk: 'single',
    //   splitChunks: {
    //     chunks: 'all',
    //     maxInitialRequests: Infinity,
    //     minSize: 20000,
    //     cacheGroups: {
    //       vendor: {
    //         test: /[\\/]node_modules[\\/]/,
    //         name(module) {
    //           // get the name. E.g. node_modules/packageName/not/this/part.js
    //           // or node_modules/packageName
    //           const packageName = module.context.match(/[\\/]node_modules[\\/](.*?)([\\/]|$)/)[1]

    //           // npm package names are URL-safe, but some servers don't like @ symbols
    //           return `npm.${packageName.replace('@', '')}`
    //         }
    //       }
    //     }
    //   }
    // }
  },
  chainWebpack: (config) => {
    const types = ['vue']
    types.forEach(type => addStyleResource(config.module.rule('less').oneOf(type)))
    if (process.env.NODE_ENV === "development") return
    config.optimization.splitChunks({
      cacheGroups: {
        common: { //commons 一般是是个人定义的
          name: 'chunk-common', // 打包后的文件名
          chunks: 'initial',
          minChunks: 1,
          maxInitialRequests: 5,
          minSize: 0,
          priority: 1,
          reuseExistingChunk: true
        },
        vendors: { //vendor 是导入的 npm 包
          name: 'chunk-vendors',
          test: /[\\/]node_modules[\\/]/,
          chunks: 'initial',
          maxSize: 600000,
          maxInitialRequests: 20,
          priority: 2,
          reuseExistingChunk: true,
          enforce: true
        },
        ElementInternals: { //把Element从chunk-vendors.js提取出来。当然我们也可以把mixins，vue.min.js等等也按照类似配置提取出来
          name: 'chunk-element-plus',
          test: /[\\/]node_modules[\\/]element-plus[\\/]/,
          chunks: 'initial',
          priority: 3,
          maxSize: 600000,
          reuseExistingChunk: true,
          enforce: true
        }
      }
    })
  }
})
