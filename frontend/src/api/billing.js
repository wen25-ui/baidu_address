import request from '../utils/request';

const BASE_URL = '/api/billing';

export const getBillingRecords = (params) => {
    return request({
        url: `${BASE_URL}/records`,
        method: 'get',
        params
    });
};

export const createBillingRecord = (data) => {
    return request({
        url: `${BASE_URL}/create`,
        method: 'post',
        data
    });
};

export const updateBillingRecord = (id, data) => {
    return request({
        url: `${BASE_URL}/update/${id}`,
        method: 'put',
        data
    });
};

export const deleteBillingRecord = (id) => {
    return request({
        url: `${BASE_URL}/delete/${id}`,
        method: 'delete'
    });
};