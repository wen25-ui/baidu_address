package com.graduation.platform.utils;

import com.graduation.platform.model.dto.BillingDTO;
import com.graduation.platform.model.entity.BillingRecord;

import java.util.concurrent.ThreadLocalRandom;

public class BillingUtil {

    private static final double BASE_RATE = 6.0;
    private static final double MAX_RATE = 10.0;
    private static final double PLATFORM_COMMISSION_RATE = 0.2;

    public static double calculateBillingAmount(double hours) {
        double rate = BASE_RATE + (ThreadLocalRandom.current().nextDouble() * (MAX_RATE - BASE_RATE));
        double totalAmount = rate * hours;
        double platformCommission = totalAmount * PLATFORM_COMMISSION_RATE;
        return totalAmount - platformCommission;
    }

    public static BillingRecord convertToBillingRecord(BillingDTO billingDTO) {
        BillingRecord record = new BillingRecord();
        record.setUserId(billingDTO.getUserId());
        record.setOrderId(billingDTO.getOrderId());
        record.setAmount(billingDTO.getAmount() != null ? billingDTO.getAmount().doubleValue() : 0.0);
        return record;
    }

    public static BillingRecord updateBillingRecord(BillingRecord record, BillingDTO billingDTO) {
        record.setUserId(billingDTO.getUserId());
        record.setOrderId(billingDTO.getOrderId());
        record.setAmount(billingDTO.getAmount() != null ? billingDTO.getAmount().doubleValue() : 0.0);
        return record;
    }
}