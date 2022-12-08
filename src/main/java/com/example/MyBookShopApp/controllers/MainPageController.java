package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.book.BookEntity;
import com.example.MyBookShopApp.data.book.BookService;
import com.example.MyBookShopApp.data.book.BooksEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@Controller
public class MainPageController {

    private final BookService bookService;

    @Autowired
    public MainPageController(BookService bookService) {
        this.bookService = bookService;
    }

    @ModelAttribute("recommendedBooks")
    public List<BooksEntity> recommendedBooks(){
        return bookService.getAllBooks();
    }
    @ModelAttribute("booksList")
    public List<BooksEntity> getBooksList(){
        return bookService.getAllBooks();
    }

    @GetMapping("/")
    public String mainPage(){
        return "index";
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
