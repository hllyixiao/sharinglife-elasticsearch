package com.example.elasticsearch.config;

import com.example.elasticsearch.dsto.BaseDSTO;
import com.example.elasticsearch.dsto.impl.BaseDSTODbImpl;
import com.example.elasticsearch.mapper.BookMapper;
import com.example.elasticsearch.mapper.UserMapper;
import com.example.elasticsearch.pojo.Book;
import com.example.elasticsearch.pojo.User;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import javax.sql.DataSource;

/**
 * @author hell
 * @date 2018/4/17
 */
@Configuration
@ConditionalOnProperty(name = "ds.source.type", havingValue = "db")
public class DefaultDataSourceConfig {

    private Logger logger = LoggerFactory.getLogger(DefaultDataSourceConfig.class);

    @Value("${ds.source.mapperlocation:}")
    private String mapperLocation;

    @Bean(name = "defaultDataSource")
    @ConfigurationProperties(prefix = "ds.source.jdbc")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "defaultSqlSessionFactory")
    public SqlSessionFactory defaultSqlSessionFactory(final @Qualifier("defaultDataSource") DataSource defaultDataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(defaultDataSource);
        //添加XML目录
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            bean.setMapperLocations(resolver.getResources(mapperLocation));
            return bean.getObject();
        } catch (Exception e) {
            logger.error("初始化默认DS SqlSessionFactory 失败",e);
            throw new RuntimeException(e);
        }

    }

    @Bean(name = "defaultBookDSTO")
    public BaseDSTO<Book> getBooktDSTO(
            @Qualifier("defaultSqlSessionFactory") SqlSessionFactory defaultSqlSessionFactory){
        return new BaseDSTODbImpl(defaultSqlSessionFactory, BookMapper.class,"book");
    }

    @Bean(name = "defaultUserDSTO")
    public BaseDSTO<User> getUserDSTO(
            @Qualifier("defaultSqlSessionFactory") SqlSessionFactory defaultSqlSessionFactory){
        return new BaseDSTODbImpl(defaultSqlSessionFactory, UserMapper.class,"user");
    }
}
