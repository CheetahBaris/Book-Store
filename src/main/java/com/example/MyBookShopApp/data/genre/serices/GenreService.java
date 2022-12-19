package com.example.MyBookShopApp.data.genre.serices;

import com.example.MyBookShopApp.data.book.BookEntity;
import com.example.MyBookShopApp.data.book.links.Book2GenreEntity;
import com.example.MyBookShopApp.data.book.repositories.BookRepository;
import com.example.MyBookShopApp.data.genre.GenreEntity;
import com.example.MyBookShopApp.data.genre.repositories.BookToGenreRepository;
import com.example.MyBookShopApp.data.genre.repositories.GenresRepository;
import liquibase.pro.packaged.A;
import liquibase.pro.packaged.P;
import liquibase.pro.packaged.S;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class GenreService {
    private final BookRepository bookRepository;
    private final GenresRepository genresRepository;
    private final BookToGenreRepository bookToGenreRepository;

    @Autowired
    public GenreService(BookRepository bookRepository, GenresRepository genresRepository, BookToGenreRepository bookToGenreRepository) {
        this.bookRepository = bookRepository;
        this.genresRepository = genresRepository;
        this.bookToGenreRepository = bookToGenreRepository;
    }


    public List<GenreEntity> getAllGenres(){
        return genresRepository.findAll();
    }
    public GenreEntity findGenreEntitiesByName(String name){
        return genresRepository.findGenreEntitiesByName(name);
    }

    public Page<BookEntity> getBooksPageByGenre(String name, Integer offset, Integer limit){
        Pageable nextPage = PageRequest.of(offset,limit);
        List<Book2GenreEntity> books = findBook2GenreEntitiesByGenreId(findGenreEntitiesByName(name).getId());
        List<Long> booksId =  new ArrayList<>();
        for(Book2GenreEntity b:books){
             booksId.add(b.getId());
        }

        return new PageImpl<>(bookRepository.findAllById(booksId), nextPage, bookRepository.findAllById(booksId).size());
    }

    public List<GenreEntity> findGenreEntitiesByParentId(Long parentId){
        return genresRepository.findGenreEntitiesByParentId(parentId);
    }

    public List<Book2GenreEntity> getAllBook2GenreEntities(){
        return bookToGenreRepository.findAll();
    }
    public List<Book2GenreEntity> findBook2GenreEntitiesByGenreId(Long genreId){
        GenreEntity genre = genresRepository.findById(genreId).get();

        return bookToGenreRepository.findBook2GenreEntitiesByGenreId(genre);
    }

    public List<Book2GenreEntity> findBook2GenreEntitiesByBookId(Long bookId){
        BookEntity book = bookRepository.findById(bookId).get();
        return bookToGenreRepository.findBook2GenreEntitiesByBookId(book);
    }



}
