package com.example.MyBookShopApp.data.author.services;

import com.example.MyBookShopApp.data.author.AuthorEntity;
import com.example.MyBookShopApp.data.author.repositoties.AuthorEntityRepository;
import com.example.MyBookShopApp.data.author.repositoties.BookToAuthorRepository;
import com.example.MyBookShopApp.data.book.BookEntity;
import com.example.MyBookShopApp.data.book.links.Book2AuthorEntity;
 import com.example.MyBookShopApp.data.book.repositories.BookRepository;
import liquibase.pro.packaged.S;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
 import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AuthorService {

    private final AuthorEntityRepository authorEntityRepository;
    private final BookRepository bookRepository;
    private final  BookToAuthorRepository bookToAuthorRepository;
    private Integer getBookEntitiesByAuthorNameSize;


    @Autowired
    public AuthorService(AuthorEntityRepository authorEntityRepository,
                         BookRepository bookRepository, BookToAuthorRepository bookToAuthorRepository) {
        this.authorEntityRepository = authorEntityRepository;
        this.bookRepository = bookRepository;
        this.bookToAuthorRepository = bookToAuthorRepository;
    }

    public Map<String, List<AuthorEntity>> getAuthorsMap() {

        return   authorEntityRepository.findAll().stream().collect(Collectors.groupingBy((AuthorEntity a) ->
        {
            return a.getName().substring(0, 1);
        }));

    }

    public AuthorEntity findAuthorEntitiesByName(String name){

     return authorEntityRepository.findAuthorEntitiesByName(name);
    }



    public Map<String,BookEntity> getBooksWithAuthor(String name, Integer offset, Integer limit){

//         authorEntityRepository.findAuthorEntitiesByName(name);

        List<Book2AuthorEntity> book2AuthorEntities = bookToAuthorRepository.findBook2AuthorEntitiesByAuthorId(
                authorEntityRepository.findAuthorEntitiesByName(name));
        HashMap<String,Long> bookIds = new HashMap<>();
        HashMap<String,Long> bookIds1 = new HashMap<>();

        List<Book2AuthorEntity> bookEntityList = new ArrayList<>();
         for (Book2AuthorEntity book: book2AuthorEntities){
             bookEntityList.addAll(bookToAuthorRepository.findBook2AuthorEntitiesByBookId(book.getBookId()));

            bookIds.put(getAuthorById(book.getAuthorId().getId()).getName(),book.getBookId().getId());
        }
        for (int i= 0; i<book2AuthorEntities.size();i++){
            
             bookIds1.put(bookEntityList.get(i).getAuthorId().getName(),book2AuthorEntities.get(i).getBookId().getId());
        }
        getBookEntitiesByAuthorNameSize = bookRepository.findAllById(bookIds.values()).size();

        PagedListHolder<BookEntity> pagedListHolder = new PagedListHolder<>(bookRepository.findAllById(bookIds.values()));
        pagedListHolder.setPageSize(limit);
        pagedListHolder.setPage(offset);
        Map<String, BookEntity> list = new HashMap<>();


        for(int i=0;i<pagedListHolder.getPageList().size();i++){
            for(Map.Entry<String, Long> b: bookIds.entrySet()){
                if(pagedListHolder.getPageList().get(i).getId().equals(b.getValue())){
                    list.put(b.getKey(),pagedListHolder.getPageList().get(i));
                }
            }
         }
        return  list;
    }

    public List<BookEntity> getBookEntitiesByAuthorName(String name, Integer offset, Integer limit){
//        Pageable nextPage = PageRequest.of(offset, limit);
//        authorEntityRepository.findAuthorEntitiesByName(name);

       List<Book2AuthorEntity> book2AuthorEntities = bookToAuthorRepository.findBook2AuthorEntitiesByAuthorId(
               authorEntityRepository.findAuthorEntitiesByName(name));
       List<Long> bookIds = new ArrayList<>();
       for (Book2AuthorEntity book: book2AuthorEntities){
           bookIds.add(book.getBookId().getId());
       }

       getBookEntitiesByAuthorNameSize = bookRepository.findAllById(bookIds).size();

       PagedListHolder<BookEntity> pagedListHolder = new PagedListHolder<>(bookRepository.findAllById(bookIds));
       pagedListHolder.setPageSize(limit);
       pagedListHolder.setPage(offset);


        return  pagedListHolder.getPageList();
    }

    public AuthorEntity getAuthorById(Long id) {
        return authorEntityRepository.findById(id).get();
    }

    public List<AuthorEntity> getAllAuthors() {
        return authorEntityRepository.findAll();
    }

    public boolean updateAuthorById(Long id, String name) {

        if (authorEntityRepository.existsById(id)) {

            AuthorEntity a = authorEntityRepository.findById(id).get();
            a.setName(name);
            authorEntityRepository.save(a);
            return true;

        } else {

            return false;
        }
    }

    public void crateAuthor(String name) {
        AuthorEntity a = new AuthorEntity();
        a.setName(name);
        authorEntityRepository.save(a);
    }

    public void deleteAllAuthors() {
        authorEntityRepository.deleteAll();
    }

    public boolean deleteAuthorById(Long id) {
        if (authorEntityRepository.existsById(id)) {
            authorEntityRepository.deleteById(id);
            return true;
        } else {
            return false;
        }

    }

    public Integer getGetBookEntitiesByAuthorNameSize() {
        return getBookEntitiesByAuthorNameSize;
    }

    public void setGetBookEntitiesByAuthorNameSize(Integer getBookEntitiesByAuthorNameSize) {
        this.getBookEntitiesByAuthorNameSize = getBookEntitiesByAuthorNameSize;
    }
}
