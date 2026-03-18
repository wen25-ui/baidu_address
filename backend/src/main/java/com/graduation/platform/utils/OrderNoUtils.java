package com.graduation.platform.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * 订单号生成工具
 */
public class OrderNoUtils {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final Random RANDOM = new Random();

    /**
     * 生成订单号
     * 格式：PS + 时间戳 + 4位随机数
     */
    public static String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String random = String.format("%04d", RANDOM.nextInt(10000));
        return "PS" + timestamp + random;
    }

    /**
     * 生成核销验证码
     * 6位数字
     */
    public static String generateVerifyCode() {
        return String.format("%06d", RANDOM.nextInt(1000000));
    }
}
