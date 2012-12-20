/** Copyright (c) 2012, THALES SYSTEMES AEROPORTES - All Rights Reserved
 * Author : Gilles Besançon
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
 * Contributors :
 *
 */
package org.openflexo.technologyadapter.emf.model;

import java.util.Collections;
import java.util.List;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.openflexo.foundation.ontology.IFlexoOntologyDataProperty;
import org.openflexo.foundation.ontology.IFlexoOntologyDataPropertyValue;
import org.openflexo.foundation.ontology.IFlexoOntologyStructuralProperty;

/**
 * EMF Object Individual Attribute Data Property Value.
 * 
 * @author gbesancon
 */
public class EMFObjectIndividualAttributeDataPropertyValue extends AEMFModelObjectImpl<EObject> implements IFlexoOntologyDataPropertyValue {

	/** Attribute. */
	protected final EAttribute attribute;

	/**
	 * Constructor.
	 */
	public EMFObjectIndividualAttributeDataPropertyValue(EMFModel model, EObject eObject, EAttribute aAttribute) {
		super(model, eObject);
		this.attribute = aAttribute;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.FlexoOntologyObjectImpl#getName()
	 */
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.FlexoObject#getFullyQualifiedName()
	 */
	@Override
	@Deprecated
	public String getFullyQualifiedName() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.FlexoOntologyObjectImpl#getDisplayableDescription()
	 */
	@Override
	public String getDisplayableDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyDataPropertyValue#getDataProperty()
	 */
	@Override
	public IFlexoOntologyDataProperty getDataProperty() {
		return ontology.getMetaModel().getConverter().convertAttributeDataProperty(ontology.getMetaModel(), attribute);
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyPropertyValue#getProperty()
	 */
	@Override
	public IFlexoOntologyStructuralProperty getProperty() {
		return getDataProperty();
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyDataPropertyValue#getValue()
	 */
	@Override
	public List<Object> getValues() {
		List<Object> result = null;
		if (object.eGet(attribute) != null) {
			if (attribute.getUpperBound() == 1) {
				result = Collections.singletonList(object.eGet(attribute));
			} else {
				result = (List<Object>) object.eGet(attribute);
			}
		} else {
			result = Collections.emptyList();
		}
		return Collections.unmodifiableList(result);
	}
}
