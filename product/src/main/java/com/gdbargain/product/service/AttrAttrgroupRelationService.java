package com.gdbargain.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gdbargain.product.entity.AttrAttrgroupRelationEntity;
import com.gdbargain.common.utils.PageUtils;

import java.util.Map;

/**
 * 属性&属性分组关联
 *
 * @author holasunhui
 * @email holasunhui@sina.com
 * @date 2022-12-09 16:23:20
 */
public interface AttrAttrgroupRelationService extends IService<AttrAttrgroupRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

