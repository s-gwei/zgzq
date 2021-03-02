import axios from 'axios'
import { VueAxios } from './axios'

// 创建 axios 实例
const service = axios.create({
  // baseURL: '/wc-api/',
  baseURL: '/',
  timeout: 9000 // 请求超时时间
})

const err = error => {
  return Promise.reject(error)
}

// request interceptor
service.interceptors.request.use(
  config => {
    config.headers['Authorization'] = 'Basic d2NhZG1pbjp3Y2FkbWlu' // 让每个请求携带自定义 token 请根据实际情况自行修改
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// response interceptor
service.interceptors.response.use(response => {
  return response.data
}, err)

const installer = {
  vm: {},
  install(Vue, router = {}) {
    Vue.use(VueAxios, router, service)
  }
}

export { installer as VueAxios, service as axios }
