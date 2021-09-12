package de.cats.restcat.controller;

import de.cats.restcat.service.Cat;
import de.cats.restcat.service.CatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/api")
public class CatRestController {

    private CatService catService;

    @Autowired
    public void setCatService(CatService catService) {
        this.catService = catService;
    }


    @GetMapping(value = "/Cat", produces = "application/json; charset=utf-8")
    public Cat getCat(@RequestBody Integer id) {
        return catService.getCat(id);
    }

    @PostMapping(value = "/Cat", produces = "application/json; charset=utf-8")
    public ResponseEntity postController(
            @RequestBody Cat cat) {
        catService.saveCat(cat);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping(value = "/Cat", produces = "application/json; charset=utf-8")
    public ResponseEntity deleteCat(@RequestBody Integer id) {
        catService.deleteCatWithID(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }


    @GetMapping(value = "/Catlist", produces = "application/json; charset=utf-8")
    public ArrayList<Cat> getCatlist() {
        return catService.getCatlist();
    }


    @PutMapping(value = "/Catlist", produces = "application/json; charset=utf-8")
    public ResponseEntity replaceCatlist(@RequestBody ArrayList<Cat> catlist) {
        catService.setNewCatList(catlist);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}