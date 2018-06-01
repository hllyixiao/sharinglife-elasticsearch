package com.example.elasticsearch.dsto;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.Date;
import java.util.List;

/**
 * @author hell
 * @date 2018/4/12
 */
public interface BaseDSTO<T> {

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
     * 删除数据
     * @param t
     * @return
     */
    int delete(T t);
    /**
     * 通过主键删除数据
     * @param id
     * @return
     */
    int deleteByPrimaryKey(Object id);
    /**
     * 更新数据
     * @param t
     * @return
     */
    int update(T t);
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
     * 通过查询条件Condition获取数据数量
     * @param cond
     * @return
     */
    Long count(Condition cond);
    /**
     * 通过查询条件获取字段key的最大值
     * @param key
     * @param cond
     * @return
     */
    Object max(String key, Condition cond);
    /**
     * 通过查询条件获取字段key的最小值
     * @param key
     * @param cond
     * @return
     */
    Object min(String key, Condition cond);
    
    /**
     * 通过查询条件获取时间字段key的最大值
     * @param key
     * @param cond
     * @return
     */
    Date maxTime(String key, Condition cond);
    /**
     * 通过查询条件获取时间字段key的最小值
     * @param key
     * @param cond
     * @return
     */
    Date minTime(String key, Condition cond);
}
