package com.example.elasticsearch.mapper;

import com.example.elasticsearch.dsto.Condition;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

/**
 * @author hell
 * @date 2018/4/16
 */
public interface CommonMapper {

    /**
     * 通过查询条件获取table表字段key的最大值
     * @param key
     * @param cond
     * @return
     */
	Object max(@Param("key") String key, @Param("table") String table, @Param("cond") Condition cond);
    /**
     * 通过查询条件获取table表字段key的最小值
     * @param key
     * @param cond
     * @return
     */
	Object min(@Param("key") String key, @Param("table") String table, @Param("cond") Condition cond);

	/**
     * 通过查询条件获取table表时间字段key的最大值
     * @param key
     * @param cond
     * @return
     */
	Date maxTime(@Param("key") String key, @Param("table") String table, @Param("cond") Condition cond);
    /**
     * 通过查询条件获取table表时间字段key的最小值
     * @param key
     * @param cond
     * @return
     */
	Date minTime(@Param("key") String key, @Param("table") String table, @Param("cond") Condition cond);


	Object sum(@Param("key") String key, @Param("table") String table, @Param("cond") Condition cond);
}
