package com.example.MyBookShopApp.controllers;

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
import java.util.*;

@Controller
public class MainPageController {

    private final BookService bookService;
    private final BooksRatingAndPopularityService booksRatingAndPopularityService;
    private final AuthorService authorService;

    private final GenreService genreService;

    @Autowired
    public MainPageController(BookService bookService, BooksRatingAndPopularityService booksRatingAndPopularityService,
                              GenreService genreService, AuthorService authorService) {
        this.bookService = bookService;
        this.booksRatingAndPopularityService = booksRatingAndPopularityService;
        this.genreService = genreService;
        this.authorService = authorService;
    }

    @ModelAttribute("booksListFull")
    public List<BookEntity> bookListFull() {
        return bookService.getBooksData();
    }

    @ModelAttribute("tagListMap")
    public Map<String, List<BookEntity>> tagListMap() {

        return bookService.getTagListMap();
    }

    @ModelAttribute("tagListMapLgSize")
    public Integer tagListMapLg() {
        List<BookEntity> bigList = bookService.getTagListMap().values().stream()
                .max(Comparator.comparing(List::size)).get();
        return bigList.size();
    }

    @ModelAttribute("tagListMapXsSize")
    public Integer tagListMapXs() {
        List<BookEntity> bigList = bookService.getTagListMap().values().stream()
                .min(Comparator.comparing(List::size)).get();
        return bigList.size();
    }

    @ModelAttribute("booksList")
    public List<BookEntity> bookList() {
        return bookService.getPageOfRecommendedBooks(0, 10).getContent();
    }

    @ModelAttribute("recommendedBooks")
    public List<BookEntity> recommendedBooks() {
        return bookService.getPageOfRecommendedBooks(0, 6).getContent();
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
        return authorService.converterListToMapWithAuthors(booksRatingAndPopularityService.getBookByRelevanceDesc(0, 6).getContent(), 0, 6);
    }

    @ModelAttribute("recentBooks")
    public List<BookEntity> recentAttrList() throws ParseException {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDateRecent = format.parse("2002-05-21");
        Date endDateRecent = format.parse(LocalDate.now().toString());
        return authorService.converterListToMapWithAuthors(bookService.findBookByPubDateBetween(fromDateRecent, endDateRecent, 0, 6).getContent(), 0, 6);
    }

    @GetMapping("/")
    public String mainPage() {

        return "index";

    }

    @GetMapping("/books/recommended")
    @ResponseBody
    public BooksPageDto getRecommendedPageSlider(@RequestParam("offset") Integer offset,
                                                 @RequestParam("limit") Integer limit) {
        return new BooksPageDto(authorService.converterListToMapWithAuthors(
                bookService.getPageOfRecommendedBooks(offset, limit).getContent(), 1, 6));
    }

    @GetMapping("/books/tags")
    public String getBookTag(@RequestParam(value = "tag") String tag, @RequestParam(value = "offset", required = false) Integer offset,
                             @RequestParam(value = "limit", required = false) Integer limit, Model model) {
        model.addAttribute("tagListMap", tagListMap());
        model.addAttribute("map", authorService.converterListToMapWithAuthors(
                bookService.findBookEntitiesByTagPage(tag, 0, 10).getContent(), 0, 10));
        model.addAttribute("TagName", tag);
        return "/tags/index.html";
    }

    @GetMapping("/books/page/tags")
    @ResponseBody
    public BooksPageDto getBookTagPage(@RequestParam(value = "tag") String tag, @RequestParam(value = "offset", required = false) Integer offset,
                                       @RequestParam(value = "limit", required = false) Integer limit) {


        return new BooksPageDto(authorService.converterListToMapWithAuthors(
                bookService.findBookEntitiesByTagPage(tag, offset + 1, limit).getContent(), offset + 1, limit));
    }


    @GetMapping("/documents")
    public String getDocuments() {
        return "/documents/index.html";
    }

    @GetMapping("/postponed")
    public String getPostponedPage() {
        return "/postponed.html";
    }

    @GetMapping("/cart")
    public String getCartPage() {
        return "/cart.html";
    }

    @GetMapping("/signin")
    public String getSingnInPage() {
        return "/signin.html";
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
