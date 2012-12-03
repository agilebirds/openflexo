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

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.openflexo.foundation.ontology.IFlexoOntologyAnnotation;
import org.openflexo.foundation.ontology.IFlexoOntologyConcept;
import org.openflexo.foundation.ontology.IFlexoOntologyConceptContainer;
import org.openflexo.foundation.ontology.IFlexoOntologyConceptVisitor;
import org.openflexo.foundation.ontology.IFlexoOntologyDataProperty;
import org.openflexo.foundation.ontology.IFlexoOntologyDataType;
import org.openflexo.foundation.ontology.IFlexoOntologyFeatureAssociation;
import org.openflexo.foundation.ontology.util.AFlexoOntologyWrapperObject;

/**
 * EMF Attribute Data Property.
 * 
 * @author gbesancon
 */
public class EMFAttributeDataProperty extends AFlexoOntologyWrapperObject<EMFMetaModel, EAttribute> implements IFlexoOntologyDataProperty {
	/**
	 * Constructor.
	 */
	public EMFAttributeDataProperty(EMFMetaModel metaModel, EAttribute aAttribute) {
		super(metaModel, aAttribute);
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
		return ontology.getConverter().convertPackage(ontology, object.getEContainingClass().getEPackage());
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
		return Collections.emptyList();
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyConcept#isSuperConceptOf(org.openflexo.foundation.ontology.IFlexoOntologyConcept)
	 */
	@Override
	public boolean isSuperConceptOf(IFlexoOntologyConcept concept) {
		return false;
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
		return false;
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
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyFeature#getReferencingFeatureAssociations()
	 */
	@Override
	public List<IFlexoOntologyFeatureAssociation> getReferencingFeatureAssociations() {
		List<IFlexoOntologyFeatureAssociation> referencingFeatureAssociation = new ArrayList<IFlexoOntologyFeatureAssociation>();
		referencingFeatureAssociation.add(ontology.getConverter().convertAttributeAssociation(ontology, object));
		return Collections.unmodifiableList(referencingFeatureAssociation);
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyDataProperty#getRange()
	 */
	@Override
	public IFlexoOntologyDataType getRange() {
		return ontology.getConverter().convertDataType(ontology, object.getEAttributeType());
	}
}
