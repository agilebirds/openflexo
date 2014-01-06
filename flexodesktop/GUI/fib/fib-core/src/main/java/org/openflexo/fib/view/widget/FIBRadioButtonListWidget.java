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

public class FIBRadioButtonListWidget<T> extends FIBMultipleValueWidget<FIBRadioButtonList, JPanel, T, T> {

	static final Logger logger = Logger.getLogger(FIBRadioButtonListWidget.class.getPackage().getName());

	private JRadioButton[] radioButtonArray;

	private JPanel panel;

	private ButtonGroup buttonGroup;

	private T selectedValue;

	public FIBRadioButtonListWidget(FIBRadioButtonList model, FIBController controller) {
		super(model, controller);
		buttonGroup = new ButtonGroup();
		panel = new JPanel(new GridLayout(0, model.getColumns(), model.getHGap(), model.getVGap()));
		panel.setOpaque(false);
		rebuildRadioButtons();
		updateData();
		/*if (getWidget().getAutoSelectFirstRow() && getMultipleValueModel().getSize() > 0) {
				radioButtonArray[0].setSelected(true);
				setSelected(getMultipleValueModel().getElementAt(0));
			}*/

		if ((getWidget().getData() == null || !getWidget().getData().isValid()) && getWidget().getAutoSelectFirstRow()
				&& getMultipleValueModel().getSize() > 0) {
			setSelectedValue(getMultipleValueModel().getElementAt(0));
		}

	}

	@Override
	protected FIBMultipleValueModel<T> createMultipleValueModel() {
		return new FIBMultipleValueModel<T>();
	}

	@Override
	protected void proceedToListModelUpdate() {
		rebuildRadioButtons();
		if (!widgetUpdating && !isDeleted() && getDynamicJComponent() != null) {
			updateWidgetFromModel();
		}
	}

	protected void rebuildRadioButtons() {
		if (panel != null) {
			panel.removeAll();
			((GridLayout) panel.getLayout()).setColumns(getWidget().getColumns());
			((GridLayout) panel.getLayout()).setHgap(getWidget().getHGap());
			((GridLayout) panel.getLayout()).setVgap(getWidget().getVGap());
			buttonGroup = new ButtonGroup();
			radioButtonArray = new JRadioButton[getMultipleValueModel().getSize()];
			for (int i = 0; i < getMultipleValueModel().getSize(); i++) {
				T object = getMultipleValueModel().getElementAt(i);
				JRadioButton rb = new JRadioButton(getStringRepresentation(object), equals(object, selectedValue));
				rb.setOpaque(false);
				rb.addActionListener(new RadioButtonListener(rb, object, i));
				radioButtonArray[i] = rb;
				panel.add(rb);
				buttonGroup.add(rb);
				if (object.equals(getValue())) {
					rb.doClick();
				}
			}
			updateFont();
			panel.revalidate();
		}
	}

	@Override
	public synchronized boolean updateWidgetFromModel() {
		T value = getValue();
		if (notEquals(value, selectedValue) /*|| listModelRequireChange()*/|| multipleValueModel == null) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("updateWidgetFromModel()");
			}

			widgetUpdating = true;
			selectedValue = value;
			setSelected(value);
			// TODO: handle selected index
			rebuildRadioButtons();

			/*if (selectedValue == null) {
				if (getWidget().getAutoSelectFirstRow() && getMultipleValueModel().getSize() > 0) {
					radioButtonArray[0].doClick();
				}
				setSelected(getMultipleValueModel().getElementAt(0));
				selectedValue = getMultipleValueModel().getElementAt(0);
				setValue(selectedValue);
			}*/

			widgetUpdating = false;
			return true;
		}
		return false;
	}

	private class RadioButtonListener implements ActionListener {

		private final T value;
		private final JRadioButton button;
		private final int index;

		public RadioButtonListener(JRadioButton button, T value, int index) {
			this.button = button;
			this.value = value;
			this.index = index;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == button && button.isSelected()) {
				selectedValue = value;
				updateModelFromWidget();
				setSelected(value);
				setSelectedIndex(index);
			}
		}

	}

	/**
	 * Update the model given the actual state of the widget
	 */
	@Override
	public synchronized boolean updateModelFromWidget() {
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
	public JPanel getJComponent() {
		return panel;
	}

	@Override
	public JPanel getDynamicJComponent() {
		return panel;
	}

	@Override
	public void updateFont() {
		super.updateFont();
		if (getFont() != null) {
			for (JRadioButton rb : radioButtonArray) {
				rb.setFont(getFont());
			}
		}
	}

	public T getSelectedValue() {
		return selectedValue;
	}

	public void setSelectedValue(T selectedValue) {
		if (selectedValue != null) {
			int newSelectedIndex = getMultipleValueModel().indexOf(selectedValue);
			if (newSelectedIndex >= 0 && newSelectedIndex < getMultipleValueModel().getSize()) {
				radioButtonArray[newSelectedIndex].doClick();
			}
		}
	}

}
