/*
 * HTMLEditorTest.java
 *
 * Created on 12 February 2004, 08:13
 */
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.event.DocumentListener;

import sferyx.administration.editors.HTMLEditor;
/**
 *This example shows how to add various listeners to the editor
 *
 */
public class HTMLEditorTestListeners extends JFrame implements FocusListener, DocumentListener{
    HTMLEditor edHTMLEditor;
    JEditorPane edJEditorPane;
    public static void main(String args[]) {
        new HTMLEditorTestListeners();

    }

    public void focusGained(FocusEvent e) {
        System.out.println("Focus gained");
    }

    public void focusLost(FocusEvent e) {
         System.out.println("Focus lost");
    }

    public void changedUpdate(javax.swing.event.DocumentEvent documentEvent)
    {
        System.out.println("Changed");
    }

    public void insertUpdate(javax.swing.event.DocumentEvent documentEvent) {
        System.out.println("Inserted");
    }

    public void removeUpdate(javax.swing.event.DocumentEvent documentEvent) {

        System.out.println("Removed");
    }

    /** Creates a new instance of HTMLEditorTest */
    public HTMLEditorTestListeners() {
        Container con = getContentPane();
        con.setLayout(new GridLayout(1,2));
        con.add(edHTMLEditor = new HTMLEditor());
        con.add(edJEditorPane = new JEditorPane());

        edHTMLEditor.setContent("Hello");
        edHTMLEditor.getInternalJEditorPane().addFocusListener(this);
        edHTMLEditor.getInternalJEditorPane().getDocument().addDocumentListener(this);
        edJEditorPane.addFocusListener(this);

		setSize(600,400);
        setVisible(true);
    }



}
