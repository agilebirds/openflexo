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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JTextArea;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class TestCustomPopup extends TextFieldCustomPopup<Object>
{

	private JTextArea _textArea;

	private JButton _button;

	public TestCustomPopup(String editedObject)
	{
		super(editedObject);
	}

	@Override
	protected ResizablePanel createCustomPanel(Object editedObject)
	{
		ResizablePanel customPanel;
		customPanel = new ResizablePanel() {
			@Override
			public Dimension getDefaultSize()
			{
				return getPreferredSize();
			}

			@Override
			public void setPreferredSize(Dimension aDimension)
			{
			}

		};
		customPanel.setLayout(new BorderLayout());
		customPanel.add(_textArea = new JTextArea((String) editedObject), BorderLayout.CENTER);
		customPanel.add(_button = new JButton("add hop"), BorderLayout.SOUTH);
		_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				setEditedObject((String) getEditedObject() + "hop");
			}
		});
		return customPanel;
	}

	@Override
	public void updateCustomPanel(Object editedObject)
	{
		_textArea.setText((String) editedObject);
	}

	@Override
	public String renderedString(Object editedObject)
	{
		return (String) editedObject;
	}

}
