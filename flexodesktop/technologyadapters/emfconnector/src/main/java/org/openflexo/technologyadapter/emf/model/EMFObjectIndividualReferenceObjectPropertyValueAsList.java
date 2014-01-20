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
public class EMFObjectIndividualReferenceObjectPropertyValueAsList extends EMFObjectIndividualReferenceObjectPropertyValue implements List<EMFObjectIndividual> {


	private static final Logger logger = Logger.getLogger(EMFObjectIndividualReferenceObjectPropertyValueAsList.class.getPackage().getName());


	private EObjectEList referencelist;

	public EMFObjectIndividualReferenceObjectPropertyValueAsList(EMFModel model, EObject eObject, EReference aReference, Object refList) {
		super(model, eObject, aReference);
		this.referencelist = (EObjectEList) refList;
	}


	
	/********************************************
	 * Methods so that it acts as a List
	 */

	@Override
	public int size() {
		return ((EObjectEList) referencelist).size();
	}

	@Override
	public boolean isEmpty() {
		return ((EObjectEList) referencelist).isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return ((EObjectEList) referencelist).contains(o);
	}

	@Override
	public Iterator<EMFObjectIndividual> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object[] toArray() {
		return ((EObjectEList) referencelist).toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return (T[]) ((EObjectEList) referencelist).toArray(a);
	}

	@Override
	public boolean add(EMFObjectIndividual e) {
		return ((EObjectEList) referencelist).add(e.getObject());
	}

	@Override
	public boolean remove(Object o) {
		return ((EObjectEList) referencelist).remove(((EMFObjectIndividual)o).getObject());
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
		((EObjectEList) referencelist).clear();

	}

	@Override
	public EMFObjectIndividual get(int index) {
		EObject o = (EObject) ((EObjectEList) referencelist).get(index);
		EMFObjectIndividual returned = ontology.getConverter().convertObjectIndividual(ontology, o);

		return returned;
	}

	@Override
	public EMFObjectIndividual set(int index, EMFObjectIndividual element) {

		((EObjectEList) referencelist).set(index, element.getObject());
		return element;
	}

	@Override
	public void add(int index, EMFObjectIndividual element) {

		((EObjectEList) referencelist).add(index, element.getObject());
	}

	@Override
	public EMFObjectIndividual remove(int index) {
		return ontology.getConverter().convertObjectIndividual(ontology, (EObject) ((EObjectEList) referencelist).remove(index));
	}

	@Override
	public int indexOf(Object o) {
		if (o instanceof EMFObjectIndividual) {
			return ((EObjectEList) referencelist).indexOf( ((EMFObjectIndividual) o).getObject());
		}
		else
			return -1;

	}

	@Override
	public int lastIndexOf(Object o) {
		if (o instanceof EMFObjectIndividual) {
			return ((EObjectEList) referencelist).lastIndexOf( ((EMFObjectIndividual) o).getObject());
		}
		else
			return -1;
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
