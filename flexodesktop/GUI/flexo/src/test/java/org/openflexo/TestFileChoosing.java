/*
 * (c) Copyright 2010-2011 AgileBirds
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openflexo;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.openflexo.AdvancedPrefs;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.localization.FlexoLocalization;

public class TestFileChoosing {

	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		System.out.println("polo");
		final JFrame dialog = new JFrame();
			
			JButton closeButton = new JButton("Close");
			closeButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					dialog.dispose();
					System.exit(0);
				}
			});
			
			JButton openButton1 = new JButton("JFileChooser- open");
			openButton1.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JFileChooser chooser = new JFileChooser();
					chooser.setCurrentDirectory(AdvancedPrefs.getLastVisitedDirectory());
					chooser.setDialogTitle(FlexoLocalization.localizedForKey("select_a_prj_directory"));
					chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
					chooser.setFileFilter(FlexoProject.getFileFilter());
					chooser.setFileView(FlexoProject.getFileView());
					chooser.showOpenDialog(dialog);
				}
			});
			
			JButton openButton2 = new JButton("FileDialog- open");
			openButton2.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					FileDialog fileDialog = new FileDialog(dialog);
					//fileDialog.setFilenameFilter(filter)
					//fileDialog.set
					try {
						fileDialog.setDirectory(AdvancedPrefs.getLastVisitedDirectory().getCanonicalPath());
					} catch (Throwable t) {
					}
					fileDialog.setVisible(true);
					
				}
			});
			
			
			JPanel controlPanel = new JPanel(new FlowLayout());
			controlPanel.add(closeButton);
			controlPanel.add(openButton1);
			controlPanel.add(openButton2);
			
			JPanel panel = new JPanel(new BorderLayout());
			
			panel.add(controlPanel,BorderLayout.CENTER);
			
			dialog.setPreferredSize(new Dimension(1000,800));
			dialog.getContentPane().add(panel);
			dialog.validate();
			dialog.pack();
			dialog.setVisible(true);
			dialog.getRootPane().putClientProperty(WINDOW_MODIFIED, Boolean.TRUE);
			dialog.setVisible(true);
			//Editor.main(null);
		}

	final static String WINDOW_MODIFIED = "windowModified";
	
	public static class Editor extends JFrame
	  implements DocumentListener, ActionListener {

	    final static String WINDOW_MODIFIED = "windowModified";

	    JEditorPane jp;
	    JMenuBar jmb;
	    JMenu file;
	    JMenuItem save;

	    public Editor(String title) {
	        super(title);
	        jp = new JEditorPane();
	        jp.getDocument().addDocumentListener(this);
	        getContentPane().add(jp);
	        jmb = new JMenuBar();
	        file = new JMenu("File");
	        save = new JMenuItem("Save");
	        save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
	          java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
	        save.addActionListener(this);
	        file.add(save);
	        jmb.add(file);
	        setJMenuBar(jmb);
	        setSize(400,600);
	        setVisible(true);
	    }

	    // doChange() and actionPerformed() handle the "windowModified" state
	    public void doChange() {
	        getRootPane().putClientProperty(WINDOW_MODIFIED, Boolean.TRUE);
	    }

	    @Override
		public void actionPerformed(ActionEvent e) {
	        // save functionality here...
	        getRootPane().putClientProperty(WINDOW_MODIFIED, Boolean.FALSE);
	    }

	    // DocumentListener implementations
	    @Override
		public void changedUpdate(DocumentEvent e) { doChange(); }
	    @Override
		public void insertUpdate(DocumentEvent e)  { doChange(); }
	    @Override
		public void removeUpdate(DocumentEvent e)  { doChange(); }

	    public static void main(String[] args) {
	        new Editor("test");
	    }
	}
}
