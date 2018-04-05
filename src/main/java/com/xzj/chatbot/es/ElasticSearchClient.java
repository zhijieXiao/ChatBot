package com.xzj.chatbot.es;

import com.xzj.chatbot.ansj.VerbProperty;
import org.ansj.app.keyword.Keyword;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.mapper.SourceToParse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RegexpQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * @param
 * @Author:
 * @Description:
 * @Date: Created in 10:22 2018/3/29
 */
@Component
public class ElasticSearchClient {
    //es连接客户端
    private TransportClient client = null;

    private final int MAX_MUSTVERB_NUM=2;

    private final String NULL_ES_SEARCH="{\"hits\":{\"total\" : 0}}";

    public ElasticSearchClient(){
        System.out.println("es client created!!");
        this.connection();
    }
    /**
     * @Description: 初始化建立客户端到es连接
     * @Param: []
     * @Return: void
     */
    public void connection(){
        try {
            client = TransportClient.builder().build()
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
        } catch (UnknownHostException e) {
            e.printStackTrace();
            this.destroy();
        }
    }

    /**
     * @Description: 根据索引类型id获取文件
     * @Param: [index, type, id] 索引，类型，id
     * @Return: java.lang.String
     */
    public String get(String index,String type,String id){
        GetResponse response = this.client.prepareGet().setIndex(index).setType(type).setId(id).get();
        return response.getSourceAsString();
    }

    /**
     * @Description: 全匹配 中文适配不佳
     * @Param: [index, questions]
     * @Return: java.lang.String
     */
    public String getHits(String index, String questions){
        System.out.println("current questions is :"+questions);
        SearchResponse response = client.prepareSearch()
                .setQuery(QueryBuilders.fuzzyQuery("groupName", questions))             // Query
                .execute()
                .actionGet();
        return response.toString();
    }

    /**
     * @Description: 查询所有内容
     * @Param: []
     * @Return: java.lang.String
     */
    public String getAll(){
        SearchResponse response = client.prepareSearch().execute().actionGet();
        return response.toString();
    }

    /**
     * @Description:
     * @Param: [index, question]
     * @Return: java.lang.String
     */
    public String getTernHits(String index,String question){
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("question", question);
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(index);
        searchRequestBuilder.setQuery(termQueryBuilder);
        SearchResponse searchResponse = searchRequestBuilder.get();
        return searchResponse.toString();
    }

    /**
     * 获取模糊关键字查询相关问题
     * @param index
     * @param indistinction
     * @param id
     * @return
     */
    public String getIndistinctionHits(String index,String indistinction,String id){
        System.out.println(indistinction+id);
        BoolQueryBuilder queryBuilder=QueryBuilders.boolQuery();
        //对模糊词匹配必须命中
        queryBuilder.must(QueryBuilders.regexpQuery("indistinction", ".*"+indistinction+".*"));
        //对查询过的问题过滤
        queryBuilder.mustNot(QueryBuilders.termQuery("_id", id));
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(index);

        searchRequestBuilder.setQuery(queryBuilder);
        SearchResponse searchResponse = searchRequestBuilder.setSize(3).get();
        System.out.println(searchResponse.toString());
        return searchResponse.toString();
    }

    /**
     * @Description: 构造多条件查询queryBuilder
     * @Date: Created in 14:22 2018/3/28
     * @Param: [result]  ansj分词结果
     * @Return: org.elasticsearch.index.query.BoolQueryBuilder
     */
    public BoolQueryBuilder getMultiMustQueryBuilder(Collection<Keyword> result, Map<String,VerbProperty> map){
        if (result.isEmpty()) return null;
        BoolQueryBuilder queryBuilder=QueryBuilders.boolQuery();
        Integer currentVerbNum=0;
        for (Keyword keyword:result){
            if( map.get(keyword.getName()) != null
                    && (map.get(keyword.getName()).getNatureStr().startsWith("n")
                    ||map.get(keyword.getName()).getNatureStr().startsWith("v"))) {
                String question = map.get(keyword.getName()).getSynonyms()== null? (keyword.getName()):(map.get(keyword.getName()).getSynonyms().get(0).toString());
                if(currentVerbNum < this.MAX_MUSTVERB_NUM) {
                    currentVerbNum++;
                    System.out.println("must question:"+question);
                    queryBuilder.must(QueryBuilders.regexpQuery("question", ".*" + question + ".*"));
                }else{
                    queryBuilder.should(QueryBuilders.regexpQuery("question", ".*" + question + ".*"));
                    System.out.println("should question:"+question);
                }
            }
        }
        return currentVerbNum.intValue() > 0?queryBuilder:null;
    }

    /**
     * 构造分词结果匹配查询，该查询根据匹配度排序
     * @param result 含有分词结果的数组以及同义词
     * @return
     */
    public BoolQueryBuilder getMultiShouldQueryBuilder(Map<String,VerbProperty> result){
        if (result.isEmpty()) return null;
        BoolQueryBuilder queryBuilder=QueryBuilders.boolQuery();
        for(Map.Entry<String,VerbProperty> map:result.entrySet()){
            String question;
            if(map.getValue().getSynonyms()!=null){
                question = map.getValue().getSynonyms().get(0).toString();
            }else {
                question = map.getKey();
            }
            System.out.println("should question:"+question);
            queryBuilder.should(QueryBuilders.regexpQuery("question", ".*" + question + ".*"));
        }
        return queryBuilder;
    }
    /**
    * @Description: 获取条件查询es结果
    * @Date: Created in 10:28 2018/3/29
    * @Param: [index, result, map] [索引，关键字，关键字词性]
    * @Return: java.lang.String
    */
    public String getBoolHits(String index,Collection<Keyword> result,Map map){
        BoolQueryBuilder queryBuilder = this.getMultiShouldQueryBuilder(map);
        if(queryBuilder == null) return this.NULL_ES_SEARCH;
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(index);
        searchRequestBuilder.setQuery(queryBuilder);
        SearchResponse searchResponse = searchRequestBuilder.setSize(5).get();
        return searchResponse.toString();
    }


    /**
    * @Description: es文档添加
    * @Date: Created in 10:27 2018/3/29
    * @Param: [map, id] [文档内容，文档id]
    * @Return: void
    */
    public void addDocument(Map map,String id){
        IndexResponse actionGet = client
                .prepareIndex("finance-chatbot3", "agency-reimbursement")
                .setSource(map)
                .setId(id)
                .execute().actionGet();
        System.out.println(actionGet.isCreated());
    }

    /**
     * es根据索引值修改点赞，没帮助，点击次数
     * @param index 索引
     * @param type 类型
     * @param id   索引id
     * @param fileds 更新字段
     * @param like  字段值
     * @throws IOException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public void updateDocumentCount(String index,String type,String id,String fileds,String like) throws IOException, ExecutionException, InterruptedException {
        long likeNum=Long.parseLong(like)+1;
        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.index(index);
        updateRequest.type(type);
        updateRequest.id(id);
        updateRequest.doc(jsonBuilder()
                .startObject()
                .field(fileds, likeNum)
                .endObject());
        client.update(updateRequest).get();

    }

    /**
     * @Description: 系统文件清理
     * @Param: []
     * @Return: void
     */
    public void destroy(){
        this.client.close();
    }

    public String testString(String input){
        return input;
    }
}
