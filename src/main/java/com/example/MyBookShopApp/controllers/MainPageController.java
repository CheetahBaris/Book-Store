package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.author.AuthorEntity;
import com.example.MyBookShopApp.data.author.services.AuthorService;
import com.example.MyBookShopApp.data.book.BookEntity;
import com.example.MyBookShopApp.data.book.services.BookService;
import com.example.MyBookShopApp.data.book.services.BooksRatingAndPopularityService;
import com.example.MyBookShopApp.data.dto.BooksPageDto;
import com.example.MyBookShopApp.data.dto.SearchWordDto;

import com.example.MyBookShopApp.data.genre.serices.GenreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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
    public List<BookEntity> bookListFull(){
        return bookService.getBooksData();
    }
    @ModelAttribute("tagListMap")
    public Map<String,List<BookEntity>> tagListMap(){

         return bookService.getTagListMap();
    }
    @ModelAttribute("tagListMapLgSize")
    public Integer tagListMapLg(){
        List<BookEntity> bigList = bookService.getTagListMap().values().stream()
                .max(Comparator.comparing(List::size)).get();
        return bigList.size();
    }
    @ModelAttribute("tagListMapXsSize")
    public Integer tagListMapXs(){
        List<BookEntity> bigList = bookService.getTagListMap().values().stream()
                .min(Comparator.comparing(List::size)).get();
        return bigList.size();
    }

    @ModelAttribute("booksList")
    public List<BookEntity> bookList(){
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
        return  booksRatingAndPopularityService.getBookByRelevanceDesc(0,6).getContent();
    }
    @ModelAttribute("Author")
    public AuthorEntity getAuthor(String author) {
         return authorService.findAuthorEntitiesByName(author);
    }
    @ModelAttribute("recentBooks")
    public List<BookEntity> recentAttrList() throws ParseException {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDateRecent = format.parse("2002-05-21");
        Date endDateRecent =  format.parse(LocalDate.now().toString());
        return bookService.findBookByPubDateBetween(fromDateRecent,endDateRecent,0, 6).getContent();
    }

    @ModelAttribute("authorsMap")
    public Map<String,List<AuthorEntity>> authorsMap(){
        return authorService.getAuthorsMap();
    }
    @GetMapping("/")
    public String mainPage() {

        return "index";

    }

    @GetMapping("/books/recommended")
    @ResponseBody
    public BooksPageDto getRecommendedPageSlider(@RequestParam("offset") Integer offset,
                                     @RequestParam("limit") Integer limit) {
        return new BooksPageDto(bookService.getPageOfRecommendedBooks(offset, limit).getContent());
    }

    @GetMapping("/books/popular")
    public String getBookPopular(@RequestParam(value = "offset", required = false) Integer offset,
                                 @RequestParam(value = "limit", required = false) Integer limit, Model model){


        model.addAttribute("popularBooks",
                booksRatingAndPopularityService.getBookByRelevanceDesc(0,10));


        return "/books/popular.html";
    }
    @GetMapping("/books/page/popular")
    @ResponseBody
    public BooksPageDto getBookPopularPage(@RequestParam(value = "offset", required = false) Integer offset,
                                           @RequestParam(value = "limit", required = false) Integer limit){


        return new BooksPageDto(booksRatingAndPopularityService.getBookByRelevanceDesc(offset+1,limit).getContent());
    }

    @GetMapping("/books/page/recent")
    @ResponseBody
    public BooksPageDto getRecentPage(@RequestParam(value = "from", required = false) String from,
                                       @RequestParam(value ="to", required = false) String to,
                                       @RequestParam(value ="offset", required = false) Integer offset,
                                       @RequestParam(value ="limit", required = false) Integer limit)
                                        throws ParseException {

        String[] fromArr = from.split("\\.");
        String[] toArr = to.split("\\.");
        String newFrom ="";
        String newTo ="";

        for(int i= fromArr.length-1;i>=0;i--){

            if(i!=0){

                newFrom+=fromArr[i]+'-';
                newTo+=toArr[i]+'-';
            }else {

                newFrom+=fromArr[i];
                newTo+=toArr[i];
            }
        }

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDateRecent = format.parse(newFrom);
        Date endDateRecent =  format.parse(newTo);

        return new BooksPageDto(bookService.findBookByPubDateBetween(fromDateRecent,endDateRecent,offset+1, limit).getContent());
    }
    @GetMapping("/books/recent")
    public String getBookRecentPage(@RequestParam(value = "from", required = false) String from,
                                    @RequestParam(value ="to", required = false) String to,
                                    @RequestParam(value ="offset", required = false) Integer offset,
                                    @RequestParam(value ="limit", required = false) Integer limit) {

         return "/books/recent.html";
    }


    @GetMapping(value = {"/search", "/search/{searchWord}"})
    public String getSearchResult(@PathVariable(value = "searchWord", required = false) SearchWordDto searchWordDto,
                                  Model model) {
        model.addAttribute("searchWordDto", searchWordDto);
        model.addAttribute("searchResults",
                bookService.getPageOfSearchResultBooks(searchWordDto.getExample(), 0, 5).getContent());
        model.addAttribute("searchResultsFullLong" ,
                bookService.getPageOfSearchResultBooks(searchWordDto.getExample(), 0, 10).getTotalElements());
        return "/search/index";
    }

    @GetMapping("/search/page/{searchWord}")
    @ResponseBody
    public BooksPageDto getNextSearchPage(@RequestParam("offset") Integer offset,
                                          @RequestParam("limit") Integer limit,
                                          @PathVariable(value = "searchWord", required = false) SearchWordDto searchWordDto) {
        return new BooksPageDto(bookService.getPageOfSearchResultBooks(searchWordDto.getExample(), offset+1, limit).getContent());
    }

    @GetMapping("/books/tags")
    public String getBookTag(@RequestParam(value = "tag") String tag, @RequestParam(value = "offset", required = false) Integer offset,
                                 @RequestParam(value = "limit", required = false) Integer limit ,Model model){
        model.addAttribute("tagListMap", tagListMap());
        model.addAttribute("map", bookService.findBookEntitiesByTagPage(tag, 0, 10).getContent());
        model.addAttribute("TagName",tag);
        return "/tags/index.html";
    }
    @GetMapping("/books/page/tags")
    @ResponseBody
    public BooksPageDto getBookTagPage(@RequestParam(value = "tag") String tag, @RequestParam(value = "offset", required = false) Integer offset,
                                           @RequestParam(value = "limit", required = false) Integer limit){


        return new BooksPageDto(bookService.findBookEntitiesByTagPage(tag,offset+1,limit).getContent());
    }
    @GetMapping("/genres")
    public String getGenres(Model model){

        model.addAttribute("GenresParentList", genreService.findGenreEntitiesByParentId(0L));
        model.addAttribute("AllGenresList", genreService.getAllGenres());

        return "/genres/index.html";
    }
    @GetMapping("books/genres")
    public String getBooksByGenreSlug(@RequestParam(value = "genre", required = false) String genre, Model model){


        model.addAttribute("GenresList", genreService.getBooksPageByGenre(genre,0,10));
        model.addAttribute("GenreTag", genre);

        return "/genres/slug.html";
    }
    @GetMapping("books/page/genres")
    @ResponseBody
    public BooksPageDto getBooksByGenre(@RequestParam(value = "genre", required = false) String genre,
                                        @RequestParam(value = "offset", required = false) Integer offset,
                                  @RequestParam(value = "limit", required = false) Integer limit){


        return new BooksPageDto(genreService.getBooksPageByGenre(genre,offset+1,limit));
    }

    @GetMapping("/authors")
    public String authorsPage(){

        return "/authors/index";
    }
    @GetMapping("books/authors")
    public String geAuthorsSlug(@RequestParam(value = "author", required = false) String author,@RequestParam("offset") Integer offset,
                                @RequestParam("limit") Integer limit, Model model){


         model.addAttribute("AuthorsBooksList",
                authorService.getBookEntitiesByAuthorName(author,offset,limit));
         model.addAttribute("Author",authorService.findAuthorEntitiesByName(author));
         model.addAttribute("AuthorsFullBookListInteger", authorService.getGetBookEntitiesByAuthorNameSize());
        return "/authors/slug.html";
    }
    @GetMapping("books/page/authors")
    @ResponseBody
    public BooksPageDto getAuthorsPage(@RequestParam(value = "author", required = false) String author,
                                       @RequestParam(value = "offset", required = false) Integer offset,
                                        @RequestParam(value = "limit", required = false) Integer limit, Model model){

//        model.addAttribute("Author",authorService.findAuthorEntitiesByName(author));
        return new BooksPageDto(authorService.getBookEntitiesByAuthorName(author,offset+1,limit));
    }
    @GetMapping("books/authorEntity")
    public String getAuthorEntitySlug(@RequestParam(value = "author", required = false) String author, Model model){

        model.addAttribute("AuthorsBooksList",
                authorService.getBooksWithAuthor(author,0,10));
         model.addAttribute("Author",authorService.findAuthorEntitiesByName(author));

        return "/books/author.html";
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
