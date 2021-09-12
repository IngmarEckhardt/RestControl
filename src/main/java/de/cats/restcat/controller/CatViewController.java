package de.cats.restcat.controller;

import de.cats.restcat.service.Cat;
import de.cats.restcat.service.CatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Controller
public class CatViewController {

    private CatService catService;

    @Autowired
    public void setCatService(CatService catService) {
        this.catService = catService;
    }

    @RequestMapping("mvc/index")
    String indexPage(Model model) {
        return "index";
    }

    @RequestMapping("mvc/formNewCat")
    String formNewCatPage() {

        return "formNewCat";
    }

    @GetMapping("mvc/catList")
    public String viewCats(Model model) {
        model.addAttribute("cats", catService.getCatlist());
        return "catList";
    }

    @RequestMapping("mvc/createCat")
    String createCatPage(@RequestParam String name, @RequestParam String age, @RequestParam String date,
                         @RequestParam String weight, @RequestParam String chubby, @RequestParam String sweet,
                         Model model) throws ParseException {

        Integer ageInt = Integer.parseInt(age);
        System.out.println("das Alter ist" + ageInt);
        Date dateSimpleDate = new SimpleDateFormat("yyyy-MM-dd").
                parse(date);
        System.out.println("Das date ist" + dateSimpleDate);
        LocalDate vaccineDate = dateSimpleDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        Float weightFloat = Float.parseFloat(weight);
        System.out.println("Das Weight ist" + weightFloat);
        Boolean chubbyBoolean = chubby.equals("true");
        System.out.println("chubby ist" + chubbyBoolean);
        Boolean sweetBoolean = sweet.equals("true");
        Cat lastCat= catService.getCat(catService.getCatlist().size()-1);
        if (lastCat.getName().equals(name) && lastCat.getWeight().equals(weightFloat) &&
                lastCat.getVaccineDate().equals(vaccineDate) && lastCat.getAge().equals(ageInt))
        {return "createCatError";}
        Cat newCat = new Cat(0, name, ageInt, vaccineDate, weightFloat, chubbyBoolean, sweetBoolean);
        catService.saveCat(newCat);


        model.addAttribute("catName", name);
        return "createCat";
    }

    @RequestMapping("mvc/restApiHelp")
    String restApiHelpPage() {

        return "restApiHelp";
    }
}