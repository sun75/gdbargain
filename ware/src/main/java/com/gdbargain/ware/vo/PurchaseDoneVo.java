package com.gdbargain.ware.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author: shh
 * @createTime: 2023/7/2711:15
 */
@Data
public class PurchaseDoneVo {
    //采购单id
    @NotNull
    private Long id;

    //采购项集合
    private List<PurchaseItemDoneVo> items;
}
