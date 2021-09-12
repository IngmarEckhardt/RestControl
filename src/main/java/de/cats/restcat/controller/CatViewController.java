package de.cats.restcat.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

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
    String createCatPage(HttpServletRequest request, Model model) {

        String name = request.getParameter("catName");
        model.addAttribute("catName", name);
        return "createCat";
    }

    @RequestMapping("mvc/restApiHelp")
    String restApiHelpPage() {

        return "restApiHelp";
    }
}