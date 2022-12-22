package com.example.MyBookShopApp.repositories;

import com.example.MyBookShopApp.data.author.AuthorEntity;
import liquibase.pro.packaged.S;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorEntityRepository extends JpaRepository<AuthorEntity,Long> {


    AuthorEntity findAuthorEntitiesByName(String name);

}
