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

import java.lang.reflect.Type;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingDefinition.BindingDefinitionType;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.ontology.ObjectPropertyStatement;
import org.openflexo.foundation.ontology.OntologyObject;
import org.openflexo.foundation.ontology.OntologyProperty;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.binding.ViewPointDataBinding;
import org.openflexo.toolbox.StringUtils;

public class AddObjectPropertyStatement extends AddStatement {

	private static final Logger logger = Logger.getLogger(AddObjectPropertyStatement.class.getPackage().getName());

	private String objectPropertyURI = null;

	public AddObjectPropertyStatement() {
	}

	@Override
	public ObjectPropertyStatementPatternRole getPatternRole() {
		PatternRole superPatternRole = super.getPatternRole();
		if (superPatternRole instanceof ObjectPropertyStatementPatternRole) {
			return (ObjectPropertyStatementPatternRole) superPatternRole;
		} else if (superPatternRole != null) {
			// logger.warning("Unexpected pattern role of type " + superPatternRole.getClass().getSimpleName());
			return null;
		}
		return null;
	}

	@Override
	public EditionActionType getEditionActionType() {
		return EditionActionType.AddObjectPropertyStatement;
	}

	/*@Override
	public List<ObjectPropertyStatementPatternRole> getAvailablePatternRoles() {
		return getEditionPattern().getPatternRoles(ObjectPropertyStatementPatternRole.class);
	}*/

	public OntologyProperty getObjectProperty() {
		if (getViewPoint() != null) {
			getViewPoint().loadWhenUnloaded();
		}
		if (StringUtils.isNotEmpty(objectPropertyURI)) {
			if (getOntologyLibrary() != null) {
				return getOntologyLibrary().getObjectProperty(objectPropertyURI);
			}
		} else {
			if (getPatternRole() != null) {
				return getPatternRole().getObjectProperty();
			}
		}
		return null;
	}

	public void setObjectProperty(OntologyProperty ontologyProperty) {
		if (ontologyProperty != null) {
			if (getPatternRole() != null) {
				if (getPatternRole().getObjectProperty().isSuperConceptOf(ontologyProperty)) {
					objectPropertyURI = ontologyProperty.getURI();
				} else {
					getPatternRole().setObjectProperty(ontologyProperty);
				}
			} else {
				objectPropertyURI = ontologyProperty.getURI();
			}
		} else {
			objectPropertyURI = null;
		}
	}

	public String _getObjectPropertyURI() {
		if (getObjectProperty() != null) {
			if (getPatternRole() != null && getPatternRole().getObjectProperty() == getObjectProperty()) {
				// No need to store an overriding type, just use default provided by pattern role
				return null;
			}
			return getObjectProperty().getURI();
		}
		return objectPropertyURI;
	}

	public void _setObjectPropertyURI(String objectPropertyURI) {
		this.objectPropertyURI = objectPropertyURI;
	}

	public OntologyObject getPropertyObject(EditionSchemeAction action) {
		return (OntologyObject) getObject().getBindingValue(action);
	}

	@Override
	public String getInspectorName() {
		return Inspectors.VPM.ADD_OBJECT_PROPERTY_INSPECTOR;
	}

	/*@Override
	protected void updatePatternRoleType()
	{
		if (getPatternRole() == null) {
			return;
		}
	}*/

	private ViewPointDataBinding object;

	private BindingDefinition OBJECT = new BindingDefinition("object", OntologyObject.class, BindingDefinitionType.GET, false);

	public BindingDefinition getObjectBindingDefinition() {
		return OBJECT;
	}

	public ViewPointDataBinding getObject() {
		if (object == null) {
			object = new ViewPointDataBinding(this, EditionActionBindingAttribute.object, getObjectBindingDefinition());
		}
		return object;
	}

	public void setObject(ViewPointDataBinding object) {
		object.setOwner(this);
		object.setBindingAttribute(EditionActionBindingAttribute.object);
		object.setBindingDefinition(getObjectBindingDefinition());
		this.object = object;
	}

	@Override
	public Type getAssignableType() {
		return ObjectPropertyStatement.class;
	}

}
