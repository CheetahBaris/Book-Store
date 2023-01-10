package com.example.MyBookShopApp.data.dto;


import org.springframework.stereotype.Component;

@Component
public class ChangeBookStarsDto {
    private String book;
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getBook() {
        return book;
    }

    public void setBook(String book) {
        this.book = book;
    }
}
