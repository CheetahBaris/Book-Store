package com.example.MyBookShopApp.services;

import com.example.MyBookShopApp.data.author.AuthorEntity;
import com.example.MyBookShopApp.data.book.BookEntity;
import com.example.MyBookShopApp.data.book.links.Book2AuthorEntity;
import com.example.MyBookShopApp.data.book.links.Book2GenreEntity;
import com.example.MyBookShopApp.data.genre.GenreEntity;
import com.example.MyBookShopApp.errs.BookstoreApiWrongParameterException;
import com.example.MyBookShopApp.repositories.BookRepository;
import com.example.MyBookShopApp.repositories.BookToAuthorRepository;
import com.example.MyBookShopApp.repositories.BookToGenreRepository;
import com.example.MyBookShopApp.repositories.GenresRepository;
import liquibase.pro.packaged.P;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@SpringBootTest
@TestPropertySource("/application-test.properties")
class BookServiceTests {
    private final BookService bookService;
    private final BookRepository bookRepository;
    private final GenresRepository genresRepository;
    private final BookToAuthorRepository bookToAuthorRepository;
    private final BookToGenreRepository bookToGenreRepository;

    private String[] postponed = new String[2];
    private String[] cart = new String[2];
    private int offset;
    private int limit;



    @Autowired
    public BookServiceTests(BookService bookService, BookRepository bookRepository, GenresRepository genresRepository, BookToAuthorRepository bookToAuthorRepository, BookToGenreRepository bookToGenreRepository) {
        this.bookService = bookService;
        this.bookRepository = bookRepository;
        this.genresRepository = genresRepository;
        this.bookToAuthorRepository = bookToAuthorRepository;
        this.bookToGenreRepository = bookToGenreRepository;
    }

    @BeforeEach
    void setUp() {
        postponed[0]= bookRepository.findById(1L).get().getSlug();
        postponed[1]= bookRepository.findById(2L).get().getSlug();

        cart[0]= bookRepository.findById(3L).get().getSlug();
        cart[1]= bookRepository.findById(4L).get().getSlug();
        limit=6;
        offset=1;
    }

    @AfterEach
    void tearDown() {
    }
    @Test
    void getPageOfRecommendedBooks() throws BookstoreApiWrongParameterException {
        List<BookEntity> booksFromMethod = bookService.getPageOfRecommendedBooks(postponed,cart, offset,limit);
        List<BookEntity> booksForTest = new ArrayList<>();
        booksForTest.addAll(bookRepository.findBookEntitiesBySlugIn(List.of(postponed)));
        booksForTest.addAll(bookRepository.findBookEntitiesBySlugIn(List.of(cart)));
        HashSet<GenreEntity> genreEntitiesFormTestEntities = new HashSet<>();
        HashSet<AuthorEntity> authorEntitiesFormTestEntities = new HashSet<>();

        boolean isAuthorContains=false;
        boolean isGenreContains=false;
        boolean isTagContains=false;

        for(Book2AuthorEntity book2Author : bookToAuthorRepository.findBook2AuthorEntitiesByBookIdIn(booksForTest)){
            authorEntitiesFormTestEntities.add(book2Author.getAuthorId());
        }

        for(Book2GenreEntity book2GenreEntity :bookToGenreRepository.findBook2GenreEntitiesByBookIdIn(booksForTest)){
            genreEntitiesFormTestEntities.add(book2GenreEntity.getGenreId());
        }

        //Tag Test
        for(BookEntity testBooks :booksForTest){
            for(BookEntity bookFromMethod :booksFromMethod){
                if(testBooks.getTag().equals(bookFromMethod.getTag())){
                    isTagContains=true;
                    break;

                }
            }
        }

        //Author Test
        for(Book2AuthorEntity book2Author :bookToAuthorRepository.findBook2AuthorEntitiesByBookIdIn(booksFromMethod)){
            for (AuthorEntity author : authorEntitiesFormTestEntities){
                if(book2Author.getAuthorId().getName().equals(author.getName())) {
                    isAuthorContains=true;
                    break;

                }
            }
        }

        //Genre Test
        for(Book2GenreEntity book2GenreEntity: bookToGenreRepository.findBook2GenreEntitiesByBookIdIn(booksFromMethod)){
            for(GenreEntity genre:genreEntitiesFormTestEntities){
                if(book2GenreEntity.getGenreId().getName().equals(genre.getName())){
                    isGenreContains=true;
                    break;
                }
            }
        }

        assertNotNull(booksFromMethod);
        assertFalse(booksFromMethod.isEmpty());
        assertTrue(isAuthorContains);
        assertTrue(isTagContains);
        assertTrue(isGenreContains);
    }

}