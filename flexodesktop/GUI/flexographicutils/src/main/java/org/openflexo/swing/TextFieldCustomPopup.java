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
package org.openflexo.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

/**
 * Abstract widget allowing to edit a complex object with a popup
 * 
 * @author sguerin
 * 
 */
public abstract class TextFieldCustomPopup<T> extends CustomPopup<T> {

	JPanel _frontComponent;
	JTextField _textField;
	JLabel _label;
	private int requestedColNumber = -1;

	public TextFieldCustomPopup(T editedObject) {
		this(editedObject, -1);
	}

	public TextFieldCustomPopup(T editedObject, int cols) {
		super(editedObject);
		requestedColNumber = cols;
		if (requestedColNumber > 0) {
			_textField.setColumns(requestedColNumber);
		}
		_textField.setText(renderedString(editedObject));
	}

	/*// Override to use specific border
	@Override
	protected Border getDownButtonBorder()
	{
		return null;
	}*/

	@Override
	protected JComponent buildFrontComponent() {
		_frontComponent = new JPanel(new BorderLayout());
		_frontComponent.setOpaque(false);
		_textField = new JTextField();
		_textField.setEditable(false);
		_textField.addActionListener(this);
		_textField.setMinimumSize(new Dimension(50, 25));
		_label = new JLabel();
		Insets labelInsets = _textField.getBorder().getBorderInsets(_textField);
		labelInsets.left = 0;
		_label.setBorder(new EmptyBorder(labelInsets));
		_frontComponent.add(_textField, BorderLayout.CENTER);
		_frontComponent.add(_label, BorderLayout.EAST);
		_label.setVisible(false);
		return _frontComponent;
	}

	@Override
	public void fireEditedObjectChanged() {
		super.fireEditedObjectChanged();
		updateTextFieldProgrammaticaly();
	}

	public void updateTextFieldProgrammaticaly() {
		String cur = _textField.getText();
		String val = renderedString(getEditedObject());
		if (cur == null && val == null) {
			return;
		}
		if (cur != null && cur.equals(val)) {
			return;
		}

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				_isProgrammaticalySet = true;
				_textField.setText(renderedString(getEditedObject()));
				_isProgrammaticalySet = false;
			}
		});

	}

	public abstract String renderedString(T editedObject);

	public boolean _isProgrammaticalySet = false;

	public boolean isProgrammaticalySet() {
		return _isProgrammaticalySet;
	}

	public void setProgrammaticalySet(boolean aFlag) {
		_isProgrammaticalySet = aFlag;
	}

	@Override
	public abstract void updateCustomPanel(T editedObject);

	public JTextField getTextField() {
		return _textField;
	}

	public JLabel getLabel() {
		return _label;
	}

	@Override
	public JPanel getFrontComponent() {
		return (JPanel) super.getFrontComponent();
	}

	@Override
	public void setFont(Font aFont) {
		super.setFont(aFont);
		if (_textField != null) {
			_textField.setFont(aFont);
		}
		if (_label != null) {
			_label.setFont(aFont);
		}
	}

}
