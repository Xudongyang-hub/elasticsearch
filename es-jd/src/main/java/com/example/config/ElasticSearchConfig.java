package com.example.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: xu
 * @Date: 2021-04-14 20:10
 * @description: 配置类
 */

@Configuration
public class ElasticSearchConfig {

    @Bean
    public RestHighLevelClient restHighLevelClient(){

        return new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http")));
    }
}
