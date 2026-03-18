const TOKEN_KEY = 'token'
const USER_KEY = 'userInfo'

export const getToken = () => {
  return uni.getStorageSync(TOKEN_KEY)
}

export const setToken = (token) => {
  uni.setStorageSync(TOKEN_KEY, token)
}

export const removeToken = () => {
  uni.removeStorageSync(TOKEN_KEY)
}

export const getUserInfo = () => {
  const info = uni.getStorageSync(USER_KEY)
  return info ? JSON.parse(info) : null
}

export const setUserInfo = (userInfo) => {
  uni.setStorageSync(USER_KEY, JSON.stringify(userInfo))
}

export const removeUserInfo = () => {
  uni.removeStorageSync(USER_KEY)
}

export const isLoggedIn = () => {
  return !!getToken()
}
