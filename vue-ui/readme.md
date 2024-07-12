1、下载依赖

    npm install --legacy-peer-deps 

    ps 后缀必须加 --legcy-peer-deps 因为有些插件目前npm已经不再支持或更新，需要强制下载旧版本或遗弃版本，不影响正常使用

2、打开 /applications/sys/public/pbRequest/index.js 更换baseUrl，具体Url跟后端沟通即可