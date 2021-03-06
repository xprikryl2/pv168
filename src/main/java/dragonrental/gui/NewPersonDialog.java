/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dragonrental.gui;

import dragonrental.backend.*;
import dragonrental.common.*;
import javax.swing.JOptionPane;
import java.util.ResourceBundle;
import javax.swing.SwingWorker;

/**
 *
 * @author zuz
 */
public class NewPersonDialog extends javax.swing.JDialog {

    private PersonManager manager;
    private ResourceBundle bundle = ResourceBundle.getBundle("dragonrental/gui/language");
    private PeopleTableModel model;
    /**
     * Creates new form NewPersonDialog
     */
    public NewPersonDialog(java.awt.Frame parent, boolean modal, PersonManager manager, PeopleTableModel model) {
        super(parent, modal);
        initComponents();
        this.manager = manager;
        this.model = model;
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
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        getSurnameField = new javax.swing.JTextField();
        getNameField = new javax.swing.JTextField();
        getEmailField = new javax.swing.JTextField();
        addPersonButton = new javax.swing.JButton();
        backButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("dragonrental/gui/language"); // NOI18N
        setTitle(bundle.getString("addPerson")); // NOI18N

        jLabel1.setText(bundle.getString("name:")); // NOI18N

        jLabel2.setText(bundle.getString("surname:")); // NOI18N

        jLabel3.setText(bundle.getString("email:")); // NOI18N

        getSurnameField.setToolTipText(bundle.getString("surname")); // NOI18N

        getNameField.setToolTipText(bundle.getString("name")); // NOI18N

        getEmailField.setToolTipText(bundle.getString("email")); // NOI18N

        addPersonButton.setText(bundle.getString("add")); // NOI18N
        addPersonButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                addPersonButtonMouseClicked(evt);
            }
        });

        backButton.setText(bundle.getString("back")); // NOI18N
        backButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                backButtonMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(addPersonButton, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 32, Short.MAX_VALUE)
                        .addComponent(backButton, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel1))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(getNameField)
                            .addComponent(getEmailField)
                            .addComponent(getSurnameField))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(getNameField, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(getSurnameField, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(getEmailField, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addPersonButton)
                    .addComponent(backButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void backButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_backButtonMouseClicked
        dispose();
    }//GEN-LAST:event_backButtonMouseClicked

    private void addPersonButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_addPersonButtonMouseClicked
      
        Person person = new Person();
        person.setEmail(getEmailField.getText());
        person.setSurname(getSurnameField.getText());
        person.setName(getNameField.getText());
        person.setId(null);
        
        String error = Validation.personValidator(person);
        
        if(error != null) {
            JOptionPane.showMessageDialog(this,
                bundle.getString(error),
                bundle.getString("addPersonError"),
                JOptionPane.WARNING_MESSAGE);
        }
        else {
            new AddPersonWorker(person, this).execute();
        }
    }//GEN-LAST:event_addPersonButtonMouseClicked

    private class AddPersonWorker extends SwingWorker<Void,Void> {
        private Person person;
        private javax.swing.JDialog parent;
        private int error;
        
        public AddPersonWorker(Person person, javax.swing.JDialog parent) {
            this.person = person;
            this.parent = parent;
            this.error = 0;
        }
        
        @Override
        protected Void doInBackground() {
            try {
                manager.addPerson(person);
            } 
            catch(ServiceFailureException | ValidationException | IllegalEntityException | IllegalStateException ex) {
                JOptionPane.showMessageDialog(parent,
                ex.getMessage(),
                bundle.getString("addPersonError"),
                JOptionPane.ERROR_MESSAGE);
                this.error = 1;
                return null;
            }
            JOptionPane.showMessageDialog(parent,
                bundle.getString("done"),
                bundle.getString("addPerson"),
                JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        
        @Override
        protected void done(){
            if(error == 0) {
                model.addPerson(person);
                model.fireTableRowsInserted(model.getRowCount(), model.getRowCount());
                parent.dispose();
            }
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addPersonButton;
    private javax.swing.JButton backButton;
    private javax.swing.JTextField getEmailField;
    private javax.swing.JTextField getNameField;
    private javax.swing.JTextField getSurnameField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    // End of variables declaration//GEN-END:variables
}
