package com.example.elasticsearch.controller;

import com.example.elasticsearch.dsto.BaseDSTO;
import com.example.elasticsearch.dsto.Condition;
import com.example.elasticsearch.pojo.Book;
import com.example.elasticsearch.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author hell
 * @date 2018/5/22
 */
@RestController
public class DemoController {

    @Autowired
    private BaseDSTO<Book> bookBaseDSTO;

    @Autowired
    private BaseDSTO<User> userBaseDSTO;

    @GetMapping("/dobook")
    public void toDoBook(@RequestParam("type") Integer type){
        switch (type){
            case 1:
                add();break;
            case 2:
                batchAdd();break;
            case 3:
                update();break;
            case 4:
                delete();break;
            case 5:
                bookBaseDSTO.list(null);break;
            case 6:
                get();break;
            default:
                System.out.println("不正确的命令");break;
        }
    }

    public void add(){
        Book book = new Book();
        book.setId(5);
        book.setPages(134);
        book.setTitle("北京奥运");
        bookBaseDSTO.add(book);

        User user = new User();
        user.setUsername("name");
        userBaseDSTO.add(user);
    }

    /**
     * 批量添加
     */
    public void batchAdd() {
        List<Book> books = new ArrayList<>(2);
        Book book = new Book();
        book.setId(2);
        book.setPages(2);
        book.setTitle("book2");
        books.add(book);

        Book book1 = new Book();
        book1.setId(3);
        book1.setPages(3);
        book1.setTitle("book3");
        books.add(book1);

        bookBaseDSTO.batchAdd(books);
    }

    public void update(){
        Book book = new Book();
        book.setId(1);
        book.setPages(111);
        book.setTitle("book11");

        bookBaseDSTO.update(book);
    }

    public void delete(){
        Book book = new Book();
        book.setId(2);
        bookBaseDSTO.deleteByPrimaryKey(book.getId());
        Condition condition = new Condition();
        Condition.Criteria c = condition.createCriteria();
        c.andBetween("pages",117,123);
        Object b = bookBaseDSTO.max("pages", null);
        Object b1 = bookBaseDSTO.min("pages", null);
        List<Book> list = bookBaseDSTO.list(condition);
        Long count = bookBaseDSTO.count(condition);
    }

    public void get(){
        Condition condition = new Condition();
        Condition.Criteria c = condition.createCriteria();
        List<String> list = new ArrayList();
        list.add("book1");
        list.add("book2");
        list.toArray();
        c.andIn("title",list);
        //c.andEqualTo("title","book1");
        //c.andLessThanOrEqualTo("pages",115);
        //c.andIsNull("pages");
        bookBaseDSTO.get(condition);
    }


}
