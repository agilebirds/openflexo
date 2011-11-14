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
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Logger;

import org.openflexo.antar.binding.AbstractBinding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.antar.binding.TypeUtils;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBAttributeNotification;
import org.openflexo.fib.model.FIBTableAction;
import org.openflexo.fib.model.FIBTableAction.FIBAddAction;
import org.openflexo.fib.model.FIBTableAction.FIBCustomAction;
import org.openflexo.fib.model.FIBTableAction.FIBRemoveAction;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class FIBTableActionListener implements ActionListener, BindingEvaluationContext, Observer {

	private static final Logger logger = Logger.getLogger(FIBTableActionListener.class.getPackage().getName());

	private FIBTableAction tableAction;

	private Object model;

	private FIBTableModel tableModel;
	private FIBController controller;

	public FIBTableActionListener(FIBTableAction tableAction, FIBTableModel tableModel, FIBController controller) {
		super();
		this.controller = controller;
		this.tableAction = tableAction;
		selectedObject = null;
		this.tableModel = tableModel;
		tableAction.addObserver(this);
	}

	public void delete() {
		tableAction.deleteObserver(this);
		this.controller = null;
		this.tableAction = null;
		selectedObject = null;
		this.tableModel = null;
	}

	@Override
	public void update(Observable o, Object arg) {
		if (arg instanceof FIBAttributeNotification && o == tableAction) {
			FIBAttributeNotification dataModification = (FIBAttributeNotification) arg;
			if (dataModification.getAttribute() == FIBTableAction.Parameters.method
					|| dataModification.getAttribute() == FIBTableAction.Parameters.isAvailable) {
				tableModel.getWidget().updateTable();
			}
		}
	}

	public FIBController getController() {
		return controller;
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
		return isCustomAction() && ((FIBCustomAction) tableAction).isStatic;
	}

	public boolean isActive(Object selectedObject) {
		if (isRemoveAction() && selectedObject == null) {
			return false;
		}
		if (tableAction.getIsAvailable() != null && tableAction.getIsAvailable().isValid()) {
			this.selectedObject = selectedObject;
			Object returned = tableAction.getIsAvailable().getBindingValue(this);
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
		if (tableAction.getMethod() != null && tableAction.getMethod().isValid()) {
			logger.info("Perform action " + tableAction.getName() + " method " + tableAction.getMethod());
			// logger.info("controller="+getController()+" of "+getController().getClass().getSimpleName());
			this.selectedObject = selectedObject;
			Object newObject = tableAction.getMethod().getBindingValue(this);
			tableModel.fireTableDataChanged();
			tableModel.getTableWidget().updateWidgetFromModel();
			tableModel.getTableWidget().setSelectedObject(newObject);

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
