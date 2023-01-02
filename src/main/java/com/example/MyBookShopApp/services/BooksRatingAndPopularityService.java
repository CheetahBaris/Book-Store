package com.example.MyBookShopApp.services;


import com.example.MyBookShopApp.data.book.BookEntity;
import com.example.MyBookShopApp.data.book.review.BookReviewEntity;
import com.example.MyBookShopApp.data.book.review.BookReviewLikeEntity;
import com.example.MyBookShopApp.data.user.UserEntity;
import com.example.MyBookShopApp.repositories.BookRepository;
import com.example.MyBookShopApp.repositories.BookReviewRepository;
import com.example.MyBookShopApp.repositories.BookToUserRepository;
import com.example.MyBookShopApp.repositories.UserRepository;
import liquibase.pro.packaged.P;
import liquibase.pro.packaged.S;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BooksRatingAndPopularityService {
    private static final double CART_PERCENT=0.7;
    private static final double KEPT_PERCENT=0.4;


    private final BookToUserRepository bookToUserRepository;
    private final BookReviewRepository bookReviewRepository;
    private final UserRepository userRepository;

    private final BookRepository bookRepository;
    @Autowired
    public BooksRatingAndPopularityService(BookToUserRepository bookToUserRepository,
                                           BookReviewRepository bookReviewRepository,
                                           BookRepository bookRepository,
                                           UserRepository userRepository) {
        this.bookToUserRepository = bookToUserRepository;
        this.bookReviewRepository = bookReviewRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }


    @EventListener(ApplicationReadyEvent.class)
    public void getPageOfBooksByRating(){

          for(BookEntity book :bookRepository.findAll()){

//
              book.setRelevance(bookToUserRepository.findBook2UserEntitiesByBookIdAndTypeId(book, 3).size()
                    +CART_PERCENT*bookToUserRepository.findBook2UserEntitiesByBookIdAndTypeId(book, 2).size()
                    + KEPT_PERCENT*bookToUserRepository.findBook2UserEntitiesByBookIdAndTypeId(book, 1).size());
             bookRepository.save(book);
          }
    }

    public Page<BookEntity> getBookByRelevanceDesc(Integer offset, Integer limit){
        Pageable nextPage = PageRequest.of(offset,limit);

        return bookRepository.getBookEntityFromRelevance(nextPage);
    }

    public Integer gradeBookFunctionAdmin(String slug,Integer value){

        BookEntity book =bookRepository.findBookEntitiesBySlug(slug);

        if(bookReviewRepository.findBookReviewEntitiesByBookIdAndUserId(book,userRepository.findUserEntitiesByName("admin"))!=null){

        BookReviewEntity bookReviewEntity=
        bookReviewRepository.findBookReviewEntitiesByBookIdAndUserId(book,userRepository.findUserEntitiesByName("admin"));
        bookReviewEntity.setBookRating(value);
        bookReviewRepository.save(bookReviewEntity);
         }else {

            BookReviewEntity bookReviewEntity = new BookReviewEntity();
            bookReviewEntity.setBookId(book);
            bookReviewEntity.setTime(LocalDateTime.now());
            bookReviewEntity.setBookRating(value);
            bookReviewEntity.setText("");

            if (userRepository.findUserEntitiesByName("admin") == null) {

                UserEntity admin = new UserEntity();
                admin.setBalance(1);
                admin.setHash("ADMIN");
                admin.setName("admin");
                admin.setRegTime(LocalDateTime.now());

                userRepository.save(admin);
            }
            bookReviewEntity.setUserId(userRepository.findUserEntitiesByName("admin"));
            bookReviewRepository.saveAndFlush(bookReviewEntity);
        }

        return getBookRatingGradeBySlug(slug);
    }
    public Integer getBookRatingGradeBySlug(String slug){
        BookEntity book =bookRepository.findBookEntitiesBySlug(slug);

//        Map<Integer, BookEntity> bookEntityWithRatingGrade = new HashMap<>();

        int bookRatingGrade = 0;

        List<BookReviewEntity> bookReviewEntityList = bookReviewRepository.findBookReviewRepositoriesByBookId(book);
        for(BookReviewEntity b:bookReviewEntityList){
            bookRatingGrade += b.getBookRating();
        }
        if( bookReviewEntityList.size()>0){
            bookRatingGrade /= bookReviewEntityList.size();
        }else {
            bookRatingGrade=0;
        }

//        bookEntityWithRatingGrade.put(bookRatingGrade,book);

        return bookRatingGrade;
    }

    public Integer getBookRatingGradeSizeBySlug(String slug){
        BookEntity book =bookRepository.findBookEntitiesBySlug(slug);

        return bookReviewRepository.findBookReviewRepositoriesByBookId(book).size();
    }

    public Integer getStarsRateSize(String slug, int stars){
         return bookReviewRepository.findBookReviewEntitiesByBookIdAndBookRating(bookRepository.findBookEntitiesBySlug(slug),stars).size();
    }

    public Map<UserEntity, BookReviewEntity> getFeedBackFromUserBySlug(String slug){
        List<BookReviewEntity> bookReviewEntityList =bookReviewRepository.findBookReviewRepositoriesByBookId(bookRepository.findBookEntitiesBySlug(slug));
        Map<UserEntity,BookReviewEntity> userEntities = new HashMap<>();
        for(BookReviewEntity b:bookReviewEntityList){
            userEntities.put(b.getUserId(),b);
         }

        return userEntities;
    }



}
