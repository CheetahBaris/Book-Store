package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.annotations.CookieSearcher;
import com.example.MyBookShopApp.data.book.BookEntity;
import com.example.MyBookShopApp.data.dto.BooksPageDto;
import com.example.MyBookShopApp.data.dto.SearchWordDto;
import com.example.MyBookShopApp.errs.BookstoreApiWrongParameterException;
import com.example.MyBookShopApp.errs.EmptySearchException;
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
public class SearchPageController {

    private final BookService bookService;
    private final BooksRatingAndPopularityService booksRatingAndPopularityService;
    private final AuthorService authorService;
    private final BookstoreUserRegister userRegister;
    private final JWTUtil jwtUtil;
    private final GenreService genreService;

    @Autowired
    public SearchPageController(BookService bookService, BooksRatingAndPopularityService booksRatingAndPopularityService,
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

    @ModelAttribute("searchResults")
    public List<BookEntity> searchResults() {
        return new ArrayList<>();
    }

    @ModelAttribute("searchResultsFull")
    public List<BookEntity> searchResultsFull() {
        return new ArrayList<>();
    }



    @GetMapping(value = {"/search", "/search/{searchWord}"})
    @CookieSearcher
    public String getSearchResult(@CookieValue(value = "cartContents", required = false) String cartContents,
                                  @CookieValue(value = "postponedContents", required = false) String postponedContents,
                                  @CookieValue(value = "token", required = false) String token,
                                  @PathVariable(value = "searchWord", required = false) SearchWordDto searchWordDto,
                                  Model model) throws BookstoreApiWrongParameterException, EmptySearchException {


        if(searchWordDto != null){

            model.addAttribute("searchWordDto", searchWordDto);
            model.addAttribute("searchResults",
                    authorService.converterBookListToListWithAuthors(bookService.getPageOfSearchResultBooks(searchWordDto.getExample(), 0, 5).getContent(), 0, 5));
            model.addAttribute("searchResultsFullLong",
                    bookService.getPageOfSearchResultBooks(searchWordDto.getExample(), 0, 10).getTotalElements());

            return "/search/index";

        }else {
            throw new EmptySearchException("Поиск по null невозможен");
        }
    }

    @GetMapping("/search/page/{searchWord}")
    @ResponseBody
    public BooksPageDto getNextSearchPage(@RequestParam("offset") Integer offset,
                                          @RequestParam("limit") Integer limit,
                                          @PathVariable(value = "searchWord", required = false) SearchWordDto searchWordDto) throws BookstoreApiWrongParameterException {
        return new BooksPageDto(authorService.converterBookListToListWithAuthors(
                bookService.getPageOfSearchResultBooks(searchWordDto.getExample(), offset + 1, limit).getContent(), offset + 1, limit));
    }

}
