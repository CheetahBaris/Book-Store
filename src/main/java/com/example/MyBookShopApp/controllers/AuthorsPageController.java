package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.author.AuthorEntity;
import com.example.MyBookShopApp.data.book.BookEntity;
import com.example.MyBookShopApp.data.dto.BooksPageDto;
import com.example.MyBookShopApp.data.dto.SearchWordDto;
import com.example.MyBookShopApp.errs.BookstoreApiWrongParameterException;
import com.example.MyBookShopApp.services.AuthorService;
import com.example.MyBookShopApp.services.BookService;
import com.example.MyBookShopApp.services.BooksRatingAndPopularityService;
import com.example.MyBookShopApp.services.GenreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

@Controller
public class AuthorsPageController {


    private final BookService bookService;
    private final BooksRatingAndPopularityService booksRatingAndPopularityService;
    private final AuthorService authorService;

    private final GenreService genreService;

    @Autowired
    public AuthorsPageController(BookService bookService, BooksRatingAndPopularityService booksRatingAndPopularityService,
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

    @ModelAttribute("booksList")
    public List<BookEntity> bookList() throws BookstoreApiWrongParameterException {
        return bookService.getPageOfRecommendedBooks(0, 10).getContent();
    }

    @ModelAttribute("recommendedBooks")
    public List<BookEntity> recommendedBooks() throws BookstoreApiWrongParameterException {
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
        return authorService.converterBookListToListWithAuthors(booksRatingAndPopularityService.getBookByRelevanceDesc(0, 6).getContent(), 0, 6);
    }

    @ModelAttribute("recentBooks")
    public List<BookEntity> recentAttrList() throws ParseException, BookstoreApiWrongParameterException {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDateRecent = format.parse("2002-05-21");
        Date endDateRecent = format.parse(LocalDate.now().toString());
        return authorService.converterBookListToListWithAuthors(bookService.findBookByPubDateBetween(fromDateRecent, endDateRecent, 0, 6).getContent(), 0, 6);
    }

    @ModelAttribute("authorsMap")
    public Map<String, List<AuthorEntity>> authorsMap() {
        return authorService.getAuthorsMap();
    }

    @ModelAttribute("Author")
    public AuthorEntity getAuthor(String author) {
        return authorService.findAuthorEntitiesByName(author);
    }


    @GetMapping("/authors")
    public String authorsPage() {

        return "/authors/index.html";
    }

    @GetMapping("books/authors")
    public String getAuthorsSlug(@RequestParam(value = "author", required = false) String author, @RequestParam("offset") Integer offset,
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
    public String getAuthorEntitySlug(@RequestParam(value = "author", required = false) String author, Model model) {

        model.addAttribute("AuthorsBooksList",
                authorService.getBookEntitiesByAuthorName(author, 0, 10));
        model.addAttribute("Author", authorService.findAuthorEntitiesByName(author));

        return "/books/author.html";
    }
}