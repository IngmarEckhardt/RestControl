package de.cats.restcat.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CatViewController {

    @RequestMapping("mvc/index")
    String indexPage(Model model) {

        String appName = "CatControl";
        model.addAttribute("appNameValue",appName);

        return "index";
    }

    @RequestMapping("mvc/formNewCat")
    String formNewCatPage() {

        return "formNewCat";
    }

    @RequestMapping("mvc/createCat")
    String createCatPage() {

        return "createCat";
    }


}

