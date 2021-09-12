package de.cats.restcat.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CatViewController {

    @RequestMapping("/CatControl")
    String sendIndexPage() {
        return "Index";
    }
}

