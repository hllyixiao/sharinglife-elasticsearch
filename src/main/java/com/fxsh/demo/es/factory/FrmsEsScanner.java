package com.fxsh.demo.es.factory;

import com.es.test.demo.es.anno.FrmsEs;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;

/**
 * @author hell
 * @date 2018/8/10
 */
public class FrmsEsScanner extends ClassPathBeanDefinitionScanner {

    public FrmsEsScanner(BeanDefinitionRegistry registry) {
        super(registry);
    }

    @Override
    public void registerDefaultFilters() {
        // 注册只被FrmsDb修饰的类
        this.addIncludeFilter(new AnnotationTypeFilter(FrmsEs.class));
    }
}
