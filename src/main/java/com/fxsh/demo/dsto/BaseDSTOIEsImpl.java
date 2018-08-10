package com.fxsh.demo.dsto;

import com.es.test.demo.utils.Condition;
import com.es.test.demo.utils.EsUtils;
import com.es.test.demo.utils.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author hell
 * @date 2018/6/29
 */
public class BaseDSTOIEsImpl<T> implements BaseDSTO<T> {

    private Class beanClass;
    private RestHighLevelClient rhlClient;
    private String indexName;
    private String typeName;

    @Override
    public int add(T t) {
        IndexRequest indexRequest = new IndexRequest(indexName, typeName);
        try {
            String source = new ObjectMapper().writeValueAsString(t);
            indexRequest.source(source, XContentType.JSON);
            rhlClient.index(indexRequest);
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
    public int delete(T t) {
        return 0;
    }

    @Override
    public int deleteByPrimaryKey(Object id) {
        return 1;
    }

    @Override
    public int update(T t) {
        return 0;
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
                return EsUtils.mapToObject(map, beanClass);
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
    public Long count(Condition cond) {
        return null;
    }

    @Override
    public Object max(String key, Condition cond) {
        return null;
    }

    @Override
    public Object min(String key, Condition cond) {
        return null;
    }

    @Override
    public Date maxTime(String key, Condition cond) {
        return null;
    }

    @Override
    public Date minTime(String key, Condition cond) {
        return null;
    }

    public Class getBeanClass() {
        return beanClass;
    }

    public void setBeanClass(Class beanClass) {
        this.beanClass = beanClass;
    }

    public RestHighLevelClient getRhlClient() {
        return rhlClient;
    }

    public void setRhlClient(RestHighLevelClient rhlClient) {
        this.rhlClient = rhlClient;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}
