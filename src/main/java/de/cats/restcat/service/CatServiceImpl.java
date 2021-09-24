package de.cats.restcat.service;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class CatServiceImpl implements CatService {

    private final CatRepoService catRepoService;
    private ArrayList<Cat> catArray;
    private ArrayList<Cat> filteredList;

    public CatServiceImpl(CatRepoService catRepoService) {
        this.catRepoService = catRepoService;
        this.catArray = catRepoService.readCats();
    }

    @Override
    public ArrayList<Cat> getCatlist() {
        catArray = catRepoService.readCats();
        return catArray;
    }

    @Override
    public Cat getCat(Integer id) {
        Cat filterCat;
        try {
            filterCat = filterCatOutOfArrayWithID(id).get(0);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
        return filterCat;
    }

    @Override
    public ArrayList<Cat> saveCat(Cat cat) {
        if (cat == null) return null;
        else if (cat.getId() == null) {
            catRepoService.addNewCat(cat);
            catArray.add(cat);
        }
        else {
            catRepoService.editCat(cat);
            filteredList = filterCatOutOfArray(cat);
            filteredList.add(cat);
            catArray.clear();
            catArray.addAll(filteredList);
        }
        return catArray;
    }


    @Override
    public ArrayList<Cat> deleteCat(Cat cat) {
        if (cat == null) return null;
        catRepoService.deleteCat(cat);
        filteredList = filterCatOutOfArray(cat);
        catArray.clear();
        catArray.addAll(filteredList);
        return catArray;
    }

    @Override
    public ArrayList<Cat> deleteCatWithID(Integer id) {
        Cat cat = null;
        try {
            cat = filterCatOutOfArrayWithID(id).get(0);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }

        return deleteCat(cat);
    }

    @Override
    public ArrayList<Cat> findAll(String stringFilter) {
        ArrayList<Cat> catList = new ArrayList<>();

        for (Cat cat : catArray) {
            boolean passesFilter = (stringFilter == null || stringFilter.isEmpty())
                    || cat.getName().toLowerCase().contains(stringFilter.toLowerCase());
            if (passesFilter) catList.add(cat);
        }
        catList.sort(new SortName());
        return catList;
    }

    @Override
    public ArrayList<Cat> setNewCatList(ArrayList<Cat> newCatlist) {
        catRepoService.replaceCatlist(newCatlist);
        catArray.clear();
        catArray.addAll(catRepoService.readCats());
        return catArray;
    }

    private ArrayList<Cat> filterCatOutOfArray(Cat cat) {
        return (ArrayList<Cat>) catArray.stream()
                .filter(myCat -> !(myCat.getId().equals(cat.getId())))
                .collect(Collectors.toList());
    }
    private ArrayList<Cat> filterCatOutOfArrayWithID(Integer id) {
        return (ArrayList<Cat>) catArray.stream()
                .filter(myCat -> myCat.getId().equals(id))
                .collect(Collectors.toList());
    }
}