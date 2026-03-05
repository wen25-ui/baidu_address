<template>
  <div class="order-table">
    <h2>订单列表</h2>
    <table>
      <thead>
        <tr>
          <th>订单ID</th>
          <th>用户</th>
          <th>起点</th>
          <th>终点</th>
          <th>费用</th>
          <th>状态</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="order in orders" :key="order.id">
          <td>{{ order.id }}</td>
          <td>{{ order.user }}</td>
          <td>{{ order.startPoint }}</td>
          <td>{{ order.endPoint }}</td>
          <td>{{ order.cost }} 元</td>
          <td>{{ order.status }}</td>
          <td>
            <button @click="viewOrder(order.id)">查看</button>
            <button @click="cancelOrder(order.id)" v-if="order.status === '进行中'">取消</button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
export default {
  data() {
    return {
      orders: []
    };
  },
  methods: {
    fetchOrders() {
      // 这里调用API获取订单数据
      this.$api.order.getOrders().then(response => {
        this.orders = response.data;
      });
    },
    viewOrder(orderId) {
      // 跳转到订单详情页面
      this.$router.push({ name: 'OrderDetail', params: { id: orderId } });
    },
    cancelOrder(orderId) {
      // 调用API取消订单
      this.$api.order.cancelOrder(orderId).then(() => {
        this.fetchOrders(); // 重新获取订单列表
      });
    }
  },
  mounted() {
    this.fetchOrders();
  }
};
</script>

<style scoped>
.order-table {
  margin: 20px;
}
table {
  width: 100%;
  border-collapse: collapse;
}
th, td {
  border: 1px solid #ddd;
  padding: 8px;
}
th {
  background-color: #f2f2f2;
}
</style>