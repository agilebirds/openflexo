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
import org.openflexo.foundation.ontology.OntologyDataProperty;
import org.openflexo.foundation.ontology.OntologyProperty;
import org.openflexo.foundation.validation.ValidationError;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;
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

	public OntologyProperty getOntologyProperty() {
		if (getViewPoint().getViewpointOntology() != null) {
			return getViewPoint().getViewpointOntology().getProperty(_getDataPropertyURI());
		}
		return null;
	}

	public void setOntologyProperty(OntologyProperty p) {
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
			if (getOntologyProperty() instanceof OntologyDataProperty) {
				if (((OntologyDataProperty) getOntologyProperty()).getDataType() != null) {
					return ((OntologyDataProperty) getOntologyProperty()).getDataType().getAccessedType();
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

	public static class DataPropertyAssertionMustDefineAnOntologyProperty extends
			ValidationRule<DataPropertyAssertionMustDefineAnOntologyProperty, DataPropertyAssertion> {
		public DataPropertyAssertionMustDefineAnOntologyProperty() {
			super(DataPropertyAssertion.class, "data_property_assertion_must_define_an_ontology_property");
		}

		@Override
		public ValidationIssue<DataPropertyAssertionMustDefineAnOntologyProperty, DataPropertyAssertion> applyValidation(
				DataPropertyAssertion assertion) {
			if (assertion.getOntologyProperty() == null) {
				return new ValidationError<DataPropertyAssertionMustDefineAnOntologyProperty, DataPropertyAssertion>(this, assertion,
						"data_property_assertion_does_not_define_an_ontology_property");
			}
			return null;
		}

	}

	public static class ValueBindingIsRequiredAndMustBeValid extends BindingIsRequiredAndMustBeValid<DataPropertyAssertion> {
		public ValueBindingIsRequiredAndMustBeValid() {
			super("'value'_binding_is_required_and_must_be_valid", DataPropertyAssertion.class);
		}

		@Override
		public ViewPointDataBinding getBinding(DataPropertyAssertion object) {
			return object.getValue();
		}

		@Override
		public BindingDefinition getBindingDefinition(DataPropertyAssertion object) {
			return object.getValueBindingDefinition();
		}

	}

}
