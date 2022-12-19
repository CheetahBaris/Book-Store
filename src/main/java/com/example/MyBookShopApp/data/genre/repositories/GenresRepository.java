package com.example.MyBookShopApp.data.genre.repositories;

import com.example.MyBookShopApp.data.genre.GenreEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GenresRepository extends JpaRepository<GenreEntity, Long> {
    GenreEntity findGenreEntitiesByName(String name);
    List<GenreEntity> findGenreEntitiesByParentId(Long parentId);
}
