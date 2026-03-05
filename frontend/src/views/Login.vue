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
import { login } from '@/api/user';

export default {
  name: 'Login',
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
        const response = await login({
          username: this.username,
          password: this.password
        });
        if (response && response.success !== false) {
          // 存储 token
          if (response.token) {
            localStorage.setItem('token', response.token);
          }
          this.$store.commit('SET_USER', response.user || response.data);
          this.$router.push('/dashboard');
        } else {
          this.errorMessage = response.message || '登录失败';
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