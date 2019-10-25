package com.fxl.guetcoursetable.booksearch;

/**
 * Created by FXL-PC on 2017/2/19.
 */

public class BookDetailInfo {
    private String callNumber;
    private String bookNum;
    private String libeary;
    private String state;
    private String borrowDate;
    private String returnDate;
    private String bookClass;

    public String getCallNumber() {
        return callNumber;
    }

    public void setCallNumber(String callNumber) {
        this.callNumber = callNumber;
    }

    public String getBookNum() {
        return bookNum;
    }

    public void setBookNum(String bookNum) {
        this.bookNum = bookNum;
    }

    public String getLibeary() {
        return libeary;
    }

    public void setLibeary(String libeary) {
        this.libeary = libeary;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(String borrowDate) {
        this.borrowDate = borrowDate;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }

    public String getBookClass() {
        return bookClass;
    }

    public void setBookClass(String bookClass) {
        this.bookClass = bookClass;
    }
}
