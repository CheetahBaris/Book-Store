package com.example.MyBookShopApp.repositories;

import com.example.MyBookShopApp.data.book.review.BookReviewEntity;
import com.example.MyBookShopApp.data.book.review.BookReviewLikeEntity;
import com.example.MyBookShopApp.data.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookReviewLikeRepository extends JpaRepository<BookReviewLikeEntity,Integer> {

    BookReviewLikeEntity findBookReviewLikeEntitiesByReviewIdAndUserId(BookReviewEntity  reviewId, UserEntity user);
    List<BookReviewLikeEntity> findBookReviewLikeEntitiesByReviewId(int reviewId);
    List<BookReviewLikeEntity> findBookReviewLikeEntitiesByUserId(UserEntity user);
}

