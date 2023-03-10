package com.example.MyBookShopApp.repositories;

import com.example.MyBookShopApp.data.book.BookEntity;
import com.example.MyBookShopApp.data.book.review.BookReviewEntity;
import com.example.MyBookShopApp.data.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface BookReviewRepository extends JpaRepository<BookReviewEntity,Long> {
    List<BookReviewEntity> findBookReviewEntitiesByBookId(BookEntity book);
    List<BookReviewEntity> findBookReviewRepositoriesByUserId(UserEntity user);
    @Query(value = "SELECT MAX(id) FROM book_review;",nativeQuery = true)
    Long getMaxId();

    BookReviewEntity findBookReviewEntitiesByBookIdAndUserId(BookEntity book, UserEntity user);
    List<BookReviewEntity> findBookReviewEntitiesByBookIdAndBookRating(BookEntity book,int bookRating);


}
