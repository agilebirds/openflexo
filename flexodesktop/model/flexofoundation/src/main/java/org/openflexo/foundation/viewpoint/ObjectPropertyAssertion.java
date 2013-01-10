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

import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.DataBinding.BindingDefinitionType;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.foundation.ontology.IFlexoOntologyConcept;
import org.openflexo.foundation.ontology.IFlexoOntologyStructuralProperty;
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
		return getViewPoint().getOntologyObjectProperty(_getObjectPropertyURI());
	}

	public void setOntologyProperty(IFlexoOntologyStructuralProperty p) {
		_setObjectPropertyURI(p != null ? p.getURI() : null);
	}

	@Override
	public BindingModel getBindingModel() {
		return getEditionScheme().getBindingModel();
	}

	private DataBinding<IFlexoOntologyConcept> object;

	public DataBinding<IFlexoOntologyConcept> getObject() {
		if (object == null) {
			object = new DataBinding<IFlexoOntologyConcept>(this, IFlexoOntologyConcept.class, BindingDefinitionType.GET);
			object.setBindingName("object");
		}
		return object;
	}

	public void setObject(DataBinding<IFlexoOntologyConcept> object) {
		if (object != null) {
			object.setOwner(this);
			object.setBindingName("object");
			object.setDeclaredType(IFlexoOntologyConcept.class);
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
