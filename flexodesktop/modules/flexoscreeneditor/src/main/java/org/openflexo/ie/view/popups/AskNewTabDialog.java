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
package org.openflexo.ie.view.popups;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.DefaultFocusTraversalPolicy;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.openflexo.foundation.ie.IERegExp;
import org.openflexo.foundation.ie.cl.FlexoComponentLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.ToolBox;
import org.openflexo.view.FlexoDialog;

/**
 * Popup allowing to choose a way to select an existing sub-process or create a new one
 * 
 * @author sguerin
 * 
 */
public class AskNewTabDialog extends FlexoDialog implements ActionListener {

	protected int returned;

	public static final int CANCEL = 0;

	public static final int VALIDATE_EXISTING_TAB = 1;

	public static final int VALIDATE_NEW_TAB = 2;

	private static final String EXISTING_TAB = "EXISTING_TAB";

	private static final String NEW_TAB = "NEW_TAB";

	private JComboBox existingProcessChoice;

	protected JTextField newTabChoice;

	protected JTextField tabTitle;

	protected JButton confirmButton;

	protected JButton cancelButton;

	public int getStatus() {
		return returned;
	}

	public AskNewTabDialog(FlexoComponentLibrary lib) {
		super();
		returned = VALIDATE_NEW_TAB;

		Vector availableTabs = (Vector) lib.getTabComponentList().clone();

		setTitle(FlexoLocalization.localizedForKey("select_a_choice"));
		getContentPane().setLayout(new BorderLayout());

		// Create the radio buttons.
		final JRadioButton existingTabButton = new JRadioButton(FlexoLocalization.localizedForKey("bind_existing_tab"));
		existingTabButton.setSelected(false);
		existingTabButton.addActionListener(this);
		existingTabButton.setActionCommand(EXISTING_TAB);
		final JRadioButton newTabButton = new JRadioButton(FlexoLocalization.localizedForKey("create_new_tab"));
		newTabButton.addActionListener(this);
		newTabButton.setActionCommand(NEW_TAB);
		tabTitle = new JTextField();
		// Group the radio buttons.
		ButtonGroup group = new ButtonGroup();
		group.add(existingTabButton);
		group.add(newTabButton);
		newTabButton.setSelected(true);

		existingProcessChoice = new JComboBox(availableTabs);
		existingProcessChoice.setEnabled(false);
		existingProcessChoice.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				existingTabButton.doClick();
			}
		});
		newTabChoice = IERegExp.getJavaClassNameValidationTextField(15);
		newTabChoice.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent e) {
				removeUpdate(e);
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				getRootPane().setDefaultButton(confirmButton);
				newTabButton.doClick();
				if (tabTitle.getText() == null || tabTitle.getText().trim().length() == 0
						|| tabTitle.getText().equals(newTabChoice.getText().substring(0, newTabChoice.getText().length() - 1)))
					tabTitle.setText(newTabChoice.getText());
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				if (newTabChoice.getText() != null && !newTabChoice.getText().equals("")) {
					getRootPane().setDefaultButton(confirmButton);
				} else {
					getRootPane().setDefaultButton(cancelButton);
				}
				if (tabTitle.getText() == null || tabTitle.getText().trim().length() == 0
						|| newTabChoice.getText().equals(tabTitle.getText().substring(0, tabTitle.getText().length() - 1)))
					tabTitle.setText(newTabChoice.getText());
			}
		});
		// newProcessChoice.setFocusable(true);
		// this.setFocusable(true);
		// newProcessChoice.setRequestFocusEnabled(true);
		setFocusTraversalPolicy(new MyFocusTraversalPolicy(newTabChoice));
		JPanel choicePanel = new JPanel();

		if (availableTabs.size() > 0) {
			choicePanel.setLayout(new GridLayout(3, 2));
		} else {
			choicePanel.setLayout(new GridLayout(2, 2));
		}
		if (availableTabs.size() > 0) {
			choicePanel.add(existingTabButton);
			choicePanel.add(existingProcessChoice);
		}
		choicePanel.add(newTabButton);
		choicePanel.add(newTabChoice);
		choicePanel.add(new JLabel(FlexoLocalization.localizedForKey("tab_title")));
		choicePanel.add(tabTitle);
		choicePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new FlowLayout());

		confirmButton = new JButton(FlexoLocalization.localizedForKey("validate"));
		cancelButton = new JButton(FlexoLocalization.localizedForKey("cancel"));
		rootPane.setDefaultButton(cancelButton);
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				returned = CANCEL;
				dispose();
			}
		});
		confirmButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		if (ToolBox.getPLATFORM() == ToolBox.MACOS) {
			controlPanel.add(cancelButton);
			controlPanel.add(confirmButton);
		} else {
			controlPanel.add(confirmButton);
			controlPanel.add(cancelButton);
		}

		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BorderLayout());

		contentPanel.add(choicePanel, BorderLayout.CENTER);
		contentPanel.add(controlPanel, BorderLayout.SOUTH);

		getContentPane().add(contentPanel, BorderLayout.CENTER);

		setModal(true);
		validate();
		pack();
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((dim.width - getSize().width) / 2, (dim.height - getSize().height) / 2);
		newTabChoice.grabFocus();
		getRootPane().setDefaultButton(confirmButton);
		setVisible(true);
	}

	/** Listens to the radio buttons. */

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals(EXISTING_TAB)) {
			existingProcessChoice.setEnabled(true);
			newTabChoice.setEnabled(false);
			returned = VALIDATE_EXISTING_TAB;
		} else if (e.getActionCommand().equals(NEW_TAB)) {
			newTabChoice.setEnabled(true);
			existingProcessChoice.setEnabled(false);
			newTabChoice.requestFocusInWindow();
			returned = VALIDATE_NEW_TAB;
		}
	}

	public String getTabName() {
		if (returned == VALIDATE_EXISTING_TAB) {
			return existingProcessChoice.getSelectedItem().toString();
		} else if (returned == VALIDATE_NEW_TAB) {
			return newTabChoice.getText();
		}
		return null;
	}

	public String getTabTitle() {
		return tabTitle.getText();
	}

	private class MyFocusTraversalPolicy extends DefaultFocusTraversalPolicy {

		private Component _firstComponent;

		public MyFocusTraversalPolicy(Component firstComponent) {
			super();
			_firstComponent = firstComponent;
		}

		/* (non-Javadoc)
		 * @see java.awt.ContainerOrderFocusTraversalPolicy#getFirstComponent(java.awt.Container)
		 */
		@Override
		public Component getFirstComponent(Container arg0) {
			return _firstComponent;
		}

	}
}
