package com.gdbargain.ware.service.impl;

import com.gdbargain.common.utils.R;
import com.gdbargain.ware.feign.ProductFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gdbargain.common.utils.PageUtils;
import com.gdbargain.common.utils.Query;

import com.gdbargain.ware.dao.WareSkuDao;
import com.gdbargain.ware.entity.WareSkuEntity;
import com.gdbargain.ware.service.WareSkuService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    ProductFeignService productFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        /**
         * skuId wareId
         */
        QueryWrapper<WareSkuEntity> wrapper = new QueryWrapper<>();
        String skuId = (String) params.get("skuId");
        String wareId = (String) params.get("wareId");
        if(!StringUtils.isEmpty(skuId)){
            wrapper.eq("sku_id", skuId);
        }
        if(!StringUtils.isEmpty(wareId)){
            wrapper.eq("ware_id", wareId);
        }
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),wrapper);

        return new PageUtils(page);
    }

    //this.baseMapper == WareSkuDao
    @Transactional
    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        //1.如果还没有这个库存记录，那就新增
        List<WareSkuEntity> list = this.baseMapper.selectList(new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId)
                .eq("ware_id", wareId));
        if(list == null || list.size() == 0){
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setSkuId(skuId);
            wareSkuEntity.setStock(skuNum);
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setStockLocked(0);

            //远程查询SKU名字，如果失败，整个事务无需回滚
            //1。自己catch异常
            try {
                //因为远程调用返回的是json，所以用R接收，进行转
                R info = productFeignService.info(skuId);
                //因为接收过来的是Object对象，将其转成Map格式
                Map<String, Object> data = (Map<String, Object>) info.get("skuInfo ");
                if(info.getCode() == 0){
                    wareSkuEntity.setSkuName((String) data.get("skuName"));
                }
            }catch(Exception e){

            }
            this.baseMapper.insert(wareSkuEntity);
        }else{
            this.baseMapper.addStock(skuId, wareId, skuNum);
        }
    }

}