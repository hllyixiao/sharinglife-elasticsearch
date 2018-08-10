package com.fxsh.demo.es.anno;

import com.es.test.demo.dsto.BaseDSTOIEsImpl;

import java.lang.annotation.*;

/**
 * <p>标记动态加载并注册对象到beanFactory,由spring托管
 *
 * <p>标记的对象必须继承{@link BaseDSTOIEsImpl}
 *
 * <p>参数要求:
 * <li>indexName : Es 的索引，必须全部为小写，默认为对象名称</li>
 * <li>typeName  : Es 型，必须全部为小写，默认为对象名称</li>
 *
 * @author hell
 * @date 2018/8/2
 * @since 1.0.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface FrmsEs {

    String indexName() default "";
    String typeName() default "";

}
