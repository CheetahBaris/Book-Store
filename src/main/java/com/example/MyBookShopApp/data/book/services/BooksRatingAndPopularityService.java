package com.example.MyBookShopApp.data.book.services;


import com.example.MyBookShopApp.data.book.BookEntity;
import com.example.MyBookShopApp.data.book.links.Book2UserEntity;
import com.example.MyBookShopApp.data.book.repositories.BookRepository;
import com.example.MyBookShopApp.data.book.repositories.BookToUserRepository;
import com.example.MyBookShopApp.data.dto.BooksPageDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BooksRatingAndPopularityService {
    private static final double CART_PERCENT=0.7;
    private static final double KEPT_PERCENT=0.4;


    private final BookToUserRepository bookToUserRepository;
    private final BookRepository bookRepository;
    @Autowired
    public BooksRatingAndPopularityService(BookToUserRepository bookToUserRepository,
                                           BookRepository bookRepository) {
        this.bookToUserRepository = bookToUserRepository;
        this.bookRepository = bookRepository;
    }



    public void getPageOfBooksByRating(){

          for(BookEntity book :bookRepository.findAll()){

//              BookEntity bookEntity = new BookEntity();
//              bookEntity.setId(book.getId());
//              bookEntity.setAuthor(book.getAuthor());
//              bookEntity.setDescription(book.getDescription());
//              bookEntity.setDiscount(book.getDiscount());
//              bookEntity.setImage(book.getImage());
//              bookEntity.setIsBestseller(book.getIsBestseller());
//              bookEntity.setPrice(book.getPrice());
//              bookEntity.setPubDate(book.getPubDate());
//              bookEntity.setSlug(book.getSlug());
//              bookEntity.setTitle(book.getTitle());
              book.setRelevance(bookToUserRepository.findBook2UserEntitiesByBookIdAndTypeId(book, 3).size()
                    +CART_PERCENT*bookToUserRepository.findBook2UserEntitiesByBookIdAndTypeId(book, 2).size()
                    + KEPT_PERCENT*bookToUserRepository.findBook2UserEntitiesByBookIdAndTypeId(book, 1).size());
             bookRepository.save(book);
//              System.out.println(bookToUserRepository.findBook2UserEntitiesById(2L));

          }
    }

    public Page<BookEntity> getBookByRelevanceAsc(Integer offset, Integer limit){
        Pageable nextPage = PageRequest.of(offset,limit);

        return bookRepository.getBookEntityFromRelevance(nextPage);
    }

}
