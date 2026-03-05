import axios from 'axios';

const request = axios.create({
    baseURL: process.env.VUE_APP_API_URL || 'http://localhost:8080/api',
    timeout: 10000,
});

// 请求拦截器
request.interceptors.request.use(
    config => {
        // 可以在这里添加请求头或其他配置
        return config;
    },
    error => {
        return Promise.reject(error);
    }
);

// 响应拦截器
request.interceptors.response.use(
    response => {
        return response.data;
    },
    error => {
        // 处理错误响应
        return Promise.reject(error);
    }
);

export default request;