<template>
  <div class="billing-detail">
    <h2>计费详情</h2>
    <div v-if="billingInfo">
      <p><strong>订单ID:</strong> {{ billingInfo.orderId }}</p>
      <p><strong>用户ID:</strong> {{ billingInfo.userId }}</p>
      <p><strong>计费时间:</strong> {{ billingInfo.billingTime }}</p>
      <p><strong>计费金额:</strong> {{ billingInfo.amount }} 元</p>
      <p><strong>平台抽成:</strong> {{ billingInfo.platformFee }} 元</p>
      <p><strong>实际收入:</strong> {{ billingInfo.actualIncome }} 元</p>
    </div>
    <div v-else>
      <p>加载中...</p>
    </div>
  </div>
</template>

<script>
import { getBillingRecords } from '@/api/billing';

export default {
  name: 'BillingDetail',
  data() {
    return {
      billingInfo: null,
    };
  },
  created() {
    this.fetchBillingDetail();
  },
  methods: {
    async fetchBillingDetail() {
      try {
        const response = await getBillingRecords();
        if (response && response.length > 0) {
          this.billingInfo = response[0];
        }
      } catch (error) {
        console.error('获取计费详情失败:', error);
      }
    },
  },
};
</script>

<style scoped>
.billing-detail {
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}
</style>