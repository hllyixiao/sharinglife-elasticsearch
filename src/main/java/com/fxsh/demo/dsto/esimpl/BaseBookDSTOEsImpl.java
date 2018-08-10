package com.fxsh.demo.dsto.esimpl;

import com.es.test.demo.data.Book;
import com.es.test.demo.dsto.BaseDSTOIEsImpl;
import com.es.test.demo.es.anno.FrmsEs;

/**
 * @author hell
 * @date 2018/8/2
 */
@FrmsEs(indexName = "book",typeName = "bookType")
public class BaseBookDSTOEsImpl extends BaseDSTOIEsImpl<Book> {

}
