package com.gdbargain.common.to;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author: shh
 * @createTime: 2023/7/2609:29
 */
@Data
public class SkuReductionTo {
    //前3个是打折信息
    private Long skuId;
    private int fullCount;
    private BigDecimal discount;
    private int countStatus;
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private int priceStatus;
    private List<MemberPrice> memberPrice;
}
