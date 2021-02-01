const getters = {
  sidebar: state => state.app.sidebar,
  token: state => state.user.token,
  avatar: state => state.user.avatar,
  loginName: state => state.user.loginName,
  roles: state => state.user.roles,
  email: state => state.user.email
}
export default getters
