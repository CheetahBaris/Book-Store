package com.example.MyBookShopApp.data.book.file;

import com.example.MyBookShopApp.data.book.BookEntity;
import com.example.MyBookShopApp.data.user.UserEntity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "file_download")
public class FileDownloadEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne
    @JoinColumn(name = "user_id",referencedColumnName = "id")
    private UserEntity userId;

    @OneToMany
    @JoinColumn(name = "book_id",referencedColumnName = "id")
    private List<BookEntity> bookId;

    @Column(columnDefinition = "INT NOT NULL DEFAULT 1")
    private int count;




    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UserEntity getUserId() {
        return userId;
    }

    public void setUserId(UserEntity userId) {
        this.userId = userId;
    }

    public List<BookEntity> getBookId() {
        return bookId;
    }

    public void setBookId(List<BookEntity> bookId) {
        this.bookId = bookId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
