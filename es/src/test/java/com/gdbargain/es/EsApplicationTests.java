package com.gdbargain.es;

import com.alibaba.fastjson2.JSON;
import com.gdbargain.es.Config.ESConfig;
import lombok.Data;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)//表示用spring的驱动进行单元测试，所以SpringRunner
@SpringBootTest
public class EsApplicationTests {
    @Autowired
    private RestHighLevelClient client;

    @Test
    public void contextLoads() {
        System.out.println(client);
    }

    /**
     * 存储更新都行
     */
    @Test
    public void indexTest() throws IOException {
        IndexRequest indexRequest = new IndexRequest("users");
        //数据id
        indexRequest.id("1");
//        indexRequest.source("userName","zs","age",18);
        User user = new User();
        user.setUserName("zhangsan");
        //要保存的内容
        String jsonString = JSON.toJSONString(user);
        indexRequest.source(jsonString, XContentType.JSON);

        //执行操作，网络操作都有异常，所以这边抛出异常
        IndexResponse index = client.index(indexRequest, ESConfig.COMMON_OPTIONS);

        //提取有用的响应数据
        System.out.println(index);
    }

    @Data
    class User{
        private String userName;
        private String gender;
        private Integer age;
    }
}
