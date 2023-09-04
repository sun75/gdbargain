package com.gdbargain.common.constant;

/**
 * @author: shh
 * @createTime: 2023/9/411:43
 */
public enum SpuStatusEnum {
    SPU_NEW(0, "新建"),
    SPU_UP(1, "上架"),
    SPU_DOWN(2, "下架");

    private int code;
    private String msg;

    SpuStatusEnum(int code, String msg){
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
