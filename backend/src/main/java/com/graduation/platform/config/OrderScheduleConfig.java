package com.graduation.platform.config;

import com.graduation.platform.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * 订单定时任务配置
 * 处理超时未支付订单和自动完成订单
 */
@Slf4j
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class OrderScheduleConfig {

    private final ReservationService reservationService;

    /**
     * 处理超时未支付订单
     * 每分钟执行一次
     */
    @Scheduled(fixedRate = 60000)
    public void handleTimeoutOrders() {
        try {
            int count = reservationService.handleTimeoutOrders();
            if (count > 0) {
                log.info("定时任务：处理超时订单 {} 个", count);
            }
        } catch (Exception e) {
            log.error("定时任务：处理超时订单异常", e);
        }
    }

    /**
     * 自动完成订单
     * 每5分钟执行一次
     */
    @Scheduled(fixedRate = 300000)
    public void autoCompleteOrders() {
        try {
            int count = reservationService.autoCompleteOrders();
            if (count > 0) {
                log.info("定时任务：自动完成订单 {} 个", count);
            }
        } catch (Exception e) {
            log.error("定时任务：自动完成订单异常", e);
        }
    }
}
