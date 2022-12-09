package com.gdbargain.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gdbargain.common.utils.PageUtils;
import com.gdbargain.product.entity.SkuImagesEntity;

import java.util.Map;

/**
 * sku图片
 *
 * @author holasunhui
 * @email holasunhui@sina.com
 * @date 2022-12-09 16:23:20
 */
public interface SkuImagesService extends IService<SkuImagesEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

