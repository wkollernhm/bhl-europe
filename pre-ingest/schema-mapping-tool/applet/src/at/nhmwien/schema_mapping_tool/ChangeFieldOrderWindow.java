/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ChangeFieldOrderWindow.java
 *
 * Created on 19.10.2010, 08:20:47
 */

package at.nhmwien.schema_mapping_tool;

import at.nhmwien.schema_mapping_tool.mappingProcess.MappingsHandler;
//import at.nhmwien.schema_mapping_tool.mappingProcess.MappingRecord;

//import java.util.HashMap;
import java.util.Iterator;
//import java.util.Map;
import java.util.ArrayList;
//import java.util.LinkedHashSet;

import javax.swing.DefaultListModel;

/**
 *
 * @author wkoller
 */
public class ChangeFieldOrderWindow extends javax.swing.JFrame {
    private DefaultListModel listModel = new DefaultListModel();
    private static ChangeFieldOrderWindow mySelf = null;
    private int fieldType = 0;

    public static ChangeFieldOrderWindow Self() {
        if( ChangeFieldOrderWindow.mySelf == null ) {
            ChangeFieldOrderWindow.mySelf = new ChangeFieldOrderWindow();
        }

        return ChangeFieldOrderWindow.mySelf;
    }
    
    /** Creates new form ChangeFieldOrderWindow */
    private ChangeFieldOrderWindow() {
        initComponents();

        this.fieldsList.setModel(this.listModel);
    }

    public void setVisible(boolean b, int p_fieldType ) {
        if( b && !this.isVisible() ) this.Load( p_fieldType );

        super.setVisible( b );
    }

    private void Load( int p_fieldType ) {
        listModel.clear();
        ArrayList<String> fieldOrder = null;

        // Check if we have input or output fields
        if( p_fieldType == MainWindow.FIELD_INPUT ) {
            fieldOrder = MappingsHandler.Self().getInputOrder();
        }
        else {
            fieldOrder = MappingsHandler.Self().getOutputOrder();
        }

        // Save our fieldtype (we need it in save & close)
        this.fieldType = p_fieldType;

        Iterator<String> ooIt = fieldOrder.iterator();
        while( ooIt.hasNext() ) {
            this.listModel.addElement( ooIt.next() );
        }

        /*HashMap<String,HashMap<String,MappingRecord>> mappings = MappingsHandler.Self().getMappings();

        Iterator<Map.Entry<String,HashMap<String,MappingRecord>>> tmpIt = mappings.entrySet().iterator();
        while( tmpIt.hasNext() ) {
            Map.Entry<String,HashMap<String,MappingRecord>> currEntry = tmpIt.next();

            this.listModel.addElement( currEntry.getKey() );
        }*/
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        fieldsList = new javax.swing.JList();
        sortUpButton = new javax.swing.JButton();
        sortDownButton = new javax.swing.JButton();
        closeButton = new javax.swing.JButton();

        setTitle("Change Field Order");
        setResizable(false);

        fieldsList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(fieldsList);

        sortUpButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/at/nhmwien/schema_mapping_tool/images/arrow_up.png"))); // NOI18N
        sortUpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sortUpButtonActionPerformed(evt);
            }
        });

        sortDownButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/at/nhmwien/schema_mapping_tool/images/arrow_down.png"))); // NOI18N
        sortDownButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sortDownButtonActionPerformed(evt);
            }
        });

        closeButton.setText("Save & Close");
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(closeButton)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(sortUpButton)
                            .addComponent(sortDownButton))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(sortUpButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sortDownButton))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 388, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(closeButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void sortUpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sortUpButtonActionPerformed
        // TODO add your handling code here:
        int selectedIndex = this.fieldsList.getSelectedIndex();

        if( selectedIndex > 0 ) {
            String currElement = (String) this.listModel.getElementAt( selectedIndex );
            this.listModel.setElementAt( this.listModel.getElementAt( selectedIndex - 1 ), selectedIndex );
            this.listModel.setElementAt( currElement, selectedIndex - 1);

            this.fieldsList.setSelectedIndex( selectedIndex - 1 );
        }
    }//GEN-LAST:event_sortUpButtonActionPerformed

    private void sortDownButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sortDownButtonActionPerformed
        // TODO add your handling code here:
        int selectedIndex = this.fieldsList.getSelectedIndex();
        int modelSize = this.fieldsList.getModel().getSize();

        if( selectedIndex < (this.fieldsList.getModel().getSize() - 1) ) {
            String currElement = (String) this.listModel.getElementAt( selectedIndex );
            this.listModel.setElementAt( this.listModel.getElementAt( selectedIndex + 1 ), selectedIndex );
            this.listModel.setElementAt( currElement, selectedIndex + 1);

            this.fieldsList.setSelectedIndex( selectedIndex + 1 );
        }
    }//GEN-LAST:event_sortDownButtonActionPerformed

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        // TODO add your handling code here:
        ArrayList<String> newFieldOrder = new ArrayList<String>();

        for( int i = 0; i < this.listModel.size(); i++ ) {
            newFieldOrder.add( (String)this.listModel.getElementAt(i) );
        }

        if( this.fieldType == MainWindow.FIELD_INPUT ) {
            MappingsHandler.Self().setInputOrder(newFieldOrder);
        }
        else {
            MappingsHandler.Self().setOutputOrder(newFieldOrder);
        }

        this.setVisible(false);
    }//GEN-LAST:event_closeButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton closeButton;
    private javax.swing.JList fieldsList;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton sortDownButton;
    private javax.swing.JButton sortUpButton;
    // End of variables declaration//GEN-END:variables

}