package com.gdbargain.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * @author: shh
 * @createTime: 2023/7/2621:20
 */
@Data
public class MergeVo {
    private Long purchaseId;  //整单ID
    private List<Long> items;  //合并项集合[1,2,3,4 ]
}
