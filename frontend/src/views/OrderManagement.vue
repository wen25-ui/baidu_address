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
import orderApi from '@/api/order';
import { getRoutes } from '@/api/navigation';

export default {
  name: 'OrderManagement',
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
        const response = await orderApi.getOrderList();
        this.orders = response.data || response;
      } catch (error) {
        console.error('获取订单失败:', error);
      }
    },
    async fetchRoutes() {
      try {
        const response = await getRoutes();
        this.routes = response.data || response;
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