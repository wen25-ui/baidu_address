package com.graduation.platform.controller;

import com.graduation.platform.common.Result;
import com.graduation.platform.model.dto.BillingDTO;
import com.graduation.platform.model.entity.BillingRecord;
import com.graduation.platform.service.BillingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/billing")
public class BillingController {

    @Autowired
    private BillingService billingService;

    @PostMapping("/create")
    public Result<BillingRecord> createBilling(@RequestBody BillingDTO billingDTO) {
        BillingRecord record = billingService.createBillingRecord(billingDTO);
        return Result.success(record);
    }

    @GetMapping("/list")
    public Result<List<BillingRecord>> listBillings() {
        List<BillingRecord> billings = billingService.getAllBillingRecords();
        return Result.success(billings);
    }

    @GetMapping("/{id}")
    public Result<BillingRecord> getBillingById(@PathVariable Long id) {
        BillingRecord record = billingService.getBillingRecordById(id);
        return Result.success(record);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteBilling(@PathVariable Long id) {
        billingService.deleteBillingRecord(id);
        return Result.success(null);
    }
}