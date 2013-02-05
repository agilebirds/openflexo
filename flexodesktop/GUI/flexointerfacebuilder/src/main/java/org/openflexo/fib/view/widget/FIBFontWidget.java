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

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBFont;
import org.openflexo.fib.view.FIBWidgetView;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.swing.FontSelector;

public class FIBFontWidget extends FIBWidgetView<FIBFont, FontSelector, Font> {

	private static final Logger logger = Logger.getLogger(FIBFontWidget.class.getPackage().getName());

	protected FontSelector _selector;
	private JCheckBox checkBox;

	private JPanel container;

	public FIBFontWidget(FIBFont model, FIBController controller) {
		super(model, controller);
		_selector = new FontSelector();
		if (isReadOnly()) {
			_selector.getDownButton().setEnabled(false);
		}
		_selector.addFocusListener(this);
		checkBox = new JCheckBox();
		checkBox.setToolTipText(FlexoLocalization.localizedForKey("undefined_value", checkBox));
		checkBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				_selector.setEnabled(!checkBox.isSelected());
				updateModelFromWidget();
			}
		});
		container = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		container.add(_selector, gbc);
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;
		gbc.anchor = GridBagConstraints.LINE_START;
		container.add(checkBox, gbc);
		updateCheckboxVisibility();
		updateFont();
	}

	public void updateCheckboxVisibility() {
		checkBox.setVisible(getWidget().getAllowsNull());
	}

	@Override
	public synchronized boolean updateWidgetFromModel() {
		// if (notEquals(getValue(),getSelectedFont())) {
		widgetUpdating = true;
		checkBox.setSelected(getValue() == null);
		_selector.setEnabled((getValue() != null || !getWidget().getAllowsNull()) && isEnabled());
		setFont(getValue());
		widgetUpdating = false;
		return true;
		// }
		// return false;
	}

	/**
	 * Update the model given the actual state of the widget
	 */
	@Override
	public synchronized boolean updateModelFromWidget() {
		// if (notEquals(getValue(), getSelectedFont())) {
		if (isReadOnly()) {
			return false;
		}

		Font editedObject = null;
		if (!checkBox.isSelected()) {
			editedObject = _selector.getEditedObject();
		}
		setValue(editedObject);
		return true;
		// }
		// return false;
	}

	@Override
	public JComponent getJComponent() {
		return container;
	}

	@Override
	public FontSelector getDynamicJComponent() {
		return _selector;
	}

	protected void setFont(Font aFont) {
		_selector.setEditedObject(aFont);
	}

}
