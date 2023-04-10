package com.gdbargain.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gdbargain.product.entity.BrandEntity;
import com.gdbargain.product.entity.CategoryBrandRelationEntity;
import com.gdbargain.common.utils.PageUtils;

import java.util.List;
import java.util.Map;

/**
 * 品牌分类关联
 *
 * @author holasunhui
 * @email holasunhui@sina.com
 * @date 2022-12-09 16:23:20
 */
public interface CategoryBrandRelationService extends IService<CategoryBrandRelationEntity> {

    void updateBrand(Long brandId, String name);

    PageUtils queryPage(Map<String, Object> params);

    void saveDetail(CategoryBrandRelationEntity categoryBrandRelation);

    void updateCategory(Long catId, String name);

    List<BrandEntity> getBrandsByCatId(Long catId);
}

