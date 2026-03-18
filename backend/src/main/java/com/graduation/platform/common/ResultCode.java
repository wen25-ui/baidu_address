package com.graduation.platform.common;

/**
 * 响应状态码枚举
 */
public enum ResultCode {
    
    SUCCESS(200, "操作成功"),
    ERROR(500, "操作失败"),
    VALIDATE_FAILED(400, "参数验证失败"),
    UNAUTHORIZED(401, "未登录或token已过期"),
    FORBIDDEN(403, "没有相关权限"),
    NOT_FOUND(404, "资源不存在"),
    
    // 用户相关
    USER_NOT_EXIST(1001, "用户不存在"),
    USER_DISABLED(1002, "用户已被禁用"),
    CREDIT_SCORE_LOW(1003, "信用分过低，暂时无法预约"),
    USER_NOT_VERIFIED(1004, "请先完成实名认证"),
    
    // 车位相关
    SPACE_NOT_EXIST(2001, "车位不存在"),
    SPACE_NOT_AVAILABLE(2002, "车位当前不可用"),
    SPACE_AUDIT_PENDING(2003, "车位正在审核中"),
    SPACE_AUDIT_REJECTED(2004, "车位审核未通过"),
    
    // 预约相关
    RESERVATION_TIME_CONFLICT(3001, "该时段已被预约"),
    RESERVATION_TIME_INVALID(3002, "预约时间不在可用时段内"),
    RESERVATION_DURATION_INVALID(3003, "预约时长不符合规则"),
    RESERVATION_NOT_EXIST(3004, "预约订单不存在"),
    RESERVATION_CANNOT_CANCEL(3005, "当前订单无法取消"),
    VERIFY_CODE_ERROR(3006, "核销码错误"),
    
    // 支付相关
    PAYMENT_FAILED(4001, "支付失败"),
    BALANCE_NOT_ENOUGH(4002, "余额不足");

    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
