package com.example.elasticsearch.dsto.impl;

import com.example.elasticsearch.dsto.BaseDSTO;
import com.example.elasticsearch.dsto.Condition;
import com.example.elasticsearch.utils.EsUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.*;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.max.Max;
import org.elasticsearch.search.aggregations.metrics.max.MaxAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.min.Min;
import org.elasticsearch.search.aggregations.metrics.min.MinAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.valuecount.ValueCount;
import org.elasticsearch.search.aggregations.metrics.valuecount.ValueCountAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;

/**
 * @author hell
 * @date 2018/5/24
 */
public class BaseDSTOEsImpl<T> implements BaseDSTO<T> {

    private RestHighLevelClient restHighLevelClient;
    private String typeName;
    private String indexName;

    public BaseDSTOEsImpl(RestHighLevelClient restHighLevelClient,
                          String typeName, String indexName){
        this.restHighLevelClient = restHighLevelClient;
        this.typeName = typeName;
        this.indexName = indexName;
    }

    @Override
    public int add(T t) {
        IndexRequest indexRequest = new IndexRequest(indexName, typeName);
        try {
            String source = new ObjectMapper().writeValueAsString(t);
            indexRequest.source(source, XContentType.JSON);
            IndexResponse a = restHighLevelClient.index(indexRequest);
            System.out.println(a);
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

            list.forEach(book ->{
                try {
                    IndexRequest indexRequest = new IndexRequest(indexName, typeName);
                    String source = new ObjectMapper().writeValueAsString(book);
                    indexRequest.source(source, XContentType.JSON);
                    bulkRequest.add(indexRequest);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            });
            try {
                restHighLevelClient.bulk(bulkRequest);
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
        if(Objects.nonNull(id)){
            String _id = EsUtils.getEsIdByObjectId(indexName,typeName,id,restHighLevelClient);
            if(Objects.nonNull(_id)){
                DeleteRequest deleteRequest = new DeleteRequest(indexName, typeName, _id);
                try {
                    restHighLevelClient.delete(deleteRequest);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return 0;
    }

    @Override
    public int update(T t) {
//        if(Objects.nonNull(t.getId())){
//            String _id = EsUtils.getEsIdByObjectId("elastic","book",book.getId(),restHighLevelClient);
//            if(Objects.nonNull(_id)){
//                UpdateRequest updateRequest = new UpdateRequest("elastic", "book", _id);
//                Map<String, Object> pojoMap = EsUtils.objectToMap(book);
//                updateRequest.doc(pojoMap);
//                try {
//                    restHighLevelClient.update(updateRequest);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
        return 0;
    }

    @Override
    public T get(Condition cond) {

        SearchRequest searchRequest = new SearchRequest(indexName);
        searchRequest.types(typeName);
        SearchSourceBuilder searchSourceBuilder = EsUtils.resolveCondition(cond);
        searchRequest.source(searchSourceBuilder);
        try {
            System.out.println(searchRequest);
            SearchResponse response = restHighLevelClient.search(searchRequest);

            SearchHits hits = response.getHits();
            SearchHit[] a = hits.getHits();
            for(SearchHit s:a){
                SearchHitField name = s.getField("name");
                System.out.println(name);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<T> list(Condition cond) {
        List<T> list = null;
        //建立搜索源生成器
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //limit
        sourceBuilder.from(0);
        sourceBuilder.size(10);
        //排序（升序）
        sourceBuilder.sort("pages", SortOrder.ASC);
        sourceBuilder.sort("id", SortOrder.DESC);
        //指定获取的字段，否则获取全部字段
        sourceBuilder.fetchSource(new String[]{"id","title","pages"}, new String[]{});

        //与范围内的文档匹配的查询  gte/lte 闭区间   gt/lt 开区间
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("pages");
        rangeQueryBuilder.gte(1);
        rangeQueryBuilder.lte(112);
        RangeQueryBuilder rangeQueryBuilder1 = QueryBuilders.rangeQuery("id");
        rangeQueryBuilder1.gte(0);
        rangeQueryBuilder1.lte(6);

        //Aggregations相当于SQL中的group by部分
        //Metric 可配合avg、max、sum等聚合函数



        //匹配查询是一种分析文本并构造查询的查询。
        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("title", "book2");
        //设置模糊匹配
        matchQueryBuilder.fuzziness(Fuzziness.AUTO);
        matchQueryBuilder.prefixLength(3);
        matchQueryBuilder.maxExpansions(10);

        TermsQueryBuilder termsQueryBuilder = QueryBuilders.termsQuery("title",new ArrayDeque<>());

        //匹配与其他查询的布尔组合匹配的文档的查询。
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        boolBuilder.must(rangeQueryBuilder);
        boolBuilder.must(rangeQueryBuilder1);
        //boolBuilder.mustNot(matchQueryBuilder);
        boolBuilder.must(matchQueryBuilder);

        sourceBuilder.query(boolBuilder);

        SearchRequest searchRequest = new SearchRequest(indexName);
        searchRequest.types(typeName);
        searchRequest.source(sourceBuilder);

        List books = null;
        try {
            SearchResponse response = restHighLevelClient.search(searchRequest);
            SearchHits hits = response.getHits();
            books = new ArrayList<>(new Long(hits.getTotalHits()).intValue());
            for (SearchHit searchHit : hits) {
                Map source = searchHit.getSource();
                //Book book =mapper.readValue(mapper.writeValueAsString(source), Book.class);
                //books.add(book);
            }
            System.out.println("response: "+response);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("searchRequest: "+searchRequest);
        return books;
    }

    @Override
    public Long count(Condition cond) {
        Long count = null;
        SearchRequest searchRequest = new SearchRequest(indexName);
        searchRequest.types(typeName);
        ValueCountAggregationBuilder aggregation =
                AggregationBuilders
                        .count("agg")
                        .field("_id");
        SearchSourceBuilder searchSourceBuilder = EsUtils.resolveCondition(cond);
        searchSourceBuilder.aggregation(aggregation);
        searchRequest.source(searchSourceBuilder);

        try {
            SearchResponse sr = restHighLevelClient.search(searchRequest);
            System.out.println(searchSourceBuilder);
            ValueCount agg = sr.getAggregations().get("agg");
            count = agg.getValue();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(count);
        return count;
    }

    @Override
    public Object max(String key, Condition cond) {
        Object max = null;
        SearchRequest searchRequest = new SearchRequest(indexName);
        searchRequest.types(typeName);
        MaxAggregationBuilder aggregation =
                AggregationBuilders
                        .max("agg")
                        .field(key);
        SearchSourceBuilder searchSourceBuilder = EsUtils.resolveCondition(cond);
        searchSourceBuilder.aggregation(aggregation);
        searchRequest.source(searchSourceBuilder);

        try {
            SearchResponse sr = restHighLevelClient.search(searchRequest);
            Max agg = sr.getAggregations().get("agg");
            max = agg.getValue();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(max);
        return max;
    }

    @Override
    public Object min(String key, Condition cond) {
        Object min = null;
        SearchRequest searchRequest = new SearchRequest(indexName);
        searchRequest.types(typeName);
        MinAggregationBuilder aggregation =
                AggregationBuilders
                        .min("agg")
                        .field(key);
        SearchSourceBuilder searchSourceBuilder = EsUtils.resolveCondition(cond);
        searchSourceBuilder.aggregation(aggregation);
        searchRequest.source(searchSourceBuilder);

        try {
            SearchResponse sr = restHighLevelClient.search(searchRequest);
            Min agg = sr.getAggregations().get("agg");
            min = agg.getValue();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(min);
        return min;
    }

    @Override
    public Date maxTime(String key, Condition cond) {
        return null;
    }

    @Override
    public Date minTime(String key, Condition cond) {
        return null;
    }
}
