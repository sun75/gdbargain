package com.gdbargain.es.Config;

import com.sun.org.apache.bcel.internal.generic.RETURN;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 1.导入依赖
 * 2.编写配置,给容器注入RestHighLevelClient
 * @author: shh
 * @createTime: 2023/8/2520:22
 */
@Configuration
public class ESConfig {
    public static final RequestOptions COMMON_OPTIONS;
    static {
        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
//        builder.addHeader("Authorization", "Bearer " + TOKEN);
//        builder.setHttpAsyncResponseConsumerFactory(
//                new HttpAsyncResponseConsumerFactory
//                        .HeapBufferedResponseConsumerFactory(30 * 1024 * 1024 * 1024));
        COMMON_OPTIONS = builder.build();
    }

    /**
     * 将RestHighLevelClient放到容器中，所以使用@Bean
     */
    @Bean
    public RestHighLevelClient esRestClient(){
//        RestHighLevelClient client = new RestHighLevelClient(
//                RestClient.builder(
//                        new HttpHost("192.168.0.104", 9200, "http")));
//        return client;

        //根据上文的官方文档，分解一下如下
        RestClientBuilder builder = null;
        //String hostname, int port, String scheme
        builder = RestClient.builder(new HttpHost("127.0.0.1", 9200, "http"));
        RestHighLevelClient client = new RestHighLevelClient(builder);
        return client;
    }

}
