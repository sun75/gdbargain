package com.gdbargain.product.service.impl;

import com.gdbargain.common.to.SkuReductionTo;
import com.gdbargain.common.to.SpuBoundsTo;
import com.gdbargain.common.to.es.SkuEsModel;
import com.gdbargain.common.utils.R;
import com.gdbargain.product.entity.*;
import com.gdbargain.product.feign.CouponSpuFeignService;
import com.gdbargain.product.service.*;
import com.gdbargain.product.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gdbargain.common.utils.PageUtils;
import com.gdbargain.common.utils.Query;

import com.gdbargain.product.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


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

    @Autowired
    SkuInfoService skuInfoService;

    @Autowired
    SkuImagesService skuImagesService;

    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    CouponSpuFeignService couponSpuFeignService;

    @Autowired
    BrandService brandService;

    @Autowired
    CategoryService categoryService;

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

        //5.保存SPU积分信息 sms_spu_bounds
        Bounds bounds = vo.getBounds();
        SpuBoundsTo spuBoundsTo = new SpuBoundsTo();
        BeanUtils.copyProperties(bounds, spuBoundsTo);
        spuBoundsTo.setSpuId(infoEntity.getId());
        //传给远程调用服务的service
        R r = couponSpuFeignService.saveSpuBounds(spuBoundsTo);
        if(r.getCode() != 0){
            log.error("远程保存SPU积分信息失败");
        }

        // 5.SPU对应的SKU信息
        List<Skus> skus = vo.getSkus();
        /**
         * 因为每个sku都会调用sku_images，所以不使用stream，使用佛reach
           skus.stream().map((sku) -> {

           }).collect(Collectors.toList())
        */
        if(skus != null && skus.size() > 0){
            skus.forEach((sku) -> {
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(sku, skuInfoEntity);
                skuInfoEntity.setBrandId(infoEntity.getBrandId());
                skuInfoEntity.setCatalogId(infoEntity.getCatalogId());
                skuInfoEntity.setSaleCount(0L);
                skuInfoEntity.setSpuId(infoEntity.getId());

                String defaultImg = "";
                for(Images img : sku.getImages()){
                    // 1 表示默认值
                    if(img.getDefaultImg() == 1){
                        defaultImg = img.getImgUrl();
                    }
                }
                skuInfoEntity.setSkuDefaultImg(defaultImg);
                // 1)SKU的基本信息：sku_info
                skuInfoService.saveSkuInfo(skuInfoEntity);

                // 2)SKU的图片信息：sku_images
                Long skuId = skuInfoEntity.getSkuId();
                List<SkuImagesEntity> imagesEntities = sku.getImages().stream().map((img) -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setSkuId(skuId);
                    skuImagesEntity.setImgUrl(img.getImgUrl());
                    skuImagesEntity.setDefaultImg(img.getDefaultImg());
                    return skuImagesEntity;
                }).filter((item)->{
                    //返回true就是需要，false就是剔除
                    return !StringUtils.isEmpty(item.getImgUrl());
                }).collect(Collectors.toList());
                skuImagesService.saveBatch(imagesEntities);

                // 3)SKU销售属性信息：sku_sale_attr_value
                List<Attr> attrs = sku.getAttr();
                List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = attrs.stream().map((attr) -> {
                    SkuSaleAttrValueEntity attrValueEntity = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(attr, attrValueEntity);
                    attrValueEntity.setSkuId(skuId);
                    return attrValueEntity;
                }).collect(Collectors.toList());
                skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntities);

                // 4)SKU的优惠/满减信息：sms_sku_ladder:这一步要操作远程服务
                SkuReductionTo skuReductionTo = new SkuReductionTo();
                BeanUtils.copyProperties(sku, skuReductionTo);
                skuReductionTo.setSkuId(skuId);
                //如果满减信息有问题，就不需要发送远程请求了
                if(skuReductionTo.getFullCount() > 0 ||
                        skuReductionTo.getFullPrice().compareTo(new BigDecimal("0")) == 1){
                    R r1 = couponSpuFeignService.saveSkuReduction(skuReductionTo);
                    if(r1.getCode() != 0){
                        log.error("远程保存SKU优惠信息失败");
                    }
                }

            });
        }



    }

    @Override
    public void saveSpuInfo(SpuInfoEntity infoEntity) {
//        this.save(infoEntity);
        //保存基本信息：写下面这种，不写上面这种
        this.baseMapper.insert(infoEntity);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();
        String key = (String)params.get("key");
        if(!StringUtils.isEmpty(key)){
            wrapper.and((w)->{
                w.eq("id", key).or().like("spu_name", key);
            });
        }

        String status = (String)params.get("status");
        if(!StringUtils.isEmpty(status)){
            wrapper.eq("publish_status", status);
        }

        String brandId = (String)params.get("brandId");
        if(!StringUtils.isEmpty(brandId)&&"0".equalsIgnoreCase(brandId)){
            wrapper.eq("brand_id", brandId);
        }

        String catelogId = (String)params.get("catelogId");
        if(!StringUtils.isEmpty(catelogId)&&"0".equalsIgnoreCase(catelogId)){
            wrapper.eq("catalog_id", catelogId);
        }

        /**
         * status key brandId catelogId
         */

        IPage<SpuInfoEntity> page = this.page(new Query<SpuInfoEntity>().getPage(params), wrapper);
        return new PageUtils(page);

    }

    /**
     * 商品上架
     * @param spuId
     */
    @Override
    public void up(Long spuId) {

        //1.查出当前spuId对应的所有SKU信息，品牌名字
        List<SkuInfoEntity> skus = skuInfoService.getSkusById(spuId);

        //todo 4查询当前sku的所有可以被检索的规格属性
        List<ProductAttrValueEntity> baseAttrs = productAttrValueService.baseAttrListForSpu(spuId);
        List<Long> attrIds = baseAttrs.stream().map((attr) -> {
            return attr.getAttrId();
        }).collect(Collectors.toList());
        List<Long> searchAttrIds =  attrService.selectSearchAttrIds(attrIds);
        HashSet<Long> idSet = new HashSet<>(searchAttrIds);
        List<SkuEsModel.Attr> attrsList = baseAttrs.stream().filter(item -> {
            return idSet.contains(item.getAttrId());
        }).map(item->{
            SkuEsModel.Attr skuAttrs = new SkuEsModel.Attr();
            BeanUtils.copyProperties(item, skuAttrs);
            return skuAttrs;
        }).collect(Collectors.toList());


        //2.封装每个sku的信息
        List<SkuEsModel> upProducts = skus.stream().map(sku -> {

            //组装需要的数据
            SkuEsModel skuEsModel = new SkuEsModel();
            BeanUtils.copyProperties(sku, skuEsModel);
            //SkuInfoEntity和SkuEsModel里面的属性找出不同的，进行处理
            skuEsModel.setSkuPrice(sku.getPrice());
            skuEsModel.setSkuImg(sku.getSkuDefaultImg());
            //todo 1发送远程调用，是否有库存

            //todo 2hotscore，默认刚上架是0
            skuEsModel.setHotScore(0L);

            //todo 3查询品牌和分类的名字信息
            BrandEntity brandEntity = brandService.getById(skuEsModel.getBrandId());

            skuEsModel.setBrandName(brandEntity.getName());
            skuEsModel.setBrandName(brandEntity.getLogo());

            CategoryEntity categoryEntity = categoryService.getById(skuEsModel.getCatalogId());
            skuEsModel.setCatalogName(categoryEntity.getName());
            //设置检索属性
            skuEsModel.setAttrs(attrsList);



            return skuEsModel;
        }).collect(Collectors.toList());

        //todo 5发送给es进行保存
    }

}