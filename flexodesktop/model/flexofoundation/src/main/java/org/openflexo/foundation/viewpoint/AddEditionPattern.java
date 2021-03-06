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
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingDefinition.BindingDefinitionType;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.foundation.validation.CompoundIssue;
import org.openflexo.foundation.validation.ValidationError;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.view.View;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.foundation.viewpoint.binding.ViewPointDataBinding;

public class AddEditionPattern extends AssignableAction {

	private static final Logger logger = Logger.getLogger(AddEditionPattern.class.getPackage().getName());

	private EditionPattern editionPatternType;
	private CreationScheme creationScheme;
	private String _creationSchemeURI;

	public AddEditionPattern(ViewPointBuilder builder) {
		super(builder);
	}

	@Override
	public EditionActionType getEditionActionType() {
		return EditionActionType.AddEditionPattern;
	}

	/*@Override
	public List<EditionPatternPatternRole> getAvailablePatternRoles() {
		return getEditionPattern().getPatternRoles(EditionPatternPatternRole.class);
	}*/

	@Override
	public String getInspectorName() {
		return null;
	}

	public View getView(EditionSchemeAction action) {
		return (View) getView().getBindingValue(action);
	}

	/*@Override
	public EditionPatternPatternRole getPatternRole() {
		try {
			return super.getPatternRole();
		} catch (ClassCastException e) {
			logger.warning("Unexpected pattern role type");
			setPatternRole(null);
			return null;
		}
	}*/

	// FIXME: if we remove this useless code, some FIB won't work (see EditionPatternView.fib, inspect an AddEditionPattern)
	// Need to be fixed in KeyValueProperty.java
	/*@Override
	public void setPatternRole(EditionPatternPatternRole patternRole) {
		super.setPatternRole(patternRole);
	}*/

	private ViewPointDataBinding view;

	private BindingDefinition VIEW = new BindingDefinition("view", View.class, BindingDefinitionType.GET, true);

	public BindingDefinition getViewBindingDefinition() {
		return VIEW;
	}

	public ViewPointDataBinding getView() {
		if (view == null) {
			view = new ViewPointDataBinding(this, EditionActionBindingAttribute.view, getViewBindingDefinition());
		}
		return view;
	}

	public void setView(ViewPointDataBinding aView) {
		if (aView != null) {
			aView.setOwner(this);
			aView.setBindingAttribute(EditionActionBindingAttribute.view);
			aView.setBindingDefinition(getViewBindingDefinition());
		}
		this.view = aView;
	}

	public EditionPattern getEditionPatternType() {
		if (getCreationScheme() != null) {
			return getCreationScheme().getEditionPattern();
		}
		return editionPatternType;
	}

	public void setEditionPatternType(EditionPattern editionPatternType) {
		this.editionPatternType = editionPatternType;
		if (getCreationScheme() != null && getCreationScheme().getEditionPattern() != editionPatternType) {
			setCreationScheme(null);
		}
	}

	public String _getCreationSchemeURI() {
		if (getCreationScheme() != null) {
			return getCreationScheme().getURI();
		}
		return _creationSchemeURI;
	}

	public void _setCreationSchemeURI(String uri) {
		if (getViewPointLibrary() != null) {
			creationScheme = (CreationScheme) getViewPointLibrary().getEditionScheme(uri);
		}
		_creationSchemeURI = uri;
	}

	public CreationScheme getCreationScheme() {
		if (getPatternRole() instanceof EditionPatternPatternRole) {
			return ((EditionPatternPatternRole) getPatternRole()).getCreationScheme();
		}
		if (creationScheme == null && _creationSchemeURI != null && getViewPointLibrary() != null) {
			creationScheme = (CreationScheme) getViewPointLibrary().getEditionScheme(_creationSchemeURI);
		}
		return creationScheme;
	}

	public void setCreationScheme(CreationScheme creationScheme) {
		this.creationScheme = creationScheme;
		if (creationScheme != null) {
			_creationSchemeURI = creationScheme.getURI();
		}
	}

	private Vector<AddEditionPatternParameter> parameters = new Vector<AddEditionPattern.AddEditionPatternParameter>();

	public Vector<AddEditionPatternParameter> getParameters() {
		updateParameters();
		return parameters;
	}

	public void setParameters(Vector<AddEditionPatternParameter> parameters) {
		this.parameters = parameters;
	}

	public void addToParameters(AddEditionPatternParameter parameter) {
		parameter.setAction(this);
		parameters.add(parameter);
	}

	public void removeFromParameters(AddEditionPatternParameter parameter) {
		parameter.setAction(null);
		parameters.remove(parameter);
	}

	public AddEditionPatternParameter getParameter(EditionSchemeParameter p) {
		for (AddEditionPatternParameter addEPParam : parameters) {
			if (addEPParam.getParam() == p) {
				return addEPParam;
			}
		}
		return null;
	}

	private void updateParameters() {
		Vector<AddEditionPatternParameter> parametersToRemove = new Vector<AddEditionPattern.AddEditionPatternParameter>(parameters);
		if (getCreationScheme() != null) {
			for (EditionSchemeParameter p : getCreationScheme().getParameters()) {
				AddEditionPatternParameter existingParam = getParameter(p);
				if (existingParam != null) {
					parametersToRemove.remove(existingParam);
				} else {
					addToParameters(new AddEditionPatternParameter(p));
				}
			}
		}
		for (AddEditionPatternParameter removeThis : parametersToRemove) {
			removeFromParameters(removeThis);
		}
	}

	public static class AddEditionPatternParameter extends EditionSchemeObject implements Bindable {

		@SuppressWarnings("unused")
		private static final Logger logger = Logger.getLogger(GraphicalElementSpecification.class.getPackage().getName());

		private AddEditionPattern action;

		private EditionSchemeParameter param;
		private String paramName;
		private ViewPointDataBinding value;
		private BindingDefinition BD;

		// Use it only for deserialization
		public AddEditionPatternParameter(ViewPointBuilder builder) {
			super(builder);
		}

		public AddEditionPatternParameter(EditionSchemeParameter param) {
			super(null);
			this.param = param;
			BD = new BindingDefinition(param.getName(), param.getType(), BindingDefinitionType.GET, true);
		}

		public EditionSchemeParameter getParameter() {
			return param;
		}

		@Override
		public EditionPattern getEditionPattern() {
			if (param != null) {
				return param.getEditionPattern();
			}
			return null;
		}

		@Override
		public EditionScheme getEditionScheme() {
			if (param != null) {
				return param.getEditionScheme();
			}
			return null;
		}

		public ViewPointDataBinding getValue() {
			if (value == null) {
				value = new ViewPointDataBinding(this, param, getBindingDefinition());
			}
			if (value.getBindingDefinition() == null) {
				value.setBindingDefinition(getBindingDefinition());
			}
			return value;
		}

		public void setValue(ViewPointDataBinding value) {
			if (value != null) {
				value.setOwner(this);
				value.setBindingAttribute(param);
				value.setBindingDefinition(getBindingDefinition());
			}
			this.value = value;
		}

		public Object evaluateParameterValue(EditionSchemeAction action) {
			if (getValue() == null || getValue().isUnset()) {
				/*logger.info("Le binding for " + param.getName() + " is not set");
				if (param instanceof URIParameter) {
					logger.info("C'est une URI, de base " + ((URIParameter) param).getBaseURI());
					logger.info("Je retourne " + ((URIParameter) param).getBaseURI().getBinding().getBindingValue(action));
					return ((URIParameter) param).getBaseURI().getBinding().getBindingValue(action);
				} else if (param.getDefaultValue() != null && param.getDefaultValue().isSet() && param.getDefaultValue().isValid()) {
					return param.getDefaultValue().getBinding().getBindingValue(action);
				}
				if (param.getIsRequired()) {
					logger.warning("Required parameter missing: " + param + ", some strange behaviour may happen from now...");
				}*/
				return null;
			} else if (getValue().isValid()) {
				return getValue().getBindingValue(action);
			} else {
				logger.warning("Invalid binding: " + getValue());
				getValue().getBinding().debugIsBindingValid();
			}
			return null;
		}

		public BindingDefinition getBindingDefinition() {
			if (BD == null && getParam() != null) {
				BD = new BindingDefinition(getParam().getName(), getParam().getType(), BindingDefinitionType.GET, true);
			}
			return BD;
		}

		@Override
		public String getInspectorName() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public BindingModel getBindingModel() {
			if (getAction() != null) {
				return getAction().getBindingModel();
			}
			return null;
		}

		@Override
		public ViewPoint getViewPoint() {
			if (getAction() != null) {
				return getAction().getViewPoint();
			}
			return null;
		}

		public AddEditionPattern getAction() {
			return action;
		}

		public void setAction(AddEditionPattern action) {
			this.action = action;
		}

		public EditionSchemeParameter getParam() {
			if (param == null && paramName != null && getAction() != null && getAction().getCreationScheme() != null) {
				param = getAction().getCreationScheme().getParameter(paramName);
			}
			return param;
		}

		public String _getParamName() {
			if (param != null) {
				return param.getName();
			}
			return paramName;
		}

		public void _setParamName(String param) {
			this.paramName = param;
		}

	}

	@Override
	public Type getAssignableType() {
		return getEditionPatternType();
	}

	public static class AddEditionPatternMustAddressACreationScheme extends
			ValidationRule<AddEditionPatternMustAddressACreationScheme, AddEditionPattern> {
		public AddEditionPatternMustAddressACreationScheme() {
			super(AddEditionPattern.class, "add_edition_pattern_action_must_address_a_valid_creation_scheme");
		}

		@Override
		public ValidationIssue<AddEditionPatternMustAddressACreationScheme, AddEditionPattern> applyValidation(AddEditionPattern action) {
			if (action.getCreationScheme() == null) {
				if (action.getEditionPatternType() == null) {
					return new ValidationError<AddEditionPatternMustAddressACreationScheme, AddEditionPattern>(this, action,
							"add_edition_pattern_action_doesn't_define_any_edition_pattern");
				} else {
					return new ValidationError<AddEditionPatternMustAddressACreationScheme, AddEditionPattern>(this, action,
							"add_edition_pattern_action_doesn't_define_any_creation_scheme");
				}
			}
			return null;
		}
	}

	public static class AddEditionPatternParametersMustBeValid extends
			ValidationRule<AddEditionPatternParametersMustBeValid, AddEditionPattern> {
		public AddEditionPatternParametersMustBeValid() {
			super(AddEditionPattern.class, "add_edition_pattern_parameters_must_be_valid");
		}

		@Override
		public ValidationIssue<AddEditionPatternParametersMustBeValid, AddEditionPattern> applyValidation(AddEditionPattern action) {
			if (action.getCreationScheme() != null) {
				Vector<ValidationIssue<AddEditionPatternParametersMustBeValid, AddEditionPattern>> issues = new Vector<ValidationIssue<AddEditionPatternParametersMustBeValid, AddEditionPattern>>();
				for (AddEditionPatternParameter p : action.getParameters()) {
					if (p.getParam().getIsRequired()) {
						if (p.getValue() == null || !p.getValue().isSet()) {
							if (p.getParam() instanceof URIParameter && ((URIParameter) p.getParam()).getBaseURI().isSet()
									&& ((URIParameter) p.getParam()).getBaseURI().isValid()) {
								// Special case, we will find a way to manage this
							} else {
								issues.add(new ValidationError(this, action, "parameter_s_value_is_not_defined: " + p.getParam().getName()));
							}
						} else if (!p.getValue().isValid()) {
							logger.info("Binding NOT valid: " + p.getValue() + " for " + p.paramName + " object="
									+ p.action.getFullyQualifiedName() + ". Reason follows.");
							p.getValue().getBinding().debugIsBindingValid();
							issues.add(new ValidationError(this, action, "parameter_s_value_is_not_valid: " + p.getParam().getName()));
						}
					}
				}
				if (issues.size() == 0) {
					return null;
				} else if (issues.size() == 1) {
					return issues.firstElement();
				} else {
					return new CompoundIssue<AddEditionPattern.AddEditionPatternParametersMustBeValid, AddEditionPattern>(action, issues);
				}
			}
			return null;
		}
	}

	public static class ViewBindingIsRequiredAndMustBeValid extends BindingIsRequiredAndMustBeValid<AddEditionPattern> {
		public ViewBindingIsRequiredAndMustBeValid() {
			super("'view'_binding_is_not_valid", AddEditionPattern.class);
		}

		@Override
		public ViewPointDataBinding getBinding(AddEditionPattern object) {
			return object.getView();
		}

		@Override
		public BindingDefinition getBindingDefinition(AddEditionPattern object) {
			return object.getViewBindingDefinition();
		}

	}

}
