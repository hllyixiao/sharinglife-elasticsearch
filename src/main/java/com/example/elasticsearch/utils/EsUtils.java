package com.example.elasticsearch.utils;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.alias.Alias;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author hell
 * @date 2018/5/23
 */
public class EsUtils {

    /**
     * 创建索引
     * @param indexName
     * @param alias
     * @param restHighLevelClient
     */
    public static void createIndex(String indexName,String alias,RestHighLevelClient restHighLevelClient){
        CreateIndexRequest createIndexRequest = new CreateIndexRequest(indexName);
        //设置分片和副本
        createIndexRequest.settings(Settings.builder()
                .put("index.number_of_shards", 3)
                .put("index.number_of_replicas", 2));
        //设置别名
        if(Objects.nonNull(alias)){
            createIndexRequest.alias(new Alias(alias));
        }
        try {
            createIndexRequest.mapping("tweet",
                    "  {\n" +
                            "    \"tweet\": {\n" +
                            "      \"properties\": {\n" +
                            "        \"message\": {\n" +
                            "          \"type\": \"text\"\n" +
                            "        }\n" +
                            "      }\n" +
                            "    }\n" +
                            "  }",
                    XContentType.JSON);
            CreateIndexResponse createIndexResponse = restHighLevelClient.indices().create(createIndexRequest);
            //指示所有节点是否已确认请求。
            boolean acknowledged = createIndexResponse.isAcknowledged();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteIndex(String indexName,RestHighLevelClient restHighLevelClient){
        DeleteIndexRequest request = new DeleteIndexRequest(indexName);
        //对集群而言 等待所有节点以时间值确认索引删除的超时时间。
        request.timeout(TimeValue.timeValueMinutes(2));
        try {
            request.indicesOptions(IndicesOptions.lenientExpandOpen());
            DeleteIndexResponse deleteIndexResponse = restHighLevelClient.indices().delete(request);
            boolean acknowledged = deleteIndexResponse.isAcknowledged();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ElasticsearchException exception){
            if (exception.status() == RestStatus.NOT_FOUND) {
                //如果未找到要删除的索引，可以进行处理
            }
        }
    }


    /**
     * 通过对象的id获取改文档的 _id
     * @param index
     * @param type
     * @param id
     * @param restHighLevelClient
     * @return
     */
    public static String getEsIdByObjectId(String index,String type,Object id,RestHighLevelClient restHighLevelClient){
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("id", id);
        sourceBuilder.query(matchQueryBuilder);

        SearchRequest searchRequest = new SearchRequest(index);
        searchRequest.types(type);
        searchRequest.source(sourceBuilder);
        try {
            //搜索请求的命中对象
            SearchHits shs = restHighLevelClient.search(searchRequest).getHits();
            if(shs.getTotalHits() > 0){
                SearchHit sh = shs.getAt(0);
                return sh.getId();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取利用反射获取类里面的值和名称
     * @param obj
     * @return
     */
    public static Map<String, Object> objectToMap(Object obj) {
        Map<String, Object> map = new HashMap<>();
        Class<?> clazz = obj.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            String fieldName = field.getName();
            Object value = null;
            try {
                value = field.get(obj);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            if(Objects.nonNull(value)){
                map.put(fieldName, value);
            }
        }
        return map;
    }
}
