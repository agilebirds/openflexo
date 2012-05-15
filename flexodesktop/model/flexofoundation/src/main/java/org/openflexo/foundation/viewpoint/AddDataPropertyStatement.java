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
import org.openflexo.foundation.ontology.DataPropertyStatement;
import org.openflexo.foundation.ontology.OntologyDataProperty;
import org.openflexo.foundation.ontology.OntologyProperty;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.binding.ViewPointDataBinding;
import org.openflexo.toolbox.StringUtils;

public class AddDataPropertyStatement extends AddStatement {

	private static final Logger logger = Logger.getLogger(AddDataPropertyStatement.class.getPackage().getName());

	private String dataPropertyURI = null;

	public AddDataPropertyStatement() {
	}

	@Override
	public EditionActionType getEditionActionType() {
		return EditionActionType.AddDataPropertyStatement;
	}

	/*@Override
	public List<DataPropertyStatementPatternRole> getAvailablePatternRoles() {
		return getEditionPattern().getPatternRoles(DataPropertyStatementPatternRole.class);
	}*/

	@Override
	public DataPropertyStatementPatternRole getPatternRole() {
		PatternRole superPatternRole = super.getPatternRole();
		if (superPatternRole instanceof DataPropertyStatementPatternRole) {
			return (DataPropertyStatementPatternRole) superPatternRole;
		} else if (superPatternRole != null) {
			// logger.warning("Unexpected pattern role of type " + superPatternRole.getClass().getSimpleName());
			return null;
		}
		return null;
	}

	public OntologyProperty getDataProperty() {
		if (getViewPoint() != null) {
			getViewPoint().loadWhenUnloaded();
		}
		if (StringUtils.isNotEmpty(dataPropertyURI)) {
			if (getOntologyLibrary() != null) {
				return getOntologyLibrary().getDataProperty(dataPropertyURI);
			}
		} else {
			if (getPatternRole() != null) {
				return getPatternRole().getDataProperty();
			}
		}
		return null;
	}

	public void setDataProperty(OntologyProperty ontologyProperty) {
		if (ontologyProperty != null) {
			if (getPatternRole() != null) {
				if (getPatternRole().getDataProperty().isSuperConceptOf(ontologyProperty)) {
					dataPropertyURI = ontologyProperty.getURI();
				} else {
					getPatternRole().setDataProperty(ontologyProperty);
				}
			} else {
				dataPropertyURI = ontologyProperty.getURI();
			}
		} else {
			dataPropertyURI = null;
		}
	}

	public String _getDataPropertyURI() {
		if (getDataProperty() != null) {
			if (getPatternRole() != null && getPatternRole().getDataProperty() == getDataProperty()) {
				// No need to store an overriding type, just use default provided by pattern role
				return null;
			}
			return getDataProperty().getURI();
		}
		return dataPropertyURI;
	}

	public void _setDataPropertyURI(String dataPropertyURI) {
		this.dataPropertyURI = dataPropertyURI;
	}

	public Object getValue(EditionSchemeAction action) {
		return getValue().getBindingValue(action);
	}

	@Override
	public String getInspectorName() {
		return Inspectors.VPM.ADD_OBJECT_PROPERTY_INSPECTOR;
	}

	private ViewPointDataBinding value;

	private BindingDefinition VALUE = new BindingDefinition("value", Object.class, BindingDefinitionType.GET, false) {
		@Override
		public java.lang.reflect.Type getType() {
			if (getDataProperty() != null) {
				return ((OntologyDataProperty) getDataProperty()).getDataType().getAccessedType();
			}
			return Object.class;
		};
	};

	public BindingDefinition getValueBindingDefinition() {
		return VALUE;
	}

	public ViewPointDataBinding getValue() {
		if (value == null) {
			value = new ViewPointDataBinding(this, EditionActionBindingAttribute.value, getValueBindingDefinition());
		}
		return value;
	}

	public void setValue(ViewPointDataBinding value) {
		value.setOwner(this);
		value.setBindingAttribute(EditionActionBindingAttribute.value);
		value.setBindingDefinition(getValueBindingDefinition());
		this.value = value;
	}

	@Override
	public Type getAssignableType() {
		return DataPropertyStatement.class;
	}

}
