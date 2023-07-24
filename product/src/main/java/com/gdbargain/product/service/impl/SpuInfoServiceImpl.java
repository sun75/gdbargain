package com.gdbargain.product.service.impl;

import com.gdbargain.product.entity.AttrEntity;
import com.gdbargain.product.entity.ProductAttrValueEntity;
import com.gdbargain.product.entity.SpuInfoDescEntity;
import com.gdbargain.product.service.*;
import com.gdbargain.product.vo.BaseAttrs;
import com.gdbargain.product.vo.SpuSaveVo;
import org.springframework.beans.BeanUtils;
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

import com.gdbargain.product.dao.SpuInfoDao;
import com.gdbargain.product.entity.SpuInfoEntity;
import org.springframework.transaction.annotation.Transactional;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    SpuInfoDescService spuInfoDescService;

    @Autowired
    SpuImagesService spuImagesService;

    @Autowired
    AttrService attrService;

    @Autowired
    ProductAttrValueService productAttrValueService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 保存要加事务注解 @Transactional
     */
    @Transactional
    @Override
    public void saveSpuInfo(SpuSaveVo vo) {

        //1.保存SPU基本信息 info
        SpuInfoEntity infoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(vo, infoEntity);
        infoEntity.setCreateTime(new Date());
        infoEntity.setUpdateTime(new Date());
        this.saveSpuInfo(infoEntity);

        // 2.保存SPU的desc图片 desc
        List<String> descripts = vo.getDecript();
        SpuInfoDescEntity descEntity = new SpuInfoDescEntity();
        descEntity.setSpuId(infoEntity.getId());
        descEntity.setDecript(String.join(",", descripts));

        spuInfoDescService.saveDescEntity(descEntity);

        // 3.保存SPU的图片集images
        List<String> images = vo.getImages();
        spuImagesService.saveImages(infoEntity.getId(), images);

        // 4.保存SPU的规格参数BaseAttrs+商品属性product attr value
        List<BaseAttrs> baseAttrs = vo.getBaseAttrs();
        List<ProductAttrValueEntity> collect = baseAttrs.stream().map((baseAttr) -> {
            ProductAttrValueEntity entity = new ProductAttrValueEntity();
            entity.setAttrId(baseAttr.getAttrId());
            entity.setSpuId(infoEntity.getId());
            //属性名的查询，需要调用attrService
            AttrEntity attrEntity = attrService.getById(baseAttr.getAttrId());
            entity.setAttrName(attrEntity.getAttrName());
            entity.setAttrValue(baseAttr.getAttrValues());
            entity.setQuickShow(baseAttr.getShowDesc());
            return entity;
        }).collect(Collectors.toList());
        productAttrValueService.saveProductAttr(collect);

        // 5.SPU对应的SKU信息
        // 1)SKU的基本信息：sku_info
        // 2)SKU的图片信息：sku_images
        // 3)SKU销售属性信息：sku_sale_attr_value
        // 4)SKU的优惠/满减信息：sms_sku_ladder

    }

    @Override
    public void saveSpuInfo(SpuInfoEntity infoEntity) {
//        this.save(infoEntity);
        //保存基本信息：写下面这种，不写上面这种
        this.baseMapper.insert(infoEntity);
    }

}