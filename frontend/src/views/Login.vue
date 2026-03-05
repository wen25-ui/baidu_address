<template>
  <div class="login">
    <h1>登录</h1>
    <form @submit.prevent="handleLogin">
      <div>
        <label for="username">用户名:</label>
        <input type="text" v-model="username" id="username" required />
      </div>
      <div>
        <label for="password">密码:</label>
        <input type="password" v-model="password" id="password" required />
      </div>
      <button type="submit">登录</button>
    </form>
    <div v-if="errorMessage" class="error">{{ errorMessage }}</div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      username: '',
      password: '',
      errorMessage: ''
    };
  },
  methods: {
    async handleLogin() {
      try {
        // 调用登录API
        const response = await this.$http.post('/api/login', {
          username: this.username,
          password: this.password
        });
        // 登录成功，处理响应
        if (response.data.success) {
          // 存储用户信息或token
          this.$store.commit('setUser', response.data.user);
          this.$router.push('/dashboard'); // 跳转到仪表盘
        } else {
          this.errorMessage = response.data.message;
        }
      } catch (error) {
        this.errorMessage = '登录失败，请重试。';
      }
    }
  }
};
</script>

<style scoped>
.login {
  max-width: 400px;
  margin: auto;
  padding: 20px;
  border: 1px solid #ccc;
  border-radius: 5px;
}

.error {
  color: red;
}
</style>