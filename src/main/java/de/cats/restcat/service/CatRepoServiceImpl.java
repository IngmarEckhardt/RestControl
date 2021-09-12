package de.cats.restcat.service;

import java.util.ArrayList;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

class CatRepoServiceImpl implements de.cats.restcat.service.CatRepoService {

    private final CatRepositoryBackupRepo backupRepo;
    private final CatRepositoryPrimaryRepo primaryRepo;
    private final ForkJoinPool repoServiceThreadPool;
    private ArrayList<Cat> catCache;


    CatRepoServiceImpl(CatRepositoryPrimaryRepo primaryRepo, CatRepositoryBackupRepo backupRepo) {
        this.backupRepo = backupRepo;
        this.primaryRepo = primaryRepo;
        this.repoServiceThreadPool = new ForkJoinPool (4);
    }

    @Override
    public ArrayList<Cat> readCats() {
        System.out.println("Read Cats aufgerufen");
        this.catCache = new ArrayList<>();
        repoServiceThreadPool.execute(() -> catCache = backupRepo.readCats());
        ArrayList<Cat> catArray = primaryRepo.readCats();

        boolean backUpAvailable = repoServiceThreadPool.awaitQuiescence(5_000, TimeUnit.MILLISECONDS);
        if(!backUpAvailable) System.out.println("BackUpRepository nicht verfügbar");

        if (catArray == null) catArray = catCache;
        if (catArray == null) return null;

        System.out.println("Vor getCatliste enthält im CatRepoService" + catArray.size());
        backupRepo.writeCats(catArray);
        catCache = catArray;
        return catCache;
    }

    @Override
    public boolean addNewCat(Cat catToAdd) {
        return primaryRepo.addNewCat(catToAdd);
    }

    @Override
    public boolean editCat(Cat catToEdit) {
        return primaryRepo.editCat(catToEdit);
    }

    @Override
    public boolean deleteCat(Cat cat) {
        return primaryRepo.deleteCat(cat);
    }

    @Override
    public boolean replaceCatlist (ArrayList<Cat> newCatlist) {
        return primaryRepo.writeCats(newCatlist);
    }
}