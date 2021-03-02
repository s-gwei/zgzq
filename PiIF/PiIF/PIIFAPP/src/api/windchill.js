import { axios } from '@/utils/wcRequest'

export function action(url, parameter, method = 'get') {
  let params = { url, method }
  if (method === 'post') {
    params.data = parameter
  } else {
    params.params = parameter
  }
  return axios(params)
}

// 模糊搜索
export const queryObjects = parameter => action('/Windchill/servlet/rest/sany/queryObjects', parameter)
// 部件或零件搜索
export const getObjectInfo = parameter => action('/Windchill/servlet/rest/sany/getObjectInfo', parameter)
// 部件搜索
export const selectPart = parameter => action('/Windchill/servlet/rest/sany/selectPart', parameter)
