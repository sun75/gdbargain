package com.gdbargain.es.service.impl;

import com.alibaba.fastjson2.JSON;
import com.gdbargain.common.to.es.SkuEsModel;
import com.gdbargain.es.Config.ESConfig;
import com.gdbargain.es.constant.EsConstant;
import com.gdbargain.es.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: shh
 * @createTime: 2023/9/123:53
 */
//@Service表示添加到容器
@Service
@Slf4j
public class ProductSaveServiceImpl implements ProductSaveService {
    @Autowired
    RestHighLevelClient restHighLevelClient;

    @Override
    public boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException {
        //保存到ES
        //1.给ES建立索引.product，建立好映射关系(这一步是操作kibana建立)

        //2.给ES保存数据
        //BulkRequest bulkRequest, RequestOptions options
        BulkRequest bulkRequest = new BulkRequest();
        for (SkuEsModel model:skuEsModels) {
            //1.构造保存请求
            IndexRequest indexRequest = new IndexRequest(EsConstant.PRODUCT_INDEX);
            indexRequest.id(model.getSkuId().toString());
            String jsonString = JSON.toJSONString(model);
            indexRequest.source(jsonString, XContentType.JSON);

            bulkRequest.add(indexRequest);
        }
        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, ESConfig.COMMON_OPTIONS);
        //todo 如果批量错误，处理错误
        boolean b = bulk.hasFailures();
        List<String> collect = Arrays.stream(bulk.getItems()).map(item -> {
            return item.getId();
        }).collect(Collectors.toList());
        log.info("商品上架完成信息:{},返回数据:{}", collect, bulk.toString());
        return b;
    }
}
