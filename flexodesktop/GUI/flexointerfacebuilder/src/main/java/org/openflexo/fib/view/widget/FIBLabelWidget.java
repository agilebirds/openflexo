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

import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBLabel;
import org.openflexo.fib.view.FIBWidgetView;

public class FIBLabelWidget extends FIBWidgetView<FIBLabel, JLabel, String> {
	private static final Logger logger = Logger.getLogger(FIBLabelWidget.class.getPackage().getName());

	private JLabel labelWidget;

	public FIBLabelWidget(FIBLabel model, FIBController controller) {
		super(model, controller);
		if (model.getData().isValid()) {
			labelWidget = new JLabel(" ");
		} else {
			labelWidget = new JLabel();
		}
		labelWidget.setFocusable(false); // There is not much point in giving focus to a label since there is no KeyBindings nor KeyListener
											// on it.
		labelWidget.setBorder(BorderFactory.createEmptyBorder(TOP_COMPENSATING_BORDER, TOP_COMPENSATING_BORDER, BOTTOM_COMPENSATING_BORDER,
				RIGHT_COMPENSATING_BORDER));
		updateFont();
		updateAlign();
		updateLabel();
	}

	@Override
	public synchronized boolean updateWidgetFromModel() {
		if (modelUpdating) {
			return false;
		}
		widgetUpdating = true;
		updateLabel();
		widgetUpdating = false;
		return false;
	}

	/**
	 * Update the model given the actual state of the widget
	 */
	@Override
	public synchronized boolean updateModelFromWidget() {
		// Read only component
		return false;
	}

	@Override
	public JLabel getJComponent() {
		return labelWidget;
	}

	@Override
	public JLabel getDynamicJComponent() {
		return labelWidget;
	}

	protected void updateAlign() {
		labelWidget.setHorizontalAlignment(getWidget().getAlign().getAlign());
	}

	protected void updateLabel() {
		if (getWidget().getData().isValid()) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					if (getWidget() != null) {
						labelWidget.setText(getWidget().getLocalize() ? getLocalized(getValue()) : getValue());
					}
				}
			});
		} else {
			labelWidget.setText(getWidget().getLocalize() ? getLocalized(getWidget().getLabel()) : getWidget().getLabel());
		}
	}

	@Override
	public void updateLanguage() {
		super.updateLanguage();
		updateLabel();
	}
}
