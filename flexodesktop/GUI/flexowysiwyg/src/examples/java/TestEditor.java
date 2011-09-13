/*
 * TestEditor.java
 *
 *
 *
 * This example illustrates the use of the editor from subclasses.
 * You can follow the javadoc documentation in order to ovverride
 * methods in order to change the editor's overall llok & behaviour.
 */

import java.net.URL;

import javax.swing.JFrame;

import sferyx.administration.editors.EditorHTMLDocument;
import sferyx.administration.editors.HTMLEditor;

/**
 *
 * @author  Vassil Boyadjiev
 */
public class TestEditor extends HTMLEditor {

    /** Creates a new instance of TestEditor */
    public TestEditor() {

      //  boolean sourceEditorVisible, boolean previewVisible, boolean toolbarsVisible, boolean mainMenuVisible, boolean statusbarVisible, boolean popupMenuVisible
        super(true, true,true, true, false,true);
        // initLookAndFeel(); // -if you wish to get the default system look & feel
        setRemovedToolbarItems("openFileButton, printFileButton");
        setRemovedMenus("menuEdit");
        setRemovedMenuItems("printFileMenuItem,openFileMenuItem ,newFileMenuItem ");
        setRemovedToolbarItems("openFileButton, printFileButton");

    }



    public static void main(String[] args)
    {

        TestEditor editor=new TestEditor();
        JFrame jframe=new JFrame();
        jframe.setSize(800,600);
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.getContentPane().add(editor);
        jframe.show();



     try
     {
		 // this will change the document base as desired
		 // Be careful when using this method since it may impact the correct resolving of
		 // relative images/objects/hyperlinks

        ((EditorHTMLDocument)(editor.getInternalJEditorPane().getDocument())).setBase(new URL("http://localhost/"));
     }
     catch(Exception except)
     {

     }

    }

}
