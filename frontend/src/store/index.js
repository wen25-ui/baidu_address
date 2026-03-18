import { createStore } from 'vuex'

const store = createStore({
  state: {
    user: null,
    token: '',
    orders: [],
    navigationData: null,
    billingRecords: [],
    platformRevenue: 0
  },
  mutations: {
    SET_USER(state, user) {
      state.user = user
    },
    SET_TOKEN(state, token) {
      state.token = token
    },
    SET_ORDERS(state, orders) {
      state.orders = orders
    },
    SET_NAVIGATION_DATA(state, data) {
      state.navigationData = data
    },
    SET_BILLING_RECORDS(state, records) {
      state.billingRecords = records
    },
    SET_PLATFORM_REVENUE(state, revenue) {
      state.platformRevenue = revenue
    },
    LOGOUT(state) {
      state.user = null
      state.token = ''
      uni.removeStorageSync('token')
      uni.removeStorageSync('userInfo')
    }
  },
  actions: {
    login({ commit }, { user, token }) {
      commit('SET_USER', user)
      commit('SET_TOKEN', token)
      uni.setStorageSync('token', token)
      uni.setStorageSync('userInfo', JSON.stringify(user))
    },
    logout({ commit }) {
      commit('LOGOUT')
      uni.reLaunch({ url: '/pages/login/login' })
    }
  },
  getters: {
    isAuthenticated(state) {
      return !!state.token
    },
    getUser(state) {
      return state.user
    },
    getOrders(state) {
      return state.orders
    }
  }
})

export default store
