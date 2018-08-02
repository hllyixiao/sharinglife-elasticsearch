package com.fxsh.es.utils;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.metrics.max.Max;
import org.elasticsearch.search.aggregations.metrics.max.MaxAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.min.Min;
import org.elasticsearch.search.aggregations.metrics.min.MinAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author hell
 * @date 2018/5/23
 */
public class EsUtils {

    private static SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 通过对象的id获取该文档的 _id
     * @param indexName
     * @param typeName
     * @param keysMap
     * @param rhlClient
     * @return
     */
    public static String getEsIdByObjectId(String indexName,String typeName,Map<String,Object> keysMap,
                                           RestHighLevelClient rhlClient){
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        Set<String> keys = keysMap.keySet();
        for(String key : keys){
            MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery(key, keysMap.get(key));
            sourceBuilder.query(matchQueryBuilder);
        }
        SearchRequest searchRequest = new SearchRequest(indexName);
        searchRequest.types(typeName);
        searchRequest.source(sourceBuilder);
        try {
            //搜索请求的命中对象
            SearchHits shs = rhlClient.search(searchRequest).getHits();
            if(shs.getTotalHits() > 0){
                return shs.getAt(0).getId();
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
            if(value != null){
                map.put(fieldName, value);
            }
        }
        return map;
    }

    /**
     * 将map转成pojo对象
     * @param map
     * @param beanClass
     * @param <E>
     * @return
     * @throws Exception
     */
    public static <E> E mapToObject(Map<String, Object> map, Class beanClass) throws Exception {
        if (map == null){
            return null;
        }
        Object obj = beanClass.newInstance();

        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            int mod = field.getModifiers();
            if(Modifier.isStatic(mod) || Modifier.isFinal(mod)){
                continue;
            }
            field.setAccessible(true);
            field.set(obj, map.get(field.getName()));
        }
        return (E) obj;
    }

    /**
     * 获取最大值或最小值
     * @param indexName
     * @param typeName
     * @param fieldName
     * @param isMax  true : max    false : min
     * @param rhlClient
     * @param cond
     * @return
     */
    public static Object maxOrMin(String indexName,String typeName,String fieldName,boolean isMax,
                                  RestHighLevelClient rhlClient,Condition cond){
        Object maxOrMinRe = null;
        SearchRequest searchRequest = new SearchRequest(indexName);
        searchRequest.types(typeName);

        SearchSourceBuilder searchSourceBuilder = null;
        if(isMax){
            MaxAggregationBuilder maxAggregation =
                    AggregationBuilders
                            .max("agg")
                            .field(fieldName);
            searchSourceBuilder.aggregation(maxAggregation);
        }else{
            MinAggregationBuilder minAggregation =
                    AggregationBuilders
                            .min("agg")
                            .field(fieldName);
            searchSourceBuilder.aggregation(minAggregation);
        }
        searchRequest.source(searchSourceBuilder);

        try {
            SearchResponse sr = rhlClient.search(searchRequest);
            if(isMax){
                Max agg = sr.getAggregations().get("agg");
                maxOrMinRe = agg.getValue();
            }else{
                Min agg = sr.getAggregations().get("agg");
                maxOrMinRe = agg.getValue();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return maxOrMinRe;
    }

    /**
     * 获取大或最小时间
     * @param indexName
     * @param typeName
     * @param fieldName
     * @param isMax
     * @param rhlClient
     * @param cond
     * @return
     */
    public static Date maxOrMinDate(String indexName, String typeName, String fieldName, boolean isMax,
                                    RestHighLevelClient rhlClient, Condition cond){
        Date maxOrMinDateRe = null;
        try {
            //返回科学计数法表示的时间戳
            Object maxOrMinData = EsUtils.maxOrMin(indexName, typeName, fieldName, isMax, rhlClient, cond).toString();
            if(maxOrMinData != null){
                Long dataLong = Long.parseLong(new BigDecimal(maxOrMinData.toString()).toPlainString());
                maxOrMinDateRe =  format.parse(format.format(dataLong));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return maxOrMinDateRe;
    }
}
