/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * fieldContainer.java
 *
 * Created on 28.09.2009, 11:07:48
 */

package at.nhmwien.schema_mapping_tool;

import java.util.*;

/**
 *
 * @author wkoller
 */
public class FieldContainer extends javax.swing.JPanel {
    ArrayList<MappingFieldPanel> myPanels;

    /** Creates new form fieldContainer */
    public FieldContainer() {
        initComponents();

        myPanels = new ArrayList<MappingFieldPanel>(0);
    }

    /**
     * Check if the container has the given field-panel
     * @param searchFor MappingFieldPanel to search for
     * @return boolean true if found, false it not
     */
    public boolean containsFieldPanel( MappingFieldPanel searchFor ) {
        if( myPanels.contains(searchFor) ) return true;

        // Try to find the panel below the sub-panels
        for( int i = 0; i < myPanels.size(); i++ ) {
            if( myPanels.get(i).hasSubField(searchFor) ) return true;
        }

        return false;
    }

    /**
     * Find a field-panel by a given ID
     * @param fieldID ID of field to search for
     * @return MappingFieldPanel Field that is searched for, null if nothing is found
     */
    public MappingFieldPanel getFieldPanelByID( String fieldID ) {
        MappingFieldPanel targetPanel = null;

        // Try to find the panel below the sub-panels
        for( int i = 0; i < myPanels.size(); i++ ) {
            targetPanel = myPanels.get(i).getFieldPanelByID(fieldID);

            if( targetPanel != null ) break;
            //if( myPanels.get(i).hasSubField(searchFor) ) return true;
        }

        return targetPanel;
    }

    /**
     * Add a new Field-Panel to the layout and keep a reference to it
     * @param comp MappingFieldPanel to add
     * @param constraints Layout information
     */
    public void add(MappingFieldPanel comp, Object constraints) {
        super.add(comp,constraints);

        // Keep a reference to the panel
        myPanels.add(comp);
    }

    /**
     * Remove all Field-Panels from this container, and cleanup the references
     */
    @Override
    public void removeAll() {
        super.removeAll();

        myPanels.clear();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setLayout(new java.awt.GridBagLayout());
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

}
