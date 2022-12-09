package com.gdbargain.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gdbargain.product.entity.CategoryBrandRelationEntity;
import com.gdbargain.common.utils.PageUtils;

import java.util.Map;

/**
 * 品牌分类关联
 *
 * @author holasunhui
 * @email holasunhui@sina.com
 * @date 2022-12-09 16:23:20
 */
public interface CategoryBrandRelationService extends IService<CategoryBrandRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

