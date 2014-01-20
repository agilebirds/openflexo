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
import java.lang.reflect.Type;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.DataBinding.BindingDefinitionType;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.ontology.IFlexoOntologyConcept;
import org.openflexo.foundation.ontology.IFlexoOntologyObjectProperty;
import org.openflexo.foundation.ontology.IFlexoOntologyStructuralProperty;
import org.openflexo.foundation.ontology.IndividualOfClass;
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
@ImplementationClass(ObjectPropertyAssertion.ObjectPropertyAssertionImpl.class)
@XMLElement
public interface ObjectPropertyAssertion extends AbstractAssertion {

	@PropertyIdentifier(type = AddIndividual.class)
	public static final String ACTION_KEY = "action";

	@PropertyIdentifier(type = String.class)
	public static final String OBJECT_PROPERTY_URI_KEY = "objectPropertyURI";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String OBJECT_KEY = "object";

	@Getter(value = ACTION_KEY, inverse = AddIndividual.OBJECT_ASSERTIONS_KEY)
	public AddIndividual<?, ?> getAction();

	@Setter(ACTION_KEY)
	public void setAction(AddIndividual<?, ?> action);

	@Getter(value = OBJECT_PROPERTY_URI_KEY)
	@XMLAttribute
	public String _getObjectPropertyURI();

	@Setter(OBJECT_PROPERTY_URI_KEY)
	public void _setObjectPropertyURI(String objectPropertyURI);

	@Getter(value = OBJECT_KEY)
	@XMLAttribute
	public DataBinding<?> getObject();

	@Setter(OBJECT_KEY)
	public void setObject(DataBinding<?> object);

	public IFlexoOntologyStructuralProperty getOntologyProperty();

	public void setOntologyProperty(IFlexoOntologyStructuralProperty p);

	public Object getValue(EditionSchemeAction action);

	public IFlexoOntologyConcept getAssertionObject(EditionSchemeAction action);

	public static abstract class ObjectPropertyAssertionImpl extends AbstractAssertionImpl implements ObjectPropertyAssertion {

		private static final Logger logger = Logger.getLogger(ObjectPropertyAssertion.class.getPackage().getName());

		private String objectPropertyURI;

		public ObjectPropertyAssertionImpl() {
			super();
		}

		@Override
		public void _setObjectPropertyURI(String objectPropertyURI) {
			this.objectPropertyURI = objectPropertyURI;
		}

		@Override
		public String _getObjectPropertyURI() {
			return objectPropertyURI;
		}

		@Override
		public IFlexoOntologyStructuralProperty getOntologyProperty() {
			if (getVirtualModel() != null) {
				return getVirtualModel().getOntologyObjectProperty(_getObjectPropertyURI());
			}
			return null;
		}

		@Override
		public void setOntologyProperty(IFlexoOntologyStructuralProperty p) {
			_setObjectPropertyURI(p != null ? p.getURI() : null);
		}

		@Override
		public BindingModel getBindingModel() {
			return getEditionScheme().getBindingModel();
		}

		private DataBinding<?> object;

		public Type getObjectType() {
			if (getOntologyProperty() instanceof IFlexoOntologyObjectProperty
					&& ((IFlexoOntologyObjectProperty) getOntologyProperty()).getRange() instanceof IFlexoOntologyClass) {
				return IndividualOfClass.getIndividualOfClass((IFlexoOntologyClass) ((IFlexoOntologyObjectProperty) getOntologyProperty())
						.getRange());
			}
			return IFlexoOntologyConcept.class;
		}

		@Override
		public DataBinding<?> getObject() {
			if (object == null) {
				object = new DataBinding<Object>(this, getObjectType(), BindingDefinitionType.GET);
				object.setBindingName("object");
			}
			return object;
		}

		@Override
		public void setObject(DataBinding<?> object) {
			if (object != null) {
				object.setOwner(this);
				object.setBindingName("object");
				object.setDeclaredType(getObjectType());
				object.setBindingDefinitionType(BindingDefinitionType.GET);
			}
			this.object = object;
		}

		@Override
		public IFlexoOntologyConcept getAssertionObject(EditionSchemeAction action) {
			Object value = null;
			try {
				value = getObject().getBindingValue(action);
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			if (value instanceof IFlexoOntologyConcept) {
				return (IFlexoOntologyConcept) value;
			}
			return null;
		}

		@Override
		public Object getValue(EditionSchemeAction action) {
			try {
				return getObject().getBindingValue(action);
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			return null;
		}

	}

	public static class ObjectPropertyAssertionMustDefineAnOntologyProperty extends
			ValidationRule<ObjectPropertyAssertionMustDefineAnOntologyProperty, ObjectPropertyAssertion> {
		public ObjectPropertyAssertionMustDefineAnOntologyProperty() {
			super(ObjectPropertyAssertion.class, "object_property_assertion_must_define_an_ontology_property");
		}

		@Override
		public ValidationIssue<ObjectPropertyAssertionMustDefineAnOntologyProperty, ObjectPropertyAssertion> applyValidation(
				ObjectPropertyAssertion assertion) {
			if (assertion.getOntologyProperty() == null) {
				return new ValidationError<ObjectPropertyAssertionMustDefineAnOntologyProperty, ObjectPropertyAssertion>(this, assertion,
						"object_property_assertion_must_define_an_ontology_property");
			}
			return null;
		}

	}

	public static class ObjectBindingIsRequiredAndMustBeValid extends BindingIsRequiredAndMustBeValid<ObjectPropertyAssertion> {
		public ObjectBindingIsRequiredAndMustBeValid() {
			super("'object'_binding_is_required_and_must_be_valid", ObjectPropertyAssertion.class);
		}

		@Override
		public DataBinding<?> getBinding(ObjectPropertyAssertion object) {
			return object.getObject();
		}

	}

}
