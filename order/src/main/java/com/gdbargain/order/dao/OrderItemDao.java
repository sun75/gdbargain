package com.gdbargain.order.dao;

import com.gdbargain.order.entity.OrderItemEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单项信息
 * 
 * @author holasunhui
 * @email holasunhui@sina.com
 * @date 2022-12-11 11:33:58
 */
@Mapper
public interface OrderItemDao extends BaseMapper<OrderItemEntity> {
	
}
