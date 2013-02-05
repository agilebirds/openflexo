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

import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.DataBinding.BindingDefinitionType;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.foundation.ontology.IFlexoOntologyDataProperty;
import org.openflexo.foundation.ontology.IFlexoOntologyStructuralProperty;
import org.openflexo.foundation.validation.ValidationError;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.view.action.EditionSchemeAction;

public class DataPropertyAssertion extends AbstractAssertion {

	private String dataPropertyURI;
	private DataBinding<Object> value;

	public DataPropertyAssertion(VirtualModel.VirtualModelBuilder builder) {
		super(builder);
	}

	public String _getDataPropertyURI() {
		return dataPropertyURI;
	}

	public void _setDataPropertyURI(String dataPropertyURI) {
		this.dataPropertyURI = dataPropertyURI;
	}

	public IFlexoOntologyStructuralProperty getOntologyProperty() {
		if (getVirtualModel() != null) {
			return getVirtualModel().getOntologyProperty(_getDataPropertyURI());
		}
		return null;
	}

	public void setOntologyProperty(IFlexoOntologyStructuralProperty p) {
		_setDataPropertyURI(p != null ? p.getURI() : null);
	}

	public Object getValue(EditionSchemeAction action) {
		try {
			return getValue().getBindingValue(action);
		} catch (TypeMismatchException e) {
			e.printStackTrace();
		} catch (NullReferenceException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public BindingModel getBindingModel() {
		return getEditionScheme().getBindingModel();
	}

	public java.lang.reflect.Type getType() {
		if (getOntologyProperty() instanceof IFlexoOntologyDataProperty) {
			if (((IFlexoOntologyDataProperty) getOntologyProperty()).getRange() != null) {
				return ((IFlexoOntologyDataProperty) getOntologyProperty()).getRange().getAccessedType();
			}
		}
		return Object.class;
	};

	public DataBinding<Object> getValue() {
		if (value == null) {
			value = new DataBinding<Object>(this, getType(), BindingDefinitionType.GET);
			value.setBindingName("value");
		}
		return value;
	}

	public void setValue(DataBinding<Object> value) {
		if (value != null) {
			value.setOwner(this);
			value.setBindingName("value");
			value.setDeclaredType(getType());
			value.setBindingDefinitionType(BindingDefinitionType.GET);
		}
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
		public DataBinding<Object> getBinding(DataPropertyAssertion object) {
			return object.getValue();
		}

	}

}
