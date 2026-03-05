<template>
  <div id="baidu-map" style="width: 100%; height: 100%;">
    <div ref="map" style="width: 100%; height: 100%;"></div>
  </div>
</template>

<script>
export default {
  name: 'BaiduMap',
  data() {
    return {
      map: null,
      markers: []
    };
  },
  mounted() {
    this.initMap();
  },
  methods: {
    initMap() {
      const map = new BMap.Map(this.$refs.map);
      const point = new BMap.Point(116.404, 39.915); // 默认中心点
      map.centerAndZoom(point, 15);
      map.enableScrollWheelZoom(true);
      this.map = map;
    },
    addMarker(lng, lat) {
      const point = new BMap.Point(lng, lat);
      const marker = new BMap.Marker(point);
      this.map.addOverlay(marker);
      this.markers.push(marker);
    },
    clearMarkers() {
      this.markers.forEach(marker => {
        this.map.removeOverlay(marker);
      });
      this.markers = [];
    }
  }
};
</script>

<style scoped>
#baidu-map {
  position: relative;
}
</style>