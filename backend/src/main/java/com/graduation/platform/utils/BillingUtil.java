package com.graduation.platform.utils;

public class BillingUtil {

    private static final double BASE_RATE = 6.0;
    private static final double MAX_RATE = 10.0;
    private static final double PLATFORM_COMMISSION_RATE = 0.2;

    public static double calculateBillingAmount(double hours) {
        double rate = BASE_RATE + (Math.random() * (MAX_RATE - BASE_RATE));
        double totalAmount = rate * hours;
        double platformCommission = totalAmount * PLATFORM_COMMISSION_RATE;
        return totalAmount - platformCommission;
    }
}