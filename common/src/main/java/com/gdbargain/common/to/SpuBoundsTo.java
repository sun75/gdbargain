package com.gdbargain.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: shh
 * @createTime: 2023/7/2511:38
 * 用来微服务之间远程调用，传输数据的：A->B，对象转为json，json再转为对象
 */
@Data
public class SpuBoundsTo {
    private Long spuId;
    private BigDecimal buyBounds;
    private BigDecimal growBounds;
}
