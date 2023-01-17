package com.example.MyBookShopApp.services;

import com.example.MyBookShopApp.data.book.BookEntity;
import com.example.MyBookShopApp.data.book.review.BookReviewEntity;
import com.example.MyBookShopApp.data.book.review.BookReviewLikeEntity;
import com.example.MyBookShopApp.data.user.UserEntity;
import com.example.MyBookShopApp.repositories.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource("/application-test.properties")
class BooksRatingAndPopularityServiceTests {

    private final BookRepository bookRepository;
    private final BooksRatingAndPopularityService booksRatingAndPopularityService;
    private final BookToAuthorRepository bookToAuthorRepository;
    private final UserRepository userRepository;
    private final BookReviewRepository bookReviewRepository;
    private final BookReviewLikeRepository bookReviewLikeRepository;
    private final BookToUserRepository bookToUserRepository;

    private HashMap<UserEntity, BookReviewEntity> feedbacks =  new HashMap<>();



    @Autowired
    public BooksRatingAndPopularityServiceTests(BookRepository bookRepository, BooksRatingAndPopularityService booksRatingAndPopularityService, BookToAuthorRepository bookToAuthorRepository, BookToUserRepository bookToUserRepository, UserRepository userRepository, BookReviewRepository bookReviewRepository, BookReviewLikeRepository bookReviewLikeRepository) {
        this.bookRepository = bookRepository;
        this.booksRatingAndPopularityService = booksRatingAndPopularityService;
        this.bookToAuthorRepository = bookToAuthorRepository;
        this.bookToUserRepository = bookToUserRepository;
        this.userRepository = userRepository;
        this.bookReviewRepository = bookReviewRepository;
        this.bookReviewLikeRepository = bookReviewLikeRepository;
    }

    @BeforeEach
    void setUp() {
        UserEntity user1 = new UserEntity();
        user1.setName("test");
        user1.setPhone("00000000");
        user1.setBalance(0);
        user1.setEmail("test@mail.org");
        user1.setHash("dsafg;kjdsaljfghkl");
        user1.setPassword("test");
        user1.setRegTime(LocalDateTime.now());

        userRepository.save(user1);

        UserEntity user2 = new UserEntity();
        user2.setName("netEster");
        user2.setPhone("11111111");
        user2.setBalance(0);
        user2.setHash("dklsjhafkljdsalkj");
        user2.setRegTime(LocalDateTime.now());
        user2.setEmail("netTestu@gmail.com");
        user2.setPassword("NEtu");
        userRepository.save(user2);



        BookReviewEntity bookReviewEntity =new BookReviewEntity();
        bookReviewEntity.setReviewRating(0);
        bookReviewEntity.setText("TEXT");
        bookReviewEntity.setBookId(bookRepository.getOne(1L));
        bookReviewEntity.setUserId(user1);
        bookReviewEntity.setTime(LocalDateTime.now());
        bookReviewEntity.setBookReviewLikeEntity(null);
        bookReviewRepository.save(bookReviewEntity);



        BookReviewLikeEntity bookReviewLikeEntity1 = new BookReviewLikeEntity();
        bookReviewLikeEntity1.setTime(LocalDateTime.now());
        bookReviewLikeEntity1.setReviewId(bookReviewEntity);
        bookReviewLikeEntity1.setUserId(user1);
        bookReviewLikeEntity1.setValue((short) 1);
        bookReviewLikeRepository.save(bookReviewLikeEntity1);



        BookReviewLikeEntity bookReviewLikeEntity2 = new BookReviewLikeEntity();
        bookReviewLikeEntity2.setTime(LocalDateTime.now());
        bookReviewLikeEntity2.setReviewId(bookReviewEntity);
        bookReviewLikeEntity2.setUserId(user2);
        bookReviewLikeEntity2.setValue((short) -1);
        bookReviewLikeRepository.save(bookReviewLikeEntity2);


        List<BookReviewLikeEntity> bookReviewLikeEntities = new ArrayList<>();
        bookReviewLikeEntities.add(bookReviewLikeEntity1);
        bookReviewLikeEntities.add(bookReviewLikeEntity2);

        bookReviewEntity.setBookReviewLikeEntity(bookReviewLikeEntities);
        bookReviewRepository.save(bookReviewEntity);

        feedbacks.put(user1,bookReviewEntity);
      }

    @AfterEach
    void tearDown() {

        feedbacks=null;
    }

    @Test
    void getBookByRelevanceDesc() {
        List<BookEntity> allBooks=bookRepository.findAll();
        boolean isRelevanceChanged=false;
        for(BookEntity b:allBooks){
            if (b.getRelevance() > 0) {
                isRelevanceChanged = true;
                break;
            }
        }
        assertTrue(isRelevanceChanged);
    }
    @Test
    void getReviewRating(){

        LinkedHashMap<UserEntity,BookReviewEntity> map =booksRatingAndPopularityService.getReviewRating(feedbacks);
        assertEquals(0,map.values().stream().findFirst().get().getBookRating());
    }

}