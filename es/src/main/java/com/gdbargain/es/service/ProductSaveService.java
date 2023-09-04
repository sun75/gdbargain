package com.gdbargain.es.service;

import com.gdbargain.common.to.es.SkuEsModel;

import java.io.IOException;
import java.util.List;

/**
 * @author: shh
 * @createTime: 2023/9/123:53
 */
public interface ProductSaveService {
    boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException;
}
