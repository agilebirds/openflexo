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
import java.lang.reflect.Type;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.DataBinding.BindingDefinitionType;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.fib.model.validation.ValidationReport;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity(isAbstract = true)
@ImplementationClass(FIBBrowserAction.FIBBrowserActionImpl.class)
public abstract interface FIBBrowserAction extends FIBModelObject {

	public static enum ActionType {
		Add, Delete, Custom
	}

	@PropertyIdentifier(type = FIBBrowserElement.class)
	public static final String OWNER_KEY = "owner";

	@PropertyIdentifier(type = DataBinding.class)
	public static final String METHOD_KEY = "method";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String IS_AVAILABLE_KEY = "isAvailable";

	@Getter(value = OWNER_KEY, inverse = FIBBrowserElement.ACTIONS_KEY)
	public FIBBrowserElement getOwner();

	@Setter(OWNER_KEY)
	public void setOwner(FIBBrowserElement browserElement);

	@Getter(value = METHOD_KEY)
	@XMLAttribute
	public DataBinding<Object> getMethod();

	@Setter(METHOD_KEY)
	public void setMethod(DataBinding<Object> method);

	@Getter(value = IS_AVAILABLE_KEY)
	@XMLAttribute
	public DataBinding<Boolean> getIsAvailable();

	@Setter(IS_AVAILABLE_KEY)
	public void setIsAvailable(DataBinding<Boolean> isAvailable);

	public ActionType getActionType();

	public static abstract class FIBBrowserActionImpl extends FIBModelObjectImpl implements FIBBrowserAction {

		private static final Logger logger = Logger.getLogger(FIBBrowserAction.class.getPackage().getName());

		private DataBinding<Object> method;
		private DataBinding<Boolean> isAvailable;

		private BindingModel actionBindingModel;

		@Deprecated
		public static BindingDefinition METHOD = new BindingDefinition("method", Object.class, BindingDefinitionType.EXECUTE, false);
		@Deprecated
		public static BindingDefinition IS_AVAILABLE = new BindingDefinition("isAvailable", Boolean.class, BindingDefinitionType.EXECUTE,
				false);

		@Override
		public FIBComponent getComponent() {
			if (getOwner() != null) {
				return getOwner().getComponent();
			}
			return null;
		}

		@Override
		public DataBinding<Object> getMethod() {
			if (method == null) {
				method = new DataBinding<Object>(this, Object.class, DataBinding.BindingDefinitionType.EXECUTE);
			}
			return method;
		}

		@Override
		public void setMethod(DataBinding<Object> method) {
			if (method != null) {
				method.setOwner(this);
				method.setDeclaredType(Object.class);
				method.setBindingDefinitionType(DataBinding.BindingDefinitionType.EXECUTE);
			}
			this.method = method;
		}

		@Override
		public DataBinding<Boolean> getIsAvailable() {
			if (isAvailable == null) {
				isAvailable = new DataBinding<Boolean>(this, Boolean.class, DataBinding.BindingDefinitionType.GET);
			}
			return isAvailable;
		}

		@Override
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
			if (getOwner() != null) {
				if (actionBindingModel == null) {
					actionBindingModel = new BindingModel(getOwner().getActionBindingModel());
					actionBindingModel.addToBindingVariables(new BindingVariable("action", Object.class) {
						@Override
						public Type getType() {
							return FIBBrowserActionImpl.this.getClass();
						}
					});
				}
				return actionBindingModel;
			}
			return null;
		}

		public void finalizeDeserialization() {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("finalizeDeserialization() for FIBTableAction " + getName());
			}
			if (method != null) {
				method.decode();
			}
			if (isAvailable != null) {
				isAvailable.decode();
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

		@Override
		protected void applyValidation(ValidationReport report) {
			super.applyValidation(report);
			performValidation(MethodBindingMustBeValid.class, report);
			performValidation(IsAvailableBindingMustBeValid.class, report);
		}

	}

	@ModelEntity
	@ImplementationClass(FIBAddAction.FIBAddActionImpl.class)
	@XMLElement(xmlTag = "BrowserAddAction")
	public static interface FIBAddAction extends FIBBrowserAction {

		public static abstract class FIBAddActionImpl extends FIBBrowserActionImpl implements FIBAddAction {

			@Override
			public ActionType getActionType() {
				return ActionType.Add;
			}
		}
	}

	@ModelEntity
	@ImplementationClass(FIBRemoveAction.FIBRemoveActionImpl.class)
	@XMLElement(xmlTag = "BrowserRemoveAction")
	public static interface FIBRemoveAction extends FIBBrowserAction {

		public static abstract class FIBRemoveActionImpl extends FIBBrowserActionImpl implements FIBRemoveAction {

			@Override
			public ActionType getActionType() {
				return ActionType.Delete;
			}
		}

	}

	@ModelEntity
	@ImplementationClass(FIBCustomAction.FIBCustomActionImpl.class)
	@XMLElement(xmlTag = "BrowserCustomAction")
	public static interface FIBCustomAction extends FIBBrowserAction {

		@PropertyIdentifier(type = boolean.class)
		public static final String IS_STATIC_KEY = "isStatic";

		@Getter(value = IS_STATIC_KEY, defaultValue = "false")
		@XMLAttribute
		public boolean isStatic();

		@Setter(IS_STATIC_KEY)
		public void setStatic(boolean isStatic);

		public static abstract class FIBCustomActionImpl extends FIBBrowserActionImpl implements FIBCustomAction {

			@Override
			public ActionType getActionType() {
				return ActionType.Custom;
			}
		}

	}

	public static class MethodBindingMustBeValid extends BindingMustBeValid<FIBBrowserAction> {
		public MethodBindingMustBeValid() {
			super("'method'_binding_is_not_valid", FIBBrowserAction.class);
		}

		@Override
		public DataBinding<?> getBinding(FIBBrowserAction object) {
			return object.getMethod();
		}
	}

	public static class IsAvailableBindingMustBeValid extends BindingMustBeValid<FIBBrowserAction> {
		public IsAvailableBindingMustBeValid() {
			super("'is_available'_binding_is_not_valid", FIBBrowserAction.class);
		}

		@Override
		public DataBinding<?> getBinding(FIBBrowserAction object) {
			return object.getIsAvailable();
		}
	}

}
