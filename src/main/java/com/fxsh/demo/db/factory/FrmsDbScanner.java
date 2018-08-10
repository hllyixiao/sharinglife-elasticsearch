package com.fxsh.demo.db.factory;

import com.es.test.demo.db.anno.FrmsDb;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;

/**
 * @author hell
 * @date 2018/8/10
 */
public class FrmsDbScanner extends ClassPathBeanDefinitionScanner {

    public FrmsDbScanner(BeanDefinitionRegistry registry) {
        super(registry);
    }

    @Override
    public void registerDefaultFilters() {
        // 注册只被FrmsDb修饰的类
        this.addIncludeFilter(new AnnotationTypeFilter(FrmsDb.class));
    }
}
