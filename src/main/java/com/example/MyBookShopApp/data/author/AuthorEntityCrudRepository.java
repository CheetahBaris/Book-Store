package com.example.MyBookShopApp.data.author;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorEntityCrudRepository extends JpaRepository<AuthorEntity,Long> {
}
