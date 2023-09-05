package com.gdbargain.ware.dao;

import com.gdbargain.ware.entity.WareInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 仓库信息
 * 
 * @author holasunhui
 * @email holasunhui@sina.com
 * @date 2022-12-11 11:47:17
 */
@Mapper
public interface WareInfoDao extends BaseMapper<WareInfoEntity> {

    Long getSKuStock(Long skuId);
}
