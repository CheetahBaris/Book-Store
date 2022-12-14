package com.example.MyBookShopApp.data.book.repositories;

import com.example.MyBookShopApp.data.book.BookEntity;
import com.example.MyBookShopApp.data.book.links.Book2UserEntity;
import liquibase.pro.packaged.B;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface BookToUserRepository extends JpaRepository<Book2UserEntity, Long> {
//    List<BookEntity> findBooksByAuthorFirstNameContaining(String authorsFirstName);

//    @Query("from Book2UserEntity where bookId.id= id and typeId=3")
//    List<Book2UserEntity> getBoughtBooks(Long id);
//    @Query("from Book2UserEntity where bookId =  and typeId=2")
//    @Query(value = "SELECT * FROM book2user WHERE book_id ="+kek+"AND type_id= 2 ;")
//    List<Book2UserEntity> getCartedBooks(Long kek);
//

//    List<Book2UserEntity> findBook2UserEntitiesById(Long id);

    Set<Book2UserEntity> findBook2UserEntitiesByBookIdAndTypeId(BookEntity bookId, int typeId);
//    @Query("from Book2UserEntity where bookId.id= id and typeId=1")
//    List<Book2UserEntity> getKeptInBooks(Long id);

}
