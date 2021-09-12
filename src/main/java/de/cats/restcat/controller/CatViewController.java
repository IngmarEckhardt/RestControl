package de.cats.restcat.controller;

import de.cats.restcat.service.Cat;
import de.cats.restcat.service.CatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CatViewController {
    static CatDTO lastCatDTO;

    private CatService catService;

    @Autowired
    public void setCatService(CatService catService) {
        this.catService = catService;
    }

    @RequestMapping("mvc/index")
    String indexPage(Model model) {
        return "index";
    }

    @RequestMapping("mvc/form-new-cat")
    String formNewCatPage() {

        return "new-cat-form-page";
    }

    @GetMapping("mvc/catlist")
    public String viewCats(Model model) {
        model.addAttribute("cats", catService.getCatlist());
        return "catlist-page";
    }

    @RequestMapping("mvc/rest-api-help")
    String restApiHelpPage() {

        return "rest-api-help-page";
    }

    @RequestMapping("mvc/create-cat")
    String createCatPage(CatDTO catDTO,
                         Model model) {
        if((lastCatDTO!=null)&&lastCatDTO.equals(catDTO)) {
            model.addAttribute("message", "Die Katze wurde schon gespeichert");
            return "create-cat-failed-page";
        }
        lastCatDTO = catDTO;
        Cat newCat = catDTO.getCat();
        if (newCat == null) {
            model.addAttribute("message", "Die Daten konnten nicht gespeichert werden." +
                    "Prüfen Sie ob die Katze schon gespeichert wurde. Falls der Fehler weiterhin auftritt kontaktieren" +
                    "Sie bitte unseren Support");
            return "create-cat-failed-page";
        }
        catService.saveCat(newCat);
        model.addAttribute("catName", catDTO.getName());
        return "create-cat-page";
    }
}