package com.gdbargain.ware.service.impl;

import com.gdbargain.common.constant.PurchaseDetailStatusEnum;
import com.gdbargain.common.constant.PurchaseStatusEnum;
import com.gdbargain.ware.entity.PurchaseDetailEntity;
import com.gdbargain.ware.service.PurchaseDetailService;
import com.gdbargain.ware.vo.MergeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gdbargain.common.utils.PageUtils;
import com.gdbargain.common.utils.Query;

import com.gdbargain.ware.dao.PurchaseDao;
import com.gdbargain.ware.entity.PurchaseEntity;
import com.gdbargain.ware.service.PurchaseService;
import org.springframework.transaction.annotation.Transactional;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

    @Autowired
    PurchaseDetailService purchaseDetailService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageUnreceive(Map<String, Object> params) {
        //status为0/1，表示采购单是新建/刚分配
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>().eq("status", 0).eq("status", 1)
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void mergePurchase(MergeVo mergeVo) {
        Long purchaseId = mergeVo.getPurchaseId();
        if(purchaseId == null){
            //1.新建
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setCreateTime(new Date());
            purchaseEntity.setUpdateTime(new Date());
            purchaseEntity.setStatus(PurchaseStatusEnum.CREATED.getCode());

            this.save(purchaseEntity);
            //新建完成以后，赋值一个ID,唯空的时候purchaseId是新增出来的
            purchaseId = purchaseEntity.getId();
        }
        //2.不管是否唯空，都要进行合并
        List<Long> items = mergeVo.getItems();
        Long finalPurchaseId = purchaseId;
        List<PurchaseDetailEntity> collect = items.stream().map((i) -> {
            PurchaseDetailEntity entity = new PurchaseDetailEntity();
            entity.setId(i);
            //当前要合并到哪个采购单上
            entity.setPurchaseId(finalPurchaseId);
            //设置现在的最新状态码
            entity.setStatus(PurchaseDetailStatusEnum.ASSIGNED.getCode());
            return entity;
        }).collect(Collectors.toList());
        purchaseDetailService.updateBatchById(collect);

        PurchaseEntity entity = new PurchaseEntity();
        entity.setId(purchaseId);
        entity.setUpdateTime(new Date());
        this.updateById(entity);
    }

}