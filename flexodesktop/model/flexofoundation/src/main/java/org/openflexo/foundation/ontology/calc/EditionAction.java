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
package org.openflexo.foundation.ontology.calc;

import java.util.Hashtable;
import java.util.logging.Logger;

import org.openflexo.antar.expr.DefaultExpressionParser;
import org.openflexo.antar.expr.Expression;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.antar.expr.UnresolvedExpressionException;
import org.openflexo.antar.expr.parser.ParseException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.ontology.OntologyObject;
import org.openflexo.foundation.ontology.action.DropSchemeAction;
import org.openflexo.foundation.ontology.action.EditionSchemeAction;
import org.openflexo.foundation.ontology.action.LinkSchemeAction;
import org.openflexo.foundation.ontology.shema.OEShape;
import org.openflexo.foundation.ontology.shema.OEShemaObject;
import org.openflexo.toolbox.StringUtils;


public abstract class EditionAction<R extends PatternRole> extends CalcObject {

	private static final Logger logger = Logger.getLogger(EditionAction.class.getPackage().getName());

	public static final String CONTAINER = "container";
	public static final String CONTAINER_OF_CONTAINER = "container.container";
	public static final String FROM_TARGET = "fromTarget";
	public static final String TO_TARGET = "toTarget";
	public static final String CONTAINER_CONCEPT = "container.concept";
	public static final String CONTAINER_OF_CONTAINER_CONCEPT = "container.container.concept";
	public static final String FROM_TARGET_CONCEPT = "fromTarget.concept";
	public static final String TO_TARGET_CONCEPT = "toTarget.concept";
	

	public static enum EditionActionType
	{
		AddClass,
		AddIndividual,
		AddObjectProperty,
		AddIsAProperty,
		AddRestriction,
		AddConnector,
		AddShape,
		AddShema,
		DeclarePatternRole,
		GoToObject
	}
	
	private EditionScheme _scheme;
	private String description;
	private String conditional;
	private String patternRole;
	private Expression condition;
	
	public EditionAction()
	{
	}
	
	public abstract EditionActionType getEditionActionType();
	
	public void setScheme(EditionScheme scheme) 
	{
		_scheme = scheme;
	}

	public EditionScheme getScheme() 
	{
		return _scheme;
	}
	
	@Override
	public String getDescription() 
	{
		return description;
	}

	@Override
	public void setDescription(String description) 
	{
		this.description = description;
	}

	@Override
	public OntologyCalc getCalc() 
	{
		return getScheme().getCalc();
	}
	
	public String getConditional() 
	{
		return conditional;
	}

	public void setConditional(String conditional) 
	{
		this.conditional = conditional;
		if (StringUtils.isNotEmpty(conditional)) {
			DefaultExpressionParser parser = new DefaultExpressionParser();
			try {
				condition = parser.parse(conditional);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}
	
	public String _getPatternRoleName() 
	{
		return patternRole;
	}

	public void _setPatternRoleName(String patternRole) 
	{
		this.patternRole = patternRole;
	}

	public boolean evaluateCondition(final Hashtable<String,Object> parameterValues)
	{
		if (condition == null) {
			return true;
		}
		try {
			return condition.evaluateCondition(parameterValues);
		} catch (TypeMismatchException e) {
			e.printStackTrace();
		} catch (UnresolvedExpressionException e) {
			e.printStackTrace();
		}
		return false;
	}

	public EditionPattern getEditionPattern()
	{
		return getScheme().getEditionPattern();
	}
	
	public R getPatternRole()
	{
		return (R)getEditionPattern().getPatternRole(_getPatternRoleName());
	}

	public void setPatternRole(R patternRole) 
	{
		_setPatternRoleName((patternRole != null) && (patternRole.getPatternRoleName() != null) ? patternRole.getPatternRoleName() : null);
		updatePatternRoleType();
	}

	protected abstract void updatePatternRoleType();

	public int getIndex()
	{
		return getScheme().getActions().indexOf(this);
	}

	protected OntologyObject retrieveOntologyObject(String identifier, EditionSchemeAction action)
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

			OEShape fromContext = linkSchemeAction.getFromShape();
			OEShape toContext = linkSchemeAction.getToShape();

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

			if (identifier.equals(CONTAINER) && (dropSchemeAction.getParent() instanceof OEShape)) {
				OEShape container = (OEShape)dropSchemeAction.getParent();
				return (OntologyObject)container.getLinkedConcept();
			}

			if (identifier.equals(CONTAINER_OF_CONTAINER) && (dropSchemeAction.getParent().getParent() instanceof OEShape)) {
				OEShape container = (OEShape)dropSchemeAction.getParent().getParent();
				return (OntologyObject)container.getLinkedConcept();
			}

			if (identifier.equals(CONTAINER_CONCEPT) 
					&& (dropSchemeAction.getParent() instanceof OEShape)
					&& (((OEShape)dropSchemeAction.getParent()).getLinkedConcept() instanceof OntologyObject)) {
				return (OntologyObject)((OEShape)dropSchemeAction.getParent()).getLinkedConcept();
			}

			if (identifier.equals(CONTAINER_OF_CONTAINER_CONCEPT) 
					&& (dropSchemeAction.getParent().getParent() instanceof OEShape)
					&& (((OEShape)dropSchemeAction.getParent().getParent()).getLinkedConcept() instanceof OntologyObject)) {
				return (OntologyObject)((OEShape)dropSchemeAction.getParent().getParent()).getLinkedConcept();
			}
}
		
		return null;
	}

	protected FlexoModelObject retrieveFlexoModelObject(String identifier, EditionSchemeAction action)
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

			if (identifier.equals(CONTAINER) && (dropSchemeAction.getParent() instanceof OEShape)) {
				return dropSchemeAction.getParent();
				}

			if (identifier.equals(CONTAINER_OF_CONTAINER) && (dropSchemeAction.getParent().getParent() instanceof OEShape)) {
				return dropSchemeAction.getParent().getParent();
			}
		}
		
		return null;
	}

	protected String generateStringForPatternRole (PatternRole patternRole, EditionSchemeAction action)
	{
		if (action.getEditionPatternInstance() != null) {
			FlexoModelObject object
			= action.getEditionPatternInstance().getPatternActor(patternRole);
			if (object != null) {
				return object.getName();
			}
		}
		return "???";
		
	}

	protected String generateStringFromIdentifier (String identifier, EditionSchemeAction action)
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
	}

	protected Object retrieveObject(String identifier, EditionSchemeAction action)
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
			if (identifier.equals(FROM_TARGET_CONCEPT) && (linkSchemeAction.getFromShape() != null)) {
				return linkSchemeAction.getFromShape().getLinkedConcept();
			}
			if (identifier.equals(TO_TARGET_CONCEPT) && (linkSchemeAction.getToShape() != null)) {
				return linkSchemeAction.getToShape().getLinkedConcept();
			}

		}
		
		else if (action instanceof DropSchemeAction) {
			
			DropSchemeAction dropSchemeAction = (DropSchemeAction)action;

			if (identifier.equals(CONTAINER) 
					&& (dropSchemeAction.getParent() instanceof OEShape)) {
				return dropSchemeAction.getParent();
			}

			if (identifier.equals(CONTAINER_OF_CONTAINER) 
					&& (dropSchemeAction.getParent().getParent() instanceof OEShape)) {
				return dropSchemeAction.getParent().getParent();
			}

			if (identifier.equals(CONTAINER_CONCEPT) 
					&& (dropSchemeAction.getParent() instanceof OEShape)) {
				return ((OEShape)dropSchemeAction.getParent()).getLinkedConcept();
			}

			if (identifier.equals(CONTAINER_OF_CONTAINER_CONCEPT) 
					&& (dropSchemeAction.getParent().getParent() instanceof OEShape)) {
				return ((OEShape)dropSchemeAction.getParent().getParent()).getLinkedConcept();
			}
		}
		
		return null;
	}

	protected OEShape retrieveOEShape(String identifier, EditionSchemeAction action)
	{
		if (identifier == null) {
			return null;
		}

		if (identifier != null) {
			Object value = action.getParameterValues().get(identifier);
			if (value instanceof OEShape) {
				return (OEShape)value;
			}
		}

		if (action.getEditionPatternInstance() != null) {
			FlexoModelObject object
			= action.getEditionPatternInstance().getPatternActor(identifier);
			if (object instanceof OEShape) {
				return (OEShape)object;
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
					&& (dropSchemeAction.getParent() instanceof OEShape)) {
				return (OEShape)dropSchemeAction.getParent();
			}

			if (identifier.equals(CONTAINER_OF_CONTAINER) 
					&& (dropSchemeAction.getParent().getParent() instanceof OEShape)) {
				return (OEShape)dropSchemeAction.getParent().getParent();
			}
		}
		
		return null;
	}

	protected OEShemaObject retrieveOEShemaObject(String identifier, EditionSchemeAction action)
	{
		if (identifier == null) {
			return null;
		}

		if (identifier != null) {
			Object value = action.getParameterValues().get(identifier);
			if (value instanceof OEShemaObject) {
				return (OEShemaObject)value;
			}
		}

		if (action.getEditionPatternInstance() != null) {
			FlexoModelObject object
			= action.getEditionPatternInstance().getPatternActor(identifier);
			if (object instanceof OEShemaObject) {
				return (OEShemaObject)object;
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
	}

	protected String retrieveString(String identifier, EditionSchemeAction action)
	{
		Object returned = retrieveObject(identifier, action);
		if (returned instanceof String) {
			return (String)returned;
		}
		return null;
	}
}
