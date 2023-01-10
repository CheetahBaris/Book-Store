package com.example.MyBookShopApp.data.user;

import com.example.MyBookShopApp.data.author.AuthorEntity;
import com.example.MyBookShopApp.data.book.BookEntity;
import com.example.MyBookShopApp.data.book.file.FileDownloadEntity;
import com.example.MyBookShopApp.data.book.links.Book2UserEntity;
import com.example.MyBookShopApp.data.book.review.BookReviewEntity;
import com.example.MyBookShopApp.data.book.review.BookReviewLikeEntity;
import com.example.MyBookShopApp.data.book.review.MessageEntity;
import com.example.MyBookShopApp.data.genre.GenreEntity;
import com.example.MyBookShopApp.data.payments.BalanceTransactionEntity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String hash;

    @Column(columnDefinition = "TIMESTAMP(6) NOT NULL")
    private LocalDateTime regTime;

    @Column(columnDefinition = "INT NOT NULL")
    private int balance;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String name;

    private String email;
    private String phone;
    private String password;


 //    @ManyToMany
//    @JoinTable( name="book2user",
//            joinColumns= @JoinColumn(name="user_id", referencedColumnName="id"),
//            inverseJoinColumns= @JoinColumn(name="book_id", referencedColumnName="id" ))
//    public List<BookEntity> books;

//    @OneToOne(mappedBy = "userId")
//    private Book2UserEntity book2UserEntity;
//    @OneToOne(mappedBy = "userId")
//    private FileDownloadEntity fileDownloadEntity;
//
//    @OneToOne(mappedBy = "userId")
//    private BalanceTransactionEntity balanceTransactionEntity;
//
//    @OneToOne(mappedBy = "userId")
//    private BookReviewEntity bookReviewEntity;
//    @OneToOne(mappedBy = "userId")
//    private BookReviewLikeEntity bookReviewLikeEntity;
//
//    @OneToOne(mappedBy = "userId")
//    private UserContactEntity userContactEntity;
//
//    @OneToOne(mappedBy = "userId")
//    private MessageEntity messageEntity;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public LocalDateTime getRegTime() {
        return regTime;
    }

    public void setRegTime(LocalDateTime regTime) {
        this.regTime = regTime;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    //    public Book2UserEntity getBook2UserEntity() {
//        return book2UserEntity;
//    }
//
//    public void setBook2UserEntity(Book2UserEntity book2UserEntity) {
//        this.book2UserEntity = book2UserEntity;
//    }
//
//    public FileDownloadEntity getFileDownloadEntity() {
//        return fileDownloadEntity;
//    }
//
//    public void setFileDownloadEntity(FileDownloadEntity fileDownloadEntity) {
//        this.fileDownloadEntity = fileDownloadEntity;
//    }
//
//    public BalanceTransactionEntity getBalanceTransactionEntity() {
//        return balanceTransactionEntity;
//    }
//
//    public void setBalanceTransactionEntity(BalanceTransactionEntity balanceTransactionEntity) {
//        this.balanceTransactionEntity = balanceTransactionEntity;
//    }
//
//    public BookReviewEntity getBookReviewEntity() {
//        return bookReviewEntity;
//    }
//
//    public void setBookReviewEntity(BookReviewEntity bookReviewEntity) {
//        this.bookReviewEntity = bookReviewEntity;
//    }
//
//    public BookReviewLikeEntity getBookReviewLikeEntity() {
//        return bookReviewLikeEntity;
//    }
//
//    public void setBookReviewLikeEntity(BookReviewLikeEntity bookReviewLikeEntity) {
//        this.bookReviewLikeEntity = bookReviewLikeEntity;
//    }
//
//    public UserContactEntity getUserContactEntity() {
//        return userContactEntity;
//    }
//
//    public void setUserContactEntity(UserContactEntity userContactEntity) {
//        this.userContactEntity = userContactEntity;
//    }
//
//    public MessageEntity getMessageEntity() {
//        return messageEntity;
//    }
//
//    public void setMessageEntity(MessageEntity messageEntity) {
//        this.messageEntity = messageEntity;
//    }
}
