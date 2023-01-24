package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.annotations.CookieSearcher;
import com.example.MyBookShopApp.errs.BookstoreApiWrongParameterException;
import com.example.MyBookShopApp.services.BookstoreUserRegister;
import com.example.MyBookShopApp.security.jwt.JWTUtil;
import com.example.MyBookShopApp.services.AuthorService;
import com.example.MyBookShopApp.data.book.BookEntity;
import com.example.MyBookShopApp.services.BookService;
import com.example.MyBookShopApp.services.BooksRatingAndPopularityService;
import com.example.MyBookShopApp.data.dto.BooksPageDto;
import com.example.MyBookShopApp.data.dto.SearchWordDto;

import com.example.MyBookShopApp.services.GenreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.beans.BeanProperty;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
public class MainPageController {

    private final BookService bookService;
    private final BooksRatingAndPopularityService booksRatingAndPopularityService;
    private final AuthorService authorService;

    private final BookstoreUserRegister userRegister;
    private final GenreService genreService;
    private final JWTUtil jwtUtil;


    @Autowired
    public MainPageController(BookService bookService, BooksRatingAndPopularityService booksRatingAndPopularityService,
                              GenreService genreService, AuthorService authorService,
                              BookstoreUserRegister userRegister, JWTUtil jwtUtil) {
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


    @GetMapping("/")
    @CookieSearcher
    public String mainPage(@CookieValue(value = "cartContents", required = false) String cartContents,
                           @CookieValue(value = "postponedContents", required = false) String postponedContents,
                           @CookieValue(value = "token", required = false) String token, Model model ) throws BookstoreApiWrongParameterException {


        model.addAttribute("popularBooks",booksRatingAndPopularityService.getBookByRelevanceDesc( 0, 6));
        model.addAttribute("recentBooks",bookService.findBookByPubDateBetween(LocalDate.parse(LocalDate.parse("2002-05-21").format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))),LocalDate.parse(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))),0, 6));
        model.addAttribute("tagListMapXsSize", bookService.getTagListMap().values().stream().min(Comparator.comparing(List::size)).get().size());
        model.addAttribute("tagListMapLgSize", bookService.getTagListMap().values().stream().max(Comparator.comparing(List::size)).get().size());
        model.addAttribute("tagListMap",bookService.getTagListMap());

        return "index";

    }

    @GetMapping("/books/recommended")
    @ResponseBody
    public BooksPageDto getRecommendedPageSlider(@CookieValue(value = "cartContents", required = false) String cartContents,
                                                 @CookieValue(value = "postponedContents", required = false) String postponedContents,
                                                 @RequestParam("offset") Integer offset,
                                                 @RequestParam("limit") Integer limit) throws BookstoreApiWrongParameterException {
        String[]  cookiePostponedSlugs = postponedContents!=null ? (postponedContents.isEmpty()? null : postponedContents.split("/")) : null;
        String[] cookieCartSlugs = cartContents!=null? (cartContents.isEmpty()?null : cartContents.split("/")):null;

        return new BooksPageDto(authorService.converterBookListToListWithAuthors(
                bookService.getPageOfRecommendedBooks(cookiePostponedSlugs,cookieCartSlugs,offset, limit), 1, 6));
    }

    @GetMapping("/books/tags")
    @CookieSearcher
    public String getBookTag( @CookieValue(value = "cartContents", required = false) String cartContents,
                              @CookieValue(value = "postponedContents", required = false) String postponedContents,
                              @CookieValue(value = "token", required = false) String token,
                              @RequestParam(value = "tag") String tag,
                              Model model) throws BookstoreApiWrongParameterException {

        model.addAttribute("tagListMap", bookService.getTagListMap());
        model.addAttribute("map", authorService.converterBookListToListWithAuthors(
                bookService.findBookEntitiesByTagPage(tag, 0, 10).getContent(), 0, 10));
        model.addAttribute("TagName", tag);
          return "/tags/index.html";
    }

    @GetMapping("/books/page/tags")
    @ResponseBody
    public BooksPageDto getBookTagPage(@RequestParam(value = "tag") String tag, @RequestParam(value = "offset", required = false) Integer offset,
                                       @RequestParam(value = "limit", required = false) Integer limit) throws BookstoreApiWrongParameterException {

        return new BooksPageDto(authorService.converterBookListToListWithAuthors(
                bookService.findBookEntitiesByTagPage(tag, offset + 1, limit).getContent(), offset + 1, limit));
    }




    @GetMapping("/documents")
    @CookieSearcher
    public String getDocuments(@CookieValue(value = "cartContents", required = false) String cartContents,
                               @CookieValue(value = "postponedContents", required = false) String postponedContents,
                               @CookieValue(value = "token", required = false) String token,Model model) {
        return "/documents/index.html";
    }


    @GetMapping("/about")
    @CookieSearcher
    public String getAboutPage(@CookieValue(value = "cartContents", required = false) String cartContents,
                               @CookieValue(value = "postponedContents", required = false) String postponedContents,
                               @CookieValue(value = "token", required = false) String token,Model model) {
        return "/about.html";
    }

    @GetMapping("/faq")
    @CookieSearcher
    public String getFAQPage(@CookieValue(value = "cartContents", required = false) String cartContents,
                             @CookieValue(value = "postponedContents", required = false) String postponedContents,
                             @CookieValue(value = "token", required = false) String token,Model model) {
        return "faq.html";
    }

    @GetMapping("/contacts")
    @CookieSearcher
    public String getContactsPage(@CookieValue(value = "cartContents", required = false) String cartContents,
                                  @CookieValue(value = "postponedContents", required = false) String postponedContents,
                                  @CookieValue(value = "token", required = false) String token,Model model) {
        return "contacts.html";
    }

}
