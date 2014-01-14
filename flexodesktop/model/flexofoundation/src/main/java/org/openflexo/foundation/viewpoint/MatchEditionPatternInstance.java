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
import java.util.Hashtable;
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
import org.openflexo.foundation.view.action.SynchronizationSchemeAction;
import org.openflexo.foundation.viewpoint.FMLRepresentationContext.FMLRepresentationOutput;
import org.openflexo.foundation.viewpoint.annotations.FIBPanel;

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
@FIBPanel("Fib/MatchEditionPatternInstancePanel.fib")
public class MatchEditionPatternInstance extends AssignableAction<VirtualModelModelSlot, EditionPatternInstance> {

	static final Logger logger = Logger.getLogger(MatchEditionPatternInstance.class.getPackage().getName());

	private EditionPattern editionPatternType;
	private CreationScheme creationScheme;
	private String _creationSchemeURI;
	private Vector<MatchingCriteria> matchingCriterias = new Vector<MatchingCriteria>();
	private Vector<CreateEditionPatternInstanceParameter> parameters = new Vector<CreateEditionPatternInstanceParameter>();

	public MatchEditionPatternInstance() {
		super();
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
			ValidationRule<MatchEditionPatternInstanceParametersMustBeValid, MatchEditionPatternInstance> {
		public MatchEditionPatternInstanceParametersMustBeValid() {
			super(MatchEditionPatternInstance.class, "match_edition_pattern_parameters_must_be_valid");
		}

		@Override
		public ValidationIssue<MatchEditionPatternInstanceParametersMustBeValid, MatchEditionPatternInstance> applyValidation(
				MatchEditionPatternInstance action) {
			if (action.getCreationScheme() != null) {
				Vector<ValidationIssue<MatchEditionPatternInstanceParametersMustBeValid, MatchEditionPatternInstance>> issues = new Vector<ValidationIssue<MatchEditionPatternInstanceParametersMustBeValid, MatchEditionPatternInstance>>();
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
									+ p.action.getStringRepresentation() + ". Reason: " + p.getValue().invalidBindingReason());
							issues.add(new ValidationError(this, action, "parameter_s_value_is_not_valid: " + p.getParam().getName()));
						}
					}
				}
				if (issues.size() == 0) {
					return null;
				} else if (issues.size() == 1) {
					return issues.firstElement();
				} else {
					return new CompoundIssue<MatchEditionPatternInstance.MatchEditionPatternInstanceParametersMustBeValid, MatchEditionPatternInstance>(
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
		public DataBinding<VirtualModelInstance> getBinding(MatchEditionPatternInstance object) {
			return object.getVirtualModelInstance();
		}

	}

}
