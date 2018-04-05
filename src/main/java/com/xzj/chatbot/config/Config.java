//package com.xzj.chatbot.config;
//
//import com.xzj.chatbot.es.ElasticSearchClient;
//import com.xzj.chatbot.service.ElasitSearchService;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
///**
// * @param
// * @Author:
// * @Description: 配置类
// * @Date: Created in 11:30 2018/3/29
// */
//@Configuration
//public class Config {
//
//    @Bean
//    public ElasticSearchClient getElasticSearchClient(){return new ElasticSearchClient();}
//
//    @Bean
//    public ElasitSearchService getElasitSearchService(){
//        ElasitSearchService esService=new ElasitSearchService();
//        esService.setClient(getElasticSearchClient());
//        return esService;
//    }
//}
