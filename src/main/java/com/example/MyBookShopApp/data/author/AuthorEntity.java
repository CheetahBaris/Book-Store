package com.example.MyBookShopApp.data.author;

import com.example.MyBookShopApp.data.book.BookEntity;
import com.example.MyBookShopApp.data.book.BooksEntity;
import com.example.MyBookShopApp.data.book.links.Book2AuthorEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "authors")
public class AuthorEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String first_name;
    private String last_name;
    @OneToMany(mappedBy = "authorEntity")
    private List<BooksEntity> bookEntityList = new ArrayList<>();

    @OneToOne(mappedBy = "authorId")
    private Book2AuthorEntity book2AuthorEntity;


    public List<BooksEntity> getBookEntityList() {
        return bookEntityList;
    }

    public void setBookEntityList(List<BooksEntity> bookEntityList) {
        this.bookEntityList = bookEntityList;
    }

    public Book2AuthorEntity getBook2AuthorEntity() {
        return book2AuthorEntity;
    }

    public void setBook2AuthorEntity(Book2AuthorEntity book2AuthorEntity) {
        this.book2AuthorEntity = book2AuthorEntity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }
}
