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
 * @author Zuzana Wolfová
 */
public class DragonTableModel extends AbstractTableModel {
    
    private DragonManager manager;
    private List<Dragon> dragons;
    private final java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("dragonrental/gui/language");

    
    public DragonTableModel(DragonManager manager) {
        this.manager = manager;
        try {
            this.dragons = manager.listAllDragons();
        } catch(ServiceFailureException ex) {
            JOptionPane.showMessageDialog(null, 
                    ex.getMessage(), 
                    bundle.getString("error"),
                    JOptionPane.ERROR_MESSAGE);
            this.dragons = new ArrayList<>();
        }
    }
    
    public Object getElementAt(int index) { 
        return dragons.get(index); 
    }
    
    @Override
    public int getRowCount() {
        return dragons.size();
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Dragon dragon = this.dragons.get(rowIndex);
        switch(columnIndex) {
            case 0:
                return dragon.getName();
            case 1:
                return dragon.getElement();
            case 2:
                return dragon.getMaximumSpeed();
            case 3:
                return dragon.getDateOfBirth();
            default:
                throw new IllegalArgumentException("Column index out of bounds.");
        }
    }
    
    @Override
    public String getColumnName(int columnIndex) {
        switch(columnIndex) {
            case 0:
                return bundle.getString("dragonName");
            case 1:
                return bundle.getString("element");
            case 2:
                return bundle.getString("speed");
            case 3:
                return bundle.getString("birth_date");
            default:
                throw new IllegalArgumentException("Column index out of bounds.");
        }
    }
    
    public void addDragon(Dragon dragon) {
        this.dragons.add(dragon);
    }
    
    public void setDragons(List<Dragon> dragons) {
        this.dragons = dragons;
    }
    
    public void setElementAt(int index, Dragon dragon) {
        this.dragons.set(index, dragon);
    }
    
    public void deleteDragonAt(int index) {
        dragons.remove(index);
    }
    
    public void updateData() {
        new ListAllDragonsWorker().execute();
    }
    
    private class ListAllDragonsWorker extends SwingWorker<List<Dragon>,Void> {
        
        public ListAllDragonsWorker() {
            
        }
        
        @Override
        protected List<Dragon> doInBackground() {
            List<Dragon> dragons;
            
            try {
                dragons = manager.listAllDragons();
            } catch (ServiceFailureException | ValidationException | IllegalEntityException | IllegalStateException ex) {
                JOptionPane.showMessageDialog(null,
                ex.getMessage(),
                bundle.getString("error"),
                JOptionPane.ERROR_MESSAGE);
                return null;
            }
            
            return dragons;
        }
        
        @Override
        protected void done() {
            try {
                dragons = get();
            } catch (InterruptedException | ExecutionException ex) {
                Logger.getLogger(PeopleTableModel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
}
