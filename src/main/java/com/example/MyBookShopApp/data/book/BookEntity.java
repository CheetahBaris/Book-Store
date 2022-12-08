package com.example.MyBookShopApp.data.book;

import com.example.MyBookShopApp.data.author.AuthorEntity;
import com.example.MyBookShopApp.data.book.file.FileDownloadEntity;
import com.example.MyBookShopApp.data.book.links.Book2AuthorEntity;
import com.example.MyBookShopApp.data.book.links.Book2GenreEntity;
import com.example.MyBookShopApp.data.book.links.Book2UserEntity;
import com.example.MyBookShopApp.data.book.review.BookReviewEntity;
import com.example.MyBookShopApp.data.genre.GenreEntity;
import com.example.MyBookShopApp.data.payments.BalanceTransactionEntity;
import com.example.MyBookShopApp.data.user.UserEntity;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "book")
public class BookEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String description;
    @Column(columnDefinition = "SMALLINT")
    private Integer discount;
    @Column(columnDefinition = "VARCHAR(225)")
    private String image;
    @Column(columnDefinition = "SMALLINT")
    private Integer is_bestseller;
     private Integer price;
    @Column(columnDefinition = "DATE")
    private LocalDate pub_date;
    @Column(columnDefinition = "VARCHAR(225)")
    private String slug;
    @Column(columnDefinition = "VARCHAR(225)")
    private String title;

    @OneToOne(mappedBy = "bookId")
    private Book2AuthorEntity book2AuthorEntity;
    @OneToOne(mappedBy = "bookId")
    private Book2GenreEntity book2GenreEntity;

    @ManyToOne
    private Book2UserEntity book2UserEntity;
    @ManyToOne
    private FileDownloadEntity fileDownloadEntity;
    @ManyToOne
    private BalanceTransactionEntity balanceTransactionEntity;
    @OneToOne(mappedBy = "bookId")
    private BookReviewEntity bookReviewEntity;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDiscount() {
        return discount;
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getIs_bestseller() {
        return is_bestseller;
    }

    public void setIs_bestseller(Integer is_bestseller) {
        this.is_bestseller = is_bestseller;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public LocalDate getPub_date() {
        return pub_date;
    }

    public void setPub_date(LocalDate pub_date) {
        this.pub_date = pub_date;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Book2AuthorEntity getBook2AuthorEntity() {
        return book2AuthorEntity;
    }

    public void setBook2AuthorEntity(Book2AuthorEntity book2AuthorEntity) {
        this.book2AuthorEntity = book2AuthorEntity;
    }

    public Book2GenreEntity getBook2GenreEntity() {
        return book2GenreEntity;
    }

    public void setBook2GenreEntity(Book2GenreEntity book2GenreEntity) {
        this.book2GenreEntity = book2GenreEntity;
    }

    public Book2UserEntity getBook2UserEntity() {
        return book2UserEntity;
    }

    public void setBook2UserEntity(Book2UserEntity book2UserEntity) {
        this.book2UserEntity = book2UserEntity;
    }

    public FileDownloadEntity getFileDownloadEntity() {
        return fileDownloadEntity;
    }

    public void setFileDownloadEntity(FileDownloadEntity fileDownloadEntity) {
        this.fileDownloadEntity = fileDownloadEntity;
    }

    public BalanceTransactionEntity getBalanceTransactionEntity() {
        return balanceTransactionEntity;
    }

    public void setBalanceTransactionEntity(BalanceTransactionEntity balanceTransactionEntity) {
        this.balanceTransactionEntity = balanceTransactionEntity;
    }

    public BookReviewEntity getBookReviewEntity() {
        return bookReviewEntity;
    }

    public void setBookReviewEntity(BookReviewEntity bookReviewEntity) {
        this.bookReviewEntity = bookReviewEntity;
    }
}
