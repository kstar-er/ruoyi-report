const filesList = []
let store = null
const getAllFilesInDirectory = async () => {
  const requireFiles = require.context('/src/applications/sys/store/fileManager', true, /\.js/)
  let fileKey = requireFiles.keys()
  await new Promise(async (resolve, reject) => {
    for (let i = 0; i < fileKey.length;) {
      await import ('/src/applications/sys/store/fileManager/' + fileKey[i].split('./')[1]).then(res => {
        filesList.push(res.default)
        i++
      })
    }

    // store = await createStore(defaultStoreOptions)
    resolve(1)
  })
}