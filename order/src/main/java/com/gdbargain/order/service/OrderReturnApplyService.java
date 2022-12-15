package com.gdbargain.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gdbargain.common.utils.PageUtils;
import com.gdbargain.order.entity.OrderReturnApplyEntity;

import java.util.Map;

/**
 * 订单退货申请
 *
 * @author holasunhui
 * @email holasunhui@sina.com
 * @date 2022-12-11 11:33:58
 */
public interface OrderReturnApplyService extends IService<OrderReturnApplyEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

