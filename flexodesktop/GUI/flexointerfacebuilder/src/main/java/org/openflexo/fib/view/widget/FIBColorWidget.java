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
import java.util.logging.Logger;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBColor;
import org.openflexo.fib.view.FIBWidgetView;
import org.openflexo.swing.ColorSelector;
import org.openflexo.swing.CustomPopup.ApplyCancelListener;

public class FIBColorWidget extends FIBWidgetView<FIBColor, ColorSelector, Color> implements ApplyCancelListener {

	private static final Logger logger = Logger.getLogger(FIBColorWidget.class.getPackage().getName());

	// public static final Icon ARROW_DOWN = new ImageIconResource("Resources/ArrowDown.gif");

	protected ColorSelector _selector;

	public FIBColorWidget(FIBColor model, FIBController controller) {
		super(model, controller);

		_selector = new ColorSelector();
		if (isReadOnly()) {
			_selector.getDownButton().setEnabled(false);
		} else {
			_selector.addApplyCancelListener(this);
		}
		getJComponent().addFocusListener(this);

		updateFont();
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
		if (notEquals(getValue(), _selector.getEditedObject())) {
			widgetUpdating = true;
			try {
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
		if (notEquals(getValue(), _selector.getEditedObject())) {
			if (isReadOnly()) {
				return false;
			}
			modelUpdating = true;
			try {
				setValue(_selector.getEditedObject());
			} finally {
				modelUpdating = false;
			}
			return true;
		}
		return false;
	}

	@Override
	public ColorSelector getJComponent() {
		return _selector;
	}

	@Override
	public ColorSelector getDynamicJComponent() {
		return _selector;
	}

	protected void setColor(Color aColor) {
		_selector.setEditedObject(aColor);
	}

}
