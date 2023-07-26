package com.gdbargain.coupon.service.impl;

import com.gdbargain.common.to.MemberPrice;
import com.gdbargain.common.to.SkuReductionTo;
import com.gdbargain.coupon.entity.MemberPriceEntity;
import com.gdbargain.coupon.entity.SkuLadderEntity;
import com.gdbargain.coupon.service.MemberPriceService;
import com.gdbargain.coupon.service.SkuLadderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gdbargain.common.utils.PageUtils;
import com.gdbargain.common.utils.Query;

import com.gdbargain.coupon.dao.SkuFullReductionDao;
import com.gdbargain.coupon.entity.SkuFullReductionEntity;
import com.gdbargain.coupon.service.SkuFullReductionService;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {

    @Autowired
    SkuLadderService skuLadderService;

    @Autowired
    MemberPriceService memberPriceService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SkuFullReductionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuReductionTo(SkuReductionTo skuReductionTo) {
        //SKU的优惠/满减信息：sms_sku_ladder:这一步要操作远程服务
        //1.sms-->sms_sku_ladder,sms_sku_full_reduction,sms_member_price
        //sms_sku_ladder,因为属性没有一一对应起来，所以不可以直接用BeanUtils.copyProperties()
        SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
        skuLadderEntity.setSkuId(skuReductionTo.getSkuId());
        skuLadderEntity.setDiscount(skuReductionTo.getDiscount());
        skuLadderEntity.setAddOther(skuReductionTo.getCountStatus());
        //如果打折，那就进行保存
        if(skuReductionTo.getFullCount() > 0){
            skuLadderService.save(skuLadderEntity);
        }

        //2.sms_sku_full_reduction
        SkuFullReductionEntity skuFullReductionEntity = new SkuFullReductionEntity();
        BeanUtils.copyProperties(skuReductionTo, skuFullReductionEntity);
        //如果满减，那就进行保存
        if(skuReductionTo.getFullPrice().compareTo(new BigDecimal("0")) ==1){
            this.save(skuFullReductionEntity);
        }

        //3.sms_member_price
        List<MemberPrice> memberPrices = skuReductionTo.getMemberPrice();
        List<MemberPriceEntity> collect = memberPrices.stream().map((memberPrice) -> {
            MemberPriceEntity memberPriceEntity = new MemberPriceEntity();
            memberPriceEntity.setSkuId(skuReductionTo.getSkuId());
            memberPriceEntity.setMemberLevelId(memberPrice.getId());
            memberPriceEntity.setMemberLevelName(memberPrice.getName());
            memberPriceEntity.setMemberPrice(memberPrice.getPrice());
            memberPriceEntity.setAddOther(1);  //数字 1 表示默认叠加其他优惠
            return memberPriceEntity;
        }).filter((memberPrice)->{
            return memberPrice.getMemberPrice().compareTo(new BigDecimal("0")) == 1;
        }).collect(Collectors.toList());
        memberPriceService.saveBatch(collect);
    }

}