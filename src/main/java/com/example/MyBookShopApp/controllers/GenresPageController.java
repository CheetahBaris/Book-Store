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
public class GenresPageController {

    private final BookService bookService;
    private final BooksRatingAndPopularityService booksRatingAndPopularityService;
    private final AuthorService authorService;
    private final BookstoreUserRegister userRegister;
    private final JWTUtil jwtUtil;
    private final GenreService genreService;

    @Autowired
    public GenresPageController(BookService bookService, BooksRatingAndPopularityService booksRatingAndPopularityService,
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



    @GetMapping("/genres")
    @CookieSearcher
    public String getGenres(@CookieValue(value = "cartContents", required = false) String cartContents,
                            @CookieValue(value = "postponedContents", required = false) String postponedContents,
                            @CookieValue(value = "token", required = false) String token,  Model model) {

        model.addAttribute("GenresParentList", genreService.findGenreEntitiesByParentId(0L));
        model.addAttribute("AllGenresList", genreService.getAllGenres());


        return "/genres/index.html";
    }

    @GetMapping("books/genres")
    @CookieSearcher
    public String getBooksByGenreSlug(@CookieValue(value = "cartContents", required = false) String cartContents,
                                      @CookieValue(value = "postponedContents", required = false) String postponedContents,
                                      @CookieValue(value = "token", required = false) String token,
                                      @RequestParam(value = "genre", required = false) String genre,Model model) {


        model.addAttribute("GenresList", authorService.converterBookListToListWithAuthors(
                genreService.getBooksPageByGenre(genre, 0, 10), 0, 10));
        model.addAttribute("GenreTag", genre);


        return "/genres/slug.html";
    }

    @GetMapping("books/page/genres")
    @ResponseBody
    public BooksPageDto getBooksByGenre(@RequestParam(value = "genre", required = false) String genre,
                                        @RequestParam(value = "offset", required = false) Integer offset,
                                        @RequestParam(value = "limit", required = false) Integer limit) {


        return new BooksPageDto(authorService.converterBookListToListWithAuthors(genreService.getBooksPageByGenre(genre, offset + 1, limit), offset + 1, limit));
    }
}
