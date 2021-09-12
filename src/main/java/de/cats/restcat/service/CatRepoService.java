package de.cats.restcat.service;

import java.util.ArrayList;

/**
 * Interface for Implementations of a RepositoryService who should manage a primary and a BackUpRepo.
 */

interface CatRepoService {


    /**
     * readCats()-Method should use non-static readCats()-method from the primary Repository to get and return a ArrayList<Cat>
     * or ask a non-static method readCats() to get a ArrayList from the BackUpRepository.
     * If the primary Repository is working it should use save the list into the BackUpRepository with its non-static-method writeCats()
     *
     * @return ArrayList<Cat>
     */
    ArrayList<Cat> readCats();


    /**
     * addNewCat() should save a new cat to the primary Repository
     *
     * @param catToAdd - a newly created Cat.class-Object
     */
    boolean addNewCat(Cat catToAdd);

    /**
     * editCat() should edit the Values of a Cat.class-Object in the primary Repository after changing field-values
     *
     * @param catToEdit - a specific Cat.class-Object
     */
    boolean editCat(Cat catToEdit);

    /**
     * deleteCat() should delete a specific Cat.class-Object in the primary Repository
     *
     * @param catToDelete - a specific Cat.class-Object
     */

    boolean deleteCat(Cat catToDelete);

    /**
     * replaceCatlist() should delete the Primary Repository and replace it with the actual Data from the Memory
     *
     * @param newCatlist - the ArrayList to write into the Primary Repository
     */

    boolean replaceCatlist(ArrayList<Cat> newCatlist);
}
