package com.fxsh.es.anno;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * indexName 必须全部为小写
 * @author hell
 * @date 2018/8/2
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface FrmsEs {

    String indexName() default "";
    String typeName() default "";

}
