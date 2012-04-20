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

import java.util.List;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingDefinition.BindingDefinitionType;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.binding.ViewPointDataBinding;
import org.openflexo.foundation.viewpoint.inspector.InspectorBindingAttribute;

public abstract class EditionAction<R extends PatternRole> extends ViewPointObject {

	private static final Logger logger = Logger.getLogger(EditionAction.class.getPackage().getName());

	/*public static final String CONTAINER = "container";
	public static final String CONTAINER_OF_CONTAINER = "container.container";
	public static final String FROM_TARGET = "fromTarget";
	public static final String TO_TARGET = "toTarget";
	public static final String CONTAINER_CONCEPT = "container.concept";
	public static final String CONTAINER_OF_CONTAINER_CONCEPT = "container.container.concept";
	public static final String FROM_TARGET_CONCEPT = "fromTarget.concept";
	public static final String TO_TARGET_CONCEPT = "toTarget.concept";*/

	public static enum EditionActionType {
		AddClass,
		AddIndividual,
		AddObjectPropertyStatement,
		AddDataPropertyStatement,
		AddIsAStatement,
		AddRestrictionStatement,
		AddConnector,
		AddShape,
		AddDiagram,
		AddEditionPattern,
		DeclarePatternRole,
		GraphicalAction,
		GoToObject
	}

	private EditionScheme _scheme;
	private String description;
	private String patternRole;

	public EditionAction() {
	}

	public abstract EditionActionType getEditionActionType();

	public void setScheme(EditionScheme scheme) {
		_scheme = scheme;
	}

	public EditionScheme getScheme() {
		return _scheme;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public ViewPoint getCalc() {
		if (getScheme() != null) {
			return getScheme().getCalc();
		}
		return null;
	}

	public String _getPatternRoleName() {
		return patternRole;
	}

	public void _setPatternRoleName(String patternRole) {
		this.patternRole = patternRole;
	}

	public boolean evaluateCondition(EditionSchemeAction action) {
		if (getConditional().isValid()) {
			return (Boolean) getConditional().getBindingValue(action);
		}
		return true;
	}

	public EditionPattern getEditionPattern() {
		if (getScheme() == null) {
			return null;
		}
		return getScheme().getEditionPattern();
	}

	public R getPatternRole() {
		if (getEditionPattern() == null) {
			return null;
		}
		return (R) getEditionPattern().getPatternRole(_getPatternRoleName());
	}

	public void setPatternRole(R patternRole) {
		_setPatternRoleName((patternRole != null) && (patternRole.getPatternRoleName() != null) ? patternRole.getPatternRoleName() : null);
		// updatePatternRoleType();
	}

	public abstract List<R> getAvailablePatternRoles();

	@Deprecated
	protected final void updatePatternRoleType() {
	}

	public int getIndex() {
		return getScheme().getActions().indexOf(this);
	}

	/*protected OntologyObject retrieveOntologyObject(String identifier, EditionSchemeAction action)
	{
		if (identifier == null) {
			return null;
		}

		if (identifier != null) {
			Object value = action.getParameterValues().get(identifier);
			if (value instanceof OntologyObject) {
				return (OntologyObject)value;
			}
		}

		if (action.getEditionPatternInstance() != null) {
			FlexoModelObject object
			= action.getEditionPatternInstance().getPatternActor(identifier);
			if (object instanceof OntologyObject) {
				return (OntologyObject)object;
			} else {
				logger.warning("Unexpected "+object);
			}
		}

		if (action instanceof LinkSchemeAction) {
			
			LinkSchemeAction linkSchemeAction = (LinkSchemeAction)action;

			ViewShape fromContext = linkSchemeAction.getFromShape();
			ViewShape toContext = linkSchemeAction.getToShape();

			logger.info("Please reimplement this !");
			return null;
			
			if (identifier.equals(FROM_TARGET) && (fromContext.getLinkedConcept() instanceof OntologyObject)) {
				return (OntologyObject)fromContext.getLinkedConcept();
			}
			if (identifier.equals(TO_TARGET) && (toContext.getLinkedConcept() instanceof OntologyObject)) {
				return (OntologyObject)toContext.getLinkedConcept();
			}

			if (identifier.equals(FROM_TARGET_CONCEPT) && (fromContext.getLinkedConcept() instanceof OntologyObject)) {
				return (OntologyObject)linkSchemeAction.getFromShape().getLinkedConcept();
			}
			if (identifier.equals(TO_TARGET_CONCEPT) && (toContext.getLinkedConcept() instanceof OntologyObject)) {
				return (OntologyObject)linkSchemeAction.getToShape().getLinkedConcept();
			}
			
		}
		
		else if (action instanceof DropSchemeAction) {
			
			DropSchemeAction dropSchemeAction = (DropSchemeAction)action;

			logger.info("Please reimplement this !");
			return null;

			if (identifier.equals(CONTAINER) && (dropSchemeAction.getParent() instanceof ViewShape)) {
				ViewShape container = (ViewShape)dropSchemeAction.getParent();
				return (OntologyObject)container.getLinkedConcept();
			}

			if (identifier.equals(CONTAINER_OF_CONTAINER) && (dropSchemeAction.getParent().getParent() instanceof ViewShape)) {
				ViewShape container = (ViewShape)dropSchemeAction.getParent().getParent();
				return (OntologyObject)container.getLinkedConcept();
			}

			if (identifier.equals(CONTAINER_CONCEPT) 
					&& (dropSchemeAction.getParent() instanceof ViewShape)
					&& (((ViewShape)dropSchemeAction.getParent()).getLinkedConcept() instanceof OntologyObject)) {
				return (OntologyObject)((ViewShape)dropSchemeAction.getParent()).getLinkedConcept();
			}

			if (identifier.equals(CONTAINER_OF_CONTAINER_CONCEPT) 
					&& (dropSchemeAction.getParent().getParent() instanceof ViewShape)
					&& (((ViewShape)dropSchemeAction.getParent().getParent()).getLinkedConcept() instanceof OntologyObject)) {
				return (OntologyObject)((ViewShape)dropSchemeAction.getParent().getParent()).getLinkedConcept();
			}
		}
		
		return null;
	}*/

	/*protected FlexoModelObject retrieveFlexoModelObject(String identifier, EditionSchemeAction action)
	{
		if (identifier == null) {
			return null;
		}

		if (identifier != null) {
			Object value = action.getParameterValues().get(identifier);
			if (value instanceof FlexoModelObject) {
				return (FlexoModelObject)value;
			}
		}

		if (action.getEditionPatternInstance() != null) {
			FlexoModelObject object
			= action.getEditionPatternInstance().getPatternActor(identifier);
			if (object != null) {
				return object;
			}
		}

		if (action instanceof LinkSchemeAction) {
			
			LinkSchemeAction linkSchemeAction = (LinkSchemeAction)action;

			if (identifier.equals(FROM_TARGET)) {
				return linkSchemeAction.getFromShape();
			}
			if (identifier.equals(TO_TARGET)) {
				return linkSchemeAction.getToShape();
			}

		}
		
		else if (action instanceof DropSchemeAction) {
			
			DropSchemeAction dropSchemeAction = (DropSchemeAction)action;

			if (identifier.equals(CONTAINER) && (dropSchemeAction.getParent() instanceof ViewShape)) {
				return dropSchemeAction.getParent();
				}

			if (identifier.equals(CONTAINER_OF_CONTAINER) && (dropSchemeAction.getParent().getParent() instanceof ViewShape)) {
				return dropSchemeAction.getParent().getParent();
			}
		}
		
		return null;
	}*/

	/*protected String generateStringForPatternRole (PatternRole patternRole, EditionSchemeAction action)
	{
		if (action.getEditionPatternInstance() != null) {
			FlexoModelObject object
			= action.getEditionPatternInstance().getPatternActor(patternRole);
			if (object != null) {
				return object.getName();
			}
		}
		return "???";
		
	}*/

	/*protected String generateStringFromIdentifier (String identifier, EditionSchemeAction action)
	{
		if (identifier == null) {
			return null;
		}

		for (PatternRole pr : getEditionPattern().getPatternRoles()) {
			if (pr.getPatternRoleName().equals(identifier)) {
				// This role is the one to be used for generating name
				return generateStringForPatternRole(pr,action);
			}
		}
	
		if (identifier != null) {
			Object value = action.getParameterValues().get(identifier);
			if (value != null) {
				return value.toString();
			}
		}
		
		return null;
	}*/

	/*protected Object retrieveObject(String identifier, EditionSchemeAction action)
	{
		if (identifier == null) {
			return null;
		}

		if (identifier != null) {
			Object value = action.getParameterValues().get(identifier);
			if (value != null) {
				return value;
			}
		}

		if (action.getEditionPatternInstance() != null) {
			FlexoModelObject object
			= action.getEditionPatternInstance().getPatternActor(identifier);
			if (object != null) {
				return object;
			}
		}

		if (action instanceof LinkSchemeAction) {
			
			LinkSchemeAction linkSchemeAction = (LinkSchemeAction)action;

			if (identifier.equals(FROM_TARGET)) {
				return linkSchemeAction.getFromShape();
			}
			if (identifier.equals(TO_TARGET)) {
				return linkSchemeAction.getToShape();
			}
			
			logger.info("Please reimplement this !");
			return null;


		}
		
		else if (action instanceof DropSchemeAction) {
			
			DropSchemeAction dropSchemeAction = (DropSchemeAction)action;

			if (identifier.equals(CONTAINER) 
					&& (dropSchemeAction.getParent() instanceof ViewShape)) {
				return dropSchemeAction.getParent();
			}

			if (identifier.equals(CONTAINER_OF_CONTAINER) 
					&& (dropSchemeAction.getParent().getParent() instanceof ViewShape)) {
				return dropSchemeAction.getParent().getParent();
			}

			logger.info("Please reimplement this !");
			return null;

		}
		
		return null;
	}*/

	/*protected ViewShape retrieveOEShape(String identifier, EditionSchemeAction action)
	{
		if (identifier == null) {
			return null;
		}

		if (identifier != null) {
			Object value = action.getParameterValues().get(identifier);
			if (value instanceof ViewShape) {
				return (ViewShape)value;
			}
		}

		if (action.getEditionPatternInstance() != null) {
			FlexoModelObject object
			= action.getEditionPatternInstance().getPatternActor(identifier);
			if (object instanceof ViewShape) {
				return (ViewShape)object;
			} else {
				logger.warning("Unexpected "+object);
			}
		}

		if (action instanceof LinkSchemeAction) {
			
			LinkSchemeAction linkSchemeAction = (LinkSchemeAction)action;

			if (identifier.equals(FROM_TARGET)) {
				return linkSchemeAction.getFromShape();
			}

			if (identifier.equals(TO_TARGET)) {
				return linkSchemeAction.getToShape();
			}
			
		}
		
		else if (action instanceof DropSchemeAction) {
			
			DropSchemeAction dropSchemeAction = (DropSchemeAction)action;

			if (identifier.equals(CONTAINER) 
					&& (dropSchemeAction.getParent() instanceof ViewShape)) {
				return (ViewShape)dropSchemeAction.getParent();
			}

			if (identifier.equals(CONTAINER_OF_CONTAINER) 
					&& (dropSchemeAction.getParent().getParent() instanceof ViewShape)) {
				return (ViewShape)dropSchemeAction.getParent().getParent();
			}
		}
		
		return null;
	}*/

	/*protected ViewObject retrieveOEShemaObject(String identifier, EditionSchemeAction action)
	{
		if (identifier == null) {
			return null;
		}

		if (identifier != null) {
			Object value = action.getParameterValues().get(identifier);
			if (value instanceof ViewObject) {
				return (ViewObject)value;
			}
		}

		if (action.getEditionPatternInstance() != null) {
			FlexoModelObject object
			= action.getEditionPatternInstance().getPatternActor(identifier);
			if (object instanceof ViewObject) {
				return (ViewObject)object;
			} else {
				logger.warning("Unexpected "+object);
			}
		}

		if (action instanceof LinkSchemeAction) {
			
			LinkSchemeAction linkSchemeAction = (LinkSchemeAction)action;

			if (identifier.equals(FROM_TARGET)) {
				return linkSchemeAction.getFromShape();
			}

			if (identifier.equals(TO_TARGET)) {
				return linkSchemeAction.getToShape();
			}
			
		}
		
		else if (action instanceof DropSchemeAction) {
			
			DropSchemeAction dropSchemeAction = (DropSchemeAction)action;

			if (identifier.equals(CONTAINER)) {
				return dropSchemeAction.getParent();
			}

			if (identifier.equals(CONTAINER_OF_CONTAINER)) {
				return dropSchemeAction.getParent().getParent();
			}
		}
		
		return null;
	}*/

	/*protected String retrieveString(String identifier, EditionSchemeAction action)
	{
		Object returned = retrieveObject(identifier, action);
		if (returned instanceof String) {
			return (String)returned;
		}
		return null;
	}*/

	public EditionScheme getEditionScheme() {
		return _scheme;
	}

	@Override
	public BindingModel getBindingModel() {
		return getEditionScheme().getBindingModel();
	}

	public static enum EditionActionBindingAttribute implements InspectorBindingAttribute {
		conditional,
		individualName,
		className,
		container,
		fromShape,
		toShape,
		object,
		subject,
		father,
		value,
		restrictionType,
		cardinality,
		target,
		diagramName,
		view
	}

	private ViewPointDataBinding conditional;

	private BindingDefinition CONDITIONAL = new BindingDefinition("conditional", Boolean.class, BindingDefinitionType.GET, false);

	public BindingDefinition getConditionalBindingDefinition() {
		return CONDITIONAL;
	}

	public ViewPointDataBinding getConditional() {
		if (conditional == null) {
			conditional = new ViewPointDataBinding(this, EditionActionBindingAttribute.conditional, getConditionalBindingDefinition());
		}
		return conditional;
	}

	public void setConditional(ViewPointDataBinding conditional) {
		conditional.setOwner(this);
		conditional.setBindingAttribute(EditionActionBindingAttribute.conditional);
		conditional.setBindingDefinition(getConditionalBindingDefinition());
		this.conditional = conditional;
	}

}
