package com.example.MyBookShopApp.data.dto;

import com.example.MyBookShopApp.data.book.BookEntity;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;

@Controller
public class BooksPageDto {

    private Integer count;
    private Integer countMap;

    private List<BookEntity> books;


    public BooksPageDto(List<BookEntity> books) {
        this.count = books.size();
        this.books = books;
    }



    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<BookEntity> getBooks() {
        return books;
    }

    public void setBooks(List<BookEntity> books) {
        this.books = books;
    }

    public Integer getCountMap() {
        return countMap;
    }

    public void setCountMap(Integer countMap) {
        this.countMap = countMap;
    }

}
