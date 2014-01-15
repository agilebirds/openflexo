/*
 * (c) Copyright 2012-2013 Openflexo
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
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.DataBinding.BindingDefinitionType;
import org.openflexo.antar.binding.ParameterizedTypeImpl;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.foundation.ontology.IFlexoOntologyIndividual;
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

@FIBPanel("Fib/RemoveFromListActionPanel.fib")
@ModelEntity
@ImplementationClass(RemoveFromListAction.RemoveFromListActionImpl.class)
@XMLElement
public interface RemoveFromListAction<MS extends ModelSlot<?>, T> extends AssignableAction<MS, Object> {

	@PropertyIdentifier(type = DataBinding.class)
	public static final String VALUE_KEY = "value";

	@Getter(value = VALUE_KEY)
	@XMLAttribute
	public DataBinding<?> getValue();

	@Setter(VALUE_KEY)
	public void setValue(DataBinding<?> value);

	public static abstract class RemoveFromListActionImpl<MS extends ModelSlot<?>, T> extends AssignableActionImpl<MS, Object> implements
			RemoveFromListAction<MS, T> {

		private static final Logger logger = Logger.getLogger(RemoveFromListAction.class.getPackage().getName());

		private DataBinding<?> value;

		public RemoveFromListActionImpl() {
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
		public DataBinding<?> getValue() {
			if (value == null) {
				value = new DataBinding<Object>(this, Object.class, BindingDefinitionType.GET);
				value.setBindingName("value");
			}
			return value;
		}

		@Override
		public void setValue(DataBinding<?> value) {
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
				return new ParameterizedTypeImpl(List.class, getValue().getAnalyzedType());
			}
			return new ParameterizedTypeImpl(List.class, IFlexoOntologyIndividual.class);
		}

		@Override
		public Object performAction(EditionSchemeAction action) {
			return getDeclaredObject(action);
		}

		@Override
		public void notifiedBindingChanged(DataBinding<?> dataBinding) {
			if (dataBinding == getValue()) {
				updateVariableAssignation();
			}
			super.notifiedBindingChanged(dataBinding);
		}

		public static class ValueBindingIsRequiredAndMustBeValid extends BindingIsRequiredAndMustBeValid<RemoveFromListAction> {
			public ValueBindingIsRequiredAndMustBeValid() {
				super("'value'_binding_is_not_valid", RemoveFromListAction.class);
			}

			@Override
			public DataBinding<Object> getBinding(RemoveFromListAction object) {
				return object.getValue();
			}

		}

	}
}
