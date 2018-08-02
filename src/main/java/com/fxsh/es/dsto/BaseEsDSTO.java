package com.fxsh.es.dsto;


import com.fxsh.es.utils.Condition;

import java.util.Date;
import java.util.List;

/**
 * ES操作的基本接口
 * @author hell
 * @date 2018/4/12
 */
public interface BaseEsDSTO<T> {

    /**
     * 添加数据
     * @param t
     * @return
     */
    int add(T t);

    /**
     * 批量添加数据
     * @param list
     * @return
     */
    int batchAdd(List<T> list);

    /**
     * 通过主键删除数据
     * @param keyName
     * @param data
     * @return
     */
    int deleteByPrimaryKey(String keyName, Object data);

    /**
     * 更新数据
     * @param t
     * @return
     */
    int update(T t, String keyName, Object data);

    /**
     * 通过查询条件Condition获取单条数据
     * @param cond
     * @return
     */
    T get(Condition cond);

    /**
     * 通过查询条件Condition获取多条数据
     * @param cond
     * @return
     */
    List<T> list(Condition cond);

    /**
     * 求topN
     * @param n 数量
     * @param type new:最新  hot:最热
     * @param attributes
     * @return
     */
    List<T> topN(int n, String type, String... attributes);

    /**
     * top5
     * @param type
     * @param attributes
     * @return
     */
    List<T> top5(String type, String... attributes);

    /**
     * 通过查询条件Condition获取数据数量
     * @param cond
     * @return
     */
    Long count(Condition cond);

    /**
     * 通过查询条件获取属性key的最大值
     * @param key
     * @param cond
     * @return
     */
    Object max(String key, Condition cond);

    /**
     * 通过查询条件获取属性key的最小值
     * @param key
     * @param cond
     * @return
     */
    Object min(String key, Condition cond);
    
    /**
     * 通过查询条件获取时间属性dataKey的最大值
     * @param dataKey
     * @param cond
     * @return
     */
    Date maxTime(String dataKey, Condition cond);

    /**
     * 通过查询条件获取时间字段dataKey的最小值
     * @param dataKey
     * @param cond
     * @return
     */
    Date minTime(String dataKey, Condition cond);
}
