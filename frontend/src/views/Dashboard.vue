<template>
  <div class="dashboard">
    <Header />
    <Sidebar />
    <div class="dashboard-content">
      <h1>仪表盘</h1>
      <div class="overview">
        <h2>平台概览</h2>
        <p>实时数据和统计信息展示</p>
        <!-- 这里可以添加更多的统计信息组件 -->
      </div>
      <div class="navigation">
        <h2>实时导航</h2>
        <RealTimeNavigation />
      </div>
      <div class="billing">
        <h2>计费信息</h2>
        <BillingDetail />
      </div>
      <div class="orders">
        <h2>订单管理</h2>
        <OrderTable />
      </div>
    </div>
  </div>
</template>

<script>
import Header from '@/components/Header.vue';
import Sidebar from '@/components/Sidebar.vue';
import RealTimeNavigation from '@/components/RealTimeNavigation.vue';
import BillingDetail from '@/components/BillingDetail.vue';
import OrderTable from '@/components/OrderTable.vue';

export default {
  name: 'Dashboard',
  components: {
    Header,
    Sidebar,
    RealTimeNavigation,
    BillingDetail,
    OrderTable,
  },
};
</script>

<style scoped>
.dashboard {
  display: flex;
}

.dashboard-content {
  flex: 1;
  padding: 20px;
}

.overview, .navigation, .billing, .orders {
  margin-bottom: 20px;
}
</style>