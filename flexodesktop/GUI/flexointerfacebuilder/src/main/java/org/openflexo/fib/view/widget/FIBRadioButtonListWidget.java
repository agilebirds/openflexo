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
package org.openflexo.fib.view.widget;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBRadioButtonList;

public class FIBRadioButtonListWidget extends FIBMultipleValueWidget<FIBRadioButtonList, JPanel, Object>
{

	static final Logger logger = Logger.getLogger(FIBRadioButtonListWidget.class.getPackage().getName());

	private JRadioButton[] radioButtonArray;

	private JPanel panel;

	private ButtonGroup buttonGroup;

	private Object selectedValue;

	public FIBRadioButtonListWidget(FIBRadioButtonList model, FIBController controller)
	{
		super(model,controller);
		buttonGroup = new ButtonGroup();
		panel = new JPanel(new GridLayout(0, model.getColumns()));
		rebuildRadioButtons();
		if (getWidget().getAutoSelectFirstRow() && getListModel().getSize() > 0) {
			radioButtonArray[0].setSelected(true);
			notifyDynamicModelChanged();
		}
	}

	protected void rebuildRadioButtons()
	{
		panel.removeAll();
		buttonGroup = new ButtonGroup();
		radioButtonArray = new JRadioButton[getListModel().getSize()];
		for (int i = 0; i < getListModel().getSize(); i++) {
			Object object = getListModel().getElementAt(i);
			JRadioButton rb = new JRadioButton(getStringRepresentation(object), equals(object, selectedValue));
			rb.addActionListener(new RadioButtonListener(rb, object, i));
			radioButtonArray[i] = rb;
			panel.add(rb);
			buttonGroup.add(rb);
		}
		panel.validate();
	}

	@Override
	public synchronized boolean updateWidgetFromModel()
	{
		Object value = getValue();
		if (notEquals(value, selectedValue) || listModelRequireChange() || listModel == null) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("updateWidgetFromModel()");
			}
			
			widgetUpdating = true;
			selectedValue = value;
			rebuildRadioButtons();
			
			if (getValue() == null && getWidget().getAutoSelectFirstRow() && getListModel().getSize() > 0) {
				radioButtonArray[0].setSelected(true);
			}

			widgetUpdating = false;
			return true;
		}
		return false;
	}

	private class RadioButtonListener implements ActionListener {

		private final Object value;
		private final JRadioButton button;
		private final int index;

		public RadioButtonListener(JRadioButton button, Object value, int index) {
			this.button = button;
			this.value = value;
			this.index = index;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == button && button.isSelected()) {
				selectedValue = value;
				updateModelFromWidget();
				getDynamicModel().selected = value;
				getDynamicModel().selectedIndex = index;
				notifyDynamicModelChanged();
			}
		}

	}

	/**
	 * Update the model given the actual state of the widget
	 */
	@Override
	public synchronized boolean updateModelFromWidget()
	{
		if (notEquals(getValue(), selectedValue)) {
			modelUpdating = true;
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("updateModelFromWidget with " + selectedValue);
			}
			if (selectedValue != null && !widgetUpdating) {
				setValue(selectedValue);
			}
			modelUpdating = false;
			return true;
		}
		return false;
	}

	@Override
	public JPanel getJComponent()
	{
		return panel;
	}

	@Override
	public JPanel getDynamicJComponent()
	{
		return panel;
	}

	@Override
	public void updateFont()
	{
		super.updateFont();
		for (JRadioButton rb : radioButtonArray) {
			rb.setFont(getFont());
		}
	}


}
