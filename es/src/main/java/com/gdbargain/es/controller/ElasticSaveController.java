package com.gdbargain.es.controller;

import com.gdbargain.common.exception.BizCodeEnum;
import com.gdbargain.common.to.es.SkuEsModel;
import com.gdbargain.common.utils.R;
import com.gdbargain.es.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author: shh
 * @createTime: 2023/9/123:49
 */
@Slf4j
@RequestMapping("/search/save")
@RestController
public class ElasticSaveController {
    @Autowired
    ProductSaveService productSaveService;

    //上架商品
    @PostMapping("/product")
    public R productStatusUp(@RequestBody List<SkuEsModel> skuEsModels) {
        boolean b = false;
        try{
            b = productSaveService.productStatusUp(skuEsModels);
        }catch(Exception e){
            log.error("商品上架错误:{}", e);
            return R.error(BizCodeEnum.PRODUCT_UP_EXECPTION.getCode(), BizCodeEnum.PRODUCT_UP_EXECPTION.getMsg());
        }
        if(b){
            return R.ok();
        }else{
            return R.error(BizCodeEnum.PRODUCT_UP_EXECPTION.getCode(), BizCodeEnum.PRODUCT_UP_EXECPTION.getMsg());
        }
    }
}
