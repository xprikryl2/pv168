/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dragonrental.backend;

import java.util.List;
import dragonrental.common.IllegalEntityException;
import dragonrental.common.ServiceFailureException;
import dragonrental.common.ValidationException;

/**
 * Service which allows person manupulation
 * @author Ondrej Prikryl
 */
public interface PersonManager {
    
    /**
     * This method adds new person to the database and generates id, which is unique.
     * @param person person to be stored in the database
     * @throws ServiceFailureException when db operation fails.
     * @throws ValidationException when the persons parameters does not match required characteristics
     * @throws IllegalEntityException when the body to be added is null
     */
    public void addPerson(Person person);
    
    /**
     * This method removes person from the database.
     * @param person to be removed
     * @throws ServiceFailureException when db operation fails.
     * @throws IllegalEntityException when the body to be deleted is null or its ID is invalid
     */
    public void deletePerson(Person person);
    
    /**
     * This method updates person characteristics like name, surname, 
     * email, etc.
     * @param person to be updated
     * @throws ServiceFailureException when db operation fails.
     * @throws ValidationException when the persons new parameter does not match required characteristics
     * @throws IllegalEntityException when the body to be updated is null or its ID is invalid
     */
    public void updatePerson(Person person);
    
    /**
     * This method lists all people stored in the database.
     * @return list of all person in the database
     * @throws ServiceFailureException when db operation fails.
     */
    public List<Person> listAllPeople();
    
    /**
     * This method searches for person in database using criteria 
     * like persons name, surname, email, etc.
     * @param person with filled searching criteria, others are null
     * @return list of all person matching searching criteria
     * @throws ServiceFailureException when db operation fails.
     */
    public List<Person> findPeople(Person person);
    
    /**
     * Searches for a person with given id.
     * @param id of the person to be searched
     * @return Person with given ID or null
     * @throws IllegalEntityException
     * @throws ServiceFailureException 
     */
    public Person getPersonById(Long id);
}
