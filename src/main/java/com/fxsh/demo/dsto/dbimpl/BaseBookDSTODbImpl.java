package com.fxsh.demo.dsto.dbimpl;

import com.es.test.demo.data.Book;
import com.es.test.demo.db.anno.FrmsDb;
import com.es.test.demo.dsto.BaseDSTODbImpl;

/**
 * @author hell
 * @date 2018/8/10
 */
@FrmsDb(mapperFullPath = "com.es.test.demo.mapper.BookMapper")
public class BaseBookDSTODbImpl extends BaseDSTODbImpl<Book> {
}
