package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.annotations.CookieSearcher;
import com.example.MyBookShopApp.data.ResourceStorage;
import com.example.MyBookShopApp.data.book.BookEntity;
import com.example.MyBookShopApp.data.dto.SearchWordDto;
import com.example.MyBookShopApp.errs.BookstoreApiWrongParameterException;
import com.example.MyBookShopApp.errs.InvalidJwtTokenException;
import com.example.MyBookShopApp.services.BookstoreUserRegister;
import com.example.MyBookShopApp.security.jwt.JWTUtil;
import com.example.MyBookShopApp.services.AuthorService;
import com.example.MyBookShopApp.services.BookService;
import com.example.MyBookShopApp.services.BooksRatingAndPopularityService;
import com.example.MyBookShopApp.services.GenreService;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Logger;

@Controller
@RequestMapping("/books")
public class BooksPageController {
    private final BookService bookService;
    private final BooksRatingAndPopularityService booksRatingAndPopularityService;
    private final AuthorService authorService;
    private final ResourceStorage storage;
    private final BookstoreUserRegister userRegister;
    private final JWTUtil jwtUtil;
    private final GenreService genreService;

    @Autowired
    public BooksPageController(BookService bookService, BooksRatingAndPopularityService booksRatingAndPopularityService,
                               AuthorService authorService,
                               BooksRatingAndPopularityService booksRatingAndPopularityService1,
                               ResourceStorage storage, BookstoreUserRegister userRegister, JWTUtil jwtUtil, GenreService genreService) {
        this.bookService = bookService;
        this.booksRatingAndPopularityService = booksRatingAndPopularityService;
        this.authorService = authorService;
        this.storage = storage;
        this.userRegister = userRegister;
        this.jwtUtil = jwtUtil;
        this.genreService = genreService;
    }



    @ModelAttribute("searchWordDto")
    public SearchWordDto searchWordDto() {
        return new SearchWordDto();
    }



    @GetMapping("/{slug}")
    @CookieSearcher
    public String bookPage(@CookieValue(value = "cartContents", required = false) String cartContents,
                           @CookieValue(value = "postponedContents", required = false) String postponedContents,
                           @CookieValue(value = "token", required = false) String token,
                           @PathVariable("slug") String slug,  Model model) throws BookstoreApiWrongParameterException {

        model.addAttribute("slugBook",bookService.getBookBySlug(slug));
        model.addAttribute("slugBook", bookService.getBookBySlug(slug));
        model.addAttribute("slugBookRatingGrade",
                booksRatingAndPopularityService.getBookRatingGradeBySlug(slug));
        model.addAttribute("slugBookRatingGradeSize",
                booksRatingAndPopularityService.getBookRatingGradeSizeBySlug(slug));
        model.addAttribute("fiveStareRate", booksRatingAndPopularityService.getStarsRateSize(slug,5));
        model.addAttribute("fourStareRate", booksRatingAndPopularityService.getStarsRateSize(slug,4));
        model.addAttribute("threeStareRate", booksRatingAndPopularityService.getStarsRateSize(slug,3));
        model.addAttribute("twoStareRate", booksRatingAndPopularityService.getStarsRateSize(slug,2));
        model.addAttribute("oneStareRate", booksRatingAndPopularityService.getStarsRateSize(slug,1));
        model.addAttribute("feedBacks",booksRatingAndPopularityService.getFeedbackFromUserBySlug(slug));

        model.addAttribute("likes", booksRatingAndPopularityService.getAmountOfLikes(slug));
        model.addAttribute("dislikes", booksRatingAndPopularityService.getAmountOfDislikes(slug));
        return "books/slug.html";
    }
    @PostMapping("/changeBookStatus")
    public String bookGradingFunction(HttpServletRequest httpServletRequest,
                                      HttpServletResponse httpServletResponse) throws IOException, org.json.simple.parser.ParseException, InvalidJwtTokenException {

        JSONObject data = new JSONObject();
        JSONParser parser = new JSONParser();
        data = (JSONObject) parser.parse(httpServletRequest.getReader().readLine());

        String bookId = (String) data.get("book");
        String value = (String) data.get("value");

        booksRatingAndPopularityService.gradeBookFunction(bookId,Integer.parseInt(value) ,userRegister.getCurrentUser());
        return "redirect:/books/"+bookId;
    }

    @PostMapping("/{slug}/img/save")
    public String saveNewBookImage(@RequestParam("file") MultipartFile file, @PathVariable("slug") String slug, Model model) throws IOException, BookstoreApiWrongParameterException {
        String savePath = storage.saveNewBookImage(file, slug);
        BookEntity bookToUpdate = bookService.getBookBySlug(slug);
        bookToUpdate.setImage(savePath);
        bookService.saveBookEntity(bookToUpdate); //save new path in db here

        return "redirect:/books/" + slug;
    }
    @PostMapping("/rateBookReview")
    public String rateBookReviewFunction(HttpServletRequest httpServletRequest,
                                         HttpServletResponse httpServletResponse,
                                         Model model)
            throws IOException, org.json.simple.parser.ParseException, InvalidJwtTokenException {

        JSONObject data = new JSONObject();
        JSONParser parser = new JSONParser();
        String bookId;
        Long value;
        Long reviewId;
        data = (JSONObject) parser.parse(httpServletRequest.getReader().readLine());
        bookId =(String) data.get("bookId");

        value =(Long) data.get("value");

        reviewId =(Long) data.get("reviewId");

        booksRatingAndPopularityService.likeOrDislikeFunction(value.intValue() , reviewId, userRegister.getCurrentUser());
        return "redirect:/books/" + bookId;
    }
    @PostMapping("/bookReview")
    public String textBookReviewFunction(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse )
            throws IOException, org.json.simple.parser.ParseException, InvalidJwtTokenException {

        JSONObject data = new JSONObject();
        JSONParser parser = new JSONParser();
        String bookId;
        String text;
        data = (JSONObject) parser.parse(httpServletRequest.getReader().readLine());
        bookId =(String) data.get("bookId");
        text =(String) data.get("text");

        booksRatingAndPopularityService.textBookFunction(bookId,text, userRegister.getCurrentUser());
        return "redirect:/books/" + bookId;
    }


    @GetMapping("/download/{hash}")
    public ResponseEntity<ByteArrayResource> bookFile(@PathVariable("hash") String hash) throws IOException {
        Path path = storage.getBookFilePath(hash);
        Logger.getLogger(this.getClass().getSimpleName()).info("book file path: " + path);

        MediaType mediaType = storage.getBookFileMime(hash);
        Logger.getLogger(this.getClass().getSimpleName()).info("book file mime type: " + mediaType);

        byte[] data = storage.getBookFileByteArray(hash);
        Logger.getLogger(this.getClass().getSimpleName()).info("book file data len: " + data.length);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + path.getFileName().toString())
                .contentType(mediaType)
                .contentLength(data.length)
                .body(new ByteArrayResource(data));
    }

}
