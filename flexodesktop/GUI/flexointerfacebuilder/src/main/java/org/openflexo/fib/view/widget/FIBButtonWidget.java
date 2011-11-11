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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.JButton;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBButton;
import org.openflexo.fib.view.FIBWidgetView;

public class FIBButtonWidget extends FIBWidgetView<FIBButton, JButton, String> {

	private static final Logger logger = Logger.getLogger(FIBButtonWidget.class.getPackage().getName());

	private final JButton buttonWidget;

	public FIBButtonWidget(FIBButton model, FIBController controller) {
		super(model, controller);
		buttonWidget = new JButton();
		buttonWidget.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				buttonClicked();
			}
		});
		updateLabel();
		// updatePreferredSize();
		updateFont();
	}

	@Override
	public synchronized boolean updateWidgetFromModel() {
		if (modelUpdating)
			return false;
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
		// not relevant
		return false;
	}

	public synchronized void buttonClicked() {
		logger.info("Button " + getWidget() + " has clicked");
		if (getWidget().getAction().isValid()) {
			getWidget().getAction().execute(getController());
		}
		updateDependancies();
	}

	@Override
	public JButton getJComponent() {
		return buttonWidget;
	}

	@Override
	public JButton getDynamicJComponent() {
		return buttonWidget;
	}

	protected void updateLabel() {
		// logger.info("Button update label with key="+getWidget().getLabel());
		buttonWidget.setText(getValue() != null ? getValue() : (getWidget().getLocalize() ? getLocalized(getWidget().getLabel())
				: getWidget().getLabel()));
	}

	@Override
	public void updateLanguage() {
		super.updateLanguage();
		updateLabel();
	}

}
