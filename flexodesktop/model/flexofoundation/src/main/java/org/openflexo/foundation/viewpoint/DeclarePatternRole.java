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

import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.FlexoModelObjectPatternRole.FlexoModelObjectType;
import org.openflexo.foundation.viewpoint.OntologicObjectPatternRole.OntologicObjectType;
import org.openflexo.foundation.viewpoint.PatternRole.PatternRoleType;
import org.openflexo.foundation.viewpoint.PrimitivePatternRole.PrimitiveType;




public class DeclarePatternRole extends EditionAction<PatternRole> {

	private static final Logger logger = Logger.getLogger(DeclarePatternRole.class.getPackage().getName());

	private String object;
	
	public DeclarePatternRole() {
	}

	@Override
	public EditionActionType getEditionActionType()
	{
		return EditionActionType.DeclarePatternRole;
	}
	
	@Override
	public String getInspectorName() {
		// TODO Auto-generated method stub
		return null;
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
				availableObjectValues.add(EditionAction.CONTAINER_CONCEPT);
				availableObjectValues.add(EditionAction.CONTAINER_OF_CONTAINER_CONCEPT);
				break;
			case LinkScheme:
				availableObjectValues.add(EditionAction.FROM_TARGET);
				availableObjectValues.add(EditionAction.TO_TARGET);
				availableObjectValues.add(EditionAction.FROM_TARGET_CONCEPT);
				availableObjectValues.add(EditionAction.TO_TARGET_CONCEPT);
				break;
			default:
				break;
			}
			for (PatternRole pr : getEditionPattern().getPatternRoles()) {
				availableObjectValues.add(pr.getPatternRoleName());
			}
			for (EditionPatternParameter p : getScheme().getParameters()) {
				availableObjectValues.add(p.getName());
			}
		}
		return availableObjectValues;
	}

	public Object getDeclaredObject(EditionSchemeAction action)
	{
		return retrieveObject(_getObject(), action);
	}

	// Called in case of pattern role is of wrong type
	private void setPatternRoleType(PatternRoleType type)
	{
		if ((getPatternRole() != null) && (getPatternRole().getType() == type)) {
			return; // Everything is fine
		}

		PatternRole newPatternRole = null;
		switch(type) {
		case Shape:
			newPatternRole = new ShapePatternRole();
			break;
		case Connector:
			newPatternRole = new ConnectorPatternRole();
			break;
		case Shema:
			newPatternRole = new ShemaPatternRole();
			break;
		case FlexoModelObject:
			newPatternRole = new FlexoModelObjectPatternRole();
			break;
		case OntologicObject:
			newPatternRole = new OntologicObjectPatternRole();
			break;
		case Primitive:
			newPatternRole = new PrimitivePatternRole();
			break;
		default:
			break;
		}
		
		if (getPatternRole() != null) {
			newPatternRole.setEditionPattern(getPatternRole().getEditionPattern());
			newPatternRole.setPatternRoleName(getPatternRole().getPatternRoleName());
		}
		
		setPatternRole(newPatternRole);
	}
	
	@Override
	protected void updatePatternRoleType()
	{
		if (getPatternRole() == null) {
			return;
		}
		
		if (_getObject() != null) {
			if (_getObject().equals(FROM_TARGET)
					|| _getObject().equals(TO_TARGET)
					|| _getObject().equals(CONTAINER)
					|| _getObject().equals(CONTAINER_OF_CONTAINER)) {
				setPatternRoleType(PatternRoleType.OntologicObject);
			} else if (getEditionPattern().getPatternRole(_getObject()) != null) {
				PatternRole pr = getEditionPattern().getPatternRole(_getObject());
				setPatternRoleType(pr.getType());
				switch (pr.getType()) {
				case OntologicObject:
					((OntologicObjectPatternRole)getPatternRole()).setOntologicObjectType(((OntologicObjectPatternRole)pr).getOntologicObjectType());
					break;
				case FlexoModelObject:
					((FlexoModelObjectPatternRole)getPatternRole()).setFlexoModelObjectType(((FlexoModelObjectPatternRole)pr).getFlexoModelObjectType());
					break;
				case Primitive:
					((PrimitivePatternRole)getPatternRole()).setPrimitiveType(((PrimitivePatternRole)pr).getPrimitiveType());
				default:
					break;
				}
			}
			else if (getScheme().getParameter(_getObject()) != null) {
				EditionPatternParameter p = getScheme().getParameter(_getObject());
				switch (p.getWidget()) {
				case TEXT_FIELD:
					setPatternRoleType(PatternRoleType.Primitive);
					((PrimitivePatternRole)getPatternRole()).setPrimitiveType(PrimitiveType.String);
					break;
				case TEXT_AREA:
					setPatternRoleType(PatternRoleType.Primitive);
					((PrimitivePatternRole)getPatternRole()).setPrimitiveType(PrimitiveType.String);
					break;
				case LOCALIZED_TEXT_FIELD:
					setPatternRoleType(PatternRoleType.Primitive);
					((PrimitivePatternRole)getPatternRole()).setPrimitiveType(PrimitiveType.LocalizedString);
					break;
				case CHECKBOX:
					setPatternRoleType(PatternRoleType.Primitive);
					((PrimitivePatternRole)getPatternRole()).setPrimitiveType(PrimitiveType.Boolean);
					break;
				case DROPDOWN:
					setPatternRoleType(PatternRoleType.Primitive);
					((PrimitivePatternRole)getPatternRole()).setPrimitiveType(PrimitiveType.String);
					break;
				case INTEGER:
					setPatternRoleType(PatternRoleType.Primitive);
					((PrimitivePatternRole)getPatternRole()).setPrimitiveType(PrimitiveType.Integer);
					break;
				case FLOAT:
					setPatternRoleType(PatternRoleType.Primitive);
					((PrimitivePatternRole)getPatternRole()).setPrimitiveType(PrimitiveType.Float);
					break;
				case CUSTOM:
					switch (p.getCustomType()) {
					case OntologyIndividual:
						setPatternRoleType(PatternRoleType.OntologicObject);
						((OntologicObjectPatternRole)getPatternRole()).setOntologicObjectType(OntologicObjectType.Individual);
						break;
					case FlexoRole:
						setPatternRoleType(PatternRoleType.FlexoModelObject);
						((FlexoModelObjectPatternRole)getPatternRole()).setFlexoModelObjectType(FlexoModelObjectType.Role);
						break;
					default:
						break;
					}
					break;
				default:
					break;
				}
			}
		}
	}	

}
