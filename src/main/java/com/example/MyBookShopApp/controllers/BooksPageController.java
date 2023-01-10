package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.ResourceStorage;
import com.example.MyBookShopApp.data.book.BookEntity;
import com.example.MyBookShopApp.data.dto.ChangeBookStarsDto;
import com.example.MyBookShopApp.data.dto.SearchWordDto;
import com.example.MyBookShopApp.errs.BookstoreApiWrongParameterException;
import com.example.MyBookShopApp.security.BookstoreUserRegister;
import com.example.MyBookShopApp.security.jwt.JWTUtil;
import com.example.MyBookShopApp.services.AuthorService;
import com.example.MyBookShopApp.services.BookService;
import com.example.MyBookShopApp.services.BooksRatingAndPopularityService;
import com.example.MyBookShopApp.services.GenreService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import liquibase.pro.packaged.S;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.authentication.preauth.j2ee.J2eeBasedPreAuthenticatedWebAuthenticationDetailsSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.spring.web.json.Json;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;
import java.io.IOException;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
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


    @ModelAttribute("booksListFull")
    public List<BookEntity> bookListFull() {
        return bookService.getBooksData();
    }

    @ModelAttribute("tagListMap")
    public Map<String, List<BookEntity>> tagListMap() throws BookstoreApiWrongParameterException {

        return bookService.getTagListMap();
    }

    @ModelAttribute("tagListMapLgSize")
    public Integer tagListMapLg() throws BookstoreApiWrongParameterException {
        List<BookEntity> bigList = bookService.getTagListMap().values().stream()
                .max(Comparator.comparing(List::size)).get();
        return bigList.size();
    }

    @ModelAttribute("tagListMapXsSize")
    public Integer tagListMapXs() throws BookstoreApiWrongParameterException {
        List<BookEntity> bigList = bookService.getTagListMap().values().stream()
                .min(Comparator.comparing(List::size)).get();
        return bigList.size();
    }

    @ModelAttribute("booksList")
    public List<BookEntity> bookList() throws BookstoreApiWrongParameterException {
        return bookService.getPageOfRecommendedBooks(0, 10).getContent();
    }

    @ModelAttribute("recommendedBooks")
    public List<BookEntity> recommendedBooks() throws BookstoreApiWrongParameterException {
        return bookService.getPageOfRecommendedBooks(0, 6).getContent();
    }

    @ModelAttribute("searchWordDto")
    public SearchWordDto searchWordDto() {
        return new SearchWordDto();
    }

    @ModelAttribute("searchResults")
    public List<BookEntity> searchResults() {
        return new ArrayList<>();
    }

    @ModelAttribute("searchResultsFull")
    public List<BookEntity> searchResultsFull() {
        return new ArrayList<>();
    }

    @ModelAttribute("popularBooks")
    public List<BookEntity> popularAttrList() {
        return authorService.converterBookListToListWithAuthors(
                booksRatingAndPopularityService.getBookByRelevanceDesc(0, 6).getContent(), 0, 6);
    }

    @ModelAttribute("recentBooks")
    public List<BookEntity> recentAttrList() throws ParseException, BookstoreApiWrongParameterException {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDateRecent = format.parse("2002-05-21");
        Date endDateRecent = format.parse(LocalDate.now().toString());
        return authorService.converterBookListToListWithAuthors(bookService.findBookByPubDateBetween(fromDateRecent, endDateRecent, 0, 6).getContent(), 0, 6);
    }

    @ModelAttribute("slugBook")
    private BookEntity getSlugBook(String slug) throws BookstoreApiWrongParameterException {
        return bookService.getBookBySlug(slug);
    }


    @GetMapping("/{slug}")
    public String bookPage(@PathVariable("slug") String slug,@CookieValue(value = "token", required = false) String token,@CookieValue(value = "cartContents", required = false) String cartContents,
                           @CookieValue(value = "postponedContents", required = false) String postponedContents,  Model model) throws BookstoreApiWrongParameterException {
        getSlugBook(slug);
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
        model.addAttribute("feedBacks",booksRatingAndPopularityService.getFeedBackFromUserBySlug(slug));

        model.addAttribute("likes", booksRatingAndPopularityService.getAmountOfLikes(slug));
        model.addAttribute("dislikes", booksRatingAndPopularityService.getAmountOfDislikes(slug));

        postponedContents = postponedContents.isEmpty()? null: postponedContents;
        cartContents = cartContents.isEmpty()? null: cartContents;
        String[]  cookiePostponedSlugs = postponedContents!=null ?postponedContents.split("/"):null;
        String[] cookieCartSlugs = cartContents!=null?cartContents.split("/"):null;

        model.addAttribute("postponedSize",cookiePostponedSlugs!=null?cookiePostponedSlugs.length:null);
        model.addAttribute("cartSize",cookieCartSlugs!=null?cookieCartSlugs.length:null);
        if(token != null){

            model.addAttribute("curUsrStatus","authorized");
            model.addAttribute("curUsr",userRegister.getCurrentUser());
        }else {
            model.addAttribute("curUsrStatus","unauthorized");
            model.addAttribute("curUsr",null);
        }

        return "books/slug.html";
    }
    @PostMapping("/changeBookStatus")
    public String bookGradingFunction(HttpServletRequest httpServletRequest,
                                      HttpServletResponse httpServletResponse) throws IOException, org.json.simple.parser.ParseException {

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
            throws IOException, org.json.simple.parser.ParseException {

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
            throws IOException, org.json.simple.parser.ParseException {

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
