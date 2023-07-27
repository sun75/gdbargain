package com.gdbargain.ware.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author: shh
 * @createTime: 2023/2/1901:11
 */

@Configuration
@EnableTransactionManagement //开启事务
@MapperScan("com.gdbargain.ware.dao")
public class MybatisConfig {
    //到Mybatis-plus官网直接复制分页插件
    @Bean
    public PaginationInterceptor paginationInterceptor(){
        PaginationInterceptor interceptor = new PaginationInterceptor();
        //设置请求的页面大鱼最大页后操作，true调回到首页，false继续请求，默认false
        interceptor.setOverflow(true);
        //设置最大但也限制数量，默认500条，-1不受限制
        interceptor.setLimit(1000);
        return interceptor;
    }
}
