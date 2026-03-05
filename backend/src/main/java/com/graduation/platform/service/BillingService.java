package com.graduation.platform.service;

import com.graduation.platform.model.dto.BillingDTO;
import com.graduation.platform.model.entity.BillingRecord;

import java.util.List;

public interface BillingService {
    BillingRecord createBillingRecord(BillingDTO billingDTO);
    List<BillingRecord> getAllBillingRecords();
    BillingRecord getBillingRecordById(Long id);
    void deleteBillingRecord(Long id);
    BillingRecord updateBillingRecord(Long id, BillingDTO billingDTO);
}