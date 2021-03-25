const getters = {
  sidebar: state => state.app.sidebar,
  token: state => state.user.token,
  avatar: state => state.user.avatar,
  loginName: state => state.user.loginName,
  email: state => state.user.email,
  userId: state => state.user.userId
}
export default getters
