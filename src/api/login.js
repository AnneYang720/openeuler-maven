import request from '@/utils/request'

export function login(email, password) {
  return request({
    url: '/user/login',
    method: 'post',
    data: {
      email,
      password
    }
  })
}

export function getInfo() {
  return request({
    url: '/user/info',
    method: 'get'
  })
}

export function updateInfo(pojo){
  return request({
      url: '/user/saveinfo',
      method: 'put',
      data: pojo
  })
}

// export function getInfoById(userId) {
//   return request({
//     url: `/user/${userId}`,
//     method: 'get'
//   })
// }

// export function logout() {
//   return request({
//     url: '/user/logout',
//     method: 'post'
//     // TODO: add token
//   })
// }
