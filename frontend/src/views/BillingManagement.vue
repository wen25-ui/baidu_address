<template>
  <div class="billing-management">
    <h1>计费管理</h1>
    <div class="billing-summary">
      <h2>计费概览</h2>
      <p>当前计费模式：平台抽成20%</p>
      <p>定价范围：每小时6-10元</p>
    </div>
    <billing-detail />
    <order-table />
  </div>
</template>

<script>
import BillingDetail from '@/components/BillingDetail.vue';
import OrderTable from '@/components/OrderTable.vue';

export default {
  name: 'BillingManagement',
  components: {
    BillingDetail,
    OrderTable
  },
  data() {
    return {
      // 这里可以添加与计费管理相关的状态
    };
  },
  methods: {
    // 这里可以添加与计费管理相关的方法
  },
  mounted() {
    // 这里可以添加组件挂载后的逻辑
  }
};
</script>

<style scoped>
.billing-management {
  padding: 20px;
}

.billing-summary {
  margin-bottom: 20px;
}
</style>