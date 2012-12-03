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
package org.openflexo.foundation.view.action;

import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.binding.AbstractBinding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.ontology.EditionPatternInstance;
import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.ontology.IFlexoOntologyIndividual;
import org.openflexo.foundation.ontology.IFlexoOntologyConcept;
import org.openflexo.foundation.ontology.IFlexoOntologyStructuralProperty;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.view.diagram.model.View;
import org.openflexo.foundation.view.diagram.model.ViewElement;
import org.openflexo.foundation.view.diagram.viewpoint.GraphicalElementPatternRole;
import org.openflexo.foundation.viewpoint.AssignableAction;
import org.openflexo.foundation.viewpoint.EditionAction;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.EditionScheme;
import org.openflexo.foundation.viewpoint.EditionSchemeParameter;
import org.openflexo.foundation.viewpoint.ListParameter;
import org.openflexo.foundation.viewpoint.URIParameter;
import org.openflexo.foundation.viewpoint.binding.EditionPatternPathElement;
import org.openflexo.foundation.viewpoint.binding.EditionSchemeParameterListPathElement;
import org.openflexo.foundation.viewpoint.binding.EditionSchemeParameterPathElement;
import org.openflexo.foundation.viewpoint.binding.GraphicalElementPathElement.ViewPathElement;
import org.openflexo.foundation.viewpoint.binding.PatternRolePathElement;
import org.openflexo.toolbox.StringUtils;

/**
 * This abstract class is the root class for all actions which can be performed at conceptual or design level, generally on a view tool
 * (such as a diagram).<br>
 * An {@link EditionSchemeAction} represents the execution of an {@link EditionScheme}.<br>
 * To be used and executed on Openflexo platform, it is wrapped in a {@link FlexoAction}.
 * 
 * @author sylvain
 * 
 * @param <A>
 */
public abstract class EditionSchemeAction<A extends EditionSchemeAction<A>> extends FlexoAction<A, FlexoModelObject, FlexoModelObject>
		implements BindingEvaluationContext /*, BindingPathElement<Object>*/{

	private static final Logger logger = Logger.getLogger(EditionSchemeAction.class.getPackage().getName());

	protected Hashtable<String, Object> variables;
	protected Hashtable<EditionSchemeParameter, Object> parameterValues;
	protected Hashtable<ListParameter, List> parameterListValues;

	public boolean escapeParameterRetrievingWhenValid = true;

	public EditionSchemeAction(FlexoActionType<A, FlexoModelObject, FlexoModelObject> actionType, FlexoModelObject focusedObject,
			Vector<FlexoModelObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
		variables = new Hashtable<String, Object>();
		parameterValues = new Hashtable<EditionSchemeParameter, Object>();
		parameterListValues = new Hashtable<ListParameter, List>();
	}

	/**
	 * Compute and store default parameters, and return a flag indicating if all parameters declared as "mandatory" could be successfully
	 * filled
	 * 
	 * @return
	 */
	// TODO: we must order this if dependancies are not resolved using basic sequence
	public boolean retrieveDefaultParameters() {
		boolean returned = true;
		EditionScheme editionScheme = getEditionScheme();
		for (final EditionSchemeParameter parameter : editionScheme.getParameters()) {
			Object defaultValue = parameter.getDefaultValue(this);
			if (defaultValue != null && !(parameter instanceof URIParameter)) {
				parameterValues.put(parameter, defaultValue);
			}
			if (parameter instanceof ListParameter) {
				List list = (List) ((ListParameter) parameter).getList(this);
				parameterListValues.put((ListParameter) parameter, list);
			}
			if (!parameter.isValid(this, defaultValue)) {
				logger.info("Parameter " + parameter + " is not valid for value " + defaultValue);
				returned = false;
			}
		}
		return returned;
	}

	public FlexoProject getProject() {
		if (getFocusedObject() != null) {
			return getFocusedObject().getProject();
		}
		return null;
	}

	public EditionPattern getEditionPattern() {
		if (getEditionScheme() != null) {
			return getEditionScheme().getEditionPattern();
		}
		return null;
	}

	/**
	 * Calling this method will register a new variable in the run-time context provided by this {@link EditionSchemeAction} instance in the
	 * context of its implementation of {@link BindingEvaluationContext}.<br>
	 * Variable is initialized with supplied name and value
	 * 
	 * @param variableName
	 * @param value
	 */
	public void declareVariable(String variableName, Object value) {
		variables.put(variableName, value);
	}

	/**
	 * Calling this method will dereference variable identified by supplied name
	 * 
	 * @param variableName
	 */
	public void dereferenceVariable(String variableName) {
		variables.remove(variableName);
	}

	/*public String getStringParameter(String parameterName) {
		System.out.println("OK, on me demande le parametre " + parameterName + ", je retourne " + parameterValues.get(parameterName));
		return (String) parameterValues.get(parameterName);
	}

	public String getURIParameter(String parameterName) {
		System.out.println("OK, on me demande l'uri " + parameterName + ", je retourned " + parameterValues.get(parameterName));
		return (String) parameterValues.get(parameterName);
	}*/

	/*public Hashtable<EditionSchemeParameter, Object> getParameterValues() {
		return parameterValues;
	}*/

	public Object getParameterValue(EditionSchemeParameter parameter) {
		/*System.out.println("On me demande la valeur du parametre " + parameter.getName() + " a priori c'est "
				+ parameterValues.get(parameter));*/
		if (parameter instanceof URIParameter) {
			if (parameterValues.get(parameter) == null || parameterValues.get(parameter) instanceof String
					&& StringUtils.isEmpty((String) parameterValues.get(parameter))) {
				return ((URIParameter) parameter).getDefaultValue(this);
			}
		}
		return parameterValues.get(parameter);
	}

	public void setParameterValue(EditionSchemeParameter parameter, Object value) {
		// System.out.println("setParameterValue " + value + " for parameter " + parameter.getName());
		parameterValues.put(parameter, value);
		/*for (EditionSchemeParameter p : getEditionScheme().getParameters()) {
			if (p instanceof URIParameter) {
				// System.out.println("Hop, je recalcule l'uri, ici");
			}
		}*/
	}

	public List getParameterListValue(ListParameter parameter) {
		/*System.out.println("On me demande la valeur du parametre " + parameter.getName() + " a priori c'est "
				+ parameterValues.get(parameter));*/
		return parameterListValues.get(parameter);
	}

	public abstract EditionScheme getEditionScheme();

	public abstract EditionPatternInstance getEditionPatternInstance();

	public View getView() {
		return retrieveOEShema();
	}

	public abstract View retrieveOEShema();

	/**
	 * This is the internal code performing execution of the control graph of {@link EditionAction} defined to be the execution control
	 * graph of related {@link EditionScheme}<br>
	 * Recursively invoke {@link #performAction(EditionAction, Hashtable)}
	 */
	protected void applyEditionActions() {
		Hashtable<EditionAction, Object> performedActions = new Hashtable<EditionAction, Object>();

		// Perform actions
		for (EditionAction action : getEditionScheme().getActions()) {
			if (action.evaluateCondition(this)) {
				performAction(action, performedActions);
			}
			// Otherwise, we just ignore the action
		}

		// Finalize actions
		for (EditionAction action : performedActions.keySet()) {
			action.finalizePerformAction(this, performedActions.get(action));
			}

	}

	/**
	 * This is the internal code performing execution of a single {@link EditionAction} defined to be part of the execution control graph of
	 * related {@link EditionScheme}<br>
	 */
	protected Object performAction(EditionAction action, Hashtable<EditionAction, Object> performedActions) {
		Object assignedObject = action.performAction(this);

		if (assignedObject != null) {
			performedActions.put(action, assignedObject);
		}

		if (assignedObject != null && action instanceof AssignableAction) {
			AssignableAction assignableAction = (AssignableAction) action;
			if (assignableAction.getAssignation().isSet() && assignableAction.getAssignation().isValid()) {
				try {
					assignableAction.getAssignation().setBindingValue(assignedObject, this);
				} catch (Exception e) {
					logger.warning("Unexpected assignation issue, " + assignableAction.getAssignation() + " object=" + assignedObject);
				}
			}
			if (assignableAction.getPatternRole() != null && assignedObject instanceof FlexoModelObject) {
				getEditionPatternInstance().setObjectForPatternRole((FlexoModelObject) assignedObject, assignableAction.getPatternRole());
			}
		}

		return assignedObject;
	}

	@Override
	public Object getValue(BindingVariable variable) {
		if (variable instanceof EditionSchemeParameterListPathElement) {
			return this;
		} else if (variable instanceof EditionSchemeParameterPathElement) {
			return getParameterValue(((EditionSchemeParameterPathElement) variable).getParameter());
		} else if (variable instanceof ViewPathElement) {
			if (variable.getVariableName().equals(EditionScheme.TOP_LEVEL)) {
				return retrieveOEShema();
			}
		} else if (variable instanceof PatternRolePathElement) {
			return getEditionPatternInstance().getPatternActor(((PatternRolePathElement) variable).getPatternRole());
		} else if (variable instanceof EditionPatternPathElement) {
			if (variable.getVariableName().equals(EditionScheme.THIS)) {
				return getEditionPatternInstance();
			}
		}

		if (variables.get(variable.getVariableName()) != null) {
			return variables.get(variable.getVariableName());
		}

		logger.warning("Unexpected variable requested in EditionSchemeAction " + variable);
		return null;
	}

	public GraphicalRepresentation<? extends ViewElement> getOverridingGraphicalRepresentation(GraphicalElementPatternRole patternRole) {
		// return overridenGraphicalRepresentations.get(patternRole);
		// TODO temporary desactivate overriden GR
		return null;
	}

}
