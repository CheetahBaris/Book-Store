package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.annotations.CookieSearcher;
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
public class PopularBooksPageController {

    private final BookService bookService;
    private final BooksRatingAndPopularityService booksRatingAndPopularityService;
    private final AuthorService authorService;
    private final BookstoreUserRegister userRegister;
    private final JWTUtil jwtUtil;
    private final GenreService genreService;

    @Autowired
    public PopularBooksPageController(BookService bookService,
                                      BooksRatingAndPopularityService booksRatingAndPopularityService,
                                      GenreService genreService,
                                      AuthorService authorService,
                                      BookstoreUserRegister userRegister,
                                      JWTUtil jwtUtil) {
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



    @GetMapping("/books/popular")
    @CookieSearcher
    public String getBookPopular(@CookieValue(value = "cartContents", required = false) String cartContents,
                                 @CookieValue(value = "postponedContents", required = false) String postponedContents,
                                 @CookieValue(value = "token", required = false) String token,
                                 @RequestParam(value = "offset", required = false) Integer offset,
                                 @RequestParam(value = "limit", required = false) Integer limit,
                                 Model model) {


        model.addAttribute("popularBooks",
                authorService.converterBookListToListWithAuthors(booksRatingAndPopularityService.getBookByRelevanceDesc(0, 10).getContent(), 0, 10));

        return "/books/popular.html";
    }

    @GetMapping("/books/page/popular")
    @ResponseBody
    public BooksPageDto getBookPopularPage(@RequestParam(value = "offset", required = false) Integer offset,
                                           @RequestParam(value = "limit", required = false) Integer limit) {


        return new BooksPageDto(authorService.converterBookListToListWithAuthors(
                booksRatingAndPopularityService.getBookByRelevanceDesc(offset + 1, limit).getContent(), offset + 1, limit));
    }


}
