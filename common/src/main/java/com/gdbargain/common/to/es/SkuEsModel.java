package com.gdbargain.common.to.es;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author: shh
 * @createTime: 2023/8/2715:38
 */
@Data
public class SkuEsModel { //common中
    private Long skuId;
    private Long spuId;
    private String skuTitle;
    private BigDecimal skuPrice;
    private String skuImg;
    private Long saleCount;
    private boolean hasStock;
    private Long hotScore;
    private Long brandId;
    private Long catalogId;
    private String brandName;
    private String brandImg;
    private String catalogName;
    private List<Attr> attrs;

    /**
     * 为了第三方工具可以对其进行序列化，反序列化，设置为public，可访问权限
     */
    @Data
    public static class Attr{
        private Long attrId;
        private String attrName;
        private String attrValue;
    }
}

