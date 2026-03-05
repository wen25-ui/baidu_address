<template>
  <div class="user-management">
    <h1>用户管理</h1>
    <div class="user-list">
      <table>
        <thead>
          <tr>
            <th>用户ID</th>
            <th>用户名</th>
            <th>邮箱</th>
            <th>注册时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in users" :key="user.id">
            <td>{{ user.id }}</td>
            <td>{{ user.username }}</td>
            <td>{{ user.email }}</td>
            <td>{{ user.createdAt }}</td>
            <td>
              <button @click="editUser(user.id)">编辑</button>
              <button @click="deleteUser(user.id)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <button @click="addUser">添加用户</button>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import { getUsers, deleteUser as deleteUserApi } from '@/api/user';

export default {
  name: 'UserManagement',
  setup() {
    const users = ref([]);

    const fetchUsers = async () => {
      const response = await getUsers();
      users.value = response.data;
    };

    const deleteUser = async (userId) => {
      await deleteUserApi(userId);
      fetchUsers();
    };

    const editUser = (userId) => {
      // 编辑用户逻辑
    };

    const addUser = () => {
      // 添加用户逻辑
    };

    onMounted(fetchUsers);

    return {
      users,
      deleteUser,
      editUser,
      addUser,
    };
  },
};
</script>

<style scoped>
.user-management {
  padding: 20px;
}

.user-list {
  margin-bottom: 20px;
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