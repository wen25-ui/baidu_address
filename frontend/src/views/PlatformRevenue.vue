<template>
  <div class="platform-revenue">
    <h1>平台收入管理</h1>
    <div class="revenue-summary">
      <h2>收入概览</h2>
      <p>当前收入: {{ totalRevenue }} 元</p>
      <p>本月收入: {{ monthlyRevenue }} 元</p>
      <p>总订单数: {{ totalOrders }}</p>
    </div>
    <div class="revenue-details">
      <h2>收入详情</h2>
      <table>
        <thead>
          <tr>
            <th>订单ID</th>
            <th>收入</th>
            <th>时间</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="record in revenueRecords" :key="record.orderId">
            <td>{{ record.orderId }}</td>
            <td>{{ record.amount }} 元</td>
            <td>{{ record.time }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import { fetchRevenueData } from '@/api/billing';

export default {
  name: 'PlatformRevenue',
  setup() {
    const totalRevenue = ref(0);
    const monthlyRevenue = ref(0);
    const totalOrders = ref(0);
    const revenueRecords = ref([]);

    const loadRevenueData = async () => {
      try {
        const data = await fetchRevenueData();
        if (data) {
          totalRevenue.value = data.totalRevenue || 0;
          monthlyRevenue.value = data.monthlyRevenue || 0;
          totalOrders.value = data.totalOrders || 0;
          revenueRecords.value = data.records || [];
        }
      } catch (error) {
        console.error('获取收入数据失败:', error);
      }
    };

    onMounted(loadRevenueData);

    return {
      totalRevenue,
      monthlyRevenue,
      totalOrders,
      revenueRecords,
    };
  },
};
</script>

<style scoped>
.platform-revenue {
  padding: 20px;
}

.revenue-summary {
  margin-bottom: 20px;
}

.revenue-details table {
  width: 100%;
  border-collapse: collapse;
}

.revenue-details th, .revenue-details td {
  border: 1px solid #ddd;
  padding: 8px;
}

.revenue-details th {
  background-color: #f2f2f2;
}
</style>