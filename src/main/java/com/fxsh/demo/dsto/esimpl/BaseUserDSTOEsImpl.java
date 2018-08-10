package com.fxsh.demo.dsto.esimpl;

import com.es.test.demo.data.User;
import com.es.test.demo.dsto.BaseDSTOIEsImpl;
import com.es.test.demo.es.anno.FrmsEs;

/**
 * @author hell
 * @date 2018/8/2
 */
@FrmsEs(indexName = "user",typeName = "userType")
public class BaseUserDSTOEsImpl extends BaseDSTOIEsImpl<User> {
}
