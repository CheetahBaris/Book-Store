package com.example.MyBookShopApp.services;


import com.example.MyBookShopApp.data.book.BookEntity;
import com.example.MyBookShopApp.data.book.review.BookReviewEntity;
import com.example.MyBookShopApp.data.book.review.BookReviewLikeEntity;
import com.example.MyBookShopApp.data.user.UserEntity;
import com.example.MyBookShopApp.repositories.*;
import io.swagger.models.auth.In;
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
    private static final double CART_PERCENT = 0.7;
    private static final double KEPT_PERCENT = 0.4;


    private final BookToUserRepository bookToUserRepository;
    private final BookReviewRepository bookReviewRepository;
    private final UserRepository userRepository;
    private final BookReviewLikeRepository bookReviewLikeRepository;
    private final BookRepository bookRepository;

    @Autowired
    public BooksRatingAndPopularityService(BookToUserRepository bookToUserRepository,
                                           BookReviewRepository bookReviewRepository,
                                           UserRepository userRepository,
                                           BookReviewLikeRepository bookReviewLikeRepository,
                                           BookRepository bookRepository) {
        this.bookToUserRepository = bookToUserRepository;
        this.bookReviewRepository = bookReviewRepository;
        this.userRepository = userRepository;
        this.bookReviewLikeRepository = bookReviewLikeRepository;
        this.bookRepository = bookRepository;
    }


    @EventListener(ApplicationReadyEvent.class)
    public void getPageOfBooksByRating() {

        for (BookEntity book : bookRepository.findAll()) {

//
            book.setRelevance(bookToUserRepository.findBook2UserEntitiesByBookIdAndTypeId(book, 3).size()
                    + CART_PERCENT * bookToUserRepository.findBook2UserEntitiesByBookIdAndTypeId(book, 2).size()
                    + KEPT_PERCENT * bookToUserRepository.findBook2UserEntitiesByBookIdAndTypeId(book, 1).size());
            bookRepository.save(book);
        }
    }

    public Page<BookEntity> getBookByRelevanceDesc(Integer offset, Integer limit) {
        Pageable nextPage = PageRequest.of(offset, limit);

        return bookRepository.getBookEntityFromRelevance(nextPage);
    }

    public void textBookFunction(String slug, String text, UserEntity user){

        BookEntity book = bookRepository.findBookEntitiesBySlug(slug);

        if (bookReviewRepository.findBookReviewEntitiesByBookIdAndUserId(book, user) != null) {

            BookReviewEntity bookReviewEntity =
                    bookReviewRepository.findBookReviewEntitiesByBookIdAndUserId(book, user);
            bookReviewEntity.setText(text);
             bookReviewRepository.save(bookReviewEntity);
        } else {

            BookReviewEntity bookReviewEntity = new BookReviewEntity();
            bookReviewEntity.setBookId(book);
            bookReviewEntity.setTime(LocalDateTime.now());
            bookReviewEntity.setText(text);
            bookReviewEntity.setUserId(user);
            bookReviewRepository.saveAndFlush(bookReviewEntity);
        }


    }

    public void gradeBookFunction(String slug, Integer value, UserEntity user) {

        BookEntity book = bookRepository.findBookEntitiesBySlug(slug);

        if (bookReviewRepository.findBookReviewEntitiesByBookIdAndUserId(book, user) != null) {

            BookReviewEntity bookReviewEntity =
                    bookReviewRepository.findBookReviewEntitiesByBookIdAndUserId(book, user);
            bookReviewEntity.setBookRating(value);
            bookReviewRepository.save(bookReviewEntity);
        } else {

            BookReviewEntity bookReviewEntity = new BookReviewEntity();
            bookReviewEntity.setBookId(book);
            bookReviewEntity.setTime(LocalDateTime.now());
            bookReviewEntity.setBookRating(value);
            bookReviewEntity.setText("");
            bookReviewEntity.setUserId(user);
            bookReviewRepository.saveAndFlush(bookReviewEntity);
        }
    }

    public Integer getBookRatingGradeBySlug(String slug) {
        BookEntity book = bookRepository.findBookEntitiesBySlug(slug);

//        Map<Integer, BookEntity> bookEntityWithRatingGrade = new HashMap<>();

        int bookRatingGrade = 0;

        List<BookReviewEntity> bookReviewEntityList = bookReviewRepository.findBookReviewRepositoriesByBookId(book);
        for (BookReviewEntity b : bookReviewEntityList) {
            bookRatingGrade += b.getBookRating();
        }
        if (bookReviewEntityList.size() > 0) {
            bookRatingGrade /= bookReviewEntityList.size();
        } else {
            bookRatingGrade = 0;
        }

//        bookEntityWithRatingGrade.put(bookRatingGrade,book);

        return bookRatingGrade;
    }

    public Integer getBookRatingGradeSizeBySlug(String slug) {
        BookEntity book = bookRepository.findBookEntitiesBySlug(slug);

        return bookReviewRepository.findBookReviewRepositoriesByBookId(book).size();
    }

    public Integer getStarsRateSize(String slug, int stars) {
        return bookReviewRepository.findBookReviewEntitiesByBookIdAndBookRating(bookRepository.findBookEntitiesBySlug(slug), stars).size();
    }

    public Map<UserEntity, BookReviewEntity> getFeedBackFromUserBySlug(String slug) {
        List<BookReviewEntity> bookReviewEntityList = bookReviewRepository.findBookReviewRepositoriesByBookId(bookRepository.findBookEntitiesBySlug(slug));
        Map<UserEntity, BookReviewEntity> userEntities = new HashMap<>();
        for (BookReviewEntity b : bookReviewEntityList) {
            userEntities.put(b.getUserId(), b);
        }

        return userEntities;
    }

    public void likeOrDislikeFunction(int value, Long reviewId, UserEntity user) {

        if (bookReviewLikeRepository.findBookReviewLikeEntitiesByReviewIdAndUserId(
                bookReviewRepository.findById(reviewId).get(),user) == null) {

            BookReviewLikeEntity bookReviewLikeEntity = new BookReviewLikeEntity();
            bookReviewLikeEntity.setTime(LocalDateTime.now());
            bookReviewLikeEntity.setReviewId(bookReviewRepository.findById(reviewId).get());
            bookReviewLikeEntity.setUserId(user);
            bookReviewLikeEntity.setValue((short) value);
            bookReviewLikeRepository.save(bookReviewLikeEntity);
        } else {

            BookReviewLikeEntity bookReviewLikeEntity = bookReviewLikeRepository.findBookReviewLikeEntitiesByReviewIdAndUserId(
                    bookReviewRepository.findById(reviewId).get(),user);
            bookReviewLikeEntity.setValue((short) value);
            bookReviewLikeRepository.save(bookReviewLikeEntity);

        }
    }

    public HashMap<BookReviewEntity,Integer> getAmountOfLikes(String slug){


        HashMap<BookReviewEntity,Integer> reteLike = new HashMap<>();
        List<BookReviewEntity> bookReviewEntityList = bookReviewRepository.findBookReviewRepositoriesByBookId(bookRepository.findBookEntitiesBySlug(slug));
        for(BookReviewEntity b:bookReviewEntityList ){
            int likes = 0;

            for (BookReviewLikeEntity like:b.getBookReviewLikeEntity()){
                if(like.getValue()>0){
                    likes++;
                }
            }
            reteLike.put(b,likes);

        }
    return reteLike;
    }

    public HashMap<BookReviewEntity,Integer> getAmountOfDislikes(String slug){


        HashMap<BookReviewEntity,Integer> reteDislike = new HashMap<>();
        List<BookReviewEntity> bookReviewEntityList = bookReviewRepository.findBookReviewRepositoriesByBookId(bookRepository.findBookEntitiesBySlug(slug));
        for(BookReviewEntity b:bookReviewEntityList ){
            int dislikes = 0;

            for (BookReviewLikeEntity like:b.getBookReviewLikeEntity()){
                if(like.getValue()<0){
                    dislikes++;
                }
            }
            reteDislike.put(b,dislikes);

        }
        return reteDislike;
    }

}
