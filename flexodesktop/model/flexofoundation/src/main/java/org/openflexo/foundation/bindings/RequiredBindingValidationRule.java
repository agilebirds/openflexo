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
package org.openflexo.foundation.bindings;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.CodeType;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.TargetType;
import org.openflexo.foundation.dm.ComponentDMEntity;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.DMProperty;
import org.openflexo.foundation.dm.DMPropertyImplementationType;
import org.openflexo.foundation.dm.DMType;
import org.openflexo.foundation.ie.IEObject;
import org.openflexo.foundation.ie.operator.ConditionalOperator;
import org.openflexo.foundation.ie.widget.IEWidget;
import org.openflexo.foundation.param.ChoiceListParameter;
import org.openflexo.foundation.param.DMEntityParameter;
import org.openflexo.foundation.param.ParameterDefinition;
import org.openflexo.foundation.param.TextFieldParameter;
import org.openflexo.foundation.validation.FixProposal;
import org.openflexo.foundation.validation.ParameteredFixProposal;
import org.openflexo.foundation.validation.ValidationError;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.localization.FlexoLocalization;

public class RequiredBindingValidationRule<T extends IEObject> extends ValidationRule<RequiredBindingValidationRule<T>, T> {
	private static final Logger logger = Logger.getLogger(RequiredBindingValidationRule.class.getPackage().getName());

	public String bindingName;
	public String bindingDefinition;

	public RequiredBindingValidationRule(Class<T> objectType, String aBindingName, String aBindingDefinition) {
		super(objectType, "binding_must_be_defined");
		this.bindingName = aBindingName;
		this.bindingDefinition = aBindingDefinition;
	}

	@Override
	public String getLocalizedName() {
		if (bindingName != null) {
			return FlexoLocalization.localizedForKeyWithParams("binding_named_($bindingName)_is_required", this);
		}
		return null;
	}

	public String getLocalizedErrorMessageForUndefinedValue() {
		return FlexoLocalization.localizedForKeyWithParams("binding_named_($bindingName)_is_not_defined", this);
	}

	public String getLocalizedErrorMessageForInvalidValue() {
		return FlexoLocalization.localizedForKeyWithParams("binding_named_($bindingName)_has_invalid_value", this);
	}

	@Override
	public ValidationIssue<RequiredBindingValidationRule<T>, T> applyValidation(final T object) {
		final FlexoModelObject validatedObject = object;
		BindingDefinition bd = (BindingDefinition) validatedObject.objectForKey(bindingDefinition);
		AbstractBinding bv = (AbstractBinding) validatedObject.objectForKey(bindingName);
		if (bd == null) {
			logger.warning("Object of type " + object.getClass().getName() + " does not define any '" + bindingDefinition
					+ "' binding definition");
			return null;
		} else if (bd.getIsMandatory()) {
			if (bv == null || !bv.isBindingValid()) {
				ValidationError<RequiredBindingValidationRule<T>, T> error;
				if (bv == null) {
					error = new ValidationError<RequiredBindingValidationRule<T>, T>(this, object, null) {
						@Override
						public String getLocalizedMessage() {
							return getLocalizedErrorMessageForUndefinedValue();
						}
					};
				} else { // !bv.isBindingValid()
					error = new ValidationError<RequiredBindingValidationRule<T>, T>(this, object, null) {
						@Override
						public String getLocalizedMessage() {
							return getLocalizedErrorMessageForInvalidValue();
						}
					};
				}
				if (object instanceof IEWidget) {
					error.addToFixProposals(new AddEntryToComponentAndSetBinding(bindingName, bd));
				}
				Vector allAvailableBV = bd.searchMatchingBindingValue((Bindable) validatedObject, 2);
				for (int i = 0; i < allAvailableBV.size(); i++) {
					BindingValue proposal = (BindingValue) allAvailableBV.elementAt(i);
					error.addToFixProposals(new SetBinding(proposal));
				}
				return error;
			}
			return null;
		}
		return null;
	}

	public class SetBinding extends FixProposal<RequiredBindingValidationRule<T>, T> {
		public BindingValue bindingValue;

		public SetBinding(BindingValue aBindingValue) {
			super("set_binding_($bindingName)_to_($bindingValue.stringRepresentation)");
			bindingValue = aBindingValue;
		}

		@Override
		protected void fixAction() {
			((FlexoModelObject) getObject()).setObjectForKey(bindingValue, bindingName);
		}

		public String getBindingName() {
			return bindingName;
		}

		public void setBindingName() {
		}
	}

	protected static ParameterDefinition[] buildParameters(BindingDefinition bd) {
		ParameterDefinition[] returned = new ParameterDefinition[3];
		returned[0] = new TextFieldParameter("variableName", "variable_name", "");
		returned[1] = new DMEntityParameter("variableType", "variable_type", bd.getType().getBaseEntity());
		returned[2] = new ChoiceListParameter<DMPropertyImplementationType>("implementationType", "implementation_type",
				DMPropertyImplementationType.PUBLIC_FIELD);
		returned[2].addParameter("format", "localizedName");
		return returned;
	}

	public class AddEntryToComponentAndSetBinding extends ParameteredFixProposal<RequiredBindingValidationRule<T>, T> {
		public BindingDefinition bindingDefinition;
		public String bindingName;

		public AddEntryToComponentAndSetBinding(String bindingName, BindingDefinition bd) {
			super("add_entry_and_set_($bindingName)_onto", buildParameters(bd));
			this.bindingName = bindingName;
			this.bindingDefinition = bd;
		}

		@Override
		protected void fixAction() {
			String newVariableName = (String) getValueForParameter("variableName");
			DMEntity newVariableType = (DMEntity) getValueForParameter("variableType");
			DMPropertyImplementationType implementationType = (DMPropertyImplementationType) getValueForParameter("implementationType");
			((IEWidget) getObject()).createsBindingVariable(newVariableName, DMType.makeResolvedDMType(newVariableType),
					implementationType, false);
			DMProperty property = null;
			ComponentDMEntity componentDMEntity = ((IEWidget) getObject()).getComponentDMEntity();
			if (componentDMEntity != null) {
				property = componentDMEntity.getDMProperty(newVariableName);
			}
			if (property != null) {
				BindingVariable var = ((IEWidget) getObject()).getBindingModel().bindingVariableNamed("component");
				BindingValue newBindingValue = new BindingValue(bindingDefinition, getObject());
				newBindingValue.setBindingVariable(var);
				newBindingValue.addBindingPathElement(property);
				newBindingValue.connect();
				((FlexoModelObject) getObject()).setObjectForKey(newBindingValue, bindingName);
			}
		}

	}

	/**
	 * Overrides isValidForTarget
	 * 
	 * @see org.openflexo.foundation.validation.ValidationRule#isValidForTarget(TargetType)
	 */
	@Override
	public boolean isValidForTarget(TargetType targetType) {
		return getObjectType() == ConditionalOperator.class || targetType != CodeType.PROTOTYPE;
	}

}
