import request from '../utils/request';

export const getRoutes = () => {
    return request.get('/api/navigation/routes');
};

export const startNavigation = (routeId) => {
    return request.post('/api/navigation/start', { routeId });
};

export const endNavigation = (routeId) => {
    return request.post('/api/navigation/end', { routeId });
};

export const getNavigationStatus = (routeId) => {
    return request.get(`/api/navigation/status/${routeId}`);
};