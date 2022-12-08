package com.example.MyBookShopApp.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
 public class SearchPageController {

    @GetMapping("/search")
    public String getSearching(){
        return "/search/index.html";
    }
}
