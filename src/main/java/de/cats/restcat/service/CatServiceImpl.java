package de.cats.restcat.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class CatServiceImpl implements CatService {

    private final CatRepoService catRepoService;
    private final ArrayList<Cat> catArray;
    private ArrayList<Cat> filteredList;

    public CatServiceImpl(CatRepoService catRepoService) {
        this.catRepoService = catRepoService;
        this.catArray = catRepoService.readCats();
        System.out.println("Konstruktur CatsService aufgerufen");
    }

    @Override
    @Cacheable(cacheNames = "cats")
    public Cat getCat(Integer id) {
        return filterCatOutOfArrayWithID(id).get(0);
    }

    @Override
    @CacheEvict(cacheNames = "cats")
    public ArrayList<Cat> saveCat(Cat cat) {
        if (cat == null) {
            return null;
        }
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
    @CacheEvict(cacheNames = "cats")
    public ArrayList<Cat> deleteCat(Cat cat) {
        catRepoService.deleteCat(cat);
        filteredList = filterCatOutOfArray(cat);
        catArray.clear();
        catArray.addAll(filteredList);
        return catArray;
    }


    @Override
    @CacheEvict(cacheNames = "cats")
    public ArrayList<Cat> deleteCatWithID(Integer id) {
        Cat cat = filterCatOutOfArrayWithID(id).get(0);

        return deleteCat(cat);
    }

    @Override
    @CacheEvict(cacheNames = "cats")
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
    @Cacheable(cacheNames = "cats")
    public ArrayList<Cat> getCatlist() {
        return catArray;
    }

    @Override
    @Cacheable(cacheNames = "cats")
    public ArrayList<Cat> setNewCatList(ArrayList<Cat> newCatlist) {
        catRepoService.replaceCatlist(newCatlist);
        catArray.clear();
        catArray.addAll(catRepoService.readCats());
        return catArray;
    }

    @CacheEvict(cacheNames = "cats")
    private ArrayList<Cat> filterCatOutOfArray(Cat cat) {
        return (ArrayList<Cat>) catArray.stream()
                .filter(myCat -> !(myCat.getId() == cat.getId()))
                .collect(Collectors.toList());
    }
    @CacheEvict(cacheNames = "cats")
    private ArrayList<Cat> filterCatOutOfArrayWithID(Integer id) {
        return (ArrayList<Cat>) catArray.stream()
                .filter(myCat -> myCat.getId() == id)
                .collect(Collectors.toList());
    }
}