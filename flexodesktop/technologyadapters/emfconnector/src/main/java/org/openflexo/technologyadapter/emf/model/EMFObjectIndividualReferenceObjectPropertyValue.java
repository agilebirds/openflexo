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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.openflexo.foundation.ontology.IFlexoOntologyConcept;
import org.openflexo.foundation.ontology.IFlexoOntologyObjectProperty;
import org.openflexo.foundation.ontology.IFlexoOntologyObjectPropertyValue;
import org.openflexo.foundation.ontology.IFlexoOntologyStructuralProperty;

/**
 * EMF Object Individual Reference Object Property Value.
 * 
 * @author gbesancon
 */
public class EMFObjectIndividualReferenceObjectPropertyValue extends AEMFModelObjectImpl<EObject> implements
		IFlexoOntologyObjectPropertyValue {

	/** Reference. */
	protected final EReference reference;

	/**
	 * Constructor.
	 * 
	 * @param ontology
	 * @param eObject
	 */
	public EMFObjectIndividualReferenceObjectPropertyValue(EMFModel model, EObject eObject, EReference aReference) {
		super(model, eObject);
		this.reference = aReference;
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
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyObjectPropertyValue#getObjectProperty()
	 */
	@Override
	public IFlexoOntologyObjectProperty getObjectProperty() {
		return ontology.getMetaModel().getConverter().convertReferenceObjectProperty(ontology.getMetaModel(), reference);
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
		if (object.eGet(reference) != null) {
			if (reference.getUpperBound() == 1) {
				if (ontology.getConverter().getIndividuals().get(object.eGet(reference)) != null) {
					result = Collections.singletonList((IFlexoOntologyConcept) (ontology.getConverter().getIndividuals().get(object
							.eGet(reference))));
				} else {
					result = Collections.emptyList();
				}
			} else {
				result = new ArrayList<IFlexoOntologyConcept>();
				List<?> valueList = (List<?>) object.eGet(reference);
				for (Object value : valueList) {
					if (ontology.getConverter().getIndividuals().get(value) != null) {
						result.add((IFlexoOntologyConcept) (ontology.getConverter().getIndividuals().get(value)));
					}
				}
			}
		} else {
			result = Collections.emptyList();
		}
		return Collections.unmodifiableList(result);
	}

	/**
	 * Getter of reference.
	 * 
	 * @return the reference value
	 */
	public EReference getReference() {
		return reference;
	}
}
