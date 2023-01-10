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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/books")
public class BookShopCartController {

    @ModelAttribute(name = "bookCart")
    public List<BookEntity> bookCart() {

        return new ArrayList<>();
    }

    private final BookService bookService;

    private final BookstoreUserRegister userRegister;
    private final BooksRatingAndPopularityService booksRatingAndPopularityService;
    private final AuthorService authorService;
    private final JWTUtil jwtUtil;
    private final GenreService genreService;

    @Autowired
    public BookShopCartController(BookService bookService, BookstoreUserRegister userRegister,
                                  BooksRatingAndPopularityService booksRatingAndPopularityService,
                                  GenreService genreService, AuthorService authorService, JWTUtil jwtUtil) {
        this.bookService = bookService;
        this.userRegister = userRegister;
        this.booksRatingAndPopularityService = booksRatingAndPopularityService;
        this.genreService = genreService;
        this.authorService = authorService;
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
    @ModelAttribute("isCartEmpty")
    public boolean getIsCartEmpty(boolean isCartEmpty){
        return isCartEmpty;
    }


    @GetMapping("/cart")
    public String handleCartRequest(@CookieValue(value = "cartContents", required = false) String cartContents,
                                    @CookieValue(value = "token", required = false) String token,
                                    Model model) throws BookstoreApiWrongParameterException {
        if (cartContents == null || cartContents.length()<=1 ) {
            model.addAttribute("isCartEmpty", true);

        } else {
            model.addAttribute("isCartEmpty", false);
            cartContents = cartContents.startsWith("/") ? cartContents.substring(1) : cartContents;
            cartContents = cartContents.endsWith("/") ? cartContents.substring(0, cartContents.length() - 1) :
                    cartContents;
            String[] cookieSlugs = cartContents.split("/");
            List<BookEntity> booksFromCookieSlugs = bookService.getBookEntitiesBySlugIn(Arrays.asList(cookieSlugs));
            double sum = 0.0;
            double sumOld = 0.0;

            for(BookEntity b:booksFromCookieSlugs){
                sumOld+=b.getPrice() + b.getPrice()*b.getDiscount();
                sum+=b.getPrice();
            }
            model.addAttribute("bookCart", booksFromCookieSlugs);
            model.addAttribute("finalPrice",sum);
            model.addAttribute("finalPriceOld",sumOld);

        }

        if(token != null){

            model.addAttribute("curUsrStatus","authorized");
            model.addAttribute("curUsr",userRegister.getCurrentUser());
        }else {
            model.addAttribute("curUsrStatus","unauthorized");
            model.addAttribute("curUsr",null);
        }


        return "cart.html";
    }

    @PostMapping("/changeBookStatus/cart/remove/{bookid}")
    public String handleRemoveBookFromCartRequest(@PathVariable("bookid") String slug, @CookieValue(name =
            "cartContents", required = false) String cartContents, HttpServletResponse response, Model model) {
        if (cartContents != null && !cartContents.equals("")) {
            ArrayList<String> cookieBooks = new ArrayList<>(Arrays.asList(cartContents.split("/")));
//            cookieBooks.clear();
            cookieBooks.remove(slug);
            Cookie cookie = new Cookie("cartContents", String.join("/", cookieBooks));
            cookie.setPath("/");
            response.addCookie(cookie);
             if(cookie.getValue().length() <=1){
                model.addAttribute("isCartEmpty", true);
            }else {
                 model.addAttribute("isCartEmpty", false);
             }
        } else {
            model.addAttribute("isCartEmpty", true);
        }

        return "redirect:/books/cart";
    }
    @PostMapping("/changeBookStatus/cart/postponed/{bookid}")
    public String handlePostponeBookFromCartRequest(@PathVariable("bookid") String slug, @CookieValue(name =
            "postponedContents", required = false) String postponedContents, HttpServletResponse response, Model model) {
        if (postponedContents == null || postponedContents.equals("") || postponedContents.equals("null")) {
            Cookie cookie = new Cookie("postponedContents", slug);

            cookie.setPath("/");
            response.addCookie(cookie);
            model.addAttribute("isCartEmpty", false);

        } else {
            StringJoiner stringJoiner = new StringJoiner("/");
            stringJoiner.add(postponedContents).add(slug);
            Cookie cookie = new Cookie("postponedContents", stringJoiner.toString());
            cookie.setPath("/");
            response.addCookie(cookie);
            model.addAttribute("isCartEmpty", false);
         }

        return "redirect:/books/cart";
    }


    @PostMapping("/changeBookStatus/cart/{bookid}")
    public String handleChangeBookStatus(@PathVariable("bookid") String slug, @CookieValue(name = "cartContents",
            required = false) String cartContents, HttpServletResponse response, Model model) {

        if (cartContents == null || cartContents.equals("")) {
            StringJoiner stringJoiner = new StringJoiner("/");

            if(slug.contains("&")){
                String[] slugs= slug.split("&");

                for (String s:slugs){
                    stringJoiner.add(cartContents).add(s);
                }
            }else {
                stringJoiner.add(cartContents).add(slug);
            }
            Cookie cookie = new Cookie("cartContents", stringJoiner.toString());
            cookie.setPath("/");
            response.addCookie(cookie);
            model.addAttribute("isCartEmpty", false);
        } else if (!cartContents.contains(slug)) {
            StringJoiner stringJoiner = new StringJoiner("/");

            if(slug.contains("&")){
                String[] slugs= slug.split("&");

                for (String s:slugs){
                    stringJoiner.add(cartContents).add(s);
                }
            }else {
                stringJoiner.add(cartContents).add(slug);
            }

            Cookie cookie = new Cookie("cartContents", stringJoiner.toString());
            cookie.setPath("/");
            response.addCookie(cookie);
            model.addAttribute("isCartEmpty", false);
        }

        return "redirect:/books/" + slug;
    }


}
