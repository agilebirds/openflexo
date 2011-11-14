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
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.openflexo.foundation.dkv.DKVModel.LanguageList;
import org.openflexo.foundation.dkv.Key;
import org.openflexo.foundation.dkv.Language;
import org.openflexo.foundation.ie.IERegExp;
import org.openflexo.foundation.ie.cl.FlexoComponentLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.swing.JTextFieldRegExp;
import org.openflexo.view.FlexoDialog;

public class AskNewKeyDialog extends FlexoDialog implements ActionListener {

	protected int returned;

	public static final int CANCEL = 0;

	public static final int VALIDATE = 1;

	public static final int VALIDATE_AND_REDO = 2;

	protected JTextFieldRegExp newKeyTextField;

	protected JTextArea newKeyDescriptionTextArea;

	protected JButton confirmButton;

	protected JButton confirmRedoButton;

	protected JButton cancelButton;

	protected Vector<KeyValueJTextField> valueTextFieldList;

	public int getStatus() {
		return returned;
	}

	public AskNewKeyDialog(Frame owner, FlexoComponentLibrary lib, Key lastCreatedKey) {
		super(owner);
		newKeyTextField = IERegExp.getMaxLength10ValidationTextField(10);
		newKeyDescriptionTextArea = new JTextArea();
		newKeyDescriptionTextArea.setRows(4);
		newKeyDescriptionTextArea.setColumns(30);
		newKeyDescriptionTextArea.setBorder(new EtchedBorder());
		newKeyDescriptionTextArea.setLineWrap(true);
		newKeyDescriptionTextArea.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}

			@Override
			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar() == KeyEvent.VK_TAB) {
					if (newKeyDescriptionTextArea.getText() != null && newKeyDescriptionTextArea.getText().length() > 0) {
						newKeyDescriptionTextArea.setText(newKeyDescriptionTextArea.getText().substring(0,
								newKeyDescriptionTextArea.getText().length() - 1));
					}
					if (e.getModifiers() == InputEvent.SHIFT_DOWN_MASK || e.getModifiers() == InputEvent.SHIFT_MASK) {
						newKeyTextField.requestFocus();
					} else {
						valueTextFieldList.get(0).requestFocus();
					}
					e.consume();
				}

			}
		});
		valueTextFieldList = new Vector<KeyValueJTextField>();
		LanguageList languageList = lib.getProject().getDKVModel().getLanguageList();
		Enumeration<Language> en = languageList.getLanguages().elements();
		while (en.hasMoreElements()) {
			Language l = en.nextElement();
			valueTextFieldList.add(new KeyValueJTextField(l, newKeyTextField));
		}
		returned = CANCEL;

		newKeyTextField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent e) {
				checkValidity();
				propagateRemoveInKey();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				checkValidity();
				propagateInsertInKey();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				checkValidity();
				propagateRemoveInKey();
			}

			private void checkValidity() {
				if (newKeyTextField.hasError()) {
					confirmButton.setEnabled(false);
					confirmRedoButton.setEnabled(false);
				} else {
					confirmButton.setEnabled(true);
					confirmRedoButton.setEnabled(true);
				}
			}
		});
		// setFocusTraversalPolicy(new MyFocusTraversalPolicy(newKeyTextField));

		setTitle(FlexoLocalization.localizedForKey("define_a_new_key"));

		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(5, 5, 5, 5);
		int row = 0;
		if (lastCreatedKey != null) {
			c.weightx = 0.0;
			c.gridx = 0;
			c.gridy = row;
			c.gridwidth = 2;
			c.anchor = GridBagConstraints.CENTER;
			JLabel infoLabel = new JLabel(lastCreatedKey.getName() + " was succesfully created.");
			contentPanel.add(infoLabel, c);
			c.gridwidth = 1;
			row++;
		}

		// row
		JLabel keyLabel = new JLabel("Key : ");
		c.weightx = 0.0;
		c.gridx = 0;
		c.gridy = row;
		c.anchor = GridBagConstraints.EAST;
		contentPanel.add(keyLabel, c);

		c.weightx = 0.5;
		c.gridx = 1;
		c.gridy = row;
		c.anchor = GridBagConstraints.WEST;
		contentPanel.add(newKeyTextField, c);
		row++;
		// row
		JLabel descLabel = new JLabel("Description : ");
		c.weightx = 0.0;
		c.gridx = 0;
		c.gridy = row;
		c.anchor = GridBagConstraints.NORTHEAST;
		contentPanel.add(descLabel, c);

		c.weightx = 1.0;
		c.gridx = 1;
		c.gridy = row;
		c.anchor = GridBagConstraints.WEST;
		contentPanel.add(newKeyDescriptionTextArea, c);
		row++;

		Enumeration<KeyValueJTextField> en2 = valueTextFieldList.elements();
		KeyValueJTextField tf = null;
		JLabel languageLabel = null;
		while (en2.hasMoreElements()) {
			tf = en2.nextElement();
			languageLabel = new JLabel(tf.getLanguage().getName() + " : ");

			c.weightx = 0.0;
			c.gridx = 0;
			c.gridy = row;
			c.anchor = GridBagConstraints.EAST;
			contentPanel.add(languageLabel, c);

			c.weightx = 1.0;
			c.gridx = 1;
			c.gridy = row;
			c.anchor = GridBagConstraints.WEST;
			contentPanel.add(tf, c);
			row++;
		}

		contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new FlowLayout());

		confirmButton = new JButton(FlexoLocalization.localizedForKey("validate"));
		confirmRedoButton = new JButton(FlexoLocalization.localizedForKey("validate_and_redo"));
		cancelButton = new JButton(FlexoLocalization.localizedForKey("cancel"));

		getRootPane().setDefaultButton(cancelButton);
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
				returned = VALIDATE;
				dispose();
			}
		});
		confirmRedoButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				returned = VALIDATE_AND_REDO;
				dispose();
			}
		});
		controlPanel.add(cancelButton);
		controlPanel.add(confirmButton);
		controlPanel.add(confirmRedoButton);

		JPanel everythingPane = new JPanel(new BorderLayout());

		everythingPane.add(controlPanel, BorderLayout.SOUTH);

		everythingPane.add(contentPanel, BorderLayout.CENTER);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(everythingPane, BorderLayout.CENTER);

		setModal(true);
		validate();
		pack();
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((dim.width - getSize().width) / 2, (dim.height - getSize().height) / 2);
		confirmButton.setEnabled(false);
		confirmRedoButton.setEnabled(false);
		newKeyTextField.grabFocus();
		setVisible(true);
	}

	public void propagateRemoveInKey() {
		if (newKeyTextField.getText() != null && !newKeyTextField.getText().equals("")) {
			getRootPane().setDefaultButton(confirmRedoButton);
		} else {
			getRootPane().setDefaultButton(cancelButton);
		}
		Enumeration<KeyValueJTextField> en = valueTextFieldList.elements();
		while (en.hasMoreElements()) {
			KeyValueJTextField tf = en.nextElement();
			String valueText = tf.getText();
			if (tf.getText() == null || tf.getText().trim().length() == 0
					|| (newKeyTextField.getText().equals(valueText.substring(0, valueText.length() - 1)))) {
				tf.setText(newKeyTextField.getText());
			}
		}
	}

	public void propagateInsertInKey() {
		getRootPane().setDefaultButton(confirmRedoButton);

		Enumeration<KeyValueJTextField> en = valueTextFieldList.elements();
		while (en.hasMoreElements()) {
			KeyValueJTextField tf = en.nextElement();
			if (tf.getText() == null || tf.getText().trim().length() == 0
					|| tf.getText().equals(newKeyTextField.getText().substring(0, newKeyTextField.getText().length() - 1))) {
				tf.setText(newKeyTextField.getText());
			}
		}

	}

	/** Listens to the radio buttons. */

	@Override
	public void actionPerformed(ActionEvent e) {

	}

	public String getNewKey() {
		return newKeyTextField.getText();
	}

	public String getValueForLanguage(Language l) {
		Enumeration<KeyValueJTextField> en = valueTextFieldList.elements();
		KeyValueJTextField tf = null;
		while (en.hasMoreElements()) {
			tf = en.nextElement();
			if (tf.getLanguage() == l) {
				return tf.getText();
			}
		}
		return null;
	}

	public String getKeyDescrition() {
		return newKeyDescriptionTextArea.getText();
	}

	private class MyFocusTraversalPolicy extends DefaultFocusTraversalPolicy {

		private Component _firstComponent;

		public MyFocusTraversalPolicy(Component firstComponent) {
			super();
			_firstComponent = firstComponent;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.ContainerOrderFocusTraversalPolicy#getFirstComponent(java.awt.Container)
		 */
		@Override
		public Component getFirstComponent(Container arg0) {
			return _firstComponent;
		}

	}

	private static class KeyValueJTextField extends JTextField implements FocusListener {

		@Override
		public void focusGained(FocusEvent e) {
			setSelectionStart(0);
			setSelectionEnd(getText().length());

		}

		@Override
		public void focusLost(FocusEvent e) {
			// TODO Auto-generated method stub

		}

		private JTextField _keyField;

		private Language _l;

		public KeyValueJTextField(Language l, JTextField keyField) {
			super();
			setColumns(30);
			_keyField = keyField;
			_l = l;
			addFocusListener(this);
		}

		public Language getLanguage() {
			return _l;
		}
	}
}
