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

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.openflexo.foundation.ontology.IFlexoOntologyConcept;
import org.openflexo.foundation.ontology.IFlexoOntologyConceptContainer;
import org.openflexo.foundation.ontology.IFlexoOntologyContainer;
import org.openflexo.foundation.ontology.IFlexoOntologyDataType;
import org.openflexo.foundation.ontology.util.AFlexoOntologyWrapperObject;

/**
 * EMF Package Container.
 * 
 * @author gbesancon
 */
public class EMFPackageContainer extends AFlexoOntologyWrapperObject<EMFMetaModel, EPackage> implements IFlexoOntologyContainer {
	/**
	 * Constructor.
	 */
	public EMFPackageContainer(EMFMetaModel metaModel, EPackage aPackage) {
		super(metaModel, aPackage);
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyContainer#getName()
	 */
	@Override
	public String getName() {
		return object.getName();
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyContainer#getDescription()
	 */
	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyContainer#getParent()
	 */
	@Override
	public IFlexoOntologyConceptContainer getParent() {
		return ontology.getConverter().convertPackage(ontology, object.getESuperPackage());
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyContainer#getSubContainers()
	 */
	@Override
	public List<IFlexoOntologyContainer> getSubContainers() {
		List<IFlexoOntologyContainer> containers = new ArrayList<IFlexoOntologyContainer>();
		for (EPackage eSubPackage : object.getESubpackages()) {
			containers.add(ontology.getConverter().convertPackage(ontology, eSubPackage));
		}
		return Collections.unmodifiableList(containers);
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyConceptContainer#getConcepts()
	 */
	@Override
	public List<IFlexoOntologyConcept> getConcepts() {
		List<IFlexoOntologyConcept> concepts = new ArrayList<IFlexoOntologyConcept>();
		for (EClassifier classifier : object.getEClassifiers()) {
			if (classifier.eClass().getClassifierID() == EcorePackage.ECLASS) {
				EClass eClass = (EClass) classifier;
				concepts.add(ontology.getConverter().convertClass(ontology, eClass));
				for (EStructuralFeature feature : eClass.getEStructuralFeatures()) {
					if (feature.eClass().getClassifierID() == EcorePackage.EREFERENCE) {
						EReference reference = (EReference) feature;
						concepts.add(ontology.getConverter().convertReferenceObjectProperty(ontology, reference));
					} else if (feature.eClass().getClassifierID() == EcorePackage.EATTRIBUTE) {
						EAttribute attribute = (EAttribute) feature;
						concepts.add(ontology.getConverter().convertAttributeProperty(ontology, attribute));
					}
				}
			} else if (classifier.eClass().getClassifierID() == EcorePackage.EENUM) {
				EEnum eEnum = (EEnum) classifier;
				concepts.add(ontology.getConverter().convertEnum(ontology, eEnum));
				for (EEnumLiteral eEnumLiteral : eEnum.getELiterals()) {
					concepts.add(ontology.getConverter().convertEnumLiteral(ontology, eEnumLiteral));
				}
			}
		}
		return Collections.unmodifiableList(concepts);
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyConceptContainer#getDataTypes()
	 */
	@Override
	public List<IFlexoOntologyDataType> getDataTypes() {
		List<IFlexoOntologyDataType> dataTypes = new ArrayList<IFlexoOntologyDataType>();
		for (EClassifier classifier : object.getEClassifiers()) {
			if (classifier.eClass().getClassifierID() == EcorePackage.EDATA_TYPE) {
				EDataType eDataType = (EDataType) classifier;
				dataTypes.add(ontology.getConverter().convertDataType(ontology, eDataType));
			}
		}
		return Collections.unmodifiableList(dataTypes);
	}

}
