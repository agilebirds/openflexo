/*
 * (c) Copyright 2010-2011 AgileBirds
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
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.foundation.validation.CompoundIssue;
import org.openflexo.foundation.validation.ValidationError;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.view.EditionPatternInstance;
import org.openflexo.foundation.view.VirtualModelInstance;
import org.openflexo.foundation.view.action.CreationSchemeAction;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.annotations.FIBPanel;

/**
 * This action is used to explicitely instanciate a new {@link EditionPatternInstance} in a given {@link VirtualModelInstance} with some
 * parameters
 * 
 * @author sylvain
 * 
 * @param <M>
 * @param <MM>
 */

@FIBPanel("Fib/AddEditionPatternInstancePanel.fib")
public class AddEditionPatternInstance extends AssignableAction<VirtualModelModelSlot, EditionPatternInstance> {

	static final Logger logger = Logger.getLogger(AddEditionPatternInstance.class.getPackage().getName());

	private EditionPattern editionPatternType;
	private CreationScheme creationScheme;
	private String _creationSchemeURI;

	public AddEditionPatternInstance() {
		super();
	}

	public VirtualModelInstance getVirtualModelInstance(EditionSchemeAction action) {
		try {
			// System.out.println("getVirtualModelInstance() with " + getVirtualModelInstance());
			// System.out.println("Valid=" + getVirtualModelInstance().isValid() + " " + getVirtualModelInstance().invalidBindingReason());
			// System.out.println("returned: " + getVirtualModelInstance().getBindingValue(action));
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

	private Vector<AddEditionPatternInstanceParameter> parameters = new Vector<AddEditionPatternInstanceParameter>();

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
		Vector<AddEditionPatternInstanceParameter> parametersToRemove = new Vector<AddEditionPatternInstanceParameter>(
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
		logger.info("Perform performAddEditionPatternInstance " + action);
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
			ValidationRule<AddEditionPatternInstanceParametersMustBeValid, AddEditionPatternInstance> {

		public AddEditionPatternInstanceParametersMustBeValid() {
			super(AddEditionPatternInstance.class, "add_edition_pattern_parameters_must_be_valid");
		}

		@Override
		public ValidationIssue<AddEditionPatternInstanceParametersMustBeValid, AddEditionPatternInstance> applyValidation(
				AddEditionPatternInstance action) {
			if (action.getCreationScheme() != null) {
				Vector<ValidationIssue<AddEditionPatternInstanceParametersMustBeValid, AddEditionPatternInstance>> issues = new Vector<ValidationIssue<AddEditionPatternInstanceParametersMustBeValid, AddEditionPatternInstance>>();
				for (AddEditionPatternInstanceParameter p : action.getParameters()) {

					EditionSchemeParameter param = p.getParam();
					if (param.getIsRequired()) {
						if (p.getValue() == null || !p.getValue().isSet()) {
							DataBinding<String> uri = ((URIParameter) param).getBaseURI();
							if (param instanceof URIParameter && uri.isSet() && uri.isValid()) {
								// Special case, we will find a way to manage this
							} else {
								issues.add(new ValidationError(this, action, "parameter_s_value_is_not_defined: " + param.getName()));
							}
						} else if (!p.getValue().isValid()) {
							logger.info("Binding NOT valid: " + p.getValue() + " for " + p.paramName + " object="
									+ p.action.getStringRepresentation() + ". Reason: " + p.getValue().invalidBindingReason());
							issues.add(new ValidationError(this, action, "parameter_s_value_is_not_valid: " + param.getName()));
						}
					}
				}
				if (issues.size() == 0) {
					return null;
				} else if (issues.size() == 1) {
					return issues.firstElement();
				} else {
					return new CompoundIssue<AddEditionPatternInstance.AddEditionPatternInstanceParametersMustBeValid, AddEditionPatternInstance>(
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
		public DataBinding<VirtualModelInstance> getBinding(AddEditionPatternInstance object) {
			return object.getVirtualModelInstance();
		}

	}

}
