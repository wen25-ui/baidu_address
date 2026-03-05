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
        <tr v-for="order in localOrders" :key="order.id">
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
import orderApi from '@/api/order';

export default {
  name: 'OrderTable',
  props: {
    orders: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      localOrders: []
    };
  },
  watch: {
    orders: {
      handler(val) {
        this.localOrders = val;
      },
      immediate: true
    }
  },
  methods: {
    fetchOrders() {
      orderApi.getOrderList().then(response => {
        this.localOrders = response.data || response;
      });
    },
    viewOrder(orderId) {
      // 查看订单详情
      console.log('查看订单:', orderId);
    },
    cancelOrder(orderId) {
      orderApi.deleteOrder(orderId).then(() => {
        this.fetchOrders();
      });
    }
  },
  mounted() {
    if (!this.orders || this.orders.length === 0) {
      this.fetchOrders();
    }
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