package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.repositories.BookRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultHandler;

import javax.servlet.http.Cookie;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;


@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
class BookShopCartControllerTests {

    private final MockMvc mockMvc;
    private final BookRepository bookRepository;
    @Autowired
    BookShopCartControllerTests(MockMvc mockMvc, BookRepository bookRepository) {
        this.mockMvc = mockMvc;
        this.bookRepository = bookRepository;
    }

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void handleRemoveBookFromCartRequest() throws Exception {

          mockMvc
                .perform(post("/books/changeBookStatus/cart/remove/{bookid}",bookRepository.findById(1L).get().getSlug())
                        .cookie(new Cookie("cartContents",bookRepository.findById(1L).get().getSlug())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books/cart"))
                .andExpect(cookie().value("cartContents", ""));
    }

    @Test
    void handleChangeBookStatus() throws Exception {
        mockMvc
                .perform(post("/books/changeBookStatus/cart/{bookid}",bookRepository.findById(1L).get().getSlug()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books/"+bookRepository.findById(1L).get().getSlug()))
                .andExpect(cookie().value("cartContents",bookRepository.findById(1L).get().getSlug()));
    }
}