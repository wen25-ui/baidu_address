package com.graduation.platform.controller;

import com.graduation.platform.common.Result;
import com.graduation.platform.model.dto.BillingDTO;
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
    public Result createBilling(@RequestBody BillingDTO billingDTO) {
        billingService.createBilling(billingDTO);
        return Result.success("Billing record created successfully");
    }

    @GetMapping("/list")
    public Result<List<BillingDTO>> listBillings() {
        List<BillingDTO> billings = billingService.listBillings();
        return Result.success(billings);
    }

    @GetMapping("/revenue")
    public Result<Double> getPlatformRevenue() {
        Double revenue = billingService.calculatePlatformRevenue();
        return Result.success(revenue);
    }
}