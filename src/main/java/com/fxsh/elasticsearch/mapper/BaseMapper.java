package com.fxsh.elasticsearch.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author hell
 * @date 2018/5/28
 */
public interface BaseMapper<T> {

    int countByExample(Condition example);

    int deleteByExample(Condition example);

    int deleteByPrimaryKey(Object cardNo);

    int insert(T record);

    int insertSelective(T record);

    List<T> selectByExample(Condition example);

    T selectByPrimaryKey(String cardNo);

    int updateByExampleSelective(@Param("record") T record, @Param("example") Condition example);

    int updateByExample(@Param("record") T record, @Param("example") Condition example);

    int updateByPrimaryKeySelective(T record);

    int updateByPrimaryKey(T record);
}
