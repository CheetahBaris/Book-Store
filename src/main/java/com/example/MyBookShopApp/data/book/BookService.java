package com.example.MyBookShopApp.data.book;

import com.example.MyBookShopApp.data.author.AuthorEntity;
import com.example.MyBookShopApp.data.author.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private final BookCrudRepository bookCrudRepository;

    private final AuthorService authorService;
    @Autowired
    public BookService(BookCrudRepository bookCrudRepository, AuthorService authorService1) {
        this.bookCrudRepository = bookCrudRepository;
        this.authorService = authorService1;
    }

    public List<BooksEntity> getAllBooks() {
        return bookCrudRepository.findAll();
    }

    public boolean updateAuthorById(Long id, AuthorEntity authorEntity, String title, Integer price_old, Integer price) {

        if (bookCrudRepository.existsById(id)) {

            BooksEntity b = bookCrudRepository.findById(id).get();
            b.setAuthorEntity(authorEntity);
            b.setTitle(title);
            b.setPriceOld(price_old);
            b.setPrice(price);
            bookCrudRepository.save(b);
            return true;

        } else {

            return false;
        }
    }

    public void crateBook(AuthorEntity authorEntity, String title, Integer priceOld, Integer price) {
        BooksEntity b = new BooksEntity();
        b.setAuthorEntity(authorEntity);
        b.setTitle(title);
        b.setPriceOld(priceOld);
        b.setPrice(price);
        bookCrudRepository.save(b);
    }

    public void deleteAllBooks() {
        bookCrudRepository.deleteAll();
    }

    public boolean deleteBookById(Long id) {
        if (bookCrudRepository.existsById(id)) {
            bookCrudRepository.deleteById(id);
            return true;
        } else {
            return false;
        }

    }
}
