package de.cats.restcat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

class CatRepositoryBackupRepo implements CatRepository {
    private final ObjectMapper objectMapper;

    private File datei = new File("Cats.json");

    public CatRepositoryBackupRepo(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public ArrayList<Cat> readCats() {
        ArrayList<Cat> catArray;

        try {
            catArray = new ArrayList<>(Arrays.asList(objectMapper.readValue(datei, Cat[].class)));
            System.out.println("Backuplist gelesen");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Die Cats.json-Datei war nicht lesbar");
        }
        return catArray;
    }

    @Override
    public boolean writeCats(ArrayList<Cat> catList) {
        try {
            objectMapper.writeValue(datei, catList);
        } catch (IOException e) {
            e.printStackTrace();
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