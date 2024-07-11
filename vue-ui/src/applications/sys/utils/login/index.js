import { pbRequest, loading } from "@/applications/sys/public/pbRequest/index"

const userLogin = async function(params) {
  let { data: { code, data, msg } } = await pbRequest.post(`/auth/login`, params)
  return code === 200 ? { data, code } : msg
}

const logout = async function() {
  let { data: { code, data, msg } } = await pbRequest.delete(`/auth/logout`)
  return code === 200 ? { data, code } : msg
}

const getUserInfo = async function() {
  let { data: { code, data, msg } } = await pbRequest.get(`/system/user/getInfo`)
  return code === 200 ? { data, code } : msg
}

const getRouters = async function() {
  let { data: { code, data, msg } } = await pbRequest.get(`/system/menu/getRouters`)
  return code === 200 ? { data, code } : msg
}

export { loading, userLogin, getUserInfo, getRouters, logout }