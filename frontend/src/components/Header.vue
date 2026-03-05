<template>
  <header class="header">
    <h1 class="header-title">导航管理平台</h1>
    <nav class="header-nav">
      <router-link to="/dashboard">仪表盘</router-link>
      <router-link to="/order-management">订单管理</router-link>
      <router-link to="/navigation-map">导航</router-link>
      <router-link to="/billing-management">计费管理</router-link>
      <router-link to="/user-management">用户管理</router-link>
      <router-link to="/platform-revenue">平台收入</router-link>
    </nav>
  </header>
</template>

<script>
export default {
  name: 'Header'
}
</script>

<style scoped>
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 20px;
  background-color: #f8f9fa;
  border-bottom: 1px solid #dee2e6;
}

.header-title {
  font-size: 24px;
  font-weight: bold;
}

.header-nav {
  display: flex;
  gap: 15px;
}

.header-nav a {
  text-decoration: none;
  color: #007bff;
}

.header-nav a:hover {
  text-decoration: underline;
}
</style>