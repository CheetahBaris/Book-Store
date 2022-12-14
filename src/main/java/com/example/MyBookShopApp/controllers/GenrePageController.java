package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.book.BookEntity;
import com.example.MyBookShopApp.data.book.services.BookService;
import com.example.MyBookShopApp.data.dto.SearchWordDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@Controller
public class GenrePageController {

    private final BookService bookService;
    @Autowired
    public GenrePageController(BookService bookService) {
        this.bookService = bookService;
    }

    @ModelAttribute("searchWordDto")
    public SearchWordDto searchWordDto() {
        return new SearchWordDto();
    }
    @ModelAttribute("booksListFull")
    public List<BookEntity> bookListFull(){
        return bookService.getBooksData();
    }
    @ModelAttribute("booksList")
    public List<BookEntity> bookList(){
        return bookService.getPageOfRecommendedBooks(0, 20).getContent();
    }

    @GetMapping("/genres")
    public String getGenres(){
        return "/genres/index.html";
    }

}
