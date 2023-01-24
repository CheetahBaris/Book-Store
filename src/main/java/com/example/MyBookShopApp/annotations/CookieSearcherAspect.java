package com.example.MyBookShopApp.annotations;

import com.example.MyBookShopApp.services.BookService;
import com.example.MyBookShopApp.services.BookstoreUserRegister;
import liquibase.pro.packaged.P;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.validation.support.BindingAwareModelMap;
import org.springframework.web.bind.annotation.CookieValue;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Aspect
@Component
public class CookieSearcherAspect {

    private Logger logger = Logger.getLogger(this.getClass().getName());
    private final BookstoreUserRegister userRegister;
    private final BookService bookService;

    @Autowired
    public CookieSearcherAspect(BookstoreUserRegister userRegister, BookService bookService) {
        this.userRegister = userRegister;
        this.bookService = bookService;
    }


    @Pointcut(value = "@annotation(com.example.MyBookShopApp.annotations.CookieSearcher) && " +
            "args(cartContents,postponedContents,token,*) || args(cartContents,postponedContents,token,*,*)  || args(cartContents,postponedContents,token,*,*,*) || args(cartContents,postponedContents,token,*,*,*,*) || args(cartContents,postponedContents,token,*,*,*,*,*)", argNames = "cartContents,postponedContents,token")
    public void getMainPagePointcut(String cartContents, String postponedContents, String token) {}


    @Around(value = "getMainPagePointcut(cartContents,postponedContents,token)", argNames = "joinPoint,cartContents,postponedContents,token")
    public Object aroundDurationCookieAnalyzingAdvice(ProceedingJoinPoint joinPoint, String cartContents, String postponedContents, String token) {

        Object returnValue = null;
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method methodSign = signature.getMethod();
        cartContents = cartContents!=null?cartContents:"";
        postponedContents = postponedContents!=null?postponedContents:"";
        token = token!=null?token:"";


        try {

            int mapElementIndex = 0;
            Object[] list = joinPoint.getArgs();
            String[] cookieCartSlugs = cartContents.isEmpty()?new String[0] : cartContents.split("/");
            String[] cookiePostponedSlugs = postponedContents.isEmpty()? new String[0] : postponedContents.split("/");

            for(int i=0; i< list.length; i++){
                if(list[i]!=null) {
                     String elem = list[i].getClass().getSimpleName();
                    if (elem.equals("BindingAwareModelMap")) {
                        mapElementIndex = i;
                        break;
                    }
                }
            }
            BindingAwareModelMap bindingAwareModelMap = new BindingAwareModelMap();
            bindingAwareModelMap.putAll((Map<String, Object>) list[mapElementIndex]);
            bindingAwareModelMap.addAttribute("cartSize", cookieCartSlugs.length);
            bindingAwareModelMap.addAttribute("postponedSize", cookiePostponedSlugs.length);
            bindingAwareModelMap.addAttribute("booksListFullSize", bookService.getBooksData().size());

                if (!token.isEmpty() && token.replaceAll("[^.]", "").length() == 2) {
                    bindingAwareModelMap.addAttribute("curUsrStatus", "authorized");
                    bindingAwareModelMap.addAttribute("curUsr", userRegister.getCurrentUser());
                } else {
                    bindingAwareModelMap.addAttribute("curUsrStatus", "unauthorized");
                    bindingAwareModelMap.addAttribute("curUsr", null);
                }

            if (methodSign.getName().equals("mainPage")) {

                bindingAwareModelMap.addAttribute("recommendedBooks", bookService.getPageOfRecommendedBooks(cookiePostponedSlugs, cookieCartSlugs, 0, 6));
            }

            ((Map<String, Object>) list[mapElementIndex]).putAll(bindingAwareModelMap.asMap());

            logger.info("Cookies analyzing complete in \"" + methodSign.getName() +"\" method!");


            returnValue = joinPoint.proceed(list);

        } catch (Throwable ex) {
            logger.warn(ex.getLocalizedMessage());
        }

        return returnValue;
    }

}
