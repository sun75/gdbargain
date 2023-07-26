package com.gdbargain.common.constant;

/**
 * @author: shh
 * @createTime: 2023/7/2621:47
 */
public enum PurchaseStatusEnum {
    CREATED(0, "新建"),
    ASSIGNED(1, "已分配"),
    RECEIVED(2, "已领取"),
    FINISHED(3, "已完成"),
    HAS_ERROR(4, "有异常");

    private int code;
    private String msg;

    PurchaseStatusEnum(int code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
