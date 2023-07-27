package com.gdbargain.ware.vo;

import lombok.Data;

/**
 * @author: shh
 * @createTime: 2023/7/2711:16
 */
@Data
public class PurchaseItemDoneVo {
    private Long itemId;

    private Integer status;

    private String reason;
}
