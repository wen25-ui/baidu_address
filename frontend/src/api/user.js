import request from '../utils/request';

export const login = (data) => {
    return request({
        url: '/api/user/login',
        method: 'post',
        data
    });
};

export const register = (data) => {
    return request({
        url: '/api/user/register',
        method: 'post',
        data
    });
};

export const getUserInfo = (userId) => {
    return request({
        url: `/api/user/${userId}`,
        method: 'get'
    });
};

export const getUsers = () => {
    return request({
        url: '/api/user/list',
        method: 'get'
    });
};

export const updateUser = (userId, data) => {
    return request({
        url: `/api/user/${userId}`,
        method: 'put',
        data
    });
};

export const deleteUser = (userId) => {
    return request({
        url: `/api/user/${userId}`,
        method: 'delete'
    });
};