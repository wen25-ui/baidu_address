<template>
  <div id="app">
    <template v-if="showLayout">
      <Header />
      <div class="app-body">
        <Sidebar />
        <div class="main-content">
          <router-view />
        </div>
      </div>
    </template>
    <router-view v-else />
  </div>
</template>

<script>
import Header from './components/Header.vue';
import Sidebar from './components/Sidebar.vue';

export default {
  name: 'App',
  components: {
    Header,
    Sidebar
  },
  computed: {
    showLayout() {
      return this.$route.path !== '/';
    }
  }
}
</script>

<style>
@import './assets/styles/global.css';

#app {
  min-height: 100vh;
}

.app-body {
  display: flex;
}

.main-content {
  flex: 1;
  padding: 20px;
  margin-left: 0;
}
</style>