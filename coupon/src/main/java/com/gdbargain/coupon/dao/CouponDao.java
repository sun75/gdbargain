package com.gdbargain.coupon.dao;

import com.gdbargain.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author holasunhui
 * @email holasunhui@sina.com
 * @date 2022-12-11 10:53:58
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
