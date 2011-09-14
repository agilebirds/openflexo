/*
 * Copyright 2002-2004 Sferyx Srl. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL Information. Use is subject to license terms.
 */

/*
 * ChineseRenderingExample.java
 *
 * Created on 7 giugno 2004, 15.13
 */



import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.util.Locale;

import javax.swing.JFrame;
import javax.swing.UIManager;
/**
 *
 * @author  Vassil Boyadjiev
 */
public class ChineseRenderingExample {

    /** Creates a new instance of ChineseRenderingExample */
    public ChineseRenderingExample() {
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

	  systemFont=new Font("SimHei",Font.PLAIN,12);

	 



      // Load a chinese page here:
	//  htmlEditor.openLocation("http://www.china.com");


      //Load your style sheet here.
      //We load it after the page in this case to override the style sheet loaded from the site
      //since there is a link tag which loads their style sheet.
      htmlEditor.loadExternalStyleSheet("http://www.example.com/chinese.css");
       changeFontRecursively(htmlEditor);

      jf.getContentPane().add("Center", htmlEditor);
      jf.setSize(800,600);
      jf.setLocation(100,100);
      jf.show();
      jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }



    static Font systemFont;


// This utility method will change the font with the Chinese one "SimHei" in this case
// all over the component tree of the editor. This is useful when some font is needed to be forced.

    public static void changeFontRecursively(Container parentComponent)
    {
        Component[] myComponents = parentComponent.getComponents();
        for (int i=0; i < myComponents.length; i++)
        {
            ((Component)myComponents[i]).setFont(systemFont);
            ((Component)myComponents[i]).setLocale(Locale.getDefault());

                if (myComponents[i] instanceof Container)
                {
                    changeFontRecursively((Container)myComponents[i]);
                }
        }
    }



}