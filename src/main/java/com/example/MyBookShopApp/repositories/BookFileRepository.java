package com.example.MyBookShopApp.repositories;

import com.example.MyBookShopApp.data.book.BookFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookFileRepository extends JpaRepository<BookFileEntity, Integer> {

    public BookFileEntity findBookFileByHash(String hash);
}