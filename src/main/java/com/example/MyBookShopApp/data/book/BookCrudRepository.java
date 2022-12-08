package com.example.MyBookShopApp.data.book;

import org.springframework.data.jpa.repository.JpaRepository;
 import org.springframework.stereotype.Repository;

@Repository
public interface BookCrudRepository extends JpaRepository<BooksEntity,Long> {
}
