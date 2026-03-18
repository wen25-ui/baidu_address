import { getToken } from './auth';

const BASE_URL = 'http://localhost:8080/api';
const TIMEOUT = 10000;
const AUTH_ENDPOINTS = ['/v1/user/login', '/v1/user/register', '/v1/user/wx-login', '/admin/login'];

const request = (options) => {
  return new Promise((resolve, reject) => {
    const token = getToken();
    const requestPath = options.url || '';
    const isAuthEndpoint = AUTH_ENDPOINTS.includes(requestPath);
    const header = {
      'Content-Type': 'application/json',
      ...(options.header || {})
    };
    if (token) {
      header['Authorization'] = `Bearer ${token}`;
    }

    uni.request({
      url: BASE_URL + options.url,
      method: options.method || 'GET',
      data: options.data || {},
      header,
      timeout: TIMEOUT,
      success: (res) => {
        if (res.statusCode === 200) {
          resolve(res.data);
        } else if (res.statusCode === 401) {
          const message = (res.data && res.data.message) || '未授权，请重新登录';
          if (!isAuthEndpoint) {
            // token 过期或未授权，跳转登录
            uni.removeStorageSync('token');
            uni.removeStorageSync('userInfo');
            uni.reLaunch({ url: '/pages/login/login' });
          }
          reject(new Error(message));
        } else {
          uni.showToast({
            title: res.data.message || '请求失败',
            icon: 'none'
          });
          reject(new Error(res.data.message || '请求失败'));
        }
      },
      fail: (err) => {
        uni.showToast({
          title: '网络异常，请稍后重试',
          icon: 'none'
        });
        reject(err);
      }
    });
  });
};

// 便捷方法
request.get = (url, data, header) => request({ url, method: 'GET', data, header });
request.post = (url, data, header) => request({ url, method: 'POST', data, header });
request.put = (url, data, header) => request({ url, method: 'PUT', data, header });
request.delete = (url, data, header) => request({ url, method: 'DELETE', data, header });

export default request;

