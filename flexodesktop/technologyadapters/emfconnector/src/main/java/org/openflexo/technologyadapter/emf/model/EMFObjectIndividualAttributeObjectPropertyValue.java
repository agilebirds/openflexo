/** Copyright (c) 2012, THALES SYSTEMES AEROPORTES - All Rights Reserved
 * Author : Gilles Besançon
 * 
 * (c) Copyright 2013 Openflexo
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
 * Additional permission under GNU GPL version 3 section 7
 *
 * If you modify this Program, or any covered work, by linking or 
 * combining it with eclipse EMF (or a modified version of that library), 
 * containing parts covered by the terms of EPL 1.0, the licensors of this 
 * Program grant you additional permission to convey the resulting work.
 *
 * Contributors :
 *
 */
package org.openflexo.technologyadapter.emf.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.openflexo.foundation.ontology.IFlexoOntologyConcept;
import org.openflexo.foundation.ontology.IFlexoOntologyObjectProperty;
import org.openflexo.foundation.ontology.IFlexoOntologyObjectPropertyValue;
import org.openflexo.foundation.ontology.IFlexoOntologyStructuralProperty;

/**
 * EMF Object Individual Attribute Object Property Value.
 * 
 * @author gbesancon
 */
public class EMFObjectIndividualAttributeObjectPropertyValue extends AEMFModelObjectImpl<EObject> implements
		IFlexoOntologyObjectPropertyValue {

	/** Attribute. */
	protected final EAttribute attribute;

	/**
	 * Constructor.
	 * 
	 * @param ontology
	 * @param object
	 */
	public EMFObjectIndividualAttributeObjectPropertyValue(EMFModel model, EObject eObject, EAttribute aAttribute) {
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
		return attribute.getName();
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
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyObjectPropertyValue#getObjectProperty()
	 */
	@Override
	public IFlexoOntologyObjectProperty getObjectProperty() {
		return ontology.getMetaModel().getConverter().convertAttributeObjectProperty(ontology.getMetaModel(), attribute);
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyPropertyValue#getProperty()
	 */
	@Override
	public IFlexoOntologyStructuralProperty getProperty() {
		return getObjectProperty();
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyObjectPropertyValue#getValue()
	 */
	@Override
	public List<IFlexoOntologyConcept> getValues() {
		List<IFlexoOntologyConcept> result = null;
		if (object.eGet(attribute) != null) {
			if (attribute.getUpperBound() == 1) {
				if (ontology.getMetaModel().getConverter().getEnumLiterals().get(object.eGet(attribute)) != null) {
					result = Collections.singletonList((IFlexoOntologyConcept) (ontology.getMetaModel().getConverter().getEnumLiterals()
							.get(object.eGet(attribute))));
				} else {
					result = Collections.emptyList();
				}
			} else {
				result = new ArrayList<IFlexoOntologyConcept>();
				List<?> valueList = (List<?>) object.eGet(attribute);
				for (Object value : valueList) {
					if (ontology.getMetaModel().getConverter().getEnumLiterals().get(value) != null) {
						result.add((ontology.getMetaModel().getConverter().getEnumLiterals().get(value)));
					}
				}
			}
		} else {
			result = Collections.emptyList();
		}
		return Collections.unmodifiableList(result);
	}
}
