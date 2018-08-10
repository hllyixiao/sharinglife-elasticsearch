package com.fxsh.demo.db.anno;

import java.lang.annotation.*;

/**
 * @author hell
 * @date 2018/8/10
 *
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface FrmsDb {
    /**
     *
     */
    String mapperFullPath();
}
