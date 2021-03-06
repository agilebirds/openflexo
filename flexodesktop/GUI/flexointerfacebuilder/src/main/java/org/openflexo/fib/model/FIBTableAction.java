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
package org.openflexo.fib.model;

import java.util.List;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingDefinition.BindingDefinitionType;
import org.openflexo.antar.binding.BindingModel;

public abstract class FIBTableAction extends FIBModelObject {

	private static final Logger logger = Logger.getLogger(FIBTableAction.class.getPackage().getName());

	private FIBTable table;

	public static enum Parameters implements FIBModelAttribute {
		method, isAvailable
	}

	public static enum ActionType {
		Add, Delete, Custom
	}

	private DataBinding method;
	private DataBinding isAvailable;

	public static BindingDefinition METHOD = new BindingDefinition("method", Object.class, BindingDefinitionType.EXECUTE, false);
	public static BindingDefinition IS_AVAILABLE = new BindingDefinition("isAvailable", Boolean.class, BindingDefinitionType.GET, false);

	public FIBTable getTable() {
		return table;
	}

	public void setTable(FIBTable table) {
		this.table = table;
	}

	@Override
	public FIBComponent getRootComponent() {
		if (getTable() != null) {
			return getTable().getRootComponent();
		}
		return null;
	}

	public DataBinding getMethod() {
		if (method == null) {
			method = new DataBinding(this, Parameters.method, METHOD);
		}
		return method;
	}

	public void setMethod(DataBinding method) {
		method.setOwner(this);
		method.setBindingAttribute(Parameters.method);
		method.setBindingDefinition(METHOD);
		this.method = method;
	}

	public DataBinding getIsAvailable() {
		if (isAvailable == null) {
			isAvailable = new DataBinding(this, Parameters.isAvailable, IS_AVAILABLE);
		}
		return isAvailable;
	}

	public void setIsAvailable(DataBinding isAvailable) {
		isAvailable.setOwner(this);
		isAvailable.setBindingAttribute(Parameters.isAvailable);
		isAvailable.setBindingDefinition(IS_AVAILABLE);
		this.isAvailable = isAvailable;
	}

	@Override
	public BindingModel getBindingModel() {
		if (getTable() != null) {
			return getTable().getActionBindingModel();
		}
		return null;
	}

	@Override
	public void finalizeDeserialization() {
		logger.fine("finalizeDeserialization() for FIBTableAction " + getName());
		super.finalizeDeserialization();
		if (method != null) {
			method.finalizeDeserialization();
		}
	}

	@Override
	public List<? extends FIBModelObject> getEmbeddedObjects() {
		return null;
	}

	public abstract ActionType getActionType();

	public static class FIBAddAction extends FIBTableAction {

		@Override
		public ActionType getActionType() {
			return ActionType.Add;
		}
	}

	public static class FIBRemoveAction extends FIBTableAction {

		@Override
		public ActionType getActionType() {
			return ActionType.Delete;
		}
	}

	public static class FIBCustomAction extends FIBTableAction {

		public boolean isStatic = false;

		@Override
		public ActionType getActionType() {
			return ActionType.Custom;
		}
	}

}
