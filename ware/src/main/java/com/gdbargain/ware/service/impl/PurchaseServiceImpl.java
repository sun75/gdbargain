package com.gdbargain.ware.service.impl;

import com.gdbargain.common.constant.PurchaseDetailStatusEnum;
import com.gdbargain.common.constant.PurchaseStatusEnum;
import com.gdbargain.ware.entity.PurchaseDetailEntity;
import com.gdbargain.ware.service.PurchaseDetailService;
import com.gdbargain.ware.service.WareSkuService;
import com.gdbargain.ware.vo.MergeVo;
import com.gdbargain.ware.vo.PurchaseItemDoneVo;
import com.gdbargain.ware.vo.PurchaseDoneVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    @Autowired
    WareSkuService wareSkuService;

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
    /**
     *改变采购单的status，改变采购需求的status
     * id是采购单ID
     */
    @Override
    public void received(List<Long> ids) {
        //1.确认当前采购单是新建或者已分配状态
        //根据ID查询当前的entity
        List<PurchaseEntity> collect = ids.stream().map((id) -> {
            PurchaseEntity entity = this.getById(id);
            return entity;
        }).filter((e) -> {
            if (e.getStatus() == PurchaseStatusEnum.CREATED.getCode() ||
                    e.getStatus() == PurchaseStatusEnum.ASSIGNED.getCode()) {
                return true;
            }
            return false;
        }).map((e)->{
            e.setStatus(PurchaseStatusEnum.RECEIVED.getCode());
            e.setUpdateTime(new Date());
            return e;
        }).collect(Collectors.toList());

        //2.改变采购单的status
        this.updateBatchById(collect);

        //3.改变采购项的status listDetailByPurchasedId
        collect.forEach((e)->{
            List<PurchaseDetailEntity> entities = purchaseDetailService.listDetailByPurchasedId(e.getId());
            List<PurchaseDetailEntity> detailEntities = entities.stream().map((entity) -> {
                PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
                purchaseDetailEntity.setId(entity.getId());
                purchaseDetailEntity.setStatus(PurchaseDetailStatusEnum.BUYING.getCode());
                return purchaseDetailEntity;
            }).collect(Collectors.toList());

            purchaseDetailService.updateBatchById(detailEntities);
        });
    }

//下面这种写法会报错
//items.forEach((e)->{
//    if(e.getStatus().equals(PurchaseDetailStatusEnum.HAS_ERROR)){
//        flag = false;
//    }
//});
    @Override
    public void done(PurchaseDoneVo doneVo) {
        Long id = doneVo.getId();
        //1.改变采购项状态
        Boolean flag = true;
        List<PurchaseItemDoneVo> items = doneVo.getItems();
        ArrayList<PurchaseDetailEntity> updates = new ArrayList<>();
        for(PurchaseItemDoneVo i : items){
            PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
            if(i.getStatus() == PurchaseDetailStatusEnum.HAS_ERROR.getCode()){
                flag = false;
                detailEntity.setStatus(i.getStatus());
            }else{
                detailEntity.setStatus(PurchaseDetailStatusEnum.FINISHED.getCode());

                //3.将成功的采购进行入库：对相应的采购增加数量
                PurchaseDetailEntity entity = purchaseDetailService.getById(i.getItemId());
                wareSkuService.addStock(entity.getSkuId(), entity.getWareId(), entity.getSkuNum());
            }
            detailEntity.setId(i.getItemId());
            updates.add(detailEntity);
        }
        purchaseDetailService.updateBatchById(updates);

        //2.改变采购单状态
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(id);
        purchaseEntity.setStatus(flag ?
                PurchaseStatusEnum.FINISHED.getCode() : PurchaseStatusEnum.HAS_ERROR.getCode());
        purchaseEntity.setUpdateTime(new Date());
        this.save(purchaseEntity);


    }

}