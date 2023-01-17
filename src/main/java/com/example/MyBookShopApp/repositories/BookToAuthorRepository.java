package com.example.MyBookShopApp.repositories;

import com.example.MyBookShopApp.data.author.AuthorEntity;
import com.example.MyBookShopApp.data.book.BookEntity;
import com.example.MyBookShopApp.data.book.links.Book2AuthorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookToAuthorRepository extends JpaRepository<Book2AuthorEntity, Long> {

    List<Book2AuthorEntity> findBook2AuthorEntitiesByAuthorId(AuthorEntity author);
    List<Book2AuthorEntity> findBook2AuthorEntitiesByBookId(BookEntity book);
    List<Book2AuthorEntity> findBook2AuthorEntitiesByBookIdIn(List<BookEntity> books);
    List<Book2AuthorEntity> findBook2AuthorEntitiesByAuthorIdIn(List<AuthorEntity> authors);


}
