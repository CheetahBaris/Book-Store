package com.example.MyBookShopApp.services;

import com.example.MyBookShopApp.data.book.BookEntity;
import com.example.MyBookShopApp.errs.BookstoreApiWrongParameterException;
import com.example.MyBookShopApp.repositories.BookRepository;
import liquibase.pro.packaged.L;
import liquibase.pro.packaged.S;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookService {
    private static final double CART_PERCENT = 0.7;
    private static final double KEPT_PERCENT = 0.4;
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

    public List<BookEntity> getBooksByTitle(String title) throws BookstoreApiWrongParameterException {
        if (title.length() <= 1) {

            throw new BookstoreApiWrongParameterException("Wrong values passed to one or more parameters");
        } else {
            List<BookEntity> data = bookRepository.findBooksByTitleContaining(title);
            if (data.size() > 0) {

                return data;
            } else {

                throw new BookstoreApiWrongParameterException("No data found with specified parameters");
            }
        }
    }

    public List<BookEntity> getBooksWithPriceBetween(Integer min, Integer max) throws BookstoreApiWrongParameterException {
        if (min > max) {

            throw new BookstoreApiWrongParameterException("Wrong values passed to one or more parameters");
        } else {
            List<BookEntity> data = bookRepository.findBooksByPriceBetween(min, max);
            if (data.size() > 0) {

                return data;
            } else {

                throw new BookstoreApiWrongParameterException("No data found with specified parameters");
            }
        }
    }

    public List<BookEntity> getBooksWithPrice(Integer price) throws BookstoreApiWrongParameterException {

            List<BookEntity> data =bookRepository.findBooksByPriceIs(price);
            if (data.size() > 0) {

                return data;
            } else {

                throw new BookstoreApiWrongParameterException("No data found with specified parameters");
            }
    }

    public List<BookEntity> getBooksWithMaxPrice() {
        return bookRepository.getBooksWithMaxDiscount();
    }

    public List<BookEntity> getBestsellers() throws BookstoreApiWrongParameterException {

            List<BookEntity> data =bookRepository.getBestsellers();
            if (data.size() > 0) {

                return data;
            } else {

                throw new BookstoreApiWrongParameterException("No data found with specified parameters");
         }
    }

    public Page<BookEntity> getPageOfRecommendedBooks(Integer offset, Integer limit) throws BookstoreApiWrongParameterException {
        Pageable nextPage = PageRequest.of(offset, limit);
         Page<BookEntity> data= bookRepository.findAll(nextPage);
            if (data.getContent().size() > 0) {

                return data;
            } else {

                throw new BookstoreApiWrongParameterException("No data found with specified parameters");
            }
        }


    public Page<BookEntity> getPageOfSearchResultBooks(String searchWord, Integer offset, Integer limit) throws BookstoreApiWrongParameterException {
        if (searchWord == null ) {

            throw new BookstoreApiWrongParameterException("Wrong values passed to one or more parameters");
        } else {
            Pageable nextPage = PageRequest.of(offset, limit);
            Page<BookEntity> data = bookRepository.findBookByTitleContaining(searchWord, nextPage);

            if (data.getContent().size() > 0) {

                return data;
            } else {

                throw new BookstoreApiWrongParameterException("No data found with specified parameters");
            }
        }

    }

    public Page<BookEntity> findBookByPubDateBetween(Date fromDateRecent, Date endDateRecent,
                                                     Integer offset, Integer limit) throws BookstoreApiWrongParameterException {
        if (fromDateRecent.after(endDateRecent)) {

            throw new BookstoreApiWrongParameterException("Wrong values passed to one or more parameters");
        } else {
            Pageable nextPage = PageRequest.of(offset, limit);
            Page<BookEntity> data = bookRepository.findBookByPubDateBetween(fromDateRecent, endDateRecent, nextPage);
            if (data.getContent().size() > 0) {

                return data;
            } else {

                throw new BookstoreApiWrongParameterException("No data found with specified parameters");
            }
        }



    }


    public Page<BookEntity> findBookEntitiesByTagPage(String tag, Integer offset, Integer limit) throws BookstoreApiWrongParameterException {
        if (tag.length() <= 1) {

            throw new BookstoreApiWrongParameterException("Wrong values passed to one or more parameters");
        } else {
            Pageable nextPage = PageRequest.of(offset, limit);
            Page<BookEntity> data = bookRepository.findBookEntitiesByTag(tag, nextPage);
            if (data.getContent().size() > 0) {

                return data;
            } else {

                throw new BookstoreApiWrongParameterException("No data found with specified parameters");
            }
        }

    }

    public Map<String, List<BookEntity>> getTagListMap() throws BookstoreApiWrongParameterException {

        Map<String, List<BookEntity>> data = bookRepository.findAll().stream().collect(Collectors.groupingBy(BookEntity::getTag));
        if (data.size() > 0) {

            return data;
        } else {

            throw new BookstoreApiWrongParameterException("No data found with specified parameters");
        }
    }

    public BookEntity getBookBySlug(String slug) throws BookstoreApiWrongParameterException {



            return bookRepository.findBookEntitiesBySlug(slug);

     }
    public List<BookEntity> getBookEntitiesBySlugIn(List<String> slug) throws BookstoreApiWrongParameterException{


            return bookRepository.findBookEntitiesBySlugIn(slug);

    }

    public void saveBookEntity(BookEntity book) throws BookstoreApiWrongParameterException {
        if (!bookRepository.findById(book.getId()).isPresent()) {
            bookRepository.save(book);
        } else {

            throw new BookstoreApiWrongParameterException("No data found with specified parameters");
        }
    }

    public void saveBooksListEntity(List<BookEntity> books) throws BookstoreApiWrongParameterException {

        if (!new HashSet<>(bookRepository.findAll()).containsAll(books)) {
            bookRepository.saveAll(books);

        } else {

            throw new BookstoreApiWrongParameterException("No data found with specified parameters");
        }
    }
}

