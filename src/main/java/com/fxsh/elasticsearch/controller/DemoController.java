package com.fxsh.elasticsearch.controller;

import com.fxsh.elasticsearch.dsto.BaseEsDSTO;
import com.fxsh.elasticsearch.pojo.Book;
import com.fxsh.elasticsearch.pojo.MyType;
import com.fxsh.elasticsearch.pojo.User;
import com.fxsh.elasticsearch.utils.Page;
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
    private BaseEsDSTO<Book> bookBaseDSTO;

    @Autowired
    private BaseEsDSTO<User> userBaseDSTO;

    @Autowired(required = false)
    private BaseEsDSTO<MyType> myTypeBaseDSTO;

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
            case 7:
                count();break;
            default:
                System.out.println("不正确的命令");break;
        }
    }

    public void add(){
        MyType myType = new MyType();
        Book book = new Book();
        book.setTitle("w shi book99");
        book.setPages(138777);
        myType.setAge(26891);
        myType.setDeleted(true);
        myType.setIp("192.168.32.127");
        myType.setName("贺淋亮11");
        myType.setKeyName("北京奥运会11");
        myType.setDou(98.0);
        myType.setBook(book);
        myType.setCreateDate(new Date());
        myTypeBaseDSTO.add(myType);
        System.out.println(myType);

        Condition condition = new Condition();
        Condition.Criteria c = condition.createCriteria();
        condition.setPage(new Page(0,2));
        c.andEqualTo("age",26891);
        myTypeBaseDSTO.get(condition);
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
        condition.setPage(new Page(1,2));
        c.andEqualTo("name","贺");
       // c.andEqualTo("pages",2);
       // c.andGreaterThan("pages",2);
//        List<String> list = new ArrayList();
//        list.add("book1");
//        list.add("book2");
//        list.toArray();
//        c.andIn("title",list);
        //c.andEqualTo("title","book1");
        //c.andLessThanOrEqualTo("pages",115);
        //c.andIsNull("pages");
        myTypeBaseDSTO.get(condition);
    }

    public void count(){
        Condition condition = new Condition();
        Condition.Criteria c = condition.createCriteria();
        condition.setPage(new Page(1,2));
        c.andEqualTo("title","book1");
        // c.andEqualTo("pages",2);
        c.andGreaterThan("pages",1);
        //bookBaseDSTO.count(condition);
        //bookBaseDSTO.max("pages",condition);
        bookBaseDSTO.min("pages",condition);
    }

}
