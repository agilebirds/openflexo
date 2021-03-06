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

import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingDefinition.BindingDefinitionType;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.ontology.OntologyObject;
import org.openflexo.foundation.ontology.OntologyProperty;
import org.openflexo.foundation.validation.ValidationError;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.EditionAction.EditionActionBindingAttribute;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.foundation.viewpoint.binding.ViewPointDataBinding;

public class ObjectPropertyAssertion extends AbstractAssertion {

	private static final Logger logger = Logger.getLogger(ObjectPropertyAssertion.class.getPackage().getName());

	private String objectPropertyURI;

	public ObjectPropertyAssertion(ViewPointBuilder builder) {
		super(builder);
	}

	@Override
	public String getInspectorName() {
		return Inspectors.VPM.OBJECT_PROPERTY_ASSERTION_INSPECTOR;
	}

	public void _setObjectPropertyURI(String objectPropertyURI) {
		this.objectPropertyURI = objectPropertyURI;
	}

	public String _getObjectPropertyURI() {
		return objectPropertyURI;
	}

	public OntologyProperty getOntologyProperty() {
		if (getViewPoint().getViewpointOntology() != null) {
			return getViewPoint().getViewpointOntology().getObjectProperty(_getObjectPropertyURI());
		}
		return null;
	}

	public void setOntologyProperty(OntologyProperty p) {
		_setObjectPropertyURI(p != null ? p.getURI() : null);
	}

	@Override
	public BindingModel getBindingModel() {
		return getEditionScheme().getBindingModel();
	}

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

	public OntologyObject getAssertionObject(EditionSchemeAction action) {
		Object value = getObject().getBindingValue(action);
		if (value instanceof OntologyObject) {
			return (OntologyObject) value;
		}
		return null;
	}

	public Object getValue(EditionSchemeAction action) {
		return getObject().getBindingValue(action);
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
		public ViewPointDataBinding getBinding(ObjectPropertyAssertion object) {
			return object.getObject();
		}

		@Override
		public BindingDefinition getBindingDefinition(ObjectPropertyAssertion object) {
			return object.getObjectBindingDefinition();
		}

	}

}
