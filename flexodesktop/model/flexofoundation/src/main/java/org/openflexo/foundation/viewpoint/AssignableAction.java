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

import java.lang.reflect.Type;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.DataBinding.BindingDefinitionType;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.toolbox.StringUtils;

/**
 * Abstract class representing an EditionAction with the particularity of returning a value which can be assigned
 * 
 * @author sylvain
 * 
 */
public abstract class AssignableAction<MS extends ModelSlot<?>, T> extends EditionAction<MS, T> {

	private static final Logger logger = Logger.getLogger(AssignableAction.class.getPackage().getName());

	private DataBinding<Object> assignation;

	private String variableName = null;

	public AssignableAction() {
		super();
	}

	public boolean isAssignationRequired() {
		return false;
	}

	/*@Override
	public abstract EditionActionType getEditionActionType();*/

	public abstract Type getAssignableType();

	public DataBinding<Object> getAssignation() {
		if (assignation == null) {
			if (StringUtils.isNotEmpty(variableName)) {
				updateVariableAssignation();
			} else {
				assignation = new DataBinding<Object>(this, Object.class, DataBinding.BindingDefinitionType.GET_SET) {
					@Override
					public Type getDeclaredType() {
						return getAssignableType();
					}
				};
				assignation.setDeclaredType(getAssignableType());
				assignation.setBindingName("assignation");
				assignation.setMandatory(isAssignationRequired());
			}
		}
		assignation.setDeclaredType(getAssignableType());
		return assignation;
	}

	public void setAssignation(DataBinding<Object> assignation) {
		if (assignation != null) {
			this.assignation = new DataBinding<Object>(assignation.toString(), this, Object.class,
					DataBinding.BindingDefinitionType.GET_SET) {
				@Override
				public Type getDeclaredType() {
					return getAssignableType();
				}
			};
			/*assignation.setOwner(this);
			assignation.setBindingName("assignation");
			assignation.setDeclaredType(getAssignableType());
			assignation.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET_SET);
			assignation.setMandatory(isAssignationRequired());*/
		}
		// this.assignation = assignation;
		notifiedBindingChanged(this.assignation);
	}

	public PatternRole<?> getPatternRole() {
		if (getEditionPattern() == null) {
			return null;
		}
		if (assignation != null) {
			return getEditionPattern().getPatternRole(assignation.toString());
		}
		return null;
	}

	@Override
	public String getStringRepresentation() {
		return getClass().getSimpleName()
				+ (StringUtils.isNotEmpty(getAssignation().toString()) ? " (" + getAssignation().toString() + ")" : "");
	}

	public String getVariableName() {
		return variableName;
	}

	public void setVariableName(String variableName) {
		if (!FlexoObject.areSameValue(variableName, this.variableName)) {
			this.variableName = variableName;
			if (StringUtils.isNotEmpty(variableName)) {
				updateVariableAssignation();
			}
			if (getActionContainer() != null) {
				getActionContainer().variableAdded(this);
			}
		}
	}

	public boolean getIsVariableDeclaration() {
		return StringUtils.isNotEmpty(getVariableName());
	}

	public void setIsVariableDeclaration(boolean flag) {
		if (flag) {
			if (StringUtils.isEmpty(getVariableName())) {
				setVariableName("newVariable");
			}
		} else {
			if (StringUtils.isNotEmpty(getVariableName())) {
				setVariableName(null);
				getAssignation().reset();
			}
		}
	}

	protected void updateVariableAssignation() {
		assignation = new DataBinding<Object>(getVariableName(), this, getAssignableType(), BindingDefinitionType.GET_SET);
	}

	@Override
	public void finalizePerformAction(org.openflexo.foundation.view.action.EditionSchemeAction action, T initialContext) {
		/*if (getIsVariableDeclaration()) {
			System.out.println("Setting variable " + getVariableName() + " with " + initialContext);
			action.declareVariable(getVariableName(), initialContext);
		}*/
	}

	@Override
	protected final BindingModel buildInferedBindingModel() {
		BindingModel returned = super.buildInferedBindingModel();
		if (getIsVariableDeclaration()) {
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
		}
		return returned;
	}

	public static class AssignationBindingMustBeValid extends BindingMustBeValid<AssignableAction> {
		public AssignationBindingMustBeValid() {
			super("'assign'_binding_is_not_valid", AssignableAction.class);
		}

		@Override
		public DataBinding<Object> getBinding(AssignableAction object) {
			return object.getAssignation();
		}

	}

}
