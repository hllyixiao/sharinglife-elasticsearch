package com.fxsh.demo.dsto;

import com.es.test.demo.mapper.BaseMapper;
import com.es.test.demo.utils.Condition;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.Date;
import java.util.List;

/**
 * @author hell
 * @date 2018/8/10
 */
public class BaseDSTODbImpl<T> implements BaseDSTO<T>{

    private SqlSessionFactory sqlSessionFactory;
    private Class<? extends BaseMapper> aClass;

    @Override
    public int add(T t) {
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH,false);
        BaseMapper baseMapper = sqlSession.getMapper(aClass);
        int i = -1;
        try{
            i = baseMapper.insertSelective(t);
            sqlSession.commit();
        }catch (Exception e) {
            sqlSession.rollback();
            //logger.error("insert DSBankOper error:", e);
        } finally {
            sqlSession.close();
        }
        return i;
    }

    @Override
    public int batchAdd(List<T> list) {
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH,false);
        BaseMapper baseMapper = sqlSession.getMapper(aClass);
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
            //logger.error("batchAdd DSBankOper error:", e);
            sqlSession.rollback();
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
        return 0;
    }

    @Override
    public int update(T t) {
        return 0;
    }

    @Override
    public T get(Condition cond) {
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

    public SqlSessionFactory getSqlSessionFactory() {
        return sqlSessionFactory;
    }

    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    public Class<? extends BaseMapper> getaClass() {
        return aClass;
    }

    public void setaClass(Class<? extends BaseMapper> aClass) {
        this.aClass = aClass;
    }
}
