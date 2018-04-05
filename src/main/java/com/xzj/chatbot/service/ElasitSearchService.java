package com.xzj.chatbot.service;

import com.xzj.chatbot.es.ElasticSearchClient;
import org.ansj.app.keyword.Keyword;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * @param
 * @Author:
 * @Description: es调用服务类
 * @Date: Created in 11:21 2018/3/29
 */
@Component
public class ElasitSearchService {
    @Autowired
    private ElasticSearchClient client;

    public ElasticSearchClient getClient() {
        return client;
    }

    public void setClient(ElasticSearchClient client) {
        this.client = client;
    }

    public String getHits(String index, Collection<Keyword> result, Map map){
        return client.getBoolHits(index,result,map);
    }

    public String getIndistinction(String index,String indistinction,String id){
        return client.getIndistinctionHits(index,indistinction,id);
    }

    public String getAnswer(String index,String type,String id){
        return client.get(index,type,id);
    }

    public void upLoadDocument(String index,String type,String id,String fileds,String like){
        try {
            client.updateDocumentCount(index,type,id,fileds,like);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String testString(String input){
        return client.testString(input);
    }
}
