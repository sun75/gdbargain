package com.gdbargain.product.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @author: shh
 * @createTime: 2023/2/2022:34
 * 复制的AttrEntity实体类中的对象
 * 不需要标注数据库有关的注解
 */
@Data //让Lombok自动生成get set方法
public class AttrVO {
//    private static final long serialVersionUID = 1L;

    /**
     * 属性id
     */
//    @TableId
    private Long attrId;
    /**
     * 属性名
     */
    private String attrName;
    /**
     * 是否需要检索[0-不需要，1-需要]
     */
    private Integer searchType;
    /**
     * 属性图标
     */
    private String icon;
    /**
     * 可选值列表[用逗号分隔]
     */
    private String valueSelect;
    /**
     * 属性类型[0-销售属性，1-基本属性，2-既是销售属性又是基本属性]
     */
    private Integer attrType;
    /**
     * 启用状态[0 - 禁用，1 - 启用]
     */
    private Long enable;
    /**
     * 所属分类
     */
    private Long catelogId;
    /**
     * 快速展示【是否展示在介绍上；0-否 1-是】，在sku中仍然可以调整
     */
    private Integer showDesc;

    /**
     * 添加分组ID
     */
    private Long attrGroupId;
}
