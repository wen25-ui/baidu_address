<template>
  <div class="real-time-navigation">
    <BaiduMap @location-updated="updateLocation" />
    <div class="navigation-info">
      <h2>实时导航</h2>
      <p>当前位置信息: {{ currentLocation }}</p>
      <p>预计到达时间: {{ estimatedArrivalTime }} 分钟</p>
      <button @click="startNavigation">开始导航</button>
      <button @click="stopNavigation">停止导航</button>
    </div>
  </div>
</template>

<script>
import BaiduMap from './BaiduMap.vue';

export default {
  components: {
    BaiduMap,
  },
  data() {
    return {
      currentLocation: '',
      estimatedArrivalTime: 0,
      navigationActive: false,
    };
  },
  methods: {
    updateLocation(location) {
      this.currentLocation = location;
      // 计算预计到达时间的逻辑
      this.estimatedArrivalTime = this.calculateEstimatedTime(location);
    },
    startNavigation() {
      this.navigationActive = true;
      // 启动导航的逻辑
    },
    stopNavigation() {
      this.navigationActive = false;
      // 停止导航的逻辑
    },
    calculateEstimatedTime(location) {
      // 根据当前位置计算预计到达时间的逻辑
      return Math.floor(Math.random() * 30) + 1; // 示例: 随机生成1到30分钟
    },
  },
};
</script>

<style scoped>
.real-time-navigation {
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.navigation-info {
  margin-top: 20px;
}

button {
  margin-right: 10px;
}
</style>