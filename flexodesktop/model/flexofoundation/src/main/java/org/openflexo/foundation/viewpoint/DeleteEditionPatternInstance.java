/*
 * (c) Copyright 2013 Openflexo
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
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.foundation.validation.CompoundIssue;
import org.openflexo.foundation.validation.Validable;
import org.openflexo.foundation.validation.ValidationError;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.view.EditionPatternInstance;
import org.openflexo.foundation.view.VirtualModelInstance;
import org.openflexo.foundation.view.action.DeletionSchemeAction;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.view.diagram.viewpoint.GraphicalElementSpecification;
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

@FIBPanel("Fib/DeleteEditionPatternInstancePanel.fib")
public class DeleteEditionPatternInstance  extends DeleteAction<VirtualModelModelSlot, EditionPatternInstance> {

	private static final Logger logger = Logger.getLogger(DeleteEditionPatternInstance.class.getPackage().getName());

	private EditionPattern editionPatternType;
	private DeletionScheme deletionScheme;
	private String _deletionSchemeURI;

	public DeleteEditionPatternInstance(VirtualModel.VirtualModelBuilder builder) {
		super(builder);
	}

	public VirtualModelInstance<?, ?> getVirtualModelInstance(EditionSchemeAction<?, ?> action) {
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
		if (getDeletionScheme() != null) {
			return getDeletionScheme().getEditionPattern();
		}
		return editionPatternType;
	}

	public void setEditionPatternType(EditionPattern editionPatternType) {
		this.editionPatternType = editionPatternType;
		if (getDeletionScheme() != null && getDeletionScheme().getEditionPattern() != editionPatternType) {
			setDeletionScheme(null);
		}
	}

	public String _getDeletionSchemeURI() {
		if (getDeletionScheme() != null) {
			return getDeletionScheme().getURI();
		}
		return _deletionSchemeURI;
	}

	public void _setDeletionSchemeURI(String uri) {
		if (getViewPointLibrary() != null) {
			deletionScheme = (DeletionScheme) getViewPointLibrary().getEditionScheme(uri);
		}
		_deletionSchemeURI = uri;
	}

	public DeletionScheme getDeletionScheme() {
		if (deletionScheme == null && _deletionSchemeURI != null && getViewPointLibrary() != null) {
			deletionScheme = (DeletionScheme) getViewPointLibrary().getEditionScheme(_deletionSchemeURI);
		}
		if (deletionScheme == null && getPatternRole() instanceof EditionPatternInstancePatternRole) {
			deletionScheme = ((EditionPatternInstancePatternRole) getPatternRole()).getEditionPattern().getDefaultDeletionScheme();
		}
		return deletionScheme;
	}

	public void setDeletionScheme(DeletionScheme deletionScheme) {
		this.deletionScheme = deletionScheme;
		if (deletionScheme != null) {
			_deletionSchemeURI = deletionScheme.getURI();
		}
	}

	private Vector<DeleteEditionPatternInstanceParameter> parameters = new Vector<DeleteEditionPatternInstance.DeleteEditionPatternInstanceParameter>();

	public Vector<DeleteEditionPatternInstanceParameter> getParameters() {
		updateParameters();
		return parameters;
	}

	public void setParameters(Vector<DeleteEditionPatternInstanceParameter> parameters) {
		this.parameters = parameters;
	}

	public void addToParameters(DeleteEditionPatternInstanceParameter parameter) {
		parameter.setAction(this);
		parameters.add(parameter);
	}

	public void removeFromParameters(DeleteEditionPatternInstanceParameter parameter) {
		parameter.setAction(null);
		parameters.remove(parameter);
	}

	public DeleteEditionPatternInstanceParameter getParameter(EditionSchemeParameter p) {
		for (DeleteEditionPatternInstanceParameter addEPParam : parameters) {
			if (addEPParam.getParam() == p) {
				return addEPParam;
			}
		}
		return null;
	}

	private void updateParameters() {
		Vector<DeleteEditionPatternInstanceParameter> parametersToRemove = new Vector<DeleteEditionPatternInstance.DeleteEditionPatternInstanceParameter>(
				parameters);
		if (getDeletionScheme() != null) {
			for (EditionSchemeParameter p : getDeletionScheme().getParameters()) {
				DeleteEditionPatternInstanceParameter existingParam = getParameter(p);
				if (existingParam != null) {
					parametersToRemove.remove(existingParam);
				} else {
					addToParameters(new DeleteEditionPatternInstanceParameter(p));
				}
			}
		}
		for (DeleteEditionPatternInstanceParameter removeThis : parametersToRemove) {
			removeFromParameters(removeThis);
		}
	}

	@Override
	public EditionPatternInstance performAction(EditionSchemeAction action) {
		logger.info("Perform performDeleteEditionPatternInstance " + action);
		VirtualModelInstance<?, ?> vmInstance = getVirtualModelInstance(action);
		logger.info("VirtualModelInstance: " + vmInstance);
		DeletionSchemeAction deletionSchemeAction = DeletionSchemeAction.actionType.makeNewEmbeddedAction(vmInstance, null, action);
		deletionSchemeAction.setVirtualModelInstance(vmInstance);
		deletionSchemeAction.setDeletionScheme(getDeletionScheme());
		for (DeleteEditionPatternInstanceParameter p : getParameters()) {
			EditionSchemeParameter param = p.getParam();
			Object value = p.evaluateParameterValue(action);
			logger.info("For parameter " + param + " value is " + value);
			if (value != null) {
				deletionSchemeAction.setParameterValue(p.getParam(), p.evaluateParameterValue(action));
			}
		}
		deletionSchemeAction.doAction();
		if (deletionSchemeAction.hasActionExecutionSucceeded()) {
			logger.info("Successfully performed performDeleteEditionPattern " + action);
			return deletionSchemeAction.getEditionPatternInstance();
		}
		return null;
	}

	public static class DeleteEditionPatternInstanceParameter extends EditionSchemeObject implements Bindable {

		@SuppressWarnings("unused")
		private static final Logger logger = Logger.getLogger(GraphicalElementSpecification.class.getPackage().getName());

		private DeleteEditionPatternInstance action;

		private EditionSchemeParameter param;
		private String paramName;
		private DataBinding<Object> value;

		// Use it only for deserialization
		public DeleteEditionPatternInstanceParameter(VirtualModel.VirtualModelBuilder builder) {
			super(builder);
		}

		public DeleteEditionPatternInstanceParameter(EditionSchemeParameter param) {
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

		public Object evaluateParameterValue(EditionSchemeAction<?, ?> action) {
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
		public VirtualModel<?> getVirtualModel() {
			if (getAction() != null) {
				return getAction().getVirtualModel();
			}
			return null;
		}

		public DeleteEditionPatternInstance getAction() {
			return action;
		}

		public void setAction(DeleteEditionPatternInstance action) {
			this.action = action;
		}

		public EditionSchemeParameter getParam() {
			if (param == null && paramName != null && getAction() != null && getAction().getDeletionScheme() != null) {
				param = getAction().getDeletionScheme().getParameter(paramName);
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

	public static class DeleteEditionPatternInstanceMustAddressADeletionScheme extends
			ValidationRule<DeleteEditionPatternInstanceMustAddressADeletionScheme, DeleteEditionPatternInstance> {
		public DeleteEditionPatternInstanceMustAddressADeletionScheme() {
			super(DeleteEditionPatternInstance.class, "delete_edition_pattern_action_must_address_a_valid_creation_scheme");
		}

		@Override
		public ValidationIssue<DeleteEditionPatternInstanceMustAddressADeletionScheme, DeleteEditionPatternInstance> applyValidation(
				DeleteEditionPatternInstance action) {
			if (action.getDeletionScheme() == null) {
				if (action.getEditionPatternType() == null) {
					return new ValidationError<DeleteEditionPatternInstanceMustAddressADeletionScheme, DeleteEditionPatternInstance>(this,
							action, "delete_edition_pattern_action_doesn't_define_any_edition_pattern");
				} else {
					return new ValidationError<DeleteEditionPatternInstanceMustAddressADeletionScheme, DeleteEditionPatternInstance>(this,
							action, "delete_edition_pattern_action_doesn't_define_any_creation_scheme");
				}
			}
			return null;
		}
	}

	public static class DeleteEditionPatternInstanceParametersMustBeValid extends
			ValidationRule<DeleteEditionPatternInstanceParametersMustBeValid, DeleteEditionPatternInstance> {

		public DeleteEditionPatternInstanceParametersMustBeValid() {
			super(DeleteEditionPatternInstance.class, "delete_edition_pattern_parameters_must_be_valid");
		}

		@Override
		public ValidationIssue<DeleteEditionPatternInstanceParametersMustBeValid, DeleteEditionPatternInstance> applyValidation(
				DeleteEditionPatternInstance action) {
			if (action.getDeletionScheme() != null) {
				Vector<ValidationIssue<DeleteEditionPatternInstanceParametersMustBeValid, DeleteEditionPatternInstance>> issues = new Vector<ValidationIssue<DeleteEditionPatternInstanceParametersMustBeValid, DeleteEditionPatternInstance>>();
				for (DeleteEditionPatternInstanceParameter p : action.getParameters()) {

					EditionSchemeParameter param = p.getParam();
					if (param.getIsRequired()) {
						if (p.getValue() == null || !p.getValue().isSet()) {
							DataBinding<String> uri = ((URIParameter) param).getBaseURI();
							if (param instanceof URIParameter && uri.isSet() && uri.isValid()) {
								// Special case, we will find a way to manage this
							} else {
								issues.add(new ValidationError<DeleteEditionPatternInstanceParametersMustBeValid, DeleteEditionPatternInstance>(this, action, "parameter_s_value_is_not_defined: " + param.getName()));
							}
						} else if (!p.getValue().isValid()) {
							logger.info("Binding NOT valid: " + p.getValue() + " for " + p.paramName + " object="
									+ p.action.getFullyQualifiedName() + ". Reason: " + p.getValue().invalidBindingReason());
							issues.add(new ValidationError<DeleteEditionPatternInstanceParametersMustBeValid, DeleteEditionPatternInstance>(this, action, "parameter_s_value_is_not_valid: " + param.getName()));
						}
					}
				}
				if (issues.size() == 0) {
					return null;
				} else if (issues.size() == 1) {
					return issues.firstElement();
				} else {
					return new CompoundIssue<DeleteEditionPatternInstance.DeleteEditionPatternInstanceParametersMustBeValid, DeleteEditionPatternInstance>(
							action, issues);
				}
			}
			return null;
		}
	}

	public static class VirtualModelInstanceBindingIsRequiredAndMustBeValid extends
			BindingIsRequiredAndMustBeValid<DeleteEditionPatternInstance> {
		public VirtualModelInstanceBindingIsRequiredAndMustBeValid() {
			super("'virtual_model_instance'_binding_is_not_valid", DeleteEditionPatternInstance.class);
		}

		@Override
		public DataBinding<VirtualModelInstance> getBinding(DeleteEditionPatternInstance object) {
			return object.getVirtualModelInstance();
		}

	}

}
