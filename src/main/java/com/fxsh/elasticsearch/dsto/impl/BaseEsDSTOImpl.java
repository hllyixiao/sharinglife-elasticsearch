package com.fxsh.elasticsearch.dsto.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fxsh.elasticsearch.dsto.BaseEsDSTO;
import com.fxsh.elasticsearch.utils.Condition;
import com.fxsh.elasticsearch.utils.EsUtils;
import com.fxsh.elasticsearch.utils.Page;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.metrics.valuecount.ValueCount;
import org.elasticsearch.search.aggregations.metrics.valuecount.ValueCountAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;

/**
 * @author hell
 * @date 2018/6/29
 */
public class BaseEsDSTOImpl<T> implements BaseEsDSTO<T> {

    private Class<T> entityClass;
    private RestHighLevelClient rhlClient;
    private String typeName;
    private String indexName;

    @Override
    public int add(T t) {
        IndexRequest indexRequest = new IndexRequest(indexName, typeName);
        try {
            String source = new ObjectMapper().writeValueAsString(t);
            indexRequest.source(source, XContentType.JSON);
            rhlClient.index(indexRequest);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 1;
    }

    @Override
    public int batchAdd(List<T> list) {
        if(!CollectionUtils.isEmpty(list)){
            BulkRequest bulkRequest = new BulkRequest();
            for(Object data : list){
                try {
                    IndexRequest indexRequest = new IndexRequest(indexName, typeName);
                    String source = new ObjectMapper().writeValueAsString(data);
                    indexRequest.source(source, XContentType.JSON);
                    bulkRequest.add(indexRequest);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
            try {
                rhlClient.bulk(bulkRequest);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    @Override
    public int deleteByPrimaryKey(String keyName, Object data) {
        int i = 0;
        if(data != null && Objects.equals(keyName,"")){
            Map<String,Object> keysMap = new HashMap<>(1);
            keysMap.put(keyName,data);
            String _id = EsUtils.getEsIdByObjectId(indexName,typeName,keysMap,rhlClient);
            DeleteRequest deleteRequest = new DeleteRequest(indexName, typeName, _id);
            try {
                rhlClient.delete(deleteRequest);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return i;
    }

    @Override
    public int update(T t,String keyName,Object data) {
        int i = 0;
        if(t != null){
            Map<String,Object> keysMap = new HashMap<>(1);
            keysMap.put(keyName,data);
            String _id = EsUtils.getEsIdByObjectId(indexName,typeName,keysMap,rhlClient);
            UpdateRequest updateRequest = new UpdateRequest(indexName, typeName, _id);
            Map<String, Object> pojoMap = EsUtils.objectToMap(data);
            updateRequest.doc(pojoMap);
            try {
                rhlClient.update(updateRequest);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return i;
    }

    @Override
    public T get(Condition cond) {
        //设置返回一条数据
        cond.setPage(new Page(0,1));

        SearchRequest searchRequest = new SearchRequest(indexName);
        searchRequest.types(typeName);
        SearchSourceBuilder searchSourceBuilder = null;

        searchRequest.source(searchSourceBuilder);
        SearchHits hits;
        try {
            hits = rhlClient.search(searchRequest).getHits();
            if(hits != null && hits.totalHits > 0){
                Map<String, Object> map = hits.getAt(0).getSource();
                return EsUtils.mapToObject(map, entityClass);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<T> list(Condition cond) {
        return null;
    }

    @Override
    public List<T> topN(int n, String type, String... attributes) {
        return null;
    }

    @Override
    public List<T> top5(String type, String... attributes) {
        return null;
    }

    @Override
    public Long count(Condition cond) {
        //设置page的length为0，使其不返回满足条件的数据，只返回满足条件数据的数量（count）
        cond.setPage(new Page());
        Long count = 0L;
        SearchRequest searchRequest = new SearchRequest(indexName);
        searchRequest.types(typeName);
        ValueCountAggregationBuilder aggregation =
                AggregationBuilders
                        .count("agg")
                        .field("_id");
        SearchSourceBuilder searchSourceBuilder = null;
        searchSourceBuilder.aggregation(aggregation);
        searchRequest.source(searchSourceBuilder);

        try {
            System.out.println(searchSourceBuilder);
            SearchResponse sr = rhlClient.search(searchRequest);
            ValueCount agg = sr.getAggregations().get("agg");
            count = agg.getValue();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return count;
    }

    @Override
    public Object max(String key, Condition cond) {
        return EsUtils.maxOrMin(indexName,typeName,key,true,rhlClient,cond);
    }

    @Override
    public Object min(String key, Condition cond) {
        return EsUtils.maxOrMin(indexName,typeName,key,false,rhlClient,cond);
    }

    @Override
    public Date maxTime(String dateKey, Condition cond) {
        return EsUtils.maxOrMinDate(indexName,typeName,dateKey,true,rhlClient,cond);
    }

    @Override
    public Date minTime(String dateKey, Condition cond) {
        return EsUtils.maxOrMinDate(indexName,typeName,dateKey,false,rhlClient,cond);
    }
}
