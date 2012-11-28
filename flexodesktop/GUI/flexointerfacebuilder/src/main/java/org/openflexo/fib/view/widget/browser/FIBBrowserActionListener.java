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
package org.openflexo.fib.view.widget.browser;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import org.openflexo.antar.binding.AbstractBinding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.antar.binding.TypeUtils;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBAttributeNotification;
import org.openflexo.fib.model.FIBBrowserAction;
import org.openflexo.fib.model.FIBBrowserAction.FIBAddAction;
import org.openflexo.fib.model.FIBBrowserAction.FIBCustomAction;
import org.openflexo.fib.model.FIBBrowserAction.FIBRemoveAction;
import org.openflexo.fib.model.FIBTableAction;
import org.openflexo.fib.view.widget.FIBBrowserWidget;

public class FIBBrowserActionListener implements ActionListener, BindingEvaluationContext, Observer {

	private static final Logger logger = Logger.getLogger(FIBBrowserActionListener.class.getPackage().getName());

	private FIBBrowserAction browserAction;

	private Object model;

	private final FIBBrowserWidget widget;

	public FIBBrowserActionListener(FIBBrowserWidget widget, FIBBrowserAction browserAction) {
		super();
		this.widget = widget;
		this.browserAction = browserAction;
		selectedObject = null;
		browserAction.addObserver(this);
	}

	public void delete() {
		browserAction.deleteObserver(this);
		this.browserAction = null;
		selectedObject = null;
	}

	@Override
	public void update(Observable o, Object arg) {
		if (arg instanceof FIBAttributeNotification && o == browserAction) {
			FIBAttributeNotification dataModification = (FIBAttributeNotification) arg;
			if (dataModification.getAttribute() == FIBTableAction.Parameters.method
					|| dataModification.getAttribute() == FIBTableAction.Parameters.isAvailable) {
				widget.updateBrowser();
			}
		}
	}

	public FIBController getController() {
		return widget.getController();
	}

	public boolean isAddAction() {
		return browserAction instanceof FIBAddAction;
	}

	public boolean isRemoveAction() {
		return browserAction instanceof FIBRemoveAction;
	}

	public boolean isCustomAction() {
		return browserAction instanceof FIBCustomAction;
	}

	public boolean isStatic() {
		return isCustomAction() && ((FIBCustomAction) browserAction).isStatic;
	}

	public boolean isActive(Object selectedObject) {
		if (isRemoveAction() && selectedObject == null) {
			return false;
		}
		if (browserAction.getIsAvailable() != null && browserAction.getIsAvailable().isValid()) {
			this.selectedObject = selectedObject;
			Object returned = browserAction.getIsAvailable().getBindingValue(this);
			if (returned == null) {
				return false;
			}
			if (TypeUtils.isBoolean(returned.getClass())) {
				return (Boolean) returned;
			}
		}
		return true;
	}

	protected void performAction(Object selectedObject) {
		if (browserAction.getMethod() != null && browserAction.getMethod().isValid()) {
			logger.fine("Perform action " + browserAction.getName() + " method " + browserAction.getMethod());
			logger.fine("controller=" + getController() + " of " + getController().getClass().getSimpleName());
			this.selectedObject = selectedObject;
			final Object newObject = browserAction.performAction(this, selectedObject);
			// browserModel.fireTableDataChanged();
			// browserModel.getBrowserWidget().updateWidgetFromModel();
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					widget.setSelectedObject(newObject);
				}
			});
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		performAction(getSelectedObject());
	}

	public Object getSelectedObject() {
		return selectedObject;
	}

	public void setSelectedObject(Object selectedObject) {
		this.selectedObject = selectedObject;
	}

	public Object getModel() {
		return model;
	}

	public void setModel(Object model) {
		this.model = model;
	}

	protected Object selectedObject;

	@Override
	public Object getValue(BindingVariable variable) {
		if (variable.getVariableName().equals("selected")) {
			return selectedObject;
		} else {
			return getController().getValue(variable);
		}
	}

}
