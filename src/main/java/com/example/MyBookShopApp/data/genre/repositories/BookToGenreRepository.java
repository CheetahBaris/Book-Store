package com.example.MyBookShopApp.data.genre.repositories;

import com.example.MyBookShopApp.data.book.BookEntity;
import com.example.MyBookShopApp.data.book.links.Book2GenreEntity;
import com.example.MyBookShopApp.data.genre.GenreEntity;
import liquibase.pro.packaged.B;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookToGenreRepository extends JpaRepository<Book2GenreEntity, Long> {

    List<Book2GenreEntity> findBook2GenreEntitiesByBookId(BookEntity book);
    List<Book2GenreEntity> findBook2GenreEntitiesByGenreId(GenreEntity genre);
}
