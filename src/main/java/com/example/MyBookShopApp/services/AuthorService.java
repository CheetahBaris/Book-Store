package com.example.MyBookShopApp.services;

import com.example.MyBookShopApp.data.author.AuthorEntity;
import com.example.MyBookShopApp.repositories.AuthorEntityRepository;
import com.example.MyBookShopApp.repositories.BookToAuthorRepository;
import com.example.MyBookShopApp.data.book.BookEntity;
import com.example.MyBookShopApp.data.book.links.Book2AuthorEntity;
 import com.example.MyBookShopApp.repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
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



//    public Map<String,BookEntity> getBooksWithAuthor(String name, Integer offset, Integer limit){
//
//        List<Book2AuthorEntity> book2AuthorEntities = bookToAuthorRepository.findBook2AuthorEntitiesByAuthorId(
//                authorEntityRepository.findAuthorEntitiesByName(name));
//
//        HashMap<String,BookEntity> bookIds = new HashMap<>();
//
//        for (int i=0; i<book2AuthorEntities.size();i++){
//            StringBuilder authorBuilder = new StringBuilder();
//            for (Book2AuthorEntity b: bookToAuthorRepository.findBook2AuthorEntitiesByBookId(book2AuthorEntities.get(i).getBookId())){
//                authorBuilder.append(b.getAuthorId().getName()).append(", ");
//            }
//            bookIds.put(authorBuilder.toString(),book2AuthorEntities.get(i).getBookId());
//
//        }
//
//        PagedListHolder<BookEntity> pagedListHolder = new PagedListHolder<>(new ArrayList<>(bookIds.values()));
//        pagedListHolder.setPageSize(limit);
//        pagedListHolder.setPage(offset);
//        HashMap<String,BookEntity> bookIds1 = new HashMap<>();
//
//        for(int i=0;i<pagedListHolder.getPageList().size();i++){
//            for(Map.Entry<String, BookEntity> entityEntry: bookIds.entrySet()){
//                if(entityEntry.getValue().equals(pagedListHolder.getPageList().get(i))){
//
//                    bookIds1.put(entityEntry.getKey().substring(0,entityEntry.getKey().trim().length()-1),
//                            pagedListHolder.getPageList().get(i));
//                }
//            }
//        }
//
//        return  bookIds1;
//    }
    public List<BookEntity> converterListToMapWithAuthors(List<BookEntity> bookEntityList, Integer offset, Integer limit){


        HashMap<String,BookEntity> bookIds = new HashMap<>();

        for (int i=0; i<bookEntityList.size();i++){
            StringBuilder authorBuilder = new StringBuilder();
            for (Book2AuthorEntity b: bookToAuthorRepository.findBook2AuthorEntitiesByBookId(bookEntityList.get(i))){
                authorBuilder.append(b.getAuthorId().getName()).append(", ");
            }
            bookEntityList.get(i).setAuthorsNames(authorBuilder.substring(0,authorBuilder.toString().trim().length()-1));
            bookRepository.save(bookEntityList.get(i));
            bookIds.put(authorBuilder.toString(),bookEntityList.get(i));

        }

        PagedListHolder<BookEntity> pagedListHolder = new PagedListHolder<>(new ArrayList<>(bookIds.values()));
        pagedListHolder.setPageSize(limit);
        pagedListHolder.setPage(offset);

        return  pagedListHolder.getPageList();
    }

    public List<BookEntity> getBookEntitiesByAuthorName(String name, Integer offset, Integer limit){

       List<Book2AuthorEntity> book2AuthorEntities = bookToAuthorRepository.findBook2AuthorEntitiesByAuthorId(
               authorEntityRepository.findAuthorEntitiesByName(name));
       List<Long> bookIds = new ArrayList<>();
       for (Book2AuthorEntity book: book2AuthorEntities){
           bookIds.add(book.getBookId().getId());
       }

       getBookEntitiesByAuthorNameSize = bookRepository.findAllById(bookIds).size();

 //       PagedListHolder<BookEntity> pagedListHolder = new PagedListHolder<>(bookRepository.findAllById(bookIds));
//       pagedListHolder.setPageSize(limit);
//       pagedListHolder.setPage(offset);


        return  converterListToMapWithAuthors(bookRepository.findAllById(bookIds),offset,limit);
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
