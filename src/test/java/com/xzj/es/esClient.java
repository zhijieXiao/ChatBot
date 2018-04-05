package com.xzj.es;

import org.ansj.app.keyword.Keyword;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RegexpQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Map;
import java.util.Scanner;

public class esClient {
    //es连接客户端
    private TransportClient client = null;

    private final int MAX_MUSTVERB_NUM=2;

    /**
     * @Description: 初始化建立客户端到es连接
     * @Param: []
     * @Return: void
     */
    public void connection(){
        //设置一些属性
        //Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name","elasticsearch").build();
        // 获取一个客户端对象
        try {
            client = TransportClient.builder().build()
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
        } catch (UnknownHostException e) {
            e.printStackTrace();
            this.destroy();
        }
        //	指定连接的Es节点Ip和端口 端口默认使用 9300

        //	获取客户端 连接上的节点信息
//        ImmutableList<DiscoveryNode> nodes = client.connectedNodes();
//        for (DiscoveryNode discoveryNode : nodes) {
//            System.out.println(discoveryNode.getHostAddress()+"\t"+discoveryNode.getHostName());
//        }
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
     * @Description: 没啥用 还是全匹配
     * @Param: [index, question]
     * @Return: java.lang.String
     */
    public String getTernHits(String index,String question){
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("indistinction", question);
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(index);
        searchRequestBuilder.setQuery(termQueryBuilder);
        SearchResponse searchResponse = searchRequestBuilder.get();
        return searchResponse.toString();
    }

    public String getRegxHits(String index,String question){
        RegexpQueryBuilder regexpQueryBuilder = QueryBuilders.regexpQuery("indistinction", ".*"+question+".*");
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(index);

        searchRequestBuilder.setQuery(regexpQueryBuilder);
        SearchResponse searchResponse = searchRequestBuilder.get();
        return searchResponse.toString();
    }

    /**
     * @Description: 构造多条件查询queryBuilder
     * @Date: Created in 14:22 2018/3/28
     * @Param: [result]  ansj分词结果
     * @Return: org.elasticsearch.index.query.BoolQueryBuilder
     */
    public BoolQueryBuilder getMultiRegxQueryBuilder(Collection<Keyword> result, Map map){
        if (result.isEmpty()) return null;
        BoolQueryBuilder queryBuilder=QueryBuilders.boolQuery();
        int currentVerbNum=0;
        for (Keyword keyword:result){
            if( map.get(keyword.getName()) != null
                    && (map.get(keyword.getName()).toString().startsWith("n")
                    ||map.get(keyword.getName()).toString().startsWith("v"))) {
                if(currentVerbNum < this.MAX_MUSTVERB_NUM) {
                    currentVerbNum++;
                    queryBuilder.must(QueryBuilders.regexpQuery("question", ".*" + keyword.getName() + ".*"));
                }else{
                    queryBuilder.should(QueryBuilders.regexpQuery("question", ".*" + keyword.getName() + ".*"));
                }
            }
        }
        return queryBuilder;
    }

    public String getBoolHits(String index,Collection<Keyword> result,Map map){
        BoolQueryBuilder queryBuilder = this.getMultiRegxQueryBuilder(result,map);
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(index);
        searchRequestBuilder.setQuery(queryBuilder);
        SearchResponse searchResponse = searchRequestBuilder.setSize(5).get();
        return searchResponse.toString();
    }


    public void addDocument(Map map,String id){
        IndexResponse actionGet = client
                .prepareIndex("finance-chatbot3", "agency-reimbursement")
                .setSource(map)
                .setId(id)
                .execute().actionGet();
        System.out.println(actionGet.isCreated());
    }

    /**
     * 获取模糊查询相关问题
     * @param index
     * @param indistinction
     * @param id
     * @return
     */
    public String getIndistinctionHits(String index,String indistinction,String id){
        BoolQueryBuilder queryBuilder=QueryBuilders.boolQuery();
        //对模糊词匹配必须命中
        queryBuilder.must(QueryBuilders.regexpQuery("indistinction", ".*"+indistinction+".*"));
        //不命中已查询过的问题
        queryBuilder.mustNot(QueryBuilders.termQuery("_id", id));
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(index);

        searchRequestBuilder.setQuery(queryBuilder);
        SearchResponse searchResponse = searchRequestBuilder.setSize(3).get();
        System.out.println(searchResponse.toString());
        return searchResponse.toString();
    }

    /**
     * @Description: 系统文件清理
     * @Param: []
     * @Return: void
     */
    public void destroy(){
        this.client.close();
    }

    public static void main(String[] args) {
        esClient es = new esClient();
        es.connection();
        Scanner scan = new Scanner(System.in);
        while( scan.hasNext() ){
            String read = scan.nextLine();
            if(read.equals("q")) break;
            System.out.println("*********************start***********************");
            System.out.println(es.getIndistinctionHits("finance-chatbot3",read,"4"));
            System.out.println("*********************end*************************");
        }
        es.destroy();
    }
}
