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
import java.lang.reflect.InvocationTargetException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.JButton;

import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBButton;
import org.openflexo.fib.model.FIBComponent;
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
				// updateDependancies();
			}
		});
		updateLabel();
		updateIcon();
		// updatePreferredSize();
		updateFont();
	}

	@Override
	public synchronized boolean updateWidgetFromModel() {
		if (modelUpdating) {
			return false;
		}
		widgetUpdating = true;
		updateLabel();
		updateIcon();
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
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Button " + getWidget() + " has clicked");
			logger.fine("Action: " + getWidget().getAction() + " valid=" + getWidget().getAction().isValid());
			logger.fine("Data: " + getController().getDataObject());
		}
		Object data = getController().getDataObject();
		if (getWidget().getAction().isValid()) {
			try {
				getWidget().getAction().execute(getController());
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		updateDependancies(new Vector<FIBComponent>());
		updateWidgetFromModel();
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
		buttonWidget.setText(getValue() != null ? getWidget().getLocalize() ? getLocalized(getValue()) : getValue() : getWidget()
				.getLocalize() ? getLocalized(getWidget().getLabel()) : getWidget().getLabel());
	}

	protected void updateIcon() {
		// logger.info("Button update label with key="+getWidget().getLabel());
		if (getWidget().getButtonIcon() != null && getWidget().getButtonIcon().isSet() && getWidget().getButtonIcon().isValid()) {
			Icon icon;
			try {
				icon = getWidget().getButtonIcon().getBindingValue(getController());
				buttonWidget.setIcon(icon);
			} catch (TypeMismatchException e) {
				e.printStackTrace();
				buttonWidget.setIcon(null);
			} catch (NullReferenceException e) {
				e.printStackTrace();
				buttonWidget.setIcon(null);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				buttonWidget.setIcon(null);
			}
		} else {
			buttonWidget.setIcon(null);
		}
	}

	@Override
	public void updateLanguage() {
		super.updateLanguage();
		updateLabel();
	}

}
