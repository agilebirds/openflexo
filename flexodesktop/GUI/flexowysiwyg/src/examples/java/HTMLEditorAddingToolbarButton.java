/*
 * HTMLEditorAddingToolbarButton.java
 *
 *
 */

/**
 *
 * @author  Vassil Boyadjiev
 *
 * This example illustrates how to add additional toolbar buttons and control their behaviour on
 * the basis of the selected editor tab - visual, source, preview
 */
import javax.swing.JButton;

public class HTMLEditorAddingToolbarButton extends javax.swing.JFrame implements javax.swing.event.ChangeListener{


    public HTMLEditorAddingToolbarButton() {
        initComponents();

        //This simply adds new button on the desired toobar with the desired action command
        openLocationButton=hTMLEditor1.createMenuButton(hTMLEditor1.getEditingToolBar(), "Import from URL", "location-open", "file-open");
        hTMLEditor1.getMainTabbedPane().addChangeListener(this);
    }



    private void initComponents() {
        hTMLEditor1 = new sferyx.administration.editors.HTMLEditor();

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });


        //This illustrates how easy is to remove menus, menu items, toolbar buttons from the editor
		//see the usersmanual for the full list of names. It could be done directly from the IDE
        hTMLEditor1.setRemovedMenuItems("openLocationMenuItem, printFileMenuItem, closeFileMenuItem");
        hTMLEditor1.setRemovedMenus("menuTools, menuHelp");
        hTMLEditor1.setRemovedToolbarItems("fontUnderlineButton,fontItalicButton, fontBoldButton, alignRightButton,fontsList");
        getContentPane().add(hTMLEditor1, java.awt.BorderLayout.CENTER);

        pack();
    }

    /** Exit the Application */
    private void exitForm(java.awt.event.WindowEvent evt) {
        System.exit(0);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        new HTMLEditorAddingToolbarButton().show();
    }


    /**
    * Here we track the changes on the selection of the JTabbedPane
    * between Visual, Source, Preview
    */
    public void stateChanged(javax.swing.event.ChangeEvent changeEvent)
    {
        int editorState=hTMLEditor1.getEditorState();

        if(editorState==hTMLEditor1.VISUAL_EDITOR)
        {
            openLocationButton.setEnabled(true);
        }
        else
        {
            openLocationButton.setEnabled(false);
        }

    }

    JButton openLocationButton;
    private sferyx.administration.editors.HTMLEditor hTMLEditor1;


}
