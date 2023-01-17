package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.book.BookEntity;
import com.example.MyBookShopApp.data.dto.SearchWordDto;
import com.example.MyBookShopApp.errs.BookstoreApiWrongParameterException;
import com.example.MyBookShopApp.errs.SigninDataNotFoundException;
import com.example.MyBookShopApp.services.BookstoreUserRegister;
import com.example.MyBookShopApp.data.dto.ContactConfirmationPayload;
import com.example.MyBookShopApp.data.dto.ContactConfirmationResponse;
import com.example.MyBookShopApp.data.dto.RegistrationForm;
import com.example.MyBookShopApp.services.*;
import com.google.errorprone.annotations.FormatMethod;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
public class AuthUserController {

    private final BookstoreUserRegister userRegister;
    private final BookService bookService;
    private final BooksRatingAndPopularityService booksRatingAndPopularityService;
    private final AuthorService authorService;
    private final BookstoreUserDetailsService bookstoreUserDetailsService;

    private final GenreService genreService;

    @Autowired
    public AuthUserController(BookstoreUserRegister userRegister, BookService bookService,
                              BooksRatingAndPopularityService booksRatingAndPopularityService,
                              GenreService genreService,
                              AuthorService authorService,
                              BookstoreUserDetailsService bookstoreUserDetailsService) {
        this.userRegister = userRegister;
        this.bookService = bookService;
        this.booksRatingAndPopularityService = booksRatingAndPopularityService;
        this.genreService = genreService;
        this.authorService = authorService;
        this.bookstoreUserDetailsService = bookstoreUserDetailsService;
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
        return authorService.converterBookListToListWithAuthors(booksRatingAndPopularityService.getBookByRelevanceDesc(0, 6).getContent(), 0, 6);
    }

    @ModelAttribute("recentBooks")
    public List<BookEntity> recentAttrList() throws ParseException, BookstoreApiWrongParameterException {
        LocalDate fromDateRecent = LocalDate.parse(LocalDate.parse("2002-05-21").format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        LocalDate endDateRecent =LocalDate.parse(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        return authorService.converterBookListToListWithAuthors(bookService.findBookByPubDateBetween(fromDateRecent, endDateRecent, 0, 6).getContent(), 0, 6);
    }


    @GetMapping("/signin")
    public String handleSignin(@CookieValue(value = "cartContents", required = false) String cartContents,
                               @CookieValue(value = "postponedContents", required = false) String postponedContents, Model model) {

        String[]  cookiePostponedSlugs = postponedContents!=null ? (postponedContents.isEmpty()? null : postponedContents.split("/")) : null;
        String[] cookieCartSlugs = cartContents!=null? (cartContents.isEmpty()?null : cartContents.split("/")):null;

        model.addAttribute("postponedSize",cookiePostponedSlugs!=null?cookiePostponedSlugs.length:null);
        model.addAttribute("cartSize",cookieCartSlugs!=null?cookieCartSlugs.length:null);
        return "signin.html";
    }

    @GetMapping("/signup")
    public String handleSignUp(Model model) {
        model.addAttribute("regForm", new RegistrationForm());
        return "signup";
    }

    @PostMapping("/requestContactConfirmation")
    @ResponseBody
    public ContactConfirmationResponse handleRequestContactConfirmation(HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse,
            @RequestBody ContactConfirmationPayload contactConfirmationPayload) throws SigninDataNotFoundException {

        if (httpServletRequest.getHeader("Referer").equals("http://localhost:8085/signup")) {
            ContactConfirmationResponse response = new ContactConfirmationResponse();
            response.setResult("true");
            return response;

         }else {

            if (bookstoreUserDetailsService.getUserByEmail(contactConfirmationPayload.getContact()) != null
                    || bookstoreUserDetailsService.getUserByPhone(contactConfirmationPayload.getContact()) != null) {

                ContactConfirmationResponse response = new ContactConfirmationResponse();
                response.setResult("true");
                return response;

            } else {
                if (contactConfirmationPayload.getContact().startsWith("+")) {
                    throw new SigninDataNotFoundException("phone not found");

                } else {
                    throw new SigninDataNotFoundException("email not found");
                }

            }
        }
    }

    @PostMapping("/approveContact")
    @ResponseBody
    public ContactConfirmationResponse handleApproveContact(@RequestBody ContactConfirmationPayload payload) {
        ContactConfirmationResponse response = new ContactConfirmationResponse();
        response.setResult("true");
        return response;
    }

    @PostMapping("/reg")
    public String handleUserRegistration(RegistrationForm registrationForm , @CookieValue(value = "cartContents", required = false) String cartContents,
                                         @CookieValue(value = "postponedContents", required = false) String postponedContents, Model model) {

        String[]  cookiePostponedSlugs = postponedContents!=null ? (postponedContents.isEmpty()? null : postponedContents.split("/")) : null;
        String[] cookieCartSlugs = cartContents!=null? (cartContents.isEmpty()?null : cartContents.split("/")):null;


        model.addAttribute("postponedSize",cookiePostponedSlugs!=null?cookiePostponedSlugs.length:null);
        model.addAttribute("cartSize",cookieCartSlugs!=null?cookieCartSlugs.length:null);
        userRegister.registerNewUser(registrationForm);
        model.addAttribute("regOk", true);
        return "signin.html";
    }

    @PostMapping("/login")
    @ResponseBody
    public ContactConfirmationResponse handleLogin(@RequestBody ContactConfirmationPayload payload,
                                                   HttpServletResponse httpServletResponse) {
        ContactConfirmationResponse loginResponse = userRegister.jwtLogin(payload);
        Cookie cookie = new Cookie("token", loginResponse.getResult());
        httpServletResponse.addCookie(cookie);
        return loginResponse;
    }

    @GetMapping("/oauth2/vk")
    public void vkHandle(@RequestParam(value = "code", required = false) String code,
                         @RequestParam(value = "state", required = false) String state,
                         HttpServletRequest httpServletRequest,
                         HttpServletResponse httpServletResponse) throws IOException, org.json.simple.parser.ParseException {

        String accessToken;
        Long userId1;
        String email;
        String fullName;
        String phone;
        JSONArray jsonArray = new JSONArray();
        JSONObject object = new JSONObject();
        JSONParser parser = new JSONParser();
        RegistrationForm registrationForm1 = new RegistrationForm();
        ContactConfirmationPayload payload = new ContactConfirmationPayload();

        String ht = Jsoup.connect("https://oauth.vk.com/access_token?client_id=51518065&client_secret=wi7BtkmkYD8Ga8tN17zP&redirect_uri=http://localhost:8085/oauth2/vk&code=" + code+"&v=5.131&state="+state).ignoreContentType(true).get().text();

        object= (JSONObject) parser.parse(ht);
        accessToken = (String) object.get("access_token");
        userId1 = (Long) object.get("user_id");
        email = (String) object.get("email");

        String html = Jsoup.connect(" https://api.vk.com/method/users.get?user_ids=" + userId1.toString() + "&fields=contacts&access_token=" + accessToken + "&v=5.131").ignoreContentType(true).get().text();

        object = (JSONObject) parser.parse(html);
        jsonArray = (JSONArray) object.get("response");
        object = (JSONObject) jsonArray.get(0);
        fullName =  object.get("first_name") +" "+  object.get("last_name");
        phone= (String) object.get("home_phone");

        registrationForm1.setEmail(email);
        registrationForm1.setName(fullName);
        registrationForm1.setPass("BKontakte");
        registrationForm1.setPhone(phone.isEmpty()?"0":phone);
        userRegister.registerNewUser(registrationForm1);

         payload.setContact(email);
        payload.setCode("BKontakte");
        userRegister.login(payload);

        ContactConfirmationResponse loginResponse = userRegister.jwtLogin(payload);
        Cookie cookie = new Cookie("token", loginResponse.getResult());
        cookie.setPath("/");
        httpServletResponse.addCookie(cookie);
        httpServletResponse.sendRedirect("http://localhost:8085/my");
    }

    @GetMapping("/my")
    public String handleMy(@CookieValue(value = "cartContents", required = false) String cartContents,
                           @CookieValue(value = "postponedContents", required = false) String postponedContents, Model model) {

        String[]  cookiePostponedSlugs = postponedContents!=null ? (postponedContents.isEmpty()? null : postponedContents.split("/")) : null;
        String[] cookieCartSlugs = cartContents!=null? (cartContents.isEmpty()?null : cartContents.split("/")):null;

        model.addAttribute("postponedSize",cookiePostponedSlugs!=null?cookiePostponedSlugs.length:null);
        model.addAttribute("cartSize",cookieCartSlugs!=null?cookieCartSlugs.length:null);
        model.addAttribute("curUsr",userRegister.getCurrentUser());

        return "my";
    }

    @GetMapping("/profile")
    public String handleProfile(@CookieValue(value = "cartContents", required = false) String cartContents,
                                @CookieValue(value = "postponedContents", required = false) String postponedContents, Model model) {

        String[]  cookiePostponedSlugs = postponedContents!=null ? (postponedContents.isEmpty()? null : postponedContents.split("/")) : null;
        String[] cookieCartSlugs = cartContents!=null? (cartContents.isEmpty()?null : cartContents.split("/")):null;

        model.addAttribute("postponedSize",cookiePostponedSlugs!=null?cookiePostponedSlugs.length:null);
        model.addAttribute("cartSize",cookieCartSlugs!=null?cookieCartSlugs.length:null);
        model.addAttribute("curUsr", userRegister.getCurrentUser());
        return "profile";
    }

}
