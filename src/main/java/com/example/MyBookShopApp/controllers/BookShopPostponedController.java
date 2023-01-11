package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.author.AuthorEntity;
import com.example.MyBookShopApp.data.book.BookEntity;
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

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/books")
public class BookShopPostponedController {

    private final BookService bookService;
    private final BooksRatingAndPopularityService booksRatingAndPopularityService;
    private final AuthorService authorService;
    private final GenreService genreService;
    private final JWTUtil jwtUtil;
    private final BookstoreUserRegister userRegister;

    @Autowired
    public BookShopPostponedController(BookService bookService, BooksRatingAndPopularityService booksRatingAndPopularityService,
                                       GenreService genreService, AuthorService authorService, JWTUtil jwtUtil, BookstoreUserRegister userRegister) {
        this.bookService = bookService;
        this.booksRatingAndPopularityService = booksRatingAndPopularityService;
        this.genreService = genreService;
        this.authorService = authorService;
        this.jwtUtil = jwtUtil;
        this.userRegister = userRegister;
    }


    @ModelAttribute(name = "postponedBooks")
    public List<BookEntity> postponedBooks() {
        return new ArrayList<>();
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

    @GetMapping("/postponed")
    public String handlePostponeRequest(@CookieValue(value = "cartContents", required = false) String cartContents,
                                        @CookieValue(value = "postponedContents", required = false) String postponedContents,
                                        @CookieValue(value = "token", required = false) String token, Model model) throws BookstoreApiWrongParameterException {

        if (postponedContents == null || postponedContents.length()<=1) {
            model.addAttribute("isPostponedEmpty", true);

        } else {
            model.addAttribute("isPostponedEmpty", false);
            postponedContents = postponedContents.startsWith("/") ? postponedContents.substring(1) : postponedContents;
            postponedContents = postponedContents.endsWith("/") ? postponedContents.substring(0, postponedContents.length() - 1) :
                    postponedContents;
            String[] cookieSlugs = postponedContents.split("/");
            List<BookEntity> booksFromCookieSlugs = bookService.getBookEntitiesBySlugIn(Arrays.asList(cookieSlugs));
            double sum = 0.0;
            double sumOld = 0.0;

            for(BookEntity b:booksFromCookieSlugs){
                sumOld+=b.getPrice() + b.getPrice()*b.getDiscount();
                sum+=b.getPrice();
            }
            model.addAttribute("postponedBooks", booksFromCookieSlugs);
            model.addAttribute("finalPrice",sum);
            model.addAttribute("finalPriceOld",sumOld);
            StringBuilder booksIDs = new StringBuilder();
            for(BookEntity b:booksFromCookieSlugs){
                booksIDs.append(b.getSlug()).append("&");
            }
            model.addAttribute("buyAllPostponedBooks", booksIDs.toString());
        }

        if(token != null){

            model.addAttribute("curUsrStatus","authorized");
            model.addAttribute("curUsr",userRegister.getCurrentUser());
        }else {
            model.addAttribute("curUsrStatus","unauthorized");
            model.addAttribute("curUsr",null);
        }
        String[]  cookiePostponedSlugs = postponedContents!=null ? (postponedContents.isEmpty()? null : postponedContents.split("/")) : null;
        String[] cookieCartSlugs = cartContents!=null? (cartContents.isEmpty()?null : cartContents.split("/")):null;


        model.addAttribute("postponedSize",cookiePostponedSlugs!=null?cookiePostponedSlugs.length:null);
        model.addAttribute("cartSize",cookieCartSlugs!=null?cookieCartSlugs.length:null);
        return "postponed.html";
    }


    @PostMapping("/changeBookStatus/postponed/remove/{bookid}")
    public String handleRemoveBookFromPostponedRequest(@PathVariable("bookid") String slug, @CookieValue(name =
            "postponedContents", required = false) String postponedContents, HttpServletResponse response, Model model) throws IOException {
        if (postponedContents != null && !postponedContents.equals("")) {
            ArrayList<String> cookieBooks = new ArrayList<>(Arrays.asList(postponedContents.split("/")));
            cookieBooks.remove(slug);
            Cookie cookie = new Cookie("postponedContents", String.join("/", cookieBooks));
            cookie.setPath("/");
            response.addCookie(cookie);
//            response.sendRedirect("/books/postponed");

            if(cookie.getValue().length() <=1){
                model.addAttribute("isCartEmpty", true);
            }else {
                model.addAttribute("isCartEmpty", false);
            }
        } else {
            model.addAttribute("isCartEmpty", true);
        }
         return "redirect:/books/postponed";
    }
    @PostMapping("/changeBookStatus/postponed/{bookid}")
    public String handlePostponeBookFromBooksSlugRequest(@PathVariable("bookid") String slug, @CookieValue(name =
            "postponedContents", required = false) String postponeContents, HttpServletResponse response, Model model) {
        if (postponeContents == null || postponeContents.equals("")) {
            Cookie cookie = new Cookie("postponedContents", slug);
            cookie.setPath("/");
            response.addCookie(cookie);
            model.addAttribute("isCartEmpty", false);

        } else {
            StringJoiner stringJoiner = new StringJoiner("/");
            stringJoiner.add(postponeContents).add(slug);
            Cookie cookie = new Cookie("postponedContents", stringJoiner.toString());
            cookie.setPath("/");
            response.addCookie(cookie);
            model.addAttribute("isCartEmpty", false);
        }

        return "redirect:/books/"+slug;
    }

    @PostMapping("/changeBookStatus/postponed/cart/{bookid}")
    public String handleCartBookFromPostponeRequest(@PathVariable("bookid") String slug, @CookieValue(name =
            "cartContents", required = false) String cartContents, HttpServletResponse response, Model model) {
        if (cartContents == null || cartContents.equals("") || cartContents.equals("null")) {
            Cookie cookie = new Cookie("cartContents", slug);
            cookie.setPath("/");
            response.addCookie(cookie);
            model.addAttribute("isCartEmpty", false);

        } else {
            StringJoiner stringJoiner = new StringJoiner("/");
            stringJoiner.add(cartContents).add(slug);
            Cookie cookie = new Cookie("cartContents", stringJoiner.toString());
            cookie.setPath("/");
            response.addCookie(cookie);
            model.addAttribute("isCartEmpty", false);
        }

        return "redirect:/books/postponed";
    }





}
