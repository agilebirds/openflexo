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

import org.openflexo.antar.binding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.DataBinding.BindingDefinitionType;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.technologyadapter.FlexoMetaModel;
import org.openflexo.foundation.technologyadapter.FlexoModel;
import org.openflexo.foundation.view.action.EditionSchemeAction;

public class DeclareVariable<M extends FlexoModel<M, MM>, MM extends FlexoMetaModel<MM>, T> extends AssignableAction<M, MM, FlexoObject> {

	private static final Logger logger = Logger.getLogger(DeclareVariable.class.getPackage().getName());

	private DataBinding<Object> value;

	private String variableName = "variable";

	public DeclareVariable(VirtualModel.VirtualModelBuilder builder) {
		super(builder);
	}

	@Override
	public EditionActionType getEditionActionType() {
		return EditionActionType.DeclareVariable;
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

	public void setValue(DataBinding<Object> object) {
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
	public FlexoObject performAction(EditionSchemeAction action) {
		return (FlexoObject) getDeclaredObject(action);
	}

	@Override
	public void finalizePerformAction(EditionSchemeAction action, FlexoObject initialContext) {
		// TODO Auto-generated method stub

	}

	public String getVariableName() {
		return variableName;
	}

	public void setVariableName(String variableName) {
		this.variableName = variableName;
		updateAssignation();
	}

	private DataBinding<Object> assignation;

	@Override
	public DataBinding<Object> getAssignation() {
		if (assignation == null) {
			updateAssignation();
		}
		return assignation;
	}

	private void updateAssignation() {
		assignation = new DataBinding<Object>(getVariableName(), this, getAssignableType(), BindingDefinitionType.GET_SET);
	}

	@Override
	public void notifiedBindingChanged(DataBinding<?> dataBinding) {
		if (dataBinding == getValue()) {
			updateAssignation();
		}
		super.notifiedBindingChanged(dataBinding);
	}

	@Override
	protected BindingModel buildInferedBindingModel() {
		BindingModel returned = super.buildInferedBindingModel();
		returned.addToBindingVariables(new BindingVariable(getVariableName(), getAssignableType()) {
			@Override
			public Object getBindingValue(Object target, BindingEvaluationContext context) {
				logger.info("What should i return for " + getVariableName() + " ? target " + target + " context=" + context);
				return super.getBindingValue(target, context);
			}

			@Override
			public Type getType() {
				return getAssignableType();
			}
		});
		return returned;
	}

	@Override
	protected void rebuildInferedBindingModel() {
		super.rebuildInferedBindingModel();
		/*for (EditionAction<?, ?, ?> action : getActionContainer().getActions()) {
			action.rebuildInferedBindingModel();
		}*/
	}

	public static class ObjectBindingIsRequiredAndMustBeValid extends BindingIsRequiredAndMustBeValid<DeclareVariable> {
		public ObjectBindingIsRequiredAndMustBeValid() {
			super("'value'_binding_is_not_valid", DeclareVariable.class);
		}

		@Override
		public DataBinding<Object> getBinding(DeclareVariable object) {
			return object.getValue();
		}

	}

}
