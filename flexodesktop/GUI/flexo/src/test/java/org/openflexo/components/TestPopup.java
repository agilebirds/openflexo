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
package org.openflexo.components;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;

import org.openflexo.components.widget.DMEntitySelector;
import org.openflexo.components.widget.DMTypeSelector;
import org.openflexo.foundation.DefaultFlexoEditor;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoEditor.FlexoEditorFactory;
import org.openflexo.foundation.FlexoTestCase.FlexoTestEditor;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoResourceManager;
import org.openflexo.foundation.utils.ProjectInitializerException;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;

public class TestPopup {

	protected static final FlexoEditorFactory EDITOR_FACTORY = new FlexoEditorFactory() {
		@Override
		public DefaultFlexoEditor makeFlexoEditor(FlexoProject project) {
			return new FlexoTestEditor(project);
		}
	};

	static FlexoEditor editor;
	static FlexoProject prj;

	static {
		try {
			editor = FlexoResourceManager.initializeExistingProject(
					new File("/Users/sylvain/Documents/TestsFlexo/TestBindingSelector.prj"), EDITOR_FACTORY, null);
		} catch (ProjectLoadingCancelledException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ProjectInitializerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		prj = editor.getProject();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		final JFrame frame = new JFrame();
		JPanel panel = new JPanel(new BorderLayout());
		frame.setContentPane(panel);
		JButton button = new JButton("coucou");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showDialog(frame);
				frame.toFront();
			}
		});
		panel.add(button, BorderLayout.SOUTH);
		frame.pack();
		frame.setVisible(true);
	}

	protected static void showDialog(JFrame frame) {
		final JDialog dialog = new JDialog(frame, true);
		JPanel panel = new JPanel(new BorderLayout());
		// panel.add(new JLabel("south"),BorderLayout.SOUTH);
		// panel.add(new JTextArea("un texte",10,30),BorderLayout.CENTER);
		// panel.add(new DateSelector(new Date()),BorderLayout.NORTH);
		panel.add(new DMEntitySelector<DMEntity>(prj, null, DMEntity.class), BorderLayout.SOUTH);
		panel.add(new JTextArea("un texte", 10, 30), BorderLayout.CENTER);
		panel.add(new DMTypeSelector(prj, null, true), BorderLayout.NORTH);
		dialog.setContentPane(panel);
		/*JButton button = new JButton("popup");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showPopup(dialog);
			}
		});
		panel.add(button,BorderLayout.NORTH);*/
		dialog.pack();
		dialog.setVisible(true);
	}

	protected static void showPopup(JDialog dialog) {
		System.out.println("coucou le popup");

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(new JLabel("north"), BorderLayout.NORTH);
		panel.add(new JLabel("center"), BorderLayout.CENTER);

		JButton quitButton = new JButton("quit");
		quitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		panel.add(quitButton, BorderLayout.SOUTH);

		JPopupMenu popup = new JPopupMenu();
		popup.insert(panel, 0);
		popup.setInvoker(dialog);

		Point position = new Point(100, 100);
		popup.show(dialog, position.x, position.y);

	}

}
