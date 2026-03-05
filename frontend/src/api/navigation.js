import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api/navigation';

export const getRoutes = () => {
    return axios.get(`${API_BASE_URL}/routes`);
};

export const startNavigation = (routeId) => {
    return axios.post(`${API_BASE_URL}/start`, { routeId });
};

export const endNavigation = (routeId) => {
    return axios.post(`${API_BASE_URL}/end`, { routeId });
};

export const getNavigationStatus = (routeId) => {
    return axios.get(`${API_BASE_URL}/status/${routeId}`);
};