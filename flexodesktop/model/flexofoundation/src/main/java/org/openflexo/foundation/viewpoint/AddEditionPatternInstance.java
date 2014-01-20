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
import java.util.ArrayList;
import java.util.List;
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
import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Getter.Cardinality;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

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
@ModelEntity
@ImplementationClass(AddEditionPatternInstance.AddEditionPatternInstanceImpl.class)
@XMLElement
public interface AddEditionPatternInstance extends AssignableAction<VirtualModelModelSlot, EditionPatternInstance> {

	@PropertyIdentifier(type = DataBinding.class)
	public static final String VIRTUAL_MODEL_INSTANCE_KEY = "virtualModelInstance";
	@PropertyIdentifier(type = String.class)
	public static final String CREATION_SCHEME_URI_KEY = "creationSchemeURI";
	@PropertyIdentifier(type = List.class)
	public static final String PARAMETERS_KEY = "parameters";

	@Getter(value = VIRTUAL_MODEL_INSTANCE_KEY)
	@XMLAttribute
	public DataBinding<VirtualModelInstance> getVirtualModelInstance();

	@Setter(VIRTUAL_MODEL_INSTANCE_KEY)
	public void setVirtualModelInstance(DataBinding<VirtualModelInstance> virtualModelInstance);

	@Getter(value = CREATION_SCHEME_URI_KEY)
	@XMLAttribute
	public String _getCreationSchemeURI();

	@Setter(CREATION_SCHEME_URI_KEY)
	public void _setCreationSchemeURI(String creationSchemeURI);

	public CreationScheme getCreationScheme();

	public void setCreationScheme(CreationScheme creationScheme);

	@Getter(value = PARAMETERS_KEY, cardinality = Cardinality.LIST, inverse = AddEditionPatternInstanceParameter.ACTION_KEY)
	@XMLElement
	public List<AddEditionPatternInstanceParameter> getParameters();

	@Setter(PARAMETERS_KEY)
	public void setParameters(List<AddEditionPatternInstanceParameter> parameters);

	@Adder(PARAMETERS_KEY)
	public void addToParameters(AddEditionPatternInstanceParameter aParameter);

	@Remover(PARAMETERS_KEY)
	public void removeFromParameters(AddEditionPatternInstanceParameter aParameter);

	public EditionPattern getEditionPatternType();

	public void setEditionPatternType(EditionPattern editionPatternType);

	public static abstract class AddEditionPatternInstanceImpl extends AssignableActionImpl<VirtualModelModelSlot, EditionPatternInstance>
			implements AddEditionPatternInstance {

		static final Logger logger = Logger.getLogger(AddEditionPatternInstance.class.getPackage().getName());

		private EditionPattern editionPatternType;
		private CreationScheme creationScheme;
		private String _creationSchemeURI;

		public AddEditionPatternInstanceImpl() {
			super();
		}

		public VirtualModelInstance getVirtualModelInstance(EditionSchemeAction action) {
			try {
				// System.out.println("getVirtualModelInstance() with " + getVirtualModelInstance());
				// System.out.println("Valid=" + getVirtualModelInstance().isValid() + " " +
				// getVirtualModelInstance().invalidBindingReason());
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

		@Override
		public DataBinding<VirtualModelInstance> getVirtualModelInstance() {
			if (virtualModelInstance == null) {
				virtualModelInstance = new DataBinding<VirtualModelInstance>(this, VirtualModelInstance.class,
						DataBinding.BindingDefinitionType.GET);
				virtualModelInstance.setBindingName("virtualModelInstance");
			}
			return virtualModelInstance;
		}

		@Override
		public void setVirtualModelInstance(DataBinding<VirtualModelInstance> aVirtualModelInstance) {
			if (aVirtualModelInstance != null) {
				aVirtualModelInstance.setOwner(this);
				aVirtualModelInstance.setBindingName("virtualModelInstance");
				aVirtualModelInstance.setDeclaredType(VirtualModelInstance.class);
				aVirtualModelInstance.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
			}
			this.virtualModelInstance = aVirtualModelInstance;
		}

		@Override
		public EditionPattern getEditionPatternType() {
			if (getCreationScheme() != null) {
				return getCreationScheme().getEditionPattern();
			}
			return editionPatternType;
		}

		@Override
		public void setEditionPatternType(EditionPattern editionPatternType) {
			this.editionPatternType = editionPatternType;
			if (getCreationScheme() != null && getCreationScheme().getEditionPattern() != editionPatternType) {
				setCreationScheme(null);
			}
		}

		@Override
		public String _getCreationSchemeURI() {
			if (getCreationScheme() != null) {
				return getCreationScheme().getURI();
			}
			return _creationSchemeURI;
		}

		@Override
		public void _setCreationSchemeURI(String uri) {
			if (getViewPointLibrary() != null) {
				creationScheme = (CreationScheme) getViewPointLibrary().getEditionScheme(uri);
			}
			_creationSchemeURI = uri;
		}

		@Override
		public CreationScheme getCreationScheme() {
			if (creationScheme == null && _creationSchemeURI != null && getViewPointLibrary() != null) {
				creationScheme = (CreationScheme) getViewPointLibrary().getEditionScheme(_creationSchemeURI);
			}
			if (creationScheme == null && getPatternRole() instanceof EditionPatternInstancePatternRole) {
				creationScheme = ((EditionPatternInstancePatternRole) getPatternRole()).getCreationScheme();
			}
			return creationScheme;
		}

		@Override
		public void setCreationScheme(CreationScheme creationScheme) {
			this.creationScheme = creationScheme;
			if (creationScheme != null) {
				_creationSchemeURI = creationScheme.getURI();
			}
		}

		// private Vector<AddEditionPatternInstanceParameter> parameters = new Vector<AddEditionPatternInstanceParameter>();

		@Override
		public List<AddEditionPatternInstanceParameter> getParameters() {
			updateParameters();
			return (List<AddEditionPatternInstanceParameter>) performSuperGetter(PARAMETERS_KEY);
		}

		public AddEditionPatternInstanceParameter getParameter(EditionSchemeParameter p) {
			for (AddEditionPatternInstanceParameter addEPParam : getParameters()) {
				if (addEPParam.getParam() == p) {
					return addEPParam;
				}
			}
			return null;
		}

		private void updateParameters() {
			List<AddEditionPatternInstanceParameter> parametersToRemove = new ArrayList<AddEditionPatternInstanceParameter>(getParameters());
			if (getCreationScheme() != null) {
				for (EditionSchemeParameter p : getCreationScheme().getParameters()) {
					AddEditionPatternInstanceParameter existingParam = getParameter(p);
					if (existingParam != null) {
						parametersToRemove.remove(existingParam);
					} else {
						addToParameters(getVirtualModelFactory().newAddEditionPatternInstanceParameter(p));
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

		/*@Override
		public Type getAssignableType() {
			return EditionPatternInstanceType.getEditionPatternInstanceType(getEditionPatternType());
		}*/

		@Override
		public Type getAssignableType() {
			// NPE Protection
			ViewPoint vp = this.getViewPoint();
			if (vp != null) {
				return vp.getInstanceType(getEditionPatternType());
			} else {
				logger.warning("Adding FlexoConcept Instance in a null ViewPoint !");
				return null;
			}
		}

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
							AddEditionPatternInstanceImpl.logger.info("Binding NOT valid: " + p.getValue() + " for " + p.getName()
									+ " object=" + p.getAction().getStringRepresentation() + ". Reason: "
									+ p.getValue().invalidBindingReason());
							issues.add(new ValidationError(this, action, "parameter_s_value_is_not_valid: " + param.getName()));
						}
					}
				}
				if (issues.size() == 0) {
					return null;
				} else if (issues.size() == 1) {
					return issues.firstElement();
				} else {
					return new CompoundIssue<AddEditionPatternInstanceParametersMustBeValid, AddEditionPatternInstance>(action, issues);
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
