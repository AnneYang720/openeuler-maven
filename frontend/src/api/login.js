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

export function register(pojo){
  return request({
      url: '/user/register',
      method: 'post',
      data: pojo
  })
}

export function getRepoUserInfo(repo){
  return request({
      url: `/user/${repo}/getrepouserinfo`,//ES6写法
      method: 'get'
  });
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
