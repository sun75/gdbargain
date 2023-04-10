package com.gdbargain.product.vo;

import lombok.Data;

/**
 * @author: shh
 * @createTime: 2023/2/2100:41
 */
@Data
public class AttrRespVO extends AttrVO{
    /**
     * 分类名称
     */
    private String catelogName;

    /**
     * 分组 名称
     */
    private String groupName;

    /**
     * 分类路径
     */
    private Long[] catelogPath;
}
