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

import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingDefinition.BindingDefinitionType;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.ontology.IFlexoOntologyDataProperty;
import org.openflexo.foundation.ontology.IFlexoOntologyStructuralProperty;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.EditionAction.EditionActionBindingAttribute;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.foundation.viewpoint.binding.ViewPointDataBinding;

public class DataPropertyAssertion extends AbstractAssertion {

	private String dataPropertyURI;

	public DataPropertyAssertion(ViewPointBuilder builder) {
		super(builder);
	}

	public String _getDataPropertyURI() {
		return dataPropertyURI;
	}

	public void _setDataPropertyURI(String dataPropertyURI) {
		this.dataPropertyURI = dataPropertyURI;
	}

	@Override
	public String getInspectorName() {
		return Inspectors.VPM.DATA_PROPERTY_ASSERTION_INSPECTOR;
	}

	public IFlexoOntologyStructuralProperty getOntologyProperty() {
		return getViewPoint().getOntologyProperty(_getDataPropertyURI());
	}

	public void setOntologyProperty(IFlexoOntologyStructuralProperty p) {
		_setDataPropertyURI(p != null ? p.getURI() : null);
	}

	public Object getValue(EditionSchemeAction action) {
		return getValue().getBindingValue(action);
	}

	@Override
	public BindingModel getBindingModel() {
		return getEditionScheme().getBindingModel();
	}

	private ViewPointDataBinding value;

	private BindingDefinition VALUE = new BindingDefinition("value", Object.class, BindingDefinitionType.GET, false) {
		@Override
		public java.lang.reflect.Type getType() {
			if (getOntologyProperty() instanceof IFlexoOntologyDataProperty) {
				if (((IFlexoOntologyDataProperty) getOntologyProperty()).getDataType() != null) {
					return ((IFlexoOntologyDataProperty) getOntologyProperty()).getDataType().getAccessedType();
				}
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

}
