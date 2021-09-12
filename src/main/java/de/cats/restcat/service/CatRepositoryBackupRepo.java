package de.cats.restcat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

class CatRepositoryBackupRepo implements de.cats.restcat.service.CatRepository {
    private final ObjectMapper objectMapper;

    public CatRepositoryBackupRepo(ObjectMapper mapper) {
        this.objectMapper = mapper;
    }

    @Override
    public ArrayList<Cat> readCats() {
        ArrayList<Cat> catArray = null;
        File datei = new File("Cats.json");
        try {
            catArray = new ArrayList<>(Arrays.asList(objectMapper.readValue(datei, Cat[].class)));
        } catch (IOException e) {
            throw new RuntimeException("Die Cats.json-Datei war nicht lesbar");
        }
        return catArray;
    }

    @Override
    public boolean writeCats(ArrayList<Cat> catList) {
        try {
            File datei = new File("Cats.json");
            objectMapper.writeValue(datei, catList);
        } catch (IOException e) {
            throw new RuntimeException("Die Cats.json-Datei war nicht schreibbar");
        }
        return true;
    }

    @Override
    public boolean addNewCat(Cat cat) {return false;}
    @Override
    public boolean deleteCat(Cat cat) {return false;}
    @Override
    public boolean editCat(Cat cat) {return false;}
}