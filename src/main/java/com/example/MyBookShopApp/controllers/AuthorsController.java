package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.author.AuthorEntity;
import com.example.MyBookShopApp.data.author.AuthorService;
import com.example.MyBookShopApp.data.book.BookEntity;
import com.example.MyBookShopApp.data.book.services.BookService;
import com.example.MyBookShopApp.data.dto.SearchWordDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;
import java.util.Map;

@Controller
 public class AuthorsController {

    private final AuthorService authorService;
    private final BookService bookService;


    @Autowired
    public AuthorsController(AuthorService authorService, BookService bookService) {
        this.authorService = authorService;
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

    @ModelAttribute("authorsMap")
    public Map<String,List<AuthorEntity>> authorsMap(){
        return authorService.getAuthorsMap();
    }

    @GetMapping("/authors")
    public String authorsPage(){
        return "/authors/index";
    }
}
