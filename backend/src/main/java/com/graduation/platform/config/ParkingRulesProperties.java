package com.graduation.platform.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 停车业务规则配置
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "parking")
public class ParkingRulesProperties {
    
    /**
     * 预约规则
     */
    private Rules rules = new Rules();
    
    /**
     * 信用规则
     */
    private Credit credit = new Credit();
    
    @Data
    public static class Rules {
        /**
         * 最短预约时长（小时）
         */
        private Double minDuration = 0.5;
        
        /**
         * 最长预约时长（小时）
         */
        private Double maxDuration = 12.0;
        
        /**
         * 最大提前预约天数
         */
        private Integer advanceDays = 3;
        
        /**
         * 免费取消时限（小时）
         */
        private Integer freeCancelHours = 1;
    }
    
    @Data
    public static class Credit {
        /**
         * 初始信用分
         */
        private Integer initialScore = 100;
        
        /**
         * 禁止预约阈值
         */
        private Integer minScore = 60;
        
        /**
         * 信用分上限
         */
        private Integer maxScore = 100;
        
        /**
         * 超时未核销扣分
         */
        private Integer timeoutDeduct = 5;
        
        /**
         * 按时完成加分
         */
        private Integer completeAdd = 2;
        
        /**
         * 临时取消扣分
         */
        private Integer lateCancelDeduct = 3;
    }
}
