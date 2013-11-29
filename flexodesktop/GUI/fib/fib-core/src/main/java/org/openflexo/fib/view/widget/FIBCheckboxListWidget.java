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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBCheckboxList;

public class FIBCheckboxListWidget<T> extends FIBMultipleValueWidget<FIBCheckboxList, JPanel, List<T>, T> {

	static final Logger logger = Logger.getLogger(FIBCheckboxListWidget.class.getPackage().getName());

	private JCheckBox[] checkboxesArray;
	private JLabel[] labelsArray;

	private JPanel panel;

	private List<T> selectedValues;

	public FIBCheckboxListWidget(FIBCheckboxList model, FIBController controller) {
		super(model, controller);
		panel = new JPanel(new GridLayout(0, model.getColumns(), model.getHGap(), model.getVGap()));
		panel.setOpaque(false);
		selectedValues = new ArrayList<T>();
		rebuildCheckboxes();
		if (getWidget().getAutoSelectFirstRow() && getMultipleValueModel().getSize() > 0) {
			checkboxesArray[0].setSelected(true);
			List<T> newList = new ArrayList<T>();
			newList.add((T) getMultipleValueModel().getElementAt(0));
			setSelected(newList);
		}
	}

	private boolean containsObject(Object object) {
		if (selectedValues == null) {
			return false;
		} else {
			return selectedValues.contains(object);
		}
	}

	@Override
	protected FIBMultipleValueModel<T> createMultipleValueModel() {
		return new FIBMultipleValueModel<T>();
	}

	@Override
	protected void proceedToListModelUpdate() {
		rebuildCheckboxes();
	}

	private void rebuildCheckboxes() {
		panel.removeAll();
		((GridLayout) panel.getLayout()).setColumns(getWidget().getColumns());
		((GridLayout) panel.getLayout()).setHgap(getWidget().getHGap());
		((GridLayout) panel.getLayout()).setVgap(getWidget().getVGap());
		checkboxesArray = new JCheckBox[getMultipleValueModel().getSize()];
		labelsArray = new JLabel[getMultipleValueModel().getSize()];
		for (int i = 0; i < getMultipleValueModel().getSize(); i++) {
			T object = (T) getMultipleValueModel().getElementAt(i);
			String text = getStringRepresentation(object);
			JCheckBox cb = new JCheckBox(text, containsObject(object));
			cb.setOpaque(false);
			cb.addActionListener(new CheckboxListener(cb, object, i));
			checkboxesArray[i] = cb;
			if (getWidget().getShowIcon() && getWidget().getIcon().isSet() && getWidget().getIcon().isValid()) {
				cb.setHorizontalAlignment(JCheckBox.LEFT);
				cb.setText(null);
				final JLabel label = new JLabel(text, getIconRepresentation(object), JLabel.LEADING);
				Dimension ps = cb.getPreferredSize();
				cb.setLayout(new BorderLayout());
				label.setLabelFor(cb);
				label.setBorder(BorderFactory.createEmptyBorder(0, ps.width, 0, 0));
				cb.add(label);
			}
			panel.add(cb);
		}
		updateFont();
		panel.revalidate();
	}

	@Override
	public synchronized boolean updateWidgetFromModel() {
		List value = getValue();
		if (notEquals(value, selectedValues) || listModelRequireChange() || multipleValueModel == null) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("updateWidgetFromModel()");
			}

			widgetUpdating = true;
			selectedValues = value;
			rebuildCheckboxes();

			widgetUpdating = false;
			return true;
		}
		return false;
	}

	private class CheckboxListener implements ActionListener {

		private final T value;
		private final JCheckBox cb;
		private final int index;

		public CheckboxListener(JCheckBox cb, T value, int index) {
			this.cb = cb;
			this.value = value;
			this.index = index;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == cb) {
				if (cb.isSelected()) {
					if (!containsObject(value)) {
						if (selectedValues == null) {
							selectedValues = new ArrayList<T>();
						}
						selectedValues.add(value);
					}
				} else {
					if (containsObject(value)) {
						selectedValues.remove(value);
					}
				}
				updateModelFromWidget();
				setSelected(selectedValues);
				setSelectedIndex(index);
			}
		}

	}

	/**
	 * Update the model given the actual state of the widget
	 */
	@Override
	public synchronized boolean updateModelFromWidget() {
		if (notEquals(getValue(), selectedValues)) {
			modelUpdating = true;
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("updateModelFromWidget with " + selectedValues);
			}
			if (!widgetUpdating) {
				setValue(selectedValues);
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
			for (JCheckBox cb : checkboxesArray) {
				cb.setFont(getFont());
			}
		}
		if (getWidget().getShowIcon() && getWidget().getIcon().isSet() && getWidget().getIcon().isValid()) {
			for (JLabel l : labelsArray) {
				if (l != null) {
					l.setFont(getFont());
				}
			}
		}
	}

}
