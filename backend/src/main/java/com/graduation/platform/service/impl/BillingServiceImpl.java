package com.graduation.platform.service.impl;

import com.graduation.platform.model.dto.BillingDTO;
import com.graduation.platform.model.entity.BillingRecord;
import com.graduation.platform.repository.BillingRepository;
import com.graduation.platform.service.BillingService;
import com.graduation.platform.utils.BillingUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BillingServiceImpl implements BillingService {

    @Autowired
    private BillingRepository billingRepository;

    @Override
    public BillingRecord createBillingRecord(BillingDTO billingDTO) {
        BillingRecord billingRecord = BillingUtil.convertToBillingRecord(billingDTO);
        return billingRepository.save(billingRecord);
    }

    @Override
    public List<BillingRecord> getAllBillingRecords() {
        return billingRepository.findAll();
    }

    @Override
    public BillingRecord getBillingRecordById(Long id) {
        return billingRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteBillingRecord(Long id) {
        billingRepository.deleteById(id);
    }

    @Override
    public BillingRecord updateBillingRecord(Long id, BillingDTO billingDTO) {
        BillingRecord existingRecord = getBillingRecordById(id);
        if (existingRecord != null) {
            BillingRecord updatedRecord = BillingUtil.updateBillingRecord(existingRecord, billingDTO);
            return billingRepository.save(updatedRecord);
        }
        return null;
    }
}