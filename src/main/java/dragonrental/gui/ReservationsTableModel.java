package dragonrental.gui;

import dragonrental.backend.*;
import dragonrental.common.*;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
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
 * @author Petr Soukop
 */
public class ReservationsTableModel extends AbstractTableModel {

    private ReservationManager rManager;
    private List<Reservation> reservations;
    private final java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("dragonrental/gui/language");
    
    public ReservationsTableModel(ReservationManager rManager) {
        this.rManager = rManager;
        try {
            this.reservations = rManager.listAllReservations();
        } catch (ServiceFailureException ex) {
            JOptionPane.showMessageDialog(null,
                ex.getMessage(),
                bundle.getString("error"),
                JOptionPane.ERROR_MESSAGE);
                this.reservations = new ArrayList<>();
        }
    }

    @Override
    public int getRowCount() {
        return reservations.size();
    }

    @Override
    public int getColumnCount() {
        return 5;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Reservation reservation = reservations.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return reservation.getBorrower().getName() + " " + reservation.getBorrower().getSurname();
            case 1:
                return reservation.getDragon().getName();
            case 2:
                return reservation.getFrom().format(DateTimeFormatter.ofLocalizedDateTime(GUIUtils.getDateTimeFormat()));
            case 3:
                return reservation.getTo() == null ? bundle.getString("noEnd") : reservation.getTo().format(DateTimeFormatter.ofLocalizedDateTime(GUIUtils.getDateTimeFormat()));
            case 4:
                return GUIUtils.getReservationStatusString(reservation);
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }
    
    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return bundle.getString("borrower");
            case 1:
                return bundle.getString("dragon");
            case 2:
                return bundle.getString("time_from");
            case 3:
                return bundle.getString("time_to");
            case 4:
                return bundle.getString("reservationStatus");
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }
    
    public void addReservation(Reservation reservation) {
        this.reservations.add(reservation);
    }

    public void deleteReservationAt(int row) {
        this.reservations.remove(row);
    }

    public Reservation getElementAt(int row) {
        return reservations.get(row);
    }
    
    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    public void setElementAt(int row, Reservation reservation) {
        reservations.set(row, reservation);
    }

    public void updateData() {
        new ListAllReservationsWorker().execute();
    }
    
    private class ListAllReservationsWorker extends SwingWorker <List<Reservation>, Void> {
        
        public ListAllReservationsWorker() {
            
        }

        @Override
        protected List<Reservation> doInBackground() throws Exception {
            List<Reservation> reservations;
            
            try {
                reservations = rManager.listAllReservations();
            } catch(ServiceFailureException | ValidationException | IllegalStateException ex) {
                JOptionPane.showMessageDialog(null,
                ex.getMessage(),
                bundle.getString("error"),
                JOptionPane.ERROR_MESSAGE);
                return null;
            }
            return reservations;
        }
        
        @Override
        protected void done() {
            try {
                reservations = get();
            } catch (InterruptedException | ExecutionException ex) {
                Logger.getLogger(ReservationsTableModel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
}
