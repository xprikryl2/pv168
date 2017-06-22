/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dragonrental.gui;

import dragonrental.backend.*;
import dragonrental.common.IllegalEntityException;
import dragonrental.common.ServiceFailureException;
import dragonrental.common.ValidationException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author JustMe
 */
public class PeopleTableModel extends AbstractTableModel {
 
    private PersonManager manager;
    private List<Person> people;
    private final java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("dragonrental/gui/language");
 
    public PeopleTableModel(PersonManager manager) {
        this.manager = manager;
        try {
            this.people = manager.listAllPeople();
        } catch (ServiceFailureException ex) {
            JOptionPane.showMessageDialog(null,
                ex.getMessage(),
                bundle.getString("error"),
                JOptionPane.ERROR_MESSAGE);
                this.people = new ArrayList<>();
        }
    }
    
    @Override
    public int getRowCount() {
        return people.size();
    }
 
    @Override
    public int getColumnCount() {
        return 3;
    }
 
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Person person = people.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return person.getName();
            case 1:
                return person.getSurname();
            case 2:
                return person.getEmail();
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }
    
    public void updateData() {
        new ListAllPeopleWorker().execute();
    }
    
    
    @Override
    public String getColumnName(int columnIndex) {
        
        switch (columnIndex) {
            case 0:
                return bundle.getString("name");
            case 1:
                return bundle.getString("surname");
            case 2:
                return bundle.getString("email");
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }
    
    public Object getElementAt(int index) { 
        return people.get(index); 
    }
    
    public void addPerson(Person person) {
        people.add(person);
    }

    public void setPeople(List<Person> people) {
        this.people = people;
    }
    
    public void setElementAt(int index, Person person) {
        people.set(index, person);
    }
    
    public void deletePersonAt(int index) {
        people.remove(index);
    }
    
    private class ListAllPeopleWorker extends SwingWorker<List<Person>,Void> {
        
        public ListAllPeopleWorker() {
            
        }
        
        @Override
        protected List<Person> doInBackground() {
            
            List<Person> people;
            
            try {
                people = manager.listAllPeople();
            } 
            catch(ServiceFailureException | ValidationException | IllegalEntityException | IllegalStateException ex) {
                JOptionPane.showMessageDialog(null,
                ex.getMessage(),
                bundle.getString("error"),
                JOptionPane.ERROR_MESSAGE);
                return null;
            }
            
            return people;
        }
        
        @Override
        protected void done() {
            try {
                people = get();
            } catch (InterruptedException | ExecutionException ex) {
                Logger.getLogger(PeopleTableModel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
