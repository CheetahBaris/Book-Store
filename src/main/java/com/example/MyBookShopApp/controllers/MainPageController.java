package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.book.BookEntity;
import com.example.MyBookShopApp.data.book.services.BookService;
import com.example.MyBookShopApp.data.book.services.BooksRatingAndPopularityService;
import com.example.MyBookShopApp.data.dto.BooksPageDto;
import com.example.MyBookShopApp.data.dto.SearchWordDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class MainPageController {

    private final BookService bookService;
    private final BooksRatingAndPopularityService booksRatingAndPopularityService;

    @Autowired
    public MainPageController(BookService bookService,  BooksRatingAndPopularityService booksRatingAndPopularityService ) {
        this.bookService = bookService;
        this.booksRatingAndPopularityService = booksRatingAndPopularityService;
    }

    @ModelAttribute("booksListFull")
    public List<BookEntity> bookListFull(){
        return bookService.getBooksData();
    }
    @ModelAttribute("booksList")
    public List<BookEntity> bookList(){
        return bookService.getPageOfRecommendedBooks(0, 20).getContent();
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

    @GetMapping("/")
    public String mainPage() {

        return "index";

    }

    @GetMapping("/books/recommended")
    @ResponseBody
    public BooksPageDto getBooksPage(@RequestParam("offset") Integer offset,
                                     @RequestParam("limit") Integer limit) {
        return new BooksPageDto(bookService.getPageOfRecommendedBooks(offset, limit).getContent());
    }
    @GetMapping("/books/popular")
    public String getBookPopular(@RequestParam(value = "offset", required = false) Integer offset,
                                     @RequestParam(value = "limit", required = false) Integer limit){

        booksRatingAndPopularityService.getPageOfBooksByRating();

        return "/books/popular.html";
    }
    @GetMapping("/books/page/popular")
    @ResponseBody
    public BooksPageDto getBookPopularPage(@RequestParam(value = "offset", required = false) Integer offset,
                                           @RequestParam(value = "limit", required = false) Integer limit){


        return new BooksPageDto(booksRatingAndPopularityService.getBookByRelevanceAsc(offset,limit).getContent());
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

        return new BooksPageDto(bookService.findBookByPubDateBetween(fromDateRecent,endDateRecent,offset, limit).getContent());
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
//        model.addAttribute("searchResultsFull" , bookService.getBooksByTitle(searchWordDto.getExample()));
        return "/search/index";
    }

    @GetMapping("/search/page/{searchWord}")
    @ResponseBody
    public BooksPageDto getNextSearchPage(@RequestParam("offset") Integer offset,
                                          @RequestParam("limit") Integer limit,
                                          @PathVariable(value = "searchWord", required = false) SearchWordDto searchWordDto) {
        return new BooksPageDto(bookService.getPageOfSearchResultBooks(searchWordDto.getExample(), offset, limit).getContent());
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
