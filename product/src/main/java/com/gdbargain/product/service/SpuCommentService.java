package com.gdbargain.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gdbargain.common.utils.PageUtils;
import com.gdbargain.product.entity.SpuCommentEntity;

import java.util.Map;

/**
 * 商品评价
 *
 * @author holasunhui
 * @email holasunhui@sina.com
 * @date 2022-12-09 16:23:20
 */
public interface SpuCommentService extends IService<SpuCommentEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

