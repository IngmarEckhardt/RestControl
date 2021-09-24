package de.cats.restcat.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.ArrayList;

/**
 * The interface that describe the implementation of the CatService-Class, as a part of the service package for Catcontrol
 * <p>
 * the implementation should contain a private ArrayList<Cat> declared as field-varioble
 */


public interface CatService {

    /**
     * getCatlist() is a getter for this field - ArrayList<Cat>.
     *
     * @return ArrayList<Cat>
     */
    ArrayList<Cat> getCatlist();

    /** getCat() should filter the database and return the Cat.class-Objekt
     * @param id - The id of the Cat that should be returned
     * @return a Cat.class-Object
     * */
    @Cacheable
    Cat getCat(Integer id);

    /**
     * saveCat() adds a nonNull - Cat.class-Object to field - ArrayList<Cat> and use addNewCat() or editCat() from a CatRepoService-
     * Implementations to save the new or changed Object in the primary repository
     *
     * @param cat - the Cat.class - Object that should be saved
     * @return the updatet field-ArrayList<Cat> or null if no Cat was added to ArrayList and repository
     */
    ArrayList<Cat> saveCat(Cat cat);

    /**
     * deleteCat() deletes a - Cat.class-Object from field - ArrayList<Cat> and use deleteCat() from a CatRepository-
     * Implementations to save the list into the repository
     *
     * @param cat - the Cat.class - Object that should be deleted
     * @return the updatet field-ArrayList<Cat> or null if no Cat was deleted
     */
    ArrayList<Cat> deleteCat(Cat cat);

    /**
     * deleteCatWithID() deletes a Cat.class-Object identified by the id from the field - ArrayList<Cat> and
     * use deleteCat() from a CatRepository-Implementations to save the list into the repository.
     *
     * @param id - the id of the cat that should be deleted
     * @return ArrayList<Cat> - the new ArrayList after deleting or null if no Cat was deleted
     **/
    ArrayList<Cat> deleteCatWithID(Integer id);

    /**
     * findAll() filters the field - ArrayList<Cat> with a String in the name-Field, and return a ArrayList with Cat.class
     * Objects which own fieldvalue name contain the String in the method parameter
     *
     * @param stringFilter - the String that is used to filter objects with their name-variable
     * @return ArrayList<Cat> - a collection of the objects which names contains the String
     */

    ArrayList<Cat> findAll(String stringFilter);

    /**
     * setNewCatList() delete the Database in the primaryRepository and then write a new List into this database
     *
     * @param newCatlist - a ArrayList with Cat.class-Objects
     * @return ArrayList<Cat> - the new ArrayList after finishing the method
     */

    @Cacheable(cacheNames = "cats")
    ArrayList<Cat> setNewCatList(ArrayList<Cat> newCatlist);
}