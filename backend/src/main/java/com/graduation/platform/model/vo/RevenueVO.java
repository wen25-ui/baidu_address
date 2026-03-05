package com.graduation.platform.model.vo;

import java.math.BigDecimal;

public class RevenueVO {
    private BigDecimal totalRevenue;
    private BigDecimal platformShare;
    private BigDecimal driverShare;

    public RevenueVO(BigDecimal totalRevenue, BigDecimal platformShare, BigDecimal driverShare) {
        this.totalRevenue = totalRevenue;
        this.platformShare = platformShare;
        this.driverShare = driverShare;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public BigDecimal getPlatformShare() {
        return platformShare;
    }

    public void setPlatformShare(BigDecimal platformShare) {
        this.platformShare = platformShare;
    }

    public BigDecimal getDriverShare() {
        return driverShare;
    }

    public void setDriverShare(BigDecimal driverShare) {
        this.driverShare = driverShare;
    }
}