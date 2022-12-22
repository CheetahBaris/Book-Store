package com.example.MyBookShopApp.data.dto;

import com.example.MyBookShopApp.data.book.BookEntity;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class BooksPageDto {

    private Integer count;
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
}
