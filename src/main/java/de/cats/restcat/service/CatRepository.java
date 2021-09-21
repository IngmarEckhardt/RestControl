package de.cats.restcat.service;

import java.io.File;
import java.util.ArrayList;

/**
 * Interface for Repository Implementations
 */

interface CatRepository {

    /**
     * readCats(): Read Object-Value from the repository and use Cat.newCat(...) to create the Cat-Objects and return them in a ArrayList
     *
     * @Return ArrayList<Cat>
     */
    ArrayList<Cat> readCats();

    /**
     * writeCats() write the complete List of Cats from Memory into Persistence, it should be avoided for the primary
     * Persistence because it delete the complete persistence at the beginning of the operation
     * and even set new IDs for the Cat.class - Objects. It's useful for the BackUpRepository.
     *
     * @param cats -an ArrayList<Cat>
     * @return boolean true if the List was sucessfully saved.
     */
    boolean writeCats(ArrayList<Cat> cats);

    /**
     * addNewCat() write a new Cat.class-Object into the Persistence. The BackUpRepository won't need to implement this method
     *
     * @param cat - a Cat.class-Object
     * @return boolean true if the Cat was sucessfully saved in the Persistence
     */
    boolean addNewCat(Cat cat);

    /**
     * deleteCat() delete a specific Cat.class-Object from the Persistence. The BackUpRepository won't need to implement this method
     *
     * @param cat - a Cat.class-Object
     * @return boolean true if the Cat was sucessfully deleted from Persistence
     */
    boolean deleteCat(Cat cat);

    /**
     * editCat() replace a edited specific Cat.class-Object in Persistence. The BackUpRepository won't need to implement this method
     *
     * @param cat - a Cat.class-Object
     * @return boolean true if the Cat was sucessfully edited in the Persistence
     */
    boolean editCat(Cat cat);
}