/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dragonrental.gui;

import dragonrental.backend.*;
import dragonrental.common.*;
import java.util.ResourceBundle;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/**
 *
 * @author Blackfox
 */
public class UpdateReservation extends javax.swing.JDialog {
    
    private int row;
    private ReservationManager reservationManager;
    private ResourceBundle bundle = ResourceBundle.getBundle("dragonrental/gui/language");
    private ReservationsTableModel model;
    private Reservation reservation;
    
    public UpdateReservation(java.awt.Frame parent, boolean modal, ReservationManager manager, int row, ReservationsTableModel model) {
        super(parent, modal);
        
        this.row = row;
        this.reservationManager = manager;
        this.model = model;
        this.reservation = model.getElementAt(row);
        
    }
    
    public class UpdateReservationWorker extends SwingWorker<Void, Void> {

        private Reservation reservation;
        private javax.swing.JDialog parent;
        private int error = 0;
        
        public UpdateReservationWorker(Reservation reservation, javax.swing.JDialog parent) {
            this.reservation = reservation;
            this.parent = parent;
        }
        
        @Override
        protected Void doInBackground() throws Exception {
            try {
                reservationManager.updateReservation(reservation);
            } 
            catch(DragonInUseException ex) {
                JOptionPane.showMessageDialog(
                        parent,
                        ex.getMessage(),
                        bundle.getString("dragonInUseError"),
                        JOptionPane.ERROR_MESSAGE
                                                );
                this.error = 1;
                return null;
            }
            catch(ServiceFailureException | ValidationException | IllegalEntityException | IllegalStateException ex) {
                JOptionPane.showMessageDialog(
                        parent,
                        ex.getMessage(),
                        bundle.getString("updateReservationError"),
                        JOptionPane.ERROR_MESSAGE
                                                );
                this.error = 1;
                return null;
            }
            JOptionPane.showMessageDialog(parent,
                bundle.getString("done"),
                bundle.getString("updateReservation"),
                JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        
        @Override
        protected void done() {
            if (error == 0) {
                model.setElementAt(row, reservation);
                model.fireTableRowsUpdated(row, row);
                parent.dispose();
            }
        }
    }
    
}
