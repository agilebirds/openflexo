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
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;

public class ObjectPropertyAssertion extends AbstractAssertion {

	private static final Logger logger = Logger.getLogger(ObjectPropertyAssertion.class.getPackage().getName());

	private String objectPropertyURI;

	public ObjectPropertyAssertion(ViewPointBuilder builder) {
		super(builder);
	}

	public void _setObjectPropertyURI(String objectPropertyURI) {
		this.objectPropertyURI = objectPropertyURI;
	}

	public String _getObjectPropertyURI() {
		return objectPropertyURI;
	}

	public IFlexoOntologyStructuralProperty getOntologyProperty() {
		if (getViewPoint() != null) {
			return getViewPoint().getOntologyObjectProperty(_getObjectPropertyURI());
		}
		return null;
	}

	public void setOntologyProperty(IFlexoOntologyStructuralProperty p) {
		_setObjectPropertyURI(p != null ? p.getURI() : null);
	}

	@Override
	public BindingModel getBindingModel() {
		return getEditionScheme().getBindingModel();
	}

	private DataBinding<Object> object;

	public Type getObjectType() {
		if (getOntologyProperty() instanceof IFlexoOntologyObjectProperty
				&& ((IFlexoOntologyObjectProperty) getOntologyProperty()).getRange() instanceof IFlexoOntologyClass) {
			return IndividualOfClass.getIndividualOfClass((IFlexoOntologyClass) ((IFlexoOntologyObjectProperty) getOntologyProperty())
					.getRange());
		}
		return IFlexoOntologyConcept.class;
	}

	public DataBinding<Object> getObject() {
		if (object == null) {
			object = new DataBinding<Object>(this, getObjectType(), BindingDefinitionType.GET);
			object.setBindingName("object");
		}
		return object;
	}

	public void setObject(DataBinding<Object> object) {
		if (object != null) {
			object.setOwner(this);
			object.setBindingName("object");
			object.setDeclaredType(getObjectType());
			object.setBindingDefinitionType(BindingDefinitionType.GET);
		}
		this.object = object;
	}

	public IFlexoOntologyConcept getAssertionObject(EditionSchemeAction action) {
		Object value = null;
		try {
			value = getObject().getBindingValue(action);
		} catch (TypeMismatchException e) {
			e.printStackTrace();
		} catch (NullReferenceException e) {
			e.printStackTrace();
		}
		if (value instanceof IFlexoOntologyConcept) {
			return (IFlexoOntologyConcept) value;
		}
		return null;
	}

	public Object getValue(EditionSchemeAction action) {
		try {
			return getObject().getBindingValue(action);
		} catch (TypeMismatchException e) {
			e.printStackTrace();
		} catch (NullReferenceException e) {
			e.printStackTrace();
		}
		return null;
	}

}
