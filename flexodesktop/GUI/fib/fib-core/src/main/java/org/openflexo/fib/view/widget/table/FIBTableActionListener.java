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
package org.openflexo.fib.view.widget.table;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.antar.binding.TypeUtils;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBTableAction;
import org.openflexo.fib.model.FIBTableAction.FIBAddAction;
import org.openflexo.fib.model.FIBTableAction.FIBCustomAction;
import org.openflexo.fib.model.FIBTableAction.FIBRemoveAction;
import org.openflexo.fib.view.widget.FIBTableWidget;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class FIBTableActionListener<T> implements ActionListener, BindingEvaluationContext, PropertyChangeListener {

	private static final Logger logger = Logger.getLogger(FIBTableActionListener.class.getPackage().getName());

	private FIBTableAction tableAction;
	private Object model;
	private FIBTableWidget<T> tableWidget;
	protected T selectedObject;

	public FIBTableActionListener(FIBTableAction tableAction, FIBTableWidget<T> tableWidget) {
		super();
		this.tableWidget = tableWidget;
		this.tableAction = tableAction;
		selectedObject = null;
		tableAction.getPropertyChangeSupport().addPropertyChangeListener(this);
	}

	public void delete() {
		// NPE Protection
		if (tableAction != null) {
			tableAction.getPropertyChangeSupport().removePropertyChangeListener(this);
		}
		this.tableAction = null;
		this.tableWidget = null;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() == tableAction) {
			if ((evt.getPropertyName().equals(FIBTableAction.METHOD_KEY))
					|| (evt.getPropertyName().equals(FIBTableAction.IS_AVAILABLE_KEY))) {
				tableWidget.updateTable();
			}
		}
	}

	public FIBController getController() {
		return tableWidget.getController();
	}

	public boolean isAddAction() {
		return tableAction instanceof FIBAddAction;
	}

	public boolean isRemoveAction() {
		return tableAction instanceof FIBRemoveAction;
	}

	public boolean isCustomAction() {
		return tableAction instanceof FIBCustomAction;
	}

	public boolean isStatic() {
		return isCustomAction() && ((FIBCustomAction) tableAction).isStatic();
	}

	public boolean isActive(T selectedObject) {
		if (isRemoveAction() && selectedObject == null) {
			return false;
		}
		if (tableAction.getIsAvailable() != null && tableAction.getIsAvailable().isValid()) {
			this.selectedObject = selectedObject;
			Object returned = null;
			try {
				returned = tableAction.getIsAvailable().getBindingValue(this);
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				// e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			if (returned == null) {
				return false;
			}
			if (TypeUtils.isBoolean(returned.getClass())) {
				return (Boolean) returned;
			}
		}
		return true;
	}

	protected void performAction(T selectedObject) {
		if (tableAction.getMethod() != null && tableAction.getMethod().isValid()) {
			logger.info("Perform action " + tableAction.getName() + " method " + tableAction.getMethod());
			// logger.info("controller="+getController()+" of "+getController().getClass().getSimpleName());
			this.selectedObject = selectedObject;
			T newObject = null;
			try {
				newObject = (T) tableAction.getMethod().getBindingValue(this);
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			tableWidget.updateWidgetFromModel();
			tableWidget.performSelect(newObject);

			/*if (newObject != null) {
				int index = tableModel.getTableWidget().getValue().indexOf(newObject);
				if (index > -1) {
					tableModel.getTableWidget().getListSelectionModel().setSelectionInterval(index,index);
				}
			}*/
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		performAction(getSelectedObject());
	}

	public T getSelectedObject() {
		return selectedObject;
	}

	public void setSelectedObject(T selectedObject) {
		this.selectedObject = selectedObject;
	}

	public Object getModel() {
		return model;
	}

	public void setModel(Object model) {
		this.model = model;
	}

	@Override
	public Object getValue(BindingVariable variable) {
		if (variable.getVariableName().equals("selected")) {
			return selectedObject;
		} else {
			return tableWidget.getBindingEvaluationContext().getValue(variable);
		}
	}

}
