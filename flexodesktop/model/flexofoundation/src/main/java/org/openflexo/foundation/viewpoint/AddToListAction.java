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
public class AddToListAction<MS extends ModelSlot<?>, T> extends EditionAction<MS, Object> {

	private static final Logger logger = Logger.getLogger(AddToListAction.class.getPackage().getName());

	private DataBinding<Object> value;
	private DataBinding<Object> list;

	public AddToListAction(VirtualModel.VirtualModelBuilder builder) {
		super(builder);
	}

	@Override
	public String getFMLRepresentation(FMLRepresentationContext context) {
		FMLRepresentationOutput out = new FMLRepresentationOutput(context);
		out.append(getList().toString() + " .add( " + getValue().toString() + ");", context);
		return out.toString();
	}

	
	public boolean isListRequired() {
		return true;
	}
	
	public boolean isValueRequired() {
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
	

	public Type getListType() {
		if (getValue().isSet() && getValue().isValid()) {
			return  new ParameterizedTypeImpl(List.class, getValueType());
		}
		return new ParameterizedTypeImpl(List.class, Object.class);
	}

	public DataBinding<Object> getList() {

		// TODO Xtof: when I will have found how to set same kind of Individual:<name> type in the XSD TA
		if (list == null) {
			list = new DataBinding<Object>(this, new ParameterizedTypeImpl(List.class, Object.class), BindingDefinitionType.GET);
			list.setBindingName("list");
		}
		return list;
	}

	public void setList(DataBinding<Object> list) {

		// TODO Xtof: when I will have found how to set same kind of Individual:<name> type in the XSD TA
		if (list != null) {
			list.setOwner(this);
			list.setBindingName("list");
			list.setDeclaredType(new ParameterizedTypeImpl(List.class, Object.class));
			list.setBindingDefinitionType(BindingDefinitionType.GET);
		}
		this.list = list;
	}

	public Type getValueType() {
		if (getValue().isSet() && getValue().isValid()) {
			return getValue().getAnalyzedType();
		}
		return Object.class;
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
	public Object performAction(EditionSchemeAction action) {
		logger.info("performing AddToListAction");

		DataBinding<Object> list = getList();
		Object objToAdd = getDeclaredObject(action);

		try {

			if (list != null) {
				Object listObj = list.getBindingValue(action);
				if (listObj instanceof List) {
					if (objToAdd != null) {
						((List) listObj).add(objToAdd);
					} else {
						logger.warning("Won't add null object to list");

					}
				} else {
					if (listObj == null) {
						logger.warning("Cannot add object to a null target : " + list.getUnparsedBinding());
					} else {
						logger.warning("Cannot add object to a non list target : " + listObj.toString());
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

	protected void updateVariableValue() {
		value = new DataBinding<Object>("value", this, getValueType(), BindingDefinitionType.GET_SET);
	}
	
	protected void updateVariableList() {
		list = new DataBinding<Object>("list", this, getListType(), BindingDefinitionType.GET_SET);
	}
	
	@Override
	public void notifiedBindingChanged(DataBinding<?> dataBinding) {
		if (dataBinding == getValue()) {
			updateVariableValue();
		}
		if (dataBinding == getList()) {
			updateVariableList();
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
	
	public static class ListBindingIsRequiredAndMustBeValid extends BindingIsRequiredAndMustBeValid<AddToListAction> {
		public ListBindingIsRequiredAndMustBeValid() {
			super("'list'_binding_is_not_valid", AddToListAction.class);
		}

		@Override
		public DataBinding<Object> getBinding(AddToListAction object) {
			return object.getList();
		}

	}

	@Override
	public void finalizePerformAction(EditionSchemeAction action,
			Object initialContext) {
		// TODO Auto-generated method stub
		
	}


}
