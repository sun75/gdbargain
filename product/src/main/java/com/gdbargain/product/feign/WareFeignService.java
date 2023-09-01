package com.gdbargain.product.feign;

import com.gdbargain.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author: shh
 * @createTime: 2023/9/112:00
 */

@FeignClient("ware")
public interface WareFeignService {

    /**
     * 方式1.R在设计的时候，加上泛型
     * 方式2.直接返回List结果
     * 方式3.自己封装解析结果
     */
    @PostMapping("/ware/wareinfo/hasStock")
    R getSkuHasStock(@RequestBody List<Long> skuIds);
}
