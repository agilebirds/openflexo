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

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.ontology.OntologyObject;
import org.openflexo.foundation.ontology.OntologyProperty;
import org.openflexo.foundation.view.action.EditionSchemeAction;


public class ObjectPropertyAssertion extends AbstractAssertion {

	private static final Logger logger = Logger.getLogger(ObjectPropertyAssertion.class.getPackage().getName());

	private String objectPropertyURI;
	private String object;

	@Override
	public String getInspectorName() 
	{
		return Inspectors.CED.OBJECT_PROPERTY_ASSERTION_INSPECTOR;
	}

	public void _setObjectPropertyURI(String objectPropertyURI)
	{
		this.objectPropertyURI = objectPropertyURI;
	}

	public String _getObjectPropertyURI() 
	{
		return objectPropertyURI;
	}

	public OntologyProperty getOntologyProperty()
	{
		return getOntologyLibrary().getObjectProperty(_getObjectPropertyURI());
	}

	public void setOntologyProperty(OntologyProperty p)
	{
		_setObjectPropertyURI (p != null ? p.getURI() : null);
	}
	
	public String _getObject()
	{
		return object;
	}
	
	public void _setObject(String anObject)
	{
		object = anObject;
	}
	
	private Vector<String> availableObjectValues = null;
	
	public Vector<String> getAvailableObjectValues()
	{
		if (availableObjectValues == null) {
			availableObjectValues = new Vector<String>();
			switch (getScheme().getEditionSchemeType()) {
			case DropScheme:
				availableObjectValues.add(EditionAction.CONTAINER);
				availableObjectValues.add(EditionAction.CONTAINER_OF_CONTAINER);
				break;
			case LinkScheme:
				availableObjectValues.add(EditionAction.FROM_TARGET);
				availableObjectValues.add(EditionAction.TO_TARGET);
				break;
			default:
				break;
			}
			for (PatternRole pr : getAction().getEditionPattern().getPatternRoles()) {
				availableObjectValues.add(pr.getPatternRoleName());
			}
			for (EditionPatternParameter p : getAction().getScheme().getParameters()) {
				availableObjectValues.add(p.getName());
			}
		}
		return availableObjectValues;
	}



	public OntologyObject getAssertionObject(EditionSchemeAction action)
	{
		return getAction().retrieveOntologyObject(_getObject(), action);
		
		/*if (action instanceof DropSchemeAction) {
			DropSchemeAction dropSchemeAction = (DropSchemeAction)action;
			if (_getObject() != null) {
				Object value = action.getParameterValues().get(_getObject());
				if (value instanceof OntologyObject) return (OntologyObject)value;
			}

			if (_getObject().equals(EditionAction.CONTAINER) && dropSchemeAction.getParent() instanceof OEShape) {
				OEShape container = (OEShape)dropSchemeAction.getParent();
				return (OntologyObject)container.getLinkedConcept();
			}

			if (_getObject().equals(EditionAction.CONTAINER_OF_CONTAINER) && dropSchemeAction.getParent().getParent() instanceof OEShape) {
				OEShape container = (OEShape)dropSchemeAction.getParent().getParent();
				return (OntologyObject)container.getLinkedConcept();
			}

			if (action.getEditionPatternInstance() != null) {
				FlexoModelObject assertionObject
				= action.getEditionPatternInstance().getPatternActor(_getObject());
				if (assertionObject instanceof OntologyObject) return (OntologyObject)assertionObject;
				else logger.warning("Unexpected "+assertionObject);
			}
		}
		
		return null;*/
	}
	

}
