package de.cats.restcat.service;

import java.util.ArrayList;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

class CatRepoServiceImpl implements CatRepoService {

    private final CatRepositoryBackup backupRepo;
    private final CatRepositoryPrimary primaryRepo;
    private final ForkJoinPool repoServiceThreadPool;
    private ArrayList<Cat> catCache;


    CatRepoServiceImpl(CatRepositoryPrimary primaryRepo, CatRepositoryBackup backupRepo) {
        this.backupRepo = backupRepo;
        this.primaryRepo = primaryRepo;
        this.repoServiceThreadPool = new ForkJoinPool(4);
    }

    @Override
    public ArrayList<Cat> readCats() {
        ArrayList<Cat> catArray = null;
        this.catCache = new ArrayList<>();
        repoServiceThreadPool.execute(() -> catCache = backupRepo.readCats());
        try {
            catArray = primaryRepo.readCats();
        } catch (RuntimeException e) {
            e.printStackTrace();
        }



        boolean backUpAvailable = repoServiceThreadPool.awaitQuiescence(5_000, TimeUnit.MILLISECONDS);
        if (!backUpAvailable) System.out.println("BackUpRepository not readable");

        if (catArray == null) catArray = catCache;
        if (catArray == null) return null;

        if (backUpAvailable && catCache != null) backupRepo.writeCats(catArray);

        catCache = catArray;
        return catCache;
    }

    @Override
    public boolean addNewCat(Cat catToAdd) {
        Boolean success;
        try {
            success = primaryRepo.addNewCat(catToAdd);
            if (success == null) return false;
        } catch (RuntimeException e) {
            e.printStackTrace();
            return false;
        }
        return success;
    }

    @Override
    public boolean editCat(Cat catToEdit) {
        Boolean success;
        try {
            success = primaryRepo.editCat(catToEdit);
            if (success == null) return false;
        } catch (RuntimeException e) {
            e.printStackTrace();
            return false;
        }
        return success;
    }

    @Override
    public boolean deleteCat(Cat cat) {
        Boolean success;
        try {
            success = primaryRepo.deleteCat(cat);
            if (success == null) return false;
        } catch (RuntimeException e) {
            e.printStackTrace();
            return false;
        }
        return success;
    }

    @Override
    public boolean replaceCatlist(ArrayList<Cat> newCatlist) {
        Boolean success;
        try {
            success = primaryRepo.writeCats(newCatlist);
            if (success == null) return false;
        } catch (RuntimeException e) {
            e.printStackTrace();
            return false;
        }
        return success;
    }
}