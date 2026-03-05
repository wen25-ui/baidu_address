package com.graduation.platform.service;

import com.graduation.platform.model.dto.BillingDTO;
import com.graduation.platform.model.vo.RevenueVO;

public interface BillingService {
    RevenueVO calculateRevenue(BillingDTO billingDTO);
    void recordBilling(BillingDTO billingDTO);
    double getPlatformCommission(double amount);
}