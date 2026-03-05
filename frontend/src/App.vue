<template>
  <div id="app">
    <Header />
    <Sidebar />
    <router-view />
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
  }
}
</script>

<style>
@import './assets/styles/global.css';
</style>