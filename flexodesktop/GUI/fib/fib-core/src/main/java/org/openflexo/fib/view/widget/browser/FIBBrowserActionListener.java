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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import org.openflexo.antar.binding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.antar.binding.TypeUtils;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBBrowserAction;
import org.openflexo.fib.model.FIBBrowserAction.ActionType;
import org.openflexo.fib.model.FIBBrowserAction.FIBCustomAction;
import org.openflexo.fib.view.widget.FIBBrowserWidget;

public class FIBBrowserActionListener<T> implements ActionListener, BindingEvaluationContext, PropertyChangeListener {

	private static final Logger logger = Logger.getLogger(FIBBrowserActionListener.class.getPackage().getName());

	private FIBBrowserAction browserAction;

	private Object model;

	private final FIBBrowserWidget<T> widget;

	public FIBBrowserActionListener(FIBBrowserWidget<T> widget, FIBBrowserAction browserAction) {
		super();
		this.widget = widget;
		this.browserAction = browserAction;
		selectedObject = null;

		browserAction.getPropertyChangeSupport().addPropertyChangeListener(this);

	}

	public void delete() {
		browserAction.getPropertyChangeSupport().removePropertyChangeListener(this);
		this.browserAction = null;
		selectedObject = null;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() == browserAction) {
			if ((evt.getPropertyName().equals(FIBBrowserAction.METHOD_KEY))
					|| (evt.getPropertyName().equals(FIBBrowserAction.IS_AVAILABLE_KEY))) {
				widget.updateBrowser();
			}
		}
	}

	public FIBController getController() {
		return widget.getController();
	}

	public boolean isAddAction() {
		return browserAction.getActionType() == ActionType.Add;
	}

	public boolean isRemoveAction() {
		return browserAction.getActionType() == ActionType.Delete;
	}

	public boolean isCustomAction() {
		return browserAction.getActionType() == ActionType.Custom;
	}

	public boolean isStatic() {
		return isCustomAction() && ((FIBCustomAction) browserAction).isStatic();
	}

	public boolean isActive(Object selectedObject) {
		if (isRemoveAction() && selectedObject == null) {
			return false;
		}
		if (browserAction.getIsAvailable() != null && browserAction.getIsAvailable().isValid()) {
			this.selectedObject = selectedObject;
			Boolean returned = null;
			try {
				returned = browserAction.getIsAvailable().getBindingValue(this);
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			if (returned == null) {
				return false;
			}
			if (TypeUtils.isBoolean(returned.getClass())) {
				return returned;
			}
		}
		return true;
	}

	protected void performAction(Object selectedObject) {
		if (browserAction.getMethod() != null && browserAction.getMethod().isValid()) {
			logger.fine("Perform action " + browserAction.getName() + " method " + browserAction.getMethod());
			logger.fine("controller=" + getController() + " of " + getController().getClass().getSimpleName());
			this.selectedObject = selectedObject;
			/*logger.info("selectedObject=" + selectedObject);
			logger.info("getMethod=" + getBrowserAction().getMethod());
			logger.info("valid=" + getBrowserAction().getMethod().isValid() + " reason:"
					+ getBrowserAction().getMethod().invalidBindingReason());*/
			try {
				final T newObject = (T) getBrowserAction().getMethod().getBindingValue(this);
				// browserModel.fireTableDataChanged();
				// browserModel.getBrowserWidget().updateWidgetFromModel();
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						widget.setSelected(newObject);
					}
				});
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	public FIBBrowserAction getBrowserAction() {
		return browserAction;
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
		} else if (variable.getVariableName().equals("action")) {
			return browserAction;
		} else {
			return widget.getBindingEvaluationContext().getValue(variable);
		}
	}
}
