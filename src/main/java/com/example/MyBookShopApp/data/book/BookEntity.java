package com.example.MyBookShopApp.data.book;

import com.example.MyBookShopApp.data.author.AuthorEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "books")
@ApiModel(description = "entity representing a book")
public class BookEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("id generated by db automatically")
    private Long id;
    @Column(name = "pub_date")
    @ApiModelProperty("date of book publication")
    private Date pubDate;
     @Column(name = "is_bestseller",columnDefinition = "SMALLINT")
    @ApiModelProperty("if isBestseller = 1 so the book is considered to be bestseller and if 0 the book is not a " +
            "bestseller")
    private Integer isBestseller;
    @Column(columnDefinition = "VARCHAR(225)")
    @ApiModelProperty("mnemonical identity sequence of characters")
    private String slug;
    @Column(columnDefinition = "VARCHAR(225)")
    @ApiModelProperty("book title")
    private String title;
    @Column(columnDefinition = "VARCHAR(225)")
    @ApiModelProperty("book url")
    private String image;
    @Column(columnDefinition = "TEXT")
    @ApiModelProperty("book description text")
    private String description;
    @ApiModelProperty("book price without discount")
    private Integer price;

    @ApiModelProperty("discount value for book")
    private Double discount;

    @Column(name = "relevance")
    @JsonIgnore
    private Double relevance;
    @Column(columnDefinition = "VARCHAR(225)")
    @JsonIgnore
    private String tag; // Children's, Classic, Coming-of-age, Epic, Fabulation, Folklore, Historical,
                        // Meta, Nonsense, Paranoid, Philosophical, Pop culture,  Religious, Young adult

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getPubDate() {
        return pubDate;
    }

    public void setPubDate(Date pubDate) {
        this.pubDate = pubDate;
    }

    public Integer getIsBestseller() {
        return isBestseller;
    }

    public void setIsBestseller(Integer isBestseller) {
        this.isBestseller = isBestseller;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public Double getRelevance() {
        return relevance;
    }

    public void setRelevance(Double relevance) {
        this.relevance = relevance;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
