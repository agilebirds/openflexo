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

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;

public abstract class FIBBrowserAction extends FIBModelObject {

	private static final Logger logger = Logger.getLogger(FIBBrowserAction.class.getPackage().getName());

	private FIBBrowserElement element;

	public static enum Parameters implements FIBModelAttribute {
		method, isAvailable
	}

	public static enum ActionType {
		Add, Delete, Custom
	}

	private DataBinding<Object> method;
	private DataBinding<Boolean> isAvailable;

	@Deprecated
	public static BindingDefinition METHOD = new BindingDefinition("method", Object.class, DataBinding.BindingDefinitionType.EXECUTE, false);
	@Deprecated
	public static BindingDefinition IS_AVAILABLE = new BindingDefinition("isAvailable", Boolean.class,
			DataBinding.BindingDefinitionType.EXECUTE, false);

	public FIBBrowserElement getBrowserElement() {
		return element;
	}

	public void setBrowserElement(FIBBrowserElement element) {
		this.element = element;
	}

	@Override
	public FIBComponent getRootComponent() {
		if (getBrowserElement() != null) {
			return getBrowserElement().getRootComponent();
		}
		return null;
	}

	public DataBinding<Object> getMethod() {
		if (method == null) {
			method = new DataBinding<Object>(this, Object.class, DataBinding.BindingDefinitionType.EXECUTE);
		}
		return method;
	}

	public void setMethod(DataBinding<Object> method) {
		if (method != null) {
			method.setOwner(this);
			method.setDeclaredType(Object.class);
			method.setBindingDefinitionType(DataBinding.BindingDefinitionType.EXECUTE);
		}
		this.method = method;
	}

	public DataBinding<Boolean> getIsAvailable() {
		if (isAvailable == null) {
			isAvailable = new DataBinding<Boolean>(this, Boolean.class, DataBinding.BindingDefinitionType.GET);
		}
		return isAvailable;
	}

	public void setIsAvailable(DataBinding<Boolean> isAvailable) {
		if (isAvailable != null) {
			isAvailable.setOwner(this);
			isAvailable.setDeclaredType(Boolean.class);
			isAvailable.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
		}
		this.isAvailable = isAvailable;
	}

	@Override
	public BindingModel getBindingModel() {
		if (getBrowserElement() != null) {
			return getBrowserElement().getActionBindingModel();
		}
		return null;
	}

	@Override
	public void finalizeDeserialization() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("finalizeDeserialization() for FIBTableAction " + getName());
		}
		super.finalizeDeserialization();
		if (method != null) {
			method.decode();
		}
		if (isAvailable != null) {
			isAvailable.decode();
		}
	}

	@Override
	public List<? extends FIBModelObject> getEmbeddedObjects() {
		return null;
	}

	public abstract ActionType getActionType();

	public static class FIBAddAction extends FIBBrowserAction {

		@Override
		public ActionType getActionType() {
			return ActionType.Add;
		}
	}

	public static class FIBRemoveAction extends FIBBrowserAction {

		@Override
		public ActionType getActionType() {
			return ActionType.Delete;
		}
	}

	public static class FIBCustomAction extends FIBBrowserAction {

		public boolean isStatic = false;

		@Override
		public ActionType getActionType() {
			return ActionType.Custom;
		}
	}

	public Object performAction(BindingEvaluationContext context, Object selectedObject) {
		if (getMethod() != null && getMethod().isSet()) {
			try {
				return getMethod().getBindingValue(context);
			} catch (TypeMismatchException e) {
				e.printStackTrace();
				return null;
			} catch (NullReferenceException e) {
				e.printStackTrace();
				return null;
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				return null;
			}
		} else {
			return null;
		}
	}

}
