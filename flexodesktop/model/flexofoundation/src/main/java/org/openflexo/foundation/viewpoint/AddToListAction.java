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
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.ViewPointObject.FMLRepresentationContext.FMLRepresentationOutput;
import org.openflexo.foundation.viewpoint.annotations.FIBPanel;

@FIBPanel("Fib/AddToListActionPanel.fib")
public class AddToListAction<MS extends ModelSlot<?>, T> extends AssignableAction<MS, Object> {

	private static final Logger logger = Logger.getLogger(AddToListAction.class.getPackage().getName());

	private DataBinding<Object> value;

	public AddToListAction(VirtualModel.VirtualModelBuilder builder) {
		super(builder);
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

	public DataBinding<Object> getValue() {
		if (value == null) {
			value = new DataBinding<Object>(this, Object.class, BindingDefinitionType.GET);
			value.setBindingName("value");
		}
		return value;
	}

	public void setValue(DataBinding<Object> value) {
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
		// TODO Xtof: when I will have found how to set same kind of Individual:<name> type in the XSD TA
		/* if (getValue().isSet() && getValue().isValid()) {
			return new ParameterizedTypeImpl(List.class,getValue().getAnalyzedType());
		} */
		return new ParameterizedTypeImpl(List.class, Object.class);
	}

	@Override
	public Object performAction(EditionSchemeAction action) {
		logger.info("performing AddToListAction");

		DataBinding<Object> assignation = getAssignation();
		Object objToAdd = getDeclaredObject(action);

		try {

			if (assignation != null) {
				Object assigObj = assignation.getBindingValue(action);
				if (assigObj instanceof List) {
					if (objToAdd != null) {
						((List) assigObj).add(objToAdd);
					} else {
						logger.warning("Won't add null object to list");

					}
				} else {
					if (assigObj == null) {
						logger.warning("Cannot add object to a null target : " + assignation.getUnparsedBinding());
					} else {
						logger.warning("Cannot add object to a non list target : " + assigObj.toString());
					}
					return null;
				}

			} else {
				logger.warning("Cannot perform Assignation as assignation is null");
			}
		} catch (TypeMismatchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullReferenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return objToAdd;
	}

	@Override
	public void notifiedBindingChanged(DataBinding<?> dataBinding) {
		if (dataBinding == getValue()) {
			updateVariableAssignation();
		}
		super.notifiedBindingChanged(dataBinding);
	}

	public static class ValueBindingIsRequiredAndMustBeValid extends BindingIsRequiredAndMustBeValid<AddToListAction> {
		public ValueBindingIsRequiredAndMustBeValid() {
			super("'value'_binding_is_not_valid", AddToListAction.class);
		}

		@Override
		public DataBinding<Object> getBinding(AddToListAction object) {
			return object.getValue();
		}

	}

}
