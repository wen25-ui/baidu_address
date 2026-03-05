<template>
  <div class="sidebar">
    <h2>管理平台</h2>
    <ul>
      <li><router-link to="/dashboard">仪表盘</router-link></li>
      <li><router-link to="/orders">订单管理</router-link></li>
      <li><router-link to="/navigation">导航</router-link></li>
      <li><router-link to="/billing">计费管理</router-link></li>
      <li><router-link to="/users">用户管理</router-link></li>
      <li><router-link to="/revenue">平台收入</router-link></li>
    </ul>
  </div>
</template>

<script>
export default {
  name: 'Sidebar'
}
</script>

<style scoped>
.sidebar {
  width: 200px;
  background-color: #f4f4f4;
  padding: 20px;
  box-shadow: 2px 0 5px rgba(0, 0, 0, 0.1);
}

.sidebar h2 {
  margin-bottom: 20px;
}

.sidebar ul {
  list-style-type: none;
  padding: 0;
}

.sidebar li {
  margin: 10px 0;
}

.sidebar a {
  text-decoration: none;
  color: #333;
}

.sidebar a:hover {
  color: #007bff;
}
</style>