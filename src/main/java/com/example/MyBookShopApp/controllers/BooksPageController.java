package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.ResourceStorage;
import com.example.MyBookShopApp.data.book.BookEntity;
import com.example.MyBookShopApp.data.dto.SearchWordDto;
import com.example.MyBookShopApp.errs.BookstoreApiWrongParameterException;
import com.example.MyBookShopApp.services.AuthorService;
import com.example.MyBookShopApp.services.BookService;
import com.example.MyBookShopApp.services.BooksRatingAndPopularityService;
import com.example.MyBookShopApp.services.GenreService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    private final GenreService genreService;

    @Autowired
    public BooksPageController(BookService bookService, BooksRatingAndPopularityService booksRatingAndPopularityService,
                               AuthorService authorService,
                               BooksRatingAndPopularityService booksRatingAndPopularityService1,
                               ResourceStorage storage, GenreService genreService) {
        this.bookService = bookService;
        this.booksRatingAndPopularityService = booksRatingAndPopularityService;
        this.authorService = authorService;
        this.storage = storage;
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
    public String bookPage(@PathVariable("slug") String slug, Model model) throws BookstoreApiWrongParameterException {
        getSlugBook(slug);
        model.addAttribute("slugBook", bookService.getBookBySlug(slug));
        model.addAttribute("slugBookRatingGrade",
                booksRatingAndPopularityService.getBookRatingGradeBySlug(slug));
        model.addAttribute("slugBookRatingGradeSize",
                booksRatingAndPopularityService.getBookRatingGradeSizeBySlug(slug));
        return "books/slug.html";
    }
    @PostMapping("/changeBookStatus")
    public String boolGradingFunction(@PathParam("bookId") String bookId, @PathParam("value") Integer value, Model model){


        booksRatingAndPopularityService.gradeBookFunctionAdmin(bookId,value);
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
