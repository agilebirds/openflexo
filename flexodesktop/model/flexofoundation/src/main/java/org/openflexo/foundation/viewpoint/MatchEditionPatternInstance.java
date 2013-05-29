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
import java.util.Hashtable;
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
import org.openflexo.foundation.view.action.SynchronizationSchemeAction;
import org.openflexo.foundation.view.diagram.viewpoint.GraphicalElementSpecification;
import org.openflexo.foundation.viewpoint.ViewPointObject.FMLRepresentationContext.FMLRepresentationOutput;

/**
 * This action is used to perform synchronization regarding an {@link EditionPatternInstance} in a given {@link VirtualModelInstance}.<br>
 * The matching is performed on some pattern roles, with some values retrieved from an expression.<br>
 * If target {@link EditionPatternInstance} could not been looked up, then a new {@link EditionPatternInstance} is created using supplied
 * {@link CreationScheme} and some parameters
 * 
 * @author sylvain
 * 
 * @param <M>
 * @param <MM>
 */
public class MatchEditionPatternInstance<M extends FlexoModel<M, MM>, MM extends FlexoMetaModel<MM>> extends
		AssignableAction<M, MM, EditionPatternInstance> {

	private static final Logger logger = Logger.getLogger(MatchEditionPatternInstance.class.getPackage().getName());

	private EditionPattern editionPatternType;
	private CreationScheme creationScheme;
	private String _creationSchemeURI;
	private Vector<MatchingCriteria> matchingCriterias = new Vector<MatchingCriteria>();
	private Vector<CreateEditionPatternInstanceParameter> parameters = new Vector<CreateEditionPatternInstanceParameter>();

	public MatchEditionPatternInstance(VirtualModel.VirtualModelBuilder builder) {
		super(builder);
	}

	@Override
	public String getFMLRepresentation(FMLRepresentationContext context) {
		FMLRepresentationOutput out = new FMLRepresentationOutput(context);
		if (getAssignation().isSet()) {
			out.append(getAssignation().toString() + " = (", context);
		}
		out.append(getClass().getSimpleName() + " as " + getEditionPatternType().getName() + " "
				+ getMatchingCriteriasFMLRepresentation(context) + " using " + getCreationScheme().getEditionPattern().getName() + ":"
				+ getCreationScheme().getName() + "(" + getCreationSchemeParametersFMLRepresentation(context) + ")", context);
		if (getAssignation().isSet()) {
			out.append(")", context);
		}
		return out.toString();
	}

	protected String getMatchingCriteriasFMLRepresentation(FMLRepresentationContext context) {
		if (matchingCriterias.size() > 0) {
			StringBuffer sb = new StringBuffer();
			sb.append("match ");
			if (matchingCriterias.size() > 1) {
				sb.append("(");
			}
			for (MatchingCriteria mc : matchingCriterias) {
				sb.append(mc.getPatternRole().getName() + "=" + mc.getValue().toString() + ";");
			}
			if (matchingCriterias.size() > 1) {
				sb.append(")");
			}
			return sb.toString();
		}
		return null;
	}

	protected String getCreationSchemeParametersFMLRepresentation(FMLRepresentationContext context) {
		if (getParameters().size() > 0) {
			StringBuffer sb = new StringBuffer();
			boolean isFirst = true;
			for (CreateEditionPatternInstanceParameter p : getParameters()) {
				sb.append((isFirst ? "" : ",") + p.getValue().toString());
				isFirst = false;
			}
			return sb.toString();
		}
		return null;
	}

	@Override
	public EditionActionType getEditionActionType() {
		return EditionActionType.MatchEditionPatternInstance;
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
			virtualModelInstance.setMandatory(true);
		}
		return virtualModelInstance;
	}

	public void setVirtualModelInstance(DataBinding<VirtualModelInstance> aVirtualModelInstance) {
		if (aVirtualModelInstance != null) {
			aVirtualModelInstance.setOwner(this);
			aVirtualModelInstance.setBindingName("virtualModelInstance");
			aVirtualModelInstance.setDeclaredType(VirtualModelInstance.class);
			aVirtualModelInstance.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
			aVirtualModelInstance.setMandatory(true);
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
		if (getPatternRole() instanceof EditionPatternInstancePatternRole
				&& ((EditionPatternInstancePatternRole) getPatternRole()).getCreationScheme() != null) {
			return ((EditionPatternInstancePatternRole) getPatternRole()).getCreationScheme();
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

	public Vector<CreateEditionPatternInstanceParameter> getParameters() {
		updateParameters();
		return parameters;
	}

	public void setParameters(Vector<CreateEditionPatternInstanceParameter> parameters) {
		this.parameters = parameters;
	}

	public void addToParameters(CreateEditionPatternInstanceParameter parameter) {
		parameter.setAction(this);
		parameters.add(parameter);
	}

	public void removeFromParameters(CreateEditionPatternInstanceParameter parameter) {
		parameter.setAction(null);
		parameters.remove(parameter);
	}

	public CreateEditionPatternInstanceParameter getParameter(EditionSchemeParameter p) {
		for (CreateEditionPatternInstanceParameter addEPParam : parameters) {
			if (addEPParam.getParam() == p) {
				return addEPParam;
			}
		}
		return null;
	}

	private void updateParameters() {
		Vector<CreateEditionPatternInstanceParameter> parametersToRemove = new Vector<CreateEditionPatternInstanceParameter>(parameters);
		if (getCreationScheme() != null) {
			for (EditionSchemeParameter p : getCreationScheme().getParameters()) {
				CreateEditionPatternInstanceParameter existingParam = getParameter(p);
				if (existingParam != null) {
					parametersToRemove.remove(existingParam);
				} else {
					addToParameters(new CreateEditionPatternInstanceParameter(p));
				}
			}
		}
		for (CreateEditionPatternInstanceParameter removeThis : parametersToRemove) {
			removeFromParameters(removeThis);
		}
	}

	public Vector<MatchingCriteria> getMatchingCriterias() {
		updateMatchingCriterias();
		return matchingCriterias;
	}

	public void setMatchingCriterias(Vector<MatchingCriteria> matchingCriterias) {
		this.matchingCriterias = matchingCriterias;
	}

	public void addToMatchingCriterias(MatchingCriteria matchingCriteria) {
		matchingCriteria.setAction(this);
		matchingCriterias.add(matchingCriteria);
	}

	public void removeFromMatchingCriterias(MatchingCriteria matchingCriteria) {
		matchingCriteria.setAction(null);
		matchingCriterias.remove(matchingCriteria);
	}

	public MatchingCriteria getMatchingCriteria(PatternRole pr) {
		for (MatchingCriteria mc : matchingCriterias) {
			if (mc.getPatternRole() == pr) {
				return mc;
			}
		}
		return null;
	}

	private void updateMatchingCriterias() {
		Vector<MatchingCriteria> criteriasToRemove = new Vector<MatchingCriteria>(matchingCriterias);
		if (getEditionPatternType() != null) {
			for (PatternRole pr : getEditionPatternType().getPatternRoles()) {
				MatchingCriteria existingCriteria = getMatchingCriteria(pr);
				if (existingCriteria != null) {
					criteriasToRemove.remove(existingCriteria);
				} else {
					addToMatchingCriterias(new MatchingCriteria(pr));
				}
			}
		}
		for (MatchingCriteria removeThis : criteriasToRemove) {
			removeFromMatchingCriterias(removeThis);
		}
	}

	@Override
	public EditionPatternInstance performAction(EditionSchemeAction action) {
		logger.info("Perform perform MatchEditionPatternInstance " + action);
		VirtualModelInstance vmInstance = getVirtualModelInstance(action);
		logger.info("VirtualModelInstance: " + vmInstance);
		Hashtable<PatternRole, Object> criterias = new Hashtable<PatternRole, Object>();
		for (MatchingCriteria mc : getMatchingCriterias()) {
			Object value = mc.evaluateCriteriaValue(action);
			if (value != null) {
				criterias.put(mc.getPatternRole(), value);
			}
			System.out.println("Pour " + mc.getPatternRole().getPatternRoleName() + " value is " + value);
		}
		logger.info("On s'arrete pour regarder ");
		EditionPatternInstance matchingEditionPatternInstance = ((SynchronizationSchemeAction) action).matchEditionPatternInstance(
				getEditionPatternType(), criterias);

		if (matchingEditionPatternInstance != null) {
			// A matching EditionPatternInstance was found
			((SynchronizationSchemeAction) action).foundMatchingEditionPatternInstance(matchingEditionPatternInstance);
		} else {

			CreationSchemeAction creationSchemeAction = CreationSchemeAction.actionType.makeNewEmbeddedAction(vmInstance, null, action);
			creationSchemeAction.setVirtualModelInstance(vmInstance);
			creationSchemeAction.setCreationScheme(getCreationScheme());
			for (CreateEditionPatternInstanceParameter p : getParameters()) {
				EditionSchemeParameter param = p.getParam();
				Object value = p.evaluateParameterValue(action);
				logger.info("For parameter " + param + " value is " + value);
				if (value != null) {
					creationSchemeAction.setParameterValue(p.getParam(), p.evaluateParameterValue(action));
				}
			}
			logger.info(">> Creating a new EPI in " + vmInstance);
			creationSchemeAction.doAction();
			if (creationSchemeAction.hasActionExecutionSucceeded()) {
				logger.info("Successfully performed performAddEditionPattern " + action);
				matchingEditionPatternInstance = creationSchemeAction.getEditionPatternInstance();
				((SynchronizationSchemeAction) action).newEditionPatternInstance(matchingEditionPatternInstance);
			} else {
				logger.warning("Could not create EditionPatternInstance for " + action);
			}
		}
		return matchingEditionPatternInstance;
	}

	public static class MatchingCriteria extends EditionSchemeObject implements Bindable {

		@SuppressWarnings("unused")
		private static final Logger logger = Logger.getLogger(GraphicalElementSpecification.class.getPackage().getName());

		private MatchEditionPatternInstance action;

		private PatternRole patternRole;
		private String patternRoleName;
		private DataBinding<Object> value;

		// Use it only for deserialization
		public MatchingCriteria(VirtualModel.VirtualModelBuilder builder) {
			super(builder);
		}

		public MatchingCriteria(PatternRole patternRole) {
			super(null);
			this.patternRole = patternRole;
		}

		@Override
		public EditionPattern getEditionPattern() {
			if (getAction() != null) {
				return getAction().getEditionPattern();
			}
			return null;
		}

		@Override
		public EditionScheme getEditionScheme() {
			if (getAction() != null) {
				return getAction().getEditionScheme();
			}
			return null;
		}

		public DataBinding<Object> getValue() {
			if (value == null) {
				value = new DataBinding<Object>(this, getPatternRole() != null ? getPatternRole().getType() : Object.class,
						DataBinding.BindingDefinitionType.GET);
				value.setBindingName(getPatternRole() != null ? getPatternRole().getName() : "param");
			}
			return value;
		}

		public void setValue(DataBinding<Object> value) {
			if (value != null) {
				value.setOwner(this);
				value.setBindingName(getPatternRole() != null ? getPatternRole().getName() : "param");
				value.setDeclaredType(getPatternRole() != null ? getPatternRole().getType() : Object.class);
				value.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
			}
			this.value = value;
		}

		public Object evaluateCriteriaValue(EditionSchemeAction action) {
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

		public MatchEditionPatternInstance getAction() {
			return action;
		}

		public void setAction(MatchEditionPatternInstance action) {
			this.action = action;
		}

		public PatternRole getPatternRole() {
			if (patternRole == null && patternRoleName != null && getAction() != null && getAction().getEditionPatternType() != null) {
				patternRole = getAction().getEditionPatternType().getPatternRole(patternRoleName);
			}
			return patternRole;
		}

		public String _getPatternRoleName() {
			if (patternRole != null) {
				return patternRole.getName();
			}
			return patternRoleName;
		}

		public void _setPatternRoleName(String patternRoleName) {
			this.patternRoleName = patternRoleName;
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

	public static class CreateEditionPatternInstanceParameter extends EditionSchemeObject implements Bindable {

		@SuppressWarnings("unused")
		private static final Logger logger = Logger.getLogger(GraphicalElementSpecification.class.getPackage().getName());

		private MatchEditionPatternInstance action;

		private EditionSchemeParameter param;
		private String paramName;
		private DataBinding<Object> value;

		// Use it only for deserialization
		public CreateEditionPatternInstanceParameter(VirtualModel.VirtualModelBuilder builder) {
			super(builder);
		}

		public CreateEditionPatternInstanceParameter(EditionSchemeParameter param) {
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

		public MatchEditionPatternInstance getAction() {
			return action;
		}

		public void setAction(MatchEditionPatternInstance action) {
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

	public static class MatchEditionPatternInstanceMustAddressACreationScheme extends
			ValidationRule<MatchEditionPatternInstanceMustAddressACreationScheme, MatchEditionPatternInstance> {
		public MatchEditionPatternInstanceMustAddressACreationScheme() {
			super(MatchEditionPatternInstance.class, "match_edition_pattern_action_must_address_a_valid_creation_scheme");
		}

		@Override
		public ValidationIssue<MatchEditionPatternInstanceMustAddressACreationScheme, MatchEditionPatternInstance> applyValidation(
				MatchEditionPatternInstance action) {
			if (action.getCreationScheme() == null) {
				if (action.getEditionPatternType() == null) {
					return new ValidationError<MatchEditionPatternInstanceMustAddressACreationScheme, MatchEditionPatternInstance>(this,
							action, "match_edition_pattern_action_doesn't_define_any_edition_pattern");
				} else {
					return new ValidationError<MatchEditionPatternInstanceMustAddressACreationScheme, MatchEditionPatternInstance>(this,
							action, "match_edition_pattern_action_doesn't_define_any_creation_scheme");
				}
			}
			return null;
		}
	}

	public static class MatchEditionPatternInstanceParametersMustBeValid extends
			ValidationRule<MatchEditionPatternInstanceParametersMustBeValid, MatchEditionPatternInstance<?, ?>> {
		public MatchEditionPatternInstanceParametersMustBeValid() {
			super(MatchEditionPatternInstance.class, "match_edition_pattern_parameters_must_be_valid");
		}

		@Override
		public ValidationIssue<MatchEditionPatternInstanceParametersMustBeValid, MatchEditionPatternInstance<?, ?>> applyValidation(
				MatchEditionPatternInstance<?, ?> action) {
			if (action.getCreationScheme() != null) {
				Vector<ValidationIssue<MatchEditionPatternInstanceParametersMustBeValid, MatchEditionPatternInstance<?, ?>>> issues = new Vector<ValidationIssue<MatchEditionPatternInstanceParametersMustBeValid, MatchEditionPatternInstance<?, ?>>>();
				for (CreateEditionPatternInstanceParameter p : action.getParameters()) {
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
					return new CompoundIssue<MatchEditionPatternInstance.MatchEditionPatternInstanceParametersMustBeValid, MatchEditionPatternInstance<?, ?>>(
							action, issues);
				}
			}
			return null;
		}
	}

	public static class VirtualModelInstanceBindingIsRequiredAndMustBeValid extends
			BindingIsRequiredAndMustBeValid<MatchEditionPatternInstance> {
		public VirtualModelInstanceBindingIsRequiredAndMustBeValid() {
			super("'virtual_model_instance'_binding_is_not_valid", MatchEditionPatternInstance.class);
		}

		@Override
		public DataBinding<View> getBinding(MatchEditionPatternInstance object) {
			return object.getVirtualModelInstance();
		}

	}

}
