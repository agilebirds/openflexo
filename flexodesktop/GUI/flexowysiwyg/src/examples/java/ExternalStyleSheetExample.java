/*
 * Copyright 2002-2004 Sferyx Srl. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL Information. Use is subject to license terms.
 */

/*
 * ExternalStyleSheetExample.java
 *
 * Created on 17 aprile 2004, 15.13
 */



import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.UIManager;
/**
 *
 * @author  Vassil Boyadjiev
 */
public class ExternalStyleSheetExample {

    /** Creates a new instance of StandAloneApplication */
    public ExternalStyleSheetExample() {
    }

    public static void main(String[] args)
    {
       try
      {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      }
      catch(Exception exc)
      {
      }

      JFrame jf=new JFrame();
      jf.setBackground(Color.white);
      jf.setTitle("Sferyx HTML Editor");
      jf.getContentPane().setLayout(new BorderLayout());


      sferyx.administration.editors.HTMLEditor htmlEditor=new sferyx.administration.editors.HTMLEditor();

	  //or you can use alternative constructor

	  // sferyx.administration.editors.HTMLEditor htmlEditor = new sferyx.administration.editors.HTMLEditor(false,false,false,false,false,false);

	  htmlEditor.setContent("Hello");

      //Load your language file here:
      htmlEditor.loadInterfaceLanguageFile("file:///C:/temp/sample-german-translation.txt");

      //Load your style sheet here:
      htmlEditor.loadExternalStyleSheet("file:///C:/temp/your.css");

      // If you want to see only the body content in the source pane
      htmlEditor.setShowBodyContentOnlyInSource("true");


	  jf.getContentPane().add("Center", htmlEditor);
      jf.setSize(800,600);
      jf.setLocation(100,100);
      jf.show();
      jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }




}