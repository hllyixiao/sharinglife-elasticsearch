package com.fxsh.demo.db.factory;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

/**
 * @author hell
 * @date 2018/8/10
 */
@Configuration
@ConditionalOnProperty(name = "ds.source.type", havingValue = "db")
public class DbProcessor implements BeanFactoryPostProcessor,ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        FrmsDbScanner frmsDbScanner = new FrmsDbScanner((BeanDefinitionRegistry) beanFactory);
        frmsDbScanner.setResourceLoader(this.applicationContext);
        frmsDbScanner.scan("com.es.test.demo.dsto.dbimpl");
    }
}
