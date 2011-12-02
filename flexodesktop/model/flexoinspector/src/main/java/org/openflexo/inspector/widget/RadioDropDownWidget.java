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
package org.openflexo.inspector.widget;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.openflexo.inspector.AbstractController;
import org.openflexo.inspector.model.PropertyModel;

/**
 * @author bmangez
 * 
 *         To change the template for this generated type comment go to Window - Preferences - Java - Code Generation - Code and Comments
 */
public class RadioDropDownWidget extends MultipleValuesWidget {

	private JPanel _panel;

	private ButtonGroup _buttonGroup;

	private Hashtable _fullHash;

	private JRadioButton[] _radioButtonArray;

	/**
	 * @param model
	 */
	public RadioDropDownWidget(PropertyModel model, AbstractController controller) {
		super(model, controller);
		_panel = new JPanel();
		_panel.setOpaque(false);
		getDynamicComponent().addFocusListener(new WidgetFocusListener(this));

	}

	@Override
	public synchronized void updateWidgetFromModel() {
		widgetUpdating = true;
		Object initValue = getObjectValue();
		_fullHash = getHashValues();
		_panel.removeAll();
		_buttonGroup = new ButtonGroup();

		_panel.setLayout(new GridLayout(_fullHash.size(), 1));

		// _panel.setBackground(InspectorCst.BACK_COLOR);
		_radioButtonArray = new JRadioButton[_fullHash.size()];
		Enumeration en = _fullHash.keys();
		int j = 0;
		while (en.hasMoreElements()) {
			String curItem = (String) en.nextElement();
			Vector curVerctor = (Vector) _fullHash.get(curItem);

			JRadioButton rb = new JRadioButton(curItem, curItem.equals(initValue));
			// rb.setBackground(InspectorCst.BACK_COLOR);

			JComboBox _jComboBox = new JComboBox(new DefaultComboBoxModel(curVerctor));
			// _jComboBox.setBackground(InspectorCst.BACK_COLOR);

			rb.addActionListener(new MyRadioButtonListener(rb, curItem, _jComboBox));
			_jComboBox.addActionListener(new MyComboBoxListener(rb, curItem, _jComboBox));

			JPanel radioDropDownPanel = new JPanel();
			// radioDropDownPanel.setBackground(InspectorCst.BACK_COLOR);
			radioDropDownPanel.add(rb);
			radioDropDownPanel.add(_jComboBox);

			_panel.add(radioDropDownPanel);
			_radioButtonArray[j] = rb;
			_buttonGroup.add(rb);
			j++;
		}
		widgetUpdating = false;

	}

	/**
	 * Update the model given the actual state of the widget
	 */
	@Override
	public synchronized void updateModelFromWidget() {
	}

	private class MyRadioButtonListener implements ActionListener {
		JRadioButton rb;

		String val;

		JComboBox cb;

		public MyRadioButtonListener(JRadioButton radioButton, String l, JComboBox comboBox) {
			super();
			rb = radioButton;
			val = l;
			cb = comboBox;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (rb.isSelected()) {
				if (cb.getSelectedItem() == null) {
					cb.setSelectedItem(cb.getModel().getElementAt(0));
				}
				setObjectValue(cb.getSelectedItem());
			}
		}
	}

	private class MyComboBoxListener implements ActionListener {
		JRadioButton rb;

		String val;

		JComboBox cb;

		public MyComboBoxListener(JRadioButton radioButton, String l, JComboBox comboBox) {
			super();
			rb = radioButton;
			val = l;
			cb = comboBox;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (rb.isSelected()) {
				if (cb.getSelectedItem() == null) {
					cb.setSelectedItem(cb.getModel().getElementAt(0));
				}
				setObjectValue(cb.getSelectedItem());
			}
		}
	}

	@Override
	public JComponent getDynamicComponent() {
		return _panel;
	}

}
