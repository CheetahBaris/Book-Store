package com.example.MyBookShopApp.services;

import com.example.MyBookShopApp.data.book.BookEntity;
import com.example.MyBookShopApp.repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookService {
    private static final double CART_PERCENT=0.7;
    private static final double KEPT_PERCENT=0.4;
    private final BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
         this.bookRepository = bookRepository;

    }

    public List<BookEntity> getBooksData() {
        return bookRepository.findAll();
    }


//    public List<BookEntity> getBooksByAuthor(String authorName){
//        return bookRepository.findBooksByAuthorFirstNameContaining(authorName);
//    }

    public List<BookEntity> getBooksByTitle(String title){
        return bookRepository.findBooksByTitleContaining(title);
    }

    public List<BookEntity> getBooksWithPriceBetween(Integer min, Integer max){
        return bookRepository.findBooksByPriceBetween(min, max);
    }

    public List<BookEntity> getBooksWithPrice(Integer price){
        return bookRepository.findBooksByPriceIs(price);
    }

    public List<BookEntity> getBooksWithMaxPrice(){
        return bookRepository.getBooksWithMaxDiscount();
    }

    public List<BookEntity> getBestsellers(){
        return bookRepository.getBestsellers();
    }

    public Page<BookEntity> getPageOfRecommendedBooks(Integer offset, Integer limit){
        Pageable nextPage = PageRequest.of(offset,limit);
        return bookRepository.findAll(nextPage);
    }

    public Page<BookEntity> getPageOfSearchResultBooks(String searchWord, Integer offset, Integer limit){
        Pageable nextPage = PageRequest.of(offset,limit);
        return bookRepository.findBookByTitleContaining(searchWord,nextPage);
    }
    public Page<BookEntity> findBookByPubDateBetween(Date fromDateRecent, Date endDateRecent,
                                                       Integer offset, Integer limit){

         Pageable nextPage = PageRequest.of(offset,limit);
        return bookRepository.findBookByPubDateBetween(fromDateRecent,endDateRecent, nextPage);
    }

    public Page<BookEntity> findBookEntitiesByTagPage(String tag, Integer offset, Integer limit){
        Pageable nextPage = PageRequest.of(offset,limit);


        return bookRepository.findBookEntitiesByTag(tag, nextPage);
    }
    public Map<String, List<BookEntity>> getTagListMap(){

        return bookRepository.findAll().stream().collect(Collectors.groupingBy(BookEntity::getTag));
     }

//    public void getPageOfBooksByRating(){
//
//         for(BookEntity book :bookRepository.findAll()){
//
//            book.setRelevance(bookToUserRepository.getBoughtBooks(book.getId()).size()
//                    +CART_PERCENT*bookToUserRepository.getCartedBooks(book.getId()).size()
//                    + KEPT_PERCENT*bookToUserRepository.getKeptInBooks(book.getId()).size());
//            bookRepository.save(book);
//        }
//    }


    //    private final BookRepository bookRepository;
//
//    private final AuthorService authorService;
//    @Autowired
//    public BookService(BookRepository bookRepository, AuthorService authorService1) {
//        this.bookRepository = bookRepository;
//        this.authorService = authorService1;
//    }
//
//    public List<BooksEntity> getAllBooks() {
//        return bookRepository.findAll();
//    }
//
//    public boolean updateAuthorById(Long id, AuthorEntity authorEntity, String title, Integer price_old, Integer price) {
//
//        if (bookRepository.existsById(id)) {
//
//            BooksEntity b = bookRepository.findById(id).get();
//            b.setAuthorEntity(authorEntity);
//            b.setTitle(title);
//            b.setPriceOld(price_old);
//            b.setPrice(price);
//            bookRepository.save(b);
//            return true;
//
//        } else {
//
//            return false;
//        }
//    }
//
//    public void crateBook(AuthorEntity authorEntity, String title, Integer priceOld, Integer price) {
//        BooksEntity b = new BooksEntity();
//        b.setAuthorEntity(authorEntity);
//        b.setTitle(title);
//        b.setPriceOld(priceOld);
//        b.setPrice(price);
//        bookRepository.save(b);
//    }
//
//    public void deleteAllBooks() {
//        bookRepository.deleteAll();
//    }
//
//    public boolean deleteBookById(Long id) {
//        if (bookRepository.existsById(id)) {
//            bookRepository.deleteById(id);
//            return true;
//        } else {
//            return false;
//        }
//
//    }
 }
