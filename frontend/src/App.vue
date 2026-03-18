<script>
export default {
  onLaunch() {
    console.log('App Launch')
  },
  onShow() {
    console.log('App Show')
  },
  onHide() {
    console.log('App Hide')
  }
}
</script>

<style>
page {
  background-color: #f4f4f4;
  font-family: Arial, sans-serif;
  font-size: 28rpx;
  color: #333;
}
.container { padding: 20rpx; }
.btn-primary {
  background-color: #007bff;
  color: #fff;
  border: none;
  border-radius: 10rpx;
  padding: 20rpx 30rpx;
  font-size: 30rpx;
}
.btn-primary:active { background-color: #0056b3; }
.btn-danger {
  background-color: #dc3545;
  color: #fff;
  border: none;
  border-radius: 10rpx;
  padding: 20rpx 30rpx;
  font-size: 30rpx;
}
.card {
  background-color: #fff;
  border-radius: 16rpx;
  padding: 30rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.08);
}
.table-header {
  display: flex;
  background-color: #35424a;
  color: #fff;
  padding: 16rpx;
  border-radius: 8rpx 8rpx 0 0;
  font-size: 24rpx;
}
.table-row {
  display: flex;
  padding: 16rpx;
  border-bottom: 1rpx solid #eee;
  font-size: 24rpx;
}
.table-row:nth-child(even) { background-color: #f9f9f9; }
.table-cell {
  flex: 1;
  text-align: center;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.empty-state {
  text-align: center;
  padding: 60rpx 0;
  color: #999;
}
</style>
