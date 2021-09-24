package de.cats.restcat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

class CatRepositoryBackupRepo implements CatRepository {
    private final ObjectMapper objectMapper;
    private final File datei;


    public CatRepositoryBackupRepo(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        ClassLoader classLoader = getClass().getClassLoader();
        datei = new File(classLoader.getResource("Cats.json").getFile());
    }

    @Override
    public ArrayList<Cat> readCats() throws RuntimeException {
        ArrayList<Cat> catArray;

        try {
            catArray = new ArrayList<>(Arrays.asList(objectMapper.readValue(datei, Cat[].class)));
            System.out.println("Backuplist gelesen");
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Cats.json-Datei was not readable");
        }
        return catArray;
    }

    @Override
    public boolean writeCats(ArrayList<Cat> catList) throws RuntimeException {
        try {
            objectMapper.writeValue(datei, catList);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Cats.json-Datei was not writable");
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