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
package org.openflexo.foundation.viewpoint;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.logging.Logger;

import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.DataBinding.BindingDefinitionType;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.FMLRepresentationContext.FMLRepresentationOutput;
import org.openflexo.foundation.viewpoint.annotations.FIBPanel;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

@FIBPanel("Fib/AssignationActionPanel.fib")
@ModelEntity
@ImplementationClass(AssignationAction.AssignationActionImpl.class)
@XMLElement
public interface AssignationAction<T> extends AssignableAction<ModelSlot<?>, T> {

	@PropertyIdentifier(type = DataBinding.class)
	public static final String VALUE_KEY = "value";

	@Getter(value = VALUE_KEY)
	@XMLAttribute
	public DataBinding<T> getValue();

	@Setter(VALUE_KEY)
	public void setValue(DataBinding<T> value);

	public static abstract class AssignationActionImpl<T> extends AssignableActionImpl<ModelSlot<?>, T> implements AssignationAction<T> {

		private static final Logger logger = Logger.getLogger(AssignationAction.class.getPackage().getName());

		private DataBinding<T> value;

		public AssignationActionImpl() {
			super();
		}

		@Override
		public String getFMLRepresentation(FMLRepresentationContext context) {
			FMLRepresentationOutput out = new FMLRepresentationOutput(context);
			out.append(getAssignation().toString() + " = " + getValue().toString() + ";", context);
			return out.toString();
		}

		@Override
		public boolean isAssignationRequired() {
			return true;
		}

		public Object getDeclaredObject(EditionSchemeAction action) {
			try {
				return getValue().getBindingValue(action);
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		public DataBinding<T> getValue() {
			if (value == null) {
				value = new DataBinding<T>(this, Object.class, BindingDefinitionType.GET);
				value.setBindingName("value");
			}
			return value;
		}

		@Override
		public void setValue(DataBinding<T> value) {
			if (value != null) {
				value.setOwner(this);
				value.setBindingName("value");
				value.setDeclaredType(Object.class);
				value.setBindingDefinitionType(BindingDefinitionType.GET);
			}
			this.value = value;
		}

		@Override
		public Type getAssignableType() {
			if (getValue().isSet() && getValue().isValid()) {
				return getValue().getAnalyzedType();
			}
			return Object.class;
		}

		@Override
		public T performAction(EditionSchemeAction action) {
			return (T) getDeclaredObject(action);
		}

		@Override
		public void notifiedBindingChanged(DataBinding<?> dataBinding) {
			if (dataBinding == getValue()) {
				updateVariableAssignation();
			}
			super.notifiedBindingChanged(dataBinding);
		}

		public static class ValueBindingIsRequiredAndMustBeValid extends BindingIsRequiredAndMustBeValid<AssignationAction> {
			public ValueBindingIsRequiredAndMustBeValid() {
				super("'value'_binding_is_not_valid", AssignationAction.class);
			}

			@Override
			public DataBinding<Object> getBinding(AssignationAction object) {
				return object.getValue();
			}

		}

	}
}
