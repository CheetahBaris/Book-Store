package com.example.MyBookShopApp.data.book.repositories;

import com.example.MyBookShopApp.data.book.BookEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.awt.print.Book;
import java.util.Date;
import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<BookEntity, Long> {
    @Query("from BookEntity")
    List<Book> customFindAllBooks();

    //NEW BOOK REST REPOSITORY

    List<BookEntity> findBooksByAuthorFirstNameContaining(String authorsFirstName);

    List<BookEntity> findBooksByTitleContaining(String bookTitle);

    List<BookEntity> findBooksByPriceBetween(Integer min, Integer max);

    List<BookEntity> findBooksByPriceIs(Integer price);

    @Query("from BookEntity where isBestseller=1")
    List<BookEntity> getBestsellers();

    @Query(value = "SELECT * FROM books WHERE discount = (SELECT MAX(discount) FROM books", nativeQuery = true)
    List<BookEntity> getBooksWithMaxDiscount();

    Page<BookEntity> findBookByTitleContaining(String bookTitle, Pageable nextPage);

    Page<BookEntity> findBookByPubDateBetween(Date fromDateRecent, Date endDateRecent, Pageable nextPage);

    @Query(value = "SELECT * FROM books ORDER BY relevance DESC ", nativeQuery = true)
    Page<BookEntity> getBookEntityFromRelevance(Pageable nextPage);

    Page<BookEntity> findBookEntitiesByTag(String tag, Pageable nextPage);

//     Page<BookEntity> findAllById(Iterable<Long> longs, Pageable nextPage);

    //    Page<BookEntity> findAllBy(List<Long> id, Pageable nextPage);


}
