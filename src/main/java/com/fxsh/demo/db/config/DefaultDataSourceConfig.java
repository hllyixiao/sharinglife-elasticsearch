package com.fxsh.demo.db.config;


import com.es.test.demo.db.anno.FrmsDb;
import com.es.test.demo.mapper.BaseMapper;
import org.apache.commons.lang.WordUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.util.Map;

/**
 * @author hell
 * @date 2018/4/17
 */
@Configuration
@ConditionalOnProperty(name = "ds.source.type", havingValue = "db")
public class DefaultDataSourceConfig implements BeanFactoryAware{

    private Logger logger = LoggerFactory.getLogger(DefaultDataSourceConfig.class);

    private BeanFactory beanFactory;

    @Value("${ds.source.mapperlocation:}")
    private String mapperLocation;

    @Override
    @DependsOn("defaultSqlSessionFactory")
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
        
    }

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
            SqlSessionFactory sqlSessionFactory = bean.getObject();
            registerDbBeanInfo(sqlSessionFactory);
            return sqlSessionFactory;
        } catch (Exception e) {
            logger.error("初始化默认DS SqlSessionFactory 失败",e);
            throw new RuntimeException(e);
        }

    }

    private void registerDbBeanInfo(@Qualifier("defaultSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        if (beanFactory != null && beanFactory instanceof DefaultListableBeanFactory) {
            DefaultListableBeanFactory dbf = (DefaultListableBeanFactory) beanFactory;

            // 获取所有被@FrmsDb注解修饰的类的类类型
            Map<String, Object> frmsDbBeanss = dbf.getBeansWithAnnotation(FrmsDb.class);

            // 遍历所有的类类型，获取@FrmsDb注解信息
            for (Object bean : frmsDbBeanss.values()) {
                Class<?> beanClass = bean.getClass();
                FrmsDb frmsDb = beanClass.getAnnotation(FrmsDb.class);

                String mapperFullPath = frmsDb.mapperFullPath();
                Assert.hasText(mapperFullPath,"ddd");

                try {
                    Class<? extends BaseMapper> baseMapperClass =
                            (Class<? extends BaseMapper>) Class.forName(mapperFullPath);
                    String   beanKey = WordUtils.uncapitalize(beanClass.getSimpleName());

                    // 动态添加属性
                    BeanDefinitionBuilder dataSourceBuider = BeanDefinitionBuilder.genericBeanDefinition(beanClass);
                    dataSourceBuider.addPropertyValue("sqlSessionFactory", sqlSessionFactory);
                    dataSourceBuider.addPropertyValue("aClass", baseMapperClass);
                    // 向beanFactory注册，由spring托管
                    dbf.registerBeanDefinition(beanKey, dataSourceBuider.getBeanDefinition());
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
