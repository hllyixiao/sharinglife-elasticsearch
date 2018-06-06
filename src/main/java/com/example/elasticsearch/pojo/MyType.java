package com.example.elasticsearch.pojo;

import java.io.Serializable;
import java.util.Date;

/**
 * @author hell
 * @date 2018/6/6
 */
public class MyType implements Serializable {
    /**
     * text
     */
    private String name;
    /**
     * keyword
     */
    private String keyName;
    /**
     * integer
     */
    private Integer age;
    /**
     * boolean
     */
    private Boolean deleted;
    /**
     * ip
     */
    private String ip;
    /**
     * double
     */
    private double dou;

    private Date createDate;

    private Book book;

    public MyType() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public double getDou() {
        return dou;
    }

    public void setDou(double dou) {
        this.dou = dou;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    @Override
    public String toString() {
        return "MyType{" +
                "name='" + name + '\'' +
                ", keyName='" + keyName + '\'' +
                ", age=" + age +
                ", deleted=" + deleted +
                ", ip='" + ip + '\'' +
                ", dou=" + dou +
                ", createDate=" + createDate +
                ", book=" + book +
                '}';
    }
}
