package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.book.BookEntity;
import com.example.MyBookShopApp.data.dto.BooksPageDto;
import com.example.MyBookShopApp.data.dto.SearchWordDto;
import com.example.MyBookShopApp.errs.BookstoreApiWrongParameterException;
import com.example.MyBookShopApp.security.BookstoreUserRegister;
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
        return authorService.converterBookListToListWithAuthors(
                booksRatingAndPopularityService.getBookByRelevanceDesc(0, 6).getContent(), 0, 6);
    }

    @ModelAttribute("recentBooks")
    public List<BookEntity> recentAttrList() throws ParseException, BookstoreApiWrongParameterException {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDateRecent = format.parse("2002-05-21");
        Date endDateRecent = format.parse(LocalDate.now().toString());
        return authorService.converterBookListToListWithAuthors(bookService.findBookByPubDateBetween(fromDateRecent, endDateRecent, 0, 6).getContent(), 0, 6);
    }

    @GetMapping("/genres")
    public String getGenres(@CookieValue(value = "token", required = false) String token,@CookieValue(value = "cartContents", required = false) String cartContents,
                            @CookieValue(value = "postponedContents", required = false) String postponedContents,  Model model) {
        postponedContents = postponedContents.isEmpty()? null: postponedContents;
        cartContents = cartContents.isEmpty()? null: cartContents;
        String[]  cookiePostponedSlugs = postponedContents!=null ?postponedContents.split("/"):null;
        String[] cookieCartSlugs = cartContents!=null?cartContents.split("/"):null;

        model.addAttribute("postponedSize",cookiePostponedSlugs!=null?cookiePostponedSlugs.length:null);
        model.addAttribute("cartSize",cookieCartSlugs!=null?cookieCartSlugs.length:null);

        model.addAttribute("GenresParentList", genreService.findGenreEntitiesByParentId(0L));
        model.addAttribute("AllGenresList", genreService.getAllGenres());

        if(token != null){

            model.addAttribute("curUsrStatus","authorized");
            model.addAttribute("curUsr",userRegister.getCurrentUser());
        }else {
            model.addAttribute("curUsrStatus","unauthorized");
            model.addAttribute("curUsr",null);
        }
        return "/genres/index.html";
    }

    @GetMapping("books/genres")
    public String getBooksByGenreSlug(@RequestParam(value = "genre", required = false) String genre,
                                      @CookieValue(value = "token", required = false) String token,@CookieValue(value = "cartContents", required = false) String cartContents,
                                      @CookieValue(value = "postponedContents", required = false) String postponedContents, Model model) {

        postponedContents = postponedContents.isEmpty()? null: postponedContents;
        cartContents = cartContents.isEmpty()? null: cartContents;

        model.addAttribute("GenresList", authorService.converterBookListToListWithAuthors(
                genreService.getBooksPageByGenre(genre, 0, 10), 0, 10));
        model.addAttribute("GenreTag", genre);
        String[]  cookiePostponedSlugs = postponedContents!=null ?postponedContents.split("/"):null;
        String[] cookieCartSlugs = cartContents!=null?cartContents.split("/"):null;

        model.addAttribute("postponedSize",cookiePostponedSlugs!=null?cookiePostponedSlugs.length:null);
        model.addAttribute("cartSize",cookieCartSlugs!=null?cookieCartSlugs.length:null);
        if(token != null){

            model.addAttribute("curUsrStatus","authorized");
            model.addAttribute("curUsr",userRegister.getCurrentUser());
        }else {
            model.addAttribute("curUsrStatus","unauthorized");
            model.addAttribute("curUsr",null);
        }

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
