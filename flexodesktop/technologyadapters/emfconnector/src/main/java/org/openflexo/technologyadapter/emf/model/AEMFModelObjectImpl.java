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

import org.eclipse.emf.ecore.EObject;
import org.openflexo.foundation.ontology.FlexoOntologyObjectImpl;
import org.openflexo.foundation.ontology.IFlexoOntology;

/**
 * Abstract Simple implementation of Flexo ontology object.
 * 
 * @author gbesancon
 * 
 */
public abstract class AEMFModelObjectImpl<T extends EObject> extends FlexoOntologyObjectImpl {

	/** MetaModel. */
	protected final EMFModel ontology;
	/** EMF Object Wrapped. */
	protected final T object;

	/**
	 * Constructor.
	 */
	public AEMFModelObjectImpl(EMFModel ontology, T object) {
		this.ontology = ontology;
		this.object = object;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.FlexoOntologyObjectImpl#getFlexoOntology()
	 */
	@Override
	public IFlexoOntology getFlexoOntology() {
		return ontology;
	}

	/**
	 * Return the wrapped objects.
	 * 
	 * @return
	 */
	public T getObject() {
		return object;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.FlexoOntologyObjectImpl#isOntology()
	 */
	@Override
	public boolean isOntology() {
		return false;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.FlexoOntologyObjectImpl#isOntologyClass()
	 */
	@Override
	public boolean isOntologyClass() {
		return false;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.FlexoOntologyObjectImpl#isOntologyIndividual()
	 */
	@Override
	public boolean isOntologyIndividual() {
		return false;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.FlexoOntologyObjectImpl#isOntologyObjectProperty()
	 */
	@Override
	public boolean isOntologyObjectProperty() {
		return false;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.FlexoOntologyObjectImpl#isOntologyDataProperty()
	 */
	@Override
	public boolean isOntologyDataProperty() {
		return false;
	}

}
