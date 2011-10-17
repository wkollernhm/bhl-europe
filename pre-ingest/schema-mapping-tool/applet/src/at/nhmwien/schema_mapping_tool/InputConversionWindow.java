/*
   Copyright 2011 Museum of Natural History Vienna

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package at.nhmwien.schema_mapping_tool;

import at.nhmwien.schema_mapping_tool.schemaReaders.*;
import java.io.*;
import java.util.*;
import javax.swing.JOptionPane;

/*
 * inputConversionWindow.java
 *
 * Created on 10.09.2009, 11:11:28
 */

/**
 *
 * @author wkoller
 */
public class InputConversionWindow extends javax.swing.JFrame {

    /** Creates new form inputConversionWindow */
    public InputConversionWindow() {
        initComponents();
    }

    /** Start reading an input file and convert it **/
    public LinkedHashMap<String,LinkedHashMap> convertFile( File inputFile ) {
        //Based on the file extension we detect the type and start the conversion
        /* Currently support file types are:
         * - FDT: File Definition Table (ISIS)
         *
        */
        this.setVisible(true);

        currentStepLabel.setText( "Detecting File Type" );

        // Extract the file extension
        String ifName = inputFile.getName();
        int dotPos = ifName.lastIndexOf(".");
        String ifExt = ifName.substring(dotPos + 1).toLowerCase();

        currentStepLabel.setText( "Starting File Conversion" );

        LinkedHashMap<String,LinkedHashMap> fields = null;

        FileConverter fc = null;

        // Start conversion based on extension
        if( ifExt.compareTo( "fdt" ) == 0 ) {
            fc = new FdtConverter();
            //fields = fdtC.parseFile(inputFile);
        }
        else if( ifExt.compareTo( "xml" ) == 0 ) {
            fc = new XmlConverter();
            //fields = xmlC.parseFile(inputFile);
        }
        else if( ifExt.compareTo( "xsd" ) == 0 ) {
            //fc = new XsdConverter();
            fc = new XsdXSOMConverter();
            //fields = xsdC.parseFile(inputFile);
        }
        else if( ifExt.compareTo( "def" ) == 0 ) {
            fc = new MARC21DefConverter();
            //fields = m21dC.parseFile(inputFile);
        }
        else if( ifExt.compareTo( "xls" ) == 0 ) {
            if( JOptionPane.showConfirmDialog(this, "The Excel converter is able to use the first row or the first column as the header fields. Press 'YES' if you want the first row to be used, otherwise press 'No'!", "Row or Column", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION ) {
                fc = new XlsConverter();
            }
            else {
                fc = new XlsRowConverter();
            }
        }
        else if( ifExt.compareTo( "xlsx" ) == 0 ) {
            fc = new XlsxConverter();
        }
        else if( ifExt.compareTo( "csv" ) == 0 ) {
            fc = new DelFileConverter();
        }

        // Check if we have a valid input format
        if( fc != null ) fields = fc.parseFile(inputFile);

        // DEBUG: Add fields to text area
        if( fields != null ) {
            Set fS = fields.entrySet();
            Iterator i = fS.iterator();
            while( i.hasNext() ) {
                Map.Entry me = (Map.Entry) i.next();
                HashMap fieldInfo = (HashMap) me.getValue();

                logTextArea.append(me.getKey() + ": " + fieldInfo.get("name") + "\n" );
            }
        }

        this.setVisible(false);

        return fields;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        conversionProgressBar = new javax.swing.JProgressBar();
        currentStepLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        logTextArea = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("inputConversionFrame"); // NOI18N
        setResizable(false);

        logTextArea.setColumns(20);
        logTextArea.setRows(5);
        jScrollPane1.setViewportView(logTextArea);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 373, Short.MAX_VALUE)
                    .add(currentStepLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 373, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, conversionProgressBar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 373, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(currentStepLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(conversionProgressBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 447, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JProgressBar conversionProgressBar;
    private javax.swing.JLabel currentStepLabel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea logTextArea;
    // End of variables declaration//GEN-END:variables

}
