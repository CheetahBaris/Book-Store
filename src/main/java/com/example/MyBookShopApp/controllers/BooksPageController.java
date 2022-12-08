package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.book.BookEntity;
import com.example.MyBookShopApp.data.book.BookService;
import com.example.MyBookShopApp.data.book.BooksEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@Controller
 public class BooksPageController {

    private final BookService bookService;
    @Autowired
    public BooksPageController(BookService bookService) {
        this.bookService = bookService;
    }

    @ModelAttribute("booksList")
    public List<BooksEntity> bookList(){
        return bookService.getAllBooks();
    }
    @GetMapping("/books/recent")
    public String getBookRecentPage(){

        return "/books/recent.html";
    }
    @GetMapping("/books/popular")
    public String getBookPopularPage(){
        return "/books/popular.html";
    }

}
