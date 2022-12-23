package com.example.MyBookShopApp.services;

import com.example.MyBookShopApp.data.book.BookEntity;
import com.example.MyBookShopApp.data.book.links.Book2GenreEntity;
import com.example.MyBookShopApp.repositories.BookRepository;
import com.example.MyBookShopApp.data.genre.GenreEntity;
import com.example.MyBookShopApp.repositories.BookToGenreRepository;
import com.example.MyBookShopApp.repositories.GenresRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    public List<BookEntity> getBooksPageByGenre(String name, Integer offset, Integer limit){
        Pageable nextPage = PageRequest.of(offset,limit);
        List<Book2GenreEntity> books = findBook2GenreEntitiesByGenreId(findGenreEntitiesByName(name).getId());
        List<Long> booksId =  new ArrayList<>();
        for(Book2GenreEntity b:books){
             booksId.add(b.getBookId().getId());
        }
        PagedListHolder<BookEntity> pages = new PagedListHolder<>( bookRepository.findAllById(booksId));
        pages.setPageSize(limit);
        pages.setPage(offset);
        return pages.getPageList();
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
