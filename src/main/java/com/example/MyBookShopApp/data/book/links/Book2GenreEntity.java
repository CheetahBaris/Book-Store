package com.example.MyBookShopApp.data.book.links;

import com.example.MyBookShopApp.data.book.BookEntity;
import com.example.MyBookShopApp.data.genre.GenreEntity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "book2genre")
public class Book2GenreEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne
    @JoinColumn(name = "book_id", referencedColumnName = "id")
    private BookEntity bookId;

    @OneToOne
    @JoinColumn(name = "genre_id", referencedColumnName = "id")
    private GenreEntity genreId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BookEntity getBookId() {
        return bookId;
    }

    public void setBookId(BookEntity bookId) {
        this.bookId = bookId;
    }

    public GenreEntity getGenreId() {
        return genreId;
    }

    public void setGenreId(GenreEntity genreId) {
        this.genreId = genreId;
    }
}
