/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dragonrental.gui;

import dragonrental.backend.Dragon;
import dragonrental.backend.DragonManager;
import dragonrental.common.IllegalEntityException;
import dragonrental.common.ServiceFailureException;
import dragonrental.common.Validation;
import dragonrental.common.ValidationException;
import java.util.ResourceBundle;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/**
 *
 * @author zuz
 */
public class UpdateDragonDialog extends javax.swing.JDialog {
    
    private DragonManager manager;
    private Dragon dragon;
    private DragonTableModel model;
    private int row;
    private ResourceBundle bundle = ResourceBundle.getBundle("dragonrental/gui/language");

    /**
     * Creates new form UpdateDragonDialog
     */
    public UpdateDragonDialog(java.awt.Frame parent, boolean modal, 
            DragonManager manager, DragonTableModel model, int selectedRow) {
        super(parent, modal);
        this.manager = manager;
        this.model = model;
        this.row = selectedRow;
        this.dragon = (Dragon) model.getElementAt(selectedRow);
        initComponents();
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

        jLabel1 = new javax.swing.JLabel();
        updateButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        nameField = new javax.swing.JTextField();
        elementLabel = new javax.swing.JLabel();
        dateLabel = new javax.swing.JLabel();
        speedSpinner = new javax.swing.JSpinner();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("dragonrental/gui/language"); // NOI18N
        setTitle(bundle.getString("updateDragon")); // NOI18N

        jLabel1.setText(bundle.getString("dragonName:")); // NOI18N

        updateButton.setText(bundle.getString("update")); // NOI18N
        updateButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                updateButtonMouseClicked(evt);
            }
        });

        cancelButton.setText(bundle.getString("cancel")); // NOI18N
        cancelButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cancelButtonMouseClicked(evt);
            }
        });

        jLabel2.setText(bundle.getString("birth_date:")); // NOI18N

        jLabel3.setText(bundle.getString("element:")); // NOI18N

        jLabel5.setText(bundle.getString("speed:")); // NOI18N

        nameField.setText(dragon.getName());

        elementLabel.setText(dragon.getElement().name());

        dateLabel.setText(dragon.getDateOfBirth().toString());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel5)
                            .addComponent(jLabel1))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(nameField, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(elementLabel)
                            .addComponent(dateLabel)
                            .addComponent(speedSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(updateButton, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(cancelButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(nameField, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(elementLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(dateLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(speedSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addGap(18, 18, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(updateButton)
                    .addComponent(cancelButton))
                .addContainerGap())
        );

        speedSpinner.setValue(dragon.getMaximumSpeed());

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void updateButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_updateButtonMouseClicked
        this.dragon.setName(nameField.getText());
        this.dragon.setMaximumSpeed((Integer)speedSpinner.getValue());
        
        String error = Validation.dragonValidator(this.dragon);
        
        if (error != null) {
            JOptionPane.showMessageDialog(this,
                    bundle.getString(error),
                    bundle.getString("error"),
                    JOptionPane.WARNING_MESSAGE);
        } else {
            new UpdateDragonWorker(this.dragon, this).execute();
        }
    }//GEN-LAST:event_updateButtonMouseClicked

    private void cancelButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cancelButtonMouseClicked
        dispose();
    }//GEN-LAST:event_cancelButtonMouseClicked

    private class UpdateDragonWorker extends SwingWorker<Void, Void> {

        private Dragon dragon;
        private javax.swing.JDialog parent;
        private boolean error = false;
        
        public UpdateDragonWorker(Dragon dragon, javax.swing.JDialog parent) {
            this.dragon = dragon;
            this.parent = parent;
        }
        
        @Override
        protected Void doInBackground() throws Exception {
            try {
                manager.updateDragon(dragon);
            } catch(ServiceFailureException | ValidationException | IllegalEntityException | IllegalStateException ex) {
                JOptionPane.showMessageDialog(parent,
                ex.getMessage(),
                bundle.getString("error"),
                JOptionPane.ERROR_MESSAGE);
                this.error = true;
                return null;
            }
            return null;
        }
        
        @Override
        protected void done(){
            if(!error) {
                JOptionPane.showMessageDialog(parent,
                bundle.getString("dragon_updated"),
                bundle.getString("done"),
                JOptionPane.INFORMATION_MESSAGE);
                model.setElementAt(row, dragon);
                model.fireTableRowsUpdated(row, row);
                parent.dispose();
            }
        }
        
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel dateLabel;
    private javax.swing.JLabel elementLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JTextField nameField;
    private javax.swing.JSpinner speedSpinner;
    private javax.swing.JButton updateButton;
    // End of variables declaration//GEN-END:variables
}