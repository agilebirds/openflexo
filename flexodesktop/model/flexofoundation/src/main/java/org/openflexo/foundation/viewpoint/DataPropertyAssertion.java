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

import java.lang.reflect.InvocationTargetException;

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
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@ImplementationClass(DataPropertyAssertion.DataPropertyAssertionImpl.class)
@XMLElement
public interface DataPropertyAssertion extends AbstractAssertion {

	@PropertyIdentifier(type = AddIndividual.class)
	public static final String ACTION_KEY = "action";

	@PropertyIdentifier(type = String.class)
	public static final String DATA_PROPERTY_URI_KEY = "dataPropertyURI";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String VALUE_KEY = "value";

	@Getter(value = ACTION_KEY, inverse = AddIndividual.DATA_ASSERTIONS_KEY)
	public AddIndividual<?, ?> getAction();

	@Setter(ACTION_KEY)
	public void setAction(AddIndividual<?, ?> action);

	@Getter(value = DATA_PROPERTY_URI_KEY)
	@XMLAttribute
	public String _getDataPropertyURI();

	@Setter(DATA_PROPERTY_URI_KEY)
	public void _setDataPropertyURI(String dataPropertyURI);

	@Getter(value = VALUE_KEY)
	@XMLAttribute
	public DataBinding<?> getValue();

	@Setter(VALUE_KEY)
	public void setValue(DataBinding<?> value);

	public IFlexoOntologyStructuralProperty getOntologyProperty();

	public void setOntologyProperty(IFlexoOntologyStructuralProperty p);

	public static abstract class DataPropertyAssertionImpl extends AbstractAssertionImpl implements DataPropertyAssertion {

		private String dataPropertyURI;
		private DataBinding<?> value;

		public DataPropertyAssertionImpl() {
			super();
		}

		@Override
		public String _getDataPropertyURI() {
			return dataPropertyURI;
		}

		@Override
		public void _setDataPropertyURI(String dataPropertyURI) {
			this.dataPropertyURI = dataPropertyURI;
		}

		@Override
		public IFlexoOntologyStructuralProperty getOntologyProperty() {
			if (getVirtualModel() != null) {
				return getVirtualModel().getOntologyProperty(_getDataPropertyURI());
			}
			return null;
		}

		@Override
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
			} catch (InvocationTargetException e) {
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

		@Override
		public DataBinding<?> getValue() {
			if (value == null) {
				value = new DataBinding<Object>(this, getType(), BindingDefinitionType.GET);
				value.setBindingName("value");
			}
			return value;
		}

		@Override
		public void setValue(DataBinding<?> value) {
			if (value != null) {
				value.setOwner(this);
				value.setBindingName("value");
				value.setDeclaredType(getType());
				value.setBindingDefinitionType(BindingDefinitionType.GET);
			}
			this.value = value;
		}

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
		public DataBinding<?> getBinding(DataPropertyAssertion object) {
			return object.getValue();
		}

	}

}
