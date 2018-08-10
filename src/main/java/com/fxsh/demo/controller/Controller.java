package com.fxsh.demo.controller;

import com.es.test.demo.data.Book;
import com.es.test.demo.data.User;
import com.es.test.demo.dsto.BaseDSTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.JsonComponentModule;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author hell
 * @date 2018/7/30
 */
@RestController
@SuppressWarnings("unused")
//@Api2Doc(name = "我是测试Controller")
public class Controller {

    @Autowired
    private BaseDSTO<User> userBaseDSTO;

    @Autowired
    private BaseDSTO<Book> bookBaseDSTO;

    //@Autowired
    private Book book;

    @Autowired
    private JsonComponentModule jsonComponentModule;


    @GetMapping(value = "/test")
    public void myTest(){
        User u = new User("aaaa",7788899);
        userBaseDSTO.add(u);
        //userBaseDSTO.deleteByPrimaryKey("userName","aaaa");
        Book book = new Book();
        book.setAuthor("bbbb");
        book.setPages(789);
        bookBaseDSTO.add(book);
        //User users = userBaseDSTO.get(null);
        //System.out.println("nnmm");
    }
}
