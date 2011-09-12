/*
 * TransparencyExample.java
 *
 * Created on 15 giugno 2004, 16.11
 */

/**
 *
 * @author  Vassil Boyadjiev
 */
import java.awt.event.FocusListener;

import javax.swing.UIManager;


public class MultipleEditorsOneToolbar extends javax.swing.JFrame implements FocusListener{
    
    /** Creates new form MultipleEditorsOneToolbarExample */
    public MultipleEditorsOneToolbar() {
        initComponents();
        setSize(800,600);
        htmlEditor1=new sferyx.administration.editors.HTMLEditor();
        htmlEditor2=new sferyx.administration.editors.HTMLEditor();
        
           
        htmlEditor1.getInternalJEditorPane().addFocusListener(this);
        htmlEditor2.getInternalJEditorPane().addFocusListener(this);
        
        internalFrame1.setSize(400,400);
        internalFrame2.setSize(400,400);
        internalFrame1.setTitle("HTMLEditor 1");
        internalFrame2.setTitle("HTMLEditor 2");
        
        htmlEditor1.setSourceEditorVisible(false);
        htmlEditor1.setPreviewVisible(false);
        htmlEditor1.setMainMenuVisible(false);
        
        htmlEditor2.setSourceEditorVisible(false);
        htmlEditor2.setPreviewVisible(false);
        htmlEditor2.setMainMenuVisible(false);
      
        
        internalFrame1.getContentPane().add(htmlEditor1);
        internalFrame2.getContentPane().add(htmlEditor2);
        
        jDesktopPane1.setBackground(java.awt.Color.lightGray.brighter());
        setTitle("Mulitple Editors One Toolbar Example");
        
        
        toolbarPanel.removeAll();
        toolbarPanel.add(htmlEditor1.getEditingToolBar());
        toolbarPanel.add(htmlEditor1.getFormattingToolBar());    

        htmlEditor1.disableButtonsUpdate=true;
        htmlEditor2.disableButtonsUpdate=true;
        
        htmlEditor1.setToolBarVisible(false);
        htmlEditor2.setToolBarVisible(false);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        jDesktopPane1 = new javax.swing.JDesktopPane();
        internalFrame1 = new javax.swing.JInternalFrame();
        internalFrame2 = new javax.swing.JInternalFrame();
        toolbarPanel = new javax.swing.JPanel();

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });

        internalFrame1.setResizable(true);
        internalFrame1.setVisible(true);
        internalFrame1.setBounds(90, 130, 270, 140);
        jDesktopPane1.add(internalFrame1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        internalFrame2.setResizable(true);
        internalFrame2.setVisible(true);
        internalFrame2.setBounds(20, 10, 290, 130);
        jDesktopPane1.add(internalFrame2, javax.swing.JLayeredPane.DEFAULT_LAYER);

        getContentPane().add(jDesktopPane1, java.awt.BorderLayout.CENTER);

        toolbarPanel.setLayout(new java.awt.GridLayout(2, 1));

        getContentPane().add(toolbarPanel, java.awt.BorderLayout.NORTH);

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
         try
      {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      }
      catch(Exception exc)
      {
      }  
        
        MultipleEditorsOneToolbar example=new MultipleEditorsOneToolbar();
        example.show();
        //example.switchToolbars();
    }
    
    public void focusGained(java.awt.event.FocusEvent focusEvent) {
        
        switchToolbars();
       
    }
    
    public void switchToolbars()
    {
        
            sferyx.administration.editors.HTMLEditor activeHTMLEditor=htmlEditor1.getActiveHTMLEditor();

            if(activeHTMLEditor==null) activeHTMLEditor=htmlEditor1;

            toolbarPanel.removeAll();
            toolbarPanel.add(activeHTMLEditor.getEditingToolBar());
            toolbarPanel.add(activeHTMLEditor.getFormattingToolBar());
            
    }
    
    
   
    
    public void focusLost(java.awt.event.FocusEvent focusEvent) {
      
        
    }
    
    
    
    boolean actionsInited=false;
    sferyx.administration.editors.HTMLEditor htmlEditor1;
    sferyx.administration.editors.HTMLEditor htmlEditor2;
   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JInternalFrame internalFrame2;
    private javax.swing.JPanel toolbarPanel;
    private javax.swing.JDesktopPane jDesktopPane1;
    private javax.swing.JInternalFrame internalFrame1;
    // End of variables declaration//GEN-END:variables
    
}
