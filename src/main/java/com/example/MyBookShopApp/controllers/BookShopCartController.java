package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.author.AuthorEntity;
import com.example.MyBookShopApp.data.book.BookEntity;
import com.example.MyBookShopApp.data.dto.SearchWordDto;
import com.example.MyBookShopApp.errs.BookstoreApiWrongParameterException;
import com.example.MyBookShopApp.errs.InvalidJwtTokenException;
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

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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


    @ModelAttribute("searchWordDto")
    public SearchWordDto searchWordDto() {
        return new SearchWordDto();
    }


    @ModelAttribute("isCartEmpty")
    public boolean getIsCartEmpty(boolean isCartEmpty){
        return isCartEmpty;
    }


    @GetMapping("/cart")
    public String handleCartRequest(@CookieValue(value = "cartContents", required = false) String cartContents,
                                    @CookieValue(value = "postponedContents", required = false) String postponedContents,
                                    @CookieValue(value = "token", required = false) String token,
                                    Model model) throws BookstoreApiWrongParameterException, InvalidJwtTokenException {

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
        String[]  cookiePostponedSlugs = postponedContents!=null ? (postponedContents.isEmpty()? null : postponedContents.split("/")) : null;
        String[] cookieCartSlugs = cartContents!=null? (cartContents.isEmpty()?null : cartContents.split("/")):null;


        model.addAttribute("postponedSize",cookiePostponedSlugs!=null?cookiePostponedSlugs.length:null);
        model.addAttribute("cartSize",cookieCartSlugs!=null?cookieCartSlugs.length:null);

        return "cart.html";
    }

    @PostMapping("/changeBookStatus/cart/remove/{bookid}")
    public String handleRemoveBookFromCartRequest(@PathVariable("bookid") String slug,@CookieValue(value = "cartContents", required = false) String cartContents,
                                                  @CookieValue(value = "postponedContents", required = false) String postponedContents, HttpServletResponse response, Model model) {

        if (cartContents != null && !cartContents.equals("")) {
            ArrayList<String> cookieBooks = new ArrayList<>(Arrays.asList(cartContents.split("/")));
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

        String[]  cookiePostponedSlugs = postponedContents!=null ? (postponedContents.isEmpty()? null : postponedContents.split("/")) : null;
        String[] cookieCartSlugs = cartContents!=null? (cartContents.isEmpty()?null : cartContents.split("/")):null;


        model.addAttribute("postponedSize",cookiePostponedSlugs!=null?cookiePostponedSlugs.length:null);
        model.addAttribute("cartSize",cookieCartSlugs!=null?cookieCartSlugs.length:null);

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
                stringJoiner.add(slug);
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
