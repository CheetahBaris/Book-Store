package com.example.MyBookShopApp.data.author;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AuthorService {

 private final AuthorEntityCrudRepository authorEntityCrudRepository;

    @Autowired
    public AuthorService(AuthorEntityCrudRepository authorEntityCrudRepository) {
        this.authorEntityCrudRepository = authorEntityCrudRepository;
     }

        public Map<String, List<AuthorEntity>> getAuthorsMap() {
        return authorEntityCrudRepository.findAll().stream().collect(Collectors.groupingBy((AuthorEntity a) ->
        {return a.getLast_name().substring(0,1);}));

    }
    public AuthorEntity getAuthorById(Long id){
        return authorEntityCrudRepository.findById(id).get();
    }
    public List<AuthorEntity> getAllAuthors(){
        return authorEntityCrudRepository.findAll();
    }
    public boolean updateAuthorById(Long id, String first_name, String last_name){

        if(authorEntityCrudRepository.existsById(id)){

            AuthorEntity a = authorEntityCrudRepository.findById(id).get();
            a.setFirst_name(first_name);
            a.setLast_name(last_name);
            authorEntityCrudRepository.save(a);
            return true;

        }else {

            return false;
        }
    }

    public  void crateAuthor(String first_name, String last_name){
        AuthorEntity a = new AuthorEntity();
        a.setFirst_name(first_name);
        a.setLast_name(last_name);
        authorEntityCrudRepository.save(a);
     }
    public void deleteAllAuthors(){
        authorEntityCrudRepository.deleteAll();
    }
    public boolean deleteAuthorById(Long id){
        if(authorEntityCrudRepository.existsById(id)){
            authorEntityCrudRepository.deleteById(id);
            return true;
        }else {
            return false;
        }

    }

}
