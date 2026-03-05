import { createStore } from 'vuex';
import { getUserInfo } from '../api/user';
import orderApi from '../api/order';
import { getRoutes } from '../api/navigation';
import { getBillingRecords } from '../api/billing';

export default createStore({
  state: {
    user: null,
    orders: [],
    navigationData: null,
    billingRecords: [],
    platformRevenue: 0,
  },
  mutations: {
    SET_USER(state, user) {
      state.user = user;
    },
    SET_ORDERS(state, orders) {
      state.orders = orders;
    },
    SET_NAVIGATION_DATA(state, navigationData) {
      state.navigationData = navigationData;
    },
    SET_BILLING_RECORDS(state, billingRecords) {
      state.billingRecords = billingRecords;
    },
    SET_PLATFORM_REVENUE(state, revenue) {
      state.platformRevenue = revenue;
    },
  },
  actions: {
    fetchUser({ commit }, userId) {
      return getUserInfo(userId).then(response => {
        commit('SET_USER', response.data);
      });
    },
    fetchOrders({ commit }) {
      return orderApi.getOrderList().then(response => {
        commit('SET_ORDERS', response.data);
      });
    },
    fetchNavigationData({ commit }) {
      return getRoutes().then(response => {
        commit('SET_NAVIGATION_DATA', response.data);
      });
    },
    fetchBillingRecords({ commit }) {
      return getBillingRecords().then(response => {
        commit('SET_BILLING_RECORDS', response.data);
      });
    },
    fetchPlatformRevenue({ commit }) {
      return getBillingRecords().then(response => {
        commit('SET_PLATFORM_REVENUE', response.data);
      });
    },
  },
  getters: {
    isAuthenticated(state) {
      return !!state.user;
    },
    getOrders(state) {
      return state.orders;
    },
    getNavigationData(state) {
      return state.navigationData;
    },
    getBillingRecords(state) {
      return state.billingRecords;
    },
    getPlatformRevenue(state) {
      return state.platformRevenue;
    },
  },
});