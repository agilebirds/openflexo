/*
 * HTMLEditorRmovingMenus.java
 *
 *
 */

/**
 *
 * @author  Vassil Boyadjiev
 *
 * This example demonstrates how to remove, customize the UI of the editor
 *
 */
public class HTMLEditorRemovingMenus extends javax.swing.JFrame {

    /** Creates new form HTMLEditorRemovingMenus */
    public HTMLEditorRemovingMenus() {
        initComponents();
    }


    private void initComponents() {//GEN-BEGIN:initComponents
        hTMLEditor1 = new sferyx.administration.editors.HTMLEditor();

        //removes the following MenuItems: openLocationMenuItem, printFileMenuItem, closeFileMenuItem
        hTMLEditor1.setRemovedMenuItems("openLocationMenuItem, printFileMenuItem, closeFileMenuItem");
        //removes the following Menus: menuTools, menuHelp
        hTMLEditor1.setRemovedMenus("menuTools, menuHelp");
        //removes the following Toolbar items: fontUnderlineButton,fontItalicButton, fontBoldButton, alignRightButton,fontsList
        hTMLEditor1.setRemovedToolbarItems("fontUnderlineButton,fontItalicButton, fontBoldButton, alignRightButton,fontsList");


		addWindowListener(new java.awt.event.WindowAdapter() {
		            public void windowClosing(java.awt.event.WindowEvent evt) {
		                exitForm(evt);
		            }
        });
        getContentPane().add(hTMLEditor1, java.awt.BorderLayout.CENTER);

        pack();
    }//GEN-END:initComponents

    /** Exit the Application */
    private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
        System.exit(0);
    }//GEN-LAST:event_exitForm

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        new HTMLEditorRemovingMenus().show();
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private sferyx.administration.editors.HTMLEditor hTMLEditor1;
    // End of variables declaration//GEN-END:variables

}
