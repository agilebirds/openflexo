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

import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.fib.model.validation.ValidationReport;
import org.openflexo.model.annotations.DeserializationFinalizer;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.Import;
import org.openflexo.model.annotations.Imports;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity(isAbstract = true)
@ImplementationClass(FIBTableAction.FIBTableActionImpl.class)
@Imports({ @Import(FIBTableAction.FIBAddAction.class), @Import(FIBTableAction.FIBRemoveAction.class),
		@Import(FIBTableAction.FIBCustomAction.class) })
public abstract interface FIBTableAction extends FIBModelObject {

	public static enum ActionType {
		Add, Delete, Custom
	}

	@PropertyIdentifier(type = FIBTable.class)
	public static final String OWNER_KEY = "owner";

	@PropertyIdentifier(type = DataBinding.class)
	public static final String METHOD_KEY = "method";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String IS_AVAILABLE_KEY = "isAvailable";

	@Getter(value = OWNER_KEY, inverse = FIBTable.ACTIONS_KEY)
	public FIBTable getOwner();

	@Setter(OWNER_KEY)
	public void setOwner(FIBTable table);

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

	@DeserializationFinalizer
	public void finalizeDeserialization();

	public static abstract class FIBTableActionImpl extends FIBModelObjectImpl implements FIBTableAction {

		private static final Logger logger = Logger.getLogger(FIBTableAction.class.getPackage().getName());

		private DataBinding<Object> method;
		private DataBinding<Boolean> isAvailable;

		@Deprecated
		public static BindingDefinition METHOD = new BindingDefinition("method", Object.class, DataBinding.BindingDefinitionType.EXECUTE,
				false);
		@Deprecated
		public static BindingDefinition IS_AVAILABLE = new BindingDefinition("isAvailable", Boolean.class,
				DataBinding.BindingDefinitionType.GET, false);

		@Override
		public FIBTable getComponent() {
			return getOwner();
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
				return getOwner().getActionBindingModel();
			}
			return null;
		}

		@Override
		public void finalizeDeserialization() {
			logger.fine("finalizeDeserialization() for FIBTableAction " + getName());
			if (method != null) {
				method.decode();
			}
		}

		public abstract ActionType getActionType();

		@Override
		protected void applyValidation(ValidationReport report) {
			super.applyValidation(report);
			performValidation(MethodBindingMustBeValid.class, report);
			performValidation(IsAvailableBindingMustBeValid.class, report);
		}

	}

	@ModelEntity
	@ImplementationClass(FIBAddAction.FIBAddActionImpl.class)
	@XMLElement(xmlTag = "AddAction")
	public static interface FIBAddAction extends FIBTableAction {

		public static abstract class FIBAddActionImpl extends FIBTableActionImpl implements FIBAddAction {

			@Override
			public ActionType getActionType() {
				return ActionType.Add;
			}
		}
	}

	@ModelEntity
	@ImplementationClass(FIBRemoveAction.FIBRemoveActionImpl.class)
	@XMLElement(xmlTag = "RemoveAction")
	public static interface FIBRemoveAction extends FIBTableAction {

		public static abstract class FIBRemoveActionImpl extends FIBTableActionImpl implements FIBRemoveAction {
			@Override
			public ActionType getActionType() {
				return ActionType.Delete;
			}
		}
	}

	@ModelEntity
	@ImplementationClass(FIBCustomAction.FIBCustomActionImpl.class)
	@XMLElement(xmlTag = "CustomAction")
	public static interface FIBCustomAction extends FIBTableAction {

		@PropertyIdentifier(type = boolean.class)
		public static final String IS_STATIC_KEY = "isStatic";

		@Getter(value = IS_STATIC_KEY, defaultValue = "false")
		@XMLAttribute
		public boolean isStatic();

		@Setter(IS_STATIC_KEY)
		public void setStatic(boolean isStatic);

		public static abstract class FIBCustomActionImpl extends FIBTableActionImpl implements FIBCustomAction {
			@Override
			public ActionType getActionType() {
				return ActionType.Custom;
			}
		}
	}

	public static class MethodBindingMustBeValid extends BindingMustBeValid<FIBTableAction> {
		public MethodBindingMustBeValid() {
			super("'method'_binding_is_not_valid", FIBTableAction.class);
		}

		@Override
		public DataBinding<?> getBinding(FIBTableAction object) {
			return object.getMethod();
		}
	}

	public static class IsAvailableBindingMustBeValid extends BindingMustBeValid<FIBTableAction> {
		public IsAvailableBindingMustBeValid() {
			super("'is_available'_binding_is_not_valid", FIBTableAction.class);
		}

		@Override
		public DataBinding<?> getBinding(FIBTableAction object) {
			return object.getIsAvailable();
		}
	}
}
