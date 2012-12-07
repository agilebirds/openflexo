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
package org.openflexo.technologyadapter.emf.metamodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.openflexo.foundation.ontology.IFlexoOntologyAnnotation;
import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.ontology.IFlexoOntologyConcept;
import org.openflexo.foundation.ontology.IFlexoOntologyConceptContainer;
import org.openflexo.foundation.ontology.IFlexoOntologyConceptVisitor;
import org.openflexo.foundation.ontology.IFlexoOntologyFeatureAssociation;
import org.openflexo.foundation.ontology.util.AFlexoOntologyWrapperObject;

/**
 * EMF Class.
 * 
 * @author gbesancon
 */
public class EMFClassClass extends AFlexoOntologyWrapperObject<EMFMetaModel, EClass> implements IFlexoOntologyClass {
	/**
	 * Constructor.
	 */
	public EMFClassClass(EMFMetaModel metaModel, EClass aClass) {
		super(metaModel, aClass);
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyConcept#getURI()
	 */
	@Override
	public String getURI() {
		return EMFMetaModelURIBuilder.getUri(object);
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyConcept#getName()
	 */
	@Override
	public String getName() {
		return object.getName();
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyConcept#getDescription()
	 */
	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyConcept#getContainer()
	 */
	@Override
	public IFlexoOntologyConceptContainer getContainer() {
		return ontology.getConverter().convertPackage(ontology, object.getEPackage());
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyConcept#getAnnotations()
	 */
	@Override
	public List<IFlexoOntologyAnnotation> getAnnotations() {
		List<IFlexoOntologyAnnotation> annotations = null;
		if (object.getEAnnotations() != null && object.getEAnnotations().size() != 0) {
			annotations = new ArrayList<IFlexoOntologyAnnotation>();
			for (EAnnotation annotation : object.getEAnnotations()) {
				annotations.add(ontology.getConverter().convertAnnotation(ontology, annotation));
			}
		} else {
			annotations = Collections.emptyList();
		}
		return annotations;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyConcept#getFeatureAssociations()
	 */
	@Override
	public List<IFlexoOntologyFeatureAssociation> getFeatureAssociations() {
		List<IFlexoOntologyFeatureAssociation> featureAssociations = new ArrayList<IFlexoOntologyFeatureAssociation>(0);
		for (EAttribute attribute : object.getEAttributes()) {
			featureAssociations.add(ontology.getConverter().convertAttributeAssociation(ontology, attribute));
		}
		for (EReference reference : object.getEReferences()) {
			featureAssociations.add(ontology.getConverter().convertReferenceAssociation(ontology, reference));
		}
		return Collections.unmodifiableList(featureAssociations);
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyConcept#isSuperConceptOf(org.openflexo.foundation.ontology.IFlexoOntologyConcept)
	 */
	@Override
	public boolean isSuperConceptOf(IFlexoOntologyConcept concept) {
		return concept instanceof IFlexoOntologyClass ? isSuperClassOf((IFlexoOntologyClass) concept) : false;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyConcept#isEqualToConcept(org.openflexo.foundation.ontology.IFlexoOntologyConcept)
	 */
	@Override
	public boolean isEqualToConcept(IFlexoOntologyConcept concept) {
		return concept == this;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyConcept#isSubConceptOf(org.openflexo.foundation.ontology.IFlexoOntologyConcept)
	 */
	@Override
	public boolean isSubConceptOf(IFlexoOntologyConcept concept) {
		return concept instanceof IFlexoOntologyClass ? isSubClassOf((IFlexoOntologyClass) concept) : false;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyConcept#accept(org.openflexo.foundation.ontology.IFlexoOntologyConceptVisitor)
	 */
	@Override
	public <T> T accept(IFlexoOntologyConceptVisitor<T> visitor) {
		return visitor.visit(this);
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyClass#getSuperClasses()
	 */
	@Override
	public List<IFlexoOntologyClass> getSuperClasses() {
		List<IFlexoOntologyClass> superClasses = new ArrayList<IFlexoOntologyClass>();
		for (EClass superClass : object.getESuperTypes()) {
			superClasses.add(ontology.getConverter().convertClass(ontology, superClass));
		}
		return Collections.unmodifiableList(superClasses);
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyClass#getSubClasses()
	 */
	@Override
	public List<IFlexoOntologyClass> getSubClasses() {
		List<IFlexoOntologyClass> subClasses = new ArrayList<IFlexoOntologyClass>();
		// FIXME biais a cause de chargement a la demande.
		for (Entry<EClass, EMFClassClass> classEntry : ontology.getConverter().getClasses().entrySet()) {
			if (classEntry.getKey().getESuperTypes().contains(object) && classEntry.getValue() != this) {
				subClasses.add(classEntry.getValue());
			}
		}
		return Collections.unmodifiableList(subClasses);
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyClass#isSuperClassOf(org.openflexo.foundation.ontology.IFlexoOntologyClass)
	 */
	@Override
	public boolean isSuperClassOf(IFlexoOntologyClass aClass) {
		boolean isSuperClass = false;
		if (aClass instanceof EMFClassClass && aClass != this) {
			isSuperClass = object.isSuperTypeOf(((EMFClassClass) aClass).getObject());
		}
		return isSuperClass;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyClass#isSubClassOf(org.openflexo.foundation.ontology.IFlexoOntologyClass)
	 */
	@Override
	public boolean isSubClassOf(IFlexoOntologyClass aClass) {
		boolean isSubClass = false;
		if (aClass instanceof EMFClassClass && aClass != this) {
			isSubClass = ((EMFClassClass) aClass).getObject().isSuperTypeOf(object);
		}
		return isSubClass;
	}
}
