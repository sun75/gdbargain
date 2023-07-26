package com.gdbargain.product.feign;

import com.gdbargain.common.to.SkuReductionTo;
import com.gdbargain.common.to.SpuBoundsTo;
import com.gdbargain.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author: shh
 * @createTime: 2023/7/2510:21
 * 1、CouponFeignService.saveSpuBounds(spuBoundTo);表示传的对象，不是传的基本类型数据
 *      *      1）、@RequestBody将这个对象转为json。
 *      *      2）、找到gulimall-coupon服务，给/coupon/spubounds/save发送请求。
 *      *          将上一步转的json放在请求体位置，发送请求；
 *      *      3）、对方服务收到请求。请求体里有json数据。
 *      *          (@RequestBody SpuBoundsEntity spuBounds)；将请求体的json转为SpuBoundsEntity；
 *      * 只要json数据模型是兼容的。双方服务无需使用同一个to
 */
//声明调用远程服务
@FeignClient("coupon")
public interface CouponSpuFeignService {
    @PostMapping("/coupon/spubounds/save")
    R saveSpuBounds(@RequestBody SpuBoundsTo spuBoundsTo);

    @PostMapping("/coupon/skufullreduction/saveinfo")
    R saveSkuReduction(@RequestBody SkuReductionTo skuReductionTo);
}
