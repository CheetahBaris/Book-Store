package com.example.MyBookShopApp.controllers;

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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    @ModelAttribute("booksListFull")
    public List<BookEntity> bookListFull() {
        return bookService.getBooksData();
    }

    @ModelAttribute("tagListMap")
    public Map<String, List<BookEntity>> tagListMap() throws BookstoreApiWrongParameterException {

        return bookService.getTagListMap();
    }

    @ModelAttribute("tagListMapLgSize")
    public Integer tagListMapLg() throws BookstoreApiWrongParameterException {
        List<BookEntity> bigList = bookService.getTagListMap().values().stream()
                .max(Comparator.comparing(List::size)).get();
        return bigList.size();
    }

    @ModelAttribute("tagListMapXsSize")
    public Integer tagListMapXs() throws BookstoreApiWrongParameterException {
        List<BookEntity> bigList = bookService.getTagListMap().values().stream()
                .min(Comparator.comparing(List::size)).get();
        return bigList.size();
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

    @ModelAttribute("popularBooks")
    public List<BookEntity> popularAttrList() {
        return authorService.converterBookListToListWithAuthors(booksRatingAndPopularityService.getBookByRelevanceDesc(0, 6).getContent(), 0, 6);
    }

    @ModelAttribute("recentBooks")
    public List<BookEntity> recentAttrList() throws ParseException, BookstoreApiWrongParameterException {
        LocalDate fromDateRecent = LocalDate.parse(LocalDate.parse("2002-05-21").format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        LocalDate endDateRecent =LocalDate.parse(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        return authorService.converterBookListToListWithAuthors(bookService.findBookByPubDateBetween(fromDateRecent, endDateRecent, 0, 6).getContent(), 0, 6);
    }
    @GetMapping("/")
    public String mainPage(@CookieValue(value = "cartContents", required = false) String cartContents,@CookieValue(value = "postponedContents", required = false) String postponedContents,@CookieValue(value = "token", required = false) String token, Model model) throws BookstoreApiWrongParameterException {

        String[]  cookiePostponedSlugs = postponedContents!=null ? (postponedContents.isEmpty()? null : postponedContents.split("/")) : null;
        String[] cookieCartSlugs = cartContents!=null? (cartContents.isEmpty()?null : cartContents.split("/")):null;

        model.addAttribute("recommendedBooks",bookService.getPageOfRecommendedBooks(cookiePostponedSlugs,cookieCartSlugs,0, 6));
        model.addAttribute("popularBooks",booksRatingAndPopularityService.getBookByRelevanceDesc( 0, 6));
        model.addAttribute("recentBooks",bookService.findBookByPubDateBetween(LocalDate.parse(LocalDate.parse("2002-05-21").format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))),LocalDate.parse(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))),0, 6));

        model.addAttribute("postponedSize",cookiePostponedSlugs!=null?cookiePostponedSlugs.length:null);
        model.addAttribute("cartSize",cookieCartSlugs!=null?cookieCartSlugs.length:null);


        if(token != null){

            model.addAttribute("curUsrStatus","authorized");
            model.addAttribute("curUsr",userRegister.getCurrentUser());

        }else {

            model.addAttribute("curUsrStatus","unauthorized");
            model.addAttribute("curUsr",null);

        }

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
    public String getBookTag(@RequestParam(value = "tag") String tag, @RequestParam(value = "offset", required = false) Integer offset,
                             @RequestParam(value = "limit", required = false) Integer limit,
                             @CookieValue(value = "token", required = false) String token,
                             @CookieValue(value = "cartContents", required = false) String cartContents,
                             @CookieValue(value = "postponedContents", required = false) String postponedContents, Model model) throws BookstoreApiWrongParameterException {

        String[]  cookiePostponedSlugs = postponedContents!=null ? (postponedContents.isEmpty()? null : postponedContents.split("/")) : null;
        String[] cookieCartSlugs = cartContents!=null? (cartContents.isEmpty()?null : cartContents.split("/")):null;


        model.addAttribute("postponedSize",cookiePostponedSlugs!=null?cookiePostponedSlugs.length:null);
        model.addAttribute("cartSize",cookieCartSlugs!=null?cookieCartSlugs.length:null);
        model.addAttribute("tagListMap", tagListMap());

        model.addAttribute("map", authorService.converterBookListToListWithAuthors(
                bookService.findBookEntitiesByTagPage(tag, 0, 10).getContent(), 0, 10));
        model.addAttribute("TagName", tag);
        if(token != null){

            model.addAttribute("curUsrStatus","authorized");
            model.addAttribute("curUsr",userRegister.getCurrentUser());
        }else {
            model.addAttribute("curUsrStatus","unauthorized");
            model.addAttribute("curUsr",null);
        }
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
    public String getDocuments() {
        return "/documents/index.html";
    }


    @GetMapping("/about")
    public String getAboutPage() {
        return "/about.html";
    }

    @GetMapping("/faq")
    public String getFAQPage() {
        return "faq.html";
    }

    @GetMapping("/contacts")
    public String getContactsPage() {
        return "contacts.html";
    }

}
