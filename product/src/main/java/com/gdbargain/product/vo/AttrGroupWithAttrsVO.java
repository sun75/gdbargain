package com.gdbargain.product.vo;

import com.gdbargain.product.entity.AttrEntity;
import lombok.Data;
import java.util.List;

/**
 * @author: shh
 * @createTime: 2023/4/510:25
 */
@Data
public class AttrGroupWithAttrsVO {
    /**
     * 分组id
     */
    private Long attrGroupId;
    /**
     * 组名
     */
    private String attrGroupName;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 描述
     */
    private String descript;
    /**
     * 组图标
     */
    private String icon;
    /**
     * 所属分类id
     */
    private Long catelogId;
    /**
     * 将attr的所有信息封装起来，所以这边用的是List
     */
    private List<AttrEntity> attrs;
}
