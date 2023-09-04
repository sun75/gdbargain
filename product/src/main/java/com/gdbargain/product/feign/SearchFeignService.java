package com.gdbargain.product.feign;

import com.gdbargain.common.to.es.SkuEsModel;
import com.gdbargain.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author: shh
 * @createTime: 2023/9/411:34
 */
@FeignClient("es")
public interface SearchFeignService {
    @PostMapping("/search/save/product")
    R productStatusUp(@RequestBody List<SkuEsModel> skuEsModels);
}
