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
        {return a.getLastName().substring(0,1);}));

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
            a.setFirstName(first_name);
            a.setLastName(last_name);
            authorEntityCrudRepository.save(a);
            return true;

        }else {

            return false;
        }
    }

    public  void crateAuthor(String first_name, String last_name){
        AuthorEntity a = new AuthorEntity();
        a.setFirstName(first_name);
        a.setLastName(last_name);
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
