package com.example.MyBookShopApp.services;

import com.example.MyBookShopApp.data.author.AuthorEntity;
import com.example.MyBookShopApp.data.book.BookEntity;
import com.example.MyBookShopApp.data.book.links.Book2AuthorEntity;
import com.example.MyBookShopApp.data.book.links.Book2GenreEntity;
import com.example.MyBookShopApp.data.genre.GenreEntity;
import com.example.MyBookShopApp.errs.BookstoreApiWrongParameterException;
import com.example.MyBookShopApp.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookService {
    private static final double CART_PERCENT = 0.7;
    private static final double KEPT_PERCENT = 0.4;
    private final BookRepository bookRepository;
    private  final BookToAuthorRepository bookToAuthorRepository;
    private final BookToGenreRepository bookToGenreRepository;

    private final AuthorEntityRepository authorEntityRepository;
    private final GenresRepository genresRepository;

    @Autowired
    public BookService(BookRepository bookRepository,
                       BookToAuthorRepository bookToAuthorRepository,
                       BookToGenreRepository bookToGenreRepository, AuthorEntityRepository authorEntityRepository, GenresRepository genresRepository) {
        this.bookRepository = bookRepository;
        this.bookToAuthorRepository = bookToAuthorRepository;

        this.bookToGenreRepository = bookToGenreRepository;
        this.authorEntityRepository = authorEntityRepository;
        this.genresRepository = genresRepository;
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

    public List<BookEntity> getPageOfRecommendedBooks(String[] postponed, String[] cart,
                                                      Integer offset, Integer limit) throws BookstoreApiWrongParameterException {

        Set<BookEntity> allBooksFromUser = new HashSet<>();
        Set<String> tagsFromUserBooks = new HashSet<>();
        if(postponed != null){
            allBooksFromUser.addAll(bookRepository.findBookEntitiesBySlugIn(List.of(postponed)));
        }
        if(cart !=null){
            allBooksFromUser.addAll(bookRepository.findBookEntitiesBySlugIn(List.of(cart)));
        }


        if(allBooksFromUser.isEmpty()){

            PagedListHolder<BookEntity> bookEntityPagedListHolder = new PagedListHolder<>(
                    new ArrayList<>(bookRepository.getTopRelevanceList()));
            bookEntityPagedListHolder.setPage(offset);
            bookEntityPagedListHolder.setPageSize(limit);
            return bookEntityPagedListHolder.getPageList();

        }else {

            for (BookEntity book : allBooksFromUser) {
                tagsFromUserBooks.add(book.getTag());
            }

            List<Book2AuthorEntity> bookToAuthorEntitiesList = bookToAuthorRepository.
                    findBook2AuthorEntitiesByBookIdIn(new ArrayList<>(allBooksFromUser));

            List<Book2GenreEntity> bookToGenreEntitiesList = bookToGenreRepository.
                    findBook2GenreEntitiesByBookIdIn(new ArrayList<>(allBooksFromUser));

            List<GenreEntity> genreEntities = new ArrayList<>();
            List<AuthorEntity> authorEntities = new ArrayList<>();


            for (Book2GenreEntity bookToGenre : bookToGenreEntitiesList) {
                genreEntities.add(bookToGenre.getGenreId());
            }
            for (Book2AuthorEntity book2Author : bookToAuthorEntitiesList) {
                authorEntities.add(book2Author.getAuthorId());
            }

            bookToGenreEntitiesList = bookToGenreRepository.findBook2GenreEntitiesByGenreIdIn(genreEntities);
            bookToAuthorEntitiesList = bookToAuthorRepository.findBook2AuthorEntitiesByAuthorIdIn(authorEntities);



            //Author & Genre Books
            for (Book2GenreEntity bookToGenre : bookToGenreEntitiesList) {
                for (Book2AuthorEntity book2Author : bookToAuthorEntitiesList) {
                     if(bookToGenre.getBookId().equals(book2Author.getBookId())){

                        allBooksFromUser.add(book2Author.getBookId());
                    }
                }
            }

            //Author & tag Books
                 for (String tag : tagsFromUserBooks) {
                     for (Book2AuthorEntity book2Author : bookToAuthorEntitiesList) {

                         if(book2Author.getBookId().getTag().equals(tag)){
                        allBooksFromUser.add(book2Author.getBookId());
                    }
                }
            }

            PagedListHolder<BookEntity> bookEntityPagedListHolder = new PagedListHolder<>();
            bookEntityPagedListHolder.setPage(offset);
            bookEntityPagedListHolder.setPageSize(limit);

            if (allBooksFromUser.size() >= 6) {
                bookEntityPagedListHolder.setSource(new ArrayList<>(allBooksFromUser));

            } else {
                bookEntityPagedListHolder.setSource(new ArrayList<>(bookRepository.getTopRelevanceList()));
            }

            return bookEntityPagedListHolder.getPageList();
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

    public Page<BookEntity> findBookByPubDateBetween(LocalDate fromDateRecent, LocalDate endDateRecent,
                                                     Integer offset, Integer limit) throws BookstoreApiWrongParameterException {
        if (fromDateRecent.isAfter(endDateRecent)) {

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

