<template>
  <div class="order-management">
    <h1>订单管理</h1>
    <OrderTable :orders="orders" @refresh="fetchOrders" />
    <div class="navigation-map">
      <BaiduMap :routes="routes" />
    </div>
  </div>
</template>

<script>
import OrderTable from '@/components/OrderTable.vue';
import BaiduMap from '@/components/BaiduMap.vue';
import { fetchOrders, fetchRoutes } from '@/api/order.js';

export default {
  components: {
    OrderTable,
    BaiduMap,
  },
  data() {
    return {
      orders: [],
      routes: [],
    };
  },
  methods: {
    async fetchOrders() {
      try {
        const response = await fetchOrders();
        this.orders = response.data;
      } catch (error) {
        console.error('获取订单失败:', error);
      }
    },
    async fetchRoutes() {
      try {
        const response = await fetchRoutes();
        this.routes = response.data;
      } catch (error) {
        console.error('获取路线失败:', error);
      }
    },
  },
  mounted() {
    this.fetchOrders();
    this.fetchRoutes();
  },
};
</script>

<style scoped>
.order-management {
  padding: 20px;
}
.navigation-map {
  margin-top: 20px;
}
</style>