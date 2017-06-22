/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dragonrental.gui;

import dragonrental.backend.*;
import dragonrental.common.*;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.util.Date;
import java.util.ResourceBundle;
import javax.swing.JOptionPane;
import javax.swing.SpinnerNumberModel;

/**
 *
 * @author zuz
 */
public class UpdateReservationDialog extends UpdateReservation {

    private ReservationManager reservationManager;
    private ResourceBundle bundle = ResourceBundle.getBundle("dragonrental/gui/language");
    private Reservation reservation;
    
    public UpdateReservationDialog(java.awt.Frame parent, boolean modal, ReservationManager manager, int row, ReservationsTableModel model) {
        super(parent, modal, manager, row, model);
        
        this.reservationManager = manager;
        this.reservation = model.getElementAt(row);
        
        initComponents();
        
        GUIUtils.setMarkSpinnerOnFocus(toHourSpinner);
        GUIUtils.setMarkSpinnerOnFocus(toMinuteSpinner);
        
        setLocationRelativeTo(parent);
        setVisible(modal);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        headerLabel = new javax.swing.JLabel();
        timeFromLabel = new javax.swing.JLabel();
        timeToLabel = new javax.swing.JLabel();
        borrowerLabel = new javax.swing.JLabel();
        dragonLabel = new javax.swing.JLabel();
        moneyPaidLabel = new javax.swing.JLabel();
        pricePerHourLabel = new javax.swing.JLabel();
        timeFromValueLabel = new javax.swing.JLabel();
        borrowerValueLabel = new javax.swing.JLabel();
        dragonValueLabel = new javax.swing.JLabel();
        pricePerHourValueLabel = new javax.swing.JLabel();
        confirmButton = new javax.swing.JButton();
        moneyPaidValueLabel = new javax.swing.JLabel();
        cancelButton = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        toDatePicker = new org.jdesktop.swingx.JXDatePicker();
        toHourSpinner = new javax.swing.JSpinner();
        toMinuteSpinner = new javax.swing.JSpinner();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("dragonrental/gui/language"); // NOI18N
        headerLabel.setText(bundle.getString("reservationChangeEnd")); // NOI18N

        timeFromLabel.setText(bundle.getString("from")); // NOI18N

        timeToLabel.setText(bundle.getString("to")); // NOI18N

        borrowerLabel.setText(bundle.getString("borrower")); // NOI18N

        dragonLabel.setText(bundle.getString("dragon")); // NOI18N

        moneyPaidLabel.setText(bundle.getString("moneyPaid")); // NOI18N

        pricePerHourLabel.setText(bundle.getString("pricePerHour")); // NOI18N

        timeFromValueLabel.setText(reservation.getFrom().format(DateTimeFormatter.ofLocalizedDateTime(GUIUtils.getDateTimeFormat())));

        borrowerValueLabel.setText(reservation.getBorrower().getName() + ' ' + reservation.getBorrower().getSurname());

        dragonValueLabel.setText(reservation.getDragon().getName());

        pricePerHourValueLabel.setText(reservation.getPricePerHour().toString());

        confirmButton.setText(bundle.getString("confirm")); // NOI18N
        confirmButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                confirmButtonMouseClicked(evt);
            }
        });

        moneyPaidValueLabel.setText(reservation.getMoneyPaid().toString());

        cancelButton.setText(bundle.getString("cancel")); // NOI18N
        cancelButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cancelButtonMouseClicked(evt);
            }
        });

        jLabel14.setText("value");

        jButton1.setText("Cancel");

        toDatePicker.setToolTipText(bundle.getString("date")); // NOI18N
        toDatePicker.setDate(reservation.getTo() == null ? null : Date.from(reservation.getTo().atZone(ZoneId.systemDefault()).toInstant())
        );

        toHourSpinner.setModel(new SpinnerNumberModel(0, 0, 23, 1));
        toHourSpinner.setToolTipText(bundle.getString("hour")); // NOI18N
        toHourSpinner.setValue(reservation.getTo() == null ? 0 : reservation.getTo().getHour());
        toHourSpinner.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                toHourSpinnerFocusGained(evt);
            }
        });

        toMinuteSpinner.setModel(new SpinnerNumberModel(0, 0, 59, 1));
        toMinuteSpinner.setToolTipText(bundle.getString("minute")); // NOI18N
        toMinuteSpinner.setValue(reservation.getTo() == null ? 0 : reservation.getTo().getMinute());

        jLabel1.setText(bundle.getString("totalPrice")); // NOI18N

        jLabel2.setText(GUIUtils.getDuePayment(reservation).toString());

        jLabel3.setText(bundle.getString("reservationStatus")); // NOI18N

        jLabel4.setText(GUIUtils.getReservationStatusString(reservation));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(cancelButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(confirmButton, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(headerLabel)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(borrowerLabel)
                                    .addComponent(dragonLabel)
                                    .addComponent(timeFromLabel)
                                    .addComponent(timeToLabel)
                                    .addComponent(pricePerHourLabel)
                                    .addComponent(jLabel1)
                                    .addComponent(moneyPaidLabel)
                                    .addComponent(jLabel3))
                                .addGap(134, 134, 134)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel4)
                                    .addComponent(moneyPaidValueLabel)
                                    .addComponent(jLabel2)
                                    .addComponent(pricePerHourValueLabel)
                                    .addComponent(dragonValueLabel)
                                    .addComponent(borrowerValueLabel)
                                    .addComponent(timeFromValueLabel)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(toDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(toHourSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(toMinuteSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addGap(0, 12, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(headerLabel)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(borrowerLabel)
                    .addComponent(borrowerValueLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dragonLabel)
                    .addComponent(dragonValueLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(timeFromLabel)
                    .addComponent(timeFromValueLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(toDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(toHourSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(toMinuteSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(timeToLabel))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pricePerHourLabel)
                    .addComponent(pricePerHourValueLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(moneyPaidLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 67, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(confirmButton)
                            .addComponent(cancelButton)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(moneyPaidValueLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void confirmButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_confirmButtonMouseClicked
        Reservation newReservation = reservationManager.getReservation(reservation.getId());
        try {
            newReservation.setTo( GUIUtils.getLocalDateTime(toDatePicker, toHourSpinner, toMinuteSpinner) );
        } catch(DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this,
                bundle.getString("wrongDateTimeFormat"),
                bundle.getString("updateReservationError"),
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        String error = Validation.validateReservationForUpdating(reservation, newReservation);
        
        if(error != null) {
            JOptionPane.showMessageDialog(this,
                bundle.getString(error),
                bundle.getString("updateReservationError"),
                JOptionPane.WARNING_MESSAGE);
        }
        else {
            new UpdateReservationWorker(newReservation, this).execute();
        }
    }//GEN-LAST:event_confirmButtonMouseClicked

    private void cancelButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cancelButtonMouseClicked
        dispose();
    }//GEN-LAST:event_cancelButtonMouseClicked

    private void toHourSpinnerFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_toHourSpinnerFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_toHourSpinnerFocusGained

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel borrowerLabel;
    private javax.swing.JLabel borrowerValueLabel;
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton confirmButton;
    private javax.swing.JLabel dragonLabel;
    private javax.swing.JLabel dragonValueLabel;
    private javax.swing.JLabel headerLabel;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel moneyPaidLabel;
    private javax.swing.JLabel moneyPaidValueLabel;
    private javax.swing.JLabel pricePerHourLabel;
    private javax.swing.JLabel pricePerHourValueLabel;
    private javax.swing.JLabel timeFromLabel;
    private javax.swing.JLabel timeFromValueLabel;
    private javax.swing.JLabel timeToLabel;
    private org.jdesktop.swingx.JXDatePicker toDatePicker;
    private javax.swing.JSpinner toHourSpinner;
    private javax.swing.JSpinner toMinuteSpinner;
    // End of variables declaration//GEN-END:variables

}
