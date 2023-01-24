package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.annotations.CookieSearcher;
import com.example.MyBookShopApp.data.author.AuthorEntity;
import com.example.MyBookShopApp.data.book.BookEntity;
import com.example.MyBookShopApp.data.dto.BooksPageDto;
import com.example.MyBookShopApp.data.dto.SearchWordDto;
import com.example.MyBookShopApp.errs.BookstoreApiWrongParameterException;
import com.example.MyBookShopApp.services.BookstoreUserRegister;
import com.example.MyBookShopApp.security.jwt.JWTUtil;
import com.example.MyBookShopApp.services.AuthorService;
import com.example.MyBookShopApp.services.BookService;
import com.example.MyBookShopApp.services.BooksRatingAndPopularityService;
import com.example.MyBookShopApp.services.GenreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
public class AuthorsPageController {


    private final BookService bookService;
    private final BooksRatingAndPopularityService booksRatingAndPopularityService;
    private final AuthorService authorService;
    private final BookstoreUserRegister userRegister;
    private final JWTUtil jwtUtil;
    private final GenreService genreService;

    @Autowired
    public AuthorsPageController(BookService bookService, BooksRatingAndPopularityService booksRatingAndPopularityService,
                                 GenreService genreService, AuthorService authorService, BookstoreUserRegister userRegister, JWTUtil jwtUtil) {
        this.bookService = bookService;
        this.booksRatingAndPopularityService = booksRatingAndPopularityService;
        this.genreService = genreService;
        this.authorService = authorService;
        this.userRegister = userRegister;
        this.jwtUtil = jwtUtil;
    }



    @ModelAttribute("searchWordDto")
    public SearchWordDto searchWordDto() {
        return new SearchWordDto();
    }



    @GetMapping("/authors")
    @CookieSearcher
    public String authorsPage(@CookieValue(value = "cartContents", required = false) String cartContents,
                              @CookieValue(value = "postponedContents", required = false) String postponedContents,
                              @CookieValue(value = "token", required = false) String token,Model model) {

        model.addAttribute("authorsMap", authorService.getAuthorsMap());


        return "/authors/index.html";


    }

    @GetMapping("books/authors")
    @CookieSearcher
    public String getAuthorsSlug(@CookieValue(value = "cartContents", required = false) String cartContents,
                                 @CookieValue(value = "postponedContents", required = false) String postponedContents,
                                 @CookieValue(value = "token", required = false) String token,
                                 @RequestParam(value = "author", required = false) String author, @RequestParam("offset") Integer offset,
                                 @RequestParam("limit") Integer limit, Model model) {


        model.addAttribute("AuthorsBooksList",
                authorService.getBookEntitiesByAuthorName(author, offset, limit));
        model.addAttribute("Author", authorService.findAuthorEntitiesByName(author));
        model.addAttribute("AuthorsFullBookListInteger", authorService.getGetBookEntitiesByAuthorNameSize());

        return "/authors/slug.html";
    }

    @GetMapping("books/page/authors")
    @ResponseBody
    public BooksPageDto getAuthorsPage(@RequestParam(value = "author", required = false) String author,
                                       @RequestParam(value = "offset", required = false) Integer offset,
                                       @RequestParam(value = "limit", required = false) Integer limit) {

        return new BooksPageDto(authorService.getBookEntitiesByAuthorName(author, offset + 1, limit));

    }

    @GetMapping("books/authorEntity")
    @CookieSearcher
    public String getAuthorEntitySlug(@CookieValue(value = "cartContents", required = false) String cartContents,
                                      @CookieValue(value = "postponedContents", required = false) String postponedContents,
                                      @CookieValue(value = "token", required = false) String token,
                                      @RequestParam(value = "author", required = false) String author,
                                      Model model) {


        model.addAttribute("AuthorsBooksList",
                authorService.getBookEntitiesByAuthorName(author, 0, 10));
        model.addAttribute("Author", authorService.findAuthorEntitiesByName(author));

        return "/books/author.html";
    }
}
