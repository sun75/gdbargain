package com.gdbargain.ware.feign;

import com.gdbargain.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author: shh
 * @createTime: 2023/7/2715:02
 */

@FeignClient("product")
public interface ProductFeignService {

    /**
     * /product/skuinfo/info/{skuId} ：直接让后台指定服务发请求
     * /api/product/skuinfo/info/{skuId}: 所有请求过网关
     * @param skuId
     * @return
     */
    @RequestMapping("/product/skuinfo/info/{skuId}")
    R info(@PathVariable("skuId") Long skuId);
}
