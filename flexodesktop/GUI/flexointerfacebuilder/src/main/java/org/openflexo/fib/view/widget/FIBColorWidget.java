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

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBColor;
import org.openflexo.fib.model.FIBModelObject;
import org.openflexo.fib.view.FIBWidgetView;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.swing.ColorSelector;
import org.openflexo.swing.CustomPopup.ApplyCancelListener;

public class FIBColorWidget extends FIBWidgetView<FIBColor, ColorSelector, Color> implements ApplyCancelListener {

	private static final Logger logger = Logger.getLogger(FIBColorWidget.class.getPackage().getName());

	// public static final Icon ARROW_DOWN = new ImageIconResource("Resources/ArrowDown.gif");

	private JCheckBox checkBox;

	protected ColorSelector _selector;

	private JPanel container;

	public FIBColorWidget(FIBColor model, FIBController controller) {
		super(model, controller);

		_selector = new ColorSelector();
		if (isReadOnly()) {
			_selector.getDownButton().setEnabled(false);
		} else {
			_selector.addApplyCancelListener(this);
		}
		_selector.addFocusListener(this);
		_selector.setEnabled(false);
		checkBox = new JCheckBox();
		checkBox.setHorizontalTextPosition(JCheckBox.LEADING);
		updateCheckboxLabel();
		checkBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				_selector.setEnabled(checkBox.isSelected());
				updateModelFromWidget();
			}
		});
		container = new JPanel(new GridBagLayout());
		container.setOpaque(false);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;
		gbc.anchor = GridBagConstraints.LINE_START;
		container.add(checkBox, gbc);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		container.add(_selector, gbc);
		updateCheckboxVisibility();
		updateFont();
	}

	private void updateCheckboxLabel() {
		checkBox.setText(FlexoLocalization.localizedForKey(FIBModelObject.LOCALIZATION, "transparent", checkBox));
		checkBox.setToolTipText(FlexoLocalization.localizedTooltipForKey(FIBModelObject.LOCALIZATION, "undefined_value", checkBox));
	}

	public void updateCheckboxVisibility() {
		checkBox.setVisible(getWidget().getAllowsNull());
	}

	@Override
	public void updateLanguage() {
		super.updateLanguage();
		updateCheckboxLabel();
	}

	@Override
	public void fireApplyPerformed() {
		updateModelFromWidget();
	}

	@Override
	public void fireCancelPerformed() {
		// Nothing to do, widget resets itself automatically and model has not been changed.
	}

	@Override
	public synchronized boolean updateWidgetFromModel() {
		Color editedObject = _selector.getEditedObject();
		if (!checkBox.isSelected()) {
			editedObject = null;
		}
		if (notEquals(getValue(), editedObject)) {
			widgetUpdating = true;
			try {
				checkBox.setSelected(getValue() == null);
				_selector.setEnabled((getValue() != null || !getWidget().getAllowsNull()) && isEnabled());
				setColor(getValue());
			} finally {
				widgetUpdating = false;
			}
			return true;
		}
		return false;
	}

	/**
	 * Update the model given the actual state of the widget
	 */
	@Override
	public synchronized boolean updateModelFromWidget() {
		Color editedObject = null;
		if (checkBox.isSelected()) {
			editedObject = _selector.getEditedObject();
		}
		if (notEquals(getValue(), editedObject)) {
			if (isReadOnly()) {
				return false;
			}
			modelUpdating = true;
			try {
				setValue(editedObject);
			} finally {
				modelUpdating = false;
			}
			return true;
		}
		return false;
	}

	@Override
	public JComponent getJComponent() {
		return container;
	}

	@Override
	public ColorSelector getDynamicJComponent() {
		return _selector;
	}

	protected void setColor(Color aColor) {
		_selector.setEditedObject(aColor);
	}

}
