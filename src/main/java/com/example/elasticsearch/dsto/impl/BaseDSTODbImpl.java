package com.example.elasticsearch.dsto.impl;

import com.example.elasticsearch.dsto.BaseDSTO;
import com.example.elasticsearch.dsto.Condition;
import com.example.elasticsearch.mapper.BaseMapper;
import com.example.elasticsearch.mapper.CommonMapper;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

/**
 * @author hell
 * @date 2018/5/28
 */
public class BaseDSTODbImpl<T> implements BaseDSTO<T> {

    Logger logger = LoggerFactory.getLogger(BaseDSTODbImpl.class);

    private SqlSessionFactory sqlSessionFactory;
    /**
     * 对应的Mapper
     */
    private Class mapperClass;
    /**
     * 表名
     */
    private String tableName;

    public BaseDSTODbImpl(SqlSessionFactory sqlSessionFactory,Class mapperClass,String tableName) {
        this.sqlSessionFactory = sqlSessionFactory;
        this.mapperClass = mapperClass;
        this.tableName = tableName;
    }


    @Override
    public int add(T t) {
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH,false);
        BaseMapper baseMapper = (BaseMapper<T>)sqlSession.getMapper(mapperClass);
        int i = 0;
        try{
            i = baseMapper.insertSelective(t);
            sqlSession.commit();
        }catch (Exception e) {
            sqlSession.rollback();
        } finally {
            sqlSession.close();
        }
        return i;
    }

    @Override
    public int batchAdd(List<T> list) {
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH,false);
        BaseMapper baseMapper = (BaseMapper<T>)sqlSession.getMapper(mapperClass);
        try {
            int commitNumber = 0;
            for (T t : list) {
                baseMapper.insertSelective(t);
                commitNumber++;
                if (commitNumber > 0 && commitNumber % 1000 == 0) {
                    sqlSession.commit();
                }
            }
            sqlSession.commit();
        } catch (Exception e) {
            sqlSession.rollback();
            logger.error("batchAdd error:", e);
        } finally {
            sqlSession.close();
        }
        return 1;
    }

    @Override
    public int delete(T t) {
        return 0;
    }

    @Override
    public int deleteByPrimaryKey(Object id) {
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH,false);
        BaseMapper baseMapper = (BaseMapper<T>)sqlSession.getMapper(mapperClass);        int i = 0;
        try{
            i = baseMapper.deleteByPrimaryKey(id);
            sqlSession.commit();
        }catch (Exception e) {
            sqlSession.rollback();
            logger.error("delete error:", e);
        } finally {
            sqlSession.close();
        }
        return i;
    }

    @Override
    public int update(T t) {
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH,false);
        BaseMapper baseMapper = (BaseMapper<T>)sqlSession.getMapper(mapperClass);
        int i = 0;
        try{
            i = baseMapper.updateByPrimaryKeySelective(t);
            sqlSession.commit();
        }catch (Exception e) {
            sqlSession.rollback();
            logger.error("update error:", e);
        } finally {
            sqlSession.close();
        }
        return i;
    }

    @Override
    public T get(Condition cond) {
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH,false);
        BaseMapper baseMapper = (BaseMapper<T>)sqlSession.getMapper(mapperClass);
        try{
            List<T> list = baseMapper.selectByExample(cond);
            if(list != null && !list.isEmpty()){
                return list.get(0);
            }
        }catch (Exception e) {
            logger.error("get error:", e);
        } finally {
            sqlSession.close();
        }
        return null;
    }

    @Override
    public List<T> list(Condition cond) {
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH,false);
        BaseMapper baseMapper = (BaseMapper<T>)sqlSession.getMapper(mapperClass);
        try{
            return baseMapper.selectByExample(cond);
        }catch (Exception e) {
            logger.error("get list error:", e);
        } finally {
            sqlSession.close();
        }
        return null;
    }

    @Override
    public Long count(Condition cond) {
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH,false);
        BaseMapper baseMapper = (BaseMapper<T>)sqlSession.getMapper(mapperClass);
        try{
            return (long)baseMapper.countByExample(cond);
        }catch (Exception e) {
            logger.error("get count error:", e);
        } finally {
            sqlSession.close();
        }
        return null;
    }

    @Override
    public Object max(String key, Condition cond) {
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH,false);
        CommonMapper commonMapper = sqlSession.getMapper(CommonMapper.class);
        try{
            return commonMapper.max(key,tableName,cond);
        }catch (Exception e) {
            logger.error("get max error:", e);
        } finally {
            sqlSession.close();
        }
        return null;
    }

    @Override
    public Object min(String key, Condition cond) {
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH,false);
        CommonMapper commonMapper = sqlSession.getMapper(CommonMapper.class);
        try{
            return commonMapper.min(key,tableName,cond);
        }catch (Exception e) {
            logger.error("get min error:", e);
        } finally {
            sqlSession.close();
        }
        return null;
    }

    @Override
    public Date maxTime(String key, Condition cond) {
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH,false);
        CommonMapper commonMapper = sqlSession.getMapper(CommonMapper.class);
        try{
            return commonMapper.maxTime(key,tableName,cond);
        }catch (Exception e) {
            logger.error("get maxTime error:", e);
        } finally {
            sqlSession.close();
        }
        return null;
    }

    @Override
    public Date minTime(String key, Condition cond) {
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH,false);
        CommonMapper commonMapper = sqlSession.getMapper(CommonMapper.class);
        try{
            return commonMapper.minTime(key,tableName,cond);
        }catch (Exception e) {
            logger.error("get minTime error:", e);
        } finally {
            sqlSession.close();
        }
        return null;
    }
}
