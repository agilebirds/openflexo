/*
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
 */

package org.openflexo.technologyadapter.emf.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Logger;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.util.EObjectEList;
import org.openflexo.foundation.action.NotImplementedException;
import org.openflexo.foundation.ontology.IFlexoOntologyConcept;
import org.openflexo.foundation.ontology.IFlexoOntologyObjectProperty;
import org.openflexo.foundation.ontology.IFlexoOntologyObjectPropertyValue;
import org.openflexo.foundation.ontology.IFlexoOntologyStructuralProperty;
import org.openflexo.technologyadapter.emf.model.io.EMFModelConverter;

/**
 * EMF EObjectEList wrapper
 * 
 * @author xtof
 */
public class EMFIndividualReferenceObjectPropertyAsList extends AEMFModelObjectImpl<EObject> implements
		IFlexoOntologyObjectPropertyValue, List<EMFObjectIndividual> {
	
	private static final Logger logger = Logger.getLogger(EMFIndividualReferenceObjectPropertyAsList.class.getPackage().getName());


	/** Reference. must be an EObjectEList */
	protected final EReference reference;
	
	/**
	 * Constructor.
	 * 
	 * @param ontology
	 * @param eObject
	 */
	public EMFIndividualReferenceObjectPropertyAsList(EMFModel model, EObject eObject, EReference aReference) {
		super(model, eObject);
		this.reference =  aReference;
		if (!(reference instanceof EObjectEList)){
			logger.warning("Trying to access to an non List Object as a List!");
		}
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.FlexoOntologyObjectImpl#getName()
	 */
	@Override
	public String getName() {
		return ((EReference) reference).getName();
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.FlexoObject#getFullyQualifiedName()
	 */
	@Override
	@Deprecated
	public String getFullyQualifiedName() {
		return getName();
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

	/********************************************
	 * Methods so that it acts as a List
	 */
	
	@Override
	public int size() {
		return ((EObjectEList) reference).size();
	}

	@Override
	public boolean isEmpty() {
		return ((EObjectEList) reference).isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return ((EObjectEList) reference).contains(o);
	}

	@Override
	public Iterator<EMFObjectIndividual> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object[] toArray() {
		return ((EObjectEList) reference).toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return (T[]) ((EObjectEList) reference).toArray(a);
	}

	@Override
	public boolean add(EMFObjectIndividual e) {
		return ((EObjectEList) reference).add(e.getObject());
	}

	@Override
	public boolean remove(Object o) {
		return ((EObjectEList) reference).remove(((EMFObjectIndividual)o).getObject());
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addAll(Collection<? extends EMFObjectIndividual> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addAll(int index, Collection<? extends EMFObjectIndividual> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void clear() {
		((EObjectEList) reference).clear();
		
	}

	@Override
	public EMFObjectIndividual get(int index) {
		// TODO TODO TODO
		// Big issue here with URI management!!!
		return null;
	}

	@Override
	public EMFObjectIndividual set(int index, EMFObjectIndividual element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void add(int index, EMFObjectIndividual element) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public EMFObjectIndividual remove(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int indexOf(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int lastIndexOf(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ListIterator<EMFObjectIndividual> listIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ListIterator<EMFObjectIndividual> listIterator(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<EMFObjectIndividual> subList(int fromIndex, int toIndex) {
		// TODO Auto-generated method stub
		return null;
	}
}
