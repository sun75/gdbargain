package com.gdbargain.product.dao;

import com.gdbargain.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author holasunhui
 * @email holasunhui@sina.com
 * @date 2022-12-09 16:23:20
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
