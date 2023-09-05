package com.gdbargain.ware.service.impl;

import com.gdbargain.ware.vo.SkuHasStockVo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gdbargain.common.utils.PageUtils;
import com.gdbargain.common.utils.Query;

import com.gdbargain.ware.dao.WareInfoDao;
import com.gdbargain.ware.entity.WareInfoEntity;
import com.gdbargain.ware.service.WareInfoService;
import org.springframework.util.StringUtils;


@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareInfoEntity> wrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if(!StringUtils.isEmpty(key)){
            wrapper.and((w)->{
                w.eq("id",key)
                        .or().like("name",key)
                        .or().like("address",key)
                        .or().like("areacode",key);
            });
        }
        IPage<WareInfoEntity> page = this.page(
                new Query<WareInfoEntity>().getPage(params),wrapper);

        return new PageUtils(page);
    }

    @Override
    public List<SkuHasStockVo> getSkuHasStock(List<Long> skuIds) {
        List<SkuHasStockVo> collect = skuIds.stream().map(skuId-> {
            SkuHasStockVo vo = new SkuHasStockVo();
            //查询SKU的总库存量 select sum(stock-stock_locked) from `wms_ware_sku` where sku_id=1
            //类型由long改成Long类型，否则在查询的时候，返回null，无法放进去
            Long count = baseMapper.getSKuStock(skuId);
            vo.setSkuId(skuId);
            //会抛出NullPointer异常
            //vo.setHasStock(count>0);
            vo.setHasStock(count == null ? false : count > 0);
            return vo;
        }).collect(Collectors.toList());
        return collect;
    }

}