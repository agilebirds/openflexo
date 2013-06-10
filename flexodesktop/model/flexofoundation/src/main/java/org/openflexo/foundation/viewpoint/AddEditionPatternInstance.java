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
import java.util.Collection;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.foundation.technologyadapter.FlexoMetaModel;
import org.openflexo.foundation.technologyadapter.FlexoModel;
import org.openflexo.foundation.validation.CompoundIssue;
import org.openflexo.foundation.validation.Validable;
import org.openflexo.foundation.validation.ValidationError;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.view.EditionPatternInstance;
import org.openflexo.foundation.view.View;
import org.openflexo.foundation.view.VirtualModelInstance;
import org.openflexo.foundation.view.action.CreationSchemeAction;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.view.diagram.viewpoint.GraphicalElementSpecification;

/**
 * This action is used to explicitely instanciate a new {@link EditionPatternInstance} in a given {@link VirtualModelInstance} with some
 * parameters
 * 
 * @author sylvain
 * 
 * @param <M>
 * @param <MM>
 */
public class AddEditionPatternInstance<M extends FlexoModel<M, MM>, MM extends FlexoMetaModel<MM>> extends
		AssignableAction<M, MM, EditionPatternInstance> {

	private static final Logger logger = Logger.getLogger(AddEditionPatternInstance.class.getPackage().getName());

	private EditionPattern editionPatternType;
	private CreationScheme creationScheme;
	private String _creationSchemeURI;

	public AddEditionPatternInstance(VirtualModel.VirtualModelBuilder builder) {
		super(builder);
	}

	@Override
	public EditionActionType getEditionActionType() {
		return EditionActionType.AddEditionPatternInstance;
	}

	public VirtualModelInstance getVirtualModelInstance(EditionSchemeAction action) {
		try {
			return getVirtualModelInstance().getBindingValue(action);
		} catch (TypeMismatchException e) {
			e.printStackTrace();
		} catch (NullReferenceException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	private DataBinding<VirtualModelInstance> virtualModelInstance;

	public DataBinding<VirtualModelInstance> getVirtualModelInstance() {
		if (virtualModelInstance == null) {
			virtualModelInstance = new DataBinding<VirtualModelInstance>(this, VirtualModelInstance.class,
					DataBinding.BindingDefinitionType.GET);
			virtualModelInstance.setBindingName("virtualModelInstance");
		}
		return virtualModelInstance;
	}

	public void setVirtualModelInstance(DataBinding<VirtualModelInstance> aVirtualModelInstance) {
		if (aVirtualModelInstance != null) {
			aVirtualModelInstance.setOwner(this);
			aVirtualModelInstance.setBindingName("virtualModelInstance");
			aVirtualModelInstance.setDeclaredType(VirtualModelInstance.class);
			aVirtualModelInstance.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
		}
		this.virtualModelInstance = aVirtualModelInstance;
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
		if (creationScheme == null && _creationSchemeURI != null && getViewPointLibrary() != null) {
			creationScheme = (CreationScheme) getViewPointLibrary().getEditionScheme(_creationSchemeURI);
		}
		if (creationScheme == null && getPatternRole() instanceof EditionPatternInstancePatternRole) {
			creationScheme = ((EditionPatternInstancePatternRole) getPatternRole()).getCreationScheme();
		}
		return creationScheme;
	}

	public void setCreationScheme(CreationScheme creationScheme) {
		this.creationScheme = creationScheme;
		if (creationScheme != null) {
			_creationSchemeURI = creationScheme.getURI();
		}
	}

	private Vector<AddEditionPatternInstanceParameter> parameters = new Vector<AddEditionPatternInstance.AddEditionPatternInstanceParameter>();

	public Vector<AddEditionPatternInstanceParameter> getParameters() {
		updateParameters();
		return parameters;
	}

	public void setParameters(Vector<AddEditionPatternInstanceParameter> parameters) {
		this.parameters = parameters;
	}

	public void addToParameters(AddEditionPatternInstanceParameter parameter) {
		parameter.setAction(this);
		parameters.add(parameter);
	}

	public void removeFromParameters(AddEditionPatternInstanceParameter parameter) {
		parameter.setAction(null);
		parameters.remove(parameter);
	}

	public AddEditionPatternInstanceParameter getParameter(EditionSchemeParameter p) {
		for (AddEditionPatternInstanceParameter addEPParam : parameters) {
			if (addEPParam.getParam() == p) {
				return addEPParam;
			}
		}
		return null;
	}

	private void updateParameters() {
		Vector<AddEditionPatternInstanceParameter> parametersToRemove = new Vector<AddEditionPatternInstance.AddEditionPatternInstanceParameter>(
				parameters);
		if (getCreationScheme() != null) {
			for (EditionSchemeParameter p : getCreationScheme().getParameters()) {
				AddEditionPatternInstanceParameter existingParam = getParameter(p);
				if (existingParam != null) {
					parametersToRemove.remove(existingParam);
				} else {
					addToParameters(new AddEditionPatternInstanceParameter(p));
				}
			}
		}
		for (AddEditionPatternInstanceParameter removeThis : parametersToRemove) {
			removeFromParameters(removeThis);
		}
	}

	@Override
	public EditionPatternInstance performAction(EditionSchemeAction action) {
		logger.info("Perform performAddEditionPattern " + action);
		VirtualModelInstance vmInstance = getVirtualModelInstance(action);
		logger.info("VirtualModelInstance: " + vmInstance);
		CreationSchemeAction creationSchemeAction = CreationSchemeAction.actionType.makeNewEmbeddedAction(vmInstance, null, action);
		creationSchemeAction.setVirtualModelInstance(vmInstance);
		creationSchemeAction.setCreationScheme(getCreationScheme());
		for (AddEditionPatternInstanceParameter p : getParameters()) {
			EditionSchemeParameter param = p.getParam();
			Object value = p.evaluateParameterValue(action);
			logger.info("For parameter " + param + " value is " + value);
			if (value != null) {
				creationSchemeAction.setParameterValue(p.getParam(), p.evaluateParameterValue(action));
			}
		}
		creationSchemeAction.doAction();
		if (creationSchemeAction.hasActionExecutionSucceeded()) {
			logger.info("Successfully performed performAddEditionPattern " + action);
			return creationSchemeAction.getEditionPatternInstance();
		}
		return null;
	}

	public static class AddEditionPatternInstanceParameter extends EditionSchemeObject implements Bindable {

		@SuppressWarnings("unused")
		private static final Logger logger = Logger.getLogger(GraphicalElementSpecification.class.getPackage().getName());

		private AddEditionPatternInstance action;

		private EditionSchemeParameter param;
		private String paramName;
		private DataBinding<Object> value;

		// Use it only for deserialization
		public AddEditionPatternInstanceParameter(VirtualModel.VirtualModelBuilder builder) {
			super(builder);
		}

		public AddEditionPatternInstanceParameter(EditionSchemeParameter param) {
			super(null);
			this.param = param;
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

		public DataBinding<Object> getValue() {
			if (value == null) {
				value = new DataBinding<Object>(this, param.getType(), DataBinding.BindingDefinitionType.GET);
				value.setBindingName(param.getName());
			}
			return value;
		}

		public void setValue(DataBinding<Object> value) {
			if (value != null) {
				value.setOwner(this);
				value.setBindingName(param != null ? param.getName() : "param");
				value.setDeclaredType(param != null ? param.getType() : Object.class);
				value.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
			}
			this.value = value;
		}

		public Object evaluateParameterValue(EditionSchemeAction action) {
			if (getValue() == null || getValue().isUnset()) {
				/*logger.info("Binding for " + param.getName() + " is not set");
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
			} else {
				logger.warning("Invalid binding: " + getValue() + " Reason: " + getValue().invalidBindingReason());
			}
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
		public VirtualModel getVirtualModel() {
			if (getAction() != null) {
				return getAction().getVirtualModel();
			}
			return null;
		}

		public AddEditionPatternInstance getAction() {
			return action;
		}

		public void setAction(AddEditionPatternInstance action) {
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

		@Override
		public Collection<? extends Validable> getEmbeddedValidableObjects() {
			return null;
		}

		@Override
		public String getURI() {
			return null;
		}

	}

	@Override
	public Type getAssignableType() {
		return EditionPatternInstanceType.getEditionPatternInstanceType(getEditionPatternType());
	}

	public static class AddEditionPatternInstanceMustAddressACreationScheme extends
			ValidationRule<AddEditionPatternInstanceMustAddressACreationScheme, AddEditionPatternInstance> {
		public AddEditionPatternInstanceMustAddressACreationScheme() {
			super(AddEditionPatternInstance.class, "add_edition_pattern_action_must_address_a_valid_creation_scheme");
		}

		@Override
		public ValidationIssue<AddEditionPatternInstanceMustAddressACreationScheme, AddEditionPatternInstance> applyValidation(
				AddEditionPatternInstance action) {
			if (action.getCreationScheme() == null) {
				if (action.getEditionPatternType() == null) {
					return new ValidationError<AddEditionPatternInstanceMustAddressACreationScheme, AddEditionPatternInstance>(this,
							action, "add_edition_pattern_action_doesn't_define_any_edition_pattern");
				} else {
					return new ValidationError<AddEditionPatternInstanceMustAddressACreationScheme, AddEditionPatternInstance>(this,
							action, "add_edition_pattern_action_doesn't_define_any_creation_scheme");
				}
			}
			return null;
		}
	}

	public static class AddEditionPatternInstanceParametersMustBeValid extends
			ValidationRule<AddEditionPatternInstanceParametersMustBeValid, AddEditionPatternInstance<?, ?>> {
		public AddEditionPatternInstanceParametersMustBeValid() {
			super(AddEditionPatternInstance.class, "add_edition_pattern_parameters_must_be_valid");
		}

		@Override
		public ValidationIssue<AddEditionPatternInstanceParametersMustBeValid, AddEditionPatternInstance<?, ?>> applyValidation(
				AddEditionPatternInstance<?, ?> action) {
			if (action.getCreationScheme() != null) {
				Vector<ValidationIssue<AddEditionPatternInstanceParametersMustBeValid, AddEditionPatternInstance<?, ?>>> issues = new Vector<ValidationIssue<AddEditionPatternInstanceParametersMustBeValid, AddEditionPatternInstance<?, ?>>>();
				for (AddEditionPatternInstanceParameter p : action.getParameters()) {
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
									+ p.action.getFullyQualifiedName() + ". Reason: " + p.getValue().invalidBindingReason());
							issues.add(new ValidationError(this, action, "parameter_s_value_is_not_valid: " + p.getParam().getName()));
						}
					}
				}
				if (issues.size() == 0) {
					return null;
				} else if (issues.size() == 1) {
					return issues.firstElement();
				} else {
					return new CompoundIssue<AddEditionPatternInstance.AddEditionPatternInstanceParametersMustBeValid, AddEditionPatternInstance<?, ?>>(
							action, issues);
				}
			}
			return null;
		}
	}

	public static class VirtualModelInstanceBindingIsRequiredAndMustBeValid extends
			BindingIsRequiredAndMustBeValid<AddEditionPatternInstance> {
		public VirtualModelInstanceBindingIsRequiredAndMustBeValid() {
			super("'virtual_model_instance'_binding_is_not_valid", AddEditionPatternInstance.class);
		}

		@Override
		public DataBinding<View> getBinding(AddEditionPatternInstance object) {
			return object.getVirtualModelInstance();
		}

	}

}
