import Vue from 'vue';
import Vuex from 'vuex';
import user from '../api/user';
import order from '../api/order';
import navigation from '../api/navigation';
import billing from '../api/billing';

Vue.use(Vuex);

export default new Vuex.Store({
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
    fetchUser({ commit }) {
      return user.getUser().then(response => {
        commit('SET_USER', response.data);
      });
    },
    fetchOrders({ commit }) {
      return order.getOrders().then(response => {
        commit('SET_ORDERS', response.data);
      });
    },
    fetchNavigationData({ commit }) {
      return navigation.getNavigationData().then(response => {
        commit('SET_NAVIGATION_DATA', response.data);
      });
    },
    fetchBillingRecords({ commit }) {
      return billing.getBillingRecords().then(response => {
        commit('SET_BILLING_RECORDS', response.data);
      });
    },
    fetchPlatformRevenue({ commit }) {
      return billing.getPlatformRevenue().then(response => {
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