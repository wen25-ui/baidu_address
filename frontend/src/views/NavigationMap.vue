<template>
  <div class="navigation-map">
    <BaiduMap :center="mapCenter" :zoom="mapZoom" @mapReady="onMapReady" />
    <RealTimeNavigation :route="currentRoute" />
  </div>
</template>

<script>
import BaiduMap from '@/components/BaiduMap.vue';
import RealTimeNavigation from '@/components/RealTimeNavigation.vue';

export default {
  components: {
    BaiduMap,
    RealTimeNavigation,
  },
  data() {
    return {
      mapCenter: { lat: 39.9042, lng: 116.4074 }, // Default center (Beijing)
      mapZoom: 12,
      currentRoute: null,
    };
  },
  methods: {
    onMapReady() {
      // Initialize navigation or other map-related functionalities here
    },
  },
};
</script>

<style scoped>
.navigation-map {
  height: 100vh;
  width: 100%;
}
</style>