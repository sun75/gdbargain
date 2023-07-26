package com.gdbargain.common.constant;

/**
 * @author: shh
 * @createTime: 2023/7/2621:47
 */
public enum PurchaseDetailStatusEnum {
    CREATED(0, "新建"),
    ASSIGNED(1, "已分配"),
    BUYING(2, "正在采购"),
    FINISHED(3, "已完成"),
    HAS_ERROR(4, "采购失败");

    private int code;
    private String msg;

    PurchaseDetailStatusEnum(int code, String msg){
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
