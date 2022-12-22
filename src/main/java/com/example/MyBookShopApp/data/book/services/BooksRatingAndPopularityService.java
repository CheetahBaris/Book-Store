package com.example.MyBookShopApp.data.book.services;


import com.example.MyBookShopApp.data.book.BookEntity;
import com.example.MyBookShopApp.data.book.repositories.BookRepository;
import com.example.MyBookShopApp.data.book.repositories.BookToUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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


    @EventListener(ApplicationReadyEvent.class)
    public void getPageOfBooksByRating(){

          for(BookEntity book :bookRepository.findAll()){

//
              book.setRelevance(bookToUserRepository.findBook2UserEntitiesByBookIdAndTypeId(book, 3).size()
                    +CART_PERCENT*bookToUserRepository.findBook2UserEntitiesByBookIdAndTypeId(book, 2).size()
                    + KEPT_PERCENT*bookToUserRepository.findBook2UserEntitiesByBookIdAndTypeId(book, 1).size());
             bookRepository.save(book);
          }
    }

    public Page<BookEntity> getBookByRelevanceDesc(Integer offset, Integer limit){
        Pageable nextPage = PageRequest.of(offset,limit);

        return bookRepository.getBookEntityFromRelevance(nextPage);
    }

}
