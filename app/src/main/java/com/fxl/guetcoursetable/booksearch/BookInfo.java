package com.fxl.guetcoursetable.booksearch;

/**
 * Created by FXL-PC on 2017/2/19.
 */

public class BookInfo {
    private String detailUrl;
    private String name;
    private String author;
    private String publisher;
    private String publishTime;
    private String isbn;
    private String callNumber;

    public String getDetailUrl() {
        return detailUrl;
    }

    public void setId(String detailUrl) {
        this.detailUrl = detailUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }


    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getCallNumber() {
        return callNumber;
    }

    public void setCallNumber(String callNumber) {
        this.callNumber = callNumber;
    }
}
